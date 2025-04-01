# `mysql Too Many Connections`错误处理

>[Dealing With “Too Many Connections” Error in MySQL 8](https://www.percona.com/blog/dealing-with-too-many-connections-error-in-mysql-8/)

## 方案1

我们知道，数据库中允许的连接数由参数“max_connections”定义。此参数的默认值为 151，并且可以动态更改，这意味着无需重新启动数据库。如果数据库中的连接数达到最大值，我们将看到可怕的消息“ERROR 1040 (08004)：连接数过多”。重要的是要记住，MySQL 默认允许一个额外的连接，此连接是为具有“SUPER”权限（此处已弃用）或 CONNECTION_ADMIN 权限的用户保留的。

 我将展示此功能的一个示例；在这个例子中，我有一个“max_connections=10”的实例，我有三个用户，用户“monitor1”没有 SUPER 权限，用户“admin1”具有 PROCESS 和 CONNECTION_ADMIN 权限，最后用户“admin2”具有 SUPER 权限（已弃用）。

`mariadb`容器实例安装`sysbench`工具

```dockerfile
RUN apt update
RUN apt install sysbench -y
```

创建用户

```sql
create user if not exists monitor1@'%' identified by '123456';
grant all privileges on *.* to monitor1@'%';
revoke super on *.* from monitor1@'%';

create user if not exists admin2@'%' identified by '123456';
grant super on *.* to admin2@'%';
```

使用`monitor1`用户占用所有连接

```bash
# 创建测试数据库
mysql -umonitor1 -p'123456' -e "create database sbtest;"

# 准备测试数据
sysbench oltp_read_write --table-size=10000 --db-driver=mysql --mysql-host=localhost --mysql-db=sbtest --mysql-user=monitor1 --mysql-password="123456" --num-threads=10 --time=0 --report-interval=1 prepare

# 开始测试
sysbench oltp_read_write --table-size=10000 --db-driver=mysql --mysql-host=localhost --mysql-db=sbtest --mysql-user=monitor1 --mysql-password="123456" --num-threads=10 --time=0 --report-interval=1 run
```

测试`Too Many Connections`错误

```bash
# 使用root用户能够建立连接，因为root用户有 SUPER 权限
mysql -uroot -p

# 使用monitor1用户不能建立链接，报告Too Many Connections错误
mysql -umonitor1 -p
```



## 方案2

注意：没有测试此方案。

从 MySQL 8.0.14 开始，引入了新的“管理连接”或“管理网络接口”功能。此功能允许通过管理端口连接到数据库，管理连接数没有限制。此功能与上例中显示的单个连接的区别在于，这是一个不同的端口，并且它不会将连接限制为只有一个，而是在需要时限制为多个连接。这应该允许我们在用户连接达到最大值时访问数据库，并从那里开始增加连接或终止一些应用程序连接。