create database if not exists demo character set utf8mb4 collate utf8mb4_general_ci;

use demo;

create table if not exists employees(
    id          int(11) not null primary key auto_increment,
    name        varchar(36) not null default '' comment '姓名',
    age         int(11) not null default 0 comment '年龄',
    position    varchar(20) not null default '' comment '职位',
    hire_time   timestamp not null default current_timestamp comment '入职时间',
    remark      varchar(255) default null comment '备注',
    key idx_name_age_position(name, age, position) using btree
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;
