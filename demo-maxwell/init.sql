-- Maxwell 账号与权限（官方 quickstart）
CREATE USER IF NOT EXISTS 'maxwell'@'%' IDENTIFIED BY '123456';
GRANT ALL ON maxwell.* TO 'maxwell'@'%';
GRANT SELECT, REPLICATION CLIENT, REPLICATION SLAVE ON *.* TO 'maxwell'@'%';

CREATE DATABASE IF NOT EXISTS demo CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE demo;

CREATE TABLE IF NOT EXISTS t_user (
    id          BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(64) NOT NULL COMMENT '名称',
    createTime  DATETIME NOT NULL COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO t_user(username, createTime) VALUES('user1', NOW());
