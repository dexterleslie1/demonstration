package com.future.demo.migration;

import org.springframework.stereotype.Component;

/**
 * 迁移里要复用的业务规则。由 Spring 注入到 Java 迁移 Bean，而不是写进 SQL。
 */
@Component
public class EmailNormalizer {

    public String normalize(String email) {
        if (email == null) {
            return null;
        }
        String trimmed = email.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed.toLowerCase();
    }
}
