package com.future.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Jackson库测试
 *
 * @author dexterleslie@gmail.com
 */
public class JacksonTests {
    private final static Logger log = LoggerFactory.getLogger(JacksonTests.class);

    /**
     * 测试 bean 转换为 json 字符串
     */
    @Test
    public void testBean2Json() throws IOException {
        long userId = 12345l;
        String loginname = "dexter";
        boolean enable = true;
        LocalDateTime createTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        BeanClass beanClass = new BeanClass();
        beanClass.setUserId(userId);
        beanClass.setLoginname(loginname);
        beanClass.setEnable(enable);
        beanClass.setCreateTime(createTime);

        ObjectMapper OMInstance = new ObjectMapper();
        String json = OMInstance.writeValueAsString(beanClass);
        beanClass = OMInstance.readValue(json, BeanClass.class);
        Assert.assertEquals(userId, beanClass.getUserId());
        Assert.assertEquals(loginname, beanClass.getLoginname());
        Assert.assertEquals(false, beanClass.isEnable());
        Assert.assertEquals(createTime, beanClass.getCreateTime());
    }

    /**
     * 测试 json 字符串转换为 bean
     */
    @Test
    public void testJson2Bean() throws IOException {
        String json = "{\"userId\":12345,\"loginname\":\"dexter\",\"createTime\":\"2025-02-11 16:46:04\",\"enable\":true}";

        long userId = 12345l;
        String loginname = "dexter";
        LocalDateTime createTime = LocalDateTime.parse("2025-02-11 16:46:04", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        ObjectMapper OMInstance = new ObjectMapper();
        BeanClass beanClass = OMInstance.readValue(json, BeanClass.class);
        Assert.assertEquals(userId, beanClass.getUserId());
        Assert.assertEquals(loginname, beanClass.getLoginname());
        Assert.assertEquals(false, beanClass.isEnable());
        Assert.assertEquals(createTime, beanClass.getCreateTime());
    }

    /**
     * 测试 json 字符串转换为 JsonNode
     *
     * @throws IOException
     */
    @Test
    public void testJson2JsonNode() throws IOException {
        String json = "{\"userId\":12345,\"loginname\":\"dexter\",\"createTime\":1577874822420,\"enable\":true}";
        ObjectMapper OMInstance = new ObjectMapper();
        JsonNode jsonNode = OMInstance.readTree(json);
        Assert.assertTrue(jsonNode.get("enable").asBoolean());
    }

    /**
     * 测试 ObjectNode 用于面向对象创建 json 结构对象
     *
     * @throws IOException
     */
    @Test
    public void testObjectNode() throws IOException {
        ObjectMapper OMInstance = new ObjectMapper();
        ObjectNode objectNode = OMInstance.createObjectNode();
        objectNode.put("enable", true);
        objectNode.put("loginname", "dexter");

        String json = OMInstance.writeValueAsString(objectNode);
        JsonNode jsonNode = OMInstance.readTree(json);
        Assert.assertTrue(jsonNode.get("enable").asBoolean());
        Assert.assertEquals("dexter", jsonNode.get("loginname").asText());

        objectNode = JsonNodeFactory.instance.objectNode();
        objectNode.put("enable", true);
        objectNode.put("loginname", "dexter");
        Assert.assertTrue(jsonNode.get("enable").asBoolean());
        Assert.assertEquals("dexter", jsonNode.get("loginname").asText());
    }

    /**
     * @throws IOException
     */
    @Test
    public void jsonFormat() throws IOException {
        long userId = 12345l;
        String loginname = "dexter";
        boolean enable = true;
        LocalDateTime createTime = LocalDateTime.now();
        BeanClass beanClass = new BeanClass();
        beanClass.setUserId(userId);
        beanClass.setLoginname(loginname);
        beanClass.setEnable(enable);
        beanClass.setCreateTime(createTime);

        String createTimeStringExpected = createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        ObjectMapper OMInstance = new ObjectMapper();
        String json = OMInstance.writeValueAsString(beanClass);
        JsonNode jsonNode = OMInstance.readTree(json);
        String createTimeStringActual = jsonNode.get("createTime").asText();
        Assert.assertEquals(createTimeStringExpected, createTimeStringActual);
    }

    /**
     * @throws JsonProcessingException
     */
    @Test
    public void prettyPrint() throws JsonProcessingException {
        long userId = 12345l;
        String loginname = "dexter";
        boolean enable = true;
        LocalDateTime createTime = LocalDateTime.now();
        BeanClass beanClass = new BeanClass();
        beanClass.setUserId(userId);
        beanClass.setLoginname(loginname);
        beanClass.setEnable(enable);
        beanClass.setCreateTime(createTime);

        ObjectMapper OMInstance = new ObjectMapper();
        String json = OMInstance.writerWithDefaultPrettyPrinter().writeValueAsString(beanClass);
        log.info(json);
    }

    /**
     * 转换JsonNode为ObjectNode
     *
     * @throws IOException
     */
    @Test
    public void jsonNodeToObjectNode() throws IOException {
        String json = "{\"userId\":12345,\"loginname\":\"dexter\",\"createTime\":1577874822420,\"enable\":true}";
        ObjectMapper OMInstance = new ObjectMapper();
        JsonNode jsonNode = OMInstance.readTree(json);
        ObjectNode objectNode = (ObjectNode) jsonNode;
        log.info(jsonNode.toString());
        log.info(objectNode.toString());
    }

    /**
     * 测试 json array 字符串转换为 java List
     */
    @Test
    public void testJsonArrayToJavaList() throws IOException {
        long userId = 12345l;
        String loginname = "dexter";
        boolean enable = true;
        LocalDateTime createTime = LocalDateTime.now();

        List<BeanClass> beanClasseList = new ArrayList<BeanClass>();
        for (int i = 1; i <= 5; i++) {
            BeanClass beanClass = new BeanClass();
            beanClass.setUserId(userId);
            beanClass.setLoginname(loginname + "#" + i);
            beanClass.setEnable(enable);
            beanClass.setCreateTime(createTime);
        }

        ObjectMapper OMInstance = new ObjectMapper();
        String json = OMInstance.writeValueAsString(beanClasseList);

        List<BeanClass> beanClassListR = OMInstance.readValue(json, new TypeReference<List<BeanClass>>() {
        });

        Assert.assertEquals(beanClasseList.size(), beanClassListR.size());
        for (int i = 0; i < beanClasseList.size(); i++) {
            Assert.assertEquals(beanClasseList.get(i).getUserId(), beanClassListR.get(i).getUserId());
            Assert.assertEquals(beanClasseList.get(i).getLoginname(), beanClassListR.get(i).getLoginname());
            Assert.assertEquals(beanClasseList.get(i).isEnable(), beanClassListR.get(i).isEnable());
            Assert.assertEquals(beanClasseList.get(i).getCreateTime(), beanClassListR.get(i).getCreateTime());
        }
    }

    /**
     * 测试 ArrayNode 用法和转换为 Stream
     *
     * @throws Exception
     */
    @Test
    public void testArrayNodeToStream() throws Exception {
        Map<String, List<String>> testMapper = new HashMap<>();
        testMapper.put("data", Arrays.asList("1", "2", "3"));
        ObjectMapper objectMapper = new ObjectMapper();
        String JSON = objectMapper.writeValueAsString(testMapper);
        JsonNode jsonNode = objectMapper.readTree(JSON);
        ArrayNode arrayNode = (ArrayNode) jsonNode.get("data");

        // https://stackoverflow.com/questions/32683785/create-java-8-stream-from-arraynode
        List<String> list = StreamSupport.stream(arrayNode.spliterator(), false).map(JsonNode::asText).collect(Collectors.toList());
        Assert.assertArrayEquals(new String[]{"1", "2", "3"}, list.toArray(new String[]{}));
    }

    /**
     * 测试后端如何返回带有枚举类型数据给前端
     *
     * @throws JsonProcessingException
     */
    @Test
    public void testEnum() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        BeanClass beanClass = new BeanClass();
        beanClass.setStatus(Status.Paying.toDto());
        String JSON = objectMapper.writeValueAsString(beanClass);
        Assert.assertEquals("{\"userId\":0,\"loginname\":null,\"createTime\":null,\"status\":{\"name\":\"Paying\",\"description\":\"支付中\"}}", JSON);

        BeanClass2 beanClass2 = new BeanClass2();
        beanClass2.setStatus(Status.Paying);
        JSON = objectMapper.writeValueAsString(beanClass2);
        Assert.assertEquals("{\"userId\":0,\"loginname\":null,\"createTime\":null,\"status\":{\"name\":\"Paying\",\"description\":\"支付中\"}}", JSON);
    }

    /**
     * 自定义序列化逻辑
     *
     * @throws JsonProcessingException
     */
    @Test
    public void testCustomizeJsonSerializer() throws JsonProcessingException {
        Exception exception = new OAuth2ExceptionWithCustomizeJson("获取访问令牌失败");
        String json = new ObjectMapper().writeValueAsString(exception);
        Assert.assertEquals("{\"errorCode\":600,\"errorMessage\":\"获取访问令牌失败\"}", json);
    }
}
