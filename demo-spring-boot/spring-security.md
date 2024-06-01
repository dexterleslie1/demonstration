# `spring-security`使用

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



## 自定义登录界面

> 例子详细配置请参考 [链接1](https://github.com/dexterleslie1/demonstration/tree/master/demo-spring-boot/demo-spring-security/spring-security-form-login) [链接2](https://github.com/dexterleslie1/demonstration/tree/master/demo-spring-boot/demo-spring-security/spring-security-customize-login)
>
> [登录才能访问受保护界面](https://spring.io/guides/gs/securing-web/)

启动例子后，访问`http://localhost:18080`按照提示操作即可了解自定义登录界面特性。

## `restful`登录配置

> 例子详细配置请参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-spring-boot/demo-spring-security/spring-security-restful-login)

