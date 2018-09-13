package com.vmb.flashlight.util;

import com.facebook.ads.AdSettings;

import java.util.ArrayList;
import java.util.List;

public class AdSetting {

    public static void addTestDevice() {
        List<String> testDevices = new ArrayList<>();
        testDevices.add("2e44e854-d33c-427a-a791-63c11369315a");
        AdSettings.addTestDevices(testDevices);
    }
}
