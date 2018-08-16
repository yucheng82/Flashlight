package com.vmb.flashlight.util;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SharedPreferencesUtil {

    public static boolean getPrefferBool(Context context, String key, boolean value) {
        if(context == null)
            return false;

        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, value);
    }

    public static void putPrefferBool(Context context, String key, boolean value) {
        if(context == null)
            return;

        Editor sharedata = PreferenceManager.getDefaultSharedPreferences(context).edit();
        sharedata.putBoolean(key, value);
        sharedata.commit();
    }

    public static String getPrefferString(Context context, String key, String value) {
        if(context == null)
            return "";

        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, value);
    }

    public static void putPrefferString(Context context, String key, String value) {
        if(context == null)
            return;

        Editor sharedata = PreferenceManager.getDefaultSharedPreferences(context).edit();
        sharedata.putString(key, value);
        sharedata.commit();
    }
}
