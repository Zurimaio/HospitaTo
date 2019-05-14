package com.ma.se.hospitato;

import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


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
    float wWhite = -1;
    float wGreen = -1;

    String hospitalName;
    String travTime;
    MapView mapView;
    LatLng loc;
    GoogleMap map;
    int millSleep = 300;

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

    /*
    NEURAL NETWORK REQUEST
     */

    public String formatModelSelection(String hospitalName){
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        //transforming day of week from java to python
        if(day == Calendar.SUNDAY)
            day = 6;
        else
            day = day - 2;

        String WorkingDays = "";
        String timeSlot = "";
        if(day == 5 || day == 6){
            WorkingDays = "NW";
        }else{
            WorkingDays = "W";
        }
        /*

        T1: 8-14
        T2: 15-20
        T3: 21-7
         */
        if(hour >= 8 && hour <= 14){
                timeSlot = "T1";
        }else if(hour>= 15 && hour<=20) {
            timeSlot = "T2";
        }else{
            timeSlot = "T3";
        }

        return hospitalName+"_Model_"+timeSlot+"_"+WorkingDays;


    }



    public HashMap<String, String> chooseModel(String hospitalName){
        HashMap<String, String> model = new HashMap<>();


        String waitingWhite = "";
        String waitingGreen = "";

        if(hospitalName.contains(Utility.MOLINETTE)){
            waitingWhite = formatModelSelection("Molinette")+"_White";
            waitingGreen = formatModelSelection("Molinette")+"_Green";
        }
        if(hospitalName.contains(Utility.REGINA_MARGHERITA)){
            waitingWhite = formatModelSelection("Margherita")+"_White";
            waitingGreen = formatModelSelection("Margherita")+"_Green";
        }
        if(hospitalName.contains(Utility.CTO)){
            waitingWhite = formatModelSelection("CTO")+"_White";
            waitingGreen = formatModelSelection("CTO")+"_Green";
        }
        if(hospitalName.contains(Utility.MARIA_VITTORIA)){
            waitingWhite = formatModelSelection("Vittoria")+"_White";
            waitingGreen = formatModelSelection("Vittoria")+"_Green";
        }
        if(hospitalName.contains(Utility.MARTINI)){
            waitingWhite = formatModelSelection("Martini")+"_White";
            waitingGreen = formatModelSelection("Martini")+"_Green";
        }
        if(hospitalName.contains(Utility.MAURIZIANO)){
            waitingWhite = formatModelSelection("Mauriziano")+"_White";
            waitingGreen = formatModelSelection("Mauriziano")+"_Green";
        }
        if(hospitalName.contains(Utility.SAN_GIOVANNI_BOSCO)){
            waitingWhite = formatModelSelection("Bosco")+"_White";
            waitingGreen = formatModelSelection("Bosco")+"_Green";
        }
        if(hospitalName.contains(Utility.SANT_ANNA)){
            waitingWhite = formatModelSelection("SantAnna")+"_White";
            waitingGreen = formatModelSelection("SantAnna")+"_Green";
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
                String greenModel = model.get("Green");
                Model(greenModel, "Green");
                try{
                    while(wGreen == -1){
                        Thread.sleep(millSleep-50);
                        Log.d("Green waitingPeople time", "sleeping");
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            greenWaitingTime.setText(
                                    String.format(Locale.getDefault(),
                                            "%d min",
                                            Math.round(wGreen)));
                        }
                    });
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            String whiteModel = model.get("White");
                            Model(whiteModel, "White");
                            try{
                                while(wWhite == -1){
                                    Thread.sleep(millSleep);
                                    Log.d("White waitingPeople time", "sleeping");
                                }
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        whiteWaitingTime.setText(
                                                String.format(Locale.getDefault(),
                                                        "%d min",
                                                        Math.round(wWhite)));
                                    }
                                });
                            }catch (Exception e){
                                Log.e("ERROR", "Waiting White");
                                e.printStackTrace();
                            }
                        }
                    }.start();

                }catch (Exception e){
                    Log.e("ERROR", "Waiting Green");
                    e.printStackTrace();
                }
            }
        }.start();



    }

    public float[][] toIntegerArrayFromHashMap(HashMap<String, String> waiting, HashMap<String, String> treatment, String color){
        String[] wait = waiting.values().toArray(new String[4]);
        String[] treat = treatment.values().toArray(new String[4]);

        float[][] arrayW = new float[1][wait.length+treat.length];
        float[][] arrayG = new float[1][wait.length+treat.length-1];

         if(color.equals("White")) {
             arrayW[0][0] = Float.parseFloat(waiting.get("bianco"));
             arrayW[0][1] = Float.parseFloat(waiting.get("verde"));
             arrayW[0][2] = Float.parseFloat(waiting.get("giallo"));
             arrayW[0][3] = Float.parseFloat(waiting.get("rosso"));
             arrayW[0][4] = Float.parseFloat(treatment.get("bianco"));
             arrayW[0][5] = Float.parseFloat(treatment.get("verde"));
             arrayW[0][6] = Float.parseFloat(treatment.get("giallo"));
             arrayW[0][7] = Float.parseFloat(treatment.get("rosso"));
             //arrayW[0][8] = wGreen;
             return arrayW;
         }
         else {
             arrayG[0][0] = Float.parseFloat(waiting.get("verde"));
             arrayG[0][1] = Float.parseFloat(waiting.get("giallo"));
             arrayG[0][2] = Float.parseFloat(waiting.get("rosso"));
             arrayG[0][3] = Float.parseFloat(treatment.get("bianco"));
             arrayG[0][4] = Float.parseFloat(treatment.get("verde"));
             arrayG[0][5] = Float.parseFloat(treatment.get("giallo"));
             arrayG[0][6] = Float.parseFloat(treatment.get("rosso"));
             System.out.println("ArrayG: " + Arrays.toString(arrayG[0]));
            return arrayG;
         }

    }

    public void Model(String localModel, final String color){
        String localModelPath = localModel + ".tflite";


        try {
            if(color.equals("White")) {
                Log.d("White Model", localModelPath);
                FirebaseLocalModel localSource =
                        new FirebaseLocalModel.Builder(localModel)  // Assign a name to this Model
                                .setAssetFilePath(localModelPath)
                                .build();

                FirebaseModelManager.getInstance().registerLocalModel(localSource);
                FirebaseModelOptions options = new FirebaseModelOptions.Builder().setLocalModelName(localModel).build();
                FirebaseModelInterpreter firebaseInterpreter = FirebaseModelInterpreter.getInstance(options);

                float[][] input = toIntegerArrayFromHashMap(waiting, treatment, color);


                FirebaseModelInputOutputOptions inputOutputOptions =
                        new FirebaseModelInputOutputOptions.Builder()
                                .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 8})
                                .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 1})
                                .build();

                System.out.println("ArrayW: " + Arrays.toString(input[0]));
                FirebaseModelInputs inputs = new FirebaseModelInputs.Builder()
                        .add(input)  // add() as many input arrays as your Model requires
                        .build();
                firebaseInterpreter.run(inputs, inputOutputOptions)
                        .addOnSuccessListener(
                                new OnSuccessListener<FirebaseModelOutputs>() {
                                    @Override
                                    public void onSuccess(FirebaseModelOutputs result) {
                                        float[][] output = result.getOutput(0);
                                        float out = output[0][0];
                                        if(color.equals("White")) {
                                            wWhite = out;
                                        }
                                        else if(color.equals("Green")) {
                                            wGreen = out;
                                        }
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
                                        e.getMessage();
                                    }
                                });

            }
            else if(color.equals("Green")) {
                Log.d("Green Model", localModelPath);
                FirebaseModelInputOutputOptions inputOutputOptions =
                        new FirebaseModelInputOutputOptions.Builder()
                                .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 7})
                                .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 1})
                                .build();

                float [][] input = toIntegerArrayFromHashMap(waiting, treatment, color);
                FirebaseModelInputs inputs = new FirebaseModelInputs.Builder()
                        .add(input)  // add() as many input arrays as your Model requires
                        .build();

                FirebaseLocalModel localSource =
                        new FirebaseLocalModel.Builder(localModel)  // Assign a name to this Model
                                .setAssetFilePath(localModelPath)
                                .build();
                FirebaseModelManager.getInstance().registerLocalModel(localSource);
                FirebaseModelOptions options = new FirebaseModelOptions.Builder().setLocalModelName(localModel).build();
                FirebaseModelInterpreter firebaseInterpreter = FirebaseModelInterpreter.getInstance(options);
                firebaseInterpreter.run(inputs, inputOutputOptions)
                        .addOnSuccessListener(
                                new OnSuccessListener<FirebaseModelOutputs>() {
                                    @Override
                                    public void onSuccess(FirebaseModelOutputs result) {
                                        float[][] output = result.getOutput(0);
                                        float out = output[0][0];
                                        if(color.equals("White")) {
                                            wWhite = out;
                                        }
                                        else if(color.equals("Green")) {
                                            wGreen = out;
                                        }
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
                                        e.getMessage();
                                    }
                                });
            }
            //query to the Model


        }catch (FirebaseMLException e){
            Log.e("ERROR", "InputOutput");
            e.printStackTrace();
        }catch (ExceptionInInitializerError ex){
            ex.printStackTrace();
        }

    }


    public void FirebaseMLInterprter(){

    }

}
