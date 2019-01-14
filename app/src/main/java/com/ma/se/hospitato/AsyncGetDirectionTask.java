package com.ma.se.hospitato;
import android.app.Activity;

import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AsyncGetDirectionTask extends AsyncTask<Object, Void, HashMap<String,Object>> {
    private int REQUEST_CHECK_SETTINGS = 2;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private List<String> destinations = new ArrayList<>();
    public Location currentPos;
    private MapView mapView;
    private Activity activity;
    public Context context;
    public String origin = "";
    public String dest = "";
    private boolean found = false;
    private DirectionsFake directionsFake;
    private SupportMapFragment mapFragment;
    private HashMap<String, Object> res= new HashMap<>();;
    private HashMap<String, String> hospitalDestination;
    private HashMap<String, JSONDirections> jsonDestination = new HashMap<>();
    private MainList.MyAdapter myAdapter;
    private List<Hospital> listHospital;
    private int act; //0 - Map View, 1 - MainActivity, 2 - Map View for displaying hospital
    JSONArray directions;
    private Double lat;
    private Double lng;


    public AsyncGetDirectionTask(FragmentActivity activity, int act){
        mapFragment = (SupportMapFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.map);
             hospitalDestination = new HashMap<>();
        this.act = act; // 0 - MAPView for drawing, 2 - MapView for displaying hospital
    }

    public AsyncGetDirectionTask(MainList.MyAdapter myAdapter, List<Hospital> listHospital){
        this.myAdapter= myAdapter;
        this.listHospital = listHospital;
        this.act = 1; //MainActivity
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d("AsyncTask", "Started");
    }

    @Override
    protected HashMap<String, Object> doInBackground(Object... objects) {
        context = (Context) objects[0];
        if(this.act == 0) {
            Log.d("Action", "Get Routes");
            mapView = (MapView) objects[1];
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mapView);
            directions = Utility.loadJSONFromRes(mapView);
            createLocationRequest(mapView);
            getDrawPathValues();
        }else if(this.act == 1){
            Log.d("Action", "Get Travel Time");
            activity = (Activity) objects[1];
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
            directions = Utility.loadJSONFromRes(activity);
            createLocationRequest(activity);
            getOnlyTravelTime();
        }


        try {
            /**
             * TODO the request is made in a fake way till now, therefore the returned object is local.
             * getResponseFromRequest(origin, destination, context);
             * res.put("Directions", jsonDirections);
             */

            /**
             * TODO the following for will be iterated in order to perform several "request Direction"
             * obtaining an array of request
             */
            int i;
            for (i =0; i<directions.length(); i++){
                JSONDirections data = new JSONDirections(directions.getJSONObject(i));
                jsonDestination.put(Utility.MAURIZIANO, data);
                jsonDestination.put(Utility.MOLINETTE, data);
                //res.put(data.get)
            }
            res.put("Directions", jsonDestination);
            res.put("travelTime",jsonDestination.get(Utility.MAURIZIANO).getDurationString() );
            res.put("Hospitals", hospitalDestination);
            //res.put("Route", data.getPolyPath());

         }catch (Exception e){
            Log.e("ERROR", "Not able to find the field");
            e.printStackTrace();
        }
        return res;
    }


    @Override
    protected void onPostExecute(HashMap aVoid) {
        super.onPostExecute(aVoid);
        if(this.act == 0)
            mapFragment.getMapAsync(mapView);
        else if(this.act == 1) {
            //update List View
            //myAdapter.travelTime = jsonDestination.get(Utility.MAURIZIANO).getDurationString();
            System.out.println("Done");

        }
    }

    public void setPosition() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d("Location", "No location result");
                    found = false;
                } else {
                    Log.d("Location Result", "found");
                    setCurrentPos(locationResult.getLocations().get(0));
                    //origin = Utility.fromDoubleToStringCoord(getCurrentPos().getLatitude(), getCurrentPos().getLongitude());
                    /**
                     * TODO simulated position to eliminate
                     */
                    origin = "45.072899" + "," + "7.670697";
                    found = true;
                }
            }
        };
    }

    protected void createLocationRequest(final Activity activity) {
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
                } catch (NullPointerException np){
                    np.printStackTrace();
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
                        resolvable.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                        Log.e("ERROR", "Start Resolution For result has been ignored");
                    }
                }
            }
        });
    }


    public void getHospitals() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Hospitals");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Hospital h = d.getValue(Hospital.class);
                    String dest = h.getCoordinate().get("Latitude") + "," + h.getCoordinate().get("Longitude");
                    destinations.add(dest);
                    hospitalDestination.put(h.getName(), dest);

                }
                /**
                 * TODO understand how choose the right destination
                 */
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

    /**
     * TODO to be used when the request will be done by means DIRECTION_API
     * @param origin
     * @param data
     * @param context
     */
    public void getResponseFromRequest(String origin, HashMap<String, String> data, Context context){

        Iterator i = data.entrySet().iterator();
        while(i.hasNext()){
            //get the OD response for each destination
            Map.Entry pair = (Map.Entry) i.next();
            String hospitalName = (String) pair.getKey();
            String dest = (String) pair.getValue();
            Utility.requestDirection(origin,dest,context);
            jsonDestination.put(hospitalName, Utility.getRes());
        }


    }



    public void getOnlyTravelTime(){
        while (!found){
            try {
                System.out.println("Searching current position... ");
                setPosition();
                Thread.sleep(500);
            }catch (InterruptedException ie){
                ie.printStackTrace();
            }
        }
    }


    public void getDrawPathValues(){
        while (origin.equals("") || dest.equals("")) {
            try {
                System.out.println("Waiting");
                setPosition();
                getHospitals();
                Thread.sleep(3000);
            } catch (Exception e) {
                Log.e("Thread", "Error");
                break;
            }
        }

        setLat(Double.parseDouble(origin.split(",")[0]));
        setLng(Double.parseDouble(origin.split(",")[1]));
        LatLng origin = new LatLng(getLat(), getLng());
        res.put("origin", origin);

        setLat(Double.parseDouble(dest.split(",")[0]));
        setLng(Double.parseDouble(dest.split(",")[1]));
        LatLng dest = new LatLng(getLat(), getLng());
        res.put("dest", dest);

    }


    /**
     * Getter and Setter
     * @return
     */

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }


    public List<String> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<String> destinations) {
        this.destinations = destinations;
    }


    public void setCurrentPos(Location currentPos) {
        this.currentPos = currentPos;
    }

    public Location getCurrentPos() {
        return currentPos;
    }

}

