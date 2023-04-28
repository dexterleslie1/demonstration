package com.future.demo.test;

import feign.Param;
import feign.RequestLine;

public interface Api {

    @RequestLine("GET /api/v1/test1?param1={param1}")
    String test1(@Param("param1") String param1);

    @RequestLine("GET /api/v1/test2?param1={param1}")
    String test2(@Param("param1") String param1);

}
