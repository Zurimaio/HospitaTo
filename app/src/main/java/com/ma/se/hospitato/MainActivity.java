package com.ma.se.hospitato;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Used temporary to go directly to the map view
        Button mapView = findViewById(R.id.toMapView);
        toMapViewButton(mapView);

    }

    //Temporary to go to the map view
    public void toMapViewButton(Button mapView){
        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMapView = new Intent(MainActivity.this, MapView.class);
                startActivity(toMapView);
            }
        });
    }
}
