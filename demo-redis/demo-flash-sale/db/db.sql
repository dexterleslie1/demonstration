create database if not exists demo character set utf8mb4 collate utf8mb4_general_ci;

use demo;

create table if not exists t_order(
    id          bigint not null auto_increment primary key,
    userId      bigint not null,
    productId   bigint not null,
    amount      int not null,
    createTime  datetime not null
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

create table if not exists t_product(
    id          bigint not null auto_increment primary key,
    name        varchar(255) not null,
    stock       int not null
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

insert into t_product(id, name, stock) values(1, '苹果', 50);
