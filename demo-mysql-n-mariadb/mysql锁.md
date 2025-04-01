# 锁

> 注意：下面演示的测试是基于`mysql8.0`的。

## 为何需要学习`mysql`锁呢？

## 学习思路

1. 锁是什么？锁的作用？锁的分类？
2. `mysql`有哪些锁
3. `mysql`查询锁信息的相关系统表
4. 学习`mysql`各个锁
5. 各类`sql`执行时会触发加锁的情况
6. 数据库死锁有哪些情况？
7. 业务开发中有哪些死锁的情况？
8. 如何监控和排查死锁呢？

## 锁的作用

MySQL中的锁概念是为了解决并发访问数据库时可能出现的数据一致性和完整性问题（todo）。

## 锁的分类

MySQL中的锁可以根据不同的维度进行分类，以下是对MySQL锁的主要分类的详细解释：

**1. 按属性分**：

- **共享锁（Shared Lock，S）**：也称为读锁，多个事务可以同时持有共享锁并读取数据，但不能修改数据。适用于同时读取同一数据的场景。
- **排他锁（Exclusive Lock，X）**：也称为写锁，事务持有排它锁时，其他事务无法同时持有共享锁或排它锁，用于保护数据的写操作。

**2. 按状态分**：

- **意向共享锁（Intention Shared Lock，IS）**：表级锁的辅助锁，表示事务要在某个表或页级锁上获取共享锁。
- **意向排他锁（Intention Exclusive Lock，IX）**：表级锁的辅助锁，表示事务要在某个表或页级锁上获取排它锁。意向锁的存在是为了协调行锁和表锁的关系，支持多粒度（表锁与行锁）的锁并存。

**3. 按粒度分**：

- **全局锁（Global Lock）**：对整个数据库实例加锁，让整个库处于只读状态，通常用于数据备份。
- **表级锁（Table Lock）**：对整个表加锁，其他连接无法修改或读取该表的数据，但可以对其他表进行操作。
- **页级锁（Page Lock）**：对数据页（通常是连续的几个行）加锁，控制并发事务对该页的访问。适用于数据较大且并发量较高的场景。
- **行级锁（Row Lock）**：对单个行加锁，只锁定需要修改的数据行，其他行可以被同时修改或读取。并发性高，但锁管理较复杂。记录锁、间隙锁和临键锁都属于行锁。

**4. 按模式分**：

- **乐观锁（Optimistic Locking）**：假设并发操作时不会发生冲突，只在提交事务时检查数据是否被其他事务修改过。常用于读多写少的场景。
- **悲观锁（Pessimistic Locking）**：假设并发操作时会发生冲突，因此在操作期间持有锁来避免冲突。常用于写多读少的场景。

此外，还有**元数据锁（Metadata Lock，MDL）**，它不需要显式使用，在访问表时自动加上，主要目的是防止DDL（数据定义语言）与DML（数据操纵语言）的并发冲突。

总结来说，MySQL的锁机制非常灵活和复杂，可以根据不同的需求和应用场景选择合适的锁类型和策略，以确保数据的一致性和并发性。

当两个事务分别尝试获取共享锁或排他锁时，两种锁的兼容性：共享锁和共享锁兼容、共享锁和排他锁互斥、排他锁和排他锁互斥。

下面示例中将会演示锁如下：

- 全局锁
- 表共享锁（本人精确称为表级共享读锁）
- 表排他锁（本人精确称为表级排他写锁）
- 元数据共享读锁（本人精确称为表级元数据共享读锁）
- 元数据共享写锁（本人精确称为表级元数据共享写锁）
- 元数据独占锁
- 意向共享锁（本人精确称为表级共享读意向锁）
- 意向排他锁（本人精确称为表级排他写意向锁）
- 行共享锁（本人精确称为行级共享读锁）
- 行排他锁（本人精确成为行级排他写锁）
- 间隙锁
- 临键锁

## 为测试作准备

使用`docker compose`运行`mysql8.0`，详细的步骤请参考 <a href="/MySQL/使用docker-compose运行MySQL.html#单机版-mysql" target="_blank">链接</a>

准备测试数据`SQL`脚本如下：

```sql
CREATE DATABASE IF NOT EXISTS testdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

use testdb;

create table if not exists course(
 id bigint primary key,
 name varchar(128) not null,
 age int not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

insert into course(id,name,age) values (5,'java',5),(15,'php',15),(16,'c',15),(31,'python',31);

create index idx_course_age on course(age);

# 设置全局锁等待超时秒数
set global innodb_lock_wait_timeout=3600;
```

## 使用`show engine innodb status`显示当前锁信息

启用`innodb status`打印锁等待信息

```sql
set global innodb_status_output_locks=ON;
```

示例：

```bash
# session1开启事务
begin;

# session2开启事务
begin;
# session2加记录共享锁
select * from course where id=5 lock in share mode;

# session1加记录排他锁，但等待
update course set name='xxx' where id=5;

# 此时查看锁等待信息
show engine innodb status\G;
# 锁等待信息如下：
TRANSACTIONS
------------
Trx id counter 1832
Purge done for trx's n:o < 1831 undo n:o < 0 state: running but idle
History list length 0
LIST OF TRANSACTIONS FOR EACH SESSION:
---TRANSACTION 421989908311256, not started
0 lock struct(s), heap size 1128, 0 row lock(s)
---TRANSACTION 421989908310448, not started
0 lock struct(s), heap size 1128, 0 row lock(s)
---TRANSACTION 421989908309640, not started
0 lock struct(s), heap size 1128, 0 row lock(s)
---TRANSACTION 1831, ACTIVE 5 sec starting index read
mysql tables in use 1, locked 1
LOCK WAIT 2 lock struct(s), heap size 1128, 1 row lock(s)
MySQL thread id 9, OS thread handle 140514761336576, query id 267 172.20.32.1 root updating
# 当前等待锁对应的sql
/* ApplicationName=IntelliJ IDEA 2023.2.5 */ update course set name='xxx' where id=5
------- TRX HAS BEEN WAITING 5 SEC FOR THIS LOCK TO BE GRANTED:
# 尝试加记录排他锁，但等待
RECORD LOCKS space id 2 page no 4 n bits 80 index PRIMARY of table `testdb`.`course` trx id 1831 lock_mode X locks rec but not gap waiting
Record lock, heap no 8 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
 # todo ...
 # 聚簇索引的值
 0: len 8; hex 8000000000000005; asc         ;;
 # 事务id
 1: len 6; hex 000000000714; asc       ;;
 # undo记录
 2: len 7; hex 81000001060110; asc        ;;
 3: len 4; hex 6a617661; asc java;;
 4: len 4; hex 80000005; asc     ;;

------------------
# 当前事务等待(waiting)锁信息，当前尝试加记录排他锁，但等待
TABLE LOCK table `testdb`.`course` trx id 1831 lock mode IX
RECORD LOCKS space id 2 page no 4 n bits 80 index PRIMARY of table `testdb`.`course` trx id 1831 lock_mode X locks rec but not gap waiting
Record lock, heap no 8 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
 0: len 8; hex 8000000000000005; asc         ;;
 1: len 6; hex 000000000714; asc       ;;
 2: len 7; hex 81000001060110; asc        ;;
 3: len 4; hex 6a617661; asc java;;
 4: len 4; hex 80000005; asc     ;;
```



## 锁信息相关系统表

### 锁信息系统表

`mysql8.0`锁信息系统表如下：

- `performance_schema.data_locks`：用于查看行锁信息的

  > 注意：`performance_schema.data_locks`中`LOCK_TYPE='RECORD' and LOCK_DATA='supremum pseudo-record'`的记录表示间隙锁，区间`(聚簇索引最后记录, +infinity]`。

- `performance_schema.data_lock_waits`：在等待其他锁释放的锁信息

- `performance_schema.metadata_locks`：用于查看元数据锁信息的

`mysql5.7`锁信息系统表：`information_schema.innodb_locks`、`information_schema.innodb_lock_waits`、`information_schema.innodb_trx`

### 演示锁信息系统表使用

这个一个简单的示例，演示`data_locks`、`data_lock_waits`、`metadata_locks`表如何记录`mysql`当前锁信息

```bash
# session1开启事务
mysql> begin;
Query OK, 0 rows affected (0.00 sec)

# session1加记录排他锁
mysql> select * from course where id=5 for update;
+----+------+-----+
| id | name | age |
+----+------+-----+
|  5 | java |   5 |
+----+------+-----+
1 row in set (0.00 sec)

# session2尝试更新同一条记录但一直等待锁释放
mysql> update course set name='javaxxx' where id=5;

# 查看锁信息
mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
# update触发加表course意向排他锁
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140105457442816:1067:140105390047616
ENGINE_TRANSACTION_ID: 102493
            THREAD_ID: 53
             EVENT_ID: 38
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140105390047616
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
# update语句触发加行排他锁，LOCK_STATUS=WAITING表示锁在等待其他锁释放状态
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140105457442816:2:4:2:140105390044704
ENGINE_TRANSACTION_ID: 102493
            THREAD_ID: 53
             EVENT_ID: 38
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140105390044704
            LOCK_TYPE: RECORD
            LOCK_MODE: X,REC_NOT_GAP # REC_NOT_GAP表示记录锁
          LOCK_STATUS: WAITING
            LOCK_DATA: 5
*************************** 3. row ***************************
# select for update触发加意向排他锁
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140105457442008:1067:140105390041520
ENGINE_TRANSACTION_ID: 102490
            THREAD_ID: 52
             EVENT_ID: 43
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140105390041520
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 4. row ***************************
# select for update触发加记录排他锁
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140105457442008:2:4:2:140105390038528
ENGINE_TRANSACTION_ID: 102490
            THREAD_ID: 52
             EVENT_ID: 43
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140105390038528
            LOCK_TYPE: RECORD
            LOCK_MODE: X,REC_NOT_GAP # REC_NOT_GAP表示记录锁
          LOCK_STATUS: GRANTED
            LOCK_DATA: 5
4 rows in set (0.00 sec)

ERROR: 
No query specified

mysql> select * from performance_schema.data_lock_waits\G;
*************************** 1. row ***************************
# update语句触发加行排他锁，这个锁在等待其他锁释放状态
                          ENGINE: INNODB
       REQUESTING_ENGINE_LOCK_ID: 140105457442816:2:4:2:140105390044704
REQUESTING_ENGINE_TRANSACTION_ID: 102493
            REQUESTING_THREAD_ID: 53
             REQUESTING_EVENT_ID: 38
REQUESTING_OBJECT_INSTANCE_BEGIN: 140105390044704
         BLOCKING_ENGINE_LOCK_ID: 140105457442008:2:4:2:140105390038528
  BLOCKING_ENGINE_TRANSACTION_ID: 102490
              BLOCKING_THREAD_ID: 52
               BLOCKING_EVENT_ID: 43
  BLOCKING_OBJECT_INSTANCE_BEGIN: 140105390038528
1 row in set (0.00 sec)

ERROR: 
No query specified

mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
# select for upate语句触发加元数据共享写锁
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140103712301200
            LOCK_TYPE: SHARED_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 52
       OWNER_EVENT_ID: 43
*************************** 5. row ***************************
          OBJECT_TYPE: GLOBAL
        OBJECT_SCHEMA: NULL
          OBJECT_NAME: NULL
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140103309653552
            LOCK_TYPE: INTENTION_EXCLUSIVE
        LOCK_DURATION: STATEMENT
          LOCK_STATUS: GRANTED
               SOURCE: sql_base.cc:3041
      OWNER_THREAD_ID: 53
       OWNER_EVENT_ID: 37
*************************** 6. row ***************************
# update语句触发加元数据共享写锁
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140103310231456
            LOCK_TYPE: SHARED_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 53
       OWNER_EVENT_ID: 37
6 rows in set (0.00 sec)

ERROR: 
No query specified

# session1释放锁
mysql> rollback;
Query OK, 0 rows affected (0.00 sec)
```

### `data_locks`表`LOCK_TYPE=RECORD`时`LOCK_MODE`字段解析

- `S/X,REC_NO_GAP`表示记录锁

  ```bash
  mysql> begin;
  Query OK, 0 rows affected (0.00 sec)
  
  mysql> select * from course where id=5 for update;
  +----+------+-----+
  | id | name | age |
  +----+------+-----+
  |  5 | java |   5 |
  +----+------+-----+
  
  mysql> select * from performance_schema.data_locks\G;
  *************************** 2. row ***************************
                 ENGINE: INNODB
         ENGINE_LOCK_ID: 140205853731192:7:4:2:140205784923488
  ENGINE_TRANSACTION_ID: 4003
              THREAD_ID: 47
               EVENT_ID: 48
          OBJECT_SCHEMA: testdb
            OBJECT_NAME: course
         PARTITION_NAME: NULL
      SUBPARTITION_NAME: NULL
             INDEX_NAME: PRIMARY
  OBJECT_INSTANCE_BEGIN: 140205784923488
              LOCK_TYPE: RECORD
              LOCK_MODE: X,REC_NOT_GAP # id=5的记录锁
            LOCK_STATUS: GRANTED
              LOCK_DATA: 5
  ```

  

- `S/X,GAP`表示间隙锁

  ```bash
  mysql> begin;
  Query OK, 0 rows affected (0.00 sec)
  
  mysql> select * from course where age=17 for update;
  Empty set (0.00 sec)
  
  mysql> select * from performance_schema.data_locks\G;
  *************************** 2. row ***************************
                 ENGINE: INNODB
         ENGINE_LOCK_ID: 140205853731192:7:5:5:140205784923488
  ENGINE_TRANSACTION_ID: 4004
              THREAD_ID: 47
               EVENT_ID: 52
          OBJECT_SCHEMA: testdb
            OBJECT_NAME: course
         PARTITION_NAME: NULL
      SUBPARTITION_NAME: NULL
             INDEX_NAME: idx_course_age
  OBJECT_INSTANCE_BEGIN: 140205784923488
              LOCK_TYPE: RECORD
              LOCK_MODE: X,GAP # 间隙锁，区间([15,15],[31,31])，不锁定记录id=31,age=31的记录
            LOCK_STATUS: GRANTED
              LOCK_DATA: 31, 31
  ```

  

