package com.future.demo;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Random;

/**
 * 基于 Jedis 的 set 性能测试
 */
public class RedisBenchmarkSetByUsingLettuceSampler extends AbstractJavaSamplerClient {
    private static final Logger log = LoggerFactory.getLogger(RedisBenchmarkSetByUsingLettuceSampler.class);
    private static final Random RANDOM = new Random();

    static StatefulConnection<String, String> connection;
    static RedisClusterCommands<String, String> sync;

    private final int KeyLength = 300;

    @Override
    public void setupTest(JavaSamplerContext context) {
        String host = context.getParameter("host", "127.0.0.1");
        int port = context.getIntParameter("port", 6379);
        String password = context.getParameter("password", "");
        boolean cluster = Boolean.parseBoolean(context.getParameter("cluster", "false"));

        synchronized (RedisBenchmarkSetByUsingLettuceSampler.class) {
            // 非redis集群
            if (!cluster) {
                if (connection == null) {
                    if (password != null && !password.isEmpty()) {
                        RedisClient client = RedisClient.create("redis://" + password + "@" + host + ":" + port);
                        connection = client.connect();
                        sync = ((StatefulRedisConnection<String, String>) connection).sync();

                        if (log.isDebugEnabled()) {
                            log.debug("成功创建 Lettuce standalone 实例 host {} port {} password *** 线程 {}", host, port, Thread.currentThread().getName());
                        }
                    } else {
                        RedisClient client = RedisClient.create("redis://" + host + ":" + port);
                        connection = client.connect();
                        sync = ((StatefulRedisConnection<String, String>) connection).sync();

                        if (log.isDebugEnabled()) {
                            log.debug("成功创建 Lettuce standalone 实例 host {} port {} password 无密码模式 线程 {}", host, port, Thread.currentThread().getName());
                        }
                    }
                }
            } else {
                // redis集群
                if (sync == null) {
                    RedisURI node1 = RedisURI.create(host, port);
                    RedisClusterClient client = RedisClusterClient.create(Collections.singletonList(node1));
                    connection = client.connect();
                    sync = ((StatefulRedisClusterConnection<String, String>) connection).sync();
                    log.debug("成功创建 Lettuce 集群实例，host{} port {} password *** ", host, port);
                }
            }
        }
    }

    @Override
    public void teardownTest(JavaSamplerContext context) {
        synchronized (RedisBenchmarkSetByUsingLettuceSampler.class) {
            if (connection != null) {
                connection.close();
                connection = null;
            }
            if (log.isDebugEnabled()) {
                log.debug("成功关闭 Lettuce 实例");
            }
        }
    }

    @Override
    public Arguments getDefaultParameters() {
        return new Arguments() {{
            // redis主机ip
            addArgument("host", "127.0.0.1");
            // redis端口
            addArgument("port", "6379");
            // redis密码
            addArgument("password", "");
            // 是否集群
            addArgument("cluster", "false");
        }};
    }

    final static String Label = "Set";

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult result = new SampleResult();

        try {
            result.sampleStart();

            result.setSampleLabel(Label);

            int randInt = RANDOM.nextInt(KeyLength);
            String key = String.valueOf(randInt);
            sync.set(key, key);

            if (log.isDebugEnabled()) {
                log.debug("写性能测试随机 key {} value {} 并使用此key和value set", key, key);
            }

            // 标记样本成功
            result.setSuccessful(true);
            // 设置样本请求成功
            result.setResponseOK();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            // 标记样本失败
            result.setSuccessful(false);
        } finally {
            result.setDataType(SampleResult.TEXT);
            result.sampleEnd();
        }

        return result;
    }
}

