package com.ma.se.hospitato;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class medical_information extends Fragment {

    TextView weight;
    TextView height;
    TextView blood;
    Profile value;
    String UID;

    FirebaseDatabase database;
    DatabaseReference myRef;


    public medical_information() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_medical_information, container, false);

        weight = view.findViewById(R.id.weightprofile);
        height = view.findViewById(R.id.heightprofile);
        blood = view.findViewById(R.id.bloodprofile);
        // Inflate the layout for this fragment

        Bundle b = getIntent().getExtras();
        if (b != null) {
            UID = b.getString("UID");
        }

        database = FirebaseDatabase.getInstance();
        displayProfile();
        return inflater.inflate(R.layout.fragment_medical_information, container, false);
    }

    public void displayProfile(){
        myRef = database.getReference("Users/"+user.getUid());
        Log.d("Reference", myRef.toString());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                value = dataSnapshot.getValue(Profile.class);
                try {
                    weight.setText(value.getWeight());
                } catch (NullPointerException n){
                    n.printStackTrace();
                }
                try{
                    height.setText(value.getHeight());
                } catch (NullPointerException n){
                    n.printStackTrace();
                }
                try {
                    blood.setText(value.getBlood());
                } catch (NullPointerException n){
                    n.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Profile view", "Failed to read value.", error.toException());
            }
        });
    }

}
