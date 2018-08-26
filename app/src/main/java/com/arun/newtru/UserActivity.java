package com.arun.newtru;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.arun.newtru.ChangeFragment;
import com.arun.newtru.RecordsFragment;
import com.arun.newtru.AccountFragment;
import com.arun.newtru.R;
import com.arun.newtru.ViewPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UserActivity extends AppCompatActivity {
    NetworkChecker mConnReceiver;
    LocationChecker mConnReceiver2;
    //This is our tablayout
    private TabLayout tabLayout;
    private FirebaseAuth mAuth;
    //This is our viewPager
    private ViewPager viewPager;
    NumberPicker npp;
    FirebaseDatabase database;
    FirebaseUser mUser;
    usernew value;

    ViewPagerAdapter adapter;

    //Fragments

    ChangeFragment changeFragment;
    RecordsFragment recordsFragment;
    AccountFragment accountFragment;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public void updateRequirement(View v){
        npp = (NumberPicker) findViewById(R.id.npp);
        int t3 = npp.getValue();
        double newRequirement = (t3+1)/2;
        DatabaseReference myRef2 = database.getReference();
        if(t3%2==1) {
            myRef2.child("user").child(mUser.getUid()).child("currRequirement").setValue(newRequirement);
        }else{
            myRef2.child("user").child(mUser.getUid()).child("currRequirement").setValue(newRequirement + 0.5);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mConnReceiver = new NetworkChecker();
        //mConnReceiver2 = new LocationChecker();
        //registerReceiver(mConnReceiver2, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        registerReceiver(mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        getSupportActionBar().setElevation(0);
        viewPager.setOffscreenPageLimit(3);
        setupViewPager(viewPager);
        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tablayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(),false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(position).select();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });




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
                final ProgressDialog progressDialog = ProgressDialog.show(UserActivity.this, "Please wait...", "Signing you out...", true);
                mAuth.signOut();
                Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupViewPager(ViewPager viewPager)
    {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        recordsFragment=new RecordsFragment();
        changeFragment=new ChangeFragment();
        accountFragment=new AccountFragment();
        adapter.addFragment(changeFragment,"CHANGE");
        adapter.addFragment(recordsFragment,"RECORDS");
        adapter.addFragment(accountFragment,"ACCOUNT");
        viewPager.setAdapter(adapter);
    }

}