package com.future.demo.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
// 等价于 xml 配置中的 <mvc:annotation-driven/>
@EnableWebMvc
// 扫描 com.future.demo 包及其子包下的 @Controller 注解的类
@ComponentScan({"com.future.demo"})
public class WebConfig extends WebMvcConfigurerAdapter {

//    @Override
//    public void configureViewResolvers(ViewResolverRegistry registry) {
//        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
//        viewResolver.setPrefix("/WEB-INF/views/");
//        viewResolver.setSuffix(".jsp");
//        registry.viewResolver(viewResolver);
//    }
//    @Bean
//    ViewResolver viewResolver() {
//        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
//        viewResolver.setPrefix("/WEB-INF/views/");
//        viewResolver.setSuffix(".jsp");
//        return viewResolver;
//    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 当访问 / 时，重定向到 Spring Security 中的 /login
        registry.addViewController("/").setViewName("redirect:/login");
    }
}
