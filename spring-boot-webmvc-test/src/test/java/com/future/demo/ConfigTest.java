package com.future.demo;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;

/**
 * 统一配置测试使用的相关bean
 */
@TestConfiguration
public class ConfigTest {

    @SpyBean
    TestService testService;

    @SpyBean
    TestService2 testService2;

}
