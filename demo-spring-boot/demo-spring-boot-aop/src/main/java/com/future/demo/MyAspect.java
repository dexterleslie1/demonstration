package com.future.demo;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

// 定义切面的执行顺序，数值越小越先执行
@Order(1)
// 切面
@Component
@Aspect
public class MyAspect {
    @Autowired
    public SharedStore sharedStore;

    public boolean invokedBefore = false;
    public boolean invokedAfterReturning = false;
    public boolean invokedAfterThrowing = false;
    public boolean invokedAfter = false;
    public String methodName = null;
    public int[] args = null;
    public Object result = null;
    public Throwable throwable = null;

    // 定义切入点
    @Pointcut("execution(int com.future.demo..MyCalculator.*(int, int))")
    public void pointcut() {
    }

    // 方法执行前
    // * 表示匹配多个任意字符或者任意类型
    // .. 表示匹配多个任意参数或者多个层级的包路径
    @Before("pointcut()"/* 引用上面定义的pointcut切入点 */)
    public void before(JoinPoint joinPoint/* joinpoint获取当前切入点信息 */) {
        this.methodName = getMethodName(joinPoint);
        Object[] argsObject = joinPoint.getArgs();
        this.args = new int[]{(int) argsObject[0], (int) argsObject[1]};
        this.invokedBefore = true;

        this.sharedStore.sharedList.add("aspect1");
    }

    // 方法执行后没有抛出异常
    @AfterReturning(value = "pointcut()",
            returning = "result"/* 指定返回值的形参变量 */)
    public void afterReturning(JoinPoint joinPoint, Object result) {
        this.methodName = getMethodName(joinPoint);
        this.result = result;
        this.invokedAfterReturning = true;
    }

    // 方法执行后抛出异常
    @AfterThrowing(value = "pointcut()", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, Throwable e) {
        this.methodName = getMethodName(joinPoint);
        this.throwable = e;
        this.invokedAfterThrowing = true;
    }

    // 方法执行后无论是否抛出异常
    @After("pointcut()")
    public void after(JoinPoint joinPoint) {
        this.methodName = getMethodName(joinPoint);
        this.invokedAfter = true;
    }

    public String getMethodName(JoinPoint joinPoint) {
        return joinPoint.getSignature().getName();
    }

    public void reset() {
        this.invokedBefore = false;
        this.invokedAfterReturning = false;
        this.invokedAfterThrowing = false;
        this.invokedAfter = false;
        this.methodName = null;
        this.args = null;
        this.result = null;
        this.throwable = null;
        this.sharedStore.sharedList = new ArrayList<>();
    }

}
