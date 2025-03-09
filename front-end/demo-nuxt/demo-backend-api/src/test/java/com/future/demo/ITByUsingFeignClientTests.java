package com.future.demo;

import com.future.demo.config.TestSupportConfiguration;
import com.future.demo.feign.TestSupportDemoFeignClient;
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
public class ITByUsingFeignClientTests {

  @Resource
  TestSupportDemoFeignClient testSupportDemoFeignClient;

  @Test
  public void test() throws Exception {
    String result = this.testSupportDemoFeignClient.test1().getData();
    Assert.assertEquals("Nuxt api返回数据, token=", result);
  }

}
