package com.arun.newtru;

import java.io.Serializable;

public class trackclass implements Serializable{
    double lat;
    double lang;
    String name;

    public trackclass(){

    }
    public trackclass(String name, double lat, double lang){
        this.lat=lat;
        this.name = name;
        this.lang=lang;
    }
}
