package com.future.demo;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import redis.clients.jedis.*;

/**
 * redis工具类
 *
 * @author Dexterleslie.Chan
 */
public class JedisUtil {
    private static JedisCluster jedisPool = null;

    private JedisUtil() {
    }

    //写成静态代码块形式，只加载一次，节省资源
    static {
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

        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        jedisClusterNodes.add(new HostAndPort("localhost", 6380));
        jedisClusterNodes.add(new HostAndPort("localhost", 6381));
        jedisClusterNodes.add(new HostAndPort("localhost", 6382));
        try {
            jedisPool = new JedisCluster(jedisClusterNodes, config);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 从jedis连接池中获取获取jedis对象
     *
     * @return
     */
    public JedisCluster getJedis() {
        return jedisPool;
    }

    private static final JedisUtil jedisUtil = new JedisUtil();

    /**
     * 获取JedisUtil实例
     *
     * @return
     */
    public static JedisUtil getInstance() {
        return jedisUtil;
    }

//    /**
//     * 回收jedis(放到finally中)
//     *
//     * @param jedis
//     */
//    public void returnJedis(Jedis jedis) {
//        if (null != jedis) {
//            jedis.close();
//        }
//    }
}

