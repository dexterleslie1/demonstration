package com.future.demo;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.MetricsServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MyMetricsServlet extends MetricsServlet {
    private SimpleClientConfig simpleClientConfig;
    public static List<String> instanceList = new ArrayList<>(Arrays.asList("gce0", "gce1", "gce2"));

    public MyMetricsServlet(SimpleClientConfig simpleClientConfig) {
        super();
        this.simpleClientConfig = simpleClientConfig;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Random random = new Random();
        String instance = instanceList.get(random.nextInt(instanceList.size()));
        // 操作Counter指标数据时指定instance标签的值为gce0、gce1、gce2
        this.simpleClientConfig.myCounter.labels(instance).inc();

        // 随机设置gauge的值，0-1的值表示0%-100%
        double randomDouble = Math.random();
        this.simpleClientConfig.myGauge.set(randomDouble);

        // 模拟最长的响应时间为60000毫秒
        int maxInt = 60000;
        int randomInt = random.nextInt(maxInt);
        // 模拟GET请求到/api/v1/data
        this.simpleClientConfig.requestDurationHistogram.labels("get", "/api/v1/data").observe(randomInt);
        // 模拟POST请求到/api/v1/submit
        this.simpleClientConfig.requestDurationHistogram.labels("post", "/api/v1/submit").observe(randomInt);

        randomInt = random.nextInt(maxInt);
        this.simpleClientConfig.summary.observe(randomInt);

        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        super.doPost(req, resp);
    }
}
