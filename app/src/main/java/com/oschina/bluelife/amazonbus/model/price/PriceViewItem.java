package com.oschina.bluelife.amazonbus.model.price;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by HiWin10 on 2016/10/17.
 */


public  class PriceViewItem {
    public String tableId;
    public ViewContext viewContext;
    public List<String> s3Keys = new ArrayList<String>();

}
