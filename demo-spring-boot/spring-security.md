# `spring-security`使用

## 工作原理

### 标准的用户+密码登录时序图

![标准的用户+密码登录时序图.drawio](https://public-images-fut.oss-cn-hangzhou.aliyuncs.com/%E6%A0%87%E5%87%86%E7%9A%84%E7%94%A8%E6%88%B7%2B%E5%AF%86%E7%A0%81%E7%99%BB%E5%BD%95%E6%97%B6%E5%BA%8F%E5%9B%BE.drawio.png)

上面的“标准的用户+密码登录时序图”是使用 [spring-security-restful-login-token](https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-security/spring-security-restful-login-token) 示例和 [博客](https://blog.csdn.net/yuanlaijike/article/details/86164160) 协助查看源码分析画出的，下是借助时序图对源码的分析：

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

    

## 配置`Spring Security`依赖

### 基于非`SpringBoot`项目配置

详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-security/demo-spring-security-without-springboot`

WebSecurityConfig

```java
// Spring Security 配置类
// 开启Spring Security的Web安全支持
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    // 定义用户信息服务
    @Bean
    protected UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("abc1").password("123456").authorities("p1").build());
        manager.createUser(User.withUsername("abc2").password("123456").authorities("p2").build());
        return manager;
    }

    // 密码编码器
    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    // 安全拦截配置
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 定义哪些URL路径需要被保护，以及这些路径应该应用哪些安全规则。通过这个方法，你可以指定哪些角色或权限的用户可以访问特定的资源。
        http.authorizeRequests()
                // /r/** 路径下的所有资源都需要身份认证后才能访问
                .antMatchers("/r/**").authenticated()
                // 其他所有请求都可以访问
                .anyRequest().permitAll()
                // 表单登录配置
                .and()
                .formLogin()
                // 自定义登录成功的页面地址
                .successForwardUrl("/login-success");
    }
}

```

Spring 应用初始化时加载 WebSecurityConfig 配置

```java
// SpringApplicationInitializer 类等价于 xml 配置的 web.xml 配置文件
public class SpringApplicationInitializer
        extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        // 加载 @ComponentScan 配置
        return new Class[]{ApplicationConfig.class, WebSecurityConfig.class};
    }
    
    ...
}
```

Spring Security 初始化类

```java
package com.future.demo.init;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class SpringSecurityApplicationInitializer extends AbstractSecurityWebApplicationInitializer {
}
```

运行示例

```bash
mvn tomcat7:run
```

未登录前访问`http://localhost:8080/r/r1`资源会被从定向到登录界面

测试 abc1 没有权限访问资源 /r/r2

- 使用 abc1 登录`http://localhost:8080/login`
- 成功访问资源 /r/r1 `http://localhost:8080/r/r1`
- 访问资源 /r/r2 失败 `http://localhost:8080/r/r2`，报告 403 错误

访问`http://localhost:8080/logout`退出登录



### 基于`SpringBoot`项目配置

详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-security/demo-spring-security-with-springboot`

pom 依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

WebSecurityConfig 配置

```java
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    // 定义用户信息服务
    @Bean
    protected UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("abc1").password("123456").authorities("p1").build());
        manager.createUser(User.withUsername("abc2").password("123456").authorities("p2").build());
        return manager;
    }

    // 密码编码器
    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    // 安全拦截配置
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 定义哪些URL路径需要被保护，以及这些路径应该应用哪些安全规则。通过这个方法，你可以指定哪些角色或权限的用户可以访问特定的资源。
        http.authorizeRequests()
                // /r/r1 路径下的资源需要拥有 p1 权限的用户才能访问
                .antMatchers("/r/r1").hasAuthority("p1")
                // /r/r2 路径下的资源需要拥有 p2 权限的用户才能访问
                .antMatchers("/r/r2").hasAuthority("p2")
                // /r/** 路径下的所有资源都需要身份认证后才能访问
                .antMatchers("/r/**").authenticated()
                // 其他所有请求都可以访问
                .anyRequest().permitAll()
                // 表单登录配置
                .and()
                .formLogin()
                // 自定义登录成功的页面地址
                .successForwardUrl("/login-success");
    }
}
```

未登录前访问`http://localhost:8080/r/r1`资源会被从定向到登录界面

