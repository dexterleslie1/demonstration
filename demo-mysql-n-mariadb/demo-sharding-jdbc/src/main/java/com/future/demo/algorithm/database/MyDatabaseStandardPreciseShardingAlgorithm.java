package com.future.demo.algorithm.database;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

/**
 * 数据源精准分片算法
 */
@Slf4j
public class MyDatabaseStandardPreciseShardingAlgorithm implements PreciseShardingAlgorithm<Long> {
    /**
     * @param collection           spring.shardingsphere.sharding.tables.order.actual-data-nodes=ds$->{1..2}.order_$->{1..2} 中已配置的数据源
     * @param preciseShardingValue 分片上下文参数
     * @return 数据所在的数据源名称
     */
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<Long> preciseShardingValue) {
        String logicTableName = preciseShardingValue.getLogicTableName();
        String columnName = preciseShardingValue.getColumnName();
        Long value = preciseShardingValue.getValue();
        log.info("All datasources {} logicTableName {} columnName {} value {}", collection, logicTableName, columnName, value);

        String dataSourceName = "ds" + (value % collection.size() + 1);

        log.info("数据源 {}", dataSourceName);

        if (!collection.contains(dataSourceName)) {
            throw new UnsupportedOperationException("数据源 " + dataSourceName + " 不存在");
        }

        return dataSourceName;
    }
}
