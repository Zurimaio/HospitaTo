package com.ma.se.hospitato;

import android.content.Intent;
import android.net.Uri;
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

public class LogInView extends AppCompatActivity {

    EditText mNomeUtente;
    EditText mPassword;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_view);

        mAuth = FirebaseAuth.getInstance();



    }

    public void btnLogInClick(View view){
        mNomeUtente = (EditText)findViewById(R.id.usernameLI);
        mPassword = (EditText)findViewById(R.id.PasswordLI);

        String nomeUtente = mNomeUtente.getText().toString();
        String password = mPassword.getText().toString();

        loginUser(nomeUtente,password);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String UID = user.getUid();
            String email = user.getEmail();
            //Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            //boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            //String uid = user.getUid();

            Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra("email",email);
            intent.putExtra("UID",UID);
            startActivity(intent);
            finish();
        }

    }



    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Activity LogIn", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Activity LogIn", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LogInView.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
}
