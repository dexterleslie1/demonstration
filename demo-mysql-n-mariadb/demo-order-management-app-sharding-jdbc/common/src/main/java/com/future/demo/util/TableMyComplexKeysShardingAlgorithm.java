package com.future.demo.util;

import com.future.demo.service.OrderService;
import com.google.common.collect.Range;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class TableMyComplexKeysShardingAlgorithm implements ComplexKeysShardingAlgorithm<BigDecimal> {
    /**
     * 订单id列名
     */
    private static final String COLUMN_ORDER_ID = "id";
    /**
     * 用户id列名
     */
    private static final String COLUMN_USER_ID = "userId";

    private static final BigInteger BIG_INTEGER_16 = new BigInteger("16");
    private static final BigInteger BIG_INTEGER_1 = new BigInteger("1");

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, ComplexKeysShardingValue<BigDecimal> shardingValue) {
//        if (!shardingValue.getColumnNameAndRangeValuesMap().isEmpty()) {
//            throw new RuntimeException("不支持除了=和in的操作");
//        }

        // 只支持订单id范围查询
        Map<String, Range<BigDecimal>> columnNameAndRangeValuesMap = shardingValue.getColumnNameAndRangeValuesMap();
        if (!columnNameAndRangeValuesMap.isEmpty()) {
            if (columnNameAndRangeValuesMap.size() != 1 || !columnNameAndRangeValuesMap.containsKey(COLUMN_ORDER_ID)) {
                throw new RuntimeException("只支持根据订单id范围查询");
            }

            return availableTargetNames;
        }

        // 获取订单id
        Collection<BigDecimal> orderIds = shardingValue.getColumnNameAndShardingValuesMap().getOrDefault(COLUMN_ORDER_ID, new ArrayList<>(1));
        // 获取客户id
        Collection<BigDecimal> userIds = shardingValue.getColumnNameAndShardingValuesMap().getOrDefault(COLUMN_USER_ID, new ArrayList<>(1));

        // 整合订单id和客户id
        List<BigDecimal> ids = new ArrayList<>();
        if (orderIds != null && !orderIds.isEmpty()) {
            ids.addAll(orderIds);
        }
        if (userIds != null && !userIds.isEmpty()) {
            ids.addAll(userIds);
        }

        // 通过订单ID和用户ID计算所有表名称
        return ids.stream()
                // 截取 订单号或客户id的后2位
                .map(id -> {
                    BigInteger remain = id.toBigInteger().and(OrderService.ShiftLeftBigInteger).mod(OrderService.TotalShardBigInteger);
                    int datasourceIndex = remain.divide(OrderService.EachDatasourceTableTotalShardBigInteger).intValue();
                    int tableIndex = remain.subtract(
                                    BigInteger.valueOf((long) datasourceIndex * OrderService.eachDatasourceTableTotalShard))
                            .intValue();
                    return tableIndex + 1;
                })
                .distinct()
                // 获取到真实的表
                .map(tableSuffix -> availableTargetNames.stream().filter(targetName -> targetName.endsWith(String.valueOf(tableSuffix))).findFirst().orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
