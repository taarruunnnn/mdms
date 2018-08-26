package com.arun.newtru;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AdminActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    private FirebaseUser mUser;
    boolean isResuming = false;
    BroadcastReceiver tickReceiver;
    boolean flag = false;
    List<usernew> userobjs = new ArrayList<usernew>();
    List<trackclass> trackobjs = new ArrayList<trackclass>();
    NetworkChecker mConnReceiver;
    DatabaseReference myref22;
    ValueEventListener listener;
    public void trackMap(View v){
        if(trackobjs.size()==0){
            Toast.makeText(this, "Empty!", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(this, TrackActivity.class);
            intent.putExtra("LIST", (Serializable) trackobjs);
            startActivity(intent);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onRestart(){
        super.onRestart();
        Log.i("order", "onStart()");
        if(!isResuming) {
            progressDialog = ProgressDialog.show(this, "Please wait...", "Loading...", true);
            myref22.child("livelocation").addListenerForSingleValueEvent(listener);
        }
        isResuming = false;
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i("order", "onResume()");
        isResuming = true;
        progressDialog = ProgressDialog.show(this, "Please wait...", "Loading...", true);
        myref22.child("livelocation").addListenerForSingleValueEvent(listener);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        //LocationChecker mConnReceiver2;
        //mConnReceiver2 = new LocationChecker();
        mConnReceiver = new NetworkChecker();
        registerReceiver(mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        //registerReceiver(mConnReceiver2, new IntentFilter(LocationManager.KEY_PROVIDER_ENABLED));
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        myref22 = database.getReference();
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot shot: dataSnapshot.getChildren()){
                    trackclass trackobj = shot.getValue(trackclass.class);
                    trackobjs.add(trackobj);
                }
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        tickReceiver  = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().compareTo(Intent.ACTION_TIME_TICK)==0){
                    Calendar cal = Calendar.getInstance();
                    if(cal.get(Calendar.HOUR_OF_DAY)==22 && cal.get(Calendar.MINUTE)==58) {
                        Toast.makeText(AdminActivity.this, "Inside broadcast receiver", Toast.LENGTH_SHORT).show();
                        DatabaseReference myRef = database.getReference();
                        ValueEventListener listener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                                    usernew obj = shot.getValue(usernew.class);
                                    userobjs.add(obj);
                                    flag = true;
                                }
                                if (flag == true) {
                                    Toast.makeText(AdminActivity.this, "Inside writing part", Toast.LENGTH_SHORT).show();
                                    DatabaseReference newRef = database.getReference();
                                    for (usernew temp : userobjs) {
                                        newRef.child("todeliver").child(temp.userid).setValue(temp);
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        };
                        myRef.child("user").addListenerForSingleValueEvent(listener);
                    }
                }
            }
        };
        registerReceiver(tickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    public void updateVals(View v){
        Calendar cal = Calendar.getInstance();
        Toast.makeText(AdminActivity.this, "Inside broadcast receiver", Toast.LENGTH_SHORT).show();
        DatabaseReference myRef = database.getReference();
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    usernew obj = shot.getValue(usernew.class);
                    userobjs.add(obj);
                    flag = true;
                }
                if (flag == true) {
                    Toast.makeText(AdminActivity.this, "Inside writing part", Toast.LENGTH_SHORT).show();
                    DatabaseReference newRef = database.getReference();
                    for (usernew temp : userobjs) {
                        newRef.child("todeliver").child(temp.userid).setValue(temp);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        myRef.child("user").addListenerForSingleValueEvent(listener);

        Toast.makeText(AdminActivity.this, "Inside broadcast receiver2", Toast.LENGTH_SHORT).show();
        DatabaseReference myRef2 = database.getReference();
        ValueEventListener listener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    usernew obj = shot.getValue(usernew.class);
                    userobjs.add(obj);
                    flag = true;
                }
                if (flag == true) {
                    Toast.makeText(AdminActivity.this, "Inside writing part2", Toast.LENGTH_SHORT).show();
                    DatabaseReference newRef = database.getReference();
                    for (usernew temp : userobjs) {
                        newRef.child("user").child(temp.userid).child("currRequirement").setValue(temp.defaultRequirement);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        myRef2.child("user").addListenerForSingleValueEvent(listener2);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        // Associate searchable configuration with the SearchView
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_logout:
                final ProgressDialog progressDialog = ProgressDialog.show(AdminActivity.this, "Please wait...", "Signing you out...", true);
                mAuth.signOut();
                Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
