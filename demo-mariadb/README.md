## 说明

> 项目demo-mariadb-performance-test用于mariadb压力测试和调优



## 分区表

> Partition key , unique key, primary key建立分区表遵循规则
> https://dev.mysql.com/doc/refman/8.0/en/partitioning-limitations-partitioning-keys-unique-keys.html
>
> information_schema.PARTITIONS查看表分区状态

```
# 创建表
create table if not exists gameReportFLSeq(
	id bigint not null auto_increment,
    ruleCode varchar(16) not null,
    seqNumber varchar(16) not null,
    userIdHy bigint not null,
    `type` int not null,
    `date` datetime not null,
    primary key(id, date)
);

# 创建分区
alter table gameReportFLSeq partition by range columns(date)(
	partition p20150512 values less than ('2015-05-12 07:30:00'),
	partition pr values less than maxvalue
);

# 重新分区
ALTER TABLE gameReportFLSeq REORGANIZE PARTITION pr INTO ( 
    partition p20150513 VALUES LESS THAN ('2015-05-13 07:30:00'), 
    partition pr VALUES LESS THAN MAXVALUE 
);

# 取消所有分区
alter table gameReportFLSeq remove partitioning;
```



## 初始化数据库脚本

```
/*创建用户表*/
create table if not exists t_user(
   id int primary key auto_increment,
   loginname varchar(50) not null comment '登录名',
   password varchar(100) not null comment '登录密码',
   createTime datetime not null comment '创建时间'
)engine=InnoDB default charset=utf8;

delimiter |
begin not atomic
	if not exists(select * from information_schema.`COLUMNS` where 
               TABLE_SCHEMA=database() and 
               TABLE_NAME='t_user' and 
               COLUMN_NAME='ticket') then
      alter table t_user add column ticket varchar(50) comment '登陆成功后，系统分配uuid身份凭证';
   end if;
   
   if not exists(select * from information_schema.TABLE_CONSTRAINTS where
                           CONSTRAINT_SCHEMA=database() and
                           CONSTRAINT_NAME='t_user_unique_1' and 
                           CONSTRAINT_TYPE='UNIQUE') then
      alter table t_user add constraint t_user_unique_1 unique(loginname);
   end if;
end|
delimiter ;

/*初始化插入用户数据*/
insert into t_user(loginname,password,createTime)
select 'root','10d66ccecdf01ccfff2b86cfb1fd2b76',now() from dual
where not exists(select id from t_user where loginname='root');

/*暂存crawl数据*/
create table if not exists t_crawl_match(
   id int primary key auto_increment,
   className varchar(25) not null comment '体育类型名',
   leagueName varchar(25) not null comment '联赛名',
   competitorHomeName varchar(25) not null comment '主参赛者',
   competitorAwayName varchar(25) not null comment '客参赛者',
   resultName varchar(25) not null comment '赛果',
   sourceName varchar(25) not null comment '赛果来源',
   odds decimal(10,5) not null comment '赛果赔率',
   `time` datetime not null comment '竞赛时间',
   createTime datetime not null comment '创建时间'
)engine=InnoDB default charset=utf8;

delimiter |
begin not atomic
	if not exists(select * from information_schema.TABLE_CONSTRAINTS where
					   CONSTRAINT_SCHEMA=database() and
					   CONSTRAINT_NAME='t_crawl_match_unique_1' and 
					   CONSTRAINT_TYPE='UNIQUE') then
	  alter table t_crawl_match add constraint t_crawl_match_unique_1 
	  unique(className,leagueName,competitorHomeName,competitorAwayName,resultName,sourceName,`time`);
   end if;
end|
delimiter ;

create table if not exists t_notification(
   id int primary key auto_increment,
   userId int not null comment '用户id',
   `type` int not null default 0 comment '通知类型',
   content varchar(1024) not null comment '通知内容',
   isReceived int not null default 0 comment '通知是否已收到',
   createTime datetime not null comment '创建时间'
)engine=InnoDB default charset=utf8;

delimiter |
begin not atomic
	if not exists(select * from information_schema.TABLE_CONSTRAINTS where
                           CONSTRAINT_SCHEMA=database() and
                           CONSTRAINT_NAME='t_notification_fk_userId' and 
                           CONSTRAINT_TYPE='FOREIGN KEY') then
      alter table t_notification add constraint t_notification_fk_userId
      foreign key (userId) references t_user(id);
   end if;
end|
delimiter ;

/*体育类型*/
create table if not exists t_sport_class(
   id int primary key auto_increment,
   name varchar(25) not null comment '体育类型名称',
   createTime datetime not null comment '创建时间'
)engine=InnoDB default charset=utf8;
/*联赛*/
create table if not exists t_sport_league(
   id int primary key auto_increment,
   sportClassId int not null comment '联赛所属体育类型id',
   name varchar(25) not null comment '联赛名',
   createTime datetime not null comment '创建时间'
)engine=InnoDB default charset=utf8;
/*联赛别名*/
create table if not exists t_sport_league_alias(
   id int primary key auto_increment,
   leagueId int not null comment '所属联赛id',
   alias varchar(25) not null comment '联赛别名',
   createTime datetime not null comment '创建时间'
)engine=InnoDB default charset=utf8;
/*参赛者*/
create table if not exists t_sport_competitor(
   id int primary key auto_increment,
   name varchar(25) not null comment '参赛者名',
   createTime datetime not null comment '创建时间'
)engine=InnoDB default charset=utf8;
/*参赛者别名*/
create table if not exists t_sport_competitor_alias(
   id int primary key auto_increment,
   competitorId int not null comment '所属参赛者id',
   alias varchar(25) not null comment '别名',
   createTime datetime not null comment '创建时间'
)engine=InnoDB default charset=utf8;
/*赛事*/
create table if not exists t_sport_match(
   id int primary key auto_increment,
   leagueId int not null comment '所属联赛id',
    competitorHomeId int not null comment '主参赛者id',
    competitorAwayId int not null comment '客参赛者id',
    `time` datetime not null comment '竞赛时间',
   createTime datetime not null comment '创建时间'
)engine=InnoDB default charset=utf8;
/*赛事结果*/
create table if not exists t_sport_match_result(
   id int primary key auto_increment,
    name varchar(25) not null comment '赛果名称，例如主胜、打和、客胜，波胆1-0、0-1、1-1等',
   createTime datetime not null comment '创建时间'
)engine=InnoDB default charset=utf8;
/*赛果赔率来源*/
create table if not exists t_sport_match_result_odds_source(
   id int primary key auto_increment,
    name varchar(100) not null comment '名称',
   createTime datetime not null comment '创建时间'
)engine=InnoDB default charset=utf8;
/*赛果赔率*/
create table if not exists t_sport_match_result_odds(
   id int primary key auto_increment,
    matchId int not null comment '所属赛事id',
    matchResultId int not null comment '所属赛果id',
    sourceId int not null comment '赔率来源',
    leagueAliasId int not null comment '所属联赛别名id',
    competitorAliasHomeId int not null comment '主参赛者别名id',
    competitorAliasAwayId int not null comment '客参赛者别名id',
    odds decimal(10,5) not null comment '赛果赔率',
    isMax int not null default 0 comment '是否最大赔率',
   createTime datetime not null comment '创建时间'
)engine=InnoDB default charset=utf8;

/*用户表约束*/
delimiter |
begin not atomic
   if not exists(select * from information_schema.TABLE_CONSTRAINTS where
                           CONSTRAINT_SCHEMA=database() and
                           CONSTRAINT_NAME='t_sport_class_unique_1' and 
                           CONSTRAINT_TYPE='UNIQUE') then
      alter table t_sport_class add constraint t_sport_class_unique_1 unique(name);
   end if;
   
   if not exists(select * from information_schema.TABLE_CONSTRAINTS where
                           CONSTRAINT_SCHEMA=database() and
                           CONSTRAINT_NAME='t_sport_league_unique_1' and 
                           CONSTRAINT_TYPE='UNIQUE') then
      alter table t_sport_league add constraint t_sport_league_unique_1 
      unique(name);
   end if;
   if not exists(select * from information_schema.TABLE_CONSTRAINTS where
                           CONSTRAINT_SCHEMA=database() and
                           CONSTRAINT_NAME='t_sport_league_fk_sportClassId' and 
                           CONSTRAINT_TYPE='FOREIGN KEY') then
      alter table t_sport_league add constraint t_sport_league_fk_sportClassId foreign key (sportClassId)
      references t_sport_class(id);
   end if;
   
   if not exists(select * from information_schema.TABLE_CONSTRAINTS where
                           CONSTRAINT_SCHEMA=database() and
                           CONSTRAINT_NAME='t_sport_league_alias_unique_1' and 
                           CONSTRAINT_TYPE='UNIQUE') then
      alter table t_sport_league_alias add constraint t_sport_league_alias_unique_1
      unique(alias);
   end if;
   if not exists(select * from information_schema.TABLE_CONSTRAINTS where
                           CONSTRAINT_SCHEMA=database() and
                           CONSTRAINT_NAME='t_sport_league_alias_fk_leagueId' and 
                           CONSTRAINT_TYPE='FOREIGN KEY') then
      alter table t_sport_league_alias add constraint t_sport_league_alias_fk_leagueId
      foreign key (leagueId) references t_sport_league(id);
   end if;
   
   if not exists(select * from information_schema.TABLE_CONSTRAINTS where
                           CONSTRAINT_SCHEMA=database() and
                           CONSTRAINT_NAME='t_sport_competitor_unique_1' and 
                           CONSTRAINT_TYPE='UNIQUE') then
      alter table t_sport_competitor add constraint t_sport_competitor_unique_1 
      unique(name);
   end if;
   if not exists(select * from information_schema.TABLE_CONSTRAINTS where
                           CONSTRAINT_SCHEMA=database() and
                           CONSTRAINT_NAME='t_sport_competitor_alias_unique_1' and 
                           CONSTRAINT_TYPE='UNIQUE') then
      alter table t_sport_competitor_alias add constraint t_sport_competitor_alias_unique_1
      unique(competitorId,alias);
   end if;
   if not exists(select * from information_schema.TABLE_CONSTRAINTS where
                           CONSTRAINT_SCHEMA=database() and
                           CONSTRAINT_NAME='t_sport_competitor_alias_fk_competitorId' and 
                           CONSTRAINT_TYPE='FOREIGN KEY') then
      alter table t_sport_competitor_alias add constraint t_sport_competitor_alias_fk_competitorId
      foreign key (competitorId) references t_sport_competitor(id);
   end if;
   
   if not exists(select * from information_schema.TABLE_CONSTRAINTS where
                           CONSTRAINT_SCHEMA=database() and
                           CONSTRAINT_NAME='t_sport_match_unique_1' and 
                           CONSTRAINT_TYPE='UNIQUE') then
      /*不可能出现同时间同一个主和客参赛者*/
      alter table t_sport_match add constraint t_sport_match_unique_1
      unique(competitorHomeId,competitorAwayId,`time`);
   end if;
   if not exists(select * from information_schema.TABLE_CONSTRAINTS where
                           CONSTRAINT_SCHEMA=database() and
                           CONSTRAINT_NAME='t_sport_match_fk_leagueId' and 
                           CONSTRAINT_TYPE='FOREIGN KEY') then
      alter table t_sport_match add constraint t_sport_match_fk_leagueId
      foreign key (leagueId) references t_sport_league(id);
   end if;
   if not exists(select * from information_schema.TABLE_CONSTRAINTS where
                           CONSTRAINT_SCHEMA=database() and
                           CONSTRAINT_NAME='t_sport_match_fk_competitorHomeId' and 
                           CONSTRAINT_TYPE='FOREIGN KEY') then
      alter table t_sport_match add constraint t_sport_match_fk_competitorHomeId
      foreign key (competitorHomeId) references t_sport_competitor(id);
   end if;
   if not exists(select * from information_schema.TABLE_CONSTRAINTS where
                           CONSTRAINT_SCHEMA=database() and
                           CONSTRAINT_NAME='t_sport_match_fk_competitorAwayId' and 
                           CONSTRAINT_TYPE='FOREIGN KEY') then
      alter table t_sport_match add constraint t_sport_match_fk_competitorAwayId
      foreign key (competitorAwayId) references t_sport_competitor(id);
   end if;
   
   if not exists(select * from information_schema.TABLE_CONSTRAINTS where
                           CONSTRAINT_SCHEMA=database() and
                           CONSTRAINT_NAME='t_sport_match_result_unique_1' and 
                           CONSTRAINT_TYPE='UNIQUE') then
      alter table t_sport_match_result add constraint t_sport_match_result_unique_1
      unique(name);
   end if;
   
   if not exists(select * from information_schema.TABLE_CONSTRAINTS where
                           CONSTRAINT_SCHEMA=database() and
                           CONSTRAINT_NAME='t_sport_match_result_odds_source_unique_1' and 
                           CONSTRAINT_TYPE='UNIQUE') then
      alter table t_sport_match_result_odds_source add constraint t_sport_match_result_odds_source_unique_1
      unique(name);
   end if;
   
   if not exists(select * from information_schema.TABLE_CONSTRAINTS where
                           CONSTRAINT_SCHEMA=database() and
                           CONSTRAINT_NAME='t_sport_match_result_odds_unique_1' and 
                           CONSTRAINT_TYPE='UNIQUE') then
      /*不能重复同一时间赛果赔率*/
      alter table t_sport_match_result_odds add constraint t_sport_match_result_odds_unique_1
      unique(matchId,matchResultId,sourceId,createTime);
   end if;
   if not exists(select * from information_schema.TABLE_CONSTRAINTS where
                           CONSTRAINT_SCHEMA=database() and
                           CONSTRAINT_NAME='t_sport_match_result_odds_fk_matchId' and 
                           CONSTRAINT_TYPE='FOREIGN KEY') then
      alter table t_sport_match_result_odds add constraint t_sport_match_result_odds_fk_matchId
      foreign key (matchId) references t_sport_match(id);
   end if;
   if not exists(select * from information_schema.TABLE_CONSTRAINTS where
                           CONSTRAINT_SCHEMA=database() and
                           CONSTRAINT_NAME='t_sport_match_result_odds_fk_matchResultId' and 
                           CONSTRAINT_TYPE='FOREIGN KEY') then
      alter table t_sport_match_result_odds add constraint t_sport_match_result_odds_fk_matchResultId
      foreign key (matchResultId) references t_sport_match_result(id);
   end if;
   if not exists(select * from information_schema.TABLE_CONSTRAINTS where
                           CONSTRAINT_SCHEMA=database() and
                           CONSTRAINT_NAME='t_sport_match_result_odds_fk_sourceId' and 
                           CONSTRAINT_TYPE='FOREIGN KEY') then
      alter table t_sport_match_result_odds add constraint t_sport_match_result_odds_fk_sourceId
      foreign key (sourceId) references t_sport_match_result_odds_source(id);
   end if;
   if not exists(select * from information_schema.TABLE_CONSTRAINTS where
                           CONSTRAINT_SCHEMA=database() and
                           CONSTRAINT_NAME='t_sport_match_result_odds_fk_leagueAliasId' and 
                           CONSTRAINT_TYPE='FOREIGN KEY') then
      alter table t_sport_match_result_odds add constraint t_sport_match_result_odds_fk_leagueAliasId
      foreign key (leagueAliasId) references t_sport_league_alias(id);
   end if;
   if not exists(select * from information_schema.TABLE_CONSTRAINTS where
                           CONSTRAINT_SCHEMA=database() and
                           CONSTRAINT_NAME='t_sport_match_result_odds_fk_competitorAliasHomeId' and 
                           CONSTRAINT_TYPE='FOREIGN KEY') then
      alter table t_sport_match_result_odds add constraint t_sport_match_result_odds_fk_competitorAliasHomeId
      foreign key (competitorAliasHomeId) references t_sport_competitor_alias(id);
   end if;
   if not exists(select * from information_schema.TABLE_CONSTRAINTS where
                           CONSTRAINT_SCHEMA=database() and
                           CONSTRAINT_NAME='t_sport_match_result_odds_fk_competitorAliasAwayId' and 
                           CONSTRAINT_TYPE='FOREIGN KEY') then
      alter table t_sport_match_result_odds add constraint t_sport_match_result_odds_fk_competitorAliasAwayId
      foreign key (competitorAliasAwayId) references t_sport_competitor_alias(id);
   end if;
end|
delimiter ;

insert into t_sport_class(name,createTime)
select '足球',now() from dual where not exists(
   select id from t_sport_class where name='足球'
);

insert into t_sport_league(sportClassId,name,createTime)
select (select id from t_sport_class where name='足球'),'英超',now() from dual where not exists(
   select id from t_sport_league where name='英超'
);
insert into t_sport_league_alias(leagueId,alias,createTime)
select (select id from t_sport_league where name='英超'),'英超',now() from dual where not exists(
   select id from t_sport_league_alias where alias='英超'
);

insert into t_sport_competitor(name,createTime)
select '曼联',now() from dual where not exists(
   select id from t_sport_competitor where name='曼联'
);
insert into t_sport_competitor_alias(competitorId,alias,createTime)
select (select id from t_sport_competitor where name='曼联'),'曼联',now() from dual where not exists(
   select id from t_sport_competitor_alias where alias='曼联' and competitorId=(select id from t_sport_competitor where name='曼联')
);
insert into t_sport_competitor(name,createTime)
select '曼城',now() from dual where not exists(
   select id from t_sport_competitor where name='曼城'
);
insert into t_sport_competitor_alias(competitorId,alias,createTime)
select (select id from t_sport_competitor where name='曼城'),'曼城',now() from dual where not exists(
   select id from t_sport_competitor_alias where alias='曼城' and competitorId=(select id from t_sport_competitor where name='曼城')
);

insert into t_sport_match_result(name,createTime)
select '主胜',now() from dual where not exists(
   select id from t_sport_match_result where name='主胜'
);
insert into t_sport_match_result(name,createTime)
select '打和',now() from dual where not exists(
   select id from t_sport_match_result where name='打和'
);
insert into t_sport_match_result(name,createTime)
select '客胜',now() from dual where not exists(
   select id from t_sport_match_result where name='客胜'
);
```



