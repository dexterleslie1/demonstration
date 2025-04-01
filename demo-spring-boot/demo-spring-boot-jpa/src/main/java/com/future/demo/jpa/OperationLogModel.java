package com.future.demo.jpa;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "operation_log")
public class OperationLogModel implements Serializable {

    // MySQL autoincremnt字段配置
    // https://stackoverflow.com/questions/4102449/how-to-annotate-mysql-autoincrement-field-with-jpa-annotations
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 当前操作日志属于哪个用户，操作日志列表接口根据该字段获取指定用户下的操作日志
    @Column(name = "auth_id")
    private Long authId;

    // 操作人
    @Column(name = "operator_id")
    private Long operatorId;

    // 被操作人
    @Column(name = "passive_id")
    private Long passiveId;

    // 操作类型
    @Column(name = "operation_type")
    private OperationType operationType;

    // 操作内容
    private String content;

    // 操作时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time")
    private Date createTime;
}
