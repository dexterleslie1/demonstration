package com.future.demo;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import com.future.common.exception.BusinessException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.ZoneOffset;
import java.util.List;

@Component
public class IndexMapper {
    @Resource
    // cassandra3 驱动程序
    // Session session
    // cassandra5 驱动程序
    CqlSession session;

    private PreparedStatement preparedStatementInsert;

    @PostConstruct
    public void init() {
        String cql = "INSERT INTO t_order_list_by_userId(user_id,create_time,status,order_id) " +
                "VALUES (?, ?, ?, ?)";
        preparedStatementInsert = session.prepare(cql);
        // cassandra3 驱动程序
        /*preparedStatementInsert.setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);*/
    }

    public void insertBatchOrderIndexListByUserId(List<OrderIndexListByUserIdModel> list) throws BusinessException {
        // region 订单ID为主键的订单表

        // 创建批量语句
        // cassandra3 驱动程序
        /*BatchStatement batch = new BatchStatement(BatchStatement.Type.LOGGED);*/
        // cassandra5 驱动程序
        BatchStatement batch = BatchStatement.newInstance(BatchType.LOGGED);

        // 添加多个订单到批量语句
        for (int i = 0; i < list.size(); i++) {
            OrderIndexListByUserIdModel model = list.get(i);
            BoundStatement bound = preparedStatementInsert.bind(
                    model.getUserId(),
                    model.getCreateTime().toInstant(ZoneOffset.ofHours(8)),
                    model.getStatus(),
                    model.getOrderId()
            );

            // cassandra5 驱动程序
            bound = bound.setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);

            batch = batch.add(bound);
        }

        // 执行批量插入
        ResultSet result = session.execute(batch);
        if (!result.wasApplied()) {
            throw new BusinessException("t_order_list_by_userId 批量插入失败");
        }

        // endregion
    }
}
