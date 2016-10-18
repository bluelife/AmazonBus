package com.oschina.bluelife.amazonbus.model.price;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HiWin10 on 2016/10/17.
 */


public class ViewContext {
    public String action;
    public Integer pageNumber;
    public Integer recordsPerPage;
    public String sortedColumnId="date";
    public String sortOrder="DESCENDING";
    public String searchText;
    public String tableId="myitable";
    public List<Filters> filters = new ArrayList<Filters>();
    public ClientState clientState;

}
