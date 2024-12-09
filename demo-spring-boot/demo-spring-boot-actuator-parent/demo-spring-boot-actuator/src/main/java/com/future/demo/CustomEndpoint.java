package com.future.demo;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

// 自定义endpoint
@Component
@Endpoint(id = "custom")
public class CustomEndpoint {
    private final Map<String, Object> map = new HashMap<String, Object>() {{
        put("name", "custom");
        put("age", 18);
    }};
    @ReadOperation
    public Map<String, Object> get() {
        return map;
    }

    @WriteOperation
    public void post(String key, Object value) {
        map.put(key, value);
    }
}
