create database if not exists demo default character set utf8mb4 collate utf8mb4_general_ci;

use demo;

create table if not exists employee_x (
    id          bigint primary key auto_increment,
    emp_name    varchar(255) not null,
    age         int not null,
    emp_salary  decimal(15,5) not null
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_general_ci;
