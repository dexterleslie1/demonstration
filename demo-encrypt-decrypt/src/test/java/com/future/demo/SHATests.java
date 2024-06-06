package com.future.demo;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHATests {
    @Test
    public void testSHA256() throws NoSuchAlgorithmException {
        String originText = RandomStringUtils.random(10 * 1024);

        // 获取SHA-256 MessageDigest实例
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // 更新原始数据
        md.update(originText.getBytes(StandardCharsets.UTF_8));

        // 完成哈希计算
        byte[] digest = md.digest();

        // 将结果转换为十六进制字符串
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }

        // SHA-256固定输出256位
        Assert.assertEquals(64, sb.length());
    }
}
