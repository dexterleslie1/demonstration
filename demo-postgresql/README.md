## 概念

PostgreSQL（常简称为 **Postgres**）是一款**开源、功能强大的关系型数据库管理系统（RDBMS）**，以**高可靠性、扩展性和标准兼容性**著称，被广泛应用于从个人项目到企业级系统的各类场景。


### 一、核心定位：关系型数据库的“进阶之选”
它遵循**SQL标准**（支持大部分ANSI SQL特性），同时突破了传统关系型数据库的局限——不仅擅长处理结构化数据（如订单、用户信息），还能通过扩展支持**半结构化/非结构化数据**（如JSON、XML、数组）、地理信息（GIS）等，甚至能模拟部分NoSQL的能力（比如文档存储）。


### 二、关键特点
1. **开源与社区驱动**  
   基于PostgreSQL License（宽松的开源协议，允许商业使用、修改和分发），无版权限制；由全球开发者社区持续维护，迭代活跃（每年发布一个大版本，如15、16版）。

2. **高可靠性与事务安全**  
   完全支持**ACID特性**（原子性、一致性、隔离性、持久性），内置**预写式日志（WAL）**机制，确保数据在崩溃后快速恢复，适合对数据一致性要求极高的场景（如金融、电商）。

3. **强大的扩展性**  
   - **数据类型丰富**：原生支持JSON/JSONB（二进制JSON，查询效率更高）、数组、UUID、几何类型（点、线、多边形）、范围类型（如日期范围）等；  
   - **自定义扩展**：可通过C语言编写扩展（如`PostGIS`用于地理空间数据、`pg_trgm`用于模糊搜索、`TimescaleDB`用于时序数据）；  
   - **函数与存储过程**：支持多种编程语言（SQL、PL/pgSQL、Python、Java、Perl等）编写业务逻辑。

4. **高性能与优化能力**  
   - 支持**并行查询**（多CPU核心协同处理复杂查询）、**分区表**（将大表拆分为小表提升查询速度）、**索引优化**（B-tree、Hash、GiST、SP-GiST、GIN等多种索引类型，适配不同场景）；  
   - 针对读写密集型场景做了优化，可应对高并发请求。

5. **跨平台与易集成**  
   支持Windows、Linux、macOS、Docker等几乎所有主流操作系统，提供丰富的客户端工具（如`pgAdmin`图形化管理工具、`psql`命令行工具），且能与Python（`psycopg2`、`SQLAlchemy`）、Java（`JDBC`）、Go等主流编程语言无缝集成。


### 三、典型应用场景
- **企业级应用**：ERP、CRM、财务系统等需要强一致性的业务；  
- **Web应用**：作为后端数据库支撑高并发网站（如GitHub早期曾用PostgreSQL）；  
- **数据分析**：结合`PostGIS`做地理数据分析（如物流轨迹、地图服务），或配合`Apache Spark`/`Metabase`做报表统计；  
- **时序数据**：通过`TimescaleDB`扩展处理物联网传感器数据、监控指标；  
- **混合负载**：同时支持OLTP（在线事务处理，如订单提交）和轻量级OLAP（在线分析处理，如销售报表）。


### 四、与其他数据库的对比
-  vs MySQL：PostgreSQL更强调**标准兼容性和扩展性**（如原生JSON支持更好），MySQL在简单Web场景下更轻量、生态更普及；  
-  vs Oracle：PostgreSQL是开源免费的“类Oracle”替代方案，功能接近但无商业授权成本；  
-  vs MongoDB：MongoDB是NoSQL文档数据库，适合非结构化数据；PostgreSQL则兼顾结构化和半结构化，且支持事务，更适合需要强一致性的场景。


### 五、总结
PostgreSQL不是“简单的数据库”，而是一套**灵活的数据管理平台**——它既能满足传统关系型数据库的需求，又能通过扩展适应现代多样化的数据场景，因此被称为“世界上最先进的开源数据库”（其官网标语）。无论是初创项目还是大型企业，都是值得信赖的选择。

## 版本有那些呢？

PostgreSQL的版本迭代遵循**年度大版本+季度小版本**的规律：每年会发布一个**主版本**（如14、15、16），每个主版本下会定期推送**小版本**（如15.1、15.2，主要修复bug和安全漏洞）。以下是关键版本的核心特性和时间线：


