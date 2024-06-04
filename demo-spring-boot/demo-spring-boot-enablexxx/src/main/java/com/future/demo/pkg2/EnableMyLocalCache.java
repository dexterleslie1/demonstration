package com.future.demo.pkg2;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

// 专门用于启用LocalCache的注解
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MyLocalCacheConfiguration.class)
public @interface EnableMyLocalCache {

}