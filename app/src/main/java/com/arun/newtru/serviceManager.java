package com.arun.newtru;

import android.content.Context;
import android.content.ContextWrapper;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class serviceManager extends ContextWrapper{
    public serviceManager(Context base) {
        super(base);
    }

    public boolean isLocationAvailable(Context context){
        LocationManager lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        boolean gps_enabled = false;
        try {
            gps_enabled =  lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch (Exception ex){
            Log.e("LocationProviderError", ex.getMessage());
        }
        return gps_enabled;
    }

    public boolean isNetworkAvailable(){
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedMobile || haveConnectedWifi;

    }
}
