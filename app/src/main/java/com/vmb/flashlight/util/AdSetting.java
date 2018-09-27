package com.vmb.flashlight.util;

import com.facebook.ads.AdSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AdSetting {

    public static void addTestDevice() {
        List<String> testDevices = new ArrayList<>();
        AdSettings.addTestDevice("ec98be7b-e9f0-46e3-82a7-7c229ad48b86");
        AdSettings.addTestDevices(testDevices);
    }

    public static int rand(int min, int max) {
        try {
            Random rn = new Random();
            int range = max - min + 1;
            int randomNum = min + rn.nextInt(range);
            return randomNum;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