## 存储过程



### 打印调试信息

> https://stackoverflow.com/questions/3314771/print-debugging-info-from-stored-procedure-in-mysql

```
delimiter |

begin not atomic
	declare my_var1 varchar(64) default '';
    set my_var1 = 'Dexter!';
    
    select concat('Hello', my_var1) as debug_info;
end|

delimiter ;
```



### 游标用法

```
delimiter |

begin not atomic
	-- https://navicat.com/en/company/aboutus/blog/1714-iterate-over-query-result-sets-using-a-cursor
    
    declare finished int default 0;
    declare my_id bigint default 0;
    declare my_col1 varchar(64) default '';
    declare my_code varchar(1024) default '';
    declare my_cursor cursor for select id,col1 from my_test_tbl order by id desc;
    declare continue handler for not found set finished = 1;
    
    -- 如果表存在则删除
    drop table if exists my_test_tbl;
    
    -- 重新创建表
    create table if not exists my_test_tbl(
		id bigint not null primary key auto_increment,
        col1 varchar(64)
    );
    
    -- 准备测试数据
    insert into my_test_tbl(col1) values('1');
    insert into my_test_tbl(col1) values('2');
    insert into my_test_tbl(col1) values('3');
    
    -- 打开游标
    open my_cursor;
    
    my_loop: loop
		-- 读取游标的记录到变量中
		fetch my_cursor into my_id, my_col1;
        
        -- 读取到最后一条记录
        if finished = 1 then
			-- 跳出循环
			leave my_loop;
        end if;
        
		set my_code = concat(my_col1, ';', my_code);
    end loop my_loop;
    
    select my_code as debug_info;
    
    -- 关闭游标
    close my_cursor;
end|

delimiter ;
```



