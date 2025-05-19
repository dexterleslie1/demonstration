package com.future.demo;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;

@SpringBootTest
@Slf4j
public class ApplicationTests {
    @Autowired
    CqlSession cqlSession;

    @Test
    public void test() {
        // region 测试单条插入

        String cql = "INSERT INTO t_order (id, user_id, status, pay_time, delivery_time, received_time, cancel_time, delete_status, create_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement prepared = cqlSession.prepare(cql);
        BoundStatement bound = prepared.bind(
                BigDecimal.valueOf(1L), // id
                1001L,                  // user_id
                "Unpay",                // status
                Instant.now(),          // pay_time
                null,                   // delivery_time
                null,                   // received_time
                null,                   // cancel_time
                "Normal",               // delete_status
                Instant.now()           // create_time
        );

        ResultSet result = cqlSession.execute(bound);
        Assertions.assertTrue(result.wasApplied());

        // endregion

        // region 测试批量插入

        // 准备批量插入语句
        cql = "INSERT INTO t_order (id, user_id, status, pay_time, delivery_time, received_time, cancel_time, delete_status, create_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        prepared = cqlSession.prepare(cql);

        // 创建批量语句
        BatchStatement batch = BatchStatement.newInstance(BatchType.LOGGED);

        // 添加多个订单到批量语句
        for (int i = 0; i < 5; i++) {
            bound = prepared.bind(
                    BigDecimal.valueOf(100L + i), // id
                    1001L,                        // user_id
                    "Unpay",                      // status
                    Instant.now(),                // pay_time
                    null,                         // delivery_time
                    null,                         // received_time
                    null,                         // cancel_time
                    "Normal",                     // delete_status
                    Instant.now()                 // create_time
            );
            batch = batch.add(bound);
        }

        // 执行批量插入
        result = cqlSession.execute(batch);
        Assertions.assertTrue(result.wasApplied());

        // endregion
    }
}
