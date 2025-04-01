package com.future.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "memory_assistant_myisam", autoResultMap = true)
public class MemoryAssistantMyISAMEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String randomStr;
    private Long extraIndexId;
}
