package com.vmb.flashlight.util;

import android.text.TextUtils;
import android.util.Log;

import com.vmb.flashlight.Interface.IGetCountry;

import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class CountryCodeUtil {

    public static void getCountryCode(final IGetCountry iGetCountry) {
        final String TAG = "CountryCodeUtil";
        if(iGetCountry == null)
            return;

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
                    country = Locale.getDefault().getCountry();

                if (country.contains("_")) {
                    String[] words = country.split("_");
                    if (words.length > 1)
                        country = words[1];
                }

                Log.d(TAG, "country = " + country);
                iGetCountry.onGetCountry(country);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
