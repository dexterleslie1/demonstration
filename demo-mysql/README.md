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

## 函数

### 字符串函数

> 暂时未用到

### 数值函数

> 暂时未用到

### 日期函数

> 暂时未用到

### 流程函数

#### if函数

> if(value,t,f): 如果value为true则返回t，否则返回f。

```shell
mysql> select if(true,'yes','no');
+---------------------+
| if(true,'yes','no') |
+---------------------+
| yes                 |
+---------------------+
1 row in set (0.00 sec)

mysql> select if(false,'yes','no');
+----------------------+
| if(false,'yes','no') |
+----------------------+
| no                   |
+----------------------+
1 row in set (0.00 sec)
```

#### ifnull函数

> ifnull(value1,value2): 如果value1不为空，则返回value1,否则返回value2

```shell
mysql> select ifnull('v1','v2');
+-------------------+
| ifnull('v1','v2') |
+-------------------+
| v1                |
+-------------------+
1 row in set (0.00 sec)

mysql> select ifnull(NULL,'v2');
+-------------------+
| ifnull(NULL,'v2') |
+-------------------+
| v2                |
+-------------------+
1 row in set (0.00 sec)
```

#### case when then else end函数

> https://www.cnblogs.com/dirgo/p/9913708.html

```shell
mysql> CREATE DATABASE IF NOT EXISTS testdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
Query OK, 1 row affected (0.01 sec)

mysql> use testdb;
Database changed

create table if not exists t_test_case(
  id bigint primary key auto_increment,
  flag varchar(64) not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

insert into t_test_case values (1,'1'),(2,'2'),(3,'3'),(4,'4');

mysql> update t_test_case set flag=(case id when 1 then 'v1' when 2 then 'v2' else 'v3' end) where id in(1,2,3);
Query OK, 3 rows affected (0.00 sec)
Rows matched: 3  Changed: 3  Warnings: 0

mysql> select * from t_test_case;
+----+------+
| id | flag |
+----+------+
|  1 | v1   |
|  2 | v2   |
|  3 | v3   |
|  4 | 4    |
+----+------+
4 rows in set (0.00 sec)

mysql> update t_test_case set flag=(case when id=1 then 'v1' when id=2 then 'v2' else 'v3' end) where id in(1,2,3);
Query OK, 3 rows affected (0.01 sec)
Rows matched: 3  Changed: 3  Warnings: 0

mysql> select * from t_test_case;
+----+------+
| id | flag |
+----+------+
|  1 | v1   |
|  2 | v2   |
|  3 | v3   |
|  4 | 4    |
+----+------+
4 rows in set (0.00 sec)
```

## 约束

> 暂时未用到

## 多表查询

> 暂时未用到

## 事务

### autocommit和start transaction区别

> https://blog.csdn.net/huangli1466384630/article/details/127468237
>
> 相同点：这三个关键字都与mysql的事务相关，直接操纵事务的语句
>
> 不同点：
> （1）他们的作用范围不一样。
> AUTOCOMMIT是数据库innodb引擎级别的属性，对于start transaction和begin/end而言是全局的，一旦使用SET AUTOCOMMIT=0 禁止自动提交，则在这个数据库内部的所有事务都不会自动提交，除非你手动的为每一个事务执行了commit或者rollback语句；而start transaction和begin/commit只能控制某一个事务。
> （2）优先级不同。存在一种set autocommit = 1/0 但是 对于某一个sql语句使用了 begin/commit的原子性操作，那么mysql会优先使用begin/commit命令控制被这组命令修饰的事务；，这一点比较重要，因为之前很多的博主都说要先设置set autocommit = 0关闭掉事务的自动提交属性才能使用 begin/commit或者begin/rollback的原子性操作，这是错误的。
>
> 至于start transaction 和 begin的区别：
> 两者的作用一摸一样，只是在begin可能成为关键字的时候，使用start transaction 可以 避免这种情况，start transaction或者begin开启一个事务，然后使用commit提交事务或者ROLLBACK回滚事务

**准备测试环境**

```shell
mysql> CREATE DATABASE IF NOT EXISTS testdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
Query OK, 1 row affected (0.01 sec)

mysql> use testdb;
Database changed

mysql> create table if not exists t_balance(
    ->   id bigint primary key auto_increment,
    ->   name varchar(64) default '' not null,
    ->   amount int default 0 not null
    -> ) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;
Query OK, 0 rows affected (0.02 sec)

mysql> insert into t_balance values (null,'zhangsan', 10000);
Query OK, 1 row affected (0.00 sec)
```

**AUTOCOMMIT是数据库innodb引擎级别的属性，对于start transaction和begin/end而言是全局的，一旦使用SET AUTOCOMMIT=0 禁止自动提交，则在这个数据库内部的所有事务都不会自动提交，除非你手动的为每一个事务执行了commit或者rollback语句**

```shell
# 查询当前autocommit值
mysql> select @@autocommit;
+--------------+
| @@autocommit |
+--------------+
|            1 |
+--------------+
1 row in set (0.00 sec)
# 关闭当前session的autocommit，其他session不受影响
mysql> set autocommit=0;
Query OK, 0 rows affected (0.00 sec)

mysql> select @@autocommit;
+--------------+
| @@autocommit |
+--------------+
|            0 |
+--------------+
1 row in set (0.00 sec)

mysql> update t_balance set amount=amount-1000 where name='zhangsan';
Query OK, 1 row affected (0.00 sec)
Rows matched: 1  Changed: 1  Warnings: 0

#session2 查询t_balance数据依旧没有变化
mysql> select * from t_balance;
+----+----------+--------+
| id | name     | amount |
+----+----------+--------+
|  3 | zhangsan |  10000 |
+----+----------+--------+
1 row in set (0.00 sec)

mysql> commit;
Query OK, 0 rows affected (0.00 sec)

# 在上面session1 执行commit后，session2查询t_balance数据才有变化
mysql> select * from t_balance;
+----+----------+--------+
| id | name     | amount |
+----+----------+--------+
|  3 | zhangsan |   9000 |
+----+----------+--------+
1 row in set (0.00 sec)
```

### 事务异常回滚

**准备环境**

```shell
mysql> CREATE DATABASE IF NOT EXISTS testdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
Query OK, 1 row affected (0.01 sec)

mysql> use testdb;
Database changed

mysql> create table if not exists t_balance(
    ->   id bigint primary key auto_increment,
    ->   name varchar(64) default '' not null,
    ->   amount int default 0 not null
    -> ) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;
Query OK, 0 rows affected (0.02 sec)

mysql> insert into t_balance values (null,'zhangsan', 10000),(null,'lisi', 10000);
Query OK, 1 row affected (0.00 sec)
```

```shell
mysql> start transaction;
Query OK, 0 rows affected (0.00 sec)

mysql> update t_balance set amount=amount-1000 where name='zhangsan';
Query OK, 1 row affected (0.00 sec)
Rows matched: 1  Changed: 1  Warnings: 0

# 模拟业务异常
mysql> jkjkdd;
ERROR 1064 (42000): You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near 'jkjkdd' at line 1

mysql> select * from t_balance;
+----+----------+--------+
| id | name     | amount |
+----+----------+--------+
|  1 | zhangsan |   9000 |
|  2 | lisi     |  10000 |
+----+----------+--------+
2 rows in set (0.00 sec)

# 事务回滚
mysql> rollback;
Query OK, 0 rows affected (0.01 sec)

# 事务回滚后余额恢复原值
mysql> select * from t_balance;
+----+----------+--------+
| id | name     | amount |
+----+----------+--------+
|  1 | zhangsan |  10000 |
|  2 | lisi     |  10000 |
+----+----------+--------+
2 rows in set (0.00 sec)
```

### 事务四大特性（ACID）

