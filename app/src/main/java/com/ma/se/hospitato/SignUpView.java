package com.ma.se.hospitato;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpView extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText mEmail;
    EditText mPassword;
    EditText mName;
    EditText mSurname;
    Profile u = new Profile();
    String UID;

    FirebaseDatabase mDatabase;



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
            Toast.makeText(this, "User already logged" + currentUser.getUid(), Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_view);

        initUI();

        mAuth = FirebaseAuth.getInstance();

        initFirebase();
    }

    private void initFirebase(){
        mDatabase = FirebaseDatabase.getInstance();
    }

    private void initUI(){
        mEmail = (EditText)findViewById(R.id.editText3);
        mPassword = (EditText)findViewById(R.id.editText4);
        mName = (EditText)findViewById(R.id.editTextname);
        mSurname = (EditText)findViewById(R.id.editTextsurname);
    }

    private void createFirebaseUser(){

        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SIGNUP", "createUserWithEmail:success");
                            FirebaseUser user_reg = FirebaseAuth.getInstance().getCurrentUser();
                            DatabaseReference ref = mDatabase.getReference("Users/" + user_reg.getUid());
                            ref.setValue(u);
                            System.out.println(user_reg.getUid());
                            System.out.println(user_reg.getEmail());
                            updateUI(user_reg);
                            startActivity(new Intent(SignUpView.this, Main2Activity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SIGNUP", "createUserWithEmail:failure", task.getException());
                            //Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                            //Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user){
        setUID(user.getUid());
        Log.d("UPDATE UID",getUID());
    }

    public void submit(View view) {

        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (true) {
            // Name, email address, and profile photo Url

            System.out.println("ci sono!");

            String email = mEmail.getText().toString();
            String name = mName.getText().toString();
            String surname = mSurname.getText().toString();
            String userpath = "Users/";

            u.setEmail(email);
            u.setName(name);
            u.setSurname(surname);
            u.setNascita("gg/mm/aaaa");

            u.setWeight(null);
            u.setHeight(null);
            u.setBlood(null);


            createFirebaseUser();

            /*

            //String UID = user.getUid();
            String path = userpath.concat(getUID());

            DatabaseReference myRefnode = mDatabase.getReference(path);
            myRefnode.setValue(email);
            DatabaseReference myRefName = mDatabase.getReference(path.concat("/name"));
            myRefName.setValue(name);
            DatabaseReference myRefmail = mDatabase.getReference(path.concat("/email"));
            myRefmail.setValue(email);
            DatabaseReference myRefSurname = mDatabase.getReference(path.concat("/surname"));
            myRefSurname.setValue(surname);
            DatabaseReference myRefnascita = mDatabase.getReference(path.concat("/nascita"));
            myRefnascita.setValue("gg/mm/aaaa");
            DatabaseReference myRefWeight = mDatabase.getReference(path.concat("/weight"));
            myRefWeight.setValue("none");
            DatabaseReference myRefHeight = mDatabase.getReference(path.concat("/height"));
            myRefHeight.setValue("none");
            DatabaseReference myRefBlood = mDatabase.getReference(path.concat("/blood"));
            myRefBlood.setValue("none");
            */
        }

    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