测试 abc1 没有权限访问资源 /r/r2

- 使用 abc1 登录`http://localhost:8080/login`
- 成功访问资源 /r/r1 `http://localhost:8080/r/r1`
- 访问资源 /r/r2 失败 `http://localhost:8080/r/r2`，报告 403 错误

访问`http://localhost:8080/logout`退出登录



## `AuthenticationManagerBuilder`使用

自定义身份验证管理器（`AuthenticationManager`）的构建过程的方法。它通常在你扩展 `WebSecurityConfigurerAdapter` 并重写其 `configure(AuthenticationManagerBuilder auth)` 方法时使用。

`AuthenticationManagerBuilder` 是一个用于构建 `AuthenticationManager` 的构建器接口。`AuthenticationManager` 是 Spring Security 的核心组件之一，它负责处理身份验证请求，验证用户提供的凭据（如用户名和密码），并返回一个已认证的 `Authentication` 对象（如果凭据有效）或抛出异常（如果凭据无效）。

通过 `configure(AuthenticationManagerBuilder auth)` 方法，你可以：

1. **定义用户详细信息服务**：你可以使用 `inMemoryAuthentication()`、`jdbcAuthentication()`、`ldapAuthentication()` 或 `userDetailsService()` 等方法来定义如何加载和验证用户凭据。例如，如果你使用基于内存的用户存储，你可以使用 `inMemoryAuthentication()` 来定义一些硬编码的用户；如果你使用数据库存储用户信息，你可以使用 `jdbcAuthentication()` 并配置数据源和查询语句；如果你有一个自定义的 `UserDetailsService` 实现，你可以使用 `userDetailsService()` 方法来指定它。
2. **配置密码编码**：你可以使用 `passwordEncoder()` 方法来配置密码编码器（如 BCrypt、PBKDF2 等），以确保用户密码在存储和验证时的安全性。
3. **自定义身份验证流程**：虽然这通常不是通过 `AuthenticationManagerBuilder` 直接完成的，但你可以通过配置其他组件（如 `AuthenticationProvider`）来影响身份验证流程。

`AuthenticationManagerBuilder`详细用法请参考`https://gitee.com/dexterleslie/demonstration/blob/master/demo-spring-boot/demo-spring-security/spring-security-form-login/src/main/java/com/future/demo/WebSecurityConfig.java`

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

