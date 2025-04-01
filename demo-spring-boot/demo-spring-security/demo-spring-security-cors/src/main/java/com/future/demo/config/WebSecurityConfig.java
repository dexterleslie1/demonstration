package com.future.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().permitAll()
                .and().cors().configurationSource(corsConfigurationSource -> {
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
                    return config;
                })
                .and().csrf().disable();
    }
}
