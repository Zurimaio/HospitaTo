package com.ma.se.hospitato;


import android.os.Bundle;
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
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private Button toMap;String travelTime;
    private HashMap<String, Object> res;
    private BottomNavigationView bnv;

    public MainList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_list, container, false);

        myRecyclerview = view.findViewById(R.id.myrecycler);
        myRecyclerview.setHasFixedSize(true);
        RecyclerView.LayoutManager LM = new LinearLayoutManager(getActivity());
        myRecyclerview.setLayoutManager(LM);
        listData = new ArrayList<>();
        adapter = new MyAdapter(listData);
        FDB = FirebaseDatabase.getInstance();
        GetDataFirebase();
        // Inflate the layout for this fragment
        return view;


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
        public void onBindViewHolder(MyAdapter.MyViewHolder holder, int position) {
            final Hospital data = listarray.get(position);
            holder.EDname.setText((data.getName()));
            holder.EDaddress.setText((data.getAddress()));
            //holder.travTime.setText(getTravelTimeAsync());

            if (position != 0) {
                holder.EDaddress.setVisibility(View.GONE);
                // holder.map_log.setVisibility(View.GONE);
                holder.E_TT.setVisibility(View.GONE);
                holder.W_waitingTime.setVisibility(View.GONE);
                holder.G_waitingTime.setVisibility(View.GONE);
                holder.travTime.setVisibility(View.GONE);
            }
            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    if(isLongClick){
                        myRecyclerview.setVisibility(View.GONE);
                        Fragment displayED = DisplayED.newInstance(data);
                        FragmentTransaction tr = getFragmentManager().beginTransaction();
                        tr.replace(R.id.fragment_container, displayED);
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

        /*
        public String getTravelTimeAsync(){

            Thread service = new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        res = new AsyncGetDirectionTask(adapter, listData).execute(getApplicationContext(), MainActivity.this).get();
                        while(res == null){
                            System.out.println("Waiting travel time");
                            Thread.sleep(1000);
                        }
                        travelTime = (String) res.get("travelTime");
                        Log.d("Adapter Async Task", travelTime);

                    }catch (InterruptedException ie){
                        ie.printStackTrace();
                    }catch (ExecutionException ee){
                        ee.printStackTrace();
                    }
                }
            };
            service.start();
            return travelTime;
        }
        */

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
            TextView EDname;
            TextView EDaddress;
            TextView G_waitingTime;
            TextView W_waitingTime;
            TextView travTime;
            TextView E_TT;
            //ImageView map_log;
            private ItemClickListener itemClickListener;


            public MyViewHolder(View itemView) {
                super(itemView);
                EDname = (TextView) itemView.findViewById(R.id.ED_name);
                EDaddress = (TextView) itemView.findViewById(R.id.ED_address);
                G_waitingTime = (TextView) itemView.findViewById(R.id.G_waitingTime);
                W_waitingTime = (TextView) itemView.findViewById(R.id.W_waitingTime);
                travTime = (TextView) itemView.findViewById(R.id.travel_time);
                E_TT = (TextView) itemView.findViewById(R.id.travel_time_txt);
                // map_log = (ImageView) itemView.findViewById(R.id.map_log);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);

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
        }
    }


}
