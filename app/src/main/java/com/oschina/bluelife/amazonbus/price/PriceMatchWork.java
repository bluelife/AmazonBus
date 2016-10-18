package com.oschina.bluelife.amazonbus.price;

import com.oschina.bluelife.amazonbus.model.price.ClientState;
import com.oschina.bluelife.amazonbus.model.price.FilterCriteria;
import com.oschina.bluelife.amazonbus.model.price.Filters;
import com.oschina.bluelife.amazonbus.model.price.Goods;
import com.oschina.bluelife.amazonbus.model.price.GoodsInfo;
import com.oschina.bluelife.amazonbus.model.price.LowPriceTip;
import com.oschina.bluelife.amazonbus.model.price.Metadata;
import com.oschina.bluelife.amazonbus.model.price.PriceUpdateItem;
import com.oschina.bluelife.amazonbus.model.price.UpdatedField;
import com.oschina.bluelife.amazonbus.model.price.UpdatedRecord;
import com.oschina.bluelife.amazonbus.model.price.ViewContext;
import com.oschina.bluelife.amazonbus.price.transfer.GoodsTransfer;
import com.oschina.bluelife.amazonbus.price.transfer.TableTransfer;
import com.oschina.bluelife.amazonbus.service.SavaPriceApi;
import com.oschina.bluelife.amazonbus.service.StorageListApi;
import com.oschina.bluelife.amazonbus.utils.UrlCombin;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import timber.log.Timber;

/**
 * Created by HiWin10 on 2016/10/17.
 */

public class PriceMatchWork {

    Retrofit retrofit;
    StorageListApi storageListApi;
    @PriceStrategy int priceStrategy=PriceStrategy.HIGN;

    private List<Goods> goodsList;
    private List<LowPriceTip> lowPriceTips;
    private float priceNumber=10f;

    public PriceMatchWork(Retrofit retrofit){
        this.retrofit=retrofit;
    }

