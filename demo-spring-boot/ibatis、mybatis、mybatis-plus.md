# `iBatis`、`MyBatis`、`MyBatis-plus`介绍



## 什么是`iBatis`、`MyBatis`、`MyBatis-plus`？

- `iBatis`是Clinton Begin在2001年发起的一个开源项目，后发展成为`MyBatis`的前身。
- `MyBatis`（前身为`iBatis`）是一个基于`Java`的持久层框架，旨在简化对象与关系数据库之间的交互映射。
- `MyBatis-Plus`（简称`MP`）是`MyBatis`的增强工具，在`MyBatis`的基础上只做增强不做改变，为简化开发、提高效率而生。



## `MyBatis-plus`

### `spring-boot`项目集成`MyBatis-plus`

> `mybatis-plus-join`插件`https://github.118899.net/yulichang/mybatis-plus-join`
>
> mybatis-plus官方文档`https://mp.baomidou.com/guide/quick-start.html#%E5%88%9D%E5%A7%8B%E5%8C%96%E5%B7%A5%E7%A8%8B`
>
> `spring-boot`项目集成`mybatis-plus`详细配置参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-mybatis/demo-spring-boot-mybatis-plus`

`pom.xml`配置引用`MyBatis-plus`依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.7.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    
    ...

    <properties>
        <java.version>1.8</java.version>
        <mybatis-plus.version>3.4.2</mybatis-plus.version>
        <lombok.version>1.18.20</lombok.version>
        <junit.version>4.13.2</junit.version>
        <mysql.connector.version>8.0.23</mysql.connector.version>
    </properties>

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
        
        <!-- 用于在entity中引用@Data注解 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
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
            <version>1.4.13</version>
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



### 用法



#### `entity`实体类

##### 声明非持久化字段

> 参考文档 [链接](https://www.eolink.com/news/post/36131.html)
>
> 详细用法请参考 [链接](https://github.com/dexterleslie1/demonstration/blob/master/demo-mybatis/demo-spring-boot-mybatis-plus/src/main/java/com/future/demo/mybatis/plus/entity/User.java#L26)

在`entity bean`中使用`@TableField(exist = false)`声明字段不需要持久化

```java
@TableField(exist = false)
// https://www.eolink.com/news/post/36131.html
// 数据库没有对应的字段，不会被保存的字段
// 把注解去除会报告java.lang.IllegalStateException: No typehandler found for property fieldNonePersist错误
private List<String> fieldNonePersist;
```



#### `mapper`基本用法

> 详细用法请参考例子 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-mybatis/demo-spring-boot-mybatis-plus)

定义`entity User`

```java
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@Data
// entity对应的数据表
@TableName(value = "user", autoResultMap = true)
public class User {
    // entity的id
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer age;
    private String email;

    // https://javamana.com/2022/146/202205260937389358.html
    @TableField(typeHandler = ListTypeHandler.class)
    private List<String> authorities;

    @TableField(exist = false)
    // https://www.eolink.com/news/post/36131.html
    // 数据库没有对应的字段，不会被保存的字段
    // 把注解去除会报告java.lang.IllegalStateException: No typehandler found for property fieldNonePersist错误
    private List<String> fieldNonePersist;
}
```

定义`UserMapper`

```java
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.future.demo.mybatis.plus.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface UserMapper extends BaseMapper<User> {
    
}
```

使用`UserMapper`新增一个`User`记录

```java
String name = "Jone";

