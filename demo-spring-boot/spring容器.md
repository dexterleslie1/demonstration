# `spring`容器



## 什么是`IOC/DI`？

在Spring框架中，IOC（Inversion of Control，控制反转）和DI（Dependency Injection，依赖注入）是两个紧密相关且至关重要的概念。以下是对这两个概念的详细解释：

**Spring IOC（控制反转）**

**定义与核心思想**：

- IOC是Spring框架的核心思想和基础，它是一种设计模式，用于实现依赖注入。
- IOC通过将对象创建和对象之间的依赖关系交由外部容器（如Spring容器）管理，实现了对象与对象之间的依赖关系从代码中解耦出来。

**工作原理**：

- IOC容器负责创建、管理和销毁对象。它通过读取配置文件或注解来获取对象的定义和依赖关系，并在应用启动时将对象实例化并注入到需要的地方。
- 程序员只需通过配置文件或注解的方式定义对象的依赖关系，而无需关注对象的创建和依赖关系的管理，从而降低了代码的复杂度。

**优点**：

- 实现了对象之间的解耦，提高了代码的可重用性和可测试性。
- 降低了对象之间的耦合度，使得代码更加灵活、可维护和可扩展。
- 为AOP（Aspect Oriented Programming，面向切面编程）提供了基础，支持事务管理、日志记录等横切关注点的功能。

**Spring DI（依赖注入）**

**定义与核心思想**：

- DI是Spring框架中实现IOC的具体方式，也是一种设计模式。
- 它通过将对象的依赖关系交由外部容器来管理，实现了对象之间的解耦和依赖关系的灵活配置。

**实现方式**：

- **构造函数注入**：通过构造函数将依赖的对象注入到待创建对象中。使用@Autowired注解或`<constructor-arg>`标签来配置注入的参数。
- **Setter方法注入**：通过Setter方法将依赖的对象注入到待创建对象中。使用@Autowired注解或`<property>`标签来配置注入的属性。
- **接口注入**：通过实现指定接口，依赖的对象被自动注入到实现类中。使用@Autowired注解自动注入对象。

**优点**：

- 提高了代码的灵活性和可测试性。通过将对象的依赖关系外部管理，对象可以更加灵活地进行组合和替换。
- 降低了对象之间的耦合度，使得代码更加清晰和易于维护。
- 简化了对象的使用和管理，开发者只需关注业务逻辑的实现，而无需关注对象的创建和依赖关系的管理。

**总结**

Spring IOC和DI是Spring框架中的核心概念，它们通过控制反转和依赖注入的方式，实现了对象之间的解耦和依赖关系的灵活管理。IOC是控制反转的设计思想，而DI是实现IOC的具体方式。这两个概念的结合使用，使得Spring框架成为了一个高度灵活、可维护和可扩展的企业级应用开发框架。



## `IOC/DI`优点有哪些呢？

### 降低耦合度

**优点说明**：

IOC/DI降低了对象之间的耦合度。在没有使用IOC/DI之前，对象之间的依赖关系往往是硬编码的，这导致对象之间的耦合度很高。而使用IOC/DI后，依赖关系被外部化到了容器或配置文件中，对象不再需要直接创建和依赖其他对象，而是通过容器或配置文件来获取依赖对象。这样，对象之间的耦合度大大降低，提高了代码的灵活性和可维护性。

**Java代码演示**：

```java
// 未使用IOC/DI的代码示例
public class UserService {
    private UserDao userDao = new UserDaoImpl();
 
    public void addUser() {
        userDao.save();
    }
}
 
// 使用IOC/DI的代码示例
public class UserService {
    private UserDao userDao;
 
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }
 
    public void addUser() {
        userDao.save();
    }
}
 
// 在Spring配置文件中进行依赖注入
<bean id="userDao" class="com.example.UserDaoImpl"/>
<bean id="userService" class="com.example.UserService">
    <property name="userDao" ref="userDao"/>
</bean>
```

### 提高代码的可测试性

**优点说明**：

IOC/DI使得代码更容易进行单元测试。在单元测试中，我们往往需要模拟依赖对象的行为，以便测试目标对象的功能。使用IOC/DI后，我们可以通过容器或配置文件轻松地替换依赖对象，从而实现对目标对象的单元测试。

**Java代码演示**：

