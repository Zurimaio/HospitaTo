package com.ma.se.hospitato;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private int REQUEST_CHECK_SETTINGS = 2;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    protected Location currentPos;
    protected List<String> destinations = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_CHECK_SETTINGS);
        setContentView(R.layout.activity_main);

        //Used temporary to go directly to the map view
        Button mapView = findViewById(R.id.toMapView);
        toMapViewButton(mapView);

        /*
        if(startService()){
            Toast.makeText(this,"AsyncTask started",
                    Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"AsyncTask NOT started",
                    Toast.LENGTH_LONG);
        }
        */

    }



    public void toMapViewButton(Button mapView) {

        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMapView = new Intent(MainActivity.this, MapView.class);
                startActivity(toMapView);
            }
        });
    }




    public boolean startService() {
        try {
            //new AsyncGetDirectionTask().execute(getApplicationContext(), MainActivity.this);
            return true;
        } catch (Exception error) {
            Log.e("Error", "AsyncTask");
            return false;
        }
    }


    }

