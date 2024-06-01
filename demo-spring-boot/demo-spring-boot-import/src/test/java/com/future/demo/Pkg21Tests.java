package com.future.demo;

import com.future.demo.app.Application;
import com.future.demo.pkg2.CacheService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, ConfigCacheLocal.class})
public class Pkg21Tests {

    @Resource
    CacheService cacheService;

    @Test
    public void test() {
        Assert.assertEquals("本地缓存", this.cacheService.getType());
    }
}
