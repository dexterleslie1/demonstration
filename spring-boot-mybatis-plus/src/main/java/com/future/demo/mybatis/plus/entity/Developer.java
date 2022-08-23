package com.future.demo.mybatis.plus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@TableName(value = "t_developer", autoResultMap = true)
public class Developer {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String name;
    private Date createTime;
}
