package com.future.demo;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@RunWith(AndroidJUnit4.class)
public class ApiServiceTest {
    private final static String Host = "192.168.235.128";
    private final static int Port = 8080;

    private ApiService apiService;
    private ApiService2 apiService2;

    @Before
    public void setup() {
        Gson gson = new Gson();

        // 创建ApiService实例
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + Host + ":" + Port)
                .client(new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Response response = chain.proceed(request);

                        String responseBodyStr = response.body().string();
                        if (!response.isSuccessful()) {
                            // 统一处理非 HTTP 200 响应
                            String errorMessage = "HTTP 错误状态码：" + response.code() + "，错误信息：" + responseBodyStr;
                            throw new BusinessException(ErrorCodeConstant.ErrorCodeCommon, errorMessage);
                        } else {
                            // 统一处理 HTTP 200 响应但业务异常
                            BaseResponse baseResponse = gson.fromJson(responseBodyStr, BaseResponse.class);
                            int errorCode = baseResponse.getErrorCode();
                            String errorMessage = baseResponse.getErrorMessage();
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
                    }
                }).build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // 参考以下链接使用Interceptor添加http header
        // https://stackoverflow.com/questions/32963394/how-to-use-interceptor-to-add-headers-in-retrofit-2-0
        retrofit = new Retrofit.Builder()
                .baseUrl("http://" + Host + ":" + Port)
                // OkHttpClient添加http header
                .client(new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("customHeader", "customHeaderValue2")
                                .build();
                        Response response = chain.proceed(request);

                        String responseBodyStr = response.body().string();
                        if (!response.isSuccessful()) {
                            // 统一处理非 HTTP 200 响应
                            String errorMessage = "HTTP 错误状态码：" + response.code() + "，错误信息：" + responseBodyStr;
                            throw new BusinessException(ErrorCodeConstant.ErrorCodeCommon, errorMessage);
                        } else {
                            // 统一处理 HTTP 200 响应但业务异常
                            BaseResponse baseResponse = gson.fromJson(responseBodyStr, BaseResponse.class);
                            int errorCode = baseResponse.getErrorCode();
                            String errorMessage = baseResponse.getErrorMessage();
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
                    }
                }).build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService2 = retrofit.create(ApiService2.class);
    }

    @Test
    public void test() throws IOException, InterruptedException {
        //  region 测试非 HTTP 200：抛出 BusinessException

        // 同步
        try {
            Call<ObjectResponse<String>> call = apiService.postWithHttp404(null);
            call.execute().body();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof BusinessException);
            Assert.assertEquals(((BusinessException) e).getErrorCode(), ErrorCodeConstant.ErrorCodeCommon);
            Assert.assertTrue(((BusinessException) e).getErrorMessage().contains("404"));
        }

        // 异步回调
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Call<ObjectResponse<String>> call = apiService.postWithHttp404(null);
        CountDownLatch finalCountDownLatch1 = countDownLatch;
        call.enqueue(new Callback<ObjectResponse<String>>() {
            @Override
            public void onResponse(Call<ObjectResponse<String>> call, retrofit2.Response<ObjectResponse<String>> response) {
            }

            @Override
            public void onFailure(Call<ObjectResponse<String>> call, Throwable t) {
                if (t instanceof BusinessException) {
                    BusinessException ex = (BusinessException) t;
                    int errorCode = ex.getErrorCode();
                    String errorMessage = ex.getErrorMessage();
                    if (errorCode == ErrorCodeConstant.ErrorCodeCommon && errorMessage.contains("404")) {
                        finalCountDownLatch1.countDown();
                    }
                }
            }
        });
        countDownLatch.await(1, TimeUnit.SECONDS);

        // endregion

        // region 测试 HTTP 200 并且没有业务异常情况

        // 同步
        call = apiService.getWithObjectResponse();
        ObjectResponse<String> response = call.execute().body();
        Assert.assertEquals("调用成功", response.getData());
        Assert.assertEquals(0, response.getErrorCode());
        Assert.assertNull(response.getErrorMessage());

        // 异步回调
        countDownLatch = new CountDownLatch(1);
        call = apiService.getWithObjectResponse();
        CountDownLatch finalCountDownLatch = countDownLatch;
        call.enqueue(new Callback<ObjectResponse<String>>() {
            @Override
            public void onResponse(Call<ObjectResponse<String>> call, retrofit2.Response<ObjectResponse<String>> response) {
                ObjectResponse<String> body = response.body();
                if (body.getData().equals("成功调用") && body.getErrorCode() == 0 && body.getErrorMessage() == null) {
                    finalCountDownLatch.countDown();
                }
            }

            @Override
            public void onFailure(Call<ObjectResponse<String>> call, Throwable t) {

            }
        });
        countDownLatch.await(1, TimeUnit.SECONDS);

        // endregion

        // region 测试 HTTP 200 但业务异常情况：抛出 BusinessException

        // 同步
        try {
            call = apiService.postAndReturnWithBusinessException(null);
            call.execute().body();
            Assert.fail();
        } catch (BusinessException ex) {
            Assert.assertEquals(ErrorCodeConstant.ErrorCodeCommon, ex.getErrorCode());
            Assert.assertEquals("测试预期异常是否出现", ex.getErrorMessage());
        }

        // 异步回调
        countDownLatch = new CountDownLatch(1);
        call = apiService.postAndReturnWithBusinessException(null);
        CountDownLatch finalCountDownLatch2 = countDownLatch;
        call.enqueue(new Callback<ObjectResponse<String>>() {
            @Override
            public void onResponse(Call<ObjectResponse<String>> call, retrofit2.Response<ObjectResponse<String>> response) {

            }

            @Override
            public void onFailure(Call<ObjectResponse<String>> call, Throwable t) {
                if (t instanceof BusinessException) {
                    BusinessException ex = (BusinessException) t;
                    int errorCode = ex.getErrorCode();
                    String errorMessage = ex.getErrorMessage();
                    if (errorCode == ErrorCodeConstant.ErrorCodeCommon && errorMessage.equals("测试预期异常是否出现")) {
                        finalCountDownLatch2.countDown();
                    }
                }
            }
        });
        countDownLatch.await(1, TimeUnit.SECONDS);

        // endregion
    }
}
