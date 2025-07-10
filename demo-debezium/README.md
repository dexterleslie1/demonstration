# `debezium`

> `debezium` 支持三种模式：服务器、`SpringBoot` 嵌入式、`Kafka Connector` 模式。
>
> `todo`：完成服务器、`Kafka Connector` 模式 `demo`。



## 配置项



### `offset.storage.file.filename`

在 Debezium 中，`offset.storage.file.filename` 是用于**自定义偏移量存储文件路径和名称**的配置参数，主要在通过文件系统（而非 Kafka 主题）存储连接器偏移量时使用。以下是关于该配置的详细说明：

#### **1. 核心作用**

Debezium 通过记录数据库变更的**偏移量（Offset）**来跟踪已处理的数据位置，确保连接器重启后能从断点继续消费，避免数据重复或丢失。`offset.storage.file.filename` 用于指定**存储这些偏移量的文件路径和名称**，仅在偏移量存储方式为文件系统时生效（对应 `offset.storage=filesystem`）。


#### **2. 使用前提**
要使用 `offset.storage.file.filename`，需先配置偏移量存储方式为文件系统：
```properties
offset.storage=filesystem  # 必须设置为文件系统存储
offset.storage.file.filename=/path/to/your/offset-file.offsets  # 自定义文件路径
```


#### **3. 配置细节**
##### （1）路径类型
- **绝对路径**：推荐使用绝对路径（如 `/var/lib/debezium/offsets/mysql-connector.offsets`），避免因工作目录变化导致文件找不到。
- **相对路径**：若使用相对路径（如 `./offsets/connector.offsets`），则相对于 Debezium 进程的工作目录（通常是启动命令的执行目录）。

##### （2）多实例隔离
若同一台机器运行多个 Debezium 连接器实例（如不同数据库或不同任务的连接器），需为每个实例设置**唯一的文件名**，避免偏移量文件被覆盖。例如：
```properties
# 实例 1（MySQL）
offset.storage.file.filename=/data/debezium/mysql-connector.offsets

# 实例 2（PostgreSQL）
offset.storage.file.filename=/data/debezium/postgres-connector.offsets
```

##### （3）文件权限
确保运行 Debezium 的用户对目标路径有**读写权限**。否则，启动时会抛出 `Permission denied` 错误。例如：
```bash
# 若 Debezium 以用户 "debezium" 运行，需授权：
chown debezium:debezium /data/debezium/
chmod 755 /data/debezium/
```


#### **4. 偏移量文件内容**
偏移量文件是 Debezium 内部维护的二进制或结构化文件（具体格式由 Debezium 版本决定），**无需手动编辑**。其内容包含：
- 各分区的最后提交偏移量（如数据库日志的 LSN、事务 ID 等）。
- 连接器的状态信息（如检查点、错误恢复标记）。


#### **5. 注意事项**
##### （1）与 Kafka 主题存储的对比
- **默认方式（Kafka 主题）**：更推荐使用默认的 `offset.storage=kafka`，偏移量存储在 Kafka 的内部主题（如 `debezium-offset-storage`），支持分布式、高可用，适合生产环境。
- **文件系统存储**：仅适用于单实例、测试或轻量级场景，无法支持多实例共享偏移量，且文件丢失会导致偏移量重置（可能重复消费数据）。

##### （2）故障恢复风险
若偏移量文件损坏或丢失，Debezium 会**从初始位置重新消费数据**（具体行为取决于连接器的 `snapshot.mode` 配置）。因此：
- 建议将偏移量文件存储在可靠的存储介质（如 RAID、云存储）。
- 定期备份偏移量文件（尤其是关键业务场景）。

##### （3）版本兼容性
不同 Debezium 版本的偏移量文件格式可能不兼容。升级 Debezium 时，需参考官方文档确认是否需要迁移或保留旧偏移量文件。


#### **6. 配置示例**
以下是一个完整的 MySQL 连接器配置示例，使用文件系统存储偏移量：
```properties
name=mysql-connector  # 连接器名称（用于生成文件名）
connector.class=io.debezium.connector.mysql.MySqlConnector
database.hostname=localhost
database.port=3306
database.user=debezium
database.password=dbz
database.server.id=184054
database.server.name=mysql-server
database.include.list=inventory

# 偏移量存储配置
offset.storage=filesystem
offset.storage.file.filename=/var/lib/debezium/offsets/mysql-connector.offsets

# 其他可选配置
snapshot.mode=when_needed
table.include.list=inventory.orders,inventory.products
```


#### **总结**
`offset.storage.file.filename` 是 Debezium 文件系统偏移量存储的核心配置，用于指定偏移量文件的路径。它适用于单实例、测试场景，但生产环境更推荐使用 Kafka 主题存储以保证高可用和可靠性。使用时需注意路径权限、多实例隔离及故障恢复风险。



### `schema.history.internal.file.filename`

在 Debezium 中，`schema.history.internal.file.filename` 是用于**自定义模式历史存储文件路径和名称**的配置参数，主要用于控制 Debezium 记录数据库表结构变更历史的存储位置。以下是关于该配置的详细说明：


#### **1. 核心作用**
Debezium 在捕获数据库变更（如 DDL 操作修改表结构）时，需要记录**模式历史（Schema History）**，以便下游消费者（如 Kafka Connect 的 Sink 连接器）能够根据历史模式正确解析变更数据（如 Avro、JSON 格式的消息）。  
`schema.history.internal.file.filename` 用于指定当模式历史存储方式为文件系统时，存储模式的文件的**路径和名称**（仅在 `schema.history.internal=filesystem` 时生效）。


#### **2. 使用前提**
要使用 `schema.history.internal.file.filename`，需先配置模式历史的存储方式为文件系统：  
```properties
schema.history.internal=filesystem  # 必须设置为文件系统存储
schema.history.internal.file.filename=/path/to/schema-history.file  # 自定义文件路径
```


