package com.future.demo.pkg3;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(MyBeanDefinitionRegister.class)
public @interface EnableMyBean {

    String name() default "";

}
