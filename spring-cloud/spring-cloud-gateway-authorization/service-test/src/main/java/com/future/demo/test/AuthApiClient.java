package com.future.demo.test;

import com.future.demo.common.vo.LoginSuccessVo;
import com.yyd.common.exception.BusinessException;
import com.yyd.common.http.response.ObjectResponse;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface AuthApiClient {
    @RequestLine("POST /api/v1/auth/loginWithPassword")
    @Headers(value = {"Content-Type: application/x-www-form-urlencoded"})
    ObjectResponse<LoginSuccessVo> loginWithPassword(@Param("username") String username,
                                                     @Param("password") String password) throws BusinessException;

    @RequestLine("GET /api/v1/admin/test1")
    @Headers(value = {"Authorization: Bearer {token}"})
    ObjectResponse<String> adminTest1(@Param("token") String token) throws BusinessException;

    @RequestLine("GET /api/v1/admin/test2")
    @Headers(value = {"Authorization: Bearer {token}"})
    ObjectResponse<String> adminTest2(@Param("token") String token) throws BusinessException;

    @RequestLine("GET /api/v1/nuser/test1")
    @Headers(value = {"Authorization: Bearer {token}"})
    ObjectResponse<String> nuserTest1(@Param("token") String token) throws BusinessException;

    @RequestLine("GET /api/v1/nuser/test2")
    @Headers(value = {"Authorization: Bearer {token}"})
    ObjectResponse<String> nuserTest2(@Param("token") String token) throws BusinessException;

}
