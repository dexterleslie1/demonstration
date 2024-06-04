# `spring-boot`的`@EnableXxx`使用

> 例子详细请参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-spring-boot/demo-spring-boot-enablexxx)
>
> 例子演示通过注解`@EnableMyCache`参数化启用不同类型（`Local`和`Redis`类型）的缓存；通过注解`@EnableMyLocalCache`启用`Local`类型的缓存。

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



## 通过`@EnableMyCache`参数化启用不同类型的缓存

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



## 通过`@EnableMyLocalCache`启用`Local`类型的缓存

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

