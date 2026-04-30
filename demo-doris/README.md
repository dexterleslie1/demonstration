## Doris是什么呢？

Apache Doris 是一款基于 MPP（大规模并行处理）架构的高性能、实时分析型数据库。它最初由百度研发（项目名为 Palo），于 2017 年开源，并在 2022 年成功从 Apache 孵化器毕业，成为 Apache 软件基金会的顶级项目。

简单来说，Apache Doris 专为处理海量数据而设计，能够在亚秒级时间内返回查询结果，既能支持高并发的点查询，也能胜任高吞吐的复杂分析。

### 核心特性

*   **性能优异**
    *   **MPP 架构与向量化引擎**：采用 MPP 架构实现节点间和节点内的并行计算，并结合向量化执行引擎，能充分利用现代 CPU 的并行处理能力，大幅提升查询性能。
    *   **高效存储与索引**：使用列式存储，数据压缩比高，能有效减少 I/O。同时支持多种索引（如 BloomFilter、倒排索引等）和智能物化视图，可加速数据过滤和预聚合。

*   **简单易用**
    *   **兼容 MySQL 协议**：高度兼容 MySQL 协议和标准 SQL 语法。这意味着你可以直接使用 MySQL 客户端或各种主流的 BI 工具（如 Tableau、Power BI、FineBI 等）无缝连接 Doris 进行数据分析和可视化，学习成本极低。

*   **架构精简**
    *   **仅两类进程**：整个系统架构非常简单，只包含两种进程：
        *   **Frontend (FE)**：负责接收用户请求、解析 SQL、管理元数据和集群节点。
        *   **Backend (BE)**：负责实际的数据存储和查询计划的执行。
    *   **易于运维**：这种高度集成的架构使得部署和运维都非常简便，无需依赖任何第三方组件。FE 和 BE 都可以方便地进行横向扩展，单集群可支持数百台机器和数十 PB 的存储容量。

*   **稳定可靠**
    *   **高可用性**：支持数据多副本存储，通过一致性协议保证服务的高可用和数据的高可靠。集群具备自愈能力，当节点发生故障时能自动感知并修复，扩容、缩容或升级过程中服务不中断。

### 适用场景

Apache Doris 广泛应用于需要快速数据分析的领域，尤其适合以下场景：

*   **报表分析**：快速生成企业内部的各类报表和可视化看板，支持高并发的报表查询。
*   **即席查询**：支持分析师进行灵活的、非固定模式的自助式数据探索和分析。
*   **统一数仓构建**：凭借其强大的性能和简洁的架构，可以替代传统复杂的“Lambda 架构”，构建轻量化的统一数据仓库。
*   **数据湖联邦查询**：可以直接查询 Hive、Iceberg、Hudi 等数据湖中的数据，无需数据搬迁，实现湖仓一体的分析体验。
*   **用户画像与行为分析**：高效处理用户行为日志，进行用户留存、转化漏斗、人群圈选等分析。

## SelectDB是什么呢？

SelectDB 是北京飞轮数据科技有限公司旗下的一个品牌，专注于实时数据仓库领域，为 Apache Doris 开源项目提供商业支持。简单来说，它基于 Apache Doris 内核进行了深度优化和增强，为企业提供更稳定、功能更丰富、服务更有保障的实时数据分析解决方案。

其核心产品形态主要有两种，以满足不同企业的部署需求：

### SelectDB Cloud (云服务版)
这是一个全托管的 SaaS 化产品，采用云原生存算分离架构。
*   **极简运维**：用户无需管理底层服务器，可以一键部署、即开即用，大大降低了运维成本。
*   **弹性伸缩**：计算和存储资源可以根据业务负载自动弹性扩缩容，轻松应对业务高峰。
*   **高性价比**：采用存储与计算分离的设计，数据存储在成本更低的对象存储中，同时支持 Serverless 模式，按实际使用量付费，有效节省资源成本。
*   **多云支持**：可以运行在阿里云、腾讯云、AWS 等多个云平台上，提供一致的使用体验。

### SelectDB Enterprise (企业版)
这是一个供企业私有化部署的版本，可以部署在客户的本地数据中心（IDC）、私有云或专有云中。
*   **安全与合规**：提供企业级的安全能力，如透明数据加密（TDE）、全链路 TLS 加密、系统漏洞治理等，满足金融、政企等行业对数据安全和审计合规的严格要求。
*   **生态兼容**：除了兼容 Apache Doris 生态，还增强了对达梦、GBase 等国产数据库的支持，方便企业进行国产化替代和数据整合。
*   **专业支持**：由 SelectDB 官方提供专业的技术支持和保障服务，确保生产环境的稳定运行。

### 核心特性与优势
无论是云服务版还是企业版，SelectDB 都具备以下核心优势：
*   **极致性能**：在实时报表、即席查询和高并发点查等场景下性能卓越，曾在全球分析型数据库性能基准测试 ClickBench 中位列第一。
*   **统一融合**：一个平台即可同时支持实时分析、交互式查询、批量处理等多种负载，并能对数据湖（如 Hive、Iceberg）和多种数据库进行联邦查询，避免了多套系统并存的复杂性。
*   **AI 与搜索增强**：新版本集成了强大的搜索和 AI 能力。例如，内置的 `search()` 函数可以在 SQL 中实现类似 Elasticsearch 的全文检索；同时支持向量搜索和通过 SQL 直接调用大模型，简化了 AI 应用的开发流程。

总而言之，SelectDB 是 Apache Doris 的官方商业化产品，通过提供云托管和私有化部署两种形态，并增强企业级特性，帮助企业更便捷、更可靠地构建高性能的实时数据仓库和 AI 数据底座。

## SelectDB开源吗？

SelectDB 本身不是开源软件，但它与开源项目 Apache Doris 有着非常紧密的关系。

