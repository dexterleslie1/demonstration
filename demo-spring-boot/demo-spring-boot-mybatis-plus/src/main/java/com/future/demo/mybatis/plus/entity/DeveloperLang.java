package com.future.demo.mybatis.plus.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_developer_lang")
public class DeveloperLang {
    @TableId
    private Long id;
    private Long developerId;
    private String lang;
}
