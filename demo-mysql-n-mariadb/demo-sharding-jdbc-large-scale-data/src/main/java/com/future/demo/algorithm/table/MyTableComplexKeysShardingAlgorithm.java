package com.future.demo.algorithm.table;

import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

import java.math.BigDecimal;
import java.util.*;

public class MyTableComplexKeysShardingAlgorithm implements ComplexKeysShardingAlgorithm {
    final static String StrUnderline = "_";
    final static String StrOrderId = "id";
    final static String StrUserId = "user_id";

    @Override
    public Collection<String> doSharding(Collection collection, ComplexKeysShardingValue complexKeysShardingValue) {
        String logicTableName = complexKeysShardingValue.getLogicTableName();
        Object objOrderId = complexKeysShardingValue.getColumnNameAndShardingValuesMap().get(StrOrderId);
        Object objUserId = complexKeysShardingValue.getColumnNameAndShardingValuesMap().get(StrUserId);

        if (objOrderId != null && !((List) objOrderId).isEmpty()) {
            List collectionReturn = new ArrayList();
            for (Object o : ((List) objOrderId)) {
                BigDecimal orderId = (BigDecimal) o;
                String orderIdStr = orderId.toString();
                String orderIdStrSuffix = orderIdStr.substring(orderIdStr.length() - 3);
                int orderIdSuffixHash = Objects.hash(orderIdStrSuffix);

                String tableName = logicTableName + StrUnderline + (orderIdSuffixHash % collection.size());
                if (!collection.contains(tableName)) {
                    throw new UnsupportedOperationException("数据表 " + tableName + " 不存在");
                }

                collectionReturn.add(tableName);
            }


            return collectionReturn;
        } else if (objUserId != null && !((List) objUserId).isEmpty()) {
            List collectionReturn = new ArrayList();
            for (Object o : ((List) objUserId)) {
                Long userId = (Long) o;
                String userIdStr = userId.toString();
                String userIdStrSuffix = userIdStr.substring(userIdStr.length() - 3);
                int userIdSuffixHash = Objects.hash(userIdStrSuffix);

                String tableName = logicTableName + StrUnderline + (userIdSuffixHash % collection.size());
                if (!collection.contains(tableName)) {
                    throw new UnsupportedOperationException("数据表 " + tableName + " 不存在");
                }

                collectionReturn.add(tableName);
            }


            return collectionReturn;
        }

        throw new UnsupportedOperationException("意料之外的不支持操作");
    }
}
