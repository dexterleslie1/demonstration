package com.future.demo.algorithm.database;

import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MyDatabaseComplexKeysShardingAlgorithm implements ComplexKeysShardingAlgorithm {
    final static String StrOrderId = "id";
    final static String StrUserId = "user_id";
    final static String StrDataSourcePrefix = "ds";

    @Override
    public Collection<String> doSharding(Collection collection, ComplexKeysShardingValue complexKeysShardingValue) {
        Object objOrderId = complexKeysShardingValue.getColumnNameAndShardingValuesMap().get(StrOrderId);
        Object objUserId = complexKeysShardingValue.getColumnNameAndShardingValuesMap().get(StrUserId);

        if (objOrderId != null && !((List) objOrderId).isEmpty()) {
            List<String> collectionReturn = new ArrayList<>();
            for (Object o : ((List) objOrderId)) {
                BigDecimal orderId = (BigDecimal) o;
                String orderIdStr = orderId.toString();
                String orderIdStrSuffix = orderIdStr.substring(orderIdStr.length() - 3);

                String dataSourceName = StrDataSourcePrefix + (Integer.parseInt(orderIdStrSuffix) % collection.size());

                if (!collection.contains(dataSourceName)) {
                    throw new UnsupportedOperationException("数据源 " + dataSourceName + " 不存在");
                }

                collectionReturn.add(dataSourceName);
            }

            return collectionReturn;
        } else if (objUserId != null && !((List) objUserId).isEmpty()) {
            List collectionReturn = new ArrayList();
            for (Object o : ((List) objUserId)) {
                Long userId = (Long) o;
                String userIdStr = userId.toString();
                String userIdStrSuffix = userIdStr.substring(userIdStr.length() - 3);

                String dataSourceName = StrDataSourcePrefix + (Integer.parseInt(userIdStrSuffix) % collection.size());

                if (!collection.contains(dataSourceName)) {
                    throw new UnsupportedOperationException("数据源 " + dataSourceName + " 不存在");
                }

                collectionReturn.add(dataSourceName);
            }

            return collectionReturn;
        }

        throw new UnsupportedOperationException("意料之外的不支持操作");
    }
}
