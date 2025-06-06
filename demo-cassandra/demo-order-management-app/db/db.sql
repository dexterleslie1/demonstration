create database if not exists demo character set utf8mb4 collate utf8mb4_general_ci;

use demo;

create table if not exists t_id_cache_assistant (
    id          bigint not null auto_increment primary key,
    orderId     bigint not null
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;
