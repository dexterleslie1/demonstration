package com.future.demo.algorithm.table;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

/**
 * 数据表精准分片算法
 */
@Slf4j
public class MyTableStandardPreciseShardingAlgorithm implements PreciseShardingAlgorithm<Long> {
    /**
     * @param collection           spring.shardingsphere.sharding.tables.order.actual-data-nodes=ds$->{1..2}.order_$->{1..2} 中已配置的数据源
     * @param preciseShardingValue 分片上下文参数
     * @return 数据所在的数据表名称
     */
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<Long> preciseShardingValue) {
        String logicTableName = preciseShardingValue.getLogicTableName();
        String columnName = preciseShardingValue.getColumnName();
        Long value = preciseShardingValue.getValue();
        log.info("All tables {} logicTableName {} columnName {} value {}", collection, logicTableName, columnName, value);

        String tableName = logicTableName + "_" + (value % collection.size() + 1);

        log.info("物理表 {}", tableName);

        if (!collection.contains(tableName)) {
            throw new UnsupportedOperationException("数据表 " + tableName + " 不存在");
        }

        return tableName;
    }
}
