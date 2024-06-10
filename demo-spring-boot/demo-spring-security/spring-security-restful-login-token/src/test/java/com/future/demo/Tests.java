package com.future.demo;


import com.fasterxml.jackson.core.type.TypeReference;
import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import com.future.common.json.JSONUtil;
import feign.*;
import feign.codec.ErrorDecoder;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Tests {
    @LocalServerPort
    int localServerPort;

    @Test
    public void test() throws BusinessException {
        String host = "localhost";
        int port = localServerPort;

        Api api = Feign.builder()
                // https://stackoverflow.com/questions/56987701/feign-client-retry-on-exception
                .retryer(Retryer.NEVER_RETRY)
                // https://qsli.github.io/2020/04/28/feign-method-timeout/
                .options(new Request.Options(15, TimeUnit.SECONDS, 15, TimeUnit.SECONDS, false))
                .encoder(new FormEncoder(new JacksonEncoder()))
                .decoder(new JacksonDecoder())
                // feign logger
                // https://cloud.tencent.com/developer/article/1588501
                .logger(new Logger.ErrorLogger()).logLevel(Logger.Level.FULL)
                // ErrorDecoder
                // https://cloud.tencent.com/developer/article/1588501
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
                .target(Api.class, "http://" + host + ":" + port);

        // 测试未登录
        try {
            api.a1(UUID.randomUUID().toString());
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals("您未登陆", ex.getErrorMessage());
        }

        // 测试登录
        String username = "admin";
        String password = "1234567";
        try {
            api.login(username, password);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals("Bad credentials", ex.getErrorMessage());
        }
        password = "123456";
        ObjectResponse<Map<String, Object>> response = api.login(username, password);
        Assert.assertEquals(4738438, response.getData().get("userId"));
        Assert.assertEquals(username, response.getData().get("loginname"));
        Assert.assertNotNull(response.getData().get("token"));
        Long userId = ((Integer) response.getData().get("userId")).longValue();

        // 测试调用接口
        String token = (String) response.getData().get("token");
        ObjectResponse<String> response1 = api.a1(token);
        Assert.assertEquals("成功调用接口/api/auth/a1，登录用户 " + userId, response1.getData());
        try {
            // 访问接口 /api/v1/a2 需要 USER1 角色
            api.a2(token);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals("权限不足", ex.getErrorMessage());
        }

        try {
            // 随便提供不存在token模拟未登录
            String randomToken = UUID.randomUUID().toString();
            api.a2(randomToken);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals("您未登陆", ex.getErrorMessage());
        }

        // 测试退出登录
        Assert.assertEquals("成功退出", api.logout(token).getData());
        try {
            api.a1(token);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals("您未登陆", ex.getErrorMessage());
        }
    }
}
