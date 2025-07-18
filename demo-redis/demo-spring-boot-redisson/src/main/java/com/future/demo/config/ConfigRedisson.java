package com.future.demo.config;

import com.future.demo.controller.MapController;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class ConfigRedisson {

    public final static int TotalShards = 128;
    /**
     * RMap 对应的 key
     */
    public final static String KeyRMap = "testRMap";
    /**
     * RMapCache 对应的 key
     */
    public final static String KeyRMapCache = "testRMapCache";
    /**
     * RLocalCachedMap 对应的 key
     */
    public final static String KeyRLocalCachedMap = "testRLocalCachedMap";
    /**
     * RSet 对应的 key
     */
    public final static String KeyRSet = "testRSet";

    @Value("${spring.future.demo.redisson.redis.mode}")
    private String mode;

    @Value("${spring.future.demo.redisson.standalone.address}")
    private String standaloneAddress;
    @Value("${spring.future.demo.redisson.standalone.password}")
    private String standalonePassword;

    @Value("${spring.future.demo.redisson.replication.addresses}")
    private String replicationAddresses;

    @Value("${spring.future.demo.redisson.sentinel.master-name}")
    private String sentinelMasterName;
    @Value("${spring.future.demo.redisson.sentinel.addresses}")
    private String sentinelAddresses;

    @Value("${spring.future.demo.redisson.cluster.addresses}")
    private String clusterAddresses;

    @Bean
    RedissonClient redissonClient() {
        Config config = new Config();

        if (mode.equalsIgnoreCase("standalone")) {
            // Redis Standalone 模式配置
            config.useSingleServer()
                    .setAddress(standaloneAddress)
                    .setPassword(standalonePassword);
        } else if (mode.equalsIgnoreCase("replication")) {
            // Redis Replication 模式配置
            config.useReplicatedServers()
                    .addNodeAddress(replicationAddresses.split(","));
        } else if (mode.equalsIgnoreCase("sentinel")) {
            // Redis Sentinel 模式配置
            config.useSentinelServers()
                    .setMasterName(sentinelMasterName)
                    // 下面配置 Sentinel 节点
                    .addSentinelAddress(sentinelAddresses.split(","));
        } else if (mode.equalsIgnoreCase("cluster")) {
            // Redis Cluster 模式配置
            config.useClusterServers()
                    .addNodeAddress(clusterAddresses.split(","));
        }

        return Redisson.create(config);
    }

    /**
     * 创建 RMap<Long, String>
     *
     * @param redissonClient
     * @return
     */
    @Bean
    List<RMap<Long, String>> rMapShardList(@Autowired RedissonClient redissonClient) {
        List<RMap<Long, String>> rMapList = new ArrayList<>();
        for (int i = 0; i < TotalShards; i++) {
            String key = KeyRMap + i;
            rMapList.add(redissonClient.getMap(key));
        }
        return rMapList;
    }

    /**
     * 创建 RMapCache<Long, String>
     *
     * @param redissonClient
     * @return
     */
    @Bean
    List<RMapCache<Long, String>> rMapCacheShardList(@Autowired RedissonClient redissonClient) {
        List<RMapCache<Long, String>> shardList = new ArrayList<>();
        for (int i = 0; i < TotalShards; i++) {
            String key = KeyRMapCache + i;
            RMapCache<Long, String> o = redissonClient.getMapCache(key);
            o.setMaxSize(MapController.TotalElementCount.intValue() * 2);
            shardList.add(o);
        }
        return shardList;
    }

    /**
     * 创建 RLocalCachedMap<Long, String>
     *
     * @param redissonClient
     * @return
     */
    @Bean
    List<RLocalCachedMap<Long, String>> rLocalCachedMapShardList(@Autowired RedissonClient redissonClient) {
        List<RLocalCachedMap<Long, String>> shardList = new ArrayList<>();
        for (int i = 0; i < TotalShards; i++) {
            String key = KeyRLocalCachedMap + i;
            shardList.add(redissonClient.getLocalCachedMap(key, LocalCachedMapOptions.defaults()));
        }
        return shardList;
    }

    /**
     * 创建 RSet<Long>
     *
     * @param redissonClient
     * @return
     */
    @Bean
    List<RSet<Long>> rSetShardList(@Autowired RedissonClient redissonClient) {
        List<RSet<Long>> shardList = new ArrayList<>();
        for (int i = 0; i < TotalShards; i++) {
            String key = KeyRSet + i;
            shardList.add(redissonClient.getSet(key));
        }
        return shardList;
    }
}
