package com.ma.se.hospitato;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Utility {

    static public void requestDirection(String origin, String destination, Context context){
        RequestQueue queue = Volley.newRequestQueue(context);
        final JSONObject res = new JSONObject();

        /**
         * Sample request
         * https://maps.googleapis.com/maps/api/directions/json?origin=Toronto&destination=Montreal&key=YOUR_API_KEY
         */

        String format = "json";
        String DIRECTION_API_KEY = "AIzaSyANzbEFTwOhnhHftnLx69rt2IlZeL-O5xs";
        String request = "https://maps.googleapis.com/maps/api/directions/"
                + format + "?"
                + "origin=" + origin + "&"
                + "destination=" + destination + "&"
                + "key=" +  DIRECTION_API_KEY;



        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, request, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Request", response.toString());

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle
                        Log.d("Request Error", error.toString());
                    }
                });


        queue.add(jsonObjectRequest);
    }

    static  public String fromDoubleToStringCoord(Double lat, Double log){
        String pos = Double.toString(lat) + ","+Double.toString(log);
        return pos;
    }

    static public HashMap<String, Double> fromStringToCoord(String coords){
        HashMap<String, Double> c = new HashMap<>();
        c.put("lat", Double.parseDouble(coords.split(",")[0]));
        c.put("lng", Double.parseDouble(coords.split(",")[1]));
        return c;

    }








    static  public List<String> getCoordinatesFromFB(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Hospitals");
        List<String> destinations = new ArrayList<>();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d: dataSnapshot.getChildren()) {
                    Hospital h = d.getValue(Hospital.class);
                    String dest = h.getCoordinate().get("Latitude") + "," + h.getCoordinate().get("Longitude");
                    destinations.add(dest);

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                return;
            }
        });
        return destinations;

    }




}
