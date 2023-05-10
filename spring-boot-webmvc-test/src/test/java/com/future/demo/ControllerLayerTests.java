package com.future.demo;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class ControllerLayerTests {
    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    TestService testService;

    @Test
    public void test() throws Exception {
        // 场景: 测试没有被mock
        ResultActions response = mockMvc.perform(get("/api/v1/add")
                .queryParam("a", "1")
                .queryParam("b", "2")
                // 注入一个随机token就模拟已经登录
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED));
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(3)));

        // 场景: 测试被mock
        Mockito.when(this.testService.add(1, 2)).thenReturn(5);
        response = mockMvc.perform(get("/api/v1/add")
                .queryParam("a", "1")
                .queryParam("b", "2")
                // 注入一个随机token就模拟已经登录
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED));
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(5)));
    }

}
