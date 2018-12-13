package com.ma.se.hospitato;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

public class MainActivity extends AppCompatActivity {
    RecyclerView myRecyclerview;
    MyAdapter adapter;
    List<Hospital> listData;
    FirebaseDatabase FDB;
    DatabaseReference DBR;
    Button toFilter;
    Button toMap;
    String travelTime;
    HashMap<String, Object> res;
    BottomNavigationView bnv;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int REQUEST_CHECK_SETTINGS = 2;

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_CHECK_SETTINGS);

        setContentView(R.layout.activity_main);
        myRecyclerview = (RecyclerView) findViewById(R.id.myrecycler);
        myRecyclerview.setHasFixedSize(true);
        RecyclerView.LayoutManager LM = new LinearLayoutManager(getApplicationContext());
        myRecyclerview.setLayoutManager(LM);
        bnv = findViewById(R.id.bottomNavigation);

        listData = new ArrayList<>();
        adapter = new MyAdapter(listData);
        FDB = FirebaseDatabase.getInstance();
        GetDataFirebase();
        selectorBottomNavigation();
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_view, parent, false);
            return new MyViewHolder(view);
        }


        @Override
        public void onBindViewHolder(MyAdapter.MyViewHolder holder, int position) {
            final Hospital data = listarray.get(position);
            holder.EDname.setText((data.getName()));
            holder.EDaddress.setText((data.getAddress()));
            holder.travTime.setText(getTravelTimeAsync());

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
                        FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        myRecyclerview.setVisibility(View.VISIBLE);
        }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int filter = item.getItemId();

        if (filter == R.id.filterButton){
            Intent toFilter = new Intent(MainActivity.this, filterView.class);
            startActivity(toFilter);
        }

        return super.onOptionsItemSelected(item);
    }

    public void selectorBottomNavigation(){
        bnv.setSelectedItemId(R.id.homeButton);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.homeButton:
                        intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.profileButton:
                        return true;
                    case R.id.mapButton:
                        intent = new Intent(MainActivity.this, MapView.class);
                        intent.putExtra("FromMain", 2); //2 - For obtaining hospital positions
                        startActivity(intent);
                        return true;

                    default:
                        return true;
                }
            }
        });
    }


}

