## 数据模型

> 建立在关系模型基础上，有多张相互连接的二维表组成。

## SQL

### SQL预编译

> https://blog.csdn.net/make_1998/article/details/118930914
>
> 一次编译、多次运行，省去了解析优化等过程；此外预编译语句能防止sql注入。

### DDL数据库操作

```shell
# 查询所有数据库
mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
4 rows in set (0.00 sec)

# 查询当前数据库
mysql> use mysql
Reading table information for completion of table and column names
You can turn off this feature to get a quicker startup with -A

Database changed
mysql> select database();
+------------+
| database() |
+------------+
| mysql      |
+------------+
1 row in set (0.00 sec)

# 创建数据库
mysql> CREATE DATABASE IF NOT EXISTS testdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
Query OK, 1 row affected (0.00 sec)

mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| mysql              |
| performance_schema |
| sys                |
| testdb             |
+--------------------+
5 rows in set (0.00 sec)

# 删除数据库
mysql> drop database if exists testdb;
Query OK, 0 rows affected (0.01 sec)

mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
4 rows in set (0.00 sec)

# 切换数据库
mysql> use mysql;
Database changed
mysql> select database();
+------------+
| database() |
+------------+
| mysql      |
+------------+
1 row in set (0.00 sec)

mysql> use sys;
Reading table information for completion of table and column names
You can turn off this feature to get a quicker startup with -A

Database changed
mysql> select database();
+------------+
| database() |
+------------+
| sys        |
+------------+
1 row in set (0.00 sec)
```

### DDL表操作

测试数据库环境准备

```shell
mysql> CREATE DATABASE IF NOT EXISTS testdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
Query OK, 1 row affected (0.01 sec)

mysql> use testdb;
Database changed
```



```shell
# 显示当前数据库所有表
mysql> use mysql
Reading table information for completion of table and column names
You can turn off this feature to get a quicker startup with -A

Database changed
mysql> show tables;
+------------------------------------------------------+
| Tables_in_mysql                                      |
+------------------------------------------------------+
| columns_priv                                         |
| component                                            |
| db                                                   |
......
+------------------------------------------------------+
37 rows in set (0.01 sec)

# 查看表名为 db 的表结构
mysql> desc db;
+-----------------------+---------------+------+-----+---------+-------+
| Field                 | Type          | Null | Key | Default | Extra |
+-----------------------+---------------+------+-----+---------+-------+
| Host                  | char(255)     | NO   | PRI |         |       |
| Db                    | char(64)      | NO   | PRI |         |       |
| User                  | char(32)      | NO   | PRI |         |       |
| Select_priv           | enum('N','Y') | NO   |     | N       |       |
......
+-----------------------+---------------+------+-----+---------+-------+
22 rows in set (0.01 sec)

# 查看表名为 db 的创建表SQL语句
mysql> show create table db\G;
*************************** 1. row ***************************
       Table: db
Create Table: CREATE TABLE `db` (
  `Host` char(255) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL DEFAULT '',
  `Db` char(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `User` char(32) COLLATE utf8_bin NOT NULL DEFAULT '',
  `Select_priv` enum('N','Y') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'N',
  ......
  PRIMARY KEY (`Host`,`Db`,`User`),
  KEY `User` (`User`)
) /*!50100 TABLESPACE `mysql` */ ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin STATS_PERSISTENT=0 ROW_FORMAT=DYNAMIC COMMENT='Database privileges'
1 row in set (0.00 sec)

ERROR: 
No query specified

# 添加字段
mysql> create table if not exists emp(
    ->  id int primary key auto_increment,
    ->  workno varchar(10),
    ->  name varchar(10),
    ->  gender char(1),
    ->  age int,
    ->  idcard char(18),
    ->  entrydate date
    -> ) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;
Query OK, 0 rows affected, 1 warning (0.01 sec)
mysql> alter table emp add column nickname varchar(20) comment "nicname";
Query OK, 0 rows affected (0.01 sec)
Records: 0  Duplicates: 0  Warnings: 0
mysql> desc emp;
+-----------+-------------+------+-----+---------+----------------+
| Field     | Type        | Null | Key | Default | Extra          |
+-----------+-------------+------+-----+---------+----------------+
| id        | int         | NO   | PRI | NULL    | auto_increment |
| workno    | varchar(10) | YES  |     | NULL    |                |
| name      | varchar(10) | YES  |     | NULL    |                |
| gender    | char(1)     | YES  |     | NULL    |                |
| age       | int         | YES  |     | NULL    |                |
| idcard    | char(18)    | YES  |     | NULL    |                |
| entrydate | date        | YES  |     | NULL    |                |
| nickname  | varchar(20) | YES  |     | NULL    |                |
+-----------+-------------+------+-----+---------+----------------+
8 rows in set (0.01 sec)

# 修改字段数据类型
mysql> alter table emp modify column nickname varchar(10);
Query OK, 0 rows affected (0.04 sec)
Records: 0  Duplicates: 0  Warnings: 0

# 修改字段名称和数据类型
mysql> alter table emp change nickname username varchar(15);
Query OK, 0 rows affected (0.00 sec)
Records: 0  Duplicates: 0  Warnings: 0

mysql> desc emp;
+-----------+-------------+------+-----+---------+----------------+
| Field     | Type        | Null | Key | Default | Extra          |
+-----------+-------------+------+-----+---------+----------------+
| id        | int         | NO   | PRI | NULL    | auto_increment |
| workno    | varchar(10) | YES  |     | NULL    |                |
| name      | varchar(10) | YES  |     | NULL    |                |
| gender    | char(1)     | YES  |     | NULL    |                |
| age       | int         | YES  |     | NULL    |                |
| idcard    | char(18)    | YES  |     | NULL    |                |
| entrydate | date        | YES  |     | NULL    |                |
| username  | varchar(15) | YES  |     | NULL    |                |
+-----------+-------------+------+-----+---------+----------------+
8 rows in set (0.01 sec)

mysql> alter table emp drop column username;
Query OK, 0 rows affected (0.02 sec)
Records: 0  Duplicates: 0  Warnings: 0
# mariadb语法支持判断字段存在才执行删除操作
mysql> alter table emp drop column if exists username;

# 修改表名
mysql> alter table emp rename to employee;
Query OK, 0 rows affected (0.01 sec)

mysql> show tables;
+------------------+
| Tables_in_testdb |
+------------------+
| employee         |
+------------------+
1 row in set (0.01 sec)

# 删除表
mysql> drop table if exists emp;
Query OK, 0 rows affected, 1 warning (0.01 sec)

# 删除表并且重新创建表
mysql> truncate table employee;
Query OK, 0 rows affected (0.02 sec)
```

### DML插入数据

```shell
# 给指定字段插入单条数据
mysql> insert into employee(workno,name,gender,age,idcard,entrydate) values('001','xiaoming','m',21,'0000000123',now());
Query OK, 1 row affected, 1 warning (0.01 sec)

mysql> select * from employee\G;
*************************** 1. row ***************************
       id: 1
   workno: 001
     name: xiaoming
   gender: m
      age: 21
   idcard: 0000000123
entrydate: 2023-02-09
1 row in set (0.00 sec)

ERROR: 
No query specified

# 给所有字段插入单条数据
mysql> insert into employee values(2,'002','xiaohong','f',18,'0000000125',now());
Query OK, 1 row affected, 1 warning (0.00 sec)

mysql> select * from employee\G;
*************************** 1. row ***************************
       id: 1
   workno: 001
     name: xiaoming
   gender: m
      age: 21
   idcard: 0000000123
entrydate: 2023-02-09
*************************** 2. row ***************************
       id: 2
   workno: 002
     name: xiaohong
   gender: f
      age: 18
   idcard: 0000000125
entrydate: 2023-02-09
2 rows in set (0.00 sec)

ERROR: 
No query specified

# 给指定字段插入多条数据
mysql> insert into employee(workno,name,gender,age,idcard,entrydate) values('0003','zhangsan','m',25,'000000111',now()),('0005','lisi','m',22,'000000222',now());
Query OK, 2 rows affected, 2 warnings (0.01 sec)
Records: 2  Duplicates: 0  Warnings: 2

mysql> select * from employee\G;
*************************** 1. row ***************************
       id: 1
   workno: 001
     name: xiaoming
   gender: m
      age: 21
   idcard: 0000000123
entrydate: 2023-02-09
*************************** 2. row ***************************
       id: 2
   workno: 002
     name: xiaohong
   gender: f
      age: 18
   idcard: 0000000125
entrydate: 2023-02-09
*************************** 3. row ***************************
       id: 3
   workno: 0003
     name: zhangsan
   gender: m
      age: 25
   idcard: 000000111
entrydate: 2023-02-09
*************************** 4. row ***************************
       id: 4
   workno: 0005
     name: lisi
   gender: m
      age: 22
   idcard: 000000222
entrydate: 2023-02-09
4 rows in set (0.01 sec)

ERROR: 
No query specified
```

### DML修改数据

> 已经掌握没有demo要写

### DML删除数据

> 已经掌握没有demo要写

### DQL分组查询

> where和having区别：
>
> 执行时机不同：where是分组之前进行过滤，不满足where条件，不参与分组。而having是分组之后对结果进行过滤。
>
> 判断条件不同：where不能对聚合函数进行判断，而having可以。

准备测试环境

```shell
mysql> CREATE DATABASE IF NOT EXISTS testdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
Query OK, 1 row affected (0.01 sec)

mysql> use testdb;
Database changed

mysql> create table if not exists employee(
    ->   id int primary key auto_increment,
    ->   workno varchar(10),
    ->   name varchar(10),
    ->   gender char(1),
    ->   age int,
    ->   idcard char(18),
    ->   workaddress varchar(64),
    ->   entrydate date
    -> ) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;
Query OK, 0 rows affected (0.02 sec)

mysql> insert into employee(workno,name,gender,age,idcard,workaddress,entrydate) values
    -> ('0001','xiaoming','m',18,'000000111','beijing','2018-05-23'),
    -> ('0002','xiaohong','f',19,'000000222','shanghai','2019-11-10'),
    -> ('0003','zhangsan','m',18,'000000333','beijing','2013-08-05'),
    -> ('0005','lisi','m',33,'000000555','shanghai','2022-08-22');
Query OK, 4 rows affected (0.01 sec)
Records: 4  Duplicates: 0  Warnings: 0
```

```shell
# 根据性别分组统计男女工人数量
mysql> select gender,count(*) from employee group by gender;
+--------+----------+
| gender | count(*) |
+--------+----------+
| m      |        3 |
| f      |        1 |
+--------+----------+
2 rows in set (0.00 sec)

# 根据性别分组统计男女工人平均年龄
mysql> select gender,avg(age) from employee group by gender;
+--------+----------+
| gender | avg(age) |
+--------+----------+
| m      |  25.3333 |
| f      |  19.0000 |
+--------+----------+
2 rows in set (0.00 sec)

# 统计年龄小于等于30并根据工作地点分组，获取员工数大于等于2的工作地点
mysql> select workaddress,count(*) from employee where age<=30 group by workaddress having count(*)>=2;
+-------------+----------+
| workaddress | count(*) |
+-------------+----------+
| beijing     |        2 |
+-------------+----------+
1 row in set (0.00 sec)
```

### DQL排序查询

准备测试环境

```shell
mysql> CREATE DATABASE IF NOT EXISTS testdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
Query OK, 1 row affected (0.01 sec)

mysql> use testdb;
Database changed

mysql> create table if not exists employee(
    ->   id int primary key auto_increment,
    ->   workno varchar(10),
    ->   name varchar(10),
    ->   gender char(1),
    ->   age int,
    ->   idcard char(18),
    ->   workaddress varchar(64),
    ->   entrydate date
    -> ) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;
Query OK, 0 rows affected (0.02 sec)

mysql> insert into employee(workno,name,gender,age,idcard,workaddress,entrydate) values
    -> ('0001','xiaoming','m',18,'000000111','beijing','2018-05-23'),
    -> ('0002','xiaohong','f',19,'000000222','shanghai','2019-11-10'),
    -> ('0003','zhangsan','m',18,'000000333','beijing','2013-08-05'),
    -> ('0005','lisi','m',33,'000000555','shanghai','2022-08-22');
Query OK, 4 rows affected (0.01 sec)
Records: 4  Duplicates: 0  Warnings: 0
```

```shell
# 根据年龄升序排序，如果年龄相同根据入职时间降序排序
mysql> select * from employee order by age asc,entrydate desc;
+----+--------+----------+--------+------+-----------+-------------+------------+
| id | workno | name     | gender | age  | idcard    | workaddress | entrydate  |
+----+--------+----------+--------+------+-----------+-------------+------------+
|  1 | 0001   | xiaoming | m      |   18 | 000000111 | beijing     | 2018-05-23 |
|  3 | 0003   | zhangsan | m      |   18 | 000000333 | beijing     | 2013-08-05 |
|  2 | 0002   | xiaohong | f      |   19 | 000000222 | shanghai    | 2019-11-10 |
|  4 | 0005   | lisi     | m      |   33 | 000000555 | shanghai    | 2022-08-22 |
+----+--------+----------+--------+------+-----------+-------------+------------+
4 rows in set (0.00 sec)
```

### DQL分页查询

准备测试环境

