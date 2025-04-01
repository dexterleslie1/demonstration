package com.future.demo.http;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface DeleteApiFeign {
    @RequestLine("DELETE /api/v1/testDeleteSubmitParamByUrl1?param1={param1}")
    String testDeleteSubmitParamByUrl1(@Param("param1") String param1);

    @RequestLine("DELETE /api/v1/testDeleteSubmitParamByUrl2?param1={param1}")
    String testDeleteSubmitParamByUrl2(@Param("param1") String param1);

    @RequestLine("DELETE /api/v1/testDeleteSubmitParamByJSON")
    @Headers(value = {"Content-Type: application/json"})
    String testDeleteSubmitParamByJSON(FormDataDTO formDataDTO);
}
