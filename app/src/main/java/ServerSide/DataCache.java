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

    private Person user;
    private static String authToken;

    //setting related information
    boolean loggedIn = false;
    boolean familyLines = true;
    boolean spouseLines = true;
    boolean eventLines = true;

    boolean haveFatherSide = true;
    boolean haveMotherSide = true;
    boolean maleEvents = true;
    boolean femaleEvents = true;

    private HashMap<String, Boolean> showedEvents;

    //uses Person id as Key for more quicker look up.
    private ArrayList<Person> peopleList;
    private HashMap<String, Person> people;
    private HashMap<String, List<Event>> personEvents;

    //use event id as key.
    private HashMap<String, Event> events;
    private ArrayList<Event> eventList;

    private float[] colors = {210, 330, 120, 270, 240, 30, 300, 60, 0};
    HashMap<String, Float> colorKey = new HashMap<>();

    //stores person IDs on dad and mom side.
    private TreeSet<String> dadSide;
    private TreeSet<String> momSide;

    //only used within login task
    public static void createCache(String givenToken, String personID) {
        instance = new DataCache(givenToken, personID);
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

        dadSide.add(user.getPersonID());
        momSide.add(user.getPersonID());

        findSides(people.get(user.getFatherID()), true);
        findSides(people.get(user.getMotherID()), false);
        loggedIn = true;
    }

    private DataCache () {
        mainSetUp();
        loggedIn = true;
        user = null;
        dadSide = null;
        momSide = null;
    }

    private void mainSetUp() {
        people = new HashMap<>();
        personEvents = new HashMap<>();
        events = new HashMap<>();
        showedEvents = new HashMap<>();
        peopleList = new ArrayList<>();
        eventList = new ArrayList<>();

        ServerProxy proxy = new ServerProxy();

        PersonRequest personRequest = new PersonRequest(authToken);
        PersonResult personResult = proxy.getPeople(personRequest);
        Person[] peopleData = personResult.getData();

        EventRequest eventRequest = new EventRequest(authToken);
        EventResult eventResult = proxy.getEvents(eventRequest);
        Event[] eventData = eventResult.getData();

        //builds people
        for (int i = 0; i < peopleData.length; ++i) {
            peopleList.add(peopleData[i]);
            people.put(peopleData[i].getPersonID(), peopleData[i]);
        }

        //builds events and personEvents
        for (int i = 0; i < eventData.length; ++i) {
            events.put(eventData[i].getEventID(), eventData[i]);
            showedEvents.put(eventData[i].getEventID(), true);
            eventList.add(eventData[i]);

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

    private List<Event> sortList(List<Event> passedList) {
        List<Event> newList = new ArrayList<>();
        List<Event> list = new ArrayList<>(passedList);

        while (list.size() > 0) {
            int minYear = 9999;
            int index = 0;
            Event trackedEvent = list.get(0);
            for (int i = 0; i < list.size(); ++i) {
                if (list.get(i).getYear() < minYear) {
                    trackedEvent = list.get(i);
                    minYear = trackedEvent.getYear();
                    index = i;
                }
                else if (list.get(i).getYear() == minYear &&
                        list.get(i).getEventType().toLowerCase().equals("birth")) {
                    trackedEvent = list.get(i);
                    index = i;
                }
                else if (list.get(i).getYear() == minYear &&
                        !list.get(i).getEventType().toLowerCase().equals("death")) {
                    trackedEvent = list.get(i);
                    index = i;
                }
            }
            newList.add(trackedEvent);
            list.remove(index);
        }

        return newList;
    }

    public void updateShowedEvents() {
        showedEvents = new HashMap<>();

        String userID = user.getPersonID();
        List<Event> userEvents = personEvents.get(userID);
        for (int i = 0; i < userEvents.size(); ++i) {
            Event currEvent = userEvents.get(i);
            if(shouldAddEvent(currEvent)) {
                showedEvents.put(currEvent.getEventID(), true);
            }
        }

        String spouseID = user.getSpouseID();
        if (spouseID != null) {
            List<Event> spouseEvents = personEvents.get(spouseID);
            for (int i = 0; i < spouseEvents.size(); ++i) {
                Event currEvent = spouseEvents.get(i);
                if (shouldAddEvent(currEvent)) {
                    showedEvents.put(currEvent.getEventID(), true);
                }
            }
        }

        if(haveFatherSide) {
            Iterator it = dadSide.iterator();
            while(it.hasNext()) {
                String currID = (String) it.next();
                List<Event> events = personEvents.get(currID);

                for (int i = 0; i < events.size(); ++i) {
                    Event currEvent = events.get(i);
                    if(shouldAddEvent(currEvent)) {
                        showedEvents.put(currEvent.getEventID(), true);
                    }
                }
            }
        }

        if(haveMotherSide) {
            Iterator it = momSide.iterator();
            while(it.hasNext()) {
                String currID = (String) it.next();
                List<Event> events = personEvents.get(currID);

                for (int i = 0; i < events.size(); ++i) {
                    Event currEvent = events.get(i);
                    if(shouldAddEvent(currEvent)) {
                        showedEvents.put(currEvent.getEventID(), true);
                    }
                }
            }
        }
    }

    private boolean shouldAddEvent(Event event) {
        Person person = people.get(event.getPersonID());

        if(person.getGender().equals("m") && !maleEvents) {
            return false;
        }
        if(person.getGender().equals("f") && !femaleEvents) {
            return false;
        }

        return true;
    }

    //==================Data Management Portion================//

    public Person getUser() {
        return user;
    }

    public float[] getColors() {
        return colors;
    }

    public HashMap<String, Float> getColorKey() {
        return colorKey;
    }

    public void setColorKey(HashMap<String, Float> colorKey) {
        this.colorKey = colorKey;
    }

    public static String getAuthToken() {
        return authToken;
    }

    public boolean isLoggedIn() {return loggedIn;}

    public Person getPerson(String id) {
        return people.get(id);
    }

    public List<Event> getPersonEvents(String id) { return personEvents.get(id);}

    public ArrayList<Person> getPeopleList() {
        return peopleList;
    }

    public ArrayList<Event> getEventList() {
        return eventList;
    }

    public List<Event> getSortedEvents(String id) {
        List<Event> returnVal = personEvents.get(id);
        return sortList(returnVal);
    }
    public Event getFirstEvent(String id) {
        List<Event> tmp = getSortedEvents(id);
        if (tmp == null || tmp.size() == 0) {
            return null;
        }
        return tmp.get(0);
    }

    public HashMap<String, Event> getEvents() {
        return events;
    }

    public boolean showEvent(String id) {
        if (showedEvents.get(id) == null) {
            return false;
        }
        return showedEvents.get(id);
    }

    public TreeSet<String> getDadSide() {
        return dadSide;
    }

    public TreeSet<String> getMomSide() {
        return momSide;
    }

    public List<Person> getFamily(String id) {
        Person person = people.get(id);
        ArrayList<Person> retList = getChildren(id);

        String desiredID = person.getFatherID();
        if (desiredID != null) {
            retList.add(people.get(desiredID));
        }

        desiredID = person.getMotherID();
        if (desiredID != null) {
            retList.add(people.get(desiredID));
        }

        desiredID = person.getSpouseID();
        if (desiredID != null) {
            retList.add(people.get(desiredID));
        }



        return retList;
    }

    private ArrayList<Person> getChildren(String id) {
        ArrayList<Person> retList = new ArrayList<>();

        for (int i = 0; i < peopleList.size(); ++i) {
            Person curr = peopleList.get(i);
            if(curr.getFatherID() != null) {
                if (curr.getFatherID().equals(id)) {
                    retList.add(curr);
                    continue;
                }
            }
            if (curr.getMotherID() != null) {
                if (curr.getMotherID().equals(id)) {
                    retList.add(curr);
                    continue;
                }
            }
        }

        return retList;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean getFamilyLines() {
        return familyLines;
    }

    public void setFamilyLines(boolean familyLines) {
        this.familyLines = familyLines;
    }

    public boolean getSpouseLines() {
        return spouseLines;
    }

    public void setSpouseLines(boolean spouseLines) {
        this.spouseLines = spouseLines;
    }

    public boolean getEventLines() {
        return eventLines;
    }

    public void setEventLines(boolean eventLiens) {
        this.eventLines = eventLiens;
    }

    public boolean isFatherSide() {
        return haveFatherSide;
    }

    public void setFatherSide(boolean fatherSide) {
        this.haveFatherSide = fatherSide;
    }

    public boolean isMotherSide() {
        return haveMotherSide;
    }

    public void setMotherSide(boolean motherSide) {
        this.haveMotherSide = motherSide;
    }

    public boolean isMaleEvents() {
        return maleEvents;
    }

    public void setMaleEvents(boolean maleEvents) {
        this.maleEvents = maleEvents;
    }

    public boolean isFemaleEvents() {
        return femaleEvents;
    }

    public void setFemaleEvents(boolean femaleEvents) {
        this.femaleEvents = femaleEvents;
    }


}