```shell
mysql> CREATE DATABASE IF NOT EXISTS testdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
Query OK, 1 row affected (0.01 sec)

mysql> use testdb;
Database changed

mysql> create table if not exists employee(
    ->   id int primary key auto_increment,
    ->   workno varchar(10),
    ->   name varchar(10),
    ->   gender char(1),
    ->   age int,
    ->   idcard char(18),
    ->   workaddress varchar(64),
    ->   entrydate date
    -> ) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;
Query OK, 0 rows affected (0.02 sec)

mysql> insert into employee(workno,name,gender,age,idcard,workaddress,entrydate) values
    -> ('0001','xiaoming','m',18,'000000111','beijing','2018-05-23'),
    -> ('0002','xiaohong','f',19,'000000222','shanghai','2019-11-10'),
    -> ('0003','zhangsan','m',18,'000000333','beijing','2013-08-05'),
    -> ('0005','lisi','m',33,'000000555','shanghai','2022-08-22');
Query OK, 4 rows affected (0.01 sec)
Records: 4  Duplicates: 0  Warnings: 0
```

```shell
# 查询第一页数据，0为记录索引开始，2为返回记录数
mysql> select * from employee order by id asc limit 0,2;
+----+--------+----------+--------+------+-----------+-------------+------------+
| id | workno | name     | gender | age  | idcard    | workaddress | entrydate  |
+----+--------+----------+--------+------+-----------+-------------+------------+
|  1 | 0001   | xiaoming | m      |   18 | 000000111 | beijing     | 2018-05-23 |
|  2 | 0002   | xiaohong | f      |   19 | 000000222 | shanghai    | 2019-11-10 |
+----+--------+----------+--------+------+-----------+-------------+------------+
2 rows in set (0.00 sec)

# 查询第二页数据，2为记录索引开始，2为返回记录数
mysql> select * from employee order by id asc limit 2,2;
+----+--------+----------+--------+------+-----------+-------------+------------+
| id | workno | name     | gender | age  | idcard    | workaddress | entrydate  |
+----+--------+----------+--------+------+-----------+-------------+------------+
|  3 | 0003   | zhangsan | m      |   18 | 000000333 | beijing     | 2013-08-05 |
|  4 | 0005   | lisi     | m      |   33 | 000000555 | shanghai    | 2022-08-22 |
+----+--------+----------+--------+------+-----------+-------------+------------+
2 rows in set (0.00 sec)
```

### DCL用户和权限管理

```shell
# 查询用户
mysql> use mysql;
Reading table information for completion of table and column names
You can turn off this feature to get a quicker startup with -A

Database changed
mysql> select * from user\G;
*************************** 1. row ***************************
                    Host: %
                    User: root
             Select_priv: Y
             Insert_priv: Y
             Update_priv: Y
             Delete_priv: Y
             Create_priv: Y
               Drop_priv: Y
             Reload_priv: Y
           Shutdown_priv: Y
            Process_priv: Y
               File_priv: Y
              Grant_priv: Y
         References_priv: Y
              Index_priv: Y
              Alter_priv: Y
            Show_db_priv: Y
              Super_priv: Y
   Create_tmp_table_priv: Y
        Lock_tables_priv: Y
            Execute_priv: Y
         Repl_slave_priv: Y
        Repl_client_priv: Y
        Create_view_priv: Y
          Show_view_priv: Y
     Create_routine_priv: Y
      Alter_routine_priv: Y
        Create_user_priv: Y
              Event_priv: Y
            Trigger_priv: Y
  Create_tablespace_priv: Y
                ssl_type: 
              ssl_cipher: 0x
             x509_issuer: 0x
            x509_subject: 0x
           max_questions: 0
             max_updates: 0
         max_connections: 0
    max_user_connections: 0
                  plugin: caching_sha2_password
   authentication_string: $A$005$(EyBGcoVb%hEqHr1Lx1QrCsLCpA9lT2TU2yRKbVuTEPAOGAgVtjA9jqo80
        password_expired: N
   password_last_changed: 2023-02-09 11:13:00
       password_lifetime: NULL
          account_locked: N
        Create_role_priv: Y
          Drop_role_priv: Y
  Password_reuse_history: NULL
     Password_reuse_time: NULL
Password_require_current: NULL
         User_attributes: NULL
*************************** 2. row ***************************
......

ERROR: 
No query specified

# 创建user1用户
mysql> create user user1@'%' identified by '123456';
Query OK, 0 rows affected (0.01 sec)

mysql> select * from user where User='user1'\G;
*************************** 1. row ***************************
                    Host: %
                    User: user1
             Select_priv: N
             Insert_priv: N
             Update_priv: N
             Delete_priv: N
             Create_priv: N
               Drop_priv: N
             Reload_priv: N
           Shutdown_priv: N
            Process_priv: N
               File_priv: N
              Grant_priv: N
         References_priv: N
              Index_priv: N
              Alter_priv: N
            Show_db_priv: N
              Super_priv: N
   Create_tmp_table_priv: N
        Lock_tables_priv: N
            Execute_priv: N
         Repl_slave_priv: N
        Repl_client_priv: N
        Create_view_priv: N
          Show_view_priv: N
     Create_routine_priv: N
      Alter_routine_priv: N
        Create_user_priv: N
              Event_priv: N
            Trigger_priv: N
  Create_tablespace_priv: N
                ssl_type: 
              ssl_cipher: 0x
             x509_issuer: 0x
            x509_subject: 0x
           max_questions: 0
             max_updates: 0
         max_connections: 0
    max_user_connections: 0
                  plugin: caching_sha2_password
   authentication_string: $A$005$1K=QE5u<1b=iI
S/x5t/lhoX7IUkNLq1X/jVrr3xvvOcOAGSDoScw7F91
        password_expired: N
   password_last_changed: 2023-02-09 17:38:17
       password_lifetime: NULL
          account_locked: N
        Create_role_priv: N
          Drop_role_priv: N
  Password_reuse_history: NULL
     Password_reuse_time: NULL
Password_require_current: NULL
         User_attributes: NULL
1 row in set (0.00 sec)

ERROR: 
No query specified

# 修改用户密码
mysql> alter user user1@'%' identified with mysql_native_password by '123456';
Query OK, 0 rows affected (0.00 sec)

# 删除用户
mysql> drop user user1@'%';
Query OK, 0 rows affected (0.00 sec)

# 查询用户user1的权限，USAGE权限表示只能够连接mysql
mysql> show grants for user1@'%';
+-----------------------------------+
| Grants for user1@%                |
+-----------------------------------+
| GRANT USAGE ON *.* TO `user1`@`%` |
+-----------------------------------+
1 row in set (0.00 sec)

# 授予用户user1对所有数据库所有表的所有操作权限
mysql> grant all on *.* to user1@'%';
Query OK, 0 rows affected (0.01 sec)
mysql> show grants for user1@'%'\G;
*************************** 1. row ***************************
Grants for user1@%: GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, RELOAD, SHUTDOWN, PROCESS, FILE, REFERENCES, INDEX, ALTER, SHOW DATABASES, SUPER, CREATE TEMPORARY TABLES, LOCK TABLES, EXECUTE, REPLICATION SLAVE, REPLICATION CLIENT, CREATE VIEW, SHOW VIEW, CREATE ROUTINE, ALTER ROUTINE, CREATE USER, EVENT, TRIGGER, CREATE TABLESPACE, CREATE ROLE, DROP ROLE ON *.* TO `user1`@`%`
*************************** 2. row ***************************
Grants for user1@%: GRANT APPLICATION_PASSWORD_ADMIN,AUDIT_ADMIN,AUTHENTICATION_POLICY_ADMIN,BACKUP_ADMIN,BINLOG_ADMIN,BINLOG_ENCRYPTION_ADMIN,CLONE_ADMIN,CONNECTION_ADMIN,ENCRYPTION_KEY_ADMIN,FLUSH_OPTIMIZER_COSTS,FLUSH_STATUS,FLUSH_TABLES,FLUSH_USER_RESOURCES,GROUP_REPLICATION_ADMIN,GROUP_REPLICATION_STREAM,INNODB_REDO_LOG_ARCHIVE,INNODB_REDO_LOG_ENABLE,PASSWORDLESS_USER_ADMIN,PERSIST_RO_VARIABLES_ADMIN,REPLICATION_APPLIER,REPLICATION_SLAVE_ADMIN,RESOURCE_GROUP_ADMIN,RESOURCE_GROUP_USER,ROLE_ADMIN,SERVICE_CONNECTION_ADMIN,SESSION_VARIABLES_ADMIN,SET_USER_ID,SHOW_ROUTINE,SYSTEM_USER,SYSTEM_VARIABLES_ADMIN,TABLE_ENCRYPTION_ADMIN,XA_RECOVER_ADMIN ON *.* TO `user1`@`%`
2 rows in set (0.00 sec)

ERROR: 
No query specified

# 从用户user1回收对所有库所有操作权限
mysql> revoke all on *.* from user1@'%';
Query OK, 0 rows affected (0.00 sec)
mysql> show grants for user1@'%'\G;
*************************** 1. row ***************************
Grants for user1@%: GRANT USAGE ON *.* TO `user1`@`%`
1 row in set (0.00 sec)

ERROR: 
No query specified
```

## 约束

> 暂时未用到

## 多表查询

> 暂时未用到

## 存储引擎

```shell
# 显示当前数据库支持的存储引擎
mysql> show engines\G;
*************************** 1. row ***************************
      Engine: FEDERATED
     Support: NO
     Comment: Federated MySQL storage engine
Transactions: NULL
          XA: NULL
  Savepoints: NULL
*************************** 2. row ***************************
      Engine: MEMORY
     Support: YES
     Comment: Hash based, stored in memory, useful for temporary tables
Transactions: NO
          XA: NO
  Savepoints: NO
*************************** 3. row ***************************
      Engine: InnoDB
     Support: DEFAULT
     Comment: Supports transactions, row-level locking, and foreign keys
Transactions: YES
          XA: YES
  Savepoints: YES
*************************** 4. row ***************************
      Engine: PERFORMANCE_SCHEMA
     Support: YES
     Comment: Performance Schema
Transactions: NO
          XA: NO
  Savepoints: NO
*************************** 5. row ***************************
      Engine: MyISAM
     Support: YES
     Comment: MyISAM storage engine
Transactions: NO
          XA: NO
  Savepoints: NO
*************************** 6. row ***************************
      Engine: MRG_MYISAM
     Support: YES
     Comment: Collection of identical MyISAM tables
Transactions: NO
          XA: NO
  Savepoints: NO
*************************** 7. row ***************************
      Engine: BLACKHOLE
     Support: YES
     Comment: /dev/null storage engine (anything you write to it disappears)
Transactions: NO
          XA: NO
  Savepoints: NO
*************************** 8. row ***************************
      Engine: CSV
     Support: YES
     Comment: CSV storage engine
Transactions: NO
          XA: NO
  Savepoints: NO
*************************** 9. row ***************************
      Engine: ARCHIVE
     Support: YES
     Comment: Archive storage engine
Transactions: NO
          XA: NO
  Savepoints: NO
9 rows in set (0.00 sec)

ERROR: 
No query specified
```

### InnoDB

> DML操作遵循ACID模型，支持事务。
>
> 行级锁，提高并发访问性能。
>
> 支持外键foreign key约束，保证数据的完整性和正确性。
>
> 
>
> InnoDB每张表都对应这硬盘上的一个xxx.ibd表空间文件，这个文件存储着该表的结构、数据、索引数据。

### MyISAM

> 不支持事务，不支持外键。
>
> 支持表锁，不支持行锁。
>
> 访问速度快。
>
> 
>
> MyISAM表被存储在xxx.MYD、xxx.MYI、xxx.SDI硬盘文件中，SDI存放表结构信息，MYD存放表中数据，MYI存放索引数据。

### Memory

> 数据存放在内存中。
>
> 默认使用hash数据结构索引。
>
> 
>
> Memory表表结构信息被存放在xxx.SDI硬盘文件中。

### 存储引擎选择

> todo 未完成

## 索引

> 优点：提高数据检索效率，降低数据库的IO成本；通过索引列对数据进行排序，降低数据排序的成本，降低CPU的消耗。
>
> 缺点：索引也要占用硬盘存储空间；索引大大提高了数据的检索速度，同时却也降低了表更新速度，对表insert、update、delete时效率降低。

### 索引的数据结构

> B+Tree索引：最常见的索引类型，大部分引擎（InnoDB、MyISAM、Memory）都支持B+Tree索引。
>
> Hash索引：底层数据结构是用Hash表实现，只有精确匹配索引列的查询才有效，不支持范围查询。
>
> R-Tree空间索引：空间索引是MyISAM引擎的一个特殊索引类型，主要用于地理空间数据类型，通常使用较少。
>
> Full-Text全文索引：是一种通过建立倒排索引，快速匹配文档的方式。类似与Lucene、Solr、Elasticsearch。
>
> 
>
> B+Tree索引InnoDB支持、MyISAM支持、Memory支持。
>
> Hash索引InnoDB不支持、MyISAM不支持、Memory支持。
>
> R-Tree空间索引InnoDB不支持、MyISAM支持、Memory不支持。
>
> Full-Text全文索引InnoDB 5.6版本之后支持、MyISAM支持、Memory不支持。
>
> 
>
> MySQL默认使用B+Tree数据结构建立索引

### B+Tree数据结构

> todo 未完成

### 索引的分类

> 主键索引：针对于表中的主键建立索引，默认自动创建并且只能有一个。
>
> 唯一索引：避免同一表中某数据列中的值重复，可以有多个。
>
> 普通索引：快速定位特定列的数据，可以有多个。
>
> 全文索引：全文索引是查找文本中的关键词，而不是比较索引中的值，可以有多个。
>
> 
>
> 在InnoDB存储引擎中，根据索引的存储形式，有可以分为一下两种：
>
> 聚集索引Clustered Index：将数据存储和索引放到了一块，索引结构的叶子节点保存了行数据。必须有而且只有一个（如果存在主键，主键索引就是聚集索引；如果不存在主键，将使用第一个唯一索引作为聚集索引；如果表没有主键或者合适的唯一索引，则InnoDB会自动生成一个rowid作为隐藏的聚集索引）。
>
> 二级索引Secondary Index：将数据与索引分开存储，索引结构的叶子节点关联的是对应的主键。可以存在多个。

