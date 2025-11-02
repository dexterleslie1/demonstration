package com.future.demo;

import com.future.common.constant.ErrorCodeConstant;
import com.future.demo.unify.Application;
import com.jayway.jsonpath.JsonPath;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@AutoConfigureMockMvc
public class ApplicationTests {

    @Autowired
    CacheManager cacheManager;
    Cache cacheSmsCaptcha;

    @Autowired
    MockMvc mockMvc;

    @PostConstruct
    public void init1() throws Exception {
        this.cacheSmsCaptcha = this.cacheManager.getCache("cacheSmsCaptcha");
    }

    @Test
    public void contextLoads() throws Exception {
        // region 短信登录

        // 测试手机号码格式错误
        String phone = "13511111111";
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/sendSms")
                .queryParam("principal" , phone));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(ErrorCodeConstant.ErrorCodeCommon))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").value("号码=" + phone + "格式错误，必需为E.164格式：+[国家代号][手机号码]，例如：+8613512345678"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(CoreMatchers.nullValue()));

        // 测试手机号码正确
        phone = "+8613511111111";
        resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/sendSms")
                .queryParam("principal" , phone));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").value(CoreMatchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("短信验证码已发送"));
        Element element = this.cacheSmsCaptcha.get(phone);
        Assert.assertEquals("111111" , element.getObjectValue());
        this.cacheSmsCaptcha.remove(phone);

        // 测试未提供短信验证码
        phone = "+8613511111111";
        resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .queryParam("type" , "sms")
                .queryParam("principal" , phone));
        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(ErrorCodeConstant.ErrorCodeCommon))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").value("没有提供短信验证码"));

        // 短信验证码已过期，请重新获取
        phone = "+8613511111111";
        String smsCaptcha = "1234567890";
        resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .queryParam("type" , "sms")
                .queryParam("principal" , phone)
                .queryParam("captchaVerifyParam" , smsCaptcha));
        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(ErrorCodeConstant.ErrorCodeCommon))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").value("短信验证码已过期，请重新获取"));

        // 提供的短信验证码错误
        phone = "+8613511111111";
        resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/sendSms")
                .queryParam("principal" , phone));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").value(CoreMatchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("短信验证码已发送"));
        smsCaptcha = "1234567890";
        resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .queryParam("type" , "sms")
                .queryParam("principal" , phone)
                .queryParam("captchaVerifyParam" , smsCaptcha));
        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(ErrorCodeConstant.ErrorCodeCommon))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").value("提供的短信验证码错误"));

        // 正常情况
        smsCaptcha = "111111";
        resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .queryParam("type" , "sms")
                .queryParam("principal" , phone)
                .queryParam("captchaVerifyParam" , smsCaptcha));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").value(CoreMatchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value(phone));
        String token = JsonPath.read(resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), "$.data.token");
        resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/info")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").value(CoreMatchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(phone));

        // endregion

        // region 测试未登录情况

        resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").value(CoreMatchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("成功退出"));
        resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/info")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
        resultActions.andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(ErrorCodeConstant.ErrorCodeLoginRequired))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").value("未登录"));

        // endregion

        // region 密码登录

        // 测试帐号密码错误
        String principal = "user1";
        resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .param("type" , "password")
                .queryParam("principal" , principal)
                .queryParam("credentials" , ""));
        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(ErrorCodeConstant.ErrorCodeCommon))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").value("用户名或者密码错误"));

        // 测试登录成功
        principal = "admin";
        resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .param("type" , "password")
                .queryParam("principal" , principal)
                .queryParam("credentials" , "123456"));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").value(CoreMatchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value(principal))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.token").value(CoreMatchers.notNullValue()));
        token = JsonPath.read(resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), "$.data.token");
        resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/info")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").value(CoreMatchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(principal));

        principal = "13511111111";
        resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .param("type" , "password")
                .queryParam("principal" , principal)
                .queryParam("credentials" , "123456"));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").value(CoreMatchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value(principal))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.token").value(CoreMatchers.notNullValue()));
        token = JsonPath.read(resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), "$.data.token");
        resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/info")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").value(CoreMatchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(principal));

        principal = "+8613511111111";
        resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .param("type" , "password")
                .queryParam("principal" , principal)
                .queryParam("credentials" , "123456"));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").value(CoreMatchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value(principal))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.token").value(CoreMatchers.notNullValue()));
        token = JsonPath.read(resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), "$.data.token");
        resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/info")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").value(CoreMatchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(principal));

        principal = "dexterleslie@gmail.com";
        resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .param("type" , "password")
                .queryParam("principal" , principal)
                .queryParam("credentials" , "123456"));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").value(CoreMatchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value(principal))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.token").value(CoreMatchers.notNullValue()));
        token = JsonPath.read(resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), "$.data.token");
        resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/info")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").value(CoreMatchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(principal));

        resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").value(CoreMatchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("成功退出"));

        /*// 测试连续5次不提供账号和密码后BusinessException#errorCode=50001
        for (int i = 0; i < 4; i++) {
            try {
                api.loginPassword(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
                Assert.fail("预期异常没有抛出");
            } catch (BusinessException ex) {
                Assert.assertEquals(ErrorCodeConstant.ErrorCodeCommon, ex.getErrorCode());
                Assert.assertEquals("没有指定用户名、手机号码、邮箱至少一项参数" , ex.getErrorMessage());
            }
        }
        try {
            api.loginPassword(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals(50001, ex.getErrorCode());
            Assert.assertEquals("没有指定用户名、手机号码、邮箱至少一项参数" , ex.getErrorMessage());
        }

        // 尝试多次登录失败后需要提供登录验证码
        try {
            api.loginPassword("usertest1" , "123456" , UUID.randomUUID().toString(), "111111");
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals(50001, ex.getErrorCode());
            Assert.assertEquals("登录验证码错误" , ex.getErrorMessage());
        }

        // 测试验证码登录
        String clientId = UUID.randomUUID().toString();
        Response response = api.getCaptcha(clientId);
        Assert.assertEquals(HttpStatus.OK.value(), response.status());
        Assert.assertTrue(response.body().length() > 0);

        username = "user1";
        responseJsonNode = api.loginPassword(username, "123456" , clientId, "111111");
        Assert.assertEquals(username, responseJsonNode.getData().get("username").asText());
//        Assert.assertEquals(1, responseJsonNode.getData().get("loginType").asInt());
        token = responseJsonNode.getData().get("token").asText();

        responseStr = api.getUserInfo(token);
        Assert.assertEquals(username, responseStr.getData());*/

        // 测试调用接口/api/v1/user/test1未登录
        resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/test1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + UUID.randomUUID().toString()));
        resultActions.andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(ErrorCodeConstant.ErrorCodeLoginRequired))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").value("未登录"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(CoreMatchers.nullValue()));

        // 测试权限
        principal = "admin";
        resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .param("type" , "password")
                .queryParam("principal" , principal)
                .queryParam("credentials" , "123456"));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").value(CoreMatchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value(principal))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.token").value(CoreMatchers.notNullValue()));
        token = JsonPath.read(resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), "$.data.token");

        resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/test1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").value(CoreMatchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("成功调用接口 /api/v1/user/test1"));

        resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/test2")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").value(CoreMatchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("成功调用接口 /api/v1/user/test2"));

        resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/test3")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
        resultActions.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(ErrorCodeConstant.ErrorCodeCommon))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").value("权限不足"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(CoreMatchers.nullValue()));

        // endregion
    }
}
