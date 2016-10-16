package com.oschina.bluelife.amazonbus.service;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Url;

/**
 * Created by slomka.jin on 2016/10/14.
 */

public interface LoginFormApi {
    @GET("/gp/homepage.html")
    @Headers({"User-Agent:Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36"})
    Call<ResponseBody> loadForm();
}
