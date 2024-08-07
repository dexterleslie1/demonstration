package com.future.demo.hystrix.client;

import com.future.demo.spring.cloud.common.ObjectResponse;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Component
public class ApiUserRestTemplate {
    @Autowired
    RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "timeoutFallback",
            commandKey = "circuit-api-user-rest-m1",
            threadPoolKey = "thread-pool-api-user-rest-1",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.strategy", value = "THREAD"),
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
                    @HystrixProperty(name = "circuitBreaker.enabled", value = "true")},
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "21"),
                    @HystrixProperty(name = "maxQueueSize", value = "101"),
                    @HystrixProperty(name = "queueSizeRejectionThreshold", value = "101")
            })
    public ResponseEntity<ObjectResponse<String>> timeout(Integer milliseconds) {
        MultiValueMap<String, Object> multiValueParams= new LinkedMultiValueMap<>();
        multiValueParams.add("milliseconds", milliseconds);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(multiValueParams, null);

        return restTemplate.exchange("http://spring-cloud-user/api/v1/user/timeout",
                HttpMethod.POST,
                httpEntity,
                new ParameterizedTypeReference<ObjectResponse<String>>(){});
    }

    @HystrixCommand(fallbackMethod = "timeoutFallback2",
            commandKey = "circuit-api-user-rest-m2",
            threadPoolKey = "thread-pool-api-user-rest-2",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.strategy", value = "THREAD"),
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
                    @HystrixProperty(name = "circuitBreaker.enabled", value = "true")},
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "21"),
                    @HystrixProperty(name = "maxQueueSize", value = "101"),
                    @HystrixProperty(name = "queueSizeRejectionThreshold", value = "101")
            })
    public ResponseEntity<ObjectResponse<String>> timeout2(Integer milliseconds) {
        MultiValueMap<String, Object> multiValueParams= new LinkedMultiValueMap<>();
        multiValueParams.add("milliseconds", milliseconds);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(multiValueParams, null);

        return restTemplate.exchange("http://spring-cloud-user/api/v1/user/timeout2",
                HttpMethod.POST,
                httpEntity,
                new ParameterizedTypeReference<ObjectResponse<String>>(){});
    }

    ResponseEntity<ObjectResponse<String>> timeoutFallback(Integer milliseconds) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorCode(600);
        response.setErrorMessage("User服务不可用，稍候...（来自ApiUserRestTemplate）");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    ResponseEntity<ObjectResponse<String>> timeoutFallback2(Integer milliseconds) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorCode(600);
        response.setErrorMessage("User服务不可用，稍候...（来自ApiUserRestTemplate）");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
