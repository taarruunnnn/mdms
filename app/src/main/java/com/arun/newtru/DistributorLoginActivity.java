package com.arun.newtru;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class DistributorLoginActivity extends AppCompatActivity{
    EditText emailLogin, passLogin;
    private FirebaseAuth mAuth;
    NetworkChecker mConnReceiver;
    List<String> distids = new ArrayList<String>();
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributor_login);
        mConnReceiver = new NetworkChecker();
        registerReceiver(mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("distributor");
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("Count ", "" + dataSnapshot.getChildrenCount());
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    distid distidobj = shot.getValue(distid.class);                    //getting values into uidobj and adding to list
                    distids.add(distidobj.id);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DistributorLoginActivity.this, "Make sure you have an active internet connection.", Toast.LENGTH_SHORT).show();
            }
        };
        myRef.addValueEventListener(listener); // adding listener to the db reference object
    }

    public void startDistributorLogin(View v){
        emailLogin = (EditText) findViewById(R.id.distEmailEditText);
        passLogin = (EditText) findViewById(R.id.distPasswordEditText);
        String email = emailLogin.getText().toString().trim();
        String password = passLogin.getText().toString();
        final ProgressDialog progressDialog = ProgressDialog.show(DistributorLoginActivity.this, "Please wait...", "Signing in...", true);
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
                            editor.commit();
                            if(distids.contains(user.getUid())) {
                                startActivity(new Intent(DistributorLoginActivity.this, DistributorActivity.class));
                                finish();
                            }else{
                                mAuth.signOut();
                                Toast.makeText(DistributorLoginActivity.this, "Distributor Authentication failed.", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(DistributorLoginActivity.this, "Distributor Authentication failed.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(DistributorLoginActivity.this, LoginActivity.class));
                            finish();
                        }
                    }
                });
    }

}

