package com.ma.se.hospitato;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.maps.MapFragment;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MapView extends FragmentActivity implements OnMapReadyCallback {

    private Double lat;
    private Double log;
    private GoogleMap mMap;
    private HashMap<String, Object> result;
    private HashMap<String, Object> hospitals;
    private LatLng origin;
    private LatLng dest;
    private List<LatLng> route;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        Bundle b = getIntent().getExtras();
        if(b!= null){
            int act = (int) b.get("FromMain");
            System.out.println("Bundle");
            System.out.println(act);
            startAsyncTask(act);
        }



    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("Map", "Ready");
        //addMarkerForHospitals();

        //mMap.addMarker(new MarkerOptions().position(dest).title("Destination"));
        //mMap.addPolyline(new PolylineOptions().addAll(route).color(Color.BLUE).width(10));
    }

    public void startAsyncTask(int act){
        Thread th = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    result = new AsyncGetDirectionTask(MapView.this, 0).execute(getApplicationContext(), MapView.this).get();
                    origin = (LatLng) result.get("origin");
                    dest =  (LatLng) result.get("dest");
                    hospitals = (HashMap<String, Object>) result.get("Hospitals");
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






    public void addMarkerForHospitals() {
        mMap.addMarker(new MarkerOptions().position(origin).title("Your Position"));
        Iterator h = hospitals.entrySet().iterator();
        while (h.hasNext()) {
            Map.Entry pair = (Map.Entry) h.next();
            String hospitalName = (String) pair.getKey();
            String dest = (String) pair.getValue();
            Double lat = Double.parseDouble(dest.split(",")[0]);
            Double lng = Double.parseDouble(dest.split(",")[1]);
            LatLng hospital = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions().position(hospital).title(hospitalName));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 1));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);

    }

}
