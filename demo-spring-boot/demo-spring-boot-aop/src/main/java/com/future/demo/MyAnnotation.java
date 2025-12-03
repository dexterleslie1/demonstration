package com.future.demo;

import java.lang.annotation.*;

// 指定该注解只能用于方法
@Target(ElementType.METHOD)
// 注解在运行时可用，可以通过反射机制读取注解
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyAnnotation {
    /**
     * 注解配置属性1
     *
     * @return
     */
    String p1() default "";

    /**
     * 注解配置属性2
     *
     * @return
     */
    String p2() default "";
}
