package com.future.demo.dto;

import lombok.Data;

@Data
public class IncreaseCountDTO {
    private Type type;
    private String flag;
    private int count;

    /**
     * 增加计数器的类型
     */
    public enum Type {
        /**
         * MySQL数据库
         */
        MySQL,
        /**
         * Cassandra
         */
        Cassandra
    }
}
