package com.future.demo.http;

import com.yyd.common.http.response.ObjectResponse;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.Response;

import java.io.File;

public interface PostApiFeign {
    @RequestLine("POST /api/v1/testPostSubmitParamByUrl1?param1={param1}")
    String testPostSubmitParamByUrl1(@Param("param1") String param1);

    @RequestLine("POST /api/v1/testPostSubmitParamByFormUrlencoded1")
    @Headers(value = {"Content-Type: application/x-www-form-urlencoded"})
    String testPostSubmitParamByFormUrlencoded1(@Param("param1") String param1);

    @RequestLine("POST /api/v1/testPostSubmitParamByFormUrlencoded2")
    @Headers(value = {"Content-Type: application/x-www-form-urlencoded"})
    String testPostSubmitParamByFormUrlencoded2(@Param("param1") String param1);

    @RequestLine("POST /api/v1/testPostSubmitParamByMultipartFormData")
    @Headers("Content-Type: multipart/form-data")
    String testPostSubmitParamByMultipartFormData(@Param("param1") String param1);

    @RequestLine("POST /api/v1/testPostSubmitParamByMultipartFormData2")
    @Headers("Content-Type: multipart/form-data")
    String testPostSubmitParamByMultipartFormData2(@Param("param1") String param1);

    @RequestLine("POST /api/v1/testPostSubmitParamByJSON")
    @Headers(value = {"Content-Type: application/json"})
    String testPostSubmitParamByJSON(FormDataDTO formDataDTO);

    @RequestLine("POST /api/v1/testPostAndResponseWithString?name={name}")
    String testPostAndResponseWithString(@Param("name") String name);

    @RequestLine("POST /api/v1/testPostAndResponseWithJSONObject?name={name}")
    String testPostAndResponseWithJSONObject(@Param("name") String name);

    @RequestLine("POST /api/v1/testPostAndResponseWithException?name={name}")
    String testPostAndResponseWithException(@Param("name") String name);

    @RequestLine("POST /api/v1/testPostAndResponseWithJSONArray?name={name}")
    String testPostAndResponseWithJSONArray(@Param("name") String name);

    @RequestLine("POST /api/v1/upload?name={name}")
    @Headers("Content-Type: multipart/form-data")
    String upload(@Param("name") String name, @Param("file1") File file);

    @RequestLine("POST /api/v1/download?filename={filename}")
    Response download(@Param("filename") String filename);
}