### mariadb匿名存储过程

> NOTE: 使用DataGrid执行匿名存储过程报告语法错误，但使用MySQLWorkbench不会存在此问题

```
delimiter |

begin not atomic
	select now();
end|

delimiter ;
```



## binlog

### 启用binlog

在my.cnf文件中添加如下内容即可启用binlog

```properties
[mysqld]
log_bin
expire_logs_days=10
binlog_format=mixed
max_binlog_size=1024m
server_id=10001
```



### 查看和管理binlog

参考 https://mariadb.com/kb/en/purge-binary-logs/

查看binlog功能是否已经开启，通过下面命令查看log_bin参数是否为ON，ON表示已经启用binlog功能。参考 https://stackoverflow.com/questions/6956106/how-to-know-if-mysql-binary-log-is-enable-through-sql-command

```sh
show variables like '%bin%';
```

显示服务器中所有binlog文件

```sh
show binary logs;
```

删除所有binlog文件

```sh
reset master;
```

切换到新的binlog文件

```sh
flush logs;
```

删除binlog到指定文件之前(不包含本文件)

```sh
purge binary logs to 'master1-bin.000003';
```

删除binlog到指定时间之前

```sh
purge binary logs before '2013-04-22 09:55:22';
```



### 关闭/禁用binlog

