# `spring-boot`项目集成`redis`

> `spring-boot`项目集成`spring-boot-starter-data-redis`具体配置参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-redis/redistemplate/redistemplate-cluster)

`maven pom.xml`配置`spring-boot-starter-data-redis`如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.2</version>
    </parent>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <!-- Spring Boot提供的一个用于操作Redis的依赖库 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
    </dependencies>
</project>
```

`application.properties`配置如下：

```properties
# 配置redis集群节点，可以是全部或者部分节点（客户端会自动发现其他节点）
spring.redis.cluster.nodes=localhost:6380,localhost:6381,localhost:6382
# 指定了 Redis 客户端在与 Redis 集群进行通信时，允许执行的重定向操作的最大次数。
# 当 Redis 客户端尝试在集群中执行某个操作时，可能会因为数据分布的原因而遇到节点的重定向操作。例如，客户端尝试访问一个 key，但该 key 并不在当前节点上，此时节点会告诉客户端去哪个节点上查找该 key。
# 用来定义这种重定向操作的最大次数。如果超过了这个限制次数，客户端将不再进行重定向，并且会报错。
spring.redis.cluster.max-redirects=6
```

在代码中注入并引用`RedisTemplate`

```java
@Autowired
RedisTemplate<String, String> redisTemplate = null;
```

