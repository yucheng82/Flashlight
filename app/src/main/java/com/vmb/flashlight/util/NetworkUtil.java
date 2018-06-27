package com.vmb.flashlight.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null)
            return false;

        NetworkInfo info = manager.getActiveNetworkInfo();

        if (null == info)
            return false;

        if (!info.isAvailable()) {
            return false;
        }

        if (!info.isConnected()) {
            return false;
        }

        return true;
    }
}
