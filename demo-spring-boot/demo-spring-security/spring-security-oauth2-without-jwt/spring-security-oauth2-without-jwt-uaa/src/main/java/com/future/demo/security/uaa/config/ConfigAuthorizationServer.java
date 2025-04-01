package com.future.demo.security.uaa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

@Configuration
@EnableAuthorizationServer
public class ConfigAuthorizationServer extends AuthorizationServerConfigurerAdapter {
    // 所有客户端密码
    private final static String ClientSecret = "123";

    @Autowired
    RedisConnectionFactory redisConnectionFactory;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    ClientDetailsService clientDetailsService;
    @Autowired
    UserDetailsService userDetailsService;

    // 配置token存放在redis中
    @Bean
    TokenStore tokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }

    // 配置客户端详情服务(ClientDetailsService),ClientDetailsService负责查找ClientDetails
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("client1")
                .secret(passwordEncoder.encode(ClientSecret))
                /*.resourceIds("resource1")*/
                // authorization_code 授权码模式，，跳转到登录页面需要用户登录后并授权后才能够获取token
                // implicit 静默授权模式，跳转到登录页面需要用户登录后并授权才能够获取token
                .authorizedGrantTypes("authorization_code", "implicit", "refresh_token")
                .scopes("all")
                .autoApprove(false)
                .redirectUris("http://www.baidu.com")
                // 设置各个客户端的 access_token 和 refresh_token 有效期，单位秒
                /*.accessTokenValiditySeconds(3600)
                .refreshTokenValiditySeconds(3600)*/

                .and()
                .withClient("client2")
                .secret(passwordEncoder.encode(ClientSecret))
                /*.resourceIds("resouce1")*/
                // password 密码模式，用户提供账号密码后不需要登录授权直接获取token
                .authorizedGrantTypes("password")
                .scopes("write")

                .and().withClient("client3")
                .secret(passwordEncoder.encode(ClientSecret))
                /*.resourceIds("resource1")*/
                // client_credentails 客户端模式，不需要提供用户账号密码信息即可获取token
                .authorizedGrantTypes("client_credentials")
                .scopes("all")

                // order-service客户端
                .and().withClient("order-service-resource")
                .secret(passwordEncoder.encode(ClientSecret))
                .authorizedGrantTypes("client_credentials")
                .scopes("all");
    }

    // 配置令牌访问端点
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                // 把 TokenStore 注入到 Spring OAuth2 中，否则 token 不会保存在 Redis 中
                .tokenStore(tokenStore())
                // 密码模式，用户提供账号密码不需要登录直接校验通过并获取token
                .authenticationManager(authenticationManager)
                // 把 Spring Security 的 UserDetailsService 注入到 Spring OAuth2 中
                // 没有此配置，在 refresh_token 时候 TokenEndpoint 会报错 Handling error: IllegalStateException, UserDetailsService is required.
                .userDetailsService(userDetailsService)
                // 注入自定义配置的 TokenServices 到 Spring OAuth2 中，否则 Spring OAuth2 会自动创建一个新的 TokenServices
                .tokenServices(tokenServices());
    }

    // 配置授权服务器端点安全
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                // 访问 /oauth/check_token 端点允许所有客户端
                .checkTokenAccess("permitAll()");
    }

    // 配置自定义的 TokenServices 以支持 access token 刷新、自定义 token 有效期
    @Bean
    AuthorizationServerTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        // 需要注入 TokenStore，否则在启动时报告 tokenStore must be set 错误
        defaultTokenServices.setTokenStore(tokenStore());
        // 支持 access_token 刷新
        defaultTokenServices.setSupportRefreshToken(true);
        // 设置token有效期为7200秒，也就是两个小时
        // defaultTokenServices.setAccessTokenValiditySeconds(7200);
        defaultTokenServices.setAccessTokenValiditySeconds(2);
        // 设置refresh_token有效期为3天
        // defaultTokenServices.setRefreshTokenValiditySeconds(259200);
        defaultTokenServices.setRefreshTokenValiditySeconds(10);
        return defaultTokenServices;
    }
}
