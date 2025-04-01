package com.future.demo;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class DemoController {
    // 用于协助/actuator/logfile测试产生日志
    // 用于协助测试自定义指标开发
    @GetMapping("/")
    public String index() {
        log.info("Hello World!");

        // 自定义指标测试
        Metrics.counter("demo.index.counter", "uri", "/").increment();
        Timer timer = Metrics.timer("demo.index.timer", "uri", "/");
        timer.record(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        return "Hello World";
    }

    // 用于协助actuator优雅关闭
    @GetMapping("test1")
    public String test1() throws InterruptedException {
        Thread.sleep(10000);
        return "成功调用";
    }
}