```java
// 未使用IOC/DI的代码示例
public class UserService {
    private UserDao userDao = new UserDaoImpl();
 
    public void addUser() {
        userDao.save();
    }
}
 
// 在进行单元测试时，无法替换UserDao的实现
 
// 使用IOC/DI的代码示例
public class UserService {
    private UserDao userDao;
 
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }
 
    public void addUser() {
        userDao.save();
    }
}
 
// 在单元测试中，可以通过传递Mock对象来测试UserService
@Test
public void testAddUser() {
    UserDao mockUserDao = Mockito.mock(UserDao.class);
    UserService userService = new UserService(mockUserDao);
    userService.addUser();
    Mockito.verify(mockUserDao).save();
}
```

### 提高代码的可重用性

**优点说明**：

IOC/DI使得代码更容易重用。由于依赖关系被外部化到了容器或配置文件中，对象不再需要关心依赖对象的创建和绑定过程。这样，我们可以将对象封装成独立的组件，然后在不同的应用程序或模块中重用这些组件。

**Java代码演示**：

```java
// 未使用IOC/DI的代码示例
public class UserService {
    private UserDao userDao = new UserDaoImpl();
 
    public void addUser() {
        userDao.save();
    }
}
 
// UserService与UserDaoImpl紧密耦合，难以重用
 
// 使用IOC/DI的代码示例
public class UserService {
    private UserDao userDao;
 
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }
 
    public void addUser() {
        userDao.save();
    }
}
 
// 在其他模块或应用程序中，可以通过传递不同的UserDao实现来重用UserService
public class AnotherUserService extends UserService {
    public AnotherUserService(UserDao userDao) {
        super(userDao);
    }
}
```

### 支持懒加载和单例模式

**优点说明**：

IOC容器通常支持懒加载和单例模式。懒加载意味着依赖对象只在实际使用时才会被创建和注入；单例模式则确保整个应用程序中只有一个依赖对象的实例。这些特性有助于提高应用程序的性能和资源利用率。

**Java代码演示**：

```java
// 在Spring配置文件中配置懒加载和单例模式
<bean id="userDao" class="com.example.UserDaoImpl" scope="singleton" lazy-init="true"/>
<bean id="userService" class="com.example.UserService">
    <property name="userDao" ref="userDao"/>
</bean>
```

在以上配置中，`userDao`被配置为单例模式（`scope="singleton"`）和懒加载（`lazy-init="true"`）。这意味着`userDao`的实例只会在第一次被使用时被创建，并且整个应用程序中只有一个`userDao`的实例。



## `IOC/DI`使用

详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-ioc-di`



### 获取 IOC 容器实例

通过注入 ConfigurableApplicationContext 获取 IOC 容器实例

```java
// 注入ConfigurableApplicationContext实例
@Autowired
private ConfigurableApplicationContext ioc;
```

或者通过 SpringApplication.run 返回值获取 IOC 容器实例

```java
public static void main(String[] args) {
    ConfigurableApplicationContext ioc = SpringApplication.run(DemoSpringBootIocDiApplication.class, args);
    System.out.println("ioc = " + ioc);
}
```



### 查看 IOC 容器实例中有哪些 bean

```java
// 查看ioc容器中有那些bean
String[] names = ioc.getBeanDefinitionNames();
List<String> nameList = Arrays.asList(names);

