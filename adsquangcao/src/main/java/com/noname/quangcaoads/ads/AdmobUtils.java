package com.noname.quangcaoads.ads;

import android.content.Context;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class AdmobUtils {

    public static AdmobUtils newInstance(Context context) {
        AdmobUtils admobUtils = new AdmobUtils();
        admobUtils.context = context;
        return admobUtils;
    }

    private Context context;
    private InterstitialAd mInterstitialAd;
    private AdRequest mAdRequest;
    private AdmobUtilsListener mListener;
    private boolean showAfterLoad = false;
    private int countLoad;

    public interface AdmobUtilsListener {
        void onAdsClicked();
        void onAdsOpened();
        void onAdsClosed();
    }

    public void initiate(String adUnitId) {
        try {
            countLoad = 1;
            mInterstitialAd = new InterstitialAd(context);
            mInterstitialAd.setAdUnitId(adUnitId);
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    showAfterLoad = false;
                    if (mListener != null) {
                        mListener.onAdsClosed();
                    }
                    mInterstitialAd.loadAd(mAdRequest);
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    super.onAdFailedToLoad(errorCode);
                    showAfterLoad = false;
                    if (countLoad < 10) {
                        mInterstitialAd.loadAd(mAdRequest);
                        countLoad++;
                    }
                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();
                    showAfterLoad = false;
                    if (mListener != null) {
                        mListener.onAdsClicked();
                    }
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    if (showAfterLoad) {

                    }
                    showAfterLoad = false;
                    countLoad = 1;
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    showAfterLoad = false;
                    if (mListener != null) {
                        mListener.onAdsOpened();
                    }
                }
            });

            mAdRequest = new AdRequest.Builder().build();
            mInterstitialAd.loadAd(mAdRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isLoaded() {
        try {
            return mInterstitialAd.isLoaded();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean showAds(AdmobUtilsListener listener) {
        try {
            mListener = listener;
            if (mInterstitialAd.isLoaded()) {
                try {
                    mInterstitialAd.show();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
