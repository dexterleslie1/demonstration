create database if not exists demo_db default character set utf8mb4 collate utf8mb4_general_ci;

use demo_db;

create table if not exists t_pet (
    id BIGINT(20) NOT NULL primary key auto_increment COMMENT '主键ID',
    `name` varchar(128) NOT NULL unique,
    age int not null,
    createTime datetime not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

