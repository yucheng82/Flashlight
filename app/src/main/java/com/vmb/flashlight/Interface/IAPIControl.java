package com.vmb.flashlight.Interface;

import com.vmb.flashlight.Config;
import com.vmb.flashlight.model.Ads;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IAPIControl {

    @GET(Config.Url.URL_API_CONTROL_ADS)
    Call<Ads> getAds(@Query("deviceID") String deviceID,
                     @Query("code") String code,
                     @Query("version") String version,
                     @Query("country") String country,
                     @Query("timereg") String timereg,
                     @Query("packg") String packg);
}
