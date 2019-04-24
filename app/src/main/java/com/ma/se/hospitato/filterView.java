package com.ma.se.hospitato;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class filterView extends Fragment implements View.OnClickListener{
    ImageView b1,b2,b3,b4,b5,b6;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_filter_view);
        Log.d("Created", "FilterView");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_filter_view, container, false);
        b1=(ImageView) view.findViewById(R.id.dep1);
        b2=(ImageView) view.findViewById(R.id.dep2);
        b3=(ImageView) view.findViewById(R.id.dep3);
        b4=(ImageView) view.findViewById(R.id.dep4);
        b5=(ImageView) view.findViewById(R.id.dep5);
        b6=(ImageView) view.findViewById(R.id.dep6);

        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        b4.setOnClickListener(this);
        b5.setOnClickListener(this);
        b6.setOnClickListener(this);


        return view;
    }
    public void onClick(View view){
        Bundle i = new Bundle();
        FilterChoice fc = new FilterChoice();
        switch(view.getId()){
            case R.id.dep1:
                String dep1 = "Department1";
                i.putString("Department", dep1);
                break;

            case R.id.dep2:
                String dep2 = "Department2";
                i.putString("Department", dep2);
                break;

            case R.id.dep3:
                String dep3 = "Department3";
                i.putString("Department", dep3);
                break;

            case R.id.dep4:
                String dep4 = "Department4";
                i.putString("Department", dep4);
                break;

            case R.id.dep5:
                String dep5 = "Department5";
                i.putString("Department", dep5);
                break;

            case R.id.dep6:
                String dep6 = "Department6";
                i.putString("Department", dep6);
                break;

        }

        fc.setArguments(i);
        loadFragment(fc);
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction tr = getFragmentManager().beginTransaction();
        tr.add(R.id.container, fragment);
        Log.d("ANNA", "CREA LA TRANSAZIONE");
        tr.addToBackStack(null);
        tr.commit();
    }



}
