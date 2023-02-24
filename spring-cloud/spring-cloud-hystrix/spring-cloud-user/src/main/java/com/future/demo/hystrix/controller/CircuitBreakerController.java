package com.future.demo.hystrix.controller;

import com.future.demo.spring.cloud.common.ObjectResponse;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class CircuitBreakerController {
    @HystrixCommand(fallbackMethod = "testCircuitBreaker1Fallback", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000"),
            // https://blog.csdn.net/qq_43509535/article/details/113799835
            //该属性用来没置在滚动时间窗中，断路器熔断的最小请求数。例如，默认该值为20的时候
            //，如果滚动时间窗（默认10秒）内仅收到了19个请求，即使这19个请求都失败了，断路器也不会打开。
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
            //该属性用来设置当断路器打开之后的休眠时间窗。休眠时间窗结束之后，
            //会将断路器置为“半开”状态，尝试熔断的请求命令，如果依然失败就将断路器继续设置为“打开”状态，
            //如果成功就没置为"关闭”状态。
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "30000"),
            //该属性用来没置在滚动时间窗中，表示在滚动时间窗中，在请求数量超过
            //circuitBreaker.requestVolumeThreshold的情况下，如果错误
            //请求数的百分比超过50,就把断路器设置为“打开”状态，否则就设置为“关闭”状态。
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5")
    })
    @GetMapping("testCircuitBreaker1")
    ResponseEntity<ObjectResponse<String>> testCircuitBreaker1(@RequestParam(value = "id", defaultValue = "0") int id) {
        if(id<=0) {
            throw new RuntimeException("id小于等于0");
        }

        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("成功调用CircuitBreakerController#testCircuitBreaker1接口" + UUID.randomUUID().toString());
        return ResponseEntity.ok(response);
    }

    ResponseEntity<ObjectResponse<String>> testCircuitBreaker1Fallback(int id) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorCode(5000);
        response.setData("调用CircuitBreakerController#testCircuitBreaker1接口失败" + UUID.randomUUID().toString());
        return ResponseEntity.ok(response);
    }
}
