package com.vmb.flashlight.util;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
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

    public void initBannerAdmob(final Context context, final RelativeLayout banner, final FrameLayout layout_ads) {
        final String TAG_BANNER = "initBannerAdmob";

        if (context == null)
            return;

        String bannerId = Config.ID_BANNER_ADMOB_UNIT;
        if (Ads.getInstance().getAdmob() != null) {
            bannerId = Ads.getInstance().getAdmob().getBanner();
            if (TextUtils.isEmpty(bannerId))
                bannerId = Config.ID_BANNER_ADMOB_UNIT;
        }
        Log.i(TAG_BANNER, "bannerId = " + bannerId);

        adView = new AdView(context);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(bannerId);

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
                if (countLoadFailBanner <= 3) {
                    loadAdView(adView);
                } else {
                    isLoadFailedBanner = true;
                    Log.d(TAG_BANNER, "countLoadFailBanner > 3");

                    if (!FBAdsUtil.getInstance().isLoadFailedBanner())
                        FBAdsUtil.getInstance().initBannerFB(context, banner, layout_ads);
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

                layout_ads.setVisibility(View.VISIBLE);
                banner.setVisibility(View.VISIBLE);

                Log.d(TAG_BANNER, "onAdLoaded()");
            }
        });

        banner.removeAllViews();
        banner.addView(adView);

        loadAdView(adView);
    }

    public void loadAdView(final AdView adView) {
        // load banner ads
        final AdRequest adRequest = new AdRequest.Builder().build();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        adView.loadAd(adRequest);
                    }
                }).run();
            }
        }, 1000);
    }

    public void initInterstitialAdmob(final Activity activity) {
        final String TAG_POPUP = "initInterstitialAdmob";

        if (activity == null)
            return;

        interstitialAd = new InterstitialAd(activity);

        String popupId = Config.ID_POPUP_ADMOB_UNIT;
        if (Ads.getInstance().getAdmob() != null) {
            popupId = Ads.getInstance().getAdmob().getPopup();
            if (TextUtils.isEmpty(popupId))
                popupId = Config.ID_POPUP_ADMOB_UNIT;
        }
        Log.i(TAG_POPUP, "popupId = " + popupId);
        interstitialAd.setAdUnitId(popupId);

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
                Log.d(TAG_POPUP, "onAdLoaded()");
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.d(TAG_POPUP, "onAdFailedToLoad()");

                countLoadFailPopup++;
                Log.i(TAG_POPUP, "countLoadFail = " + countLoadFailPopup);
                if (countLoadFailPopup <= 3) {
                    loadPopup();
                } else {
                    Log.d(TAG_POPUP, "countLoadFailPopup > 3");
                }
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Log.d(TAG_POPUP, "onAdOpened()");
            }
        });

        loadPopup();
    }

    public void loadPopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Create ad request.
                        AdRequest adRequestFull = new AdRequest.Builder().build();
                        // Begin loading your interstitial.
                        interstitialAd.loadAd(adRequestFull);
                    }
                }).run();
            }
        }, 1000);
    }

    public void displayInterstitial() {
        String TAG = "displayAdmob";
        if (interstitialAd == null) {
            Log.d(TAG, "interstitialAd == null");
            return;
        }

        if (AdsUtil.getInstance().isShowPopupCloseApp()) {
            if (interstitialAd.isLoaded()) {
                interstitialAd.show();
                Log.d(TAG, "displayCloseApp()");
            } else {
                Log.d(TAG, "interstitialAd.loadFailed()");
                if (FBAdsUtil.getInstance().getInterstitialAd() == null) {
                    Log.d(TAG, "FB Interstitial == null");
                    return;
                }

                if (FBAdsUtil.getInstance().getInterstitialAd().isAdLoaded())
                    FBAdsUtil.getInstance().displayInterstitial();
                else
                    Log.d(TAG, "FB Interstitial load failed");
            }
            return;
        }

        Log.d(TAG, "displayInterstitial() = true");
        boolean firstShow = AdsUtil.getInstance().isShowPopupFirstTime();
        Log.d(TAG, "firstShow = " + firstShow);

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
                Log.d(TAG, "displayInterstitial()");
            } else {
                Log.d(TAG, "interstitialAd.loadFailed()");
                if (FBAdsUtil.getInstance().getInterstitialAd() == null) {
                    Log.d(TAG, "FB Interstitial == null");
                    return;
                }

                if (FBAdsUtil.getInstance().getInterstitialAd().isAdLoaded())
                    FBAdsUtil.getInstance().displayInterstitial();
                else
                    Log.d(TAG, "FB Interstitial load failed");
            }
            return;
        }

        boolean check = AdsUtil.getInstance().isCanShowPopup();
        Log.d(TAG, "check = " + check);

        if (!check)
            return;

        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
            AdsUtil.getInstance().restartCountDown();
            Log.d(TAG, "displayInterstitial() = true");
        } else {
            Log.d(TAG, "interstitialAd.loadFailed()");
            if (FBAdsUtil.getInstance().getInterstitialAd() == null) {
                Log.d(TAG, "FB Interstitial == null");
                return;
            }

            if (FBAdsUtil.getInstance().getInterstitialAd().isAdLoaded())
                FBAdsUtil.getInstance().displayInterstitial();
            else
                Log.d(TAG, "FB Interstitial load failed");
        }
    }

    public InterstitialAd getInterstitialAd() {
        return interstitialAd;
    }

    public boolean isLoadFailedBanner() {
        return isLoadFailedBanner;
    }
}
