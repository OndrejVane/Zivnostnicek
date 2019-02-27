package com.example.ondrejvane.zivnostnicek.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class WifiCheckerUtility {

    /**
     * Metoda, která kontroluje, zda je zařízení připojeno k internetu pomocí
     * wifi nebo ne.
     *
     * @param context kontext aktivity
     * @return logická hodnota připojen/nepřipojen
     */
    public static boolean isConnected(Context context){
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            return true;
        }else {
            return false;
        }
    }
}
