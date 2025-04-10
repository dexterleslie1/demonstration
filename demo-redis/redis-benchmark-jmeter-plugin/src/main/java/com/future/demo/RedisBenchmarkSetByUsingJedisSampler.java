package com.future.demo;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.util.Random;

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
    static JedisCluster jedisCluster;
    private final int KeyLength = 300;

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
            synchronized (RedisBenchmarkSetByUsingLettuceSampler.class) {
                // redis集群
                if (jedisCluster == null) {
                    int maxTotal = 600;
                    int maxIdle = 300;
                    int maxWaitMillis = 1000;
                    boolean testOnBorrow = false;

                    JedisPoolConfig config = new JedisPoolConfig();
                    //控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
                    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
                    config.setMaxTotal(maxTotal);
                    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
                    config.setMaxIdle(maxIdle);
                    //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
                    config.setMaxWaitMillis(maxWaitMillis);
                    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
                    config.setTestOnBorrow(testOnBorrow);
                    jedisCluster = new JedisCluster(new HostAndPort(host, port), config);
                    log.debug("成功创建 Jedis 集群实例，host{} port {} password *** ", host, port);
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
            synchronized (RedisBenchmarkSetByUsingJedisSampler.class) {
                if (jedisCluster != null) {
                    jedisCluster.close();
                    jedisCluster = null;

                    if (log.isDebugEnabled()) {
                        log.debug("成功关闭jedisCluster实例");
                    }
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

            int randInt = RANDOM.nextInt(KeyLength);
            String key = String.valueOf(randInt);
            if (!this.cluster) {
                this.jedis.set(key, key);
            } else {
                jedisCluster.set(key, key);
            }

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

