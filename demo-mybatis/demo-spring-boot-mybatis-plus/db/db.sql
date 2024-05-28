create database if not exists mybatisplusdemo default character set utf8mb4 collate utf8mb4_general_ci;

use mybatisplusdemo;

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

/** 操作记录表 */
CREATE TABLE IF NOT EXISTS `operation_log`(
    id                  BIGINT NOT NULL AUTO_INCREMENT,
    auth_id             BIGINT NOT NULL COMMENT '当前操作日志属于哪个用户，操作日志列表接口根据该字段获取指定用户下的操作日志',
    operator_id         BIGINT NOT NULL COMMENT '操作人',
    passive_id          BIGINT NOT NULL COMMENT '被操作人',
    operation_type      TINYINT NOT NULL COMMENT '操作类型',
    content             TEXT COMMENT '操作内容',
    create_time         DATETIME NOT NULL COMMENT '操作时间',
    primary key(id, auth_id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

ALTER TABLE operation_log MODIFY COLUMN passive_id BIGINT COMMENT '被操作人';
ALTER TABLE operation_log ADD COLUMN IF NOT EXISTS auth_id BIGINT NOT NULL COMMENT '当前操作日志属于哪个用户，操作日志列表接口根据该字段获取指定用户下的操作日志';

-- optimize: 创建以下索引后对于查询性能有飞跃式的提高
create index if not exists idx_operation_log_comb1 on operation_log (auth_id, operation_type);
