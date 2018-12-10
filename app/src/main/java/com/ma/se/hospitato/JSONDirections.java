package com.ma.se.hospitato;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONDirections extends JSONObject {

    private JSONObject json;
    private JSONObject routes;
    private JSONObject legs;
    private JSONObject distance;
    private  JSONObject duration;

    private String distanceString;
    private Integer distanceValue;

    private String durationString;
    private Integer durationValue;

    private String start_address;
    private String end_address;



    public JSONDirections() {
    }


    public JSONDirections(JSONObject json) throws  JSONException{
        setJson(json);
        setRoutes((JSONObject) json.getJSONArray("routes").get(0));
        setLegs((JSONObject) getRoutes().getJSONArray("legs").get(0));
        setDistance((JSONObject)getLegs().get("distance"));
        setDuration((JSONObject) getLegs().get("duration"));

        setDurationString(getDuration().getString("text"));
        setDurationValue(getDuration().getInt("value"));

        setDistanceString(getDistance().getString("text"));
        setDistanceValue(getDistance().getInt("value"));

        setStart_address(getLegs().getString("start_address"));
        setEnd_address(getLegs().getString("end_address"));

    }

    public JSONDirections(String json) throws JSONException {
        super(json);
    }

    public String getDistanceString() {
        return distanceString;
    }

    public void setDistanceString(String distanceString) {
        this.distanceString = distanceString;
    }

    public Integer getDistanceValue() {
        return distanceValue;
    }

    public void setDistanceValue(Integer distanceValue) {
        this.distanceValue = distanceValue;
    }

    public String getDurationString() {
        return durationString;
    }

    public void setDurationString(String durationString) {
        this.durationString = durationString;
    }

    public Integer getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(Integer durationValue) {
        this.durationValue = durationValue;
    }

    public String getStart_address() {
        return start_address;
    }

    public void setStart_address(String start_address) {
        this.start_address = start_address;
    }

    public String getEnd_address() {
        return end_address;
    }

    public void setEnd_address(String end_address) {
        this.end_address = end_address;
    }

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    public JSONObject getRoutes() {
        return routes;
    }

    public void setRoutes(JSONObject routes) {
        this.routes = routes;
    }


    public JSONObject getLegs() {
        return legs;
    }

    public void setLegs(JSONObject legs) {
        this.legs = legs;
    }

    public JSONObject getDistance() {
        return distance;
    }

    public void setDistance(JSONObject distance) {
        this.distance = distance;
    }

    public JSONObject getDuration() {
        return duration;
    }

    public void setDuration(JSONObject duration) {
        this.duration = duration;
    }
}

