# 使用`docker`运行

## 使用`docker compose`运行

>[Running Locust with Docker](https://docs.locust.io/en/1.5.2/running-locust-docker.html)

运行 [演示](https://github.com/dexterleslie1/demonstration/tree/master/demo-locust/docker-compose) 步骤如下：

1. 编译演示

   ```bash
   docker compose build
   ```

2. 运行演示

   ```bash
   docker compose up -d
   ```

3. 访问`http://localhost:8089/`启动测试

4. 关闭演示

   ```bash
   docker compose down -v
   ```



## 使用`docker swarm`运行

运行 [演示](https://github.com/dexterleslie1/demonstration/tree/master/demo-locust/docker-swarm) 步骤如下：

1. 参考 <a href="/docker容器/docker-swarm.html#centos7-搭建" target="_blank">链接</a> 搭建`docker swarm`

   ```bash
   # 给各个节点打标签，locust master被调度到manager节点，locust worker被调度到worker节点
   
   # 给swarm管理节点打manager标签
   docker node update --label-add mylabel=manager manager
   
   # 给swarm worker节点打worker标签
   docker node update --label-add mylabel=worker worker1
   docker node update --label-add mylabel=worker worker2
   docker node update --label-add mylabel=worker worker3
   ```

2. 编译演示

   ```bash
   docker compose build
   ```

3. 推送镜像到`docker`仓库

   ```bash
   docker compose push
   ```

4. 在`swarm`管理节点上运行`locust`集群

   ```bash
   docker stack deploy test1 -c docker-compose.yaml
   ```

5. 查看`stack`中`service`的容器实例状态

   ```bash
   docker stack ps test1
   ```

6. 访问`http://localhost:8089/`启动测试

7. 删除`stack test1`

   ```bash
   docker stack rm test1
   ```

   
