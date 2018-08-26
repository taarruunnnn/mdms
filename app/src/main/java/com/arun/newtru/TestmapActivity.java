package com.arun.newtru;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.google.android.gms.maps.CameraUpdateFactory.newCameraPosition;

public class TestmapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

    private GoogleMap mMap;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    double[] latarray, langarray;
    List<usernew> userobjs;
    int counter = 0;
    ListView lv;
    LatLngBounds.Builder builder;
    Marker marker[], currMarker;
    private static final String TAG = "DemoActivity";
    FusedLocationProviderClient mFusedLocationClient;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker lastClicked;
    List<String> your_array_list;
    SupportMapFragment mapFragment;
    CameraUpdate cu;
    LatLng latLang;
    ArrayAdapter<String> arrayAdapter;
    NetworkChecker mConnReceiver;
    LocationChecker mConnReceiver2;
    int lastClickedMarkerIndex;


    private SlidingUpPanelLayout mLayout;


    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (currMarker != null) {
                    currMarker.remove();
                }
                //Place current location marker
                latLang = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLang);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                currMarker = mMap.addMarker(markerOptions);
                database = FirebaseDatabase.getInstance();
                mAuth = FirebaseAuth.getInstance();
                mUser = mAuth.getCurrentUser();
                DatabaseReference newref2222 = database.getReference();
                newref2222.child("livelocation").child(mUser.getUid()).child("name").setValue(mUser.getUid());
                newref2222.child("livelocation").child(mUser.getUid()).child("lat").setValue(location.getLatitude());
                newref2222.child("livelocation").child(mUser.getUid()).child("lang").setValue(location.getLongitude());

            }
        };

    };
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(TestmapActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

    }

    @SuppressLint("RestrictedApi")
    public void initLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000); // two minute interval
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        initLocationRequest();
        builder = new LatLngBounds.Builder();
        marker = new Marker[latarray.length];
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }
        for (int i = 0; i < latarray.length; i++) {
            LatLng temp = new LatLng(latarray[i], langarray[i]);
            marker[i] = mMap.addMarker(new MarkerOptions().position(temp).title("Location " + Integer.toString(i + 1)));
            builder.include(marker[i].getPosition());
            counter++;
        }
        //move map camera
        LatLngBounds bounds = builder.build();
        int padding = 200;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.moveCamera(cu);
        mMap.setOnMarkerClickListener(this);
        // Add a marker in Sydney, Australia, and move the camera.
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testmap);
        mConnReceiver = new NetworkChecker();
        mConnReceiver2 = new LocationChecker();
        registerReceiver(mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        registerReceiver(mConnReceiver2, new IntentFilter(LocationManager.KEY_PROVIDER_ENABLED));
        database = FirebaseDatabase.getInstance();
        Intent intent = getIntent();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        userobjs = (List<usernew>)intent.getSerializableExtra("LIST");
        latarray = new double[userobjs.size()];
        langarray = new double[userobjs.size()];
        for (int i = 0; i < latarray.length; i++) {
            latarray[i] = userobjs.get(i).lat;
            langarray[i] = userobjs.get(i).lang;
        }
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main);
        mapFragment.getMapAsync(this);
        //setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));
        lv = (ListView) findViewById(R.id.list);


        your_array_list = new ArrayList<String>(Arrays.asList(
                "Please select a marker on map", "and then check here for details"
        ));

        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1, your_array_list );
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 3) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(TestmapActivity.this);
                    dialog.setTitle(Html.fromHtml("<font color='#FF0000'>Confirm?</font>"));
                    dialog.setMessage("Please confirm that you delivered the correct number of packets to the respective household.");
                    dialog.setPositiveButton(Html.fromHtml("<font color='#009688'>Yes</font>"), null); //Set to null. We override the onclick
                    dialog.setNegativeButton("No", null);
                    dialog.setCancelable(false);
                    final AlertDialog d = dialog.create();
                    d.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            Button button = ((AlertDialog) d).getButton(AlertDialog.BUTTON_POSITIVE);
                            button.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {
                                    String toremove1 = arrayAdapter.getItem(0);
                                    arrayAdapter.remove(toremove1);
                                    String toremove2 = arrayAdapter.getItem(0);
                                    arrayAdapter.remove(toremove2);
                                    String toremove3 = arrayAdapter.getItem(0);
                                    arrayAdapter.remove(toremove3);
                                    String toremove4 = arrayAdapter.getItem(0);
                                    arrayAdapter.remove(toremove4);
                                    d.dismiss();
                                    arrayAdapter.notifyDataSetChanged();
                                    mLayout.setPanelState(PanelState.HIDDEN);
                                    DatabaseReference ref3 = database.getReference("consumption");
                                    ref3.child(userobjs.get(lastClickedMarkerIndex).userid).child(Integer.toString(userobjs.get(lastClickedMarkerIndex).msyear)).child(Integer.toString(userobjs.get(lastClickedMarkerIndex).msmonth)).child(Integer.toString(userobjs.get(lastClickedMarkerIndex).msdate)).child("quantity").setValue(Double.toString(userobjs.get(lastClickedMarkerIndex).currRequirement));
                                    lastClicked.remove();
                                    DatabaseReference reff = database.getReference("todeliver");
                                    reff.child(userobjs.get(lastClickedMarkerIndex).userid).setValue(null);
                                }
                            });
                            Button button2 = ((AlertDialog) d).getButton(AlertDialog.BUTTON_NEGATIVE);
                            button2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    d.dismiss();
                                }
                            });
                        }
                    });
                    d.show();
                    //Toast.makeText(TestmapActivity.this, "onItemClick", Toast.LENGTH_SHORT).show();
                }else{
                    mLayout.setPanelState(PanelState.COLLAPSED);
                }
            }
        });
        arrayAdapter.notifyDataSetChanged();
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.addPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);
            }
        });
        mLayout.setFadeOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(PanelState.HIDDEN);
            }
        });
        mLayout.setAnchorPoint(0.4f);
        mLayout.setPanelState(PanelState.HIDDEN);
        //mLayout.setPanelState(PanelState.CO);
        TextView t = (TextView) findViewById(R.id.name);
        t.setText("Slide Up");
    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.demo, menu);
        MenuItem item = menu.findItem(R.id.action_toggle);
        if (mLayout != null) {
            if (mLayout.getPanelState() == PanelState.HIDDEN) {
                item.setTitle(R.string.action_show);
            } else {
                item.setTitle(R.string.action_hide);
            }
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_toggle: {
                if (mLayout != null) {
                    if (mLayout.getPanelState() != PanelState.HIDDEN) {
                        mLayout.setPanelState(PanelState.HIDDEN);
                        item.setTitle(R.string.action_show);
                    } else {
                        mLayout.setPanelState(PanelState.COLLAPSED);
                        item.setTitle(R.string.action_hide);
                    }
                }
                return true;
            }
            case R.id.action_anchor: {
                if (mLayout != null) {
                    if (mLayout.getAnchorPoint() == 1.0f) {
                        mLayout.setAnchorPoint(0.7f);
                        mLayout.setPanelState(PanelState.ANCHORED);
                        item.setTitle(R.string.action_anchor_disable);
                    } else {
                        mLayout.setAnchorPoint(1.0f);
                        mLayout.setPanelState(PanelState.COLLAPSED);
                        item.setTitle(R.string.action_anchor_enable);
                    }
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    */



    @Override
    public void onBackPressed() {
        if (mLayout != null &&
                (mLayout.getPanelState() == PanelState.EXPANDED || mLayout.getPanelState() == PanelState.ANCHORED)) {
            mLayout.setPanelState(PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
            this.startActivity(new Intent(TestmapActivity.this,DistributorActivity.class));
            finish();
        }
    }

    @Override
    public boolean onMarkerClick(Marker markr) {
        if (markr != currMarker) {
            for (int i = 0; i < marker.length; i++) {
                if (markr.equals(marker[i])) {
                    Double temp = userobjs.get(i).currRequirement*2;
                    List<String> currentlist = new ArrayList<String>(Arrays.asList(
                            "Name: "+ userobjs.get(i).name,
                            "Address: "+ userobjs.get(i).address,
                            "Requirement: "+ temp.intValue() + " packets", "Click here to set as delivered."));
                    arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, currentlist){
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View row = super.getView(position, convertView, parent);

                            if(position == 3)
                            {
                                row.setBackgroundColor (getResources().getColor(R.color.listhighlight)); // some color
                            }
                            else
                            {
                                // default state
                                row.setBackgroundColor (Color.WHITE); // default coloe
                            }
                            return row;
                        }
                    };
                    arrayAdapter.notifyDataSetChanged();
                    lv.setAdapter(arrayAdapter);
                    if (markr == lastClicked && mLayout.getPanelState() == PanelState.HIDDEN)
                        mLayout.setPanelState(PanelState.COLLAPSED);
                    else if(markr==lastClicked && mLayout.getPanelState() == PanelState.COLLAPSED)
                        mLayout.setPanelState(PanelState.HIDDEN);
                    else if (markr !=lastClicked && mLayout.getPanelState() == PanelState.HIDDEN)
                        mLayout.setPanelState(PanelState.COLLAPSED);
                    /*else if (markr !=lastClicked && mLayout.getPanelState() == PanelState.COLLAPSED)
                        mLayout.setPanelState(PanelState.COLLAPSED);*/
                    else if (markr == currMarker)
                        mLayout.setPanelState(PanelState.HIDDEN);
                    lastClicked = markr;
                    lastClickedMarkerIndex = i;
                }
            }
        }else{
            arrayAdapter= new ArrayAdapter<String>(
                    this, android.R.layout.simple_list_item_1, your_array_list );
            arrayAdapter.notifyDataSetChanged();
            mLayout.setPanelState(PanelState.HIDDEN);

        }
        return false;
    }
}
