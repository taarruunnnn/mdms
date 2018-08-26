package com.arun.newtru;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DistributorActivity extends AppCompatActivity {
    Button mapBtn;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    List<usernew> uobjs2 = new ArrayList<usernew>();
    DatabaseReference myRef;
    boolean isResuming = false;
    ValueEventListener listener, temp;
    NetworkChecker mConnReceiver;
    LocationChecker mConnReceiver2;
    FirebaseUser mUser;


    @Override
    public void onPause(){
        super.onPause();
        if(temp!=null) {

        }
    }

    @Override
    public void onStop(){
        super.onStop();
        if(temp!=null) {

        }
    }

    @Override
    public void onRestart(){
        super.onRestart();
        Log.i("order", "onStart()");
        if(!isResuming) {
            progressDialog = ProgressDialog.show(DistributorActivity.this, "Please wait...", "Loading...", true);
            myRef.child("todeliver").addListenerForSingleValueEvent(listener);
        }
        isResuming = false;
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i("order", "onResume()");
        isResuming = true;
        progressDialog = ProgressDialog.show(DistributorActivity.this, "Please wait...", "Loading...", true);
        myRef.child("todeliver").addListenerForSingleValueEvent(listener);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributor);
        mConnReceiver = new NetworkChecker();
        mConnReceiver2 = new LocationChecker();
        registerReceiver(mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        registerReceiver(mConnReceiver2, new IntentFilter(LocationManager.KEY_PROVIDER_ENABLED));
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        myRef = database.getReference();
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot shot2: dataSnapshot.getChildren()){
                    usernew value = shot2.getValue(usernew.class);
                    uobjs2.add(value);
                }
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError){

            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        // Associate searchable configuration with the SearchView
        return true;
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_logout:
                DatabaseReference newnewref = database.getReference();
                newnewref.child("livelocation").child(mUser.getUid()).setValue(null);
                final ProgressDialog progressDialog = ProgressDialog.show(DistributorActivity.this, "Please wait...", "Signing you out...", true);
                mAuth.signOut();
                Intent intent = new Intent(DistributorActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startMap(View v){
        if(uobjs2.size()==0){
            Toast.makeText(this, "Currently no customers to deliver to. Contact admin.", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(DistributorActivity.this, TestmapActivity.class);
            intent.putExtra("LIST", (Serializable) uobjs2);
            startActivity(intent);
        }
    }
}
