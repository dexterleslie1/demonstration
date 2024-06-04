package com.xx.thrid.party.test;

import com.xx.thrid.party.Application;
import com.xx.thrid.party.test.config.TestSupportConfiguration;
import com.xx.thrid.party.test.feign.TestSupportDemoFeignClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, TestSupportConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class FeignClientTests {

    @Resource
    TestSupportDemoFeignClient testSupportDemoFeignClient;

    @Test
    public void test() throws Exception {
        Assert.assertEquals("Hello world!",
                this.testSupportDemoFeignClient.test1().getData());
    }

}
