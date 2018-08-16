package com.vmb.flashlight.util;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.vmb.flashlight.Config;
import com.vmb.flashlight.model.Ads;

import java.util.Calendar;

public class AdUtil {

    private static AdUtil adsUtil;
    private static InterstitialAd interstitialAd;

    private int countLoadFailPopup = 0;
    private int countLoadFailBanner = 0;

    private CountDownTimer countDownTimer;
    private Calendar timeStart = Calendar.getInstance();

    private boolean isInitGetAds = false;
    private boolean canShowPopup = true;
    private boolean isShowPopupFirstTime = false;

    public static AdUtil getInstance() {
        synchronized (AdUtil.class) {
            if (adsUtil == null) {
                adsUtil = new AdUtil();
            }
            return adsUtil;
        }
    }

    public void showAdBanner(final Context context, final RelativeLayout banner, final FrameLayout layout_ads) {
        final String TAG_BANNER = "showAdBanner";

        if(context == null)
            return;

        final AdView adView = new AdView(context);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.d(TAG_BANNER, "onAdClosed()");

                layout_ads.setVisibility(View.GONE);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.d(TAG_BANNER, "onAdFailedToLoad()");

                countLoadFailBanner++;
                Log.i(TAG_BANNER, "countLoadFail = " + countLoadFailBanner);
                if (countLoadFailBanner <= 10) {
                    loadAdView(context, adView);
                } else {
                    Log.d(TAG_BANNER, "countLoadFailBanner > 10");
                }
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                Log.d(TAG_BANNER, "onAdLeftApplication()");

                layout_ads.setVisibility(View.GONE);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Log.d(TAG_BANNER, "onAdOpened()");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d(TAG_BANNER, "onAdLoaded()");

                layout_ads.setVisibility(View.VISIBLE);
            }
        });

        String bannerId = Config.ID_BANNER_AD_UNIT;
        if (Ads.getInstance().getAdmob() != null) {
            bannerId = Ads.getInstance().getAdmob().getBanner();
            if (TextUtils.isEmpty(bannerId))
                bannerId = Config.ID_BANNER_AD_UNIT;
        }
        Log.i(TAG_BANNER, "bannerId = " + bannerId);
        adView.setAdUnitId(bannerId);
        banner.addView(adView);
        loadAdView(context, adView);
    }

    public void loadAdView(Context context, AdView adView) {
        if(!NetworkUtil.isNetworkAvailable(context))
            return;

        // load banner ads
        AdRequest adRequestFull = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.loadAd(adRequestFull);
    }

    public void initInterstitialAd(final Context context) {
        final String TAG_POPUP = "initInterstitialAd";

        interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                Log.d(TAG_POPUP, "onAdLeftApplication()");
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.d(TAG_POPUP, "onAdClosed()");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d(TAG_POPUP, "onAdLoaded()");
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.d(TAG_POPUP, "onAdFailedToLoad()");

                countLoadFailPopup++;
                Log.i(TAG_POPUP, "countLoadFail = " + countLoadFailPopup);
                if (countLoadFailPopup <= 10) {
                    loadPopup(context);
                } else {
                    Log.d(TAG_POPUP, "countLoadFailPopup > 10");
                }
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Log.d(TAG_POPUP, "onAdOpened()");

                countLoadFailPopup = 1;
                loadPopup(context);
            }
        });

        String popupId = Config.ID_POPUP_AD_UNIT;
        if (Ads.getInstance().getAdmob() != null) {
            popupId = Ads.getInstance().getAdmob().getPopup();
            if (TextUtils.isEmpty(popupId))
                popupId = Config.ID_POPUP_AD_UNIT;
        }
        Log.i(TAG_POPUP, "popupId = " + popupId);
        interstitialAd.setAdUnitId(popupId);
        loadPopup(context);
    }

    public void loadPopup(Context context) {
        if(!NetworkUtil.isNetworkAvailable(context))
            return;

        // Create ad request.
        AdRequest adRequestFull = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        // Begin loading your interstitial.
        interstitialAd.loadAd(adRequestFull);
    }

    public void displayInterstitial() {
        String TAG = "displayInterstitial";
        if (interstitialAd == null)
            return;

        boolean firstShow = AdUtil.getInstance().isShowPopupFirstTime();
        Log.d(TAG, "firstShow = " + firstShow);

        if (!firstShow) {
            Calendar timeStart = AdUtil.getInstance().getTimeStart();
            long toStart = timeStart.getTimeInMillis();

            Calendar now = Calendar.getInstance();
            long toNow = now.getTimeInMillis();

            long caculate = toNow - toStart;
            Log.i(TAG, "toNow - toStart = " + caculate);

            int time_start_show_popup = Ads.getInstance().getConfig().getTime_start_show_popup();
            Log.i(TAG, "time_start_show_popup = " + time_start_show_popup);

            if (caculate < time_start_show_popup)
                return;

            interstitialAd.show();
            AdUtil.getInstance().restartCountDown();
            AdUtil.getInstance().setShowPopupFirstTime(true);
            Log.d(TAG, "displayInterstitial()");
            return;
        }

        boolean check = AdUtil.getInstance().isCanShowPopup();
        Log.d(TAG, "check = " + check);

        if (!check)
            return;

        interstitialAd.show();
        AdUtil.getInstance().restartCountDown();
        Log.d(TAG, "displayInterstitial() = true");
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
