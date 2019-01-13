package com.ma.se.hospitato;


import android.os.Bundle;
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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 */
public class Personal_information extends Fragment {

    TextView email;
    TextView name;
    TextView surname;
    TextView nascita;
    Profile value;
    FirebaseUser user;
    String UID;
    FirebaseDatabase database;
    DatabaseReference myRef;

    public Personal_information() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getActivity().getIntent().getExtras();
        if (b!= null) {
            email.setText(b.getString("email"));
            UID = b.getString("UID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.personal_information, container, false);
        email = view.findViewById(R.id.emailprofile);
        name = view.findViewById(R.id.nameprofile);
        surname = view.findViewById(R.id.surnameprofile);
        nascita = view.findViewById(R.id.nascitaprofile);
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();

        if (user!=null) {
            displayProfile();
        }
        return view;
    }

    public void displayProfile(){
        myRef = database.getReference("Users/"+user.getUid());
        Log.d("Reference", myRef.toString());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                value = dataSnapshot.getValue(Profile.class);
                name.setText(value.getName());
                email.setText(value.getEmail());
                surname.setText(value.getSurname());
                nascita.setText(value.getNascita());
                Log.d("Profile view", "Value is: " + value.getName());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Profile view", "Failed to read value.", error.toException());
            }
        });
    }
}




