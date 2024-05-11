# 使用 docker-compose 运行 MySQL

## 单机版 MySQL

1. 准备实验环境

   参考 <a href="/docker容器/docker的安装/" target="_blank">安装 Docker 环境</a>

2. 创建`docker-compose.yaml`和`my-customize.cnf`文件

   **`docker-compose.yaml`内容如下：**

   ```yaml
   version: "3.0"
   
   services:
     # https://hub.docker.com/_/mysql?tab=description  
     demo-mysql-standalone-server:
       image: mysql
       command:
        - --character-set-server=utf8mb4
        - --collation-server=utf8mb4_general_ci
        - --skip-character-set-client-handshake 
       volumes:
        # 指定 MySQL 配置文件
        - ./my-customize.cnf:/etc/mysql/conf.d/my-customize.cnf
       environment:
         - LANG=C.UTF-8
         # 指定容器环境时区
         - TZ=Asia/Shanghai
         # 指定 root 密码
         - MYSQL_ROOT_PASSWORD=123456
       ports:
         - 3306:3306
   
   ```

   **`my-customize.cnf`内容如下：**

   ```properties
   [mysqld]
   # 启用慢日志
   slow_query_log=1
   long_query_time=1
   slow_query_log_file=slow-query.log
   
   ```

3. 启动`MySQL`

   ```sh
   docker compose up -d
   ```

4. 关闭`MySQL`

   ```sh
   docker compose down -v
   ```

   