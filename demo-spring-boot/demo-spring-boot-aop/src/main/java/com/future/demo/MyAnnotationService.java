package com.future.demo;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyAnnotationService {
    /**
     * 用于协助测试自定义注解aop
     */
    @MyAnnotation(p1 = "属性1", p2 = "属性2")
    public List<String> method1(List<String> paramList) {
        return paramList;
    }
}
