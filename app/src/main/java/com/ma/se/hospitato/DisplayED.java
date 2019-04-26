package com.ma.se.hospitato;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.logging.StreamHandler;


public class DisplayED extends Fragment implements OnMapReadyCallback {

    private Hospital Hospitals;
    private TextView Name;
    private TextView Address;
    private TextView PhoneNumber;
    private TextView TravelTime;
    private TextView WaitNotAvailable ;
    private TextView TreatNotAvailable ;

    private HashMap<String, Boolean> Departments;
    private HashMap<String, String> Coordinate;
    private static DisplayED fragment;
    private TextView redWaiting;
    private TextView yellowWaiting;
    private TextView greenWaiting;
    private TextView whiteWaiting;

    private TextView redTreat;
    private TextView yellowTreat;
    private TextView greenTreat;
    private TextView whiteTreat;

    HashMap<String, String> waiting = new HashMap<>();
    HashMap<String, String> treatment= new HashMap<>();
    Utility utility;

    String hospitalName;
    String travTime;
    MapView mapView;
    LatLng loc;
    GoogleMap map;

    public void setED(Hospital Hospitals) {this.Hospitals=Hospitals;}
    public Hospital getED(){return this.Hospitals;}
    public DisplayED(){}

    public static DisplayED newInstance(Hospital Hospitals){
        Log.d("mostra",Hospitals.getName());
        fragment = new DisplayED();
        Bundle args = new Bundle();
        args.putParcelable("Hospitals", Hospitals);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Hospitals = getArguments().getParcelable("Hospital");
        travTime = getArguments().getString("TravelTime");

        Log.d("FILTER TRAVEL TIME", travTime);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display_ed,container,false);
        Name=view.findViewById(R.id.ED_name_f);
        Address=view.findViewById(R.id.ED_address_f);
        PhoneNumber=view.findViewById(R.id.PhoneNumber);
        TravelTime=view.findViewById(R.id.travel_time);
        WaitNotAvailable = view.findViewById(R.id.data_not_available_wait);
        TreatNotAvailable = view.findViewById(R.id.data_not_available_treat);

        redWaiting = view.findViewById(R.id.RedTag);
        yellowWaiting = view.findViewById(R.id.YellowTag);
        greenWaiting = view.findViewById(R.id.GreenTag);
        whiteWaiting = view.findViewById(R.id.WhiteTag);

        redTreat = view.findViewById(R.id.RedTreat);
        yellowTreat= view.findViewById(R.id.YellowTreat);
        greenTreat = view.findViewById(R.id.GreenTreat);
        whiteTreat = view.findViewById(R.id.WhiteTreat);

        Name.setText(Hospitals.getName());
        Address.setText(Hospitals.getAddress());
        PhoneNumber.setText(Hospitals.getPhoneNumber());
        setPeopleInPS();
        setLoc(new LatLng(
                Double.parseDouble(Hospitals.getCoordinate().get("Latitude")),
                Double.parseDouble(Hospitals.getCoordinate().get("Longitude"))
                ));
        hospitalName = Hospitals.getName();
        mapView =view.findViewById(R.id.lite_map);


        if (mapView != null) {
            mapView.onCreate(null);
            mapView.getMapAsync(this);
        }

        //Retrieve TravelTime

        TravelTime.setText(travTime);

        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        map = googleMap;
        setMapLocation();
    }

    public void setMapLocation(){
        if(map==null) return;
        //LatLng l = new LatLng(45.0504965,7.6636196);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(getLoc(), 15f));
        map.addMarker(new MarkerOptions().position(getLoc()).title(hospitalName)).showInfoWindow();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    public LatLng getLoc() {
        return loc;
    }

    public void setLoc(LatLng loc) {
        this.loc = loc;
    }


    private void setPeopleInPS() {

        new Thread() {
            public void run() {
                    boolean suc = Utility.peopleInPS(getContext(), Hospitals.getName());
                    HashMap<String, HashMap> result = new HashMap<>();
                    if(suc) {
                        try {
                            while (result.isEmpty()) {
                                Thread.sleep(300);
                                result = Utility.getPeopleInPS();
                                Log.d("PeopleInED", "Waiting");
                            }
                            waiting = result.get("waiting");
                            treatment = result.get("treatment");
                            System.out.println(waiting);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    redWaiting.setText(waiting.get("rosso"));
                                    yellowWaiting.setText(waiting.get("giallo"));
                                    greenWaiting.setText(waiting.get("verde"));
                                    whiteWaiting.setText(waiting.get("bianco"));

                                    redTreat.setText(treatment.get("rosso"));
                                    yellowTreat.setText(treatment.get("giallo"));
                                    greenTreat.setText(treatment.get("verde"));
                                    whiteTreat.setText(treatment.get("bianco"));
                                }
                            });

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                redWaiting.setVisibility(View.GONE);
                                yellowWaiting.setVisibility(View.GONE);
                                greenWaiting.setVisibility(View.GONE);
                                whiteWaiting.setVisibility(View.GONE);

                                redTreat.setVisibility(View.GONE);
                                yellowTreat.setVisibility(View.GONE);
                                greenTreat.setVisibility(View.GONE);
                                whiteTreat.setVisibility(View.GONE);

                                TreatNotAvailable.setVisibility(View.VISIBLE);
                                WaitNotAvailable.setVisibility(View.VISIBLE);
                            }
                        });

                        Log.e("PEOPLEINPS", "BAD NAME");
                        return;
                    }
                }

        }.start();

    }

}