> 例子详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-security/spring-security-user-and-password-datasource`

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



## 会话管理

Spring Security的会话管理是其安全框架中的一个重要组成部分，它涉及用户会话的创建、维护、认证、授权以及安全性保护等多个方面。以下是对Spring Security会话管理的详细阐述：

一、会话管理的基本概念

在Web应用程序中，会话（Session）是一种在客户端和服务器之间保持用户状态的一种机制。由于HTTP协议本身是无状态的，即每次请求都是独立的，服务器不会记住任何关于客户端的信息。因此，为了实现会话的概念，服务器需要在客户端和服务器之间建立一个持续的状态，以便能够识别同一个用户的后续请求。

Spring Security作为Java应用的安全框架，提供了强大的会话管理功能，以保护应用程序中的用户数据和系统资源。

二、会话管理的核心功能

1. **会话创建**：根据应用程序的需求和配置，Spring Security可以自动创建会话，或者在需要时手动创建。
2. **会话认证**：验证用户身份的过程，确保只有合法用户才能访问受保护的资源。
3. **会话授权**：根据用户的角色和权限，控制用户访问特定资源或执行特定操作。
4. **会话保持**：在多次请求之间保持用户状态，以便用户可以无缝地浏览应用程序的不同部分。
5. **会话过期**：为会话设置一个过期时间，当超过该时间未活动时，会话将被终止。这有助于防止未授权访问和潜在的安全风险。
6. **会话安全**：保护会话免受各种安全威胁，如会话固定攻击、会话劫持等。

三、会话管理的实现方式

1. **基于Session的会话管理**：
   - 服务器端生成用户相关数据并保存在Session中。
   - 在给客户端的Cookie中放入Session ID，客户端请求时带上Session ID，以验证服务端是否有对应的Session。
   - 当用户退出或Session过期时，Session ID将无效。
2. **基于Token的会话管理**：
   - 认证成功后，服务器端生成Token并发给客户端。
   - 客户端将Token存储在Cookie或本地存储中，并在每次请求时带上Token。
   - 服务器端收到请求后，验证Token的有效性，并根据Token中的信息识别用户身份和权限。

四、会话管理的配置

在Spring Security中，会话管理的配置通常在`SecurityConfig`类中进行。以下是一些常见的配置选项：

1. **sessionCreationPolicy**：定义会话创建策略，例如`STATELESS`（无状态，不使用Session）、`IF_REQUIRED`（仅在需要时才创建Session）等。
2. **maximumSessions**：设置单个用户的最大会话数。如果超过此限制，新的会话请求将被拒绝或旧会话将被强制下线。
3. **maxSessionsPreventsLogin**：设置为`true`时，如果达到最大会话数，新的登录请求将被阻止；设置为`false`时，将允许新登录，但旧会话会被强制下线。
4. **expiredUrl**：指定会话过期后的重定向页面。当用户尝试访问过期会话时，将被重定向到该页面。
5. **invalidSessionUrl**：指定无效会话时的重定向页面。当用户尝试访问无效的Session ID时，将被重定向到该页面。
6. **sessionFixation**：配置会话固定保护策略。例如，`migrateSession`表示在会话固定攻击发生时，将保留会话内容但生成新的会话ID。

五、会话管理的注意事项

1. **防止会话固定攻击**：会话固定攻击是一种攻击方式，攻击者通过猜测或窃取用户的会话ID来获得访问权限。为了防止这种攻击，Spring Security提供了会话固定保护策略，如`migrateSession`等。
2. **管理会话超时**：合理设置会话超时时间可以防止未授权访问和潜在的安全风险。同时，也需要注意在会话过期前提醒用户重新登录或保存工作。
3. **处理并发会话**：在多个设备或浏览器上同时登录同一用户时，需要处理并发会话问题。可以通过配置`maximumSessions`和`maxSessionsPreventsLogin`等选项来控制并发会话的数量和行为。
4. **保护会话数据**：会话中存储的数据可能包含敏感信息，如用户身份、权限等。因此，需要采取措施保护会话数据的安全性，如使用HTTPS加密传输、防止会话劫持等。

综上所述，Spring Security提供了强大的会话管理功能，可以根据应用程序的需求进行灵活配置。通过合理配置和使用这些功能，可以有效地保护应用程序中的用户数据和系统资源的安全。



### 获取用户身份

详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-security/spring-security-form-login`

```java
@RequestMapping(value = "/")
public String index(Model model, Principal principal) {
    model.addAttribute("username", principal.getName());
    return "welcome";
}
```



### 会话创建策略

Spring Security的`SessionCreationPolicy`是一个枚举类，用于配置会话（Session）的创建行为。它决定了Spring Security何时以及如何创建HTTP会话。以下是`SessionCreationPolicy`各个选项的使用场景：

1. **ALWAYS**：
   - **使用场景**：每次请求都会创建一个新的会话，无论是否必要。这通常用于需要跟踪用户会话状态的应用场景，如在线购物网站、在线银行等，这些场景需要随时记录用户的操作状态。
   - **注意事项**：这种策略可能会导致服务器资源的大量消耗，因为每个请求都会创建一个新的会话。因此，在使用时需要谨慎考虑。
