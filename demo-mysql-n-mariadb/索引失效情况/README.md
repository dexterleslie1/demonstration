# 索引失效情况

> https://blog.51cto.com/u_11966318/5825208
>
> https://m.php.cn/article/487049.html

## 准备测试数据

```
CREATE DATABASE IF NOT EXISTS testdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

use testdb;

drop table test_user;

create table test_user(
 id bigint primary key not null auto_increment,
 name varchar(512) not null,
 age int not null,
 sex char(1) not null,
 score double not null,
 createTime datetime not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

create index idx_test_user_comb1 on test_user(name,age,sex);
create index idx_test_user_score on test_user(score);

insert into test_user(id,name,age,sex,score,createTime) values
(NULL,'张三',56,'男',85.47,now()),
(NULL,'李四',33,'男',25.47,now()),
(NULL,'王五',23,'男',35.87,now()),
(NULL,'老黄',65,'女',27.76,now()),
(NULL,'积分',12,'男',86.34,now()),
(NULL,'123',11,'女',24.56,now()),
(NULL,'就佛额ur',22,'男',45.77,now()),
(NULL,'发咯入耳',56,'女',36.45,now()),
(NULL,'经济而',77,'男',76.87,now()),
(NULL,'第一热',22,'女',34.76,now()),
(NULL,'黄芪',24,'男',55.76,now()),
(NULL,'解决开发就',11,'男',33.56,now());
```

## 不符合最左前缀法则

```
# 符合idx_test_user_name_n_age索引的最左前缀法则
mysql> explain select * from test_user where name='李四' and age=33;
+----+-------------+-----------+------------+------+--------------------------+--------------------------+---------+-------------+------+----------+-------+
| id | select_type | table     | partitions | type | possible_keys            | key                      | key_len | ref         | rows | filtered | Extra |
+----+-------------+-----------+------------+------+--------------------------+--------------------------+---------+-------------+------+----------+-------+
|  1 | SIMPLE      | test_user | NULL       | ref  | idx_test_user_name_n_age | idx_test_user_name_n_age | 2054    | const,const |    1 |   100.00 | NULL  |
+----+-------------+-----------+------------+------+--------------------------+--------------------------+---------+-------------+------+----------+-------+
1 row in set, 1 warning (0.00 sec)

# 不符合索引的最左前缀法则
mysql> explain select * from test_user where age=33;
+----+-------------+-----------+------------+------+---------------+------+---------+------+------+----------+-------------+
| id | select_type | table     | partitions | type | possible_keys | key  | key_len | ref  | rows | filtered | Extra       |
+----+-------------+-----------+------------+------+---------------+------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | test_user | NULL       | ALL  | NULL          | NULL | NULL    | NULL |   12 |    10.00 | Using where |
+----+-------------+-----------+------------+------+---------------+------+---------+------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)
```



## like以%开头

```
# %为前缀索引失效
mysql> explain select * from test_user where name like '%黄';
+----+-------------+-----------+------------+------+---------------+------+---------+------+------+----------+-------------+
| id | select_type | table     | partitions | type | possible_keys | key  | key_len | ref  | rows | filtered | Extra       |
+----+-------------+-----------+------------+------+---------------+------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | test_user | NULL       | ALL  | NULL          | NULL | NULL    | NULL |   12 |    11.11 | Using where |
+----+-------------+-----------+------------+------+---------------+------+---------+------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)

# %不为前缀索引不失效
mysql> explain select id from test_user where name like '%黄';
+----+-------------+-----------+------------+-------+---------------+--------------------------+---------+------+------+----------+--------------------------+
| id | select_type | table     | partitions | type  | possible_keys | key                      | key_len | ref  | rows | filtered | Extra                    |
+----+-------------+-----------+------------+-------+---------------+--------------------------+---------+------+------+----------+--------------------------+
|  1 | SIMPLE      | test_user | NULL       | index | NULL          | idx_test_user_name_n_age | 2054    | NULL |   12 |    11.11 | Using where; Using index |
+----+-------------+-----------+------------+-------+---------------+--------------------------+---------+------+------+----------+--------------------------+
1 row in set, 1 warning (0.00 sec)
```

