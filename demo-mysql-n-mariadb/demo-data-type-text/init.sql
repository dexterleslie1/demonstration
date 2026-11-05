create database if not exists demo default character set utf8mb4 collate utf8mb4_general_ci;

use demo;

-- 不开启压缩
create table if not exists t_mediumtext (
    id bigint unsigned not null auto_increment,
    content mediumtext,
    primary key (id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_general_ci;

-- 字段级压缩（MariaDB 10.3+）
create table if not exists t_mediumtext_compressed (
    id bigint unsigned not null auto_increment,
    content mediumtext compressed,
    primary key (id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_general_ci;
