package com.ma.se.hospitato;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.BreakIterator;
import java.util.Arrays;

public class FirstView extends AppCompatActivity {

    FirebaseAuth mAuth;
    private Intent intent;
    FirebaseDatabase mDatabase;
    private CallbackManager mCallbackManager;
    private static final String TAG= "FACELOG";
    GoogleSignInClient mGoogleSignInClient;
    ImageView googleBTN;
    ImageView fb_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_view);

        //FOR THE HASH KEY OF THE PHONE - FACEBOOK
        try {
            PackageInfo info = getPackageManager().getPackageInfo( "com.ma.se.hospitato", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures)
            { MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(signature.toByteArray());
            Log.d("KeyHash", "KeyHash:" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            //Toast.makeText(getApplicationContext(), Base64.encodeToString(md.digest(), Base64.DEFAULT), Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e)
        {

        } catch (NoSuchAlgorithmException e) { }
        //


        mAuth = FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        googleBTN = (ImageView) findViewById(R.id.googleBTN);
        googleBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
            }
        });

        if (mAuth.getCurrentUser() != null) {
            intent = new Intent(this, Main2Activity.class);
            startActivity(intent);
            finish();
        }

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        fb_button=(ImageView)findViewById(R.id.login_button);
        fb_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(FirstView.this, Arrays.asList("email", "public_profile","user_friends"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebook:onCancel");
                        // ...
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "facebook:onError", error);
                        // ...
                    }
                });
            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser !=null){
            updateUI(currentUser);
        }

    }

    @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }

        // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            DatabaseReference ref = mDatabase.getReference("Users/" + user.getUid());
                            Profile ru = new Profile();
                            setProfile(user,ru);
                            ref.setValue(ru);
                            Intent i = new Intent(getApplicationContext(),Main2Activity.class);
                            startActivity(i);
                            finish();
                            Toast.makeText(getApplicationContext(),"User logged successfully",Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(),"You're already logged",Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            DatabaseReference ref = mDatabase.getReference("Users/" + user.getUid());
                            Profile ru = new Profile();
                            setProfile(user,ru);
                            ref.setValue(ru);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(FirstView.this, "Authentication Failed. You've used the same email for Google authentication.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void signIn(View view){
        intent = new Intent(this, LogInView.class);
        startActivity(intent);
        finish();
    }

    public void signUp(View view){
        intent = new Intent(this, SignUpView.class);
        startActivity(intent);
        finish();
    }

    public void skipIt(View view){
        intent = new Intent(this, Main2Activity.class);
        intent.putExtra("firstAction", "skipped");
        startActivity(intent);

    }

    private void updateUI(FirebaseUser user) {
        intent=new Intent(this,Main2Activity.class);
        startActivity(intent);
        finish();
    }

    private void setProfile(FirebaseUser user,Profile ru){
        ru.setName(user.getDisplayName());
        ru.setEmail(user.getEmail());
        ru.setSurname("-");
        ru.setNascita("gg/mm/aaaa");
        ru.setWeight(null);
        ru.setHeight(null);
        ru.setBlood(null);
    }


}
