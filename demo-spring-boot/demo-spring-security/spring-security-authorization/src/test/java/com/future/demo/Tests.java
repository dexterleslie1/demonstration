package com.future.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import com.future.common.json.JSONUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@AutoConfigureMockMvc
public class Tests {

    @Autowired
    MockMvc mockMvc;

    /**
     * 测试@Secured注解
     *
     * @throws BusinessException
     */
    @Test
    public void testAnnotationSecured() throws Exception {
        String password = "123456";

        // 测试同时拥有r1和r2角色
        String username = "user-with-role-r1-and-role-r2";
        MvcResult mvcResult = this.mockMvc.perform(post("/api/auth/login")
                        .queryParam("username", username)
                        .queryParam("password", password))
                .andExpect(status().isOk())
                .andReturn();
        String token = (String) JSONUtil.ObjectMapperInstance.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
                , new TypeReference<ObjectResponse<Map<String, Object>>>() {
                }).getData().get("token");
        mvcResult = this.mockMvc.perform(get("/api/v1/test1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        ObjectResponse<String> response1 = JSONUtil.ObjectMapperInstance.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
                , new TypeReference<ObjectResponse<String>>() {
                });
        Assert.assertEquals("成功调用接口/api/v1/test1", response1.getData());

        // 测试只有r1角色
        username = "user-with-only-role-r1";
        mvcResult = this.mockMvc.perform(post("/api/auth/login")
                        .queryParam("username", username)
                        .queryParam("password", password))
                .andExpect(status().isOk())
                .andReturn();
        token = (String) JSONUtil.ObjectMapperInstance.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
                , new TypeReference<ObjectResponse<Map<String, Object>>>() {
                }).getData().get("token");
        mvcResult = this.mockMvc.perform(get("/api/v1/test1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        response1 = JSONUtil.ObjectMapperInstance.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
                , new TypeReference<ObjectResponse<String>>() {
                });
        Assert.assertEquals("成功调用接口/api/v1/test1", response1.getData());

        // 测试只有r2角色
        username = "user-with-only-role-r2";
        mvcResult = this.mockMvc.perform(post("/api/auth/login")
                        .queryParam("username", username)
                        .queryParam("password", password))
                .andExpect(status().isOk())
                .andReturn();
        token = (String) JSONUtil.ObjectMapperInstance.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
                , new TypeReference<ObjectResponse<Map<String, Object>>>() {
                }).getData().get("token");
        mvcResult = this.mockMvc.perform(get("/api/v1/test1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        response1 = JSONUtil.ObjectMapperInstance.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
                , new TypeReference<ObjectResponse<String>>() {
                });
        Assert.assertEquals("成功调用接口/api/v1/test1", response1.getData());

        // 测试没有任何角色
        username = "user-with-none-role";
        mvcResult = this.mockMvc.perform(post("/api/auth/login")
                        .queryParam("username", username)
                        .queryParam("password", password))
                .andExpect(status().isOk())
                .andReturn();
        token = (String) JSONUtil.ObjectMapperInstance.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
                , new TypeReference<ObjectResponse<Map<String, Object>>>() {
                }).getData().get("token");
        this.mockMvc.perform(get("/api/v1/test1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(content().string("{\"errorCode\":50002,\"errorMessage\":\"权限不足\",\"data\":null}"));
    }

