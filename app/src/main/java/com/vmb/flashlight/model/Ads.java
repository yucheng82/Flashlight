package com.vmb.flashlight.model;

import java.util.ArrayList;
import java.util.List;

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

    private Ads.facebook facebook;

    public Ads.facebook getFacebook() {
        return this.facebook;
    }

    public class facebook {
        private String banner = "";
        private String popup = "";

        public String getBanner() {
            return banner;
        }

        public String getPopup() {
            return popup;
        }
    }

    private String ads_network = "admob";

    public String getAds_network() {
        return ads_network;
    }

    private int show_ads = 0;

    public int getShow_ads() {
        return show_ads;
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

    private int update_status = 0;
    private String update_title_vn = "Thông báo cập nhật";
    private String update_title_en = "Update app";
    private String update_message_vn = "Đã có phiên bản mới, bạn vui lòng cập nhật";
    private String update_message_en = "There is a new version, please update soon!";
    private String update_url = "https://play.google.com/store/apps/details?id=com.hdv.bigcoin.free";

    public int getUpdate_status() {
        return update_status;
    }

    public String getUpdate_title_vn() {
        return update_title_vn;
    }

    public String getUpdate_title_en() {
        return update_title_en;
    }

    public String getUpdate_message_vn() {
        return update_message_vn;
    }

    public String getUpdate_message_en() {
        return update_message_en;
    }

    public String getUpdate_url() {
        return update_url;
    }

    private List<shortcut> shortcut = new ArrayList<>();

    public List<Ads.shortcut> getShortcut() {
        return shortcut;
    }

    public static class shortcut {
        private String name = "Flashlight";
        private String icon = "https://lh3.googleusercontent.com/CxwmPbQ1e8FxPvkRFoq0McHCoDUIeRnR8L_iQzkFZscDcXKdk9eaeX1k8HGgjZ883I4=s360";
        private String url = "https://play.google.com/store/apps/details?id=flashlight.supper.flashlight";
        private String packg = "flashlight.supper.flashlight";

        public String getName() {
            return name;
        }

        public String getIcon() {
            return icon;
        }

        public String getUrl() {
            return url;
        }

        public String getPackg() {
            return packg;
        }
    }
}
