package com.future.demo.unify.gateway;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.future.common.constant.ErrorCodeConstant;
import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import com.future.common.json.JSONUtil;
import feign.*;
import feign.codec.ErrorDecoder;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class IntegrationTests {
    @LocalServerPort
    int localServerPort;

    @Autowired
    CacheManager cacheManager;
    Cache cacheSmsCaptcha;

    @PostConstruct
    public void init1() throws Exception {
        this.cacheSmsCaptcha = this.cacheManager.getCache("cacheSmsCaptcha");
    }

    @Test
    public void test() throws BusinessException {
        String host = "localhost";
        int port = localServerPort;

        Api api = Feign.builder()
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
                .target(Api.class, "http://" + host + ":" + port);

        /*
        测试发送短信验证码
         */
        // 测试手机号码格式错误
        String phone = "13511111111";
        try {
            api.sendSms(phone);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals(ErrorCodeConstant.ErrorCodeCommon, ex.getErrorCode());
            Assert.assertEquals("号码=" + phone + "格式错误，必需为E.164格式：+[国家代号][手机号码]，例如：+8613512345678", ex.getErrorMessage());
        }

        // 测试手机号码正确
        phone = "+8613511111111";
        ObjectResponse<String> responseStr = api.sendSms(phone);
        Assert.assertEquals("短信验证码已发送", responseStr.getData());
        Element element = this.cacheSmsCaptcha.get(phone);
        Assert.assertEquals("111111", element.getObjectValue());
        this.cacheSmsCaptcha.remove(phone);

        /*
        测试使用手机号码+短信验证码登录
         */
        // 未提供短信验证码
        phone = "+8613511111111";
        try {
            api.loginSms(phone, StringUtils.EMPTY);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals(ErrorCodeConstant.ErrorCodeCommon, ex.getErrorCode());
            Assert.assertEquals("没有提供短信验证码", ex.getErrorMessage());
        }

        // 短信验证码已过期，请重新获取
        phone = "+8613511111111";
        String smsCaptcha = "1234567890";
        try {
            api.loginSms(phone, smsCaptcha);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals(ErrorCodeConstant.ErrorCodeCommon, ex.getErrorCode());
            Assert.assertEquals("短信验证码已过期，请重新获取", ex.getErrorMessage());
        }

        // 提供的短信验证码错误
        phone = "+8613511111111";
        responseStr = api.sendSms(phone);
        Assert.assertEquals("短信验证码已发送", responseStr.getData());

        smsCaptcha = "1234567890";
        try {
            api.loginSms(phone, smsCaptcha);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals(ErrorCodeConstant.ErrorCodeCommon, ex.getErrorCode());
            Assert.assertEquals("提供的短信验证码错误", ex.getErrorMessage());
        }

        // 正常情况
        smsCaptcha = "111111";
        ObjectResponse<JsonNode> responseJsonNode = api.loginSms(phone, smsCaptcha);
        Assert.assertEquals(phone, responseJsonNode.getData().get("username").asText());
//        Assert.assertEquals(4, responseJsonNode.getData().get("loginType").asInt());
        String token = responseJsonNode.getData().get("token").asText();

        responseStr = api.getUserInfo(token);
        Assert.assertEquals(phone, responseStr.getData());

        /*
        测试未登录情况
         */
        responseStr = api.logout(token);
        Assert.assertEquals("成功退出", responseStr.getData());
        try {
            api.getUserInfo(token);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals(ErrorCodeConstant.ErrorCodeLoginRequired, ex.getErrorCode());
            Assert.assertEquals("未登录", ex.getErrorMessage());
        }

        /*
        测试用户名、手机号码、邮箱+密码登录
         */
        // 测试帐号密码错误
        String username = "user1";
        try {
            api.loginPassword(username, "xxxxxx", StringUtils.EMPTY, StringUtils.EMPTY);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals(ErrorCodeConstant.ErrorCodeCommon, ex.getErrorCode());
            Assert.assertEquals("用户名或者密码错误", ex.getErrorMessage());
        }

        // 测试登录成功loginType
        responseJsonNode = api.loginPassword(username, "123456", StringUtils.EMPTY, StringUtils.EMPTY);
        Assert.assertEquals(username, responseJsonNode.getData().get("username").asText());
//        Assert.assertEquals(1, responseJsonNode.getData().get("loginType").asInt());
        token = responseJsonNode.getData().get("token").asText();
        responseStr = api.getUserInfo(token);
        Assert.assertEquals(username, responseStr.getData());

        username = "13511111111";
        responseJsonNode = api.loginPassword(username, "123456", StringUtils.EMPTY, StringUtils.EMPTY);
        Assert.assertEquals(username, responseJsonNode.getData().get("username").asText());
//        Assert.assertEquals(2, responseJsonNode.getData().get("loginType").asInt());
        token = responseJsonNode.getData().get("token").asText();
        responseStr = api.getUserInfo(token);
        Assert.assertEquals(username, responseStr.getData());

        username = "+8613511111111";
        responseJsonNode = api.loginPassword(username, "123456", StringUtils.EMPTY, StringUtils.EMPTY);
        Assert.assertEquals(username, responseJsonNode.getData().get("username").asText());
//        Assert.assertEquals(2, responseJsonNode.getData().get("loginType").asInt());
        token = responseJsonNode.getData().get("token").asText();
        responseStr = api.getUserInfo(token);
        Assert.assertEquals(username, responseStr.getData());

        username = "dexterleslie@gmail.com";
        responseJsonNode = api.loginPassword(username, "123456", StringUtils.EMPTY, StringUtils.EMPTY);
        Assert.assertEquals(username, responseJsonNode.getData().get("username").asText());
//        Assert.assertEquals(3, responseJsonNode.getData().get("loginType").asInt());
        token = responseJsonNode.getData().get("token").asText();
        responseStr = api.getUserInfo(token);
        Assert.assertEquals(username, responseStr.getData());

        responseStr = api.logout(token);
        Assert.assertEquals("成功退出", responseStr.getData());

        // 测试连续5次不提供账号和密码后BusinessException#errorCode=50001
        for (int i = 0; i < 4; i++) {
            try {
                api.loginPassword(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
                Assert.fail("预期异常没有抛出");
            } catch (BusinessException ex) {
                Assert.assertEquals(ErrorCodeConstant.ErrorCodeCommon, ex.getErrorCode());
                Assert.assertEquals("没有指定用户名、手机号码、邮箱至少一项参数", ex.getErrorMessage());
            }
        }
        try {
            api.loginPassword(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals(50001, ex.getErrorCode());
            Assert.assertEquals("没有指定用户名、手机号码、邮箱至少一项参数", ex.getErrorMessage());
        }

        // 尝试多次登录失败后需要提供登录验证码
        try {
            api.loginPassword("usertest1", "123456", UUID.randomUUID().toString(), "111111");
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals(50001, ex.getErrorCode());
            Assert.assertEquals("登录验证码错误", ex.getErrorMessage());
        }

        // 测试验证码登录
        String clientId = UUID.randomUUID().toString();
        Response response = api.getCaptcha(clientId);
        Assert.assertEquals(HttpStatus.OK.value(), response.status());
        Assert.assertTrue(response.body().length() > 0);

        username = "user1";
        responseJsonNode = api.loginPassword(username, "123456", clientId, "111111");
        Assert.assertEquals(username, responseJsonNode.getData().get("username").asText());
//        Assert.assertEquals(1, responseJsonNode.getData().get("loginType").asInt());
        token = responseJsonNode.getData().get("token").asText();

        responseStr = api.getUserInfo(token);
        Assert.assertEquals(username, responseStr.getData());

        // 测试未登录
        try {
            api.test1(UUID.randomUUID().toString());
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals(ErrorCodeConstant.ErrorCodeLoginRequired, ex.getErrorCode());
            Assert.assertEquals("未登录", ex.getErrorMessage());
        }

        // 测试权限
        responseStr = api.test1(token);
        Assert.assertEquals("成功调用接口 /api/v1/user/test1", responseStr.getData());

        responseStr = api.test2(token);
        Assert.assertEquals("成功调用接口 /api/v1/user/test2", responseStr.getData());

        try {
            api.test3(token);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals(ErrorCodeConstant.ErrorCodeCommon, ex.getErrorCode());
            Assert.assertEquals("权限不足", ex.getErrorMessage());
        }
    }
}
