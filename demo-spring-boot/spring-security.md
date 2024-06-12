# `spring-security`使用

## 工作原理

### 标准的用户+密码登录时序图

![标准的用户+密码登录时序图.drawio](https://public-images-fut.oss-cn-hangzhou.aliyuncs.com/%E6%A0%87%E5%87%86%E7%9A%84%E7%94%A8%E6%88%B7%2B%E5%AF%86%E7%A0%81%E7%99%BB%E5%BD%95%E6%97%B6%E5%BA%8F%E5%9B%BE.drawio.png)

上面的“标准的用户+密码登录时序图”是使用 [spring-security-restful-login-token](https://github.com/dexterleslie1/demonstration/tree/master/demo-spring-boot/demo-spring-security/spring-security-restful-login-token) 示例和 [博客](https://blog.csdn.net/yuanlaijike/article/details/86164160) 协助查看源码分析画出的，下是借助时序图对源码的分析：

- `UsernamePasswordAuthenticationFilter`功能如下：

  - 拦截匹配的登录`url`，通过查看源码得知此`url`为`/login`，`http`方法为`POST`

    ```java
    public UsernamePasswordAuthenticationFilter() {
        // 登录url为/login
        // http请求方法为POST
    	super(new AntPathRequestMatcher("/login", "POST"));
    }
    ```

  - 获取登录请求中的帐号和密码

  - 使用此帐号和密码构造`UsernamePasswordAuthenticationToken`实例后，调用`AuthenticationManager`的`authenticate`方法进行下一步登录请求的帐号和密码校验

    ```java
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            // 获取登录请求中的帐号
            String username = this.obtainUsername(request);
            // 获取登录请求中的密码
            String password = this.obtainPassword(request);
            if (username == null) {
                username = "";
            }
    
            if (password == null) {
                password = "";
            }
    
            username = username.trim();
            // 使用登录帐号和密码构造UsernamePasswordAuthenticationToken实例
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
            this.setDetails(request, authRequest);
            // 使用token实例调用AuthenticationManager的authenticate方法进行登录帐号和密码的校验
            return this.getAuthenticationManager().authenticate(authRequest);
        }
    }
    ```

    

- `AuthenticationManager`的实现类`ProviderManager`功能如下：

  - `authenticate`方法中通过`AuthenticationProvider`列表找到支持处理`UsernamePasswordAuthenticationToken`的`AuthenticationProvider`

  - 调用`AuthenticationProvider`的`authenticate`方法进一步校验

    ```java
    public Authentication authenticate(Authentication authentication)
    			throws AuthenticationException {
        Class<? extends Authentication> toTest = authentication.getClass();
        // 代码省略 ...
    
        // 遍历AuthenticationProvider列表
        for (AuthenticationProvider provider : getProviders()) {
            // 判断AuthenticationProvider是否支持处理UsernamePasswordAuthenticationToken
            if (!provider.supports(toTest)) {
                continue;
            }
    
            // 代码省略 ...
    
            try {
                // 如果AuthenticationProvider支持处理当前token，则调用其authenticate方法进一步校验
                result = provider.authenticate(authentication);
    
                if (result != null) {
                    copyDetails(authentication, result);
                    break;
                }
            }
            catch (AccountStatusException | InternalAuthenticationServiceException e) {
                prepareException(e, authentication);
                // SEC-546: Avoid polling additional providers if auth failure is due to
                // invalid account status
                throw e;
            } catch (AuthenticationException e) {
                lastException = e;
            }
        }
    
        // 代码省略 ...
    }
    ```

    

- `AuthenticationProvider`实现类`DaoAuthenticationProvider`功能如下：

  - 支持处理`UsernamePasswordAuthenticationToken`

    ```java
    public boolean supports(Class<?> authentication) {
        // 支持处理UsernamePasswordAuthenticationToken
        return (UsernamePasswordAuthenticationToken.class
                .isAssignableFrom(authentication));
    }
    ```

  - 调用`UserDetailsService`从数据源加载用户信息

  - 校验登录帐号和密码是否正确

  - 创建成功登录后的`Authentication`实例

    ```java
    // 加载用户信息、校验登录帐号和密码、成功登录后创建Authentication实例
    public Authentication authenticate(Authentication authentication)
    			throws AuthenticationException {
        // 代码省略 ...
    
        if (user == null) {
            cacheWasUsed = false;
    
            try {
                // 从数据源查询用户信息
                user = retrieveUser(username,
                                    (UsernamePasswordAuthenticationToken) authentication);
            }
            catch (UsernameNotFoundException notFound) {
                // 代码省略 ...
            }
    
            // 代码省略 ...
        }
    
        try {
            // 代码省略 ...
            
            // 校验登录帐号和密码是否正确
            additionalAuthenticationChecks(user,
                                           (UsernamePasswordAuthenticationToken) authentication);
        }
        catch (AuthenticationException exception) {
            // 代码省略 ...
        }
    
        // 代码省略 ...
    
        // 成功后创建Authentication实例
        return createSuccessAuthentication(principalToReturn, authentication, user);
    }
    
    // 调用UserDetailsService从数据源加载用户信息
    protected final UserDetails retrieveUser(String username,
    			UsernamePasswordAuthenticationToken authentication)
    			throws AuthenticationException {
        // 代码省略 ...
        
        try {
            // 调用UserDetailsService从数据源加载用户信息
            UserDetails loadedUser = this.getUserDetailsService().loadUserByUsername(username);
             // 代码省略 ...
        }
         // 代码省略 ...
    }
    
    // 校验登录帐号和密码是否正确
    @SuppressWarnings("deprecation")
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication)
        throws AuthenticationException {
        // 代码省略 ...
    
        String presentedPassword = authentication.getCredentials().toString();
    
        // 调用PasswordEncoder校验登录密码
        if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            // 代码省略 ...
    
            // 如果登录帐号和密码不正确，则抛出BadCredentialsException异常
            throw new BadCredentialsException(messages.getMessage(
                "AbstractUserDetailsAuthenticationProvider.badCredentials",
                "Bad credentials"));
        }
    }
    
    // 成功登录后构造Authentication实例
    protected Authentication createSuccessAuthentication(Object principal,
    			Authentication authentication, UserDetails user) {
        // Ensure we return the original credentials the user supplied,
        // so subsequent attempts are successful even with encoded passwords.
        // Also ensure we return the original getDetails(), so that future
        // authentication events after cache expiry contain the details
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
            principal, authentication.getCredentials(),
            authoritiesMapper.mapAuthorities(user.getAuthorities()));
        result.setDetails(authentication.getDetails());
    
        return result;
    }
    ```

- `AuthenticationSuccessHandler`功能如下：

  - 登录成功后的客户端响应

    ```java
    AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                Long userId = ((CustomizeUserDetails) (authentication).getPrincipal()).getUserId();
                String token = UUID.randomUUID().toString();
                Map<String, Object> mapReturn = new HashMap<>();
                mapReturn.put("userId", userId);
                mapReturn.put("loginname", authentication.getName());
                mapReturn.put("token", token);
                CustomizeUser customizeUser = new CustomizeUser(userId, ((CustomizeUserDetails) authentication.getPrincipal()).getAuthorities());
                WebSecurityConfig.this.tokenStore.store(token, customizeUser);
                ResponseUtils.writeSuccessResponse(response, mapReturn);
            }
        };
    }
    ```

    

- `AuthenticationFailureHandler`功能如下：

  - 登录失败后客户端响应

    ```java
    AuthenticationFailureHandler authenticationFailureHandler() {
        return new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                ResponseUtils.writeFailResponse(response, HttpServletResponse.SC_UNAUTHORIZED, ErrorCodeConstant.ErrorCodeCommon, exception.getMessage());
            }
        };
    }
    ```

    

## `spring-boot`项目配置`spring-security`依赖

> 详细配置请参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-spring-boot/demo-spring-security/spring-security-form-login)

`maven`配置`spring-security`依赖，配置`org.springframework.boot:spring-boot-starter-security`依赖后默认自动启用`spring-security`机制

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.7.RELEASE</version>
    </parent>

    <groupId>com.future.demo</groupId>
    <artifactId>spring-security-form-login</artifactId>
    <version>1.0.0</version>
    <packaging>war</packaging>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
    </dependencies>
</project>
```

## `AuthenticationManagerBuilder`使用

> `AuthenticationManagerBuilder`详细用法请参考 [链接](https://github.com/dexterleslie1/demonstration/blob/master/demo-spring-boot/demo-spring-security/spring-security-form-login/src/main/java/com/future/demo/WebSecurityConfig.java)
>
> 自定义身份验证管理器（`AuthenticationManager`）的构建过程的方法。它通常在你扩展 `WebSecurityConfigurerAdapter` 并重写其 `configure(AuthenticationManagerBuilder auth)` 方法时使用。
>
> `AuthenticationManagerBuilder` 是一个用于构建 `AuthenticationManager` 的构建器接口。`AuthenticationManager` 是 Spring Security 的核心组件之一，它负责处理身份验证请求，验证用户提供的凭据（如用户名和密码），并返回一个已认证的 `Authentication` 对象（如果凭据有效）或抛出异常（如果凭据无效）。
>
> 通过 `configure(AuthenticationManagerBuilder auth)` 方法，你可以：
>
> 1. **定义用户详细信息服务**：你可以使用 `inMemoryAuthentication()`、`jdbcAuthentication()`、`ldapAuthentication()` 或 `userDetailsService()` 等方法来定义如何加载和验证用户凭据。例如，如果你使用基于内存的用户存储，你可以使用 `inMemoryAuthentication()` 来定义一些硬编码的用户；如果你使用数据库存储用户信息，你可以使用 `jdbcAuthentication()` 并配置数据源和查询语句；如果你有一个自定义的 `UserDetailsService` 实现，你可以使用 `userDetailsService()` 方法来指定它。
> 2. **配置密码编码**：你可以使用 `passwordEncoder()` 方法来配置密码编码器（如 BCrypt、PBKDF2 等），以确保用户密码在存储和验证时的安全性。
> 3. **自定义身份验证流程**：虽然这通常不是通过 `AuthenticationManagerBuilder` 直接完成的，但你可以通过配置其他组件（如 `AuthenticationProvider`）来影响身份验证流程。

下面是一个简单的例子，展示了如何使用 `configure(AuthenticationManagerBuilder auth)` 方法来配置基于内存的用户存储和 BCrypt 密码编码器：

```java
@Configuration  
@EnableWebSecurity  
public class SecurityConfig extends WebSecurityConfigurerAdapter {  
  
    @Autowired  
    public void configure(AuthenticationManagerBuilder auth) throws Exception {  
        auth  
            .inMemoryAuthentication()  
                .withUser("user").password(passwordEncoder().encode("password")).roles("USER")  
                .and()  
                .withUser("admin").password(passwordEncoder().encode("admin")).roles("USER", "ADMIN");  
    }  
  
    @Bean  
    public PasswordEncoder passwordEncoder() {  
        return new BCryptPasswordEncoder();  
    }  
  
    // ... 其他配置 ...  
}
```

在这个例子中，我们定义了两个用户（"user" 和 "admin"），并使用 BCrypt 对密码进行了编码。注意，`passwordEncoder()` 方法是在 `SecurityConfig` 类中作为一个 Bean 定义的，并通过自动装配（`@Autowired`）注入到 `configureGlobal` 方法中。

下面是一个简单的例子，展示了如何使用 `configure(AuthenticationManagerBuilder auth)` 方法来配置自定义用户信息数据源：

```java
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    // ... 其他配置 ...
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 自定义用户信息数据源，提供用户信息给验证框架校验
        auth.userDetailsService(userDetailsService());
    }
    
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static class UserDetailService implements UserDetailsService {
        @Resource
        private PasswordEncoder passwordEncoder;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            String password = this.passwordEncoder.encode("1234567");
            return new User(username, password, Collections.emptyList());
        }
    }
}
```

## 用户和密码数据源配置

> 例子详细用法请参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-spring-boot/demo-spring-security/spring-security-user-and-password-datasource)

### `UserDetailsService`方式

> 可通过此方式从数据库读取用户信息，甚至可以从任何其他数据源读取用户信息。

`MyUserDetailsService`

```java
@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    PasswordEncoder passwordEncoder;

    // 使用UserDetailsService自定义加载用户数据
    // 可用于从数据库自定义加载用户信息
    //
    // 使用正则自动识别手机、邮箱、用户名实现支持三个字段登录
    // https://blog.csdn.net/qq_41589293/article/details/82953674
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new User("user3", this.passwordEncoder.encode("123456"), AuthorityUtils.commaSeparatedStringToAuthorityList("role3"));
    }
}
```



### `application.properties`文件配置方式

> 此方式通常用于测试用途，注意: 不需要配置`WebSecurityConfigurerAdapter`和配置`PasswordEncoder`(否则登录时报错)

`application.properties`配置如下：

```properties
# 使用配置文件配置用户密码
# 注意: 不需要配置WebSecurityConfigurerAdapter和配置PasswordEncoder(否则登录时报错)
spring.security.user.name=user1
spring.security.user.password=123456
spring.security.user.roles=role1
```



### 通过配置类临时内存存储

> 此方式通常用于测试用途，用户的信息存储于内存中。

配置如下：

```java
@Configuration
public class ConfigInMemoryDatasource extends WebSecurityConfigurerAdapter {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 使用配置类配置用户密码
        // 用户认证信息在内存中临时存放
        auth.inMemoryAuthentication()
                .withUser("user2").password(passwordEncoder.encode("123456")).roles("role2");
    }
}
```



## 示例自定义登录界面

> 示例详细配置请参考 [链接1](https://github.com/dexterleslie1/demonstration/tree/master/demo-spring-boot/demo-spring-security/spring-security-form-login) [链接2](https://github.com/dexterleslie1/demonstration/tree/master/demo-spring-boot/demo-spring-security/spring-security-customize-login)
>
> [登录才能访问受保护界面](https://spring.io/guides/gs/securing-web/)

启动例子后，访问`http://localhost:18080`按照提示操作即可了解自定义登录界面特性。

