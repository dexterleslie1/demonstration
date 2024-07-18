package com.future.demo.test;

import com.future.demo.Application;
import com.future.demo.test.config.TestSupportConfiguration;
import com.future.demo.test.feign.TestSupportDemoFeignClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, TestSupportConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class ApplicationTests {

    @Resource
    TestSupportDemoFeignClient testSupportDemoFeignClient;

    @Test
    public void testGet() throws Exception {
        String uuid = this.testSupportDemoFeignClient.testGet().getData();
        Assert.assertNotNull(uuid);
    }

}
