package com.vmb.flashlight.util;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

    public void init(final Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference ban = database.getReference("ban");
        ban.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String ban_adm = dataSnapshot.getValue(String.class);
                SharedPreferencesUtil.putPrefferString(context, "ban", ban_adm);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        DatabaseReference inter = database.getReference("inter");
        inter.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String inter_adm = dataSnapshot.getValue(String.class);
                SharedPreferencesUtil.putPrefferString(context, "inter", inter_adm);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        DatabaseReference ban_f = database.getReference("ban_f");
        ban_f.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String ban_adm = dataSnapshot.getValue(String.class);
                SharedPreferencesUtil.putPrefferString(context, "ban_f", ban_adm);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        DatabaseReference inter_f = database.getReference("inter_f");
        inter_f.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String inter_adm = dataSnapshot.getValue(String.class);
                SharedPreferencesUtil.putPrefferString(context, "inter_f", inter_adm);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    public void initCountDown() {
        String TAG = "initCountDown";

        if (Ads.getInstance() == null || Ads.getInstance().getConfig() == null) {
            Log.i(TAG, "Ads.getInstance() null || Ads.getInstance().getConfig() null");
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