## 示例`restful`登录配置

> 示例详细配置请参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-spring-boot/demo-spring-security/spring-security-restful-login)

## 示例同时支持密码、短信验证码登录

>示例使用`spring-security`做登录统一网关，包括：获取登录验证码、手机号码+短信验证码登录、手机号码、用户名、邮箱+密码登录
>
>[SpringBoot 集成 Spring Security（8）——短信验证码登录](https://blog.csdn.net/yuanlaijike/article/details/86164160)
>
>示例详细用法请参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-spring-boot/demo-spring-security/spring-security-unify-gateway)

示例通过自定义`CustomizePasswordAuthenticationFilter`、`CustomizePasswordAuthenticationToken`、`CustomizePasswordAuthenticationProvider`、`CustomizePasswordUserDetailsService`实现密码登录，各个组件功能如下：

- `CustomizePasswordAuthenticationFilter`

  - 拦截密码登录`url /api/v1/password/login`
  - 获取登录请求中的帐号和密码
  - 使用帐号密码构造`CustomizePasswordAuthenticationToken`实例
  - 使用`token`实例调用`AuthenticationManager`中的`authenticate`方法

- `CustomizePasswordAuthenticationToken`

  - 用于传递密码登录请求提交的帐号和密码

- `CustomizePasswordAuthenticationProvider`

  - 校验密码登录提供的验证码
  - 调用`CustomizePasswordUserDetailsService`获取密码登录的用户信息
  - 校验登录请求提交的帐号密码是否和数据源中的帐号密码匹配

