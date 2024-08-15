create database if not exists demo default character set utf8mb4 collate utf8mb4_general_ci;

use demo;

CREATE TABLE `user`
(
	id BIGINT(20) NOT NULL auto_increment COMMENT '主键ID',
	name VARCHAR(30) NULL DEFAULT NULL unique COMMENT '姓名',
	PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;
