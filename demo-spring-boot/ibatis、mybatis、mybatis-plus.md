# `iBatis`、`MyBatis`、`MyBatis-plus`介绍



## 什么是`iBatis`、`MyBatis`、`MyBatis-plus`？

- `iBatis`是Clinton Begin在2001年发起的一个开源项目，后发展成为`MyBatis`的前身。
- `MyBatis`（前身为`iBatis`）是一个基于`Java`的持久层框架，旨在简化对象与关系数据库之间的交互映射。
- `MyBatis-Plus`（简称`MP`）是`MyBatis`的增强工具，在`MyBatis`的基础上只做增强不做改变，为简化开发、提高效率而生。



## `IDEA`的`MyBatisX`插件使用

安装`MybatisX`插件后，通过光标放置到`mapper`类中再使用`alt+enter`组合键激活`MyBatisX`插件上下文菜单。



### 从数据库生成 mapper、mapper配置、bean

详细用法请使用 student 数据表协助生成操作。

在 IDEA 中新增数据库数据源

展开新增后的数据源选中需要生成 mapper 的数据表，点击右键弹出上下文菜单后点击 MybatisX-Generator 功能，配置信息如下：

- module path 选择相应的目标模块
- base package 为 com.future.demo
- relative package 为 bean

点击 Next 按钮，配置信息如下：

- annotation 选择 None
- options 勾选 Comment、Lombok、Model
- template 选择 default-all（表示生成 mapper 接口、mapper 配置文件、bean）

点击 Finish 按钮等待 mapper、mapper 配置文件、bean 生成完毕。



## `MyBatis`

>详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-mybatis`



### MyBatis 和 SpringBoot 兼容性

- SpringBoot 3.4.0 和 mybatis-spring-boot-starter 3.0.3以上兼容
- SpringBoot 2.2.7.RELEASE 和 mybatis-spring-boot-starter 2.3.2以下兼容



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

#### 全局方式

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



#### 局部方式

详细用法请参考 OrderMapper 中的 findByIdWithCustomerStep 方法

在 ResultMap 中配置 fetchType=lazy

```xml
<resultMap id="orderStepResultMap" type="com.future.demo.bean.Order">
    <id column="id" property="id"/>
    <result column="amount" property="amount"/>
    <result column="address" property="address"/>
    <result column="customer_id" property="customerId"/>
    <!--
        select="com.future.demo.mapper.CustomerMapper.findById"表示使用CustomerMapper的findById SQL查询订单
        column="{id=customer_id}"表示使用列customer_id的值作为findById SQL的id参数值
        fetchType="lazy"启用延迟加载
    -->
    <association property="customer"
                 select="com.future.demo.mapper.CustomerMapper.findById"
                 column="{id=customer_id}"
                 fetchType="lazy"/>
