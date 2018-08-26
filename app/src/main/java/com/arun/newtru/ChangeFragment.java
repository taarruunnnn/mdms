package com.arun.newtru;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeFragment extends Fragment {
    NumberPicker npp;
    FirebaseAuth mAuth;
    public BroadcastReceiver tickReceiver;
    FirebaseDatabase database;
    FirebaseUser mUser;
    int mhourtoday, mhourtmrw, mminstoday, mminstmrw, today, tmrw;
    Calendar cal, cal2;
    Button updateBtn;
    Double dt, tdt;
    TextView dummyTextView, dummyTextView2;
    usernew value;
    public ChangeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        tickReceiver  = new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().compareTo(Intent.ACTION_TIME_TICK)==0){
                    database = FirebaseDatabase.getInstance();
                    mAuth = FirebaseAuth.getInstance();
                    mUser = mAuth.getCurrentUser();
                    DatabaseReference myRef = database.getReference();
                    ValueEventListener listener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot shot2: dataSnapshot.getChildren()){
                                if(shot2.getValue(usernew.class).userid.equals(mUser.getUid())) {
                                    value = shot2.getValue(usernew.class);
                                    dt = (value.currRequirement * 2) - 1;
                                    tdt = dt;
                                    npp.setValue(dt.intValue());
                                    cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
                                    today = cal.get(Calendar.DAY_OF_YEAR);
                                    cal2 = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
                                    cal2.add(Calendar.DAY_OF_YEAR, 1);
                                    tmrw = cal2.get(Calendar.DAY_OF_YEAR);
                                    mhourtoday = cal.get(Calendar.HOUR_OF_DAY);
                                    mminstoday = cal.get(Calendar.MINUTE);
                                    mhourtmrw = cal2.get(Calendar.HOUR_OF_DAY);
                                    mminstmrw = cal2.get(Calendar.MINUTE);
                                    if((mhourtoday >= 21 && mhourtoday <= 23)||(mhourtmrw>=0 && mhourtoday<=7) ) {
                                        dummyTextView.setText("Time for updating is over. The following final updated milk requirement will be considered and delivered tomorrow.");
                                        String temp = Double.toString(value.currRequirement);
                                        temp = temp + " litres";
                                        dummyTextView2.setText(temp);
                                        dummyTextView2.setVisibility(View.VISIBLE);
                                        npp.setVisibility(View.GONE);
                                        updateBtn.setVisibility(View.GONE);
                                    }
                                    else {
                                        dummyTextView.setText("Update your requirements of milk for tomorrow. This will automatically be disabled every day by 09:00 PM for our convenience.");
                                        npp.setValue(tdt.intValue());
                                        dummyTextView2.setVisibility(View.GONE);
                                        updateBtn.setVisibility(View.VISIBLE);
                                        npp.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    myRef.child("user").addValueEventListener(listener);
                }
            }
        };
        View v = inflater.inflate(R.layout.fragment_change, container, false);
        // Inflate the layout for this fragment
        dummyTextView = (TextView) v.findViewById(R.id.dummyTextView);
        dummyTextView.setText("Update your requirements of milk for tomorrow. This will automatically be disabled every day by 09:00 PM for our convenience.");
        dummyTextView2 = (TextView) v.findViewById(R.id.dummyTextView2);
        dummyTextView2.setVisibility(View.GONE);
        npp = (NumberPicker) v.findViewById(R.id.npp);
        updateBtn = (Button) v.findViewById(R.id.updateValueButton);
        final String nums[] = {"0.5 litres", "1 litre", "1.5 litres", "2 litres", "2.5 litres", "3 litres", "3.5 litres", "4 litres", "4.5 litres", "5 litres", "5.5 litres", "6 litre", "6.5 litres", "7 litres", "7.5 litres", "8 litres", "8.5 litres", "9 litres", "9.5 litres", "10 litres"};
        npp.setMaxValue(nums.length - 1);
        npp.setMinValue(0);
        npp.setWrapSelectorWheel(false);
        npp.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        npp.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return nums[value];
            }
        });
        try {
            @SuppressLint("PrivateApi")
            Method method = npp.getClass().getDeclaredMethod("changeValueByOne", boolean.class);
            method.setAccessible(true);
            method.invoke(npp, true);
        } catch (NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        today = cal.get(Calendar.DAY_OF_YEAR);
        cal2 = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        cal2.add(Calendar.DAY_OF_YEAR, 1);
        tmrw = cal2.get(Calendar.DAY_OF_YEAR);
        mhourtoday = cal.get(Calendar.HOUR_OF_DAY);
        mminstoday = cal.get(Calendar.MINUTE);
        mhourtmrw = cal2.get(Calendar.HOUR_OF_DAY);
        mminstmrw = cal2.get(Calendar.MINUTE);
        if((mhourtoday >= 21 && mhourtoday <= 23)||(mhourtmrw>=0 && mhourtoday<=7) ) {
            dummyTextView.setText("Time for updating is over. The following final updated milk requirement will be considered and delivered tomorrow.");
            dummyTextView2.setText("Fetching value. Please wait...");
            dummyTextView2.setVisibility(View.VISIBLE);
            npp.setVisibility(View.GONE);
            updateBtn.setVisibility(View.GONE);
        }
        else {
            dummyTextView.setText("Update your requirements of milk for tomorrow. This will automatically be disabled every day by 09:00 PM for our convenience.");
            dummyTextView2.setVisibility(View.GONE);
            updateBtn.setVisibility(View.VISIBLE);
            npp.setVisibility(View.VISIBLE);
        }
        getActivity().registerReceiver(tickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_change_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){

    }
}

