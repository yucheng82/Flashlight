package com.vmb.flashlight;

public class Config {

    public static final String ID_BANNER_AD_UNIT = "";
    public static final String CODE_CONTROL_APP = "";

    public class RequestCode {
        public static final int CODE_REQUEST_PERMISSION_INTERNET = 0;
        public static final int CODE_REQUEST_PERMISSION_ACCESS_NETWORK_STATE = 1;
        public static final int CODE_REQUEST_PERMISSION_CAMERA = 2;
    }

    public class Url {
        public static final String URL_API_CONTROL_ADS = "http://gamemobileglobal.com/api/control-app.php?";
        public static final String URL_LINK_SERVER = "http://gamemobileglobal.com/api/control_s.php";
    }
}
