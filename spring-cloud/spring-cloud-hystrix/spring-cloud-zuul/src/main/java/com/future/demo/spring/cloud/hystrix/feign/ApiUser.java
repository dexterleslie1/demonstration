package com.future.demo.spring.cloud.hystrix.feign;

import com.future.demo.spring.cloud.common.ObjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "spring-cloud-user", fallbackFactory = ApiUserFallbackFactory.class)
// NOTE: 千万不能再Feign interface上添加@RequestMapping，否则报告There is already 'XXXXX' bean method错误
// https://blog.csdn.net/weixin_44495198/article/details/105931661
//@RequestMapping("/api/v1")
public interface ApiUser {

    @PostMapping("/api/v1/user/timeout")
    ResponseEntity<ObjectResponse<String>> timeout(@RequestParam(value = "milliseconds") Integer milliseconds);

    @PostMapping("/api/v1/user/timeout2")
    ResponseEntity<ObjectResponse<String>> timeout2(@RequestParam(value = "milliseconds") Integer milliseconds);

    class ApiUserFallback implements ApiUser{
        @Override
        public ResponseEntity<ObjectResponse<String>> timeout(Integer milliseconds) {
            return createErrorResponse();
        }

        @Override
        public ResponseEntity<ObjectResponse<String>> timeout2(Integer milliseconds) {
            return createErrorResponse();
        }

        ResponseEntity<ObjectResponse<String>> createErrorResponse() {
            ObjectResponse<String> response = new ObjectResponse<>();
            response.setErrorCode(600);
            response.setErrorMessage("User服务不可用，稍候...（来自ApiUser）");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }
}
