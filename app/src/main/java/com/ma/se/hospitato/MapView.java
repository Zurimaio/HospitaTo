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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MapView extends FragmentActivity implements OnMapReadyCallback {

    private int REQUEST_CHECK_SETTINGS = 2;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private Double lat;
    private Double log;
    private GoogleMap mMap;
    private String res;
    private HashMap<String, Double> pos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Thread thread = new Thread(){
            @Override
            public void run() {
                Log.d("Starting Thread", "started");
                try {
                    res = new AsyncGetDirectionTask().execute(getApplicationContext(), MapView.this).get();
                    pos = Utility.fromStringToCoord(res);
                    setLat(pos.get("lat"));
                    setLog(pos.get("lng"));
                }catch (Exception e){
                    Log.e("Thread Error", "No value");
                    e.printStackTrace();
                }
            }
        };
        thread.start();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Thread th = new Thread() {
            @Override
            public void run() {
                while (res == null) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("Waiting");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                System.out.println("COORDINATESSSSSSSSSSSSSSSSSSSSSSSSSSSSS");


            }
        };
        th.start();
        /**
         * TODO get the last location know and then update the location
         *
        LatLng pos = new LatLng(getLat(), getLog());
        mMap.addMarker(new MarkerOptions().position(pos).title("Your Position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
        */
    }




    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLog() {
        return log;
    }

    public void setLog(Double log) {
        this.log = log;
    }



    public boolean startService() {
        try {
            //String res = new AsyncGetDirectionTask().execute(getApplicationContext(), MapView.this).get();
            //Log.d("Map View", res);
            return true;
        } catch (Exception error) {
            Log.e("Error", "AsyncTask");
            return false;
        }
    }
}
