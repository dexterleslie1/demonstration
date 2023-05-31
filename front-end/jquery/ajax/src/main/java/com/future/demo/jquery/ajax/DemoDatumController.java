package com.future.demo.jquery.ajax;

import org.springframework.web.bind.annotation.*;

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
    public Map<String, String> post(@RequestParam(value = "param1", defaultValue = "") String param1) {
        Map<String, String> params = new HashMap<>();
        params.put("key1", "value1");
        params.put("param1", param1);
        return params;
    }
}
