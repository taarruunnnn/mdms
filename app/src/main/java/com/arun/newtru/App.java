package com.arun.newtru;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Rajesh on 3/25/2018.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/AkkRg_Pro_1.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
