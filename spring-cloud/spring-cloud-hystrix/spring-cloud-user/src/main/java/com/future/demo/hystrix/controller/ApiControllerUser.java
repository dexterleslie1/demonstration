package com.future.demo.hystrix.controller;

import com.future.demo.spring.cloud.common.ObjectResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/user")
public class ApiControllerUser {
    @Value("${spring.application.name}")
    String applicationName;

    @GetMapping("timeout")
    ResponseEntity<ObjectResponse<String>> timeout(@RequestParam(value = "milliseconds", defaultValue = "0") int milliseconds) {
        if(milliseconds>0) {
            try {
                TimeUnit.MILLISECONDS.sleep(milliseconds);
            } catch (InterruptedException e) {
                //
            }
        }

        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("成功调用" + applicationName + " timeout接口");
        return ResponseEntity.ok(response);
    }

    /**
     * 此接口用于演示同一个微服务线程隔离特性
     * @param milliseconds
     * @return
     */
    @GetMapping("timeout2")
    ResponseEntity<ObjectResponse<String>> timeout2(@RequestParam(value = "milliseconds", defaultValue = "0") int milliseconds) {
        if(milliseconds>0) {
            try {
                TimeUnit.MILLISECONDS.sleep(milliseconds);
            } catch (InterruptedException e) {
                //
            }
        }

        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("成功调用" + applicationName + " timeout2接口");
        return ResponseEntity.ok(response);
    }
}
