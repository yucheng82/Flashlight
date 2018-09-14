package com.vmb.flashlight.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.vmb.flashlight.Config;
import com.vmb.flashlight.model.Ads;

import java.util.Calendar;

public class FBAdsUtil {
    private static FBAdsUtil fbAdsUtils;
    private InterstitialAd interstitialAd;

    private boolean isLoadFailedBanner = false;

    private int countLoadFailPopup = 0;
    private int countLoadFailBanner = 0;

    public static FBAdsUtil getInstance() {
        synchronized (FBAdsUtil.class) {
            if (fbAdsUtils == null) {
                fbAdsUtils = new FBAdsUtil();
            }
            return fbAdsUtils;
        }
    }

    public void initBannerFB(final Context context, final RelativeLayout banner, final FrameLayout layout_ads) {
        final String TAG_BANNER = "initBannerFB";

        if (context == null)
            return;

        String bannerId = Config.AdsID.ID_BANNER_FB_UNIT;
        if (Ads.getInstance().getFacebook() != null) {
            bannerId = Ads.getInstance().getFacebook().getBanner();
            if (TextUtils.isEmpty(bannerId))
                bannerId = Config.AdsID.ID_BANNER_FB_UNIT;
        }
        Log.i(TAG_BANNER, "bannerId = " + bannerId);

        final AdView adView = new AdView(context, bannerId, AdSize.BANNER_HEIGHT_50);
        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                Log.i(TAG_BANNER, "onError(): " + adError.getErrorMessage());

                countLoadFailBanner++;
                Log.i(TAG_BANNER, "countLoadFail = " + countLoadFailBanner);
                if (countLoadFailBanner <= 3) {
                    loadAdView(adView);
                } else {
                    isLoadFailedBanner = true;
                    Log.i(TAG_BANNER, "countLoadFailBanner > 3");

                    if (!AdmobUtil.getInstance().isLoadFailedBanner())
                        AdmobUtil.getInstance().initBannerAdmob(context, banner, layout_ads);
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                layout_ads.setVisibility(View.VISIBLE);
                banner.setVisibility(View.VISIBLE);

                Log.i(TAG_BANNER, "onAdLoaded()");
            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.i(TAG_BANNER, "onAdClicked()");
                layout_ads.setVisibility(View.GONE);
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                Log.i(TAG_BANNER, "onLoggingImpression()");
            }
        });

        banner.removeAllViews();
        banner.addView(adView);

        AdSetting.addTestDevice();
        loadAdView(adView);
    }

    public void loadAdView(final AdView adView) {
        adView.loadAd();
    }

    public void initInterstitialFB(final Activity activity) {
        final String TAG_POPUP = "initInterstitialFB";

        if (activity == null)
            return;

        String popupId = Config.AdsID.ID_POPUP_FB_UNIT;
        if (Ads.getInstance().getFacebook() != null) {
            popupId = Ads.getInstance().getFacebook().getPopup();
            if (TextUtils.isEmpty(popupId))
                popupId = Config.AdsID.ID_POPUP_FB_UNIT;
        }
        Log.i(TAG_POPUP, "popupId = " + popupId);

        interstitialAd = new InterstitialAd(activity, popupId);
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                Log.i(TAG_POPUP, "onInterstitialDisplayed()");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                Log.i(TAG_POPUP, "onInterstitialDismissed()");

                if (AdsUtil.getInstance().isShowPopupCloseApp()) {
                    activity.finish();
                    return;
                }

                countLoadFailPopup = 1;
                loadPopup();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.i(TAG_POPUP, "onAdLoaded()");
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.i(TAG_POPUP, "onAdClicked()");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                Log.i(TAG_POPUP, "onLoggingImpression()");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.i(TAG_POPUP, "onError(): " + adError.getErrorMessage());

                countLoadFailPopup++;
                Log.i(TAG_POPUP, "countLoadFail = " + countLoadFailPopup);
                if (countLoadFailPopup <= 3) {
                    loadPopup();
                } else {
                    Log.i(TAG_POPUP, "countLoadFailPopup > 3");
                }
            }
        });

        AdSetting.addTestDevice();
        loadPopup();
    }

    public void loadPopup() {
        // load interstitial ads
        interstitialAd.loadAd();
    }

    public void displayInterstitial() {
        String TAG = "displayFB";
        if (interstitialAd == null) {
            Log.i(TAG, "interstitialAd == null");
            return;
        }

        if (AdsUtil.getInstance().isShowPopupCloseApp()) {
            if (interstitialAd.isAdLoaded()) {
                interstitialAd.show();
                Log.i(TAG, "displayCloseApp()");
            } else {
                Log.i(TAG, "interstitialAd.loadFailed()");
                if (AdmobUtil.getInstance().getInterstitialAd() == null) {
                    Log.i(TAG, "Admob Interstitial == null");
                    return;
                }

                if (AdmobUtil.getInstance().getInterstitialAd().isLoaded())
                    AdmobUtil.getInstance().displayInterstitial();
                else
                    Log.i(TAG, "Admob Interstitial load failed");
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

            if (interstitialAd.isAdLoaded()) {
                interstitialAd.show();
                AdsUtil.getInstance().restartCountDown();
                AdsUtil.getInstance().setShowPopupFirstTime(true);
                Log.i(TAG, "displayInterstitial()");
            } else {
                Log.i(TAG, "interstitialAd.loadFailed()");
                if (AdmobUtil.getInstance().getInterstitialAd() == null) {
                    Log.i(TAG, "Admob Interstitial == null");
                    return;
                }

                if (AdmobUtil.getInstance().getInterstitialAd().isLoaded())
                    AdmobUtil.getInstance().displayInterstitial();
                else
                    Log.i(TAG, "Admob Interstitial load failed");
            }
            return;
        }

        boolean check = AdsUtil.getInstance().isCanShowPopup();
        Log.i(TAG, "check = " + check);

        if (!check)
            return;

        if (interstitialAd.isAdLoaded()) {
            interstitialAd.show();
            AdsUtil.getInstance().restartCountDown();
            Log.i(TAG, "displayInterstitial() = true");
        } else {
            Log.i(TAG, "interstitialAd.loadFailed()");
            if (AdmobUtil.getInstance().getInterstitialAd() == null) {
                Log.i(TAG, "Admob Interstitial == null");
                return;
            }

            if (AdmobUtil.getInstance().getInterstitialAd().isLoaded())
                AdmobUtil.getInstance().displayInterstitial();
            else
                Log.i(TAG, "Admob Interstitial load failed");
        }
    }

    public InterstitialAd getInterstitialAd() {
        return interstitialAd;
    }

    public boolean isLoadFailedBanner() {
        return isLoadFailedBanner;
    }
}
