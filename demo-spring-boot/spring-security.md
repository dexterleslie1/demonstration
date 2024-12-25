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



#### 自定义 UserDetailsService

>可通过此方式从数据库读取用户信息，甚至可以从任何其他数据源读取用户信息。

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



#### 基于内存的 UserDetailsService

>`https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/in-memory.html`

```java
@Configuration
public class ConfigSecurity {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService users() {
        UserDetails user = User.builder()
                .username("user3")
                .password(this.passwordEncoder().encode("123456"))
                .roles("role3")
                .build();
        return new InMemoryUserDetailsManager(user);
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



## HTTP Basic 验证

详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-http-basic`

配置 Spring Security HTTP Basic

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .anyRequest().authenticated()
                // https://docs.spring.io/spring-security/site/docs/4.2.12.RELEASE/apidocs/org/springframework/security/config/annotation/web/builders/HttpSecurity.html#httpBasic--
                .and().httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // http basic 凭证配置
        auth.inMemoryAuthentication()
                .withUser("client1").password("123").authorities("ROLE_USER");
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
```



## OAuth2.0

### 什么是 OAuth2.0 呢？

OAuth 2.0，全名为“开放授权2.0”（Open Authorization 2.0），是一种开放标准的授权协议，用于授权一个应用程序或服务访问用户在另一个应用程序中的资源，而无需提供用户名和密码。这使得用户可以安全地分享他们的数据资源，同时保持对其数据的控制。以下是对OAuth 2.0的详细解析：

一、主要角色

- **资源所有者**（Resource Owner）：资源所有者是数据的拥有者，他们可以授权其他应用程序来访问他们的资源。
- **客户端**（Client）：客户端是请求访问资源的应用程序。它可以是Web应用、移动应用、桌面应用，甚至是其他服务。
- **授权服务器**（Authorization Server）：授权服务器是资源所有者的服务提供者，负责验证资源所有者的身份并向客户端颁发访问令牌。这通常是第三方身份验证提供商，如Google或Facebook。
- **资源服务器**（Resource Server）：托管受保护资源的服务器。

二、核心概念

- **访问令牌**（Access Token）：访问令牌是客户端用来访问资源服务器上受保护资源的凭证。它是客户端向授权服务器请求的，通常具有一定的时效性。
- **授权代码**（Authorization Code）：授权代码是客户端向授权服务器请求访问令牌的中间凭证。

三、工作原理

1. **注册应用**：客户端必须在授权服务器上注册，并获得一个客户端标识（Client ID）和客户端密码（Client Secret）。
2. **重定向用户**：客户端将用户重定向到授权服务器，以请求授权。
3. **授权授予**：一旦用户同意授权，授权服务器将生成一个授权代码，并将其发送回客户端。
4. **获取访问令牌**：客户端使用授权代码来请求访问令牌。
5. **访问资源**：客户端使用访问令牌来请求资源服务器上的受保护资源。资源服务器验证令牌，如果有效，则提供资源。

四、授权方式

OAuth 2.0协议定义了四种获得令牌的授权方式（authorization grant），具体如下：

- **授权码**（authorization-code）：这是最为复杂但安全系数最高的授权方式。第三方应用先申请一个授权码，然后再用该码获取令牌。这种方式适用于兼具前后端的Web项目。
- **隐藏式**（implicit）：直接向前端颁发令牌，没有授权码这个中间步骤。这种方式不安全，通常用于安全要求不高的场景，并且令牌的有效期非常短。
- **密码式**（password）：用户把用户名和密码直接告诉某个高度信任的应用，该应用就使用用户的密码申请令牌。这种方式通常用在用户对客户端高度信任的情况下。
- **客户端凭证**（client credentials）：客户端以自己的名义，而不是以用户的名义，向服务提供商进行授权。适用于没有前端的命令行应用。

五、应用场景

OAuth 2.0广泛应用于各种场景，包括但不限于：

- **社交登录**：用户可以使用他们的社交媒体账户登录到其他应用程序，例如使用Google或Facebook登录。
- **API访问**：开发人员可以使用OAuth 2.0来访问第三方API，例如使用GitHub API或Twitter API。
- **单点登录**：用户可以使用一个身份验证提供商登录到多个相关的应用程序，而无需多次输入凭证。
- **授权访问**：应用程序可以请求用户授权访问其资源，例如Google云存储或Dropbox。
- **移动应用授权**：移动应用程序可以安全地请求访问用户数据，如照片、联系人或位置信息。

六、安全性

OAuth 2.0的安全性主要通过以下几个关键机制来保证：

- **授权码流程**：可以确保用户的登录凭据不会被泄露给客户端应用，因为授权码的交换是在用户和授权服务器之间进行的。
- **刷新令牌**（Refresh Tokens）：允许使用刷新令牌来获取新的访问令牌，而不需要每次都重新获取用户授权。这可以减少用户与授权服务器的交互次数，提高用户体验。
- **访问令牌的过期时间**：访问令牌有一定的过期时间，这可以防止未授权的第三方长期持有或滥用令牌。
- **互操作性**：OAuth 2.0规范要求所有组件都必须实现一些关键的安全措施，例如使用HTTPS进行通信，以及使用安全的令牌存储方式。
- **安全存储**：对于客户端应用来说，确保令牌的安全存储至关重要。这通常通过使用加密技术和密钥管理服务来实现。
- **保护资源服务器**：确保只有拥有有效令牌的客户端应用可以访问受保护的资源服务器。资源服务器应该验证每个请求的令牌，并拒绝任何无效或过期的令牌。

综上所述，OAuth 2.0是一种强大的身份验证和授权协议，它为开发人员提供了强大的工具，使他们能够创建安全、用户友好的应用程序，并能够与其他应用程序集成。



### OAuth2.0 解决什么问题？

>`https://www.ruanyifeng.com/blog/2019/04/oauth_design.html`