简单来说，**SelectDB 是 Apache Doris 的商业化产品**，由 Apache Doris 的创始团队打造，旨在为企业提供更稳定、功能更丰富、服务更有保障的解决方案。

### 与开源 Apache Doris 的关系

*   **核心同源**：SelectDB 的内核完全基于开源的 Apache Doris 构建，并与其保持 100% 兼容。这意味着您在 SelectDB 上使用的 SQL 语法、功能和生态工具与 Apache Doris 基本一致，可以无缝迁移。
*   **深度贡献**：SelectDB 背后的公司是 Apache Doris 社区最大的贡献者，会持续将技术创新成果回馈给开源社区，共同推动项目发展。

### 产品形态与开源

虽然 SelectDB 不是开源软件，但它提供了灵活的部署方式，其中包含了免费的选项：

*   **云服务版 (SelectDB Cloud)**：这是一个全托管的 SaaS 服务，用户按需付费，无需自行运维。
*   **企业版 (SelectDB Enterprise)**：这是一个供企业私有化部署的商业软件，提供更强的稳定性、安全性和官方技术支持。
*   **免费工具**：SelectDB 也提供完全免费的工具，例如此前向您介绍过的 **SelectDB Studio** 桌面版，所有人都可以免费下载和使用。

总而言之，如果您希望使用完全开源的版本，可以直接使用 **Apache Doris**。如果您需要企业级的稳定性、安全保障和专业服务，可以选择 **SelectDB** 的商业版本。

## X2Doris是什么呢？

**X2Doris** 是由 SelectDB 团队（Apache Doris 的主要贡献者）开发的一款**免费、可视化的一站式海量数据迁移工具**。

它的核心使命是解决将数据从其他系统（如 Hive、ClickHouse 等）迁移到 Apache Doris 或 SelectDB 时的痛点，比如建表繁琐、迁移速度慢、操作复杂等。你可以把它看作是数据仓库界的“搬家神器”，旨在让数据同步变得像“复制粘贴”一样简单。

### 核心亮点

*   **永久免费**：面向所有社区用户免费下载使用，无需付费授权。
*   **可视化操作**：全程 Web 界面化操作，无需编写复杂的 ETL 脚本，通过鼠标点击即可完成配置。
*   **自动建表**：这是它最“杀手级”的功能。它能自动读取源端（如 Hive）的元数据，在 Doris 中自动生成对应的建表语句（DDL），自动处理字段类型映射和分区，省去了手动建表的巨大工作量。
*   **极速稳定**：底层基于 Spark 引擎，针对 Doris 的 Stream Load 写入进行了深度优化。据测试，其性能比 DataX 等同类工具快 2-10 倍，单机即可达到 800MB/s 的写入速度。

### 支持的数据源

X2Doris 支持多种主流数据源作为**源端**，将数据迁移至 **Apache Doris** 或 **SelectDB**（包括 Cloud 版和企业版）：

| 类型         | 支持的数据源                                                 |
| :----------- | :----------------------------------------------------------- |
| **数据源端** | **Apache Hive** (1.x/2.x)**Apache Doris** (支持 Doris 2 Doris)**StarRocks****Apache Kudu****ClickHouse***(注：Greenplum、Druid 等正在开发支持中)* |
| **目标端**   | **Apache Doris****SelectDB** (Cloud / Enterprise)            |

### 主要功能特性

1.  **智能建表与映射**
    *   **自动识别**：自动识别源表的字段名、类型、分区信息。
    *   **类型转换**：自动处理复杂类型（如 Array、Map、Bitmap）的映射。
    *   **模型适配**：默认创建 Duplicate Key 模型表，并支持手动修改 DDL。

2.  **高性能迁移**
    *   利用 Spark 的分布式计算能力，适合处理 TB/PB 级的大规模数据。
    *   优化了内存使用和攒批逻辑，确保在大规模数据写入时的稳定性。

3.  **灵活部署**
    *   **独立部署**：如果没有大数据环境，可以直接下载包含 Spark 的安装包进行单机部署。
    *   **集群部署**：推荐部署在有 Hadoop/Yarn 的环境中，以充分利用集群资源加速迁移。

### 适用场景

*   **离线数仓实时化**：将基于 Hadoop/Hive 的离线数据快速迁移到 Doris，实现实时分析。
*   **异构数据库迁移**：从 ClickHouse、Kudu 或 StarRocks 迁移到 Doris。
*   **Doris 集群升级/备份**：在不同 Doris 集群之间进行数据同步或备份恢复。

**总结来说**，如果你需要将大量数据从 Hive 或其他 OLAP 系统迁移到 Doris，X2Doris 是目前官方推荐的首选工具，它能帮你省去写代码的时间，极大提升迁移效率。

## DataX是什么呢？

DataX 是阿里巴巴集团开源的一款**高性能、异构数据源离线同步工具**，也是阿里云 DataWorks 数据集成模块的底层核心引擎。

简单来说，它的核心使命是在不同的数据源之间高效、稳定地“搬运”数据。它致力于解决大数据领域中，多种异构系统（如 MySQL、Oracle、HDFS、Hive 等）之间数据流转的难题。

### 核心设计理念

DataX 的设计非常巧妙，它将复杂的网状数据同步链路变成了星型结构，DataX 作为中间枢纽连接所有数据源。

*   **插件化架构 (Framework + Plugin)**：这是 DataX 最核心的设计。它将数据读取（Reader）和数据写入（Writer）的逻辑抽象为独立的插件。
    *   **Reader**：负责从源数据源（如 MySQL）读取数据。
    *   **Writer**：负责将数据写入目标数据源（如 HDFS）。
    *   **Framework**：作为核心框架，负责任务调度、并发控制、流量控制、数据一致性保障等公共逻辑。