Assertions.assertTrue(nameList.contains("org.springframework.context.annotation.internalConfigurationAnnotationProcessor"));
```



### bean 注册



#### 使用`@Bean`注解注册`bean`到`Spring`容器中

通过方法名注册，方法名即为 Bean 名称

```java
// 注册MyBean1到Spring容器中
// 通过方法名注册，方法名即为Bean名称
@Bean
public MyBean1 myBean1() {
    return new MyBean1();
}
```

通过 @Bean 指定 Bean 名称

```java
// 通过@Bean指定Bean名称
@Bean("myBean12")
public MyBean1 myBeanaa() {
    return new MyBean1();
}
```



#### 根据`bean`名称获取`bean`实例

bean 不存在时会抛出 NoSuchBeanDefinitionException 异常

```java
// bean不存在
try {
    ioc.getBean("noneExists");
    Assertions.fail("预期异常没有抛出");
} catch (NoSuchBeanDefinitionException e) {
    Assert.isInstanceOf(NoSuchBeanDefinitionException.class, e);
}
```



bean 存在时

```java
// bean存在
Assertions.assertNotNull(ioc.getBean("myBean1"));
```



#### 根据`bean`类型获取`bean`实例

bean 不存在时会抛出 NoSuchBeanDefinitionException 异常

```java
// bean不存在
try {
    ioc.getBean(String.class);
    Assertions.fail("预期异常没有抛出");
} catch (NoSuchBeanDefinitionException e) {
    Assert.isInstanceOf(NoSuchBeanDefinitionException.class, e);
}
```

不是唯一 bean 时会抛出 NoUniqueBeanDefinitionException 异常

```java
// 不是唯一的bean
try {
    ioc.getBean(MyBean1.class);
    Assertions.fail("预期异常没有抛出");
} catch (NoUniqueBeanDefinitionException ex) {
    Assert.isInstanceOf(NoUniqueBeanDefinitionException.class, ex);
}
```

根据 bean 类型列出所有指定类型的 bean

```java
Assertions.assertFalse(ioc.getBeansOfType(MyBean1.class).isEmpty());
```

根据 bean 名称和 bean 类型获取单个 bean

```java
Assertions.assertNotNull(ioc.getBean("myBean1", MyBean1.class));
```



#### `bean`默认是提前初始化并且是单实例的

```java
// bean默认是提前初始化并且是单实例的
MyBean1 myBean1 = (MyBean1) ioc.getBean("myBean1");
MyBean1 myBean2 = (MyBean1) ioc.getBean("myBean1");
Assertions.assertSame(myBean1, myBean2);
```



#### `@Configuration`配置类使用

@Configuration 配置

```java
package com.future.demo;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

// 需要使用@Configuration注解@Bean注解才能生效
@Configuration
public class MyBean2Config {
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    public MyBean2 myBean2() {
        return new MyBean2();
    }
}

