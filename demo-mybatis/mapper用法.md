# `mapper`的用法

## 定义`mapper`用于操作数据库

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

