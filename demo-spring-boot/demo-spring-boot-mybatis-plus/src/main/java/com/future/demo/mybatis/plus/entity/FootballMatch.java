package com.future.demo.mybatis.plus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "t_football_match", autoResultMap = true)
public class FootballMatch {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long teamIdA;
    private Long teamIdB;
    private Date createTime;
}