*   **中间数据标准化**：Reader 读取的数据会被转换成 DataX 内部统一的 `Record` 格式，然后通过一个内存通道（Channel）传递给 Writer。这种设计实现了 Reader 和 Writer 的完全解耦，使得任意 Reader 和 Writer 插件可以自由组合，实现“多对多”的数据同步能力。
*   **单机多线程**：DataX 采用单机运行模式，通过多线程并发执行来提升数据同步的吞吐量，避免了传统 MapReduce 任务中大量的磁盘 I/O 开销，因此性能非常出色。

### 主要特点

| 特点                 | 说明                                                         |
| :------------------- | :----------------------------------------------------------- |
| **广泛的数据源支持** | 支持关系型数据库（MySQL, Oracle, PostgreSQL）、NoSQL（HBase, MongoDB）、文件存储（HDFS, FTP）、数据仓库（Hive, MaxCompute）等超过30种数据源。 |
| **高性能**           | 基于内存管道传输，相比 Sqoop 等基于磁盘 I/O 的工具，同步速度通常有数倍的提升。 |
| **高可靠性**         | 提供完善的容错机制，如失败重试、脏数据过滤、流量控制等，确保数据同步的稳定性和一致性。 |
| **配置化**           | 用户只需编写一个 JSON 格式的配置文件，定义好 Reader、Writer 和相关参数，即可启动同步任务，无需编写代码。 |

### 一个典型的使用场景

假设你需要每天将 MySQL 数据库中的用户订单数据同步到 HDFS 上的 Hive 表中进行分析。

1.  **编写配置**：创建一个 JSON 文件，配置 `mysqlreader` 作为读取端，`hdfswriter` 作为写入端，并指定好数据库连接信息、表名、字段等。
2.  **启动任务**：通过命令行 `python datax.py your_job_config.json` 启动任务。
3.  **执行同步**：DataX 框架会解析配置，将任务拆分成多个并发的子任务（Task），通过多线程从 MySQL 读取数据，写入到 HDFS。
4.  **查看结果**：任务完成后，你可以在日志中看到详细的同步统计信息，如总记录数、传输速度、耗时等。

总而言之，DataX 是一款功能强大、性能卓越且社区活跃的开源数据集成工具，特别适合用于构建数据仓库的 ETL（抽取、转换、加载）流程、进行异构数据库迁移或历史数据批量同步。

## DataX、Airbyte、Canal、Debezium、Fivetran与Apache SeaTunnel

https://www.cnblogs.com/seatunnel/p/19599905

## SeaTunnel是什么呢？

>说明：目前使用这个工具进行全量数据迁移，因为有可视化界面。

Apache SeaTunnel 是一个高性能、分布式的**数据集成平台**，旨在解决海量数据的实时同步与转换问题。它的前身是 Waterdrop，于2017年开源，并在2021年更名为 SeaTunnel。

简单来说，你可以把它想象成一个功能强大的“数据搬运工”和“数据加工厂”，能够高效地在各种数据源之间进行数据的抽取、转换和加载（ETL）。

### 它能解决什么问题？

SeaTunnel 专注于应对数据集成领域的常见挑战：

*   **数据源多样**：支持超过100种连接器，轻松对接各种关系型数据库（如 MySQL、Oracle）、NoSQL 数据库（如 MongoDB、Redis）、数据仓库（如 Hive）、消息队列（如 Kafka）以及各类云存储（如 S3）。
*   **同步场景复杂**：无论是离线的批量同步，还是基于 CDC（变更数据捕获）的实时同步，SeaTunnel 都能提供支持。
*   **资源消耗高**：通过优化，它能有效解决海量小表同步时占用过多 JDBC 连接等资源的难题。
*   **数据质量与监控**：提供完善的实时监控，让你清晰了解同步任务的 QPS、数据量等关键指标，并保证数据的一致性。

### 核心特性

*   **批流一体**：同一套连接器（Connector）可以同时支持离线批处理和实时流处理场景，大大降低了开发和维护的复杂度。
*   **多引擎支持**：SeaTunnel 的一个显著特点是其灵活的引擎支持。
    1.  **SeaTunnel Engine (Zeta)**：这是其默认且推荐的自研引擎，专为数据同步场景优化，无需依赖外部组件，部署和使用都非常简单。
    2.  **Apache Flink**：可以复用现有的 Flink 集群和生态。
    3.  **Apache Spark**：可以复用现有的 Spark 集群和生态。
*   **插件化架构**：采用插件化设计，用户可以非常方便地开发自己的连接器并集成到 SeaTunnel 中，扩展性极强。
*   **数据一致性**：支持分布式快照算法，确保数据在同步过程中的精确一次（Exactly-Once）语义，避免数据丢失或重复。
*   **易于使用**：提供多种作业开发方式，包括简单的配置文件和可视化的 Web 界面（SeaTunnel Web）。

### 工作原理

SeaTunnel 的核心工作流程遵循经典的 **Source -> Transform -> Sink** 三段式模型：

1.  **Source (源)**：负责从各种数据源并行读取数据。
2.  **Transform (转换)**：对读取的数据进行清洗、过滤、聚合、SQL 处理等操作。
3.  **Sink (目标)**：将处理后的数据写入到目标系统中。

### 谁在使用？

SeaTunnel 拥有庞大的用户群体，包括 Shopee、游族网络、唯品会、Bilibili、OPPO 等众多知名企业，在生产环境中稳定地处理着每天数百亿级别的数据量。它已经成功从 Apache 孵化器毕业，成为 Apache 软件基金会的顶级项目，标志着其成熟度和社区活跃度得到了国际开源界的广泛认可。

## 把MariaDB中的数据实时同步到Doris中呢？

