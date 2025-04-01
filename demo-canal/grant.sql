create user 'canal'@'%' identified by 'canal';
grant SELECT, REPLICATION SLAVE, REPLICATION CLIENT on *.* to 'canal'@'%';

create database if not exists demo_canal default character set utf8mb4 collate utf8mb4_general_ci;

use demo_canal;

CREATE TABLE IF NOT EXISTS t_test(
	id                  BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	createTime          datetime not null comment '创建时间'
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;
