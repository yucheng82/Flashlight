package com.vmb.flashlight.util;

import android.os.CountDownTimer;
import android.util.Log;

import com.vmb.flashlight.model.Ads;

import java.util.Calendar;

public class AdsUtil {
    private static AdsUtil adsUtils;

    private CountDownTimer countDownTimer;
    private Calendar timeStart = Calendar.getInstance();

    private boolean isInitGetAds = false;
    private boolean canShowPopup = true;
    private boolean isShowPopupFirstTime = false;
    private boolean isShowPopupCloseApp = false;

    public static AdsUtil getInstance() {
        synchronized (AdsUtil.class) {
            if (adsUtils == null) {
                adsUtils = new AdsUtil();
            }
            return adsUtils;
        }
    }

    public void setInstance(AdsUtil adsUtils) {
        this.adsUtils = adsUtils;
    }

    public void displayInterstitial() {
        if (Ads.getInstance().getAds_network().equals("admob")) {
            AdmobUtil.getInstance().displayInterstitial();
        } else {
            FBAdsUtil.getInstance().displayInterstitial();
        }
    }

    public boolean isInitGetAds() {
        return isInitGetAds;
    }

    public void setInitGetAds(boolean initGetAds) {
        isInitGetAds = initGetAds;
    }

    public Calendar getTimeStart() {
        return timeStart;
    }

    public boolean isCanShowPopup() {
        return canShowPopup;
    }

    public boolean isShowPopupFirstTime() {
        return isShowPopupFirstTime;
    }

    public void setShowPopupFirstTime(boolean showPopupFirstTime) {
        isShowPopupFirstTime = showPopupFirstTime;
    }

    public boolean isShowPopupCloseApp() {
        return isShowPopupCloseApp;
    }

    public void setShowPopupCloseApp(boolean showPopupCloseApp) {
        isShowPopupCloseApp = showPopupCloseApp;
    }

    public void initCountDown() {
        String TAG = "initCountDown";

        if (Ads.getInstance() == null || Ads.getInstance().getConfig() == null) {
            Log.d(TAG, "Ads.getInstance() null || Ads.getInstance().getConfig() null");
            return;
        }

        int offset_time_show_popup = Ads.getInstance().getConfig().getOffset_time_show_popup();
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

        canShowPopup = true;
        countDownTimer.cancel();
    }
}
