# 容器运行`redis`

> redis官方镜像`https://hub.docker.com/_/redis?tab=tags`



## 配置选项

>redis 配置文件为 redis.conf

### protected-mode

在Redis的配置文件中，`protected-mode` 是一个安全相关的设置。当 `protected-mode` 设置为 `yes`（默认值）时，Redis 会采取一系列措施来防止外部未经授权的访问。这通常包括检查是否仅通过本地接口（如127.0.0.1）或者通过配置文件中明确指定的接口和端口来监听连接请求，以及是否需要密码验证。

如果将 `protected-mode` 设置为 `no`，Redis 将关闭这些额外的安全检查。这意味着，只要Redis服务器的端口（默认是6379）在防火墙或网络层面是对外开放的，任何能够访问该端口的客户端都可以连接到Redis服务器，而无需进行密码验证或满足特定的网络接口要求。

**注意**：

- 将 `protected-mode` 设置为 `no` 会降低Redis服务器的安全性，因为它允许未经授权的访问。
- 在生产环境中，强烈建议保持 `protected-mode` 为 `yes`，并通过配置密码（`requirepass` 配置项）和/或限制监听接口（`bind` 配置项）来增强安全性。
- 如果你确实需要将 `protected-mode` 设置为 `no`（例如，在受信任的内部网络环境中），请确保其他安全措施（如防火墙规则、网络隔离等）已经到位，以防止未经授权的访问。



### port

指定 redis 监听端口，例如：port 6379 表示指定 redis 监听端口为 6379



### daemonize

在Redis的配置中，`daemonize` 是一个关键选项，用于控制Redis服务器是否以守护进程（Daemon）的方式运行。当设置为 `no` 时，具有特定的含义和影响：

一、含义

- **前台运行**：当 `daemonize` 设置为 `no` 时，Redis服务器将以前台进程的方式运行，而不是作为后台守护进程。这意味着Redis将占用启动它的终端，并在该终端中显示其输出和日志信息。

二、影响

1. **交互性**：
   - 由于Redis在前台运行，它将占用启动它的终端。因此，一旦启动Redis，用户将无法在同一个终端中执行其他命令，直到手动停止Redis服务或关闭终端。
2. **管理**：
   - 以非守护进程方式运行的Redis更容易直接监控和管理，因为所有的输出（包括日志和错误信息）都会直接显示在终端上。这有助于开发者和管理员快速发现和解决问题。
   - 另一方面，由于Redis占用终端，可能需要编写脚本来自动化启动、停止和重启Redis服务。
3. **自动化**：
   - 在自动化部署和管理场景中，通常更倾向于使用守护进程方式运行Redis（即 `daemonize` 设置为 `yes`），以便能够更容易地集成到脚本和监控系统中。
4. **性能**：
   - `daemonize` 设置为 `no` 对Redis本身的性能影响较小。主要的影响在于Redis的运行方式，即是否占用终端。

三、使用场景

- **开发和调试环境**：在开发和调试Redis应用时，通常希望Redis在前台运行，以便能够看到输出和错误信息。这有助于快速定位和解决问题。
- **临时测试**：在进行临时测试或快速验证Redis功能时，也可以将 `daemonize` 设置为 `no`，以便快速启动和停止Redis服务。

四、配置方法

- 要设置 `daemonize` 为 `no`，可以编辑Redis的配置文件（通常是 `redis.conf`），找到 `daemonize` 行，并将其值修改为 `no`。然后，重新启动Redis服务以使更改生效。

综上所述，`daemonize no` 在Redis配置中用于控制Redis以前台进程的方式运行，这主要影响Redis的交互性、管理方式和自动化部署场景。在开发和调试环境中，这种设置可能很有用，但在生产环境中，通常更倾向于使用守护进程方式运行Redis。



### pidfile

以 daemonize yes 模式运行 redis 时，redis pid 将被写入到 pidfile 指定的文件中，例如：pidfile /var/run/redis_6379.pid



### logfile

