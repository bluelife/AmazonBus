package com.oschina.bluelife.amazonbus.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by HiWin10 on 2016/10/16.
 */

public interface WaitApi {
    @GET
    Call<ResponseBody> getWait(@Url String url);
}
