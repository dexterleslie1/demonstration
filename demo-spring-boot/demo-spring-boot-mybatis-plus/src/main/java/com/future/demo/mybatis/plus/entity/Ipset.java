package com.future.demo.mybatis.plus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "t_ipset", autoResultMap = true)
public class Ipset {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String name;
    private Date createTime;
}