将 MariaDB 中的数据实时同步到 Doris，最主流和推荐的方式是利用 **Flink CDC** 技术。MariaDB 与 MySQL 高度兼容，因此所有适用于 MySQL 的 CDC 方案同样适用于 MariaDB。

以下是两种主要的实现方案，推荐优先选择方案一。

### 方案一：使用 Flink CDC（推荐）

这是目前最强大和灵活的方案，能够实现全量 + 增量的一体化同步，并支持 Schema 自动变更。

#### 核心原理
Flink CDC 通过读取 MariaDB 的二进制日志（Binlog）来捕获数据变更（INSERT, UPDATE, DELETE），然后利用 Doris 的主键模型（Unique Key）的 UPSERT 能力，将这些变更实时应用到 Doris 表中。

#### 实施步骤
1.  **配置 MariaDB 源端**
    *   **开启 Binlog**：修改 MariaDB 配置文件（如 `my.cnf`），确保开启 Binlog 并设置为 ROW 模式。
        
        ```ini
        [mysqld]
        log-bin = mysql-bin
        binlog_format = ROW
        server-id = 1  # 设置一个唯一的 server-id
        ```
    *   **创建授权用户**：为 Flink CDC 创建一个有权限读取 Binlog 的数据库用户。
        ```sql
        CREATE USER 'flink_cdc'@'%' IDENTIFIED BY 'your_password';
        GRANT SELECT, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'flink_cdc'@'%';
        FLUSH PRIVILEGES;
        ```
    
2.  **创建 Flink CDC 同步任务**
    你可以选择以下两种方式之一来创建任务：

    *   **方式 A：使用 Doris 原生的 `CREATE JOB` 语句 (最简单)**
        这是 Doris 2.0 之后集成的功能，内部封装了 Flink CDC，可以直接在 Doris 中通过 SQL 创建同步任务，无需编写 Flink 代码。
        
        ```sql
        CREATE JOB my_mariadb_sync_job
        ON STREAMING
        FROM MYSQL (
            "jdbc_url" = "jdbc:mysql://<mariadb_host>:3306",
            "driver_url" = "mysql-connector-j-8.0.31.jar",
            "driver_class" = "com.mysql.cj.jdbc.Driver",
            "user" = "flink_cdc",
            "password" = "your_password",
            "database" = "your_database",
            "include_tables" = "table1,table2", -- 指定要同步的表
            "offset" = "initial" -- 从初始位置开始全量同步
        )
        TO DATABASE target_doris_db (
            "table.create.properties.replication_num" = "1"
        );
        ```
        
    *   **方式 B：使用 Flink SQL**
        这种方式更加灵活，适合需要复杂数据转换的场景。
        ```sql
        -- 1. 定义 MariaDB 源表
        CREATE TABLE mariadb_source (
            id INT,
            name STRING,
            age INT,
            PRIMARY KEY (id) NOT ENFORCED
        ) WITH (
            'connector' = 'mysql-cdc',
            'hostname' = '<mariadb_host>',
            'port' = '3306',
            'username' = 'flink_cdc',
            'password' = 'your_password',
            'database-name' = 'your_database',
            'table-name' = 'your_table',
            'server-id' = '5400-5404'
        );
        
        -- 2. 定义 Doris 目标表
        CREATE TABLE doris_sink (
            id INT,
            name STRING,
            age INT,
            PRIMARY KEY (id) NOT ENFORCED
        ) WITH (
            'connector' = 'doris',
            'fenodes' = '<doris_fe_host>:8030',
            'table.identifier' = 'target_doris_db.your_table',
            'username' = 'root',
            'password' = '',
            'sink.enable-schema-change' = 'true', -- 开启 Schema 变更支持
            'sink.properties.format' = 'json'
        );
        
        -- 3. 启动数据同步
        INSERT INTO doris_sink SELECT * FROM mariadb_source;
        ```

#### 方案优点
*   **全增量一体**：自动处理历史数据（全量）和实时增量数据。
*   **Schema 演进**：支持自动同步源端的 DDL 变更（如加列），无需中断任务。
*   **无锁读取**：Flink CDC 采用增量快照算法，全量读取阶段不会锁表，不影响线上业务。
*   **断点续传**：任务失败后可从上次检查点（Checkpoint）恢复，保证数据不丢失。

---

### 方案二：使用 Kafka + Routine Load

这是一个解耦的方案，通过消息队列作为缓冲，适用于数据需要被多个下游系统消费的场景。

#### 核心原理
1.  使用 **Canal** 或 **Flink CDC** 等工具作为数据捕获程序，监听 MariaDB 的 Binlog 并将变更数据实时写入 **Kafka** 主题中。
2.  在 Doris 中创建 **Routine Load** 任务，持续从 Kafka 主题中消费数据并导入到 Doris 表中。

#### 实施步骤
1.  **配置 MariaDB 源端**：与方案一相同，需要开启 Binlog。
2.  **部署数据捕获程序**：部署 Canal Server，配置其监听 MariaDB，并将解析出的数据发送到 Kafka。
3.  **创建 Kafka Topic**：使用 Kafka 命令行工具创建用于接收数据的 Topic。
4.  **创建 Doris Routine Load 任务**：
    ```sql
    CREATE ROUTINE LOAD my_routine_load
    ON TABLE target_doris_table
    PROPERTIES (
        "desired_concurrent_number" = "3",
        "max_batch_interval" = "10"
    )
    FROM KAFKA (
        "kafka.broker.list" = "<kafka_host>:9092",
        "kafka.topic" = "my_mariadb_topic",
        "kafka.group.id" = "my_doris_group",
        "format" = "json"
    );
    ```

#### 方案优点
*   **高解耦**：数据生产者和消费者通过 Kafka 解耦，一个数据源可以服务多个消费者。
*   **削峰填谷**：Kafka 可以作为缓冲区，应对数据流量的突发高峰。

