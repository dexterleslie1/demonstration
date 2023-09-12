package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ConfigAspect {

    // before
    // https://www.javatpoint.com/spring-boot-aop-before-advice
    @Before(value = "execution (* com.future.demo.TestServiceI.test1(..)) && args(param1, param2)")
    public void beforeAdvice(JoinPoint joinPoint, Long param1, String param2) {
        log.debug("before方法={},param1={},param2={}", joinPoint.getSignature(), param1, param2);
    }

    // after
    @After(value = "execution (* com.future.demo.TestServiceI.test1(..)) && args(param1, param2)")
    public void afterAdvice(JoinPoint joinPoint, Long param1, String param2) {
        log.debug("after方法={},param1={},param2={}", joinPoint.getSignature(), param1, param2);
    }

    // around
    @Around(value = "execution (* com.future.demo.TestServiceI.test1(..)) && args(param1, param2)")
    public Object aroundAdvice(ProceedingJoinPoint proceedingJoinPoint, Long param1, String param2) throws Throwable {
        try {
            log.debug("around before方法={},param1={},param2={}", proceedingJoinPoint.getSignature(), param1, param2);

            Object[] args = proceedingJoinPoint.getArgs();
            args[0] = 2001L;
            return proceedingJoinPoint.proceed(args);
        } finally {
            log.debug("around after方法={},param1={},param2={}", proceedingJoinPoint.getSignature(), param1, param2);
        }

    }

}