```

测试

```java
// 测试@Configuration配置类配置的myBean2
Assertions.assertNotNull(ioc.getBean("myBean2"));
```



#### `@Controller`、`@Service`、`@Repository`、`@Component`分层注解使用

提醒：

- 使用mvc分层注解@Controller、@Service、@Repository、@Component直接注册bean，比@Configuration+@Bean更简单
- @Controller、@Service、@Repository注解实质是@Component注解
- @Controller用于注解控制器类，@Service用于注解服务类，@Repository用于注解数据访问类
- @Component用于注解除控制器、服务类、数据访问类外的普通类

@Controller 注解

```java
@Controller
public class MyController1 {
}
```

@Service 注解

```java
@Service
public class MyService1 {  
}
```

@Repository 注解

```java
@Repository
public class MyDao1 {
}
```

@Component 注解

```java
@Component
public class MyComponent1 {
}
```

测试

```java
// 使用mvc分层注解@Controller、@Service、@Repository、@Component直接注册bean，比@Configuration+@Bean更简单
// @Controller、@Service、@Repository注解实质是@Component注解
// @Controller用于注解控制器类，@Service用于注解服务类，@Repository用于注解数据访问类
// @Component用于注解除控制器、服务类、数据访问类外的普通类
Assertions.assertNotNull(ioc.getBean(MyController1.class));
Assertions.assertNotNull(ioc.getBean(MyService1.class));
Assertions.assertNotNull(ioc.getBean(MyDao1.class));
Assertions.assertNotNull(ioc.getBean(MyComponent1.class));
```



#### `@ComponentScan`注解使用

默认情况下Spring只扫描@SpringBootApplication所在的包及其子包下的注解，如果需要扫描其他包，可以使用@ComponentScan注解指定扫描的包

@ComponentScan 配置如下：

```java
// 指定扫描com.future.demo和com.future.demo1包下的所有注解，否则会报告myBean3没有定义
@ComponentScan(basePackages = {"com.future.demo", "com.future.demo1"})
@SpringBootApplication
public class DemoSpringBootIocDiApplication {
```

com.future.demo1.MyBean3 不在 @SpringBootApplication 所在的包及其子包下

```java
package com.future.demo1;

import org.springframework.stereotype.Component;

@Component
public class MyBean3 {
}

```

测试

```java
// 测试@ComponentScan注解扫描到的bean
// 默认情况下Spring只扫描@SpringBootApplication所在的包及其子包下的注解，如果需要扫描其他包，可以使用@ComponentScan注解指定扫描的包
Assertions.assertNotNull(ioc.getBean("myBean3"));
```



#### `@Import`注解使用

>`spring-boot`中`@Import`的三种使用方法`https://blog.csdn.net/Java_zhujia/article/details/128062040`



##### 引入普通类

> 此例子详细请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-import/src/main/java/com/future/demo/pkg1`

下面是一个简单的例子，展示一个配置文件可以统一导入组件内部多个`XXXConfiguration`：

`TestServiceAllConfiguration.java`

```java
@Import({TestService1Configuration.class, TestService2Configuration.class})
public class TestServiceAllConfiguration {
}
```

`TestService1Configuration.java`

```java
public class TestService1Configuration {
    @Bean
    TestService1 testService1() {
        return new TestService1();
    }
}
```

`TestService2Configuration.java`

```java
public class TestService2Configuration {
    @Bean
    TestService2 testService2() {
        return new TestService2();
    }
}
```

`Application.java`

```java
@SpringBootApplication
// 引入普通类
@Import(TestServiceAllConfiguration.class)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

在这个例子中，`TestService1Configuration`和`TestService2Configuration`分别创建`TestService1`和`TestService2`实例，`TestServiceAllConfiguration`通过`@Import({TestService1Configuration.class, TestService2Configuration.class})`实现统一导入多个`XXXConfiguration`，在`Application`使用整个组件时只需借助`@Import(TestServiceAllConfiguration.class)`即可导入整个组件的目地。

##### 引入`ImportSelector`的实现类

> 此例子详细请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-import/src/main/java/com/future/demo/pkg2`

`ImportSelector`在`Spring`框架中的使用场景主要涉及动态地向`Spring`容器注册`Bean`。

下面是一个简单的例子，演示借助`ImportSelector`获取配置的缓存类型`CacheType`并根据配置的缓存类型动态地注册相应缓存实例（本地缓存或`Redis`缓存实例）到`Spring`容器中：

`CacheSelector`

```java
public class CacheSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        // 获取 @EnableMyCache 缓存类型注解值
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(EnableMyCache.class.getName());
        CacheType type = (CacheType) annotationAttributes.get("type");
        // 根据注解返回不同的缓存实现类名称，以实现动态地向Spring IoC容器中导入组件
        switch (type) {
            case Local: {
                return new String[]{LocalCacheService.class.getName()};
            }
            case Redis: {
                return new String[]{RedisCacheService.class.getName()};
            }
            default: {
                throw new RuntimeException(MessageFormat.format("unsupport cache type {0}", type.toString()));
            }
        }
    }
}
```

通过`EnableMyCache`使用`CacheSelector`

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CacheSelector.class)
public @interface EnableMyCache {
    /**
     * 缓存类型
     *
     * @return
     */
    CacheType type() default CacheType.Redis;
}
```

定义缓存类型的枚举`CacheType`

```java
/**
 * 缓存类型
 */
public enum CacheType {
    Local, Redis
}
```

启用`Redis`缓存类型的配置

```java
@Configuration
@EnableMyCache(type = CacheType.Redis)
public class ConfigCacheRedis {
}
```

在测试中使用`ConfigCacheRedis`配置

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, ConfigCacheRedis.class})
public class Pkg22Tests {

    @Resource
    CacheService cacheService;

    @Test
    public void test() {
        Assert.assertEquals("redis缓存", this.cacheService.getType());
    }
}
```



##### 引入`ImportBeanDefinitionRegistrar`的实现类

> 此例子详细请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-import/src/main/java/com/future/demo/pkg3`

`ImportBeanDefinitionRegistrar`提供了动态注册`Bean`到`Spring`容器中的能力，但`ImportBeanDefinitionRegistrar`提供了更大的灵活性和编程控制能力

下面是一个简单的例子，演示借助`ImportBeanDefinitionRegistrar`动态地创建`MyBean`实例并初始化其属性`name`的值：

`MyBeanDefinitionRegister`

```java
public class MyBeanDefinitionRegister implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取 @EnableMyBean name 注解值
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(EnableMyBean.class.getName());
        String name = (String) annotationAttributes.get("name");

        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
        rootBeanDefinition.setBeanClass(MyBean.class);
        // 把 @EnableMyBean name 注解值注入到 MyBean 实例的 name 字段中
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("name", name);
        // 添加属性
        rootBeanDefinition.setPropertyValues(propertyValues);
        // 注入到spring容器
        registry.registerBeanDefinition(MyBean.class.getName(), rootBeanDefinition);
    }
}
```

