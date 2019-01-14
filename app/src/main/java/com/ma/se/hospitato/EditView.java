package com.ma.se.hospitato;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditView extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText mName;
    EditText mSurname;
    EditText mNascita;
    EditText mHeight;
    EditText mWeight;
    EditText mBlood;
    String email;
    Profile u;

    FirebaseDatabase mDatabase;
    DatabaseReference myRef ;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_view);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("Users/" + user.getUid());

        initUI();
        /*
        if (getIntent() != null){
            Bundle b = getIntent().getExtras();
            mName.setText(b.getString("Name"));
            mSurname.setText(b.getString("Surname"));
            mNascita.setText(b.getString("Nascita"));
            try {
                mHeight.setText(b.getString("Height"));
            } catch (NullPointerException n) {
                n.printStackTrace();
            }
            try {
                mWeight.setText(b.getString("Weight"));
            } catch (NullPointerException n) {
                n.printStackTrace();
            }
            try {
                mBlood.setText(b.getString("Blood"));
            } catch (NullPointerException n) {
                n.printStackTrace();
            }
            u = new Profile(b.getString("Name"),b.getString("Surname"),b.getString("Email"),b.getString("Nascita"),b.getString("Blood"),b.getString("Height"),b.getString("Weight"));
        }
        */

    }

    private void initUI(){
        mName = (EditText)findViewById(R.id.editname);
        mSurname = (EditText)findViewById(R.id.editsurname);
        mNascita = (EditText)findViewById(R.id.editnascita);
        mHeight = (EditText)findViewById(R.id.editheight);
        mWeight = (EditText)findViewById(R.id.editweight);
        mBlood = (EditText)findViewById(R.id.editblood);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                u = dataSnapshot.getValue(Profile.class);
                mName.setText(u.getName());
                //email.setText(value.getEmail());
                email = u.getEmail();
                mSurname.setText(u.getSurname());
                try {
                    mNascita.setText(u.getNascita());
                }catch (NullPointerException n){
                    n.printStackTrace();
                }

                try {
                    mWeight.setText(u.getWeight());
                } catch (NullPointerException n){
                    n.printStackTrace();
                }
                try{
                    mHeight.setText(u.getHeight());
                } catch (NullPointerException n){
                    n.printStackTrace();
                }
                try {
                    mBlood.setText(u.getBlood());
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

    public void update(View view){
        try {
            u.setBlood("Blood Group: " + mBlood.getText().toString());
        } catch (NullPointerException n) {
            n.printStackTrace();
        }
        try {
            u.setHeight("Height: " + mHeight.getText().toString());
        } catch (NullPointerException n) {
            n.printStackTrace();
        }
        try {
            u.setWeight("Weight: " + mWeight.getText().toString());
        } catch (NullPointerException n){
            n.printStackTrace();
        }
        u.setNascita(mNascita.getText().toString());
        u.setName(mName.getText().toString());
        u.setSurname(mSurname.getText().toString());

        myRef.setValue(u);
        finish();
    }
}
