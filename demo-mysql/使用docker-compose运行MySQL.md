# 使用 docker-compose 运行 MySQL

## 单机版 MySQL

>示例详细用法请参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-mysql/mysql-server/standalone)

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

   