`MyBean`

```java
public class MyBean {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MyBean{" +
                "name='" + name + '\'' +
                '}';
    }
}
```

通过`EnableMyBean`使用`MyBeanDefinitionRegister`

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(MyBeanDefinitionRegister.class)
public @interface EnableMyBean {

    String name() default "";

}
```

在测试中使用`EnableMyBean`并传入参数`name=lisi`

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@EnableMyBean(name = "lisi")
public class Pkg3Tests {

    @Resource
    MyBean myBean;

    @Test
    public void test() {
        Assert.assertEquals("lisi", this.myBean.getName());
    }
}
```



##### `ImportSelector`和`ImportBeanDefinitionRegistrar`区别

`ImportBeanDefinitionRegistrar`和`ImportSelector`在`Spring`框架中都是用于动态注册Bean到容器中的工具，但它们在实现方式和应用场景上存在显著的区别。以下是两者之间的主要区别：

1. 核心方法与返回值：

  - ImportSelector：
    - 核心方法：`String[] selectImports(AnnotationMetadata importingClassMetadata)`
    - 返回值：返回一个字符串数组，包含要导入的配置类的全限定名。Spring容器会基于这些全限定名来加载和注册相应的Bean。
  - ImportBeanDefinitionRegistrar：
    - 核心方法：`void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry)`
    - 返回值：无。该方法允许开发人员直接操作`BeanDefinitionRegistry`，以编程方式注册额外的Bean定义。

2. 实现与应用：

  - ImportSelector：
    - 主要用于根据特定条件动态选择要导入的配置类。开发人员需要实现`selectImports`方法，根据自定义的逻辑返回要导入的配置类的全限定名数组。
    - 通常与`@Configuration`注解中的`@Import`注解一起使用，以导入其他配置类。
  - ImportBeanDefinitionRegistrar：
    - 提供了更大的灵活性，允许开发人员以编程方式注册Bean定义。在`registerBeanDefinitions`方法中，开发人员可以直接操作`BeanDefinitionRegistry`，注册Bean定义。
    - 常用于条件化注册Bean、集成第三方库、处理自定义注解等场景。

3. 与Spring容器的集成：

  - 两者都是Spring容器扩展点的一部分，但它们的集成方式略有不同。
  - **ImportSelector**的实现类通常作为`@Import`注解的value值，Spring容器会自动调用其`selectImports`方法，并基于返回的配置类全限定名来导入相应的配置类。
  - **ImportBeanDefinitionRegistrar**的实现类同样通过`@Import`注解引入，但Spring容器会调用其`registerBeanDefinitions`方法，允许开发人员直接操作`BeanDefinitionRegistry`。

总结来说，ImportBeanDefinitionRegistrar和ImportSelector都提供了动态注册Bean到Spring容器中的能力，但ImportBeanDefinitionRegistrar提供了更大的灵活性和编程控制能力，而ImportSelector则更侧重于根据条件动态选择要导入的配置类。在具体使用时，应根据实际需求选择合适的工具。



#### `@EnableXxx`使用

> 例子详细请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-enablexxx`

例子演示通过注解`@EnableMyCache`参数化启用不同类型（`Local`和`Redis`类型）的缓存；通过注解`@EnableMyLocalCache`启用`Local`类型的缓存。

`CacheType`缓存类型枚举

```java
public enum CacheType {
    Local, Redis
}
```

`CacheService`接口如下：

```java
public interface CacheService {

    /**
     * 返回缓存类型字符串
     *
     * @return
     */
    String getType();

}
```

`LocalCacheService`本地缓存实现

```java
public class LocalCacheService implements CacheService {

    @Override
    public String getType() {
        return "本地缓存";
    }

}
```

`RedisCacheService redis`缓存实现

```java
public class RedisCacheService implements CacheService {

    @Override
    public String getType() {
        return "redis缓存";
    }

}
```



##### 通过`@EnableMyCache`参数化启用不同类型的缓存

`EnableMyCache`注解定义

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CacheSelector.class)
public @interface EnableMyCache {
    /**
     * 缓存类型
     *
     * @return
     */
    CacheType type() default CacheType.Redis;
}
```

