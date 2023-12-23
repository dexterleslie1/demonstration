package com.future.demo.test;

import com.future.demo.Application;
import lombok.extern.slf4j.Slf4j;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class Tests {

    @Resource
    MemcachedClient memcachedClient;

    @Test
    public void test() throws ExecutionException, InterruptedException {
        String username = "dexter";
        // 放入缓存, 如下参数key为name，值为dexter，过期时间为5000，单位为毫秒
        OperationFuture<Boolean> flag = memcachedClient.set("name", 1000, username);
        Assert.assertTrue("memcached get expected true, got false", flag.get());
        // 取出缓存
        Object value = memcachedClient.get("name");
        Assert.assertEquals(username, value);
    }
}
