package com.future.demo.http;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface PutApiFeign {
    @RequestLine("PUT /api/v1/testPutSubmitParamByUrl1?param1={param1}")
    String testPutSubmitParamByUrl1(@Param("param1") String param1);

    @RequestLine("PUT /api/v1/testPutSubmitParamByFormUrlencoded1")
    @Headers(value = {"Content-Type: application/x-www-form-urlencoded"})
    String testPutSubmitParamByFormUrlencoded1(@Param("param1") String param1);

    @RequestLine("PUT /api/v1/testPutSubmitParamByFormUrlencoded2")
    @Headers(value = {"Content-Type: application/x-www-form-urlencoded"})
    String testPutSubmitParamByFormUrlencoded2(@Param("param1") String param1);

    @RequestLine("PUT /api/v1/testPutSubmitParamByJSON")
    @Headers(value = {"Content-Type: application/json"})
    String testPutSubmitParamByJSON(FormDataDTO formDataDTO);
}
