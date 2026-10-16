package com.future.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Flyway 示例：启动时按版本执行尚未跑过的迁移，并写入 flyway_schema_history。
 * SQL 脚本位于 classpath:db/migration；JavaMigration 类型的 Spring Bean
 * 也会按 V{版本}__描述 参与同一条版本链。
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