### 创建、查看、删除索引

**测试环境准备**

```shell
mysql> CREATE DATABASE IF NOT EXISTS testdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
Query OK, 1 row affected (0.01 sec)

mysql> use testdb;
Database changed

create table if not exists tb_user(
  id bigint primary key auto_increment,
  name varchar(64) not null,
  phone varchar(32) not null,
  email varchar(32) not null,
  profession varchar(32) not null,
  age int not null,
  gender int default 1 not null,
  status char(1) default 0 not null,
  createTime datetime not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

insert into tb_user values
(1,'吕布','17799990000','lvbu666@163.com','软件工程',23,1,'6','2001-02-02 00:00:00'),
(2,'曹操','17799990001','caocao666@qq.com','通讯工程',33,1,'0','2001-03-05 00:00:00'),
(3,'赵云','17799990002','17799990@139.com','英语',34,1,'2','2002-03-02 00:00:00'),
(4,'孙悟空','17799990003','17799990@sina.com','工程造价',54,1,'0','2001-07-02 00:00:00'),
(5,'花木兰','17799990004','19980729@sina.com','软件工程',23,2,'1','2001-04-22 00:00:00'),
(6,'大乔','17799990005','daqiao6666@sina.com','舞蹈',22,2,'0','2001-02-07 00:00:00'),
(7,'露娜','17799990006','luna_love@sina.com','应用数学',24,2,'0','2001-02-08 00:00:00'),
(8,'程咬金','17799990007','chengyaojin@163.com','化工',38,1,'5','2001-05-23 00:00:00'),
(9,'项羽','17799990008','xiangyu666@qq.com','金属材料',43,1,'0','2001-09-18 00:00:00'),
(10,'白起','17799990009','baiqi666@sina.com','机械工程及其自动化',27,1,'2','2001-08-16 00:00:00'),
(11,'韩信','17799990010','hanxin520@163.com','无机非金属材料工程',27,1,'0','2001-06-12 00:00:00'),
(12,'荆柯','17799990011','jingke123@163.com','会计',29,1,'0','2001-05-11 00:00:00'),
(13,'兰陵王','17799990012','lanlinwang666@126.com','工程造价',44,1,'1','2001-04-09 00:00:00'),
(14,'狂铁','17799990013','kuangtie@sina.com','应用数学',43,2,'2','2001-04-11 00:00:00'),
(15,'貂蝉','17799990014','84958948374@qq.com','软件工程',40,2,'3','2001-02-12 00:00:00'),
(16,'坦己','17799990015','2783238293@qq.com','软件工程',31,2,'0','2001-01-30 00:00:00'),
(17,'月丹','17799990016','xiaomin2001@sina.com','工业经济',35,1,'0','2000-05-03 00:00:00'),
(18,'赢政','17799990017','8839434342@qq.com','化工',38,1,'1','2001-08-08 00:00:00'),
(19,'狄仁杰','17799990018','jujiamlm8166@163.com','国际贸易',30,2,'0','2007-03-12 00:00:00'),
(20,'安琪拉','17799990019','jdodm1h@126.com','城市规划',51,2,'0','2001-08-15 00:00:00'),
(21,'典韦','17799990020','ycaunanjian@163.com','城市规划',52,1,'2','2000-04-12 00:00:00'),
(22,'廉颇','17799990021','lianpo321@126.com','土木工程',19,1,'3','2002-07-18 00:00:00'),
(23,'后羿','17799990022','altycj2000@139.com','城市园林',20,1,'0','2002-03-10 00:00:00'),
(24,'姜子牙','17799990023','37483844@qq.com','工程造价',29,1,'4','2003-05-26 00:00:00');
```

```shell
# 查询tb_user有哪些索引
mysql> show index from tb_user\G;
*************************** 1. row ***************************
        Table: tb_user
   Non_unique: 0
     Key_name: PRIMARY
 Seq_in_index: 1
  Column_name: id
    Collation: A
  Cardinality: 24
     Sub_part: NULL
       Packed: NULL
         Null: 
   Index_type: BTREE
      Comment: 
Index_comment: 
      Visible: YES
   Expression: NULL
1 row in set (0.01 sec)

ERROR: 
No query specified

# 给tb_user表的name字段创建索引
mysql> create index idx_tb_user_name on tb_user(name);
Query OK, 0 rows affected (0.02 sec)
Records: 0  Duplicates: 0  Warnings: 0

mysql> show index from tb_user\G;
*************************** 1. row ***************************
        Table: tb_user
   Non_unique: 0
     Key_name: PRIMARY
 Seq_in_index: 1
  Column_name: id
    Collation: A
  Cardinality: 24
     Sub_part: NULL
       Packed: NULL
         Null: 
   Index_type: BTREE
      Comment: 
Index_comment: 
      Visible: YES
   Expression: NULL
*************************** 2. row ***************************
        Table: tb_user
   Non_unique: 1
     Key_name: idx_tb_user_name
 Seq_in_index: 1
  Column_name: name
    Collation: A
  Cardinality: 1
     Sub_part: NULL
       Packed: NULL
         Null: 
   Index_type: BTREE
      Comment: 
Index_comment: 
      Visible: YES
   Expression: NULL
2 rows in set (0.00 sec)

ERROR: 
No query specified

# 给tb_user的phone字段创建唯一索引
mysql> create unique index idx_unique_tb_user_phone on tb_user(phone);
Query OK, 0 rows affected (0.02 sec)
Records: 0  Duplicates: 0  Warnings: 0

mysql> show index from tb_user\G;
*************************** 1. row ***************************
        Table: tb_user
   Non_unique: 0
     Key_name: PRIMARY
 Seq_in_index: 1
  Column_name: id
    Collation: A
  Cardinality: 24
     Sub_part: NULL
       Packed: NULL
         Null: 
   Index_type: BTREE
      Comment: 
Index_comment: 
      Visible: YES
   Expression: NULL
*************************** 2. row ***************************
        Table: tb_user
   Non_unique: 0
     Key_name: idx_unique_tb_user_phone
 Seq_in_index: 1
  Column_name: phone
    Collation: A
  Cardinality: 24
     Sub_part: NULL
       Packed: NULL
         Null: 
   Index_type: BTREE
      Comment: 
Index_comment: 
      Visible: YES
   Expression: NULL
*************************** 3. row ***************************
        Table: tb_user
   Non_unique: 1
     Key_name: idx_tb_user_name
 Seq_in_index: 1
  Column_name: name
    Collation: A
  Cardinality: 1
     Sub_part: NULL
       Packed: NULL
         Null: 
   Index_type: BTREE
      Comment: 
Index_comment: 
      Visible: YES
   Expression: NULL
3 rows in set (0.01 sec)

ERROR: 
No query specified

# 给tb_user的profession、age、status创建联合索引
mysql> create index idx_tb_user_profession_age_status on tb_user(profession,age,status);
Query OK, 0 rows affected (0.02 sec)
Records: 0  Duplicates: 0  Warnings: 0

mysql> show index from tb_user\G;
*************************** 1. row ***************************
        Table: tb_user
   Non_unique: 0
     Key_name: PRIMARY
 Seq_in_index: 1
  Column_name: id
    Collation: A
  Cardinality: 24
     Sub_part: NULL
       Packed: NULL
         Null: 
   Index_type: BTREE
      Comment: 
Index_comment: 
      Visible: YES
   Expression: NULL
*************************** 2. row ***************************
        Table: tb_user
   Non_unique: 0
     Key_name: idx_unique_tb_user_phone
 Seq_in_index: 1
  Column_name: phone
    Collation: A
  Cardinality: 24
     Sub_part: NULL
       Packed: NULL
         Null: 
   Index_type: BTREE
      Comment: 
Index_comment: 
      Visible: YES
   Expression: NULL
*************************** 3. row ***************************
        Table: tb_user
   Non_unique: 1
     Key_name: idx_tb_user_name
 Seq_in_index: 1
  Column_name: name
    Collation: A
  Cardinality: 1
     Sub_part: NULL
       Packed: NULL
         Null: 
   Index_type: BTREE
      Comment: 
Index_comment: 
      Visible: YES
   Expression: NULL
*************************** 4. row ***************************
        Table: tb_user
   Non_unique: 1
     Key_name: idx_tb_user_profession_age_status
 Seq_in_index: 1
  Column_name: profession
    Collation: A
  Cardinality: 1
     Sub_part: NULL
       Packed: NULL
         Null: 
   Index_type: BTREE
      Comment: 
Index_comment: 
      Visible: YES
   Expression: NULL
*************************** 5. row ***************************
        Table: tb_user
   Non_unique: 1
     Key_name: idx_tb_user_profession_age_status
 Seq_in_index: 2
  Column_name: age
    Collation: A
  Cardinality: 19
     Sub_part: NULL
       Packed: NULL
         Null: 
   Index_type: BTREE
      Comment: 
Index_comment: 
      Visible: YES
   Expression: NULL
*************************** 6. row ***************************
        Table: tb_user
   Non_unique: 1
     Key_name: idx_tb_user_profession_age_status
 Seq_in_index: 3
  Column_name: status
    Collation: A
  Cardinality: 24
     Sub_part: NULL
       Packed: NULL
         Null: 
   Index_type: BTREE
      Comment: 
Index_comment: 
      Visible: YES
   Expression: NULL
6 rows in set (0.01 sec)

ERROR: 
No query specified

# 给tb_user的email创建索引
mysql> create index idx_tb_user_email on tb_user(email);
Query OK, 0 rows affected (0.03 sec)
Records: 0  Duplicates: 0  Warnings: 0

mysql> show index from tb_user\G;
*************************** 1. row ***************************
        Table: tb_user
   Non_unique: 0
     Key_name: PRIMARY
 Seq_in_index: 1
  Column_name: id
    Collation: A
  Cardinality: 24
     Sub_part: NULL
       Packed: NULL
         Null: 
   Index_type: BTREE
      Comment: 
Index_comment: 
      Visible: YES
   Expression: NULL
*************************** 2. row ***************************
        Table: tb_user
   Non_unique: 0
     Key_name: idx_unique_tb_user_phone
 Seq_in_index: 1
  Column_name: phone
    Collation: A
  Cardinality: 24
     Sub_part: NULL
       Packed: NULL
         Null: 
   Index_type: BTREE
      Comment: 
Index_comment: 
      Visible: YES
   Expression: NULL
*************************** 3. row ***************************
        Table: tb_user
   Non_unique: 1
     Key_name: idx_tb_user_name
 Seq_in_index: 1
  Column_name: name
    Collation: A
  Cardinality: 1
     Sub_part: NULL
       Packed: NULL
         Null: 
   Index_type: BTREE
      Comment: 
Index_comment: 
      Visible: YES
   Expression: NULL
*************************** 4. row ***************************
        Table: tb_user
   Non_unique: 1
     Key_name: idx_tb_user_profession_age_status
 Seq_in_index: 1
  Column_name: profession
    Collation: A
  Cardinality: 1
     Sub_part: NULL
       Packed: NULL
         Null: 
   Index_type: BTREE
      Comment: 
Index_comment: 
      Visible: YES
   Expression: NULL
*************************** 5. row ***************************
        Table: tb_user
   Non_unique: 1
     Key_name: idx_tb_user_profession_age_status
 Seq_in_index: 2
  Column_name: age
    Collation: A
  Cardinality: 19
     Sub_part: NULL
       Packed: NULL
         Null: 
   Index_type: BTREE
      Comment: 
Index_comment: 
      Visible: YES
   Expression: NULL
*************************** 6. row ***************************
        Table: tb_user
   Non_unique: 1
     Key_name: idx_tb_user_profession_age_status
 Seq_in_index: 3
  Column_name: status
    Collation: A
  Cardinality: 24
     Sub_part: NULL
       Packed: NULL
         Null: 
   Index_type: BTREE
      Comment: 
Index_comment: 
      Visible: YES
   Expression: NULL
*************************** 7. row ***************************
        Table: tb_user
   Non_unique: 1
     Key_name: idx_tb_user_email
 Seq_in_index: 1
  Column_name: email
    Collation: A
  Cardinality: 24
     Sub_part: NULL
       Packed: NULL
         Null: 
   Index_type: BTREE
      Comment: 
Index_comment: 
      Visible: YES
   Expression: NULL
7 rows in set (0.00 sec)

ERROR: 
No query specified

# 删除tb_user的idx_tb_user_email索引
mysql> drop index idx_tb_user_email on tb_user;
Query OK, 0 rows affected (0.01 sec)
Records: 0  Duplicates: 0  Warnings: 0

mysql> show index from tb_user\G;
*************************** 1. row ***************************
        Table: tb_user
   Non_unique: 0
     Key_name: PRIMARY
 Seq_in_index: 1
  Column_name: id
    Collation: A
  Cardinality: 24
     Sub_part: NULL
       Packed: NULL
         Null: 
   Index_type: BTREE
      Comment: 
Index_comment: 
      Visible: YES
   Expression: NULL
*************************** 2. row ***************************
        Table: tb_user
   Non_unique: 0
     Key_name: idx_unique_tb_user_phone
 Seq_in_index: 1
  Column_name: phone
    Collation: A
  Cardinality: 24
     Sub_part: NULL
       Packed: NULL
         Null: 
   Index_type: BTREE
      Comment: 
Index_comment: 
      Visible: YES
   Expression: NULL
*************************** 3. row ***************************
        Table: tb_user
   Non_unique: 1
     Key_name: idx_tb_user_name
 Seq_in_index: 1
  Column_name: name
    Collation: A
  Cardinality: 1
     Sub_part: NULL
       Packed: NULL
         Null: 
   Index_type: BTREE
      Comment: 
Index_comment: 
      Visible: YES
   Expression: NULL
*************************** 4. row ***************************
        Table: tb_user
   Non_unique: 1
     Key_name: idx_tb_user_profession_age_status
 Seq_in_index: 1
  Column_name: profession
    Collation: A
  Cardinality: 1
     Sub_part: NULL
       Packed: NULL
         Null: 
   Index_type: BTREE
      Comment: 
Index_comment: 
      Visible: YES
   Expression: NULL
*************************** 5. row ***************************
        Table: tb_user
   Non_unique: 1
     Key_name: idx_tb_user_profession_age_status
 Seq_in_index: 2
  Column_name: age
    Collation: A
  Cardinality: 19
     Sub_part: NULL
       Packed: NULL
         Null: 
   Index_type: BTREE
      Comment: 
Index_comment: 
      Visible: YES
   Expression: NULL
*************************** 6. row ***************************
        Table: tb_user
   Non_unique: 1
     Key_name: idx_tb_user_profession_age_status
 Seq_in_index: 3
  Column_name: status
    Collation: A
  Cardinality: 24
     Sub_part: NULL
       Packed: NULL
         Null: 
   Index_type: BTREE
      Comment: 
Index_comment: 
      Visible: YES
   Expression: NULL
6 rows in set (0.01 sec)

ERROR: 
No query specified
```

