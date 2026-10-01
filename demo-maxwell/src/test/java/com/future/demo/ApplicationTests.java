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
import java.util.UUID;
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
        Properties before = amqpAdmin.getQueueProperties(Config.QueueName);
        int consumersBefore = before == null ? 0
                : Integer.parseInt(String.valueOf(before.getOrDefault(RabbitAdmin.QUEUE_CONSUMER_COUNT, "0")));
        // SpringBoot 启动后本进程会挂 ConcurrentConsumers 个消费者；若明显更多，说明有外部实例在抢消息
        Assertions.assertTrue(consumersBefore <= Config.ConcurrentConsumers,
                "检测到其他进程在消费 " + Config.QueueName
                        + "（consumers=" + consumersBefore + "），请先停止 IDEA 中的 Application / 其他消费者再测");

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

    private void batchInsertUsers(int total) {
        String sql = "insert into t_user(username, createTime) values(?, now())";
        int offset = 0;
        while (offset < total) {
            final int from = offset;
            final int size = Math.min(JdbcBatchSize, total - offset);
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setString(1, "perf-" + from + "-" + i + "-" + UUID.randomUUID());
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