## 函数计算或者会导致索引失效

```
# 因为name列使用了函数left导致索引失效后进行全表扫描
mysql> explain select * from test_user where left(name,1)='黄';
+----+-------------+-----------+------------+------+---------------+------+---------+------+------+----------+-------------+
| id | select_type | table     | partitions | type | possible_keys | key  | key_len | ref  | rows | filtered | Extra       |
+----+-------------+-----------+------------+------+---------------+------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | test_user | NULL       | ALL  | NULL          | NULL | NULL    | NULL |   12 |   100.00 | Using where |
+----+-------------+-----------+------------+------+---------------+------+---------+------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)

```

## 对索引列进行数学运算(如：+、-、*、/)

```
# 对age索引列进行数学运算导致索引部分失效
mysql> explain select * from test_user where name='黄芪' and age+1=24;
+----+-------------+-----------+------------+------+--------------------------+--------------------------+---------+-------+------+----------+-----------------------+
| id | select_type | table     | partitions | type | possible_keys            | key                      | key_len | ref   | rows | filtered | Extra                 |
+----+-------------+-----------+------------+------+--------------------------+--------------------------+---------+-------+------+----------+-----------------------+
|  1 | SIMPLE      | test_user | NULL       | ref  | idx_test_user_name_n_age | idx_test_user_name_n_age | 2050    | const |    1 |   100.00 | Using index condition |
+----+-------------+-----------+------------+------+--------------------------+--------------------------+---------+-------+------+----------+-----------------------+
1 row in set, 1 warning (0.00 sec)
```

## 如果字段类型是字符串，where查询时一定用引号括起来，否则索引失效

```
mysql> explain select * from test_user where name=123;
+----+-------------+-----------+------------+------+--------------------------+------+---------+------+------+----------+-------------+
| id | select_type | table     | partitions | type | possible_keys            | key  | key_len | ref  | rows | filtered | Extra       |
+----+-------------+-----------+------------+------+--------------------------+------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | test_user | NULL       | ALL  | idx_test_user_name_n_age | NULL | NULL    | NULL |   12 |    10.00 | Using where |
+----+-------------+-----------+------------+------+--------------------------+------+---------+------+------+----------+-------------+
1 row in set, 3 warnings (0.00 sec)
```

## 范围条件右边的列索引失效

> 范围右边的列不能使用索引。比如 < 、<=、 >、 >=、 between。
>
> 创建的联合索引中，务必把范围涉及到的字段写在最后。

```
# 所有查询条件使用索引
mysql> explain select * from test_user where name='黄芪' and age=24 and sex='男';
+----+-------------+-----------+------------+------+---------------------+---------------------+---------+-------------------+------+----------+-------+
| id | select_type | table     | partitions | type | possible_keys       | key                 | key_len | ref               | rows | filtered | Extra |
+----+-------------+-----------+------------+------+---------------------+---------------------+---------+-------------------+------+----------+-------+
|  1 | SIMPLE      | test_user | NULL       | ref  | idx_test_user_comb1 | idx_test_user_comb1 | 2058    | const,const,const |    1 |   100.00 | NULL  |
+----+-------------+-----------+------------+------+---------------------+---------------------+---------+-------------------+------+----------+-------+
1 row in set, 1 warning (0.00 sec)

# 因为age>24导致sex没有使用索引查询，解决办法是创建以下索引
# create index idx_test_user_comb2 on test_user(name,sex,age);
mysql> explain select * from test_user where name='黄芪' and age>24 and sex='男';
+----+-------------+-----------+------------+-------+---------------------+---------------------+---------+------+------+----------+-----------------------+
| id | select_type | table     | partitions | type  | possible_keys       | key                 | key_len | ref  | rows | filtered | Extra                 |
+----+-------------+-----------+------------+-------+---------------------+---------------------+---------+------+------+----------+-----------------------+
|  1 | SIMPLE      | test_user | NULL       | range | idx_test_user_comb1 | idx_test_user_comb1 | 2054    | NULL |    1 |    10.00 | Using index condition |
+----+-------------+-----------+------------+-------+---------------------+---------------------+---------+------+------+----------+-----------------------+
1 row in set, 1 warning (0.00 sec)
```

