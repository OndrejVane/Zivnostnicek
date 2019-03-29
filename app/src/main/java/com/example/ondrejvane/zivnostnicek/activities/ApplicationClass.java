package com.example.ondrejvane.zivnostnicek.activities;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.squareup.leakcanary.LeakCanary;

/**
 * Aplikační třída
 */
public class ApplicationClass extends Application {

    private final String TAG = "ApplicationClass";

    //proměnná, která určuje zda ze zapnutá kontrola paměti nebo ne
    private static boolean isLeakCanaryOn = false;

    //rozlišení obrazovky
    public static int screenWidth;
    public static int screenHeight;

    /**
     * Metoda, která je volána při spuštění celé aktivity.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        //kontrola memory leaku
        if (isLeakCanaryOn) {
            Log.d(TAG, "LeakCanary is ON");
            if (LeakCanary.isInAnalyzerProcess(this)) {
                return;
            }
            LeakCanary.install(this);
        } else {
            Log.d(TAG, "LeakCanary is OFF");
        }


        //zjištění šířky a výšky obrazovky
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        assert wm != null;
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //uložení do globálních proměnných
        screenWidth = size.x;
        screenHeight = size.y;

        Log.d(TAG, "Screen width: " + screenWidth);
        Log.d(TAG, "Screen height: " + screenHeight);


    }

    /**
     * Tato metoda je spuštěna, pokud má systém málo paměti.
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(TAG, "Low memory!!!");

    }
}
