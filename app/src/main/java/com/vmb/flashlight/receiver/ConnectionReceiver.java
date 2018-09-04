package com.vmb.flashlight.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vmb.flashlight.ui.MainActivity;
import com.vmb.flashlight.util.AdsUtil;
import com.vmb.flashlight.util.NetworkUtil;

/**
 * Created by keban on 9/1/2017.
 */

public class ConnectionReceiver extends BroadcastReceiver {

    private static ConnectionReceiver receiver;
    private Activity activity;

    public static ConnectionReceiver getInstance() {
        if (receiver == null)
            synchronized (ConnectionReceiver.class) {
                receiver = new ConnectionReceiver();
            }
        return receiver;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String TAG = "onReceiveConnection";

        if (NetworkUtil.isNetworkAvailable(context)) {
            Activity activity = ConnectionReceiver.getInstance().getActivity();

            if (activity == null) {
                Log.d(TAG, "activity null");
                return;
            }

            if (activity instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) activity;
                if (!AdsUtil.getInstance().isInitGetAds()) {
                    mainActivity.initGetAds();
                }
            }
        }
    }
}
