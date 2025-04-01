package com.future.demo.argument;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collection;
import java.util.Collections;

// NOTE: 取消字符串转换为Collection逗号分开特性
// https://stackoverflow.com/questions/42120588/how-to-prevent-spring-mvc-from-interpreting-commas-when-converting-to-a-collecti
@Configuration
public class ConfigSpringMvc implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.removeConvertible(String.class, Collection.class);
        registry.addConverter(String.class, Collection.class, Collections::singletonList);
    }
}
