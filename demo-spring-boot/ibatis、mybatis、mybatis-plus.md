# `iBatis`、`MyBatis`、`MyBatis-plus`介绍



## 什么是`iBatis`、`MyBatis`、`MyBatis-plus`？

- `iBatis`是Clinton Begin在2001年发起的一个开源项目，后发展成为`MyBatis`的前身。
- `MyBatis`（前身为`iBatis`）是一个基于`Java`的持久层框架，旨在简化对象与关系数据库之间的交互映射。
- `MyBatis-Plus`（简称`MP`）是`MyBatis`的增强工具，在`MyBatis`的基础上只做增强不做改变，为简化开发、提高效率而生。



## `IDEA`的`MyBatisX`插件使用

安装`MybatisX`插件后，通过光标放置到`mapper`类中再使用`alt+enter`组合键激活`MyBatisX`插件上下文菜单。



## `MyBatis`

>详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-mybatis`

### `spring boot`集成`MyBatis`

`maven`新增`mybatis`和`mariadb jdbc`驱动依赖

```xml
<!-- mybatis依赖 -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>3.0.4</version>
</dependency>
<!-- mariadb驱动依赖 -->
<dependency>
    <groupId>org.mariadb.jdbc</groupId>
    <artifactId>mariadb-java-client</artifactId>
    <scope>runtime</scope>
</dependency>
```

配置`application.properties`

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/demo
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# 配置mybatis xml配置文件路径
mybatis.mapper-locations=classpath:mapper/*.xml,classpath:mapper/**/*.xml

# 输入SQL到日志中
logging.level.com.future.demo=debug
```

创建`Employee bean`

```java
package com.future.demo.bean;

import lombok.Data;

@Data
public class Employee {
    private Long id;
    private String empName;
    private Integer age;
    private Double empSalary;
}

```

创建`EmployeeMapper`

```java
package com.future.demo.mapper;

import com.future.demo.bean.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

// 表示这是一个mybatis的mapper接口
@Mapper
public interface EmployeeMapper {
    void insert(Employee employee);

    void update(Employee employee);

    void delete(@Param("id") Long id);

    Employee getById(@Param("id") Long id);
}

```

