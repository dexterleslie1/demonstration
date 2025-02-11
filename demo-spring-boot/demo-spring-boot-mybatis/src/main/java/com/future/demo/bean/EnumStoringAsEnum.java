package com.future.demo.bean;

import lombok.Data;

/**
 * MySQL 使用 enum 存储枚举
 */
@Data
public class EnumStoringAsEnum {
    private long id;
    private Status status;
}
