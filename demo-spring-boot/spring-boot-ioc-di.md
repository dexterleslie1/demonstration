# `IOC/DI`



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

### 知识点

`bean`注册

- 使用`@Bean`注解注册`bean`到`Spring`容器中
- 根据`bean`名称获取`bean`实例
- 根据`bean`类型获取`bean`实例
- `bean`默认是提前初始化并且是单实例的
- `@Configuration`配置类使用
- `@Controller`、`@Service`、`@Repository`、`@Component`分层注解使用
- `@ComponentScan`注解使用
- `@Import`注解使用，详细用法请参考 <a href="/spring-boot/spring-boot中@Import用法.html" target="_blank">链接</a>
- `@Scope`用法
- `@Lazy`用法
- `FactoryBean`用法
- `@Condition`用法，详细用法请参考 <a href="/spring-boot/spring-boot的condition用法.html" target="_blank">链接</a>

`bean`注入

- `@Autowired`用法
- `@Qulifier+@Primary`用法
- `@Resource`和`@Autowired`区别，详细请参考 <a href="/spring-boot/@Autowired和@Resource的区别.html" target="_blank">链接</a>
- 构造器注入和`setter`注入
- `XxxAware`感知接口
- `@Value`属性注入和`SpEL`表达式
- `@PropertySource`用法
- `spring Resource`用法，详细用法请参考 <a href="/spring-boot/spring的resource使用.html" target="_blank">链接</a>
- `@Profile`用法

`bean`生命周期

- 使用`@Bean`指定`bean`生命周期`init`和`destroy`方法
- `InitializingBean`、`DisposableBean`用法
- `@PostConstruct`、`@PreDestroy`用法
- `BeanPostProcessor`用法