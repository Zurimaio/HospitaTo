package com.ma.se.hospitato;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MapView extends FragmentActivity implements OnMapReadyCallback {

    private Double lat;
    private Double log;
    private GoogleMap mMap;
    private HashMap<String, Object> result;
    private LatLng origin;
    private LatLng dest;
    private List<LatLng> route;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        Thread th = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    result = new AsyncGetDirectionTask(MapView.this, mMap).execute(getApplicationContext(), MapView.this).get();
                    origin = (LatLng) result.get("origin");
                    dest =  (LatLng) result.get("dest");
                    route = (List<LatLng>) result.get("Route");
                }catch (ExecutionException e){
                    e.printStackTrace();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        th.start();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(origin).title("Your Position"));
        mMap.addMarker(new MarkerOptions().position(dest).title("Destination"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 10));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        mMap.addPolyline(new PolylineOptions().addAll(route));
    }



}
