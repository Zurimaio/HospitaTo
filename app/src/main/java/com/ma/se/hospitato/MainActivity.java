package com.ma.se.hospitato;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;
import android.support.v7.app.ActionBar;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    RecyclerView myRecyclerview;
    List<Hospital> listData;
    FirebaseDatabase FDB;
    DatabaseReference DBR;
    Button toFilter;
    Button toMap;
    String travelTime;
    HashMap<String, Object> res;
    BottomNavigationView bnv;
    private BottomNavigationView.OnNavigationItemSelectedListener nav;
    private ActionBar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int REQUEST_CHECK_SETTINGS = 2;

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_CHECK_SETTINGS);
        setContentView(R.layout.activity_main);

        //NEW CODE
        toolbar = getSupportActionBar();
        BottomNavigationView bnv =  findViewById(R.id.bottomNavigation);
        selectorBottomNavigation();
        bnv.setOnNavigationItemSelectedListener(nav);
        toolbar.setTitle("HospitaTo");
        //

        loadFragment(new MainList());


    }


    /*
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        myRecyclerview.setVisibility(View.VISIBLE);
        }
    */


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int profile = item.getItemId();

        if (profile == R.id.profileButton){
            Log.d("Profile Button", "Clicked");
            Intent toProfile = new Intent(MainActivity.this, ProfileView.class);
            startActivity(toProfile);
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectorBottomNavigation(){
        nav = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homeButton:
                        //intent = new Intent(MainActivity.this, MainActivity.class);
                        //startActivity(intent);
                        toolbar.setTitle("HospitaTo");
                        loadFragment(new MainList());

                        return true;

                    case R.id.filterButton:
                        //intent = new Intent(MainActivity.this, ProfileView.class);
                        //startActivity(intent);
                        toolbar.setTitle(R.string.filterButton);
                        loadFragment(new filterView());
                        return true;
                    case R.id.mapButton:
                        //intent = new Intent(MainActivity.this, MapView.class);
                        //intent.putExtra("FromMain", 2); //2 - For obtaining hospital positions
                        //startActivity(intent);
                        toolbar.setTitle(R.string.mapButton);
                        return true;

                    default:
                        return true;
                }
            }
        };
    }



    private void loadFragment(Fragment fragment) {

        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

