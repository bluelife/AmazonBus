package com.oschina.bluelife.amazonbus.service;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by slomka.jin on 2016/10/14.
 */

public interface LoginFormApi {
    @GET("/gp/homepage.html")
    Call<ResponseBody> loadForm();
}
