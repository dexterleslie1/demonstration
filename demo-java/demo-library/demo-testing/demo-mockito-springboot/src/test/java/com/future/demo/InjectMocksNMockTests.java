package com.future.demo;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 演示 @InjectMocks+@Mock 用法
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class InjectMocksNMockTests {

    @InjectMocks
    MyService myService;

    @Mock
    MyServiceInner myServiceInner;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void test() {
        Mockito.doReturn("param1=p2").when(this.myServiceInner).test1(Mockito.anyString());
        String str = this.myService.test1("p1");
        Assert.assertEquals("param1=p2", str);
    }

    /**
     * NOTE: 下面通过 restful api 调用接口时， Spring 上下文中的 MyServiceInner 没有被 mock 的 myServiceInner 实例替换，所以测试不能通过
     * 只有直接调用 MyService 服务才能够被 mock，如上面测试 test()
     */
    @Ignore
    @Test
    public void test1() throws Exception {
        Mockito.doReturn("param1=p2").when(this.myServiceInner).test1(Mockito.anyString());
        ResponseEntity<String> response = this.restTemplate.getForEntity(
                "http://localhost:8080/api/test2?param1=p1",
                String.class);
        Assert.assertEquals("param1=p2", response.getBody());
    }

}
