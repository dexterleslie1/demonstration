package com.future.demo;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

// 定义切面的执行顺序，数值越小越先执行
@Order(2)
@Component
@Aspect
public class AMyAspect {
    @Autowired
    public SharedStore sharedStore;

    @Pointcut("execution(int com.future.demo..MyCalculator.*(int, int))")
    public void pointcut() {
    }

    @Before("pointcut()"/* 引用上面定义的pointcut切入点 */)
    public void before(JoinPoint joinPoint/* joinpoint获取当前切入点信息 */) {
        this.sharedStore.sharedList.add("aspect2");
    }
}