#### 方案缺点
*   **架构复杂**：相比方案一，需要额外维护 Canal 和 Kafka 组件，运维成本更高。
*   **配置繁琐**：需要分别配置 Canal、Kafka 和 Doris Routine Load。

### 总结与建议

| 方案                     | 优点                                             | 缺点                                                       | 适用场景                       |
| :----------------------- | :----------------------------------------------- | :--------------------------------------------------------- | :----------------------------- |
| **Flink CDC**            | 架构简单、功能强大、支持 Schema 变更、全增量一体 | 需要部署 Flink 环境（若使用 `CREATE JOB` 则由 Doris 托管） | **绝大多数实时同步场景的首选** |
| **Kafka + Routine Load** | 系统解耦、可扩展性强                             | 架构复杂、运维成本高                                       | 数据需要被多个下游系统消费     |

**建议：**
对于绝大多数从 MariaDB 到 Doris 的实时同步需求，**方案一（Flink CDC）是最佳选择**。特别是使用 Doris 的 `CREATE JOB` 语句，可以极大地简化配置和运维工作。

## Docker Compose运行

详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-doris/docker-compose.yaml

在宿主机设置vm.max_map_count=2000000

```sh
# 临时生效
sudo sysctl -w vm.max_map_count=2000000

# 持久化（重启后仍生效）
echo 'vm.max_map_count=2000000' | sudo tee -a /etc/sysctl.conf
sudo sysctl -p
```

关闭 swap

```sh
# 关闭 swap（临时）
sudo swapoff -a

# 关闭 swap（永久，重启后也不回来）
# 编辑 /etc/fstab，把所有 swap 行注释掉（行首加 #）
```

启动Doris

```sh
docker compose up -d
```

修改Doris密码

```sh
# 进入Doris FE
docker compose exec -it doris-fe bash

# 先用 MySQL 协议连上 Doris（默认 root 无密码）：
mysql -h 127.0.0.1 -P 9030 -u root

# 修改密码
ALTER USER 'root'@'%' IDENTIFIED BY '123456';
```

## 手动迁移MariaDB数据到Doris

>说明：通过csv导出和导入数据。

启动本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-doris/docker-compose.yaml

手动迁移：

```sh
# 在Doris创建数据库
CREATE DATABASE `cloud_cloth_db`;

# 在Doris创建表
CREATE TABLE `inventory` (
  `id` BIGINT NOT NULL,
  `company_id` BIGINT NULL DEFAULT "0" COMMENT "公司id",
  `ck_id` BIGINT NOT NULL COMMENT "仓库id",
  `md_id` BIGINT NULL DEFAULT "0" COMMENT "门店id",
  `cp_id` BIGINT NOT NULL COMMENT "产品id",
  `cp_ys_id` BIGINT NULL DEFAULT "0" COMMENT "产品颜色id",
  `fk` VARCHAR(80) NULL COMMENT "幅宽",
  `fk_dw_id` BIGINT NULL DEFAULT "0" COMMENT "幅宽单位id",
  `kz` VARCHAR(80) NULL COMMENT "克重",
  `kz_dw_id` BIGINT NULL DEFAULT "0" COMMENT "克重单位id",
  `dw_id` BIGINT NULL DEFAULT "0" COMMENT "单位id",
  `gh_id` BIGINT NULL DEFAULT "0" COMMENT "缸号id",
  `gg` VARCHAR(80) NULL DEFAULT "" COMMENT "规格",
  `kc_sl` DECIMAL(20,2) NULL DEFAULT "0.00" COMMENT "库存数量",
  `kc_ps` INT NULL DEFAULT "0" COMMENT "库存匹数",
  `tm` VARCHAR(120) NULL COMMENT "条码",
  `ph` VARCHAR(120) NULL DEFAULT "" COMMENT "批号",
  `create_time` DATETIME NULL COMMENT "入库时间",
  `formal_inv_id` BIGINT NULL COMMENT "对应正式库存ID(无条码冗余待入库记录关联的正式库存ID，仅status=3时填写)"
) ENGINE=OLAP
UNIQUE KEY(`id`)
DISTRIBUTED BY HASH(`id`) BUCKETS AUTO
PROPERTIES (
"replication_num" = "1", -- 添加此行，强制指定副本数为1
"is_being_synced" = "false",
"storage_medium" = "hdd",
"storage_format" = "V2",
"inverted_index_storage_format" = "V2",
"enable_unique_key_merge_on_write" = "true",
"light_schema_change" = "true",
"disable_auto_compaction" = "false",
"enable_single_replica_compaction" = "false",
"group_commit_interval_ms" = "10000",
"group_commit_data_bytes" = "134217728",
"enable_mow_light_delete" = "false"
);

# 在MariaDB中导出数据到csv中
mysqldump -u root -p -T /tmp cloud_cloth_finance_db inventory \
--fields-terminated-by=',' \
--fields-enclosed-by='"' \
--lines-terminated-by='\n'

# 导入数据到Doris中
# 192.168.1.181:8030是Doris FE地址
# /api/cloud_cloth_db/inventory/_stream_load是Doris中的库和表
curl --location-trusted -u admin: \
-H "column_separator:," \
-H "enclose:\"" \
-H "trim_double_quotes:true" \
-T /tmp/inventory.txt \
http://192.168.1.181:8030/api/cloud_cloth_db/inventory/_stream_load
```

## Doris4.0.5 ON STREAMING数据同步是基于Flink CDC吗？

Apache Doris 的 `ON STREAMING` 数据同步功能与 Flink CDC 关系密切，但并非简单的“基于”关系。更准确地说，Doris 提供了多种数据同步方案，其中一些深度集成了 Flink CDC 的能力，而另一些则是 Doris 自身实现的功能。