- `CustomizePasswordUserDetailsService`

  - 根据手机号码、`email`、用户名从数据源中获取用户登录密码和权限信息

- 通过`ConfigWebSecurity`中如下代码配置密码相关组件到`spring security`中

  ```java
  // 允许用户名、手机号码、邮箱+密码登录url
  .and().authorizeRequests().antMatchers("/api/v1/password/login").permitAll()
      // 添加密码登录AuthenticationProvider
      .and().authenticationProvider(customizePasswordAuthenticationProvider)
      // 密码登录AuthenticationFilter
      .addFilterBefore(customizePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
  ```

  

通过自定义`SmsCaptchaAuthenticationFilter`、`SmsCaptchaAuthenticationToken`、`SmsCaptchaAuthenticationProvider`、`SmsCaptchaUserDetailsService`实现短信验证码登录，各个组件功能如下：

- `SmsCaptchaAuthenticationFilter`

  - 拦截短信验证码登录`url /api/v1/sms/login`
  - 获取登录请求中的手机号码和短信验证码
  - 使用手机号码和短信验证码构造`SmsCaptchaAuthenticationToken`实例
  - 使用`token`实例调用`AuthenticationManager`中的`authenticate`方法

- `SmsCaptchaAuthenticationToken`

  - 用于传递短信登录请求提交的手机号码和短信验证码