> 原子性（Atomicity）：事务是不可分割的最小操作单元，要么全部成功，要么全部失败。转账交易场景，要么A账户扣款成功并且B帐户加款成功，要么A帐号不扣款并且B账户不加款。
>
> 一致性（Consistency）：事务完成时，必须使所有数据都保持一致状态。转账交易场景，无论事务提交还是回滚，事务结束后A账户+B账户的总余额等于交易开始前的总余额。
>
> 隔离性（Isolation）：数据库系统提供的隔离机制，保证事务在不受外部并发操作影响的独立环境下运行。
>
> 持久性（Durability）：事务一但提交或者回滚，它对数据库中的数据的改变就是永久的。

### 事务隔离级别

> https://blog.csdn.net/ahuangqingfeng/article/details/124407846
>
> 并发事务问题（并发事务在各自事务中操作同一个数据所引发的问题）：
>
> 脏读：一个事务读取到另外一个事务还没有提交的数据。A事务读取B事务尚未提交的更改数据，并在这个数据基础上操作。如果B事务回滚，那么A事务读到的数据根本不是合法的，称为脏读。在oracle中，由于有version控制，不会出现脏读。
>
> 不可重复读：一次事务中，两次读操作中，读出来的数据内容不一致（读的是修改或者删除，MySQL底层使用行锁解决此问题）。A事务读取了B事务已经提交的更改（或删除）数据。比如A事务第一次读取数据，然后B事务更改该数据并提交，A事务再次读取数据，两次读取的数据不一样。
>
> 幻读：一次事务中，两次读操作中，读到的数据行数不一致（读的是插入的新数据，因为是插入数据MySQL底层无法使用行锁解决次问题）。A事务读取了B事务已经提交的新增数据。注意和不可重复读的区别，这里是新增，不可重复读是更改（或删除）。这两种情况对策是不一样的，对于不可重复读，只需要采取行级锁防止该记录数据被更改或删除，然而对于幻读必须加表级锁，防止在这个表中新增一条数据。
>
> 
>
> https://blog.csdn.net/ahuangqingfeng/article/details/124407846
>
> 事务隔离级别用于解决并发事务所引发的问题：
>
> 读未提交（read uncommitted）：脏读存在、不可重复读存在、幻读存在。
>
> 读已提交（read committed）：脏读不存在、不可重复读存在、幻读存在。
>
> 可重复读（repeatable read 默认级别）：脏读不存在、不可重复读不存在、幻读存在。
>
> 可串行化（serializable）：所有都不存在。
>
> 读未提交性能最好，可串行化性能最差。

**准备测试环境**

```shell
mysql> CREATE DATABASE IF NOT EXISTS testdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
Query OK, 1 row affected (0.01 sec)

mysql> use testdb;
Database changed

mysql> create table if not exists t_balance(
    ->   id bigint primary key auto_increment,
    ->   name varchar(64) default '' not null,
    ->   amount int default 0 not null
    -> ) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;
Query OK, 0 rows affected (0.02 sec)
```

#### 脏读

**read uncommitted存在**

```shell
mysql> truncate table t_balance;
Query OK, 0 rows affected (0.02 sec)

# 查看当前session事务隔离级别
mysql> select @@transaction_isolation;
+-------------------------+
| @@transaction_isolation |
+-------------------------+
| REPEATABLE-READ         |
+-------------------------+
1 row in set (0.00 sec)

# 设置当前session事务隔离级别为read uncommitted
mysql> set session transaction isolation level read uncommitted;
Query OK, 0 rows affected (0.00 sec)

mysql> select @@transaction_isolation;
+-------------------------+
| @@transaction_isolation |
+-------------------------+
| READ-UNCOMMITTED        |
+-------------------------+
1 row in set (0.00 sec)

mysql> insert into t_balance values(1,'zhangsan',10000);
Query OK, 1 row affected (0.00 sec)

mysql> select * from t_balance;
+----+----------+--------+
| id | name     | amount |
+----+----------+--------+
|  1 | zhangsan |  10000 |
+----+----------+--------+
1 row in set (0.00 sec)

mysql> start transaction;
Query OK, 0 rows affected (0.00 sec)

# session2开启事务
mysql> start transaction;
Query OK, 0 rows affected (0.00 sec)
# session2修改金额
mysql> update t_balance set amount=amount-1000 where name='zhangsan';
Query OK, 1 row affected (0.00 sec)
Rows matched: 1  Changed: 1  Warnings: 0

# session1能够读取session2未提交的修改
mysql> select * from t_balance;
+----+----------+--------+
| id | name     | amount |
+----+----------+--------+
|  1 | zhangsan |   9000 |
+----+----------+--------+
1 row in set (0.00 sec)

# session2 回滚修改
mysql> rollback;
Query OK, 0 rows affected (0.00 sec)

# session1 查询到session2回滚后的数据
mysql> select * from t_balance;
+----+----------+--------+
| id | name     | amount |
+----+----------+--------+
|  1 | zhangsan |  10000 |
+----+----------+--------+
1 row in set (0.00 sec)

mysql> commit;
Query OK, 0 rows affected (0.00 sec)
```

**read commited不存在**

```shell
mysql> truncate table t_balance;
Query OK, 0 rows affected (0.02 sec)

mysql> set session transaction isolation level read committed;
Query OK, 0 rows affected (0.00 sec)

mysql> select @@transaction_isolation;
+-------------------------+
| @@transaction_isolation |
+-------------------------+
| READ-COMMITTED          |
+-------------------------+
1 row in set (0.00 sec)

mysql> insert into t_balance values(1,'zhangsan',10000);
Query OK, 1 row affected (0.01 sec)

mysql> start transaction;
Query OK, 0 rows affected (0.00 sec)

# session2
mysql> start transaction;
Query OK, 0 rows affected (0.00 sec)

# session2
mysql> update t_balance set amount=amount-1000 where name='zhangsan';
Query OK, 1 row affected (0.00 sec)
Rows matched: 1  Changed: 1  Warnings: 0

# session1不会再读取到session2未提交的修改
mysql> select * from t_balance;
+----+----------+--------+
| id | name     | amount |
+----+----------+--------+
|  1 | zhangsan |  10000 |
+----+----------+--------+
1 row in set (0.00 sec)

# session2
mysql> commit;
Query OK, 0 rows affected (0.00 sec)

mysql> commit;
Query OK, 0 rows affected (0.00 sec)
```

#### 不可重复读

**read commited存在**

```shell
mysql> truncate table t_balance;
Query OK, 0 rows affected (0.02 sec)

mysql> set session transaction isolation level read committed;
Query OK, 0 rows affected (0.00 sec)

mysql> select @@transaction_isolation;
+-------------------------+
| @@transaction_isolation |
+-------------------------+
| READ-COMMITTED          |
+-------------------------+
1 row in set (0.00 sec)

mysql> insert into t_balance values(1,'zhangsan',10000);
Query OK, 1 row affected (0.01 sec)

mysql> start transaction;
Query OK, 0 rows affected (0.00 sec)

# session2
mysql> start transaction;
Query OK, 0 rows affected (0.00 sec)

mysql> select * from t_balance;
+----+----------+--------+
| id | name     | amount |
+----+----------+--------+
|  1 | zhangsan |  10000 |
+----+----------+--------+
1 row in set (0.00 sec)

# session2
mysql> update t_balance set amount=amount-1000 where name='zhangsan';
Query OK, 1 row affected (0.00 sec)
Rows matched: 1  Changed: 1  Warnings: 0
# session2
mysql> commit;
Query OK, 0 rows affected (0.01 sec)

# session1中发现同一个事务同一个SQL返回结果前后不一致
mysql> select * from t_balance;
+----+----------+--------+
| id | name     | amount |
+----+----------+--------+
|  1 | zhangsan |   9000 |
+----+----------+--------+
1 row in set (0.00 sec)

mysql> commit;
Query OK, 0 rows affected (0.00 sec)
```

**repeatable read不存在**

