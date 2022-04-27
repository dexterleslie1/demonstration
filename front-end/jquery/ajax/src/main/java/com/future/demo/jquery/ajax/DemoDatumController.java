package com.future.demo.jquery.ajax;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/")
public class DemoDatumController {
    @GetMapping("get")
    public Map<String, String> get() {
        Map<String, String> params = new HashMap<>();
        params.put("key1", "value1");
        return params;
    }

    @PostMapping("post")
    public Map<String, String> post() {
        Map<String, String> params = new HashMap<>();
        params.put("key1", "value1");
        return params;
    }
}
