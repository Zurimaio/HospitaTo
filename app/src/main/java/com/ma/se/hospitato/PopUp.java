package com.ma.se.hospitato;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PopUp extends AppCompatActivity {

    Spinner hop;
    Button mAddFeed;
    Spinner estim;
    RatingBar rate;
    TextView close;

    DatabaseReference databaseReference;

    @Override
    public void onCreate(Bundle saveInstanceState) {

        super.onCreate(saveInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);

        setContentView(R.layout.popupwindow);

        DisplayMetrics dm= new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width=dm.widthPixels;
        int height=dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.6));

        hop = (Spinner) findViewById(R.id.hop);
        mAddFeed=(Button) findViewById(R.id.add);
        estim=(Spinner) findViewById(R.id.stimated);
        rate=(RatingBar) findViewById(R.id.rateApp);
        close=(TextView) findViewById(R.id.close) ;

        databaseReference = FirebaseDatabase.getInstance().getReference("Feedback");


        mAddFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFeed();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void addFeed(){
        String hosp=hop.getSelectedItem().toString();
        String stima=estim.getSelectedItem().toString();
        Integer rat=rate.getNumStars();

        if(!TextUtils.isEmpty(hosp)){
            String id=databaseReference.push().getKey();

            Feedback f= new Feedback(id,hosp,stima,rat);

            databaseReference.child(id).setValue(f);

            Toast.makeText(this,"Feedback added! Thanks for your help",Toast.LENGTH_LONG).show();;

        }else{
            Toast.makeText(this,"You should enter a name",Toast.LENGTH_LONG).show();
        }
    }

}
