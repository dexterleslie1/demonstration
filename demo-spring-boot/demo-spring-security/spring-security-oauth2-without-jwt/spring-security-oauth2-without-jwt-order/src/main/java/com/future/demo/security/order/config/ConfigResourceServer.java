package com.future.demo.security.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

@Configuration
// order-service作为资源服务器
@EnableResourceServer
public class ConfigResourceServer extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources
                // 资源ID，和客户端配置的资源ID一致
                .resourceId("order-service")
                // 验证令牌服务
                .tokenServices(tokenService())
                // 服务器不会创建或维护会话（HttpSession）
                .stateless(true);
    }

    // 支持DefaultTokenServices，当授权服务和资源服务在同一个服务中时使用此DefaultTokenServices
    // 支持RemoteTokenServices，当授权服务和资源服务不在同一个服务中时使用此RemoteTokenServices
    @Bean
    public ResourceServerTokenServices tokenService() {
        RemoteTokenServices tokenServices = new RemoteTokenServices();
        // order-service请求uaa服务客户端信息
        tokenServices.setClientId("order-service-resource");
        tokenServices.setClientSecret("123");
        // uaa服务check_token端点
        tokenServices.setCheckTokenEndpointUrl("http://localhost:9999/oauth/check_token");
        return tokenServices;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                // 禁用csrf
                .csrf().disable()
                // 服务器不会创建或维护会话（HttpSession）
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // OAuth2客户端要有write权限范围
                .and().authorizeRequests()
                .antMatchers("/**").access("#oauth2.hasScope('write')");
    }
}
