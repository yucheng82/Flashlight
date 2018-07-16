package com.noname.quangcaoads;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.noname.quangcaoads.util.DeviceUtil;
import com.noname.quangcaoads.util.FileCacheUtil;
import com.noname.quangcaoads.util.SharedPreferencesGlobalUtil;

import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QuangCaoSetup {

    private static QuangCaoSetup quangCaoSetup;
    private Context context;
    private OkHttpClient client;
    private QuangCaoSetupCallback callback;

    public static void setConfig(Context context, String code, String version) {
        SharedPreferencesGlobalUtil.setValue(context, "code", code);
        SharedPreferencesGlobalUtil.setValue(context, "version", version);
    }

    public static void initiate(Activity activity) {
        try {
            quangCaoSetup = new QuangCaoSetup();
            quangCaoSetup.setup(activity, null);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 12321);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initiate(Context context, QuangCaoSetupCallback callback) {
        quangCaoSetup = new QuangCaoSetup();
        quangCaoSetup.setup(context, callback);
    }

    public static void setServiceEnable(Context context, boolean isEnable) {
        SharedPreferencesGlobalUtil.setValue(context, "isServiceEnable", String.valueOf(isEnable));
    }

    public void setup(final Context context, final QuangCaoSetupCallback callback) {
        String timeRegister = SharedPreferencesGlobalUtil.getValue(context, "time_register");
        if (TextUtils.isEmpty(timeRegister)) {
            SharedPreferencesGlobalUtil.setValue(context, "time_register",
                    String.valueOf(System.currentTimeMillis() / 1000));
        }

        this.context = context;
        this.callback = callback;

        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        new Thread() {
            @Override
            public void run() {
                try {
                    getCountryOnline();
                    getConfig(context);
                    if (QuangCaoSetup.this.callback != null)
                        QuangCaoSetup.this.callback.onSuccess();
                } catch (Exception e) {
                }
            }
        }.start();
    }

    private void getCountryOnline() {
        String country = SharedPreferencesGlobalUtil.getValue(context, "country");
        if (TextUtils.isEmpty(country)) {
            try {


                Request request = new Request.Builder()
                        .url("https://ipinfo.io/json").get().build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                JSONObject jsonObject = new JSONObject(result);
                country = jsonObject.getString("country").toUpperCase();
            } catch (Exception e) {
                country = Locale.getDefault().toString().toUpperCase();
            }
            SharedPreferencesGlobalUtil.setValue(context, "country", country);
        }
    }

    private String getCountryOffline() {
        String country = null;
        try {
            country = SharedPreferencesGlobalUtil.getValue(context, "country");
        } catch (Exception e) {
        }
        if (TextUtils.isEmpty(country)) {
            country = "VN";
        }
        return country;
    }

    private void getConfig(Context context) {
        try {
            String code = SharedPreferencesGlobalUtil.getValue(context, "code");
            if (TextUtils.isEmpty(code))
                code = "78974";
            String version = SharedPreferencesGlobalUtil.getValue(context, "version");
            if (TextUtils.isEmpty(version))
                version = "1.0";
            String timeRegister = SharedPreferencesGlobalUtil.getValue(context, "time_register");
            if (TextUtils.isEmpty(timeRegister))
                timeRegister = String.valueOf(System.currentTimeMillis() / 1000);
            String domain = "http://gamemobileglobal.com/api/control_s.php";
            String url = domain + "?code=_code_&version=_version_&deviceID=_deviceID_&country=_country_&timereg=_timereg_&timenow=_timenow_&mobileNetwork=_mobileNetwork_";
            url = url.replace("_code_", code);
            url = url.replace("_version_", version);
            url = url.replace("_timereg_", timeRegister);
            url = url.replace("_timenow_", String.valueOf(System.currentTimeMillis() / 1000));
            url = url.replace("_deviceID_", DeviceUtil.getDeviceId(context));
            url = url.replace("_mobileNetwork_", DeviceUtil.getTelcoName(context));
            url = url.replace("_country_", getCountryOffline());
            Request request = new Request.Builder().url(url).get().build();
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.length() > 0) {
                FileCacheUtil.saveConfig(context, result);
            }
        } catch (Exception e) {
//            Gson gson = new Gson();
//            try {
//                String strConfig = FileCacheUtil.loadConfig();
//                ControlAds controlAds = gson.fromJson(strConfig, ControlAds.class);
//                controlAds.getActive_ads().getPopup();
//                return;
//            } catch (Exception e1) {
//            }
//            ControlAds controlAds = new ControlAds();
//            ConfigShow config_show = new ConfigShow();
//            config_show.setOffset_show_banner(518500);
//            config_show.setOffset_show_popup(518500);
//            config_show.setTime_start_banner(518500);
//            config_show.setTime_start_popup(518500);
//            controlAds.setConfig_show(config_show);
        }
    }

    public interface QuangCaoSetupCallback {
        void onSuccess();
    }

}
