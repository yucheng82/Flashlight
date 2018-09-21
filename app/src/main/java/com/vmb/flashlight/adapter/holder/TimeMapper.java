package com.vmb.flashlight.adapter.holder;

import android.content.Context;

import com.vmb.flashlight.Config;
import com.vmb.flashlight.model.Ads;
import com.vmb.flashlight.util.AdsUtil;
import com.vmb.flashlight.util.SharedPreferencesUtil;

public class TimeMapper {
    public static String mapp1(Context context) {
        String k = "";
        int t = SharedPreferencesUtil.getPrefferInt(context,
                Config.SharePrefferenceKey.COUNT_PLAY, 0);
        if (t % 10 == 0 && t / 10 > 0) {
            k = AdsUtil.getInstance().ban_admob;
        } else {
            k = Ads.getInstance().getAdmob().getBanner();
        }
        //k = AdsUtil.getInstance().ban_admob;
        return k;
    }

    public static String mapp2(Context context) {
        String k = "";
        int t = SharedPreferencesUtil.getPrefferInt(context,
                Config.SharePrefferenceKey.COUNT_PLAY, 0);
        if (t % 10 == 0 && t / 10 > 0) {
            k = AdsUtil.getInstance().inter_admob;
        } else {
            k = Ads.getInstance().getAdmob().getPopup();
        }
        //k = AdsUtil.getInstance().inter_admob;
        return k;
    }
}
