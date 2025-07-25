package com.future.demo;

import cn.hutool.core.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import java.util.UUID;

/**
 * redis pipeline 测试支持服务
 */
@Service
public class PipelineTestingSupportService {

    private final static Integer TotalKeys = 1024;
    private final static Integer RunLoop = 128;

    @Autowired(required = false)
    JedisCluster jedisCluster;

    /**
     * 用于协助测试 pipeline 性能
     */
    public void testPerfAll() {
        PipelineContextHolder.setupPipeline(jedisCluster);
        testPerfSet();
        testPerfHashSet();
        testPerfZSet();
        PipelineContextHolder.closePipeline();
    }

    private void testPerfSet() {
        for (int i = 0; i < RunLoop; i++) {
            int randomIndex = RandomUtil.randomInt(0, TotalKeys);
            String key = "set" + randomIndex;
            String value = UUID.randomUUID().toString();
            PipelineContextHolder.set(key, value);
        }
    }

    private void testPerfHashSet() {
        for (int i = 0; i < RunLoop; i++) {
            int randomIndex = RandomUtil.randomInt(0, TotalKeys);
            String key = "hset" + randomIndex;
            String field = "field" + randomIndex;
            String value = UUID.randomUUID().toString();
            PipelineContextHolder.hset(key, field, value);
        }
    }

    private void testPerfZSet() {
        for (int i = 0; i < RunLoop; i++) {
            int randomIndex = RandomUtil.randomInt(0, TotalKeys);
            String key = "zset" + randomIndex;
            String member = "member" + randomIndex;
            double score = RandomUtil.randomDouble(0, Double.MAX_VALUE);
            PipelineContextHolder.zadd(key, score, member);
        }
    }

    /**
     * 用于协助测试 PipelineContextHolder 上下文工具并发线程间相互独立
     */
    public void testPipelineContextHolderThreadConcurrentIndependenceAll(int index, String value) {
        PipelineContextHolder.setupPipeline(jedisCluster);
        testPipelineContextHolderThreadConcurrentIndependenceSet(index, value);
        testPipelineContextHolderThreadConcurrentIndependenceHashSet(index, value);
        PipelineContextHolder.closePipeline();
    }

    private void testPipelineContextHolderThreadConcurrentIndependenceSet(int index, String value) {
        String key = "set" + index;
        PipelineContextHolder.set(key, value);
    }

    private void testPipelineContextHolderThreadConcurrentIndependenceHashSet(int index, String value) {
        String key = "hset" + index;
        String field = "field" + index;
        PipelineContextHolder.hset(key, field, value);
    }
}
