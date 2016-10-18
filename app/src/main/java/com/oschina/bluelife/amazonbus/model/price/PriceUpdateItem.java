package com.oschina.bluelife.amazonbus.model.price;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by slomka.jin on 2016/10/18.
 */

public class PriceUpdateItem {
    public String tableId="myitable";
    public List<UpdatedRecord> updatedRecords = new ArrayList<UpdatedRecord>();
    public ViewContext viewContext;
}
