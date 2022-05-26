create database if not exists mybatisplusdemo default character set utf8mb4 collate utf8mb4_general_ci;

use mybatisplusdemo;

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user`
(
	id BIGINT(20) NOT NULL auto_increment COMMENT '主键ID',
	name VARCHAR(30) NULL DEFAULT NULL COMMENT '姓名',
	age INT(11) NULL DEFAULT NULL COMMENT '年龄',
	email VARCHAR(50) NULL DEFAULT NULL COMMENT '邮箱',
	authorities varchar(256) default null comment '用户权限模拟字符串逗号分隔到java List类型转换',
	PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

/* 用于调试mybatis-plus自定义partition SQL */
create table if not exists dan(
  id int auto_increment,
  createDate datetime not null,
  primary key(id,createDate)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci
partition by range(to_days(createDate))(
    partition pdefault values less than (to_days('2015-12-13 00:00:00'))
);