package com.future.demo.test;

import feign.*;
import feign.form.FormEncoder;
import feign.jackson.JacksonEncoder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class ApiTests {

    Api api;

    @Before
    public void before() {
        String host = "localhost";
        int port = 8081;

        api = Feign.builder()
                .retryer(Retryer.NEVER_RETRY)
                .options(new Request.Options(15, TimeUnit.SECONDS, 15, TimeUnit.SECONDS, false))
                .encoder(new FormEncoder(new JacksonEncoder()))
                .logger(new Logger.ErrorLogger()).logLevel(Logger.Level.FULL)
                .target(Api.class, "http://" + host + ":" + port);
    }

    @Test
    public void test() throws Exception {
        // 演示路由基本配置
        String param1 = "Dexterleslie";
        String str = api.test1(param1);
        Assert.assertEquals("成功调用/api/v1/test1接口，[param1=" + param1 + "]", str);

        try {
            api.test2(param1);
            Assert.fail("预期异常没有抛出");
        } catch (FeignException ex) {
            Assert.assertTrue(ex.getMessage().contains("Internal Server Error"));
        }
    }

}