在Redis的配置文件中，`logfile` 参数用于指定日志文件的路径和名称。如果 `logfile` 被设置为空字符串（`""`），这意味着Redis不会将日志信息写入到任何文件中。以下是对此设置的详细解释：

一、含义

- **无日志文件**：当 `logfile` 设置为空字符串时，Redis将不会生成日志文件。所有的日志信息将不会被持久化到磁盘上，而是可能通过标准输出（stdout）或标准错误（stderr）输出（这取决于Redis的运行方式和操作系统的配置）。

二、影响

1. **日志记录**：
   - 由于没有指定日志文件，Redis将无法将重要的操作信息（如启动和关闭事件、配置更改、错误和警告信息、关键命令的执行状态等）记录到文件中。
   - 这可能导致在故障排查和性能分析时缺乏必要的日志信息。
2. **故障排查**：
   - 在没有日志文件的情况下，开发者和运维人员可能更难定位和解决问题。日志文件通常包含有关系统状态、错误和警告的详细信息，这些信息对于故障排查至关重要。
3. **安全性**：
   - 在某些情况下，不记录日志可能被视为一种安全措施，因为它减少了潜在的敏感信息泄露的风险。然而，这也可能使得系统更难被监控和审计。
4. **性能**：
   - 不记录日志可能会对Redis的性能产生轻微的影响（通常是正面的），因为避免了将日志信息写入磁盘的操作。然而，这种影响通常是非常小的，并且不太可能在大多数应用场景中显著改变Redis的性能。

三、配置方法

- 要更改 `logfile` 的设置，可以编辑Redis的配置文件（通常是 `redis.conf`），找到 `logfile` 行，并为其指定一个有效的路径和文件名。例如：

```bash
logfile /var/log/redis/redis-server.log
```

- 确保Redis服务进程有权限写入指定的日志文件。如有必要，可以调整相应的权限。
- 重新启动Redis服务以使更改生效。

四、注意事项

- 在决定不记录日志之前，请仔细考虑其潜在的影响，并确保你了解这样做可能带来的风险。
- 如果你需要记录日志以便进行故障排查或性能分析，请确保 `logfile` 参数被正确设置，并且Redis服务进程有权限写入指定的日志文件。

综上所述，将 `logfile` 设置为空字符串将导致Redis不记录任何日志信息。这可能会影响到故障排查、性能分析和系统监控等方面。因此，在配置Redis时，请仔细考虑是否需要记录日志，并相应地设置 `logfile` 参数。



### cluster-enabled

在Redis的配置中，`cluster-enabled` 参数用于指定Redis节点是否以集群模式运行。当 `cluster-enabled` 设置为 `yes` 时，意味着该Redis节点将作为集群的一部分来运行。以下是对此设置的详细解释：

一、含义

- **集群模式**：`cluster-enabled yes` 表示启用Redis的集群模式。在这种模式下，Redis节点将与其他节点协同工作，共同处理数据请求，以实现数据的高可用性和可扩展性。

二、影响

1. **节点通信**：
   - 在集群模式下，Redis节点之间会通过内部通信机制（如Gossip协议）来交换状态信息、发现新节点、处理故障转移等。
   - 这需要节点之间能够相互通信，通常是通过内部网络或局域网来实现的。
2. **数据分片**：
   - Redis集群会将数据分成多个槽（slot），并将这些槽分布到不同的节点上。
   - 当客户端请求数据时，集群会根据键的哈希值来确定它属于哪个槽，并将请求路由到负责该槽的节点上。
3. **高可用性和故障转移**：
   - Redis集群提供了高可用性和故障转移机制。如果某个节点出现故障，集群中的其他节点将接管其负责的数据槽，并继续处理请求。
   - 这需要集群中的节点能够感知到其他节点的状态变化，并自动进行故障转移。
4. **配置和管理**：
   - 在集群模式下，Redis的配置和管理变得更加复杂。需要为每个节点指定集群配置文件、节点超时时间等参数。
   - 此外，还需要使用Redis CLI或其他管理工具来管理集群（如添加或删除节点、查看集群状态等）。

