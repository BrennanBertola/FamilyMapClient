package com.example.familymapclient;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Model.Event;
import ServerSide.DataCache;

public class GoogleMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private GoogleMap map;
    private HashMap<Marker, Event> markers;
    private float[] colors = {210, 330, 120, 270, 240, 30, 300, 60, 0, 180};
    private int currColor = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        View view = layoutInflater.inflate(R.layout.map_container, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);

        addMarkers();
        // Add a marker in Sydney and move the camera
    }

    @Override
    public void onMapLoaded() {
        // You probably don't need this callback. It occurs after onMapReady and I have seen
        // cases where you get an error when adding markers or otherwise interacting with the map in
        // onMapReady(...) because the map isn't really all the way ready. If you see that, just
        // move all code where you interact with the map (everything after
        // map.setOnMapLoadedCallback(...) above) to here.
    }



    private void addMarkers() {
        markers = new HashMap<>();
        DataCache cache = DataCache.getInstance();
        HashMap<String, Event> events = cache.getEvents();
        HashMap<String, Float> colorKey = new HashMap<>();

        Iterator it = events.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry tmp = (Map.Entry)it.next();
            Event event = (Event) tmp.getValue();

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
            markers.put(marker, event);
        }
    }
}
