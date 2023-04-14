package com.future.demo.mockito;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 演示 @SpyBean 用法
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes={Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class SpyBeanTests {

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
    public void test() {
        Mockito.doReturn("param1=p2").when(this.myServiceInner).test2(Mockito.anyString());
        ResponseEntity<String> response = this.restTemplate.getForEntity(
                "http://localhost:"+ port  + "/api/test21?param2=p1",
                String.class);
        Assert.assertEquals("param1=p2", response.getBody());

        // 因为使用 @SpyBean 注入 MyServiceInner，所以没有设置 mock 规则的方法会执行原来的代码逻辑实现
        response = this.restTemplate.getForEntity(
                "http://localhost:"+ port  + "/api/test2?param1=pp",
                String.class);
        Assert.assertEquals("param1=pp", response.getBody());
    }

}
