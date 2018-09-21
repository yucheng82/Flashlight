package com.vmb.flashlight;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.FirebaseApp;
import com.noname.quangcaoads.QuangCaoSetup;
import com.vmb.flashlight.util.TimeRegUtil;

import io.fabric.sdk.android.Fabric;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());
        TimeRegUtil.setTimeRegister(getApplicationContext());

        QuangCaoSetup.setConfig(getApplicationContext(), Config.CODE_CONTROL_APP, Config.VERSION_APP);
        QuangCaoSetup.setLinkServer(getApplicationContext(), Config.Url.URL_LINK_SERVER);
    }
}
