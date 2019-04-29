package com.ma.se.hospitato;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseLocalModel;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.common.modeldownload.FirebaseRemoteModel;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
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

    private Button greenWaitingTime;
    private Button whiteWaitingTime;

    HashMap<String, String> waiting = new HashMap<>();
    HashMap<String, String> treatment= new HashMap<>();
    Utility utility;
    HashMap<String, HashMap> resultPrev;
    float waitingWhite = -1;
    float waitingGreen = -1;

    String hospitalName;
    String travTime;
    MapView mapView;
    LatLng loc;
    GoogleMap map;
    int millSleep = 250;

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
        /*
        waitingWhite = getArguments().getString("WhiteTime");
        waitingGreen = getArguments().getString("GreenTime");
        waiting = (HashMap) getArguments().getSerializable("WaitingPeople");
        treatment = (HashMap) getArguments().getSerializable("TreatmentPeople");
        */
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

        greenWaitingTime = view.findViewById(R.id.G_waitingTime_f);
        whiteWaitingTime = view.findViewById(R.id.W_waitingTime_f);
        Name.setText(Hospitals.getName());
        Address.setText(Hospitals.getAddress());
        PhoneNumber.setText(Hospitals.getPhoneNumber());
        setPeopleInPS(Hospitals.getName());

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

        //Retrieve info from bundle
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

    private void setPeopleInPS(final String hospitalName) {
        new Thread() {
            public void run() {
                    boolean suc = Utility.peopleInPS(getContext(), hospitalName);
                    HashMap<String, HashMap> result = new HashMap<>();

                if(suc) {
                        try {
                            while (result.isEmpty()) {
                                Thread.sleep(millSleep);
                                result = Utility.getPeopleInPS();
                                Log.d("PeopleInED", "Waiting");
                            }

                            waiting = result.get("waitingPeople");
                            treatment = result.get("treatmentPeople");
                            System.out.println("Waiting " + waiting.toString());
                            System.out.println("Treatment " + treatment.toString());

                            requestToModel(Hospitals.getName());
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
                                whiteWaitingTime.setText(R.string.data_not_avaialable);
                                greenWaitingTime.setText(R.string.data_not_avaialable);

                            }
                        });

                        Log.e("PEOPLEINPS", "BAD NAME");
                        return;
                    }
                }

        }.start();

    }




    public HashMap<String, String> chooseModel(String hospitalName){
        HashMap<String, String> model = new HashMap<>();

        String waitingWhite = "";
        String waitingGreen = "";

        if(hospitalName.contains(Utility.MOLINETTE)){
            waitingWhite = "molinette";
            waitingGreen = "molinette_verde";
        }
        if(hospitalName.contains(Utility.REGINA_MARGHERITA)){
            waitingWhite = "margherita";
            waitingGreen = "margherita_verde";
        }
        if(hospitalName.contains(Utility.CTO)){
            waitingWhite = "CTO";
            waitingGreen = "CTO_verde";
        }
        if(hospitalName.contains(Utility.MARIA_VITTORIA)){
            waitingWhite = "vittoria";
            waitingGreen = "vittoria_verde";
        }
        if(hospitalName.contains(Utility.MARTINI)){
            waitingWhite = "martini";
            waitingGreen = "martini_verde";
        }
        if(hospitalName.contains(Utility.MAURIZIANO)){
            waitingWhite = "mauriziano";
            waitingGreen = "maurziano_verde";
        }
        if(hospitalName.contains(Utility.SAN_GIOVANNI_BOSCO)){
            waitingWhite = "bosco";
            waitingGreen = "bosco_verde";
        }
        if(hospitalName.contains(Utility.SANT_ANNA)){
            waitingWhite = "anna";
            waitingGreen = "anna_verde";
        }
        model.put("White", waitingWhite);
        model.put("Green", waitingGreen);

        return model;
    }

    public void requestToModel(final String localModel){
        final HashMap<String, String> model = chooseModel(localModel);
        Log.d("MODEL", model.toString());
        //CHOOSE THE MODEL TO QUERY

        new Thread(){
            @Override
            public void run() {
                super.run();
                String whiteModel = model.get("White");
                model(whiteModel, "White");
                try{
                    while(waitingWhite == -1){
                        Thread.sleep(millSleep-50);
                        Log.d("White waitingPeople time", "sleeping");
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            whiteWaitingTime.setText(
                                    String.format(Locale.getDefault(),
                                            "%d min",
                                            Math.round(waitingWhite)));
                        }
                    });
                }catch (Exception e){
                    Log.e("ERROR", "Waiting White");
                    e.printStackTrace();
                }
            }
        }.start();


        new Thread(){
            @Override
            public void run() {
                super.run();
                String greenModel = model.get("Green");
                model(greenModel, "Green");
                try{
                    while(waitingGreen == -1){
                        Thread.sleep(millSleep-50);
                        Log.d("Green waitingPeople time", "sleeping");
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            greenWaitingTime.setText(
                                    String.format(Locale.getDefault(),
                                            "%d min",
                                            Math.round(waitingGreen)));
                        }
                    });
                }catch (Exception e){
                    Log.e("ERROR", "Waiting Green");
                    e.printStackTrace();
                }
            }
        }.start();


    }

    public float[][] toIntegerArrayFromHashMap(HashMap<String, String> waiting, HashMap<String, String> treatment){
        String[] wait = waiting.values().toArray(new String[4]);
        String[] treat = treatment.values().toArray(new String[4]);
        float[][] array =new float[1][wait.length+treat.length+1];

        for(int i = 0; i < array.length; i++) {
            for (String str : wait)
                array[0][i++] = Float.parseFloat(str);

            for (String str : treat)
                array[0][i++] = Float.parseFloat(str);
        }
         /*
        Python          Java
        Lunedì      0 - 2
        Martedì     1 - 3
        Mercoledì   2 - 4
        Giovedì     3 - 5
        Venerdì     4 - 6
        Sabato      5 - 7
        Domenica    6 - 1
         */

        float day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        //transforming day of week from java to python
        day = day - 2;
        array[0][8] = day;
        return array;
    }

    public void model(String localModel, final String color){
        String localModelPath = localModel + ".tflite";

        FirebaseLocalModel localSource =
                new FirebaseLocalModel.Builder(localModel)  // Assign a name to this model
                        .setAssetFilePath(localModelPath)
                        .build();

        FirebaseModelManager.getInstance().registerLocalModel(localSource);
        FirebaseModelOptions options = new FirebaseModelOptions.Builder().setLocalModelName(localModel).build();
        try {
            FirebaseModelInterpreter firebaseInterpreter = FirebaseModelInterpreter.getInstance(options);
            FirebaseModelInputOutputOptions inputOutputOptions =
                    new FirebaseModelInputOutputOptions.Builder()
                            .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 9})
                            .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 1})
                            .build();

            float[][] input = toIntegerArrayFromHashMap(waiting, treatment);

            //query to the model
            FirebaseModelInputs inputs = new FirebaseModelInputs.Builder()
                    .add(input)  // add() as many input arrays as your model requires
                    .build();
            firebaseInterpreter.run(inputs, inputOutputOptions)
                    .addOnSuccessListener(
                            new OnSuccessListener<FirebaseModelOutputs>() {
                                @Override
                                public void onSuccess(FirebaseModelOutputs result) {

                                    float[][] output = result.getOutput(0);
                                    System.out.println("Output");
                                    float out = output[0][0];
                                    if(color.equals("White"))
                                        waitingWhite = out;
                                    else if(color.equals("Green"))
                                        waitingGreen = out;

                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception
                                    // ...
                                    System.out.println("SCAAAAAAAAAAAASSSSOOO");
                                    e.printStackTrace();
                                    e.getCause();
                                }
                            });

        }catch (FirebaseMLException e){
            Log.e("ERROR", "InputOutput");
            e.printStackTrace();
        }


    }


}
