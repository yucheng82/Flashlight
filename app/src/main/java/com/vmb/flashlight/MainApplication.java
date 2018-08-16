package com.vmb.flashlight;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;

import com.crashlytics.android.Crashlytics;
import com.noname.quangcaoads.QuangCaoSetup;
import com.vmb.flashlight.util.TimeRegUtil;

import java.util.Arrays;

import flashlight.supper.flashlight.R;
import io.fabric.sdk.android.Fabric;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());
        TimeRegUtil.setTimeRegister(getApplicationContext());

        QuangCaoSetup.setConfig(getApplicationContext(), Config.CODE_CONTROL_APP, "1.0");
        QuangCaoSetup.setLinkServer(getApplicationContext(), Config.Url.URL_LINK_SERVER);
    }
}
