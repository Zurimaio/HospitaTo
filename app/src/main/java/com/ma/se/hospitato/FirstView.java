package com.ma.se.hospitato;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class FirstView extends AppCompatActivity {

    FirebaseAuth mAuth;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_view);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            intent = new Intent(this, Main2Activity.class);
            startActivity(intent);
            finish();
        }


    }



    public void signIn(View view){
        intent = new Intent(this, LogInView.class);
        startActivity(intent);
        //finish();
    }

    public void signUp(View view){
        intent = new Intent(this, SignUpView.class);
        startActivity(intent);
        //finish();
    }

    public void skipIt(View view){
        intent = new Intent(this, Main2Activity.class);
        intent.putExtra("firstAction", "skipped");
        startActivity(intent);
        //finish();
    }



}