## 索引字段上使用(!=、<>、not in)时，可能会导致索引失效

```
mysql> explain select * from test_user where name!='黄芪';
+----+-------------+-----------+------------+------+---------------------+------+---------+------+------+----------+-------------+
| id | select_type | table     | partitions | type | possible_keys       | key  | key_len | ref  | rows | filtered | Extra       |
+----+-------------+-----------+------------+------+---------------------+------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | test_user | NULL       | ALL  | idx_test_user_comb1 | NULL | NULL    | NULL |   12 |   100.00 | Using where |
+----+-------------+-----------+------------+------+---------------------+------+---------+------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)

mysql> explain select * from test_user where name<>'黄芪';
+----+-------------+-----------+------------+------+---------------------+------+---------+------+------+----------+-------------+
| id | select_type | table     | partitions | type | possible_keys       | key  | key_len | ref  | rows | filtered | Extra       |
+----+-------------+-----------+------------+------+---------------------+------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | test_user | NULL       | ALL  | idx_test_user_comb1 | NULL | NULL    | NULL |   12 |   100.00 | Using where |
+----+-------------+-----------+------------+------+---------------------+------+---------+------+------+----------+-------------+
1 row in set, 1 warning (0.01 sec)

mysql> explain select * from test_user where name not in('黄芪');
+----+-------------+-----------+------------+------+---------------------+------+---------+------+------+----------+-------------+
| id | select_type | table     | partitions | type | possible_keys       | key  | key_len | ref  | rows | filtered | Extra       |
+----+-------------+-----------+------------+------+---------------------+------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | test_user | NULL       | ALL  | idx_test_user_comb1 | NULL | NULL    | NULL |   12 |   100.00 | Using where |
+----+-------------+-----------+------------+------+---------------------+------+---------+------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)
```

## 在索引字段上使用is null、is not null，可能导致索引失效

> 如果is null记录很多

### is null测试

