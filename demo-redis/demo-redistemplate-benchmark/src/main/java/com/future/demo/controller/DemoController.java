package com.future.demo.controller;

import cn.hutool.core.util.RandomUtil;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class DemoController {

    public final static String Prefix = "testMigration:";

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 协助测试迁移时生成测试数据以检查是否丢失数据
     *
     * @return
     * @throws InterruptedException
     */
    @GetMapping("testAssistMigrationGenerateDatum")
    public ObjectResponse<String> testAssistMigrationGenerateDatum() throws InterruptedException {
        String uuidStr = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(Prefix + uuidStr, uuidStr);
        int randomMilliseconds = RandomUtil.randomInt(1, 1000);
        TimeUnit.MILLISECONDS.sleep(randomMilliseconds);
        return ResponseUtils.successObject("成功调用");
    }

    /**
     * 获取测试迁移数据总数以检查是否丢失数据
     *
     * @return
     */
    @GetMapping("testAssistMigrationGetTotalCount")
    public ObjectResponse<Integer> testAssistMigrationGetTotalCount() {
        Set<String> keys = redisTemplate.keys(Prefix + "*");
        return ResponseUtils.successObject(keys == null ? 0 : keys.size());
    }
}
