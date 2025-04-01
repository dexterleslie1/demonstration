package com.future.demo.unify.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.Response;
import org.springframework.http.HttpHeaders;

public interface Api {

    @RequestLine("GET /api/v1/sms/captcha/send?phone={phone}")
    ObjectResponse<String> sendSms(@Param("phone") String phone) throws BusinessException;

    @RequestLine("POST /api/v1/sms/login")
    @Headers(value = {"Content-Type: application/x-www-form-urlencoded"})
    ObjectResponse<JsonNode> loginSms(@Param("phone") String phone,
                                      @Param("smsCaptcha") String smsCaptcha) throws BusinessException;

    @RequestLine("GET api/v1/user/info")
    @Headers(
            value = HttpHeaders.AUTHORIZATION + ": Bearer {token}"
    )
    ObjectResponse<String> getUserInfo(@Param("token") String token) throws BusinessException;

    @RequestLine("POST /api/v1/logout")
    @Headers(
            value = HttpHeaders.AUTHORIZATION + ": Bearer {token}"
    )
    ObjectResponse<String> logout(@Param("token") String token) throws BusinessException;

    @RequestLine("POST /api/v1/password/login")
    @Headers(value = {"Content-Type: application/x-www-form-urlencoded"})
    ObjectResponse<JsonNode> loginPassword(@Param("username") String username,
                                           @Param("password") String smsCaptcha,
                                           @Param("clientId") String clientId,
                                           @Param("captcha") String captcha) throws BusinessException;

    @RequestLine("GET /api/v1/password/captcha/get?clientId={clientId}")
    Response getCaptcha(@Param("clientId") String clientId) throws BusinessException;

    @RequestLine("GET /api/v1/user/test1")
    @Headers(
            value = HttpHeaders.AUTHORIZATION + ": Bearer {token}"
    )
    ObjectResponse<String> test1(@Param("token") String token) throws BusinessException;

    @RequestLine("GET /api/v1/user/test2")
    @Headers(
            value = HttpHeaders.AUTHORIZATION + ": Bearer {token}"
    )
    ObjectResponse<String> test2(@Param("token") String token) throws BusinessException;

    @RequestLine("GET /api/v1/user/test3")
    @Headers(
            value = HttpHeaders.AUTHORIZATION + ": Bearer {token}"
    )
    ObjectResponse<String> test3(@Param("token") String token) throws BusinessException;

}
