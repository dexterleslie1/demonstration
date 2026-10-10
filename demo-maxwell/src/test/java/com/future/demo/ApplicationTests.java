package com.future.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * 批量插入 t_user，观察 Receiver 批量消费吞吐。
 * <p>
 * 前置：{@code docker compose up -d} 已启动 MariaDB + RabbitMQ + Maxwell。
 * </p>
 *
 * @author Dexterleslie.Chan
 */
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ApplicationTests {
    private static final Logger log = LoggerFactory.getLogger(ApplicationTests.class);

    /** 本次压测插入条数 */
    private static final int InsertCount = 100000;
    /** JDBC 每批写入条数 */
    private static final int JdbcBatchSize = 500;
    /** 等待消费完成的超时（Maxwell→RabbitMQ 可能较慢） */
    private static final long ConsumeTimeoutSeconds = 300;

    @Resource
    Receiver receiver;
    @Resource
    JdbcTemplate jdbcTemplate;
    @Resource
    AmqpAdmin amqpAdmin;

    @Test
    public void testBatchInsertThroughput() throws InterruptedException {
        assertNoForeignConsumers();

        // 清空队列与统计，避免历史消息干扰
        amqpAdmin.purgeQueue(Config.QueueName, false);
        TimeUnit.SECONDS.sleep(2);
        receiver.resetStats();

        long insertStart = System.currentTimeMillis();
        batchInsertUsers(InsertCount);
        long insertCostMs = System.currentTimeMillis() - insertStart;
        log.info("批量插入完成 count={}, costMs={}, insertQps={}",
                InsertCount, insertCostMs, qps(InsertCount, insertCostMs));

        long waitStart = System.currentTimeMillis();
        boolean done = waitUntilConsumed(InsertCount, ConsumeTimeoutSeconds);
        long waitCostMs = System.currentTimeMillis() - waitStart;

        long consumed = receiver.getConsumed().get();
        long batches = receiver.getBatchCallbacks().get();
        long consumeWindowMs = Math.max(1, receiver.getLastConsumeAt() - receiver.getFirstConsumeAt());
        int queueDepth = queueMessageCount();

        log.info("批量消费结束 consumed={}, batches={}, avgBatchSize={}, queueDepth={}, waitCostMs={}, consumeWindowMs={}, consumeQps={}",
                consumed,
                batches,
                batches == 0 ? 0 : (consumed * 1.0 / batches),
                queueDepth,
                waitCostMs,
                consumeWindowMs,
                qps(consumed, consumeWindowMs));

        Assertions.assertTrue(done,
                "超时未消费完，expected=" + InsertCount
                        + ", actual=" + consumed
                        + ", queueDepth=" + queueDepth);
    }

    /**
     * 同一事务删除 10 万行：验证 Receiver 单次 batch 不会一次收到 10 万条。
     * <p>
     * 预期：Maxwell 仍按行产出约 10 万条 CDC；Spring AMQP 受 {@link Config#BatchSize}
     * 与 prefetch 限制，单次 {@code receiveMessages} 的 List 大小 &lt;= BatchSize。
     * </p>
     */
    @Test
    public void testSingleTxnDeleteDoesNotProduceHugeBatch() throws InterruptedException {
        assertNoForeignConsumers();

        String prefix = "td" + Long.toString(System.currentTimeMillis(), 36) + "-";
        amqpAdmin.purgeQueue(Config.QueueName, false);
        TimeUnit.SECONDS.sleep(2);
        receiver.resetStats();

        // 先插入 10 万行并等 insert CDC 消费完，再单独测 delete 批次
        batchInsertUsers(InsertCount, prefix);
        Assertions.assertTrue(waitUntilConsumed(InsertCount, ConsumeTimeoutSeconds),
                "insert CDC 未消费完，无法继续测 delete");

        amqpAdmin.purgeQueue(Config.QueueName, false);
        TimeUnit.SECONDS.sleep(1);
        receiver.resetStats();

        long deleteStart = System.currentTimeMillis();
        // 单条 DELETE = 单个事务删掉全部匹配行（非逐行提交）
        int deleted = jdbcTemplate.update("delete from t_user where username like ?", prefix + "%");
        long deleteCostMs = System.currentTimeMillis() - deleteStart;
        log.info("单事务删除完成 deleted={}, costMs={}", deleted, deleteCostMs);
        Assertions.assertEquals(InsertCount, deleted, "应在同一 DELETE 中删掉全部准备数据");

        boolean done = waitUntilConsumed(InsertCount, ConsumeTimeoutSeconds);
        long consumed = receiver.getConsumed().get();
        long batches = receiver.getBatchCallbacks().get();
        long maxBatch = receiver.getMaxBatchSize();
        double avgBatch = batches == 0 ? 0 : (consumed * 1.0 / batches);

        log.info("单事务 delete 消费结束 consumed={}, batches={}, avgBatchSize={}, maxBatchSize={}, configBatchSize={}",
                consumed, batches, avgBatch, maxBatch, Config.BatchSize);

        Assertions.assertTrue(done,
                "超时未消费完 delete CDC，expected=" + InsertCount + ", actual=" + consumed);
        Assertions.assertTrue(maxBatch > 0, "应至少收到一批消息");
        // 核心断言：不会因为「一个事务删了 10 万行」就在一个 List 里塞 10 万条
        Assertions.assertTrue(maxBatch <= Config.BatchSize,
                "单次 batch 过大：maxBatchSize=" + maxBatch + ", configBatchSize=" + Config.BatchSize);
        Assertions.assertTrue(batches >= InsertCount / Config.BatchSize,
                "批次数过少，可能出现超大 batch：batches=" + batches);
    }

    private void assertNoForeignConsumers() {
        Properties before = amqpAdmin.getQueueProperties(Config.QueueName);
        int consumersBefore = before == null ? 0
                : Integer.parseInt(String.valueOf(before.getOrDefault(RabbitAdmin.QUEUE_CONSUMER_COUNT, "0")));
        Assertions.assertTrue(consumersBefore <= Config.ConcurrentConsumers,
                "检测到其他进程在消费 " + Config.QueueName
                        + "（consumers=" + consumersBefore + "），请先停止 IDEA 中的 Application / 其他消费者再测");
    }

    private void batchInsertUsers(int total) {
        batchInsertUsers(total, "perf-");
    }

    private void batchInsertUsers(int total, String usernamePrefix) {
        String sql = "insert into t_user(username, createTime) values(?, now())";
        int offset = 0;
        while (offset < total) {
            final int from = offset;
            final int size = Math.min(JdbcBatchSize, total - offset);
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    // username VARCHAR(64)：前缀 + 序号即可，避免 UUID 超长
                    ps.setString(1, usernamePrefix + (from + i));
                }

                @Override
                public int getBatchSize() {
                    return size;
                }
            });
            offset += size;
        }
    }

    private boolean waitUntilConsumed(int expected, long timeoutSeconds) throws InterruptedException {
        long deadline = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(timeoutSeconds);
        long lastLogged = -1;
        while (System.currentTimeMillis() < deadline) {
            long consumed = receiver.getConsumed().get();
            if (consumed >= expected) {
                return true;
            }
            // 每消费约 1000 条打一次进度，便于观察 Maxwell/Receiver 是否卡住
            if (consumed / 1000 != lastLogged) {
                lastLogged = consumed / 1000;
                log.info("消费进度 consumed={}/{}, queueDepth={}, batches={}",
                        consumed, expected, queueMessageCount(), receiver.getBatchCallbacks().get());
            }
            TimeUnit.MILLISECONDS.sleep(200);
        }
        return receiver.getConsumed().get() >= expected;
    }

    private int queueMessageCount() {
        Properties props = amqpAdmin.getQueueProperties(Config.QueueName);
        if (props == null) {
            return -1;
        }
        Object count = props.get(RabbitAdmin.QUEUE_MESSAGE_COUNT);
        return count == null ? -1 : Integer.parseInt(count.toString());
    }

    private static long qps(long count, long costMs) {
        if (costMs <= 0) {
            return count;
        }
        return count * 1000L / costMs;
    }
}
