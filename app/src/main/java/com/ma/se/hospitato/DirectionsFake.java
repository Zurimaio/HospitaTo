package com.ma.se.hospitato;

import android.app.Activity;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class DirectionsFake {

    public JSONObject toMauriziano;
    public JSONObject toMolinette;
    public JSONObject toMargherita;
    public JSONObject toSantAnna;
    public HashMap<String, String> directions;
    public Activity activity;

    public DirectionsFake(Activity activity){
        this.activity = activity;
        Directions(); //get the direction fake
        this.toMauriziano = toJSONObject(directions.get("Mauriziano"));
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

        String mauriziano = loadJSONFromAsset();
        directions = new HashMap<>();
        directions.put("Mauriziano", mauriziano);


        return directions;
    }



    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = activity.getAssets().open("directions.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
