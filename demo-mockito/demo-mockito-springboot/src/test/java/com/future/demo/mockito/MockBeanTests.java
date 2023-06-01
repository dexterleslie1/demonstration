package com.future.demo.mockito;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 演示 @MockBean 用法
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class MockBeanTests {

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
    public void test() {
        Mockito.doReturn("param1=p2").when(this.myServiceInner).test1(Mockito.anyString());
        ResponseEntity<String> response = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/api/test2?param1=p1",
                String.class);
        Assert.assertEquals("param1=p2", response.getBody());
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
