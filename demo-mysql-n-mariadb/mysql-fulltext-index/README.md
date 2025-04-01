## MySQL fulltext index学习

没有深入学习MySQL fulltext index，因为MariadbDB不支持ngram分词解析器，只有MySQL支持ngram分词解析器

## MySQL fulltext index测试脚本

```sql
create database if not exists demo_fulltext default character set utf8mb4 collate utf8mb4_unicode_ci;

use demo_fulltext;

create table if not exists t_fulltext(
	id	bigint not null primary key,
    content text
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

# 创建fulltext索引
alter table t_fulltext add fulltext index tfulltextFt1(content) with parser ngram;

# 下面等价于alter table
# create fulltext index tfulltextFt1 on t_fulltext(content) with parser ngram;

insert into t_fulltext(id,content) select 1, "一路一带" from dual
where not exists(select id from t_fulltext where id=1);
```

## 借助information_scheme数据库协助分析MySQL fulltext index

### 分析分词数据

https://dev.mysql.com/doc/refman/5.7/en/information-schema-innodb-ft-index-table-table.html

```sql
# 查看INNODB_FT_INDEX_TABLE之前需要先启用这个功能

# 启用innodb_optimize_fulltext_only变量
set global innodb_optimize_fulltext_only=on;

# fulltext index缓存写盘
optimize table t_fulltext;

# 设置innodb_ft_aux_table变量
set global innodb_ft_aux_table = 'demo_fulltext/t_fulltext';

# 查询分词数据
select * from information_schema.innodb_ft_index_table;
```