package com.vmb.flashlight.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.noname.quangcaoads.util.DeviceUtil;
import com.vmb.flashlight.Config;

import flashlight.supper.flashlight.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;

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

    public static void shareFB(final Activity activity, CallbackManager callbackManager) {
        final String TAG = "ShareCallback";

        if (callbackManager == null) {
            ToastUtil.shortToast(activity, activity.getString(R.string.share_error));
            Log.i(TAG, "callbackManager == null");
        }

        ShareDialog shareDialog = new ShareDialog(activity);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                ToastUtil.shortToast(activity, activity.getString(R.string.share_success));
                Log.i(TAG, "onSuccess");

                final String deviceId = DeviceUtil.getDeviceId(activity);
                final String code = Config.CODE_CONTROL_APP;
                final String version = Config.VERSION_APP;
                final String packg = Config.PACKAGE_NAME;
                final String country_code = CountryCodeUtil.getCountryCode(activity);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String timeRegister = com.noname.quangcaoads.util.SharedPreferencesGlobalUtil.getValue(activity, "time_register");
                            if (TextUtils.isEmpty(timeRegister))
                                timeRegister = String.valueOf(System.currentTimeMillis() / 1000);

                            String url = "http://gamemobileglobal.com/api/log_share_app.php?"
                                    + "deviceID=" + deviceId
                                    + "&code=" + code
                                    + "&version=" + version
                                    + "&country=" + country_code
                                    + "&timereg=" + timeRegister
                                    + "&package=" + packg;

                            Log.i(TAG, "url_control.php = " + url);
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(url)
                                    .build();
                            client.newCall(request).execute();

                        } catch (Exception e) {
                            Log.i(TAG, "catch Exception");
                        }
                    }
                }).start();
            }

            @Override
            public void onCancel() {
                ToastUtil.shortToast(activity, activity.getString(R.string.share_cancel));
                Log.i(TAG, "onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                ToastUtil.shortToast(activity, activity.getString(R.string.share_error) + "\n" + error.getMessage());
                Log.i(TAG, "onError: " + error.getMessage());
            }
        }, Config.RequestCode.SHARE_FB);

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=" + Config.PACKAGE_NAME))
                    .setShareHashtag(new ShareHashtag.Builder()
                            .setHashtag(activity.getString(R.string.app_name) + " - Brightest Flashlight App")
                            .build())
                    .setQuote(activity.getString(R.string.app_name) + " - Brightest Flashlight App")
                    .build();
            shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
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