2. **IF_REQUIRED**（默认值）：
   - **使用场景**：仅在需要时才创建会话，例如，当用户登录时。这是大多数应用的默认行为，适用于大多数Web应用程序。
   - **优点**：只有在需要时才会创建会话，避免了不必要的资源消耗。
   - **缺点**：如果应用程序需要跟踪用户的会话状态，则可能需要手动管理会话的创建和销毁。
3. **NEVER**：
   - **使用场景**：Spring Security不会创建会话，但如果应用程序本身创建了会话，Spring Security会使用它。这通常用于无状态应用或API，如RESTful服务，这些服务通常不需要跟踪用户的会话状态。
   - **优点**：减少了服务器的资源消耗，因为不需要为每个用户维护会话状态。
   - **缺点**：无法跟踪用户的会话状态，可能限制了某些功能，如基于会话的用户跟踪和状态管理。
4. **STATELESS**：
   - **使用场景**：Spring Security不仅不会创建会话，而且也不会使用任何现有的会话。这适用于完全无状态的应用，如RESTful API，这些API通常通过其他机制（如令牌、OAuth等）来验证用户身份和授权。
   - **优点**：进一步减少了服务器的资源消耗，并且提高了应用程序的安全性，因为攻击者无法利用会话劫持等攻击手段。
   - **缺点**：完全无状态的应用可能需要额外的机制来验证用户身份和授权，增加了开发的复杂性。

在选择`SessionCreationPolicy`时，需要考虑应用程序的类型、安全性和性能等因素。对于需要跟踪用户会话状态的应用，可以选择`ALWAYS`或`IF_REQUIRED`；对于无状态应用或API，可以选择`NEVER`或`STATELESS`。同时，还需要根据应用程序的具体需求和场景来做出最佳决策。



详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-security/spring-security-form-login`

```java
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                ...
            
                // 会话管理配置
                .and()
                // 指定会话创建策略，这里是 IF_REQUIRED 表示只有在需要时才创建会话
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
    }
}
```



## 退出

详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-security/spring-security-form-login`



## 授权

### 使用`HttpSecurity`编程式配置

详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-security/spring-security-authorization`

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,/* 开启secured注解判断是否拥有角色 */
        prePostEnabled = true/* 开启preAuthorize注解 */)
public class ConfigWebSecurity extends WebSecurityConfigurerAdapter {

    @Resource
    TokenAuthenticationFilter tokenAuthenticationFilter;
    @Resource
    TokenStore tokenStore;
    @Resource
    PasswordEncoder passwordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                ...
            
                .and()
                .authorizeRequests()
                // 同时拥有r1和r2角色的用户都可以调用次方法。
                .antMatchers("/api/v1/test3").access("hasRole('r1') and hasRole('r2')")
                // 拥有权限 auth:test5才能调用此方法。
                .antMatchers("/api/v1/test5").hasAuthority("perm:test5")
                .antMatchers("/api/auth/login").permitAll()
                .anyRequest().authenticated();
    }
}
```



### `@Secured`注解

详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-security/spring-security-authorization`

启用`@Secured`需要配置`securedEnabled = true`

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,/* 开启secured注解判断是否拥有角色 */
        prePostEnabled = true/* 开启preAuthorize注解 */)
public class ConfigWebSecurity extends WebSecurityConfigurerAdapter {
```

```java
// 拥有r1或者r2角色的用户都可以调用次方法。另外需要注意的是这里匹配的字符串需要添加前缀"ROLE_"
@Secured(value = {"ROLE_r1", "ROLE_r2"})
@GetMapping(value = "test1")
public ObjectResponse<String> test1() {
    return ResponseUtils.successObject("成功调用接口/api/v1/test1");
}
```



### `@PreAuthorize`注解

详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-security/spring-security-authorization`

启用`@Secured`需要配置`prePostEnabled = true`

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,/* 开启secured注解判断是否拥有角色 */
        prePostEnabled = true/* 开启preAuthorize注解 */)
public class ConfigWebSecurity extends WebSecurityConfigurerAdapter {
```

