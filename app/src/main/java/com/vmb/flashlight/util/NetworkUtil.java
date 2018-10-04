package com.vmb.flashlight.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

    public static boolean isNetworkAvailable(Context context) {
        if (context == null)
            return false;

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null)
            return false;

        NetworkInfo info = manager.getActiveNetworkInfo();

        if (info == null)
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
