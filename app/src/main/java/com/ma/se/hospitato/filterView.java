package com.ma.se.hospitato;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class filterView extends AppCompatActivity implements View.OnClickListener{
    ImageView b1,b2,b3,b4,b5,b6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_view);

        b1=(ImageView) findViewById(R.id.dep1);
        b2=(ImageView) findViewById(R.id.dep2);
        b3=(ImageView) findViewById(R.id.dep3);
        b4=(ImageView) findViewById(R.id.dep4);
        b5=(ImageView) findViewById(R.id.dep5);
        b6=(ImageView) findViewById(R.id.dep6);

        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        b4.setOnClickListener(this);
        b5.setOnClickListener(this);
        b6.setOnClickListener(this);
    }

    public void onClick(View view){
        //Toast.makeText(this, "Button Selected!",Toast.LENGTH_SHORT).show();
        Intent i=new  Intent(getApplicationContext(),filteredView.class);

        switch(view.getId()){
            case R.id.dep1:
                String dep1 = "Department1";
                i.putExtra("Department", dep1);
                break;

            case R.id.dep2:
                String dep2 = "Department2";
                i.putExtra("Department", dep2);
                break;

            case R.id.dep3:
                String dep3 = "Department3";
                i.putExtra("Department", dep3);
                break;

            case R.id.dep4:
                String dep4 = "Department4";
                i.putExtra("Department", dep4);
                break;

            case R.id.dep5:
                String dep5 = "Department5";
                i.putExtra("Department", dep5);
                break;

            case R.id.dep6:
                String dep6 = "Department6";
                i.putExtra("Department", dep6);
                break;

        }

        startActivity(i);

    }
}
