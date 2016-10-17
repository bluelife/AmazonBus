package com.oschina.bluelife.amazonbus.utils;

/**
 * Created by HiWin10 on 2016/10/17.
 */

public class JS {
    public static String injectLogin(String email,String password){
        final String js = "javascript:document.forms[0].email.value = '" + email + "';"
                + "document.forms[0].password.value = '" + password + "';"
                + "(function(){" +
                "l=document.getElementById('signInSubmit');" +
                "e=document.createEvent('HTMLEvents');" +
                "e.initEvent('click',true,true);" +
                "l.dispatchEvent(e);" +
                "})()";
        return js;
    }
}
