package com.future.demo.hystrix.feign;

import com.future.demo.spring.cloud.common.ObjectResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@Slf4j
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
