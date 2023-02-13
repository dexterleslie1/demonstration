create database if not exists demo_mybatis default character set utf8mb4 collate utf8mb4_general_ci;

use demo_mybatis;

drop table if exists tb_test_annotation_and_xml_mapper;
create table tb_test_annotation_and_xml_mapper (
    id bigint not null primary key auto_increment,
    flag varchar(32) not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user`
(
	id BIGINT(20) NOT NULL primary key auto_increment COMMENT '主键ID',
	name VARCHAR(30) NULL DEFAULT NULL COMMENT '姓名'
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;
