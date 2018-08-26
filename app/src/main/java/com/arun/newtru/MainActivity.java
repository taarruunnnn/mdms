package com.arun.newtru;


import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    private final static int PLACE_PICKER_REQUEST = 1;
    double lat, lang;
    FirebaseDatabase database;
    Calendar cal;
    double milkAmount;
    NetworkChecker mConnReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mConnReceiver = new NetworkChecker();
        database = FirebaseDatabase.getInstance();
        DatabaseReference ref3 = database.getReference();
        ref3.child("amount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                milkAmount = dataSnapshot.getValue(Double.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        cal = Calendar.getInstance();
        registerReceiver(mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        database = FirebaseDatabase.getInstance();
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            // for activty
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
            // for fragment
            //startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case PLACE_PICKER_REQUEST:
                    Place place = PlacePicker.getPlace(this, data);
                    String placeName = String.format("Place: %s", place.getName());
                    lat = place.getLatLng().latitude;
                    lang = place.getLatLng().longitude;
                    Intent intent = getIntent();
                    user uobj = (user) intent.getSerializableExtra("myclass");
                    uobj.addLatLang(lat, lang);
                    int msyear = cal.get(Calendar.YEAR);
                    int msmonth = cal.get(Calendar.MONTH);
                    int msdate = cal.get(Calendar.DAY_OF_MONTH);
                    usernew uobjnew = new usernew(uobj.address, uobj.defaultRequirement, uobj.defaultRequirement, uobj.email, uobj.lat, uobj.lang, msdate, msmonth, msyear, uobj.name, uobj.userid);
                    DatabaseReference ref2 = database.getReference("user");
                    ref2.child(uobjnew.userid).setValue(uobjnew);
                    startActivity(new Intent(MainActivity.this, UserActivity.class));
                    finish();
            }
        }
    }
}
