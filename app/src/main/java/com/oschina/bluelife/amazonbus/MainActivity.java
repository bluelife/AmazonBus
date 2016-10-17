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
import com.oschina.bluelife.amazonbus.model.price.PriceUpdateItem;
import com.oschina.bluelife.amazonbus.price.PriceMatchWork;
import com.oschina.bluelife.amazonbus.service.LoginApi;
import com.oschina.bluelife.amazonbus.service.WaitApi;
import com.oschina.bluelife.amazonbus.utils.JS;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;


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
import timber.log.Timber;


public class MainActivity extends AppCompatActivity {

    private OkHttpClient client;
    private Retrofit retrofit;
    private Handler handler = new Handler();
    private String currentUrl;
    private String cookie;
    private boolean login;
    private PriceMatchWork priceMatchWork;

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
        final String js=JS.injectLogin(email,password);
        webView.setWebChromeClient(new WebChromeClient() {
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                currentUrl = url;
                Log.w("page", url.contains("sellercentral.amazon.com/gp/homepage.html") + "");
                if (url.contains("sellercentral.amazon.com/gp/homepage.html")) {
                    login=true;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Log.w("eeee", "load js");
                    view.evaluateJavascript(js, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            cookie = CookieManager.getInstance().getCookie(currentUrl);
                            Log.d("rrrrr", "All the cookies in a string:" + cookie);
                            buildRetrofit();
                            priceMatchWork=new PriceMatchWork(retrofit);
                            priceMatchWork.loadStorageList();

                        }
                    });
                } else {
                    view.loadUrl(js);
                }
            }
        });
        //testJson();
        webView.loadUrl("https://sellercentral.amazon.com/gp/homepage.html/ref=xx_home_logo_xx/164-8024327-0010362");
    }

    private void testJson(){
        String json="{\"tableId\":\"myitable\",\"viewContext\":{\"action\":\"DELAYED_LOAD\",\"pageNumber\":1,\"recordsPerPage\":25,\"sortedColumnId\":\"date\",\"sortOrder\":\"DESCENDING\",\"searchText\":\"\",\"tableId\":\"myitable\",\"filters\":[{\"filterGroupId\":\"LISTINGS_VIEW\",\"filterCriteria\":[{\"value\":\"true\",\"filterId\":\"Catalog\"}]},{\"filterGroupId\":\"FULFILLMENT\",\"filterCriteria\":[{\"value\":\"true\",\"filterId\":\"AllChannels\"}]}],\"clientState\":{\"recordsAboveTheFold\":\"10\",\"confirmActionPageMaxRecords\":\"250\",\"totalNumberOfRecords\":\"15\",\"currentNumberOfRecords\":\"10\",\"viewId\":\"FBA\",\"customActionType\":\"\"}},\"s3Keys\":[\"516cca4f-452c-45b9-b535-0a706765d5a1\"]}";
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<PriceUpdateItem> jsonAdapter=moshi.adapter(PriceUpdateItem.class);
        try {
            PriceUpdateItem priceUpdateItem=jsonAdapter.fromJson(json);
            priceUpdateItem.viewContext.pageNumber=2;
            String js=jsonAdapter.toJson(priceUpdateItem);
            Timber.d(js);
        } catch (IOException e) {
            e.printStackTrace();
        }


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



}