**通过配置文件关闭binlog**

把my.cnf配置中的log_bin配置删除即可关闭binlog

**通过命令关闭binlog**

查阅相关资料后证实不能通过命令关闭binlog，只能够通过命令管理binlog



## 用户和权限管理

创建用户，用户没有创建数据库和数据表等权限，只有查看information_schema数据库权限。参考 https://mariadb.com/kb/en/create-user/

```sql
create user test1@localhost identified by 'test';
```

创建或者替换用户，如果用户已经存在不会报告错误。

```sql
create or replace user test1@localhost identified by 'test';
```

如果用户存在就提示警告

```sql
create user if not exists test1@localhost identified by 'test';
```

重命名用户，如果不指定host部分则默认使用%。

```sql
# test1@'localhost'用户被修改为test11@'%'
rename user test1@'localhost' to test11;
# test11@'%'用户被修改为test1@'localhost'
rename user test11@'%' to test1@'localhost';
```

删除用户

```sql
drop user test1@'localhost';
# 如果用户不存在只会提示警告
drop user if exists test1@'localhost';
```

修改用户密码，参考 https://mariadb.com/kb/en/set-password/

```sql
set password for test1@localhost = password("12345678");
```

使用alter user修改用户密码，参考 https://mariadb.com/kb/en/alter-user/

