# `binlog`相关

## 什么是`binlog`呢？

MySQL的binlog，即二进制日志，是一种用于记录数据库更改的日志文件。它以二进制格式存储，包含了所有对数据库执行的修改操作，如插入（INSERT）、更新（UPDATE）和删除（DELETE）等，但不包括像SELECT和SHOW这样不影响数据的查询操作。

## 怎么启用`binlog`呢？

在`my.cnf`配置中添加如下配置即可启用`binlog`

```ini
[mysqld]
# 启用binlog
log_bin
# 推荐使用 ROW 格式，但你也可以选择 STATEMENT 或 MIXED
binlog_format = MIXED
# 设置 binlog 文件在自动删除前的保留天数   
expire_logs_days = 30 
# 设置单个 binlog 文件的最大大小
max_binlog_size = 512M
server-id = 10001
```

## 查看和管理binlog

> [参考链接](https://mariadb.com/kb/en/purge-binary-logs/)

查看binlog功能是否已经开启，通过下面命令查看log_bin参数是否为ON，ON表示已经启用binlog功能。[参考链接](https://stackoverflow.com/questions/6956106/how-to-know-if-mysql-binary-log-is-enable-through-sql-command)

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



## `binlog`日志文件的解析

>[参考链接](https://dba.stackexchange.com/questions/101651/show-sql-statements-from-mysql-binlog-gtid)

解析`mysql-bin.000005 binlog`输出到`1.sql`文件

```bash
mysqlbinlog --base64-output=auto --verbose mysql-bin.000005 > 1.sql
```

指定日志开始位置（包含）到停止时间

```bash
mysqlbinlog --start-position=256442 --stop-datetime='2017-12-14 15:02:32' master1-bin.000003y>1.sql
```



## 自动清除`binlog`

在`MySQL`性能测试过程会产生大量的`binlog`导致硬盘空间占满，需要自动释放`binlog`。

使用以下脚本自动释放`binlog`

```bash
while :; do; mysql -uroot -p123456 -h127.0.0.1 -e "reset master;"; echo "成功执行SQL"; sleep 120; done
```

