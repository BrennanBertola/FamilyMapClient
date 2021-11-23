package com.example.familymapclient;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;

import Model.Person;

public class EventActivity extends AppCompatActivity implements MapFragment.Listener{
    public static final String EVENT_KEY = "event key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Family Map: Event");
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String eventID = intent.getStringExtra(EVENT_KEY);

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentFrameLayout);

        if(fragment == null) {
            fragment = new MapFragment();
            Bundle args = new Bundle();

            args.putString(MapFragment.MAP_KEY, eventID);
            fragment.setArguments(args);
            ((MapFragment) fragment).registerListener(this);

            fragmentManager.beginTransaction()
                    .add(R.id.fragmentFrameLayout, fragment)
                    .commit();
        }
        else {
            if(fragment instanceof MapFragment) {
                ((MapFragment) fragment).registerListener(this);
            }
        }
    }

    @Override
    public void switchToPerson(Person person) {
        Intent intent = new Intent(EventActivity.this, PersonActivity.class);
        intent.putExtra(PersonActivity.PERSON_KEY, person.getPersonID());
        startActivity(intent);
    }

    @Override
    public void switchToSearch() {
        Intent intent = new Intent(EventActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    @Override
    public void switchToSettings() {
        Intent intent = new Intent(EventActivity.this, SettingActivity.class);
        startActivity(intent);
    }
}