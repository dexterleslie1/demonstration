package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ConfigWebSecurity.class})
@Slf4j
public class BCryptPasswordEncoderTests {
    @Autowired
    @Qualifier(value = "bCryptPasswordEncoder")
    private PasswordEncoder passwordEncoder = null;

    @Test
    public void test() {
        String rawValue = "123456";
        // 使用BCryptPasswordEncoder编码密码两次编码同一个密码不会重复
        String encodePassword = this.passwordEncoder.encode(rawValue);
        String encodePassword1 = this.passwordEncoder.encode(rawValue);
        Assert.assertNotEquals(encodePassword, encodePassword1);
        // 调用BCryptPasswordEncoder#matches函数比较密码是否正确
        boolean match = this.passwordEncoder.matches(rawValue, encodePassword);
        Assert.assertTrue(match);
        match = this.passwordEncoder.matches(rawValue, encodePassword1);
        Assert.assertTrue(match);
    }

    @Test
    public void test1() {
        for (int i = 0; i < 100; i++) {
            String uuid = UUID.randomUUID().toString();
            match(uuid);
        }
    }

    void match(String rawValue) {
        String encodePassword = this.passwordEncoder.encode(rawValue);
        String encodePassword1 = this.passwordEncoder.encode(rawValue);
        Assert.assertNotEquals(encodePassword, encodePassword1);
        boolean match = this.passwordEncoder.matches(rawValue, encodePassword);
        Assert.assertTrue(match);
        match = this.passwordEncoder.matches(rawValue, encodePassword1);
        Assert.assertTrue(match);
    }

    @Test
    public void testPerformance() {
        int count = 100;
        Map<String, String> rawToEncodeMap = new HashMap<>();

        Date timeStart = new Date();
        for (int i = 0; i < count; i++) {
            String uuid = UUID.randomUUID().toString();
            String encodePassword = this.passwordEncoder.encode(uuid);
            rawToEncodeMap.put(uuid, encodePassword);
        }
        Date timeEnd = new Date();
        log.info("编码 {} 个密码耗时 {} 毫秒", count, (timeEnd.getTime() - timeStart.getTime()));

        timeStart = new Date();
        for (String key : rawToEncodeMap.keySet()) {
            String value = rawToEncodeMap.get(key);
            boolean match = this.passwordEncoder.matches(key, value);
            Assert.assertTrue(match);
        }
        timeEnd = new Date();
        log.info("对比 {} 个密码耗时 {} 毫秒", count, (timeEnd.getTime() - timeStart.getTime()));
    }
}
