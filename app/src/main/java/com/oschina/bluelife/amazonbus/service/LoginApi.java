package com.oschina.bluelife.amazonbus.service;



import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;


/**
 * Created by slomka.jin on 2016/10/14.
 */

public interface LoginApi {

    @FormUrlEncoded
    @POST
    Call<ResponseBody> login(@Url String url, @FieldMap HashMap<String, String> authData);
}
