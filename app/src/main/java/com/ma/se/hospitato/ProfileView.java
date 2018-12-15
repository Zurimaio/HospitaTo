package com.ma.se.hospitato;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileView extends FragmentActivity {

    TextView email;
    TextView name;
    TextView surname;
    TextView nascita;
    TextView weight;
    TextView height;
    TextView blood;

    Profile value;

    String UID;

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        email = findViewById(R.id.emailprofile);
        name = findViewById(R.id.nameprofile);
        surname = findViewById(R.id.surnameprofile);
        nascita = findViewById(R.id.nascitaprofile);
        weight = findViewById(R.id.weightprofile);
        height = findViewById(R.id.heightprofile);
        blood = findViewById(R.id.bloodprofile);
        user = FirebaseAuth.getInstance().getCurrentUser();

        Bundle b = getIntent().getExtras();
        if (b != null) {
            email.setText(b.getString("email"));
            UID = b.getString("UID");
        }

        database = FirebaseDatabase.getInstance();

        if(user != null){
            displayProfile();
        }else{
            //TODO create the popup for create a profile
        }



    }

    public void EditProfile(View view) {
        Log.d("imageButton","The user want to edit it's profile");

        Toast toast = Toast.makeText(getApplicationContext(),"It will be redirect to the Edit View", Toast.LENGTH_SHORT);
        toast.show();

        Intent toEdit = new Intent(this,EditView.class);
        toEdit.putExtra("Name",value.getName());
        toEdit.putExtra("Surname",value.getSurname());
        toEdit.putExtra("Nascita",value.getNascita());
        toEdit.putExtra("Email",value.getEmail());
        try {
            toEdit.putExtra("Weight", value.getWeight());
        } catch (NullPointerException n){
            n.printStackTrace();
        }
        try {
            toEdit.putExtra("Height", value.getHeight());
        } catch (NullPointerException n){
            n.printStackTrace();
        }
        try {
            toEdit.putExtra("Blood", value.getBlood());
        } catch (NullPointerException n) {
            n.printStackTrace();
        }
        startActivity(toEdit);

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
                name.setText(value.getName());
                email.setText(value.getEmail());
                surname.setText(value.getSurname());
                nascita.setText(value.getNascita());
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
