package com.oschina.bluelife.amazonbus.price;

import com.oschina.bluelife.amazonbus.service.StorageListApi;
import com.oschina.bluelife.amazonbus.utils.UrlCombin;


import java.io.IOException;

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
                    Timber.d("list:%s",response.body().string());
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

    public void updatePrice(){
        String json="{\"tableId\":\"myitable\",\"viewContext\":{\"action\":\"DELAYED_LOAD\",\"pageNumber\":1,\"recordsPerPage\":25,\"sortedColumnId\":\"date\",\"sortOrder\":\"DESCENDING\",\"searchText\":\"\",\"tableId\":\"myitable\",\"filters\":[{\"filterGroupId\":\"LISTINGS_VIEW\",\"filterCriteria\":[{\"value\":\"true\",\"filterId\":\"Catalog\"}]},{\"filterGroupId\":\"FULFILLMENT\",\"filterCriteria\":[{\"value\":\"true\",\"filterId\":\"AllChannels\"}]}],\"clientState\":{\"recordsAboveTheFold\":\"10\",\"confirmActionPageMaxRecords\":\"250\",\"totalNumberOfRecords\":\"15\",\"currentNumberOfRecords\":\"10\",\"viewId\":\"FBA\",\"customActionType\":\"\"}},\"s3Keys\":[\"516cca4f-452c-45b9-b535-0a706765d5a1\"]}";
        RequestBody body=RequestBody.create(MediaType.parse("text/plain"), json);
        Call<ResponseBody> updateHtml=storageListApi.updatePrice(body);
        updateHtml.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String html=response.body().string();
                    Timber.d(html);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


}
