package com.vmb.flashlight;

import flashlight.supper.flashlight.BuildConfig;

public class Config {
    public static final String PACKAGE_NAME = BuildConfig.APPLICATION_ID;
    public static final String VERSION_APP = BuildConfig.VERSION_NAME;
    public static final String CODE_CONTROL_APP = "50384";

    public class AdsID {
        public static final String ID_BANNER_ADMOB_UNIT = "ca-app-pub-7253559581206279/6087271449";
        public static final String ID_POPUP_ADMOB_UNIT = "ca-app-pub-7253559581206279/7758611615";

        public static final String ID_BANNER_FB_UNIT = "688669748161717_688670691494956";
        public static final String ID_POPUP_FB_UNIT = "688669748161717_688670391494986";
    }

    public class RequestCode {
        public static final int CODE_REQUEST_PERMISSION_CAMERA = 0;
        public static final int CODE_REQUEST_PERMISSION_CAMERA_IN_SET_MODE = 1;

        public static final int SHARE_APP = 100;
        public static final int RATE_APP = 101;
        public static final int SHARE_FB = 102;
    }

    public class Url {
        public static final String URL_BASE = "http://gamemobileglobal.com/api/";
        public static final String URL_LINK_SERVER = URL_BASE + "control_s.php";
        public static final String URL_API_CONTROL_ADS = "flashlight.php";
    }

    public class SharePrefferenceKey {
        public static final String COUNT_PLAY = "count_play";
        public static final String SHOW_RATE = "show_rate";
    }
}
