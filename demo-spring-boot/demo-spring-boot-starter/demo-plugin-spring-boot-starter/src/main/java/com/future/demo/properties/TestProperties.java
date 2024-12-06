package com.future.demo.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "com.future.demo.test")
public class TestProperties {
    private String prop1;
    private int prop2;
    private List<String> prop3;
    private Map<String, String> prop4;
    private NestedTestProperties nested;

    @Data
    public static class NestedTestProperties {
        private String prop1;
        private int prop2;
    }
}
