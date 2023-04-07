package com.future.demo.test;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.http.ResponseEntity;

public interface Api {
    @RequestLine("GET /api/v1/test1?param1={param1}")
    String test1(@Param("param1") String param1);

    @RequestLine("POST /api/v1/test2?param1={param1}")
    String test21(@Param("param1") String param1);

    @RequestLine("POST /api/v1/test2")
    String test22();

    @RequestLine("POST /api/v1/test3?param1={param1}")
    String test3(@Param("param1") String param1);

    @RequestLine("POST /api/v1/test5")
    @Headers( value = {
            "header1: {header1}",
            "header2: {header2}",

    })
    String test5(@Param("header1") String header1,
                 @Param("header2") String header2);

    /**
     * NOTE: 提供userId参数用于测试userId参数注入情况
     *
     * @param userId
     * @return
     */
    @RequestLine("POST /api/v1/test6?userId={userId}")
    String test61(@Param("userId") Long userId);

    /**
     * NOTE: 提供userId参数用于测试userId参数注入情况
     *
     * @param userId
     * @return
     */
    @RequestLine("POST /api/v1/test6")
    @Headers(value = {"Content-Type: application/x-www-form-urlencoded"})
    String test62(@Param("userId") Long userId);

    @RequestLine("GET obs-default-epu-555-33/2021-08-20/uuu-01.jpg")
    String getObject();

}
