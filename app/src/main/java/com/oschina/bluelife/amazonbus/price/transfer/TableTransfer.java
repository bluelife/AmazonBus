package com.oschina.bluelife.amazonbus.price.transfer;

import android.util.Log;

import com.oschina.bluelife.amazonbus.model.price.ClientState;
import com.oschina.bluelife.amazonbus.model.price.FilterCriteria;
import com.oschina.bluelife.amazonbus.model.price.Filters;
import com.oschina.bluelife.amazonbus.model.price.LowPriceTip;
import com.oschina.bluelife.amazonbus.model.price.Metadata;
import com.oschina.bluelife.amazonbus.model.price.PriceUpdateItem;
import com.oschina.bluelife.amazonbus.model.price.PriceViewItem;
import com.oschina.bluelife.amazonbus.model.price.UpdatedField;
import com.oschina.bluelife.amazonbus.model.price.UpdatedRecord;
import com.oschina.bluelife.amazonbus.model.price.ViewContext;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by slomka.jin on 2016/10/18.
 */

public class TableTransfer {

    public static List<LowPriceTip> parser(String html){
        Document document=Jsoup.parse(html,"", Parser.xmlParser());
        List<LowPriceTip> lowPriceTips=new ArrayList<>();
        PriceUpdateItem updateItem=new PriceUpdateItem();
        Elements tables=document.select("div#itemRecords script tr[class=mt-row] [data-column=lowPrice-match-link]");
        for (int i = 0; i < tables.size(); i++) {
            Element table=tables.get(i);
            UpdatedRecord record=new UpdatedRecord();


            String match=table.select("span").attr("data-match-low-price");
            Moshi moshi = new Moshi.Builder().build();
            LowPriceTip lowPriceTip=new LowPriceTip();
            JsonAdapter<LowPriceTip> jsonAdapter=moshi.adapter(LowPriceTip.class);
            if(!match.equals("")) {
                try {
                    lowPriceTip= jsonAdapter.fromJson(match);
                    lowPriceTip.hasLow=true;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            lowPriceTips.add(lowPriceTip);
            //record.recordId=
            Log.w("rrr",i+" "+match);
        }


        return lowPriceTips;
    }
}
