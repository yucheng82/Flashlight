package com.vmb.flashlight.util;

import com.facebook.ads.AdSettings;

import java.util.ArrayList;
import java.util.List;

public class AdSetting {

    public static void addTestDevice() {
        List<String> testDevices = new ArrayList<>();
        AdSettings.addTestDevice("ec98be7b-e9f0-46e3-82a7-7c229ad48b86");
        AdSettings.addTestDevices(testDevices);
    }
}
