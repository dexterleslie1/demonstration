package com.future.demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 演示 @InjectMocks+@Spy 用法
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes={Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class InjectMocksNSpyTests {

    @LocalServerPort
    int port;

    @InjectMocks
    MyService myService;

    @Spy
    MyServiceInner myServiceInner;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void test() throws Exception {
        Mockito.doReturn("param2=p2").when(this.myServiceInner).test2(Mockito.anyString());
        String str = this.myService.test2("p1");
        Assert.assertEquals("param2=p2", str);

        // 因为使用 @Spy 注入 MyServiceInner，所以没有设置 mock 规则的方法会执行原来的代码逻辑实现
        str = this.myService.test1("pp");
        Assert.assertEquals("param1=pp", str);
    }
}
