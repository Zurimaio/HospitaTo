package com.ma.se.hospitato;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class filteredView extends AppCompatActivity {

    DatabaseReference databaseReference;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered_view);

        databaseReference =FirebaseDatabase.getInstance().getReference().child("ApprovedEvents");

        recyclerView =(RecyclerView) findViewById(R.id.request_HospitalsList);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart(){
        final String event_cat= getIntent().getStringExtra("Department");
        final String filter= "Departments/"+event_cat;
        super.onStart();

       FirebaseRecyclerAdapter<Hospital, RequestViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Hospital, RequestViewHolder>(
               Hospital.class,
               R.layout.first_element_filtered_list,
               RequestViewHolder.class,
               databaseReference.orderByChild(filter).equalTo(true)
        ) {
            @Override
            protected void populateViewHolder(RequestViewHolder viewHolder, Hospital model, int position) {
                viewHolder.setName(model.getName());
                viewHolder.setAddress(model.getAddress());
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public RequestViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView a_name = (TextView) mView.findViewById(R.id.request_name);
            a_name.setText(name);
        }

        public void setAddress(String address) {
            TextView a_address = (TextView) mView.findViewById(R.id.request_address);
            a_address.setText(address);
        }
    }
}
