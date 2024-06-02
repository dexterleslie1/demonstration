package com.future.demo.starter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

public class TestPropertiesPrinter {
    @Autowired
    TestProperties testProperties;

    public String print() {
        ConfigurationProperties configurationProperties =
                TestProperties.class.getAnnotation(ConfigurationProperties.class);
        String prefix = configurationProperties.prefix();

        StringBuilder builder = new StringBuilder();
        builder.append(prefix + ".prop1=" + testProperties.getProp1());
        return builder.toString();
    }
}
