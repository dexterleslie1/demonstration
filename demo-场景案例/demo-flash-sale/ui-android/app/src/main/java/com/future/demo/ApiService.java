package com.future.demo;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    /**
     * 查询商品列表
     *
     * @return
     */
    @GET("/api/v1/order/listProductByIds")
    Call<JsonObject> listProductByIds();

    @GET("/api/v1/order/addOrdinaryProduct")
    Call<JsonObject> addOrdinaryProduct(/*@Query("merchantId") long merchantId,
                                        @Query("stockAmount") int stockAmount*/);

    @GET("/api/v1/order/addFlashSaleProduct")
    Call<JsonObject> addFlashSaleProduct();

    @GET("/api/v1/order/getProductById")
    Call<JsonObject> getProductById(@Query("id") long id);

    @GET("/api/v1/order/create")
    Call<JsonObject> createOrderOrdinary(@Query("userId") long userId,
                                         @Query("productId") long productId,
                                         @Query("randomCreateTime") boolean randomCreateTime);

    @GET("/api/v1/order/createFlashSale")
    Call<JsonObject> createOrderFlashSale(@Query("userId") long userId,
                                          @Query("productId") long productId,
                                          @Query("randomCreateTime") boolean randomCreateTime);
}
