package com.future.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.future.demo.config.ConfigKafkaListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@SpringBootTest(classes = Application.class)
@Slf4j
public class ApplicationTests {

    @Resource
    ConfigKafkaListener configKafkaListener;
    @Resource
    CommonMapper commonMapper;

    @Test
    public void contextLoads() throws InterruptedException {
        // region 测试插入

        AtomicReference<List<JsonNode>> reference = new AtomicReference<>();
        configKafkaListener.registerCallback(new Consumer<List<JsonNode>>() {
            @Override
            public void accept(List<JsonNode> jsonNodeList) {
                synchronized (reference) {
                    if (reference.get() == null) {
                        reference.set(new ArrayList<>());
                    }
                    reference.get().addAll(jsonNodeList);
                }
            }
        });

        String username = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        int affectRows = commonMapper.insertUser(username, password);
        Assertions.assertEquals(1, affectRows);
        TimeUnit.MILLISECONDS.sleep(3000);
        Assertions.assertNotNull(reference.get());
        Assertions.assertEquals(1, reference.get().size());
        JsonNode jsonNode = reference.get().get(0);
        Assertions.assertEquals("demo", jsonNode.get("payload").get("source").get("db").asText());
        Assertions.assertEquals("t_user", jsonNode.get("payload").get("source").get("table").asText());
        Assertions.assertEquals("c", jsonNode.get("payload").get("op").asText());
        Assertions.assertEquals(username, jsonNode.get("payload").get("after").get("username").asText());
        Assertions.assertEquals(password, jsonNode.get("payload").get("after").get("password").asText());

        // endregion

        // region 测试修改

        reference.set(null);
        String usernameNew = UUID.randomUUID().toString();
        affectRows = commonMapper.updateUser(usernameNew, username);
        Assertions.assertEquals(1, affectRows);
        TimeUnit.MILLISECONDS.sleep(1000);
        Assertions.assertNotNull(reference.get());
        Assertions.assertEquals(1, reference.get().size());
        jsonNode = reference.get().get(0);
        Assertions.assertEquals("demo", jsonNode.get("payload").get("source").get("db").asText());
        Assertions.assertEquals("t_user", jsonNode.get("payload").get("source").get("table").asText());
        Assertions.assertEquals("u", jsonNode.get("payload").get("op").asText());
        Assertions.assertEquals(usernameNew, jsonNode.get("payload").get("after").get("username").asText());
        Assertions.assertEquals(password, jsonNode.get("payload").get("after").get("password").asText());

        // endregion

        // region 测试删除

        reference.set(null);
        affectRows = commonMapper.deleteUser(usernameNew);
        Assertions.assertEquals(1, affectRows);
        TimeUnit.MILLISECONDS.sleep(1000);
        Assertions.assertNotNull(reference.get());
        Assertions.assertEquals(1, reference.get().size());
        jsonNode = reference.get().get(0);
        Assertions.assertEquals("demo", jsonNode.get("payload").get("source").get("db").asText());
        Assertions.assertEquals("t_user", jsonNode.get("payload").get("source").get("table").asText());
        Assertions.assertEquals("d", jsonNode.get("payload").get("op").asText());
        Assertions.assertEquals(usernameNew, jsonNode.get("payload").get("before").get("username").asText());
        Assertions.assertEquals(password, jsonNode.get("payload").get("before").get("password").asText());
        Assertions.assertEquals("null", jsonNode.get("payload").get("after").asText());

        // endregion

        // region 测试批量插入

        reference.set(null);
        List<String> usernameList = Arrays.asList(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );
        List<Map<String, String>> userList = usernameList.stream()
                .map(o -> Map.of("username", o, "password", password))
                .toList();
        affectRows = commonMapper.insertUserBatch(userList);
        Assertions.assertEquals(2, affectRows);
        TimeUnit.MILLISECONDS.sleep(1000);
        Assertions.assertNotNull(reference.get());
        Assertions.assertEquals(2, reference.get().size());
        List<Long> idList = new ArrayList<>();
        for (int i = 0; i < reference.get().size(); i++) {
            jsonNode = reference.get().get(i);
            Assertions.assertEquals("demo", jsonNode.get("payload").get("source").get("db").asText());
            Assertions.assertEquals("t_user", jsonNode.get("payload").get("source").get("table").asText());
            Assertions.assertEquals("c", jsonNode.get("payload").get("op").asText());
            Assertions.assertEquals(usernameList.get(i), jsonNode.get("payload").get("after").get("username").asText());
            Assertions.assertEquals(password, jsonNode.get("payload").get("after").get("password").asText());
            Long id = jsonNode.get("payload").get("after").get("id").asLong();
            idList.add(id);
        }

        // endregion

        // region 测试批量修改

        reference.set(null);
        usernameNew = UUID.randomUUID().toString();
        affectRows = commonMapper.updateUserBatch(idList, usernameNew);
        Assertions.assertEquals(2, affectRows);
        TimeUnit.MILLISECONDS.sleep(1000);
        Assertions.assertNotNull(reference.get());
        Assertions.assertEquals(2, reference.get().size());
        for (int i = 0; i < reference.get().size(); i++) {
            jsonNode = reference.get().get(i);
            Assertions.assertEquals("demo", jsonNode.get("payload").get("source").get("db").asText());
            Assertions.assertEquals("t_user", jsonNode.get("payload").get("source").get("table").asText());
            Assertions.assertEquals("u", jsonNode.get("payload").get("op").asText());
            Assertions.assertEquals(usernameNew, jsonNode.get("payload").get("after").get("username").asText());
            Assertions.assertEquals(password, jsonNode.get("payload").get("after").get("password").asText());
        }

        // endregion

        // region 测试批量删除

        reference.set(null);
        affectRows = commonMapper.deleteUserBatch(idList);
        Assertions.assertEquals(2, affectRows);
        TimeUnit.MILLISECONDS.sleep(1000);
        Assertions.assertNotNull(reference.get());
        Assertions.assertEquals(2, reference.get().size());
        for (int i = 0; i < reference.get().size(); i++) {
            jsonNode = reference.get().get(i);
            Assertions.assertEquals("demo", jsonNode.get("payload").get("source").get("db").asText());
            Assertions.assertEquals("t_user", jsonNode.get("payload").get("source").get("table").asText());
            Assertions.assertEquals("d", jsonNode.get("payload").get("op").asText());
            Assertions.assertEquals(usernameNew, jsonNode.get("payload").get("before").get("username").asText());
            Assertions.assertEquals(password, jsonNode.get("payload").get("before").get("password").asText());
            Assertions.assertEquals("null", jsonNode.get("payload").get("after").asText());
        }

        // endregion

        // region 测试 Kafka spring.kafka.consumer.max-poll-records=2 控制单批最大记录数量

        AtomicReference<HashMap<Integer, List<JsonNode>>> reference2 = new AtomicReference<>();
        configKafkaListener.registerCallback(new Consumer<List<JsonNode>>() {
            @Override
            public void accept(List<JsonNode> jsonNodeList) {
                synchronized (reference2) {
                    if (reference2.get() == null) {
                        reference2.set(new HashMap<>());
                    }

                    Map<Integer, List<JsonNode>> map = reference2.get();
                    int size = map.size();
                    map.put(size, new ArrayList<>(jsonNodeList));
                }
            }
        });

        usernameList = Arrays.asList(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );
        userList = usernameList.stream().map(o -> Map.of("username", o, "password", password)).toList();
        affectRows = commonMapper.insertUserBatch(userList);
        Assertions.assertEquals(5, affectRows);
        TimeUnit.MILLISECONDS.sleep(1000);
        Assertions.assertNotNull(reference2.get());
        // 5 条记录有 3 个批次
        Assertions.assertEquals(3, reference2.get().size());
        Assertions.assertTrue(reference2.get().containsKey(0));
        Assertions.assertTrue(reference2.get().containsKey(1));
        Assertions.assertTrue(reference2.get().containsKey(2));
        // 第一个批次 2 条记录
        Assertions.assertEquals(2, reference2.get().get(0).size());
        // 第二个批次 2 条记录
        Assertions.assertEquals(2, reference2.get().get(1).size());
        // 第三个批次 1 条记录
        Assertions.assertEquals(1, reference2.get().get(2).size());

        // endregion
    }

    /**
     * 测试初始化数据库数据的读取事件
     */
    @Test
    public void testInitializingReadEvent() throws InterruptedException {
        AtomicReference<List<JsonNode>> reference = new AtomicReference<>();
        configKafkaListener.registerCallback(new Consumer<List<JsonNode>>() {
            @Override
            public void accept(List<JsonNode> jsonNodeList) {
                synchronized (reference) {
                    if (reference.get() == null) {
                        reference.set(new ArrayList<>());
                    }
                    reference.get().addAll(jsonNodeList);
                }
            }
        });
        TimeUnit.SECONDS.sleep(5);
        Assertions.assertEquals(1, reference.get().size());
        Assertions.assertEquals("r", reference.get().get(0).get("payload").get("op").asText());
        Assertions.assertEquals("userx", reference.get().get(0).get("payload").get("after").get("username").asText());
        Assertions.assertEquals("123456", reference.get().get(0).get("payload").get("after").get("password").asText());
    }
}