```shell
mysql> truncate table t_balance;
Query OK, 0 rows affected (0.02 sec)

mysql> set session transaction isolation level repeatable read;
Query OK, 0 rows affected (0.00 sec)

mysql> select @@transaction_isolation;
+-------------------------+
| @@transaction_isolation |
+-------------------------+
| REPEATABLE-READ         |
+-------------------------+
1 row in set (0.00 sec)

mysql> insert into t_balance values(1,'zhangsan',10000);
Query OK, 1 row affected (0.01 sec)

mysql> start transaction;
Query OK, 0 rows affected (0.00 sec)

# session2
mysql> start transaction;
Query OK, 0 rows affected (0.00 sec)

mysql> select * from t_balance;
+----+----------+--------+
| id | name     | amount |
+----+----------+--------+
|  1 | zhangsan |  10000 |
+----+----------+--------+
1 row in set (0.00 sec)

# session2
mysql> update t_balance set amount=amount-1000 where name='zhangsan';
Query OK, 1 row affected (0.01 sec)
Rows matched: 1  Changed: 1  Warnings: 0
# session2
mysql> commit;
Query OK, 0 rows affected (0.00 sec)

# session1不会有不可重复读问题
mysql> select * from t_balance;
+----+----------+--------+
| id | name     | amount |
+----+----------+--------+
|  1 | zhangsan |  10000 |
+----+----------+--------+
1 row in set (0.00 sec)

mysql> commit;
Query OK, 0 rows affected (0.00 sec)

mysql> select * from t_balance;
+----+----------+--------+
| id | name     | amount |
+----+----------+--------+
|  1 | zhangsan |   9000 |
+----+----------+--------+
1 row in set (0.00 sec)
```

#### 幻读

**repeatable read存在**

```shell
mysql> truncate table t_balance;
Query OK, 0 rows affected (0.02 sec)

mysql> set session transaction isolation level repeatable read;
Query OK, 0 rows affected (0.00 sec)

mysql> select @@transaction_isolation;
+-------------------------+
| @@transaction_isolation |
+-------------------------+
| REPEATABLE-READ         |
+-------------------------+
1 row in set (0.00 sec)

mysql> start transaction;
Query OK, 0 rows affected (0.00 sec)

mysql> select * from t_balance;
Empty set (0.00 sec)

# session2
mysql> start transaction;
Query OK, 0 rows affected (0.00 sec)
# session2
mysql> insert into t_balance values(1,'zhangsan',10000);
Query OK, 1 row affected (0.01 sec)
# session2
mysql> commit;
Query OK, 0 rows affected (0.00 sec)

# session1插入id=1的记录提示主键冲突错误，但是select * from t_balance没有返回记录
mysql> insert into t_balance values(1,'zhangsan',10000);
ERROR 1062 (23000): Duplicate entry '1' for key 't_balance.PRIMARY'
mysql> select * from t_balance;
Empty set (0.01 sec)

mysql> commit;
Query OK, 0 rows affected (0.00 sec)

mysql> select * from t_balance;
+----+----------+--------+
| id | name     | amount |
+----+----------+--------+
|  1 | zhangsan |  10000 |
+----+----------+--------+
1 row in set (0.00 sec)
```

**serializable不存在**

```shell
mysql> truncate table t_balance;
Query OK, 0 rows affected (0.02 sec)

mysql> set session transaction isolation level serializable;
Query OK, 0 rows affected (0.00 sec)

mysql> select @@transaction_isolation;
+-------------------------+
| @@transaction_isolation |
+-------------------------+
| SERIALIZABLE            |
+-------------------------+
1 row in set (0.00 sec)

mysql> start transaction;
Query OK, 0 rows affected (0.00 sec)
mysql> select * from t_balance;
Empty set (0.00 sec)

# session2
mysql> start transaction;
Query OK, 0 rows affected (0.00 sec)

# session2执行下面SQL一直等待状态
mysql> insert into t_balance values(1,'zhangsan',10000);

mysql> insert into t_balance values(1,'zhangsan',10000);
Query OK, 1 row affected (0.00 sec)

mysql> commit;
Query OK, 0 rows affected (0.00 sec)

# session1 commit事务后insert等待结束
mysql> insert into t_balance values(1,'zhangsan',10000);
ERROR 1205 (HY000): Lock wait timeout exceeded; try restarting transaction
# session2
mysql> commit;
Query OK, 0 rows affected (0.00 sec)
```

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

### 慢日志

```shell
# 查看是否启用慢日志
show variables like 'slow_query%';

# 启用慢日志，编辑my.cnf添加如下内容
[mysqld]
slow_query_log=1
long_query_time=1
slow_query_log_file=slow-query.log

mysql> show variables like 'slow_query%';
+---------------------+----------------+
| Variable_name       | Value          |
+---------------------+----------------+
| slow_query_log      | ON             |
| slow_query_log_file | slow-query.log |
+---------------------+----------------+
2 rows in set (0.01 sec)

# 不重启mariadb服务启用slow_query_log功能
# 在配置文件/etc/my.conf.d/server.conf先配置
slow_query_log=0
long_query_time=1
slow_query_log_file=slow-query.log

show global variables like 'slow_query_log';

在mysql cli设置global slow_query_log=on
set global slow_query_log=on;

打开新的mysql连接
select sleep(3)

注意：设置global slow_query_log=on后要打开新的mysql连接，否者旧的mysql连接不会被log
```

### 性能分析show profiles

```shell
# 查看当前数据库是否支持profiling
mysql> select @@have_profiling;
+------------------+
| @@have_profiling |
+------------------+
| YES              |
+------------------+
1 row in set, 1 warning (0.00 sec)

# 判断profiling开关是否开启
mysql> select @@profiling;
+-------------+
| @@profiling |
+-------------+
|           0 |
+-------------+
1 row in set, 1 warning (0.00 sec)

# 开启profiling功能
mysql> set profiling=1;
Query OK, 0 rows affected, 1 warning (0.00 sec)

mysql> select @@profiling;
+-------------+
| @@profiling |
+-------------+
|           1 |
+-------------+
1 row in set, 1 warning (0.00 sec)

# 查看每一条SQL的耗时情况
mysql> show profiles;
+----------+------------+-----------------------+
| Query_ID | Duration   | Query                 |
+----------+------------+-----------------------+
|        1 | 0.00043900 | select @@profiling    |
|        2 | 0.00028625 | select * from tb_user |
|        3 | 0.00050750 | SELECT DATABASE()     |
|        4 | 0.00485225 | show databases        |
|        5 | 0.00383850 | show tables           |
|        6 | 0.00097625 | select * from tb_user |
+----------+------------+-----------------------+
6 rows in set, 1 warning (0.00 sec)

# 查看指定query id的SQL语句各个阶段的耗时情况
mysql> show profile for query 6;
+--------------------------------+----------+
| Status                         | Duration |
+--------------------------------+----------+
| starting                       | 0.000345 |
| Executing hook on transaction  | 0.000012 |
| starting                       | 0.000024 |
| checking permissions           | 0.000014 |
| Opening tables                 | 0.000076 |
| init                           | 0.000017 |
| System lock                    | 0.000029 |
| optimizing                     | 0.000011 |
| statistics                     | 0.000036 |
| preparing                      | 0.000038 |
| executing                      | 0.000176 |
| end                            | 0.000011 |
| query end                      | 0.000008 |
| waiting for handler commit     | 0.000018 |
| closing tables                 | 0.000018 |
| freeing items                  | 0.000039 |
| cleaning up                    | 0.000105 |
+--------------------------------+----------+
17 rows in set, 1 warning (0.00 sec)

# 查看指定query id的SQL cpu的使用情况
mysql> show profile cpu for query 6;
+--------------------------------+----------+----------+------------+
| Status                         | Duration | CPU_user | CPU_system |
+--------------------------------+----------+----------+------------+
| starting                       | 0.000345 | 0.000083 |   0.000108 |
| Executing hook on transaction  | 0.000012 | 0.000004 |   0.000006 |
| starting                       | 0.000024 | 0.000011 |   0.000014 |
| checking permissions           | 0.000014 | 0.000006 |   0.000008 |
| Opening tables                 | 0.000076 | 0.000045 |   0.000059 |
| init                           | 0.000017 | 0.000008 |   0.000010 |
| System lock                    | 0.000029 | 0.000012 |   0.000016 |
| optimizing                     | 0.000011 | 0.000005 |   0.000006 |
| statistics                     | 0.000036 | 0.000016 |   0.000021 |
| preparing                      | 0.000038 | 0.000016 |   0.000021 |
| executing                      | 0.000176 | 0.000089 |   0.000118 |
| end                            | 0.000011 | 0.000005 |   0.000005 |
| query end                      | 0.000008 | 0.000003 |   0.000005 |
| waiting for handler commit     | 0.000018 | 0.000008 |   0.000010 |
| closing tables                 | 0.000018 | 0.000008 |   0.000010 |
| freeing items                  | 0.000039 | 0.000016 |   0.000022 |
| cleaning up                    | 0.000105 | 0.000046 |   0.000060 |
+--------------------------------+----------+----------+------------+
17 rows in set, 1 warning (0.01 sec)
```