    /**
     * 测试@PreAuthorize注解用于角色
     *
     * @throws BusinessException
     */
    @Test
    public void testAnnotationPreAuthorizeForRole() throws Exception {
        String password = "123456";

        // 测试同时拥有r1和r2角色
        String username = "user-with-role-r1-and-role-r2";
        MvcResult mvcResult = this.mockMvc.perform(post("/api/auth/login")
                        .queryParam("username", username)
                        .queryParam("password", password))
                .andExpect(status().isOk())
                .andReturn();
        String token = (String) JSONUtil.ObjectMapperInstance.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
                , new TypeReference<ObjectResponse<Map<String, Object>>>() {
                }).getData().get("token");
        mvcResult = this.mockMvc.perform(get("/api/v1/test2")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        ObjectResponse<String> response1 = JSONUtil.ObjectMapperInstance.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
                , new TypeReference<ObjectResponse<String>>() {
                });
        Assert.assertEquals("成功调用接口/api/v1/test2", response1.getData());
        // 测试只有r1角色
        username = "user-with-only-role-r1";
        mvcResult = this.mockMvc.perform(post("/api/auth/login")
                        .queryParam("username", username)
                        .queryParam("password", password))
                .andExpect(status().isOk())
                .andReturn();
        token = (String) JSONUtil.ObjectMapperInstance.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
                , new TypeReference<ObjectResponse<Map<String, Object>>>() {
                }).getData().get("token");
        mvcResult = this.mockMvc.perform(get("/api/v1/test2")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        response1 = JSONUtil.ObjectMapperInstance.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
                , new TypeReference<ObjectResponse<String>>() {
                });
        Assert.assertEquals("成功调用接口/api/v1/test2", response1.getData());
        // 测试只有r2角色
        username = "user-with-only-role-r2";
        mvcResult = this.mockMvc.perform(post("/api/auth/login")
                        .queryParam("username", username)
                        .queryParam("password", password))
                .andExpect(status().isOk())
                .andReturn();
        token = (String) JSONUtil.ObjectMapperInstance.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
                , new TypeReference<ObjectResponse<Map<String, Object>>>() {
                }).getData().get("token");
        mvcResult = this.mockMvc.perform(get("/api/v1/test2")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        response1 = JSONUtil.ObjectMapperInstance.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
                , new TypeReference<ObjectResponse<String>>() {
                });
        Assert.assertEquals("成功调用接口/api/v1/test2", response1.getData());
        // 测试没有任何角色
        username = "user-with-none-role";
        mvcResult = this.mockMvc.perform(post("/api/auth/login")
                        .queryParam("username", username)
                        .queryParam("password", password))
                .andExpect(status().isOk())
                .andReturn();
        token = (String) JSONUtil.ObjectMapperInstance.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
                , new TypeReference<ObjectResponse<Map<String, Object>>>() {
                }).getData().get("token");
        this.mockMvc.perform(get("/api/v1/test2")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(content().string("{\"errorCode\":50002,\"errorMessage\":\"权限不足\",\"data\":null}"));

        // 测试同时拥有r1和r2角色
        username = "user-with-role-r1-and-role-r2";
        mvcResult = this.mockMvc.perform(post("/api/auth/login")
                        .queryParam("username", username)
                        .queryParam("password", password))
                .andExpect(status().isOk())
                .andReturn();
        token = (String) JSONUtil.ObjectMapperInstance.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
                , new TypeReference<ObjectResponse<Map<String, Object>>>() {
                }).getData().get("token");
        mvcResult = this.mockMvc.perform(get("/api/v1/test3")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        response1 = JSONUtil.ObjectMapperInstance.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
                , new TypeReference<ObjectResponse<String>>() {
                });
        Assert.assertEquals("成功调用接口/api/v1/test3", response1.getData());
        // 测试只有r1角色
        username = "user-with-only-role-r1";
        mvcResult = this.mockMvc.perform(post("/api/auth/login")
                        .queryParam("username", username)
                        .queryParam("password", password))
                .andExpect(status().isOk())
                .andReturn();
        token = (String) JSONUtil.ObjectMapperInstance.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
                , new TypeReference<ObjectResponse<Map<String, Object>>>() {
                }).getData().get("token");
        this.mockMvc.perform(get("/api/v1/test3")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(content().string("{\"errorCode\":50002,\"errorMessage\":\"权限不足\",\"data\":null}"));
        // 测试只有r2角色
        username = "user-with-only-role-r2";
        mvcResult = this.mockMvc.perform(post("/api/auth/login")
                        .queryParam("username", username)
                        .queryParam("password", password))
                .andExpect(status().isOk())
                .andReturn();
        token = (String) JSONUtil.ObjectMapperInstance.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
                , new TypeReference<ObjectResponse<Map<String, Object>>>() {
                }).getData().get("token");
        this.mockMvc.perform(get("/api/v1/test3")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(content().string("{\"errorCode\":50002,\"errorMessage\":\"权限不足\",\"data\":null}"));
        // 测试没有任何角色
        username = "user-with-none-role";
        mvcResult = this.mockMvc.perform(post("/api/auth/login")
                        .queryParam("username", username)
                        .queryParam("password", password))
                .andExpect(status().isOk())
                .andReturn();
        token = (String) JSONUtil.ObjectMapperInstance.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
                , new TypeReference<ObjectResponse<Map<String, Object>>>() {
                }).getData().get("token");
        this.mockMvc.perform(get("/api/v1/test3")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(content().string("{\"errorCode\":50002,\"errorMessage\":\"权限不足\",\"data\":null}"));

        // 测试拥有权限 perm:test5
        username = "user-with-perm-test5";
        mvcResult = this.mockMvc.perform(post("/api/auth/login")
                        .queryParam("username", username)
                        .queryParam("password", password))
                .andExpect(status().isOk())
                .andReturn();
        token = (String) JSONUtil.ObjectMapperInstance.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
                , new TypeReference<ObjectResponse<Map<String, Object>>>() {
                }).getData().get("token");
        mvcResult = this.mockMvc.perform(get("/api/v1/test5")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        response1 = JSONUtil.ObjectMapperInstance.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
                , new TypeReference<ObjectResponse<String>>() {
                });
        Assert.assertEquals("成功调用接口/api/v1/test5", response1.getData());
        // 测试没有权限 perm:test5
        username = "user-with-role-r1";
        mvcResult = this.mockMvc.perform(post("/api/auth/login")
                        .queryParam("username", username)
                        .queryParam("password", password))
                .andExpect(status().isOk())
                .andReturn();
        token = (String) JSONUtil.ObjectMapperInstance.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
                , new TypeReference<ObjectResponse<Map<String, Object>>>() {
                }).getData().get("token");
        this.mockMvc.perform(get("/api/v1/test5")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(content().string("{\"errorCode\":50002,\"errorMessage\":\"权限不足\",\"data\":null}"));

        // 测试自定义权限验证拥有 /api/v1/test6
        username = "user-with-allow-uri-test6";
        mvcResult = this.mockMvc.perform(post("/api/auth/login")
                        .queryParam("username", username)
                        .queryParam("password", password))
                .andExpect(status().isOk())
                .andReturn();
        token = (String) JSONUtil.ObjectMapperInstance.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
                , new TypeReference<ObjectResponse<Map<String, Object>>>() {
                }).getData().get("token");
        mvcResult = this.mockMvc.perform(get("/api/v1/test6")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        response1 = JSONUtil.ObjectMapperInstance.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
                , new TypeReference<ObjectResponse<String>>() {
                });
        Assert.assertEquals("成功调用接口/api/v1/test6", response1.getData());
        // 测试自定义权限验证没有 /api/v1/test6
        username = "user-with-role-r1";
        mvcResult = this.mockMvc.perform(post("/api/auth/login")
                        .queryParam("username", username)
                        .queryParam("password", password))
                .andExpect(status().isOk())
                .andReturn();
        token = (String) JSONUtil.ObjectMapperInstance.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
                , new TypeReference<ObjectResponse<Map<String, Object>>>() {
                }).getData().get("token");
        this.mockMvc.perform(get("/api/v1/test6")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(content().string("{\"errorCode\":50002,\"errorMessage\":\"权限不足\",\"data\":null}"));
    }

}
