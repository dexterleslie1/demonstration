package com.future.demo;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Aspect
public class MyAnnotationAspect {

    // 定义Pointcut
    // 定义拦截所有@MyAnnotation方法的Pointcut表达式
    @Pointcut("@annotation(com.future.demo.MyAnnotation)")
    public void pointcut() {

    }

    // 定义Advice
    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        Object param = joinPoint.getArgs()[0];
        if (param != null && param instanceof List) {
            // 在Advice中获取注解属性值
            // 获取方法签名
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 从方法签名中获取MyAnnotation注解实例
            MyAnnotation myAnnotation = signature.getMethod().getAnnotation(MyAnnotation.class);
            // 获取注解中的属性值
            String p1Value = myAnnotation.p1();
            String p2Value = myAnnotation.p2();

            // 把注解中的p1和p2值加入到参数中
            ((List) param).add(p1Value);
            ((List) param).add(p2Value);
        }
    }
}
