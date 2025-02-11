create database if not exists demo default character set utf8mb4 collate utf8mb4_general_ci;

use demo;

create table if not exists employee (
    id          bigint primary key auto_increment,
    emp_name    varchar(255) not null,
    age         int not null,
    emp_salary  decimal(15,5) not null
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_general_ci;

create table if not exists customer (
    id          bigint primary key auto_increment,
    customer_name    varchar(255) not null,
    phone       varchar(255) not null
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_general_ci;

insert into customer(id, customer_name, phone) values(1, '张三', '13800000000');
insert into customer(id, customer_name, phone) values(2, '李四', '15800000000');
insert into customer(id, customer_name, phone) values(3, '王五', '18900000000');

create table if not exists `order` (
    id          bigint primary key auto_increment,
    address     varchar(255) not null,
    amount      decimal(15,5) not null,
    customer_id bigint not null,
    create_time datetime not null
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_general_ci;

insert into `order`(id, address, amount, customer_id, create_time) values(1, '北京市', 200.56, 1, now());
insert into `order`(id, address, amount, customer_id, create_time) values(2, '上海市', 300.56, 1, now());
insert into `order`(id, address, amount, customer_id, create_time) values(3, '广州市', 400.56, 2, now());
insert into `order`(id, address, amount, customer_id, create_time) values(4, '深圳市', 500.56, 3, now());
insert into `order`(id, address, amount, customer_id, create_time) values(5, '深圳市', 500.56, 3, now());

create table if not exists student (
    id          bigint primary key auto_increment,
    name        varchar(255) not null,
    age         int not null,
    score       decimal(15,5) not null
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_general_ci;

create table if not exists t_enum_storing_as_int (
    id          bigint primary key auto_increment,
    `status`    int not null
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_general_ci;

create table if not exists t_enum_storing_as_varchar (
    id          bigint primary key auto_increment,
    `status`    varchar(128) not null
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_general_ci;

create table if not exists t_enum_storing_as_enum (
    id          bigint primary key auto_increment,
    `status`    enum('Create','Paying','InProgress','Failed','Reversed')
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_general_ci;
