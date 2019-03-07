package com.example.ondrejvane.zivnostnicek.activities;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.squareup.leakcanary.LeakCanary;

public class ApplicationClass extends Application {

    private final String TAG = "ApplicationClass";
    private boolean isLeakCanaryOn = false;
    public static int screenWidth;
    public static int screenHeight;

    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();

        //kontrola memory leaku
        if(isLeakCanaryOn){
            Log.d(TAG, "LeakCanary is ON");
            if (LeakCanary.isInAnalyzerProcess(this)) {
                return;
            }
            LeakCanary.install(this);
        }else {
            Log.d(TAG, "LeakCanary is OFF");
        }


        //zjištění šířky a výšky obrazovky
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        assert wm != null;
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        Log.d(TAG, "Screen width: " + screenWidth);
        Log.d(TAG, "Screen height: " + screenHeight);


    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(TAG, "Low memory!!!");

    }
}
