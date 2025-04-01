package com.future.demo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@Data
@TableName(value = "user", autoResultMap = true)
public class UserModel {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer age;
    private String email;

    // https://javamana.com/2022/146/202205260937389358.html
    @TableField(typeHandler = ListTypeHandler.class)
    private List<String> authorities;
}
