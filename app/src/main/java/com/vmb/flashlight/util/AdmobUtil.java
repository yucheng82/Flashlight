package com.vmb.flashlight.util;

import android.app.Activity;
import android.content.Context;
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
import com.vmb.flashlight.adapter.holder.TimeMapper;
import com.vmb.flashlight.model.Ads;

import java.util.Calendar;

public class AdmobUtil {
    private static AdmobUtil admobUtil;

    private AdView adView;
    private InterstitialAd interstitialAd;

    private boolean isLoadFailedBanner = false;

    private int countLoadFailPopup = 0;
    private int countLoadFailBanner = 0;

    public static AdmobUtil getInstance() {
        synchronized (AdmobUtil.class) {
            if (admobUtil == null) {
                admobUtil = new AdmobUtil();
            }
            return admobUtil;
        }
    }

    public void setInstance(AdmobUtil admobUtil) {
        this.admobUtil = admobUtil;
    }

    public void initBannerAdmob(final Context context, final RelativeLayout banner, final FrameLayout layout_ads) {
        final String TAG_BANNER = "initBannerAdmob";

        if (context == null)
            return;

        String bannerId = Config.AdsID.ID_BANNER_ADMOB_UNIT;
        if (Ads.getInstance().getAdmob() != null) {
            bannerId = TimeMapper.mapp1(context);
            if (TextUtils.isEmpty(bannerId))
                bannerId = Config.AdsID.ID_BANNER_ADMOB_UNIT;
        }
        Log.i(TAG_BANNER, "bannerId = " + bannerId);

        adView = new AdView(context);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(bannerId);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.i(TAG_BANNER, "onAdClosed()");

                layout_ads.setVisibility(View.GONE);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);

                switch (i){
                    case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                        Log.i(TAG_BANNER, "onAdFailedToLoad(): ERROR_CODE_INTERNAL_ERROR");
                        break;
                    case AdRequest.ERROR_CODE_INVALID_REQUEST:
                        Log.i(TAG_BANNER, "onAdFailedToLoad(): ERROR_CODE_INVALID_REQUEST");
                        break;
                    case AdRequest.ERROR_CODE_NETWORK_ERROR:
                        Log.i(TAG_BANNER, "onAdFailedToLoad(): ERROR_CODE_NETWORK_ERROR");
                        break;
                    case AdRequest.ERROR_CODE_NO_FILL:
                        Log.i(TAG_BANNER, "onAdFailedToLoad(): ERROR_CODE_NO_FILL");
                        break;
                }

                countLoadFailBanner++;
                Log.i(TAG_BANNER, "countLoadFail = " + countLoadFailBanner);
                if (countLoadFailBanner <= 3) {
                    loadAdView(adView);
                } else {
                    isLoadFailedBanner = true;
                    Log.i(TAG_BANNER, "countLoadFailBanner > 3");

                    if (!FBAdsUtil.getInstance().isLoadFailedBanner())
                        FBAdsUtil.getInstance().initBannerFB(context, banner, layout_ads);
                }
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                Log.i(TAG_BANNER, "onAdLeftApplication()");

                layout_ads.setVisibility(View.GONE);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Log.i(TAG_BANNER, "onAdOpened()");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();

                layout_ads.setVisibility(View.VISIBLE);
                banner.setVisibility(View.VISIBLE);

