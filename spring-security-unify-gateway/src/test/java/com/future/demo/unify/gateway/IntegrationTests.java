package com.future.demo.unify.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import com.yyd.common.exception.BusinessException;
import com.yyd.common.http.response.ObjectResponse;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTests {
    @LocalServerPort
    int localServerPort;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CacheManager cacheManager;
    Cache cacheSmsCaptcha;

    @PostConstruct
    public void init1() {
        this.cacheSmsCaptcha = this.cacheManager.getCache("cacheSmsCaptcha");
    }

    @Test
    public void test() throws UnsupportedEncodingException {
        /*
        测试发送短信验证码
         */
        // 测试手机号码格式错误
        String phone = "13511111111";
        String url = "http://localhost:" + localServerPort + "/api/v1/sms/captcha/send?phone={phone}";
        ResponseEntity<ObjectResponse<String>> responseEntity = null;
        try {
            restTemplate.exchange(url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ObjectResponse<String>>() {
                    },
                    URLEncoder.encode(phone, StandardCharsets.UTF_8.name()));
            Assert.fail("预期异常没有抛出");
        } catch (ResourceAccessException ex) {
            BusinessException businessException = (BusinessException)ex.getCause();
            Assert.assertEquals(600, businessException.getErrorCode());
            Assert.assertEquals("号码=" + phone + "格式错误，必需为E.164格式：+[国家代号][手机号码]，例如：+8613512345678", businessException.getErrorMessage());
        }

        // 测试手机号码正确
        phone = "+8613511111111";
        responseEntity =
                restTemplate.exchange(url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<ObjectResponse<String>>() {},
                        URLEncoder.encode(phone, StandardCharsets.UTF_8.name()));
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(0, responseEntity.getBody().getErrorCode());
        Assert.assertEquals("短信验证码已发送", responseEntity.getBody().getData());

        Element element = this.cacheSmsCaptcha.get(phone);
        Assert.assertEquals("111111", element.getObjectValue());
        this.cacheSmsCaptcha.remove(phone);

        /*
        测试使用手机号码+短信验证码登录
         */
        // 未提供短信验证码
        phone = "+8613511111111";
        url = "http://localhost:" + localServerPort + "/api/v1/sms/login";
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("phone", phone);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(params, null);
        try {
            restTemplate.exchange(url,
                    HttpMethod.POST,
                    httpEntity,
                    new ParameterizedTypeReference<ObjectResponse<String>>() {
                    });
            Assert.fail("预期异常没有抛出");
        } catch (ResourceAccessException ex) {
            BusinessException businessException = (BusinessException)ex.getCause();
            Assert.assertEquals(600, businessException.getErrorCode());
            Assert.assertEquals("没有提供短信验证码", businessException.getErrorMessage());
        }

        // 短信验证码已过期，请重新获取
        phone = "+8613511111111";
        String smsCaptcha = "1234567890";
        url = "http://localhost:" + localServerPort + "/api/v1/sms/login";
        params = new LinkedMultiValueMap<>();
        params.add("phone", phone);
        params.add("smsCaptcha", smsCaptcha);
        httpEntity = new HttpEntity<>(params, null);
        try {
            restTemplate.exchange(url,
                    HttpMethod.POST,
                    httpEntity,
                    new ParameterizedTypeReference<ObjectResponse<String>>() {
                    });
            Assert.fail("预期异常没有抛出");
        } catch (ResourceAccessException ex) {
            BusinessException businessException = (BusinessException)ex.getCause();
            Assert.assertEquals(600, businessException.getErrorCode());
            Assert.assertEquals("短信验证码已过期，请重新获取", businessException.getErrorMessage());
        }

        // 提供的短信验证码错误
        phone = "+8613511111111";
        url = "http://localhost:" + localServerPort + "/api/v1/sms/captcha/send?phone={phone}";
        responseEntity =
                restTemplate.exchange(url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<ObjectResponse<String>>() {},
                        URLEncoder.encode(phone, StandardCharsets.UTF_8.name()));
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(0, responseEntity.getBody().getErrorCode());
        Assert.assertEquals("短信验证码已发送", responseEntity.getBody().getData());

        smsCaptcha = "1234567890";
        url = "http://localhost:" + localServerPort + "/api/v1/sms/login";
        params = new LinkedMultiValueMap<>();
        params.add("phone", phone);
        params.add("smsCaptcha", smsCaptcha);
        httpEntity = new HttpEntity<>(params, null);
        try {
            restTemplate.exchange(url,
                    HttpMethod.POST,
                    httpEntity,
                    new ParameterizedTypeReference<ObjectResponse<String>>() {
                    });
            Assert.fail("预期异常没有抛出");
        } catch (ResourceAccessException ex) {
            BusinessException businessException = (BusinessException)ex.getCause();
            Assert.assertEquals(600, businessException.getErrorCode());
            Assert.assertEquals("提供的短信验证码错误", businessException.getErrorMessage());
        }

        // 正常情况
        smsCaptcha = "111111";
        url = "http://localhost:" + localServerPort + "/api/v1/sms/login";
        params = new LinkedMultiValueMap<>();
        params.add("phone", phone);
        params.add("smsCaptcha", smsCaptcha);
        httpEntity = new HttpEntity<>(params, null);
        responseEntity =
                restTemplate.exchange(url,
                        HttpMethod.POST,
                        httpEntity,
                        new ParameterizedTypeReference<ObjectResponse<String>>() {});
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(phone, responseEntity.getBody().getData());

        url = "http://localhost:" + localServerPort + "/api/v1/user/info";
        responseEntity =
                restTemplate.exchange(url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<ObjectResponse<String>>() {});
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(phone, responseEntity.getBody().getData());

        /*
        测试未登录情况
         */
        url = "http://localhost:" + localServerPort + "/api/v1/logout";
        responseEntity = restTemplate.exchange(url, HttpMethod.POST, null, new ParameterizedTypeReference<ObjectResponse<String>>() {
        });
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals("成功退出", responseEntity.getBody().getData());
        try {
            url = "http://localhost:" + localServerPort + "/api/v1/user/info";
            responseEntity =
                    restTemplate.exchange(url,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<ObjectResponse<String>>() {
                            });
            Assert.fail("预期异常没有抛出");
        } catch (ResourceAccessException ex) {
            BusinessException businessException = (BusinessException)ex.getCause();
            Assert.assertEquals(50003, businessException.getErrorCode());
            Assert.assertEquals("您未登录", businessException.getErrorMessage());
        }

        /*
        测试用户名、手机号码、邮箱+密码登录
         */
        // 测试登录成功loginType
        url = "http://localhost:" + localServerPort + "/api/v1/password/login";
        String username = "user1";
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("username", username);
        multiValueMap.add("password", "123456");
        httpEntity = new HttpEntity<>(multiValueMap, null);
        ResponseEntity<ObjectResponse<JsonNode>> responseEntityJsonNode =
                restTemplate.exchange(url, HttpMethod.POST, httpEntity, new ParameterizedTypeReference<ObjectResponse<JsonNode>>() {});
        Assert.assertEquals(HttpStatus.OK, responseEntityJsonNode.getStatusCode());
        Assert.assertEquals(username, responseEntityJsonNode.getBody().getData().get("username").asText());
        Assert.assertEquals(1, responseEntityJsonNode.getBody().getData().get("loginType").asInt());
        url = "http://localhost:" + localServerPort + "/api/v1/user/info";
        responseEntity =
                restTemplate.exchange(url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<ObjectResponse<String>>() {});
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(username, responseEntity.getBody().getData());

        url = "http://localhost:" + localServerPort + "/api/v1/password/login";
        username = "13511111111";
        multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("username", username);
        multiValueMap.add("password", "123456");
        httpEntity = new HttpEntity<>(multiValueMap, null);
        responseEntityJsonNode =
                restTemplate.exchange(url, HttpMethod.POST, httpEntity, new ParameterizedTypeReference<ObjectResponse<JsonNode>>() {});
        Assert.assertEquals(HttpStatus.OK, responseEntityJsonNode.getStatusCode());
        Assert.assertEquals(username, responseEntityJsonNode.getBody().getData().get("username").asText());
        Assert.assertEquals(2, responseEntityJsonNode.getBody().getData().get("loginType").asInt());
        url = "http://localhost:" + localServerPort + "/api/v1/user/info";
        responseEntity =
                restTemplate.exchange(url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<ObjectResponse<String>>() {});
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(username, responseEntity.getBody().getData());

        url = "http://localhost:" + localServerPort + "/api/v1/password/login";
        username = "+8613511111111";
        multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("username", username);
        multiValueMap.add("password", "123456");
        httpEntity = new HttpEntity<>(multiValueMap, null);
        responseEntityJsonNode =
                restTemplate.exchange(url, HttpMethod.POST, httpEntity, new ParameterizedTypeReference<ObjectResponse<JsonNode>>() {});
        Assert.assertEquals(HttpStatus.OK, responseEntityJsonNode.getStatusCode());
        Assert.assertEquals(username, responseEntityJsonNode.getBody().getData().get("username").asText());
        Assert.assertEquals(2, responseEntityJsonNode.getBody().getData().get("loginType").asInt());
        url = "http://localhost:" + localServerPort + "/api/v1/user/info";
        responseEntity =
                restTemplate.exchange(url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<ObjectResponse<String>>() {});
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(username, responseEntity.getBody().getData());

        url = "http://localhost:" + localServerPort + "/api/v1/password/login";
        username = "dexterleslie@gmail.com";
        multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("username", username);
        multiValueMap.add("password", "123456");
        httpEntity = new HttpEntity<>(multiValueMap, null);
        responseEntityJsonNode =
                restTemplate.exchange(url, HttpMethod.POST, httpEntity, new ParameterizedTypeReference<ObjectResponse<JsonNode>>() {});
        Assert.assertEquals(HttpStatus.OK, responseEntityJsonNode.getStatusCode());
        Assert.assertEquals(username, responseEntityJsonNode.getBody().getData().get("username").asText());
        Assert.assertEquals(3, responseEntityJsonNode.getBody().getData().get("loginType").asInt());
        url = "http://localhost:" + localServerPort + "/api/v1/user/info";
        responseEntity =
                restTemplate.exchange(url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<ObjectResponse<String>>() {});
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(username, responseEntity.getBody().getData());

        // 用户登出
        url = "http://localhost:" + localServerPort + "/api/v1/logout";
        responseEntity = restTemplate.exchange(url, HttpMethod.POST, null, new ParameterizedTypeReference<ObjectResponse<String>>() {
        });
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals("成功退出", responseEntity.getBody().getData());

        // 测试连续5次不提供账号和密码后BusinessException#errorCode=50001
        url = "http://localhost:" + localServerPort + "/api/v1/password/login";
        for(int i=0; i<4; i++) {
            try {
                restTemplate.exchange(url, HttpMethod.POST, null, new ParameterizedTypeReference<ObjectResponse<String>>() {
                });
                Assert.fail("预期异常没有抛出");
            } catch (ResourceAccessException ex) {
                BusinessException businessException = (BusinessException) ex.getCause();
                Assert.assertEquals(50000, businessException.getErrorCode());
                Assert.assertEquals("没有指定用户名、手机号码、邮箱至少一项参数", businessException.getErrorMessage());
            }
        }
        try {
            restTemplate.exchange(url, HttpMethod.POST, null, new ParameterizedTypeReference<ObjectResponse<String>>() {
            });
            Assert.fail("预期异常没有抛出");
        } catch (ResourceAccessException ex) {
            BusinessException businessException = (BusinessException) ex.getCause();
            Assert.assertEquals(50001, businessException.getErrorCode());
            Assert.assertEquals("没有指定用户名、手机号码、邮箱至少一项参数", businessException.getErrorMessage());
        }

        // 尝试多次登录失败后需要提供登录验证码
        try {
            multiValueMap = new LinkedMultiValueMap<>();
            multiValueMap.add("clientId", UUID.randomUUID().toString());
            multiValueMap.add("captcha", "111111");
            multiValueMap.add("username", "usertest1");
            multiValueMap.add("password", "123456");
            httpEntity = new HttpEntity<>(multiValueMap, null);
            restTemplate.exchange(url, HttpMethod.POST, httpEntity, new ParameterizedTypeReference<ObjectResponse<String>>() {
            });
            Assert.fail("预期异常没有抛出");
        } catch (ResourceAccessException ex) {
            BusinessException businessException = (BusinessException) ex.getCause();
            Assert.assertEquals(50001, businessException.getErrorCode());
            Assert.assertEquals("登录验证码错误", businessException.getErrorMessage());
        }

        // 测试验证码登录
        String clientId = UUID.randomUUID().toString();
        url = "http://localhost:" + localServerPort + "/api/v1/password/captcha/get?clientId={clientId}";
        ResponseEntity<byte[]> responseEntityByteArr = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<byte[]>() {}, clientId);
        Assert.assertEquals(HttpStatus.OK, responseEntityByteArr.getStatusCode());

        username = "user1";
        multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("clientId", clientId);
        multiValueMap.add("captcha", "111111");
        multiValueMap.add("username", username);
        multiValueMap.add("password", "123456");
        httpEntity = new HttpEntity<>(multiValueMap, null);
        url = "http://localhost:" + localServerPort + "/api/v1/password/login";
        responseEntityJsonNode =
                restTemplate.exchange(url, HttpMethod.POST, httpEntity, new ParameterizedTypeReference<ObjectResponse<JsonNode>>() {});
        Assert.assertEquals(HttpStatus.OK, responseEntityJsonNode.getStatusCode());
        Assert.assertEquals(username, responseEntityJsonNode.getBody().getData().get("username").asText());
        Assert.assertEquals(1, responseEntityJsonNode.getBody().getData().get("loginType").asInt());
        url = "http://localhost:" + localServerPort + "/api/v1/user/info";
        responseEntity =
                restTemplate.exchange(url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<ObjectResponse<String>>() {});
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(username, responseEntity.getBody().getData());
    }
}