### 一、版本命名规则
PostgreSQL版本号采用**`主版本.次版本`**格式（如`16.1`中，`16`是主版本，`1`是小版本）。主版本升级通常包含新特性，小版本仅修复问题（需停机升级，但风险极低）。


### 二、重要主版本及核心特性
以下按时间顺序梳理**影响深远的主版本**（覆盖近10年关键迭代）：

#### 1. PostgreSQL 9.x 系列（2010-2016）：奠定现代基础
- **9.0（2010年）**：首次支持**异步流复制**（无需共享存储的主从同步），开启高可用时代；  
- **9.1（2011年）**：引入**同步流复制**（确保主从数据完全一致）、**外部数据包装器（FDW）**（跨库查询其他数据源，如MySQL、CSV）；  
- **9.2（2012年）**：支持**JSON数据类型**（初步支持文档存储）、**级联复制**（从库可再作为其他从库的主库）；  
- **9.3（2013年）**：强化JSON支持（新增`json_extract_path`等函数）、**物化视图**（缓存查询结果提升性能）；  
- **9.4（2014年）**：推出**JSONB**（二进制JSON，支持索引和高效查询，成为PostgreSQL处理半结构化数据的核心）；  
- **9.5（2015年）**：新增**UPSERT**（`INSERT ... ON CONFLICT`，避免重复插入冲突）、**行级安全（RLS）**（精细化控制用户可见数据）；  
- **9.6（2016年）**：支持**并行查询**（多CPU协同处理复杂查询，大幅提升分析性能）、**全文搜索增强**。


#### 2. PostgreSQL 10.x（2017年）：分区与逻辑复制突破
- 引入**声明式分区**（无需手动创建触发器，直接通过`PARTITION BY`定义范围/列表分区，简化大表管理）；  
- 支持**逻辑复制**（基于WAL的逻辑解码，可实现跨版本/跨集群的数据同步，区别于物理复制的块级同步）；  
- 新增**并行索引构建**（加速大索引创建）。


#### 3. PostgreSQL 11.x（2018年）：存储过程与分区增强
- 支持**SQL存储过程**（`CREATE PROCEDURE`，替代原有的函数模拟，支持事务控制）；  
- 分区表增强：**哈希分区**、**默认分区**、**分区裁剪优化**（查询时自动跳过无关分区）；  
- 并行查询扩展：支持`JOIN`、`聚合函数`（如`SUM`、`COUNT`）的并行执行。


#### 4. PostgreSQL 12.x（2019年）：性能与易用性提升
- **表达式索引优化**：支持函数/表达式的并行构建；  
- **分区表增强**：支持**外键引用分区表**、**分区表的`UPDATE`移动分区**；  
- JSONB增强：支持`JSON PATH`查询（类似XPath的JSON查询语法）；  
- 移除`contrib`模块的编译依赖（简化安装）。


#### 5. PostgreSQL 13.x（2020年）：清理与性能优化
- **B-tree索引优化**：减少索引膨胀，提升查询和更新性能；  
- **分区表增强**：支持**分区表的`TRUNCATE`单个分区**、**并行`VACUUM`**；  
- 逻辑复制增强：支持**复制列的子集**、**从库可执行`COPY`命令**；  
- 新增`gen_random_uuid()`内置函数（生成UUID更便捷）。


#### 6. PostgreSQL 14.x（2021年）：自动化与JSON增强
- **自动vacuum策略调整**：根据负载动态优化`autovacuum`频率，减少资源占用；  
- JSON增强：支持`JSON TABLE`（将JSON数据转为关系表结构）、**JSON聚合函数**（如`jsonb_agg`的性能提升）；  
- 存储过程增强：支持**事务内嵌套调用存储过程**；  
- 新增`date_bin`函数（对齐时间序列到指定间隔，如按小时聚合监控数据）。


#### 7. PostgreSQL 15.x（2022年）：删除与权限重构
- **移除过时功能**：不再支持Python 2扩展、`libpq`的旧认证方式；  
- **逻辑复制增强**：支持**从库可发起复制**、**复制过滤规则细化**；  
- 权限管理：`REVOKE`支持**级联撤销**、新增`PUBLICATION`/`SUBSCRIPTION`的细粒度权限；  
- 字符串处理：新增`regexp_count`（统计正则匹配次数）、`substring`增强。