#### 快递员问题

我住在一个大型的居民小区。

小区有门禁系统。

进入的时候需要输入密码。

我经常网购和外卖，每天都有快递员来送货。我必须找到一个办法，让快递员通过门禁系统，进入小区。

如果我把自己的密码，告诉快递员，他就拥有了与我同样的权限，这样好像不太合适。万一我想取消他进入小区的权力，也很麻烦，我自己的密码也得跟着改了，还得通知其他的快递员。

有没有一种办法，让快递员能够自由进入小区，又不必知道小区居民的密码，而且他的唯一权限就是送货，其他需要密码的场合，他都没有权限？



于是，我设计了一套授权机制。

第一步，门禁系统的密码输入器下面，增加一个按钮，叫做"获取授权"。快递员需要首先按这个按钮，去申请授权。

第二步，他按下按钮以后，屋主（也就是我）的手机就会跳出对话框：有人正在要求授权。系统还会显示该快递员的姓名、工号和所属的快递公司。

我确认请求属实，就点击按钮，告诉门禁系统，我同意给予他进入小区的授权。

第三步，门禁系统得到我的确认以后，向快递员显示一个进入小区的令牌（access token）。令牌就是类似密码的一串数字，只在短期内（比如七天）有效。

第四步，快递员向门禁系统输入令牌，进入小区。

有人可能会问，为什么不是远程为快递员开门，而要为他单独生成一个令牌？这是因为快递员可能每天都会来送货，第二天他还可以复用这个令牌。另外，有的小区有多重门禁，快递员可以使用同一个令牌通过它们。



#### 互联网场景的应用

我们把上面的例子搬到互联网，就是 OAuth 的设计了。

首先，居民小区就是储存用户数据的网络服务。比如，微信储存了我的好友信息，获取这些信息，就必须经过微信的"门禁系统"。

其次，快递员（或者说快递公司）就是第三方应用，想要穿过门禁系统，进入小区。

最后，我就是用户本人，同意授权第三方应用进入小区，获取我的数据。

**简单说，OAuth 就是一种授权机制。数据的所有者告诉系统，同意授权第三方应用进入系统，获取这些数据。系统从而产生一个短期的进入令牌（token），用来代替密码，供第三方应用使用。**



### 授权方式

>`https://www.ruanyifeng.com/blog/2019/04/oauth-grant-types.html`

注意，不管哪一种授权方式，第三方应用申请令牌之前，都必须先到系统备案，说明自己的身份，然后会拿到两个身份识别码：客户端 ID（client ID）和客户端密钥（client secret）。这是为了防止令牌被滥用，没有备案过的第三方应用，是不会拿到令牌的。



#### 授权码（authorization-code）

授权码（authorization code）方式，指的是第三方应用先申请一个授权码，然后再用该码获取令牌。

这种方式是最常用的流程，安全性也最高，它适用于那些有后端的 Web 应用。授权码通过前端传送，令牌则是储存在后端，而且所有与资源服务器的通信都在后端完成。这样的前后端分离，可以避免令牌泄漏。

