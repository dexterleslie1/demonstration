package com.future.demo.test;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.future.demo.Application;
import com.future.demo.TestService;
import com.future.demo.UserModel;
import com.future.demo.mapper.UserMapper;
import com.future.demo.test.config.TestSupportConfiguration;
import com.future.demo.test.feign.TestSupportDemoFeignClient;
import feign.FeignException;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.UUID;

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
    UserMapper userMapper;
    @SpyBean
    TestService testService;
    @Resource
    TestSupportDemoFeignClient testSupportDemoFeignClient;

    @Test
    public void test() throws Exception {
        // 场景: 测试spybean使用原来的逻辑
        Integer result = this.testSupportDemoFeignClient.add(1, 2, "Bearer " + UUID.randomUUID().toString()).getData();
        Assert.assertEquals(new Integer(3), result);

        // 场景: 测试spybean使用被mock后指定的规则
        Mockito.doReturn(5).when(this.testService).add(Mockito.anyInt(), Mockito.anyInt());
        result = this.testSupportDemoFeignClient.add(1, 2, "Bearer " + UUID.randomUUID().toString()).getData();
        Assert.assertEquals(new Integer(5), result);


        // 场景: 测试没有被mock
        result = this.testSupportDemoFeignClient.minus(1, 2, "Bearer " + UUID.randomUUID().toString()/* 注入一个随机token就模拟已经登录 */).getData();
        Assert.assertEquals(new Integer(-1), result);

        // 场景: 测试spring-security在mockmvc测试中是否生效，不提供token预期报错
        try {
            this.testSupportDemoFeignClient.minus(1, 2, StringUtils.EMPTY);
            Assert.fail("预期异常没有抛出");
        } catch (FeignException ex) {
            Assert.assertEquals(403, ex.status());
        }


        // 场景: 测试集成mybatis-plus测试，查看是否正确加载mybatis-plus
        this.userMapper.delete(Wrappers.query());
        String resultStr = this.testSupportDemoFeignClient.addUser("Bearer " + UUID.randomUUID().toString()).getData();
        Assert.assertEquals("成功创建用户", resultStr);
        UserModel userModel = this.userMapper.selectList(Wrappers.query()).get(0);
        Assert.assertEquals("中文测试", userModel.getName());
        Assert.assertEquals("dexterleslie@gmail.com", userModel.getEmail());
    }

}
