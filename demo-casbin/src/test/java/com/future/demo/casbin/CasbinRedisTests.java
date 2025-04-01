package com.future.demo.casbin;

import org.casbin.adapter.RedisAdapter;
import org.casbin.jcasbin.main.Enforcer;
import org.casbin.jcasbin.model.Model;
import org.junit.Ignore;
import org.junit.Test;

public class CasbinRedisTests {
    // https://github.com/jcasbin/redis-adapter
    @Ignore
    @Test
    public void test() {
        String redisHost = "";
        int redisPort = 6379;
        RedisAdapter adapter = new RedisAdapter(redisHost, redisPort);

        Model model = new Model();
        model.loadModelFromText("");
        Enforcer enforcer = new Enforcer("", adapter);
        enforcer.loadPolicy();
    }
}
