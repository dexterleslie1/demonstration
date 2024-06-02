# `spring-boot`的`condition`用法

> 例子的详细用法请参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-spring-boot/demo-spring-boot-condition)

## `@ConditionalOnProperty`用法

`application.properties`中配置`spring.boot.condition.need.test-service1=true`时才创建`TestService1`

```java
@Bean
// application.properties中配置spring.boot.condition.need.test-service1=true时才创建TestService1
@ConditionalOnProperty(
    value = "spring.boot.condition.need.test-service1",
    havingValue = "true")
TestService1 testService1() {
    TestService1 service = new TestService1();
    return service;
}
```

## `@ConditionalOnMissingBean`用法

只有`testService1One bean`不存在时才创建新增`TestService1`实例

```java
@Bean
// 只有testService1One bean不存在时才创建新增TestService1实例
@ConditionalOnMissingBean(name = "testService1One")
TestService1 testService1Two() {
    TestService1 service = new TestService1();
    return service;
}
```

## `@ConditionalOnBean`用法

```java
@Bean
// testService1 bean存在时才创建TestService2的实例
@ConditionalOnBean(name = "testService1")
TestService2 testService2() {
    TestService2 service = new TestService2();
    return service;
}
```

## `@ConditionalOnExpression`用法

```java
@Bean
// 当括号中的内容为true时，使用该注解的类才被实例化。
@ConditionalOnExpression("${spring.boot.condition.on.expression.test-service1-one:false}")
TestService1 testService1One() {
    TestService1 service = new TestService1() {
        @Override
        public void sayHello() {
            log.info("testService1One sayHello.");
        }
    };
    return service;
}
```

```java
@Bean
// spring.boot.condition.on.expression.test-service1-three不为空则创建TestService1
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${spring.boot.condition.on.expression.test-service1-three:}')")
TestService1 testService1Three() {
    TestService1 service = new TestService1() {
        @Override
        public void sayHello() {
            log.info("testService1Three sayHello.");
        }
    };
    return service;
}
```

