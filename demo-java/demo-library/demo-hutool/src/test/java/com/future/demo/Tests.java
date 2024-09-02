package com.future.demo;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Tests {
    @Test
    public void testHttpUtil() {
        // 测试get
        String content = HttpUtil.get("http://httpbin.org/get", StandardCharsets.UTF_8);
        Assert.assertTrue(content.contains("X-Amzn-Trace-Id"));

        // 测试post
        content = HttpUtil.post("http://httpbin.org/post", "name=张三&age=30");
        Assert.assertTrue(content.contains("form"));

        // 测试post提交JSON数据
        // Map.of("name", "张三", "age", 30)
        String json = JSONUtil.toJsonStr(new HashMap() {{
            this.put("name", "张三");
            this.put("age", 30);
        }});
        HttpResponse response = HttpRequest.post("http://httpbin.org/post")
                .header("Content-Type", "application/json")
                .body(json)
                .execute();
        content = response.body();
        Assert.assertTrue(content.contains("json"));

        // 自定义请求
        response = HttpRequest.get("http://httpbin.org/get")
                .form(new HashMap() {{
                    this.put("key", "value");
                }})
                .header("Authorization", "Bearer your_token_here") // 添加请求头
                .timeout(5000) // 设置超时时间
                .execute(); // 发送请求
        content = response.body();
        Assert.assertTrue(content.contains("args"));
    }
}
