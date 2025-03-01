# Docker Compose



## yaml 文件指定 command

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

   