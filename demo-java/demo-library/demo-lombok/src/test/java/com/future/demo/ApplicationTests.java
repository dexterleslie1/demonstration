package com.future.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationTests {

    @Resource
    MockMvc mockMvc;

    @Test
    public void contextLoads() throws Exception {
        // region @SuperBuilder 注解测试

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("param1", "p1");
        String authenticationBasicUser = "user1";
        String host = "192.168.1.1";
        int port = 8091;
        String uri = "/api/v1/user/login";

        // 测试创建RESTful类型AsyncInvocation
        AsyncInvocationRestModel asyncInvocationRestModel =
                AsyncInvocationRestModel.builder()
                        .parameters(parameters)
                        .authenticationBasicUser(authenticationBasicUser)
                        .host(host)
                        .port(port)
                        .uri(uri)
                        .type(AsyncInvocationType.REST)
                        .build();

        Assertions.assertEquals(parameters.get("param1"), asyncInvocationRestModel.getParameters().get("param1"));
        Assertions.assertEquals(authenticationBasicUser, asyncInvocationRestModel.getAuthenticationBasicUser());
        Assertions.assertEquals(host, asyncInvocationRestModel.getHost());
        Assertions.assertEquals(port, asyncInvocationRestModel.getPort());
        Assertions.assertEquals(uri, asyncInvocationRestModel.getUri());
        Assertions.assertEquals(AsyncInvocationType.REST, asyncInvocationRestModel.getType());

        // 测试lombok json
        String json = asyncInvocationRestModel.toJson();
        AsyncInvocationRestModel asyncInvocationRestModelFromJson =
                (AsyncInvocationRestModel) AsyncInvocationModel.fromJson(json, AsyncInvocationRestModel.class);
        Assertions.assertEquals(parameters.get("param1"), asyncInvocationRestModelFromJson.getParameters().get("param1"));
        Assertions.assertEquals(authenticationBasicUser, asyncInvocationRestModelFromJson.getAuthenticationBasicUser());
        Assertions.assertEquals(host, asyncInvocationRestModelFromJson.getHost());
        Assertions.assertEquals(port, asyncInvocationRestModelFromJson.getPort());
        Assertions.assertEquals(uri, asyncInvocationRestModelFromJson.getUri());
        Assertions.assertEquals(AsyncInvocationType.REST, asyncInvocationRestModel.getType());

        // endregion

        // region 用于协助测试“找不到符号变量log”报错

        ResultActions response = mockMvc.perform(get("/api/v1/test1"));
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$", is("成功调用")));

        // endregion
    }
}
