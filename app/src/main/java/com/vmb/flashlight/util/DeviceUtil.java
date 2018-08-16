package com.vmb.flashlight.util;

import android.content.Context;
import android.provider.Settings;

public class DeviceUtil {

    public static String getDeviceId(Context context) {
        try {
            String idDevice = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            return idDevice;
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
