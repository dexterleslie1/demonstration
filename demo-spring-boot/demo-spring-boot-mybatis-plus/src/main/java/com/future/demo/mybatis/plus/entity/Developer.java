package com.future.demo.mybatis.plus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@TableName(value = "t_developer")
public class Developer {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String name;
    private Date createTime;

    // ipset列表，用于协助测试多对多关系
    @TableField(exist = false)
    private List<Ipset> ipsetList;

    // developer info，用于协助测试一对一关系
    @TableField(exist = false)
    private DeveloperInfo info;

    // developer lang，用于协助测试一对多查询
    @TableField(exist = false)
    private List<DeveloperLang> langList;
}