```sql
alter user test1@localhost identified by 'test';
```

### 权限管理

NOTE： 使用mariadb:11.3.2测试，使用mariadb:10.4.19有些权限不存在导致grant语法错误。

查看用户权限，参考 https://mariadb.com/kb/en/show-grants/

```sql
show grants for test1@localhost;
```

#### 全局权限

##### binlog admin权限

启用二进制日志的管理，包括 PURGE BINARY LOGS 语句和设置相关binlog系统变量

参考 https://mariadb.com/kb/en/grant/#binlog-admin

```sql
grant binlog admin on *.* to test1@localhost;
```

##### binlog monitor权限

MariaDB 10.5.2 中 REPLICATION CLIENT 的新名称（出于兼容性目的，仍支持将 REPLICATION CLIENT 作为别名）。 允许运行与二进制日志相关的 SHOW 命令，特别是 SHOW BINLOG STATUS 和 SHOW BINARY LOGS 语句。 与 MariaDB 10.5 之前的 REPLICATION CLIENT 不同，此权限不包含 SHOW REPLICA STATUS，并且需要 REPLICA MONITOR。

参考 https://mariadb.com/kb/en/grant/#binlog-monitor

```sql
grant binlog monitor on *.* to test1@localhost;
```

##### binlog replay权限

允许使用 BINLOG 语句（由 mariadb-binlog 生成）重放二进制日志，在 secure_timestamp 设置为复制时执行 SET 时间戳，并设置通常包含在 BINLOG 输出中的系统变量的会话值。

参考 https://mariadb.com/kb/en/grant/#binlog-replay

```sql
grant binlog replay on *.* to test1@localhost;
```

##### shutdown权限

使用 SHUTDOWN 或 mariadb-admin shutdown 命令关闭服务器。

参考 https://mariadb.com/kb/en/grant/#shutdown

```sql
grant shutdown on *.* to test1@localhost;
```

