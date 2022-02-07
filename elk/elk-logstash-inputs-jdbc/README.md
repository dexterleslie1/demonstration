# 使用logstash-inputs-jdbc插件实现MySQL和elasticsearch数据同步

## 参考资料

[如何使用 Logstash 和 JDBC 确保 Elasticsearch 与关系型数据库保持同步](https://www.elastic.co/cn/blog/how-to-keep-elasticsearch-synchronized-with-a-relational-database-using-logstash)

[官方jdbc插件使用](https://www.elastic.co/guide/en/logstash/7.17/plugins-inputs-jdbc.html#plugins-inputs-jdbc)

## MySQL测试脚本

```sql
create database if not exists demo_logstash_inputs_jdbc default character set utf8mb4 collate utf8mb4_unicode_ci;

use demo_logstash_inputs_jdbc;

drop table if exists t_testing;

create table if not exists t_testing (
	id bigint not null primary key auto_increment,
    content text not null comment '这个字段有些数据是JSON格式，有写数据不是JSON格式',
    updateTime datetime not null default current_timestamp on update current_timestamp
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

drop table if exists t_testing2;

create table if not exists t_testing2 (
	id bigint not null primary key auto_increment,
    content text not null,
    updateTime datetime not null default current_timestamp on update current_timestamp
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

insert into t_testing2(id,content) select 1,'内容1' from dual
where not exists(select id from t_testing2 where id=1);
insert into t_testing2(id,content) select 2,'内容2' from dual
where not exists(select id from t_testing2 where id=2);
insert into t_testing2(id,content) select 3,'内容3' from dual
where not exists(select id from t_testing2 where id=3);
insert into t_testing2(id,content) select 4,'内容4' from dual
where not exists(select id from t_testing2 where id=4);
insert into t_testing2(id,content) select 5,'内容5' from dual
where not exists(select id from t_testing2 where id=5);
```

## 在宿主机运行demo

1. 使用上面的脚本初始化MySQL测试数据库

2. 创建~/data-logstash目录，用于保存.logstash_jdbc_last_run_xxx文件

```shell
mkdir ~/data-logstash

chmod -R o+w ~/data-logstash
```

3. 修改logstash.conf相关MySQL配置

4. 复制mysql-connector-java-8.0.23.jar到 $HOME/mysql-connector-java-8.0.23.jar中

```shell
sudo cp mysql-connector-java-8.0.23.jar ~/mysql-connector-java-8.0.23.jar
```

5. 启动logstash

```shell
./logstash -f /path/to/config/logstash.conf
```

## 使用docker-compose运行demo

1. 使用上面的脚本初始化MySQL测试数据库

2. 创建~/data-logstash目录，用于保存.logstash_jdbc_last_run_xxx文件

```shell
mkdir ~/data-logstash

chmod -R o+w ~/data-logstash
```

3. 修改logstash.conf相关MySQL配置

4. 编译docker本地镜像
```shell
sh build.sh
```

5. 使用docker-compose启动容器
 
```shell
docker-compose up
```

6. 使用docker-compose删除容器

```shell
docker-compose down
```