# 运行`mysql`和`mariadb`

## 使用容器运行单机版`mysql`

>示例详细用法请参考 [链接](https://gitee.com/dexterleslie/demonstration/tree/master/demo-mysql/mysql-server/standalone)

1. 准备实验环境

   参考 <a href="/docker容器/docker的安装/" target="_blank">安装 Docker 环境</a>

2. 创建`docker-compose.yaml`和`my-customize.cnf`文件

   **`docker-compose.yaml`内容如下：**

   ```yaml
   version: "3.0"
   
   services:
     # https://hub.docker.com/_/mysql?tab=description  
     db:
       image: mysql:5.7
       command:
        - --character-set-server=utf8mb4
        - --collation-server=utf8mb4_general_ci
        - --skip-character-set-client-handshake 
       volumes:
       #  - ~/data-demo-mysql-standalone-server:/var/lib/mysql
        - ./my-customize.cnf:/etc/mysql/conf.d/my-customize.cnf
       environment:
         - LANG=C.UTF-8
         - TZ=Asia/Shanghai
         - MYSQL_ROOT_PASSWORD=123456
       ports:
         - '3306:3306'
   
   ```

   **`my-customize.cnf`内容如下：**

   ```properties
   [mysqld]
   slow_query_log=1
   long_query_time=1
   slow_query_log_file=slow-query.log
   
   # 启用binlog
   log_bin
   # 推荐使用 ROW 格式，但你也可以选择 STATEMENT 或 MIXED
   binlog_format = MIXED
   # 设置 binlog 文件在自动删除前的保留天数   
   expire_logs_days = 7  
   # 设置单个 binlog 文件的最大大小
   max_binlog_size = 512M
   server-id = 10001
   
   ```

3. 启动`MySQL`

   ```sh
   docker compose up -d
   ```

4. 关闭`MySQL`

   ```sh
   docker compose down -v
   ```




## 使用容器运行单机版`mariadb`

> 使用`docker`运行单机版`mariadb`参考[链接](https://gitee.com/dexterleslie/demonstration/tree/master/demo-mysql-n-mariadb/mariadb-server/standalone)

`.env`定义数据库密码

```properties
dbPassword=123456
```

初始化数据库的脚本`db.sql`

```sql
CREATE DATABASE IF NOT EXISTS demo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE demo_db;

CREATE TABLE IF NOT EXISTS `auth`(
    id                  BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    account             VARCHAR(64) NOT NULL UNIQUE COMMENT '账号',
    `password`          VARCHAR(64) NOT NULL COMMENT '密码',
    create_time         DATETIME NOT NULL COMMENT '创建时间'
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
```

数据库配置文件`my-customize.cnf`

```properties
[mysqld]
max_connections=250
slow_query_log=1
long_query_time=1
slow_query_log_file=slow-query.log
innodb_flush_log_at_trx_commit=0
max_allowed_packet=10m
key_buffer_size=512m
innodb_log_file_size=512m
innodb_log_buffer_size=256m
innodb_file_per_table=1
max_binlog_size=512m

# 一旦提供log_bin参数无论是何值或者不提供值时，表示启用binlog功能
# 不提供log_bin表示禁用binlog功能
log_bin
expire_logs_days=10
binlog_format=mixed
max_binlog_size=1024m
# 指定binlog文件的前缀
log_basename=master1
server_id=10001
```

编译`docker`镜像的`Dockerfile`

```dockerfile
FROM mariadb:10.4.19

RUN apt-get update
RUN apt-get install -y curl

ENV DOCKERIZE_VERSION v0.6.1
RUN curl --silent --output dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz https://bucketxyh.oss-cn-hongkong.aliyuncs.com/docker/dockerize-linux-amd64-v0.6.1.tar.gz
RUN tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz

COPY db.sql /docker-entrypoint-initdb.d/
COPY my-customize.cnf /etc/mysql/conf.d/my-customize.cnf
```

运行数据库服务`docker-compose.yaml`

```yaml
version: "3.0"

# 创建网络
networks:
  net:
    name: demo-mariadb-standalone-net

services:
  # docker 运行 centos/mariadb
  # https://hub.docker.com/r/centos/mariadb
  # 使用指定字符集启动mariadb
  # https://stackoverflow.com/questions/45729326/how-to-change-the-default-character-set-of-mysql-using-docker-compose/53629912
  
  # 挂载数据库初始化脚本db.sql
  # NOTE: db.sql脚本挂载前已存，否则映射到容器的db.sql为目录类型，宿主机的db.sql需要chmod a+r db.sql赋予r权限，否则容器启动过程中会失败并且提示permission denied。
  # docker run --rm --name mariadb-demo -p 3306:3306 -v /data/data-mariadb-demo:/var/lib/mysql -v $(pwd)/db.sql:/docker-entrypoint-initdb.d/db.sql -e MYSQL_ROOT_PASSWORD=123456 -e TZ=Asia/Shanghai mariadb:10.4.19  --character-set-server=utf8mb4 --collation-server=utf8mb4_general_ci --skip-character-set-client-handshake
  demo-mariadb-standalone-server:
    build:
      context: ./
    container_name: demo-mariadb-standalone-server
    image: demo-mariadb-standalone-server
    command:
     - --character-set-server=utf8mb4
     - --collation-server=utf8mb4_general_ci
     - --skip-character-set-client-handshake
     # 设置innodb-buffer-pool-size
     # https://stackoverflow.com/questions/64825998/how-to-change-the-default-config-for-mysql-when-using-docker-image
     - --innodb-buffer-pool-size=1g
    #volumes:
    #  - ~/data-demo-mariadb-standalone-server:/var/lib/mysql
    environment:
      - LANG=C.UTF-8
      - TZ=Asia/Shanghai
      - MYSQL_ROOT_PASSWORD=${dbPassword}
    ports:
      - 50000:3306
    networks:
      - net

  demo-mariadb-standalone-server-update:
    container_name: demo-mariadb-standalone-server-update
    image: demo-mariadb-standalone-server
    environment:
      - TZ=Asia/Shanghai
    command: sh -c "dockerize -wait tcp://demo-mariadb-standalone-server:3306 -timeout 120s -wait-retry-interval 5s
      && mysql -uroot -p${dbPassword} -P3306 -hdemo-mariadb-standalone-server demo_db < /docker-entrypoint-initdb.d/db.sql
      && echo \"成功执行数据库脚本\""
    networks:
      - net

```

启动数据库服务

```bash
docker compose up -d
```

删除数据库服务

```bash
docker compose down -v
```

