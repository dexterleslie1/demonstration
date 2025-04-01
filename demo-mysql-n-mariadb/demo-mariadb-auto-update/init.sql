-- 用于初始化数据库脚本

CREATE DATABASE IF NOT EXISTS demo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE demo_db;

CREATE TABLE IF NOT EXISTS `auth`(
    id                  BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    account             VARCHAR(64) NOT NULL UNIQUE COMMENT '账号',
    `password`          VARCHAR(64) NOT NULL COMMENT '密码',
    create_time         DATETIME NOT NULL COMMENT '创建时间'
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;