User user = new User();
user.setId(1L);
user.setAge(18);
user.setName(name);
user.setEmail("test1@baomidou.com");
userMapper.insert(user);
```



#### `mapper`调用存储过程

详细用法请参考 [链接](https://gitee.com/dexterleslie/demonstration/blob/master/demo-computer-information-security/demo-sql-injection/src/main/java/com/future/demo/mapper/UserMapper.java#L31)

- 定义存储过程

  ```sql
  delimiter |
  
  drop procedure if exists proc_sql_injection_assistant;
  
  create procedure proc_sql_injection_assistant(in v_username varchar(1024))
  begin
      set @v_dynamic_sql=concat('select * from `user` where username=''', v_username, '''');
      prepare p_statement from @v_dynamic_sql;
      execute p_statement;
      deallocate prepare p_statement;
  end|
  
  delimiter ;
  ```

- `mapper`调用存储过程

  ```java
  package com.future.demo.mapper;
  
  import com.baomidou.mybatisplus.core.mapper.BaseMapper;
  import com.future.demo.User;
  import org.apache.ibatis.annotations.Options;
  import org.apache.ibatis.annotations.Param;
  import org.apache.ibatis.annotations.Select;
  import org.apache.ibatis.mapping.StatementType;
  
  import java.util.List;
  
  public interface UserMapper extends BaseMapper<User> {
      /**
       * 协助存储过程sql注入测试
       *
       * @param username
       * @return
       */
      @Select("call proc_sql_injection_assistant(#{username,mode=IN,jdbcType=VARCHAR})")
      @Options(statementType = StatementType.CALLABLE)
      List<User> getByUsernameViaProcedure(@Param("username") String username);
  }
  ```



#### `QueryWrapper`查询返回指定列

> 请参考例子 [链接](https://github.com/dexterleslie1/demonstration/blob/master/demo-mybatis/demo-spring-boot-mybatis-plus/src/test/java/com/future/demo/mybatis/plus/mapper/UserMapperTests.java#L49)

```java
QueryWrapper<User> queryWrapper = Wrappers.query();
queryWrapper.select("id", "name", "age");
queryWrapper.eq("name", name);
queryWrapper.orderByAsc("id");
List<Map<String, Object>> mapList = userMapper.selectMaps(queryWrapper);
Assert.assertEquals(1, mapList.size());
Assert.assertEquals(name, mapList.get(0).get("name"));
Assert.assertEquals(18, mapList.get(0).get("age"));
```




#### `join`查询

详细用法请参考 [示例](https://gitee.com/dexterleslie/demonstration/blob/master/demo-mybatis/demo-spring-boot-mybatis-plus/src/test/java/com/future/demo/mybatis/plus/mapper/JoinTests.java)

- 引入`join`查询插件

  ```xml
  <!-- 用于mybatis-plus join查询插件 -->
  <dependency>
      <groupId>com.github.yulichang</groupId>
      <artifactId>mybatis-plus-join</artifactId>
      <version>1.4.13</version>
  </dependency>
  ```

- `mapper`继承`MPJBaseMapper`

  ```java
  public interface DeveloperMapper extends MPJBaseMapper<Developer> {
  }
  ```

  

- `join`查询

  ```java
  MPJLambdaWrapper<Developer> mpjLambdaWrapper = new MPJLambdaWrapper<>();
  mpjLambdaWrapper.
          // 查询所有Developer列
          selectAll(Developer.class)
          // 只查询DeveloperAndIpsetRelation ipsetId列
          .select(DeveloperAndIpsetRelation::getIpsetId)
          // Developer左连接DeveloperAndIpsetRelation使用DeveloperAndIpsetRelation.developerId=Developer.id
          .leftJoin(DeveloperAndIpsetRelation.class, DeveloperAndIpsetRelation::getDeveloperId, Developer::getId)
          .eq(DeveloperAndIpsetRelation::getIpsetId, ipsetId11);
  // selectOne返回一个领域对象
  Developer developerObject = this.developerMapper.selectJoinOne(Developer.class, mpjLambdaWrapper);
  Assert.assertEquals(developerId1, developerObject.getId().longValue());
  
  // selectOne返回一个Map<String, Object>包含指定select列
  Map<String, Object> mapObject = this.developerMapper.selectJoinMap(mpjLambdaWrapper);
  Assert.assertEquals(developerId1, mapObject.get("id"));
  Assert.assertEquals(ipsetId11, mapObject.get("ipsetId"));
  ```

  



