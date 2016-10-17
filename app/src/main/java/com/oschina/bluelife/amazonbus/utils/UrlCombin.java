package com.oschina.bluelife.amazonbus.utils;

/**
 * Created by HiWin10 on 2016/10/17.
 */

public class UrlCombin {

    public static final String STORAGE_LIST="https://sellercentral.amazon.com/hz/inventory/ref=id_invmgr_dnav_xx_?tbla_myitable=sort:%7B%22sortOrder%22%3A%22DESCENDING%22%2C%22sortedColumnId%22%3A%22date%22%7D;search:;pagination:";

    public static String getStorageList(int page){
        return STORAGE_LIST+page+";";
    }
}
