package com.future.demo.zuul.config;

import com.future.demo.zuul.filter.GenericFilterBeanFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.filter.GenericFilterBean;

@Configuration
@EnableWebSecurity
public class ConfigSpringSecurity extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests()
                .antMatchers("/api/v1/**").permitAll()
                .anyRequest().authenticated();
    }

    @Bean
    GenericFilterBean genericFilterBeanFilter() {
        return new GenericFilterBeanFilter();
    }
}