第一步，A 网站提供一个链接，用户点击后就会跳转到 B 网站，授权用户数据给 A 网站使用。下面就是 A 网站跳转 B 网站的一个示意链接。

```javascript
https://b.com/oauth/authorize?
  response_type=code&
  client_id=CLIENT_ID&
  redirect_uri=CALLBACK_URL&
  scope=read
```

上面 URL 中，`response_type`参数表示要求返回授权码（`code`），`client_id`参数让 B 知道是谁在请求，`redirect_uri`参数是 B 接受或拒绝请求后的跳转网址，`scope`参数表示要求的授权范围（这里是只读）。

第二步，用户跳转后，B 网站会要求用户登录，然后询问是否同意给予 A 网站授权。用户表示同意，这时 B 网站就会跳回`redirect_uri`参数指定的网址。跳转时，会传回一个授权码，就像下面这样。

```javascript
https://a.com/callback?code=AUTHORIZATION_CODE
```

上面 URL 中，`code`参数就是授权码。

第三步，A 网站拿到授权码以后，就可以在后端，向 B 网站请求令牌。

```javascript
https://b.com/oauth/token?
 client_id=CLIENT_ID&
 client_secret=CLIENT_SECRET&
 grant_type=authorization_code&
 code=AUTHORIZATION_CODE&
 redirect_uri=CALLBACK_URL
```

上面 URL 中，`client_id`参数和`client_secret`参数用来让 B 确认 A 的身份（`client_secret`参数是保密的，因此只能在后端发请求），`grant_type`参数的值是`AUTHORIZATION_CODE`，表示采用的授权方式是授权码，`code`参数是上一步拿到的授权码，`redirect_uri`参数是令牌颁发后的回调网址。

第四步，B 网站收到请求以后，就会颁发令牌。具体做法是向`redirect_uri`指定的网址，发送一段 JSON 数据。

```json
{    
  "access_token":"ACCESS_TOKEN",
  "token_type":"bearer",
  "expires_in":2592000,
  "refresh_token":"REFRESH_TOKEN",
  "scope":"read",
  "uid":100101,
  "info":{...}
}
```

上面 JSON 数据中，`access_token`字段就是令牌，A 网站在后端拿到了。



#### 隐藏式（implicit）

有些 Web 应用是纯前端应用，没有后端。这时就不能用上面的方式了，必须将令牌储存在前端。**RFC 6749 就规定了第二种方式，允许直接向前端颁发令牌。这种方式没有授权码这个中间步骤，所以称为（授权码）"隐藏式"（implicit）。**

第一步，A 网站提供一个链接，要求用户跳转到 B 网站，授权用户数据给 A 网站使用。

```javascript
https://b.com/oauth/authorize?
  response_type=token&
  client_id=CLIENT_ID&
  redirect_uri=CALLBACK_URL&
  scope=read
```

上面 URL 中，`response_type`参数为`token`，表示要求直接返回令牌。

第二步，用户跳转到 B 网站，登录后同意给予 A 网站授权。这时，B 网站就会跳回`redirect_uri`参数指定的跳转网址，并且把令牌作为 URL 参数，传给 A 网站。

```javascript
https://a.com/callback#token=ACCESS_TOKEN
```

上面 URL 中，`token`参数就是令牌，A 网站因此直接在前端拿到令牌。

注意，令牌的位置是 URL 锚点（fragment），而不是查询字符串（querystring），这是因为 OAuth 2.0 允许跳转网址是 HTTP 协议，因此存在"中间人攻击"的风险，而浏览器跳转时，锚点不会发到服务器，就减少了泄漏令牌的风险。

这种方式把令牌直接传给前端，是很不安全的。因此，只能用于一些安全要求不高的场景，并且令牌的有效期必须非常短，通常就是会话期间（session）有效，浏览器关掉，令牌就失效了。



#### 密码式（password）

**如果你高度信任某个应用，RFC 6749 也允许用户把用户名和密码，直接告诉该应用。该应用就使用你的密码，申请令牌，这种方式称为"密码式"（password）。**

第一步，A 网站要求用户提供 B 网站的用户名和密码。拿到以后，A 就直接向 B 请求令牌。

```javascript
https://oauth.b.com/token?
  grant_type=password&
  username=USERNAME&
  password=PASSWORD&
  client_id=CLIENT_ID
```

上面 URL 中，`grant_type`参数是授权方式，这里的`password`表示"密码式"，`username`和`password`是 B 的用户名和密码。

第二步，B 网站验证身份通过后，直接给出令牌。注意，这时不需要跳转，而是把令牌放在 JSON 数据里面，作为 HTTP 回应，A 因此拿到令牌。

