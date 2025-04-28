package com.future.demo;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Tests {
    @Test
    public void testHttpUtil() {
        // 测试get
        String content = HttpUtil.get("http://httpbin.org/get", StandardCharsets.UTF_8);
        Assert.assertTrue(content.contains("X-Amzn-Trace-Id"));

        // 测试post
        content = HttpUtil.post("http://httpbin.org/post", "name=张三&age=30");
        Assert.assertTrue(content.contains("form"));

        // 测试post提交JSON数据
        String json = JSONUtil.toJsonStr(new HashMap() {{
            this.put("name", "张三");
            this.put("age", 30);
        }});
        HttpResponse response = HttpRequest.post("http://httpbin.org/post")
                .header("Content-Type", "application/json")
                .body(json)
                .execute();
        content = response.body();
        Assert.assertTrue(content.contains("json"));

        // 自定义请求
        response = HttpRequest.get("http://httpbin.org/get")
                .form(new HashMap() {{
                    put("key", "value");
                }})
                .header("Authorization", "Bearer your_token_here") // 添加请求头
                .timeout(5000) // 设置超时时间
                .execute(); // 发送请求
        content = response.body();
        Assert.assertTrue(content.contains("args"));
    }

    // 在分布式环境中，唯一ID生成应用十分广泛，生成方法也多种多样，Hutool针对一些常用生成策略做了简单封装。
    // https://doc.hutool.cn/pages/IdUtil/
    @Test
    public void testIdUtil() throws InterruptedException {
        // 根据本地网卡 MAC 地址计算数据中心 ID
        // WORKER_ID 为 Snowflake 中的 MAX_WORKER_ID
        Snowflake snowflake = IdUtil.getSnowflake();
        long id = snowflake.nextId();
        long id2 = snowflake.nextId();
        long id3 = IdUtil.getSnowflakeNextId();

        Assert.assertEquals(snowflake.getDataCenterId(id), snowflake.getDataCenterId(id2));
        Assert.assertEquals(snowflake.getWorkerId(id), snowflake.getWorkerId(id2));

        Assert.assertEquals(snowflake.getDataCenterId(id), snowflake.getDataCenterId(id3));
        Assert.assertEquals(snowflake.getWorkerId(id), snowflake.getWorkerId(id3));

        // 注意：下面代码不要删除，用于测试"在并发很低时，生成的分布式ID总是偶数的"
        /*Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            id = IdUtil.getSnowflakeNextId();
            Integer remain = (int) (id % 8);
            if (!map.containsKey(remain)) {
                map.put(remain, 0);
            }
            map.put(remain, map.get(remain) + 1);
            TimeUnit.MILLISECONDS.sleep(10);
        }
        for (Integer key : map.keySet()) {
            System.out.println("key=" + key + ",value=" + map.get(key));
        }*/
    }

    /**
     * 分库分表策略的基因算法
     * https://blog.csdn.net/qq_42875345/article/details/132662916
     */
    @Test
    public void testDbShardingGeneAlgorithm() {
        Long originalOrderId = IdUtil.getSnowflakeNextId();
        String str = Long.toBinaryString(originalOrderId);
        System.out.println(str + "（原始订单ID）");

        // 原始订单ID去除后4位
        Long orderId = originalOrderId >> 4 << 4;
        str = Long.toBinaryString(orderId);
        System.out.println(str + "（去除后4位后）");

        Long originalUserId = RandomUtil.randomLong(0, Long.MAX_VALUE);
        str = Long.toBinaryString(originalUserId);
        System.out.println(str + "（原始用户ID）");

        // 只保留用户ID的后4位作为订单ID的基因值
        Long userId = originalUserId % 16;
        str = Long.toBinaryString(userId);
        System.out.println(str + "（用户ID后4位）");

        // 替换拼接用户ID基因值到订单ID后4位中
        orderId = orderId | userId;
        str = Long.toBinaryString(orderId);
        System.out.println(str + "（订单ID和用户ID后4位替换拼接后）");

        // 通过订单ID计算分片索引
        System.out.println((orderId % 16 % 5 + 1) + "（订单ID计算得到的分片索引）");
        // 通过用户ID计算分片索引
        System.out.println((originalUserId % 16 % 5 + 1) + "（用户ID计算得到的分片索引）");

        // 追加拼接用户ID基因值到订单ID后
        userId = originalUserId % 16;
        str = Long.toBinaryString(originalOrderId) + StringUtils.leftPad(Long.toBinaryString(userId), 4, "0");
        System.out.println(str + "（订单ID和用户ID后4位追加拼接后）");

        BigInteger bigInteger = new BigInteger(str, 2);
        System.out.println(bigInteger.toString(10) + "（订单ID和用户ID后4位拼接后的十进制数）");

        // 通过订单ID计算分片索引
        bigInteger = bigInteger.mod(new BigInteger("16")).mod(new BigInteger("5")).add(new BigInteger("1"));
        System.out.println(bigInteger.intValue() + "（订单ID计算得到的分片索引）");
        // 通过用户ID计算分片索引
        System.out.println((originalUserId % 16 % 5 + 1) + "（用户ID计算得到的分片索引）");
    }
}
