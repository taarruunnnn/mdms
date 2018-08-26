package com.arun.newtru;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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

public class DummyActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    boolean flag = false;
    ProgressDialog progressDialog;
    NetworkChecker mConnReceiver;
    List<String> distids = new ArrayList<String>();
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);
        mConnReceiver = new NetworkChecker();
        //LocationChecker mConnReceiver2;
        //mConnReceiver2 = new LocationChecker();
        registerReceiver(mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        //registerReceiver(mConnReceiver2, new IntentFilter(LocationManager.KEY_PROVIDER_ENABLED));
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser fuser = mAuth.getCurrentUser();
        DatabaseReference myRef2 = database.getReference();
        ValueEventListener listener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot2) {
                Log.i("Count ", "" + dataSnapshot2.getChildrenCount());
                for (DataSnapshot shot2 : dataSnapshot2.getChildren()) {
                    distid distidobj = shot2.getValue(distid.class);                    //getting values into uidobj and adding to list
                    if(fuser.getUid().equals(distidobj.id)){
                        progressDialog.dismiss();
                        flag=true;
                        startActivity(new Intent(DummyActivity.this, DistributorActivity.class));
                        finish();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DummyActivity.this, "Make sure you have an active internet connection.", Toast.LENGTH_SHORT).show();
            }
        };
        ValueEventListener listener3 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot2) {
                Log.i("Count ", "" + dataSnapshot2.getChildrenCount());
                for (DataSnapshot shot2 : dataSnapshot2.getChildren()) {
                    distid distidobj = shot2.getValue(distid.class);                    //getting values into uidobj and adding to list
                    if(fuser.getUid().equals(distidobj.id)){
                        progressDialog.dismiss();
                        flag=true;
                        startActivity(new Intent(DummyActivity.this, AdminActivity.class));
                        finish();
                    }
                    if(flag == false){
                        progressDialog.dismiss();
                        startActivity(new Intent(DummyActivity.this, UserActivity.class));
                        finish();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DummyActivity.this, "Make sure you have an active internet connection.", Toast.LENGTH_SHORT).show();
            }
        };
        progressDialog = ProgressDialog.show(DummyActivity.this, "Please wait...", "Loading...", true);
        myRef2.child("distributor").addListenerForSingleValueEvent(listener2);
        myRef2.child("admin").addListenerForSingleValueEvent(listener3);


    }

}
