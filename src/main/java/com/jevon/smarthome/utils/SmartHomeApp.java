package com.jevon.smarthome.utils;

import android.app.Application;
import android.content.Context;


public class SmartHomeApp extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        super.onCreate();
    }

    public static Context getContext() {
        return mContext;
    }


}