三、配置方法

- 要启用Redis的集群模式，可以在Redis的配置文件（通常是 `redis.conf`）中找到 `cluster-enabled` 行，并将其值设置为 `yes`。
- 同时，还需要配置其他与集群相关的参数，如 `cluster-config-file`（指定集群配置文件的路径和名称）、`cluster-node-timeout`（指定节点超时时间）等。
- 确保所有集群节点的配置文件都正确设置，并启动所有节点以形成集群。

四、注意事项

- 在启用集群模式之前，请确保你已经了解了Redis集群的基本原理和配置方法。
- 确保所有集群节点都能够相互通信，并且网络延迟和带宽足够支持集群的正常运行。
- 定期检查集群的状态和性能，以及时发现和解决问题。

综上所述，`cluster-enabled yes` 是启用Redis集群模式的关键配置。在配置和使用Redis集群时，请务必仔细考虑和设置相关参数，以确保集群的正常运行和高可用性。



### 单机版配置样板文件

`https://gitee.com/dexterleslie/demonstration/blob/master/demo-redis/redis-server/docker-based/mode-standalone/redis.conf`



## `standalone`模式（单机模式）

复制`https://gitee.com/dexterleslie/demonstration/tree/master/demo-redis/redis-server/docker-based/mode-standalone`相关配置到本地

切换到`mode-standalone`目录内

```bash
cd mode-standalone
```

启动`redis`服务

```bash
docker compose up -d
```

删除`redis`服务

```bash
docker compose down -v
```



## `replication`模式（主从复制模式）

> 示例会启动`demo-redis-replication-node1`容器作为`redis master`节点，`demo-redis-replication-node2`、`demo-redis-replication-node3`、`demo-redis-replication-node4`容器作为`redis slave`节点。
>
> `docker-compose-add-slave-node.yml`用于添加`slave`节点到现有的`replication`集群中。

复制`https://gitee.com/dexterleslie/demonstration/tree/master/demo-redis/redis-server/docker-based/mode-replication`相关配置到本地

切换到`mode-replication`目录内

```bash
cd mode-replication
```

启动`redis`服务

```bash
docker compose up -d
```

查看`redis master`的`replication`状态，显示`role: master`

```bash
docker compose exec -it demo-redis-replication-node1 redis-cli info replication
```

查看`redis slave`的`replication`状态，显示`role: slave`

```bash
docker compose exec -it demo-redis-replication-node2 redis-cli info replication
```

添加`redis slave`到现有的主从集群中，下面命令执行后会启动一个名为`demo-redis-replication-node-extra`容器并自动加入到`demo-redis-replication-node1`主从集群中

```bash
docker compose -f docker-compose-add-slave-node.yml up -d
```

从现有的主从集群中删除`redis slave`

```bash
docker compose -f docker-compose-add-slave-node.yml down -v
```



## `sentinel`模式（哨兵模式）

> `docker-compose-add-slave-node.yml`用于添加`slave`节点到现有的集群中。

复制`https://gitee.com/dexterleslie/demonstration/tree/master/demo-redis/redis-server/docker-based/mode-sentinel`相关配置到本地

切换到`mode-sentinel`目录内

```bash
cd mode-sentinel
```

编辑`.env`文件填写主机ip地址，`.env`内容如下：

```properties
# 宿主机ip
varHostIp=192.168.1.181
```

启动`redis sentinel`服务

```bash
docker compose up -d
```

查看`sentinel`相关状态包括`redis master`节点的信息

```bash
docker compose exec -it demo-redis-sentinel-node1 redis-cli -p 26379 info sentinel
```

删除`redis sentinel`服务

```bash
docker compose down -v
```



## `cluster`模式（集群模式）



### 基于`docker compose`集群

> 使用`docker compose`建立`redis cluster` `https://blog.yowko.com/docker-compose-redis-cluster/`