具体可以从以下两个层面来理解：

### 方案一：深度集成 Flink CDC 的整库同步

这是 `ON STREAMING` 语法最主要的应用场景，也是与 Flink CDC 关系最紧密的部分。

*   **核心能力**：当你使用 `CREATE JOB ... ON STREAMING FROM MYSQL ...` 这样的命令进行**整库或多表同步**时，Doris 的底层正是利用了 Flink CDC 的能力。
*   **工作原理**：Doris 通过集成 Flink CDC，能够直接读取 MySQL 等数据库的事务日志（如 Binlog），实现全量数据快照和增量变更数据的无缝捕获与同步。这个过程对用户是透明的，你只需要通过简单的 SQL 命令配置同步任务，而无需手动编写和部署 Flink 作业。
*   **功能特性**：这种集成方案支持自动创建下游表、处理表结构变更（Schema Change）等高级功能，极大地简化了实时数据集成的复杂度。

### 方案二：Doris 原生的 CDC Stream TVF

Doris 也提供了一套原生的表值函数（TVF）来处理 CDC 数据。

*   **核心能力**：`cdc_stream()` 是一个 Doris 内置的表值函数，可以直接在 SQL 中使用，例如 `SELECT * FROM cdc_stream(...)`。
*   **与 Flink CDC 的关系**：`cdc_stream()` 是 Doris 自身提供的功能，用于监听数据库的变更日志。它通常与 `ON STREAMING` 配合使用，以实现持续的单表增量同步。虽然它和 Flink CDC 的目标一致，但它是 Doris 原生实现的一套机制。

### 总结

| 特性           | 基于 Flink CDC 的整库同步                    | Doris 原生 CDC Stream TVF                      |
| :------------- | :------------------------------------------- | :--------------------------------------------- |
| **底层技术**   | **Flink CDC**                                | **Doris 原生实现**                             |
| **使用语法**   | `CREATE JOB ... ON STREAMING FROM MYSQL ...` | `SELECT * FROM cdc_stream(...)`                |
| **主要场景**   | 整库、多表的**全量+增量**同步                | 单表的**增量**同步（或配合 Job 实现全量+增量） |
| **配置复杂度** | 低，一条 SQL 即可                            | 相对灵活，可嵌入复杂查询                       |

总而言之，你提到的 `ON STREAMING` 数据同步是一个功能集合。在处理复杂的整库同步场景时，它确实依赖于 Flink CDC 的强大能力；而在更轻量或更定制化的单表同步场景中，则会使用 Doris 原生的 `cdc_stream` 函数。

## 自动同步MariaDB数据到Doris中

>说明：使用on streaming job自动同步。

1. 启动本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-doris/docker-compose.yaml

2. MariaDB启用binlog_format=ROW格式的二进制日志。

3. Doris BE、FE、CDC Client配置使用utf8编码格式，否则数据同步后中文会乱码。

   ```yaml
   doris-be:
       build:
         context: .      
         dockerfile: Dockerfile-doris-be
       image: demo-doris-be-dev
       restart: unless-stopped
       # 与 FE 相同，使用宿主机网络，保证 BE 在 FE 元数据中登记为对外可达 IP。
       network_mode: host
       environment:
         TZ: Asia/Shanghai
         # 强制容器内 locale 为 UTF-8，避免 Java/脚本在极简镜像里退回 ASCII 导致中文被替换为 '?'
         LANG: C.UTF-8
         LC_ALL: C.UTF-8
         # 额外说明（本问题关键）：
         # - `CREATE JOB ... FROM MYSQL` 会在 Doris 侧启动 CDC/同步相关的 Java 进程（你日志里看到的 `cdc-client.jar`）
         # - 该进程不一定会读取 `be.conf`/`fe.conf` 里的 `JAVA_OPTS_FOR_JDK_17`
         # - 因此这里用 `JAVA_TOOL_OPTIONS` 兜底强制 UTF-8，避免中文在写入前被编码器替换为 '?'
         JAVA_TOOL_OPTIONS: "-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -Xms512m -Xmx512m"
   ```

   be.conf配置：

   ```properties
   JAVA_OPTS_FOR_JDK_17="-Dfile.encoding=UTF-8 ..."
   ```

4. 启动Doris服务

   ```sh
   docker compose up -d
   ```

5. 连接Doris服务创建同步job

   ```sh
   $ mysql -uroot -h 127.0.0.1 -P 9030 -p123456
   
   CREATE DATABASE demo;
   
   CREATE JOB demo_db_sync_job
   ON STREAMING
   /*PROPERTIES (
     "max_interval" = "1s"
   )*/
   FROM MYSQL (
       "jdbc_url" = "jdbc:mysql://192.168.1.181:3306",
       "driver_url" = "mysql-connector-j-8.0.31.jar",
       "driver_class" = "com.mysql.cj.jdbc.Driver",
       "user" = "root",
       "password" = "123456",
       "database" = "demo",
       "include_tables" = "auth,student,course", -- 指定要同步的表
       "offset" = "initial" -- 从初始位置开始全量同步
   )
   TO DATABASE demo (
       "table.create.properties.replication_num" = "1"
   );
   
   # 查看数据是否全部同步
   SELECT * FROM auth;
   
   # 测试增量同步
   insert into auth(account,`password`,create_time) values('admin','123456',now());
   ```

6. 测试on streaming job是否支持Schema变更（经过测试证明不支持）

   ```sql
   # 修改表添加列
   alter table auth add column email varchar(255) comment '邮箱';
   
   # 测试是否同步新列和数据
   insert into auth(account,`password`,email,create_time) values('root','123456','a@qq.com',now());
   ```

