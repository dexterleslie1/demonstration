create database if not exists demo character set utf8mb4 collate utf8mb4_general_ci;

use demo;

create table if not exists t_flag_created(
    id              bigint not null primary key auto_increment,
    flag            varchar(32) not null unique,
    createTime      datetime not null
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;
