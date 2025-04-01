package com.future.demo;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 基于 Jedis 的 set 性能测试
 */
public class RedisBenchmarkSetByUsingLettuceSampler extends AbstractJavaSamplerClient {
    private static final Logger log = LoggerFactory.getLogger(RedisBenchmarkSetByUsingLettuceSampler.class);
    private static final Random RANDOM = new Random();

    /**
     * 是否连接redis集群
     */
    private boolean cluster;

    /**
     * 集群模式
     */
    RedisClusterClient clusterClient;
    /**
     * 集群模式
     */
    StatefulRedisClusterConnection<String, String> clusterConnection;
    RedisClusterCommands<String, String> clusterSync;

    /**
     * Standalone模式
     */
    RedisClient client;
    /**
     * Standalone模式
     */
    StatefulRedisConnection<String, String> connection;
    RedisCommands<String, String> sync;

    private final int totalKeyListSize = 100;
    /**
     * 用于测试 set 指令性能的 Key 和 Value
     */
    private List<String> keyListToWrite = new ArrayList<String>() {{
        for (int i = 0; i < totalKeyListSize; i++) {
            this.add(UUID.randomUUID().toString());
        }
    }};

    @Override
    public void setupTest(JavaSamplerContext context) {
        String host = context.getParameter("host", "127.0.0.1");
        int port = context.getIntParameter("port", 6379);
        String password = context.getParameter("password", "");
        this.cluster = Boolean.parseBoolean(context.getParameter("cluster", "false"));

        // 非redis集群
        if (!this.cluster) {
            if (clusterClient == null) {
                if (password != null && !password.isEmpty()) {
                    client = RedisClient.create("redis://" + password + "@" + host + ":" + port);
                    connection = client.connect();
                    sync = connection.sync();

                    if (log.isDebugEnabled()) {
                        log.debug("成功创建 Lettuce standalone 实例 host {} port {} password *** 线程 {}", host, port, Thread.currentThread().getName());
                    }
                } else {
                    client = RedisClient.create("redis://" + host + ":" + port);
                    connection = client.connect();
                    sync = connection.sync();

                    if (log.isDebugEnabled()) {
                        log.debug("成功创建 Lettuce standalone 实例 host {} port {} password 无密码模式 线程 {}", host, port, Thread.currentThread().getName());
                    }
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("意料之外，Lettuce standalone 实例已经初始化");
                }
            }
        } else {
            // redis集群
            if (this.clusterClient == null) {
                RedisURI node1 = RedisURI.create(host, port);
                clusterClient = RedisClusterClient.create(Collections.singletonList(node1));
                clusterConnection = clusterClient.connect();
                clusterSync = clusterConnection.sync();
                log.debug("成功创建 Lettuce 集群实例，host{} port {} password *** ", host, port);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("意料之外，Lettuce 集群实例已经初始化");
                }
            }
        }
    }

    @Override
    public void teardownTest(JavaSamplerContext context) {
        if (!this.cluster) {
            if (this.connection != null) {
                this.connection.close();
                this.connection = null;
            }
            if (client != null) {
                client.shutdown();
                client = null;
            }

            if (log.isDebugEnabled()) {
                log.debug("成功关闭 Lettuce standalone 实例");
            }
        } else {
            if (clusterConnection != null) {
                clusterConnection.close();
                clusterConnection = null;
            }
            if (clusterClient != null) {
                clusterClient.shutdown();
                clusterClient = null;
            }

            if (log.isDebugEnabled()) {
                log.debug("成功关闭 Lettuce 集群实例");
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

            String keyToWrite = this.keyListToWrite.get(RANDOM.nextInt(totalKeyListSize));
            if (!this.cluster) {
                sync.set(keyToWrite, keyToWrite);
            } else {
                clusterSync.set(keyToWrite, keyToWrite);
            }

            if (log.isDebugEnabled()) {
                log.debug("写性能测试随机 key {} value {} 并使用此key和value set", keyToWrite, keyToWrite);
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