```
drop table test1;

create table test1(
 id bigint primary key not null auto_increment,
 name varchar(512) not null,
 age int not null,
 sex char(1),
 createTime datetime not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

create index idx_test1_comb1 on test1(sex);

insert into test1(id,name,age,sex,createTime) values
(NULL,'张三',56,'男',now()),
(NULL,'李四',33,'男',now()),
(NULL,'王五',23,'男',now()),
(NULL,'老黄',65,'男',now()),
(NULL,'积分',12,NULL,now()),
(NULL,'123',11,'男',now()),
(NULL,'就佛额ur',22,'男',now()),
(NULL,'发咯入耳',56,'男',now()),
(NULL,'经济而',77,'男',now()),
(NULL,'第一热',22,'男',now()),
(NULL,'黄芪',24,'男',now()),
(NULL,'解决开发就',11,NULL,now());

# is null数据少时使用了idx_test1_comb1索引
mysql> explain select * from test1 where sex is null;
+----+-------------+-------+------------+------+-----------------+-----------------+---------+-------+------+----------+-----------------------+
| id | select_type | table | partitions | type | possible_keys   | key             | key_len | ref   | rows | filtered | Extra                 |
+----+-------------+-------+------------+------+-----------------+-----------------+---------+-------+------+----------+-----------------------+
|  1 | SIMPLE      | test1 | NULL       | ref  | idx_test1_comb1 | idx_test1_comb1 | 5       | const |    2 |   100.00 | Using index condition |
+----+-------------+-------+------------+------+-----------------+-----------------+---------+-------+------+----------+-----------------------+
1 row in set, 1 warning (0.00 sec)

drop table test1;

create table test1(
 id bigint primary key not null auto_increment,
 name varchar(512) not null,
 age int not null,
 sex char(1),
 createTime datetime not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

create index idx_test1_comb1 on test1(sex);

insert into test1(id,name,age,sex,createTime) values
(NULL,'张三',56,NULL,now()),
(NULL,'李四',33,NULL,now()),
(NULL,'王五',23,NULL,now()),
(NULL,'老黄',65,NULL,now()),
(NULL,'积分',12,NULL,now()),
(NULL,'123',11,NULL,now()),
(NULL,'就佛额ur',22,NULL,now()),
(NULL,'发咯入耳',56,NULL,now()),
(NULL,'经济而',77,NULL,now()),
(NULL,'第一热',22,'男',now()),
(NULL,'黄芪',24,'男',now()),
(NULL,'解决开发就',11,NULL,now());

# is null数据多时使用了idx_test1_comb1索引
mysql> explain select * from test1 where sex is null;
+----+-------------+-------+------------+------+-----------------+------+---------+------+------+----------+-------------+
| id | select_type | table | partitions | type | possible_keys   | key  | key_len | ref  | rows | filtered | Extra       |
+----+-------------+-------+------------+------+-----------------+------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | test1 | NULL       | ALL  | idx_test1_comb1 | NULL | NULL    | NULL |   12 |    83.33 | Using where |
+----+-------------+-------+------------+------+-----------------+------+---------+------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)
```

### is not null测试

```
drop table test1;

create table test1(
 id bigint primary key not null auto_increment,
 name varchar(512) not null,
 age int not null,
 sex char(1),
 createTime datetime not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

create index idx_test1_comb1 on test1(sex);

insert into test1(id,name,age,sex,createTime) values
(NULL,'张三',56,NULL,now()),
(NULL,'李四',33,NULL,now()),
(NULL,'王五',23,NULL,now()),
(NULL,'老黄',65,NULL,now()),
(NULL,'积分',12,NULL,now()),
(NULL,'123',11,NULL,now()),
(NULL,'就佛额ur',22,NULL,now()),
(NULL,'发咯入耳',56,NULL,now()),
(NULL,'经济而',77,NULL,now()),
(NULL,'第一热',22,'男',now()),
(NULL,'黄芪',24,'男',now()),
(NULL,'解决开发就',11,NULL,now());

# is not null数据少时使用了索引
mysql> explain select * from test1 where sex is not null;
+----+-------------+-------+------------+-------+-----------------+-----------------+---------+------+------+----------+-----------------------+
| id | select_type | table | partitions | type  | possible_keys   | key             | key_len | ref  | rows | filtered | Extra                 |
+----+-------------+-------+------------+-------+-----------------+-----------------+---------+------+------+----------+-----------------------+
|  1 | SIMPLE      | test1 | NULL       | range | idx_test1_comb1 | idx_test1_comb1 | 5       | NULL |    2 |   100.00 | Using index condition |
+----+-------------+-------+------------+-------+-----------------+-----------------+---------+------+------+----------+-----------------------+
1 row in set, 1 warning (0.00 sec)

drop table test1;

create table test1(
 id bigint primary key not null auto_increment,
 name varchar(512) not null,
 age int not null,
 sex char(1),
 createTime datetime not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

create index idx_test1_comb1 on test1(sex);

insert into test1(id,name,age,sex,createTime) values
(NULL,'张三',56,'男',now()),
(NULL,'李四',33,'男',now()),
(NULL,'王五',23,'男',now()),
(NULL,'老黄',65,'男',now()),
(NULL,'积分',12,NULL,now()),
(NULL,'123',11,'男',now()),
(NULL,'就佛额ur',22,'男',now()),
(NULL,'发咯入耳',56,'男',now()),
(NULL,'经济而',77,'男',now()),
(NULL,'第一热',22,'男',now()),
(NULL,'黄芪',24,'男',now()),
(NULL,'解决开发就',11,NULL,now());

# is not null数据多时不使用索引
mysql> explain select * from test1 where sex is not null;
+----+-------------+-------+------------+------+-----------------+------+---------+------+------+----------+-------------+
| id | select_type | table | partitions | type | possible_keys   | key  | key_len | ref  | rows | filtered | Extra       |
+----+-------------+-------+------------+------+-----------------+------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | test1 | NULL       | ALL  | idx_test1_comb1 | NULL | NULL    | NULL |   12 |    83.33 | Using where |
+----+-------------+-------+------------+------+-----------------+------+---------+------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)
```

