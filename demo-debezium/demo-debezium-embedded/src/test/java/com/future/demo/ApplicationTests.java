package com.future.demo;

import com.future.demo.mapper.CommomMapper;
import io.debezium.data.Envelope;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@SpringBootTest
@Slf4j
public class ApplicationTests {

    @Resource
    CommomMapper commomMapper;
    @Resource
    ConfigDebezium configDebezium;

    @Test
    public void contextLoads() throws InterruptedException {
        // region 测试读取初始化数据库时的数据，READ 操作类型

        AtomicReference<List<ConfigDebezium.RecordChangeEventWrapper>> reference = new AtomicReference<>();
        configDebezium.registerCallback(wrapperList -> {
            synchronized (reference) {
                if (reference.get() == null) {
                    reference.set(new ArrayList<>());
                }
                reference.get().addAll(wrapperList);
            }
        });

        // 删除其他干扰数据
        commomMapper.deleteUserIdNotIn(Arrays.asList(1L, 2L));

        TimeUnit.MILLISECONDS.sleep(500);
        Assertions.assertTrue(reference.get().size() >= 2);
        List<ConfigDebezium.RecordChangeEventWrapper> wrapperList = reference.get().stream().filter(o -> {
            Long id = (Long) o.getPayload().get("id");
            return id.equals(1L) || id.equals(2L);
        }).toList();
        Assertions.assertEquals(2, wrapperList.size());
        for (ConfigDebezium.RecordChangeEventWrapper wrapper : wrapperList) {
            // 取初始化数据库时的数据操作类型是 READ
            Assertions.assertEquals(Envelope.Operation.READ, wrapper.getOperation());
        }

        // endregion

        // region 测试新增

        reference.set(null);
        String username = "user2";
        String password = "123456";
        int affectRows = commomMapper.insertUser(username, password);
        Assertions.assertEquals(1, affectRows);
        TimeUnit.MILLISECONDS.sleep(500);
        Assertions.assertEquals(1, reference.get().size());
        Assertions.assertEquals("t_user", reference.get().get(0).getTableName());
        Assertions.assertEquals(username, reference.get().get(0).getPayload().get("username"));
        Assertions.assertEquals(password, reference.get().get(0).getPayload().get("password"));
        Assertions.assertEquals(Envelope.Operation.CREATE, reference.get().get(0).getOperation());

        // endregion

        // region 测试修改

        reference.set(null);
        String usernameNew = "userx";
        affectRows = commomMapper.updateUser(username, usernameNew);
        Assertions.assertEquals(1, affectRows);
        TimeUnit.MILLISECONDS.sleep(500);
        Assertions.assertEquals(1, reference.get().size());
        Assertions.assertEquals("t_user", reference.get().get(0).getTableName());
        Assertions.assertEquals(usernameNew, reference.get().get(0).getPayload().get("username"));
        Assertions.assertEquals(password, reference.get().get(0).getPayload().get("password"));
        Assertions.assertEquals(Envelope.Operation.UPDATE, reference.get().get(0).getOperation());

        // endregion

        // region 测试删除

        reference.set(null);
        affectRows = commomMapper.deleteUser(usernameNew);
        Assertions.assertEquals(1, affectRows);
        TimeUnit.MILLISECONDS.sleep(500);
        Assertions.assertEquals(1, reference.get().size());
        Assertions.assertEquals("t_user", reference.get().get(0).getTableName());
        Assertions.assertEquals(usernameNew, reference.get().get(0).getPayload().get("username"));
        Assertions.assertEquals(password, reference.get().get(0).getPayload().get("password"));
        Assertions.assertEquals(Envelope.Operation.DELETE, reference.get().get(0).getOperation());

        // endregion

        // region 测试 max.batch.size=10

        AtomicReference<HashMap<Integer, List<ConfigDebezium.RecordChangeEventWrapper>>> reference2 = new AtomicReference<>();
        configDebezium.registerCallback(new Consumer<List<ConfigDebezium.RecordChangeEventWrapper>>() {
            @Override
            public void accept(List<ConfigDebezium.RecordChangeEventWrapper> wrapperList) {
                synchronized (reference2) {
                    if (reference2.get() == null) {
                        reference2.set(new HashMap<>());
                    }

                    int size = reference2.get().size();
                    reference2.get().put(size, wrapperList);
                }
            }
        });
        List<String> usernameList = Arrays.asList(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );
        List<Map<String, String>> userList = usernameList.stream()
                .map(o -> Map.of("username", o, "password", password))
                .toList();
        affectRows = commomMapper.insertUserBatch(userList);
        Assertions.assertEquals(usernameList.size(), affectRows);
        TimeUnit.MILLISECONDS.sleep(2000);
        Assertions.assertNotNull(reference2.get());
        // 共 2 个批次
        Assertions.assertEquals(2, reference2.get().size());
        Assertions.assertTrue(reference2.get().containsKey(0));
        Assertions.assertTrue(reference2.get().containsKey(1));
        // 第一个批次为 10 条记录
        Assertions.assertEquals(10, reference2.get().get(0).size());
        // 第二个批次为 3 条记录
        Assertions.assertEquals(3, reference2.get().get(1).size());

        // endregion
    }
}