这种方式需要用户给出自己的用户名/密码，显然风险很大，因此只适用于其他授权方式都无法采用的情况，而且必须是用户高度信任的应用。



#### 客户端凭证（client credentials）

**最后一种方式是凭证式（client credentials），适用于没有前端的命令行应用，即在命令行下请求令牌。**

第一步，A 应用在命令行向 B 发出请求。

```javascript
https://oauth.b.com/token?
  grant_type=client_credentials&
  client_id=CLIENT_ID&
  client_secret=CLIENT_SECRET
```

上面 URL 中，`grant_type`参数等于`client_credentials`表示采用凭证式，`client_id`和`client_secret`用来让 B 确认 A 的身份。

第二步，B 网站验证通过以后，直接返回令牌。

这种方式给出的令牌，是针对第三方应用的，而不是针对用户的，即有可能多个用户共享同一个令牌。



### 令牌的使用

A 网站拿到令牌以后，就可以向 B 网站的 API 请求数据了。

此时，每个发到 API 的请求，都必须带有令牌。具体做法是在请求的头信息，加上一个`Authorization`字段，令牌就放在这个字段里面。

```bash
curl -H "Authorization: Bearer ACCESS_TOKEN" \
"https://api.b.com"
```

上面命令中，`ACCESS_TOKEN`就是拿到的令牌。



### 更新令牌

令牌的有效期到了，如果让用户重新走一遍上面的流程，再申请一个新的令牌，很可能体验不好，而且也没有必要。OAuth 2.0 允许用户自动更新令牌。

具体方法是，B 网站颁发令牌的时候，一次性颁发两个令牌，一个用于获取数据，另一个用于获取新的令牌（refresh token 字段）。令牌到期前，用户使用 refresh token 发一个请求，去更新令牌。

```javascript
https://b.com/oauth/token?
  grant_type=refresh_token&
  client_id=CLIENT_ID&
  client_secret=CLIENT_SECRET&
  refresh_token=REFRESH_TOKEN
```

上面 URL 中，`grant_type`参数为`refresh_token`表示要求更新令牌，`client_id`参数和`client_secret`参数用于确认身份，`refresh_token`参数就是用于更新令牌的令牌。

B 网站验证通过以后，就会颁发新的令牌。



### 第三方应用和 GitHub 登录集成

>`https://www.ruanyifeng.com/blog/2019/04/github-oauth.html`

注意：下面的实验已经使用 postman 测试通过。



#### 原理

所谓第三方登录，实质就是 OAuth 授权。用户想要登录 A 网站，A 网站让用户提供第三方网站的数据，证明自己的身份。获取第三方网站的身份数据，就需要 OAuth 授权。

举例来说，A 网站允许 GitHub 登录，背后就是下面的流程。

1. A 网站让用户跳转到 GitHub。
2. GitHub 要求用户登录，然后询问"A 网站要求获得 xx 权限，你是否同意？"
3. 用户同意，GitHub 就会重定向回 A 网站，同时发回一个授权码。
4. A 网站使用授权码，向 GitHub 请求令牌。
5. GitHub 返回令牌.
6. A 网站使用令牌，向 GitHub 请求用户数据。

下面就是这个流程的代码实现。

#### 应用登记

一个应用要求 OAuth 授权，必须先到对方网站登记，让对方知道是谁在请求。

所以，你要先去 GitHub 登记一下。当然，我已经登记过了，你使用我的登记信息也可以，但为了完整走一遍流程，还是建议大家自己登记。这是免费的。

