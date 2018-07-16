package com.noname.quangcaoads;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.noname.quangcaoads.util.SharedPreferencesGlobalUtil;

public class UserPresentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isServiceEnable = true;
        try {
            String str = SharedPreferencesGlobalUtil.getValue(context, "isServiceEnable");
            if (!TextUtils.isEmpty(str)) {
                isServiceEnable = Boolean.valueOf(str);
            }
        } catch (Exception e) {
        }
        if (!isServiceEnable) {
            return;
        }

        if (!isMyServiceRunning(context, AppCheckServices.class.getName())) {
            context.startService(new Intent(context, AppCheckServices.class));
        } else {
            Intent mIntent = new Intent("XXX.AdsReceiver");
            context.sendBroadcast(mIntent);
        }
    }

    public static boolean isMyServiceRunning(Context context, String serviceName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName()) &&
                    context.getApplicationContext().getPackageName().equals(service.service.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}