</resultMap>
```



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



### 使用注解的方式配置 mapper

详细用法请参考 EmployeeAnnotationMapper

新增 Employee 的例子如下：

```java
@Insert("insert into employee (id, emp_name, age, emp_salary) values (#{id}, #{empName}, #{age}, #{empSalary})")
@Options(useGeneratedKeys = true, keyProperty = "id")
void insert(Employee employee);
```



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



### 缓存



#### 什么是 MyBatis 缓存？

MyBatis缓存是MyBatis提供的一种性能优化机制，它通过减少Java Application与数据库的交互次数来提升程序的运行效率。以下是对MyBatis缓存的详细介绍：

**一、MyBatis缓存的分类**

MyBatis缓存主要分为一级缓存和二级缓存。

1. **一级缓存**
   - 一级缓存是MyBatis的默认缓存，也称为本地缓存或SqlSession级别的缓存。
   - 它是基于SqlSession的缓存，即在同一个SqlSession中执行的多次查询会将查询结果缓存在本地内存中。
   - 当同一个SqlSession中执行相同的SQL查询时，MyBatis会首先检查一级缓存，如果缓存中已经存在相同的查询结果，就直接返回缓存中的数据，而不会再次向数据库发出查询请求。
   - 一级缓存的作用是提高相同查询的响应速度，减少数据库访问次数。
   - 一级缓存的失效情况包括：不同SqlSession对应不同的一级缓存；同一个SqlSession单查询条件不同；同一个SqlSession两次查询期间执行了任何一次增删改操作；同一个SqlSession两次查询期间手动清空了缓存。
2. **二级缓存**
   - 二级缓存是映射器级别的缓存，也称为全局范围的缓存。
   - 除了当前SqlSession能用外，其他的SqlSession也可以使用。
   - 二级缓存需要在mapper文件中进行配置，并且pojo需要实现序列化的接口。
   - 二级缓存的作用是在多个SqlSession之间共享缓存数据，从而提高应用程序的性能。它适用于需要缓存共享的数据，如基础数据表（如国家、城市）等，以减少数据库查询的负担。
   - 二级缓存的配置包括在mybatis的sqlMapConfig.xml中配置、在mapper.xml中配置、在select标签中配置以及在mapper映射文件中开启二级缓存等步骤。

**二、MyBatis缓存的工作原理**

1. **一级缓存的工作原理**
   - 当一个SqlSession第一次执行查询操作时，MyBatis会将查询结果存储在一级缓存中。
   - 如果在同一个SqlSession中再次执行相同的查询操作，MyBatis会直接从一级缓存中获取数据，而不是再次向数据库发出查询请求。
   - 当执行增删改操作时，MyBatis会清空一级缓存，以确保缓存中的数据是最新的。
2. **二级缓存的工作原理**
   - 当一个SqlSession执行查询操作时，如果一级缓存中没有数据，MyBatis会检查二级缓存。
   - 如果二级缓存中有数据，MyBatis会直接从二级缓存中获取数据。
   - 如果二级缓存中也没有数据，MyBatis会向数据库发出查询请求，并将查询结果存储到二级缓存中。
   - 当执行增删改操作时，MyBatis会清空与当前mapper相关的二级缓存。

**三、MyBatis缓存的配置与使用**

1. **一级缓存的配置**
   - 一级缓存是MyBatis的默认缓存，无需进行额外配置即可使用。
   - 可以通过SqlSession的clearCache()方法手动清空一级缓存。
2. **二级缓存的配置**
   - 需要在mybatis-config.xml文件中开启二级缓存。
   - 在mapper.xml文件中配置<cache/>标签来启用二级缓存。
   - 可以配置二级缓存的eviction（驱逐策略）、flushInterval（刷新间隔）、size（缓存大小）和readOnly（只读性）等属性。

**四、MyBatis缓存的注意事项**

1. **缓存数据的更新**
   - 当数据库中的数据发生变化时，需要及时清空相关的缓存，以确保缓存中的数据是最新的。
   - 可以通过执行增删改操作来清空一级缓存和与当前mapper相关的二级缓存。
2. **缓存的适用场景**
   - 缓存适用于经常查询并且不经常改变的数据，如公司的介绍、新闻等。
   - 对于经常改变的数据或数据的正确与否对最终结果影响很大的数据，如商品的库存、股市的牌价等，不适合使用缓存。
3. **自定义缓存**
   - MyBatis还提供了自定义缓存的功能，可以根据实际需求实现自定义的缓存机制。

综上所述，MyBatis缓存是一种有效的性能优化机制，通过合理配置和使用缓存，可以显著提高应用程序的运行效率。



#### 启用一级缓存

MyBatis 一级缓存默认是启用状态。

在业务方法中使用 @Transactional 开启事务（同一个 SqlSession）即可以使用一级缓存特性，参考 EmployeeService 中的 testLevel1Cache 方法



#### 启用二级缓存

MyBatis 二级缓存默认是不启用的，参考 EmployeeService 中的 testLevel2Cache 方法

1. Bean 实现 Serializable 接口以支持 Bean 实例的序列化

   ```java
   public class Employee implements Serializable {
   ```

2. Mapper 配置文件里面加入 `<cache/>`启用二级缓存

   ```xml
   <mapper namespace="com.future.demo.mapper.EmployeeMapper">
       <!-- 启用MyBatis二级缓存 -->
       <cache/>
   </mapper>
   ```



### 分页

#### pagehelper

>pagehelper 官方参考`https://github.com/pagehelper/Mybatis-PageHelper/blob/master/wikis/zh/HowToUse.md`
>
>详细用法请参考 EmployeeService 中的 findByPage 方法

引入依赖

```xml
<!-- 分页插件 -->
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper-spring-boot-starter</artifactId>
    <version>2.1.0</version>
</dependency>
```

插件配置类

```java
package com.future.demo.config;

import com.github.pagehelper.PageInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class MybatisConfig {
    /**
     * pagehelper分页插件配置
     *
     * @return
     */
    @Bean
    public PageInterceptor pageInterceptor() {
        PageInterceptor interceptor = new PageInterceptor();
        Properties properties = new Properties();
        // 启用合理化，如果pageNum<1会查询第一页，如果pageNum>pages会查询最后一页
        properties.setProperty("reasonable", "true");
        interceptor.setProperties(properties);
        return interceptor;
    }
}

```

分页查询

```java
package com.future.demo.service;

import com.future.demo.bean.Employee;
import com.future.demo.mapper.EmployeeMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {
    @Autowired
    EmployeeMapper employeeMapper;

    /**
     * 用于协助测试pagehelper分页插件
     */
    public PageInfo<Employee> findByPage(int pageNum, int pageSize) {
        return PageHelper.<Employee>startPage(pageNum, pageSize)
                .doSelectPageInfo(() -> this.employeeMapper.listAll());
    }
}

```



### 枚举类型存储

#### MySQL 枚举类型

MySQL 的枚举类型 (ENUM) 允许你定义一组预定义的字符串值，列的值只能从该集合中选择。  它在存储空间和数据完整性方面具有一定的优势。

**创建枚举类型列:**

使用 `ENUM` 关键字在创建表时定义枚举类型列：

```sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    status ENUM('active', 'inactive', 'pending')
);
```

这创建了一个名为 `users` 的表，其中 `status` 列是一个枚举类型，允许的值为 `'active'`, `'inactive'`, 和 `'pending'`。

**插入数据:**

你可以使用枚举列的允许值来插入数据：

```sql
INSERT INTO users (status) VALUES ('active');
INSERT INTO users (status) VALUES ('inactive');
```

尝试插入不在允许值列表中的值会报错：

```sql
INSERT INTO users (status) VALUES ('deleted'); -- 报错
```

**修改枚举类型列的值:**

你可以更新 `status` 列的值，但新值必须是已定义的枚举值之一：

```sql
UPDATE users SET status = 'pending' WHERE id = 1;
```

**查询数据:**

你可以像查询其他列一样查询枚举类型列：

```sql
SELECT * FROM users WHERE status = 'active';
```

**枚举类型的重要特性和注意事项:**

- **存储方式:**  MySQL 内部以整数存储枚举值，'active'，'inactive'，'pending' 分别对应整数 1, 2, 3  (顺序和定义顺序一致)。  这使得存储空间相对较小。
- **添加/删除枚举值:**  修改枚举类型定义（添加或删除枚举值）会比较复杂。  如果你添加了新的枚举值，旧的数据仍然有效，新值对应新的整数。但如果你删除了枚举值，包含该值的记录仍然存在，但是其整数可能变成 NULL 或其他值，这需要谨慎处理，可能需要数据迁移。
- **空值:**  枚举类型列可以存储 NULL 值。
- **索引:**  枚举类型列可以被索引，这可以提高查询效率。
- **字符串比较:**  在比较时，使用单引号括起来枚举值 (例如 `'active'` )。
- **性能:**  由于 MySQL 内部使用整数存储，枚举类型通常比 VARCHAR 类型在存储空间和查询速度方面更具优势，尤其是在大量数据的情况下。
- **可读性:**  枚举类型提高了数据的可读性和可理解性，因为值是具有语义意义的字符串。

**与其他数据类型的比较:**

相比 `VARCHAR` 类型，枚举类型有以下优势：

- **数据完整性:**  保证列的值只能从预定义的集合中选择，避免了无效数据。
- **存储空间:**  通常比 `VARCHAR` 占用更少的存储空间。
- **性能:**  查询速度可能更快。

但是，枚举类型的灵活性较差，修改枚举值需要谨慎处理，可能会影响现有数据。  因此，在选择使用枚举类型时，需要权衡其优缺点，并考虑未来的可扩展性。  如果需要高度的灵活性，则可能更适合使用 `VARCHAR` 类型或其他更灵活的数据类型，并结合应用层逻辑进行数据验证。



#### 如何存储枚举类型呢？

>`https://blog.csdn.net/JoeBlackzqq/article/details/90216582`

总结：存储枚举类型方式：使用 int 数据类型存储、使用 varchar 数据类型存储、使用 MySQL enum 数据类型存储，推荐使用 MySQL enum 数据类型存储。

参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-mybatis`中的 EnumTests、EnumPerfTests 分别使用三种数据类型存储枚举，并分别比较三种数据类型的存储空间使用率和查询性能。

数据量均为  100w 时查询性能比较结果：

```
Benchmark                                Mode  Cnt     Score      Error  Units
EnumPerfTests.testEnumStoringAsEnum     thrpt    3  4747.686 ± 8779.277  ops/s
EnumPerfTests.testEnumStoringAsInt      thrpt    3  4699.738 ± 5563.983  ops/s
EnumPerfTests.testEnumStoringAsVarchar  thrpt    3  4550.752 ± 6471.301  ops/s
```

- 似乎查询性能差异不大

数据量均为 100w 是存储空间使用率比较结果：

- t_enum_storing_as_int 表为 32047104 字节
- t_enum_storing_as_varchar 表为 37289984 字节
- t_enum_storing_as_enum 表为 28901376 字节



### 使用 LocalDateTime 类型代替 java.util.Date 类型

```sql
create table if not exists `order` (
    id          bigint primary key auto_increment,
    address     varchar(255) not null,
    amount      decimal(15,5) not null,
    customer_id bigint not null,
    create_time datetime not null
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_general_ci;
```

```java
@Data
public class Order {
    private Long id;
    private String address;
    private BigDecimal amount;
    private Long customerId;
    private LocalDateTime createTime;

    /**
     * 订单对应的客户
     */
    private Customer customer;
}
```

```java
@Insert("insert into `order`(address,amount,customer_id,create_time) values(#{address},#{amount},#{customerId},#{createTime})")
@Options(useGeneratedKeys = true, keyProperty = "id")
int add(Order order);
```

```java
// region 测试 LocalDateTime

LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
order = new Order();
order.setAddress("address");
order.setAmount(BigDecimal.valueOf(100));
order.setCustomerId(1L);
order.setCreateTime(now);
int result = this.orderMapper.add(order);
Assertions.assertEquals(1, result);
id = order.getId();
order = this.orderMapper.findByIdWithCustomer(id);
this.orderMapper.delete(id);
Assertions.assertEquals(now, order.getCreateTime());

// endregion
```



### 批量插入

>`https://blog.csdn.net/chang100111/article/details/115664432`

详细用法请参考示例 `https://gitee.com/dexterleslie/demonstration/blob/master/demo-spring-boot/demo-spring-boot-mybatis-benchmark`

结论：

- 使用 CompletableFuture+MyBatis 事务批量插入数据速度最佳。
- 单机数据库性能不会随着 CPU 增加而线性增加，需要使用分布式数据库方案以水平扩展数据库以提高性能。



#### 介绍

优化 MySQL 批量插入数据的性能是一个复杂的问题，需要从多个方面入手。 没有一个放之四海而皆准的最佳方案，最佳策略取决于你的数据量、硬件资源、数据结构和可接受的错误率。 以下是一些关键的优化策略：

1. 选择合适的批量插入方法:

    INSERT INTO ... VALUES (...),(...),(...) (批量插入): 这是最常用的方法，一次性插入多行。 比单行插入快得多。 批量大小需要测试，过大可能导致内存溢出，过小则提升有限。 通常几百到几千行是一个不错的起点，但需要根据实际情况调整。

    LOAD DATA INFILE: 如果数据预先存储在文本文件中（例如 CSV），这是最快的方法。它绕过了 MySQL 服务器的连接层，直接将数据加载到表中。 需要服务器有足够的权限访问数据文件，文件格式需符合要求。 特别适合处理非常大的数据集。

    使用存储过程: 可以将批量插入逻辑封装到存储过程中，这在一定程度上可以优化网络传输和数据库处理，特别是在分布式环境下。

2. 优化表结构和索引:

    禁用键约束和索引 (临时): 在导入数据前，暂时禁用外键约束和索引 (ALTER TABLE ... DISABLE KEYS) 可以显著提高速度。 导入完成后再重新启用 (ALTER TABLE ... ENABLE KEYS)。 这会牺牲数据完整性，仅在数据导入完成后保证数据一致性即可。

    选择合适的存储引擎: InnoDB 和 MyISAM 的性能差异很大。 对于大量数据的导入，MyISAM 通常更快（因为它使用表锁，比 InnoDB 的行锁机制更简单），但它不支持事务。 如果数据一致性至关重要，则必须使用 InnoDB。

    合理的字段类型和长度: 避免使用过大的数据类型，选择最合适的类型可以节省存储空间和提高效率。

3. 优化数据库服务器配置:

    足够的内存: MySQL 需要足够的内存来缓存数据和索引，从而减少磁盘 I/O 操作。 调整 innodb_buffer_pool_size (InnoDB) 或 key_buffer_size (MyISAM) 等参数。 (注意：MyISAM 已经过时，不推荐在新项目中使用)

    快速存储: 使用 SSD 硬盘可以显著提高导入速度。

    优化服务器参数: 根据服务器配置和数据特性调整 MySQL 服务器参数，例如 bulk_insert_buffer_size，max_allowed_packet 等。 这需要深入了解 MySQL 参数的意义和影响。

    调整线程数: MySQL 允许并发插入，可以适当调整 innodb_thread_concurrency 参数 (InnoDB)，但过高的并发可能会导致资源竞争。

4. 使用事务 (谨慎):

事务可以保证数据的一致性，但过大的事务会导致锁等待时间过长，影响性能。 可以考虑分批提交事务，例如每插入一定数量的数据就提交一次事务。 START TRANSACTION, COMMIT 控制事务范围。

5. 数据预处理:

在导入数据之前，对数据进行预处理，例如数据清洗、转换等，可以减少导入时间并避免不必要的错误。

6. 其他优化策略:

    使用合适的客户端: 选择高效的 MySQL 客户端，例如 mysqlimport，可以提供更快的导入速度。

    多线程/多进程导入: 考虑使用多线程或多进程的方式并行导入数据，但这需要仔细处理资源竞争问题。

    分表分库: 对于极大的数据集，可以考虑分库分表来降低单表的数据量，提高性能。

总结:

优化批量插入的性能是一个迭代过程。 需要先尝试一些简单的优化方法，例如调整批量大小和禁用索引，然后根据实际情况逐步尝试更高级的优化策略。 在进行大规模数据导入之前，务必在测试环境中进行充分的测试和性能评估，选择最适合自己情况的方案。 同时，记得在导入前备份数据！



#### 多线程插入

结论：增加并发线程数为 CPU 总数的 2、3、4、5、6 倍，数据插入速度没有明显变化。

100w 数据量，数据库内存 2g 条件下：

- 并发线程数为 1 倍 CPU 总数，日志输出为 totalCount=1000000,databaseMemory=2g,availableProcessorsMultiplier=1 耗时 13373 毫秒
- 并发线程数为 2 倍 CPU 总数，日志输出为 totalCount=1000000,databaseMemory=2g,availableProcessorsMultiplier=2 耗时 13137 毫秒
- 并发线程数为 3 倍 CPU 总数，日志输出为 totalCount=1000000,databaseMemory=2g,availableProcessorsMultiplier=3 耗时 12596 毫秒
- 并发线程数为 4 倍 CPU 总数，日志输出为 totalCount=1000000,databaseMemory=2g,availableProcessorsMultiplier=4 耗时 12757 毫秒
- 并发线程数为 5 倍 CPU 总数，日志输出为 totalCount=1000000,databaseMemory=2g,availableProcessorsMultiplier=5 耗时 12462 毫秒
- 并发线程数为 6 倍 CPU 总数，日志输出为 totalCount=1000000,databaseMemory=2g,availableProcessorsMultiplier=6 耗时 12538 毫秒



#### innodb-buffer-pool-size 大小

结论：增加 innodb 内存大小数据插入速度没有明显变化。

100w 数据量，并发线程数为 4 倍 CPU 总数（32 线程）

- 数据库内存 512m，日志输出为 totalCount=1000000,databaseMemory=512m,availableProcessorsMultiplier=4 耗时 14999 毫秒
- 数据库内存 2g，日志输出为 totalCount=1000000,databaseMemory=2g,availableProcessorsMultiplier=4 耗时 13485 毫秒



#### bulk_insert_buffer_size 和 max_allowed_packet 参数

结论：这两个参数对 innodb 引擎的数据插入速度没有影响。



#### innodb_thread_concurrency 参数

结论：此参数对数据插入速度没有明显影响。



#### 使用事务

结论：使用此方法有一点加快数据插入速度。

代码如下：

```java
SqlSessionFactory sqlSessionFactory = applicationContext.getBean(SqlSessionFactory.class);
SqlSession sqlSession = null;
try {
    sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
    OrderMapper orderMapperInternal = sqlSession.getMapper(OrderMapper.class);

    int count = 0;
    for (int i = 0; i < internalRunLoopCount; i++) {
        Order order = orderRandomlyUtil.createRandomly();
        orderMapperInternal.add(order);
        count++;

        if (count == 1000) {
            sqlSession.commit();
            count = 0;
        }
    }
    if (count > 0) {
        sqlSession.commit();
        count = 0;
    }
} catch (Exception ex) {
    if (sqlSession != null) {
        sqlSession.rollback();
    }
    throw ex;
} finally {
    if (sqlSession != null) {
        sqlSession.close();
    }
}
```

100w 数据量，并发线程数为 4 倍 CPU 总数（32 线程），数据库内存 2g，日志输出 totalCount=1000000,databaseMemory=2g,availableProcessorsMultiplier=4 耗时 9568 毫秒



#### 启用 bin_log

结论：启用此特性对数据插入速度没有明显影响。



#### 数据服务器 CPU 数

CPU 类型：Intel(R) Xeon(R) Platinum 8269CY CPU @ 2.50GHz

测试结果如下：

- 2 核，100w 数据量，输出日志 totalCount=1000000 耗时 16949 毫秒
- 4 核，100w 数据量，输出日志 totalCount=1000000 耗时 10646 毫秒
- 6 核，100w 数据量，输出日志 totalCount=1000000 耗时 9138 毫秒

测试结论：

- 添加超过 4 核后的 CPU 对应批量插入速度没有明显影响。



#### 各种批量插入方式性能对比

插入 100w 数据，各种批量插入方式性能对比结果如下：

```
使用 for 循环一条一条插入数据 - 耗时 406081 毫秒
使用 foreach 动态生成 insert values (...),(...),(...) - 耗时 21504 毫秒
使用 foreach 动态生成  insert values (...); insert values (...); insert values (...); - 耗时 158774 毫秒
MyBatis batch 模式插入数据 - 耗时 162665 毫秒
使用 CompletableFuture 多线程并发插入数据 - 耗时 11408 毫秒
```



#### 批量插入获取自増 id

MyBatis Spring 依赖设置如下版本

```xml
<!-- mybatis依赖 -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>3.0.4</version>
</dependency>
```

不能使用 mariadb jdbc 驱动，只能使用 mysql jdbc 驱动，否则只返回第一条记录的自増 id，其他记录为 null

```xml
<!-- mariadb驱动依赖 -->
<!--<dependency>
    <groupId>org.mariadb.jdbc</groupId>
    <artifactId>mariadb-java-client</artifactId>
    &lt;!&ndash; 指定mariadb版本 &ndash;&gt;
    &lt;!&ndash;<version>3.4.0</version>&ndash;&gt;
    <scope>runtime</scope>
</dependency>-->
<!-- MySQL驱动才支持insert values (...),(...)批量插入返回自増id，mariadb驱动只返回第一条记录的自増id -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency>
```

批量插入的 xml 配置

```xml
<insert id="insertBatch" useGeneratedKeys="true" keyProperty="id">
    insert into employee(emp_name, age, emp_salary)
    values
    <foreach item="e" collection="employees" separator=",">
        (#{e.empName}, #{e.age}, #{e.empSalary})
    </foreach>
</insert>
```

测试

```java
this.employeeMapper.insertBatch(employees);
employees.forEach(o -> Assertions.assertTrue(o.getId() > 0));
```



### 多数据源配置

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-spring-boot/demo-spring-boot-mybatis-multiple-datasources)

application.properties 分别配置数据源

```properties
# 数据源1配置
spring.datasource.db1.jdbc-url=jdbc:mysql://localhost:3306/demo?allowMultiQueries=true
spring.datasource.db1.username=root
spring.datasource.db1.password=123456
spring.datasource.db1.driver-class-name=com.mysql.cj.jdbc.Driver

# 数据源2配置
spring.datasource.db2.jdbc-url=jdbc:mysql://localhost:3307/demo?allowMultiQueries=true
spring.datasource.db2.username=root
spring.datasource.db2.password=123456
spring.datasource.db2.driver-class-name=com.mysql.cj.jdbc.Driver
```

数据源1 Java 配置

```java
@Configuration
@MapperScan(
        // 配置Java mapper所在包
        basePackages = "com.future.demo.mapper",
        sqlSessionFactoryRef = "db1SqlSessionFactory",
        sqlSessionTemplateRef = "db1SqlSessionTemplate")
public class Datasource1Config {

    @Bean(name = "db1DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.db1")
    @Primary
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "db1SqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("db1DataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);

        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        bean.setConfiguration(configuration);

        // pagehelper分页插件配置
        PageInterceptor interceptor = new PageInterceptor();
        Properties properties = new Properties();
        // 启用合理化，如果pageNum<1会查询第一页，如果pageNum>pages会查询最后一页
        properties.setProperty("reasonable", "true");
        interceptor.setProperties(properties);
        bean.setPlugins(interceptor);

        // 配置mapper位置
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/db1/*Mapper.xml"));
        return bean.getObject();
    }

    @Bean(name = "db1SqlSessionTemplate")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("db1SqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
```

数据源2 Java 配置

```java
@Configuration
@MapperScan(
        // 配置Java mapper所在包
        basePackages = "com.future.demo.mapper2",
        sqlSessionFactoryRef = "db2SqlSessionFactory",
        sqlSessionTemplateRef = "db2SqlSessionTemplate")
public class Datasource2Config {

    @Bean(name = "db2DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.db2")
    @Primary
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "db2SqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("db2DataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);

        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        bean.setConfiguration(configuration);

        // pagehelper分页插件配置
        PageInterceptor interceptor = new PageInterceptor();
        Properties properties = new Properties();
        // 启用合理化，如果pageNum<1会查询第一页，如果pageNum>pages会查询最后一页
        properties.setProperty("reasonable", "true");
        interceptor.setProperties(properties);
        bean.setPlugins(interceptor);

        // 配置mapper位置
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/db2/*Mapper.xml"));
        return bean.getObject();
    }

    @Bean(name = "db2SqlSessionTemplate")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("db2SqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
```

分别引用数据源1和2的 Mapper

```java
@Autowired
EmployeeMapper employeeMapper;

@Autowired
EmployeeMapper2 employeeMapper2;
```



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

  



