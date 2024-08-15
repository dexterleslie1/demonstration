CREATE DATABASE IF NOT EXISTS demo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

use demo;

create table if not exists course(
     id bigint primary key,
     name varchar(128) not null,
     age int not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

insert into course(id,name,age) values (5,'java',5),(15,'php',15),(16,'c',15),(31,'python',31);

create index idx_course_age on course(age);