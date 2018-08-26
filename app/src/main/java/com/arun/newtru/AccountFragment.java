package com.arun.newtru;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {
    FirebaseDatabase database;
    AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    usernew value;
    TextView title;
    EditText nam, firaddr, secaddr, thaddr, nppp2;
    String name;
    double lat, lang;
    String[] address;
    Double dtdt, tdtdt;
    Button btn, submit;
    ProgressDialog progressDialog;

    public AccountFragment() {
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account, container, false);
        nam = (EditText) v.findViewById(R.id.nameUpdateText);
        title = (TextView)v.findViewById(R.id.editAccountTextView);
        firaddr = (EditText) v.findViewById(R.id.firstAddressUpdateText);
        secaddr = (EditText) v.findViewById(R.id.secondAddressUpdateText);
        thaddr = (EditText) v.findViewById(R.id.thirdAddressUpdateText);
        nppp2 = (EditText) v.findViewById(R.id.nppp2);
        submit = (Button) v.findViewById(R.id.submitUpdates);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        DatabaseReference myref = database.getReference();
        progressDialog = ProgressDialog.show(getActivity(), "Please wait...", "Signing you out...", true);
        myref.child("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                for(DataSnapshot shot: dataSnapshot.getChildren()){
                    if(mUser.getUid().equals(shot.getValue(usernew.class).userid)){
                        value = shot.getValue(usernew.class);
                        break;
                    }
                }
                nam.setText(value.name);
                dtdt = (value.defaultRequirement * 2) - 1;
                tdtdt = dtdt;
                title.setText("EDIT ACCOUNT - "+value.email);
                address = value.address.split(", ");
                firaddr.setText(address[0]);
                secaddr.setText(address[1]);
                thaddr.setText(address[2]);
                nppp2.setText(Double.toString(value.defaultRequirement));
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAwesomeValidation.addValidation(nam, RegexTemplate.NOT_EMPTY, "Cannot be empty");
                        mAwesomeValidation.addValidation(firaddr, RegexTemplate.NOT_EMPTY, "Cannot be empty");
                        mAwesomeValidation.addValidation(secaddr, RegexTemplate.NOT_EMPTY, "Cannot be empty");
                        mAwesomeValidation.addValidation(thaddr, RegexTemplate.NOT_EMPTY, "Cannot be empty");
                        mAwesomeValidation.addValidation(nppp2, RegexTemplate.NOT_EMPTY, "Cannot be empty");
                        String n = nam.getText().toString();
                        String a = firaddr.getText().toString()+", "+secaddr.getText().toString()+", "+thaddr.getText().toString();
                        String temp = nppp2.getText().toString();
                        Double newRequirement = Double.parseDouble(temp);
                        newRequirement = Math.round(newRequirement * 2)/2.0;
                        if(mAwesomeValidation.validate()) {
                            DatabaseReference newref = database.getReference();
                            newref.child("user").child(mUser.getUid()).child("name").setValue(n);
                            newref.child("user").child(mUser.getUid()).child("address").setValue(a);
                            newref.child("user").child(mUser.getUid()).child("defaultRequirement").setValue(newRequirement);
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.detach(AccountFragment.this);
                            ft.attach(AccountFragment.this);
                            ft.commit();
                            Toast.makeText(getActivity(), "Changes saved!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getActivity(), "Oops! There are problems.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_change_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

}

