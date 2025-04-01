package com.future.demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 演示 @SpyBean 用法
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
public class SpyBeanTests {

    @Autowired
    MockMvc mockMvc;

    @LocalServerPort
    int port;

    @SpyBean
    MyServiceInner myServiceInner;

    @Autowired
    TestRestTemplate restTemplate;

    /**
     * NOTE: 想通过 restful api 进行 mock 测试，
     * 被 mock 的bean必须使用 @SpyBean 注解，不能使用 @InjectMock+@Spy方式，
     * 正如当前测试那样使用 @SpyBean 注入 MyServiceInner
     */
    @Test
    public void test() throws Exception {
        Mockito.doReturn("param1=p2").when(this.myServiceInner).test2(Mockito.anyString());
        this.mockMvc.perform(get("/api/test21?param2=p1"))
                .andExpect(status().isOk())
                .andExpect(content().string("param1=p2"));

        // 因为使用 @SpyBean 注入 MyServiceInner，所以没有设置 mock 规则的方法会执行原来的代码逻辑实现
        this.mockMvc.perform(get("/api/test2?param1=pp"))
                .andExpect(status().isOk())
                .andExpect(content().string("param1=pp"));

        // 测试mock抛出异常
        Mockito.doThrow(new Exception("预期异常1")).when(this.myServiceInner).test2(Mockito.anyString());
        try {
            this.mockMvc.perform(get("/api/test21?param2=p1"));
            Assert.fail();
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("预期异常1"));
        }
    }

}