#### **3. 配置细节**
##### （1）路径类型
- **绝对路径**：推荐使用绝对路径（如 `/var/lib/debezium/schema-history/mysql-connector.schema`），避免因 Debezium 进程工作目录变化导致文件找不到。  
- **相对路径**：若使用相对路径（如 `./schema-history/connector.schema`），则相对于 Debezium 进程的工作目录（通常是启动命令的执行目录）。

##### （2）多实例隔离
若同一台机器运行多个 Debezium 连接器实例（如不同数据库或不同任务的连接器），需为每个实例设置**唯一的文件名**，避免模式历史文件被覆盖。例如：  
```properties
# 实例 1（MySQL 连接器）
schema.history.internal=filesystem
schema.history.internal.file.filename=/data/debezium/mysql-schema-history.schema

# 实例 2（PostgreSQL 连接器）
schema.history.internal=filesystem
schema.history.internal.file.filename=/data/debezium/postgres-schema-history.schema
```

##### （3）文件权限
确保运行 Debezium 的用户对目标路径有**读写权限**。否则，启动时会抛出 `Permission denied` 错误。例如：  
```bash
# 若 Debezium 以用户 "debezium" 运行，需授权：
chown debezium:debezium /data/debezium/
chmod 755 /data/debezium/
```


#### **4. 模式历史文件内容**
模式历史文件是 Debezium 内部维护的结构化文件（通常为二进制或 JSON 格式，具体取决于版本），**无需手动编辑**。其内容包含：  
- 表的唯一标识（如 `server_name.database_name.table_name`）。  
- 模式的版本号（随每次 DDL 变更递增）。  
- 每个版本对应的完整模式信息（如列名、类型、约束等）。  
- 时间戳（记录模式变更的时间）。  


#### **5. 注意事项**
##### （1）与默认存储（Kafka 主题）的对比
- **默认方式（Kafka 主题）**：更推荐使用默认的 `schema.history.internal=kafka`，模式历史存储在 Kafka 的内部主题（如 `debezium-schema-history`），支持分布式、高可用，适合生产环境。  
- **文件系统存储**：仅适用于单实例、测试或轻量级场景，无法支持多实例共享模式历史，且文件丢失会导致模式历史重置（可能引发下游解析错误）。

##### （2）故障恢复风险
若模式历史文件损坏或丢失，Debezium 会**从初始状态重新记录模式变更**（即仅保留当前最新的模式），可能导致下游消费者因缺少历史模式而无法正确解析早期变更数据。因此：  
- 建议将模式历史文件存储在可靠的存储介质（如 RAID、云存储）。  
- 定期备份模式历史文件（尤其是关键业务场景）。

##### （3）版本兼容性
不同 Debezium 版本的**模式历史文件格式可能不兼容**。升级 Debezium 时，需参考官方文档确认是否需要迁移或保留旧模式历史文件（部分版本可能要求清空旧文件以避免解析错误）。

##### （4）与偏移量存储的区别
`schema.history.internal.file.filename` 控制的是**模式历史**的存储路径，而 `offset.storage.file.filename` 控制的是**偏移量**（已处理数据的进度）的存储路径，两者用途完全不同，需分别配置。


#### **6. 配置示例**
以下是一个完整的 MySQL 连接器配置示例，使用文件系统存储模式历史：  
```properties
name=mysql-orders-connector  # 连接器名称（用于生成唯一文件名）
connector.class=io.debezium.connector.mysql.MySqlConnector
database.hostname=localhost
database.port=3306
database.user=debezium
database.password=dbz
database.server.id=184054
database.server.name=mysql-orders
database.include.list=orders_db

# 模式历史存储配置（文件系统）
schema.history.internal=filesystem
schema.history.internal.file.filename=/var/lib/debezium/schema-history/mysql-orders-connector.schema

# 偏移量存储配置（可选，此处示例仍用默认 Kafka 存储）
offset.storage=kafka
offset.storage.topic=debezium-offsets
offset.storage.partitions=24

# 其他配置
snapshot.mode=when_needed
table.include.list=orders_db.orders,orders_db.customers
```


#### **总结**
`schema.history.internal.file.filename` 是 Debezium 文件系统模式历史存储的核心配置，用于指定模式历史文件的路径。它适用于单实例、测试场景，但生产环境强烈建议使用默认的 Kafka 主题存储，以保证模式历史的高可用性和可靠性。使用时需注意路径权限、多实例隔离及故障恢复风险。



## `SpringBoot` 嵌入式模式

>  `https://www.baeldung.com/debezium-intro`
>
>  `https://debezium.io/documentation/reference/stable/development/engine.html`
>
>  详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-debezium/demo-debezium-embedded)

注意：第一次启动 `SpringBoot` 应用没有 `offset` 信息时会全量读取一次数据库并回调 `SpringBoot CDC` 接口。官方这种模式已经废弃，但是 `future` 项目还是暂时采用这种模式。

启动相关容器

```bash
docker compose up -d
```

启动 `SpringBoot` 服务

在数据库中执行以下 `SQL` 后，观察 `Debezium` 在 `SpringBoot` 应用输出的日志

```sql
insert into t_user(username,`password`,createTime) values('user2','123456',now());

update t_user set username='userx' where username='user2';

delete from t_user where username='userx';
```

应用日志输出如下：

```
table=t_user,payload={password=123456, createTime=1752076266000, id=3, username=user2},operation=CREATE
table=t_user,payload={password=123456, createTime=1752076266000, id=3, username=userx},operation=UPDATE
table=t_user,payload={password=123456, createTime=1752076266000, id=3, username=userx},operation=DELETE
```

