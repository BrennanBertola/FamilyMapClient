package ServerSide;

import android.provider.ContactsContract;

import java.util.*;

import Model.Event;
import Model.Person;
import Model.User;
import Request.EventRequest;
import Request.PersonRequest;
import Result.EventResult;
import Result.PersonResult;

public class DataCache {

    private static DataCache instance;

    //only used within login task
    public static DataCache getInstance(String givenToken, String personID) {
        if (instance == null) {
            instance = new DataCache(givenToken, personID);
        }
        else if (!authToken.equals(givenToken)){
            instance = new DataCache(givenToken, personID);
        }
        return instance;
    }

    public static DataCache getInstance() {
        if (authToken == null) {
            return null;
        }
        else if (instance == null) {
            instance = new DataCache();
        }
        return instance;
    }

    private DataCache (String givenToken, String personID) {
        authToken = givenToken;
        mainSetUp();

        user = people.get(personID);
        dadSide = new TreeSet<>();
        momSide = new TreeSet<>();

        //user is in neither, easy fix if you don't want this down the road.
        findSides(people.get(user.getFatherID()), true);
        findSides(people.get(user.getMotherID()), false);

    }

    private DataCache () {
        mainSetUp();
        user = null;
        dadSide = null;
        momSide = null;
    }

    private void mainSetUp() {
        people = new HashMap<>();
        personEvents = new HashMap<>();
        events = new HashMap<>();

        ServerProxy proxy = new ServerProxy();

        PersonRequest personRequest = new PersonRequest(authToken);
        PersonResult personResult = proxy.getPeople(personRequest);
        Person[] peopleData = personResult.getData();

        EventRequest eventRequest = new EventRequest(authToken);
        EventResult eventResult = proxy.getEvents(eventRequest);
        Event[] eventData = eventResult.getData();

        //builds people
        for (int i = 0; i < peopleData.length; ++i) {
            people.put(peopleData[i].getPersonID(), peopleData[i]);
        }

        //builds events and personEvents
        for (int i = 0; i < eventData.length; ++i) {
            events.put(eventData[i].getEventID(), eventData[i]);

            String key = eventData[i].getPersonID();
            if(personEvents.containsKey(key)) {
                List<Event> tmp = personEvents.get(key);
                tmp.add(eventData[i]);
                personEvents.remove(key);
                personEvents.put(key, tmp);
            }
            else {
                List<Event> tmp = new ArrayList<>();
                tmp.add(eventData[i]);
                personEvents.put(key, tmp);
            }
        }
    }

    //==================Data Management Portion================//
    private Person user;
    private static String authToken;

    //uses Person id as Key for more quicker look up.
    private HashMap<String, Person> people;
    private HashMap<String, List<Event>> personEvents;

    //use event id as key.
    private HashMap<String, Event> events;

    //stores person IDs on dad and mom side.
    private TreeSet<String> dadSide;
    private TreeSet<String> momSide;

    private void findSides(Person person, boolean fatherSide) {
        if (person == null) {
            return;
        }

        if (fatherSide) {
            dadSide.add(person.getPersonID());
        }
        else {
            momSide.add(person.getPersonID());
        }

        if (people.containsKey(person.getFatherID())) {
            findSides(people.get(person.getFatherID()), fatherSide);
        }
        if (people.containsKey(person.getMotherID())) {
            findSides(people.get(person.getMotherID()), fatherSide);
        }
    }
}
