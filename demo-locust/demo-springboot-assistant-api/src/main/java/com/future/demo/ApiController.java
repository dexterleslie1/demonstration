package com.future.demo;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    /**
     * 用于测试locust HttpUser client.get方法
     *
     * @return
     */
    @GetMapping("testGet")
    ObjectResponse<String> testGet() {
        String uuid = UUID.randomUUID().toString();
        return ResponseUtils.successObject(uuid);
    }

}
