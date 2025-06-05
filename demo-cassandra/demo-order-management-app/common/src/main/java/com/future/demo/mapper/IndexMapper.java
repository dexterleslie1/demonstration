package com.future.demo.mapper;

import com.datastax.driver.core.*;
import com.future.common.exception.BusinessException;
import com.future.demo.entity.OrderIndexListByUserIdModel;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Component
public class IndexMapper {
    @Resource
    Session session;

    private PreparedStatement preparedStatementInsert;

    @PostConstruct
    public void init() {
        String cql = "INSERT INTO t_order_list_by_userId(user_id,create_time,status,order_id) " +
                "VALUES (?, ?, ?, ?)";
        preparedStatementInsert = session.prepare(cql);
        preparedStatementInsert.setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);
    }

    public void insertBatchOrderIndexListByUserId(List<OrderIndexListByUserIdModel> list) throws BusinessException {
        // region 订单ID为主键的订单表

        // 创建批量语句
        BatchStatement batch = new BatchStatement(BatchStatement.Type.LOGGED);

        // 添加多个订单到批量语句
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        for (int i = 0; i < list.size(); i++) {
            OrderIndexListByUserIdModel model = list.get(i);
            BoundStatement bound = preparedStatementInsert.bind(
                    model.getUserId(),
                    Date.from(model.getCreateTime().atZone(zoneId).toInstant()),
                    model.getStatus().name(),
                    model.getOrderId()
            );
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
