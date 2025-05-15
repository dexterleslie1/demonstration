# TiDB



## 介绍

TiDB 是一个开源的**分布式关系型数据库**，由 PingCAP 公司开发并维护，结合了传统关系型数据库（如 MySQL）的易用性和 NoSQL 数据库的可扩展性，专为大规模数据处理和高并发场景设计。以下是其核心特性和应用场景的详细介绍：

------

### **核心特性**

1. 水平扩展性
   - 通过 **TiKV**（分布式存储引擎）和 **PD**（Placement Driver，元数据管理组件）实现数据分片和动态扩容，支持从单节点扩展到数千节点，轻松应对 PB 级数据。
   - 计算与存储分离架构，可独立扩展计算（TiDB Server）或存储（TiKV）资源。
2. MySQL 兼容性
   - 支持 **MySQL 5.7 协议**和大部分语法，现有 MySQL 应用可无缝迁移（需少量适配）。
   - 提供兼容的 **JDBC/ODBC 驱动**和 **ORM 工具支持**（如 Hibernate、MyBatis）。
3. 高可用与容灾
   - 基于 **Raft 协议**的多副本复制，确保数据强一致性（默认 3 副本）。
   - 支持跨数据中心部署（如三地五中心），满足金融级容灾需求。
4. 分布式事务
   - 采用 **Percolator 模型**实现跨分片事务，支持 ACID，确保复杂业务逻辑的一致性。
5. 实时 HTAP 能力
   - 通过 **TiFlash**（列式存储引擎）实现实时分析查询，无需 ETL 即可同时处理事务和分析负载。
6. 云原生与多云部署
   - 支持 **Kubernetes 容器化部署**，适配公有云（AWS、Azure、GCP）和私有云环境。
   - 提供 **TiDB Operator** 简化集群管理。

------

### **应用场景**

1. 海量数据存储与高并发 OLTP
   - 电商、金融、物联网等场景中，需处理每秒数万次交易或设备数据写入。
2. 实时数据分析（HTAP）
   - 例如实时风控、用户行为分析，避免传统 Lambda 架构的延迟和复杂性。
3. 混合负载系统
   - 同一集群同时承载订单系统（OLTP）和报表查询（OLAP），降低架构复杂度。
4. 云原生应用
   - 微服务架构下，作为有状态服务的持久化存储层，支持弹性伸缩。

------

### **架构组件**

- **TiDB Server**：计算节点，处理 SQL 请求，无状态可水平扩展。
- **PD (Placement Driver)**：元数据管理和调度节点，管理集群拓扑和负载均衡。
- **TiKV**：分布式键值存储引擎，基于 Raft 协议实现数据分片和强一致。
- **TiFlash**：列式存储引擎，为分析查询加速。
- **TiSpark**：通过 Spark 连接 TiDB，支持复杂分析任务。

------

### **与同类数据库对比**



| **特性**      | **TiDB**                   | **CockroachDB**             | **YugabyteDB**           |
| ------------- | -------------------------- | --------------------------- | ------------------------ |
| **协议兼容**  | MySQL                      | PostgreSQL                  | PostgreSQL/Cassandra     |
| **事务模型**  | Percolator（乐观）         | Optimistic/Pessimistic 混合 | HBase 风格（无全局时钟） |
| **HTAP 支持** | 原生支持（TiFlash）        | 需外部工具                  | 需外部工具               |
| **生态工具**  | 丰富（TiDB Dashboard、BR） | 较简单                      | 依赖 PostgreSQL 生态     |



------

### **学习与使用建议**

1. 快速上手
   - 部署本地测试环境：使用 `tiup playground` 一键启动单节点集群。
   - 官方文档：https://docs.pingcap.com/
2. 性能调优
   - 合理配置分片键（避免热点）。
   - 针对分析场景，使用 `TiFlash` 副本。
3. 监控与运维
   - 内置 Prometheus + Grafana 监控面板。
   - 支持 **BR (Backup & Restore)** 工具实现全量/增量备份。

------

### **典型用户案例**

- **Shopee**：东南亚电商平台，使用 TiDB 处理每秒 300 万次订单写入。
- **知乎**：支撑问答社区的高并发读写和实时数据分析。
- **微众银行**：金融级分布式核心系统，满足强一致性和容灾需求。

------

TiDB 的设计理念是“让数据库像云服务一样简单”，适合需要**大规模数据、高并发、强一致性**且希望**降低运维复杂度**的场景。如果您的业务有类似需求，TiDB 是一个值得考虑的选项。



## 整体架构

TiDB分布式数据库的整体架构由计算层、存储层和协调层三大核心组件构成，各组件通过高效协作实现水平扩展、高可用和实时HTAP能力，具体架构如下：

### **一、计算层（TiDB Server）**

- **功能**：作为无状态的SQL层，TiDB Server负责解析SQL、生成执行计划，并与存储层交互获取数据。
- 特性：
  - 支持MySQL协议和语法，兼容性极高，可无缝迁移现有应用。
  - 水平扩展能力强，通过负载均衡组件（如LVS、HAProxy）实现多实例部署，提升并发处理能力。
- **作用**：将SQL请求转换为对存储层的键值操作，是系统的入口和计算核心。

### **二、存储层**

#### **1. TiKV（行存储引擎）**

- **功能**：负责OLTP数据的存储，采用行存储格式，支持事务机制。
- 特性：
  - **Region分片**：数据按Key Range分片为Region，每个Region默认约96MB-140MB，超过阈值自动分裂。
  - **多副本强一致**：默认3副本，基于Raft协议实现强一致性，支持自动故障转移。
  - **MVCC并发控制**：实现多版本并发控制，避免读写冲突。
- **作用**：提供高可用的OLTP存储能力，确保数据强一致性和事务支持。

#### **2. TiFlash（列存储引擎）**

- **功能**：专门用于OLAP分析场景，提供列式存储。
- 特性：
  - **异步复制**：实时从TiKV复制数据，保证与TiKV的一致性读取。
  - **高效分析查询**：列式存储提升分析查询效率，适合大规模数据分析。
- **作用**：实现实时HTAP能力，在同一集群中同时支持事务和分析负载。

### **三、协调层（Placement Driver，PD）**

- **功能**：作为集群的“大脑”，负责元数据管理、调度和负载均衡。
- 特性：
  - **元数据存储**：存储每个TiKV节点的实时数据分布情况和集群拓扑结构。
  - **调度和负载均衡**：根据数据分布状态，下发调度命令，确保数据均匀分布和负载均衡。
  - **事务ID分配**：为分布式事务分配全局唯一且递增的事务ID。
  - **高可用性**：至少3个节点构成，支持自动故障切换。
- **作用**：确保集群的高可用性、数据一致性和性能优化。

### **四、架构协作流程**

1. **SQL请求处理**：客户端发送SQL请求到TiDB Server，TiDB Server解析SQL并生成执行计划。
2. **数据定位**：TiDB Server通过PD获取数据存储位置（TiKV或TiFlash）。
3. 数据读取与计算：
   - 对于OLTP请求，TiDB Server将请求转发到TiKV执行。
   - 对于OLAP请求，TiDB Server可根据优化器选择TiKV或TiFlash执行。
4. **结果返回**：TiDB Server将执行结果返回给客户端。

### **五、架构优势**

1. **水平扩展性**：通过增加TiKV或TiFlash节点，轻松扩展存储和计算能力。
2. **高可用性**：多副本和自动故障转移机制确保系统在节点故障时仍能正常运行。
3. **实时HTAP能力**：TiDB和TiFlash的结合，实现事务和分析负载的实时处理，避免数据同步延迟。
4. **MySQL兼容性**：无缝迁移现有MySQL应用，降低迁移成本。



## 部署

