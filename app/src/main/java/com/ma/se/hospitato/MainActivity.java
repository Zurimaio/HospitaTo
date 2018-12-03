package com.ma.se.hospitato;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.w3c.dom.Text;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {
    private int MY_LOCATION_REQUEST_CODE = 1;
    private int REQUEST_CHECK_SETTINGS = 2;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private String latitude;
    private String longitude;
    protected Location currentPos;
    TextView lat;
    TextView log;

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

        //TODO temporary value for latitude and longitude
        lat = findViewById(R.id.latitude);
        log = findViewById(R.id.longitude);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d("Location", "No location result");
                    return;
                }else{
                    Log.d("Location Result", "found");
                    setCurrentPos(locationResult.getLocations().get(0));
                    lat.setText(Double.toString(getCurrentPos().getLatitude()));
                    log.setText(Double.toString(getCurrentPos().getLongitude()));

                }

            }
        };

        //getLastLocation();


    }

    //TODO Temporary to go to the map view
    public void toMapViewButton(Button mapView) {
        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMapView = new Intent(MainActivity.this, MapView.class);
                startActivity(toMapView);
            }
        });
    }




    public void getLastLocation() {
        //checking if the permissions are satisfied
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            Log.d("Location", "Obtained");
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener((Executor) this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Log.d("Location", "Obtained");
                                latitude = Double.toString(location.getLatitude());
                                longitude = Double.toString(location.getLongitude());
                            }
                        }
                    });
        } else {
            // Show rationale and request permission.
            Log.d("Location", "No permession     ");
        }
    }

    protected void createLocationRequest() {
        Log.d("Location request", "Making the location request");
        mLocationRequest =  LocationRequest.create();
        mLocationRequest.setInterval(30000);
        //trmLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.

                try{
                    mFusedLocationClient.requestLocationUpdates(
                            mLocationRequest,mLocationCallback,null);
                    Log.d("Location Settings", "Satisfied");
                }catch(SecurityException e){
                    Log.e("SECURITY EXCEPTION", "Bad request");
                }
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.

                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                        Log.e("ERROR", "Start Resolution For result has been ignored");
                    }
                }
            }
        });
    }



    public Location getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(Location currentPos) {
        this.currentPos = currentPos;
    }



}

