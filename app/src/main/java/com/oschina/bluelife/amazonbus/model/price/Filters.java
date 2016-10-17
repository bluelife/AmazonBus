package com.oschina.bluelife.amazonbus.model.price;

import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HiWin10 on 2016/10/17.
 */


public  class Filters {
    public String filterGroupId;
    public List<FilterCriteria> filterCriteria = new ArrayList<FilterCriteria>();
}
