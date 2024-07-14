package com.future.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
// entity对应的数据表
@TableName(value = "user", autoResultMap = true)
public class User {
    // entity的id
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String name;
}
