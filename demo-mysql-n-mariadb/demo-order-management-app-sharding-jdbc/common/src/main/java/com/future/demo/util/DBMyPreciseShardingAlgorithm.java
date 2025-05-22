package com.future.demo.util;

import com.future.demo.service.OrderService;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.math.BigDecimal;
import java.util.Collection;

public class DBMyPreciseShardingAlgorithm implements PreciseShardingAlgorithm<BigDecimal> {

    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<BigDecimal> shardingValue) {
        // 获取订单id
        BigDecimal orderId = shardingValue.getValue();
        String tableSuffix = String.valueOf(
                orderId.toBigInteger().and(OrderService.ShiftLeftBigInteger)
                        .mod(OrderService.TotalShardBigInteger)
                        .divide(OrderService.EachDatasourceTableTotalShardBigInteger)
                        .add(OrderService.BIG_INTEGER_1)
                        .intValue());
        return availableTargetNames.stream().filter(targetName -> targetName.endsWith(tableSuffix)).findFirst().orElse(null);
    }
}