`CacheSelector`根据注解传入的缓存类型返回相应的类型的缓存实现类

```java
public class CacheSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        // 获取 @EnableMyCache 缓存类型注解值
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(EnableMyCache.class.getName());
        CacheType type = (CacheType) annotationAttributes.get("type");
        // 根据注解返回不同的缓存实现类名称，以实现动态地向Spring IoC容器中导入组件
        switch (type) {
            case Local: {
                return new String[]{LocalCacheService.class.getName()};
            }
            case Redis: {
                return new String[]{RedisCacheService.class.getName()};
            }
            default: {
                throw new RuntimeException(MessageFormat.format("unsupport cache type {0}", type.toString()));
            }
        }
    }
}
```

启用`redis`缓存测试

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
// 启用redis缓存
@EnableMyCache(type = CacheType.Redis)
public class EnableRedisCacheTests {

    @Resource
    CacheService cacheService;

    @Test
    public void test() {
        Assert.assertEquals("redis缓存", this.cacheService.getType());
    }
}
```

启用本地缓存测试

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
// 启用本地缓存
@EnableMyCache(type = CacheType.Local)
public class EnableLocalCacheTests {

    @Resource
    CacheService cacheService;

    @Test
    public void test() {
        Assert.assertEquals("本地缓存", this.cacheService.getType());
    }
}
```



##### 通过`@EnableMyLocalCache`启用`Local`类型的缓存

`EnableMyLocalCache`注解定义

```java
// 专门用于启用LocalCache的注解
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MyLocalCacheConfiguration.class)
public @interface EnableMyLocalCache {

}
```

用于创建`LocalCacheService bean`的配置

```java
// 用于创建LocalCacheService bean的配置
public class MyLocalCacheConfiguration {
    @Bean
    CacheService cacheService() {
        return new LocalCacheService();
    }
}
```

使用`@EnableMyLocalCache`注解启用本地缓存测试

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
// 启用本地缓存
@EnableMyLocalCache
public class EnableMyLocalCacheTests {

    @Resource
    CacheService cacheService;

    @Test
    public void test() {
        Assert.assertEquals("本地缓存", this.cacheService.getType());
    }
}
```



#### `@Scope`用法

prototype作用域的bean是每次请求都会创建一个新的实例，并且不会被缓存

```java
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Bean
public MyBean2 myBean2() {
    return new MyBean2();
}
```

```java
Assertions.assertNotSame(ioc.getBean("myBean2"), ioc.getBean("myBean2"));
```



singleton作用域的bean是每次请求都会返回同一个实例，并且会被缓存

```java
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Bean
public MyBean1 myBean13() {
    return new MyBean1();
}
```

```java
Assertions.assertSame(ioc.getBean("myBean13"), ioc.getBean("myBean13"));
```



#### `@Lazy`用法

singleton作用域的bean默认是非懒加载的，即在容器启动时就创建好，并缓存起来

```java
@Bean
public MyBean4 myBean41() {
    return new MyBean4();
}
```

```java
Thread.sleep(1000);

// singleton作用域的bean默认是非懒加载的，即在容器启动时就创建好，并缓存起来
Assertions.assertTrue(new Date().getTime() - ioc.getBean("myBean41", MyBean4.class).getCreateTime().getTime() >= 1000);
```



singleton作用域并标注@Lazy的bean是懒加载的，即在第一次请求时才创建好，并缓存起来

```java
@Lazy
@Bean
public MyBean4 myBean42() {
    return new MyBean4();
}
```

```java
Thread.sleep(1000);

// singleton作用域并标注@Lazy的bean是懒加载的，即在第一次请求时才创建好，并缓存起来
Assertions.assertTrue(new Date().getTime() - ioc.getBean("myBean42", MyBean4.class).getCreateTime().getTime() <= 1);
```



#### `FactoryBean`用法

```java
// 使用场景：用于创建比较复杂的对象，比如需要依赖其他对象的创建
@Component
public class MyFactoryBean implements FactoryBean<MyBean5> {
    @Override
    public MyBean5 getObject() throws Exception {
        return new MyBean5();
    }