- `S`表示临键锁（锁定记录左开右闭区间）

  ```bash
  mysql> begin;
  Query OK, 0 rows affected (0.00 sec)
  
  mysql> select * from course where age>8 and age<17 for update;
  +----+------+-----+
  | id | name | age |
  +----+------+-----+
  | 15 | php  |  15 |
  | 16 | c    |  15 |
  +----+------+-----+
  2 rows in set (0.01 sec)
  
  mysql> select * from performance_schema.data_locks\G;
  *************************** 2. row ***************************
                 ENGINE: INNODB
         ENGINE_LOCK_ID: 140205853731192:7:5:3:140205784923488
  ENGINE_TRANSACTION_ID: 4020
              THREAD_ID: 47
               EVENT_ID: 56
          OBJECT_SCHEMA: testdb
            OBJECT_NAME: course
         PARTITION_NAME: NULL
      SUBPARTITION_NAME: NULL
             INDEX_NAME: idx_course_age
  OBJECT_INSTANCE_BEGIN: 140205784923488
              LOCK_TYPE: RECORD
              LOCK_MODE: X # 临键锁，区间([5,5], [15,15]]
            LOCK_STATUS: GRANTED
              LOCK_DATA: 15, 15
  *************************** 3. row ***************************
                 ENGINE: INNODB
         ENGINE_LOCK_ID: 140205853731192:7:5:4:140205784923488
  ENGINE_TRANSACTION_ID: 4020
              THREAD_ID: 47
               EVENT_ID: 56
          OBJECT_SCHEMA: testdb
            OBJECT_NAME: course
         PARTITION_NAME: NULL
      SUBPARTITION_NAME: NULL
             INDEX_NAME: idx_course_age
  OBJECT_INSTANCE_BEGIN: 140205784923488
              LOCK_TYPE: RECORD
              LOCK_MODE: X # 临键锁，区间([15,15], [16,15]]
            LOCK_STATUS: GRANTED
              LOCK_DATA: 15, 16
  *************************** 4. row ***************************
                 ENGINE: INNODB
         ENGINE_LOCK_ID: 140205853731192:7:5:5:140205784923488
  ENGINE_TRANSACTION_ID: 4020
              THREAD_ID: 47
               EVENT_ID: 56
          OBJECT_SCHEMA: testdb
            OBJECT_NAME: course
         PARTITION_NAME: NULL
      SUBPARTITION_NAME: NULL
             INDEX_NAME: idx_course_age
  OBJECT_INSTANCE_BEGIN: 140205784923488
              LOCK_TYPE: RECORD
              LOCK_MODE: X # 临键锁，区间([16,15], [31,31]]
            LOCK_STATUS: GRANTED
              LOCK_DATA: 31, 31
  *************************** 5. row ***************************
                 ENGINE: INNODB
         ENGINE_LOCK_ID: 140205853731192:7:4:3:140205784923832
  ENGINE_TRANSACTION_ID: 4020
              THREAD_ID: 47
               EVENT_ID: 56
          OBJECT_SCHEMA: testdb
            OBJECT_NAME: course
         PARTITION_NAME: NULL
      SUBPARTITION_NAME: NULL
             INDEX_NAME: PRIMARY
  OBJECT_INSTANCE_BEGIN: 140205784923832
              LOCK_TYPE: RECORD
              LOCK_MODE: X,REC_NOT_GAP # 记录锁id=15
            LOCK_STATUS: GRANTED
              LOCK_DATA: 15
  *************************** 6. row ***************************
                 ENGINE: INNODB
         ENGINE_LOCK_ID: 140205853731192:7:4:4:140205784923832
  ENGINE_TRANSACTION_ID: 4020
              THREAD_ID: 47
               EVENT_ID: 56
          OBJECT_SCHEMA: testdb
            OBJECT_NAME: course
         PARTITION_NAME: NULL
      SUBPARTITION_NAME: NULL
             INDEX_NAME: PRIMARY
  OBJECT_INSTANCE_BEGIN: 140205784923832
              LOCK_TYPE: RECORD
              LOCK_MODE: X,REC_NOT_GAP # 记录锁id=16
            LOCK_STATUS: GRANTED
              LOCK_DATA: 16
  ```

  

## 各类`SQL`执行时触发加锁的情况

