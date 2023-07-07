package com.future.demo.test;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.future.demo.Application;
import com.future.demo.TestService;
import com.future.demo.UserModel;
import com.future.demo.mapper.UserMapper;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.annotation.Resource;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// https://stackoverflow.com/questions/42249791/resolving-port-already-in-use-in-a-spring-boot-test-defined-port
@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
// 启用mockmvc测试
@AutoConfigureMockMvc
public class ControllerLayerTests {

    @Resource
    UserMapper userMapper;
    @SpyBean
    TestService testService;
    @Resource
    private MockMvc mockMvc;

    @Test
    public void test() throws Exception {
        // 场景: 测试spybean使用原来的逻辑
        ResultActions response = mockMvc.perform(get("/api/v1/add")
                .queryParam("a", "1")
                .queryParam("b", "2")
                // 注入一个随机token就模拟已经登录
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED));
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(3)));

        // 场景: 测试spybean使用被mock后指定的规则
        Mockito.doReturn(5).when(this.testService).add(Mockito.anyInt(), Mockito.anyInt());
        response = mockMvc.perform(get("/api/v1/add")
                .queryParam("a", "1")
                .queryParam("b", "2")
                // 注入一个随机token就模拟已经登录
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED));
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(5)));

        // 场景: 测试没有被mock
        response = mockMvc.perform(get("/api/v1/minus")
                .queryParam("a", "1")
                .queryParam("b", "2")
                // 注入一个随机token就模拟已经登录
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED));
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(-1)));

        // 场景: 测试spring-security在mockmvc测试中是否生效，不提供token预期报错
        response = mockMvc.perform(get("/api/v1/minus")
                .queryParam("a", "1")
                .queryParam("b", "2")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED));
        response.andExpect(status().isForbidden());

        // 场景: 测试集成mybatis-plus测试，查看是否正确加载mybatis-plus
        this.userMapper.delete(Wrappers.query());
        response = mockMvc.perform(get("/api/v1/addUser")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + UUID.randomUUID().toString()));
        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", CoreMatchers.is("成功创建用户")));
        UserModel userModel = this.userMapper.selectList(Wrappers.query()).get(0);
        Assert.assertEquals("中文测试", userModel.getName());
        Assert.assertEquals("dexterleslie@gmail.com", userModel.getEmail());
    }

}
