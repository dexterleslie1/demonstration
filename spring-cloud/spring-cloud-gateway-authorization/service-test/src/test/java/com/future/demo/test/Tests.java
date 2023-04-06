package com.future.demo.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.future.demo.common.vo.LoginSuccessVo;
import com.yyd.common.exception.BusinessException;
import com.yyd.common.http.response.ObjectResponse;
import com.yyd.common.json.JSONUtil;
import feign.*;
import feign.codec.ErrorDecoder;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Tests {
    @Test
    public void test() throws BusinessException {
        AuthApiClient authApiClient = Feign.builder()
                .retryer(Retryer.NEVER_RETRY)
                .options(new Request.Options(15, TimeUnit.SECONDS, 15, TimeUnit.SECONDS, false))
                .encoder(new FormEncoder(new JacksonEncoder()))
                .decoder(new JacksonDecoder())
                .logger(new Logger.ErrorLogger()).logLevel(Logger.Level.NONE)
                .errorDecoder(new ErrorDecoder() {
                    @Override
                    public Exception decode(String methodKey, Response response) {
                        try {
                            String json = IOUtils.toString(response.body().asInputStream(), StandardCharsets.UTF_8);
                            ObjectResponse<String> responseError = JSONUtil.ObjectMapperInstance.readValue(json, new TypeReference<ObjectResponse<String>>() {
                            });
                            return new BusinessException(responseError.getErrorCode(), responseError.getErrorMessage());
                        } catch (IOException e) {
                            return e;
                        }
                    }
                })
                .target(AuthApiClient.class, "http://localhost:8080");

        // 身份认证测试
        String username = "admin111";
        String password = "123456";
        try {
            authApiClient.loginWithPassword(username, password);
            Assert.fail("没有抛出预期异常");
        } catch (BusinessException ex) {
            Assert.assertEquals("帐号密码错误！", ex.getErrorMessage());
        }

        username = "admin";
        ObjectResponse<LoginSuccessVo> response = authApiClient.loginWithPassword(username, password);
        Assert.assertEquals(new Long(1L), response.getData().getUserId());
        Assert.assertEquals(username, response.getData().getLoginName());
        Assert.assertFalse(StringUtils.isBlank(response.getData().getToken()));
        Assert.assertNull(response.getData().getMenuList());
        Assert.assertNull(response.getData().getPermissionList());

        username = "user1";
        response = authApiClient.loginWithPassword(username, password);
        Assert.assertEquals(new Long(2L), response.getData().getUserId());
        Assert.assertEquals(username, response.getData().getLoginName());
        Assert.assertFalse(StringUtils.isBlank(response.getData().getToken()));
        Assert.assertEquals(1, response.getData().getMenuList().size());
        Assert.assertEquals("menu1", response.getData().getMenuList().get(0));
        Assert.assertEquals(1, response.getData().getPermissionList().size());
        Assert.assertEquals("nuser:fun1", response.getData().getPermissionList().get(0));

        // 授权权限测试
        String token = UUID.randomUUID().toString();
        try {
            authApiClient.adminTest1(token);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals("未登陆", ex.getMessage());
        }

        username = "admin";
        response = authApiClient.loginWithPassword(username, password);
        token = response.getData().getToken();
        ObjectResponse<String> response1 = authApiClient.adminTest1(token);
        Assert.assertEquals("成功调用接口 /api/v1/admin/test1", response1.getData());

        try {
            authApiClient.adminTest2(token);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals("调用接口[uri=/api/v1/admin/test2]权限不足！", ex.getErrorMessage());
        }
        try {
            authApiClient.nuserTest1(token);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals("调用接口[uri=/api/v1/nuser/test1]权限不足！", ex.getErrorMessage());
        }
        try {
            authApiClient.nuserTest2(token);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals("调用接口[uri=/api/v1/nuser/test2]权限不足！", ex.getErrorMessage());
        }

        username = "user1";
        response = authApiClient.loginWithPassword(username, password);
        token = response.getData().getToken();
        response1 = authApiClient.nuserTest1(token);
        Assert.assertEquals("成功调用接口 /api/v1/nuser/test1", response1.getData());

        try {
            authApiClient.nuserTest2(token);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals("调用接口[uri=/api/v1/nuser/test2]权限不足！", ex.getErrorMessage());
        }
        try {
            authApiClient.adminTest1(token);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals("调用接口[uri=/api/v1/admin/test1]权限不足！", ex.getErrorMessage());
        }
        try {
            authApiClient.adminTest2(token);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals("调用接口[uri=/api/v1/admin/test2]权限不足！", ex.getErrorMessage());
        }
    }
}
