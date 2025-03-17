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
     * 是否连接redis集群
     */
    private boolean cluster;
    /**
     * RedisTemplate 连接工厂
     */
    RedisConnectionFactory connectionFactory = null;
    /**
     * RedisTemplate 客户端
     */
    StringRedisTemplate redisTemplate;

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

        if (this.connectionFactory == null) {
            if (!this.cluster) {
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
                this.connectionFactory = new LettuceConnectionFactory(configuration, clientConfig);
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
                this.connectionFactory = new LettuceConnectionFactory(configuration, clientConfig);
            }
            ((LettuceConnectionFactory) this.connectionFactory).afterPropertiesSet();

            this.redisTemplate = new StringRedisTemplate(this.connectionFactory);
            this.redisTemplate.afterPropertiesSet();

            if (log.isDebugEnabled()) {
                log.debug("成功创建 RedisTemplate 实例 host {} port {} password *** 线程 {}", host, port, Thread.currentThread().getName());
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("意料之外，connectionFactory 实例已经初始化");
            }
        }
    }


    @Override
    public void teardownTest(JavaSamplerContext context) {
        if (this.connectionFactory != null) {
            try {
                ((DisposableBean) this.connectionFactory).destroy();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            this.connectionFactory = null;

            if (log.isDebugEnabled()) {
                log.debug("成功关闭 RedisTemplate 实例");
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
            this.redisTemplate.opsForValue().set(keyToWrite, keyToWrite);

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