### 索引使用规则最左前缀法制

> 联合索引，要遵守最左前缀法则。最左前缀法则指的是查询从索引的最左列开始，并且不跳过索引中的列。如果跳跃某一列，索引将部分失效（后面的字段索引失效）。

```shell
mysql> CREATE DATABASE IF NOT EXISTS testdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
Query OK, 1 row affected (0.01 sec)

mysql> use testdb;
Database changed

create table if not exists tb_user(
  id bigint primary key auto_increment,
  name varchar(64) not null,
  phone varchar(32) not null,
  email varchar(32) not null,
  profession varchar(32) not null,
  age int not null,
  gender int default 1 not null,
  status char(1) default 0 not null,
  createTime datetime not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

insert into tb_user values
(1,'吕布','17799990000','lvbu666@163.com','软件工程',23,1,'6','2001-02-02 00:00:00'),
(2,'曹操','17799990001','caocao666@qq.com','通讯工程',33,1,'0','2001-03-05 00:00:00'),
(3,'赵云','17799990002','17799990@139.com','英语',34,1,'2','2002-03-02 00:00:00'),
(4,'孙悟空','17799990003','17799990@sina.com','工程造价',54,1,'0','2001-07-02 00:00:00'),
(5,'花木兰','17799990004','19980729@sina.com','软件工程',23,2,'1','2001-04-22 00:00:00'),
(6,'大乔','17799990005','daqiao6666@sina.com','舞蹈',22,2,'0','2001-02-07 00:00:00'),
(7,'露娜','17799990006','luna_love@sina.com','应用数学',24,2,'0','2001-02-08 00:00:00'),
(8,'程咬金','17799990007','chengyaojin@163.com','化工',38,1,'5','2001-05-23 00:00:00'),
(9,'项羽','17799990008','xiangyu666@qq.com','金属材料',43,1,'0','2001-09-18 00:00:00'),
(10,'白起','17799990009','baiqi666@sina.com','机械工程及其自动化',27,1,'2','2001-08-16 00:00:00'),
(11,'韩信','17799990010','hanxin520@163.com','无机非金属材料工程',27,1,'0','2001-06-12 00:00:00'),
(12,'荆柯','17799990011','jingke123@163.com','会计',29,1,'0','2001-05-11 00:00:00'),
(13,'兰陵王','17799990012','lanlinwang666@126.com','工程造价',44,1,'1','2001-04-09 00:00:00'),
(14,'狂铁','17799990013','kuangtie@sina.com','应用数学',43,2,'2','2001-04-11 00:00:00'),
(15,'貂蝉','17799990014','84958948374@qq.com','软件工程',40,2,'3','2001-02-12 00:00:00'),
(16,'坦己','17799990015','2783238293@qq.com','软件工程',31,2,'0','2001-01-30 00:00:00'),
(17,'月丹','17799990016','xiaomin2001@sina.com','工业经济',35,1,'0','2000-05-03 00:00:00'),
(18,'赢政','17799990017','8839434342@qq.com','化工',38,1,'1','2001-08-08 00:00:00'),
(19,'狄仁杰','17799990018','jujiamlm8166@163.com','国际贸易',30,2,'0','2007-03-12 00:00:00'),
(20,'安琪拉','17799990019','jdodm1h@126.com','城市规划',51,2,'0','2001-08-15 00:00:00'),
(21,'典韦','17799990020','ycaunanjian@163.com','城市规划',52,1,'2','2000-04-12 00:00:00'),
(22,'廉颇','17799990021','lianpo321@126.com','土木工程',19,1,'3','2002-07-18 00:00:00'),
(23,'后羿','17799990022','altycj2000@139.com','城市园林',20,1,'0','2002-03-10 00:00:00'),
(24,'姜子牙','17799990023','37483844@qq.com','工程造价',29,1,'4','2003-05-26 00:00:00');

mysql> create index idx_tb_user_profession_age_status on tb_user(profession,age,status);
Query OK, 0 rows affected (0.03 sec)
Records: 0  Duplicates: 0  Warnings: 0

# profession、age、status使用了索引，全部索引
mysql> explain select * from tb_user where profession='软件工程' and age=31 and status='0'\G;
*************************** 1. row ***************************
           id: 1
  select_type: SIMPLE
        table: tb_user
   partitions: NULL
         type: ref
possible_keys: idx_tb_user_profession_age_status
          key: idx_tb_user_profession_age_status
      key_len: 138
          ref: const,const,const
         rows: 1
     filtered: 100.00
        Extra: NULL
1 row in set, 1 warning (0.00 sec)

ERROR: 
No query specified

# profession、age使用了索引，部分索引
mysql> explain select * from tb_user where profession='软件工程' and age=31\G;
*************************** 1. row ***************************
           id: 1
  select_type: SIMPLE
        table: tb_user
   partitions: NULL
         type: ref
possible_keys: idx_tb_user_profession_age_status
          key: idx_tb_user_profession_age_status
      key_len: 134
          ref: const,const
         rows: 1
     filtered: 100.00
        Extra: NULL
1 row in set, 1 warning (0.00 sec)

ERROR: 
No query specified

# profession使用了索引，部分索引
mysql> explain select * from tb_user where profession='软件工程'\G;
*************************** 1. row ***************************
           id: 1
  select_type: SIMPLE
        table: tb_user
   partitions: NULL
         type: ref
possible_keys: idx_tb_user_profession_age_status
          key: idx_tb_user_profession_age_status
      key_len: 130
          ref: const
         rows: 4
     filtered: 100.00
        Extra: NULL
1 row in set, 1 warning (0.00 sec)

ERROR: 
No query specified

# 使用全表扫描，因为违背最左前缀法则没有索引可用
mysql> explain select * from tb_user where age=31 and status='0'\G;
*************************** 1. row ***************************
           id: 1
  select_type: SIMPLE
        table: tb_user
   partitions: NULL
         type: ALL
possible_keys: NULL
          key: NULL
      key_len: NULL
          ref: NULL
         rows: 24
     filtered: 4.17
        Extra: Using where
1 row in set, 1 warning (0.00 sec)

ERROR: 
No query specified
mysql> explain select * from tb_user where status='0'\G;
*************************** 1. row ***************************
           id: 1
  select_type: SIMPLE
        table: tb_user
   partitions: NULL
         type: ALL
possible_keys: NULL
          key: NULL
      key_len: NULL
          ref: NULL
         rows: 24
     filtered: 10.00
        Extra: Using where
1 row in set, 1 warning (0.00 sec)

ERROR: 
No query specified

# 只有profession使用了索引，status并未使用索引，部分索引
mysql> explain select * from tb_user where profession='软件工程' and status='0'\G;
*************************** 1. row ***************************
           id: 1
  select_type: SIMPLE
        table: tb_user
   partitions: NULL
         type: ref
possible_keys: idx_tb_user_profession_age_status
          key: idx_tb_user_profession_age_status
      key_len: 130
          ref: const
         rows: 4
     filtered: 10.00
        Extra: Using index condition
1 row in set, 1 warning (0.00 sec)

ERROR: 
No query specified
```

### 索引失效情况

