package com.ma.se.hospitato;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.gms.maps.MapView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 */
public class FilterChoice extends Fragment {

    DatabaseReference databaseReference;
    private HashMap<String, Object> res;
    RecyclerView recyclerView;
    private HashMap<String,String> infoToFragment = new HashMap<>();
    String filter;
    public String travelTime;

    TextView name;
    List<String> hospitalName = new ArrayList<>();
    private FirebaseRecyclerAdapter<Hospital, RequestViewHolder> adapter;
    public FilterChoice() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Created", "FilterChoice");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("Adapter", "start listening");
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("Adapter", "stop listening");
        adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_filter_choice, container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference("Hospitals");
        recyclerView = view.findViewById(R.id.myrecycler);
        //Avoid unnecessary layout passes by setting setHasFixedSize to true
        //recyclerView.setHasFixedSize(true);
        filter = getArguments().getString("Department");
        Log.d("Filtered Result",databaseReference.getRef().toString());
        filtering();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    public void filtering(){
        Query query = FirebaseDatabase
                .getInstance()
                .getReference("Hospitals");

        FirebaseRecyclerOptions<Hospital> options =
                new FirebaseRecyclerOptions.Builder<Hospital>()
                        .setQuery(query, Hospital.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<Hospital, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(final RequestViewHolder viewHolder, int position, Hospital model) {
                final boolean t= model.getDepartments().get(filter);
                final Bundle info =  new Bundle();
                //retrieve information of travel time of a given hospital
                if(t) {
                    viewHolder.setDetails(model.getName(), model.getAddress());
                    getTravelTimeAsync(viewHolder,model.getName());
                    info.putParcelable("Hospital", model);
                    hospitalName.add(model.getName());
                    //Log.d("Position " + Model.getName(),Integer.toString(position));


                }
                else{
                    viewHolder.noDetails();
                }


                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        if(isLongClick){
                            FragmentTransaction tr = getFragmentManager().beginTransaction();
                            Fragment displayED = new DisplayED();
                            info.putString("TravelTime",infoToFragment.get(hospitalName.get(position)));
                            displayED.setArguments(info);
                            //recyclerView.setVisibility(View.GONE);
                            tr.replace(R.id.fragment_filter_choice, displayED);
                            tr.addToBackStack(null);
                            tr.commit();
                        }
                    }
                });
            }

            @Override
            public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.item_list_view, parent, false);
                return new RequestViewHolder(view);
            }


                    };
        recyclerView.setAdapter(adapter);
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener,OnMapReadyCallback {
        public View mView;
        MapView mapView;
        GoogleMap map;
        private ItemClickListener itemClickListener;

        public RequestViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mapView = itemView.findViewById(R.id.lite_map);
            if (mapView != null) {
                mapView.onCreate(null);
                mapView.getMapAsync(this);
            }
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setDetails(String Nameh, String Addressh ) {
            name = (TextView) mView.findViewById(R.id.ED_name);
            TextView address = (TextView) mView.findViewById(R.id.ED_address);
            mapView.setVisibility(View.GONE);
            name.setText(Nameh);
            address.setText(Addressh);
        }


        public void setTravelTime(String travelTime){
            TextView travTime = mView.findViewById(R.id.estimated_time);
            travTime.setText(travelTime);
        }

        public void noDetails() {
            itemView.setVisibility(View.GONE);
            mapView.setVisibility(View.GONE);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }

        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener=itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),true);
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),true);
            return true;
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(getContext());
        }
    }


    public void getTravelTimeAsync(final RequestViewHolder holder, final String hospitalName) {
        Thread service = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    res = new AsyncGetDirectionTask().execute(getActivity().getApplicationContext(), getActivity()).get();
                    while (res.isEmpty()) {
                        System.out.println("Waiting travel time");
                        Thread.sleep(1000);
                    }
                    /**
                     * Todo the following "if" is taking into account only those hospitals of which we have data duration
                     * because of Google API restrictions
                     */
                    if (hospitalName.equals(Utility.MAURIZIANO) || hospitalName.equals(Utility.MOLINETTE)) {
                        travelTime = ((JSONDirections) ((HashMap) res.get("Directions")).get(hospitalName)).getDurationString();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                infoToFragment.put(hospitalName, travelTime);
                                Log.d("Filter Info To fragment", infoToFragment.toString());
                                holder.setTravelTime(travelTime);
                            }
                        });
                    }else{
                        travelTime = getString(R.string.data_not_avaialable);
                        infoToFragment.put(hospitalName, travelTime);
                    }

                } catch(InterruptedException ie){
                    ie.printStackTrace();
                } catch(ExecutionException ee){
                    ee.printStackTrace();
                }

            }
        };
        service.start();
        //return estimatedTime;
    }
}

