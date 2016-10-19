package com.oschina.bluelife.amazonbus.price.transfer;

import com.oschina.bluelife.amazonbus.model.price.Goods;
import com.oschina.bluelife.amazonbus.model.price.GoodsInfo;
import com.oschina.bluelife.amazonbus.model.price.LowPriceTip;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by slomka.jin on 2016/10/18.
 */

public class GoodsTransfer {

    public static List<Goods> parser(String html){
        List<Goods> goodsList=new ArrayList<>();
        Document document= Jsoup.parse(html);
        Elements goodsElement=document.select("div.spaui-squishy-content tbody tr.mt-row");
        Timber.d(goodsElement.size()+"");
        for (int i = 0; i < goodsElement.size(); i++) {
            Element element=goodsElement.get(i);
            String priceJson=element.attr("data-delayed-dependency-data");
            Moshi moshi = new Moshi.Builder().build();

            JsonAdapter<GoodsInfo> jsonAdapter=moshi.adapter(GoodsInfo.class);
            GoodsInfo goodsInfo=new GoodsInfo();
            try {
                goodsInfo=jsonAdapter.fromJson(priceJson);
                String goodId=element.attr("id");

                Goods goods=new Goods(goodId,goodsInfo.MYIService.Price);
                Timber.d(goods.id+","+goods.price);
                goodsList.add(goods);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return goodsList;
    }
}
