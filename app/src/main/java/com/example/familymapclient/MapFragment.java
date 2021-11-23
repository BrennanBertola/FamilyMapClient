package com.example.familymapclient;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Model.Event;
import Model.Person;
import ServerSide.DataCache;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private GoogleMap map;
    private View view;
    private DataCache cache;
    private List<Marker> markers;
    private Map<String, Marker> idToMarker;
    private List<Polyline> lines;

    private int currColor = 0;
    private Person person = null;

    private String eventID = "";
    private boolean eventView = false;

    public static final String MAP_KEY = "map key";

    private Listener listener;
    public interface Listener {
        void switchToPerson(Person person);
        void switchToSettings();
        void switchToSearch();
    }
    public void registerListener(MapFragment.Listener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        view = layoutInflater.inflate(R.layout.map_fragment, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setHasOptionsMenu(true);
        Iconify.with(new FontAwesomeModule());

        ImageButton button = view.findViewById(R.id.personButton);
        Drawable icon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_map_marker)
                .colorRes(R.color.black)
                .actionBarSize();
        button.setImageDrawable(icon);
        button.setEnabled(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.switchToPerson(person);
                }
            }
        });

        if (getArguments() != null) {
            eventID = getArguments().getString(MAP_KEY);
            eventView = true;
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!eventView) {
            inflater.inflate(R.menu.main_menu, menu);
            MenuItem menuItem = menu.findItem(R.id.searchMenuItem);
            menuItem.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_search)
                    .colorRes(R.color.white)
                    .actionBarSize());

            menuItem = menu.findItem(R.id.settingMenuItem);
            menuItem.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_gear)
                    .colorRes(R.color.white)
                    .actionBarSize());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        if (eventView) {
            getActivity().finish();
            return super.onOptionsItemSelected(menu);
        }
        else {
            switch (menu.getItemId()) {
                case R.id.searchMenuItem:
                    listener.switchToSearch();
                    return true;
                case R.id.settingMenuItem:
                    listener.switchToSettings();
                    return true;
                default:
                    return super.onOptionsItemSelected(menu);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);
        cache = DataCache.getInstance();


        addMarkers();
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                updateInfo(marker);
                addLines(marker);
                return true;
            }
        });

       if (eventView) {
           Marker marker = idToMarker.get(eventID);
           Event event = (Event) marker.getTag();

           LatLng location = new LatLng(event.getLatitude(), event.getLongitude());
           map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 4));

           updateInfo(marker);
           addLines(marker);
       }
    }

    @Override
    public void onMapLoaded() {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) {
            addMarkers();
            updateInfo(null);
            removeLines();
        }
    }

    private void updateInfo(Marker marker) {
        if (marker == null){
            TextView textView = view.findViewById(R.id.eventInfo);
            String info = "Select an event to see more details";
            textView.setText(info);

            ImageButton button = view.findViewById(R.id.personButton);
            Drawable icon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_map_marker)
                    .colorRes(R.color.black)
                    .actionBarSize();
            button.setImageDrawable(icon);
            button.setEnabled(false);
            return;
        }

        Event event = (Event) marker.getTag();
        Person person = cache.getPerson(event.getPersonID());
        this.person = person;

        ImageButton button = view.findViewById(R.id.personButton);
        Drawable icon;

        if (person.getGender().equals("m")) {
            icon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male)
                    .colorRes(R.color.maleBlue)
                    .actionBarSize();
        }
        else {
            icon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female)
                    .colorRes(R.color.femalePink)
                    .actionBarSize();
        }

        button.setImageDrawable(icon);
        button.setEnabled(true);

        TextView textView = view.findViewById(R.id.eventInfo);
        String info = person.getFirstName() + " " + person.getLastName() + " " +
                event.getEventType() + ": " + event.getCity() + ", " +
                event.getCountry() + " (" + event.getYear() + ")";
        textView.setText(info);
    }

    private void removeMarkers() {
        if (markers != null) {
            for (int i = 0; i < markers.size(); ++i) {
                Marker tmp = markers.get(i);
                tmp.remove();
            }
        }
    }

    private void addMarkers() {
        removeMarkers();
        currColor = 0;

        markers = new ArrayList<>();
        idToMarker = new HashMap<>();

        HashMap<String, Event> events = cache.getEvents();

        HashMap<String, Float> colorKey = cache.getColorKey();
        float[] colors = cache.getColors();

        Iterator it = events.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry tmp = (Map.Entry)it.next();
            Event event = (Event) tmp.getValue();
            if(!cache.showEvent(event.getEventID())) {
                continue;
            }

            Float color = colorKey.get(event.getEventType());
            if (color == null) {
                color = colors[currColor];
                colorKey.put(event.getEventType(), color);
                ++currColor;
                if (currColor >= colors.length) {
                    currColor = 0;
                }
            }

            LatLng location = new LatLng(event.getLatitude(), event.getLongitude());
            Marker marker = map.addMarker(new MarkerOptions().position(location).
                    icon(BitmapDescriptorFactory.defaultMarker(color))
                    .title(event.getEventType()));
            marker.setTag(event);

            markers.add(marker);
            idToMarker.put(event.getEventID(), marker);
        }
    }

    private void removeLines() {
        if (lines != null) {
            for (int i = 0; i < lines.size(); ++i) {
                Polyline tmp = lines.get(i);
                tmp.remove();
            }
        }
    }

    private void addLines(Marker marker) {
        removeLines();

        Event selectedEvent = (Event) marker.getTag();
        lines = new ArrayList<>();

        if (cache.getSpouseLines()) {
            spouseLines(selectedEvent);
        }

        if (cache.getFamilyLines()) {
            familyTreeLines(selectedEvent);
        }

        if (cache.getEventLines()) {
            lifeStoryLines(selectedEvent);
        }
    }

    private void spouseLines(Event event) {
        int color = Color.MAGENTA;
        float width = 20;
        Person person = cache.getPerson(event.getPersonID());
        if(person.getSpouseID() == null) {
            return;
        }

        List<Event> spouseEvents = cache.getSortedEvents(person.getSpouseID());
        if (spouseEvents == null || spouseEvents.size() == 0) {
            return;
        }

        if(cache.showEvent(spouseEvents.get(0).getEventID())) {
            drawLine(event, spouseEvents.get(0), color, width);
        }
    }

    private void familyTreeLines(Event event) {
        int color = Color.CYAN;
        float width = 25;
        drawFamilyLines(event, color, width);
    }

    private void drawFamilyLines(Event event, int color, float width) {
        Person currPerson = cache.getPerson(event.getPersonID());
        String dadID = currPerson.getFatherID();
        String momID = currPerson.getMotherID();

        if (dadID != null) {
            Event dadEvent= cache.getFirstEvent(dadID);
            if (dadEvent != null) {
                if(cache.showEvent(dadEvent.getEventID())) {
                    drawLine(event, dadEvent, color, width);
                    if (width - 5 <= 0) {
                        drawFamilyLines(dadEvent, color, 1);
                    } else {
                        drawFamilyLines(dadEvent, color, width - 5);
                    }
                }
            }
        }

        if (momID != null) {
            Event momEvent= cache.getFirstEvent(momID);
            if (momEvent != null) {
                if (cache.showEvent(momEvent.getEventID())) {
                    drawLine(event, momEvent, color, width);
                    if (width - 5 <= 0) {
                        drawFamilyLines(momEvent, color, 1);
                    } else {
                        drawFamilyLines(momEvent, color, width - 5);
                    }
                }
            }
        }
    }

    private void lifeStoryLines(Event event) {
        String personId = event.getPersonID();
        int color = Color.RED;
        float width = 20;
        List<Event> events = cache.getSortedEvents(personId);

        if (events == null || events.size() == 0) {
            return;
        }

        Event event1 = events.get(0);
        Event event2;
        int startingIndex = 0;

        while(!cache.showEvent(event1.getEventID()) && startingIndex < events.size()) {
            ++startingIndex;
            event1 = events.get(startingIndex);
        }

        for (int i = startingIndex; i < events.size(); ++i) {
            event2 = events.get(i);
            if (cache.showEvent(event2.getEventID())) {
                drawLine(event1, event2, color, width);
                event1 = event2;
            }
        }

    }

    private void drawLine(Event start, Event end, int color, float width) {
        LatLng startPoint = new LatLng(start.getLatitude(),start.getLongitude());
        LatLng endPoint = new LatLng(end.getLatitude(), end.getLongitude());

        PolylineOptions options = new PolylineOptions()
                .add(startPoint, endPoint)
                .color(color)
                .width(width);
        Polyline line = map.addPolyline(options);
        lines.add(line);
    }
}