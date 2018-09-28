package com.vmb.flashlight.util;

import com.facebook.ads.AdSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AdSetting {

    public static void addTestDevice() {
        List<String> testDevices = new ArrayList<>();
        AdSettings.addTestDevice("938e3e29-f81b-4aba-bd74-c0d4272dbef3");
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
