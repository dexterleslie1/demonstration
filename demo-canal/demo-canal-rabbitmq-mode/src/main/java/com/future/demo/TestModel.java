package com.future.demo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "t_test", autoResultMap = true)
public class TestModel {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Date createTime;
}
