package com.future.demo;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 用于协助初始化所有 retrofit 接口并提供全局调用
 */
public class RetrofitClient {

    private final static String TAG = RetrofitClient.class.getSimpleName();

    private final static ApiService apiService;

    static {
        // 初始化 ApiService 实例
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();

        // 创建ApiService实例
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.235.128:8080")
                .client(new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        try {
                            Request request = chain.request();
                            Response response = chain.proceed(request);

                            String responseBodyStr = response.body().string();
                            if (!response.isSuccessful()) {
                                // 统一处理非 HTTP 200 响应
                                String errorMessage = "HTTP 错误状态码：" + response.code() + "，错误信息：" + responseBodyStr;
                                throw new BusinessException(ErrorCodeConstant.ErrorCodeCommon, errorMessage);
                            } else {
                                // 统一处理 HTTP 200 响应但业务异常
                                JsonObject jsonObject = gson.fromJson(responseBodyStr, JsonObject.class);
                                int errorCode = jsonObject.get("errorCode").getAsInt();
                                String errorMessage =
                                        jsonObject.get("errorMessage").isJsonNull() ? null : jsonObject.get("errorMessage").getAsString();
                                if (errorCode > 0) {
                                    throw new BusinessException(errorCode, errorMessage);
                                }
                            }

                            // 因为之前已经读取 response 内容需要重新构造一个新的
                            MediaType contentType = response.body().contentType();
                            ResponseBody newResponseBody = ResponseBody.create(contentType, responseBodyStr);
                            return response.newBuilder()
                                    .body(newResponseBody)
                                    .build();
                        } catch (Throwable t) {
                            if (!(t instanceof BusinessException)) {
                                Log.e(TAG, t.getMessage(), t);
                            }
                            throw t;
                        }
                    }
                }).build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public static ApiService getApiService() {
        return apiService;
    }

}
