package com.oschina.bluelife.amazonbus;

import android.webkit.JavascriptInterface;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by HiWin10 on 2016/10/16.
 */

class LoadListener{
    private MainActivity activity;
    public LoadListener(MainActivity act){
        activity=act;
    }
    @JavascriptInterface
    public void processHTML(String html)
    {
        com.oschina.bluelife.amazonbus.Log.d("oks",html);
            Document document= Jsoup.parse(html);
            Element element=document.select("form[name=signIn]").first();
            Elements inputDataes=element.select("input[type=hidden]");
            Element element1=document.select("input[name=metadata1]").first();
            android.util.Log.w("sssss",element1.attr("value")+"");
            HashMap<String,String> authDataMap=new HashMap<>();
            authDataMap.put("password","qq9488598");
            authDataMap.put("email","anxier84556@163.com");
            authDataMap.put("x","43");
            authDataMap.put("y","11");
            //authDataMap.put("metadata1",element1.attr("value"));
            for (Element data: inputDataes) {
                if(!data.attr("name").equals("")) {
                    authDataMap.put(data.attr("name"), data.attr("value"));
                }
            }
            android.util.Log.w("rrr",element.attr("action")+"");
        Setting.postDataes=authDataMap;
        String url=element.attr("action");
        activity.doLogin(url,authDataMap);

    }
}
