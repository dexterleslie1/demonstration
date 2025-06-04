package com.future.demo;

import com.datastax.driver.core.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.PostConstruct;
import java.sql.Date;
import java.time.Instant;

@SpringBootTest
@Slf4j
public class ApplicationTests {

    @Autowired
    Session session;

    private PreparedStatement preparedStatementOrderInsertion;

    @PostConstruct
    public void init() {
        // cql 只需要 prepare 一次
        String cql = "INSERT INTO t_order (id, user_id, status, pay_time, delivery_time, received_time, cancel_time, delete_status, create_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        this.preparedStatementOrderInsertion = session.prepare(cql);
    }

    @Test
    public void test() {
        // region 测试单条插入

        BoundStatement bound = preparedStatementOrderInsertion.bind(
                1L, // id
                1001L,                  // user_id
                "Unpay",                // status
                Date.from(Instant.now()),          // pay_time
                null,                   // delivery_time
                null,                   // received_time
                null,                   // cancel_time
                "Normal",               // delete_status
                Date.from(Instant.now())           // create_time
        );

        ResultSet result = session.execute(bound);
        Assertions.assertTrue(result.wasApplied());

        // endregion

        // region 测试批量插入

        // 准备批量插入语句

        // 创建批量语句
        BatchStatement batch = new BatchStatement(BatchStatement.Type.LOGGED);

        // 添加多个订单到批量语句
        for (int i = 0; i < 5; i++) {
            bound = preparedStatementOrderInsertion.bind(
                    100L + i, // id
                    1001L,                        // user_id
                    "Unpay",                      // status
                    // https://stackoverflow.com/questions/39926022/codec-not-found-for-requested-operation-timestamp-java-lang-long
                    Date.from(Instant.now()),                // pay_time
                    null,                         // delivery_time
                    null,                         // received_time
                    null,                         // cancel_time
                    "Normal",                     // delete_status
                    Date.from(Instant.now())                 // create_time
            );
            batch = batch.add(bound);
        }

        // 执行批量插入
        result = session.execute(batch);
        Assertions.assertTrue(result.wasApplied());

        // endregion
    }
}
