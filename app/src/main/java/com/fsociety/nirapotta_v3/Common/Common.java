package com.fsociety.nirapotta_v3.Common;

import android.location.Location;

import com.fsociety.nirapotta_v3.Remote.FCMClient;
import com.fsociety.nirapotta_v3.Remote.IFCMService;
import com.fsociety.nirapotta_v3.Remote.IGoogleAPI;
import com.fsociety.nirapotta_v3.Remote.RetrofitClient;


public class Common {

    //public static String currentToken="";

//    public static IGoogleAPI getGoogleAPI()
//    {
//        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
//    }

    public static final String token_table = "Tokens";
    public static Location mLastLocation = null;

    public static final String baseURL = "https://maps.googleapis.com";
    public static final String fcmURL = "https://fcm.googleapis.com";

    public static IGoogleAPI getGoogleAPI() {
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }

    public static IFCMService getFCMService() {
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }
}
