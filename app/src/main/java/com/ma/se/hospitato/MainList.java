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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainList extends Fragment {

    private RecyclerView myRecyclerview;
    private MyAdapter adapter;
    private List<Hospital> listData;
    private FirebaseDatabase FDB;
    private DatabaseReference DBR;
    private Button toMap;
    public String travelTime;
    private String hospitalName;
    private HashMap<String, Object> res;
    private BottomNavigationView bnv;
    private static MainList fragment;
    private LatLng loc;
    ProgressBar progressBar;
    float waitingWhiteTime = -1;
    float waitingGreenTime = -1;
    private HashMap<String, String> waitingPeople = new HashMap<>();
    private HashMap<String, String> treatmentPeople = new HashMap<>();

    private HashMap<String, HashMap<String, String>> queueWait = new HashMap<>();
    private HashMap<String, HashMap<String, String>> queueTreat = new HashMap<>();

    private HashMap<String, String> whiteTimeMap = new HashMap<>();
    private HashMap<String, String> greenTimeMap = new HashMap<>();
    private HashMap<String,String> travTimeMap = new HashMap<>();

    MapView mapView;

    public MainList() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FDB = FirebaseDatabase.getInstance();
        listData = new ArrayList<>();
        adapter = new MyAdapter(listData);
        GetDataFirebase();
        // Retrieve coordinate of the best hospital, when the best list will be implemented
        getBestHospitalName("Mauriziano");
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
        adapter.notifyDataSetChanged();

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
                Hospital data = dataSnapshot.getValue(Hospital.class);
                //ADD DATA TO ARRAY LIST
                listData.add(data);
                //ADD DATA INTO ADAPTER/RECYCLER VIEW
                myRecyclerview.setAdapter(adapter);
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
            if(position == 0) {
                Log.d("Frist item", data.getName());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                holder.item_layout.setLayoutParams(params);
                holder.item_layout.requestLayout();
            }

            getTravelTimeAsync(holder,data.getName());
            //setPeopleInPS(data.getName());

            if (position != 0) {
                holder.EDaddress.setVisibility(View.GONE);
                holder.E_TT.setVisibility(View.GONE);
                holder.travTime.setVisibility(View.GONE);
                holder.mapView.setVisibility(View.GONE);
            }

            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    if (isLongClick) {
                        Bundle info =  new Bundle();

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
            TextView travTime;
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
                travTime = (TextView) itemView.findViewById(R.id.travel_time);
                E_TT = (TextView) itemView.findViewById(R.id.travel_time_txt);
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

            public void setMapLocation(){
                if(map==null) return;
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
                    //travelTime = (String) res.get("travelTime");
                    /**
                     * Todo the following "if" is taking into account only those hospitals of which we have data duration
                     * because of Google API restrictions
                     */
                    if (hospitalName.equals(Utility.MAURIZIANO) || hospitalName.equals(Utility.MOLINETTE)) {
                        travelTime = ((JSONDirections) ((HashMap) res.get("Directions")).get(hospitalName)).getDurationString();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("Adapter Async Task", travelTime);
                                holder.travTime.setText(travelTime);
                                travTimeMap.put(hospitalName, travelTime);
                            }
                        });
                    }else{
                        holder.travTime.setText(getText(R.string.data_not_avaialable).toString());
                        travelTime = getText(R.string.data_not_avaialable).toString();
                        travTimeMap.put(hospitalName, travelTime);
                    }

                    } catch(InterruptedException ie){
                        ie.printStackTrace();
                    } catch(ExecutionException ee){
                        ee.printStackTrace();
                    }

            }
        };
        service.start();
        //return travelTime;
    }


    public void getBestHospitalName(final String hospitalName) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Hospitals/");
        Log.d("Ref", ref.getRef().toString());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot d: dataSnapshot.getChildren()) {
                        Hospital h = d.getValue(Hospital.class);
                        if(h.getName().equals(hospitalName)) {
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

        new Thread(){
            @Override
            public void run() {
                super.run();

                try{
                    if(localModel.equals(Utility.MAURIZIANO)
                            || localModel.equals(Utility.SAN_GIOVANNI_BOSCO)
                            || localModel.equals(Utility.MARIA_VITTORIA)
                            || localModel.equals(Utility.MARTINI)
                    ){
                        //no Model available
                        whiteTimeMap.put(localModel, getText(R.string.data_not_avaialable).toString());
                    }else {
                        String whiteModel = model.get("White");
                        model(whiteModel, "White");

                        while (waitingWhiteTime == -1) {
                            Thread.sleep(100);
                            Log.d("White waitingPeople time", "sleeping");
                        }
                        whiteTimeMap.put(localModel,
                                String.format(Locale.getDefault(),
                                        "%d min",
                                        Math.round(waitingWhiteTime)));

                    }

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
                try{
                    if(localModel.equals(Utility.MAURIZIANO)
                            || localModel.equals(Utility.SAN_GIOVANNI_BOSCO)
                            || localModel.equals(Utility.MARIA_VITTORIA)
                            || localModel.equals(Utility.MARTINI)
                    ){
                       greenTimeMap.put(localModel, getText(R.string.data_not_avaialable).toString());
                    }else {
                        String greenModel = model.get("Green");
                        model(greenModel, "Green");

                        while (waitingGreenTime == -1) {
                            Thread.sleep(100);
                            Log.d("Green waitingPeople time", "sleeping");
                        }
                        greenTimeMap.put(localModel,
                                String.format(Locale.getDefault(),
                                        "%d min",
                                        Math.round(waitingGreenTime)));
                    }
                }catch (Exception e){
                    Log.e("ERROR", "Waiting Green");
                    e.printStackTrace();
                }
            }
        }.start();


    }

    public void model(String localModel, final String color){
        String localModelPath = localModel + ".tflite";

        FirebaseLocalModel localSource =
                new FirebaseLocalModel.Builder(localModel)  // Assign a name to this Model
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

            float[][] input = toIntegerArrayFromHashMap(waitingPeople, treatmentPeople);

            //query to the Model
            FirebaseModelInputs inputs = new FirebaseModelInputs.Builder()
                    .add(input)  // add() as many input arrays as your Model requires
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
                                        waitingWhiteTime = out;
                                    else if(color.equals("Green"))
                                        waitingGreenTime = out;

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



    private void setPeopleInPS(final String hospital) {
        new Thread() {
            public void run() {
                boolean suc = Utility.peopleInPS(getContext(), hospital);
                HashMap<String, HashMap> result = new HashMap<>();
                if(suc) {
                    try {
                        while (result.isEmpty()) {
                            Thread.sleep(300);
                            result = Utility.getPeopleInPS();
                            Log.d("PeopleInED", "Waiting");
                        }
                        waitingPeople = result.get("waitingPeople");
                        treatmentPeople = result.get("treatmentPeople");
                        //get the waiting time from people in waiting and treatment
                        //queueWait.put(hospital, waitingPeople);
                        //queueTreat.put(hospital, treatmentPeople);
                        //requestToModel(hospital);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    String noAvailable = getText(R.string.data_not_avaialable).toString();
                    waitingPeople.put(noAvailable, noAvailable);
                    treatmentPeople.put(noAvailable, noAvailable);

                    //queueTreat.put(hospital,waitingPeople);
                    //queueWait.put(hospital, treatmentPeople);

                    Log.e("PEOPLEINPS", "BAD NAME");
                    return;
                }
            }

        }.start();

    }

}


