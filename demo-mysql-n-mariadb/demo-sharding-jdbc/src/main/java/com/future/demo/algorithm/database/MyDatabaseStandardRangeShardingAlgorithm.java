package com.future.demo.algorithm.database;

import com.google.common.collect.Range;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;

import java.util.Collection;

/**
 * 数据源范围分片算法
 */
@Slf4j
public class MyDatabaseStandardRangeShardingAlgorithm implements RangeShardingAlgorithm<Long> {
    /**
     * @param collection         spring.shardingsphere.sharding.tables.order.actual-data-nodes=ds$->{1..2}.order_$->{1..2} 中已配置的数据源
     * @param rangeShardingValue
     * @return 数据所在的数据源名称集合
     */
    @Override
    public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<Long> rangeShardingValue) {
        String logicTableName = rangeShardingValue.getLogicTableName();
        String columnName = rangeShardingValue.getColumnName();
        Range<Long> valueRange = rangeShardingValue.getValueRange();
        log.info("All datasources {} logicTableName {} columnName {} valueRange {}", collection, logicTableName, columnName, valueRange);

        // 通过判断 valueRange 计算数据所在的数据源，这里简单地返回所有数据源
        return collection;
    }
}