```shell
mysql> CREATE DATABASE IF NOT EXISTS testdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
Query OK, 1 row affected (0.01 sec)

mysql> use testdb;
Database changed

create table if not exists tb_user(
  id bigint primary key auto_increment,
  name varchar(64) not null,
  phone varchar(32) not null,
  email varchar(32) not null,
  profession varchar(32) not null,
  age int not null,
  gender int default 1 not null,
  status char(1) default 0 not null,
  createTime datetime not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

insert into tb_user values
(1,'吕布','17799990000','lvbu666@163.com','软件工程',23,1,'6','2001-02-02 00:00:00'),
(2,'曹操','17799990001','caocao666@qq.com','通讯工程',33,1,'0','2001-03-05 00:00:00'),
(3,'赵云','17799990002','17799990@139.com','英语',34,1,'2','2002-03-02 00:00:00'),
(4,'孙悟空','17799990003','17799990@sina.com','工程造价',54,1,'0','2001-07-02 00:00:00'),
(5,'花木兰','17799990004','19980729@sina.com','软件工程',23,2,'1','2001-04-22 00:00:00'),
(6,'大乔','17799990005','daqiao6666@sina.com','舞蹈',22,2,'0','2001-02-07 00:00:00'),
(7,'露娜','17799990006','luna_love@sina.com','应用数学',24,2,'0','2001-02-08 00:00:00'),
(8,'程咬金','17799990007','chengyaojin@163.com','化工',38,1,'5','2001-05-23 00:00:00'),
(9,'项羽','17799990008','xiangyu666@qq.com','金属材料',43,1,'0','2001-09-18 00:00:00'),
(10,'白起','17799990009','baiqi666@sina.com','机械工程及其自动化',27,1,'2','2001-08-16 00:00:00'),
(11,'韩信','17799990010','hanxin520@163.com','无机非金属材料工程',27,1,'0','2001-06-12 00:00:00'),
(12,'荆柯','17799990011','jingke123@163.com','会计',29,1,'0','2001-05-11 00:00:00'),
(13,'兰陵王','17799990012','lanlinwang666@126.com','工程造价',44,1,'1','2001-04-09 00:00:00'),
(14,'狂铁','17799990013','kuangtie@sina.com','应用数学',43,2,'2','2001-04-11 00:00:00'),
(15,'貂蝉','17799990014','84958948374@qq.com','软件工程',40,2,'3','2001-02-12 00:00:00'),
(16,'坦己','17799990015','2783238293@qq.com','软件工程',31,2,'0','2001-01-30 00:00:00'),
(17,'月丹','17799990016','xiaomin2001@sina.com','工业经济',35,1,'0','2000-05-03 00:00:00'),
(18,'赢政','17799990017','8839434342@qq.com','化工',38,1,'1','2001-08-08 00:00:00'),
(19,'狄仁杰','17799990018','jujiamlm8166@163.com','国际贸易',30,2,'0','2007-03-12 00:00:00'),
(20,'安琪拉','17799990019','jdodm1h@126.com','城市规划',51,2,'0','2001-08-15 00:00:00'),
(21,'典韦','17799990020','ycaunanjian@163.com','城市规划',52,1,'2','2000-04-12 00:00:00'),
(22,'廉颇','17799990021','lianpo321@126.com','土木工程',19,1,'3','2002-07-18 00:00:00'),
(23,'后羿','17799990022','altycj2000@139.com','城市园林',20,1,'0','2002-03-10 00:00:00'),
(24,'姜子牙','17799990023','37483844@qq.com','工程造价',29,1,'4','2003-05-26 00:00:00');

mysql> create index idx_tb_user_profession_age_status on tb_user(profession,age,status);
Query OK, 0 rows affected (0.03 sec)
Records: 0  Duplicates: 0  Warnings: 0
mysql> create index idx_tb_user_phone on tb_user(phone);
Query OK, 0 rows affected (0.03 sec)
Records: 0  Duplicates: 0  Warnings: 0

# 情况1：联合索引中，出现范围查询（>，<)，范围查询右侧的列索引失效
# 通过key_len判断出下面查询只是哟个profession和age部分索引，原因是age>31导致status='0'索引失效
mysql> explain select * from tb_user where profession='软件工程' and age>31 and status='0'\G;
*************************** 1. row ***************************
           id: 1
  select_type: SIMPLE
        table: tb_user
   partitions: NULL
         type: range
possible_keys: idx_tb_user_profession_age_status
          key: idx_tb_user_profession_age_status
      key_len: 134
          ref: NULL
         rows: 1
     filtered: 10.00
        Extra: Using index condition
1 row in set, 1 warning (0.01 sec)

ERROR: 
No query specified
# 针对情况1索引失效解决办法是>，<修改为>=,<=
mysql> explain select * from tb_user where profession='软件工程' and age>=31 and status='0'\G;
*************************** 1. row ***************************
           id: 1
  select_type: SIMPLE
        table: tb_user
   partitions: NULL
         type: range
possible_keys: idx_tb_user_profession_age_status
          key: idx_tb_user_profession_age_status
      key_len: 138
          ref: NULL
         rows: 2
     filtered: 10.00
        Extra: Using index condition
1 row in set, 1 warning (0.00 sec)

ERROR: 
No query specified

# 情况2：不要在索引列上进行运算操作，否则索引失效
# 正常查询使用idx_tb_user_phone索引
mysql> explain select * from tb_user where phone='17799990015';
+----+-------------+---------+------------+------+-------------------+-------------------+---------+-------+------+----------+-------+
| id | select_type | table   | partitions | type | possible_keys     | key               | key_len | ref   | rows | filtered | Extra |
+----+-------------+---------+------------+------+-------------------+-------------------+---------+-------+------+----------+-------+
|  1 | SIMPLE      | tb_user | NULL       | ref  | idx_tb_user_phone | idx_tb_user_phone | 130     | const |    1 |   100.00 | NULL  |
+----+-------------+---------+------------+------+-------------------+-------------------+---------+-------+------+----------+-------+
1 row in set, 1 warning (0.00 sec)
# 在phone索引列上进行运算操作导致索引idx_tb_user_phone失效导致全部扫描
mysql> explain select * from tb_user where substring(phone,10,2)='15';
+----+-------------+---------+------------+------+---------------+------+---------+------+------+----------+-------------+
| id | select_type | table   | partitions | type | possible_keys | key  | key_len | ref  | rows | filtered | Extra       |
+----+-------------+---------+------------+------+---------------+------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | tb_user | NULL       | ALL  | NULL          | NULL | NULL    | NULL |   24 |   100.00 | Using where |
+----+-------------+---------+------------+------+---------------+------+---------+------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)

# 情况3：字符串类型字段查询时，不加引号，索引失效
mysql> explain select * from tb_user where phone=17799990015;
+----+-------------+---------+------------+------+-------------------+------+---------+------+------+----------+-------------+
| id | select_type | table   | partitions | type | possible_keys     | key  | key_len | ref  | rows | filtered | Extra       |
+----+-------------+---------+------------+------+-------------------+------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | tb_user | NULL       | ALL  | idx_tb_user_phone | NULL | NULL    | NULL |   24 |    10.00 | Using where |
+----+-------------+---------+------------+------+-------------------+------+---------+------+------+----------+-------------+
1 row in set, 3 warnings (0.00 sec)
mysql> explain select * from tb_user where profession='软件工程' and age=31 and status=0;
+----+-------------+---------+------------+------+-----------------------------------+-----------------------------------+---------+-------------+------+----------+-----------------------+
| id | select_type | table   | partitions | type | possible_keys                     | key                               | key_len | ref         | rows | filtered | Extra                 |
+----+-------------+---------+------------+------+-----------------------------------+-----------------------------------+---------+-------------+------+----------+-----------------------+
|  1 | SIMPLE      | tb_user | NULL       | ref  | idx_tb_user_profession_age_status | idx_tb_user_profession_age_status | 134     | const,const |    1 |    10.00 | Using index condition |
+----+-------------+---------+------------+------+-----------------------------------+-----------------------------------+---------+-------------+------+----------+-----------------------+
1 row in set, 2 warnings (0.00 sec)

# 情况4：模糊匹配查询中，尾部模糊匹配索引正常，头部模糊匹配索引失效
mysql> explain select * from tb_user where profession like '软件%';
+----+-------------+---------+------------+-------+-----------------------------------+-----------------------------------+---------+------+------+----------+-----------------------+
| id | select_type | table   | partitions | type  | possible_keys                     | key                               | key_len | ref  | rows | filtered | Extra                 |
+----+-------------+---------+------------+-------+-----------------------------------+-----------------------------------+---------+------+------+----------+-----------------------+
|  1 | SIMPLE      | tb_user | NULL       | range | idx_tb_user_profession_age_status | idx_tb_user_profession_age_status | 130     | NULL |    4 |   100.00 | Using index condition |
+----+-------------+---------+------------+-------+-----------------------------------+-----------------------------------+---------+------+------+----------+-----------------------+
1 row in set, 1 warning (0.00 sec)
mysql> explain select * from tb_user where profession like '%软件';
+----+-------------+---------+------------+------+---------------+------+---------+------+------+----------+-------------+
| id | select_type | table   | partitions | type | possible_keys | key  | key_len | ref  | rows | filtered | Extra       |
+----+-------------+---------+------------+------+---------------+------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | tb_user | NULL       | ALL  | NULL          | NULL | NULL    | NULL |   24 |    11.11 | Using where |
+----+-------------+---------+------------+------+---------------+------+---------+------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)

# 情况5：用or分割开的条件，如果or中任何一个查询条件没有索引，那么查询中所有索引都失效
# id=10索引失效，因为age=23没有索引
mysql> explain select * from tb_user where id=10 or age=23;
+----+-------------+---------+------------+------+---------------+------+---------+------+------+----------+-------------+
| id | select_type | table   | partitions | type | possible_keys | key  | key_len | ref  | rows | filtered | Extra       |
+----+-------------+---------+------------+------+---------------+------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | tb_user | NULL       | ALL  | PRIMARY       | NULL | NULL    | NULL |   24 |    13.75 | Using where |
+----+-------------+---------+------------+------+---------------+------+---------+------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)
# phone='xxx'索引失效，因为age=23没有索引
mysql> explain select * from tb_user where phone='17799990017' or age=23;
+----+-------------+---------+------------+------+-------------------+------+---------+------+------+----------+-------------+
| id | select_type | table   | partitions | type | possible_keys     | key  | key_len | ref  | rows | filtered | Extra       |
+----+-------------+---------+------------+------+-------------------+------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | tb_user | NULL       | ALL  | idx_tb_user_phone | NULL | NULL    | NULL |   24 |    19.00 | Using where |
+----+-------------+---------+------------+------+-------------------+------+---------+------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)
# 为age创建索引后所有索引随之生效
mysql> create index idx_tb_user_age on tb_user(age);
Query OK, 0 rows affected (0.04 sec)
Records: 0  Duplicates: 0  Warnings: 0

mysql> explain select * from tb_user where id=10 or age=23;
+----+-------------+---------+------------+-------------+-------------------------+-------------------------+---------+------+------+----------+---------------------------------------------------+
| id | select_type | table   | partitions | type        | possible_keys           | key                     | key_len | ref  | rows | filtered | Extra                                             |
+----+-------------+---------+------------+-------------+-------------------------+-------------------------+---------+------+------+----------+---------------------------------------------------+
|  1 | SIMPLE      | tb_user | NULL       | index_merge | PRIMARY,idx_tb_user_age | PRIMARY,idx_tb_user_age | 8,4     | NULL |    3 |   100.00 | Using union(PRIMARY,idx_tb_user_age); Using where |
+----+-------------+---------+------------+-------------+-------------------------+-------------------------+---------+------+------+----------+---------------------------------------------------+
1 row in set, 1 warning (0.00 sec)

mysql> explain select * from tb_user where phone='17799990017' or age=23;
+----+-------------+---------+------------+-------------+-----------------------------------+-----------------------------------+---------+------+------+----------+-------------------------------------------------------------+
| id | select_type | table   | partitions | type        | possible_keys                     | key                               | key_len | ref  | rows | filtered | Extra                                                       |
+----+-------------+---------+------------+-------------+-----------------------------------+-----------------------------------+---------+------+------+----------+-------------------------------------------------------------+
|  1 | SIMPLE      | tb_user | NULL       | index_merge | idx_tb_user_phone,idx_tb_user_age | idx_tb_user_phone,idx_tb_user_age | 130,4   | NULL |    3 |   100.00 | Using union(idx_tb_user_phone,idx_tb_user_age); Using where |
+----+-------------+---------+------------+-------------+-----------------------------------+-----------------------------------+---------+------+------+----------+-------------------------------------------------------------+
1 row in set, 1 warning (0.00 sec)

# 情况6：数据分布的影响，如果MySQL评估使用索引比全表扫描更慢，则不使用索引
# 不使用索引，因为大部分数据都phone>='17799990005'
mysql> explain select * from tb_user where phone>='17799990005';
+----+-------------+---------+------------+------+-------------------+------+---------+------+------+----------+-------------+
| id | select_type | table   | partitions | type | possible_keys     | key  | key_len | ref  | rows | filtered | Extra       |
+----+-------------+---------+------------+------+-------------------+------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | tb_user | NULL       | ALL  | idx_tb_user_phone | NULL | NULL    | NULL |   24 |    79.17 | Using where |
+----+-------------+---------+------------+------+-------------------+------+---------+------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)
# 使用索引，因为大部分数据都phone<'17799990015'
mysql> explain select * from tb_user where phone>='17799990015';
+----+-------------+---------+------------+-------+-------------------+-------------------+---------+------+------+----------+-----------------------+
| id | select_type | table   | partitions | type  | possible_keys     | key               | key_len | ref  | rows | filtered | Extra                 |
+----+-------------+---------+------------+-------+-------------------+-------------------+---------+------+------+----------+-----------------------+
|  1 | SIMPLE      | tb_user | NULL       | range | idx_tb_user_phone | idx_tb_user_phone | 130     | NULL |    9 |   100.00 | Using index condition |
+----+-------------+---------+------------+-------+-------------------+-------------------+---------+------+------+----------+-----------------------+
1 row in set, 1 warning (0.00 sec)
# 使用索引，因为没有数据profession is null
mysql> explain select * from tb_user where profession is null;
+----+-------------+-------+------------+------+---------------+------+---------+------+------+----------+------------------+
| id | select_type | table | partitions | type | possible_keys | key  | key_len | ref  | rows | filtered | Extra            |
+----+-------------+-------+------------+------+---------------+------+---------+------+------+----------+------------------+
|  1 | SIMPLE      | NULL  | NULL       | NULL | NULL          | NULL | NULL    | NULL | NULL |     NULL | Impossible WHERE |
+----+-------------+-------+------------+------+---------------+------+---------+------+------+----------+------------------+
1 row in set, 1 warning (0.00 sec)
# 不使用索引，因为全部数据都profession is not null
mysql> explain select * from tb_user where profession is not null;
+----+-------------+---------+------------+------+---------------+------+---------+------+------+----------+-------+
| id | select_type | table   | partitions | type | possible_keys | key  | key_len | ref  | rows | filtered | Extra |
+----+-------------+---------+------------+------+---------------+------+---------+------+------+----------+-------+
|  1 | SIMPLE      | tb_user | NULL       | ALL  | NULL          | NULL | NULL    | NULL |   24 |   100.00 | NULL  |
+----+-------------+---------+------------+------+---------------+------+---------+------+------+----------+-------+
1 row in set, 1 warning (0.00 sec)
```

### SQL提示use、ignore、force索引

