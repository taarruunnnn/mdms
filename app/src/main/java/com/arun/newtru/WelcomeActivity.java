package com.arun.newtru;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import com.arun.newtru.distid;

public class WelcomeActivity extends AppCompatActivity{

    //declare required variables
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;                                             //adding dots to the layout
    private int[] layouts;                                              //declaring no of pages required in the viewPager
    private Button btnNext, btnSkip;                                //next button in layout
    private FirebaseAuth mAuth;
    private PrefManager prefManager;                                    //used for firstTimeLaunch() process
    int mLastPage=0;                                                    //to store what the last page in the viewPager is
    List<String> userids = new ArrayList<String>();                     //for storing retrieved newuser tokens from firebase                                                    //usertoken editText
    private FirebaseDatabase database;     //firebase object
    AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC); //validator object
    EditText nam, ema, firad, secad, thad, uid;
    NumberPicker np;
    MaterialEditText pass, passConfirm;
    NetworkChecker mConnReceiver;
    private FirebaseUser fuser;
    private FirebaseUser fuser2;
    ProgressDialog progressDialog;
    String name, email, firstaddr, secondaddr, thirdaddr, userid, address;
    double defaultRequirement;
    //LocationChecker mConnReceiver2;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        fuser = mAuth.getCurrentUser();
        database =  FirebaseDatabase.getInstance();
        mConnReceiver = new NetworkChecker();
        //mConnReceiver2 = new LocationChecker();
        registerReceiver(mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        //registerReceiver(mConnReceiver2, new IntentFilter(LocationManager.KEY_PROVIDER_ENABLED));

        SharedPreferences settings = getSharedPreferences(PrefManager.PREFS_NAME, 0);
        //Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_welcome);
        //Getting usertokens from database and initialising list
        // adding listener to the db reference object

        //checking whether user logged in
        if(fuser!=null){
            startActivity(new Intent(WelcomeActivity.this, DummyActivity.class));
            finish();
        }else {
                DatabaseReference myRef = database.getReference("newusers");
                ValueEventListener listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i("Count ", "" + dataSnapshot.getChildrenCount());
                        for (DataSnapshot shot : dataSnapshot.getChildren()) {
                            userid uidobj = shot.getValue(userid.class);                    //getting values into uidobj and adding to list
                            userids.add(uidobj.id);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(WelcomeActivity.this, "Make sure you have an active internet connection.", Toast.LENGTH_SHORT).show();
                    }
                };
                myRef.addListenerForSingleValueEvent(listener);
                viewPager = (ViewPager) findViewById(R.id.view_pager);
                dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
                btnSkip = (Button) findViewById(R.id.btn_skip);
                btnSkip.setText(getString(R.string.login));
                btnNext = (Button) findViewById(R.id.btn_next);

                // layouts of all welcome sliders
                // add few more layouts if you want
                layouts = new int[]{
                        R.layout.welcome_side1,
                        R.layout.welcome_side2,
                        R.layout.welcome_side3,
                };

                // adding bottom dots
                addBottomDots(0);

                // making notification bar transparent
                changeStatusBarColor();

                myViewPagerAdapter = new MyViewPagerAdapter();
                viewPager.setAdapter(myViewPagerAdapter);
                viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
                btnSkip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int current = getItem(-1);
                        if (current >= 0) {
                            viewPager.setCurrentItem(current);
                        } else if (current + 1 == 0) {
                            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                        }
                    }
                });

                btnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // checking for last page
                        // if last page home screen will be launched
                        int current = getItem(+1);
                        if (current < layouts.length) {
                            // move to next screen
                            viewPager.setCurrentItem(current);
                        } else {
                            launchHomeScreen();
                        }
                    }
                });
            }

    }



    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];
        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }


    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }


    public void launchHomeScreen() {
        int val = 0;
        String temp = pass.getText().toString();
        String temp2 = passConfirm.getText().toString();
        if(temp.length()>=8){
            val = 1;
            if(temp.equals(temp2)){
                val = 2;
            }
        }else {
            val = 0;
        }
        if(val == 2) {
            name = nam.getText().toString();
            email = ema.getText().toString();
            firstaddr = firad.getText().toString();
            secondaddr = secad.getText().toString();
            thirdaddr = thad.getText().toString();
            address = firstaddr + ", " + secondaddr + ", " + thirdaddr;
            userid = uid.getText().toString().trim();
            int t3 = np.getValue();
            defaultRequirement = (t3+1)/2;
            progressDialog = ProgressDialog.show(WelcomeActivity.this, "Please wait...", "Loading...", true);
            mAuth.createUserWithEmailAndPassword(email, temp)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(WelcomeActivity.this, "Authentication success",
                                        Toast.LENGTH_SHORT).show();
                                fuser2 = mAuth.getCurrentUser();
                                user uobj = new user(name, email, address, fuser2.getUid(), defaultRequirement+0.5, defaultRequirement+0.5);
                                DatabaseReference ref3 = database.getReference("newusers");
                                ref3.child(userid).setValue(null);
                                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                                intent.putExtra("myclass", uobj);
                                progressDialog.dismiss();
                                startActivity(intent);
                                Toast.makeText(WelcomeActivity.this, "Yay! You've successfully registered!", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                if(!task.isSuccessful()){
                                    FirebaseAuthException e = (FirebaseAuthException)task.getException();
                                    Log.e("LoginActivity", "Failed Registration", e);
                                    return;
                                }
                            }
                        }
                    });
        }else if(val == 1) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Password must be atleast 8 characters long!", Toast.LENGTH_LONG).show();
        }


    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @RequiresApi(api = Build.VERSION_CODES.FROYO)
        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            int curr = viewPager.getCurrentItem();
            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
            }
            if (position == 0){
                btnSkip.setText(getString(R.string.login));
            }else{
                btnSkip.setText(getString(R.string.skip));
            }
            if ((curr == 2 && mLastPage == 1) || (curr == 2 && mLastPage == 2)) {    //validation done when page changer from 1->2
                //adding validators
                np = (NumberPicker) findViewById(R.id.np);
                final String nums[] = {"0.5 litres", "1 litre", "1.5 litres", "2 litres", "2.5 litres", "3 litres", "3.5 litres", "4 litres", "4.5 litres", "5 litres", "5.5 litres", "6 litre", "6.5 litres", "7 litres", "7.5 litres", "8 litres", "8.5 litres", "9 litres", "9.5 litres", "10 litres" };
                np.setMaxValue(nums.length-1);
                np.setMinValue(0);
                np.setWrapSelectorWheel(false);
                np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                np.setFormatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int value) {
                        return nums[value];
                    }
                });
                try {
                    @SuppressLint("PrivateApi")
                    Method method = np.getClass().getDeclaredMethod("changeValueByOne", boolean.class);
                    method.setAccessible(true);
                    method.invoke(np, true);
                } catch (NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                nam = (EditText)findViewById(R.id.nameEditText);
                ema = (EditText) findViewById(R.id.emailEditText);
                firad = (EditText) findViewById(R.id.firstAddressEditText);
                secad = (EditText) findViewById(R.id.secondAddressEditText);
                thad = (EditText) findViewById(R.id.thirdAddressEditText);
                uid = (EditText) findViewById(R.id.uidEditText);
                pass = (MaterialEditText) findViewById(R.id.passEditText);
                passConfirm = (MaterialEditText) findViewById(R.id.passConfirmEditText);
                mAwesomeValidation.addValidation(nam, "[a-zA-Z\\s]+", "Please enter a valid name");
                mAwesomeValidation.addValidation(ema, android.util.Patterns.EMAIL_ADDRESS, "Please enter a valid email ID");
                mAwesomeValidation.addValidation(firad, RegexTemplate.NOT_EMPTY, "Cannot be empty");
                mAwesomeValidation.addValidation(secad, RegexTemplate.NOT_EMPTY, "Cannot be empty");
                mAwesomeValidation.addValidation(thad, RegexTemplate.NOT_EMPTY, "Cannot be empty");
                mAwesomeValidation.addValidation(uid, new SimpleCustomValidation() {   //validation for user token
                    @Override
                    public boolean compare(String s) {
                        return userids.contains(uid.getText().toString().trim());
                    }
                }, "Please turn on WiFi/Mobile Data. Or your token is wrong. Please contact office for further details.");
                boolean val = mAwesomeValidation.validate();
                if (!val) {
                    Toast.makeText(WelcomeActivity.this, "Oops! There are some problems", Toast.LENGTH_SHORT).show();
                    viewPager.setCurrentItem(1, true);
                }
            }
            mLastPage = position;
        }


        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }


        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };


    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }


    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}