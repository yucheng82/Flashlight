package com.vmb.flashlight.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class CountryCodeUtil {

    public static void setCountryCode(final Context context) {
        final String TAG = "setCountryCode()";
        if(context == null)
            return;

        String country_code = getCountryCode(context);

        if (TextUtils.isEmpty(country_code)) {
            final OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();

            new Thread() {
                @Override
                public void run() {
                    try {
                        Request request = new Request.Builder()
                                .url("https://ipinfo.io/json").get().build();

                        okhttp3.Response response = client.newCall(request).execute();
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        String country = jsonObject.getString("country").toUpperCase();

                        if (TextUtils.isEmpty(country))
                            country = Locale.getDefault().getCountry().toUpperCase();

                        if (country.contains("_")) {
                            String[] words = country.split("_");
                            if (words.length > 1)
                                country = words[1];
                        }

                        SharedPreferencesUtil.putPrefferString(context, "country_code", country);
                        Log.i(TAG, "country = " + country);

                    } catch (Exception e) {
                        Log.i(TAG, "catch: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    public static String getCountryCode(Context context) {
        if(context == null)
            return "";

        String country_code = SharedPreferencesUtil.getPrefferString(context, "country_code", "");
        return country_code;
    }
}
