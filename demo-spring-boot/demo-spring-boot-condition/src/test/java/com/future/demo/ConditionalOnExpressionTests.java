package com.future.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author dexterleslie@gmail.com
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {Application.class, ConditionalOnExpressionTests.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
public class ConditionalOnExpressionTests {
    private final static Logger log = LoggerFactory.getLogger(ConditionalOnExpressionTests.class);

    @Resource
    TestService1 testService1 = null;

    /**
     * @return
     */
    @Bean
    // 当括号中的内容为true时，使用该注解的类才被实例化。
    @ConditionalOnExpression("${spring.boot.condition.on.expression.test-service1-one:false}")
    TestService1 testService1One() {
        TestService1 service = new TestService1() {
            @Override
            public void sayHello() {
                log.info("testService1One sayHello.");
            }
        };
        return service;
    }

    /**
     * @return
     */
    @Bean
    @ConditionalOnExpression("${spring.boot.condition.on.expression.test-service1-two:false}")
    TestService1 testService1Two() {
        TestService1 service = new TestService1() {
            @Override
            public void sayHello() {
                log.info("testService1Two sayHello.");
            }
        };
        return service;
    }

    @Bean
    // spring.boot.condition.on.expression.test-service1-three不为空则创建TestService1
    @ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${spring.boot.condition.on.expression.test-service1-three:}')")
    TestService1 testService1Three() {
        TestService1 service = new TestService1() {
            @Override
            public void sayHello() {
                log.info("testService1Three sayHello.");
            }
        };
        return service;
    }

    /**
     *
     */
    @Test
    public void test() {
        testService1.sayHello();
    }
}
