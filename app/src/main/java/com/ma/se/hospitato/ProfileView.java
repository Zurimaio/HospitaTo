package com.ma.se.hospitato;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class ProfileView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
    }

    public void EditProfile(View view) {
        Log.d("imageButton","The user want to edit it's profile");

        Toast toast = Toast.makeText(getApplicationContext(),"It will be redirect to the Edit View", Toast.LENGTH_SHORT);
        toast.show();
    }
}
