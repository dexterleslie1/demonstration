package com.future.demo.mybatis.plus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "t_developer_and_ipset_relation", autoResultMap = true)
public class DeveloperAndIpsetRelation {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long developerId;
    private Long ipsetId;
    private Date createTime;
}