### 性能分析explain

> explain执行计划各个字段含义：
>
> id：select查询的序列号，表示查询中执行select子句或者是操作表的顺序（id相同，执行顺序从上到下执行；id不同，值越大，越先执行）。
>
> select_type：https://www.cnblogs.com/joimages/p/14521966.html，表示select的类型，常见的取值有SIMPLE（查询语句中不包含`UNION`或者子查询的查询都算作是`SIMPLE`类型，无论是单表查询还是联合查询这些查询的级别都是 simple。顾名思义，这些查询都被 MySQL 认为是比较简单的查询模式。）、PRIMARY（对于包含UNION、UNION ALL或者子查询的大查询来说，它是由几个小查询组成的，其中最左边的那个查询的select_type值就是PRIMARY）、UNION（包含UNION、UNION ALL或者子查询的大查询来说，它是由几个小查询组成的嘛。除了第一个是 PRIMARY，其他的都是 UNION）、UNION RESULT（如果 MySQL 中的 UNION 需要用到临时表进行去重的话，那么这个小查询的级别就是 UNION RESULT）、SUBQUERY（如果我们的子查询不能转换对应 semi-join的形式，而且这个查询不是相关子查询的话，并且查询优化器决定采用将该子查询物化的方案来执行该子查询时，这个时候该子查询的第一个 SELECT 的级别就是 SUBQUERY）
>
> type：表示连接类型，性能有好到差的连接类型为NULL、system、const、eq_ref、ref、range、index（用了索引但是需要对整个索引遍历扫描）、all。
>
> possible_key：表示可能应用在这个查询上的索引，一个或者多个。
>
> key：表示查询中实际用到的索引，NULL为没有用到任何索引。
>
> key_len：表示索引中使用的字节数，该值为索引字段最大可能长度，并非实际使用的长度，在不损失精确性的前提下，长度越短越好。
>
> rows：MySQL认为必须要执行查询的行数，在InnoDB引擎的表中，是一个估计值，可能并不总是准确的。
>
> filtered：表示返回结果的行数占总读取行数的百分比，filtered的值越大越好。

```shell
mysql> CREATE DATABASE IF NOT EXISTS testdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
Query OK, 1 row affected (0.01 sec)

mysql> use testdb;
Database changed

create table if not exists course(
  id bigint primary key auto_increment,
  name varchar(64) not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

create table if not exists student(
  id bigint primary key auto_increment,
  name varchar(64) not null,
  `no` varchar(64) not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

create table if not exists student_course(
  id bigint primary key auto_increment,
  studentid bigint not null,
  courseid bigint not null,
  constraint fk_studentCourse_studentId foreign key (studentid) references student(id),
  constraint fk_studentCourse_courseid foreign key (courseid) references course(id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

insert into course values
(1,'Java'),
(2,'PHP'),
(3,'MySQL'),
(4,'Hadoop');

insert into student values
(1,'黛倚丝','2000100101'),
(2,'谢逊','2000100102'),
(3,'昕天正','2000100103'),
(4,'韦一笑','2000100104');

insert into student_course values
(1,1,1),
(2,1,2),
(3,1,3),
(4,2,2),
(5,2,3),
(6,3,4);

# 执行计划id相同，执行顺序从上到下执行
# id都等于1,先执行student，再执行student_course，在执行course
mysql> explain select s.*,c.* from student s,student_course sc,course c where s.id=sc.studentid and c.id=sc.courseid;
+----+-------------+-------+------------+--------+------------------------------------------------------+----------------------------+---------+--------------------+------+----------+-------+
| id | select_type | table | partitions | type   | possible_keys                                        | key                        | key_len | ref                | rows | filtered | Extra |
+----+-------------+-------+------------+--------+------------------------------------------------------+----------------------------+---------+--------------------+------+----------+-------+
|  1 | SIMPLE      | s     | NULL       | ALL    | PRIMARY                                              | NULL                       | NULL    | NULL               |    4 |   100.00 | NULL  |
|  1 | SIMPLE      | sc    | NULL       | ref    | fk_studentCourse_studentId,fk_studentCourse_courseid | fk_studentCourse_studentId | 8       | testdb.s.id        |    1 |   100.00 | NULL  |
|  1 | SIMPLE      | c     | NULL       | eq_ref | PRIMARY                                              | PRIMARY                    | 8       | testdb.sc.courseid |    1 |   100.00 | NULL  |
+----+-------------+-------+------------+--------+------------------------------------------------------+----------------------------+---------+--------------------+------+----------+-------+
3 rows in set, 1 warning (0.00 sec)

# 执行计划id不同，值越大，越先执行
# 先执行course子查询，在执行student查询，最后执行stuent_course查询
mysql> explain select s.* from student s where s.id in(select sc.studentid from student_course sc where sc.courseid=(select c.id from course c where c.name='MySQL'));
+----+-------------+-------+------------+------+------------------------------------------------------+----------------------------+---------+-------------+------+----------+----------------------------+
| id | select_type | table | partitions | type | possible_keys                                        | key                        | key_len | ref         | rows | filtered | Extra                      |
+----+-------------+-------+------------+------+------------------------------------------------------+----------------------------+---------+-------------+------+----------+----------------------------+
|  1 | PRIMARY     | s     | NULL       | ALL  | PRIMARY                                              | NULL                       | NULL    | NULL        |    4 |   100.00 | NULL                       |
|  1 | PRIMARY     | sc    | NULL       | ref  | fk_studentCourse_studentId,fk_studentCourse_courseid | fk_studentCourse_studentId | 8       | testdb.s.id |    1 |    33.33 | Using where; FirstMatch(s) |
|  3 | SUBQUERY    | c     | NULL       | ALL  | NULL                                                 | NULL                       | NULL    | NULL        |    4 |    25.00 | Using where                |
+----+-------------+-------+------------+------+------------------------------------------------------+----------------------------+---------+-------------+------+----------+----------------------------+
3 rows in set, 1 warning (0.00 sec)
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

## 锁

> 按照锁粒度分为以下三类：
>
> 全局锁：锁定数据库中的所有表
>
> 表级锁：锁定数据表
>
> 行级锁：锁定对应的行数据

### 各类SQL执行触发加锁时相关锁信息表数据

#### 测试环境准备

```
mysql> CREATE DATABASE IF NOT EXISTS testdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
Query OK, 1 row affected (0.01 sec)

mysql> use testdb;
Database changed

