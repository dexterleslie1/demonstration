package com.future.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yyd.common.exception.BusinessException;
import com.yyd.common.http.response.ObjectResponse;
import com.yyd.common.json.JSONUtil;
import feign.*;
import feign.codec.ErrorDecoder;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Tests {

    @LocalServerPort
    int localServerPort;

    Api api;

    @Before
    public void before() {
        api = Feign.builder()
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
                .target(Api.class, "http://localhost:" + localServerPort);
    }

    /**
     * 测试@Secured注解
     *
     * @throws BusinessException
     */
    @Test
    public void testAnnotationSecured() throws BusinessException {
        String password = "123456";

        // 测试同时拥有r1和r2角色
        String username = "user-with-role-r1-and-role-r2";
        ObjectResponse<Map<String, Object>> response = api.login(username, password);
        String token = (String) response.getData().get("token");
        ObjectResponse<String> response1 = api.test1(token);
        Assert.assertEquals("成功调用接口/api/v1/test1", response1.getData());

        // 测试只有r1角色
        username = "user-with-only-role-r1";
        response = api.login(username, password);
        token = (String) response.getData().get("token");
        response1 = api.test1(token);
        Assert.assertEquals("成功调用接口/api/v1/test1", response1.getData());

        // 测试只有r2角色
        username = "user-with-only-role-r2";
        response = api.login(username, password);
        token = (String) response.getData().get("token");
        response1 = api.test1(token);
        Assert.assertEquals("成功调用接口/api/v1/test1", response1.getData());

        // 测试没有任何角色
        username = "user-with-none-role";
        response = api.login(username, password);
        token = (String) response.getData().get("token");
        try {
            api.test1(token);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals("权限不足", ex.getErrorMessage());
        }
    }

    /**
     * 测试@PreAuthorize注解用于角色
     *
     * @throws BusinessException
     */
    @Test
    public void testAnnotationPreAuthorizeForRole() throws BusinessException {
        String password = "123456";

        // 测试同时拥有r1和r2角色
        String username = "user-with-role-r1-and-role-r2";
        ObjectResponse<Map<String, Object>> response = api.login(username, password);
        String token = (String) response.getData().get("token");
        ObjectResponse<String> response1 = api.test2(token);
        Assert.assertEquals("成功调用接口/api/v1/test2", response1.getData());
        // 测试只有r1角色
        username = "user-with-only-role-r1";
        response = api.login(username, password);
        token = (String) response.getData().get("token");
        response1 = api.test2(token);
        Assert.assertEquals("成功调用接口/api/v1/test2", response1.getData());
        // 测试只有r2角色
        username = "user-with-only-role-r2";
        response = api.login(username, password);
        token = (String) response.getData().get("token");
        response1 = api.test2(token);
        Assert.assertEquals("成功调用接口/api/v1/test2", response1.getData());
        // 测试没有任何角色
        username = "user-with-none-role";
        response = api.login(username, password);
        token = (String) response.getData().get("token");
        try {
            api.test2(token);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals("权限不足", ex.getErrorMessage());
        }

        // 测试同时拥有r1和r2角色
        username = "user-with-role-r1-and-role-r2";
        response = api.login(username, password);
        token = (String) response.getData().get("token");
        response1 = api.test3(token);
        Assert.assertEquals("成功调用接口/api/v1/test3", response1.getData());
        // 测试只有r1角色
        username = "user-with-only-role-r1";
        response = api.login(username, password);
        token = (String) response.getData().get("token");
        try {
            api.test3(token);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals("权限不足", ex.getErrorMessage());
        }
        // 测试只有r2角色
        username = "user-with-only-role-r2";
        response = api.login(username, password);
        token = (String) response.getData().get("token");
        try {
            api.test3(token);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals("权限不足", ex.getErrorMessage());
        }
        // 测试没有任何角色
        username = "user-with-none-role";
        response = api.login(username, password);
        token = (String) response.getData().get("token");
        try {
            api.test3(token);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals("权限不足", ex.getErrorMessage());
        }

        // 测试拥有权限 perm:test5
        username = "user-with-perm-test5";
        response = api.login(username, password);
        token = (String) response.getData().get("token");
        response1 = api.test5(token);
        Assert.assertEquals("成功调用接口/api/v1/test5", response1.getData());
        // 测试没有权限 perm:test5
        username = "user-with-role-r1";
        response = api.login(username, password);
        token = (String) response.getData().get("token");
        try {
            api.test5(token);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals("权限不足", ex.getErrorMessage());
        }

        // 测试自定义权限验证拥有 /api/v1/test6
        username = "user-with-allow-uri-test6";
        response = api.login(username, password);
        token = (String) response.getData().get("token");
        response1 = api.test6(token);
        Assert.assertEquals("成功调用接口/api/v1/test6", response1.getData());
        // 测试自定义权限验证没有 /api/v1/test6
        username = "user-with-role-r1";
        response = api.login(username, password);
        token = (String) response.getData().get("token");
        try {
            api.test6(token);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals("权限不足", ex.getErrorMessage());
        }
    }

}
