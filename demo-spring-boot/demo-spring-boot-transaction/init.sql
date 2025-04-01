drop database if exists demo;

create database if not exists demo character set utf8mb4 collate utf8mb4_unicode_ci;

use demo;

create table if not exists balance (
  id bigint primary key auto_increment,
  amount decimal(20,5) not null default 0
) engine=InnoDB character set utf8mb4 collate utf8mb4_unicode_ci;

create table if not exists balance_log (
  id bigint primary key auto_increment,
  amount decimal(20,5) not null,
  createTime datetime not null
) engine=InnoDB character set utf8mb4 collate utf8mb4_unicode_ci;

create table if not exists book(
    id      bigint primary key auto_increment,
    `name`  varchar(255) not null,
    price   decimal(10,5),
    stock   int
) engine=InnoDB character set utf8mb4 collate utf8mb4_unicode_ci;

insert into book(id,`name`,price,stock) values(1,'Java编程',100,50);

create table if not exists account(
    id          bigint primary key auto_increment,
    username    varchar(255) not null,
    age         int,
    balance     decimal(15,5)
) engine=InnoDB character set utf8mb4 collate utf8mb4_unicode_ci;

insert into account(id,username,age,balance) values(1,'Dexter',30,10000);
