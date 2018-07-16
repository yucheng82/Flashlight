package com.vmb.flashlight.util;

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
import com.vmb.flashlight.MainApplication;
import com.vmb.flashlight.model.Ads;

public class AdUtil {
    private static AdUtil adsUtil;
    private static InterstitialAd interstitialAd;

    private String TAG_POPUP = "AdInterstitial";
    private int countLoadFailPopup = 0;

    public static AdUtil getIntance() {
        synchronized (AdUtil.class) {
            if (adsUtil == null) {
                adsUtil = new AdUtil();
            }
            return adsUtil;
        }
    }

    public static void showAdBanner(Context context, final RelativeLayout banner, final FrameLayout layout_ads) {
        final String TAG_BANNER = "AdBanner";
        final int[] countLoadFailBanner = {0};

        final AdView adView = new AdView(context);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(Config.ID_BANNER_AD_UNIT);
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

                countLoadFailBanner[0]++;
                Log.i(TAG_BANNER, "countLoadFail = " + countLoadFailBanner[0]);
                if (countLoadFailBanner[0] <= 10) {
                    // Create ad request.
                    AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
                    // Begin loading your banner.
                    adView.loadAd(adRequest);
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

        // load advertisement
        AdRequest adRequestFull = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.loadAd(adRequestFull);
        banner.addView(adView);
    }

    public InterstitialAd initInterstitialAd(Context context) {
        synchronized (InterstitialAd.class) {
            if (interstitialAd == null) {
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
                            // Create ad request.
                            AdRequest adRequestFull = new AdRequest.Builder().build();
                            // Begin loading your interstitial.
                            interstitialAd.loadAd(adRequestFull);
                        } else {
                            Log.d(TAG_POPUP, "countLoadFailPopup > 10");
                        }
                    }

                    @Override
                    public void onAdOpened() {
                        super.onAdOpened();
                        Log.d(TAG_POPUP, "onAdOpened()");

                        countLoadFailPopup = 1;
                        // Create ad request.
                        AdRequest adRequestFull = new AdRequest.Builder().build();
                        // Begin loading your interstitial.
                        interstitialAd.loadAd(adRequestFull);
                    }
                });

                if(Ads.getInstance() == null)
                    return interstitialAd;

                if(Ads.getInstance().getAdmob() == null)
                    return interstitialAd;

                String popupId = Ads.getInstance().getAdmob().getPopup();
                if(TextUtils.isEmpty(popupId))
                    popupId = Config.ID_POPUP_AD_UNIT;

                AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
                interstitialAd.setAdUnitId(popupId);
                interstitialAd.loadAd(adRequest);

            } else {

            }

            return interstitialAd;
        }
    }

    public void displayInterstitial() {
        String TAG = "AdUtil";
        if (interstitialAd == null)
            return;

        boolean check = MainApplication.getInstance().isCanShowPopup();
        Log.d(TAG, "check = " + check);

        if(!check)
            return;

        interstitialAd.show();
        MainApplication.getInstance().restartCountDown();
        Log.d(TAG, "displayInterstitial()");
    }
}
