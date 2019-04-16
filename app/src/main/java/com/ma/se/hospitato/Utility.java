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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utility {
    public static final String MAURIZIANO = "Mauriziano";
    public static final String MOLINETTE = "Molinette";
    public static final String MARIA_VITTORIA = "Maria Vittoria";
    public static final String SAN_GIOVANNI_BOSCO= "San Giovanni Bosco";
    public static final String CTO = "CTO";
    public static final String SANT_ANNA = "Sant'Anna";
    public static final String REGINA_MARGHERITA = "Regina Margherita";
    public static final String MARTINI = "Martini";
    public static JSONDirections res;
    public static final JSONObject json = new JSONObject();


    static public void requestDirection(String origin, String destination, Context context){
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONDirections data;

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
                        try {
                            res = new JSONDirections(response);

                        }catch (JSONException jx){
                            jx.printStackTrace();
                        }
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




    static public JSONArray loadJSONFromRes(Activity activity) {
        String json = null;
        JSONArray jsonArray = null;

        try {
            InputStream is = activity.getResources().openRawResource(R.raw.directions);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            jsonArray = new JSONArray(json);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }catch (JSONException jx){
            jx.printStackTrace();
            return null;
        }


        return jsonArray;
    }

    public static JSONDirections getRes() {
        return res;
    }

    public static void setRes(JSONDirections res) {
        Utility.res = res;
    }


    public static void requestTestData(Context context){
        HashMap<String,String> links = new HashMap<>();
        String Molinette = "01090101";
        String CTO = "01090201";
        String SantAnna = "01090301";
        String Margherita = "01090302";
        final String link = "http://listeps.cittadellasalute.to.it/gtotal.php?id=";


        links.put("Molinette",link+Molinette);
        links.put("CTO", link+CTO);
        links.put("SantAnna", link+SantAnna);
        links.put("Margherita", link+Margherita);
        RequestQueue queue = Volley.newRequestQueue(context);


        for(Map.Entry<String,String> entry: links.entrySet()) {
            final HashMap<String, JSONArray> res = new HashMap<>();
            final String hospital = entry.getKey();
            String url = entry.getValue();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean bianco = false;
                                boolean verde = false;
                                boolean giallo = false;
                                boolean rosso = false;
                                JSONObject json;
                                JSONArray array = response.getJSONArray("colors");
                                JSONObject obj = new JSONObject();

                                for(int i = 0; i < array.length(); i++){
                                    json = array.getJSONObject(i);
                                    if(json.get("colore").equals(null)){
                                        array.remove(i);
                                        break;
                                    }
                                    if(json.get("colore").equals("bianco")){
                                        bianco = true;
                                    }
                                    if(json.get("colore").equals("verde")){
                                        verde = true;
                                    }
                                    if(json.get("colore").equals("giallo")){
                                        giallo = true;
                                    }
                                    if(json.get("colore").equals("rosso")){
                                        rosso = true;
                                    }
                                }
                                if(!bianco){
                                    obj.put("colore", "bianco");
                                    obj.put("attesa", "0");
                                    obj.put("visita", "0");
                                    array.put(obj);
                                }

                                if(!giallo){
                                    obj.put("colore", "giallo");
                                    obj.put("attesa", "0");
                                    obj.put("visita", "0");
                                    array.put(obj);
                                }
                                if(!verde){
                                    obj.put("colore", "verde");
                                    obj.put("attesa", "0");
                                    obj.put("visita", "0");
                                    array.put(obj);
                                }
                                if(!rosso){
                                    obj.put("colore", "rosso");
                                    obj.put("attesa", "0");
                                    obj.put("visita", "0");
                                    array.put(obj);
                                }


                                res.put(hospital, array);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            System.out.println(res);
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle
                            Log.d("Request Error", error.toString());
                        }
                    });

            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest);
        }


    }
}
