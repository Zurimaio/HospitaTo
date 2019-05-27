package com.ma.se.hospitato;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.maps.MapView;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseLocalModel;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainList extends Fragment {

    private RecyclerView myRecyclerview;
    private MyAdapter adapter;
    private List<Hospital> listData = new ArrayList<>();
    private FirebaseDatabase FDB;
    private DatabaseReference DBR;
    private Button toMap;
    public String estimatedTime;
    private String hospitalName;
    private HashMap<String, Object> res;
    private BottomNavigationView bnv;
    private static MainList fragment;
    private LatLng loc;
    ProgressBar progressBar;
    float wWhite = -1;
    float wGreen = -1;
    private Hospital Hospitals;
    HashMap<String, String> waiting = new HashMap<>();
    HashMap<String, String> treatment = new HashMap<>();
    private HashMap<String, String> travTimeMap = new HashMap<>();
    int millSleep = 300;
    HashMap<String,Integer> EstimatedTime = new HashMap<>();
    MapView mapView;
    int estimated = 0;

    public MainList() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FDB = FirebaseDatabase.getInstance();
        GetDataFirebase();
        Log.d("onCreate", "Mainlist");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_list, container, false);
        myRecyclerview = view.findViewById(R.id.myrecycler);
        myRecyclerview.setHasFixedSize(true);
        myRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        progressBar = view.findViewById(R.id.progress_circular);
        progressBar.setVisibility(View.VISIBLE);
        // Inflate the layout for this fragment
        Log.d("MainLIst", "Created View");
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("OnStart wGreen", String.format(Locale.getDefault(), "%d size",
                EstimatedTime.size()));

    }

    public static MainList newInstance() {

        if (fragment == null) {
            Log.d("Fragment", "New Instance");
            fragment = new MainList();
        }
        return fragment;
    }


    void GetDataFirebase() {
        DBR = FDB.getReference("Hospitals"); //"Hospitals"
        DBR.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Hospital h = dataSnapshot.getValue(Hospital.class);
                //ADD DATA TO ARRAY LIST
                /**
                 * put here the summation the prediction time and travel time
                 */
                if(h.getName().equals(Utility.MOLINETTE)){
                    h.setEstimatedTime(127+11);
                }
                if(h.getName().equals(Utility.SANT_ANNA)){
                    h.setEstimatedTime(10+12);
                }
                if(h.getName().equals(Utility.CTO)){
                    h.setEstimatedTime(11+14);
                }if( h.getName().contains(Utility.REGINA_MARGHERITA)) {
                    h.setEstimatedTime(50+13);
                }

                if(!h.getName().equals(Utility.MOLINETTE)
                        && !h.getName().equals(Utility.SANT_ANNA)
                        && !h.getName().equals(Utility.CTO)
                        && !h.getName().contains(Utility.REGINA_MARGHERITA)) {
                h.setEstimatedTime(400);
                }
                listData.add(h);
                //setPeopleInPS(data.getName());
                //ADD DATA INTO ADAPTER/RECYCLER VIEW
                if (listData.size() == 8) {
                    //List<Hospital> hospitals = getEstimationTime(listData);
                    Collections.sort(listData, Hospital.BY_TIME);
                    // Retrieve coordinate of the best hospital, when the best list will be implemented
                    getBestHospitalName(listData.get(0).getName());
                    adapter = new MyAdapter(listData);
                    myRecyclerview.setAdapter(adapter);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        List<Hospital> listarray;  //listdata


        public MyAdapter(List<Hospital> list) {
            this.listarray = list;
        }

        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_view, parent, false);
            return new MyViewHolder(view);
        }


        @Override
        public void onBindViewHolder(final MyAdapter.MyViewHolder holder, int position) {
            progressBar.setVisibility(View.GONE);
            final Hospital data = listarray.get(position);
            holder.EDname.setText((data.getName()));
            holder.EDaddress.setText((data.getAddress()));
            holder.estimatedTime.setText(data.getEstimatedTime().toString() + " min");
            if (position == 0) {
                Log.d("Frist item", data.getName());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                holder.item_layout.setLayoutParams(params);
                holder.item_layout.requestLayout();
            }

            //getTravelTimeAsync(holder,data.getName());
            //setPeopleInPS(data.getName());

            if (position != 0) {
                holder.EDaddress.setVisibility(View.GONE);
                holder.E_TT.setVisibility(View.GONE);
                holder.estimatedTime.setVisibility(View.GONE);
                holder.mapView.setVisibility(View.GONE);
            }

            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    if (isLongClick) {
                        Bundle info = new Bundle();

                        //retrieve information of travel time of a given hospital
                        info.putString("TravelTime", travTimeMap.get(data.getName()));
                        //info.putString("WhiteTime", whiteTimeMap.get(data.getName()));
                        //info.putString("GreenTime", greenTimeMap.get(data.getName()));
                        //info.putSerializable("WaitingPeople", queueWait.get(data.getName()));
                        //info.putSerializable("TreatmentPeople", queueTreat.get(data.getName()));
                        info.putParcelable("Hospital", data);


                        Fragment displayED = new DisplayED();
                        displayED.setArguments(info);
                        FragmentTransaction tr = getFragmentManager().beginTransaction();
                        tr.add(R.id.fragment_container, displayED);
                        tr.addToBackStack(null);
                        tr.commit();
                    }

                }
            });


        }

        @Override
        public int getItemCount() {
            return listarray.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, OnMapReadyCallback {
            TextView EDname;
            TextView EDaddress;
            TextView G_waitingTime;
            TextView W_waitingTime;
            TextView estimatedTime;
            TextView E_TT;
            MapView mapView;
            GoogleMap map;
            LinearLayout item_layout;

            //ImageView map_log;
            private ItemClickListener itemClickListener;


            public MyViewHolder(View itemView) {
                super(itemView);
                EDname = (TextView) itemView.findViewById(R.id.ED_name);
                EDaddress = (TextView) itemView.findViewById(R.id.ED_address);
                //G_waitingTime = (TextView) itemView.findViewById(R.id.G_waitingTime);
                //W_waitingTime = (TextView) itemView.findViewById(R.id.W_waitingTime);
                estimatedTime = (TextView) itemView.findViewById(R.id.estimated_time);
                E_TT = (TextView) itemView.findViewById(R.id.estimated_time_txt);
                //map_log = (ImageView) itemView.findViewById(R.id.map_log);
                mapView = itemView.findViewById(R.id.lite_map);
                item_layout = itemView.findViewById(R.id.item_layout);

                if (mapView != null) {
                    mapView.onCreate(null);
                    mapView.getMapAsync(this);
                }


                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            public void setItemClickListener(ItemClickListener itemClickListener) {
                this.itemClickListener = itemClickListener;
            }

            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, getAdapterPosition(), true);
            }

            @Override
            public boolean onLongClick(View v) {
                itemClickListener.onClick(v, getAdapterPosition(), true);
                return true;
            }


            /**
             * MAP SECTION
             */

            @Override
            public void onMapReady(GoogleMap googleMap) {
                MapsInitializer.initialize(getContext());
                map = googleMap;
                setMapLocation();
            }

            public void setMapLocation() {
                if (map == null) return;
                //LatLng l = new LatLng(45.0504965,7.6636196);

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(getLoc(), 15f));
                map.addMarker(new MarkerOptions().position(getLoc()).title(hospitalName)).showInfoWindow();
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }


        }


    }


    public RecyclerView getMyRecyclerview() {
        return myRecyclerview;
    }

    public void setMyRecyclerview(RecyclerView myRecyclerview) {
        this.myRecyclerview = myRecyclerview;
    }


    public void getTravelTimeAsync(final MyAdapter.MyViewHolder holder, final String hospitalName) {
        Thread service = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    res = new AsyncGetDirectionTask().execute(getActivity().getApplicationContext(), getActivity()).get();
                    while (res.isEmpty()) {
                        System.out.println("Waiting travel time");
                        Thread.sleep(100);
                    }
                    //estimatedTime = (String) res.get("estimatedTime");
                    /**
                     * Todo the following "if" is taking into account only those hospitals of which we have data duration
                     * because of Google API restrictions
                     */
                    if (hospitalName.equals(Utility.MAURIZIANO) || hospitalName.equals(Utility.MOLINETTE)) {
                        estimatedTime = ((JSONDirections) ((HashMap) res.get("Directions")).get(hospitalName)).getDurationString();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("Adapter Async Task", estimatedTime);
                                holder.estimatedTime.setText(estimatedTime);
                                travTimeMap.put(hospitalName, estimatedTime);
                            }
                        });
                    } else {
                        holder.estimatedTime.setText(getText(R.string.data_not_avaialable).toString());
                        estimatedTime = getText(R.string.data_not_avaialable).toString();
                        travTimeMap.put(hospitalName, estimatedTime);
                    }

                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                } catch (ExecutionException ee) {
                    ee.printStackTrace();
                }

            }
        };
        service.start();
        //return estimatedTime;
    }


    public void getBestHospitalName(final String hospitalName) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Hospitals/");
        Log.d("Ref", ref.getRef().toString());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Hospital h = d.getValue(Hospital.class);
                    if (h.getName().equals(hospitalName)) {
                        Double lat = Double.parseDouble(h.getCoordinate().get("Latitude"));
                        Double lng = Double.parseDouble(h.getCoordinate().get("Longitude"));
                        setLoc(new LatLng(lat, lng));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                return;
            }
        });
    }

    public LatLng getLoc() {
        return loc;
    }

    public void setLoc(LatLng loc) {
        this.loc = loc;
    }


    public List<Hospital> getEstimationTime(final List<Hospital> hospitals) {
        final Random random = new Random();
        final List<Hospital> hospi = new ArrayList<>();
        final int travelTime = random.nextInt(40) + 1;
        for(Hospital h: hospitals){
            if(h.getName().equals(Utility.MOLINETTE)
                    || h.getName().equals(Utility.SANT_ANNA)
                    || h.getName().equals(Utility.CTO)
                    || h.getName().equals(Utility.REGINA_MARGHERITA)) {
                //setPeopleInPS(h.getName());
                h.setEstimatedTime(travelTime);
                hospi.add(h);
            }else{
                estimated = 400;
                h.setEstimatedTime(estimated);
                hospi.add(h);
            }

        }
        //Log.d("Estimated time", EstimatedTime.toString());
        Collections.sort(hospi, Hospital.BY_TIME);
        return hospi;

}
    /**
     * Neural newtwork requests
     * @param localModel
     */
    public void requestToModel(final String localModel){
        final HashMap<String, String> model = chooseModel(localModel);
        Log.d("MODEL", model.toString());
        //CHOOSE THE MODEL TO QUERY
        String greenModel = model.get("Green");
        Model(greenModel, localModel, "Green");

    }
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

    public void Model(String localModel, final String hospitalName, final String color){
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
                                        wGreen = -1;
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
                                            estimated = Math.round(wGreen);
                                            Log.d("Estimated time for "+ hospitalName, Integer.toString(estimated));

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
                            Log.d("PeopleInED", hospitalName + " Waiting");
                        }
                        waiting = result.get("waitingPeople");
                        treatment = result.get("treatmentPeople");
                        Log.d("PeopleInED","Waiting " + waiting.toString());
                        Log.d("PeopleInED","Treatment " + treatment.toString());
                        requestToModel(hospitalName);
                        result.clear();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.e("PEOPLEINPS", hospitalName + "BAD NAME");

                    return;
                }
            }

        }.start();

    }

}


