package com.future.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
  securedEnabled = true,/* 开启secured注解判断是否拥有角色 */
  prePostEnabled = true/* 开启preAuthorize注解 */)
public class ConfigWebSecurity extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      // 禁止csrf
      .csrf().disable()
      // 无状态应用
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

      .and()
      .authorizeRequests()
      // 放行指定uri
      .antMatchers("/api/**").permitAll()
      .anyRequest().authenticated()

      .and()
      // 禁用form登录
      .formLogin().disable();
  }

}
