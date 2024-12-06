package com.future.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class ApplicationTests {
    @Autowired
    MyDatasource myDatasource;
    @Autowired
    MyConfig myConfig;

    @Test
    public void contextLoads() {
        // 测试@Profile
        // 默认的profile为default
        // 通过java -jar myapp.jar --spring.profiles.active=dev指定profile
        // 通过设置application.properties文件中的spring.profiles.active=dev指定profile
        String url = myDatasource.getUrl();
        Assertions.assertEquals("test-ds", url);

        // 测试properties文件不同的Profile配置
        Assertions.assertEquals("test2", myConfig.p2);

        // 测试yaml文件不同的Profile配置
        Assertions.assertEquals("test1", myConfig.p1);
    }
}
