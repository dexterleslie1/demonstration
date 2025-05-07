package com.future.demo.util;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

public class DBMyPreciseShardingAlgorithm implements PreciseShardingAlgorithm<BigDecimal> {
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<BigDecimal> shardingValue) {
        // 获取订单id
        BigDecimal orderId = shardingValue.getValue();
        String tableSuffix = String.valueOf(orderId.toBigInteger().mod(new BigInteger(String.valueOf(availableTargetNames.size()))).intValue() + 1);
        return availableTargetNames.stream().filter(targetName -> targetName.endsWith(tableSuffix)).findFirst().orElse(null);
    }
}
