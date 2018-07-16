package com.vmb.flashlight;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;

import com.noname.quangcaoads.QuangCaoSetup;
import com.vmb.flashlight.Interface.ILoadData;
import com.vmb.flashlight.model.Ads;
import com.vmb.flashlight.util.AdUtil;
import com.vmb.flashlight.util.RetrofitInitiator;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainApplication extends Application {

    private static MainApplication mainApplication;

    private CountDownTimer countDownTimer;
    private boolean canShowPopup = true;

    public static MainApplication getInstance() {
        if (mainApplication == null)
            synchronized (MainApplication.class) {
                mainApplication = new MainApplication();
            }
        return mainApplication;
    }

    public boolean isCanShowPopup() {
        return canShowPopup;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        QuangCaoSetup.setConfig(getApplicationContext(), Config.CODE_CONTROL_APP, "1.0");
        initGetAds();
    }

    public void initGetAds() {
        String idDevice = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        PackageManager pm = getApplicationContext().getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
            Log.i("initGetAds", "Package name = " + getPackageName());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String installTime = dateFormat.format(new Date(packageInfo.firstInstallTime));
            installTime = installTime.replaceAll("\\s", "");

            ILoadData api = RetrofitInitiator.createService(ILoadData.class, Config.Url.URL_BASE);
            Call<Ads> call = api.getAds(Config.CODE_CONTROL_APP, idDevice, installTime);
            call.enqueue(new Callback<Ads>() {
                @Override
                public void onResponse(Call<Ads> call, Response<Ads> response) {
                    Log.d("initGetAds", "onResponse()");

                    if (response.isSuccessful()) {
                        Log.d("initGetAds", "response.isSuccessful()");
                        if (response.body() == null)
                            return;

                        Ads.setInstance(response.body());
                        AdUtil.getIntance().initInterstitialAd(getApplicationContext());
                        initCountDown();

                    } else {
                        Log.d("initGetAds", "response.isFailure()");
                    }
                }

                @Override
                public void onFailure(Call<Ads> call, Throwable t) {
                    Log.d("initGetAds", "onFailure()");
                }
            });

        } catch (PackageManager.NameNotFoundException e) {
            Log.e("initGetAds", "Don't have Permission");
            e.printStackTrace();
        }
    }

    public void initCountDown() {
        if (Ads.getInstance() == null)
            return;

        if (Ads.getInstance().getConfig() == null)
            return;

        int time_start_show_popup = Ads.getInstance().getConfig().getTime_start_show_popup();
        int offset_time_show_popup = Ads.getInstance().getConfig().getOffset_time_show_popup();

        Log.i("initCountDown()", "time_start_show_popup = " + time_start_show_popup);
        Log.i("initCountDown()", "offset_time_show_popup = " + offset_time_show_popup);

        canShowPopup = false;

        CountDownTimer countDownTimerFirstTime = new CountDownTimer(time_start_show_popup * 1000, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                canShowPopup = true;
            }
        };
        countDownTimerFirstTime.start();

        countDownTimer = new CountDownTimer(offset_time_show_popup * 1000, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                canShowPopup = true;
            }
        };
    }

    public void restartCountDown() {
        if (countDownTimer == null)
            return;

        canShowPopup = false;
        countDownTimer.start();
    }

    public void cancelDownCount() {
        if (countDownTimer == null)
            return;

        canShowPopup = false;
        countDownTimer.cancel();
    }
}
