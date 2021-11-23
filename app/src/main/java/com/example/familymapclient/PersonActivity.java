package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.List;

import Model.Event;
import Model.Person;
import ServerSide.DataCache;

public class PersonActivity extends AppCompatActivity {
    public static final String PERSON_KEY = "person key";
    Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Family Map: Person");

        Intent intent = getIntent();
        String personID = intent.getStringExtra(PERSON_KEY);
        DataCache cache = DataCache.getInstance();
        person = cache.getPerson(personID);

        TextView textView = findViewById(R.id.personFirstName);
        textView.setText(person.getFirstName());

        textView = findViewById(R.id.personLastName);
        textView.setText(person.getLastName());

        textView = findViewById(R.id.personGender);
        if (person.getGender().equals("m")) {
            textView.setText("Male");
        }
        else {
            textView.setText("Female");
        }

        List<Event> tmp = cache.getSortedEvents(personID);
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < tmp.size(); ++i) {
            Event curr = tmp.get(i);
            if(cache.showEvent(curr.getEventID())) {
                events.add(curr);
            }
        }

        List<Person> family = cache.getFamily(personID);

        ExpandableListView expandableListView = findViewById(R.id.expandableListView);
        expandableListView.setAdapter(new ExpandableListAdapter(events, family));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.finish();
        return super.onOptionsItemSelected(item);
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        private static final int EVENT_GROUP_POSITION = 0;
        private static final int FAMILY_GROUP_POSITION = 1;

        private final List<Event> events;
        private final List<Person> family;

        ExpandableListAdapter(List<Event> events, List<Person> family) {
            this.events = events;
            this.family = family;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    return events.size();
                case FAMILY_GROUP_POSITION:
                    return family.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    return getString(R.string.eventTitle);
                case FAMILY_GROUP_POSITION:
                    return getString(R.string.familyTitle);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    return events.get(childPosition);
                case FAMILY_GROUP_POSITION:
                    return family.get(childPosition);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                                 ViewGroup parent) {

            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_list, parent,
                        false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    titleView.setText(R.string.eventTitle);
                    break;
                case FAMILY_GROUP_POSITION:
                    titleView.setText(R.string.familyTitle);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " +
                            groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parent) {

            View itemView;

            switch(groupPosition) {
                case EVENT_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.event_item, parent,
                            false);
                    initializeEventView(itemView, childPosition);
                    break;
                case FAMILY_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.person_item, parent,
                            false);
                    initializeFamilyView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " +
                            groupPosition);
            }

            return itemView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        private void initializeEventView(View eventItemView, final int childPosition) {
            Event event = events.get(childPosition);
            String eventInfo = event.getEventType() + ": " + event.getCity() + ", " +
                    event.getCountry() + " (" + event.getYear() + ")";

            TextView textView = eventItemView.findViewById(R.id.eventListInfo);
            textView.setText(eventInfo);

            DataCache cache = DataCache.getInstance();
            Person currPerson = cache.getPerson(event.getPersonID());
            String name = currPerson.getFirstName() + " " + currPerson.getLastName();

            textView = eventItemView.findViewById(R.id.personFromEvent);
            textView.setText(name);

            ImageButton button = eventItemView.findViewById(R.id.eventListButton);
            Drawable icon = new IconDrawable(eventItemView.getContext(),
                    FontAwesomeIcons.fa_map_marker)
                    .colorRes(R.color.black)
                    .actionBarSize();
            button.setImageDrawable(icon);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PersonActivity.this, EventActivity.class);
                    intent.putExtra(EventActivity.EVENT_KEY, event.getEventID());
                    startActivity(intent);
                }
            });


        }

        private void initializeFamilyView(View familyItemView, final int childPosition) {
            Person currPerson = family.get(childPosition);
            String personInfo = currPerson.getFirstName() + " " + currPerson.getLastName();

            TextView textView = familyItemView.findViewById(R.id.personListName);
            textView.setText(personInfo);

            String relation = "Child";

            if (currPerson.getPersonID().equals(person.getFatherID())) {
                relation = "Father";
            }
            else if (currPerson.getPersonID().equals(person.getMotherID())) {
                relation = "Mother";
            }
            else if (currPerson.getPersonID().equals(person.getSpouseID())) {
                relation = "Spouse";
            }

            textView = familyItemView.findViewById(R.id.personRelation);
            textView.setText(relation);

            ImageButton button = familyItemView.findViewById(R.id.personListButton);
            Drawable icon;

            if (currPerson.getGender().equals("m")) {
                icon = new IconDrawable(familyItemView.getContext(),
                        FontAwesomeIcons.fa_male)
                        .colorRes(R.color.maleBlue)
                        .actionBarSize();
            }
            else {
                icon = new IconDrawable(familyItemView.getContext(),
                        FontAwesomeIcons.fa_female)
                        .colorRes(R.color.femalePink)
                        .actionBarSize();
            }
            button.setImageDrawable(icon);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
                    intent.putExtra(PersonActivity.PERSON_KEY, currPerson.getPersonID());
                    startActivity(intent);
                }
            });
        }
    }
}