package com.future.demo.config;

import com.future.demo.interceptor.MyHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer /* WebMvcConfigurer对spring mvc进行配置 */ {

    @Autowired
    MyHandlerInterceptor myHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(myHandlerInterceptor)
                // 只对/hello路径进行拦截
                .addPathPatterns("/hello");
    }

    // 全局跨域配置
    /*@Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        // 允许跨域携带cookie
        config.setAllowCredentials(true);
        // 只允许 abc.com 跨域访问
        config.setAllowedOrigins(Collections.singletonList("abc.com"));
        config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"));
        // 所有路径都允许跨域访问
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }*/
}
