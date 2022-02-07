# 演示datetime数据类型记录插入和修改相应时间列也被自动修改

## 用于测试的MySQL脚本

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