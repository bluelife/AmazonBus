package com.oschina.bluelife.amazonbus.service;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by slomka.jin on 2016/10/18.
 */

public interface SavaPriceApi {

    @Headers("Content-Type: application/json")
    @POST("/inventory/save?viewId=FBA&ref_=xx_xx_save_xx")
    Call<ResponseBody> save(@Body RequestBody body);
}
