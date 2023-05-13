package com.future.demo.pkg2;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CacheSelector.class)
public @interface EnableMyCache {
    /**
     * 缓存类型
     *
     * @return
     */
    CacheType type() default CacheType.Redis;
}