>[参考官方文档](https://docs.pingcap.com/zh/tidb/stable/quick-start-with-tidb)



### 部署本地测试集群

>注意：TiUP Playground 默认监听 `127.0.0.1`，服务仅本地可访问。若需要使服务可被外部访问，可使用 `--host` 参数指定监听网卡绑定外部可访问的 IP。

本节介绍如何利用本地 macOS 或者单机 Linux 环境快速部署 TiDB 测试集群。通过部署 TiDB 集群，你可以了解 TiDB 的基本架构，以及 TiDB、TiKV、PD、监控等基础组件的运行。

TiDB 是一个分布式系统。最基础的 TiDB 测试集群通常由 2 个 TiDB 实例、3 个 TiKV 实例、3 个 PD 实例和可选的 TiFlash 实例构成。通过 TiUP Playground，可以快速搭建出上述的一套基础测试集群，步骤如下：

1. 下载并安装 TiUP。

   ```bash
   $ curl --proto '=https' --tlsv1.2 -sSf https://tiup-mirrors.pingcap.com/install.sh | sh
     % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                    Dload  Upload   Total   Spent    Left  Speed
   100 5180k  100 5180k    0     0  9774k      0 --:--:-- --:--:-- --:--:-- 9774k
   Successfully set mirror to https://tiup-mirrors.pingcap.com
   Detected shell: bash
   Shell profile:  /root/.bashrc
   /root/.bashrc has been modified to add tiup to PATH
   open a new terminal or source /root/.bashrc to use it
   Installed path: /root/.tiup/bin/tiup
   ===============================================
   Have a try:     tiup playground
   ===============================================
   
   ```

2. 加载最新的 /root/.bashrc 以使用 tiup 命令

   ```bash
   source /root/.bashrc
   ```

3. 检查 tiup 命令

   ```bash
   $ tiup --version
   1.16.2 v1.16.2-nightly-7
   Go Version: go1.21.13
   Git Ref: master
   GitHash: 2a6bd3144e8d3ed8329e035f2580d3800b02f4be
   ```

4. 在当前 session 执行以下命令启动集群。

   >注意：
   >
   >- 如果按以下方式执行 playground，在结束部署测试后，TiUP 会自动清理掉原集群数据，重新执行命令会得到一个全新的集群。
   >
   >- 如果希望持久化数据，需要在启动集群时添加 TiUP 的 `--tag` 参数，详见 [启动集群时指定 `tag` 以保留数据](https://docs.pingcap.com/zh/tidb/stable/tiup-playground/#启动集群时指定-tag-以保留数据)。
   >
   >  ```bash
   >  tiup playground --tag ${tag_name}
   >  ```
   >
   >  

   - 直接运行 `tiup playground` 命令会运行最新版本的 TiDB 集群，其中 TiDB、TiKV、PD 和 TiFlash 实例各 1 个：

     ```bash
     $ tiup playground
     Checking updates for component playground... 
     A new version of playground is available:  -> v1.16.2
     
         To update this component:   tiup update playground
         To update all components:   tiup update --all
     
     The component `playground` version  is not installed; downloading from repository.
     download https://tiup-mirrors.pingcap.com/playground-v1.16.2-linux-amd64.tar.gz 8.20 MiB / 8.20 MiB 100.00% 64.31 MiB/s                                                                                            
     
     Note: Version constraint  is resolved to v8.5.1. If you'd like to use other versions:
     
         Use exact version:      tiup playground v7.1.0
         Use version range:      tiup playground ^5
         Use nightly:            tiup playground nightly
     
     The component `pd` version v8.5.1 is not installed; downloading from repository.
     download https://tiup-mirrors.pingcap.com/pd-v8.5.1-linux-amd64.tar.gz 54.32 MiB / 54.32 MiB 100.00% 18.41 MiB/s                                                                                                   
     Start pd instance: v8.5.1
     The component `tikv` version v8.5.1 is not installed; downloading from repository.
     download https://tiup-mirrors.pingcap.com/tikv-v8.5.1-linux-amd64.tar.gz 364.63 MiB / 364.63 MiB 100.00% 18.53 MiB/s                                                                                               
     Start tikv instance: v8.5.1
     The component `tidb` version v8.5.1 is not installed; downloading from repository.
     download https://tiup-mirrors.pingcap.com/tidb-v8.5.1-linux-amd64.tar.gz 91.70 MiB / 91.70 MiB 100.00% 21.57 MiB/s                                                                                                 
     Start tidb instance: v8.5.1
     Waiting for tidb instances ready
     - TiDB: 127.0.0.1:4000 ... Done
     The component `prometheus` version v8.5.1 is not installed; downloading from repository.
     download https://tiup-mirrors.pingcap.com/prometheus-v8.5.1-linux-amd64.tar.gz 122.53 MiB / 122.53 MiB 100.00% 11.92 MiB/s                                                                                         
     download https://tiup-mirrors.pingcap.com/grafana-v8.5.1-linux-amd64.tar.gz 50.18 MiB / 50.18 MiB 100.00% 27.01 MiB/s                                                                                              
     The component `tiflash` version v8.5.1 is not installed; downloading from repository.
     download https://tiup-mirrors.pingcap.com/tiflash-v8.5.1-linux-amd64.tar.gz 509.42 MiB / 509.42 MiB 100.00% 12.61 MiB/s                                                                                            
     Start tiflash instance: v8.5.1
     Waiting for tiflash instances ready
     - TiFlash: 127.0.0.1:3930 ... Done
     
     🎉 TiDB Playground Cluster is started, enjoy!
     
     Connect TiDB:    mysql --comments --host 127.0.0.1 --port 4000 -u root
     TiDB Dashboard:  http://127.0.0.1:2379/dashboard
     Grafana:         http://127.0.0.1:3000
     
     ```

   - 或者指定 TiDB 版本以及各组件实例个数，命令类似于：

     ```bash
     tiup playground v8.5.1 --db 2 --pd 3 --kv 3
     ```

     如果要查看当前支持部署的所有 TiDB 版本，执行 `tiup list tidb`。

5. 新开启一个 session 以访问 TiDB 数据库和集群端点。

   - 连接 TiDB 数据库：

     - 使用 TiUP `client` 连接 TiDB：

       ```bash
       tiup client
       ```

     - 或者使用 MySQL 客户端连接 TiDB：

       ```bash
       mysql --host 127.0.0.1 --port 4000 -u root
       ```

   - 访问 Prometheus 管理界面：[http://127.0.0.1:9090](http://127.0.0.1:9090/)。

   - 访问 [TiDB Dashboard](https://docs.pingcap.com/zh/tidb/stable/dashboard-intro/) 页面：http://127.0.0.1:2379/dashboard，默认用户名为 `root`，密码为空。

   - 访问 Grafana 界面：[http://127.0.0.1:3000](http://127.0.0.1:3000/)，默认用户名和密码都为 `admin`。

6. 测试完成之后，可以通过执行以下步骤来清理集群：

   - 按下 Control+C 键停掉上述启用的 TiDB 服务。

   - 等待服务退出操作完成后，执行以下命令：

     ```bash
     tiup clean --all
     ```

     清理所有通过 TiUP 安装的组件及其相关数据，彻底删除 TiUP 管理的所有组件（如 TiDB、PD、TiKV、TiDB Dashboard 等）及其运行时产生的数据（如日志、临时文件等）。



### 在单机上模拟部署生产环境集群

本节介绍如何在单台 Linux 服务器上体验 TiDB 最小的完整拓扑的集群，并模拟生产环境下的部署步骤。

下文将参照 TiUP 最小拓扑的一个 YAML 文件部署 TiDB 集群。

1. 下载并安装 TiUP：

   ```bash
   curl --proto '=https' --tlsv1.2 -sSf https://tiup-mirrors.pingcap.com/install.sh | sh
   ```

2. 加载最新的 /root/.bashrc 以使用 tiup 命令

   ```bash
   source /root/.bashrc
   ```

3. 检查 tiup 命令

   ```bash
   $ tiup --version
   1.16.2 v1.16.2-nightly-7
   Go Version: go1.21.13
   Git Ref: master
   GitHash: 2a6bd3144e8d3ed8329e035f2580d3800b02f4be
   ```

4. 安装 TiUP 的 cluster 组件：

   ```bash
   tiup cluster
   ```

5. 如果机器已经安装 TiUP cluster，需要更新软件版本：

   ```bash
   tiup update --self && tiup update cluster
   ```

6. 由于模拟多机部署，需要通过 root 用户调大 sshd 服务的连接数限制：

   - 修改 `/etc/ssh/sshd_config` 将 `MaxSessions` 调至 20。

   - 重启 sshd 服务：

     ```bash
     service sshd restart
     ```

7. 创建并启动集群：

   按下面的配置模板，创建并编辑 [拓扑配置文件](https://docs.pingcap.com/zh/tidb/stable/tiup-cluster-topology-reference/)，命名为 `topo.yaml`。其中：

   - `user: "tidb"`：表示通过 `tidb` 系统用户（部署会自动创建）来做集群的内部管理，默认使用 22 端口通过 ssh 登录目标机器
   - `replication.enable-placement-rules`：设置这个 PD 参数来确保 TiFlash 正常运行
   - `host`：设置为本部署主机的 IP

   配置模板如下：

   ```yaml
   # # Global variables are applied to all deployments and used as the default value of
   # # the deployments if a specific deployment value is missing.
   global:
    user: "tidb"
    ssh_port: 22
    deploy_dir: "/tidb-deploy"
    data_dir: "/tidb-data"
   
   # # Monitored variables are applied to all the machines.
   monitored:
    node_exporter_port: 9100
    blackbox_exporter_port: 9115
   
   server_configs:
    tidb:
      instance.tidb_slow_log_threshold: 300
    tikv:
      readpool.storage.use-unified-pool: false
      readpool.coprocessor.use-unified-pool: true
    pd:
      replication.enable-placement-rules: true
      replication.location-labels: ["host"]
    tiflash:
      logger.level: "info"
   
   pd_servers:
    - host: 192.168.235.156
   
   tidb_servers:
    - host: 192.168.235.156
   
   tikv_servers:
    - host: 192.168.235.156
      port: 20160
      status_port: 20180
      config:
        server.labels: { host: "logic-host-1" }
   
    - host: 192.168.235.156
      port: 20161
      status_port: 20181
      config:
        server.labels: { host: "logic-host-2" }
   
    - host: 192.168.235.156
      port: 20162
      status_port: 20182
      config:
        server.labels: { host: "logic-host-3" }
   
   tiflash_servers:
    - host: 192.168.235.156
   
   monitoring_servers:
    - host: 192.168.235.156
   
   grafana_servers:
    - host: 192.168.235.156
   ```

8. 执行集群部署命令：

   ```bash
   tiup cluster deploy <cluster-name> <version> ./topo.yaml --user root -p
   ```

   - 参数 `<cluster-name>` 表示设置集群名称
   - 参数 `<version>` 表示设置集群版本，例如 `v8.5.1`。可以通过 `tiup list tidb` 命令来查看当前支持部署的 TiDB 版本
   - 参数 `--user` 表示初始化环境的用户
   - 参数 `-p` 表示在连接目标机器时使用密码登录

   >注意：如果主机通过密钥进行 SSH 认证，请使用 `-i` 参数指定密钥文件路径，`-i` 与 `-p` 不可同时使用。

   按照引导，输入”y”及 root 密码，来完成部署：

   ```bash
   Do you want to continue? [y/N]:  y
   Input SSH password:
   ```

9. 启动集群：

   ```bash
   tiup cluster start <cluster-name>
   ```

10. 访问集群端点：

    - 安装 MySQL 客户端。如果已安装，则跳过这一步骤。

      ```bash
      yum -y install mysql
      ```

    - 使用 MySQL 客户端访问 TiDB 数据库，密码为空：

      ```bash
      mysql -h 192.168.235.156 -P 4000 -u root
      ```

    - 访问 Grafana 监控页面：[http://{grafana-ip}:3000](http://{grafana-ip}:3000/)，默认用户名和密码均为 `admin`。

    - 访问集群 [TiDB Dashboard](https://docs.pingcap.com/zh/tidb/stable/dashboard-intro/) 监控页面：http://{pd-ip}:2379/dashboard，默认用户名为 `root`，密码为空。

11. （可选）查看集群列表和拓扑结构：

    - 执行以下命令确认当前已经部署的集群列表：

      ```bash
      tiup cluster list
      ```

    - 执行以下命令查看集群的拓扑结构和状态：

      ```bash
      tiup cluster display <cluster-name>
      ```

12. 测试完成之后，可以通过执行以下步骤来清理集群：

    - 停止集群。

      ```bash
      tiup cluster stop <cluster-name>
      ```
    
    - 删除集群的所有数据（但不删除集群），执行以下命令：
    
      ```bash
      tiup cluster clean <cluster-name> --all
      ```
      
    - 删除集群
    
      ```bash
      tiup cluster destroy <cluster-name>
      ```
    
      

### 部署生产环境集群

>[参考官方文档](https://docs.pingcap.com/zh/tidb/stable/production-deployment-using-tiup/)

本指南介绍如何在生产环境中使用 [TiUP](https://github.com/pingcap/tiup) 部署 TiDB 集群。

TiUP 是在 TiDB v4.0 中引入的集群运维工具，提供了使用 Golang 编写的集群管理组件 [TiUP cluster](https://github.com/pingcap/tiup/tree/master/components/cluster)。通过使用 TiUP cluster 组件，你可以轻松执行日常的数据库运维操作，包括部署、启动、关闭、销毁、弹性扩缩容、升级 TiDB 集群，以及管理 TiDB 集群参数。

TiUP 还支持部署 TiDB、TiFlash、TiCDC 以及监控系统。本指南介绍了如何部署不同拓扑的 TiDB 集群。

#### 软硬件环境需求及前置检查

务必阅读以下文档：

- [软硬件环境需求](https://docs.pingcap.com/zh/tidb/stable/hardware-and-software-requirements/)
- [环境与系统配置检查](https://docs.pingcap.com/zh/tidb/stable/check-before-deployment/)

此外，建议阅读了解 [TiDB 安全配置最佳实践](https://docs.pingcap.com/zh/tidb/stable/best-practices-for-security-configuration/)。

TiDB 支持部署和运行在 Intel x86-64 架构的 64 位通用硬件服务器平台或者 ARM 架构的硬件服务器平台。对于开发、测试及生产环境的服务器硬件配置（不包含操作系统 OS 本身的占用）有以下要求和建议：

##### 开发及测试环境

| **组件** | **CPU** | **内存** | **本地存储**                                                 | **网络** | **实例数量(最低要求)** |
| :------- | :------ | :------- | :----------------------------------------------------------- | :------- | :--------------------- |
| TiDB     | 8 核+   | 16 GB+   | [磁盘空间要求](https://docs.pingcap.com/zh/tidb/stable/hardware-and-software-requirements/#磁盘空间要求) | 千兆网卡 | 1（可与 PD 同机器）    |
| PD       | 4 核+   | 8 GB+    | SAS, 200 GB+                                                 | 千兆网卡 | 1（可与 TiDB 同机器）  |
| TiKV     | 8 核+   | 32 GB+   | SSD, 200 GB+                                                 | 千兆网卡 | 3                      |
| TiFlash  | 32 核+  | 64 GB+   | SSD, 200 GB+                                                 | 千兆网卡 | 1                      |
| TiCDC    | 8 核+   | 16 GB+   | SAS, 200 GB+                                                 | 千兆网卡 | 1                      |

>注意
>
>- 验证测试环境中的 TiDB 和 PD 可以部署在同一台服务器上。
>- 如进行性能相关的测试，避免采用低性能存储和网络硬件配置，防止对测试结果的正确性产生干扰。
>- TiKV 的 SSD 盘推荐使用 NVME 接口以保证读写更快。
>- 如果仅验证功能，建议使用 [TiDB 数据库快速上手指南](https://docs.pingcap.com/zh/tidb/stable/quick-start-with-tidb/)进行单机功能测试。
>- 从 v6.3.0 开始，在 Linux AMD64 架构的硬件平台部署 TiFlash 时，CPU 必须支持 AVX2 指令集。确保命令 `grep avx2 /proc/cpuinfo` 有输出。而在 Linux ARM64 架构的硬件平台部署 TiFlash 时，CPU 必须支持 ARMv8 架构。确保命令 `grep 'crc32' /proc/cpuinfo | grep 'asimd'` 有输出。通过使用向量扩展指令集，TiFlash 的向量化引擎能提供更好的性能。

##### 生产环境

| **组件** | **CPU** | **内存** | **硬盘类型**   | **网络**             | **实例数量(最低要求)** |
| :------- | :------ | :------- | :------------- | :------------------- | :--------------------- |
| TiDB     | 16 核+  | 48 GB+   | SSD            | 万兆网卡（2 块最佳） | 2                      |
| PD       | 8 核+   | 16 GB+   | SSD            | 万兆网卡（2 块最佳） | 3                      |
| TiKV     | 16 核+  | 64 GB+   | SSD            | 万兆网卡（2 块最佳） | 3                      |
| TiFlash  | 48 核+  | 128 GB+  | 1 or more SSDs | 万兆网卡（2 块最佳） | 2                      |
| TiCDC    | 16 核+  | 64 GB+   | SSD            | 万兆网卡（2 块最佳） | 2                      |
| 监控     | 8 核+   | 16 GB+   | SAS            | 千兆网卡             | 1                      |

>注意
>
>- 生产环境中的 TiDB 和 PD 可以部署和运行在同一台服务器上，如对性能和可靠性有更高的要求，应尽可能分开部署。
>- 强烈建议分别为生产环境中的 TiDB、TiKV 和 TiFlash 配置至少 8 核的 CPU。强烈推荐使用更高的配置，以获得更好的性能。
>- TiKV 硬盘大小配置建议 PCIe SSD 不超过 4 TB，普通 SSD 不超过 1.5 TB。
>- 如果你在云服务商（如 AWS、Google Cloud 或 Azure）上部署 TiDB 集群，建议 TiKV 节点使用云盘。在云环境中，TiKV 实例崩溃时，本地磁盘上的数据可能会丢失。

##### 本站实验环境配置

| **组件** | **CPU** | **内存** | **硬盘类型** | **网络**         | **实例数量** |
| :------- | :------ | :------- | :----------- | :--------------- | :----------- |
| TiProxy  | 8 核    | 6 GB     | SSD          | 万兆网卡（1 块） | 1            |
| TiDB     | 8 核    | 8 GB     | SSD          | 万兆网卡（1 块） | 3            |
| PD       | 4 核    | 8 GB     | SSD          | 万兆网卡（1 块） | 1            |
| TiKV     | 8 核    | 32 GB    | SSD          | 万兆网卡（1 块） | 3            |
| 监控     | 4 核    | 8 GB     | SSD          | 万兆网卡（1 块） | 1            |

- TiDB 和 TiKV 比较消耗 CPU。

#### 设置 root 用户统一登录

在各个实例中设置 root 统一密码或者统一的免密码登录，因为稍后 tiup 命令使用 root 用户登录各个实例。

#### 在中控机上部署 TiUP 组件

在中控机上部署 TiUP 组件有两种方式：在线部署和离线部署。

##### 在线部署

以普通用户身份登录中控机。以 `tidb` 用户为例，后续安装 TiUP 及集群管理操作均通过该用户完成：

1. 执行如下命令安装 TiUP 工具：

   ```bash
   curl --proto '=https' --tlsv1.2 -sSf https://tiup-mirrors.pingcap.com/install.sh | sh
   ```

2. 按如下步骤设置 TiUP 环境变量：

   - 重新声明全局环境变量：

     ```bash
     source /root/.bashrc
     ```

   - 确认 TiUP 工具是否安装：

     ```bash
     which tiup
     ```

3. 安装 TiUP 集群组件：

   ```bash
   tiup cluster
   ```

4. 如果已经安装，则更新 TiUP 集群组件至最新版本：

   ```bash
   tiup update --self && tiup update cluster
   ```

   预期输出 `“Updated successfully!”` 字样。

5. 验证当前 TiUP 集群版本信息。执行如下命令查看 TiUP 集群组件版本：

   ```bash
   tiup --binary cluster
   ```

#### 初始化集群拓扑文件

执行如下命令，生成集群初始化配置文件：

```bash
tiup cluster template > topology.yaml
```

针对两种常用的部署场景，也可以通过以下命令生成建议的拓扑模板：

- 混合部署场景：单台机器部署多个实例，详情参见 [混合部署拓扑架构](https://docs.pingcap.com/zh/tidb/stable/hybrid-deployment-topology/)。

  ```sh
  tiup cluster template --full > topology.yaml
  ```

- 跨机房部署场景：跨机房部署 TiDB 集群，详情参见 [跨机房部署拓扑架构](https://docs.pingcap.com/zh/tidb/stable/geo-distributed-deployment-topology/)。

  ```sh
  tiup cluster template --multi-dc > topology.yaml
  ```

根据注释编辑 topology.yaml 相关内容，本站实验环境配置示例：

```yaml
# # Global variables are applied to all deployments and used as the default value of
# # the deployments if a specific deployment value is missing.
global:
  # # The user who runs the tidb cluster.
  user: "tidb"
  # # group is used to specify the group name the user belong to if it's not the same as user.
  # group: "tidb"
  # # systemd_mode is used to select whether to use sudo permissions. When its value is set to user, there is no need to add global.user to sudoers. The default value is system.
  # systemd_mode: "system"
  # # SSH port of servers in the managed cluster.
  ssh_port: 22
  # # Storage directory for cluster deployment files, startup scripts, and configuration files.
  deploy_dir: "/tidb-deploy"
  # # TiDB Cluster data storage directory
  data_dir: "/tidb-data"
  # # default listen_host for all components
  listen_host: 0.0.0.0
  # # Supported values: "amd64", "arm64" (default: "amd64")
  arch: "amd64"
  # # Resource Control is used to limit the resource of an instance.
  # # See: https://www.freedesktop.org/software/systemd/man/systemd.resource-control.html
  # # Supports using instance-level `resource_control` to override global `resource_control`.
  # resource_control:
  #   # See: https://www.freedesktop.org/software/systemd/man/systemd.resource-control.html#MemoryLimit=bytes
  #   memory_limit: "2G"
  #   # See: https://www.freedesktop.org/software/systemd/man/systemd.resource-control.html#CPUQuota=
  #   # The percentage specifies how much CPU time the unit shall get at maximum, relative to the total CPU time available on one CPU. Use values > 100% for allotting CPU time on more than one CPU.
  #   # Example: CPUQuota=200% ensures that the executed processes will never get more than two CPU time.
  #   cpu_quota: "200%"
  #   # See: https://www.freedesktop.org/software/systemd/man/systemd.resource-control.html#IOReadBandwidthMax=device%20bytes
  #   io_read_bandwidth_max: "/dev/disk/by-path/pci-0000:00:1f.2-scsi-0:0:0:0 100M"
  #   io_write_bandwidth_max: "/dev/disk/by-path/pci-0000:00:1f.2-scsi-0:0:0:0 100M"

server_configs:
  tidb:
    log.level: "error"
    # 创建带有 TiProxy 的集群时避免 TiDB server 下线时客户端连接中断
    # https://docs.pingcap.com/zh/tidb/stable/tiproxy-overview/#%E5%88%9B%E5%BB%BA%E5%B8%A6%E6%9C%89-tiproxy-%E7%9A%84%E9%9B%86%E7%BE%A4
    graceful-wait-before-shutdown: 15
  tikv:
    log-level: "error"
  pd:
    log-level: "error"

# # Monitored variables are applied to all the machines.
monitored:
  # # The communication port for reporting system information of each node in the TiDB cluster.
  node_exporter_port: 9100
  # # Blackbox_exporter communication port, used for TiDB cluster port monitoring.
  blackbox_exporter_port: 9115
  # # Storage directory for deployment files, startup scripts, and configuration files of monitoring components.
  # deploy_dir: "/tidb-deploy/monitored-9100"
  # # Data storage directory of monitoring components.
  # data_dir: "/tidb-data/monitored-9100"
  # # Log storage directory of the monitoring component.
  # log_dir: "/tidb-deploy/monitored-9100/log"

# # Server configs are used to specify the runtime configuration of TiDB components.
# # All configuration items can be found in TiDB docs:
# # - TiDB: https://docs.pingcap.com/tidb/stable/tidb-configuration-file
# # - TiKV: https://docs.pingcap.com/tidb/stable/tikv-configuration-file
# # - PD: https://docs.pingcap.com/tidb/stable/pd-configuration-file
# # - TiFlash: https://docs.pingcap.com/tidb/stable/tiflash-configuration
# #
# # All configuration items use points to represent the hierarchy, e.g:
# #   readpool.storage.use-unified-pool
# #           ^       ^
# # - example: https://github.com/pingcap/tiup/blob/master/examples/topology.example.yaml.
# # You can overwrite this configuration via the instance-level `config` field.
# server_configs:
  # tidb:
  # tikv:
  # pd:
  # tiflash:
  # tiflash-learner:
  #

tiproxy_servers:
  - host: 192.168.1.88
    deploy_dir: "/tiproxy-deploy"
    port: 6000
    status_port: 3080

# # Server configs are used to specify the configuration of PD Servers.
pd_servers:
  # # The ip address of the PD Server.
  - host: 192.168.1.92
    # # SSH port of the server.
    # ssh_port: 22
    # # PD Server name
    # name: "pd-1"
    # # communication port for TiDB Servers to connect.
    # client_port: 2379
    # # Communication port among PD Server nodes.
    # peer_port: 2380
    # # PD Server deployment file, startup script, configuration file storage directory.
    # deploy_dir: "/tidb-deploy/pd-2379"
    # # PD Server data storage directory.
    # data_dir: "/tidb-data/pd-2379"
    # # PD Server log file storage directory.
    # log_dir: "/tidb-deploy/pd-2379/log"
    # # numa node bindings.
    # numa_node: "0,1"
    # # The following configs are used to overwrite the `server_configs.pd` values.
    # config:
    #   schedule.max-merge-region-size: 20
    #   schedule.max-merge-region-keys: 200000

# # Server configs are used to specify the configuration of TiDB Servers.
tidb_servers:
  # # The ip address of the TiDB Server.
  - host: 192.168.1.90
    # # SSH port of the server.
    # ssh_port: 22
    # # The port for clients to access the TiDB cluster.
    # port: 4000
    # # TiDB Server status API port.
    # status_port: 10080
    # # TiDB Server deployment file, startup script, configuration file storage directory.
    # deploy_dir: "/tidb-deploy/tidb-4000"
    # # TiDB Server log file storage directory.
    # log_dir: "/tidb-deploy/tidb-4000/log"
  # # The ip address of the TiDB Server.
  - host: 192.168.1.91
    # ssh_port: 22
    # port: 4000
    # status_port: 10080
    # deploy_dir: "/tidb-deploy/tidb-4000"
    # log_dir: "/tidb-deploy/tidb-4000/log"
  # # The ip address of the TiDB Server.
  - host: 192.168.1.87
    # ssh_port: 22
    # port: 4000
    # status_port: 10080
    # deploy_dir: "/tidb-deploy/tidb-4000"
    # log_dir: "/tidb-deploy/tidb-4000/log"

# # Server configs are used to specify the configuration of TiKV Servers.
tikv_servers:
  # # The ip address of the TiKV Server.
  - host: 192.168.1.95
    # # SSH port of the server.
    # ssh_port: 22
    # # TiKV Server communication port.
    # port: 20160
    # # TiKV Server status API port.
    # status_port: 20180
    # # TiKV Server deployment file, startup script, configuration file storage directory.
    # deploy_dir: "/tidb-deploy/tikv-20160"
    # # TiKV Server data storage directory.
    # data_dir: "/tidb-data/tikv-20160"
    # # TiKV Server log file storage directory.
    # log_dir: "/tidb-deploy/tikv-20160/log"
    # # The following configs are used to overwrite the `server_configs.tikv` values.
    # config:
    #   log.level: warn
  # # The ip address of the TiKV Server.
  - host: 192.168.1.96
    # ssh_port: 22
    # port: 20160
    # status_port: 20180
    # deploy_dir: "/tidb-deploy/tikv-20160"
    # data_dir: "/tidb-data/tikv-20160"
    # log_dir: "/tidb-deploy/tikv-20160/log"
    # config:
    #   log.level: warn
  - host: 192.168.1.97
    # ssh_port: 22
    # port: 20160
    # status_port: 20180
    # deploy_dir: "/tidb-deploy/tikv-20160"
    # data_dir: "/tidb-data/tikv-20160"
    # log_dir: "/tidb-deploy/tikv-20160/log"
    # config:
    #   log.level: warn

# # Server configs are used to specify the configuration of Prometheus Server.  
monitoring_servers:
  # # The ip address of the Monitoring Server.
  - host: 192.168.1.98
    # # SSH port of the server.
    # ssh_port: 22
    # # Prometheus Service communication port.
    # port: 9090
    # # ng-monitoring servive communication port
    # ng_port: 12020
    # # Prometheus deployment file, startup script, configuration file storage directory.
    # deploy_dir: "/tidb-deploy/prometheus-8249"
    # # Prometheus data storage directory.
    # data_dir: "/tidb-data/prometheus-8249"
    # # Prometheus log file storage directory.
    # log_dir: "/tidb-deploy/prometheus-8249/log"

# # Server configs are used to specify the configuration of Grafana Servers.  
grafana_servers:
  # # The ip address of the Grafana Server.
  - host: 192.168.1.98
    # # Grafana web port (browser access)
    # port: 3000
    # # Grafana deployment file, startup script, configuration file storage directory.
    # deploy_dir: /tidb-deploy/grafana-3000

# # Server configs are used to specify the configuration of Alertmanager Servers.  
alertmanager_servers:
  # # The ip address of the Alertmanager Server.
  - host: 192.168.1.98
    # # SSH port of the server.
    # ssh_port: 22
    # # Alertmanager web service port.
    # web_port: 9093
    # # Alertmanager communication port.
    # cluster_port: 9094
    # # Alertmanager deployment file, startup script, configuration file storage directory.
    # deploy_dir: "/tidb-deploy/alertmanager-9093"
    # # Alertmanager data storage directory.
    # data_dir: "/tidb-data/alertmanager-9093"
    # # Alertmanager log file storage directory.
    # log_dir: "/tidb-deploy/alertmanager-9093/log"

```

更多参数说明，请参考：

- [TiDB `config.toml.example`](https://github.com/pingcap/tidb/blob/release-8.5/pkg/config/config.toml.example)
- [TiKV `config.toml.example`](https://github.com/tikv/tikv/blob/release-8.5/etc/config-template.toml)
- [PD `config.toml.example`](https://github.com/tikv/pd/blob/release-8.5/conf/config.toml)
- [TiFlash `config.toml.example`](https://github.com/pingcap/tiflash/blob/release-8.5/etc/config-template.toml)

#### 执行部署命令

>注意
>
>通过 TiUP 部署集群时用于初始化的用户（通过 `--user` 指定），可以使用密钥或者交互密码的方式进行安全认证：
>
>- 如果使用密钥方式，可以通过 `-i` 或者 `--identity_file` 指定密钥的路径。
>- 如果使用密码方式，可以通过 `-p` 进入密码交互窗口。
>- 如果已经配置免密登录目标机，则不需填写认证。
>
>TiUP 用于实际执行相关进程的用户和组（通过 `topology.yaml` 指定，默认值为 `tidb`），一般情况下会在目标机器上自动创建，但以下情况例外：
>
>- `topology.yaml` 中设置的用户名在目标机器上已存在。
>- 在命令行上使用了参数 `--skip-create-user` 明确指定跳过创建用户的步骤。
>
>无论 `topology.yaml` 中约定的用户和组是否被自动创建，TiUP 都会自动生成一对 ssh key，并为每台机器的该用户设置免密登录。在此后的操作中都会使用这个用户和 ssh key 去管理机器，而用于初始化的用户和密码在部属完成后不再被使用。

执行部署命令前，先使用 `check` 及 `check --apply` 命令检查和自动修复集群存在的潜在风险：

1. 检查集群存在的潜在风险：

   ```bash
   tiup cluster check ./topology.yaml --user root [-p] [-i /home/root/.ssh/gcp_rsa]
   ```

2. 自动修复集群存在的潜在风险：

   ```bash
   tiup cluster check ./topology.yaml --apply --user root [-p] [-i /home/root/.ssh/gcp_rsa]
   ```

   不能自动修复集群存在的潜在风险请参考以下：

   - `numactl not usable, bash: numactl: command not found`

     ```bash
     sudo apt install numactl
     ```

   - `THP is enabled, please disable it for best performance`

     通过`systemd`服务。

     创建服务文件：

     ```bash
     sudo vim /etc/systemd/system/disable-thp.service
     ```

     添加以下内容：

     ```properties
     [Unit]
     Description=Disable Transparent Huge Pages (THP)
     
     [Service]
     Type=simple
     ExecStart=/bin/sh -c "echo 'never' > /sys/kernel/mm/transparent_hugepage/enabled && echo 'never' > /sys/kernel/mm/transparent_hugepage/defrag"
     
     [Install]
     WantedBy=multi-user.target
     ```

     保存文件后，重新加载`systemd`守护进程并启用服务：

     ```bash
     sudo systemctl daemon-reload
     sudo systemctl enable disable-thp.service
     sudo systemctl start disable-thp.service
     ```

     验证 THP 状态

     ```bash
     cat /sys/kernel/mm/transparent_hugepage/enabled
     cat /sys/kernel/mm/transparent_hugepage/defrag
     ```

     - 确保输出为`[never]`，表示THP已成功禁用。

   - `mount point / does not have 'nodelalloc' option set`

     要永久禁用延迟分配，需要编辑 `/etc/fstab` 文件，修改根文件系统的挂载选项。

     ```bash
     sudo vim /etc/fstab
     ```

     找到根文件系统的行，并添加 `nodelalloc` 选项。例如，如果您的挂载行如下：

     ```
     UUID=1234-5678 / ext4 defaults 0 1
     ```

     修改为：

     ```plaintext
     UUID=1234-5678 / ext4 defaults,nodelalloc 0 1
     ```

     重启操作系统。

     

3. 部署 TiDB 集群：

   ```bash
   tiup cluster deploy tidb-test v8.5.1 ./topology.yaml --user root [-p] [-i /home/root/.ssh/gcp_rsa]
   ```

以上部署示例中：

- `tidb-test` 为部署的集群名称。
- `v8.5.1` 为部署的集群版本，可以通过执行 `tiup list tidb` 来查看 TiUP 支持的最新可用版本。
- 初始化配置文件为 `topology.yaml`。
- `--user root` 表示通过 root 用户登录到目标主机完成集群部署，该用户需要有 ssh 到目标机器的权限，并且在目标机器有 sudo 权限。也可以用其他有 ssh 和 sudo 权限的用户完成部署。
- [-i] 及 [-p] 为可选项，如果已经配置免密登录目标机，则不需填写。否则选择其一即可，[-i] 为可登录到目标机的 root 用户（或 `--user` 指定的其他用户）的私钥，也可使用 [-p] 交互式输入该用户的密码。

预期日志结尾输出 `Deployed cluster `tidb-test` successfully` 关键词，表示部署成功。

#### 查看 TiUP 管理的集群情况

```bash
tiup cluster list
```

TiUP 支持管理多个 TiDB 集群，该命令会输出当前通过 TiUP cluster 管理的所有集群信息，包括集群名称、部署用户、版本、密钥信息等。

#### 检查部署的 TiDB 集群情况

例如，执行如下命令检查 `tidb-test` 集群情况：

```bash
tiup cluster display tidb-test
```

预期输出包括 `tidb-test` 集群中实例 ID、角色、主机、监听端口和状态（由于还未启动，所以状态为 Down/inactive）、目录信息。

#### 启动集群

安全启动是 TiUP cluster 从 v1.9.0 起引入的一种新的启动方式，采用该方式启动数据库可以提高数据库安全性。推荐使用安全启动。

安全启动后，TiUP 会自动生成 TiDB root 用户的密码，并在命令行界面返回密码。

>注意
>
>- 使用安全启动方式后，不能通过无密码的 root 用户登录数据库，你需要记录命令行返回的密码进行后续操作。
>- 该自动生成的密码只会返回一次，如果没有记录或者忘记该密码，请参照[忘记 root 密码](https://docs.pingcap.com/zh/tidb/stable/user-account-management/#忘记-root-密码)修改密码。

##### 安全启动

```bash
tiup cluster start tidb-test --init
```

预期结果如下，表示启动成功。

```bash
Started cluster `tidb-test` successfully.
The root password of TiDB database has been changed.
The new password is: 'y_+3Hwp=*AWz8971s6'.
Copy and record it to somewhere safe, it is only displayed once, and will not be stored.
The generated password can NOT be got again in future.
```

##### 普通启动

```bash
tiup cluster start tidb-test
```

预期结果输出 `Started cluster `tidb-test` successfully`，表示启动成功。使用普通启动方式后，可通过无密码的 root 用户登录数据库。

参考本站 <a href="/tidb/README.html#修改-root-密码" target="_blank">链接</a> 修改 root 密码。

#### 验证集群运行状态

```bash
tiup cluster display tidb-test
```

预期结果输出：各节点 Status 状态信息为 `Up` 说明集群状态正常。

访问 Grafana 监控页面：[http://{grafana-ip}:3000](http://{grafana-ip}:3000/)，默认用户名和密码均为 `admin`。

访问集群 [TiDB Dashboard](https://docs.pingcap.com/zh/tidb/stable/dashboard-intro/) 监控页面：http://{pd-ip}:2379/dashboard，默认用户名为 `root`，密码为修改后的 root 密码。



## 优化



### TiDB 配置

升高日志级别，可以减少打印日志数量，对 TiDB 的性能有积极影响。具体在 TiUP 配置文件（topology.yaml 文件）中加入：

```yaml
server_configs:
  tidb:
    log.level: "error"
```

参考本站 <a href="/tidb/README.html#修改配置参数" target="_blank">链接</a> 修改配置。



## 在线修改集群配置

>[参考官方文档](https://docs.pingcap.com/zh/tidb/stable/dynamic-config/#%E5%9C%A8%E7%BA%BF%E4%BF%AE%E6%94%B9%E9%9B%86%E7%BE%A4%E9%85%8D%E7%BD%AE)

在线配置变更主要是通过利用 SQL 对包括 TiDB、TiKV 以及 PD 在内的各组件的配置进行在线更新。用户可以通过在线配置变更对各组件进行性能调优而无需重启集群组件。但目前在线修改 TiDB 实例配置的方式和修改其他组件 (TiKV, PD) 的有所不同。



### 查看实例配置

可以通过 SQL 语句 `show config` 来直接查看集群所有实例的配置信息，结果如下：

```sql
show config;
```

还可以根据对应的字段进行过滤，如：

```sql
show config where type='tidb'
show config where instance in (...)
show config where name like '%log%'
show config where type='tikv' and name='log.level'
```



### 在线修改 TiKV 配置

>注意：在线修改 TiKV 配置项后，同时会自动修改 TiKV 的配置文件。但还需要使用 `tiup edit-config` 命令来修改对应的配置项，否则 `upgrade` 和 `reload` 等运维操作会将在线修改配置后的结果覆盖。修改配置的操作请参考：[使用 TiUP 修改配置](https://docs.pingcap.com/zh/tidb/stable/maintain-tidb-using-tiup/#修改配置参数)。执行 `tiup edit-config` 后不需要执行 `tiup reload` 操作。

执行 SQL 语句 `set config`，可以结合实例地址或组件类型来修改单个实例配置或全部实例配置，如：

- 修改全部 TiKV 实例配置：

  >注意：建议使用反引号包裹变量名称。

  ```sql
  set config tikv `split.qps-threshold`=1000
  ```

- 修改单个 TiKV 实例配置：

  ```sql
  set config "127.0.0.1:20180" `split.qps-threshold`=1000
  ```

设置成功会返回 `Query OK`：

```sql
Query OK, 0 rows affected (0.01 sec)
```

在批量修改时如果有错误发生，会以 warning 的形式返回：

```sql
set config tikv `log-level`='warn';
```

```sql
Query OK, 0 rows affected, 1 warning (0.04 sec)
```

```sql
show warnings;
```

```sql
+---------+------+---------------------------------------------------------------------------------------------------------------+
| Level   | Code | Message                                                                                                       |
+---------+------+---------------------------------------------------------------------------------------------------------------+
| Warning | 1105 | bad request to http://127.0.0.1:20180/config: fail to update, error: "config log-level can not be changed" |
+---------+------+---------------------------------------------------------------------------------------------------------------+
1 row in set (0.00 sec)
```

批量修改配置不保证原子性，可能出现某些实例成功，而某些失败的情况。如使用 `set tikv key=val` 命令修改整个 TiKV 集群配置时，可能有部分实例失败，请执行 `show warnings` 进行查看。

如遇到部分修改失败的情况，需要重新执行对应的修改语句，或通过修改单个实例的方式完成修改。如果因网络或者机器故障等原因无法访问到的 TiKV，需要等到恢复后再次进行修改。

针对 TiKV 可在线修改的配置项，如果成功修改后，修改的结果会被持久化到配置文件中，后续以配置文件中的配置为准。某些配置项名称可能和 TiDB 预留关键字冲突，如 `limit`、`key` 等，对于此类配置项，需要用反引号 ``` 包裹起来，如 ``raftstore.raft-log-gc-size-limit``。

支持的配置项列表如下：

| 配置项                                                   | 简介                                                         |
| :------------------------------------------------------- | :----------------------------------------------------------- |
| log.level                                                | 日志等级                                                     |
| raftstore.raft-max-inflight-msgs                         | 待确认的日志个数，如果超过这个数量，Raft 状态机会减缓发送日志的速度 |
| raftstore.raft-log-gc-tick-interval                      | 删除 Raft 日志的轮询任务调度间隔时间                         |
| raftstore.raft-log-gc-threshold                          | 允许残余的 Raft 日志个数，软限制                             |
| raftstore.raft-log-gc-count-limit                        | 允许残余的 Raft 日志个数，硬限制                             |
| raftstore.raft-log-gc-size-limit                         | 允许残余的 Raft 日志大小，硬限制                             |
| raftstore.raft-max-size-per-msg                          | 允许生成的单个消息包的大小，软限制                           |
| raftstore.raft-entry-max-size                            | 单个 Raft 日志最大大小，硬限制                               |
| raftstore.raft-entry-cache-life-time                     | 内存中日志 cache 允许的最长残留时间                          |
| raftstore.max-apply-unpersisted-log-limit                | 允许 apply 已 commit 但尚未持久化的 Raft 日志的最大数量      |
| raftstore.split-region-check-tick-interval               | 检查 Region 是否需要分裂的时间间隔                           |
| raftstore.region-split-check-diff                        | 允许 Region 数据超过指定大小的最大值                         |
| raftstore.region-compact-check-interval                  | 检查是否需要人工触发 RocksDB compaction 的时间间隔           |
| raftstore.region-compact-check-step                      | 每轮校验人工 compaction 时，一次性检查的 Region 个数         |
| raftstore.region-compact-min-tombstones                  | 触发 RocksDB compaction 需要的 tombstone 个数                |
| raftstore.region-compact-tombstones-percent              | 触发 RocksDB compaction 需要的 tombstone 所占比例            |
| raftstore.pd-heartbeat-tick-interval                     | 触发 Region 对 PD 心跳的时间间隔                             |
| raftstore.pd-store-heartbeat-tick-interval               | 触发 store 对 PD 心跳的时间间隔                              |
| raftstore.snap-mgr-gc-tick-interval                      | 触发回收过期 snapshot 文件的时间间隔                         |
| raftstore.snap-gc-timeout                                | snapshot 文件的最长保存时间                                  |
| raftstore.lock-cf-compact-interval                       | 触发对 lock CF compact 检查的时间间隔                        |
| raftstore.lock-cf-compact-bytes-threshold                | 触发对 lock CF 进行 compact 的大小                           |
| raftstore.messages-per-tick                              | 每轮处理的消息最大个数                                       |
| raftstore.max-peer-down-duration                         | 副本允许的最长未响应时间                                     |
| raftstore.max-leader-missing-duration                    | 允许副本处于无主状态的最长时间，超过将会向 PD 校验自己是否已经被删除 |
| raftstore.abnormal-leader-missing-duration               | 允许副本处于无主状态的时间，超过将视为异常，标记在 metrics 和日志中 |
| raftstore.peer-stale-state-check-interval                | 触发检验副本是否处于无主状态的时间间隔                       |
| raftstore.consistency-check-interval                     | 触发一致性检查的时间间隔（不建议使用该配置项，因为与 TiDB GC 操作不兼容） |
| raftstore.raft-store-max-leader-lease                    | Region 主可信任期的最长时间                                  |
| raftstore.merge-check-tick-interval                      | 触发 Merge 完成检查的时间间隔                                |
| raftstore.cleanup-import-sst-interval                    | 触发检查过期 SST 文件的时间间隔                              |
| raftstore.local-read-batch-size                          | 一轮处理读请求的最大个数                                     |
| raftstore.apply-yield-write-size                         | Apply 线程每一轮处理单个状态机写入的最大数据量               |
| raftstore.hibernate-timeout                              | 启动后进入静默状态前需要等待的最短时间，在该时间段内不会进入静默状态（未 release） |
| raftstore.apply-pool-size                                | 处理把数据落盘至磁盘的线程池中线程的数量，即 Apply 线程池大小 |
| raftstore.store-pool-size                                | 处理 Raft 的线程池中线程的数量，即 Raftstore 线程池的大小    |
| raftstore.apply-max-batch-size                           | Raft 状态机由 BatchSystem 批量执行数据写入请求，该配置项指定每批可执行请求的最多 Raft 状态机个数。 |
| raftstore.store-max-batch-size                           | Raft 状态机由 BatchSystem 批量执行把日志落盘至磁盘的请求，该配置项指定每批可执行请求的最多 Raft 状态机个数。 |
| raftstore.store-io-pool-size                             | 处理 Raft I/O 任务的线程池中线程的数量，即 StoreWriter 线程池的大小（不支持将该配置项由非零值调整为 0，或者从 0 调整为非零值） |
| raftstore.periodic-full-compact-start-max-cpu            | 控制 TiKV 执行周期性全量数据整理时的 CPU 使用率阈值          |
| readpool.unified.max-thread-count                        | 统一处理读请求的线程池最多的线程数量，即 UnifyReadPool 线程池大小 |
| readpool.unified.max-tasks-per-worker                    | 统一处理读请求的线程池中单个线程允许积压的最大任务数量，超出后会返回 Server Is Busy。 |
| readpool.unified.auto-adjust-pool-size                   | 是否开启自适应调整 UnifyReadPool 的大小                      |
| resource-control.priority-ctl-strategy                   | 配置低优先级任务的流量管控策略。                             |
| coprocessor.split-region-on-table                        | 开启按 table 分裂 Region 的开关                              |
| coprocessor.batch-split-limit                            | 批量分裂 Region 的阈值                                       |
| coprocessor.region-max-size                              | Region 容量空间的最大值                                      |
| coprocessor.region-split-size                            | 分裂后新 Region 的大小                                       |
| coprocessor.region-max-keys                              | Region 最多允许的 key 的个数                                 |
| coprocessor.region-split-keys                            | 分裂后新 Region 的 key 的个数                                |
| pessimistic-txn.wait-for-lock-timeout                    | 悲观事务遇到锁后的最长等待时间                               |
| pessimistic-txn.wake-up-delay-duration                   | 悲观事务被重新唤醒的时间                                     |
| pessimistic-txn.pipelined                                | 是否开启流水线式加悲观锁流程                                 |
| pessimistic-txn.in-memory                                | 是否开启内存悲观锁功能                                       |
| pessimistic-txn.in-memory-peer-size-limit                | 控制单个 Region 内存悲观锁的内存使用上限                     |
| pessimistic-txn.in-memory-instance-size-limit            | 控制单个 TiKV 实例内存悲观锁的内存使用上限                   |
| quota.foreground-cpu-time                                | 限制处理 TiKV 前台读写请求所使用的 CPU 资源使用量，软限制    |
| quota.foreground-write-bandwidth                         | 限制前台事务写入的带宽，软限制                               |
| quota.foreground-read-bandwidth                          | 限制前台事务读取数据和 Coprocessor 读取数据的带宽，软限制    |
| quota.background-cpu-time                                | 限制处理 TiKV 后台读写请求所使用的 CPU 资源使用量，软限制    |
| quota.background-write-bandwidth                         | 限制后台事务写入的带宽，软限制，暂未生效                     |
| quota.background-read-bandwidth                          | 限制后台事务读取数据和 Coprocessor 读取数据的带宽，软限制，暂未生效 |
| quota.enable-auto-tune                                   | 是否支持 quota 动态调整。如果打开该配置项，TiKV 会根据 TiKV 实例的负载情况动态调整对后台请求的限制 quota |
| quota.max-delay-duration                                 | 单次读写请求被强制等待的最大时间                             |
| gc.ratio-threshold                                       | 跳过 Region GC 的阈值（GC 版本个数/key 个数）                |
| gc.batch-keys                                            | 一轮处理 key 的个数                                          |
| gc.max-write-bytes-per-sec                               | 一秒可写入 RocksDB 的最大字节数                              |
| gc.enable-compaction-filter                              | 是否使用 compaction filter                                   |
| gc.compaction-filter-skip-version-check                  | 是否跳过 compaction filter 的集群版本检查（未 release）      |
| {db-name}.max-total-wal-size                             | WAL 总大小限制                                               |
| {db-name}.max-background-jobs                            | RocksDB 后台线程个数                                         |
| {db-name}.max-background-flushes                         | RocksDB flush 线程个数                                       |
| {db-name}.max-open-files                                 | RocksDB 可以打开的文件总数                                   |
| {db-name}.compaction-readahead-size                      | Compaction 时候 readahead 的大小                             |
| {db-name}.bytes-per-sync                                 | 异步同步的限速速率                                           |
| {db-name}.wal-bytes-per-sync                             | WAL 同步的限速速率                                           |
| {db-name}.writable-file-max-buffer-size                  | WritableFileWrite 所使用的最大的 buffer 大小                 |
| {db-name}.{cf-name}.block-cache-size                     | block cache size 大小                                        |
| {db-name}.{cf-name}.write-buffer-size                    | memtable 大小                                                |
| {db-name}.{cf-name}.max-write-buffer-number              | 最大 memtable 个数                                           |
| {db-name}.{cf-name}.max-bytes-for-level-base             | base level (L1) 最大字节数                                   |
| {db-name}.{cf-name}.target-file-size-base                | base level 的目标文件大小                                    |
| {db-name}.{cf-name}.level0-file-num-compaction-trigger   | 触发 compaction 的 L0 文件最大个数                           |
| {db-name}.{cf-name}.level0-slowdown-writes-trigger       | 触发 write stall 的 L0 文件最大个数                          |
| {db-name}.{cf-name}.level0-stop-writes-trigger           | 完全阻停写入的 L0 文件最大个数                               |
| {db-name}.{cf-name}.max-compaction-bytes                 | 一次 compaction 最大写入字节数                               |
| {db-name}.{cf-name}.max-bytes-for-level-multiplier       | 每一层的默认放大倍数                                         |
| {db-name}.{cf-name}.disable-auto-compactions             | 自动 compaction 的开关                                       |
| {db-name}.{cf-name}.soft-pending-compaction-bytes-limit  | pending compaction bytes 的软限制                            |
| {db-name}.{cf-name}.hard-pending-compaction-bytes-limit  | pending compaction bytes 的硬限制                            |
| {db-name}.{cf-name}.titan.blob-run-mode                  | 处理 blob 文件的模式                                         |
| {db-name}.{cf-name}.titan.min-blob-size                  | 数据存储在 Titan 的阈值，当数据的 value 达到该阈值时将存储在 Titan 的 Blob 文件中 |
| {db-name}.{cf-name}.titan.blob-file-compression          | Titan 的 Blob 文件所使用的压缩算法                           |
| {db-name}.{cf-name}.titan.discardable-ratio              | Titan 数据文件 GC 的垃圾数据比例阈值，当一个 Blob 文件中无用数据的比例超过该阈值时将会触发 Titan GC |
| server.grpc-memory-pool-quota                            | gRPC 可使用的内存大小限制                                    |
| server.max-grpc-send-msg-len                             | gRPC 可发送的最大消息长度                                    |
| server.raft-msg-max-batch-size                           | 单个 gRPC 消息可包含的最大 Raft 消息个数                     |
| server.simplify-metrics                                  | 精简监控采样数据的开关                                       |
| server.snap-io-max-bytes-per-sec                         | 处理 snapshot 时最大允许使用的磁盘带宽                       |
| server.concurrent-send-snap-limit                        | 同时发送 snapshot 的最大个数                                 |
| server.concurrent-recv-snap-limit                        | 同时接受 snapshot 的最大个数                                 |
| storage.block-cache.capacity                             | 共享 block cache 的大小（自 v4.0.3 起支持）                  |
| storage.flow-control.enable                              | 是否开启流量控制机制                                         |
| storage.flow-control.memtables-threshold                 | 触发流量控制的 KvDB memtable 数量阈值                        |
| storage.flow-control.l0-files-threshold                  | 触发流量控制的 KvDB L0 文件数量阈值                          |
| storage.flow-control.soft-pending-compaction-bytes-limit | 触发流控机制开始拒绝部分写入请求的 KvDB pending compaction bytes 阈值 |
| storage.flow-control.hard-pending-compaction-bytes-limit | 触发流控机制拒绝所有新写入请求的 KvDB pending compaction bytes 阈值 |
| storage.scheduler-worker-pool-size                       | Scheduler 线程池中线程的数量                                 |
| import.num-threads                                       | 处理恢复或导入 RPC 请求的线程数量（自 v8.1.2 起支持在线修改） |
| backup.num-threads                                       | backup 线程的数量（自 v4.0.3 起支持）                        |
| split.qps-threshold                                      | 对 Region 执行 load-base-split 的阈值。如果连续 10s 内，某个 Region 的读请求的 QPS 超过 qps-threshold，则尝试切分该 Region |
| split.byte-threshold                                     | 对 Region 执行 load-base-split 的阈值。如果连续 10s 内，某个 Region 的读请求的流量超过 byte-threshold，则尝试切分该 Region |
| split.region-cpu-overload-threshold-ratio                | 对 Region 执行 load-base-split 的阈值。如果连续 10s 内，某个 Region 的 Unified Read Pool CPU 使用时间占比超过了 region-cpu-overload-threshold-ratio，则尝试切分该 Region（自 v6.2.0 起支持） |
| split.split-balance-score                                | load-base-split 的控制参数，确保 Region 切分后左右访问尽量均匀，数值越小越均匀，但也可能导致无法切分 |
| split.split-contained-score                              | load-base-split 的控制参数，数值越小，Region 切分后跨 Region 的访问越少 |
| cdc.min-ts-interval                                      | 定期推进 Resolved TS 的时间间隔                              |
| cdc.old-value-cache-memory-quota                         | 缓存在内存中的 TiCDC Old Value 的条目占用内存的上限          |
| cdc.sink-memory-quota                                    | 缓存在内存中的 TiCDC 数据变更事件占用内存的上限              |
| cdc.incremental-scan-speed-limit                         | 增量扫描历史数据的速度上限                                   |
| cdc.incremental-scan-concurrency                         | 增量扫描历史数据任务的最大并发执行个数                       |

上述前缀为 `{db-name}` 或 `{db-name}.{cf-name}` 的是 RocksDB 相关的配置项。`db-name` 的取值可为 `rocksdb` 或 `raftdb`。

- 当 `db-name` 为 `rocksdb` 时，`cf-name` 的可取值有：`defaultcf`、`writecf`、`lockcf`、`raftcf`；
- 当 `db-name` 为 `raftdb` 时，`cf-name` 的可取值有：`defaultcf`。

具体配置项的意义可参考 [TiKV 配置文件描述](https://docs.pingcap.com/zh/tidb/stable/tikv-configuration-file/)



### 在线修改 PD 配置

PD 暂不支持单个实例拥有独立配置。所有实例共享一份配置，可以通过下列方式修改 PD 的配置项：

```sql
set config pd `log.level`='info'
```

设置成功会返回 `Query OK`：

```sql
Query OK, 0 rows affected (0.01 sec)
```

针对 PD 可在线修改的配置项，成功修改后则会持久化到 etcd 中，不会对配置文件进行持久化，后续以 etcd 中的配置为准。同上，若和 TiDB 预留关键字冲突，需要用反引号 ``` 包裹此类配置项，例如 ``schedule.leader-schedule-limit``。

支持配置项列表如下：

| 配置项                                   | 简介                                                        |
| :--------------------------------------- | :---------------------------------------------------------- |
| log.level                                | 日志级别                                                    |
| cluster-version                          | 集群的版本                                                  |
| schedule.max-merge-region-size           | 控制 Region Merge 的 size 上限（单位是 MiB）                |
| schedule.max-merge-region-keys           | 控制 Region Merge 的 key 数量上限                           |
| schedule.patrol-region-interval          | 控制 checker 检查 Region 健康状态的运行频率                 |
| scheduler.patrol-region-worker-count     | 控制 checker 检查 Region 健康状态时，创建 operator 的并发数 |
| schedule.split-merge-interval            | 控制对同一个 Region 做 split 和 merge 操作的间隔            |
| schedule.max-snapshot-count              | 控制单个 store 最多同时接收或发送的 snapshot 数量           |
| schedule.max-pending-peer-count          | 控制单个 store 的 pending peer 上限                         |
| schedule.max-store-down-time             | PD 认为失联 store 无法恢复的时间                            |
| schedule.leader-schedule-policy          | 用于控制 leader 调度的策略                                  |
| schedule.leader-schedule-limit           | 可以控制同时进行 leader 调度的任务个数                      |
| schedule.region-schedule-limit           | 可以控制同时进行 Region 调度的任务个数                      |
| schedule.replica-schedule-limit          | 可以控制同时进行 replica 调度的任务个数                     |
| schedule.merge-schedule-limit            | 控制同时进行的 Region Merge 调度的任务                      |
| schedule.hot-region-schedule-limit       | 可以控制同时进行的热点调度的任务个数                        |
| schedule.hot-region-cache-hits-threshold | 用于设置 Region 被视为热点的阈值                            |
| schedule.high-space-ratio                | 用于设置 store 空间充裕的阈值                               |
| schedule.low-space-ratio                 | 用于设置 store 空间不足的阈值                               |
| schedule.tolerant-size-ratio             | 控制 balance 缓冲区大小                                     |
| schedule.enable-remove-down-replica      | 用于开启自动删除 DownReplica 的特性                         |
| schedule.enable-replace-offline-replica  | 用于开启迁移 OfflineReplica 的特性                          |
| schedule.enable-make-up-replica          | 用于开启补充副本的特性                                      |
| schedule.enable-remove-extra-replica     | 用于开启删除多余副本的特性                                  |
| schedule.enable-location-replacement     | 用于开启隔离级别检查                                        |
| schedule.enable-cross-table-merge        | 用于开启跨表 Merge                                          |
| schedule.enable-one-way-merge            | 用于开启单向 Merge（只允许和下一个相邻的 Region Merge）     |
| replication.max-replicas                 | 用于设置副本的数量                                          |
| replication.location-labels              | 用于设置 TiKV 集群的拓扑信息                                |
| replication.enable-placement-rules       | 开启 Placement Rules                                        |
| replication.strictly-match-label         | 开启 label 检查                                             |
| pd-server.use-region-storage             | 开启独立的 Region 存储                                      |
| pd-server.max-gap-reset-ts               | 用于设置最大的重置 timestamp 的间隔（BR）                   |
| pd-server.key-type                       | 用于设置集群 key 的类型                                     |
| pd-server.metric-storage                 | 用于设置集群 metrics 的存储地址                             |
| pd-server.dashboard-address              | 用于设置 dashboard 的地址                                   |
| replication-mode.replication-mode        | 备份的模式                                                  |

具体配置项意义可参考 [PD 配置文件描述](https://docs.pingcap.com/zh/tidb/stable/pd-configuration-file/)。



### 在线修改 TiDB 配置

在线修改 TiDB 配置的方式和 TiKV/PD 有所不同，你可以通过修改 [系统变量](https://docs.pingcap.com/zh/tidb/stable/system-variables/) 来实现。

下面例子展示了如何通过变量 `tidb_slow_log_threshold` 在线修改配置项 `slow-threshold`。

`slow-threshold` 默认值是 300 毫秒，可以通过设置系统变量 `tidb_slow_log_threshold` 将其修改为 200 毫秒：

```sql
set tidb_slow_log_threshold = 200;
Query OK, 0 rows affected (0.00 sec)
select @@tidb_slow_log_threshold;
+---------------------------+
| @@tidb_slow_log_threshold |
+---------------------------+
| 200                       |
+---------------------------+
1 row in set (0.00 sec)
```

支持在线修改的配置项和相应的 TiDB 系统变量如下：

| 配置项                                                | 对应变量                                   | 简介                                                         |
| :---------------------------------------------------- | :----------------------------------------- | :----------------------------------------------------------- |
| instance.tidb_enable_slow_log                         | tidb_enable_slow_log                       | 慢日志的开关                                                 |
| instance.tidb_slow_log_threshold                      | tidb_slow_log_threshold                    | 慢日志阈值                                                   |
| instance.tidb_expensive_query_time_threshold          | tidb_expensive_query_time_threshold        | expensive 查询阈值                                           |
| instance.tidb_enable_collect_execution_info           | tidb_enable_collect_execution_info         | 控制是否记录各个算子的执行信息                               |
| instance.tidb_record_plan_in_slow_log                 | tidb_record_plan_in_slow_log               | 控制是否在慢日志中记录执行计划                               |
| instance.tidb_force_priority                          | tidb_force_priority                        | 该 TiDB 实例的语句优先级                                     |
| instance.max_connections                              | max_connections                            | 该 TiDB 实例同时允许的最大客户端连接数                       |
| instance.tidb_enable_ddl                              | tidb_enable_ddl                            | 控制该 TiDB 实例是否可以成为 DDL owner                       |
| pessimistic-txn.constraint-check-in-place-pessimistic | tidb_constraint_check_in_place_pessimistic | 控制悲观事务中唯一约束检查是否会被推迟到下一次对该唯一索引加锁时或事务提交时才进行 |



### 在线修改 TiFlash 配置

目前，你可以通过修改系统变量 [`tidb_max_tiflash_threads`](https://docs.pingcap.com/zh/tidb/stable/system-variables/#tidb_max_tiflash_threads-从-v610-版本开始引入) 来在线修改 TiFlash 配置项 `max_threads`。`tidb_max_tiflash_threads` 表示 TiFlash 中 request 执行的最大并发度。

`tidb_max_tiflash_threads` 默认值是 `-1`，表示此系统变量无效，由 TiFlash 的配置文件决定 max_threads。你可以通过设置系统变量 `tidb_max_tiflash_threads` 将其修改为 10：

```sql
set tidb_max_tiflash_threads = 10;
Query OK, 0 rows affected (0.00 sec)
select @@tidb_max_tiflash_threads;
+----------------------------+
| @@tidb_max_tiflash_threads |
+----------------------------+
| 10                         |
+----------------------------+
1 row in set (0.00 sec)
```



## 集群运维

>[官方参考文档](https://docs.pingcap.com/zh/tidb/stable/maintain-tidb-using-tiup/#tiup-%E5%B8%B8%E8%A7%81%E8%BF%90%E7%BB%B4%E6%93%8D%E4%BD%9C)

本文介绍了使用 TiUP 运维 TiDB 集群的常见操作，包括查看集群列表、启动集群、查看集群状态、修改配置参数、关闭集群、销毁集群等。

### 查看集群列表

TiUP cluster 组件可以用来管理多个 TiDB 集群，在每个 TiDB 集群部署完毕后，该集群会出现在 TiUP 的集群列表里，可以使用 list 命令来查看。

```bash
tiup cluster list
```

### 启动集群

启动集群操作会按 PD -> TiKV -> TiDB -> TiFlash -> TiCDC -> Prometheus -> Grafana -> Alertmanager 的顺序启动整个 TiDB 集群所有组件：

```bash
tiup cluster start ${cluster-name}
```

>注意：你需要将 `${cluster-name}` 替换成实际的集群名字，若忘记集群名字，可通过 `tiup cluster list` 查看。

该命令支持通过 `-R` 和 `-N` 参数来只启动部分组件。

例如，下列命令只启动 PD 组件：

```bash
tiup cluster start ${cluster-name} -R pd
```

下列命令只启动 `1.2.3.4` 和 `1.2.3.5` 这两台机器上的 PD 组件：

```bash
tiup cluster start ${cluster-name} -N 1.2.3.4:2379,1.2.3.5:2379
```

>注意：若通过 `-R` 和 `-N` 启动指定组件，需要保证启动顺序正确（例如需要先启动 PD 才能启动 TiKV），否则可能导致启动失败。

### 查看集群状态

todo

### 修改配置参数

集群运行过程中，如果需要调整某个组件的参数，可以使用 `edit-config` 命令来编辑参数。具体的操作步骤如下：

1. 以编辑模式打开该集群的配置文件：

   ```bash
   tiup cluster edit-config ${cluster-name}
   ```

2. 设置参数：

   首先确定配置的生效范围，有以下两种生效范围：

   - 如果配置的生效范围为该组件全局，则配置到 `server_configs`。例如：

     ```plaintext
     server_configs:
       tidb:
         log.slow-threshold: 300
     ```

   - 如果配置的生效范围为某个节点，则配置到具体节点的 `config` 中。例如：

     ```plaintext
     tidb_servers:
     - host: 10.0.1.11
       port: 4000
       config:
           log.slow-threshold: 300
     ```

   参数的格式参考 [TiUP 配置参数模版](https://github.com/pingcap/tiup/blob/master/embed/examples/cluster/topology.example.yaml)。

   **配置项层次结构使用 `.` 表示**。

   关于组件的更多配置参数说明，可参考 [tidb `config.toml.example`](https://github.com/pingcap/tidb/blob/release-8.5/pkg/config/config.toml.example)、[tikv `config.toml.example`](https://github.com/tikv/tikv/blob/release-8.5/etc/config-template.toml) 和 [pd `config.toml.example`](https://github.com/tikv/pd/blob/release-8.5/conf/config.toml)。

3. 执行 `reload` 命令滚动分发配置、重启相应组件：

   ```bash
   tiup cluster reload ${cluster-name} [-N <nodes>] [-R <roles>]
   ```

#### 示例

如果要调整 tidb-server 中事务大小限制参数 `txn-total-size-limit` 为 `1G`，该参数位于 [performance](https://github.com/pingcap/tidb/blob/release-8.5/pkg/config/config.toml.example) 模块下，调整后的配置如下：

```plaintext
server_configs:
  tidb:
    performance.txn-total-size-limit: 1073741824
```

然后执行 `tiup cluster reload ${cluster-name} -R tidb` 命令滚动重启。

### Hotfix 版本替换

常规的升级集群请参考[升级文档](https://docs.pingcap.com/zh/tidb/stable/upgrade-tidb-using-tiup/)，但是在某些场景下（例如 Debug），可能需要用一个临时的包替换正在运行的组件，此时可以用 `patch` 命令：

```bash
tiup cluster patch --help
Replace the remote package with a specified package and restart the service

Usage:
  tiup cluster patch <cluster-name> <package-path> [flags]

Flags:
  -h, --help                   帮助信息
  -N, --node strings           指定被替换的节点
      --overwrite              在未来的 scale-out 操作中使用当前指定的临时包
  -R, --role strings           指定被替换的服务类型
      --transfer-timeout int   transfer leader 的超时时间

Global Flags:
      --native-ssh        使用系统默认的 SSH 客户端
      --wait-timeout int  等待操作超时的时间
      --ssh-timeout int   SSH 连接的超时时间
  -y, --yes               跳过所有的确认步骤
```

例如，有一个 TiDB 实例的 hotfix 包放在 `/tmp/tidb-hotfix.tar.gz` 目录下。如果此时想要替换集群上的所有 TiDB 实例，则可以执行以下命令：

```bash
tiup cluster patch test-cluster /tmp/tidb-hotfix.tar.gz -R tidb
```

或者只替换其中一个 TiDB 实例：

```bash
tiup cluster patch test-cluster /tmp/tidb-hotfix.tar.gz -N 172.16.4.5:4000
```

### 删除 TiDB、TiProxy 节点

>[参考官方文档](https://docs.pingcap.com/zh/tidb/stable/tiup-component-cluster-scale-in/)

查看集群 test1 节点

```bash
tiup cluster display test1
```

删除 ID 为 `192.168.1.87:4000` 的 TiDB 节点

```bash
tiup cluster scale-in test1 -N 192.168.1.87:4000
```

删除 ID 为 `192.168.1.88:6000` 的 TiProxy 节点

```bash
tiup cluster scale-in test1 -N 192.168.1.88:6000
```

### 添加 TiDB 节点

把部署集群时的 topology.yaml 配置文件中的 global、server_configs、monitored 复制到 add-tidb.yaml 配置文件中，例如：

```yaml
# # Global variables are applied to all deployments and used as the default value of
# # the deployments if a specific deployment value is missing.
global:
  # # The user who runs the tidb cluster.
  user: "tidb"
  # # group is used to specify the group name the user belong to if it's not the same as user.
  # group: "tidb"
  # # systemd_mode is used to select whether to use sudo permissions. When its value is set to user, there is no need to add global.user to sudoers. The default value is system.
  # systemd_mode: "system"
  # # SSH port of servers in the managed cluster.
  ssh_port: 22
  # # Storage directory for cluster deployment files, startup scripts, and configuration files.
  deploy_dir: "/tidb-deploy"
  # # TiDB Cluster data storage directory
  data_dir: "/tidb-data"
  # # default listen_host for all components
  listen_host: 0.0.0.0
  # # Supported values: "amd64", "arm64" (default: "amd64")
  arch: "amd64"
  # # Resource Control is used to limit the resource of an instance.
  # # See: https://www.freedesktop.org/software/systemd/man/systemd.resource-control.html
  # # Supports using instance-level `resource_control` to override global `resource_control`.
  # resource_control:
  #   # See: https://www.freedesktop.org/software/systemd/man/systemd.resource-control.html#MemoryLimit=bytes
  #   memory_limit: "2G"
  #   # See: https://www.freedesktop.org/software/systemd/man/systemd.resource-control.html#CPUQuota=
  #   # The percentage specifies how much CPU time the unit shall get at maximum, relative to the total CPU time available on one CPU. Use values > 100% for allotting CPU time on more than one CPU.
  #   # Example: CPUQuota=200% ensures that the executed processes will never get more than two CPU time.
  #   cpu_quota: "200%"
  #   # See: https://www.freedesktop.org/software/systemd/man/systemd.resource-control.html#IOReadBandwidthMax=device%20bytes
  #   io_read_bandwidth_max: "/dev/disk/by-path/pci-0000:00:1f.2-scsi-0:0:0:0 100M"
  #   io_write_bandwidth_max: "/dev/disk/by-path/pci-0000:00:1f.2-scsi-0:0:0:0 100M"

server_configs:
  tidb:
    log.level: "error"
    graceful-wait-before-shutdown: 15
  tikv:
    log-level: "error"
  pd:
    log-level: "error"

# # Monitored variables are applied to all the machines.
monitored:
  # # The communication port for reporting system information of each node in the TiDB cluster.
  node_exporter_port: 9100
  # # Blackbox_exporter communication port, used for TiDB cluster port monitoring.
  blackbox_exporter_port: 9115
  # # Storage directory for deployment files, startup scripts, and configuration files of monitoring components.
  # deploy_dir: "/tidb-deploy/monitored-9100"
  # # Data storage directory of monitoring components.
  # data_dir: "/tidb-data/monitored-9100"
  # # Log storage directory of the monitoring component.
  # log_dir: "/tidb-deploy/monitored-9100/log"

# # Server configs are used to specify the runtime configuration of TiDB components.
# # All configuration items can be found in TiDB docs:
# # - TiDB: https://docs.pingcap.com/tidb/stable/tidb-configuration-file
# # - TiKV: https://docs.pingcap.com/tidb/stable/tikv-configuration-file
# # - PD: https://docs.pingcap.com/tidb/stable/pd-configuration-file
# # - TiFlash: https://docs.pingcap.com/tidb/stable/tiflash-configuration
# #
# # All configuration items use points to represent the hierarchy, e.g:
# #   readpool.storage.use-unified-pool
# #           ^       ^
# # - example: https://github.com/pingcap/tiup/blob/master/examples/topology.example.yaml.
# # You can overwrite this configuration via the instance-level `config` field.
# server_configs:
  # tidb:
  # tikv:
  # pd:
  # tiflash:
  # tiflash-learner:
  #
  #
```

在 add-tidb.yaml 配置文件中再追加新的 TiDB 节点信息

```yaml
# # Server configs are used to specify the configuration of TiDB Servers.
tidb_servers:
  # # The ip address of the TiDB Server.
  - host: 192.168.1.87
    # # SSH port of the server.
    # ssh_port: 22
    # # The port for clients to access the TiDB cluster.
    # port: 4000
    # # TiDB Server status API port.
    # status_port: 10080
    # # TiDB Server deployment file, startup script, configuration file storage directory.
    # deploy_dir: "/tidb-deploy/tidb-4000"
    # # TiDB Server log file storage directory.
    # log_dir: "/tidb-deploy/tidb-4000/log"
```

使用 scale-out 命令部署 TiDB 新节点

```bash
tiup cluster scale-out test1 add-tidb.yaml -uroot -p
```

### 添加 TiProxy 节点

>[参考官方文档](https://docs.pingcap.com/zh/tidb/stable/tiproxy-overview/#%E4%B8%BA%E5%B7%B2%E6%9C%89%E9%9B%86%E7%BE%A4%E5%90%AF%E7%94%A8-tiproxy)

把部署集群时的 topology.yaml 配置文件中的 global、server_configs、monitored 复制到 add-tiproxy.yaml 配置文件中，例如：

```yaml
# # Global variables are applied to all deployments and used as the default value of
# # the deployments if a specific deployment value is missing.
global:
  # # The user who runs the tidb cluster.
  user: "tidb"
  # # group is used to specify the group name the user belong to if it's not the same as user.
  # group: "tidb"
  # # systemd_mode is used to select whether to use sudo permissions. When its value is set to user, there is no need to add global.user to sudoers. The default value is system.
  # systemd_mode: "system"
  # # SSH port of servers in the managed cluster.
  ssh_port: 22
  # # Storage directory for cluster deployment files, startup scripts, and configuration files.
  deploy_dir: "/tidb-deploy"
  # # TiDB Cluster data storage directory
  data_dir: "/tidb-data"
  # # default listen_host for all components
  listen_host: 0.0.0.0
  # # Supported values: "amd64", "arm64" (default: "amd64")
  arch: "amd64"
  # # Resource Control is used to limit the resource of an instance.
  # # See: https://www.freedesktop.org/software/systemd/man/systemd.resource-control.html
  # # Supports using instance-level `resource_control` to override global `resource_control`.
  # resource_control:
  #   # See: https://www.freedesktop.org/software/systemd/man/systemd.resource-control.html#MemoryLimit=bytes
  #   memory_limit: "2G"
  #   # See: https://www.freedesktop.org/software/systemd/man/systemd.resource-control.html#CPUQuota=
  #   # The percentage specifies how much CPU time the unit shall get at maximum, relative to the total CPU time available on one CPU. Use values > 100% for allotting CPU time on more than one CPU.
  #   # Example: CPUQuota=200% ensures that the executed processes will never get more than two CPU time.
  #   cpu_quota: "200%"
  #   # See: https://www.freedesktop.org/software/systemd/man/systemd.resource-control.html#IOReadBandwidthMax=device%20bytes
  #   io_read_bandwidth_max: "/dev/disk/by-path/pci-0000:00:1f.2-scsi-0:0:0:0 100M"
  #   io_write_bandwidth_max: "/dev/disk/by-path/pci-0000:00:1f.2-scsi-0:0:0:0 100M"

server_configs:
  tidb:
    log.level: "error"
    graceful-wait-before-shutdown: 15
  tikv:
    log-level: "error"
  pd:
    log-level: "error"

# # Monitored variables are applied to all the machines.
monitored:
  # # The communication port for reporting system information of each node in the TiDB cluster.
  node_exporter_port: 9100
  # # Blackbox_exporter communication port, used for TiDB cluster port monitoring.
  blackbox_exporter_port: 9115
  # # Storage directory for deployment files, startup scripts, and configuration files of monitoring components.
  # deploy_dir: "/tidb-deploy/monitored-9100"
  # # Data storage directory of monitoring components.
  # data_dir: "/tidb-data/monitored-9100"
  # # Log storage directory of the monitoring component.
  # log_dir: "/tidb-deploy/monitored-9100/log"

# # Server configs are used to specify the runtime configuration of TiDB components.
# # All configuration items can be found in TiDB docs:
# # - TiDB: https://docs.pingcap.com/tidb/stable/tidb-configuration-file
# # - TiKV: https://docs.pingcap.com/tidb/stable/tikv-configuration-file
# # - PD: https://docs.pingcap.com/tidb/stable/pd-configuration-file
# # - TiFlash: https://docs.pingcap.com/tidb/stable/tiflash-configuration
# #
# # All configuration items use points to represent the hierarchy, e.g:
# #   readpool.storage.use-unified-pool
# #           ^       ^
# # - example: https://github.com/pingcap/tiup/blob/master/examples/topology.example.yaml.
# # You can overwrite this configuration via the instance-level `config` field.
# server_configs:
  # tidb:
  # tikv:
  # pd:
  # tiflash:
  # tiflash-learner:
  #
  #
```

在 add-tiproxy.yaml 配置文件中再追加新的 TiDB 节点信息

```yaml
tiproxy_servers:
  - host: 192.168.1.88
    deploy_dir: "/tiproxy-deploy"
    port: 6000
    status_port: 3080
```

使用 scale-out 命令部署 TiProxy 新节点

```bash
tiup cluster scale-out test1 add-tiproxy.yaml -uroot -p
```

### 重命名集群

部署并启动集群后，可以通过 `tiup cluster rename` 命令来对集群重命名：

```bash
tiup cluster rename ${cluster-name} ${new-name}
```

>注意
>
>- 重命名集群会重启监控（Prometheus 和 Grafana）。
>- 重命名集群之后 Grafana 可能会残留一些旧集群名的面板，需要手动删除这些面板。

### 关闭集群

关闭集群操作会按 Alertmanager -> Grafana -> Prometheus -> TiCDC -> TiFlash -> TiDB -> TiKV -> PD 的顺序关闭整个 TiDB 集群所有组件（同时也会关闭监控组件）：

```bash
tiup cluster stop ${cluster-name}
```

和 `start` 命令类似，`stop` 命令也支持通过 `-R` 和 `-N` 参数来只停止部分组件。

例如，下列命令只停止 TiDB 组件：

```bash
tiup cluster stop ${cluster-name} -R tidb
```

下列命令只停止 `1.2.3.4` 和 `1.2.3.5` 这两台机器上的 TiDB 组件：

```bash
tiup cluster stop ${cluster-name} -N 1.2.3.4:4000,1.2.3.5:4000
```

### 清除集群数据

此操作会关闭所有服务，并清空其数据目录或/和日志目录，并且无法恢复，需要**谨慎操作**。

清空集群所有服务的数据，但保留日志：

```bash
tiup cluster clean ${cluster-name} --data
```

清空集群所有服务的日志，但保留数据：

```bash
tiup cluster clean ${cluster-name} --log
```

清空集群所有服务的数据和日志：

```bash
tiup cluster clean ${cluster-name} --all
```

清空 Prometheus 以外的所有服务的日志和数据：

```bash
tiup cluster clean ${cluster-name} --all --ignore-role prometheus
```

清空节点 `172.16.13.11:9000` 以外的所有服务的日志和数据：

```bash
tiup cluster clean ${cluster-name} --all --ignore-node 172.16.13.11:9000
```

清空部署在 `172.16.13.12` 以外的所有服务的日志和数据：

```bash
tiup cluster clean ${cluster-name} --all --ignore-node 172.16.13.12
```

### 销毁集群

销毁集群操作会关闭服务，清空数据目录和部署目录，并且无法恢复，需要**谨慎操作**。

```bash
tiup cluster destroy ${cluster-name}
```



### 扩缩容

TiDB 集群可以在不中断线上服务的情况下进行扩容和缩容。

本文介绍如何使用 TiUP 扩容缩容集群中的 TiDB、TiKV、PD、TiCDC 或者 TiFlash 节点。

你可以通过 `tiup cluster list` 查看当前的集群名称列表。

#### 扩容 TiDB/PD/TiKV 节点

如果要添加一个 TiDB 节点，IP 地址为 10.0.1.5，可以按照如下步骤进行操作。

>注意：添加 PD 节点和添加 TiDB 节点的步骤类似。添加 TiKV 节点前，建议预先根据集群的负载情况调整 PD 调度参数。

1. 编写扩容拓扑配置

   >注意
   >
   >- 默认情况下，可以不填写端口以及目录信息。但在单机多实例场景下，则需要分配不同的端口以及目录，如果有端口或目录冲突，会在部署或扩容时提醒。
   >- 从 TiUP v1.0.0 开始，扩容配置会继承原集群配置的 global 部分。

   在 scale-out.yml 文件添加扩容拓扑配置：

   TiDB 配置文件参考：

   ```yaml
   tidb_servers:
     - host: 10.0.1.5
       ssh_port: 22
       port: 4000
       status_port: 10080
       deploy_dir: /tidb-deploy/tidb-4000
       log_dir: /tidb-deploy/tidb-4000/log
   ```

   TiKV 配置文件参考：

   ```yaml
   tikv_servers:
     - host: 10.0.1.5
       ssh_port: 22
       port: 20160
       status_port: 20180
       deploy_dir: /tidb-deploy/tikv-20160
       data_dir: /tidb-data/tikv-20160
       log_dir: /tidb-deploy/tikv-20160/log
   ```

   PD 配置文件参考：

   ```ini
   pd_servers:
     - host: 10.0.1.5
       ssh_port: 22
       name: pd-1
       client_port: 2379
       peer_port: 2380
       deploy_dir: /tidb-deploy/pd-2379
       data_dir: /tidb-data/pd-2379
       log_dir: /tidb-deploy/pd-2379/log
   ```

   可以使用 `tiup cluster edit-config <cluster-name>` 查看当前集群的配置信息，因为其中的 `global` 和 `server_configs` 参数配置默认会被 `scale-out.yml` 继承，因此也会在 `scale-out.yml` 中生效。

2. 执行扩容命令

   执行 scale-out 命令前，先使用 `check` 及 `check --apply` 命令，检查和自动修复集群存在的潜在风险：

   >注意：针对 scale-out 命令的检查功能在 tiup cluster v1.9.3 及后续版本中支持，请操作前先升级 tiup cluster 版本。

   （1）检查集群存在的潜在风险：

   ```sh
   tiup cluster check <cluster-name> scale-out.yml --cluster --user root [-p] [-i /home/root/.ssh/gcp_rsa]
   ```

   （2）自动修复集群存在的潜在风险：

   ```sh
   tiup cluster check <cluster-name> scale-out.yml --cluster --apply --user root [-p] [-i /home/root/.ssh/gcp_rsa]
   ```

   （3）执行 scale-out 命令扩容 TiDB 集群：

   ```sh
   tiup cluster scale-out <cluster-name> scale-out.yml [-p] [-i /home/root/.ssh/gcp_rsa]
   ```

   以上操作示例中：

   - 扩容配置文件为 `scale-out.yml`。
   - `--user root` 表示通过 root 用户登录到目标主机完成集群部署，该用户需要有 ssh 到目标机器的权限，并且在目标机器有 sudo 权限。也可以用其他有 ssh 和 sudo 权限的用户完成部署。
   - [-i] 及 [-p] 为可选项，如果已经配置免密登录目标机，则不需填写。否则选择其一即可，[-i] 为可登录到目标机的 root 用户（或 --user 指定的其他用户）的私钥，也可使用 [-p] 交互式输入该用户的密码。

   预期日志结尾输出 `Scaled cluster `<cluster-name>` out successfully` 信息，表示扩容操作成功。

3. 刷新集群配置

   >注意
   >
   >- 刷新集群配置仅适用于扩容 PD 节点，扩容 TiDB 或 TiKV 节点时无需执行此操作。
   >- 如果你使用的是 TiUP v1.15.0 或之后版本，请跳过该操作，因为 TiUP 会完成相应操作；如果你使用的是 TiUP v1.15.0 之前的版本，则需执行以下步骤。

   （1）更新集群配置：

   ```sh
   tiup cluster reload <cluster-name> --skip-restart
   ```

   （2）更新 Prometheus 配置并重启：

   ```sh
   tiup cluster reload <cluster-name> -R prometheus
   ```

4. 查看集群状态

   ```sh
   tiup cluster display <cluster-name>
   ```

   打开浏览器访问监控平台 [http://10.0.1.5:3000](http://10.0.1.5:3000/)，监控整个集群和新增节点的状态。

#### 缩容 TiDB/PD/TiKV 节点

如果要移除 IP 地址为 10.0.1.5 的一个 TiKV 节点，可以按照如下步骤进行操作。

>注意
>
>- 移除 TiDB、PD 节点和移除 TiKV 节点的步骤类似。
>- 由于 TiKV 和 TiFlash 组件是异步下线的，且下线过程耗时较长，所以 TiUP 对 TiKV 和 TiFlash 组件做了特殊处理，详情参考 [下线特殊处理](https://docs.pingcap.com/zh/tidb/stable/tiup-component-cluster-scale-in/#下线特殊处理)。
>- TiKV 中的 PD Client 会缓存 PD 节点的列表。当前版本的 TiKV 有定期自动更新 PD 节点的机制，可以降低 TiKV 缓存的 PD 节点列表过旧这一问题出现的概率。但你应尽量避免在扩容新 PD 后直接一次性缩容所有扩容前就已经存在的 PD 节点。如果需要，请确保在下线所有之前存在的 PD 节点前将 PD 的 leader 切换至新扩容的 PD 节点。

1. 查看节点 ID 信息

   ```sh
   tiup cluster display <cluster-name>
   ```

2. 执行缩容操作

   ```sh
   tiup cluster scale-in <cluster-name> --node 10.0.1.5:20160
   ```

   其中 `--node` 参数为需要下线节点的 ID。

   预期输出 Scaled cluster `<cluster-name>` in successfully 信息，表示缩容操作成功。

3. 刷新集群配置

   >注意
   >
   >- 刷新集群配置仅适用于缩容 PD 节点，缩容 TiDB 或 TiKV 节点时无需执行此操作。
   >- 如果你使用的是 TiUP v1.15.0 或之后版本，请跳过该操作，因为 TiUP 会完成相应操作；如果你使用的是 TiUP v1.15.0 之前的版本，则需执行以下步骤。

   （1）更新集群配置：

   ```sh
   tiup cluster reload <cluster-name> --skip-restart
   ```

   （2）更新 Prometheus 配置并重启：

   ```sh
   tiup cluster reload <cluster-name> -R prometheus
   ```

4. 查看集群状态

   下线需要一定时间，下线节点的状态变为 Tombstone 就说明下线成功。

   执行如下命令检查节点是否下线成功：

   ```sh
   tiup cluster display <cluster-name>
   ```

   打开浏览器访问监控平台 [http://10.0.1.5:3000](http://10.0.1.5:3000/)，监控整个集群的状态。

5. 清理 Tombstone 节点

   由于 TiKV 和 TiFlash 组件的下线是异步的（需要先通过 API 执行移除操作）并且下线过程耗时较长（需要持续观察节点是否已经下线成功），所以对 TiKV 和 TiFlash 组件做了特殊处理：

   - 对 TiKV 和 TiFlash 组件的操作
     - tiup-cluster 通过 API 将其下线后直接退出而不等待下线完成
     - 执行 `tiup cluster display` 查看下线节点的状态，等待其状态变为 Tombstone
     - 执行 `tiup cluster prune` 命令清理 Tombstone 节点，该命令会执行以下操作：
       - 停止已经下线掉的节点的服务
       - 清理已经下线掉的节点的相关数据文件
       - 更新集群的拓扑，移除已经下线掉的节点
   - 对其他组件的操作
     - 下线 PD 组件时，会通过 API 将指定节点从集群中删除掉（这个过程很快），然后停掉指定 PD 的服务并且清除该节点的相关数据文件
     - 下线其他组件时，直接停止并且清除节点的相关数据文件



## 修改 root 密码

使用 MySQL 客户端连接到 TiDB 并修改密码

```sql
ALTER USER 'root'@'%' IDENTIFIED BY '123456';
FLUSH PRIVILEGES;
```



## tiup 命令



### 查看当前支持部署的所有 TiDB 版本

```bash
tiup list tidb
```



### 使用 TiUP `client` 连接 TiDB

```
tiup client
```



### 清理所有通过 TiUP 安装的组件及其相关数据

>彻底删除 TiUP 管理的所有组件（如 TiDB、PD、TiKV、TiDB Dashboard 等）及其运行时产生的数据（如日志、临时文件等）。

```bash
tiup clean --all
```



## tiup cluster 命令



### 部署集群

```bash
tiup cluster deploy <cluster-name> <version> ./topo.yaml --user root -p
```

- 参数 `<cluster-name>` 表示设置集群名称
- 参数 `<version>` 表示设置集群版本，例如 `v8.5.1`。可以通过 `tiup list tidb` 命令来查看当前支持部署的 TiDB 版本
- 参数 `--user` 表示初始化环境的用户
- 参数 `-p` 表示在连接目标机器时使用密码登录

>注意：如果主机通过密钥进行 SSH 认证，请使用 `-i` 参数指定密钥文件路径，`-i` 与 `-p` 不可同时使用。

按照引导，输入”y”及 root 密码，来完成部署：

```bash
Do you want to continue? [y/N]:  y
Input SSH password:
```



### 启动集群

```bash
tiup cluster start <cluster-name>
```



### 显示已经部署的集群列表

```bash
tiup cluster list
```



### 查看集群的拓扑结构和状态

```bash
tiup cluster display <cluster-name>
```



### 停止集群

```bash
tiup cluster stop <cluster-name>
```



### 删除集群所有数据，但不删除集群

```bash
tiup cluster clean <cluster-name> --all
```



### 删除集群

```bash
tiup cluster destroy <cluster-name>
```



## 基准测试

### 测试配置

使用谷歌 GCE E2 实例测试。E2 实例配置如下：

| **组件** | **CPU** | **内存** | **硬盘类型** | **网络**         | **实例数量** |
| :------- | :------ | :------- | :----------- | :--------------- | :----------- |
| TiProxy  | 8 核    | 6 GB     | SSD          | 万兆网卡（1 块） | 1            |
| TiDB     | 8 核    | 8 GB     | SSD          | 万兆网卡（1 块） | 3            |
| PD       | 4 核    | 8 GB     | SSD          | 万兆网卡（1 块） | 1            |
| TiKV     | 8 核    | 32 GB    | SSD          | 万兆网卡（1 块） | 3            |
| 监控     | 4 核    | 8 GB     | SSD          | 万兆网卡（1 块） | 1            |



### 部署

参考本站 <a href="/tidb/README.html#部署生产环境集群" target="_blank">链接</a> 部署集群。



