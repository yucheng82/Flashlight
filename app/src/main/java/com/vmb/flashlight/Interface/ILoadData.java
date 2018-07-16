package com.vmb.flashlight.Interface;

import com.vmb.flashlight.Config;
import com.vmb.flashlight.model.Ads;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ILoadData {

    @GET(Config.Url.URL_API_CONTROL_ADS)
    Call<Ads> getAds(@Query("code") String code, @Query("deviceID") String deviceID, @Query("time_install") String time_install);
}
