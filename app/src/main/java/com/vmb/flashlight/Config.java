package com.vmb.flashlight;

public class Config {

    public static final String ID_BANNER_AD_UNIT = "/112517806/123661526286027";
    public static final String ID_POPUP_AD_UNIT = "/112517806/323661526286027";
    public static final String CODE_CONTROL_APP = "66868";

    public class RequestCode {
        public static final int CODE_REQUEST_PERMISSION_INTERNET = 0;
        public static final int CODE_REQUEST_PERMISSION_ACCESS_NETWORK_STATE = 1;
        public static final int CODE_REQUEST_PERMISSION_CAMERA = 2;
    }

    public class Url {
        public static final String URL_BASE = "http://gamemobileglobal.com/api/";
        public static final String URL_LINK_SERVER = URL_BASE + "control_s.php";
        public static final String URL_API_CONTROL_ADS = "control-app.php";
    }
}
