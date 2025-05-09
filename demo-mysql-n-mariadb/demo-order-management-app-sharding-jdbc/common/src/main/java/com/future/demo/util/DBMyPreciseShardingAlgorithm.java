package com.future.demo.util;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

public class DBMyPreciseShardingAlgorithm implements PreciseShardingAlgorithm<BigDecimal> {
    private static final BigInteger BIG_INTEGER_16 = new BigInteger("16");
    private static final BigInteger BIG_INTEGER_1 = new BigInteger("1");

    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<BigDecimal> shardingValue) {
        // 获取订单id
        BigDecimal orderId = shardingValue.getValue();
        String tableSuffix = String.valueOf(
                orderId.toBigInteger().mod(BIG_INTEGER_16)
                        .mod(new BigInteger(String.valueOf(availableTargetNames.size())))
                        .add(BIG_INTEGER_1)
                        .intValue());
        return availableTargetNames.stream().filter(targetName -> targetName.endsWith(tableSuffix)).findFirst().orElse(null);
    }
}