## or查询前后存在非索引的列，索引失效

```
# or两个条件索引都生效，所以or查询索引没有失效
mysql> explain select * from test_user where name='黄芪' or (name='王五' and age=23);
+----+-------------+-----------+------------+-------+---------------------+---------------------+---------+------+------+----------+-----------------------+
| id | select_type | table     | partitions | type  | possible_keys       | key                 | key_len | ref  | rows | filtered | Extra                 |
+----+-------------+-----------+------------+-------+---------------------+---------------------+---------+------+------+----------+-----------------------+
|  1 | SIMPLE      | test_user | NULL       | range | idx_test_user_comb1 | idx_test_user_comb1 | 2054    | NULL |    2 |   100.00 | Using index condition |
+----+-------------+-----------+------------+-------+---------------------+---------------------+---------+------+------+----------+-----------------------+
1 row in set, 1 warning (0.00 sec)

# 因为age=23没有索引所以导致or查询索引失效
mysql> explain select * from test_user where name='黄芪' or (age=23);
+----+-------------+-----------+------------+------+---------------------+------+---------+------+------+----------+-------------+
| id | select_type | table     | partitions | type | possible_keys       | key  | key_len | ref  | rows | filtered | Extra       |
+----+-------------+-----------+------------+------+---------------------+------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | test_user | NULL       | ALL  | idx_test_user_comb1 | NULL | NULL    | NULL |   12 |    19.00 | Using where |
+----+-------------+-----------+------------+------+---------------------+------+---------+------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)
```

## MySQL评估使用全表扫描要比使用索引快，则不使用索引

```
# 大部分记录都大于等于10，所以查询自动优化不使用索引
mysql> explain select * from test_user where score>=10;
+----+-------------+-----------+------------+------+---------------------+------+---------+------+------+----------+-------------+
| id | select_type | table     | partitions | type | possible_keys       | key  | key_len | ref  | rows | filtered | Extra       |
+----+-------------+-----------+------------+------+---------------------+------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | test_user | NULL       | ALL  | idx_test_user_score | NULL | NULL    | NULL |   12 |   100.00 | Using where |
+----+-------------+-----------+------------+------+---------------------+------+---------+------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)

# 大于等于90的记录少查询使用索引
mysql> explain select * from test_user where score>=90;
+----+-------------+-----------+------------+-------+---------------------+---------------------+---------+------+------+----------+-----------------------+
| id | select_type | table     | partitions | type  | possible_keys       | key                 | key_len | ref  | rows | filtered | Extra                 |
+----+-------------+-----------+------------+-------+---------------------+---------------------+---------+------+------+----------+-----------------------+
|  1 | SIMPLE      | test_user | NULL       | range | idx_test_user_score | idx_test_user_score | 8       | NULL |    1 |   100.00 | Using index condition |
+----+-------------+-----------+------------+-------+---------------------+---------------------+---------+------+------+----------+-----------------------+
1 row in set, 1 warning (0.00 sec)
```



