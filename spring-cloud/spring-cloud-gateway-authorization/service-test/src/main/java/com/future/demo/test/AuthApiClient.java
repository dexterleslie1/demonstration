package com.future.demo.test;

import com.future.demo.common.vo.LoginSuccessVo;
import com.future.demo.common.vo.RefreshTokenVo;
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
    @Headers(value = {"Authorization: Bearer {accessToken}"})
    ObjectResponse<String> adminTest1(@Param("accessToken") String accessToken) throws BusinessException;

    @RequestLine("GET /api/v1/admin/test2")
    @Headers(value = {"Authorization: Bearer {accessToken}"})
    ObjectResponse<String> adminTest2(@Param("accessToken") String accessToken) throws BusinessException;

    @RequestLine("GET /api/v1/nuser/test1")
    @Headers(value = {"Authorization: Bearer {accessToken}"})
    ObjectResponse<String> nuserTest1(@Param("accessToken") String accessToken) throws BusinessException;

    @RequestLine("GET /api/v1/nuser/test2")
    @Headers(value = {"Authorization: Bearer {accessToken}"})
    ObjectResponse<String> nuserTest2(@Param("accessToken") String accessToken) throws BusinessException;

    @RequestLine("POST /api/v1/auth/refreshToken")
    @Headers(value = {"Authorization: Bearer {refreshToken}"})
    ObjectResponse<RefreshTokenVo> refreshToken(@Param("refreshToken") String refreshToken) throws BusinessException;

}