- `SmsCaptchaAuthenticationProvider`

  - 调用`SmsCaptchaUserDetailsService`获取缓存中的短信验证码和用户权限信息
  - 校验登录请求提交的短信验证码是否和缓存中的短信验证码匹配

- `SmsCaptchaUserDetailsService`

  - 根据手机号码从缓存中获取短信验证码和权限信息

- 通过`ConfigWebSecurity`中如下代码配置密码相关组件到`spring security`中

  ```java
  // 配置手机号码+短信验证码登录
  .and().authorizeRequests().antMatchers("/api/v1/sms/login").permitAll()
      // 添加短信登录AuthenticationProvider
      .and().authenticationProvider(smsCaptchaAuthenticationProvider)
      // 密码登录AuthenticationFilter
      .addFilterBefore(smsCaptchaAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
  ```

  

密码和短信登录使用的公共组件如下：

- `CustomizeAccessDeniedHandler`
  - 权限不足时的处理逻辑
- `CustomizeAuthentication`
  - `spring security`上下文中表示用户已登录的`authentication`对象
- `CustomizeAuthenticationEntryPoint`
  - 用户未登录时处理逻辑
- `CustomizeAuthenticationFailureHandler`
  - 密码登录失败时客户端响应处理逻辑
  - 短信验证码登录失败时客户端响应处理逻辑
- `CustomizeAuthenticationSuccessHandler`
  - 密码登录成功后客户端响应处理逻辑
  - 短信验证码登录成功后客户端响应处理逻辑
- `CustomizeLogoutSuccessHandler`
  - 用户退出成功后客户端响应逻辑
- `CustomizeTokenAuthenticationFilter`
  - 用户请求时`token`拦截，判断用户`token`是否合法
  - 如果`token`合法，创建`CustomizeAuthentication`实例注入到`spring security`上下文中表示用户已经登录
- `CustomizeUser`
  - 用户信息对象，存储用户权限信息
- `ConfigWebSecurity`
  - `spring security`的配置

