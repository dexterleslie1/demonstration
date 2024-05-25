package com.future.demo;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.MetricsServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MyMetricsServlet extends MetricsServlet {
    private Counter myCounter;
    private Gauge myGauge;
    public static List<String> typeList = new ArrayList<>(Arrays.asList("type0", "type1", "type2"));

    public MyMetricsServlet(Counter myCounter, Gauge myGauge) {
        super();
        this.myCounter = myCounter;
        this.myGauge = myGauge;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Random random = new Random();
        String type = typeList.get(random.nextInt(typeList.size()));

        this.myCounter.labels(type).inc();

        // 随机设置gauge的值，0-1的值表示0%-100%
        double randomDouble = Math.random();
        this.myGauge.labels(type).set(randomDouble);

        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println();
        super.doPost(req, resp);
    }
}
