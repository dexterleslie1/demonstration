package com.future.demo.controller;

import com.future.common.http.ObjectResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1")
public class DemoController {
    @GetMapping("test1")
    public ObjectResponse<String> test1(@RequestParam(value = "flag", defaultValue = "") String flag) throws Exception {
        if ("exception".equals(flag)) {
            throw new RuntimeException("测试异常");
        }

        if ("sleep".equals(flag)) {
            TimeUnit.MILLISECONDS.sleep(1100);
        }

        if ("bulkheadsleep".equals(flag)) {
            TimeUnit.MILLISECONDS.sleep(3000);
        }

        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("flag=" + flag);
        return response;
    }
}
