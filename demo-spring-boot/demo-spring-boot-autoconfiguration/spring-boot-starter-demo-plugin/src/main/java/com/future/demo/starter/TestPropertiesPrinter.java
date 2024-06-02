package com.future.demo.starter;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

public class TestPropertiesPrinter {
    @Autowired
    TestProperties testProperties;

    public String print() {
        // 获取自定义属性的前缀
        ConfigurationProperties configurationProperties =
                TestProperties.class.getAnnotation(ConfigurationProperties.class);
        String prefix = configurationProperties.prefix();

        return prefix + ".prop1=" + testProperties.getProp1();
    }

    @Data
    @ConfigurationProperties(prefix = "com.future.demo.test")
    public static class TestProperties {
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
}
