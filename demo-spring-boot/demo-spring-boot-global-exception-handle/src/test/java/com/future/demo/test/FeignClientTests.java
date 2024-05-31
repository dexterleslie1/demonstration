package com.future.demo.test;

import com.future.common.exception.BusinessException;
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

/**
 * 使用feignclient集成测试
 */
// https://stackoverflow.com/questions/42249791/resolving-port-already-in-use-in-a-spring-boot-test-defined-port
@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, TestSupportConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class FeignClientTests {

    @Resource
    TestSupportDemoFeignClient testSupportDemoFeignClient;

    @Test
    public void test() throws Exception {
        try {
            this.testSupportDemoFeignClient.testBusinessException();
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals("测试BusinessException", ex.getMessage());
        }

        try {
            this.testSupportDemoFeignClient.testIllegalArgumentException();
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals("测试IllegalArgumentException", ex.getMessage());
        }
    }

}
