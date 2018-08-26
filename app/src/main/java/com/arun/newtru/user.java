package com.arun.newtru;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class user implements Serializable{
    String name;
    String email;
    String address;
    String userid;
    double defaultRequirement, currRequirement, lat, lang;

    public user(){

    }

    public void addLatLang(double lat, double lang){
        this.lat=lat;
        this.lang=lang;
    }

    public void setUserid(String userid){
        this.userid = userid;
    }

    public user(String n, String e, String addr, String userid, double defaultRequirement, double currRequirement){
        this.name=n;
        this.email=e;
        this.address=addr;
        this.userid = userid;
        this.defaultRequirement = defaultRequirement;
        this.currRequirement = currRequirement;
    }
}
