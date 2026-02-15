package com.future.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Flyway 示例：启动时自动执行 db/migration 下未执行过的 V*.sql 脚本，
 * 并在库中维护 flyway_schema_history 表记录执行历史。
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
