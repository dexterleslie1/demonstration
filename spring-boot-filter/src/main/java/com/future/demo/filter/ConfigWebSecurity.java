package com.future.demo.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class ConfigWebSecurity extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and().addFilterBefore(testFilter(), UsernamePasswordAuthenticationFilter.class)

                .logout().disable()

                .authorizeRequests()
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()

                .and()
                .formLogin().disable();
    }

    @Bean
    TestFilter testFilter() {
        return new TestFilter();
    }

}