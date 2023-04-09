package com.future.demo.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@Configuration
public class ConfigWebFluxSecurity {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        httpSecurity
//                // 登录认证处理
//                .authenticationManager(reactiveAuthenticationManager())
//                .securityContextRepository(defaultSecurityContextRepository)
//                // 请求拦截处理
//                .authorizeExchange(exchange -> exchange
//                        .pathMatchers(noFilter).permitAll()
//                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
//                        .anyExchange().access(defaultAuthorizationManager)
//                )
                .formLogin()
//                // 自定义处理
//                .authenticationSuccessHandler(defaultAuthenticationSuccessHandler)
//                .authenticationFailureHandler(defaultAuthenticationFailureHandler)
//                .and()
//                .exceptionHandling()
//                .authenticationEntryPoint(defaultAuthenticationEntryPoint)
//                .and()
//                .exceptionHandling()
//                .accessDeniedHandler(defaultAccessDeniedHandler)
                .and()
                .csrf().disable()
        ;
        return httpSecurity.build();
    }

}
