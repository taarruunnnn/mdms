package com.arun.newtru;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;


public class LoginActivity extends AppCompatActivity{
    EditText emailLogin, passLogin;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    NetworkChecker mConnReceiver;
    AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mConnReceiver = new NetworkChecker();
        registerReceiver(mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    public void startDistributorLogin(View v){
        startActivity(new Intent(LoginActivity.this, DistributorLoginActivity.class));
    }

    public void startRegister(View v){
        startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
        finish();
    }

    public void startLogin(View v){
        emailLogin = (EditText) findViewById(R.id.emailLoginEditText);
        passLogin = (EditText) findViewById(R.id.passwordLoginEditText);
        mAwesomeValidation.addValidation(emailLogin, RegexTemplate.NOT_EMPTY, "Cannot be empty");
        mAwesomeValidation.addValidation(emailLogin, RegexTemplate.NOT_EMPTY, "Cannot be empty");
        String email = emailLogin.getText().toString().trim();
        String password = passLogin.getText().toString();
        boolean val = mAwesomeValidation.validate();
        if(val){
        final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "Please wait...", "Signing in...", true);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("login", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            SharedPreferences settings = getSharedPreferences(PrefManager.PREFS_NAME, 0); // 0 - for private mode
                            SharedPreferences.Editor editor = settings.edit();
                            //Set "hasLoggedIn" to true
                            editor.putBoolean("hasLoggedIn", true);
                            // Commit the edits!
                            editor.apply();
                            startActivity(new Intent(LoginActivity.this, DummyActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }else{
            Toast.makeText(this, "Oops! There are problems.", Toast.LENGTH_SHORT).show();
        }
    }

}

