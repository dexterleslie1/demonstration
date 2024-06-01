# `spring-boot`项目集成`MyBatis-plus`

> [mybatis-plus-join插件](https://github.118899.net/yulichang/mybatis-plus-join)
>
> [mybatis-plus官方文档](https://mp.baomidou.com/guide/quick-start.html#%E5%88%9D%E5%A7%8B%E5%8C%96%E5%B7%A5%E7%A8%8B)
>
> `spring-boot`项目集成`mybatis-plus`详细配置参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-mybatis/demo-spring-boot-mybatis-plus)

`pom.xml`配置引用`MyBatis-plus`依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    ...

    <properties>
        <java.version>1.8</java.version>
        <mybatis-plus.version>3.4.2</mybatis-plus.version>
        <lombok.version>1.18.20</lombok.version>
        <junit.version>4.13.2</junit.version>
        <mysql.connector.version>8.0.23</mysql.connector.version>
    </properties>

    <dependencies>
        <!-- 用于在entity中引用@Data注解 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
        </dependency>
        <!-- spring-boot2.4.5不再包含junit4依赖 -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
        <!-- 引用mybatis-plus依赖 -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>
        <!-- 用于mybatis-plus join查询插件 -->
        <dependency>
            <groupId>com.github.yulichang</groupId>
            <artifactId>mybatis-plus-join</artifactId>
            <version>1.2.4</version>
        </dependency>
        <!-- mysql jdbc驱动程序 -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql.connector.version}</version>
        </dependency>
    </dependencies>
</project>

```

`application.properties`配置数据库数据源和`mybatis-plus`相关参数

> 其中`mybatis-plus.configuration.map-underscore-to-camel-case`为`true`时，`java bean`驼峰转换为数据库字段下划线，如：`clientId`转换为`client_id`；为`false`时，不作出任何转换。
>
> [`mybatis-plus.configuration.map-underscore-to-camel-case`开启下划线转化为驼峰](https://www.cnblogs.com/zhaixingzhu/p/12731664.html)
>
> [`mybatis-plus.configuration.map-underscore-to-camel-case`开启下划线转化为驼峰](https://blog.csdn.net/weixin_43314519/article/details/109351688)

```properties
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://127.0.0.1:50000/mybatisplusdemo?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=123456

# 为`true`时，`java bean`驼峰转换为数据库字段下划线，如：`clientId`转换为`client_id`；为`false`时，不作出任何转换。
mybatis-plus.configuration.map-underscore-to-camel-case=false
# 用于指定mybatis-plus mapper xml所在路径
mybatis.mapper-locations=classpath:mapper/*.xml,classpath:mapper/**/*.xml
# 用于指定mybatis-plus扫描java枚举所在的包，
# 实现Java枚举类型与数据库中的数据类型（如字符串、整数等）之间的转换
mybatis-plus.type-enums-package=com.future.demo

# 启用mybatis-plus SQL调试输出
logging.level.com.future.demo=debug
```

`spring-boot`启动类配置`mybatis-plus XxxMapper`所在包路径`@MapperScan("com.future.demo.mybatis.plus.mapper")`以便`mybatis-plus`扫描`mapper`

```java
@SpringBootApplication
@MapperScan("com.future.demo.mybatis.plus.mapper")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

配置`mybatis-plus`分页插件

```java
@Configuration
public class Config {
    /**
     * mybatis-plus分页插件
     * 注意：要使用mybatis-plus分页功能必须配置MybatisPlusInterceptor，否则无法使用mybatis-plus分页功能
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }
}
```

上面已经完成在`spring-boot`项目中配置`mybatis-plus`工作，接下来就可以在项目中编写`mapper`和`service`了。