复制`https://gitee.com/dexterleslie/demonstration/tree/master/demo-redis/redis-server/docker-based/mode-cluster`示例到本地

启动`redis cluster`服务

```bash
# 基于redis6的集群
docker compose -f docker-compose-redis6.yml up -d

# 基于redis7的集群
# 注意：经过多次测试重启集群后容器ip地址变动也不会影响集群ok状态。
docker compose -f docker-compose-redis7.yml up -d
```

查看集群状态信息

```bash
docker compose exec -it node1 redis-cli -p 6380 cluster info
```

查看集群节点信息

```bash
docker compose exec -it node1 redis-cli -p 6380 cluster nodes
```

删除`redis cluster`服务

```bash
docker compose -f docker-compose-redis6.yml down -v

docker compose -f docker-compose-redis7.yml down -v
```



### 基于`docker swarm`集群

>注意：在使用`docker swarm`运行`redis`集群并且没有使用`nfs`存储时，需要绑定`redis`节点到指定的`swarm`节点中，否则在集群重启后会导致集群`down`。在使用`nfs`存储时则理论上不需要绑定`redis`节点到`swarm`节点中（未做实验）。

注意：通过`docker stack rm test1`删除`redis`集群后，需要手动在每个`swarm`节点上删除`redis`节点数据卷（命令`docker volume rm $(docker volume ls | grep cluster-node | awk '{print $2}')`），否则在下次创建集群时会报告错误导致无法创建新的`redis`集群。

复制`https://gitee.com/dexterleslie/demonstration/tree/master/demo-redis/redis-server/docker-based/mode-cluster`示例到本地，其中`docker-stack.yml`文件是`redis`集群在`docker swarm`部署的核心配置。

在`swarm`管理节点上执行以下命令部署`redis`集群

```bash
docker stack deploy test1 -c docker-stack.yaml
```

在`swarm`管理节点上查看`redis`集群状态

```bash
docker exec -it `docker ps |grep node1 | awk '{print $1}'` redis-cli -c cluster info
docker exec -it `docker ps |grep node1 | awk '{print $1}'` redis-cli -c cluster nodes
```

测试`swarm`中的`redis`集群是否丢失数据

```bash
# 生成数据
for i in {1..15}; do docker exec -it `docker ps|grep node1|awk '{print $1}'` redis-cli -c set key$i value$i; done

# 删除service模拟节点失败
docker service rm test1_node1
docker service rm test1_node3
docker service rm test1_node5

# 重建集群
docker stack deploy test1 -c docker-stack.yaml

# 执行以下命令依然能够读取redis集群中的数据，表示redis集群数据不丢失
for i in {1..15}; do v_val=$(docker exec -it $(docker ps|grep node2|awk '{print $1}') redis-cli -c get key$i); echo key$i=$v_val; done
```

或者可以通过示例`https://gitee.com/dexterleslie/demonstration/blob/master/spring-cloud/demo-spring-cloud-assistant/deployer/docker-stack.yaml`测试`swarm`中的`redis`集群是否会丢失数据。



### 基于`k8s`集群

#### 参考资料

参考以下资料基于`k8s`运行各种模式的`redis`

- `https://github.com/bitnami/charts/tree/main/bitnami/redis-cluster`
- `https://github.com/bitnami/charts/tree/main/bitnami/redis`
- `https://github.com/bitnami/containers/tree/main/bitnami/redis-cluster`
- `https://github.com/bitnami/containers/tree/main/bitnami/redis`



#### 运行`redis cluser`模式

> 注意：为了实现`redis-cluster-0`等待其他所有节点准备好并执行`redis-cli --cluster create`逻辑，需要借助`helm`生成`REDIS_NODES`(格式：`redis-cluster-0:6379 redis-cluster-1:6379 redis-cluster-2:6379`)。所以暂时放弃自动创建`redis cluster`方法转而使用人工介入的方式创建`redis cluster`。
>
> 注意：使用`k8s`运行`redis7`（`redis7`在初始化集群时支持不使用节点`ip`）集群，使用`--cluster-announce-ip`参数指定`redis`节点的无头服务名称，即使`k8s`节点重启后`redis`节点`ip`地址变化也不会导致`redis`集群失败。

