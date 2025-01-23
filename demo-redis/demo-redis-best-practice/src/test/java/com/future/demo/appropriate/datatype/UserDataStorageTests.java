package com.future.demo.appropriate.datatype;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.demo.Application;
import com.future.demo.bean.UserBean;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;
import java.util.Objects;

@SpringBootTest(classes = {Application.class})
public class UserDataStorageTests {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    ObjectMapper objectMapper;

    // 使用 string 类型存储 10w 个用户
    @Test
    public void testStoringByUsingStringDataType() {
        Objects.requireNonNull(this.redisTemplate.getConnectionFactory()).getConnection().flushDb();

        for (int i = 0; i < 100000; i++) {
            int finalI = i;
            this.redisTemplate.opsForValue().multiSet(new HashMap<String, String>() {{
                this.put("user:id:" + finalI, String.valueOf(finalI));
                this.put("user:name:" + finalI, "name" + finalI);
                this.put("user:age:" + finalI, String.valueOf(finalI + 1));
            }});
        }
    }

    // 使用 string类型+JSON 存储 10w 个用户
    @Test
    public void testStoringByUsingStringDataTypeAndJSONString() throws JsonProcessingException {
        Objects.requireNonNull(this.redisTemplate.getConnectionFactory()).getConnection().flushDb();

        for (int i = 0; i < 100000; i++) {
            UserBean userBean = new UserBean((long) i, "name" + i, i);
            String JSON = this.objectMapper.writeValueAsString(userBean);
            this.redisTemplate.opsForValue().set("user:" + i, JSON);
        }
    }

    // 使用 hash 类型存储 10w 个用户
    @Test
    public void testStoringByUsingHashDataType() {
        Objects.requireNonNull(this.redisTemplate.getConnectionFactory()).getConnection().flushDb();

        for (int i = 0; i < 100000; i++) {
            int finalI = i;
            this.redisTemplate.opsForHash().putAll("user:" + i, new HashMap<String, String>() {{
                this.put("id", String.valueOf(finalI));
                this.put("name", "name" + finalI);
                this.put("age", String.valueOf(finalI + 1));
            }});
        }
    }
}