#### 8. PostgreSQL 16.x（2023年）：并行与扩展支持
- **并行查询再升级**：支持`FULL OUTER JOIN`、`窗口函数`的并行执行；  
- 逻辑复制增强：**双向复制**（主从互相同步）、**复制进度监控API**；  
- 扩展支持：新增`pg_stat_statements`的`top-level`查询跟踪；  
- 性能优化：`VACUUM`内存使用更高效，大表`DELETE`操作更快。


#### 9. PostgreSQL 17.x（预计2024年）：即将发布的新特性
- 预览特性包括：**MERGE`语句增强**（支持更多条件分支）、**并行`ANALYZE`**（加速统计信息收集）、**JSONB`的二进制压缩**等。


### 三、小版本说明
每个主版本下的小版本（如`16.1`、`16.2`）**仅修复bug和安全漏洞**，不引入新特性。例如：
- `16.1`修复了`REINDEX`可能导致索引损坏的问题；  
- `15.5`修复了JSONB查询的潜在崩溃风险。  

建议生产环境始终使用**最新的小版本**（如主版本16优先选16.3而非16.0），以确保稳定性。


### 四、版本支持周期
PostgreSQL官方对每个主版本提供**5年的安全更新支持**：
- 前3年为**常规支持**（修复bug和新特性）；  
- 后2年为**扩展支持**（仅修复严重bug和安全漏洞）。  

例如：
- PostgreSQL 12的支持截止到**2024年11月**；  
- PostgreSQL 13的支持截止到**2025年11月**；  
- 最新主版本（如16）的支持截止到**2028年**。


### 总结
PostgreSQL的版本迭代始终围绕**性能、扩展性、易用性**三大方向，从早期的JSON支持到现在的并行查询、逻辑复制，不断适配现代数据场景。选择版本时，建议优先考虑**仍在常规支持期内的主版本**（如14、15、16），并使用最新小版本以避免已知问题。

## SpringBoot集成

具体用法参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-postgresql/demo-spring-boot

## Like查询性能

>说明：通过pg_trgm类型索引提升Like "%xxx%"查询性能。

使用本站示例协助测试：https://gitee.com/dexterleslie/demonstration/tree/main/demo-postgresql/demo-benchmark-pg16

启动PG服务：https://gitee.com/dexterleslie/demonstration/tree/main/demo-postgresql

```sh
docker compose up -d
```

准备1500万测试数据

```sh
ab -n 15000 -c 32 -k http://localhost:8080/api/v1/goods/generate
```

like "%xxx%"查询

```sh
$ wrk -t8 -c32 -d30s --latency --timeout 60 http://localhost:8080/api/v1/goods/queryByCompanyIdAndNameWildcard
Running 30s test @ http://localhost:8080/api/v1/goods/queryByCompanyIdAndNameWildcard
  8 threads and 32 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   100.68ms   28.59ms 395.76ms   86.37%
    Req/Sec    39.86      7.90    70.00     57.51%
  Latency Distribution
     50%   95.39ms
     75%  109.55ms
     90%  125.74ms
     99%  202.43ms
  9570 requests in 30.05s, 1.25MB read
Requests/sec:    318.45
Transfer/sec:     42.55KB
```

like "xxx%"查询

```sh
$ wrk -t8 -c32 -d30s --latency --timeout 60 http://localhost:8080/api/v1/goods/queryByCompanyIdAndNamePrefix
Running 30s test @ http://localhost:8080/api/v1/goods/queryByCompanyIdAndNamePrefix
  8 threads and 32 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     1.31s   196.33ms   2.46s    84.31%
    Req/Sec     4.60      4.02    20.00     76.79%
  Latency Distribution
     50%    1.30s 
     75%    1.39s 
     90%    1.52s 
     99%    1.66s 
  720 requests in 30.05s, 94.90KB read
Requests/sec:     23.96
Transfer/sec:      3.16KB
```

="xxx"查询

```sh
$ wrk -t8 -c32 -d30s --latency --timeout 60 http://localhost:8080/api/v1/goods/queryByCompanyIdAndNameTerm
Running 30s test @ http://localhost:8080/api/v1/goods/queryByCompanyIdAndNameTerm
  8 threads and 32 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     2.90ms    2.38ms  53.73ms   88.48%
    Req/Sec     1.54k   197.85     2.31k    69.75%
  Latency Distribution
     50%    2.33ms
     75%    3.68ms
     90%    5.50ms
     99%   11.69ms
  367711 requests in 30.01s, 48.03MB read
Requests/sec:  12252.13
Transfer/sec:      1.60MB
```

