package com.future.demo;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    /**
     * 查询商品列表
     *
     * @return
     */
    @GET("/api/v1/order/listProductByIds")
    Call<JsonObject> listProductByIds();

}
