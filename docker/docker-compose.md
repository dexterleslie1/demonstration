# `Docker Compose`



## `yaml` 文件指定 `command`

docker-compose.yaml 内容如下：

```yaml
version: "3.0"

services:
  demo-test:
    container_name: demo-test
    image: centos
    command: /bin/sh -c "date > /1.txt && cat /1.txt"

```

启动服务，控制台会输出当前时间

```sh
docker compose up
```



## 仅对单个服务 service 进行操作

仅更新 yyd-websocket-service

```sh
docker compose pull yyd-websocket-service
```



强制重建 yyd-websocket-service，--no-deps 表示依赖的相关容器不会被重建

```sh
docker compose up -d --no-deps --force-recreate yyd-websocket-service
```



## 获取 docker-compose up 返回状态值

> `https://github.com/docker/compose/issues/10225`

```bash
# 在当前目录执行以下命令，不使用--abort-on-container-exit时下面脚本不会执行echo
docker-compose up --abort-on-container-exit || { echo '执行失败'; }
```



## 设置项目名称

> 参考`stackoverflow`[链接](https://stackoverflow.com/questions/44924082/set-project-name-in-docker-compose-file)

1. 通过`docker-compose.yaml`中`name`属性指定项目名称

   - 直接在`docker-compose.yaml`中设置`name`属性

     ```yaml
     version: "3.0"
     
     name: test
     
     services:
       my_busybox:
         image: busybox
         command: |
           sh -c "sleep infinity"
     ```

   - 通过变量的方式设置`name`属性

     `.env`文件如下：

     ```properties
     projectName=test
     ```

     `docker-compose.yaml`文件如下：

     ```yaml
     version: "3.0"
     
     name: ${projectName}
     
     services:
       my_busybox:
         image: busybox
         command: |
           sh -c "sleep infinity"
     ```

     

2. 通过`.env`设置项目名称

   > 推荐使用此方式设置项目名称

   ```properties
   COMPOSE_PROJECT_NAME=mytest
   ```

3. 通过`docker compose`命令`-p`参数设置项目名称

   `up`命令

   ```bash
   docker compose -p mytest1 up -d
   ```

   `ps`命令

   ```bash
   docker compose -p mytest1 ps
   ```

   `down`命令

   ```bash
   docker compose -p mytest1 down -v
   ```

   

## 指定或者扩展服务实例的数量

注意：nginx 服务不能指定端口，否则会端口冲突导致不能启动多个实例。

详细用法请参考示例 `https://gitee.com/dexterleslie/demonstration/tree/master/docker/demo-docker-compose/docker-compose-replicas.yaml`



### yaml 文件指定服务实例的数量

通过 yaml 文件的 deploy.replicas 配置多个实例

```yaml
version: "3.0"

services:
  nginx:
    image: nginx
    deploy:
      # 表示两个实例
      replicas: 2
    # ports:
    #   - '80:80'
```

启动

```bash
docker compose -f docker-compose-replicas.yaml up -d
```

查看实例数量

```bash
docker compose -f docker-compose-replicas.yaml ps
```



### 手动扩展服务实例的数量

yaml 文件和普通的 yaml 文件一致

```yaml
version: "3.0"

services:
  nginx:
    image: nginx
    # ports:
    #   - '80:80'
```

启动

```bash
docker compose -f docker-compose-replicas.yaml up -d
```

手动扩展实例数量

```bash
docker compose -f docker-compose-replicas.yaml up -d --scale nginx=3
```

查看实例数量

```bash
docker compose -f docker-compose-replicas.yaml ps
```



## `compose` 文件配置

### `restart` 策略

>[restart 策略官方参考](https://docs.docker.com/reference/compose-file/services/#restart)

restart 定义平台在容器终止时应用的策略。

- no：默认重启策略。它在任何情况下都不会重启容器。
- always：该策略始终重启容器，直到将其移除。
- on-failure[:max-retries]：如果退出代码指示错误，该策略将重启容器。（可选）限制 Docker 守护程序尝试重启的次数。
- unless-stopped：无论退出代码如何，该策略都会重启容器，但当服务停止或移除时会停止重启。

例如如下配置：

```yaml
restart: "no"
restart: always
restart: on-failure
restart: on-failure:3
restart: unless-stopped
```



## `docker compose up -d` 触发容器重建

在 Docker Compose 中，`docker compose up -d`（后台启动）**是否重建容器取决于当前容器状态与配置文件的匹配程度**。其核心逻辑是：**对比现有容器的配置与 `docker-compose.yml` 中的最新配置，若发现差异则重建容器，否则仅启动或重启已有容器**。


### 一、触发重建的核心条件：配置哈希不匹配
Docker Compose 会为每个服务容器生成一个 **配置哈希值**（存储在容器的标签 `com.docker.compose.config-hash` 中），用于标识当前容器的配置状态。当运行 `docker compose up -d` 时，Compose 会执行以下检查：

1. **计算当前配置的哈希值**：基于 `docker-compose.yml` 中该服务的所有配置（如镜像、端口、环境变量、卷、网络、命令等）生成新的哈希值。  
2. **对比已有容器的哈希值**：读取现有容器的 `com.docker.compose.config-hash` 标签，与当前计算的哈希值比较。  
3. **若哈希不一致**：说明配置已变更，需要重建容器（删除旧容器 → 按新配置创建新容器）。  
4. **若哈希一致**：说明配置未变，仅启动或重启容器（若容器未运行）。  


### 二、具体触发重建的场景
以下是常见的触发 `docker compose up -d` 重建容器的场景：

#### 1. **`docker-compose.yml` 配置变更**  
当 `docker-compose.yml` 中服务的**关键配置**被修改时，会触发哈希值变化，从而重建容器。典型场景包括：  
- **镜像变更**：修改了 `image` 字段（如从 `myapp:v1` 改为 `myapp:v2`），或拉取了同名镜像的新版本（需注意：若镜像标签是 `latest`，手动拉取新版本后，Compose 可能不会自动检测，需显式触发）。  
- **容器运行参数变更**：修改了 `ports`（端口映射）、`volumes`（卷挂载）、`environment`（环境变量）、`command`（启动命令）、`restart`（重启策略）等。  
- **依赖资源变更**：关联的 `networks`（网络）或 `secrets`（密钥）配置被修改（如网络驱动类型变化）。  

**示例**：  
原 `docker-compose.yml` 中 `ports` 为 `"8080:80"`，修改为 `"8081:80"` 后，哈希值变化，运行 `up -d` 会重建容器并应用新端口映射。


#### 2. **显式强制重建（`--force-recreate` 标志）**  
即使 `docker-compose.yml` 配置未变，通过添加 `--force-recreate` 标志，可强制 Compose 重建容器（无论哈希是否匹配）。  
**命令示例**：  
```bash
docker compose up -d --force-recreate
```
此场景适用于需要**强制刷新容器状态**（如清除容器内临时数据、应用未通过卷持久化的配置变更）的情况。


#### 3. **容器不存在或状态异常**  
- **首次启动**：若服务对应的容器尚未创建（如首次运行 `docker compose up -d`），Compose 会直接创建新容器。  
- **容器被手动删除**：若用户通过 `docker rm` 手动删除了容器，再次运行 `up -d` 时，Compose 会根据配置重新创建容器。  


#### 4. **镜像更新未通过 `image` 字段显式声明**  
若 `docker-compose.yml` 中使用 `build` 构建镜像（而非 `image` 指定预构建镜像），且构建后的镜像内容变化但标签未更新（如标签为 `latest`），Compose **默认不会自动检测镜像变更**，因此不会触发重建。  
**例外**：若通过 `docker compose build` 重新构建镜像后，镜像的哈希值变化，且 `docker-compose.yml` 中 `build` 字段的上下文或 Dockerfile 被修改，Compose 可能检测到配置变更并触发重建（具体取决于 Compose 版本和实现）。  


### 三、不会触发重建的场景
以下情况通常**不会触发容器重建**（仅启动或重启已有容器）：  
- 仅修改 `docker-compose.yml` 中的**非关键配置**（如注释、未使用的变量、`labels` 元数据等不影响容器运行的配置）。  
- 容器配置未变，但容器已停止（`docker compose up -d` 会直接启动已停止的容器，无需重建）。  
- 镜像标签未变且未重新构建（如 `image: nginx:alpine` 未修改，且未手动拉取新版本镜像）。  


### 四、验证是否重建的方法
可通过以下方式确认容器是否被重建：  
1. **查看容器创建时间**：  
   运行 `docker inspect <容器名或ID> --format '{{.Created}}'`，对比重建前后的创建时间。  
2. **检查容器日志**：  
   重建后的容器日志会显示新容器的启动信息（如 `Hello from Docker` 等初始化日志）。  
3. **观察配置哈希标签**：  
   运行 `docker inspect <容器名或ID> --format '{{.Config.Labels}}'`，查看 `com.docker.compose.config-hash` 是否变化。  


### 总结
`docker compose up -d` 重建容器的核心逻辑是**配置哈希是否匹配**。当 `docker-compose.yml` 关键配置变更、显式强制重建、容器不存在或状态异常时，会触发重建；若配置未变且容器已存在，则仅启动或重启容器。  
**最佳实践**：修改关键配置后，若需应用变更，建议直接运行 `docker compose up -d`（Compose 会自动判断是否需要重建）；若需强制清除旧状态，可添加 `--force-recreate`。



## 容器重建导致数据丢失



### 重现过程

`Dockerfile`：

```dockerfile
FROM busybox

RUN echo "1" > /tmp/1.txt

ENTRYPOINT [ "sleep", "infinity" ]

```

`docker-compose.yaml`：

```yaml
version: "3.0"

services:
  container1:
    build:
      context: ./
      dockerfile: Dockerfile
    image: my-busybox-1
    environment:
      - TZ=Asia/Shanghai
      
```

编译镜像

```sh
docker compose build
```

启动容器

```sh
docker compose up -d
```

进入容器模拟制造业务数据

```sh
# 进入容器
docker compose exec -it container1 sh

# 模拟制造业务数据
mkdir -p /var/lib/test
echo "1" > /var/lib/test/1.txt
```

升级镜像 `Dockerfile` 修改为：

```dockerfile
FROM busybox

RUN echo "2" > /tmp/1.txt

ENTRYPOINT [ "sleep", "infinity" ]

```

再次编译镜像

```sh
docker compose build
```

再次启动容器，注意：此时容器被重建。

```sh
docker compose up -d
```

再次进入容器并查看业务数据，发现业务数据已经丢失：

```sh
# 进入容器
docker compose exec -it container1 sh

# 业务数据已经丢失
$ cat /var/lib/test/1.txt
cat: can't open '/var/lib/test/1.txt': No such file or directory
```



### 使用命名卷防止数据丢失

`docker-compose.yaml`：

```yaml
version: "3.0"

services:
  container1:
    build:
      context: ./
      dockerfile: Dockerfile
    image: my-busybox-1
    environment:
      - TZ=Asia/Shanghai
    volumes:
      - vol1:/var/lib/test/

volumes:
  vol1:
```

- 命名卷 `vol1` 不会因为容器重建而丢失数据，除非使用 `docker compose down -v` 命令显示地删除容器和命名卷。
