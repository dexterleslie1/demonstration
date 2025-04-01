package com.future.demo.bean;

import lombok.Data;

/**
 * MySQL 使用 varchar 存储枚举
 */
@Data
public class EnumStoringAsVarchar {
    private long id;
    private Status status;
}