7. job管理

   ```sql
   # 查看job状态
   SELECT * FROM jobs("type"="insert");
   
   # 暂停job
   PAUSE JOB WHERE jobname = 'demo_db_sync_job';
   
   # 恢复job
   RESUME JOB WHERE jobname = 'demo_db_sync_job';
   
   # 删除job
   DROP JOB WHERE jobname = 'demo_db_sync_job';
   
   # 修改job同步频率为1秒
   ALTER JOB demo_db_sync_job
   PROPERTIES (
     "max_interval" = "1"
   );
   ```

## 物化视图是什么呢？

Apache Doris 的**物化视图（Materialized View，简称 MV）**，简单来说，就是一种**“以空间换时间”**的查询加速技术。

你可以把它想象成 Doris 为你预先计算并存储好的一张“小抄”或“汇总表”。当你的查询请求到来时，Doris 会自动判断是否能用这张“小抄”来直接回答，而不用去翻那本厚厚的“原始账本”（基表），从而实现毫秒级的查询响应。

以下是关于 Doris 物化视图的核心解析：

### 1. 核心概念：它是什么？

*   **物理存储**：与普通的视图（只存 SQL 逻辑）不同，物化视图**实实在在地存储了数据**。它占用磁盘空间，是物理存在的表。
*   **预计算**：数据在创建物化视图时或基表数据变更时，就已经按照你定义的规则（如 `GROUP BY`、`SUM`、`JOIN`）计算好了。
*   **透明加速**：这是 Doris 最强大的地方。你**不需要修改业务 SQL**，依然查询原始表，Doris 的优化器会自动识别并“路由”到物化视图上，你完全无感知。

### 2. 两种主要类型

Doris 的物化视图主要分为两类，适用场景不同：

| 特性         | **同步物化视图 (Sync MV)**                                   | **异步物化视图 (Async MV)**                           |
| :----------- | :----------------------------------------------------------- | :---------------------------------------------------- |
| **别名**     | Rollup (老版本叫法)                                          | 异步 MV                                               |
| **更新方式** | **强同步**。基表写入数据时，物化视图同步更新，保证数据强一致性。 | **异步**。定时或手动刷新（如每隔1小时），数据有延迟。 |
| **支持逻辑** | 主要是**单表**的聚合、排序、列裁剪。                         | 支持**多表 JOIN**、复杂的嵌套查询。                   |
| **适用场景** | 实时性要求极高的报表、固定维度的聚合分析。                   | 离线报表、复杂的多表关联分析、数据仓库分层。          |
| **性能**     | 极快，查询延迟最低。                                         | 快，但受限于刷新频率。                                |

### 3. 为什么需要它？（对比普通视图）

为了让你更直观地理解，我们可以对比一下：

*   **普通视图 (Hive View/Logical View)**：像一个**快捷方式**。每次你点击它，系统都要重新运行一遍底层的 SQL。如果数据量大，每次都会很慢。
*   **物化视图 (Doris MV)**：像一份**提前做好的饭菜**。客人来了直接端上桌，不需要现场买菜做饭。虽然占冰箱空间（存储），但上菜速度极快。

### 4. 典型应用场景

1.  **固定维度的聚合加速**：
    *   例如：你有一张亿级的订单表，但老板每天都要看“按城市、按日期”统计的销售额。你可以创建一个按 `city` 和 `date` 分组的物化视图，查询速度可从分钟级提升至毫秒级。
2.  **列裁剪（宽表瘦身）**：
    *   例如：原表有 200 个字段，但 90% 的查询只用其中 5 个字段。创建一个只包含这 5 个字段的物化视图，可以大幅减少 I/O，提升查询效率。
3.  **去重计数加速**：
    *   利用 `BITMAP_UNION` 或 `HLL_UNION` 等函数，在物化视图中预先计算 UV（独立访客数），避免每次查询都进行昂贵的去重计算。
4.  **多表关联（Join）**：
    *   使用**异步物化视图**，将经常需要关联的“订单表”和“用户表”预先 Join 成一张宽表存起来，查询时直接查这张宽表，避免运行时的 Shuffle 开销。

### 5. 简单的代码示例

假设你有一张原始订单表 `sales`，查询经常是“按城市统计销售额”：

```sql
-- 1. 创建物化视图
CREATE MATERIALIZED VIEW mv_city_sales
AS
SELECT city, SUM(amount)
FROM sales
GROUP BY city;

-- 2. 用户查询（完全不用改 SQL，依然查原表）
SELECT city, SUM(amount) 
FROM sales 
WHERE city = 'Beijing' 
GROUP BY city;

-- 3. 结果
-- Doris 优化器会自动发现 mv_city_sales 可以命中，
-- 直接读取物化视图的数据返回，速度提升 10-100 倍。
```

**总结：**
Doris 物化视图是解决 OLAP 查询“既要灵活（查明细）又要快（查聚合）”矛盾的神器。对于高频、固定的查询模式，合理使用物化视图可以将查询性能提升几个数量级。

## 怎么创建异步物化视图呢？

>辅助工具（用于生产逗号隔开的表）：
>
>```sql
>SELECT GROUP_CONCAT(TABLE_NAME) 
>FROM information_schema.tables 
>WHERE table_schema = 'cloud_cloth_process_db' 
>AND TABLE_NAME != 'undo_log';
>```

在 Apache Doris 中，**异步物化视图（Asynchronous Materialized View）** 是处理复杂查询加速、多表关联（Join）以及 T+1 报表场景的神器。与同步物化视图不同，它不会阻塞数据写入，而是通过定时或手动触发的方式进行刷新。

以下是创建异步物化视图的完整指南，包含语法结构、常用场景示例及关键参数说明。

### 1. 核心语法结构

创建异步物化视图的基本语法如下：

