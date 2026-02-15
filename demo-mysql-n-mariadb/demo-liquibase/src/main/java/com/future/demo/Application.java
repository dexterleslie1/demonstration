package com.future.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Liquibase 示例：启动时自动执行 db/changelog 下未执行过的 ChangeSet，
 * 并在库中维护 DATABASECHANGELOG 表记录执行历史。支持声明式 DDL 与回滚。
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
