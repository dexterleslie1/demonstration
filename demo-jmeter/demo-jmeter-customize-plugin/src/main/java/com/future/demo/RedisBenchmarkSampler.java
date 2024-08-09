package com.future.demo;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.Jedis;

import java.util.Random;
import java.util.UUID;

/**
 * redis性能测试sampler
 * 测试策略：redis写和读每个测试loop随机选择
 * 1、测试redis写(set)性能：随机选择redis-benchmark-0到redis-benchmark-10000000范围的键作为key，使用随机uuid作为value。
 * 2、测试redis读(get)性能：随机选择redis-benchmark-0到redis-benchmark-10000000范围的键作为key。
 */
public class RedisBenchmarkSampler extends AbstractJavaSamplerClient {
    private static final Logger log = LoggerFactory.getLogger(RedisBenchmarkSampler.class);

    private static final String KeyPrefix = "redis-benchmark-";
    private static final int KeySuffixMaximum = 10000000;
    private static final Random R = new Random(System.currentTimeMillis());

    Jedis jedis;

    @Override
    public void setupTest(JavaSamplerContext context) {
        if (jedis == null) {
            String host = context.getParameter("host", "127.0.0.1");
            int port = context.getIntParameter("port", 6379);
            String password = context.getParameter("password", "123456");
            // 注意：暂时没有找到方法当jedis实例创建失败时停止测试
            jedis = new Jedis(host, port, DefaultJedisClientConfig.builder().password(password).build());

            if (log.isDebugEnabled()) {
                log.debug("成功创建jedis实例 host {} port {} password *** 线程 {}", host, port, Thread.currentThread().getName());
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("意料之外，jedis实例已经初始化");
            }
        }
    }

    @Override
    public void teardownTest(JavaSamplerContext context) {
        if (jedis != null) {
            jedis.close();
            jedis = null;

            if (log.isDebugEnabled()) {
                log.debug("成功关闭jedis实例");
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
            addArgument("password", "123456");
        }};
    }

    final static int ignored1 = 2;
    final static String ignored2 = "value";
    final static String ignored3 = "set";
    final static String ignored4 = "get";

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult result = new SampleResult();

        try {
            result.sampleStart();

            int randomInt = R.nextInt(ignored1);
            if (randomInt == 0) {
                result.setSampleLabel(ignored3);

                // 写性能
                String key = KeyPrefix + R.nextInt(KeySuffixMaximum);
                String value = UUID.randomUUID().toString();
                this.jedis.set(key, value);

                if (log.isDebugEnabled()) {
                    log.debug("写性能测试随机 key {} value {} 并使用此key和value set", key, value);
                }
            } else {
                result.setSampleLabel(ignored4);

                // 读性能
                String key = KeyPrefix + R.nextInt(KeySuffixMaximum);
                String value = this.jedis.get(key);

                if (value != null) {
                    context.getJMeterContext().setVariables(new JMeterVariables() {{
                        this.put(ignored2, value);
                    }});
                }

                if (log.isDebugEnabled()) {
                    log.debug("读性能测试随机 key {} 并使用此key get得到value {}", key, value);
                }
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
