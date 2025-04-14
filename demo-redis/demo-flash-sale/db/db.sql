create database if not exists demo character set utf8mb4 collate utf8mb4_general_ci;

use demo;

create table if not exists t_order(
    id          bigint not null auto_increment primary key,
    userId      bigint not null,
    createTime  datetime not null
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

create table if not exists t_order_detail (
    id          bigint not null auto_increment primary key,
    orderId     bigint not null,
    userId      bigint not null comment '协助简化和提高用户重复下单判断逻辑',
    productId   bigint not null,
    amount      int not null,
    constraint fk_order_detail_orderId foreign key(orderId) references t_order(id)
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

create table if not exists t_product(
    id          bigint not null auto_increment primary key,
    name        varchar(255) not null,
    stock       int not null
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

insert into t_product(id, name, stock) values(1, '苹果', 50);
