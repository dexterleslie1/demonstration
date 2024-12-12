package com.future.demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 演示 @MockBean 用法
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
public class MockBeanTests {

    @Autowired
    MockMvc mockMvc;

    @LocalServerPort
    int port;

    @MockBean
    MyServiceInner myServiceInner;

    @Autowired
    TestRestTemplate restTemplate;

    /**
     * NOTE: 想通过 restful api 进行 mock 测试，
     * 被 mock 的bean必须使用 @MockBean 注解，不能使用 @InjectMock+@Mock方式，
     * 正如当前测试那样使用 @MockBean 注入 MyServiceInner
     */
    @Test
    public void test() throws Exception {
        Mockito.doReturn("param1=p2").when(this.myServiceInner).test1(Mockito.anyString());
        this.mockMvc.perform(get("/api/test2?param1=p1"))
                .andExpect(status().isOk())
                .andExpect(content().string("param1=p2"));
    }

    /**
     * mock多种不同参数场景
     */
    @Test
    public void test2() {
        for (int i = 1; i <= 5; i++) {
            Mockito.doReturn("p" + i).when(this.myServiceInner).test1(String.valueOf(i));
        }

        String str = this.myServiceInner.test1("1");
        Assert.assertEquals("p1", str);
        str = this.myServiceInner.test1("2");
        Assert.assertEquals("p2", str);
    }

}