```java
// 拥有r1或者r2角色的用户都可以调用次方法。
// 和前面@Secured(value = {"ROLE_r1", "ROLE_r2"})等价
@PreAuthorize("hasAnyRole('r1','r2')")
@GetMapping(value = "test2")
public ObjectResponse<String> test2() {
    return ResponseUtils.successObject("成功调用接口/api/v1/test2");
}
```



### `@Secured`和`@PreAuthorize`区别

@Secured和@PreAuthorize都是Spring Security框架中用于方法安全性的注解，它们允许开发者定义哪些用户或角色有权限调用特定的方法。尽管这两个注解的目的相同，但它们提供了不同的功能和表达方式，主要区别如下：

**使用方式与灵活性**

- **@Secured**：
  - 允许在方法上定义角色名称，以确保只有具有指定角色的用户可以访问该方法。
  - 不能使用Spring Expression Language（SpEL）表达式，只能指定角色名称。
  - 如果用户没有满足注解内指定的角色之一，方法调用会被拒绝。
  - 示例代码：`@Secured("ROLE_ADMIN")` 或 `@Secured({"ROLE_USER","ROLE_ADMIN"})`。
- **@PreAuthorize**：
  - 在方法调用之前进行安全检查。
  - 支持SpEL表达式，这使得开发者可以编写更复杂的安全条件。
  - 可以实现基于方法参数的动态安全表达式。
  - 示例代码：`@PreAuthorize("hasRole('ROLE_USER')")` 或 `@PreAuthorize("#userId == authentication.principal.id")`。

**配置与启用**

- **@Secured**：
  - 在Spring Security配置中，需要启用@Secured注解的支持，通常通过`.securedEnabled(true)`来配置。
- **@PreAuthorize**：
  - 在Spring Security配置中，需要启用@PreAuthorize注解的支持，通常通过`.prePostEnabled(true)`来配置。

**适用场景**

- **@Secured**：
  - 适用于简单的角色基础的访问控制。
  - 当只需要基于角色进行访问控制，且不需要复杂的表达式时，@Secured更加简洁明了。
- **@PreAuthorize**：
  - 适用于更复杂的、基于表达式的访问控制。
  - 当需要基于用户属性、方法参数或其他动态条件进行访问控制时，@PreAuthorize更加灵活和强大。

综上所述，@Secured和@PreAuthorize各有优缺点，开发者应根据具体需求和场景选择合适的注解来实现方法级别的安全性控制。



## 跨域配置

详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-security/demo-spring-security-cors`

```java
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
```

测试跨域配置

```bash
curl -H "Origin: abc.com" -H "Access-Control-Request-Method: GET" -H "Access-Control-Request-Headers: accept, content-type" -X OPTIONS --verbose  http://localhost:8080/
```

- 服务器会返回 Access-Control-Allow-Origin: abc.com、Access-Control-Allow-Methods: GET,HEAD,POST、Access-Control-Allow-Headers: accept, content-type、Access-Control-Max-Age: 1800、Allow: GET, HEAD, POST, PUT, DELETE, OPTIONS, PATCH 等响应头表示支持跨域



## 示例自定义登录界面

> 登录才能访问受保护界面`https://spring.io/guides/gs/securing-web/`

示例详细配置请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-security/spring-security-form-login`、`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-security/spring-security-customize-login`

启动例子后，访问`http://localhost:18080`按照提示操作即可了解自定义登录界面特性。



## 示例`restful`登录配置

示例详细配置请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-security/spring-security-restful-login`



## 示例同时支持密码、短信验证码登录

>示例使用`spring-security`做登录统一网关，包括：获取登录验证码、手机号码+短信验证码登录、手机号码、用户名、邮箱+密码登录
>
>SpringBoot 集成 Spring Security（8）——短信验证码登录`https://blog.csdn.net/yuanlaijike/article/details/86164160`

示例详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-security/spring-security-unify-gateway`

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