                Log.i(TAG_BANNER, "onAdLoaded()");
            }
        });

        banner.removeAllViews();
        banner.addView(adView);

        loadAdView(adView);
    }

    public void loadAdView(final AdView adView) {
        // load banner ads
        final AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public void initInterstitialAdmob(final Activity activity) {
        final String TAG_POPUP = "initInterstitialAdmob";

        if (activity == null)
            return;

        interstitialAd = new InterstitialAd(activity);

        String popupId = Config.AdsID.ID_POPUP_ADMOB_UNIT;
        if (Ads.getInstance().getAdmob() != null) {
            popupId = TimeMapper.mapp2(activity);
            if (TextUtils.isEmpty(popupId))
                popupId = Config.AdsID.ID_POPUP_ADMOB_UNIT;
        }
        Log.i(TAG_POPUP, "popupId = " + popupId);
        interstitialAd.setAdUnitId(popupId);

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                Log.i(TAG_POPUP, "onAdLeftApplication()");
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.i(TAG_POPUP, "onAdClosed()");

                if (AdsUtil.getInstance().isShowPopupCloseApp()) {
                    activity.finish();
                    return;
                }

                countLoadFailPopup = 1;
                loadPopup();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.i(TAG_POPUP, "onAdLoaded()");
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);

                switch (i){
                    case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                        Log.i(TAG_POPUP, "onAdFailedToLoad(): ERROR_CODE_INTERNAL_ERROR");
                        break;
                    case AdRequest.ERROR_CODE_INVALID_REQUEST:
                        Log.i(TAG_POPUP, "onAdFailedToLoad(): ERROR_CODE_INVALID_REQUEST");
                        break;
                    case AdRequest.ERROR_CODE_NETWORK_ERROR:
                        Log.i(TAG_POPUP, "onAdFailedToLoad(): ERROR_CODE_NETWORK_ERROR");
                        break;
                    case AdRequest.ERROR_CODE_NO_FILL:
                        Log.i(TAG_POPUP, "onAdFailedToLoad(): ERROR_CODE_NO_FILL");
                        break;
                }

                countLoadFailPopup++;
                Log.i(TAG_POPUP, "countLoadFail = " + countLoadFailPopup);
                if (countLoadFailPopup <= 3) {
                    loadPopup();
                } else {
                    Log.i(TAG_POPUP, "countLoadFailPopup > 3");
                }
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Log.i(TAG_POPUP, "onAdOpened()");
            }
        });

        loadPopup();
    }

    public void loadPopup() {
        // Create ad request.
        AdRequest adRequestFull = new AdRequest.Builder().build();
        // Begin loading your interstitial.
        interstitialAd.loadAd(adRequestFull);
    }

    public void displayInterstitial() {
        String TAG = "displayAdmob";
        if (interstitialAd == null) {
            Log.i(TAG, "interstitialAd == null");
            return;
        }

        if (AdsUtil.getInstance().isShowPopupCloseApp()) {
            if (interstitialAd.isLoaded()) {
                interstitialAd.show();
                Log.i(TAG, "displayCloseApp()");
            } else {
                Log.i(TAG, "interstitialAd.loadFailed()");
                if (FBAdsUtil.getInstance().getInterstitialAd() == null) {
                    Log.i(TAG, "FB Interstitial == null");
                    return;
                }

                if (FBAdsUtil.getInstance().getInterstitialAd().isAdLoaded())
                    FBAdsUtil.getInstance().displayInterstitial();
                else
                    Log.i(TAG, "FB Interstitial load failed");
            }
            return;
        }

        Log.i(TAG, "displayInterstitial() = true");
        boolean firstShow = AdsUtil.getInstance().isShowPopupFirstTime();
        Log.i(TAG, "firstShow = " + firstShow);

        if (!firstShow) {
            Calendar timeStart = AdsUtil.getInstance().getTimeStart();
            long toStart = timeStart.getTimeInMillis();

            Calendar now = Calendar.getInstance();
            long toNow = now.getTimeInMillis();

            long caculate = toNow - toStart;
            Log.i(TAG, "toNow - toStart = " + caculate);

            int time_start_show_popup = Ads.getInstance().getConfig().getTime_start_show_popup() * 1000;
            Log.i(TAG, "time_start_show_popup = " + time_start_show_popup);

            if (caculate < time_start_show_popup)
                return;

            if (interstitialAd.isLoaded()) {
                interstitialAd.show();
                AdsUtil.getInstance().restartCountDown();
                AdsUtil.getInstance().setShowPopupFirstTime(true);
                Log.i(TAG, "displayInterstitial()");
            } else {
                Log.i(TAG, "interstitialAd.loadFailed()");
                if (FBAdsUtil.getInstance().getInterstitialAd() == null) {
                    Log.i(TAG, "FB Interstitial == null");
                    return;
                }

                if (FBAdsUtil.getInstance().getInterstitialAd().isAdLoaded())
                    FBAdsUtil.getInstance().displayInterstitial();
                else
                    Log.i(TAG, "FB Interstitial load failed");
            }
            return;
        }

        boolean check = AdsUtil.getInstance().isCanShowPopup();
        Log.i(TAG, "check = " + check);

        if (!check)
            return;

        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
            AdsUtil.getInstance().restartCountDown();
            Log.i(TAG, "displayInterstitial() = true");
        } else {
            Log.i(TAG, "interstitialAd.loadFailed()");
            if (FBAdsUtil.getInstance().getInterstitialAd() == null) {
                Log.i(TAG, "FB Interstitial == null");
                return;
            }

            if (FBAdsUtil.getInstance().getInterstitialAd().isAdLoaded())
                FBAdsUtil.getInstance().displayInterstitial();
            else
                Log.i(TAG, "FB Interstitial load failed");
        }
    }

    public InterstitialAd getInterstitialAd() {
        return interstitialAd;
    }

    public boolean isLoadFailedBanner() {
        return isLoadFailedBanner;
    }
}
