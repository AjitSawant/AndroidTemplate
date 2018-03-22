package com.palash.sampleapp.utilities;

public class Constants {
    public static final String TAG = "InamdarFeedbackApp";
    public static final String KEY_REQUEST_DATA = "data";

    public static final int HTTP_OK_200 = 200;
    public static final int HTTP_CREATED_201 = 201;
    public static final int HTTP_NO_RECORD_FOUND_OK_204 = 204;
    public static final int HTTP_NOT_FOUND_401 = 401;
    public static final int HTTP_NOT_OK_404 = 404;
    public static final int HTTP_NOT_OK_500 = 500;
    public static final int HTTP_NOT_OK_501 = 501;
    public static final int HTTP_AMBIGUOUS_300 = 300;

    public static final int SYNC_STOP = 1001;

    //Local database link
    public static final String BASE_URL = "http://192.168.1.133/InamdarApp/";        // local live
    //public static final String BASE_URL = "http://103.229.5.22/InamdarApp/";        // developer live

    //Live database link
    //public static final String BASE_URL = "http://49.248.153.18:8080/InamdarApp/";     // inamdar public live(client side)

    public static final String LOGIN_URL = BASE_URL + "Login/DocLogin?LoginType=";

    public static final String STATUS_LOG_IN = "login";
    public static final String STATUS_LOG_OUT = "logout";
}
