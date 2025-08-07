package com.future.demo.controller;

import cn.hutool.core.util.RandomUtil;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.config.PrometheusCustomMonitor;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Dexterleslie.Chan
 */
@RestController
@RequestMapping(value = "/api/v1")
public class ApiController {
    @Resource
    PrometheusCustomMonitor monitor;
    @Resource
    AtomicInteger orderMasterTotalCount;
    @Resource
    AtomicInteger orderDetailTotalCount;
    @Resource
    MeterRegistry meterRegistry;

    private List<byte[]> bytesHolder = new ArrayList<>();

    /**
     * @return
     */
    @GetMapping(value = "consume5mbmemory")
    public ResponseEntity<Map<String, Object>> consume5mbmemory() {
        bytesHolder.add(new byte[5 * 1024 * 1024]);
        return null;
    }

    @GetMapping("testCounter")
    public ObjectResponse<String> testCounter() {
        this.monitor.incrementOrderCountMaster();
        this.monitor.incrementOrderCountDetail();
        return ResponseUtils.successObject("成功下单");
    }

    /**
     * 测试设置订单主表总记录数
     *
     * @return
     */
    @GetMapping("testSetOrderMasterTotalCount")
    public ObjectResponse<String> testSetOrderMasterTotalCount() {
        this.orderMasterTotalCount.incrementAndGet();
        return ResponseUtils.successObject("调用成功");
    }

    /**
     * 测试设置订单明细表总记录数
     *
     * @return
     */
    @GetMapping("testSetOrderDetailTotalCount")
    public ObjectResponse<String> testSetOrderDetailTotalCount() {
        this.orderDetailTotalCount.incrementAndGet();
        return ResponseUtils.successObject("调用成功");
    }

    List<String> methodList = Arrays.asList("get", "put");
    List<String> urlList = Arrays.asList("/api/v1/test1", "/api/v1/test2");

    /**
     * 协助测试请求延迟直方图
     *
     * @return
     */
    @GetMapping("testAssistRequestLatency")
    public ObjectResponse<String> testAssistRequestLatency() {
        String method = methodList.get(RandomUtil.randomInt(0, methodList.size()));
        String url = urlList.get(RandomUtil.randomInt(0, urlList.size()));
        int milliseconds = RandomUtil.randomInt(0, 70000);
        // 模拟请求延迟直方图
        Timer.builder("http_request_duration")
                .description("Request latency in seconds.")
                // 桶中的区间的分布如下 5-10（单位是毫秒）、10-20、...
                .serviceLevelObjectives(
                        Duration.ofMillis(5),
                        Duration.ofMillis(10),
                        Duration.ofMillis(20),
                        Duration.ofMillis(30),
                        Duration.ofMillis(50),
                        Duration.ofMillis(75),
                        Duration.ofMillis(100),
                        Duration.ofMillis(200),
                        Duration.ofMillis(300),
                        Duration.ofMillis(400),
                        Duration.ofMillis(500),
                        Duration.ofMillis(750),
                        Duration.ofMillis(1000),
                        Duration.ofMillis(1500),
                        Duration.ofMillis(2000),
                        Duration.ofMillis(3000),
                        Duration.ofMillis(4000),
                        Duration.ofMillis(5000),
                        Duration.ofMillis(10000),
                        Duration.ofMillis(20000),
                        Duration.ofMillis(30000),
                        Duration.ofMillis(60000)
                )
                .tags("method", method, "url", url)
                .register(meterRegistry).record(Duration.ofMillis(milliseconds));
        return ResponseUtils.successObject("调用成功");
    }
}
