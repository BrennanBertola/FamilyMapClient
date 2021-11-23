package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import ServerSide.DataCache;

public class SettingActivity extends AppCompatActivity {
    boolean switchChanged= false;
    DataCache cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Family Map: Settings");

        cache = DataCache.getInstance();
        setSwitches();

        Button button = findViewById(R.id.logoutButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataCache cache = DataCache.getInstance();
                cache.setLoggedIn(false);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (switchChanged) {
            cache.updateShowedEvents();
        }
        super.finish();
        return super.onOptionsItemSelected(item);
    }

    private void setSwitches() {
        Switch currSwitch = (Switch) findViewById(R.id.eventLinesSwitch);
        currSwitch.setChecked(cache.getEventLines());
        currSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cache.setEventLines(isChecked);
                switchChanged = true;
            }
        });

        currSwitch = (Switch) findViewById(R.id.familyLinesSwitch);
        currSwitch.setChecked(cache.getFamilyLines());
        currSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cache.setFamilyLines(isChecked);
                switchChanged = true;
            }
        });

        currSwitch = (Switch) findViewById(R.id.spouseLinesSwitch);
        currSwitch.setChecked(cache.getSpouseLines());
        currSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cache.setSpouseLines(isChecked);
                switchChanged = true;
            }
        });

        currSwitch = (Switch) findViewById(R.id.fathersSideSwitch);
        currSwitch.setChecked(cache.isFatherSide());
        currSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cache.setFatherSide(isChecked);
                switchChanged = true;
            }
        });

        currSwitch = (Switch) findViewById(R.id.mothersSideSwitch);
        currSwitch.setChecked(cache.isMotherSide());
        currSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cache.setMotherSide(isChecked);
                switchChanged = true;
            }
        });

        currSwitch = (Switch) findViewById(R.id.maleEventsSwitch);
        currSwitch.setChecked(cache.isMaleEvents());
        currSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cache.setMaleEvents(isChecked);
                switchChanged = true;
            }
        });

        currSwitch = (Switch) findViewById(R.id.femaleEventSwitch);
        currSwitch.setChecked(cache.isFemaleEvents());
        currSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cache.setFemaleEvents(isChecked);
                switchChanged = true;
            }
        });
    }



}