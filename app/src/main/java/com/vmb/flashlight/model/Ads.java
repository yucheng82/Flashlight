package com.vmb.flashlight.model;

public class Ads {

    private static Ads ads;

    public static Ads getInstance() {
        if (ads == null) {
            synchronized (Flashlight.class) {
                ads = new Ads();
            }
        }
        return ads;
    }

    public static void setInstance(Ads popup) {
        ads = popup;
    }

    private Ads.admob admob;

    public Ads.admob getAdmob() {
        return this.admob;
    }

    public class admob {
        private String banner = "";
        private String popup = "";

        public String getBanner() {
            return banner;
        }

        public String getPopup() {
            return popup;
        }
    }

    private Ads.config config;

    public Ads.config getConfig() {
        return this.config;
    }

    public class config {
        private int time_start_show_popup = 0;
        private int offset_time_show_popup = 0;

        public int getTime_start_show_popup() {
            return time_start_show_popup;
        }

        public int getOffset_time_show_popup() {
            return offset_time_show_popup;
        }
    }
}