```shell
mysql> CREATE DATABASE IF NOT EXISTS testdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
Query OK, 1 row affected (0.01 sec)

mysql> use testdb;
Database changed

create table if not exists tb_user(
  id bigint primary key auto_increment,
  name varchar(64) not null,
  phone varchar(32) not null,
  email varchar(32) not null,
  profession varchar(32) not null,
  age int not null,
  gender int default 1 not null,
  status char(1) default 0 not null,
  createTime datetime not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

insert into tb_user values
(1,'吕布','17799990000','lvbu666@163.com','软件工程',23,1,'6','2001-02-02 00:00:00'),
(2,'曹操','17799990001','caocao666@qq.com','通讯工程',33,1,'0','2001-03-05 00:00:00'),
(3,'赵云','17799990002','17799990@139.com','英语',34,1,'2','2002-03-02 00:00:00'),
(4,'孙悟空','17799990003','17799990@sina.com','工程造价',54,1,'0','2001-07-02 00:00:00'),
(5,'花木兰','17799990004','19980729@sina.com','软件工程',23,2,'1','2001-04-22 00:00:00'),
(6,'大乔','17799990005','daqiao6666@sina.com','舞蹈',22,2,'0','2001-02-07 00:00:00'),
(7,'露娜','17799990006','luna_love@sina.com','应用数学',24,2,'0','2001-02-08 00:00:00'),
(8,'程咬金','17799990007','chengyaojin@163.com','化工',38,1,'5','2001-05-23 00:00:00'),
(9,'项羽','17799990008','xiangyu666@qq.com','金属材料',43,1,'0','2001-09-18 00:00:00'),
(10,'白起','17799990009','baiqi666@sina.com','机械工程及其自动化',27,1,'2','2001-08-16 00:00:00'),
(11,'韩信','17799990010','hanxin520@163.com','无机非金属材料工程',27,1,'0','2001-06-12 00:00:00'),
(12,'荆柯','17799990011','jingke123@163.com','会计',29,1,'0','2001-05-11 00:00:00'),
(13,'兰陵王','17799990012','lanlinwang666@126.com','工程造价',44,1,'1','2001-04-09 00:00:00'),
(14,'狂铁','17799990013','kuangtie@sina.com','应用数学',43,2,'2','2001-04-11 00:00:00'),
(15,'貂蝉','17799990014','84958948374@qq.com','软件工程',40,2,'3','2001-02-12 00:00:00'),
(16,'坦己','17799990015','2783238293@qq.com','软件工程',31,2,'0','2001-01-30 00:00:00'),
(17,'月丹','17799990016','xiaomin2001@sina.com','工业经济',35,1,'0','2000-05-03 00:00:00'),
(18,'赢政','17799990017','8839434342@qq.com','化工',38,1,'1','2001-08-08 00:00:00'),
(19,'狄仁杰','17799990018','jujiamlm8166@163.com','国际贸易',30,2,'0','2007-03-12 00:00:00'),
(20,'安琪拉','17799990019','jdodm1h@126.com','城市规划',51,2,'0','2001-08-15 00:00:00'),
(21,'典韦','17799990020','ycaunanjian@163.com','城市规划',52,1,'2','2000-04-12 00:00:00'),
(22,'廉颇','17799990021','lianpo321@126.com','土木工程',19,1,'3','2002-07-18 00:00:00'),
(23,'后羿','17799990022','altycj2000@139.com','城市园林',20,1,'0','2002-03-10 00:00:00'),
(24,'姜子牙','17799990023','37483844@qq.com','工程造价',29,1,'4','2003-05-26 00:00:00');

mysql> create index idx_tb_user_profession_age_status on tb_user(profession,age,status);
Query OK, 0 rows affected (0.03 sec)
Records: 0  Duplicates: 0  Warnings: 0
mysql> create index idx_tb_user_phone on tb_user(phone);
Query OK, 0 rows affected (0.03 sec)
Records: 0  Duplicates: 0  Warnings: 0
mysql> create index idx_tb_user_profession on tb_user(profession);
Query OK, 0 rows affected (0.04 sec)
Records: 0  Duplicates: 0  Warnings: 0

# MySQL默认使用idx_tb_user_profession_age_status索引
mysql> explain select * from tb_user where profession='软件工程';
+----+-------------+---------+------------+------+----------------------------------------------------------+-----------------------------------+---------+-------+------+----------+-------+
| id | select_type | table   | partitions | type | possible_keys                                            | key                               | key_len | ref   | rows | filtered | Extra |
+----+-------------+---------+------------+------+----------------------------------------------------------+-----------------------------------+---------+-------+------+----------+-------+
|  1 | SIMPLE      | tb_user | NULL       | ref  | idx_tb_user_profession_age_status,idx_tb_user_profession | idx_tb_user_profession_age_status | 130     | const |    4 |   100.00 | NULL  |
+----+-------------+---------+------------+------+----------------------------------------------------------+-----------------------------------+---------+-------+------+----------+-------+
1 row in set, 1 warning (0.00 sec)

# 建议MySQL使用idx_tb_user_profession索引，但是查询不一定使用建议的索引
mysql> explain select * from tb_user use index(idx_tb_user_profession) where profession='软件工程';
+----+-------------+---------+------------+------+------------------------+------------------------+---------+-------+------+----------+-------+
| id | select_type | table   | partitions | type | possible_keys          | key                    | key_len | ref   | rows | filtered | Extra |
+----+-------------+---------+------------+------+------------------------+------------------------+---------+-------+------+----------+-------+
|  1 | SIMPLE      | tb_user | NULL       | ref  | idx_tb_user_profession | idx_tb_user_profession | 130     | const |    4 |   100.00 | NULL  |
+----+-------------+---------+------------+------+------------------------+------------------------+---------+-------+------+----------+-------+
1 row in set, 1 warning (0.01 sec)

# 忽略idx_tb_user_profession_age_status索引，最后执行只能使用idx_tb_user_profession索引
mysql> explain select * from tb_user ignore index(idx_tb_user_profession_age_status) where profession='软件工程';
+----+-------------+---------+------------+------+------------------------+------------------------+---------+-------+------+----------+-------+
| id | select_type | table   | partitions | type | possible_keys          | key                    | key_len | ref   | rows | filtered | Extra |
+----+-------------+---------+------------+------+------------------------+------------------------+---------+-------+------+----------+-------+
|  1 | SIMPLE      | tb_user | NULL       | ref  | idx_tb_user_profession | idx_tb_user_profession | 130     | const |    4 |   100.00 | NULL  |
+----+-------------+---------+------------+------+------------------------+------------------------+---------+-------+------+----------+-------+
1 row in set, 1 warning (0.00 sec)

# 强制使用idx_tb_user_profession索引
mysql> explain select * from tb_user force index(idx_tb_user_profession) where profession='软件工程';
+----+-------------+---------+------------+------+------------------------+------------------------+---------+-------+------+----------+-------+
| id | select_type | table   | partitions | type | possible_keys          | key                    | key_len | ref   | rows | filtered | Extra |
+----+-------------+---------+------------+------+------------------------+------------------------+---------+-------+------+----------+-------+
|  1 | SIMPLE      | tb_user | NULL       | ref  | idx_tb_user_profession | idx_tb_user_profession | 130     | const |    4 |   100.00 | NULL  |
+----+-------------+---------+------------+------+------------------------+------------------------+---------+-------+------+----------+-------+
1 row in set, 1 warning (0.01 sec)
```

### 覆盖索引&回表查询

> 尽量使用覆盖索引（查询使用了索引，并且需要返回的列数据，在索引中已经全部找到不需要回表查询），减少 select.*。
>
> using index condition：查找使用了索引，但是需要回表查询数据。
>
> using where; using index：查找使用了索引，但是需要的数据都在索引列中能够找到，所以不需要回表查询数据。

```shell
mysql> CREATE DATABASE IF NOT EXISTS testdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
Query OK, 1 row affected (0.01 sec)

mysql> use testdb;
Database changed

create table if not exists tb_user(
  id bigint primary key auto_increment,
  name varchar(64) not null,
  phone varchar(32) not null,
  email varchar(32) not null,
  profession varchar(32) not null,
  age int not null,
  gender int default 1 not null,
  status char(1) default 0 not null,
  createTime datetime not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

insert into tb_user values
(1,'吕布','17799990000','lvbu666@163.com','软件工程',23,1,'6','2001-02-02 00:00:00'),
(2,'曹操','17799990001','caocao666@qq.com','通讯工程',33,1,'0','2001-03-05 00:00:00'),
(3,'赵云','17799990002','17799990@139.com','英语',34,1,'2','2002-03-02 00:00:00'),
(4,'孙悟空','17799990003','17799990@sina.com','工程造价',54,1,'0','2001-07-02 00:00:00'),
(5,'花木兰','17799990004','19980729@sina.com','软件工程',23,2,'1','2001-04-22 00:00:00'),
(6,'大乔','17799990005','daqiao6666@sina.com','舞蹈',22,2,'0','2001-02-07 00:00:00'),
(7,'露娜','17799990006','luna_love@sina.com','应用数学',24,2,'0','2001-02-08 00:00:00'),
(8,'程咬金','17799990007','chengyaojin@163.com','化工',38,1,'5','2001-05-23 00:00:00'),
(9,'项羽','17799990008','xiangyu666@qq.com','金属材料',43,1,'0','2001-09-18 00:00:00'),
(10,'白起','17799990009','baiqi666@sina.com','机械工程及其自动化',27,1,'2','2001-08-16 00:00:00'),
(11,'韩信','17799990010','hanxin520@163.com','无机非金属材料工程',27,1,'0','2001-06-12 00:00:00'),
(12,'荆柯','17799990011','jingke123@163.com','会计',29,1,'0','2001-05-11 00:00:00'),
(13,'兰陵王','17799990012','lanlinwang666@126.com','工程造价',44,1,'1','2001-04-09 00:00:00'),
(14,'狂铁','17799990013','kuangtie@sina.com','应用数学',43,2,'2','2001-04-11 00:00:00'),
(15,'貂蝉','17799990014','84958948374@qq.com','软件工程',40,2,'3','2001-02-12 00:00:00'),
(16,'坦己','17799990015','2783238293@qq.com','软件工程',31,2,'0','2001-01-30 00:00:00'),
(17,'月丹','17799990016','xiaomin2001@sina.com','工业经济',35,1,'0','2000-05-03 00:00:00'),
(18,'赢政','17799990017','8839434342@qq.com','化工',38,1,'1','2001-08-08 00:00:00'),
(19,'狄仁杰','17799990018','jujiamlm8166@163.com','国际贸易',30,2,'0','2007-03-12 00:00:00'),
(20,'安琪拉','17799990019','jdodm1h@126.com','城市规划',51,2,'0','2001-08-15 00:00:00'),
(21,'典韦','17799990020','ycaunanjian@163.com','城市规划',52,1,'2','2000-04-12 00:00:00'),
(22,'廉颇','17799990021','lianpo321@126.com','土木工程',19,1,'3','2002-07-18 00:00:00'),
(23,'后羿','17799990022','altycj2000@139.com','城市园林',20,1,'0','2002-03-10 00:00:00'),
(24,'姜子牙','17799990023','37483844@qq.com','工程造价',29,1,'4','2003-05-26 00:00:00');

mysql> create index idx_tb_user_profession_age_status on tb_user(profession,age,status);
Query OK, 0 rows affected (0.03 sec)
Records: 0  Duplicates: 0  Warnings: 0

# Extra=Using index表示不需要回表查询
mysql> explain select id,profession,age,status from tb_user where profession='软件工程' and age=31 and status='0';
+----+-------------+---------+------------+------+--------------------------------------------------------------------------+-----------------------------------+---------+-------------------+------+----------+-------------+
| id | select_type | table   | partitions | type | possible_keys                                                            | key                               | key_len | ref               | rows | filtered | Extra       |
+----+-------------+---------+------------+------+--------------------------------------------------------------------------+-----------------------------------+---------+-------------------+------+----------+-------------+
|  1 | SIMPLE      | tb_user | NULL       | ref  | idx_tb_user_profession_age_status,idx_tb_user_age,idx_tb_user_profession | idx_tb_user_profession_age_status | 138     | const,const,const |    1 |   100.00 | Using index |
+----+-------------+---------+------------+------+--------------------------------------------------------------------------+-----------------------------------+---------+-------------------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)

# Extra=NULL表示需要回表查询
mysql> explain select id,profession,age,status,name from tb_user where profession='软件工程' and age=31 and status='0';
+----+-------------+---------+------------+------+--------------------------------------------------------------------------+-----------------------------------+---------+-------------------+------+----------+-------+
| id | select_type | table   | partitions | type | possible_keys                                                            | key                               | key_len | ref               | rows | filtered | Extra |
+----+-------------+---------+------------+------+--------------------------------------------------------------------------+-----------------------------------+---------+-------------------+------+----------+-------+
|  1 | SIMPLE      | tb_user | NULL       | ref  | idx_tb_user_profession_age_status,idx_tb_user_age,idx_tb_user_profession | idx_tb_user_profession_age_status | 138     | const,const,const |    1 |   100.00 | NULL  |
+----+-------------+---------+------------+------+--------------------------------------------------------------------------+-----------------------------------+---------+-------------------+------+----------+-------+
1 row in set, 1 warning (0.01 sec)

mysql> explain select * from tb_user where profession='软件工程' and age=31 and status='0';
+----+-------------+---------+------------+------+--------------------------------------------------------------------------+-----------------------------------+---------+-------------------+------+----------+-------+
| id | select_type | table   | partitions | type | possible_keys                                                            | key                               | key_len | ref               | rows | filtered | Extra |
+----+-------------+---------+------------+------+--------------------------------------------------------------------------+-----------------------------------+---------+-------------------+------+----------+-------+
|  1 | SIMPLE      | tb_user | NULL       | ref  | idx_tb_user_profession_age_status,idx_tb_user_age,idx_tb_user_profession | idx_tb_user_profession_age_status | 138     | const,const,const |    1 |   100.00 | NULL  |
+----+-------------+---------+------------+------+--------------------------------------------------------------------------+-----------------------------------+---------+-------------------+------+----------+-------+
1 row in set, 1 warning (0.00 sec)
```

## SQL优化

### 主键优化

> 满足业务需求的情况下，尽量降低主键的长度。
>
> 插入数据是尽量选择顺序插入，选择使用auto_increment自增主键。
>
> 尽量不要使用UUID做主键或者其他自然逐渐，如身份证号。
>
> 业务操作时，避免对主键的修改。

### order by优化

> using filesort: 通过表的索引或者全表扫描，读取满足条件的数据行，然后在排序缓冲区sort buffer中完成排序操作，所有不是通过索引直接返回排序结果的排序都叫filesort排序。
>
> using index: 通过有序索引顺序扫描直接返回有序数据，这种情况即为using index，不需要而外排序，操作效率高。
>
> 
>
> 优化原则：
>
> 根据排序字段建立合适的索引，多字段排序时，也遵循最左前缀法则。
>
> 尽量使用覆盖索引。
>
> 多个字段排序时，一个升序一个降序，此时需要注意联合索引在创建时的规则(asc/desc)。
>
> 如果不可以避免出现filesort大数据量排序时，可适当增大排序缓冲区大小sort_buffer_size(默认256K)。

