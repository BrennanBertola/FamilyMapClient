package ServerSide;

import android.provider.ContactsContract;

import java.util.*;

import Model.Event;
import Model.Person;
import Model.User;

public class DataCache {

    private static DataCache instance;
    private static String authToken;

    public static DataCache getInstance(String givenToken) {
        if (instance == null) {
            instance = new DataCache(givenToken);
        }
        else if (!authToken.equals(givenToken)){
            instance = new DataCache(givenToken);
        }
        return instance;
    }

    private DataCache (String givenToken) {
        authToken = givenToken;
    }

    //==================Data Management Portion================//
    private User user;

    //uses Person id as Key for more quicker look up.
    private Map<String, Person> people;
    private Map<String, List<Event>> personEvents;

    //use event id as key.
    private Map<String, Event> events;

    //stores person IDs on dad and mom side.
    private Set<String> dadSide;
    private Set<String> momSide;
}
