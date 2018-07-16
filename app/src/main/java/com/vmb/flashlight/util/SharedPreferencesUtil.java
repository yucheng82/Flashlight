package com.vmb.flashlight.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesUtil {

    public static String getValue(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("game_pref_config", Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, null);
        return value;
    }

    public static void setValue(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("game_pref_config", Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        if (value != null) {
            editor.putString(key, value);
        } else {
            editor.remove(key);
        }
        editor.commit();
    }
}
