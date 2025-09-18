package com.future.demo;


import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/http/library/api/getWithHeaderAndQueryParamter")
    Call<ResponseBody> getWithHeaderAndQueryParamter(
            @Header("customHeader") String customHeader,
            @Query("username") String username);

    @GET("/http/library/api/getWithObjectResponse")
    Call<JsonObject> getWithObjectResponse();

    @POST("/http/library/api/postWithHttp404")
    Call<JsonObject> postWithHttp404(
            @Query("name") String name
    );

    @POST("/http/library/api/postAndReturnWithBusinessException")
    Call<JsonObject> postAndReturnWithBusinessException(
            @Query("name") String name
    );
}
