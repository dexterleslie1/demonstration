package com.future.demo.mybatis.plus.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_developer_info")
public class DeveloperInfo {
    @TableId
    private Long developerId;
    private Integer age;
}
