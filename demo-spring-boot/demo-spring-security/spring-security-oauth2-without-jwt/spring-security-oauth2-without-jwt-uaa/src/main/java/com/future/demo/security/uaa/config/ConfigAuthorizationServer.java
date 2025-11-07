package com.future.demo.security.uaa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
// 启用授权服务器
@EnableAuthorizationServer
public class ConfigAuthorizationServer extends AuthorizationServerConfigurerAdapter {

    // 所有客户端密码
    private final static String ClientSecret = "123";

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    TokenStore tokenStore;
    @Autowired
    AuthorizationCodeServices authorizationCodeServices;
    @Autowired
    ClientDetailsService clientDetailsService;

    // 配置客户端详情服务(ClientDetailsService),ClientDetailsService负责查找ClientDetails
    // 支持InMemoryClientDetailsService和JdbcClientDetailsService客户端详情服务
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("client1")
                .secret(passwordEncoder.encode(ClientSecret))
                // 客户端允许访问的资源id，多个资源用逗号隔开，默认为空，即所有资源都可以访问
                /*.resourceIds("resource1")*/
                // authorization_code 授权码模式，，跳转到登录页面需要用户登录后并授权后才能够获取token
                // implicit 静默授权模式，跳转到登录页面需要用户登录后并授权才能够获取token
                // refresh_token配置了在请求令牌时才会颁发刷新令牌，否则只颁发访问令牌
                .authorizedGrantTypes("authorization_code", "implicit", "refresh_token")
                // 允许的授权范围
                .scopes("all", "api", "read", "write")
                // false表示用户登录成功后跳转到授权确认页面等待用户确认授权
                .autoApprove(false)
                // 同意或者拒绝授权的302重定向地址
                .redirectUris("http://www.baidu.com")
                // 设置各个客户端的 access_token 和 refresh_token 有效期，单位秒
                /*.accessTokenValiditySeconds(3600)
                .refreshTokenValiditySeconds(3600)*/

                .and()
                .withClient("client2")
                .secret(passwordEncoder.encode(ClientSecret))
                .resourceIds("order-service")
                // password 密码模式，用户提供账号密码后不需要登录授权直接获取token
                .authorizedGrantTypes("password", "refresh_token")
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
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                // 把 TokenStore 注入到 Spring OAuth2 中，否则 token 不会保存在 Redis 中
                .tokenStore(tokenStore)
                // 密码模式，用于校验账号密码并颁发token
                .authenticationManager(authenticationManager)
                // 授权码模式，用于授权码相关配置
                // 支持InMemoryAuthorizationCodeServices、JdbcAuthorizationCodeServices、RandomValueAuthorizationCodeServices
                .authorizationCodeServices(authorizationCodeServices)
                // 把 Spring Security 的 UserDetailsService 注入到 Spring OAuth2 中
                // 没有此配置，在 refresh_token 时候 TokenEndpoint 会报错 Handling error: IllegalStateException, UserDetailsService is required.
                .userDetailsService(userDetailsService)
                // 令牌管理服务
                .tokenServices(tokenServices())
                // 自定义授权确认页面路径
                .pathMapping("/oauth/confirm_access", "/custom/confirm");
    }

    // 配置令牌管理服务
    @Bean
    AuthorizationServerTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        // 令牌存储的地方
        // 需要注入 TokenStore，否则在启动时报告 tokenStore must be set 错误
        defaultTokenServices.setTokenStore(tokenStore);
        // 是否生成刷新令牌并支持 access_token 刷新
        defaultTokenServices.setSupportRefreshToken(true);
        // 设置Access Token有效期为7200秒，也就是两个小时
        defaultTokenServices.setAccessTokenValiditySeconds(7200);
        //defaultTokenServices.setAccessTokenValiditySeconds(2);
        // 设置Refresh Token有效期为3天
        defaultTokenServices.setRefreshTokenValiditySeconds(259200);
        //defaultTokenServices.setRefreshTokenValiditySeconds(10);
        // 设置客户端详情服务
        defaultTokenServices.setClientDetailsService(clientDetailsService);
        return defaultTokenServices;
    }

    /**
     * 存储在内存中的授权码
     *
     * @return
     */
    @Bean
    AuthorizationCodeServices authorizationCodeServices() {
        return new InMemoryAuthorizationCodeServices();
    }

    // 配置授权服务器端点安全约束
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security
                // 访问 /oauth/check_token 端点允许所有客户端
                .checkTokenAccess("permitAll()");
    }
}
