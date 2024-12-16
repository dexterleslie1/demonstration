package com.future.demo.init;

import com.future.demo.config.ApplicationConfig;
import com.future.demo.config.WebConfig;
import com.future.demo.config.WebSecurityConfig;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

// SpringApplicationInitializer 类等价于 xml 配置的 web.xml 配置文件
public class SpringApplicationInitializer
        extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        // 加载 @ComponentScan 配置
        return new Class[]{ApplicationConfig.class, WebSecurityConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        // 加载 Spring MVC 配置
        return new Class[]{WebConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        // 所有请求转发到 DispatcherServlet 中处理
        return new String[]{"/"};
    }
}
