package com.future.demo;

import com.yyd.common.exception.BusinessException;
import com.yyd.common.http.response.ObjectResponse;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.Map;

public interface Api {

    @RequestLine("POST /api/auth/login?username={username}&password={password}")
    ObjectResponse<Map<String, Object>> login(@Param("username") String username,
                                              @Param("password") String password) throws BusinessException;

    @RequestLine("GET /api/v1/test1")
    @Headers(value = {
            "Authorization: Bearer {token}"
    })
    ObjectResponse<String> test1(@Param("token") String token) throws BusinessException;

    @RequestLine("GET /api/v1/test2")
    @Headers(value = {
            "Authorization: Bearer {token}"
    })
    ObjectResponse<String> test2(@Param("token") String token) throws BusinessException;

    @RequestLine("GET /api/v1/test3")
    @Headers(value = {
            "Authorization: Bearer {token}"
    })
    ObjectResponse<String> test3(@Param("token") String token) throws BusinessException;

    @RequestLine("GET /api/v1/test5")
    @Headers(value = {
            "Authorization: Bearer {token}"
    })
    ObjectResponse<String> test5(@Param("token") String token) throws BusinessException;

    @RequestLine("GET /api/v1/test6")
    @Headers(value = {
            "Authorization: Bearer {token}"
    })
    ObjectResponse<String> test6(@Param("token") String token) throws BusinessException;

}