```shell
mysql> CREATE DATABASE IF NOT EXISTS testdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
Query OK, 1 row affected (0.01 sec)

mysql> use testdb;
Database changed

create table if not exists tb_user(
  id bigint primary key auto_increment,
  name varchar(64) not null,
  phone varchar(32) not null,
  email varchar(32) not null,
  profession varchar(32) not null,
  age int not null,
  gender int default 1 not null,
  status char(1) default 0 not null,
  createTime datetime not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

insert into tb_user values
(1,'吕布','17799990000','lvbu666@163.com','软件工程',23,1,'6','2001-02-02 00:00:00'),
(2,'曹操','17799990001','caocao666@qq.com','通讯工程',33,1,'0','2001-03-05 00:00:00'),
(3,'赵云','17799990002','17799990@139.com','英语',34,1,'2','2002-03-02 00:00:00'),
(4,'孙悟空','17799990003','17799990@sina.com','工程造价',54,1,'0','2001-07-02 00:00:00'),
(5,'花木兰','17799990004','19980729@sina.com','软件工程',23,2,'1','2001-04-22 00:00:00'),
(6,'大乔','17799990005','daqiao6666@sina.com','舞蹈',22,2,'0','2001-02-07 00:00:00'),
(7,'露娜','17799990006','luna_love@sina.com','应用数学',24,2,'0','2001-02-08 00:00:00'),
(8,'程咬金','17799990007','chengyaojin@163.com','化工',38,1,'5','2001-05-23 00:00:00'),
(9,'项羽','17799990008','xiangyu666@qq.com','金属材料',43,1,'0','2001-09-18 00:00:00'),
(10,'白起','17799990009','baiqi666@sina.com','机械工程及其自动化',27,1,'2','2001-08-16 00:00:00'),
(11,'韩信','17799990010','hanxin520@163.com','无机非金属材料工程',27,1,'0','2001-06-12 00:00:00'),
(12,'荆柯','17799990011','jingke123@163.com','会计',29,1,'0','2001-05-11 00:00:00'),
(13,'兰陵王','17799990012','lanlinwang666@126.com','工程造价',44,1,'1','2001-04-09 00:00:00'),
(14,'狂铁','17799990013','kuangtie@sina.com','应用数学',43,2,'2','2001-04-11 00:00:00'),
(15,'貂蝉','17799990014','84958948374@qq.com','软件工程',40,2,'3','2001-02-12 00:00:00'),
(16,'坦己','17799990015','2783238293@qq.com','软件工程',31,2,'0','2001-01-30 00:00:00'),
(17,'月丹','17799990016','xiaomin2001@sina.com','工业经济',35,1,'0','2000-05-03 00:00:00'),
(18,'赢政','17799990017','8839434342@qq.com','化工',38,1,'1','2001-08-08 00:00:00'),
(19,'狄仁杰','17799990018','jujiamlm8166@163.com','国际贸易',30,2,'0','2007-03-12 00:00:00'),
(20,'安琪拉','17799990019','jdodm1h@126.com','城市规划',51,2,'0','2001-08-15 00:00:00'),
(21,'典韦','17799990020','ycaunanjian@163.com','城市规划',52,1,'2','2000-04-12 00:00:00'),
(22,'廉颇','17799990021','lianpo321@126.com','土木工程',19,1,'3','2002-07-18 00:00:00'),
(23,'后羿','17799990022','altycj2000@139.com','城市园林',20,1,'0','2002-03-10 00:00:00'),
(24,'姜子牙','17799990023','37483844@qq.com','工程造价',29,1,'4','2003-05-26 00:00:00');

# 表中只有一个主键索引
mysql> show index from tb_user;
+---------+------------+----------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+---------+------------+
| Table   | Non_unique | Key_name | Seq_in_index | Column_name | Collation | Cardinality | Sub_part | Packed | Null | Index_type | Comment | Index_comment | Visible | Expression |
+---------+------------+----------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+---------+------------+
| tb_user |          0 | PRIMARY  |            1 | id          | A         |          24 |     NULL |   NULL |      | BTREE      |         |               | YES     | NULL       |
+---------+------------+----------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+---------+------------+
1 row in set (0.00 sec)

# age没有对应的索引，所以order by age进行了低效的using filesort排序
mysql> explain select id,age,phone from tb_user order by age;
+----+-------------+---------+------------+------+---------------+------+---------+------+------+----------+----------------+
| id | select_type | table   | partitions | type | possible_keys | key  | key_len | ref  | rows | filtered | Extra          |
+----+-------------+---------+------------+------+---------------+------+---------+------+------+----------+----------------+
|  1 | SIMPLE      | tb_user | NULL       | ALL  | NULL          | NULL | NULL    | NULL |   24 |   100.00 | Using filesort |
+----+-------------+---------+------------+------+---------------+------+---------+------+------+----------+----------------+
1 row in set, 1 warning (0.00 sec)

# 创建age asc、phone asc索引
mysql> create index idx_tb_user_age_a_phone_a on tb_user(age,phone);
Query OK, 0 rows affected (0.02 sec)
Records: 0  Duplicates: 0  Warnings: 0

# order by使用了idx_tb_user_age_a_phone_a索引排序
mysql> explain select id,age,phone from tb_user order by age;
+----+-------------+---------+------------+-------+---------------+---------------------------+---------+------+------+----------+-------------+
| id | select_type | table   | partitions | type  | possible_keys | key                       | key_len | ref  | rows | filtered | Extra       |
+----+-------------+---------+------------+-------+---------------+---------------------------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | tb_user | NULL       | index | NULL          | idx_tb_user_age_a_phone_a | 134     | NULL |   24 |   100.00 | Using index |
+----+-------------+---------+------------+-------+---------------+---------------------------+---------+------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)
mysql> explain select id,age,phone from tb_user order by age,phone;
+----+-------------+---------+------------+-------+---------------+---------------------------+---------+------+------+----------+-------------+
| id | select_type | table   | partitions | type  | possible_keys | key                       | key_len | ref  | rows | filtered | Extra       |
+----+-------------+---------+------------+-------+---------------+---------------------------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | tb_user | NULL       | index | NULL          | idx_tb_user_age_a_phone_a | 134     | NULL |   24 |   100.00 | Using index |
+----+-------------+---------+------------+-------+---------------+---------------------------+---------+------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)

# order by使用了idx_tb_user_age_a_phone_a索引反向排序(backward index scan)，效率高
mysql> explain select id,age,phone from tb_user order by age desc,phone desc;
+----+-------------+---------+------------+-------+---------------+---------------------------+---------+------+------+----------+----------------------------------+
| id | select_type | table   | partitions | type  | possible_keys | key                       | key_len | ref  | rows | filtered | Extra                            |
+----+-------------+---------+------------+-------+---------------+---------------------------+---------+------+------+----------+----------------------------------+
|  1 | SIMPLE      | tb_user | NULL       | index | NULL          | idx_tb_user_age_a_phone_a | 134     | NULL |   24 |   100.00 | Backward index scan; Using index |
+----+-------------+---------+------------+-------+---------------+---------------------------+---------+------+------+----------+----------------------------------+
1 row in set, 1 warning (0.00 sec)

# 因为orer by phone,age没有索引匹配，所以需要创建idx_tb_user_phone_a_age_a索引
mysql> explain select id,age,phone from tb_user order by phone,age;
+----+-------------+---------+------------+-------+---------------+---------------------------+---------+------+------+----------+-----------------------------+
| id | select_type | table   | partitions | type  | possible_keys | key                       | key_len | ref  | rows | filtered | Extra                       |
+----+-------------+---------+------------+-------+---------------+---------------------------+---------+------+------+----------+-----------------------------+
|  1 | SIMPLE      | tb_user | NULL       | index | NULL          | idx_tb_user_age_a_phone_a | 134     | NULL |   24 |   100.00 | Using index; Using filesort |
+----+-------------+---------+------------+-------+---------------+---------------------------+---------+------+------+----------+-----------------------------+
1 row in set, 1 warning (0.00 sec)
mysql> create index idx_tb_user_phone_a_age_a on tb_user(phone,age);
Query OK, 0 rows affected (0.03 sec)
Records: 0  Duplicates: 0  Warnings: 0
mysql> explain select id,age,phone from tb_user order by phone,age;
+----+-------------+---------+------------+-------+---------------+---------------------------+---------+------+------+----------+-------------+
| id | select_type | table   | partitions | type  | possible_keys | key                       | key_len | ref  | rows | filtered | Extra       |
+----+-------------+---------+------------+-------+---------------+---------------------------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | tb_user | NULL       | index | NULL          | idx_tb_user_phone_a_age_a | 134     | NULL |   24 |   100.00 | Using index |
+----+-------------+---------+------------+-------+---------------+---------------------------+---------+------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)

# 因为orer by age asc,phone desc没有索引匹配，所以需要创建idx_tb_user_age_a_phone_d索引
mysql> explain select id,age,phone from tb_user order by age asc,phone desc;
+----+-------------+---------+------------+-------+---------------+---------------------------+---------+------+------+----------+-----------------------------+
| id | select_type | table   | partitions | type  | possible_keys | key                       | key_len | ref  | rows | filtered | Extra                       |
+----+-------------+---------+------------+-------+---------------+---------------------------+---------+------+------+----------+-----------------------------+
|  1 | SIMPLE      | tb_user | NULL       | index | NULL          | idx_tb_user_age_a_phone_a | 134     | NULL |   24 |   100.00 | Using index; Using filesort |
+----+-------------+---------+------------+-------+---------------+---------------------------+---------+------+------+----------+-----------------------------+
1 row in set, 1 warning (0.00 sec)
mysql> create index idx_tb_user_age_a_phone_d on tb_user(age asc,phone desc);
Query OK, 0 rows affected (0.03 sec)
Records: 0  Duplicates: 0  Warnings: 0
mysql> explain select id,age,phone from tb_user order by age asc,phone desc;
+----+-------------+---------+------------+-------+---------------+---------------------------+---------+------+------+----------+-------------+
| id | select_type | table   | partitions | type  | possible_keys | key                       | key_len | ref  | rows | filtered | Extra       |
+----+-------------+---------+------------+-------+---------------+---------------------------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | tb_user | NULL       | index | NULL          | idx_tb_user_age_a_phone_d | 134     | NULL |   24 |   100.00 | Using index |
+----+-------------+---------+------------+-------+---------------+---------------------------+---------+------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)

# 因为查询中返回中有name字段导致不能覆盖索引所以order by不使用索引排序
mysql> explain select id,age,phone,name from tb_user order by age asc,phone desc;
+----+-------------+---------+------------+------+---------------+------+---------+------+------+----------+----------------+
| id | select_type | table   | partitions | type | possible_keys | key  | key_len | ref  | rows | filtered | Extra          |
+----+-------------+---------+------------+------+---------------+------+---------+------+------+----------+----------------+
|  1 | SIMPLE      | tb_user | NULL       | ALL  | NULL          | NULL | NULL    | NULL |   24 |   100.00 | Using filesort |
+----+-------------+---------+------------+------+---------------+------+---------+------+------+----------+----------------+
1 row in set, 1 warning (0.00 sec)
```

### group by优化

> 分组操作时，索引的使用也需要满足最左前缀法则。

```shell
mysql> CREATE DATABASE IF NOT EXISTS testdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
Query OK, 1 row affected (0.01 sec)

mysql> use testdb;
Database changed

create table if not exists tb_user(
  id bigint primary key auto_increment,
  name varchar(64) not null,
  phone varchar(32) not null,
  email varchar(32) not null,
  profession varchar(32) not null,
  age int not null,
  gender int default 1 not null,
  status char(1) default 0 not null,
  createTime datetime not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

insert into tb_user values
(1,'吕布','17799990000','lvbu666@163.com','软件工程',23,1,'6','2001-02-02 00:00:00'),
(2,'曹操','17799990001','caocao666@qq.com','通讯工程',33,1,'0','2001-03-05 00:00:00'),
(3,'赵云','17799990002','17799990@139.com','英语',34,1,'2','2002-03-02 00:00:00'),
(4,'孙悟空','17799990003','17799990@sina.com','工程造价',54,1,'0','2001-07-02 00:00:00'),
(5,'花木兰','17799990004','19980729@sina.com','软件工程',23,2,'1','2001-04-22 00:00:00'),
(6,'大乔','17799990005','daqiao6666@sina.com','舞蹈',22,2,'0','2001-02-07 00:00:00'),
(7,'露娜','17799990006','luna_love@sina.com','应用数学',24,2,'0','2001-02-08 00:00:00'),
(8,'程咬金','17799990007','chengyaojin@163.com','化工',38,1,'5','2001-05-23 00:00:00'),
(9,'项羽','17799990008','xiangyu666@qq.com','金属材料',43,1,'0','2001-09-18 00:00:00'),
(10,'白起','17799990009','baiqi666@sina.com','机械工程及其自动化',27,1,'2','2001-08-16 00:00:00'),
(11,'韩信','17799990010','hanxin520@163.com','无机非金属材料工程',27,1,'0','2001-06-12 00:00:00'),
(12,'荆柯','17799990011','jingke123@163.com','会计',29,1,'0','2001-05-11 00:00:00'),
(13,'兰陵王','17799990012','lanlinwang666@126.com','工程造价',44,1,'1','2001-04-09 00:00:00'),
(14,'狂铁','17799990013','kuangtie@sina.com','应用数学',43,2,'2','2001-04-11 00:00:00'),
(15,'貂蝉','17799990014','84958948374@qq.com','软件工程',40,2,'3','2001-02-12 00:00:00'),
(16,'坦己','17799990015','2783238293@qq.com','软件工程',31,2,'0','2001-01-30 00:00:00'),
(17,'月丹','17799990016','xiaomin2001@sina.com','工业经济',35,1,'0','2000-05-03 00:00:00'),
(18,'赢政','17799990017','8839434342@qq.com','化工',38,1,'1','2001-08-08 00:00:00'),
(19,'狄仁杰','17799990018','jujiamlm8166@163.com','国际贸易',30,2,'0','2007-03-12 00:00:00'),
(20,'安琪拉','17799990019','jdodm1h@126.com','城市规划',51,2,'0','2001-08-15 00:00:00'),
(21,'典韦','17799990020','ycaunanjian@163.com','城市规划',52,1,'2','2000-04-12 00:00:00'),
(22,'廉颇','17799990021','lianpo321@126.com','土木工程',19,1,'3','2002-07-18 00:00:00'),
(23,'后羿','17799990022','altycj2000@139.com','城市园林',20,1,'0','2002-03-10 00:00:00'),
(24,'姜子牙','17799990023','37483844@qq.com','工程造价',29,1,'4','2003-05-26 00:00:00');

# 没有索引group by profession使用using temporary临时表效率低
mysql> explain select profession,count(*) from tb_user group by profession;
+----+-------------+---------+------------+------+---------------+------+---------+------+------+----------+-----------------+
| id | select_type | table   | partitions | type | possible_keys | key  | key_len | ref  | rows | filtered | Extra           |
+----+-------------+---------+------------+------+---------------+------+---------+------+------+----------+-----------------+
|  1 | SIMPLE      | tb_user | NULL       | ALL  | NULL          | NULL | NULL    | NULL |   24 |   100.00 | Using temporary |
+----+-------------+---------+------------+------+---------------+------+---------+------+------+----------+-----------------+
1 row in set, 1 warning (0.00 sec)

mysql> create index idx_tb_user_profession_age_status on tb_user(profession,age,status);
Query OK, 0 rows affected (0.03 sec)
Records: 0  Duplicates: 0  Warnings: 0

# 使用索引idx_tb_user_profession_age_status group by
mysql> explain select profession,count(*) from tb_user group by profession;
+----+-------------+---------+------------+-------+-----------------------------------+-----------------------------------+---------+------+------+----------+-------------+
| id | select_type | table   | partitions | type  | possible_keys                     | key                               | key_len | ref  | rows | filtered | Extra       |
+----+-------------+---------+------------+-------+-----------------------------------+-----------------------------------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | tb_user | NULL       | index | idx_tb_user_profession_age_status | idx_tb_user_profession_age_status | 138     | NULL |   24 |   100.00 | Using index |
+----+-------------+---------+------------+-------+-----------------------------------+-----------------------------------+---------+------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)
mysql> explain select profession,age,count(*) from tb_user group by profession,age;
+----+-------------+---------+------------+-------+-----------------------------------+-----------------------------------+---------+------+------+----------+-------------+
| id | select_type | table   | partitions | type  | possible_keys                     | key                               | key_len | ref  | rows | filtered | Extra       |
+----+-------------+---------+------------+-------+-----------------------------------+-----------------------------------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | tb_user | NULL       | index | idx_tb_user_profession_age_status | idx_tb_user_profession_age_status | 138     | NULL |   24 |   100.00 | Using index |
+----+-------------+---------+------------+-------+-----------------------------------+-----------------------------------+---------+------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)
mysql> explain select profession,age,count(*) from tb_user where profession='软件工程' group by age;
+----+-------------+---------+------------+------+-----------------------------------+-----------------------------------+---------+-------+------+----------+-------------+
| id | select_type | table   | partitions | type | possible_keys                     | key                               | key_len | ref   | rows | filtered | Extra       |
+----+-------------+---------+------------+------+-----------------------------------+-----------------------------------+---------+-------+------+----------+-------------+
|  1 | SIMPLE      | tb_user | NULL       | ref  | idx_tb_user_profession_age_status | idx_tb_user_profession_age_status | 130     | const |    4 |   100.00 | Using index |
+----+-------------+---------+------------+------+-----------------------------------+-----------------------------------+---------+-------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)
```

