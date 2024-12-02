package com.future.demo.mybatis.plus.entity;

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
