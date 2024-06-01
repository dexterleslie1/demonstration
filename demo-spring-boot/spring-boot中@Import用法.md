# `spring-boot`中`@Import`的用法

> [`spring-boot`中`@Import`的三种使用方法](https://blog.csdn.net/Java_zhujia/article/details/128062040)

## 引入普通类

> 此例子详细请参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-spring-boot/demo-spring-boot-import/src/main/java/com/future/demo/pkg1)

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

## 引入`ImportSelector`的实现类

> 此例子详细请参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-spring-boot/demo-spring-boot-import/src/main/java/com/future/demo/pkg2)
>
> `ImportSelector`在`Spring`框架中的使用场景主要涉及动态地向`Spring`容器注册`Bean`。

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



## 引入`ImportBeanDefinitionRegistrar`的实现类

> 此例子详细请参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-spring-boot/demo-spring-boot-import/src/main/java/com/future/demo/pkg3)
>
> `ImportBeanDefinitionRegistrar`提供了动态注册`Bean`到`Spring`容器中的能力，但`ImportBeanDefinitionRegistrar`提供了更大的灵活性和编程控制能力

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



## `ImportSelector`和`ImportBeanDefinitionRegistrar`区别

>`ImportBeanDefinitionRegistrar`和`ImportSelector`在`Spring`框架中都是用于动态注册Bean到容器中的工具，但它们在实现方式和应用场景上存在显著的区别。以下是两者之间的主要区别：
>
>1. 核心方法与返回值：
>   - ImportSelector：
>     - 核心方法：`String[] selectImports(AnnotationMetadata importingClassMetadata)`
>     - 返回值：返回一个字符串数组，包含要导入的配置类的全限定名。Spring容器会基于这些全限定名来加载和注册相应的Bean。
>   - ImportBeanDefinitionRegistrar：
>     - 核心方法：`void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry)`
>     - 返回值：无。该方法允许开发人员直接操作`BeanDefinitionRegistry`，以编程方式注册额外的Bean定义。
>2. 实现与应用：
>   - ImportSelector：
>     - 主要用于根据特定条件动态选择要导入的配置类。开发人员需要实现`selectImports`方法，根据自定义的逻辑返回要导入的配置类的全限定名数组。
>     - 通常与`@Configuration`注解中的`@Import`注解一起使用，以导入其他配置类。
>   - ImportBeanDefinitionRegistrar：
>     - 提供了更大的灵活性，允许开发人员以编程方式注册Bean定义。在`registerBeanDefinitions`方法中，开发人员可以直接操作`BeanDefinitionRegistry`，注册Bean定义。
>     - 常用于条件化注册Bean、集成第三方库、处理自定义注解等场景。
>3. 与Spring容器的集成：
>   - 两者都是Spring容器扩展点的一部分，但它们的集成方式略有不同。
>   - **ImportSelector**的实现类通常作为`@Import`注解的value值，Spring容器会自动调用其`selectImports`方法，并基于返回的配置类全限定名来导入相应的配置类。
>   - **ImportBeanDefinitionRegistrar**的实现类同样通过`@Import`注解引入，但Spring容器会调用其`registerBeanDefinitions`方法，允许开发人员直接操作`BeanDefinitionRegistry`。
>
>总结来说，ImportBeanDefinitionRegistrar和ImportSelector都提供了动态注册Bean到Spring容器中的能力，但ImportBeanDefinitionRegistrar提供了更大的灵活性和编程控制能力，而ImportSelector则更侧重于根据条件动态选择要导入的配置类。在具体使用时，应根据实际需求选择合适的工具。