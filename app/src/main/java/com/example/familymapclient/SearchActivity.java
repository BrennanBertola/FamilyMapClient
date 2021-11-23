package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.List;

import Model.Event;
import Model.Person;
import ServerSide.DataCache;

public class SearchActivity extends AppCompatActivity {

    private static final int EVENT = 0;
    private static final int PERSON = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Family Map: Search");

        ImageView image = findViewById(R.id.searchIcon);
        Drawable icon = new IconDrawable(this,
                FontAwesomeIcons.fa_search)
                .colorRes(R.color.black)
                .actionBarSize();
        image.setImageDrawable(icon);

        Button button = findViewById(R.id.searchButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.searchInput);
                String search = editText.getText().toString();
                if (!search.equals("")) {
                    updateResults(search);
                }
            }
        });

        DataCache cache = DataCache.getInstance();
        List<Person> people = new ArrayList<>();
        List<Event> events = new ArrayList<>();

        RecyclerView recView = findViewById(R.id.searchView);
        recView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(events, people);
        recView.setAdapter(adapter);
    }

    private void updateResults(String search) {
        DataCache cache = DataCache.getInstance();
        List<Person> people = cache.getPeopleList();
        List<Event> events = cache.getEventList();


        List<Person> filteredPeople = new ArrayList<>();
        for (int i = 0; i < people.size(); ++i) {
            Person curr = people.get(i);
            String name = (curr.getFirstName() + " " + curr.getLastName()).toLowerCase();
            if(name.contains(search.toLowerCase())) {
                filteredPeople.add(curr);
            }
        }

        List<Event> filteredEvents = new ArrayList<>();
        for (int i = 0; i < events.size(); ++i) {
            Event curr = events.get(i);
            String info = (curr.getEventType() + ": " + curr.getCity() + ", " +
                    curr.getCountry() + " (" + curr.getYear() + ")").toLowerCase();
            if(info.contains(search.toLowerCase()) && cache.showEvent(curr.getEventID())) {
                filteredEvents.add(curr);
            }
        }

        RecyclerView recView = findViewById(R.id.searchView);
        recView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(filteredEvents, filteredPeople);
        recView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.finish();
        return super.onOptionsItemSelected(item);
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
        private List<Event> events;
        private List<Person> people;

        RecyclerViewAdapter(List<Event> events, List<Person> people) {
            this.events = events;
            this.people = people;
        }

        @Override
        public int getItemViewType(int position) {
            return position < events.size() ? EVENT : PERSON;
        }

        @NonNull
        @Override
        public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == EVENT) {
                view = getLayoutInflater().inflate(R.layout.event_item, parent, false);
            }
            else {
                view = getLayoutInflater().inflate(R.layout.person_item, parent, false);
            }

            return new RecyclerViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
            if(position < events.size()) {
                holder.bind(events.get(position));
            } else {
                holder.bind(people.get(position - events.size()));
            }
        }

        @Override
        public int getItemCount() {
            return events.size() + people.size();
        }
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView info;
        private final TextView name;
        private final ImageButton button;
        private final int viewType;

        private Event event;
        private Person person;

        RecyclerViewHolder(View v, int viewType) {
            super(v);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            if(viewType == EVENT) {
                info = itemView.findViewById(R.id.eventListInfo);
                name = itemView.findViewById(R.id.personFromEvent);
                button = itemView.findViewById(R.id.eventListButton);
            }
            else {
                info = itemView.findViewById(R.id.personRelation);
                name = itemView.findViewById(R.id.personListName);
                button = itemView.findViewById(R.id.personListButton);
            }
        }

        private void bind(Event event) {
            this.event = event;
            String info = event.getEventType() + ": " + event.getCity() + ", " +
                    event.getCountry() + " (" + event.getYear() + ")";

            DataCache cache = DataCache.getInstance();
            Person person = cache.getPerson(event.getPersonID());
            String name = person.getFirstName() + " " + person.getLastName();

            this.info.setText(info);
            this.name.setText(name);

            Drawable icon = new IconDrawable(itemView.getContext(),
                    FontAwesomeIcons.fa_map_marker)
                    .colorRes(R.color.black)
                    .actionBarSize();
            button.setImageDrawable(icon);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SearchActivity.this, EventActivity.class);
                    intent.putExtra(EventActivity.EVENT_KEY, event.getEventID());
                    startActivity(intent);
                }
            });
        }

        private void bind(Person person) {
            this.person = person;
            String name = person.getFirstName() + " " + person.getLastName();

            this.name.setText(name);
            this.info.setText("");

            Drawable icon;

            if (person.getGender().equals("m")) {
                icon = new IconDrawable(itemView.getContext(),
                        FontAwesomeIcons.fa_male)
                        .colorRes(R.color.maleBlue)
                        .actionBarSize();
            }
            else {
                icon = new IconDrawable(itemView.getContext(),
                        FontAwesomeIcons.fa_female)
                        .colorRes(R.color.femalePink)
                        .actionBarSize();
            }
            button.setImageDrawable(icon);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SearchActivity.this, PersonActivity.class);
                    intent.putExtra(PersonActivity.PERSON_KEY, person.getPersonID());
                    startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View view) {
            if(viewType == EVENT) {
                Intent intent = new Intent(SearchActivity.this, EventActivity.class);
                intent.putExtra(EventActivity.EVENT_KEY, event.getEventID());
                startActivity(intent);
            } else {
                Intent intent = new Intent(SearchActivity.this, PersonActivity.class);
                intent.putExtra(PersonActivity.PERSON_KEY, person.getPersonID());
                startActivity(intent);
            }
        }
    }
}