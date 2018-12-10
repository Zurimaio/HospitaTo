package com.ma.se.hospitato;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.List;

public class AsyncGetDirectionTask extends AsyncTask<Object, Void, Void> {
    private int REQUEST_CHECK_SETTINGS = 2;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    protected List<String> destinations = new ArrayList<>();
    public Location currentPos;
    public MapView activity;
    public Context context;
    public String origin = "";
    public String dest = "";
    Double lat = null;
    Double lng = null;
    private boolean fromMap = true;
    DirectionsFake directionsFake;
    SupportMapFragment mapFragment;




    public AsyncGetDirectionTask(FragmentActivity activity){
        mapFragment = (SupportMapFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.map);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Object... objects) {
        /**
         * Set up variables for location task
         */

        Log.d("AsincTask", "doing in background");
        context = (Context) objects[0];
        activity = (MapView) objects[1];

        directionsFake = new DirectionsFake(activity);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        createLocationRequest();


        while (origin.equals("") || dest.equals("")) {
            try {
                System.out.println("Waiting");
                setPosition();
                getDestination();
                Thread.sleep(1000);
            } catch (Exception e) {
                Log.e("Thread", "Error");
                break;
            }
        }

        return null;

    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
                /**
         * TODO the request is made in a fake way 'till now, therefore the returned obejct is local.
         * Utility.requestDirection(origin, dest, context);
         */
        Log.d("Real Origin-Destination", origin);
        Log.d("Real Origin-Destination", dest);
        //activity.setLog(lat);
        mapFragment.getMapAsync(activity);
        //LatLng pos = new LatLng(getLat(), getLog());
        //mMap.addMarker(new MarkerOptions().position(pos).title("Your Position"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));

        /**
         * TODO whenever will be possible to draw on map
         *
        try {
            JSONDirections data = new JSONDirections(directionsFake.toMauriziano);
            Log.d("Mauriziano", "Distance " + data.getDistanceString());
            Log.d("Mauriziano", "Duration " +  data.getDurationString());
            Log.d("Mauriziano", "Start Address " + data.getStart_address());
            Log.d("Mauriziano", "End Address " + data.getEnd_address());
        }catch (Exception e){
            Log.e("ERROR", "Not able to find the field");
            e.printStackTrace();
        }
         */

    }

    public void setPosition() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d("Location", "No location result");
                    return;
                } else {
                    Log.d("Location Result", "found");
                    setCurrentPos(locationResult.getLocations().get(0));
                    origin = Utility.fromDoubleToStringCoord(getCurrentPos().getLatitude(), getCurrentPos().getLongitude());

                }

            }
        };
    }

    public List<String> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<String> destinations) {
        this.destinations = destinations;
    }

    public Location getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(Location currentPos) {
        this.currentPos = currentPos;
    }


    protected void createLocationRequest() {
        Log.d("Location request", "Making the location request");
        mLocationRequest = LocationRequest.create();
        //mLocationRequest.setInterval(5000);
        //mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.

                try {
                    mFusedLocationClient.requestLocationUpdates(
                            mLocationRequest, mLocationCallback, null);
                    Log.d("Location Settings", "Satisfied");
                } catch (SecurityException e) {
                    Log.e("SECURITY EXCEPTION", "Bad request");
                }
            }
        });

        task.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.

                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(activity,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                        Log.e("ERROR", "Start Resolution For result has been ignored");
                    }
                }
            }
        });
    }


    public void getDestination() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Hospitals");
        //List<String> destinations = new ArrayList<>();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Hospital h = d.getValue(Hospital.class);
                    String dest = h.getCoordinate().get("Latitude") + "," + h.getCoordinate().get("Longitude");
                    destinations.add(dest);
                }
                setDestinations(destinations);
                dest = getDestinations().get(0);
                //Log.d("Dest", dest);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                return;
            }
        });


    }





}

