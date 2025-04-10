package com.future.demo;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.resource.ClientResources;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;

/**
 * 基于 RedisTemplate 的 set 性能测试
 */
public class RedisBenchmarkSetByUsingRedisTemplateSampler extends AbstractJavaSamplerClient {
    private static final Logger log = LoggerFactory.getLogger(RedisBenchmarkSetByUsingRedisTemplateSampler.class);
    private static final Random RANDOM = new Random();

    /**
     * RedisTemplate 连接工厂
     */
    static RedisConnectionFactory connectionFactory = null;
    /**
     * RedisTemplate 客户端
     */
    static StringRedisTemplate redisTemplate;

    private final int KeyLength = 300;

    @Override
    public void setupTest(JavaSamplerContext context) {
        String host = context.getParameter("host", "127.0.0.1");
        int port = context.getIntParameter("port", 6379);
        String password = context.getParameter("password", "");
        boolean cluster = Boolean.parseBoolean(context.getParameter("cluster", "false"));

        synchronized (RedisBenchmarkSetByUsingRedisTemplateSampler.class) {
            if (connectionFactory == null) {
                if (!cluster) {
                    GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
                    poolConfig.setMaxTotal(512);
                    LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                            .poolConfig(poolConfig)
                            .clientResources(ClientResources.builder().build())
                            .clientOptions(ClientOptions.builder().build())
                            .build();

                    RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
                    if (password != null && !password.isEmpty()) {
                        configuration.setPassword(password);
                    }
                    connectionFactory = new LettuceConnectionFactory(configuration, clientConfig);
                } else {
                    GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
                    poolConfig.setMaxTotal(512);
                    LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                            .poolConfig(poolConfig)
                            .clientResources(ClientResources.builder().build())
                            .clientOptions(ClientOptions.builder().build())
                            .build();

                    RedisClusterConfiguration configuration = new RedisClusterConfiguration();
                    configuration.setClusterNodes(new HashSet<RedisNode>() {{
                        this.add(new RedisNode(host, port));
                    }});
                    connectionFactory = new LettuceConnectionFactory(configuration, clientConfig);
                }
                ((LettuceConnectionFactory) connectionFactory).afterPropertiesSet();

                redisTemplate = new StringRedisTemplate(this.connectionFactory);
                redisTemplate.afterPropertiesSet();

                if (log.isDebugEnabled()) {
                    log.debug("成功创建 RedisTemplate 实例 host {} port {} password *** 线程 {}", host, port, Thread.currentThread().getName());
                }
            }
        }
    }


    @Override
    public void teardownTest(JavaSamplerContext context) {
        synchronized (RedisBenchmarkSetByUsingRedisTemplateSampler.class) {
            if (connectionFactory != null) {
                try {
                    ((DisposableBean) connectionFactory).destroy();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                connectionFactory = null;

                if (log.isDebugEnabled()) {
                    log.debug("成功关闭 RedisTemplate 实例");
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
            redisTemplate.opsForValue().set(key, key);

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

