CREATE DATABASE IF NOT EXISTS demo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE demo_db;

CREATE TABLE IF NOT EXISTS t_user(
    id                 BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    username           VARCHAR(64) NOT NULL COMMENT '名称',
    `password`         VARCHAR(64) NOT NULL COMMENT '密码',
    createTime         DATETIME NOT NULL COMMENT '创建时间'
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- NOTE: 下面只生成一个cdc事件，类型op=r
/*insert into t_user(username,`password`,createTime) values('user1','123456',now());
update t_user set username='userx' where username='user1';*/


