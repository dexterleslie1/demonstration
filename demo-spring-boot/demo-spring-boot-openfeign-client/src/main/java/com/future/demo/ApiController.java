package com.future.demo;

import com.future.common.http.ObjectResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1")
@Slf4j
public class ApiController {

    // 用于测试http 401错误
    @GetMapping(value = "test401Error")
    public ResponseEntity<ObjectResponse<String>> test401Error() {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorCode(90000);
        response.setErrorMessage("调用 /api/v1/test401Error 失败");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