> [加锁分析](https://zhuanlan.zhihu.com/p/530275892)
>
> [MySQL记录锁、间隙锁、临键锁（Next-Key Locks）加锁过程](https://www.cnblogs.com/caibaotimes/p/17958671)

### `flush tables with read lock`

> 对整个`MySQL`实例加全局锁

```bash
mysql> flush tables with read lock;
Query OK, 0 rows affected (0.00 sec)

mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
# 全局锁
          OBJECT_TYPE: GLOBAL
        OBJECT_SCHEMA: NULL
          OBJECT_NAME: NULL
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140103712531040
            LOCK_TYPE: SHARED
        LOCK_DURATION: EXPLICIT
          LOCK_STATUS: GRANTED
               SOURCE: lock.cc:1051
      OWNER_THREAD_ID: 52
       OWNER_EVENT_ID: 55
*************************** 2. row ***************************
          OBJECT_TYPE: COMMIT
        OBJECT_SCHEMA: NULL
          OBJECT_NAME: NULL
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140103517431056
            LOCK_TYPE: SHARED
        LOCK_DURATION: EXPLICIT
          LOCK_STATUS: GRANTED
               SOURCE: lock.cc:1126
      OWNER_THREAD_ID: 52
       OWNER_EVENT_ID: 55

ERROR: 
No query specified

mysql> unlock tables;
Query OK, 0 rows affected (0.00 sec)
```

### `lock tables course read`

>对表`course`加元数据共享读锁，其他锁不需要理会。

```bash
# 对表course加表共享锁
mysql> lock tables course read;
Query OK, 0 rows affected (0.00 sec)

mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
# 对表course加元数据共享读锁
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140103712308272
            LOCK_TYPE: SHARED_READ_ONLY
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 52
       OWNER_EVENT_ID: 59
*************************** 2. row ***************************
# 对数据库testdb加意向排他锁
          OBJECT_TYPE: SCHEMA
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: NULL
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140103712301872
            LOCK_TYPE: INTENTION_EXCLUSIVE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: dd_schema.cc:108
      OWNER_THREAD_ID: 52
       OWNER_EVENT_ID: 59
*************************** 3. row ***************************
# 对id字段加元数据共享读锁
          OBJECT_TYPE: COLUMN STATISTICS
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: id
OBJECT_INSTANCE_BEGIN: 140103712310432
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: STATEMENT
          LOCK_STATUS: GRANTED
               SOURCE: sql_base.cc:579
      OWNER_THREAD_ID: 52
       OWNER_EVENT_ID: 59
*************************** 4. row ***************************
# 对name字段加元数据共享读锁
          OBJECT_TYPE: COLUMN STATISTICS
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: name
OBJECT_INSTANCE_BEGIN: 140103712493072
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: STATEMENT
          LOCK_STATUS: GRANTED
               SOURCE: sql_base.cc:579
      OWNER_THREAD_ID: 52
       OWNER_EVENT_ID: 59
5 rows in set (0.00 sec)

ERROR: 
No query specified

# 释放锁
mysql> unlock tables;
Query OK, 0 rows affected (0.00 sec)
```

### `lock tables course write`

>对表course加元数据SHARED_NO_READ_WRITE锁，其他锁不需要理会。

```bash
# 对表course加表排他锁
mysql> lock tables course write;
Query OK, 0 rows affected (0.00 sec)

mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
# 对mysql实例加意向排他锁
          OBJECT_TYPE: GLOBAL
        OBJECT_SCHEMA: NULL
          OBJECT_NAME: NULL
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140103712491040
            LOCK_TYPE: INTENTION_EXCLUSIVE
        LOCK_DURATION: STATEMENT
          LOCK_STATUS: GRANTED
               SOURCE: sql_base.cc:5459
      OWNER_THREAD_ID: 52
       OWNER_EVENT_ID: 64
*************************** 2. row ***************************
# 对testdb加意向排他锁
          OBJECT_TYPE: SCHEMA
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: NULL
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140103712493232
            LOCK_TYPE: INTENTION_EXCLUSIVE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_base.cc:5446
      OWNER_THREAD_ID: 52
       OWNER_EVENT_ID: 64
*************************** 3. row ***************************
# 对表course加元数据SHARED_NO_READ_WRITE锁
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140103712299824
            LOCK_TYPE: SHARED_NO_READ_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 52
       OWNER_EVENT_ID: 64
*************************** 4. row ***************************
# 对表course表空间加意向排他锁
          OBJECT_TYPE: TABLESPACE
        OBJECT_SCHEMA: NULL
          OBJECT_NAME: testdb/course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140103712261760
            LOCK_TYPE: INTENTION_EXCLUSIVE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: lock.cc:804
      OWNER_THREAD_ID: 52
       OWNER_EVENT_ID: 64
5 rows in set (0.00 sec)

ERROR: 
No query specified

# 释放锁
mysql> unlock tables;
Query OK, 0 rows affected (0.00 sec)
```

### `RU/RC`+条件列无索引

#### 测试环境准备

```sql
# 设置RU级别
set session transaction isolation level read uncommitted;

# 或者设置RC级别
set session transaction isolation level read committed;

# 删除关于name字段的索引
alter table course drop index idx_course_name;
```

#### `select * from course where name='java'`

加元数据共享读锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140225325388656
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 70
       OWNER_EVENT_ID: 44
```



#### `select * from course where name='java-noneexists'`

加元数据共享读锁

```sql
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140225325388656
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 70
       OWNER_EVENT_ID: 44
```



#### `select * from course where name='java' lock in share mode`

加元数据共享读锁、意向共享锁、记录锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
# 元数据共享读锁
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140225325388656
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 70
       OWNER_EVENT_ID: 55

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
# 意向共享锁
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:1069:140227192629680
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 70
             EVENT_ID: 55
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140227192629680
            LOCK_TYPE: TABLE
            LOCK_MODE: IS
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
# 记录锁
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:2:140227192626688
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 70
             EVENT_ID: 55
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: S,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 5
```



#### `select * from course where name='java-noneexists' lock in share mode`

加元数据共享读锁、意向共享锁

提示：因为处于`RU/RC`事务隔离级别，所以没有间隙锁和临键锁。

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140225325388656
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 70
       OWNER_EVENT_ID: 67

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:1069:140227192629680
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 70
             EVENT_ID: 67
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140227192629680
            LOCK_TYPE: TABLE
            LOCK_MODE: IS
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
```



#### `select * from course where name='java' for update`

加元数据共享写锁、意向排他锁、记录锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140225325292864
            LOCK_TYPE: SHARED_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 70
       OWNER_EVENT_ID: 71

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:1069:140227192629680
ENGINE_TRANSACTION_ID: 1895
            THREAD_ID: 70
             EVENT_ID: 71
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140227192629680
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:2:140227192626688
ENGINE_TRANSACTION_ID: 1895
            THREAD_ID: 70
             EVENT_ID: 71
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: X,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 5
```



#### `select * from course where name='java-noneexists' for update`

加元数据共享写锁、意向排他锁

提示：因为处于`RU/RC`事务隔离级别，所以没有间隙锁和临键锁。

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140225325388656
            LOCK_TYPE: SHARED_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 70
       OWNER_EVENT_ID: 75

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:1069:140227192629680
ENGINE_TRANSACTION_ID: 1896
            THREAD_ID: 70
             EVENT_ID: 75
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140227192629680
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
```



### `RU/RC`+条件列是主键索引

#### 测试环境准备

```sql
# 设置RU级别
set session transaction isolation level read uncommitted;

# 或者设置RC级别
set session transaction isolation level read committed;
```

#### `select * from course where id=5`

加元数据共享读锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140225325388656
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 70
       OWNER_EVENT_ID: 79
```



#### `select * from course where id=12345`

加元数据共享读锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140225325388656
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 70
       OWNER_EVENT_ID: 83
```



#### `select * from course where id=5 lock in share mode`

加元数据共享读锁、意向共享锁、记录锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140225325388656
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 70
       OWNER_EVENT_ID: 87

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:1069:140227192629680
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 70
             EVENT_ID: 87
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140227192629680
            LOCK_TYPE: TABLE
            LOCK_MODE: IS
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:2:140227192626688
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 70
             EVENT_ID: 87
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: S,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 5
```



#### `select * from course where id=12345 lock in share mode`

加元数据共享读锁、意向共享锁

提示：因为处于`RU/RC`事务隔离级别，所以没有间隙锁和临键锁。

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140225325388656
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 70
       OWNER_EVENT_ID: 91

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:1069:140227192629680
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 70
             EVENT_ID: 91
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140227192629680
            LOCK_TYPE: TABLE
            LOCK_MODE: IS
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
```



#### `select * from course where id=5 for update`

加元数据共享写锁、意向排他锁、记录锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140225325292864
            LOCK_TYPE: SHARED_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 70
       OWNER_EVENT_ID: 96

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:1069:140227192629680
ENGINE_TRANSACTION_ID: 1897
            THREAD_ID: 70
             EVENT_ID: 96
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140227192629680
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:2:140227192626688
ENGINE_TRANSACTION_ID: 1897
            THREAD_ID: 70
             EVENT_ID: 96
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: X,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 5
```



#### `select * from course where id=12345 for update`

加元数据共享写锁、意向排他锁

提示：因为处于`RU/RC`事务隔离级别，所以没有间隙锁和临键锁。

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140225325388656
            LOCK_TYPE: SHARED_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 70
       OWNER_EVENT_ID: 100

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:1069:140227192629680
ENGINE_TRANSACTION_ID: 1898
            THREAD_ID: 70
             EVENT_ID: 100
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140227192629680
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
```



### `RU/RC`+条件列是非唯一索引

#### 测试环境准备

```sql
# 设置RU级别
set session transaction isolation level read uncommitted;

# 或者设置RC级别
set session transaction isolation level read committed;

# 创建非唯一索引
create index idx_course_name on course(name);
```

#### `select * from course where name='java'`

加元数据共享读锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140225314761008
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 70
       OWNER_EVENT_ID: 106
```



#### `select * from course where name='java-noneexists'`

加元数据共享读锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140225314761008
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 70
       OWNER_EVENT_ID: 110
```



#### `select * from course where name='java' lock in share mode`

加元数据共享读锁、意向共享锁、记录共享锁（锁定记录对应的`idx_course_name`和主键索引记录）

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140225314761008
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 70
       OWNER_EVENT_ID: 114

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:1069:140227192629680
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 70
             EVENT_ID: 114
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140227192629680
            LOCK_TYPE: TABLE
            LOCK_MODE: IS
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:6:3:140227192626688
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 70
             EVENT_ID: 114
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: idx_course_name
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: S,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 'java', 5
*************************** 3. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:2:140227192627032
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 70
             EVENT_ID: 114
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192627032
            LOCK_TYPE: RECORD
            LOCK_MODE: S,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 5
```



#### `select * from course where name='java-noneexists' lock in share mode`

加元数据共享读锁、意向共享锁

提示：因为处于`RU/RC`事务隔离级别，所以没有间隙锁和临键锁。

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140225314761008
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 70
       OWNER_EVENT_ID: 118

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:1069:140227192629680
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 70
             EVENT_ID: 118
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140227192629680
            LOCK_TYPE: TABLE
            LOCK_MODE: IS
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
```



#### `select * from course where name='java' for update`

加元数据共享写锁、意向排他锁、记录排他锁（锁定记录对应的`idx_course_name`和主键索引记录）

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140225325378736
            LOCK_TYPE: SHARED_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 70
       OWNER_EVENT_ID: 122

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:1069:140227192629680
ENGINE_TRANSACTION_ID: 1908
            THREAD_ID: 70
             EVENT_ID: 122
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140227192629680
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:6:3:140227192626688
ENGINE_TRANSACTION_ID: 1908
            THREAD_ID: 70
             EVENT_ID: 122
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: idx_course_name
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: X,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 'java', 5
*************************** 3. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:2:140227192627032
ENGINE_TRANSACTION_ID: 1908
            THREAD_ID: 70
             EVENT_ID: 122
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192627032
            LOCK_TYPE: RECORD
            LOCK_MODE: X,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 5
```



#### `select * from course where name='java-noneexists' for update`

加元数据共享写锁、意向排他锁

提示：因为处于`RU/RC`事务隔离级别，所以没有间隙锁和临键锁。

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140225314761008
            LOCK_TYPE: SHARED_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 70
       OWNER_EVENT_ID: 126

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:1069:140227192629680
ENGINE_TRANSACTION_ID: 1909
            THREAD_ID: 70
             EVENT_ID: 126
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140227192629680
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
```



### `RU/RC`+条件列是唯一索引

#### 测试环境准备

```sql
# 设置RU级别
set session transaction isolation level read uncommitted;

# 或者设置RC级别
set session transaction isolation level read committed;

# 创建非唯一索引
create unique index idx_course_name on course(name);
```

#### `select * from course where name='java'`

加元数据共享读锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140225325493248
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 70
       OWNER_EVENT_ID: 134
```



#### `select * from course where name='java-noneexists'`

加元数据共享读锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140225325493248
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 70
       OWNER_EVENT_ID: 138
```



#### `select * from course where name='java' lock in share mode`

加元数据共享读锁、意向共享锁、记录共享锁（锁定记录对应的`idx_course_name`和主键索引记录）

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140225325493248
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 70
       OWNER_EVENT_ID: 142

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:1069:140227192629680
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 70
             EVENT_ID: 142
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140227192629680
            LOCK_TYPE: TABLE
            LOCK_MODE: IS
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:6:3:140227192626688
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 70
             EVENT_ID: 142
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: idx_course_name
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: S,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 'java', 5
*************************** 3. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:2:140227192627032
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 70
             EVENT_ID: 142
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192627032
            LOCK_TYPE: RECORD
            LOCK_MODE: S,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 5
```



#### `select * from course where name='java-noneexists' lock in share mode`

加元数据共享读锁、意向共享锁

提示：因为处于`RU/RC`事务隔离级别，所以没有间隙锁和临键锁。

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140225325493248
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 70
       OWNER_EVENT_ID: 146

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:1069:140227192629680
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 70
             EVENT_ID: 146
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140227192629680
            LOCK_TYPE: TABLE
            LOCK_MODE: IS
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
```



#### `select * from course where name='java' for update`

加元数据共享写锁、意向排他锁、记录排他锁（锁定记录对应的`idx_course_name`和主键索引记录）

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140225325215536
            LOCK_TYPE: SHARED_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 70
       OWNER_EVENT_ID: 150

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:1069:140227192629680
ENGINE_TRANSACTION_ID: 1925
            THREAD_ID: 70
             EVENT_ID: 150
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140227192629680
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:6:3:140227192626688
ENGINE_TRANSACTION_ID: 1925
            THREAD_ID: 70
             EVENT_ID: 150
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: idx_course_name
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: X,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 'java', 5
*************************** 3. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:2:140227192627032
ENGINE_TRANSACTION_ID: 1925
            THREAD_ID: 70
             EVENT_ID: 150
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192627032
            LOCK_TYPE: RECORD
            LOCK_MODE: X,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 5
```



#### `select *  from course where name='java-noneexists' for update`

加元数据共享写锁、意向排他锁

提示：因为处于`RU/RC`事务隔离级别，所以没有间隙锁和临键锁。

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140225325493248
            LOCK_TYPE: SHARED_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 70
       OWNER_EVENT_ID: 154

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:1069:140227192629680
ENGINE_TRANSACTION_ID: 1926
            THREAD_ID: 70
             EVENT_ID: 154
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140227192629680
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
```



### `RR`+条件列无索引

#### 测试环境准备

```sql
# 设置RR级别
set session transaction isolation level repeatable read;

# 删除索引
alter table course drop index idx_course_name;
```

#### `select * from course where name='java'`

加元数据共享读锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140224978104464
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 95
       OWNER_EVENT_ID: 24
```



#### `select * from course where name='java-noneexists'`

加元数据共享读锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140224978104464
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 95
       OWNER_EVENT_ID: 28
```



#### `select * from course where name='java' lock in share mode`

加元数据共享读锁、意向共享锁、多个临键锁（用多个临键锁实现全表锁定不允许插入新记录）

提示：因为处于`RR`事务隔离级别，`name`字段没有索引，所以会退化为全表锁定以防止幻读。

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140224978104464
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 95
       OWNER_EVENT_ID: 32

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:1069:140227192629680
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 95
             EVENT_ID: 32
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140227192629680
            LOCK_TYPE: TABLE
            LOCK_MODE: IS
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
# 临键锁，区间(31, +infinity]
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:1:140227192626688
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 95
             EVENT_ID: 32
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: S
          LOCK_STATUS: GRANTED
            LOCK_DATA: supremum pseudo-record
*************************** 3. row ***************************
# 临键锁，区间(-infinity, 5]
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:2:140227192626688
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 95
             EVENT_ID: 32
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: S
          LOCK_STATUS: GRANTED
            LOCK_DATA: 5
*************************** 4. row ***************************
# 临键锁，区间(5, 15]
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:3:140227192626688
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 95
             EVENT_ID: 32
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: S
          LOCK_STATUS: GRANTED
            LOCK_DATA: 15
*************************** 5. row ***************************
# 临键锁，区间(15, 16]
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:4:140227192626688
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 95
             EVENT_ID: 32
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: S
          LOCK_STATUS: GRANTED
            LOCK_DATA: 16
*************************** 6. row ***************************
# 临键锁，区间(16, 31]
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:5:140227192626688
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 95
             EVENT_ID: 32
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: S
          LOCK_STATUS: GRANTED
            LOCK_DATA: 31
```



#### `select * from course where name='java-noneexists' lock in share mode`

加元数据共享读锁、意向共享锁、多个临键锁（用多个临键锁实现全表锁定不允许插入新记录）

提示：因为处于`RR`事务隔离级别，`name`字段没有索引，所以会退化为全表锁定以防止幻读。

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140224978104464
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 95
       OWNER_EVENT_ID: 38

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:1069:140227192629680
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 95
             EVENT_ID: 38
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140227192629680
            LOCK_TYPE: TABLE
            LOCK_MODE: IS
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
# 临键锁，区间(31, +infinity]
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:1:140227192626688
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 95
             EVENT_ID: 38
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: S
          LOCK_STATUS: GRANTED
            LOCK_DATA: supremum pseudo-record
*************************** 3. row ***************************
# 临键锁，区间(-infinity, 5]
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:2:140227192626688
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 95
             EVENT_ID: 38
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: S
          LOCK_STATUS: GRANTED
            LOCK_DATA: 5
*************************** 4. row ***************************
# 临键锁，区间(1, 15]
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:3:140227192626688
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 95
             EVENT_ID: 38
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: S
          LOCK_STATUS: GRANTED
            LOCK_DATA: 15
*************************** 5. row ***************************
# 临键锁，区间(15, 16]
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:4:140227192626688
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 95
             EVENT_ID: 38
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: S
          LOCK_STATUS: GRANTED
            LOCK_DATA: 16
*************************** 6. row ***************************
# 临键锁，区间(16, 31]
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:5:140227192626688
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 95
             EVENT_ID: 38
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: S
          LOCK_STATUS: GRANTED
            LOCK_DATA: 31
```



#### `select * from course where name='java' for update`

加元数据共享写锁、意向排他锁、多个临键锁（用多个临键锁实现全表锁定不允许插入新记录）

提示：因为处于`RR`事务隔离级别，`name`字段没有索引，所以会退化为全表锁定以防止幻读。

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140224978028336
            LOCK_TYPE: SHARED_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 95
       OWNER_EVENT_ID: 42

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:1069:140227192629680
ENGINE_TRANSACTION_ID: 1945
            THREAD_ID: 95
             EVENT_ID: 42
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140227192629680
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
# 临键锁，区间(31, +infinity]
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:1:140227192626688
ENGINE_TRANSACTION_ID: 1945
            THREAD_ID: 95
             EVENT_ID: 42
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: X
          LOCK_STATUS: GRANTED
            LOCK_DATA: supremum pseudo-record
*************************** 3. row ***************************
# 临键锁，区间(-infinity, 5]
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:2:140227192626688
ENGINE_TRANSACTION_ID: 1945
            THREAD_ID: 95
             EVENT_ID: 42
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: X
          LOCK_STATUS: GRANTED
            LOCK_DATA: 5
*************************** 4. row ***************************
# 临键锁，区间(5, 15]
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:3:140227192626688
ENGINE_TRANSACTION_ID: 1945
            THREAD_ID: 95
             EVENT_ID: 42
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: X
          LOCK_STATUS: GRANTED
            LOCK_DATA: 15
*************************** 5. row ***************************
# 临键锁，区间(15, 16]
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:4:140227192626688
ENGINE_TRANSACTION_ID: 1945
            THREAD_ID: 95
             EVENT_ID: 42
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: X
          LOCK_STATUS: GRANTED
            LOCK_DATA: 16
*************************** 6. row ***************************
# 临键锁，区间(16, 31]
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:5:140227192626688
ENGINE_TRANSACTION_ID: 1945
            THREAD_ID: 95
             EVENT_ID: 42
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: X
          LOCK_STATUS: GRANTED
            LOCK_DATA: 31
```



#### `select * from course where name='java-noneexists' for update`

加元数据共享写锁、意向排他锁、多个临键锁（用多个临键锁实现全表锁定不允许插入新记录）

提示：因为处于`RR`事务隔离级别，`name`字段没有索引，所以会退化为全表锁定以防止幻读。

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140224978104464
            LOCK_TYPE: SHARED_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 95
       OWNER_EVENT_ID: 46

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:1069:140227192629680
ENGINE_TRANSACTION_ID: 1946
            THREAD_ID: 95
             EVENT_ID: 46
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140227192629680
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
# 临键锁，区间(31, +infinity]
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:1:140227192626688
ENGINE_TRANSACTION_ID: 1946
            THREAD_ID: 95
             EVENT_ID: 46
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: X
          LOCK_STATUS: GRANTED
            LOCK_DATA: supremum pseudo-record
*************************** 3. row ***************************
# 临键锁，区间(-infinity, 5]
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:2:140227192626688
ENGINE_TRANSACTION_ID: 1946
            THREAD_ID: 95
             EVENT_ID: 46
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: X
          LOCK_STATUS: GRANTED
            LOCK_DATA: 5
*************************** 4. row ***************************
# 临键锁，区间(5, 15]
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:3:140227192626688
ENGINE_TRANSACTION_ID: 1946
            THREAD_ID: 95
             EVENT_ID: 46
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: X
          LOCK_STATUS: GRANTED
            LOCK_DATA: 15
*************************** 5. row ***************************
# 临键锁，区间(15, 16]
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:4:140227192626688
ENGINE_TRANSACTION_ID: 1946
            THREAD_ID: 95
             EVENT_ID: 46
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: X
          LOCK_STATUS: GRANTED
            LOCK_DATA: 16
*************************** 6. row ***************************
# 临键锁，区间(16, 31]
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:5:140227192626688
ENGINE_TRANSACTION_ID: 1946
            THREAD_ID: 95
             EVENT_ID: 46
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: X
          LOCK_STATUS: GRANTED
            LOCK_DATA: 31
```



### `RR`+条件列是主键索引

#### 测试环境准备

```sql
# 设置RR级别
set session transaction isolation level repeatable read;
```

#### `select * from course where id=5`

加元数据共享读锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140224978104464
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 95
       OWNER_EVENT_ID: 50
```



#### `select * from course where id=12345`

加元数据共享读锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140224978104464
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 95
       OWNER_EVENT_ID: 54
```



#### `select * from course where id=5 lock in share mode`

加元数据共享读锁、意向共享锁、记录共享锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140224978104464
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 95
       OWNER_EVENT_ID: 58

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:1069:140227192629680
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 95
             EVENT_ID: 58
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140227192629680
            LOCK_TYPE: TABLE
            LOCK_MODE: IS
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:2:140227192626688
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 95
             EVENT_ID: 58
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: S,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 5
```



#### `select * from course where id=12345 lock in share mode`

加元数据共享读锁、意向共享锁、临键锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140224978104464
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 95
       OWNER_EVENT_ID: 62

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:1069:140227192629680
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 95
             EVENT_ID: 62
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140227192629680
            LOCK_TYPE: TABLE
            LOCK_MODE: IS
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
# 临键锁，区间(31, +infinity]
# 因为记录不存在，所以使用临键锁锁定记录落在的区间以防止幻读。
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140227304053976:4:4:1:140227192626688
ENGINE_TRANSACTION_ID: 421702280764632
            THREAD_ID: 95
             EVENT_ID: 62
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140227192626688
            LOCK_TYPE: RECORD
            LOCK_MODE: S
          LOCK_STATUS: GRANTED
            LOCK_DATA: supremum pseudo-record
```



#### `select * from course where id=5 for update`

加元数据共享写锁、意向排他锁、记录排他锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140627028406064
            LOCK_TYPE: SHARED_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 48
       OWNER_EVENT_ID: 16

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140629053713624:1069:140628973398448
ENGINE_TRANSACTION_ID: 2317
            THREAD_ID: 48
             EVENT_ID: 16
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140628973398448
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140629053713624:4:4:2:140628973395456
ENGINE_TRANSACTION_ID: 2317
            THREAD_ID: 48
             EVENT_ID: 16
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140628973395456
            LOCK_TYPE: RECORD
            LOCK_MODE: X,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 5
```



#### `select * from course where id=12345 for update`

加元数据共享写锁、意向排他锁、临键锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140627028501184
            LOCK_TYPE: SHARED_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 48
       OWNER_EVENT_ID: 20

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140629053713624:1069:140628973398448
ENGINE_TRANSACTION_ID: 2318
            THREAD_ID: 48
             EVENT_ID: 20
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140628973398448
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
# 临键锁，区间(31, +infinity]
# 因为记录不存在，所以使用临键锁锁定记录落在的区间以防止幻读。
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140629053713624:4:4:1:140628973395456
ENGINE_TRANSACTION_ID: 2318
            THREAD_ID: 48
             EVENT_ID: 20
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140628973395456
            LOCK_TYPE: RECORD
            LOCK_MODE: X
          LOCK_STATUS: GRANTED
            LOCK_DATA: supremum pseudo-record
```



### `RR`+条件列是非唯一索引

#### 测试环境准备

```sql
# 设置RU级别
set session transaction isolation level repeatable read;

# 创建非唯一索引
create index idx_course_name on course(name);
```

#### `select * from course where name='java'`

加元数据共享读锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140627028546144
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 48
       OWNER_EVENT_ID: 32
```



#### `select * from course where name='java-noneexists'`

加元数据共享读锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140627028546144
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 48
       OWNER_EVENT_ID: 36
```



#### `select * from course where name='java' lock in share mode`

加元数据共享读锁、意向共享锁、记录锁、间隙锁、临键锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140627028546144
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 48
       OWNER_EVENT_ID: 40

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140629053713624:1069:140628973398448
ENGINE_TRANSACTION_ID: 422104030424280
            THREAD_ID: 48
             EVENT_ID: 40
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140628973398448
            LOCK_TYPE: TABLE
            LOCK_MODE: IS
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
# 临键锁，区间([16,c], [5, java]]
# 因为列name为非唯一索引，需要使用临键锁锁定记录左边区间以防止幻读。
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140629053713624:4:6:3:140628973395456
ENGINE_TRANSACTION_ID: 422104030424280
            THREAD_ID: 48
             EVENT_ID: 40
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: idx_course_name
OBJECT_INSTANCE_BEGIN: 140628973395456
            LOCK_TYPE: RECORD
            LOCK_MODE: S
          LOCK_STATUS: GRANTED
            LOCK_DATA: 'java', 5
*************************** 3. row ***************************
# 记录锁，id=5
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140629053713624:4:4:2:140628973395800
ENGINE_TRANSACTION_ID: 422104030424280
            THREAD_ID: 48
             EVENT_ID: 40
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140628973395800
            LOCK_TYPE: RECORD
            LOCK_MODE: S,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 5
*************************** 4. row ***************************
# 间隙锁，区间([5,java], [15, php])
# 因为列name为非唯一索引，需要使用间隙锁锁定记录右边区间以防止幻读。
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140629053713624:4:6:4:140628973396144
ENGINE_TRANSACTION_ID: 422104030424280
            THREAD_ID: 48
             EVENT_ID: 40
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: idx_course_name
OBJECT_INSTANCE_BEGIN: 140628973396144
            LOCK_TYPE: RECORD
            LOCK_MODE: S,GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 'php', 15
```



#### `select * from course where name='java-noneexists' lock in share mode`

加元数据共享读锁、意向共享锁、间隙锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140627028546144
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 48
       OWNER_EVENT_ID: 45

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140629053713624:1069:140628973398448
ENGINE_TRANSACTION_ID: 422104030424280
            THREAD_ID: 48
             EVENT_ID: 45
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140628973398448
            LOCK_TYPE: TABLE
            LOCK_MODE: IS
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
# 间隙锁，区间([5,java], [15,php])
# 因为记录不存在，需要使用间隙锁锁定记录落在的区间以防止幻读。
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140629053713624:4:6:4:140628973395456
ENGINE_TRANSACTION_ID: 422104030424280
            THREAD_ID: 48
             EVENT_ID: 45
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: idx_course_name
OBJECT_INSTANCE_BEGIN: 140628973395456
            LOCK_TYPE: RECORD
            LOCK_MODE: S,GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 'php', 15
```



#### `select * from course where name='java' for update`

加元数据共享写锁、意向排他锁、记录锁、间隙锁、临键锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140627028611616
            LOCK_TYPE: SHARED_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 48
       OWNER_EVENT_ID: 49

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140629053713624:1069:140628973398448
ENGINE_TRANSACTION_ID: 2420
            THREAD_ID: 48
             EVENT_ID: 49
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140628973398448
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
# 临键锁，区间([16,c], [5, java]]
# 因为列name为非唯一索引，需要使用临键锁锁定记录左边区间以防止幻读。
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140629053713624:4:6:3:140628973395456
ENGINE_TRANSACTION_ID: 2420
            THREAD_ID: 48
             EVENT_ID: 49
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: idx_course_name
OBJECT_INSTANCE_BEGIN: 140628973395456
            LOCK_TYPE: RECORD
            LOCK_MODE: X
          LOCK_STATUS: GRANTED
            LOCK_DATA: 'java', 5
*************************** 3. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140629053713624:4:4:2:140628973395800
ENGINE_TRANSACTION_ID: 2420
            THREAD_ID: 48
             EVENT_ID: 49
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140628973395800
            LOCK_TYPE: RECORD
            LOCK_MODE: X,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 5
*************************** 4. row ***************************
# 间隙锁，区间([5,java], [15, php])
# 因为列name为非唯一索引，需要使用间隙锁锁定记录右边区间以防止幻读。
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140629053713624:4:6:4:140628973396144
ENGINE_TRANSACTION_ID: 2420
            THREAD_ID: 48
             EVENT_ID: 49
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: idx_course_name
OBJECT_INSTANCE_BEGIN: 140628973396144
            LOCK_TYPE: RECORD
            LOCK_MODE: X,GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 'php', 15
```



#### `select * from course where name='java-noneexists' for update`

加元数据共享读锁、意向共享锁、间隙锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140627028546144
            LOCK_TYPE: SHARED_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 48
       OWNER_EVENT_ID: 53

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140629053713624:1069:140628973398448
ENGINE_TRANSACTION_ID: 2421
            THREAD_ID: 48
             EVENT_ID: 53
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140628973398448
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
# 间隙锁，区间([5,java], [15,php])
# 因为记录不存在，需要使用间隙锁锁定记录落在的区间以防止幻读
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140629053713624:4:6:4:140628973395456
ENGINE_TRANSACTION_ID: 2421
            THREAD_ID: 48
             EVENT_ID: 53
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: idx_course_name
OBJECT_INSTANCE_BEGIN: 140628973395456
            LOCK_TYPE: RECORD
            LOCK_MODE: X,GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 'php', 15
```



### `RR`+条件列是唯一索引

#### 测试环境准备

```sql
# 设置RU级别
set session transaction isolation level repeatable read;

# 创建唯一索引
create unique index idx_course_name on course(name);
```

#### `select * from course where name='java'`

加元数据共享读锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140627028546144
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 48
       OWNER_EVENT_ID: 57
```



#### `select * from course where name='java-noneexists'`

加元数据共享读锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140627028501184
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 48
       OWNER_EVENT_ID: 61
```



#### `select * from course where name='java' lock in share mode`

加元数据共享读锁、意向共享锁、记录锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140627028501184
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 48
       OWNER_EVENT_ID: 65

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140629053713624:1069:140628973398448
ENGINE_TRANSACTION_ID: 422104030424280
            THREAD_ID: 48
             EVENT_ID: 65
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140628973398448
            LOCK_TYPE: TABLE
            LOCK_MODE: IS
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
# 记录锁，name='java'
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140629053713624:4:6:3:140628973395456
ENGINE_TRANSACTION_ID: 422104030424280
            THREAD_ID: 48
             EVENT_ID: 65
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: idx_course_name
OBJECT_INSTANCE_BEGIN: 140628973395456
            LOCK_TYPE: RECORD
            LOCK_MODE: S,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 'java', 5
*************************** 3. row ***************************
# 记录锁，id=5
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140629053713624:4:4:2:140628973395800
ENGINE_TRANSACTION_ID: 422104030424280
            THREAD_ID: 48
             EVENT_ID: 65
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140628973395800
            LOCK_TYPE: RECORD
            LOCK_MODE: S,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 5
```



#### `select * from course where name='java-noneexists' lock in share mode`

加元数据共享读锁、意向共享锁、间隙锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140627028501184
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 48
       OWNER_EVENT_ID: 69

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140629053713624:1069:140628973398448
ENGINE_TRANSACTION_ID: 422104030424280
            THREAD_ID: 48
             EVENT_ID: 69
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140628973398448
            LOCK_TYPE: TABLE
            LOCK_MODE: IS
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
# 间隙锁，区间([5,java], [15,php])
# 因为记录不存在，需要使用间隙锁锁定记录落在的区间以防止幻读
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140629053713624:4:6:4:140628973395456
ENGINE_TRANSACTION_ID: 422104030424280
            THREAD_ID: 48
             EVENT_ID: 69
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: idx_course_name
OBJECT_INSTANCE_BEGIN: 140628973395456
            LOCK_TYPE: RECORD
            LOCK_MODE: S,GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 'php', 15
```



#### `select * from course where name='java' for update`

加元数据共享写锁、意向排他锁、记录锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140627027322992
            LOCK_TYPE: SHARED_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 48
       OWNER_EVENT_ID: 73

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140629053713624:1069:140628973398448
ENGINE_TRANSACTION_ID: 2437
            THREAD_ID: 48
             EVENT_ID: 73
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140628973398448
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
# 记录锁，name='java'
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140629053713624:4:6:3:140628973395456
ENGINE_TRANSACTION_ID: 2437
            THREAD_ID: 48
             EVENT_ID: 73
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: idx_course_name
OBJECT_INSTANCE_BEGIN: 140628973395456
            LOCK_TYPE: RECORD
            LOCK_MODE: X,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 'java', 5
*************************** 3. row ***************************
# 记录锁，id=5
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140629053713624:4:4:2:140628973395800
ENGINE_TRANSACTION_ID: 2437
            THREAD_ID: 48
             EVENT_ID: 73
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140628973395800
            LOCK_TYPE: RECORD
            LOCK_MODE: X,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 5
```



#### `select * from course where name='java-noneexists' for update`

加元数据共享写锁、意向排他锁、间隙锁

```bash
mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140627028501184
            LOCK_TYPE: SHARED_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 48
       OWNER_EVENT_ID: 77

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140629053713624:1069:140628973398448
ENGINE_TRANSACTION_ID: 2438
            THREAD_ID: 48
             EVENT_ID: 77
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140628973398448
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
# 间隙锁，区间([5,java], [15,php])
# 因为记录不存在，需要使用间隙锁锁定记录落在的区间以防止幻读
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140629053713624:4:6:4:140628973395456
ENGINE_TRANSACTION_ID: 2438
            THREAD_ID: 48
             EVENT_ID: 77
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: idx_course_name
OBJECT_INSTANCE_BEGIN: 140628973395456
            LOCK_TYPE: RECORD
            LOCK_MODE: X,GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 'php', 15
```



## 全局锁

### 什么是全局锁？

MySQL中的全局锁（Global Lock）是一个对整个数据库实例加锁的机制，使得在整个数据库上只能有一个线程进行写操作（包括DDL操作），同时其他线程只能进行读操作。全局锁的主要目的是确保数据库的一致性，特别是在进行数据库备份的时候。

当MySQL服务器上有多个数据库或表需要备份时，为了避免在备份过程中数据发生变更（如其他线程进行写操作），就需要使用全局锁来锁定整个数据库实例。在备份期间，其他线程仍然可以读取数据，但不能进行写操作，从而确保备份数据的一致性。

在MySQL 5.5及之前的版本中，`FLUSH TABLES WITH READ LOCK (FTWRL)` 命令经常被用来实现全局锁。但是，`FTWRL` 的缺点是它会阻塞所有写操作，包括DDL操作（如`ALTER TABLE`），并且只有当所有的表都被关闭（flushed）之后，锁才会被加上。这在实际应用中可能会导致一些性能问题。

从MySQL 5.6开始，引入了一种新的元数据锁（Metadata Lock，MDL）来替代一部分`FTWRL`的功能。MDL不需要显式使用，在访问一个表的结构定义时会自动加上。虽然MDL不会阻塞读操作，但是它会阻塞DDL操作和其他需要修改表结构的操作。

然而，在进行物理备份（如使用`mysqldump --single-transaction`或`xtrabackup`等工具）时，由于这些工具通常会在事务中执行备份操作，并且使用一致性快照来确保备份数据的一致性，因此并不需要全局锁。但是，在进行逻辑备份或某些特定的备份操作时，可能仍然需要使用全局锁来确保数据的一致性。

需要注意的是，全局锁会阻塞所有的写操作，包括DDL操作，因此在使用全局锁时需要谨慎考虑其对数据库性能的影响。同时，也需要注意在备份完成后及时释放全局锁，以避免长时间阻塞其他线程的操作。

### 为何需要全局锁呢？

>注意：以下数据备份不一致状态是数据库中有`MyISAM`表时出现的，如果数据库中所有表都是使用`InnoDB`引擎，则在`mysqldump`时添加`--single-transaction`即可避免数据备份的数据不一致情况发生。

在有`MyISAM`表（`InnoDB`表使用事务，通过`mysqldump`的`--single-transaction`选项）的数据库备份时使用全局锁避免数据一致性和完整性问题。

试想这样一个场景，数据库中有用户余额表和购买套餐表。在数据备份开始时，用户余额为100元，购买套餐为空。当数据备份刚好在用户余额表备份完毕时，用户此时购买了一个65元套餐。在购买套餐成功的瞬间，数据备份开始备份购买套餐表数据直到到数据备份结束。最后的备份数据中用户余额为100元（因为在用户余额表备份时余额始终为100元），但是购买套餐却有一个65元套餐，备份数据处于数据不一致状态。

### 演示`FTWRL`锁

> `FTWRL`是`flush tables with read lock`简称，属于共享锁。

可以使用`FTWRL`锁解决在类似上面提到的场景数据备份（数据库中有`MyISAM`表）导致数据不一致情况。

```bash
# 为`MySQL`实例加全局锁
mysql> flush tables with read lock;
Query OK, 0 rows affected (0.00 sec)

# 查看锁情况
mysql> select * from performance_schema.metadata_locks\G;
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

# session2 中尝试 update 数据被挂起，因为 session1 加全局锁
mysql> update course set name='java' where id=1;
^C^C -- query aborted
ERROR 1317 (70100): Query execution was interrupted

# 使用`mysqldump`工具备份数据

# 释放锁
mysql> unlock tables;
Query OK, 0 rows affected (0.00 sec)
```

### 会发生死锁吗？

`FTWRL`不会发生死锁。

## 表级锁

### 什么是表级锁？

MySQL 中的表级锁（Table-level Locking）是最简单的锁定策略，也是开销最小的策略。当表被加锁时，其他事务不能对该表进行写操作（包括 INSERT、UPDATE、DELETE），但在某些类型的存储引擎中，读操作（SELECT）可能是允许的，这取决于具体的存储引擎和锁的类型。

### 表级锁有哪些类型？

和锁的分类一样，分为共享读锁和排他写锁。

### 什么时候触发加表级锁呢？

#### 对`MyISAM`表的读操作，会自动加上表共享锁

> 为了解决`MyISAM`表的数据不一致问题发生。

#### 对`MyISAM`表的写操作，会自动加上表排他锁

>为了解决`MyISAM`表的数据不一致问题发生。

#### alter table

>这个命令用于更改表的结构，如添加列、删除列、改变列的类型等。执行这个命令的时候，`MySQL`需要锁定整个表（表排他锁）以防止在更改过程中有新的数据写入。

#### `drop table`和`truncate table`

>这两个命令都会触发加表排他锁。`drop table`命令会删除整个表，而`truncate table`命令会删除表中的所有数据。在执行这些命令的时候，`MySQL`需要锁定整个表以防止在删除过程中有新的数据写入。

#### `lock tables`

>这个命令可以显式地为一个或多个表加上表共享锁或表排他锁。`lock tables`命令后面可以跟上一系列的表名和锁模式，用来指定需要锁定哪些表，以及使用什么样的锁模式。例如，`lock tables t1 write, t2 read`；命令会给表 t1 加上表排他锁，给表 t2 加上表共享锁。

#### 全表扫描或大范围扫描

>对于`MyISAM`存储引擎，全表扫描或大范围扫描会触发添加表锁。

#### `flush tables with read lock`

>这个命令可以给所有表加上全局共享锁，其他会话在此期间不能对数据进行修改。

### 演示`lock tables`加表共享锁

```bash
# 表共享读演示
mysql> lock tables course read;
Query OK, 0 rows affected (0.00 sec)

mysql> select * from performance_schema.metadata_locks\G;
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

# session1更新数据失败
mysql> update course set name='java3' where id=5;
ERROR 1099 (HY000): Table 'course' was locked with a READ lock and can't be updated

# session2更新数据被阻塞直到session1 unlock tables;
mysql> update course set name='java3' where id=5;

# session1释放表共享锁
mysql> unlock tables;
Query OK, 0 rows affected (0.00 sec)

```

### 演示`lock tables`加表排他锁

```shell
# session1添加表排他锁
mysql> lock tables course write;
Query OK, 0 rows affected (0.00 sec)

mysql> select * from performance_schema.metadata_locks\G;
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

# session1查询course数据
mysql> select * from course;
+----+--------+-----+
| id | name   | age |
+----+--------+-----+
|  5 | java   |   5 |
| 15 | php    |  15 |
| 16 | c      |  15 |
| 31 | python |  31 |
+----+--------+-----+
4 rows in set (0.01 sec)

# session1能够修改数据
mysql> update course set name='java5' where id=5;
Query OK, 1 row affected (0.00 sec)
Rows matched: 1  Changed: 1  Warnings: 0

# session2读取course一直被阻塞直到session1 unlock tables;
mysql> select * from course;
+----+--------+-----+
| id | name   | age |
+----+--------+-----+
|  5 | java5  |   5 |
| 15 | php    |  15 |
| 16 | c      |  15 |
| 31 | python |  31 |
+----+--------+-----+
4 rows in set (0.01 sec)

# session释放表排他锁
mysql> unlock tables;
Query OK, 0 rows affected (0.00 sec)
```

### 元数据锁（meta data lock，MDL）

#### 什么是元数据锁呢？

MDL加锁过程是系统自动控制，无需显示使用，在访问一张表的时候会自动加上。MDL锁主要作用是维护表元数据的数据一致性，在表上有活动事务的时候，不可以对表元数据进行写入操作。为了避免DML和DDL冲突，保证读写的正确性。

在MySQL5.5中引入了MDL，当对一张表进行增删改查的时候，自动加MDL读锁（共享）。当对表结构进行变更操作时候，自动加MDL写锁（排他）。

#### 元数据锁有哪些类型呢？

MDL锁主要可以分为以下几种类型：

1. 共享读锁（SHARED_READ）：
   - 当一个会话执行查询（如SELECT）时，它首先会获取被查询对象的共享读锁。
   - 这种锁允许多个会话同时持有，因此多个会话可以同时读取同一对象的元数据。
   - 兼容`SHARE_READ`、`SHARED_WRITE`锁
2. 共享写锁（SHARED_WRITE）：
   - 在某些情况下，虽然会话正在执行修改数据的操作（如DML操作），但它可能并不需要修改表的元数据，此时它可能会获取共享写锁。
   - 与共享读锁类似，多个会话可以同时持有共享写锁。
   - 兼容`SHARE_READ`、`SHARED_WRITE`锁
3. 独占锁（EXCLUSIVE）：
   - 当一个会话执行DDL（数据定义语言）操作（如ALTER TABLE、DROP TABLE等）时，它会首先获取被操作对象的独占锁。
   - 独占锁不允许其他会话同时持有任何类型的锁（包括共享读锁和共享写锁），从而确保DDL操作的原子性和一致性。

#### 哪些`sql`执行会触发加元数据锁呢？

- `drop table`、`alter table`、`create index`会触发加元数据排他锁。
- `select`语句会触发加元数据共享读锁，`insert`、`delete`、`update`语句会触发加元数据共享写锁。

演示`select`触发加元数据共享读锁

```bash
# 演示select语句自动加入元数据共享读锁
mysql> begin;
Query OK, 0 rows affected (0.00 sec)

mysql> select * from course;
+----+--------+-----+
| id | name   | age |
+----+--------+-----+
|  5 | java   |   5 |
| 15 | php    |  15 |
| 16 | c      |  15 |
| 31 | python |  31 |
+----+--------+-----+
4 rows in set (0.00 sec)

# 查看元数据共享锁信息
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
```

演示触发加元数据共享写锁

```bash
# 演示delete、update、insert、select ... for update语句自动加入元数据共享写锁
mysql> begin;
Query OK, 0 rows affected (0.00 sec)

mysql> update course set name='java' where id=5;
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
```



#### 元数据锁之间的兼容性

|                | 元数据共享读锁 | 元数据共享写锁 | 元数据排他锁 |
| -------------- | -------------- | -------------- | ------------ |
| 元数据共享读锁 | 兼容           | 兼容           | 不兼容       |
| 元数据共享写锁 | 兼容           | 兼容           | 不兼容       |
| 元数据排他锁   | 不兼容         | 不兼容         | 不兼容       |



演示元数据锁不兼容情况

```bash
# 演示alter table 元数据排他锁和select语句的元数据共享读锁冲突情景
# 演示元数据共享读锁、元数据共享写锁和元数据排他锁冲突
mysql> begin;
Query OK, 0 rows affected (0.00 sec)

mysql> select * from course;
+----+--------+-----+
| id | name   | age |
+----+--------+-----+
|  5 | java   |   5 |
| 15 | php    |  15 |
| 16 | c      |  15 |
| 31 | python |  31 |
+----+--------+-----+
4 rows in set (0.00 sec)

# session2 执行alter table语句一直阻塞(如下面显示的pending状态)，因为元数据排他锁和session1 select语句触发加的元数据共享读锁冲突
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

# 演示元数据共享读锁和元数据共享写锁不冲突
mysql> begin;
Query OK, 0 rows affected (0.01 sec)

mysql> select * from course;
+----+--------+-----+
| id | name   | age |
+----+--------+-----+
|  5 | java   |   5 |
| 15 | php    |  15 |
| 16 | c      |  15 |
| 31 | python |  31 |
+----+--------+-----+
4 rows in set (0.00 sec)

# session2
mysql> begin;
Query OK, 0 rows affected (0.00 sec)
# session2
mysql> update course set name='java100' where id=5;
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



### 意向锁

#### 什么是意向锁呢？

意向锁是表锁，为了协调行锁和表锁的关系，支持多粒度（表锁和行锁）的锁并存。

意向锁的作用是当事务A对记录加行锁时，`MySQL`会自动为该表添加意向锁，事务B如果想申请整个表的表级排他写锁，那么不需要遍历每一行判断是否存在行锁，而直接判断是否存在意向锁，增强性能。

#### 意向锁有哪些类型呢？

和锁的分类一样，分为共享读锁（IS）和排他写锁（IX）。

#### 什么时候触发加意向锁呢？

- `select * from course where id=5 for update`加意向排他锁
- `select * from course where id=5 lock in share mode`加意向共享锁
- `update course set name='java' where id=5`加意向排他锁
- `delete from course where id=5`加意向排他锁
- `insert into course values(5,'java',2)`加意向排他锁

#### 意向锁的兼容互斥性

|                    | 意向共享锁（IS锁） | 意向排他锁（IX锁） | 表共享锁（表S锁） | 表排他锁（表X锁） |
| ------------------ | ------------------ | ------------------ | ----------------- | ----------------- |
| 意向共享锁（IS锁） | 兼容               | 兼容               | 兼容              | 不兼容            |
| 意向排他锁（IX锁） | 兼容               | 兼容               | 不兼容            | 不兼容            |
| 表共享锁（表S锁）  | 兼容               | 不兼容             | 兼容              | 不兼容            |
| 表排他锁（表X锁）  | 不兼容             | 不兼容             | 不兼容            | 不兼容            |



演示意向共享锁和表共享锁兼容

```bash
# session1开启事务
mysql> begin;
Query OK, 0 rows affected (0.00 sec)

# session1添加意向共享锁
mysql> select * from course where id=5 lock in share mode;
+----+------+-----+
| id | name | age |
+----+------+-----+
|  5 | java |   5 |
+----+------+-----+
1 row in set (0.00 sec)

# session2不会阻塞，因为意向共享锁和表共享锁兼容
mysql> lock table course read;
Query OK, 0 rows affected (0.00 sec)
# session2释放锁
mysql> unlock tables;
Query OK, 0 rows affected (0.00 sec)

# session1释放锁
mysql> commit;
Query OK, 0 rows affected (0.00 sec)
```

演示意向共享锁和表排他锁互斥

```bash
# session1开启事务
mysql> begin;
Query OK, 0 rows affected (0.00 sec)

# session1加意向共享锁
mysql> select * from course where id=5 lock in share mode;
+----+------+-----+
| id | name | age |
+----+------+-----+
|  5 | java |   5 |
+----+------+-----+
1 row in set (0.00 sec)

# session2一直被阻塞，因为意向共享锁和表排他锁互斥
mysql> lock table course write;

# session1释放锁
mysql> commit;
Query OK, 0 rows affected (0.00 sec)

# session2释放锁
mysql> unlock tables;
Query OK, 0 rows affected (0.00 sec)
```

演示意向排他锁和表共享锁、表排他锁互斥

```bash
# session1开启事务
mysql> begin;
Query OK, 0 rows affected (0.00 sec)

# session1加意向排他锁
mysql> select * from course where id=5 for update;
+----+------+-----+
| id | name | age |
+----+------+-----+
|  5 | java |   5 |
+----+------+-----+
1 row in set (0.00 sec)

# session2 SQL被阻塞，因为意向排他锁和表共享锁互斥
mysql> lock table course read;

# session2 SQL被阻塞，因为意向排他锁和表排他锁互斥
mysql> lock table course write;

# session1释放锁
mysql> commit;
Query OK, 0 rows affected (0.00 sec)

# session2释放锁
mysql> unlock tables;
Query OK, 0 rows affected (0.00 sec)
```



### 会发生死锁吗？

理论上会，但是无法设计例子演示。

## 行级锁

### 什么是行级锁？

每次操作锁住对应的行数据。锁定粒度最小，发生锁冲突的概率最低，并发度最高。应用在`InnoDB`存储引擎中，`MyISAM`不支持行级锁

`InnoDB`的数据是基于索引组织的，行锁是通过对索引上的索引项加锁来实现的，而不是对记录加的锁。

值得注意的是，行级锁只在事务中有效，也就是说，只有在一个事务开始后并在事务提交或回滚之前，才能对数据进行锁定。如果在非事务环境中执行`SQL`语句，那么`InnoDB`会在语句执行结束后立即释放所有的锁。

加锁的对象是索引，加锁的基本单位是临键锁，在某种情况下临键锁会退化为记录锁或者间隙锁。

### 行级锁有哪些类型？

和锁的分类一样，分为共享读锁和排他写锁。

### 记录锁、间隙锁和临键锁用于解决什么问题？

MySQL中的记录锁、间隙锁和临键锁是用于并发控制的锁机制，它们在锁定范围、锁定粒度、锁定效果以及适用场景等方面存在显著的区别。以下是关于这三种锁机制的详细比较：

1. 记录锁（Record Locks）

   - **锁定范围**：记录锁是针对单个行记录的锁，它封锁该行的索引记录。
   - **锁定粒度**：记录锁是细粒度的锁定机制，只锁定被访问的行数据。

   - **锁定效果**：记录锁能够确保在并发情况下，被锁定的行记录不会被其他事务修改或删除。

   - **适用场景**：记录锁通常用于通过主键索引或唯一索引对数据行进行UPDATE或SELECT ... FOR UPDATE操作时。

2. 间隙锁（Gap Locks）

   - **锁定范围**：间隙锁锁定索引记录之间的“间隙”，但不包括锁住索引记录本身。

   - **锁定粒度**：间隙锁是粗粒度的锁定机制，每次锁定多个索引记录之间的间隙。

   - **锁定效果**：间隙锁能够保证在并发情况下，不会出现两个事务同时插入相同索引记录之间的间隙的情况，从而避免了幻读问题。

   - **适用场景**：间隙锁适用于插入和删除操作，特别是在使用范围条件（如BETWEEN）进行查询时。


3. 临键锁（Next-Key Locks）

   - **锁定范围**：临键锁是指对索引记录上的锁，包括锁住该索引记录本身以及后面的“间隙”（包括该记录和下一个记录之间的间隙）。

   - **锁定粒度**：临键锁是细粒度的锁定机制，每次锁定一个索引记录以及后面的间隙。

   - **锁定效果**：临键锁能够确保在并发情况下，不会出现两个事务同时插入相同索引记录或相同索引记录之间的间隙的情况，从而避免了脏读、不可重复读和幻读问题。

   - **适用场景**：临键锁适用于读操作和插入操作，特别是在InnoDB存储引擎的事务处理中。


总结

- **记录锁**：针对单个行记录，确保行数据的并发一致性。
- **间隙锁**：锁定索引记录之间的间隙，避免幻读。
- **临键锁**：结合了记录锁和间隙锁的特点，既锁定行数据又锁定间隙，提供最强的并发控制。

### 记录锁（Record Lock）

#### 什么是记录锁呢？

**即锁住一条记录。注意了，该锁是对索引记录进行加锁**！锁是在加索引上而不是行上的。注意了，innodb一定存在聚簇索引，因此行锁最终都会落到聚簇索引上！

#### 什么时候触发加记录锁呢？

- `select ... for update`

  这种查询会对存在的行添加一个排他锁（X锁），这意味着其他事务不能修改这些行，也不能对这些行添加共享锁。

- `select ... lock in share mode`

  这种查询会对存在的行添加一个共享锁（S锁），这意味这其他事务不能修改这些行，但可以对这些行添加共享锁。

- `update`

  更新操作会对被更新并且存在的行添加一个排他锁（X锁）。

- `delete`

  删除操作会对被删除并且存在的行添加一个排他锁（X锁）。

#### 演示记录排他锁

```bash
# session1开启事务
mysql> begin;
Query OK, 0 rows affected (0.01 sec)

# session1对表course中id=5的记录加行排他锁
mysql> select * from course where id=5 for update;
+----+------+-----+
| id | name | age |
+----+------+-----+
|  5 | java |   5 |
+----+------+-----+
1 row in set (0.00 sec)

# 查看行锁情况
mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140565765037272:1067:140565689739696
ENGINE_TRANSACTION_ID: 1828
            THREAD_ID: 53
             EVENT_ID: 40
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140565689739696
            LOCK_TYPE: TABLE # 锁定整个表
            LOCK_MODE: IX # 自动加上意向排他锁
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140565765037272:2:4:2:140565689736704
ENGINE_TRANSACTION_ID: 1828
            THREAD_ID: 53
             EVENT_ID: 40
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY # 对主键索引加锁
OBJECT_INSTANCE_BEGIN: 140565689736704
            LOCK_TYPE: RECORD # 锁定单条记录
            LOCK_MODE: X,REC_NOT_GAP # 记录排他锁
          LOCK_STATUS: GRANTED
            LOCK_DATA: 5 # id=5的记录
2 rows in set (0.00 sec)

mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140563543620800
            LOCK_TYPE: SHARED_WRITE
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 53
       OWNER_EVENT_ID: 40
2 rows in set (0.00 sec)

# session2中使用select ... for update给同一记录加记录排他锁
# 结果sql执行一直在等待状态，因为记录排他锁和记录排他锁是互斥的
mysql> select * from course where id=5 for update;

# session2中使用select ... lock in share mode给同一记录加行共享锁
# 结果sql执行一直在等待状态，因为记录排他锁和记录共享锁是互斥的
mysql> select * from course where id=5 lock in share mode;

# session2中使用update修改同一记录
# 结果sql执行一直在等待状态，因为记录排他锁和update语句自动加上的记录排他锁是互斥的
mysql> update course set name='java' where id=5;

# session2中使用delete删除同一记录
# 结果sql执行一直在等待状态，因为记录排他锁和delete语句自动加上的记录排他锁是互斥的
mysql> delete from course where id=5;

# session2中使用insert插入同一记录
# 结果sql执行一直在等待状态，因为记录排他锁和insert语句自动加上的记录排他锁是互斥的
mysql> insert into course values(5,'java',2);

# session1提交事务释放锁
mysql> commit;
Query OK, 0 rows affected (0.00 sec)
```

#### 演示记录共享锁

```bash
# session1开启事务
mysql> begin;

# session1对表course中id=1的记录加记录共享锁
mysql> select * from course where id=5 lock in share mode;
+----+------+-----+
| id | name | age |
+----+------+-----+
|  5 | java |   5 |
+----+------+-----+
1 row in set (0.00 sec)

# 查看行锁情况
mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140565765037272:1067:140565689739696
ENGINE_TRANSACTION_ID: 422040741747928
            THREAD_ID: 53
             EVENT_ID: 49
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140565689739696
            LOCK_TYPE: TABLE # 锁定整个表
            LOCK_MODE: IS # 自动加上意向共享锁
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140565765037272:2:4:2:140565689736704
ENGINE_TRANSACTION_ID: 422040741747928
            THREAD_ID: 53
             EVENT_ID: 49
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY # 对主键索引加锁
OBJECT_INSTANCE_BEGIN: 140565689736704
            LOCK_TYPE: RECORD # 锁定单条记录
            LOCK_MODE: S,REC_NOT_GAP # 记录共享锁
          LOCK_STATUS: GRANTED
            LOCK_DATA: 5 # id=5的记录
2 rows in set (0.00 sec)

mysql> select * from performance_schema.metadata_locks\G;
*************************** 1. row ***************************
          OBJECT_TYPE: TABLE
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
          COLUMN_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140563543620800
            LOCK_TYPE: SHARED_READ
        LOCK_DURATION: TRANSACTION
          LOCK_STATUS: GRANTED
               SOURCE: sql_parse.cc:5903
      OWNER_THREAD_ID: 53
       OWNER_EVENT_ID: 49
2 rows in set (0.00 sec)

# session2中使用select ... for update给同一记录加记录排他锁
# 结果sql执行一直在等待状态，因为记录共享锁和记录排他锁是互斥的
mysql> select * from course where id=5 for update;

# session2中使用select ... lock in share mode给同一记录加记录共享锁
# 结果sql执行成功，因为记录共享锁和记录共享锁是兼容的
mysql> select * from course where id=5 lock in share mode;

# session2中使用update修改同一记录
# 结果sql执行一直在等待状态，因为记录共享锁和update语句自动加上的记录排他锁是互斥的
mysql> update course set name='java' where id=5;

# session2中使用delete删除同一记录
# 结果sql执行一直在等待状态，因为记录共享锁和delete语句自动加上的记录排他锁是互斥的
mysql> delete from course where id=5;

# session2中使用insert插入同一记录
# todo: 没有出现预期结果“sql执行一直在等待状态，因为记录共享锁和insert语句自动加上的记录排他锁是互斥的。”，却出现非预期结果“ERROR 1062 (23000): Duplicate entry '5' for key 'course.PRIMARY'”
mysql> insert into course values(5,'java',2);

# session1提交事务释放锁
mysql> commit;
Query OK, 0 rows affected (0.00 sec)
```



#### 演示记录共享锁和记录共享锁之间是兼容

```bash
# session1开启事务
mysql> begin;
Query OK, 0 rows affected (0.01 sec)

# session1对表course中id=1的记录加行共享锁
mysql> select * from course where id=5 lock in share mode;
+----+------+
| id | name |
+----+------+
|  5 | java |
+----+------+
1 row in set (0.00 sec)

# session2中对course表中id=1的记录加行共享锁
# 结果sql执行成功，因为行共享锁和行共享锁是兼容的
mysql> select * from course where id=5 lock in share mode;
+----+------+
| id | name |
+----+------+
|  5 | java |
+----+------+
1 row in set (0.00 sec)

# session1提交事务并释放锁
mysql> commit;
Query OK, 0 rows affected (0.00 sec)
```

#### 演示记录共享锁和记录排他锁之间是互斥

```bash
# session1开启事务
mysql> begin;
Query OK, 0 rows affected (0.00 sec)

# session1给course表id=1记录加表共享锁
mysql> select * from course where id=5 lock in share mode;
+----+------+
| id | name |
+----+------+
|  5 | java |
+----+------+
1 row in set (0.00 sec)

# session2中对course表中id=1的记录加行排他锁
# 结果sql执行一直在等待状态，因为行共享锁和行排他锁是互斥的
mysql> select * from course where id=5 for update;

# session1提交事务并释放锁
mysql> commit;
Query OK, 0 rows affected (0.00 sec)
```

#### 演示记录排他锁和记录排他锁之间是互斥

```bash
# session1开启事务
mysql> begin;
Query OK, 0 rows affected (0.00 sec)

# session1给course表id=1记录加行排他锁
mysql> select * from course where id=5 for update;
+----+------+
| id | name |
+----+------+
|  5 | java |
+----+------+
1 row in set (0.00 sec)

# session2中对course表中id=1的记录加行排他锁
# 结果sql执行一直在等待状态，因为行排他锁和行排他锁是互斥的
mysql> select * from course where id=5 for update;

# session1提交事务并释放锁
mysql> commit;
Query OK, 0 rows affected (0.00 sec)
```

### 间隙锁（Gap Lock）

#### 什么是间隙锁呢？

是对索引的间隙加锁，其目的只有一个，防止其他事物插入数据。在`Read Committed`隔离级别下，不会使用间隙锁。隔离级别比`Read Committed`低的情况下，也不会使用间隙锁，如隔离级别为`Read Uncommited`时，也不存在间隙锁。当隔离级别为`Repeatable Read`和`Serializable`时，就会存在间隙锁。**即锁定一个区间，左开右开（因为间隙锁不包含记录锁，所以是左开右开）。**

#### 间隙锁有哪些类型呢？

和锁的分类一样，分为共享锁和排他锁。

#### 间隙锁的兼容性

- 与其他间隙锁的兼容性：由于间隙锁是共享锁，多个事务可以同时持有同一个间隙锁，因此间隙锁之间是相互兼容的。
- 与记录锁的兼容性：间隙锁与记录锁（行锁）是不兼容的。如果一个事务已经持有了某个记录的行锁，那么其他事务无法在该记录所在的间隙上获得间隙锁。
- 与意向锁的兼容性：意向锁（Intention Locks）是InnoDB自动加的，用于表示事务准备获取某种类型的锁（共享锁或排他锁）。意向锁与间隙锁是兼容的，因为意向锁只是表示事务的加锁意向，并不实际锁定任何数据。

#### 哪些`sql`执行会触发加间隙锁呢？

- 在`RR`事务隔离级别中，`select for update`、`select lock in share mode`、`update`、`delete`语句`where`条件列没有索引时会加间隙锁（锁定表中所有间隙）。
- 在`RR`事物隔离级别中，`select for update`、`select lock in share mode`、`update`、`delete`不存在的记录时加间隙锁。
- 在`RR`事务隔离级别中，`select for update`、`select lock in share mode`、`update`、`delete`语句`where`条件列为非唯一索引时会加间隙所。

#### 演示间隙锁

```bash
# session1开启事务
mysql> begin;
Query OK, 0 rows affected (0.00 sec)

# session1给不存在的id加间隙锁
mysql> select * from course where id=18 for update;
Empty set (0.00 sec)

mysql> select * from performance_schema.data_locks\G;
*************************** 2. row ***************************
# 间隙锁，区间(16,31)
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139953947237592:7:4:7:139953858223616
ENGINE_TRANSACTION_ID: 5392
            THREAD_ID: 47
             EVENT_ID: 33
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 139953858223616
            LOCK_TYPE: RECORD
            LOCK_MODE: X,GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 31
                       
# session2插入间隙锁边界是允许的，因为间隙锁是左开右开的（id=16和id=31的记录不在主键索引间隙锁之间）
mysql> insert into course values(16,'xxx',22);
ERROR 1062 (23000): Duplicate entry '16' for key 'course.PRIMARY'
mysql> insert into course values(31,'xxx',22);
ERROR 1062 (23000): Duplicate entry '31' for key 'course.PRIMARY'

# session2插入区间中的记录，一直在等待，因为id=17在主键索引间隙锁之间
mysql> insert into course values(17,'xxx',22);

# session1释放锁
mysql> commit;
Query OK, 0 rows affected (0.01 sec)
```



#### 插入意向锁

>[参考链接](https://www.51cto.com/article/759298.html)

##### 什么是隐式锁呢？

当事物需要加锁时，如果这个锁不可能发生冲突，InnoDB会跳过加锁环节，这种机制称为隐式锁。隐式锁时InnoDB实现的延时加锁机制，只有当可能会产生冲突的时候才会加锁，减少锁的数量，提高系统的性能。在Insert过程中不加锁，遇到特殊情况，将隐式锁转为显示锁。

##### 什么是插入意向锁呢？

插入意向锁是由 INSERT 操作在插入记录之前加的一种间隙锁。插入意向锁是一种排他（LOCK_X）间隙锁（LOCK_GAP）。

由于多个间隙锁可以共存，插入记录需要加锁时，如果直接使用间隙锁，一个事务锁住了某个间隙，其它事务执行 INSERT 语句还可以插入记录到该间隙中，这样会存在幻读的问题。为了解决这个问题，InnoDB 引入了插入意向锁。

##### 显示插入意向锁

>在插入意向锁没有和其他锁冲突时，通过`performance_schema.data_locks`是无法查询到插入意向锁信息。（这种机制称为隐式锁）

```bash
# session1开启事务
begin;
# session1加间隙锁，为了间隙锁和插入意向锁冲突
select * from course where id>16 and id<31 for update;

# session1加的间隙锁信息如下
mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140484288074536:1067:140484219590880
ENGINE_TRANSACTION_ID: 2340
            THREAD_ID: 58
             EVENT_ID: 95
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140484219590880
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140484288074536:2:4:5:140484219587968
ENGINE_TRANSACTION_ID: 2340
            THREAD_ID: 58
             EVENT_ID: 95
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140484219587968
            LOCK_TYPE: RECORD
            LOCK_MODE: X,GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 31

# session2开启事务
begin;
# session2加插入意向锁但阻塞，因为和间隙锁冲突
insert into course values(18,'xxx',18);

mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140484288075344:1067:140484219596864
ENGINE_TRANSACTION_ID: 2341
            THREAD_ID: 60
             EVENT_ID: 76
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140484219596864
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
# 插入意向锁被阻塞
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140484288075344:2:4:5:140484219593952
ENGINE_TRANSACTION_ID: 2341
            THREAD_ID: 60
             EVENT_ID: 76
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140484219593952
            LOCK_TYPE: RECORD
            LOCK_MODE: X,GAP,INSERT_INTENTION
          LOCK_STATUS: WAITING
            LOCK_DATA: 31
*************************** 3. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140484288074536:1067:140484219590880
ENGINE_TRANSACTION_ID: 2340
            THREAD_ID: 58
             EVENT_ID: 95
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140484219590880
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 4. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140484288074536:2:4:5:140484219587968
ENGINE_TRANSACTION_ID: 2340
            THREAD_ID: 58
             EVENT_ID: 95
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140484219587968
            LOCK_TYPE: RECORD
            LOCK_MODE: X,GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 31

------------
TRANSACTIONS
------------
Trx id counter 2344
Purge done for trx's n:o < 2336 undo n:o < 0 state: running but idle
History list length 0
LIST OF TRANSACTIONS FOR EACH SESSION:
---TRANSACTION 421959264784384, not started
0 lock struct(s), heap size 1128, 0 row lock(s)
---TRANSACTION 421959264783576, not started
0 lock struct(s), heap size 1128, 0 row lock(s)
---TRANSACTION 421959264782768, not started
0 lock struct(s), heap size 1128, 0 row lock(s)
---TRANSACTION 421959264781960, not started
0 lock struct(s), heap size 1128, 0 row lock(s)
---TRANSACTION 2343, ACTIVE 6 sec inserting
mysql tables in use 1, locked 1
LOCK WAIT 2 lock struct(s), heap size 1128, 1 row lock(s)
MySQL thread id 14, OS thread handle 140483752056576, query id 496 172.20.1.1 root update
/* ApplicationName=IntelliJ IDEA 2023.2.5 */ insert into course values(18,'xxx',18)
------- TRX HAS BEEN WAITING 6 SEC FOR THIS LOCK TO BE GRANTED:
RECORD LOCKS space id 2 page no 4 n bits 72 index PRIMARY of table `testdb`.`course` trx id 2343 lock_mode X locks gap before rec insert intention waiting
Record lock, heap no 5 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
 0: len 8; hex 800000000000001f; asc         ;;
 1: len 6; hex 000000000914; asc       ;;
 2: len 7; hex 81000001060143; asc       C;;
 3: len 6; hex 707974686f6e; asc python;;
 4: len 4; hex 8000001f; asc     ;;

------------------
TABLE LOCK table `testdb`.`course` trx id 2343 lock mode IX
RECORD LOCKS space id 2 page no 4 n bits 72 index PRIMARY of table `testdb`.`course` trx id 2343 lock_mode X locks gap before rec insert intention waiting
Record lock, heap no 5 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
 0: len 8; hex 800000000000001f; asc         ;;
 1: len 6; hex 000000000914; asc       ;;
 2: len 7; hex 81000001060143; asc       C;;
 3: len 6; hex 707974686f6e; asc python;;
 4: len 4; hex 8000001f; asc     ;;

---TRANSACTION 2342, ACTIVE 31 sec
2 lock struct(s), heap size 1128, 1 row lock(s)
MySQL thread id 12, OS thread handle 140483755218688, query id 480 172.20.1.1 root
TABLE LOCK table `testdb`.`course` trx id 2342 lock mode IX
RECORD LOCKS space id 2 page no 4 n bits 72 index PRIMARY of table `testdb`.`course` trx id 2342 lock_mode X locks gap before rec
Record lock, heap no 5 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
 0: len 8; hex 800000000000001f; asc         ;;
 1: len 6; hex 000000000914; asc       ;;
 2: len 7; hex 81000001060143; asc       C;;
 3: len 6; hex 707974686f6e; asc python;;
 4: len 4; hex 8000001f; asc     ;;
```



##### 间隙锁会阻塞插入意向锁

```bash
# session1开启事务
begin;
# session1加锁
select * from course where id>16 and id<31 for update;

# session2插入记录被阻塞
insert into course values(18,'xxx',12);

mysql> select * from performance_schema.data_locks\G;
*************************** 2. row ***************************
# session2插入意向锁
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140497656111912:2:4:5:140497574251904
ENGINE_TRANSACTION_ID: 1844
            THREAD_ID: 74
             EVENT_ID: 42
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140497574251904
            LOCK_TYPE: RECORD
            LOCK_MODE: X,GAP,INSERT_INTENTION
          LOCK_STATUS: WAITING
            LOCK_DATA: 31
*************************** 4. row ***************************
# session1间隙锁
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140497656111104:2:4:5:140497574245920
ENGINE_TRANSACTION_ID: 1843
            THREAD_ID: 73
             EVENT_ID: 50
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140497574245920
            LOCK_TYPE: RECORD
            LOCK_MODE: X,GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 31
            
------------
TRANSACTIONS
------------
Trx id counter 1845
Purge done for trx's n:o < 1839 undo n:o < 0 state: running but idle
History list length 0
LIST OF TRANSACTIONS FOR EACH SESSION:
---TRANSACTION 421972632823376, not started
0 lock struct(s), heap size 1128, 0 row lock(s)
---TRANSACTION 421972632820952, not started
0 lock struct(s), heap size 1128, 0 row lock(s)
---TRANSACTION 421972632820144, not started
0 lock struct(s), heap size 1128, 0 row lock(s)
---TRANSACTION 421972632819336, not started
0 lock struct(s), heap size 1128, 0 row lock(s)
---TRANSACTION 1844, ACTIVE 152 sec inserting
mysql tables in use 1, locked 1
LOCK WAIT 2 lock struct(s), heap size 1128, 1 row lock(s)
MySQL thread id 28, OS thread handle 140496583227136, query id 685 172.20.41.1 root update
/* ApplicationName=IntelliJ IDEA 2023.2.5 */ insert into course values(18,'xxx',12)
------- TRX HAS BEEN WAITING 152 SEC FOR THIS LOCK TO BE GRANTED:
# 加插入意向锁但等待
RECORD LOCKS space id 2 page no 4 n bits 72 index PRIMARY of table `testdb`.`course` trx id 1844 lock_mode X locks gap before rec insert intention waiting
Record lock, heap no 5 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
 0: len 8; hex 800000000000001f; asc         ;;
 1: len 6; hex 000000000714; asc       ;;
 2: len 7; hex 81000001060143; asc       C;;
 3: len 6; hex 707974686f6e; asc python;;
 4: len 4; hex 8000001f; asc     ;;

------------------
TABLE LOCK table `testdb`.`course` trx id 1844 lock mode IX
RECORD LOCKS space id 2 page no 4 n bits 72 index PRIMARY of table `testdb`.`course` trx id 1844 lock_mode X locks gap before rec insert intention waiting
Record lock, heap no 5 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
 0: len 8; hex 800000000000001f; asc         ;;
 1: len 6; hex 000000000714; asc       ;;
 2: len 7; hex 81000001060143; asc       C;;
 3: len 6; hex 707974686f6e; asc python;;
 4: len 4; hex 8000001f; asc     ;;

---TRANSACTION 1843, ACTIVE 177 sec
2 lock struct(s), heap size 1128, 1 row lock(s)
MySQL thread id 27, OS thread handle 140496579016448, query id 664 172.20.41.1 root
TABLE LOCK table `testdb`.`course` trx id 1843 lock mode IX
RECORD LOCKS space id 2 page no 4 n bits 72 index PRIMARY of table `testdb`.`course` trx id 1843 lock_mode X locks gap before rec
Record lock, heap no 5 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
 0: len 8; hex 800000000000001f; asc         ;;
 1: len 6; hex 000000000714; asc       ;;
 2: len 7; hex 81000001060143; asc       C;;
 3: len 6; hex 707974686f6e; asc python;;
 4: len 4; hex 8000001f; asc     ;;
```

##### 插入意向锁不会阻塞间隙锁

```bash
# session1开启事务
begin;
# session1加间隙锁，区间(16,31)
select * from course where id>16 and id<31 for update;

# session2开启事务
begin;
# session2加插入意向锁但被阻塞
insert into course values(18,'xxx',18);

# session1回滚事务
rollback;

# 插入意向锁信息如下：
mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140484288075344:1067:140484219596864
ENGINE_TRANSACTION_ID: 2345
            THREAD_ID: 60
             EVENT_ID: 132
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140484219596864
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140484288075344:2:4:5:140484219593952
ENGINE_TRANSACTION_ID: 2345
            THREAD_ID: 60
             EVENT_ID: 132
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140484219593952
            LOCK_TYPE: RECORD
            LOCK_MODE: X,GAP,INSERT_INTENTION
          LOCK_STATUS: GRANTED
            LOCK_DATA: 31
            
# session3开启事务
begin;
# session3加间隙锁，区间(16,31)
select * from course where id>18 and id<31 for update;

# 插入意向锁不会阻塞间隙锁
mysql> select * from performance_schema.data_locks\G;
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140484288076152:1067:140484219603136
ENGINE_TRANSACTION_ID: 2346
            THREAD_ID: 64
             EVENT_ID: 50
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140484219603136
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140484288076152:2:4:5:140484219600224
ENGINE_TRANSACTION_ID: 2346
            THREAD_ID: 64
             EVENT_ID: 50
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140484219600224
            LOCK_TYPE: RECORD
            LOCK_MODE: X,GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 31
*************************** 3. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140484288075344:1067:140484219596864
ENGINE_TRANSACTION_ID: 2345
            THREAD_ID: 60
             EVENT_ID: 132
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140484219596864
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 4. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 140484288075344:2:4:5:140484219593952
ENGINE_TRANSACTION_ID: 2345
            THREAD_ID: 60
             EVENT_ID: 132
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 140484219593952
            LOCK_TYPE: RECORD
            LOCK_MODE: X,GAP,INSERT_INTENTION
          LOCK_STATUS: GRANTED
            LOCK_DATA: 31

```

##### 插入意向锁相互之间不会阻塞

```bash
# session1开启事务
begin;
# session1加间隙锁
select * from course where id>16 and id<31 for update;

# session2
begin;
# session2加插入意向锁但被阻塞
insert into course values(18,'xxx',18);

# session3
begin;
# session3加插入意向锁但被阻塞
insert into course values(19,'xxx',19);

# session1释放间隙锁后，session2和session3都能够成功插入记录，说明插入意向锁之间兼容
rollback;
```



### 临键锁（Next-Key Lock）

#### 什么是临键锁呢？

理解为记录锁+索引前面的间隙锁，**记录锁+间隙锁锁定的区间，左开右闭（因为临键锁包含记录锁，所以左开右闭，右闭表示记录锁）。**记住了，锁住的是索引前面的间隙！比如一个索引包含值，10，11，13和20。那么，间隙锁的范围如下：

```
(negative infinity, 10]
(10, 11]
(11, 13]
(13, 20]
(20, positive infinity)
```

#### 什么时候触发加临键锁呢？

- 在`RR`事务隔离级别中，`select lock in share mode`、`select for update`、`update`、`delete`语句`where`条件列没有索引会触发加多个临键锁以锁定整个表阻止插入新记录导致幻读。
- 在`RR`事务隔离级别中，`select lock in share mode`、`select for update`、`update`、`delete`语句`where`条件列为非唯一索引并且记录存在时会触发加临键锁一阻止插入满足`where`条件的新记录导致幻读。
- 在`RR`事务隔离级别中，`select * from course for update`会触发加多个临键排他锁锁定整个表以阻止插入新记录，`select * from course lock in share mode`会触发加多个临键共享锁锁定整个表以阻止插入新记录。

#### 演示临键锁

```bash
# 先创建非唯一索引
mysql> create index idx_course_name on course(name);
Query OK, 0 rows affected (0.02 sec)
Records: 0  Duplicates: 0  Warnings: 0

# session1开启事务
mysql> begin;
Query OK, 0 rows affected (0.00 sec)

# session1触发加临键锁
mysql> select * from course where name='java' for update;
+----+------+-----+
| id | name | age |
+----+------+-----+
|  5 | java |   5 |
+----+------+-----+
1 row in set (0.00 sec)

mysql> select * from performance_schema.data_locks\G;
*************************** 2. row ***************************
# 临键锁，区间([16,c], [5,java]]
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139953947237592:7:6:3:139953858223616
ENGINE_TRANSACTION_ID: 5418
            THREAD_ID: 47
             EVENT_ID: 40
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: idx_course_name
OBJECT_INSTANCE_BEGIN: 139953858223616
            LOCK_TYPE: RECORD
            LOCK_MODE: X
          LOCK_STATUS: GRANTED
            LOCK_DATA: 'java', 5
*************************** 3. row ***************************
# 记录锁
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139953947237592:7:4:2:139953858223960
ENGINE_TRANSACTION_ID: 5418
            THREAD_ID: 47
             EVENT_ID: 40
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: PRIMARY
OBJECT_INSTANCE_BEGIN: 139953858223960
            LOCK_TYPE: RECORD
            LOCK_MODE: X,REC_NOT_GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 5
*************************** 4. row ***************************
# 间隙锁，区间([5,java],[15,php])
               ENGINE: INNODB
       ENGINE_LOCK_ID: 139953947237592:7:6:4:139953858224304
ENGINE_TRANSACTION_ID: 5418
            THREAD_ID: 47
             EVENT_ID: 40
        OBJECT_SCHEMA: testdb
          OBJECT_NAME: course
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: idx_course_name
OBJECT_INSTANCE_BEGIN: 139953858224304
            LOCK_TYPE: RECORD
            LOCK_MODE: X,GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 'php', 15
    
# session2允许插入下面记录，因为name='b'和name='c'不在idx_course_name索引临键锁之间
mysql> insert into course values(17,'b',22);
Query OK, 1 row affected (0.00 sec)
mysql> insert into course values(14,'c',22);
Query OK, 1 row affected (0.01 sec)

# session2不允许插入下面记录（语句一直在等待），因为name='c1'和name='java'在idx_course_name索引临键锁之间
mysql> insert into course values(17,'c1',22);
mysql> insert into course values(1,'java',22);

# session1释放锁
mysql> commit;
Query OK, 0 rows affected (0.00 sec)
```

### 不同情况下记录锁、间隙锁、临键锁加锁情况

>[参考链接](https://blog.csdn.net/yzx3105/article/details/129728659)

#### `RR`事务级别时

##### 无索引等值或范围查询

>因为查询条件为无索引，所以会使用临键锁锁定所有间隙+记录。

```bash
# session1开启事务
begin;

# session1加锁
select * from course where name='java' for update;

# session2全表锁定不能插入任何数据
insert into course values(17,'xxx',17);
# session2全表锁定不能修改任何数据
update course set name='xxx' where id=16;
```



##### 唯一索引等值查询

- 记录存在时

  > 查询条件为聚簇索引或唯一索引时，临键锁退化为记录锁。

  ```bash
  # session1开启事务
  begin;
  
  # session1加锁
  select * from course where id=15 for update;
  
  # session2允许插入记录
  insert into course values(17,'xxx',17);
  # session2不能修改记录，因为有记录锁存在
  update course set name='xxx' where id=15;
  ```

  

- 记录不存在时

  >查询条件为聚簇索引或唯一索引时，临键锁退化为间隙锁。

  ```bash
  # session1开启事务
  begin;
  # session1加锁，间隙锁区间为(31,+infinity)
  select * from course where id=12345 for update;
  
  # session2允许插入间隙锁区间外的记录
  insert into course values(17,'xxx',17);
  # session2不允许插入间隙锁区间内的记录
  insert into course values(33,'xxx',33);
  ```

  

##### 唯一索引范围查询

>查询条件为聚簇索引或唯一索引时，临键锁+记录锁。

```bash
# session1开启事务
begin;
# session1加锁
select * from course where id>14 and id<19 for update;

# session2不允许插入临键锁区间
insert into course values(12,'xxx',12);
insert into course values(17,'xxx',17);
insert into course values(16,'xxx',16);

# session2不允许修改记录
update course set name='xxx' where id=15;
```



##### 非唯一索引等值查询

- 记录存在时

>临键锁+间隙锁+记录锁

```bash
# session1开启事务
begin;
# session1加锁
select * from course where name='java' for update;

# session2不允许插入
insert into course values(38,'c1',12);
insert into course values(39,'c',12);
insert into course values(40,'java',12);
insert into course values(41,'pha',12);

# session2不允许修改
update course set name='xxx' where id=5;
```



- 记录不存在时

  >间隙锁

  ```bash
  # session1开启事务
  begin;
  # session1加锁
  select * from course where name='java-noneexists' for update;
  
  # session2不能插入记录
  insert into course values(32,'java',12);
  insert into course values(2,'php',12);
  ```

  

##### 非唯一索引范围查询

>临键锁+记录锁。

```bash
# session1开启事务
begin;
# session1加锁
select * from course where name>='c' and name<='java' for update;

# session2不能插入记录
insert into course values(2,'c0',12);
# session2不能修改记录
update course set name='xxx' where id=5;
```



## 数据库死锁有哪些情况呢？

### 什么是数据库死锁呢？

数据库的死锁是指不同的事务在获取资源时相互等待，导致无法继续执行的一种情况。当发生死锁时，数据库系统会自动中断其中一个事务，以解除死锁。在数据库中，事务可以分为读事务和写事务。读事务只需要获取读锁，而写事务需要获取写锁。当多个事务同时操作同一组数据时，可能会引发死锁的出现。

### 死锁有哪些情况呢？

#### 对资源的锁定顺序不一致

重现过程

```sql
# session1开启事务
begin;
# session1更新记录id=5
update course set name='xxx' where id=5;

# session2开启事务
begin;
# session2更新记录id=15
update course set name='xxx1' where id=15;

# session1更新记录id=15
update course set name='xxx1' where id=15;

# session2更新记录id=5，此时报告死锁异常
update course set name='xxx' where id=5;
```

分析：

```bash
# 使用下面命令获取最近一次死锁信息
show engine innodb status\G;

# 注意：无法根据下面信息获取当前锁详细信息和当前锁是由什么sql触发加的
------------------------
LATEST DETECTED DEADLOCK
------------------------
# 死锁发生时间
2024-06-22 12:21:29 139953469400832
# session1
*** (1) TRANSACTION:
TRANSACTION 5561, ACTIVE 16 sec starting index read
mysql tables in use 1, locked 1
LOCK WAIT 3 lock struct(s), heap size 1128, 2 row lock(s), undo log entries 1
MySQL thread id 24, OS thread handle 139953793070848, query id 2507 172.20.35.1 root updating
/* ApplicationName=IntelliJ IDEA 2023.2.5 */ update course set name='xxx1' where id=15
# 当前事务持有的锁
*** (1) HOLDS THE LOCK(S):
# update course set name='xxx' where id=5;触发加记录排他锁
RECORD LOCKS space id 7 page no 4 n bits 88 index PRIMARY of table `testdb`.`course` trx id 5561 lock_mode X locks rec but not gap
Record lock, heap no 16 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
 0: len 8; hex 8000000000000005; asc         ;;
 1: len 6; hex 0000000015b9; asc       ;;
 2: len 7; hex 0100000140027e; asc     @ ~;;
 3: len 3; hex 787878; asc xxx;;
 4: len 4; hex 80000005; asc     ;;
# 当前事务等待的锁
*** (1) WAITING FOR THIS LOCK TO BE GRANTED:
# 等待锁是由update course set name='xxx1' where id=15;触发的，等待session2记录排他锁释放
RECORD LOCKS space id 7 page no 4 n bits 88 index PRIMARY of table `testdb`.`course` trx id 5561 lock_mode X locks rec but not gap waiting
Record lock, heap no 18 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
 0: len 8; hex 800000000000000f; asc         ;;
 1: len 6; hex 0000000015ba; asc       ;;
 2: len 7; hex 020000013e0286; asc     >  ;;
 3: len 4; hex 78787831; asc xxx1;;
 4: len 4; hex 8000000f; asc     ;;

# session2
*** (2) TRANSACTION:
TRANSACTION 5562, ACTIVE 8 sec starting index read
mysql tables in use 1, locked 1
LOCK WAIT 3 lock struct(s), heap size 1128, 2 row lock(s), undo log entries 1
MySQL thread id 23, OS thread handle 139953794127616, query id 2516 172.20.35.1 root updating
/* ApplicationName=IntelliJ IDEA 2023.2.5 */ update course set name='xxx' where id=5
# 当前事务持有的锁
*** (2) HOLDS THE LOCK(S):
# update course set name='xxx' where id=15;触发加记录排他锁
RECORD LOCKS space id 7 page no 4 n bits 88 index PRIMARY of table `testdb`.`course` trx id 5562 lock_mode X locks rec but not gap
Record lock, heap no 18 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
 0: len 8; hex 800000000000000f; asc         ;;
 1: len 6; hex 0000000015ba; asc       ;;
 2: len 7; hex 020000013e0286; asc     >  ;;
 3: len 4; hex 78787831; asc xxx1;;
 4: len 4; hex 8000000f; asc     ;;
# 当前事务等待的锁
*** (2) WAITING FOR THIS LOCK TO BE GRANTED:
# 等待锁是由update course set name='xxx1' where id=5;触发的，等待session1记录排他锁释放
RECORD LOCKS space id 7 page no 4 n bits 88 index PRIMARY of table `testdb`.`course` trx id 5562 lock_mode X locks rec but not gap waiting
Record lock, heap no 16 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
 0: len 8; hex 8000000000000005; asc         ;;
 1: len 6; hex 0000000015b9; asc       ;;
 2: len 7; hex 0100000140027e; asc     @ ~;;
 3: len 3; hex 787878; asc xxx;;
 4: len 4; hex 80000005; asc     ;;

*** WE ROLL BACK TRANSACTION (2)
```

解决办法：不同事务中的加锁顺序尽量保持统一，例如：上面示例按照`id`相同的顺序（一致降序或者升序）更新记录

#### 记录锁和间隙锁发生死锁

重现过程

```bash
# session1开启事务
begin;
# session1为id=15的主键加锁
update course set name='xxx' where id=15;

# session2开启事务
begin;
# session2为id<30的主键加间隙锁
# 因为id=15的主键被session1锁定，所以session2间隙锁在id=15的主键处一直等待session1释放id=15主键的排他锁
update course set name='xxx' where id<30;

# session1修改id=5记录，但是报告死锁错误，因为id=5的主键被session2持有
update course set name='xxx1' where id=5;
```

分析：

```bash
# 使用下面命令获取最近一次死锁信息
show engine innodb status\G;

# 注意：无法根据下面信息获取当前锁详细信息和当前锁是由什么sql触发加的
------------------------
LATEST DETECTED DEADLOCK
------------------------
2024-06-22 16:11:00 139953469400832
# session2
*** (1) TRANSACTION:
TRANSACTION 5592, ACTIVE 42 sec fetching rows
mysql tables in use 1, locked 1
LOCK WAIT 3 lock struct(s), heap size 1128, 2 row lock(s), undo log entries 1
MySQL thread id 31, OS thread handle 139953459951360, query id 2981 172.20.35.1 root updating
/* ApplicationName=IntelliJ IDEA 2023.2.5 */ update course set name='xxx' where id<30

*** (1) HOLDS THE LOCK(S):
# 当前事务持有间隙排他锁
RECORD LOCKS space id 7 page no 4 n bits 96 index PRIMARY of table `testdb`.`course` trx id 5592 lock_mode X
Record lock, heap no 24 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
 0: len 8; hex 8000000000000005; asc         ;;
 1: len 6; hex 0000000015d8; asc       ;;
 2: len 7; hex 01000001440392; asc     D  ;;
 3: len 3; hex 787878; asc xxx;;
 4: len 4; hex 80000005; asc     ;;


*** (1) WAITING FOR THIS LOCK TO BE GRANTED:
# 上面的sql尝试加间隙排他锁，等待session1记录排他锁释放
RECORD LOCKS space id 7 page no 4 n bits 96 index PRIMARY of table `testdb`.`course` trx id 5592 lock_mode X waiting
Record lock, heap no 22 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
 0: len 8; hex 800000000000000f; asc         ;;
 1: len 6; hex 0000000015d3; asc       ;;
 2: len 7; hex 02000002140151; asc       Q;;
 3: len 3; hex 787878; asc xxx;;
 4: len 4; hex 8000000f; asc     ;;

# session1
*** (2) TRANSACTION:
TRANSACTION 5587, ACTIVE 48 sec starting index read
mysql tables in use 1, locked 1
LOCK WAIT 3 lock struct(s), heap size 1128, 2 row lock(s), undo log entries 1
MySQL thread id 24, OS thread handle 139953793070848, query id 3011 172.20.35.1 root updating
/* ApplicationName=IntelliJ IDEA 2023.2.5 */ update course set name='xxx1' where id=5

*** (2) HOLDS THE LOCK(S):
# 当前事务持有记录排他锁
RECORD LOCKS space id 7 page no 4 n bits 96 index PRIMARY of table `testdb`.`course` trx id 5587 lock_mode X locks rec but not gap
Record lock, heap no 22 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
 0: len 8; hex 800000000000000f; asc         ;;
 1: len 6; hex 0000000015d3; asc       ;;
 2: len 7; hex 02000002140151; asc       Q;;
 3: len 3; hex 787878; asc xxx;;
 4: len 4; hex 8000000f; asc     ;;


*** (2) WAITING FOR THIS LOCK TO BE GRANTED:
# 上面sql尝试加记录排他锁，等待session2间隙排他锁释放
RECORD LOCKS space id 7 page no 4 n bits 96 index PRIMARY of table `testdb`.`course` trx id 5587 lock_mode X locks rec but not gap waiting
Record lock, heap no 24 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
 0: len 8; hex 8000000000000005; asc         ;;
 1: len 6; hex 0000000015d8; asc       ;;
 2: len 7; hex 01000001440392; asc     D  ;;
 3: len 3; hex 787878; asc xxx;;
 4: len 4; hex 80000005; asc     ;;

*** WE ROLL BACK TRANSACTION (2)
```

解决办法：尽量避免间隙锁，建议将间隙所转化为记录锁。例如：将上面的`update course set name='xxx' where id<30;`改为使用主键修改数据。

#### `lock in share mode`死锁

重现过程

```bash
# session1开启事务
begin;
# session1锁记录，只允许其他事务读
select * from course where id=5 lock in share mode;

# session2开启事务
begin;
# session2锁记录，只允许其他事务读
select * from course where id=5 lock in share mode;

# session1修改数据
update course set name='xxx' where id=5;

# session2修改数据，但报告死锁异常，因为session1等待session2记录共享锁，session2等待session1记录排他锁
update course set name='xxx' where id=5;
```

分析：

```bash
# 使用下面命令获取最近一次死锁信息
show engine innodb status\G;

------------------------
LATEST DETECTED DEADLOCK
------------------------
2024-06-22 17:43:35 139953469400832
*** (1) TRANSACTION:
TRANSACTION 5599, ACTIVE 24 sec starting index read
mysql tables in use 1, locked 1
LOCK WAIT 4 lock struct(s), heap size 1128, 2 row lock(s)
MySQL thread id 24, OS thread handle 139953793070848, query id 3202 172.20.35.1 root updating
/* ApplicationName=IntelliJ IDEA 2023.2.5 */ update course set name='xxx' where id=5

*** (1) HOLDS THE LOCK(S):
# 当前持有记录共享锁
RECORD LOCKS space id 7 page no 4 n bits 96 index PRIMARY of table `testdb`.`course` trx id 5599 lock mode S locks rec but not gap
Record lock, heap no 26 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
 0: len 8; hex 8000000000000005; asc         ;;
 1: len 6; hex 000000000f96; asc       ;;
 2: len 7; hex 82000000a90110; asc        ;;
 3: len 4; hex 6a617661; asc java;;
 4: len 4; hex 80000005; asc     ;;


*** (1) WAITING FOR THIS LOCK TO BE GRANTED:
# 请求记录排他锁，等待session2释放记录共享锁
RECORD LOCKS space id 7 page no 4 n bits 96 index PRIMARY of table `testdb`.`course` trx id 5599 lock_mode X locks rec but not gap waiting
Record lock, heap no 26 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
 0: len 8; hex 8000000000000005; asc         ;;
 1: len 6; hex 000000000f96; asc       ;;
 2: len 7; hex 82000000a90110; asc        ;;
 3: len 4; hex 6a617661; asc java;;
 4: len 4; hex 80000005; asc     ;;


*** (2) TRANSACTION:
TRANSACTION 5600, ACTIVE 19 sec starting index read
mysql tables in use 1, locked 1
LOCK WAIT 4 lock struct(s), heap size 1128, 2 row lock(s)
MySQL thread id 31, OS thread handle 139953459951360, query id 3212 172.20.35.1 root updating
/* ApplicationName=IntelliJ IDEA 2023.2.5 */ update course set name='xxx' where id=5

*** (2) HOLDS THE LOCK(S):
# 当前持有记录共享锁
RECORD LOCKS space id 7 page no 4 n bits 96 index PRIMARY of table `testdb`.`course` trx id 5600 lock mode S locks rec but not gap
Record lock, heap no 26 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
 0: len 8; hex 8000000000000005; asc         ;;
 1: len 6; hex 000000000f96; asc       ;;
 2: len 7; hex 82000000a90110; asc        ;;
 3: len 4; hex 6a617661; asc java;;
 4: len 4; hex 80000005; asc     ;;


*** (2) WAITING FOR THIS LOCK TO BE GRANTED:
# 请求记录排他锁，等待session1释放记录共享锁
RECORD LOCKS space id 7 page no 4 n bits 96 index PRIMARY of table `testdb`.`course` trx id 5600 lock_mode X locks rec but not gap waiting
Record lock, heap no 26 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
 0: len 8; hex 8000000000000005; asc         ;;
 1: len 6; hex 000000000f96; asc       ;;
 2: len 7; hex 82000000a90110; asc        ;;
 3: len 4; hex 6a617661; asc java;;
 4: len 4; hex 80000005; asc     ;;

*** WE ROLL BACK TRANSACTION (2)
```

解决办法：

1. 使用悲观锁`select * from course where id=5 for update`锁定记录，这样会导致并发性能降低。
2. 使用乐观锁，这里要注意，当同时两个会话针对同一行数据执行上述更新操作的时候，可能会导致同一行的记录被锁，所以我们在进行update的时候，可以用一个version字段去管理。但是这种设计，可能会导致一次更新失败，需要进行重试，因此并发量高的情况下，容易对MySQL造成较大的压力。
3. 在业务层使用分布式锁控制并发。

#### todo 暂时不知道称呼什么

> https://blog.csdn.net/cxyxysam/article/details/136301789

```bash
# session1开启事务
begin;
# session1判断记录是否存在
select * from course where id=18 for update;

# session2开启事务
begin;
# session2判断记录是否存在
select * from course where id=19 for update;

# session1判断记录不存在则插入
insert into course values(18,'xxx',18);

# session2判断记录不存在则插入，此时报告死锁
insert into course values(19,'xxx',19);

# session1回滚事务
rollback;
```



#### `insert`触发的死锁

>https://zhuanlan.zhihu.com/p/683009416
>
>https://baijiahao.baidu.com/s?id=1781188447015451234&wfr=spider&for=pc



## 业务开发中应该如何避免死锁呢？

- 不同事务中的加锁顺序尽量保持统一
- 尽量避免大事务，占有的资源锁越多，越容易出现死锁。建议拆成小事务
- 尽量避免间隙锁。建议将间隙所转化为行锁

## 数据库死锁问题如何监控和排查呢？

> [MySQL死锁系列-线上死锁问题排查思路](https://www.cnblogs.com/remcarpediem/p/13843180.html)

### 监控和排查死锁步骤

1. 通过`prometheus`监控并报警`mysql`死锁
2. 查看应用报告的死锁错误日志堆栈信息（找到死锁对应的具体业务）
3. 查看`mysql`死锁相关日志
   - 如果启用`innodb_status_output`，则在`mysql log_error`配置的错误日志中找出和应用报告死锁时间接近的日志
   - 如果没有启用`innodb_status_output`，则使用`show engine innodb status\G;`查看最近一次死锁信息
4. 根据`mysql`日志中的信息找到死锁相关的事务`id`，然后再根据相关事务`id` 在`binlog`中查看执行的具体`sql`
5. 通过分析或者建立模拟环境一步步执行上面找到的`sql`重现死锁过程
6. 修改业务代码解决死锁问题

### 演示排查死锁步骤

下面是一个简单的示例，简化模拟应用开发中遇到死锁场景，并按照上面提到的步骤逐步分析死锁问题。

1. 通过运行基于`mybatis-plus`的死锁示例触发死锁 [示例链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-mysql/demo-mybatis-plus-deadlock)

2. 查看示例中的控制台确认业务代码已经打印死锁异常日志

3. 查看`mysql`死锁相关日志

   ```bash
   # 死锁发生时即时触发的死锁日志
   TRANSACTION 2317, ACTIVE 1 sec starting index read
   mysql tables in use 1, locked 1
   LOCK WAIT 4 lock struct(s), heap size 1128, 2 row lock(s)
   MySQL thread id 9, OS thread handle 140088953771776, query id 59 127.0.0.1 root updating
   UPDATE course  SET name='javab19b1693-7d80-4b48-9195-8fa489eedd02', age=5  WHERE id=5
   RECORD LOCKS space id 2 page no 4 n bits 72 index PRIMARY of table `demo`.`course` trx id 2317 lock mode S locks rec but not gap
   Record lock, heap no 6 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
    0: len 8; hex 8000000000000005; asc         ;;
    1: len 6; hex 00000000053b; asc      ;;;
    2: len 7; hex 81000001110110; asc        ;;
    3: len 4; hex 6a617661; asc java;;
    4: len 4; hex 80000005; asc     ;;
   
   RECORD LOCKS space id 2 page no 4 n bits 72 index PRIMARY of table `demo`.`course` trx id 2317 lock_mode X locks rec but not gap waiting
   Record lock, heap no 6 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
    0: len 8; hex 8000000000000005; asc         ;;
    1: len 6; hex 00000000053b; asc      ;;;
    2: len 7; hex 81000001110110; asc        ;;
    3: len 4; hex 6a617661; asc java;;
    4: len 4; hex 80000005; asc     ;;
   
   TRANSACTION 2318, ACTIVE 1 sec starting index read
   mysql tables in use 1, locked 1
   LOCK WAIT 4 lock struct(s), heap size 1128, 2 row lock(s)
   MySQL thread id 8, OS thread handle 140089352570624, query id 61 127.0.0.1 root updating
   UPDATE course  SET name='java676c73cc-2cf5-4fc8-9c7a-d9c42a604959', age=5  WHERE id=5
   RECORD LOCKS space id 2 page no 4 n bits 72 index PRIMARY of table `demo`.`course` trx id 2318 lock mode S locks rec but not gap
   Record lock, heap no 6 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
    0: len 8; hex 8000000000000005; asc         ;;
    1: len 6; hex 00000000053b; asc      ;;;
    2: len 7; hex 81000001110110; asc        ;;
    3: len 4; hex 6a617661; asc java;;
    4: len 4; hex 80000005; asc     ;;
   
   RECORD LOCKS space id 2 page no 4 n bits 72 index PRIMARY of table `demo`.`course` trx id 2318 lock_mode X locks rec but not gap waiting
   Record lock, heap no 6 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
    0: len 8; hex 8000000000000005; asc         ;;
    1: len 6; hex 00000000053b; asc      ;;;
    2: len 7; hex 81000001110110; asc        ;;
    3: len 4; hex 6a617661; asc java;;
    4: len 4; hex 80000005; asc     ;;
     
   # 周期性打印的死锁日志
   ------------------------
   LATEST DETECTED DEADLOCK
   ------------------------
   2024-06-24 00:05:00 140089020913408
   *** (1) TRANSACTION:
   TRANSACTION 2317, ACTIVE 1 sec starting index read
   mysql tables in use 1, locked 1
   LOCK WAIT 4 lock struct(s), heap size 1128, 2 row lock(s)
   MySQL thread id 9, OS thread handle 140088953771776, query id 59 127.0.0.1 root updating
   UPDATE course  SET name='javab19b1693-7d80-4b48-9195-8fa489eedd02', age=5  WHERE id=5
   
   *** (1) HOLDS THE LOCK(S):
   RECORD LOCKS space id 2 page no 4 n bits 72 index PRIMARY of table `demo`.`course` trx id 2317 lock mode S locks rec but not gap
   Record lock, heap no 6 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
    0: len 8; hex 8000000000000005; asc         ;;
    1: len 6; hex 00000000053b; asc      ;;;
    2: len 7; hex 81000001110110; asc        ;;
    3: len 4; hex 6a617661; asc java;;
    4: len 4; hex 80000005; asc     ;;
   
   
   *** (1) WAITING FOR THIS LOCK TO BE GRANTED:
   RECORD LOCKS space id 2 page no 4 n bits 72 index PRIMARY of table `demo`.`course` trx id 2317 lock_mode X locks rec but not gap waiting
   Record lock, heap no 6 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
    0: len 8; hex 8000000000000005; asc         ;;
    1: len 6; hex 00000000053b; asc      ;;;
    2: len 7; hex 81000001110110; asc        ;;
    3: len 4; hex 6a617661; asc java;;
    4: len 4; hex 80000005; asc     ;;
   
   
   *** (2) TRANSACTION:
   TRANSACTION 2318, ACTIVE 1 sec starting index read
   mysql tables in use 1, locked 1
   LOCK WAIT 4 lock struct(s), heap size 1128, 2 row lock(s)
   MySQL thread id 8, OS thread handle 140089352570624, query id 61 127.0.0.1 root updating
   UPDATE course  SET name='java676c73cc-2cf5-4fc8-9c7a-d9c42a604959', age=5  WHERE id=5
   
   *** (2) HOLDS THE LOCK(S):
   RECORD LOCKS space id 2 page no 4 n bits 72 index PRIMARY of table `demo`.`course` trx id 2318 lock mode S locks rec but not gap
   Record lock, heap no 6 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
    0: len 8; hex 8000000000000005; asc         ;;
    1: len 6; hex 00000000053b; asc      ;;;
    2: len 7; hex 81000001110110; asc        ;;
    3: len 4; hex 6a617661; asc java;;
    4: len 4; hex 80000005; asc     ;;
   
   
   *** (2) WAITING FOR THIS LOCK TO BE GRANTED:
   RECORD LOCKS space id 2 page no 4 n bits 72 index PRIMARY of table `demo`.`course` trx id 2318 lock_mode X locks rec but not gap waiting
   Record lock, heap no 6 PHYSICAL RECORD: n_fields 5; compact format; info bits 0
    0: len 8; hex 8000000000000005; asc         ;;
    1: len 6; hex 00000000053b; asc      ;;;
    2: len 7; hex 81000001110110; asc        ;;
    3: len 4; hex 6a617661; asc java;;
    4: len 4; hex 80000005; asc     ;;
   
   *** WE ROLL BACK TRANSACTION (2)
   ```

   

## 数据库锁给并发性能带来的影响

> todo ...