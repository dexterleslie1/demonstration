# MySQL date、datetime、timestamp数据类型使用

## 演示datetime数据类型记录插入和修改相应时间列也被自动修改

### 参考资料

[Automatic Initialization and Updating for TIMESTAMP and DATETIME](https://dev.mysql.com/doc/refman/8.0/en/timestamp-initialization.html)

### 用于测试的MySQL脚本

```sql
create database if not exists demo_datetime default character set utf8mb4 collate utf8mb4_unicode_ci;

use demo_datetime;

drop table if exists t_testing;

create table if not exists t_testing (
	id bigint not null primary key auto_increment,
	username varchar(128) not null,
    createTime datetime not null default current_timestamp,
    updateTime datetime not null default current_timestamp on update current_timestamp
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

insert into t_testing(username) values("Dexter");
insert into t_testing(username) values("Dexter1");

select sleep(2);

update t_testing set username="Dexter2" where username="Dexter";
```

## 演示datetime where条件

### 用于测试的MySQL脚本

```sql
create database if not exists demo_datetime default character set utf8mb4 collate utf8mb4_unicode_ci;

use demo_datetime;

drop table if exists t_datetime_comparison;

create table if not exists t_datetime_comparison (
	id bigint not null primary key auto_increment,
	username varchar(128) not null,
    createTime datetime not null
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

insert into t_datetime_comparison(username,createTime) values('Dexter','2022-02-07 00:05:00');
insert into t_datetime_comparison(username,createTime) values('Dexter','2022-02-07 00:05:01');
insert into t_datetime_comparison(username,createTime) values('Dexter','2022-02-07 00:05:02');
insert into t_datetime_comparison(username,createTime) values('Dexter','2022-02-07 00:05:03');
insert into t_datetime_comparison(username,createTime) values('Dexter','2022-02-07 00:05:04');

# 字符串隐式转换比较
select * from t_datetime_comparison where createTime>='2022-02-07 00:05:02';

# 秒数转换比较
select * from t_datetime_comparison where unix_timestamp(createTime)>=unix_timestamp('2022-02-07 00:05:02');

# 使用to_seconds函数转换比较
select * from t_datetime_comparison where to_seconds(createTime)>=to_seconds('2022-02-07 00:05:02');
```