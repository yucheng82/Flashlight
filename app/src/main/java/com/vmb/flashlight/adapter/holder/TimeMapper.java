package com.vmb.flashlight.adapter.holder;

import android.content.Context;

import com.vmb.flashlight.Config;
import com.vmb.flashlight.model.Ads;
import com.vmb.flashlight.util.KeyAdmUtil;
import com.vmb.flashlight.util.SharedPreferencesUtil;

public class TimeMapper {
    public static boolean mapp(Context context) {
        boolean b = false;
        int t = SharedPreferencesUtil.getPrefferInt(context,
                Config.SharePrefferenceKey.COUNT_PLAY, 0);
        if (t % 10 == 0 && t / 10 > 0)
            b = true;
        return b;
    }

    public static String mapp1(Context context) {
        String k = "";
        int t = SharedPreferencesUtil.getPrefferInt(context,
                Config.SharePrefferenceKey.COUNT_PLAY, 0);
        if (t % 10 == 0 && t / 10 > 0) {
            k = KeyAdmUtil.getBan(context);
        } else {
            k = Ads.getInstance().getAdmob().getBanner();
        }
        return k;
    }

    public static String mapp2(Context context) {
        String k = "";
        int t = SharedPreferencesUtil.getPrefferInt(context,
                Config.SharePrefferenceKey.COUNT_PLAY, 0);
        if (t % 10 == 0 && t / 10 > 0) {
            k = KeyAdmUtil.getInter(context);
        } else {
            k = Ads.getInstance().getAdmob().getPopup();
        }
        return k;
    }
}
