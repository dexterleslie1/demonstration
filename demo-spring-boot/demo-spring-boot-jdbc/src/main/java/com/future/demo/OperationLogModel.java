package com.future.demo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OperationLogModel implements Serializable {

    private Long id;

    // 当前操作日志属于哪个用户，操作日志列表接口根据该字段获取指定用户下的操作日志
    private Long authId;

    // 操作人
    private Long operatorId;

    // 被操作人
    private Long passiveId;

    // 操作类型
    private OperationType operationType;

    // 操作内容
    private String content;

    // 操作时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
