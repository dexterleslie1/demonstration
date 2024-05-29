create database if not exists demo_spring_boot_test default character set utf8mb4 collate utf8mb4_general_ci;

use demo_spring_boot_test;

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user`
(
	id BIGINT(20) NOT NULL auto_increment COMMENT '主键ID',
	name VARCHAR(30) NULL DEFAULT NULL COMMENT '姓名',
	age INT(11) NULL DEFAULT NULL COMMENT '年龄',
	email VARCHAR(50) NULL DEFAULT NULL COMMENT '邮箱',
	authorities varchar(256) default null comment '用户权限模拟字符串逗号分隔到java List类型转换',
	PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

/* 用于调试mybatis-plus自定义partition SQL */
create table if not exists dan(
  id int auto_increment,
  createDate datetime not null,
  primary key(id,createDate)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci
partition by range(to_days(createDate))(
    partition pdefault values less than (to_days('2015-12-13 00:00:00'))
);

/* 用于演示mybatis-plus join查询 */
create table if not exists t_developer(
    id bigint primary key auto_increment,
    `name` varchar(512) not null,
    createTime datetime not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

create table if not exists t_ipset(
    id bigint primary key auto_increment,
    `name` varchar(512) not null,
    createTime datetime not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

create table if not exists t_developer_and_ipset_relation(
    id bigint primary key auto_increment,
    developerId BIGINT(20) not null,
    ipsetId BIGINT(20) not null,
    createTime datetime not null,
    constraint `fk_tDeveloperToIpsetRelation_developerId` foreign key (developerId) references t_developer(id),
    constraint `fk_tDeveloperToIpsetRelation_ipsetId` foreign key (ipsetId) references t_ipset(id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

create table if not exists t_football_match(
    id bigint primary key auto_increment,
    teamIdA bigint not null,
    teamIdB bigint not null,
    createTime datetime not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

insert into t_football_match(teamIdA,teamIdB,createTime)
select 1,2,now() from dual where not exists(
    select * from t_football_match where teamIdA=1 and teamIdB=2
);
insert into t_football_match(teamIdA,teamIdB,createTime)
select 1,3,now() from dual where not exists(
    select * from t_football_match where teamIdA=1 and teamIdB=3
);
insert into t_football_match(teamIdA,teamIdB,createTime)
select 2,3,now() from dual where not exists(
    select * from t_football_match where teamIdA=2 and teamIdB=3
);
