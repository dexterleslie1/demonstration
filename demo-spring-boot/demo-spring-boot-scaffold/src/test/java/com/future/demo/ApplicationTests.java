package com.future.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {Application.class},
        // 启动测试时spring-boot随机分配端口
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class ApplicationTests {
    @Test
    public void test() {
    }
}
