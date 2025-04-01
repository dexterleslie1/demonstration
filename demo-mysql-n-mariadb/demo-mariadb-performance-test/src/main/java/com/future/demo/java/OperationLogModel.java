package com.future.demo.java;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 *
 * 操作日志
 */
@Data
@TableName(value = "operation_log", autoResultMap = true)
public class OperationLogModel {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 当前操作日志属于哪个用户，操作日志列表接口根据该字段获取指定用户下的操作日志
    @TableField("auth_id")
    private Long authId;

    // 操作人
    @TableField("operator_id")
    private Long operatorId;

    // 被操作人
    @TableField("passive_id")
    private Long passiveId;

    // 操作类型
    @TableField("operation_type")
    private OperationType operationType;

    // 操作内容
    @TableField("content")
    private String content;

    // 操作时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @TableField("create_time")
    private Date createTime;
}
