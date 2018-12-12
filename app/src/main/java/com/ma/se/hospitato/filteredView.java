package com.ma.se.hospitato;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class filteredView extends AppCompatActivity {

    DatabaseReference databaseReference;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button hide_filter = findViewById(R.id.toFilter);
        hide_filter.setVisibility(View.GONE);
        Button hide_map = findViewById(R.id.toMap);
        hide_map.setVisibility(View.GONE);



        databaseReference = FirebaseDatabase.getInstance().getReference("Hospitals");

        recyclerView = (RecyclerView) findViewById(R.id.myrecycler);
        //Avoid unnecessary layout passes by setting setHasFixedSize to true
        recyclerView.setHasFixedSize(true);
        //Select the type of layout manager you would use for your recyclerView
        RecyclerView.LayoutManager LM = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(LM);



        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot adSnapshot: dataSnapshot.getChildren()) {
                    Hospital h = adSnapshot.getValue(Hospital.class);
                    }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot adSnapshot: dataSnapshot.getChildren()) {
                    Hospital h = adSnapshot.getValue(Hospital.class);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    protected void onStart(){
        super.onStart();


        FirebaseRecyclerAdapter<Hospital, RequestViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Hospital, RequestViewHolder>(
                Hospital.class,
                R.layout.item_list_view,
                RequestViewHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(RequestViewHolder viewHolder, final Hospital model, int position) {
                final String filter= getIntent().getStringExtra("Department");
                final boolean t=model.getDepartments().get(filter);

                if(model.getDepartments().get(filter)) {
                    viewHolder.setDetails(model.getName(), model.getAddress());
                }
                else{
                    viewHolder.noDetails();
                }

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        if(isLongClick){
                            recyclerView.setVisibility(View.GONE);
                            Fragment displayED = DisplayED.newInstance(model);
                            FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
                            tr.replace(R.id.fragment_container, displayED);
                            tr.addToBackStack(null);
                            tr.commit();
                        }
                    }
                });
            }

        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        public View mView;
        private ItemClickListener itemClickListener;
        public RequestViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setDetails(String Nameh, String Addressh ) {
            TextView name = (TextView) mView.findViewById(R.id.ED_name);
            TextView address = (TextView) mView.findViewById(R.id.ED_address);
            name.setText(Nameh);
            address.setText(Addressh);
        }

        public void noDetails() {

            itemView.setVisibility(View.GONE);
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
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        recyclerView.setVisibility(View.VISIBLE);
    }


}
