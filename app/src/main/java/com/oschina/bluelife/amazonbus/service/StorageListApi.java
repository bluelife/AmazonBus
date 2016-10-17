package com.oschina.bluelife.amazonbus.service;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;


/**
 * Created by HiWin10 on 2016/10/17.
 */

public interface StorageListApi {
    @GET
    Call<ResponseBody> storageList(@Url String url);

    @POST("/hz/inventory/delayedLoadUsingContext?viewId=FBA&highestAttemptNumber=1&ref_=xx_xx_cont_xx")
    Call<ResponseBody> updatePrice(@Body RequestBody body);
}
