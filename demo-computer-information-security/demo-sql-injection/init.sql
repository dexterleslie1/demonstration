create database if not exists demo default character set utf8mb4 collate utf8mb4_general_ci;

use demo;

CREATE TABLE IF NOT EXISTS `user`
(
    id         BIGINT(20) NOT NULL auto_increment COMMENT '主键ID',
    username   VARCHAR(30) NULL DEFAULT NULL COMMENT '昵称',
    `password` VARCHAR(128) NOT NULL COMMENT '登录密码',
    createTime DATETIME     NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

insert into `user`(username,password,createTime) values ('user1', 'xxx1', now());
insert into `user`(username,password,createTime) values ('user2', 'xxx2', now());

create table if not exists `secret_data`(
    id bigint not null primary key auto_increment,
    col1 varchar(50),
    col2 varchar(50)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

insert into `secret_data`(col1, col2) values('secret-col1','secret-col2');
