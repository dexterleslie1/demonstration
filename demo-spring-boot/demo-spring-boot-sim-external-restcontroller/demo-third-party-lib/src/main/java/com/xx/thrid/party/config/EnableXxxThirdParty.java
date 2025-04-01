package com.xx.thrid.party.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

// 调用这个注解启用此库
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Documented
@Import({MyThirdPartyConfiguration.class})
public @interface EnableXxxThirdParty {

}
