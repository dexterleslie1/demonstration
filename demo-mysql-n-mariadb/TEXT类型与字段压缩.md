# MariaDB TEXT 类型与字段压缩

演示用 `MEDIUMTEXT` 存储较长 JSON，并对比**未压缩**与**字段级压缩**（`COMPRESSED`）的磁盘占用。

## TEXT 类型简介

MariaDB/MySQL 中变长文本常用以下类型（按**字节**计上限）：

| 类型 | 最大长度 | 大约容量 |
|------|----------|----------|
| `TEXT` | 65,535 字节 | 约 64KB |
| `MEDIUMTEXT` | 16,777,215 字节 | 约 16MB |
| `LONGTEXT` | 4,294,967,295 字节 | 约 4GB |

说明：

- 上限按字节计算；`utf8mb4` 下中文通常占 3～4 字节，实际可存字符数少于字节上限。
- 类型声明本身几乎不占满上限空间，实际占用取决于写入内容长度。
- MariaDB 的 `JSON` 类型底层是 `LONGTEXT`，并额外校验 JSON 合法性。
- 存较长 JSON：一般十几 MB 内用 `MEDIUMTEXT`；更大用 `LONGTEXT` / `JSON`。

## 字段级压缩（COMPRESSED）

MariaDB 10.3+ 支持存储引擎无关的**列压缩**，写在列定义上：

```sql
content mediumtext compressed
-- 等价于显式指定算法（目前仅支持 zlib）
content mediumtext compressed=zlib
```

注意：

- 列上只能声明是否压缩及算法；**压缩级别不能写在列上**。
- 压缩强度通过会话/全局变量控制，例如：

```sql
-- 压缩级别：1 最快，9 压缩率最高，默认 6
set session column_compression_zlib_level = 9;

-- 短于该字节数的值不压缩，默认 100
set session column_compression_threshold = 100;
```

- 压缩列不能建索引。
- 字段压缩与 InnoDB 页压缩（`PAGE_COMPRESSED`）是两套机制，不要叠在同一数据上重复压缩。

## 性能与磁盘占用

- **类型选择**（`MEDIUMTEXT` vs `LONGTEXT`）在数据大小相近时，性能差异可忽略；真正影响的是实际数据体积，以及查询是否把大字段读出来。
- **字段压缩**：可减少磁盘占用（尤其重复多、冗余高的 JSON），但写入要压缩、读取要解压，会增加 CPU。适合“写少读大字段不多、磁盘更敏感”的场景。
- 列表查询避免 `SELECT *`，大字段按需查询。

## 本示例结构

示例目录：`demo-data-type-text`

| 文件 | 说明 |
|------|------|
| `demo-data-type-text/init.sql` | 建库；创建未压缩表 `t_mediumtext`、压缩表 `t_mediumtext_compressed` |
| `demo-data-type-text/docker-compose.yml` / `Dockerfile` / `my.cnf` | 启动 MariaDB 10.11 |
| `ApplicationTests#testInsertMediumTextJson` | 插入前 `truncate`，再并发向两表各写入相同 JSON |
| `demo-data-type-text/src/test/resources/sample-json.json` | 测试用 JSON 样例 |

表结构要点：

```sql
-- 未压缩
create table t_mediumtext (
    id bigint unsigned not null auto_increment,
    content mediumtext,
    primary key (id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_general_ci;

-- 字段压缩
create table t_mediumtext_compressed (
    id bigint unsigned not null auto_increment,
    content mediumtext compressed,
    primary key (id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_general_ci;
```

## 运行步骤

1. 启动数据库：

```bash
cd demo-data-type-text
docker-compose up -d --build
```

> `init.sql` 仅在数据目录首次初始化时执行。库已存在但缺表时，可手动执行 `demo-data-type-text/init.sql`，或删掉数据卷后重建。

2. 确认连接（默认见 `demo-data-type-text/src/main/resources/application.properties`）：`127.0.0.1:3306`，库 `demo`，用户 `root` / `123456`。

3. 运行测试插入数据：

```bash
cd demo-data-type-text
mvn -Dtest=ApplicationTests#testInsertMediumTextJson test
```

插入条数、并发线程数可在 `demo-data-type-text` 的 `ApplicationTests` 中修改 `TOTAL_COUNT`、`CONCURRENT_THREADS`。

## 对比磁盘占用

```sql
select
    table_name,
    round(data_length / 1024 / 1024, 2) as data_mb,
    round(index_length / 1024 / 1024, 2) as index_mb,
    round((data_length + index_length) / 1024 / 1024, 2) as total_mb,
    table_rows
from information_schema.tables
where table_schema = 'demo'
  and table_name in ('t_mediumtext', 't_mediumtext_compressed');
```

也可直接比较两张表对应的 `.ibd` 文件大小。

参考：[Storage-Engine Independent Column Compression](https://mariadb.com/docs/server/ha-and-performance/optimization-and-tuning/optimization-and-tuning-compression/storage-engine-independent-column-compression)
