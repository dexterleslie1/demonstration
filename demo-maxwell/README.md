## Maxwell

> Maxwell 官方参考：`https://github.com/zendesk/maxwell`  
> 文档站点：`https://maxwells-daemon.io/`

**Maxwell（Maxwell's Daemon）** 是 Zendesk 开源的 **MySQL CDC（Change Data Capture）** 工具。它读取 MySQL 的 **binlog**，把行级变更（INSERT / UPDATE / DELETE）转成 **JSON**，再写入 Kafka、Kinesis、RabbitMQ、Redis、stdout 等目标。

常见用途：

- ETL / 数据同步
- 数据库变更审计
- 缓存构建与失效
- 搜索索引更新
- 服务间基于数据变更的通信

工作示意：

```
MySQL 发生 UPDATE
  → Maxwell 解析 binlog
  → 输出 JSON，例如：

{
  "database": "test",
  "table": "maxwell",
  "type": "update",
  "ts": 1449786310,
  "data": { "id": 1, "daemon": "Stanislaw Lem", "mycol": 55 },
  "old":  { "mycol": 23, "daemon": "what once was" }
}
```

运行前提（MySQL）：

- 开启 binlog
- `binlog_format=ROW`
- 建议 `binlog_row_image=FULL`

与本站其他 CDC demo 的关系：同类方案还有 Canal（`demo-canal`）、Debezium（`demo-debezium`）。



## 运行示例（Maxwell + RabbitMQ + SpringBoot）

```bash
# 1. 启动 MariaDB + RabbitMQ + Maxwell
docker compose up -d

# 2. 启动 SpringBoot 消费者（自动声明队列 maxwell-queue 并绑定到 fanout 交换机 maxwell）
mvn spring-boot:run

# 3. 在 MySQL 中增删改，观察 SpringBoot 控制台 CDC 日志
docker compose exec db mariadb -uroot -p123456 demo
```

```sql
insert into t_user(username, createTime) values('user2', now());
update t_user set username='userx' where username='user2';
delete from t_user where username='userx';
```

SpringBoot 日志示例：

```
CDC database=demo, table=t_user, type=insert, data={...}, old=null
CDC database=demo, table=t_user, type=update, data={...}, old={...}
CDC database=demo, table=t_user, type=delete, data={...}, old=null
```

默认账号：

| 服务 | 用户 | 密码 | 说明 |
|------|------|------|------|
| MariaDB | root | 123456 | 端口 `3306` |
| MariaDB | maxwell | 123456 | Maxwell 专用账号 |
| RabbitMQ | root | 123456 | AMQP `5672`，管理台 `15672` |



## 复现：`ADD COLUMN IF NOT EXISTS` 导致 schema 漂移

基于 `init.sql` 里的 `t_user`。生产日志特征：

```
Unable to cast 0 (java.lang.Integer) into column ...from_type (type 'varchar')
... has a position mismatch, 126 vs 118 in new
```

**根因**：MariaDB 对「列已存在」的 `ADD COLUMN IF NOT EXISTS` 仍写入 binlog；Maxwell v1.46 会按普通 `ADD COLUMN` 在内部 schema **重复加列**，列位置错位后，读 ROW 事件时把后面的 int 当成前面的 varchar → cast 失败并退出。

```bash
# 前置：docker compose up -d（若上次已复现失败，先清 Maxwell 元数据再重启）
docker compose exec db mariadb -uroot -p123456 -e 'DROP DATABASE IF EXISTS maxwell;'
docker compose restart maxwell
./reproduce-schema-drift.sh
docker compose logs -f maxwell
```

### 用 Flyway / Liquibase 避免漂移

漂移来自**同一条幂等 DDL 被反复执行并写入 binlog**。Flyway、Liquibase 这类版本化迁移工具会在库里记已执行版本（如 `flyway_schema_history`、`DATABASECHANGELOG`），**每条变更只跑一次**，不会在每次启动时再发 `ADD COLUMN IF NOT EXISTS`。

做法：

- 列变更写成普通 `ADD COLUMN` / `MODIFY COLUMN`，放进有版本号的 migration，由 Flyway 或 Liquibase 执行
- 不要在应用启动、定时任务或手工脚本里反复跑带 `IF NOT EXISTS` / `IF EXISTS` 的 DDL
- 已漂移的 Maxwell 元数据仍需单独恢复（清 `maxwell` 库或 `--recapture_schema`）；Flyway/Liquibase 管的是**之后不再制造**这类空 ALTER

```sql
-- V20261019__add_t_user_is_jh.sql（Flyway 示例，只执行一次）
ALTER TABLE t_user ADD COLUMN `is_jh` TINYINT(1) NULL DEFAULT 0;
```



## 批量消费吞吐测试

前置：

1. `docker compose up -d`
2. **先停止**其他正在消费 `maxwell-queue` 的 SpringBoot（例如 IDEA 里跑的 `Application`），否则会抢消息导致计数不准

```bash
mvn -Dtest=ApplicationTests#testBatchInsertThroughput test
```

测试会批量插入 `10000` 条 `t_user`，等待 `Receiver` 消费完后打印 insertQps / consumeQps / avgBatchSize。
