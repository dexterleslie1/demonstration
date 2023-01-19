create database if not exists testdb default character set utf8mb4 collate utf8mb4_general_ci;

use testdb;

create table if not exists test1(
    id          BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	createTime  DATETIME NOT NULL COMMENT '创建时间'
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

insert into test1(createTime) values(now());

