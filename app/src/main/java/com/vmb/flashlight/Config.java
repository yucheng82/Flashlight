package com.vmb.flashlight;

import flashlight.supper.flashlight.BuildConfig;

public class Config {
    public static final String PACKAGE_NAME = BuildConfig.APPLICATION_ID;
    public static final String VERSION_APP = BuildConfig.VERSION_NAME;
    public static final String CODE_CONTROL_APP = "50384";

    public static final String ID_BANNER_AD_UNIT = "/112517806/125581528882973";
    public static final String ID_POPUP_AD_UNIT = "/112517806/325581528882973";

    public class RequestCode {
        public static final int CODE_REQUEST_PERMISSION_CAMERA = 0;
    }

    public class Url {
        public static final String URL_BASE = "http://gamemobileglobal.com/api/";
        public static final String URL_LINK_SERVER = URL_BASE + "control_s.php";
        public static final String URL_API_CONTROL_ADS = "flashlight.php";
    }
}
