package com.future.demo;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * 基于 Jedis 的 set 性能测试
 */
public class RedisBenchmarkSetByUsingJedisSampler extends AbstractJavaSamplerClient {
    private static final Logger log = LoggerFactory.getLogger(RedisBenchmarkSetByUsingJedisSampler.class);
    private static final Random RANDOM = new Random();

    /**
     * 是否连接redis集群
     */
    private boolean cluster;
    /**
     * 非redis集群客户端
     */
    Jedis jedis;
    /**
     * redis集群客户端
     */
    JedisCluster jedisCluster;
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
            if (jedis == null) {
                if (password != null && !password.isEmpty()) {
                    // 注意：暂时没有找到方法当jedis实例创建失败时停止测试
                    jedis = new Jedis(new HostAndPort(host, port), DefaultJedisClientConfig.builder().password(password).build());

                    if (log.isDebugEnabled()) {
                        log.debug("成功创建jedis实例 host {} port {} password *** 线程 {}", host, port, Thread.currentThread().getName());
                    }
                } else {
                    jedis = new Jedis(host, port);

                    if (log.isDebugEnabled()) {
                        log.debug("成功创建jedis实例 host {} port {} password 无密码模式 线程 {}", host, port, Thread.currentThread().getName());
                    }
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("意料之外，jedis实例已经初始化");
                }
            }
        } else {
            // redis集群
            if (this.jedisCluster == null) {
                this.jedisCluster = new JedisCluster(new HostAndPort(host, port));
                log.debug("成功创建 Jedis 集群实例，host{} port {} password *** ", host, port);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("意料之外，jedisCluster实例已经初始化");
                }
            }
        }
    }

    @Override
    public void teardownTest(JavaSamplerContext context) {
        if (!this.cluster) {
            if (jedis != null) {
                jedis.close();
                jedis = null;

                if (log.isDebugEnabled()) {
                    log.debug("成功关闭jedis实例");
                }
            }
        } else {
            if (this.jedisCluster != null) {
                this.jedisCluster.close();
                this.jedisCluster = null;

                if (log.isDebugEnabled()) {
                    log.debug("成功关闭jedisCluster实例");
                }
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
                this.jedis.set(keyToWrite, keyToWrite);
            } else {
                this.jedisCluster.set(keyToWrite, keyToWrite);
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

