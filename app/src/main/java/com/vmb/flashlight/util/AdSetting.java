package com.vmb.flashlight.util;

import com.facebook.ads.AdSettings;

import java.util.ArrayList;
import java.util.List;

public class AdSetting {

    public static void addTestDevice() {
        List<String> testDevices = new ArrayList<>();
        testDevices.add("aa5ce164-5c3f-4ab5-a166-117acb368ee3");
        AdSettings.addTestDevices(testDevices);
    }
}