```sql
CREATE MATERIALIZED VIEW [IF NOT EXISTS] <视图名称>
[BUILD <构建模式>] 
[REFRESH <刷新方式> <刷新触发器>]
[PARTITION BY (<分区列> | DATE_TRUNC(<分区列>, <粒度>))]
[DISTRIBUTED BY HASH(<分桶列>) BUCKETS <分桶数> | RANDOM]
[PROPERTIES ("<属性名>" = "<属性值>")]
AS <查询语句>;
```

### 2. 常用场景与示例

根据你的需求，以下是三种最常见的创建方式：

#### 场景一：单表聚合加速（按天分区，定时刷新）
这是最典型的 T+1 报表或实时大屏场景。我们使用 `DATE_TRUNC` 进行分区上卷，将基表的细粒度数据按月或按天聚合。

```sql
CREATE MATERIALIZED VIEW mv_order_daily
-- 按天分区，利用 DATE_TRUNC 函数
PARTITION BY date_trunc('day', order_date)
-- 按用户ID哈希分桶
DISTRIBUTED BY HASH(user_id) BUCKETS 16
-- 设置刷新策略：异步，每 10 分钟刷新一次
REFRESH ASYNC EVERY (INTERVAL 10 MINUTE)
PROPERTIES (
    "replication_num" = "3"
)
AS
SELECT
    order_date,
    user_id,
    city,
    SUM(order_amount) as gmv,
    COUNT(*) as order_cnt
FROM ods_order
GROUP BY order_date, user_id, city;
```

#### 场景二：多表关联（Join）加速
异步物化视图支持复杂的 Join 操作，这是同步物化视图难以做到的。

```sql
CREATE MATERIALIZED VIEW mv_user_order_detail
BUILD DEFERRED -- 创建时不立即刷新，稍后手动触发
REFRESH AUTO ON MANUAL -- 设置为自动增量刷新，但手动触发
DISTRIBUTED BY HASH(o.order_id) BUCKETS 10
AS
SELECT
    o.order_id,
    o.order_date,
    u.user_name,
    u.age,
    o.amount
FROM order_table o
JOIN user_table u ON o.user_id = u.id;
```

#### 场景三：基于外部表（数据湖）
你可以直接基于 Hive、Iceberg 等外部表创建物化视图，将数据湖的数据加速查询。

```sql
CREATE MATERIALIZED VIEW mv_iceberg_stats
REFRESH ASYNC EVERY (INTERVAL 1 HOUR)
AS
SELECT
    user_id,
    date_trunc('day', event_time) as dt,
    COUNT(*) as pv
FROM iceberg_catalog.db.user_behavior
GROUP BY user_id, dt;
```

### 3. 关键参数详解

为了让你更灵活地控制物化视图，以下是核心参数的说明：

| 参数类别         | 关键字             | 说明                                 | 推荐用法                           |
| :--------------- | :----------------- | :----------------------------------- | :--------------------------------- |
| **构建模式**     | `BUILD IMMEDIATE`  | 创建后立即执行一次全量刷新（默认）。 | 适合数据量小的场景。               |
| (Build Mode)     | `BUILD DEFERRED`   | 创建时不刷新，需后续手动触发。       | **推荐**，避免创建时占用大量资源。 |
| **刷新方式**     | `REFRESH AUTO`     | 智能增量刷新，只刷新变化的分区。     | **首选**，性能最高。               |
| (Refresh Method) | `REFRESH COMPLETE` | 全量刷新，每次把所有数据重算一遍。   | 仅用于数据量极小或逻辑复杂的场景。 |
| **触发策略**     | `ON MANUAL`        | 手动触发刷新。                       | 适合 T+1 报表，配合调度系统使用。  |
| (Trigger)        | `ON SCHEDULE`      | 定时触发（如 `EVERY 1 HOUR`）。      | 适合需要准实时更新的场景。         |

### 4. 运维与管理

创建完成后，你需要掌握如何查看和维护它：

*   **手动触发刷新**：
    如果你设置了 `ON MANUAL`，或者想强制更新数据，可以执行：
    ```sql
    REFRESH MATERIALIZED VIEW mv_order_daily;
    ```

*   **查看刷新任务状态**：
    通过以下命令查看异步任务的执行进度和报错信息：
    ```sql
    SELECT * FROM jobs("type"="mv");
    ```

*   **查看视图定义**：
    ```sql
    SHOW CREATE MATERIALIZED VIEW mv_order_daily;
    ```

### 5. 避坑指南

1.  **分区建议**：对于数据量大的表，**强烈建议**加上 `PARTITION BY`（通常配合 `DATE_TRUNC`）。这样在刷新时，Doris 只会重算变化的分区（例如昨天和今天），性能会比全量刷新高几个数量级。
2.  **唯一键限制**：异步物化视图目前不支持 Unique Key 模型，通常使用 Aggregate Key 或 Duplicate Key 模型。
3.  **字段类型**：列名可以自定义，但列类型是由查询语句自动推导的，不能手动指定。

### 6. 实践

1. 启动本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-doris/docker-compose.yaml

   ```sh
   docker compose up -d
   ```

2. 参考上面说明配置MariaDB同步数据到Doris中。

3. 创建同步物化视图

   ```sql
   CREATE MATERIALIZED VIEW mv (
   	id,
   	student_name,
   	course_name,
   	create_time
   )
   -- REFRESH ASYNC EVERY (INTERVAL 10 MINUTE)
   REFRESH AUTO ON COMMIT
   PROPERTIES (
       "replication_num" = "1"
   )
   AS
   SELECT
       parent.id,
       parent.name AS student_name,
       child.name AS course_name,
       parent.create_time 
   FROM course child
   JOIN student parent ON child.student_id=parent.id;
   ```

4. 查看所有物化视图相关任务状态

   ```sql
   SELECT * FROM tasks("type"="mv");
   ```