    public void loadStorageList(){
        storageListApi=retrofit.create(StorageListApi.class);
        Call<ResponseBody> storageHtml=storageListApi.storageList(UrlCombin.getStorageList(1));
        storageHtml.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    goodsList= GoodsTransfer.parser(response.body().string());
                    loadPrichMatch();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void loadPrichMatch(){
        String json="{\"tableId\":\"myitable\",\"viewContext\":{\"action\":\"DELAYED_LOAD\",\"pageNumber\":1,\"recordsPerPage\":25,\"sortedColumnId\":\"date\",\"sortOrder\":\"DESCENDING\",\"searchText\":\"\",\"tableId\":\"myitable\",\"filters\":[{\"filterGroupId\":\"LISTINGS_VIEW\",\"filterCriteria\":[{\"value\":\"true\",\"filterId\":\"Catalog\"}]},{\"filterGroupId\":\"FULFILLMENT\",\"filterCriteria\":[{\"value\":\"true\",\"filterId\":\"AllChannels\"}]}],\"clientState\":{\"recordsAboveTheFold\":\"10\",\"confirmActionPageMaxRecords\":\"250\",\"totalNumberOfRecords\":\"15\",\"currentNumberOfRecords\":\"10\",\"viewId\":\"FBA\",\"customActionType\":\"\"}},\"s3Keys\":[\"516cca4f-452c-45b9-b535-0a706765d5a1\"]}";
        RequestBody body=RequestBody.create(MediaType.parse("text/plain"), json);
        Call<ResponseBody> updateHtml=storageListApi.updatePrice(body);
        updateHtml.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String html=response.body().string();
                    lowPriceTips= TableTransfer.parser(html);
                    updatePrice();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void updatePrice(){

        List<UpdatedRecord> updatedRecords=new ArrayList<>();
        PriceUpdateItem updateItem=new PriceUpdateItem();
        switch (priceStrategy){
            case PriceStrategy.HIGN:

                for (int i = 0; i < goodsList.size(); i++) {
                    Goods goods=goodsList.get(i);
                    float newPrice=Float.parseFloat(goods.price)+priceNumber;
                    UpdatedRecord record=new UpdatedRecord();
                    record.recordId = goods.id;
                    Metadata metadata = new Metadata();
                    metadata.recordId = goods.id;
                    UpdatedField updatedField = new UpdatedField();
                    updatedField.beforeValue = goods.price;
                    updatedField.changedValue = ""+newPrice;
                    updatedField.metadata = metadata;
                    List<UpdatedField> updatedFields = new ArrayList<>();
                    updatedFields.add(updatedField);
                    record.updatedFields = updatedFields;
                    updatedRecords.add(record);
                }
                break;
            case PriceStrategy.LOW:
                for (int i = 0; i < goodsList.size(); i++) {
                    Goods goods=goodsList.get(i);
                    float newPrice=Float.parseFloat(goods.price)-priceNumber;
                    UpdatedRecord record=new UpdatedRecord();
                    record.recordId = goods.id;
                    Metadata metadata = new Metadata();
                    metadata.recordId = goods.id;
                    UpdatedField updatedField = new UpdatedField();
                    updatedField.beforeValue = goods.price;
                    updatedField.changedValue = ""+newPrice;
                    updatedField.metadata = metadata;
                    List<UpdatedField> updatedFields = new ArrayList<>();
                    updatedFields.add(updatedField);
                    record.updatedFields = updatedFields;
                    updatedRecords.add(record);
                }
                break;

            case PriceStrategy.MATCH:
                for (int i = 0; i < goodsList.size(); i++) {
                    if(lowPriceTips.get(i).hasLow){
                        Goods goods=goodsList.get(i);
                        UpdatedRecord record=new UpdatedRecord();
                        record.recordId = goods.id;
                        Metadata metadata = new Metadata();
                        metadata.recordId = goods.id;
                        UpdatedField updatedField = new UpdatedField();
                        updatedField.beforeValue = goods.price;
                        updatedField.changedValue = lowPriceTips.get(i).lowPrice;
                        updatedField.metadata = metadata;
                        List<UpdatedField> updatedFields = new ArrayList<>();
                        updatedFields.add(updatedField);
                        record.updatedFields = updatedFields;
                        updatedRecords.add(record);
                    }
                }
                break;
        }

        ViewContext viewContext=new ViewContext();
        ClientState clientState=new ClientState();
        FilterCriteria filterCriteria1=new FilterCriteria();
        filterCriteria1.filterId="Catalog";
        filterCriteria1.value="true";
        FilterCriteria filterCriteria2=new FilterCriteria();
        filterCriteria2.filterId="AllChannels";
        List<FilterCriteria> filterCriteriaList=new ArrayList<>();
        filterCriteriaList.add(filterCriteria1);
        filterCriteriaList.add(filterCriteria2);
        filterCriteria2.value="true";
        Filters filters=new Filters();
        filters.filterCriteria=filterCriteriaList;
        viewContext.action="TABLE_SAVED";
        viewContext.clientState=clientState;
        viewContext.pageNumber=1;
        List<Filters> filtersList=new ArrayList<>();
        filtersList.add(filters);
        viewContext.filters=filtersList;
        viewContext.clientState=clientState;
        viewContext.recordsPerPage=25;
        updateItem.updatedRecords=updatedRecords;
        updateItem.viewContext=viewContext;
        Moshi moshi = new Moshi.Builder().build();

        JsonAdapter<PriceUpdateItem> jsonAdapter=moshi.adapter(PriceUpdateItem.class);
        String json=jsonAdapter.toJson(updateItem);
        RequestBody body=RequestBody.create(MediaType.parse("text/plain"), json);
        SavaPriceApi savaPriceApi=retrofit.create(SavaPriceApi.class);
        Call<ResponseBody> result=savaPriceApi.save(body);
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }



}
