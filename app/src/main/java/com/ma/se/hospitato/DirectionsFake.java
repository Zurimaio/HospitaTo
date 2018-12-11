package com.ma.se.hospitato;

import android.app.Activity;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;


/**
 * TODO to be eliminated whenever the DIRECTION_API will be purchased
 */
public class DirectionsFake {

    public JSONObject toMauriziano;
    public JSONObject toMolinette;
    public JSONObject toMargherita;
    public JSONObject toSantAnna;
    public HashMap<String, String> directions;
    public Activity activity;

    public DirectionsFake(Activity activity){
        this.activity = activity;
        //Directions(); //get the direction fake
        //this.toMauriziano = toJSONObject(directions.get("Mauriziano")); //test
    }



    public JSONObject toJSONObject(String json){
        try {
            JSONObject hospital = new JSONObject(json);
            return hospital;
        }catch (Exception e){
            Log.e("Error JSON_object", e.getMessage());
            return null;
        }

    }





    public HashMap<String, String> Directions(){

        //String mauriziano = loadJSONFromRes();
        directions = new HashMap<>();
        //directions.put("Mauriziano", mauriziano);


        return directions;
    }



    public JSONArray loadJSONFromRes() {
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

}
