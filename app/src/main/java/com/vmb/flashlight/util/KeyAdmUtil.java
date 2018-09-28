package com.vmb.flashlight.util;

import android.content.Context;
import android.text.TextUtils;

import com.vmb.flashlight.Config;

public class KeyAdmUtil {

    public static String getBan(Context context) {
        if(context == null)
            return "";
        String k = SharedPreferencesUtil.getPrefferString(context, "ban", "");
        if (TextUtils.isEmpty(k)) {
            k = Config.AdsID.ID_BANNER_ADMOB_UNIT;
        }
        return k;
    }

    public static String getInter(Context context) {
        if(context == null)
            return "";
        String k = SharedPreferencesUtil.getPrefferString(context, "inter", "");
        if (TextUtils.isEmpty(k)) {
            k = Config.AdsID.ID_POPUP_ADMOB_UNIT;
        }
        return k;
    }

    public static String getBan_f(Context context) {
        if(context == null)
            return "";
        String k = SharedPreferencesUtil.getPrefferString(context, "ban_f", "");
        if (TextUtils.isEmpty(k)) {
            k = Config.AdsID.ID_BANNER_FB_UNIT;
        }
        return k;
    }

    public static String getInter_f(Context context) {
        if(context == null)
            return "";
        String k = SharedPreferencesUtil.getPrefferString(context, "inter_f", "");
        if (TextUtils.isEmpty(k)) {
            k = Config.AdsID.ID_POPUP_FB_UNIT;
        }
        return k;
    }
}
