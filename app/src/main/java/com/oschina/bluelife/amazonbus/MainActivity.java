package com.oschina.bluelife.amazonbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.oschina.bluelife.amazonbus.cookie.AddCookiesInterceptor;
import com.oschina.bluelife.amazonbus.cookie.ReceivedCookiesInterceptor;
import com.oschina.bluelife.amazonbus.service.LoginApi;
import com.oschina.bluelife.amazonbus.service.LoginFormApi;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity implements Callback<ResponseBody> {

    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(new AddCookiesInterceptor(this)); // VERY VERY IMPORTANT
        builder.addInterceptor(new ReceivedCookiesInterceptor(this)); // VERY VERY IMPORTANT
        client = builder.build();

        Retrofit retrofit=new Retrofit.Builder()
                .client(client)
                .baseUrl("https://sellercentral.amazon.com/")
                .build();
        LoginFormApi loginFormApi=retrofit.create(LoginFormApi.class);
        Call<ResponseBody> call=loginFormApi.loadForm();
        call.enqueue(this);
    }



    private void loginTask(String url,HashMap<String,String> maps){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("https://sellercentral.amazon.com/")
                .client(client)
                .build();
        LoginApi loginApi=retrofit.create(LoginApi.class);
        Call<ResponseBody> result=loginApi.login(url,maps);
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String data= null;
                try {
                    data = response.body().string();
                    Log.w("ok",response.code()+" "+data.contains("请回答如下"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //com.oschina.bluelife.amazonbus.Log.d("okk",data);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        Log.w("eeee","res");
        try {
            String html=response.body().string();
            Document document= Jsoup.parse(html);
            Element element=document.select("form[name=signIn]").first();
            Elements inputDataes=element.select("input[type=hidden]");
            HashMap<String,String> authDataMap=new HashMap<>();
            for (Element data: inputDataes) {
                if(!data.attr("name").equals("")) {
                    Log.w("ssss", data.attr("name") + "===" + data.attr("value"));
                    authDataMap.put(data.attr("name"), data.attr("value"));
                }
            }
            authDataMap.put("email","anxier84556@163.com");
            authDataMap.put("password","qq9488598");
            loginTask(element.attr("action"),authDataMap);
            Log.w("rrr",element.attr("action")+"");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {

    }
}
