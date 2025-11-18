package com.future.demo;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.CoreMatchers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class ApplicationTests {

    @Resource
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void testHttpUtil() {
        // 测试get
        String content = HttpUtil.get("http://httpbin.org/get", StandardCharsets.UTF_8);
        Assertions.assertTrue(content.contains("X-Amzn-Trace-Id"));

        // 测试post
        content = HttpUtil.post("http://httpbin.org/post", "name=张三&age=30");
        Assertions.assertTrue(content.contains("form"));

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
        Assertions.assertTrue(content.contains("json"));

        // 自定义请求
        response = HttpRequest.get("http://httpbin.org/get")
                .form(new HashMap() {{
                    put("key", "value");
                }})
                .header("Authorization", "Bearer your_token_here") // 添加请求头
                .timeout(5000) // 设置超时时间
                .execute(); // 发送请求
        content = response.body();
        Assertions.assertTrue(content.contains("args"));
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

        Assertions.assertEquals(snowflake.getDataCenterId(id), snowflake.getDataCenterId(id2));
        Assertions.assertEquals(snowflake.getWorkerId(id), snowflake.getWorkerId(id2));

        Assertions.assertEquals(snowflake.getDataCenterId(id), snowflake.getDataCenterId(id3));
        Assertions.assertEquals(snowflake.getWorkerId(id), snowflake.getWorkerId(id3));

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

    /**
     * 测试ServletUtil
     *
     * @throws Exception
     */
    @Test
    public void testServletUtil() throws Exception {
        // 测试ServletUtil写响应
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/test1"));
        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode", CoreMatchers.is(90001)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage", CoreMatchers.is("测试失败")));

        // 测试ServletUtil获取客户端ip地址
        resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/testServletUtilGetClientIp"));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", CoreMatchers.is("客户端IP：127.0.0.1")));
    }

    /**
     * 测试JSONUtil
     */
    @Test
    public void testJSONUtil() {
        // 把对象转换成JSON字符串
        ObjectResponse<String> objectResponse = ResponseUtils.successObject("测试成功");
        String json = JSONUtil.toJsonStr(objectResponse);
        Assertions.assertEquals("{\"data\":\"测试成功\",\"errorCode\":0}", json);

        // 不忽略null值
        objectResponse = ResponseUtils.successObject("测试成功");
        json = JSONUtil.toJsonStr(objectResponse, JSONConfig.create().setIgnoreNullValue(false));
        Assertions.assertEquals("{\"data\":\"测试成功\",\"errorCode\":0,\"errorMessage\":null}", json);

        // JSONUtil.toJsonStr不能和jackson ObjectNode混合使用，否则json字符串转换后不正常
        /*ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("name", "张三");
        objectNode.put("age", 18);*/
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "张三");
        jsonObject.put("age", 18);
        ObjectResponse<JSONObject> objectNodeResponse = ResponseUtils.successObject(jsonObject);
        json = JSONUtil.toJsonStr(objectNodeResponse, JSONConfig.create().setIgnoreNullValue(false));
        Assertions.assertEquals("{\"data\":{\"name\":\"张三\",\"age\":18},\"errorCode\":0,\"errorMessage\":null}", json);

        // region 将 JSON 字符串转换为 Java 对象

        String name = "张三";
        int age = 25;
        String jsonStr = "{\"name\":\"" + name + "\",\"age\":" + age + "}";

        // 转换为 Map
        Map map = JSONUtil.toBean(jsonStr, Map.class);
        Assertions.assertEquals(name, map.get("name"));
        Assertions.assertEquals(age, map.get("age"));

        // 转换为自定义 JavaBean
        User user = JSONUtil.toBean(jsonStr, User.class);
        Assertions.assertEquals(name, user.getName());
        Assertions.assertEquals(age, user.getAge());

        // endregion
    }
}
