package com.oschina.bluelife.amazonbus;

import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.oschina.bluelife.amazonbus.service.LoginApi;
import com.oschina.bluelife.amazonbus.service.WaitApi;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.CookieHandler;
import java.util.HashMap;

import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity implements Callback<ResponseBody> {

    private OkHttpClient client;
    private Retrofit retrofit;
    private Handler handler = new Handler();
    private String currentUrl;
    private String cookie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //LoginFormApi loginFormApi=retrofit.create(LoginFormApi.class);
        //Call<ResponseBody> call=loginFormApi.loadForm();
        //call.enqueue(this);
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        String email = "anxier84556@163.com";
        String password = "qq9488598";
        //final String loadJs="javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');";
        final String js = "javascript:document.forms[0].email.value = '" + email + "';"
                + "document.forms[0].password.value = '" + password + "';"
                + "(function(){" +
                "l=document.getElementById('signInSubmit');" +
                "e=document.createEvent('HTMLEvents');" +
                "e.initEvent('click',true,true);" +
                "l.dispatchEvent(e);" +
                "})()";
        webView.setWebChromeClient(new WebChromeClient() {
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                currentUrl = url;
                Log.w("page", url.contains("sellercentral.amazon.com/gp/homepage.html") + "");
                if (url.contains("sellercentral.amazon.com/gp/homepage.html")) {

                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Log.w("eeee", "load js");
                    view.evaluateJavascript(js, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            cookie = CookieManager.getInstance().getCookie(currentUrl);
                            Log.d("rrrrr", "All the cookies in a string:" + cookie);
                            buildRetrofit();
                            WaitApi waitApi = retrofit.create(WaitApi.class);
                            Call<ResponseBody> result = waitApi.getWait("https://sellercentral.amazon.com/gp/orders-v2/list/ref=id_myo_wos4_home?byDate=shipDate&statusFilter=Pending&searchFulfillers=mfn&ignoreSearchType=1&searchType=OrderStatus&_encoding=UTF8&searchDateOption=preSelected&sortBy=OrderStatusDescending&showCancelled=0&shipSearchDateOption=noTimeLimit");
                            result.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    try {
                                        com.oschina.bluelife.amazonbus.Log.d("okk", response.body().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {

                                }
                            });
                            Log.w("rrrrr", "load data");
                        }
                    });
                } else {
                    view.loadUrl(js);
                }
            }
        });
        webView.addJavascriptInterface(new LoadListener(this), "HTMLOUT");
        webView.loadUrl("https://sellercentral.amazon.com/gp/homepage.html/ref=xx_home_logo_xx/164-8024327-0010362");
    }

    public void doLogin(String url, HashMap<String, String> maps) {
        loginTask(url, maps);
    }

    private void buildRetrofit() {
        client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        JavaNetCookieJar jncj = new JavaNetCookieJar(CookieHandler.getDefault());
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        ClearableCookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(this));
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(logging);
        //builder.cookieJar(cookieJar);

        builder.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                final Request original = chain.request();

                final Request authorized = original.newBuilder()
                        .addHeader("Cookie", cookie)
                        .build();

                Log.w("rrrrrrr","addcookie="+cookie);
                return chain.proceed(authorized);
            }
        });

        //builder.addInterceptor(new AddCookiesInterceptor(this)); // VERY VERY IMPORTANT
        //builder.addInterceptor(new ReceivedCookiesInterceptor(this)); // VERY VERY IMPORTANT
        client = builder.build();

        retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://sellercentral.amazon.com/")
                .build();
    }

    private void loginTask(String url, HashMap<String, String> maps) {
        /*Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("https://sellercentral.amazon.com/")
                .client(client)
                .build();*/
        LoginApi loginApi = retrofit.create(LoginApi.class);
        Call<ResponseBody> result = loginApi.login(url, maps);
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String data = null;
                try {
                    data = response.body().string();
                    Log.w("ok", response.code() + " " + data.contains("请回答如下"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                com.oschina.bluelife.amazonbus.Log.d("okk", data);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
        Log.w("tttt", "login" + url);
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        Log.w("eeee", "res");
        try {
            String html = response.body().string();
            Document document = Jsoup.parse(html);
            Element element = document.select("form[name=signIn]").first();
            Elements inputDataes = element.select("input[type=hidden]");
            Element element1 = document.select("input[name=metadata1]").first();
            Log.w("sssss", element1 + "");
            HashMap<String, String> authDataMap = new HashMap<>();
            authDataMap.put("password", "qq9488598");
            authDataMap.put("email", "anxier84556@163.com");
            authDataMap.put("x", "43");
            authDataMap.put("y", "11");
            authDataMap.put("metadata1", "Ap152hBSKdXW2MaVRJbjwiaVgGGMsHkIFtV8KZ9coCxLd48kEDtRk9bR/F9vnq3Rlkf2ykd/rUhI6MyUhYVi9vYcSMVndmbuXbNthtpFH4S4VCKzihVspyKjoZyuAwYRTWLxbQ2r/CkYxoJXeqaUIq3LWAUPKGt0syLOaZabPM6aU5ErAv5IEj/qyA2Wxs0XBbFw6beh2fh5NWtbI8DLuTIg8kLxjtcYWwRRA0BfTt1CzPUvvPWT0mXT8mx/OTdZDYm3TxUNhkUPLi/4F6OJiwtwpe1e6Hgx66PtstF+lyKYf2VX3vaYrQrkJqNgc5M0B6XMhAS0VWUg9gKU7PjVrcu4wyMKYh6wpbrfAWeLgc8J8kgS5HoM3V4ucCI48oEgDE4CH0N3PiciKh7Trjf0CJBd/v1XwrH7+mx+UD3p1KZ/9XtCOBvNvOEWoHkVXf1bswj9rMSb9SPiHGJ90ztOBjleS6G1qKnUmN7ElAhqDpKo9U9LurH6KfD5n+wrc+MWAQjkny+jNwFenLyQIcGgt2gDetJHN7fhWgBBfv/xnGxvEwWo3b72nrxJdTSHmCkWmBk9fZpkovqlUaJlcCzfNEFfAhHp+ri+Ii8oxtL01e6NNLysf1kJjw2NAdKQVORqdckRUHNg4frG+1Opn/agK2GIQfzPybjyOUjH5kDwNRAacYmJCog6yGGSZwrkjS5qKlLXVyx6RoBoKoSxnupIKPfo6J4/qPuIPO2TTm516/qiI31waduqqjeOKVLcAS3I");
            for (Element data : inputDataes) {
                if (!data.attr("name").equals("")) {
                    authDataMap.put(data.attr("name"), data.attr("value"));
                }
            }


            loginTask(element.attr("action"), authDataMap);
            Log.w("rrr", element.attr("action") + "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {

    }
}