下载`redis cluster`配置到本地，链接`https://gitee.com/dexterleslie/demonstration/tree/master/demo-redis/redis-server/kubernetes-based/mode-cluster`

给`redis`集群性能测试专用节点加污点和标签

> 注意：在性能测试时，避免`jmeter slave`和`redis`集群在同一个`k8s`节点上运行导致相互干扰，所以需要给`redis`集群专用节点添加污点，标签是用于`redis`集群定向调度到指定`k8s`节点中。

```bash
kubectl taint node k8s-node-openresty support-only-perf-target:NoSchedule
kubectl label node k8s-node-openresty support-only-perf-target=
```

创建并启动所有`redis cluster`节点

```bash
./create-k8s.sh
```

查看`redis statefulset`是否已经启动

```bash
kubectl get statefulset
```

等待所有`redis cluster`节点`running`状态后手动创建`redis`集群

>`https://medium.com/geekculture/redis-cluster-on-kubernetes-c9839f1c14b6`

```bash
# 注意：最新版本redis集群创建可以通过下面的 <pod-name>.<headless-service> 方式创建，不需要使用ip地址
export REDIS_NODES=$(kubectl get pods -l app.kubernetes.io/name=redis-cluster -o json | jq -r '.items | map(.status.podIP) | join(":6379 ")'):6379

kubectl exec -it redis-cluster-0 -- redis-cli --cluster create --cluster-replicas 1 ${REDIS_NODES}

# 或者针对redis7使用下面方式创建
# 注意：最新版本`redis`通过`<pod-name>.<headless-service>`方式创建集群
kubectl exec -it redis-cluster-0 -- redis-cli --cluster create --cluster-replicas 1 redis-cluster-0.redis-cluster-headless:6379 redis-cluster-1.redis-cluster-headless:6379 redis-cluster-2.redis-cluster-headless:6379 redis-cluster-3.redis-cluster-headless:6379 redis-cluster-4.redis-cluster-headless:6379 redis-cluster-5.redis-cluster-headless:6379

# 或者
kubectl exec -it redis-cluster-0 -- redis-cli --cluster create redis-cluster-0.redis-cluster-headless:6379 redis-cluster-1.redis-cluster-headless:6379 redis-cluster-2.redis-cluster-headless:6379 redis-cluster-3.redis-cluster-headless:6379 redis-cluster-4.redis-cluster-headless:6379 redis-cluster-5.redis-cluster-headless:6379
```

删除`redis`集群

```bash
./destroy-k8s.sh
```



#### 测试`redis`集群是否正常

查看集群状态是否正常

```bash
kubectl exec -it redis-cluster-0 -- redis-cli -c cluster info
kubectl exec -it redis-cluster-0 -- redis-cli -c cluster nodes
```

通过`set/get`指令测试集群读写是否正常

```bash
# 测试写
kubectl exec -it redis-cluster-0 -- bash -c "for i in {1..15}; do redis-cli -c set key\$i value\$i 1>/dev/null && echo \"成功set key\$i value\$i\"; done"

# 测试读，能够读取所有key*值说明读正常
kubectl exec -it redis-cluster-0 -- bash -c "for i in {1..15}; do echo \"key\$i=\$(redis-cli -c get key\$i)\"; done"
```

删除若干`redis`集群节点，再次查看数据是否会丢失

```bash
# 删除若干节点
kubectl delete pod redis-cluster-0
kubectl delete pod redis-cluster-1
kubectl delete pod redis-cluster-2

kubectl exec -it redis-cluster-0 -- redis-cli -c cluster info

# 测试读，能够读取所有key*值说明没有丢失数据
kubectl exec -it redis-cluster-0 -- bash -c "for i in {1..15}; do echo \"key\$i=\$(redis-cli -c get key\$i)\"; done"
```

