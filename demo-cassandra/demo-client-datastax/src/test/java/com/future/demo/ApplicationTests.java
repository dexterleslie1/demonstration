package com.future.demo;

import com.datastax.driver.core.*;
import com.future.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@Slf4j
public class ApplicationTests {

    @Autowired
    Session session;
    @Resource
    CommonMapper commonMapper;
    @Resource
    IndexMapper indexMapper;

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

        long userId = 1001L;
        Instant now = Instant.now();

        BoundStatement bound = preparedStatementOrderInsertion.bind(
                BigDecimal.valueOf(1L), // id
                userId,                  // user_id
                "Unpay",                // status
                Date.from(now),          // pay_time
                null,                   // delivery_time
                null,                   // received_time
                null,                   // cancel_time
                "Normal",               // delete_status
                Date.from(now)           // create_time
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
                    BigDecimal.valueOf(100L + i), // id
                    userId,                        // user_id
                    "Unpay",                      // status
                    // https://stackoverflow.com/questions/39926022/codec-not-found-for-requested-operation-timestamp-java-lang-long
                    Date.from(now),                // pay_time
                    null,                         // delivery_time
                    null,                         // received_time
                    null,                         // cancel_time
                    "Normal",                     // delete_status
                    Date.from(now)                 // create_time
            );
            batch = batch.add(bound);
        }

        // 执行批量插入
        result = session.execute(batch);
        Assertions.assertTrue(result.wasApplied());

        // endregion
    }

    /**
     * 测试并发更新同一条数据时数据是否一致
     */
    @Test
    public void testUpdateConcurrently() throws InterruptedException {
        // 重置 t_count 计数
        String cql = "select count from t_count where flag='order'";
        ResultSet resultSet = session.execute(cql);
        Row row = resultSet.one();
        long count = row.getLong("count");
        cql = "update t_count set count=count-" + count + " where flag='order'";
        resultSet = session.execute(cql);
        Assertions.assertTrue(resultSet.wasApplied());

        cql = "update t_count set count=count+? where flag='order'";
        PreparedStatement preparedStatement = session.prepare(cql);
        preparedStatement.setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);

        long totalCount = 10000;
        int concurrentThreads = 128;
        AtomicInteger counter = new AtomicInteger();
        ExecutorService threadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < concurrentThreads; i++) {
            threadPool.submit(() -> {
                try {
                    while (true) {
                        int countInternal = counter.getAndIncrement();
                        if (countInternal >= totalCount) {
                            break;
                        }

                        // 于 cassandra counter 类型列运算的数据类型只能为 long 类型
                        BoundStatement boundStatement = preparedStatement.bind(1L);
                        ResultSet resultSet1 = session.execute(boundStatement);
                        Assertions.assertTrue(resultSet1.wasApplied());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
        threadPool.shutdown();
        while (!threadPool.awaitTermination(100, TimeUnit.MILLISECONDS)) ;

        cql = "select * from t_count where flag='order'";
        resultSet = session.execute(cql);
        row = resultSet.one();
        count = row.getLong("count");
        Assertions.assertEquals(totalCount, count);
    }

    /**
     * 测试分页
     */
    @Test
    public void testPagination() throws BusinessException {
        // 删除旧数据
        this.commonMapper.truncate("t_order_list_by_userid");

        // 初始化数据
        long userId = 1L;
        String status = "Unpay";
        Instant now = Instant.now();
        int totalCount = 5;
        List<OrderIndexListByUserIdModel> modelList = new ArrayList<>();
        for (int i = 0; i < totalCount; i++) {
            OrderIndexListByUserIdModel model = new OrderIndexListByUserIdModel();
            model.setUserId(userId);
            model.setStatus(status);
            model.setCreateTime(now.atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime());

            long ordeId = i + 1;
            model.setOrderId(ordeId);
            modelList.add(model);
        }
        this.indexMapper.insertBatchOrderIndexListByUserId(modelList);

        // 获取第一页数据
        String cql = "select create_time,order_id from t_order_list_by_userid where user_id=?" +
                " and status=? and create_time>=? and create_time<=?" +
                " limit ?";
        PreparedStatement preparedStatement = session.prepare(cql);
        BoundStatement boundStatement = preparedStatement.bind(userId, "Unpay", Date.from(now), Date.from(now), 3);
        ResultSet resultSet = session.execute(boundStatement);
        List<Long> orderIdList = new ArrayList<>();
        LocalDateTime createTimeLast = null;
        for (Row row : resultSet) {
            Long orderId = row.getLong("order_id");
            orderIdList.add(orderId);

            if (!resultSet.iterator().hasNext()) {
                java.util.Date createTimeDate = row.getTimestamp("create_time");
                createTimeLast = createTimeDate.toInstant().atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
            }
        }
        Assertions.assertArrayEquals(new Long[]{5L, 4L, 3L}, orderIdList.toArray(new Long[]{}));

        // 获取第二页数据，使用第一页最后 order_id 记录作为边界查询
        cql = "select order_id from t_order_list_by_userid where user_id=?" +
                " and status=? and (create_time,order_id)<(?,?)" +
                " limit ?";
        preparedStatement = session.prepare(cql);
        boundStatement = preparedStatement.bind(userId, "Unpay",
                Date.from(createTimeLast.atZone(ZoneId.of("Asia/Shanghai")).toInstant()), orderIdList.get(orderIdList.size() - 1), 3);
        resultSet = session.execute(boundStatement);
        orderIdList = new ArrayList<>();
        for (Row row : resultSet) {
            Long orderId = row.getLong("order_id");
            orderIdList.add(orderId);
        }
        Assertions.assertArrayEquals(new Long[]{2L, 1L}, orderIdList.toArray(new Long[]{}));
    }

}
