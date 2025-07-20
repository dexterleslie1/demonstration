create database if not exists demo character set utf8mb4 collate utf8mb4_general_ci;

use demo;

create table if not exists t_product(
    id          bigint not null primary key,
    `name`      varchar(255) not null,
    merchantId  bigint not null comment '商家ID',
    stock       int not null comment '商品库存'
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;