create table if not exists course(
 id bigint primary key not null auto_increment,
 name varchar(128) not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

insert into course(id,name) values (1,'java'),(2,'php'),(3,'c'),(4,'python');
```



#### **flush tables with read lock**

> 加全局锁

```
mysql> flush tables with read lock;
Query OK, 0 rows affected (0.00 sec)

mysql> select * from performance_schema.metadata_locks where owner_thread_id!=sys.ps_thread_id(connection_id())\G;
*************************** 1. row ***************************
          OBJECT_TYPE: GLOBAL
        OBJECT_SCHEMA: NULL
          OBJECT_NAME: NULL
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139734949679568
            LOCK_TYPE: SHARED
        LOCK_DURATION: EXPLICIT # 显式上锁
          LOCK_STATUS: GRANTED
               SOURCE: lock.cc:1051
      OWNER_THREAD_ID: 82
       OWNER_EVENT_ID: 37
*************************** 2. row ***************************
          OBJECT_TYPE: COMMIT
        OBJECT_SCHEMA: NULL
          OBJECT_NAME: NULL
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139734950256192
            LOCK_TYPE: SHARED
        LOCK_DURATION: EXPLICIT # 显式上锁
          LOCK_STATUS: GRANTED
               SOURCE: lock.cc:1126
      OWNER_THREAD_ID: 82
       OWNER_EVENT_ID: 37
2 rows in set (0.01 sec)

ERROR: 
No query specified

mysql> unlock tables;
Query OK, 0 rows affected (0.01 sec)
```

#### lock tables course read

> 加表级共享读MDL锁（表级S锁）

```
mysql> lock tables course read;
Query OK, 0 rows affected (0.00 sec)

mysql> select * from performance_schema.metadata_locks where owner_thread_id!=sys.ps_thread_id(connection_id())\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139734949219536
            LOCK_TYPE: SHARED_READ_ONLY
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 82
       OWNER_EVENT_ID: 51
1 row in set (0.00 sec)

ERROR: 
No query specified

mysql> unlock tables;
Query OK, 0 rows affected (0.00 sec)
```

#### lock tables course write

> 加表级排他写MDL锁（表级X锁）

```
mysql> lock tables course write;
Query OK, 0 rows affected (0.01 sec)

mysql> select * from performance_schema.metadata_locks where owner_thread_id!=sys.ps_thread_id(connection_id())\G;
*************************** 1. row ***************************
          OBJECT_TYPE: GLOBAL
        OBJECT_SCHEMA: NULL
          OBJECT_NAME: NULL
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139734949219536
            LOCK_TYPE: INTENTION_EXCLUSIVE
        LOCK_DURATION: STATEMENT
          LOCK_STATUS: GRANTED
               SOURCE: sql_base.cc:5459
      OWNER_THREAD_ID: 82
       OWNER_EVENT_ID: 54
*************************** 2. row ***************************
          OBJECT_TYPE: SCHEMA
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: NULL
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139734949416064
            LOCK_TYPE: INTENTION_EXCLUSIVE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_base.cc:5446
      OWNER_THREAD_ID: 82
       OWNER_EVENT_ID: 54
*************************** 3. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139734949266832
            LOCK_TYPE: SHARED_NO_READ_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 82
       OWNER_EVENT_ID: 54
*************************** 4. row ***************************
          OBJECT_TYPE: TABLESPACE
        OBJECT_SCHEMA: NULL
          OBJECT_NAME: testdb/course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139734950331488
            LOCK_TYPE: INTENTION_EXCLUSIVE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: lock.cc:804
      OWNER_THREAD_ID: 82
       OWNER_EVENT_ID: 54
4 rows in set (0.00 sec)

ERROR: 
No query specified

mysql> unlock tables;
Query OK, 0 rows affected (0.00 sec)
```

#### select

> 加表级共享读MDL锁（表级S锁）

```
mysql> begin;
Query OK, 0 rows affected (0.00 sec)

mysql> select * from course;
+----+-------------+------+
| id | name        | test |
+----+-------------+------+
|  1 | java100     |    0 |
|  2 | php         |    0 |
|  3 | hello       |    0 |
|  4 | python      |    0 |
|  6 | hello world |    0 |
+----+-------------+------+
5 rows in set (0.00 sec)

mysql> commit;
Query OK, 0 rows affected (0.00 sec)

mysql> select * from performance_schema.metadata_locks where owner_thread_id!=sys.ps_thread_id(connection_id())\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139734949203600
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 82
       OWNER_EVENT_ID: 63
1 row in set (0.00 sec)

ERROR: 
No query specified
```

#### select ... lock in share mode

> 加表级IS锁、表级shared_read MDL锁、行级S锁。

```
mysql> begin;
Query OK, 0 rows affected (0.00 sec)

mysql> select * from course where id=1 lock in share mode;
+----+---------+
| id | name    |
+----+---------+
|  1 | java100 |
+----+---------+
1 row in set (0.00 sec)

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:1070:139737096601984
ENGINE_TRANSACTION_ID: 421212186345472
            THREAD_ID: 88
             EVENT_ID: 34
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139737096601984
            LOCK_TYPE: TABLE
            LOCK_MODE: IS
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:5:4:2:139737096599072
ENGINE_TRANSACTION_ID: 421212186345472
            THREAD_ID: 88
             EVENT_ID: 34
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 139737096599072
            LOCK_TYPE: RECORD
            LOCK_MODE: S,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 1
2 rows in set (0.00 sec)

ERROR: 
No query specified

mysql> select * from performance_schema.metadata_locks where owner_thread_id!=sys.ps_thread_id(connection_id())\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139734882067616
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 88
       OWNER_EVENT_ID: 34
1 row in set (0.00 sec)

ERROR: 
No query specified

mysql> commit;
Query OK, 0 rows affected (0.00 sec)
```

#### select ... for update

> 加表级IX锁、表级shared_write MDL锁、行级X锁。

```
mysql> begin;
Query OK, 0 rows affected (0.00 sec)

mysql> select * from course where id=1 for update;
+----+---------+
| id | name    |
+----+---------+
|  1 | java100 |
+----+---------+
1 row in set (0.00 sec)

mysql> select * from performance_schema.metadata_locks where owner_thread_id!=sys.ps_thread_id(connection_id())\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139734882082912
            LOCK_TYPE: SHARED_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 88
       OWNER_EVENT_ID: 54
1 row in set (0.01 sec)

ERROR: 
No query specified

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:1070:139737096601984
ENGINE_TRANSACTION_ID: 1985
            THREAD_ID: 88
             EVENT_ID: 54
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139737096601984
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:5:4:10:139737096599072
ENGINE_TRANSACTION_ID: 1985
            THREAD_ID: 88
             EVENT_ID: 54
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 139737096599072
            LOCK_TYPE: RECORD
            LOCK_MODE: X,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 1
2 rows in set (0.00 sec)

ERROR: 
No query specified

mysql> rollback;
Query OK, 0 rows affected (0.00 sec)
```

#### update where主键

> 加表级IX锁、表级shared_write MDL锁、行级X锁。

```
mysql> begin;
Query OK, 0 rows affected (0.00 sec)

mysql> update course set name='1111' where id=1;
Query OK, 1 row affected (0.00 sec)
Rows matched: 1  Changed: 1  Warnings: 0

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:1070:139737096601984
ENGINE_TRANSACTION_ID: 1987
            THREAD_ID: 88
             EVENT_ID: 62
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139737096601984
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:5:4:10:139737096599072
ENGINE_TRANSACTION_ID: 1987
            THREAD_ID: 88
             EVENT_ID: 62
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 139737096599072
            LOCK_TYPE: RECORD
            LOCK_MODE: X,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 1
2 rows in set (0.00 sec)

ERROR: 
No query specified

mysql> select * from performance_schema.metadata_locks where owner_thread_id!=sys.ps_thread_id(connection_id())\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139734882082912
            LOCK_TYPE: SHARED_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 88
       OWNER_EVENT_ID: 62
1 row in set (0.00 sec)

ERROR: 
No query specified

mysql> rollback;
Query OK, 0 rows affected (0.00 sec)
```

#### update where没有使用索引

> 由于没有使用索引，行级X锁会退化为锁定所有行X锁。
>
> 加表级IX锁、表级shared_write MDL锁、所有行级X锁。

```
mysql> select * from course;
+----+-------------+
| id | name        |
+----+-------------+
|  1 | java100     |
|  2 | php         |
|  3 | hello       |
|  4 | python      |
|  6 | hello world |
|  7 | java1       |
|  8 | java2       |
+----+-------------+
7 rows in set (0.00 sec)

mysql> begin;
Query OK, 0 rows affected (0.00 sec)

mysql> update course set name='111' where name='java100';
Query OK, 1 row affected (0.00 sec)
Rows matched: 1  Changed: 1  Warnings: 0

mysql> select * from performance_schema.metadata_locks where owner_thread_id!=sys.ps_thread_id(connection_id())\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139734882067616
            LOCK_TYPE: SHARED_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 88
       OWNER_EVENT_ID: 80
1 row in set (0.01 sec)

ERROR: 
No query specified

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:1070:139737096601984
ENGINE_TRANSACTION_ID: 1996
            THREAD_ID: 88
             EVENT_ID: 80
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139737096601984
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:5:4:1:139737096599072
ENGINE_TRANSACTION_ID: 1996
            THREAD_ID: 88
             EVENT_ID: 80
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 139737096599072
            LOCK_TYPE: RECORD
            LOCK_MODE: X
          LOCK_STATUS: GRANTED
            LOCK_DATA: supremum pseudo-record
*************************** 3. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:5:4:3:139737096599072
ENGINE_TRANSACTION_ID: 1996
            THREAD_ID: 88
             EVENT_ID: 80
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 139737096599072
            LOCK_TYPE: RECORD
            LOCK_MODE: X
          LOCK_STATUS: GRANTED
            LOCK_DATA: 2
*************************** 4. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:5:4:4:139737096599072
ENGINE_TRANSACTION_ID: 1996
            THREAD_ID: 88
             EVENT_ID: 80
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 139737096599072
            LOCK_TYPE: RECORD
            LOCK_MODE: X
          LOCK_STATUS: GRANTED
            LOCK_DATA: 3
*************************** 5. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:5:4:5:139737096599072
ENGINE_TRANSACTION_ID: 1996
            THREAD_ID: 88
             EVENT_ID: 80
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 139737096599072
            LOCK_TYPE: RECORD
            LOCK_MODE: X
          LOCK_STATUS: GRANTED
            LOCK_DATA: 4
*************************** 6. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:5:4:6:139737096599072
ENGINE_TRANSACTION_ID: 1996
            THREAD_ID: 88
             EVENT_ID: 80
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 139737096599072
            LOCK_TYPE: RECORD
            LOCK_MODE: X
          LOCK_STATUS: GRANTED
            LOCK_DATA: 6
*************************** 7. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:5:4:7:139737096599072
ENGINE_TRANSACTION_ID: 1996
            THREAD_ID: 88
             EVENT_ID: 80
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 139737096599072
            LOCK_TYPE: RECORD
            LOCK_MODE: X
          LOCK_STATUS: GRANTED
            LOCK_DATA: 7
*************************** 8. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:5:4:8:139737096599072
ENGINE_TRANSACTION_ID: 1996
            THREAD_ID: 88
             EVENT_ID: 80
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 139737096599072
            LOCK_TYPE: RECORD
            LOCK_MODE: X
          LOCK_STATUS: GRANTED
            LOCK_DATA: 8
*************************** 9. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:5:4:11:139737096599072
ENGINE_TRANSACTION_ID: 1996
            THREAD_ID: 88
             EVENT_ID: 80
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 139737096599072
            LOCK_TYPE: RECORD
            LOCK_MODE: X
          LOCK_STATUS: GRANTED
            LOCK_DATA: 1
9 rows in set (0.00 sec)

ERROR: 
No query specified

mysql> rollback;
Query OK, 0 rows affected (0.00 sec)
```

#### update where使用非主键和唯一索引

> 加表级IX锁、表级shared_write MDL锁、行级X锁。

```
mysql> select * from course;
+----+-------------+
| id | name        |
+----+-------------+
|  1 | java100     |
|  2 | php         |
|  3 | hello       |
|  4 | python      |
|  6 | hello world |
|  7 | java1       |
|  8 | java2       |
+----+-------------+
7 rows in set (0.00 sec)

mysql> create index idx_course_name on course(name);
Query OK, 0 rows affected (0.03 sec)
Records: 0  Duplicates: 0  Warnings: 0

mysql> begin;
Query OK, 0 rows affected (0.00 sec)

mysql> update course set name='111' where name='java100';
Query OK, 1 row affected (0.00 sec)
Rows matched: 1  Changed: 1  Warnings: 0

mysql> select * from performance_schema.metadata_locks where owner_thread_id!=sys.ps_thread_id(connection_id())\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139734882072192
            LOCK_TYPE: SHARED_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 88
       OWNER_EVENT_ID: 86
1 row in set (0.01 sec)

ERROR: 
No query specified

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:1070:139737096601984
ENGINE_TRANSACTION_ID: 2006
            THREAD_ID: 88
             EVENT_ID: 86
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139737096601984
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:5:5:5:139737096599072
ENGINE_TRANSACTION_ID: 2006
            THREAD_ID: 88
             EVENT_ID: 86
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: idx_course_name
OBJECT_INSTANCE_BEGIN: 139737096599072
            LOCK_TYPE: RECORD
            LOCK_MODE: X
          LOCK_STATUS: GRANTED
            LOCK_DATA: 'java100', 1
*************************** 3. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:5:4:12:139737096599416
ENGINE_TRANSACTION_ID: 2006
            THREAD_ID: 88
             EVENT_ID: 86
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 139737096599416
            LOCK_TYPE: RECORD
            LOCK_MODE: X,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 1
*************************** 4. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:5:5:6:139737096599760
ENGINE_TRANSACTION_ID: 2006
            THREAD_ID: 88
             EVENT_ID: 86
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: idx_course_name
OBJECT_INSTANCE_BEGIN: 139737096599760
            LOCK_TYPE: RECORD
            LOCK_MODE: X,GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 'java2', 8
4 rows in set (0.00 sec)

ERROR: 
No query specified

mysql> rollback;
Query OK, 0 rows affected (0.00 sec)
```

#### update where使用唯一索引



#### delete

> 加表级IX锁、表级shared_write MDL锁、行级X锁。

```
mysql> begin;
Query OK, 0 rows affected (0.00 sec)

mysql> delete from course where id=1;
Query OK, 1 row affected (0.00 sec)

mysql> select * from performance_schema.metadata_locks where owner_thread_id!=sys.ps_thread_id(connection_id())\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139734882067616
            LOCK_TYPE: SHARED_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 88
       OWNER_EVENT_ID: 66
1 row in set (0.00 sec)

ERROR: 
No query specified

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:1070:139737096601984
ENGINE_TRANSACTION_ID: 1989
            THREAD_ID: 88
             EVENT_ID: 66
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139737096601984
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:5:4:11:139737096599072
ENGINE_TRANSACTION_ID: 1989
            THREAD_ID: 88
             EVENT_ID: 66
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 139737096599072
            LOCK_TYPE: RECORD
            LOCK_MODE: X,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 1
2 rows in set (0.00 sec)

ERROR: 
No query specified

mysql> rollback;
Query OK, 0 rows affected (0.00 sec)
```

#### insert

> 加表级IX锁、表级shared_write MDL锁。

```
mysql> begin;
Query OK, 0 rows affected (0.00 sec)

mysql> insert into course values(null,'1111');
Query OK, 1 row affected (0.00 sec)

mysql> select * from performance_schema.metadata_locks where owner_thread_id!=sys.ps_thread_id(connection_id())\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139734882082912
            LOCK_TYPE: SHARED_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 88
       OWNER_EVENT_ID: 70
1 row in set (0.00 sec)

ERROR: 
No query specified

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:1070:139737096601984
ENGINE_TRANSACTION_ID: 1991
            THREAD_ID: 88
             EVENT_ID: 70
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139737096601984
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
1 row in set (0.00 sec)

ERROR: 
No query specified

mysql> rollback;
Query OK, 0 rows affected (0.00 sec)
```



### 全局锁

> 全局锁就是对整个数据库实例加锁，加锁后整个实例就处于只读状态，后续的dml、ddl语句都将被阻塞。
>
> 其典型的使用场景是做全库的逻辑备份，对所有的表进行锁定，从而获取一致性视图，保证数据的完整性。

### 表级锁

#### 表锁

> 表锁分为两类：表共享读锁（所有session都能够读取数据但是不能修改数据，也称为表级S锁）、表排他写锁write lock（本session能够读写数据，其他session读写数据会被阻塞，也称为表级X锁）

**demo测试表级共享读锁和表级排他写锁使用**

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

# 表共享读锁演示
mysql> lock tables course read;
Query OK, 0 rows affected (0.00 sec)

mysql> update course set name='java3' where id=1;
ERROR 1099 (HY000): Table 'course' was locked with a READ lock and can't be updated

# session2更新数据被阻塞直到session1 unlock tables;
mysql> update course set name='java3' where id=1;
Query OK, 1 row affected (8.04 sec)
Rows matched: 1  Changed: 1  Warnings: 0

mysql> unlock tables;
Query OK, 0 rows affected (0.00 sec)

# 表独占写锁
mysql> lock tables course write;
Query OK, 0 rows affected (0.00 sec)

mysql> select * from course;
+----+--------+
| id | name   |
+----+--------+
|  1 | java3  |
|  2 | php    |
|  3 | c1     |
|  4 | python |
+----+--------+
4 rows in set (0.00 sec)

mysql> update course set name='java5' where id=1;
Query OK, 1 row affected (0.00 sec)
Rows matched: 1  Changed: 1  Warnings: 0

# session2读取course一直被阻塞直到session1 unlock tables;
mysql> select * from course;
+----+--------+
| id | name   |
+----+--------+
|  1 | java5  |
|  2 | php    |
|  3 | c1     |
|  4 | python |
+----+--------+
4 rows in set (4.97 sec)

mysql> unlock tables;
Query OK, 0 rows affected (0.00 sec)
```

#### 元数据锁（meta data lock，MDL）

> MDL加锁过程是系统自动控制，无需显示使用，在访问一张表的时候会自动加上。MDL锁主要作用是维护表元数据的数据一致性，在表上有活动事务的时候，不可以对表元数据进行写入操作。为了避免DML和DDL冲突，保证读写的正确性。
>
> 在MySQL5.5中引入了MDL，当对一张表进行增删改查的时候，自动加MDL读锁（共享）。当对表结构进行变更操作时候，自动加MDL写锁（排他）。
>
> select、select ... lock in share mode自动加入 shared_read MDL锁。
>
> update、delete、insert、select ... for update自动加入shared_write MDL锁。
>
> alter table 自动加入exclusive排他MDL锁。
>
> shared_read MDL和shared_write MDL锁不冲突
>
> shared_read、shared_write和exclusive MDL锁冲突



```shell
# 查看当前MDL锁
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: performance_schema
          OBJECT_NAME: metadata_locks
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139734949992928
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 70
       OWNER_EVENT_ID: 16
1 row in set (0.00 sec)

ERROR: 
No query specified

# 演示select语句自动加入shared_read MDL锁
mysql> start transaction;
Query OK, 0 rows affected (0.00 sec)

mysql> select * from course;
+----+--------+
| id | name   |
+----+--------+
|  1 | java5  |
|  2 | php    |
|  3 | c1     |
|  4 | python |
+----+--------+
4 rows in set (0.00 sec)
mysql> select * from course lock in share mode;
+----+--------+
| id | name   |
+----+--------+
|  1 | java5  |
|  2 | php    |
|  3 | c1     |
|  4 | python |
+----+--------+
4 rows in set (0.00 sec)

mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139735419844000
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 71
       OWNER_EVENT_ID: 12

mysql> commit;
Query OK, 0 rows affected (0.00 sec)

# 演示delete、update、insert、select ... for update语句自动加入shared_write MDL锁
mysql> start transaction;
Query OK, 0 rows affected (0.00 sec)

mysql> update course set name='java' where id=1;
Query OK, 1 row affected (0.00 sec)
Rows matched: 1  Changed: 1  Warnings: 0

mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139735418820368
            LOCK_TYPE: SHARED_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 71
       OWNER_EVENT_ID: 24

mysql> commit;
Query OK, 0 rows affected (0.00 sec)

# 演示alter table exclusive MDL排他锁和select语句的shared_read共享读MDL锁冲突情景
# 演示shared_read、shared_write和exclusive MDL锁冲突
mysql> start transaction;
Query OK, 0 rows affected (0.00 sec)

mysql> select * from course;
+----+-------------+
| id | name        |
+----+-------------+
|  1 | java        |
|  2 | php         |
|  3 | hello       |
|  4 | python      |
|  6 | hello world |
+----+-------------+
5 rows in set (0.00 sec)

# session2 执行alter table DDL一直阻塞(如下面显示的pending状态)，因为exclusive MDL排他锁和session1 select语句自动加的shared_read MDL锁冲突
mysql> alter table course add column test int not null;

mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139734949992928
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 70
       OWNER_EVENT_ID: 39
...
*************************** 9. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139735431619088
            LOCK_TYPE: EXCLUSIVE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: PENDING
               SOURCE: mdl.cc:3753
      OWNER_THREAD_ID: 71
       OWNER_EVENT_ID: 52
9 rows in set (0.00 sec)

ERROR: 
No query specified

mysql> commit;
Query OK, 0 rows affected (0.01 sec)

# 演示shared_read MDL和shared_write MDL锁不冲突
mysql> begin;
Query OK, 0 rows affected (0.01 sec)

mysql> select * from course;
+----+-------------+------+
| id | name        | test |
+----+-------------+------+
|  1 | java        |    0 |
|  2 | php         |    0 |
|  3 | hello       |    0 |
|  4 | python      |    0 |
|  6 | hello world |    0 |
+----+-------------+------+
5 rows in set (0.00 sec)

# session2
mysql> begin;
Query OK, 0 rows affected (0.00 sec)
# session2
mysql> update course set name='java100' where id=1;
Query OK, 1 row affected (0.01 sec)
Rows matched: 1  Changed: 1  Warnings: 0

mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139734949936816
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 70
       OWNER_EVENT_ID: 46
*************************** 3. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139735431605824
            LOCK_TYPE: SHARED_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 71
       OWNER_EVENT_ID: 56
3 rows in set (0.00 sec)

ERROR: 
No query specified

mysql> commit;
Query OK, 0 rows affected (0.00 sec)

# session2
mysql> commit;
Query OK, 0 rows affected (0.00 sec)
```

#### 意向锁

> 意向锁分为：意向共享锁（IS）、意向排他锁（IX）。
>
> 意向锁作用：意向锁是在当事务加表锁时发挥作用。比如一个事务想要对表加排他锁，如果没有意向锁的话，那么该事务在加锁前需要判断当前表的每一行是否已经加了锁，如果表很大，遍历每行进行判断需要耗费大量的时间。如果使用意向锁的话，那么加表锁前，只需要判断当前表是否有意向锁即可，这样加快了对表锁的处理速度。
>
> 意向锁和表锁之间的兼容性：
>
> - IS锁：与表级S锁兼容，与表级X锁互斥
> - IX锁：与表级S锁和表级X锁都互斥
>
> 意向锁和意向锁之间是兼容的。

##### 演示IS锁和表级S锁兼容

```
mysql1> begin;
Query OK, 0 rows affected (0.00 sec)

mysql1> select * from course where id=1 lock in share mode;
+----+---------+
| id | name    |
+----+---------+
|  1 | java100 |
+----+---------+
1 row in set (0.00 sec)

# lock table course read;不会阻塞因为IS锁和表级S锁兼容
mysql2> lock table course read;
Query OK, 0 rows affected (0.00 sec)
mysql2> unlock table;
Query OK, 0 rows affected (0.00 sec)

mysql1> commit;
Query OK, 0 rows affected (0.00 sec)
```

##### 演示IS锁和表级X锁互斥

```
mysql1> begin;
Query OK, 0 rows affected (0.00 sec)

mysql1> select * from course where id=1 lock in share mode;
+----+---------+
| id | name    |
+----+---------+
|  1 | java100 |
+----+---------+
1 row in set (0.00 sec)

# lock table course write;一直被阻塞，因为IS锁和表级X锁互斥
mysql2> lock table course write;
Query OK, 0 rows affected (4.37 sec)

mysql1> commit;
Query OK, 0 rows affected (0.00 sec)

mysql2> unlock table;
Query OK, 0 rows affected (0.00 sec)
```

##### 演示IX锁和表级S、X锁互斥

```
mysql1> begin;
Query OK, 0 rows affected (0.00 sec)

mysql1> select * from course where id=1 for update;
+----+---------+
| id | name    |
+----+---------+
|  1 | java100 |
+----+---------+
1 row in set (0.00 sec)

# 下面SQL被阻塞，因为IX锁和表级S锁互斥
mysql2> lock table course read;

mysql1> commit;
Query OK, 0 rows affected (0.00 sec)

mysql2> unlock table;
Query OK, 0 rows affected (0.00 sec)
```

### 行级锁

> 每次操作锁住对应的行数据。锁定粒度最小，发生锁冲突的概率最低，并发度最高。应用在InnoDB存储引擎中,MyISAM不支持行级锁
>
> InnoDB的数据是基于索引组织的，行锁是通过对索引上的索引项加锁来实现的，而不是对记录加的锁。

#### 行锁（Record Lock）

> 锁定单个行记录的锁，防止其他事务对此行进行update和delete。在RC、RR隔离级别下都支持。（ RR:read committed、RC:repeatable read）
>
> InnoDB实现了以下两种类型的行锁：
> 共享锁（S）：允许一个事务去读一行，阻止其他事务获得相同数据集的排它锁。
> 排他锁（X）：允许获取排他锁的事务更新数据，阻止其他事务获得相同数据集的共享锁和排他锁。
> 注：行锁中的共享锁在lock_mode字段中叫"S,REC_NOT_GAP",
> 行锁中的排他锁在lock_mode字段中分别叫"X,REC_NOT_GAP"
>
> 
>
> 共享锁和共享锁之间是兼容的
>
> 共享锁和排他锁之间是互斥的
>
> 排他锁和排他锁之间是互斥的

##### 演示共享锁和共享锁之间是兼容

```
mysql1> begin;
Query OK, 0 rows affected (0.01 sec)
mysql1> select * from course where id=1 lock in share mode;
+----+---------+
| id | name    |
+----+---------+
|  1 | java100 |
+----+---------+
1 row in set (0.00 sec)
mysql1> rollback;
Query OK, 0 rows affected (0.00 sec)

mysql2> begin;
Query OK, 0 rows affected (0.01 sec)
mysql2> select * from course where id=1 lock in share mode;
+----+---------+
| id | name    |
+----+---------+
|  1 | java100 |
+----+---------+
1 row in set (0.00 sec)
mysql2> rollback;
Query OK, 0 rows affected (0.00 sec)

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:1070:139737096601984
ENGINE_TRANSACTION_ID: 421212186345472
            THREAD_ID: 88
             EVENT_ID: 98
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139737096601984
            LOCK_TYPE: TABLE
            LOCK_MODE: IS
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:5:4:14:139737096599072
ENGINE_TRANSACTION_ID: 421212186345472
            THREAD_ID: 88
             EVENT_ID: 98
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 139737096599072
            LOCK_TYPE: RECORD
            LOCK_MODE: S,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 1
*************************** 3. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209635624:1070:139737096607968
ENGINE_TRANSACTION_ID: 421212186346280
            THREAD_ID: 89
             EVENT_ID: 30
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139737096607968
            LOCK_TYPE: TABLE
            LOCK_MODE: IS
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 4. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209635624:5:4:14:139737096605056
ENGINE_TRANSACTION_ID: 421212186346280
            THREAD_ID: 89
             EVENT_ID: 30
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 139737096605056
            LOCK_TYPE: RECORD
            LOCK_MODE: S,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 1
4 rows in set (0.00 sec)

ERROR: 
No query specified
```

##### 演示共享锁和排他锁之间是互斥

```
mysql1> begin;
Query OK, 0 rows affected (0.00 sec)

mysql2> begin;
Query OK, 0 rows affected (0.00 sec)

mysql1> select * from course where id=1 lock in share mode;
+----+---------+
| id | name    |
+----+---------+
|  1 | java100 |
+----+---------+
1 row in set (0.00 sec)

# SQL被阻塞，原因行级S锁和行级X锁互斥
mysql2> select * from course where id=1 for update;
+----+---------+
| id | name    |
+----+---------+
|  1 | java100 |
+----+---------+
1 row in set (4.20 sec)

mysql1> rollback;
Query OK, 0 rows affected (0.00 sec)

mysql2> rollback;
Query OK, 0 rows affected (0.00 sec)

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209635624:1070:139737096607968
ENGINE_TRANSACTION_ID: 2028
            THREAD_ID: 89
             EVENT_ID: 42
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139737096607968
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209635624:5:4:14:139737096605056
ENGINE_TRANSACTION_ID: 2028
            THREAD_ID: 89
             EVENT_ID: 42
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 139737096605056
            LOCK_TYPE: RECORD
            LOCK_MODE: X,REC_NOT_GAP
          LOCK_STATUS: WAITING
            LOCK_DATA: 1
*************************** 3. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:1070:139737096601984
ENGINE_TRANSACTION_ID: 421212186345472
            THREAD_ID: 88
             EVENT_ID: 110
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139737096601984
            LOCK_TYPE: TABLE
            LOCK_MODE: IS
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 4. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:5:4:14:139737096599072
ENGINE_TRANSACTION_ID: 421212186345472
            THREAD_ID: 88
             EVENT_ID: 110
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 139737096599072
            LOCK_TYPE: RECORD
            LOCK_MODE: S,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 1
4 rows in set (0.00 sec)

ERROR: 
No query specified
```

##### 演示共享锁和共享锁之间是互斥

```
mysql1> begin;
Query OK, 0 rows affected (0.00 sec)

mysql2> begin;
Query OK, 0 rows affected (0.00 sec)

mysql1> select * from course where id=1 for update;
+----+---------+
| id | name    |
+----+---------+
|  1 | java100 |
+----+---------+
1 row in set (0.00 sec)

# SQL被阻塞，原因行级X锁和行级X锁互斥
mysql2> select * from course where id=1 for update;
+----+---------+
| id | name    |
+----+---------+
|  1 | java100 |
+----+---------+
1 row in set (18.64 sec)

mysql1> rollback;
Query OK, 0 rows affected (0.00 sec)

mysql2> rollback;
Query OK, 0 rows affected (0.00 sec)

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209635624:1070:139737096607968
ENGINE_TRANSACTION_ID: 2031
            THREAD_ID: 89
             EVENT_ID: 50
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139737096607968
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209635624:5:4:14:139737096605056
ENGINE_TRANSACTION_ID: 2031
            THREAD_ID: 89
             EVENT_ID: 50
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 139737096605056
            LOCK_TYPE: RECORD
            LOCK_MODE: X,REC_NOT_GAP
          LOCK_STATUS: WAITING
            LOCK_DATA: 1
*************************** 3. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:1070:139737096601984
ENGINE_TRANSACTION_ID: 2030
            THREAD_ID: 88
             EVENT_ID: 118
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 139737096601984
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 4. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139737209634816:5:4:14:139737096599072
ENGINE_TRANSACTION_ID: 2030
            THREAD_ID: 88
             EVENT_ID: 118
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 139737096599072
            LOCK_TYPE: RECORD
            LOCK_MODE: X,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 1
4 rows in set (0.00 sec)

ERROR: 
No query specified
```



#### 间隙锁（Gap Lock）

#### 临键锁（Next-Key Lock）

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

