package com.future.demo.util;

import com.future.demo.service.OrderService;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

public class TableMyPreciseShardingAlgorithm implements PreciseShardingAlgorithm<BigDecimal> {

    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<BigDecimal> shardingValue) {
        // 获取订单id
        BigDecimal orderId = shardingValue.getValue();
        BigInteger remain = orderId.toBigInteger().and(OrderService.ShiftLeftBigInteger).mod(OrderService.TotalShardBigInteger);
        int datasourceIndex = remain.divide(OrderService.EachDatasourceTableTotalShardBigInteger).intValue();
        int tableIndex = remain.subtract(BigInteger.valueOf((long) datasourceIndex * OrderService.eachDatasourceTableTotalShard)).intValue() + 1;
        return shardingValue.getLogicTableName() + tableIndex;
    }
}
