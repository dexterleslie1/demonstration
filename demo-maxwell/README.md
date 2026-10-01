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



## 批量消费吞吐测试

前置：

1. `docker compose up -d`
2. **先停止**其他正在消费 `maxwell-queue` 的 SpringBoot（例如 IDEA 里跑的 `Application`），否则会抢消息导致计数不准

```bash
mvn -Dtest=ApplicationTests#testBatchInsertThroughput test
```

测试会批量插入 `10000` 条 `t_user`，等待 `Receiver` 消费完后打印 insertQps / consumeQps / avgBatchSize。