    @Override
    public Class<?> getObjectType() {
        return MyBean5.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
```

```java
public class MyBean5 {
}
```

```java
Assertions.assertSame(ioc.getBean(MyBean5.class), ioc.getBean(MyBean5.class));
```



#### `@Condition`用法

> 详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-condition`

##### `@ConditionalOnProperty`用法

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

##### `@ConditionalOnMissingBean`用法

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

##### `@ConditionalOnBean`用法

```java
@Bean
// testService1 bean存在时才创建TestService2的实例
@ConditionalOnBean(name = "testService1")
TestService2 testService2() {
    TestService2 service = new TestService2();
    return service;
}
```

##### `@ConditionalOnExpression`用法

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



### bean 注入

#### `@Autowired`用法

根据类型 @Autowired 注入

```java
// 需要使用@Configuration注解@Bean注解才能生效
@Configuration
public class MyBean2Config {
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    public MyBean2 myBean2() {
        return new MyBean2();
    }
}
```

```java
// 根据类型@Autowired注入
@Autowired
public MyBean2 xxx;
```

```java
// 测试根据类型@Autowired注入
Assertions.assertNotNull(ioc.getBean(MyService1.class).xxx);
```



先根据类型 @Autowired 注入，再根据名称 @Autowired 注入，如果没有找到，则抛出异常

```java
// 先根据类型@Autowired注入，再根据名称@Autowired注入，如果没有找到，则抛出异常
@Autowired
public MyBean4 myBean41;
```

```java
// 先根据类型@Autowired注入，再根据名称@Autowired注入，如果没有找到，则抛出异常
Assertions.assertNotNull(ioc.getBean(MyService1.class).myBean41);
```



根据类型注入多个 bean

```java
// 根据类型注入多个bean
@Autowired
public List<MyBean1> myBean1List;

// 根据类型注入多个bean
@Autowired
public Map<String, MyBean1> myBean1Map;
```

```java
Assertions.assertEquals(4, ioc.getBean(MyService1.class).myBean1List.size());
Assertions.assertEquals(4, ioc.getBean(MyService1.class).myBean1Map.size());
Assertions.assertTrue(ioc.getBean(MyService1.class).myBean1Map.containsKey("myBean11"));
```



注入 spring ioc 容器

```java
// 注入spring ioc容器
@Autowired
public ApplicationContext ioc;
```

```java
// 注入spring ioc容器
Assertions.assertSame(ioc, ioc.getBean(MyService1.class).ioc);
```



#### `@Qulifier+@Primary`用法

提醒：

- @Qulifier注解用法，精确指定根据bean名称注入
- 在有@Primary指定默认的bean时，@Autowired会自动注入此bean，此时使用@Qulifier注解指定注入的bean名称以忽略@Primary注解

```java
@Configuration
public class MyBean6Config {
    // 多个MyBean6存在，在使用@Autowired根据类型注入MyBean6时会报告错误，使用@Primary指定默认的bean解决问题
    @Primary
    @Bean
    public MyBean6 myBean61() {
        return new MyBean6();
    }

    @Bean
    public MyBean6 myBean62() {
        return new MyBean6();
    }
}
```

```java
// 因为有@Primary注解指定myBean61为默认的bean，所以使用@Qualifier注解指定myBean62以忽略myBean61的@Primary注解
@Qualifier("myBean62")
@Autowired
public MyBean6 xxx2;
```

```java
// @Qulifier注解用法，精确指定根据bean名称注入
// 在有@Primary指定默认的bean时，@Autowired会自动注入此bean，此时使用@Qulifier注解指定注入的bean名称以忽略@Primary注解
Assertions.assertSame(ioc.getBean(MyService1.class).xxx2, ioc.getBean("myBean62"));
```



#### `@Resource`和`@Autowired`区别

详细请参考 <a href="/spring-boot/@Autowired和@Resource的区别.html" target="_blank">链接</a>



#### 构造器注入和`setter`注入

```java
@Repository
public class MyDao1 {

    private MyBean7 myBean7;
    private MyBean8 myBean8;

    // spring自动调用此构造函数并注入MyBean7
    public MyDao1(MyBean7 myBean7) {
        this.myBean7 = myBean7;
    }

    public MyBean7 getMyBean7() {
        return myBean7;
    }

    // setter方法注入
    @Autowired
    public void setMyBean8(MyBean8 myBean8) {
        this.myBean8 = myBean8;
    }
    public MyBean8 getMyBean8() {
        return myBean8;
    }
}
```

```java
// 测试构造器注入
Assertions.assertNotNull(ioc.getBean(MyDao1.class).getMyBean7());

// 测试setter方法注入
Assertions.assertNotNull(ioc.getBean(MyDao1.class).getMyBean8());
```



#### `XxxAware`感知接口

```java
// 通过EnvironmentAware和BeanNameAware接口，实现对beanName和环境变量的注入
@Component
public class MyBean8 implements EnvironmentAware, BeanNameAware {
    private String beanName;
    private Environment env;

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    public String getBeanName() {
        return beanName;
    }

    public String getOsType() {
        return env.getProperty("os.name");
    }
}
```

```java
// 测试XxxAware感知接口
String osType = ioc.getBean(MyBean8.class).getOsType();
Assertions.assertEquals(System.getProperty("os.name"), osType);
String beanName = ioc.getBean(MyBean8.class).getBeanName();
Assertions.assertEquals("myBean8", beanName);
```



#### `@Value`属性注入和`SpEL`表达式、`@PropertySource`用法

```java
// @Value、@PropertySource用法
// 指定从my.properties文件中读取属性值，其中*表示从所有包路径下查找（包括第三方jar包）
@PropertySource("classpath*:my.properties")
@Component
public class MyBean9 {
    // 注入application.properties配置文件中的值
    @Value("${mybean9.name}")
    private String name;

    // SpEL表达式
    @Value("#{T(java.util.UUID).randomUUID().toString()}")
    private String id;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
```

```java
// 测试@Value
String name = ioc.getBean(MyBean9.class).getName();
Assertions.assertEquals("测试", name);
String id = ioc.getBean(MyBean9.class).getId();
Assertions.assertNotNull(id);
```



#### `spring Resource`用法

详细用法请参考 <a href="/spring-boot/spring的resource使用.html" target="_blank">链接</a>



### bean 生命周期

#### 使用`@Bean`指定`bean`生命周期`init`和`destroy`方法，`InitializingBean`、`DisposableBean`用法，`@PostConstruct`、`@PreDestroy`用法

```java
@Configuration
public class MyBeanLifeCycleConfig {

    @Bean(initMethod = "initMethod", destroyMethod = "destroyMethod")
    public MyBeanLifeCycle myBean() {
        return new MyBeanLifeCycle();
    }
}
```

```java
// 测试bean生命周期方法
public class MyBeanLifeCycle implements InitializingBean, DisposableBean {
    boolean isInit = false;
    boolean invokedAfterPropertiesSet = false;
    boolean invokedPostConstruct = false;

    public void initMethod() {
        isInit = true;
    }

    public void destroyMethod() {
        isInit = false;
    }

    @Override
    public void destroy() throws Exception {
        // 调用destroyMethod方法之前被调用
    }

    // 属性设置完成后被调用
    @Override
    public void afterPropertiesSet() throws Exception {
        // @Autowired完成后被调用
        // 调用initMethod方法之前被调用
        invokedAfterPropertiesSet = true;
    }

    @PostConstruct
    public void postConstruct() {
        // 在构造函数和@Autowired之后被调用
        // 在afterPropertiesSet之前被调用
        invokedPostConstruct = true;
    }

    @PreDestroy
    public void preDestroy() {
        // 在destroy方法之前被调用
    }
}
```

```java
// 测试bean生命周期方法init和destroy
MyBeanLifeCycle myBeanLifeCycle = ioc.getBean(MyBeanLifeCycle.class);
Assertions.assertTrue(myBeanLifeCycle.isInit);
//ioc.close();
//Assertions.assertFalse(myBeanLifeCycle.isInit);
// 测试InitializingBean接口
Assertions.assertTrue(myBeanLifeCycle.invokedAfterPropertiesSet);
// 测试@PostConstruct
Assertions.assertTrue(myBeanLifeCycle.invokedPostConstruct);
```



#### `BeanPostProcessor`用法

```java
// 拦截beance的初始化过程
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof MyBean10) {
            ((MyBean10) bean).setName("hello myBean10");
        }
        return bean;
    }
}
```

```java
@Data
public class MyBean10 {
    private String name;
}
```

```java
// 测试BeanPostProcessor
// 具体使用案例参考spring框架中AutowiredAnnotationBeanPostProcessor
String name1 = ioc.getBean(MyBean10.class).getName();
Assertions.assertEquals("hello myBean10", name1);
```
