package com.future.demo.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.authentication.DelegatingReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.stream.Collectors;

@EnableWebFluxSecurity
@Configuration
@Slf4j
public class ConfigSecurity {

    private final String[] path = {
            "/favicon.ico",
            "/book/**",
            "/user/login.html",
            "/user/__MACOSX/**",
            "/user/css/**",
            "/user/fonts/**",
            "/user/images/**",
            "/api/v1/auth/loginWithPassword"};
//    @Resource
//    SecurityUserDetailsService securityUserDetailsService;
    @Resource
    AuthorizationManager authorizationManager;
    @Resource
    AccessDeniedHandler accessDeniedHandler;
//    @Resource
//    AuthenticationSuccessHandler authenticationSuccessHandler;
//    @Resource
//    AuthenticationFailureHandler authenticationFailureHandler;
//    @Autowired
//    CookieToHeadersFilter cookieToHeadersFilter;
//    @Autowired
//    LogoutSuccessHandler logoutSuccessHandler;
//    @Autowired
//    LogoutHandler logoutHandler;
    @Resource
    SecurityRepository securityRepository;
    @Resource
    AuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        //SecurityWebFiltersOrder枚举类定义了执行次序
        http.authorizeExchange(exchange -> exchange // 请求拦截处理
                        .pathMatchers(path).permitAll()
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .anyExchange().access(authorizationManager)//权限
                //.and().authorizeExchange().pathMatchers("/user/normal/**").hasRole("ROLE_USER")
                //.and().authorizeExchange().pathMatchers("/user/admin/**").hasRole("ROLE_ADMIN")
                //也可以这样写 将匹配路径和角色权限写在一起
        )
                .httpBasic()
//                .and()
//                .formLogin().loginPage("/api/v1/auth/loginWithPassword")//登录接口
//                .authenticationSuccessHandler(authenticationSuccessHandler) //认证成功
//                .authenticationFailureHandler(authenticationFailureHandler) //登陆验证失败
                .and().exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)//基于http的接口请求鉴权失败
                .and().csrf().disable();//必须支持跨域
//                .logout().logoutUrl("/user/logout")
//                .logoutHandler(logoutHandler)
//                .logoutSuccessHandler(logoutSuccessHandler);
        http.securityContextRepository(securityRepository);
//        //无状态 默认情况下使用的WebSession
//        http.securityContextRepository(NoOpServerSecurityContextRepository.getInstance());
        return http.build();
    }

//    @Bean
//    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
//        LinkedList<ReactiveAuthenticationManager> managers = new LinkedList<>();
//        managers.add(authentication -> {
//            // 其他登陆方式
//            return Mono.empty();
//        });
//        managers.add(new UserDetailsRepositoryReactiveAuthenticationManager(securityUserDetailsService));
//        return new DelegatingReactiveAuthenticationManager(managers);
//    }

    // https://www.cnblogs.com/w84422/p/15519310.html
    @Bean
    @ConditionalOnMissingBean
    public HttpMessageConverters messageConverters(ObjectProvider<HttpMessageConverter<?>> converters) {
        return new HttpMessageConverters(converters.orderedStream().collect(Collectors.toList()));
    }

}
