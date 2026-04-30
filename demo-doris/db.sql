CREATE DATABASE IF NOT EXISTS demo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE demo;

CREATE TABLE IF NOT EXISTS `auth`(
    id                  BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    account             VARCHAR(64) NOT NULL UNIQUE COMMENT '账号',
    `password`          VARCHAR(64) NOT NULL COMMENT '密码',
    create_time         DATETIME NOT NULL COMMENT '创建时间'
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

create table if not exists student(
    id                  BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `name`              VARCHAR(64) NOT NULL UNIQUE COMMENT '名称',
    create_time         DATETIME NOT NULL COMMENT '创建时间'
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

create table if not exists course(
    id                  BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    student_id          BIGINT NOT NULL,
    `name`              VARCHAR(64) NOT NULL UNIQUE COMMENT '名称'
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

insert into student(id,`name`,create_time) values(1,'小明',now());
insert into course(id,student_id,`name`) values(1,1,'生物');
insert into course(id,student_id,`name`) values(2,1,'化学');