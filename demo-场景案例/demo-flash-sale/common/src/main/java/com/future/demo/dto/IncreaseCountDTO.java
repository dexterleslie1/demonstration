package com.future.demo.dto;

import lombok.Data;

@Data
public class IncreaseCountDTO {
    /**
     * 幂等标识，防止 kafka 重复消费消息
     */
    private String idempotentUuid;
    /*private Type type;*/
    private String flag;
    private int count;

    /**
     * 幂等标识通过 idempotentUuidPrefix+":"+flag 生成，因为同一个订单建立两种 Cassandra 索引需要区分开幂等标识
     *
     * @param idempotentUuidPrefix
     * @param flag
     */
    public IncreaseCountDTO(String idempotentUuidPrefix, String flag) {
        this.idempotentUuid = idempotentUuidPrefix + ":" + flag;
        this.flag = flag;
    }

//    /**
//     * 增加计数器的类型
//     */
//    public enum Type {
//        /**
//         * MySQL数据库
//         */
//        MySQL,
//        /**
//         * Cassandra
//         */
//        Cassandra
//    }
}