访问这个[网址](https://github.com/settings/applications/new)，填写登记表。

![img](https://cdn.beekka.com/blogimg/asset/201904/bg2019042102.jpg)

应用的名称随便填，主页 URL 填写`https://www.baidu.com`，跳转网址填写 `https://www.baidu.com`。

提交表单以后，GitHub 应该会返回客户端 ID（client ID）和客户端密钥（client secret），这就是应用的身份识别码。

#### 浏览器跳转 GitHub 登录和授权界面

示例的首页很简单，就是一个链接，让用户跳转到 GitHub。

![img](https://cdn.beekka.com/blogimg/asset/201904/bg2019042103.jpg)

跳转的 URL 如下。

```
https://github.com/login/oauth/authorize?
  client_id=7e015d8ce32370079895&
  redirect_uri=https://www.baidu.com
```

这个 URL 指向 GitHub 的 OAuth 授权网址，带有两个参数：`client_id`告诉 GitHub 谁在请求，`redirect_uri`是稍后跳转回来的网址。

用户点击到了 GitHub，GitHub 会要求用户登录，确保是本人在操作。

#### 获取授权码

登录后，GitHub 询问用户，该应用正在请求数据，你是否同意授权。

![img](https://cdn.beekka.com/blogimg/asset/201904/bg2019042104.png)

用户同意授权， GitHub 就会跳转到`redirect_uri`指定的跳转网址，并且带上授权码，跳转回来的 URL 就是下面的样子。

```
https://www.baidu.com?
  code=859310e7cecc9196f4af
```

后端收到这个请求以后，就拿到了授权码（`code`参数）。

#### 获取令牌

后端使用这个授权码，向 GitHub 请求令牌。

```javascript
const tokenResponse = await axios({
  method: 'post',
  url: 'https://github.com/login/oauth/access_token?' +
    `client_id=${clientID}&` +
    `client_secret=${clientSecret}&` +
    `code=${requestToken}`,
  headers: {
    accept: 'application/json'
  }
});
```

上面代码中，GitHub 的令牌接口`https://github.com/login/oauth/access_token`需要提供三个参数。

- `client_id`：客户端的 ID
- `client_secret`：客户端的密钥
- `code`：授权码

作为回应，GitHub 会返回一段 JSON 数据，里面包含了令牌`accessToken`。

```javascript
const accessToken = tokenResponse.data.access_token;
```

#### 调用 API 获取用户数据

有了令牌以后，就可以向 API 请求数据了。

```javascript
const result = await axios({
  method: 'get',
  url: `https://api.github.com/user`,
  headers: {
    accept: 'application/json',
    Authorization: `token ${accessToken}`
  }
});
```

上面代码中，GitHub API 的地址是`https://api.github.com/user`，请求的时候必须在 HTTP 头信息里面带上令牌`Authorization: token 361507da`。

然后，就可以拿到用户数据，得到用户的身份。

```javascript
const name = result.data.name;
ctx.response.redirect(`/welcome.html?name=${name}`);
```



## Spring Security OAuth2.0

详细用法请参考示例：

- `https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-security/spring-security-oauth2-without-jwt`



### token 超时设置

分别设置每个客户端超时

```java
clients.inMemory()
    .withClient("client1")
    .secret(passwordEncoder.encode(ClientSecret))
    // authorization_code 授权码模式，，跳转到登录页面需要用户登录后并授权后才能够获取token
    // implicit 静默授权模式，跳转到登录页面需要用户登录后并授权才能够获取token
    .authorizedGrantTypes("authorization_code", "implicit", "refresh_token")
    .scopes("all")
    .autoApprove(false)
    .redirectUris("http://www.baidu.com")
    .accessTokenValiditySeconds(3600)
    .refreshTokenValiditySeconds(3600)
```

设置全局超时

```java
@Bean
AuthorizationServerTokenServices tokenServices() {
    DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
    defaultTokenServices.setClientDetailsService(clientDetailsService);
    defaultTokenServices.setTokenStore(tokenStore());
    defaultTokenServices.setSupportRefreshToken(true);
    // 设置token有效期为7200秒，也就是两个小时
    // defaultTokenServices.setAccessTokenValiditySeconds(7200);
    defaultTokenServices.setAccessTokenValiditySeconds(2);
    // 设置refresh_token有效期为3天
    // defaultTokenServices.setRefreshTokenValiditySeconds(259200);
    defaultTokenServices.setRefreshTokenValiditySeconds(5);
    return defaultTokenServices;
}
```



### todo 列表

- 有哪些端点
- 客户端信息从数据库加载
- 自定义登录和授权确认界面
- 支持验证码、短信、人脸识别、邮箱、authenticator登录
- 授权码模式如何在移动端实现呢？
- 如何自动获取公钥
- 生产级别示例的完整版
- 根据`https://www.ruanyifeng.com/blog/2019/04/github-oauth.html`指引实现一个基于 Spring MVC 的示例
- 参考此资料`https://cloud.tencent.com/developer/article/2248770`总结
- 端点响应自定义格式 JSON
- 自定义端点 URL
- 测试 resourceId 和 scope
- approval 定制
- refresh token 过期如何处理？
- github 登录成功回调后如何保存 github 账号信息到本地数据库
- 分布式系统统一认证鉴权需求
- SAML 协议
- RBAC、ACL、数据权限
- 有没有基于 SpringBoot 开箱即用的 RBAC 是插件呢？