### update优化（避免行锁升级为表锁）

> InnoDB行锁是针对索引加锁（update时没有使用索引就升级为表锁）的，不是针对记录加锁的，并且该索引不能失效，否则会从行锁升级为表锁。

```shell
mysql> CREATE DATABASE IF NOT EXISTS testdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
Query OK, 1 row affected (0.01 sec)

mysql> use testdb;
Database changed

create table if not exists course(
 id bigint primary key not null auto_increment,
 name varchar(128) not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

insert into course(id,name) values (1,'java'),(2,'php'),(3,'c'),(4,'python');

# 根据课程名修改课程名，因为课程名没有索引（无法锁定行），导致升级为表级锁，导致另外一个事务根据id修改课程名一直阻塞
# 解决方案创建课程名索引 create index idx_course_name on course(name);
mysql> start transaction;

mysql> update course set name='java2' where name='java';
Query OK, 1 row affected (0.00 sec)
Rows matched: 1  Changed: 1  Warnings: 0

# session2
mysql> start transaction;
Query OK, 0 rows affected (0.00 sec)
# session2
mysql> update course set name='c1' where id=3;
Query OK, 1 row affected (4.56 sec)
Rows matched: 1  Changed: 1  Warnings: 0

mysql> commit;
Query OK, 0 rows affected (0.00 sec)

# session2
mysql> commit;
Query OK, 0 rows affected (0.00 sec)
```



## performance_schema.threads中的thread_id、processlist_id、thread_os_id

```
https://www.linuxe.cn/post-718.html

thread_os_id: 对应操作系统mysql进程下的线程ID,与ps -ef出来的mysql线程号相同
使用top -H -p mysqld_pid列出mysql进程下的所有线程并找到thread_os_id对应的pid

processlist_id: 对应连接id，通过select connection_id()获取当前session对应的processlist_id，对应show processlist显示的id

thread_id: mysql内部自增的线程id，select sys.ps_thread_id(connection_id())返回发当前session的内部thread_id，select sys.ps_thread_id(processlist_id)返回指定processlist_id的内部thread_id

proccesslist_id和thread_id的关系:
processlist_id通过函数sys.ps_thread_id(processlist_id)转换为thread_id
thread_id通过查询performance_schema.threads表找到对应的processlist_id，如下面SQL所示：
mysql> select * from performance_schema.threads where thread_id=82\G;
*************************** 1. row ***************************
          THREAD_ID: 82
               NAME: thread/sql/one_connection
               TYPE: FOREGROUND
     PROCESSLIST_ID: 19
   PROCESSLIST_USER: root
   PROCESSLIST_HOST: localhost
     PROCESSLIST_DB: NULL
PROCESSLIST_COMMAND: Query
   PROCESSLIST_TIME: 0
  PROCESSLIST_STATE: statistics
   PROCESSLIST_INFO: select * from performance_schema.threads where thread_id=82
   PARENT_THREAD_ID: NULL
               ROLE: NULL
       INSTRUMENTED: YES
            HISTORY: YES
    CONNECTION_TYPE: Socket
       THREAD_OS_ID: 237
     RESOURCE_GROUP: USR_default
1 row in set (0.00 sec)

ERROR: 
No query specified
```

## MySQL和elasticsearch数据同步

> http://www.ppmy.cn/news/12297.html
>
> 数据同步解决方案：
>
> - 同步双写：优点是实现简单。缺点是业务耦合，商品的管理中耦合大量数据同步代码；影响性能，写入两个存储，响应时间变长
>   不便扩展；搜索可能有一些个性化需求，需要对数据进行聚合，这种方式不便实现。
> - 异步双写：上架商品的时候, 先把商品数据丢入MQ, 为了解耦, 拆分一个搜索微服务, 搜搜微服务去订阅商品变动的信息, 完成同步。优点是解耦合，商品服务无需关注数据同步；实时性较好，使用MQ，正常情况下，同步完成在秒级。缺点是引入了新的组件和服务，增加了复杂度。
> - 定时任务：定时任务, 频率不好选择, 频率高的话, 会引起业务的波峰, 使得Cpu, 内存的上升, 频率低的话, 时效性比较差。优点是实现比较简单。缺点是实时性难以保证，对存储压力较大。
> - 数据订阅：MySQL通过binlog订阅实现主从同步，各路数据订阅框架比如canal就依据这个原理，将client组件伪装成从库，来实现数据订阅。优点是相比于异步双写, 可以降低商品的耦合性, 时效性更好；业务入侵较少，实时性较好。
>
> 完整的MySQL到elasticsearch数据同步demo没有编写，只编写了demo-canal demo可以作为具体实现方案的参考

## MySQL8 possible_keys为null，key却不为null解答

> https://blog.csdn.net/eden_Liang/article/details/108026148
>
> **这种情况一般发生在覆盖索引条件下，`possible_keys`为`null`说明用不上索引的树形查找，但如果二**
>
> **级索引包含了所有要查找的数据，二级索引往往比聚集索引小，所以\*mysql\*可能会选择顺序遍历这个二**
>
> **级索引直接返回，但没有发挥树形查找优势，所以就出现了这个情况。**

```
CREATE DATABASE IF NOT EXISTS testdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

use testdb;

drop table test_user;

create table test_user(
 id bigint primary key not null auto_increment,
 name varchar(512) not null,
 createTime datetime not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

create index idx_test_user_name on test_user(name);

insert into test_user(id,name,createTime) values(NULL,'张三',now()),(NULL,'李四',now()),(NULL,'王五',now()),(NULL,'老黄',now()),(NULL,'积分',now()),(NULL,'i额ur',now()),(NULL,'就佛额ur',now()),(NULL,'发咯入耳',now()),(NULL,'经济而',now()),(NULL,'第一热',now()),(NULL,'ui偶尔哦',now()),(NULL,'解决开发就',now()),(NULL,'就偶尔',now()),(NULL,'空间考虑日额',now()),(NULL,'副偶尔urgn',now()),(NULL,'u热ure',now()),(NULL,'就反而哦入耳',now()),(NULL,'i若ieu',now()),(NULL,'ui哦入耳给你',now()),(NULL,'看iore',now()),(NULL,'iu人偶尔',now()),(NULL,'你楼而',now()),(NULL,'哦iue',now()),(NULL,'就刻录机了解偶尔',now()),(NULL,'偶哦入耳',now()),(NULL,'积分金额ur',now()),(NULL,'楼热',now()),(NULL,'你率ueor',now()),(NULL,'率偶尔',now()),(NULL,'偶哦人',now()),(NULL,'女看看看入耳',now()),(NULL,'i入耳给你',now()),(NULL,'看破ioire',now()),(NULL,'那就vuer',now()),(NULL,'角落irue',now()),(NULL,'v你看哦ieur',now()),(NULL,'是热热u',now()),(NULL,'看配额uoirue',now()),(NULL,'你的人',now()),(NULL,'v弄丢热',now()),(NULL,'的热偶尔ug',now()),(NULL,'v你的哦ieur',now()),(NULL,'u哦入耳',now()),(NULL,'v地方耨而',now()),(NULL,'色热偶偶尔',now());

# 因为返回列为 * ，所以没有使用idx_test_user_name索引返回数据，因为索引中不包含createTime数据
mysql> explain select * from test_user where name like '%黄';
+----+-------------+-----------+------------+------+---------------+------+---------+------+------+----------+-------------+
| id | select_type | table     | partitions | type | possible_keys | key  | key_len | ref  | rows | filtered | Extra       |
+----+-------------+-----------+------------+------+---------------+------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | test_user | NULL       | ALL  | NULL          | NULL | NULL    | NULL |   45 |    11.11 | Using where |
+----+-------------+-----------+------------+------+---------------+------+---------+------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)

# 没有使用idx_test_user_name检索数据，但是使用了idx_test_user_name索引返回了id数据，所以possible_keys为NULL，而key却不为NULL
mysql> explain select id from test_user where name like '%黄';
+----+-------------+-----------+------------+-------+---------------+--------------------+---------+------+------+----------+--------------------------+
| id | select_type | table     | partitions | type  | possible_keys | key                | key_len | ref  | rows | filtered | Extra                    |
+----+-------------+-----------+------------+-------+---------------+--------------------+---------+------+------+----------+--------------------------+
|  1 | SIMPLE      | test_user | NULL       | index | NULL          | idx_test_user_name | 2050    | NULL |   45 |    11.11 | Using where; Using index |
+----+-------------+-----------+------------+-------+---------------+--------------------+---------+------+------+----------+--------------------------+
1 row in set, 1 warning (0.00 sec)
```

## binlog日志

> binlog 有三种格式：
>
> - Statement（Statement-Based Replication,SBR）：Statement 模式只记录执行的 SQL，不需要记录每一行数据的变化，因此极大的减少了 binlog 的日志量，避免了大量的 IO 操作，提升了系统的性能。
>
>   但是，正是由于 Statement 模式只记录 SQL，而如果一些 SQL 中 包含了函数，那么可能会出现执行结果不一致的情况。比如说 uuid() 函数，每次执行的时候都会生成一个随机字符串，在 master 中记录了 uuid，当同步到 slave 之后，再次执行，就得到另外一个结果了。
>
>   所以使用 Statement 格式会出现一些数据一致性问题。
>
> - Row（Row-Based Replication,RBR）：从 MySQL5.1.5 版本开始，binlog 引入了 Row 格式，Row 格式不记录 SQL 语句上下文相关信息，仅仅只需要记录某一条记录被修改成什么样子了。
>
>   Row 格式的日志内容会非常清楚地记录下每一行数据修改的细节，这样就不会出现 Statement 中存在的那种数据无法被正常复制的情况。
>
>   不过 Row 格式也有一个很大的问题，那就是日志量太大了，特别是批量 update、整表 delete、alter 表等操作，由于要记录每一行数据的变化，此时会产生大量的日志，大量的日志也会带来 IO 性能问题。
>
> - Mixed（Mixed-Based Replication,MBR）：从 MySQL5.1.8 版开始，MySQL 又推出了 Mixed 格式，这种格式实际上就是 Statement 与 Row 的结合。
>
>   在 Mixed 模式下，系统会自动判断 该 用 Statement 还是 Row：一般的语句修改使用 Statement 格式保存 binlog；对于一些 Statement 无法准确完成主从复制的操作，则采用 Row 格式保存 binlog。
>
>   Mixed 模式中，MySQL 会根据执行的每一条具体的 SQL 语句来区别对待记录的日志格式，也就是在 Statement 和 Row 之间选择一种。

### 问题解决：Warning Unsafe statement written to the binary log using statement format since BINLOG_FORMAT = STATEMENT

> https://stackoverflow.com/questions/17057593/warning-unsafe-statement-written-to-the-binary-log-using-statement-format-since
>
> 
>
> 把日志类型修改为mixed
> binlog_format=mixed
