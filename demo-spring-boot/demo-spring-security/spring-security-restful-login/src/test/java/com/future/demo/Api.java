package com.future.demo;

import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.Map;

public interface Api {

    @RequestLine("POST /api/auth/login?username={username}&password={password}")
    ObjectResponse<Map<String, Object>> login(@Param("username") String username,
                                              @Param("password") String password) throws BusinessException;

    @RequestLine("GET /api/auth/a1")
    @Headers(value = {
            "Authorization: Bearer {token}"
    })
    ObjectResponse<String> a1(@Param("token") String token) throws BusinessException;

    @RequestLine("GET /api/auth/a2")
    @Headers(value = {
            "Authorization: Bearer {token}"
    })
    ObjectResponse<String> a2(@Param("token") String token) throws BusinessException;

}