创建`resources/mapper/EmployeeMapper.xml mapper`配置文件

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<!-- namespace对应mapper接口的全类名，表示此xml配置文件和mapper类绑定 -->
<mapper namespace="com.future.demo.mapper.EmployeeMapper">
    <!--  useGeneratedKeys="true" keyProperty="id"表示将数据库主键自动生成到实体类中 -->
    <insert id="insert" useGeneratedKeys="true" keyProperty="id">
        insert into employee(emp_name, age, emp_salary)
        values (#{empName}, #{age}, #{empSalary})
    </insert>

    <update id="update">
        update employee
        set emp_name   = #{empName},
            age        = #{age},
            emp_salary = #{empSalary}
        where id = #{id}
    </update>

    <delete id="delete">
        delete
        from employee
        where id = #{id}
    </delete>

    <!-- id对应mapper接口中的方法名，resultType返回值类型 -->
    <select id="getById" resultType="com.future.demo.bean.Employee">
        select id, emp_name as empName, age, emp_salary as empSalary
        from employee
        where id = #{id}
    </select>
</mapper>
```

编写测试`DemoSpringBootMybatisApplicationTests`

```java
package com.future.demo;

import com.future.demo.bean.Employee;
import com.future.demo.mapper.EmployeeMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoSpringBootMybatisApplicationTests {

    @Autowired
    EmployeeMapper employeeMapper;

    @Test
    void contextLoads() {
        // region 测试EmployeeMapper CRUD

        String empName = "张三";
        int age = 20;
        double empSalary = 1000.0;
        Employee employee = new Employee();
        employee.setEmpName(empName);
        employee.setAge(age);
        employee.setEmpSalary(empSalary);
        this.employeeMapper.insert(employee);

        Long id = employee.getId();

        employee = this.employeeMapper.getById(id);
        Assertions.assertEquals(empName, employee.getEmpName());
        Assertions.assertEquals(age, employee.getAge());
        Assertions.assertEquals(empSalary, employee.getEmpSalary());

        empName = "李四";
        age = 30;
        empSalary = 2000.0;
        employee.setEmpName(empName);
        employee.setAge(age);
        employee.setEmpSalary(empSalary);
        this.employeeMapper.update(employee);

        employee = this.employeeMapper.getById(id);
        Assertions.assertEquals(empName, employee.getEmpName());
        Assertions.assertEquals(age, employee.getAge());
        Assertions.assertEquals(empSalary, employee.getEmpSalary());

        this.employeeMapper.delete(id);

        employee = this.employeeMapper.getById(id);
        Assertions.assertNull(employee);

        // endregion
    }

}

```



### 插入数据后获取自增`id`

`EmployeeMapper.xml`配置如下以获取插入后的自增`id`

```xml
<!--  useGeneratedKeys="true" keyProperty="id"表示将数据库主键自动生成到实体类中 -->
<insert id="insert" useGeneratedKeys="true" keyProperty="id">
    insert into employee(emp_name, age, emp_salary)
    values (#{empName}, #{age}, #{empSalary})
</insert>
```

代码中获取自增`id`

```java
String empName = "张三";
int age = 20;
double empSalary = 1000.0;
Employee employee = new Employee();
employee.setEmpName(empName);
employee.setAge(age);
employee.setEmpSalary(empSalary);
this.employeeMapper.insert(employee);

Long id = employee.getId();
```



### 启用数据库字段下划线到`bean`驼峰命名自动转换

`application.properties`配置中添加如下配置：

```properties
# 启用数据库字段下划线到`bean`驼峰命名自动转换
mybatis.configuration.map-underscore-to-camel-case=true
```

自动把`employee.emp_name`数据库字段转换并设置到`Employee bean`的`empName`字段中。



### 启用延迟加载特性

`application.properties`配置如下：

```properties
# 启用延迟加载特性
mybatis.configuration.lazy-loading-enabled=true
mybatis.configuration.aggressive-lazy-loading=false
```

在MyBatis的配置中，`lazy-loading-enabled` 和 `aggressive-lazy-loading` 是两个关于延迟加载（Lazy Loading）的重要配置选项。下面是对这两个配置选项的解释：

1. **`mybatis.configuration.lazy-loading-enabled=true`**

   这个配置选项用于启用或禁用MyBatis的延迟加载特性。当设置为`true`时，表示启用延迟加载。延迟加载是一种优化技术，它允许MyBatis在真正需要数据时才从数据库中加载数据，而不是在查询一开始就加载所有相关的数据。这可以减少数据库访问的次数，提高应用程序的性能，特别是在处理复杂查询和大量数据时。

2. **`mybatis.configuration.aggressive-lazy-loading=false`**

   这个配置选项用于控制MyBatis的积极延迟加载行为。当设置为`false`时，表示不启用积极延迟加载。积极延迟加载（Aggressive Lazy Loading）是指在访问对象的任何属性时，都会触发延迟加载，即使这个属性与当前的数据库操作无关。这可能会导致不必要的数据库访问，因为每次访问对象的属性时，MyBatis都会检查是否需要从数据库中加载数据。

   当`aggressive-lazy-loading`设置为`true`时，MyBatis会采取一种更加积极的策略来触发延迟加载，这可能会导致更多的数据库访问。因此，在大多数情况下，将其设置为`false`是一个更合理的选择，以避免不必要的数据库访问，提高应用程序的性能。

综上所述，将`lazy-loading-enabled`设置为`true`并`aggressive-lazy-loading`设置为`false`，是一种优化MyBatis性能的配置方式，它允许MyBatis在需要时延迟加载数据，同时避免因为积极延迟加载而导致的额外数据库访问。



### 支持一个连接中执行多条 SQL

application.properties 配置添加 allowMultiQueries=true

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/demo?allowMultiQueries=true
```

`allowMultiQueries=true` 这个参数通常用于配置数据库连接字符串中，特别是在使用MySQL数据库时较为常见。这个参数的作用是允许在一个数据库连接中执行多个查询语句。默认情况下，出于安全考虑，大多数数据库驱动不允许在同一个查询字符串中执行多个SQL语句，以防止SQL注入攻击等安全问题。

当`allowMultiQueries=true`被设置时，意味着客户端（如Java应用程序）可以通过一个数据库连接发送包含多个SQL语句的字符串，并且数据库会依次执行这些语句。这在某些情况下可以提高效率，比如当你需要在一个数据库操作中执行多个更新或插入操作时。

然而，使用这个参数需要谨慎，因为它可能会增加SQL注入的风险。如果应用程序的输入没有得到适当的清理和验证，攻击者可能会利用这个特性构造恶意的SQL语句，进而控制或破坏数据库。

因此，在决定使用`allowMultiQueries=true`时，应该确保：

1. 所有用户输入都经过严格的验证和清理，以防止SQL注入攻击。
2. 仅在确实需要执行多个查询的情况下使用这个参数。
3. 考虑使用参数化查询或其他安全措施来进一步降低风险。

总之，`allowMultiQueries=true`是一个有用的功能，但使用时需要权衡其带来的便利性和可能的安全风险。



### 参数处理

参考示例中的`EmployeeParamMapper`

获取参数情况如下：

- 方法只有一个参数时，使用`#{参数名/map key}`获取参数值
- 方法有多个参数时，需要使用`@Param`标注的参数并使用`@Param`标注的参数名获取参数值



### 返回类型处理

参考示例中的`EmployeeReturnValueMapper`

关注情况：

- `List<Employee`类型返回
- `Map<Long, Employee>`类型返回
- 使用自定义结果集`ResultMap`配置数据库到`bean`字段映射



### 关联查询

#### 一对一关系

>使用`association`标签配置一对一关系。

使用单条`SQL join`查询实现：根据订单`id`查询订单信息并且同时查询订单对应的客户信息，参考`OrderMapper`中的`findByIdWithCustomer`方法

使用分步查询实现：根据订单`id`查询订单信息并且同时查询订单对应的客户信息，参考`OrderMapper`中的`findByIdWithCustomerStep`方法

#### 一对多关系

>使用`collection`标签配置一对多关系。

使用单条`SQL join`查询实现：根据`id`查询客户，并且查询出客户关联的订单，参考`CustomerMapper`中的`findByIdWithOrders`方法

使用分步查询实现：根据`id`查询客户，并且查询出客户关联的订单，参考`CustomerMapper`中的`findByIdWithOrdersStep`方法



### 动态 SQL

知识点：

- 动态条件查询 if、where 标签用法，参考 EmployeeMapper 中的 findByNameAndSalary 方法
- 动态条件查询 where、choose、when、otherwise 标签用法，参考 EmployeeMapper 中的 findByNameAndSalaryAndId方法
- 动态更新数据 if、set 标签用法，参考 EmployeeMapper 中的 updateDynamicSet 方法
- in 查询、批量插入、批量更新 foreach 标签用法，参考 EmployeeMapper 中的 findByIds、insertBatch、updateBatch 方法
- 使用 sql 标签定义 sql 片段，使用 include 标签引用 sql 片段，参考 EmployeeMapper 中的 getById 方法



## `MyBatis-plus`

### `spring-boot`项目集成`MyBatis-plus`

> `mybatis-plus-join`插件`https://github.118899.net/yulichang/mybatis-plus-join`
>
> mybatis-plus官方文档`https://mp.baomidou.com/guide/quick-start.html#%E5%88%9D%E5%A7%8B%E5%8C%96%E5%B7%A5%E7%A8%8B`
>
> `spring-boot`项目集成`mybatis-plus`详细配置参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-mybatis-plus`

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

  



