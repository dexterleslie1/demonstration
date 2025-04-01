# `spring-boot`的`PasswordEncoder`使用

> 例子详细请参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-spring-boot/demo-spring-security/spring-security-passwordencoder)
>
> 例子中演示`BCryptPasswordEncoder`的使用

配置`spring-security`并创建`BCryptPasswordEncoder`实例

```java
@Configuration
@EnableWebSecurity
public class ConfigWebSecurity extends WebSecurityConfigurerAdapter {
    @Bean(name = "bCryptPasswordEncoder")
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

引用`BCryptPasswordEncoder`实例

```java
@Autowired
@Qualifier(value = "bCryptPasswordEncoder")
private PasswordEncoder passwordEncoder = null;
```

密码的编码和比较

```java
@Test
public void test() {
    String rawValue = "123456";
    // 使用BCryptPasswordEncoder编码密码两次编码同一个密码不会重复
    String encodePassword = this.passwordEncoder.encode(rawValue);
    String encodePassword1 = this.passwordEncoder.encode(rawValue);
    Assert.assertNotEquals(encodePassword, encodePassword1);
    // 调用BCryptPasswordEncoder#matches函数比较密码是否正确
    boolean match = this.passwordEncoder.matches(rawValue, encodePassword);
    Assert.assertTrue(match);
    match = this.passwordEncoder.matches(rawValue, encodePassword1);
    Assert.assertTrue(match);
}
```

