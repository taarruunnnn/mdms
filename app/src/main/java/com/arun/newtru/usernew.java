package com.arun.newtru;

import java.io.Serializable;

public class usernew implements Serializable {
    String name;
    String email;
    String address;
    String userid;
    double defaultRequirement, lat, lang, currRequirement;
    int msyear, msmonth, msdate;

    public usernew(){

    }

    public usernew(String address, double currRequirement, double defaultRequirement, String email,  double lat, double lang, int msdate, int msmonth, int msyear, String name, String userid){
        this.name=name;
        this.email=email;
        this.currRequirement = currRequirement;
        this.address=address;
        this.userid = userid;
        this.defaultRequirement=defaultRequirement;
        this.lat=lat;
        this.lang=lang;
        this.msdate=msdate;
        this.msmonth=msmonth;
        this.msyear=msyear;
    }
}
