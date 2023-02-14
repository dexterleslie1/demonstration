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
	name VARCHAR(64) not null COMMENT '姓名',
	password varchar(64) not null,
	age int not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

drop table if exists test_resultmap;
create table test_resultmap(
    rmid bigint not null primary key auto_increment,
    rmname varchar(64) not null,
    rmpassword varchar(64) not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

insert into test_resultmap values (NULL,'用户1','123456'),(NULL,'用户2','123456');

create table t_dept(
    did bigint not null primary key auto_increment,
    dname varchar(64) not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;
create table t_address(
    id bigint not null primary key auto_increment,
    address varchar(64) not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;
create table t_emp(
    eid bigint not null primary key auto_increment,
    ename varchar(64) not null,
    eage varchar(64) not null,
    did bigint not null,
    addressId bigint not null unique,
    constraint fk_t_emp_did foreign key(did) references t_dept(did),
    constraint fk_t_emp_addressId foreign key(addressId) references t_address(id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

insert into t_dept values (1,'产品部'),(2,'研发部');
insert into t_address values (1,'上海'),(2,'北京'),(3,'广州');
insert into t_emp values (1,'张三',21,2,1),(2,'李四',22,2,2),(3,'小红',33,1,3);