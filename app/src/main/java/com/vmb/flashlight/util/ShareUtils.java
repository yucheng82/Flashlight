package com.vmb.flashlight.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.vmb.flashlight.Config;

/**
 * Created by jacky on 11/21/17.
 */

public class ShareUtils {

    public static void shareApp(Activity activity) {
        try {
            String appPackageName = activity.getPackageName();
            String shareBody = "https://play.google.com/store/apps/details?id=" + appPackageName;
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            activity.startActivityForResult(Intent.createChooser(sharingIntent, "Share App"),
                    Config.RequestCode.SHARE_APP);
        } catch (Exception e) {
        }
    }

    public static void rateApp(Activity activity) {
        try {
            String appPackageName = activity.getPackageName();
            try {
                activity.startActivityForResult(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + appPackageName)), Config.RequestCode.RATE_APP);
            } catch (Exception e) {
                activity.startActivityForResult(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)), Config.RequestCode.RATE_APP);
            }
        } catch (Exception e) {
        }
    }
}