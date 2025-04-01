package com.future.demo.http;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface GetApiFeign {
    @RequestLine("GET /api/v1/testGetSubmitParamByUrl1?param1={param1}")
    String testGetSubmitParamByUrl1(@Param("param1") String param1);

    @RequestLine("GET /api/v1/testGetSubmitParamByUrl2?param1={param1}")
    String testGetSubmitParamByUrl2(@Param("param1") String param1);

    @RequestLine("GET /api/v1/testGetSubmitParamByJSON")
    @Headers(value = {"Content-Type: application/json"})
    String testGetSubmitParamByJSON(FormDataDTO formDataDTO);
}
