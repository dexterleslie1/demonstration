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

本指南介绍如何在生产环境中使用 [TiUP](https://github.com/pingcap/tiup) 部署 TiDB 集群。

TiUP 是在 TiDB v4.0 中引入的集群运维工具，提供了使用 Golang 编写的集群管理组件 [TiUP cluster](https://github.com/pingcap/tiup/tree/master/components/cluster)。通过使用 TiUP cluster 组件，你可以轻松执行日常的数据库运维操作，包括部署、启动、关闭、销毁、弹性扩缩容、升级 TiDB 集群，以及管理 TiDB 集群参数。

TiUP 还支持部署 TiDB、TiFlash、TiCDC 以及监控系统。本指南介绍了如何部署不同拓扑的 TiDB 集群。



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

根据注释编辑 topology.yaml 相关内容。

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

#### 验证集群运行状态

```bash
tiup cluster display tidb-test
```

预期结果输出：各节点 Status 状态信息为 `Up` 说明集群状态正常。



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

