# `docker swarm`使用

注意：`docker swarm`没有内置支持各个容器副本独立的数据存储卷功能，似乎`mesos`支持这个特性。



## `docker swarm`环境搭建

注意：公司环境和`google`环境在`centOS8.5`、`centOS stream 8`上搭建`swarm`后`overlay`网络都无法正常工作；公司环境`ubuntu20.4`上搭建`swarm`后 `overlay`网络也是无法正常工作；只有`centOS7`搭建`swarm`后`overlay`网络能够正常工作。

建议：因为`k8s`能够实现`swarm`的所有功能，所以建议使用`k8s`作为`swarm`的替代方案。



### `centOS7`搭建

> `https://docs.docker.com/engine/swarm/swarm-tutorial/`

使用`dcli`安装`docker`

```bash
# 设置dcli工具
sudo rm -f /usr/bin/dcli && sudo curl https://fut001.oss-cn-hangzhou.aliyuncs.com/dcli/dcli-linux-x86_64 --output /usr/bin/dcli && sudo chmod +x /usr/bin/dcli

# 安装docker，版本26.1.1-1
sudo dcli docker install
```

分别设置`manager`、`worker1`、`worker2`节点主机名称

```bash
hostnamectl set-hostname manager
hostnamectl set-hostname worker1
hostnamectl set-hostname worker2
```

在`manager`节点上执行下面命令初始化`manager`节点

```bash
docker swarm init --advertise-addr 192.168.1.203
```

在`manager`节点上执行下面命令获取`worker`节点加入`manager`节点的命令

```bash
docker swarm join-token worker
```

在`worker`节点中执行上面的`docker swarm join`命令加入`manager`节点

```bash
docker swarm join --token SWMTKN-1-23k8qyy8z3ljysi615rt6ytx7r2vmk5js4e9sg1h5nopiop08e-anddk72iojfkkc5mkm8haq9m9 192.168.1.203:2377
```

在`manager`节点上执行下面命令查看`swarm`集群节点信息

```bash
docker node ls
```

当前节点退出集群

```bash
docker swarm leave
```

创建服务测试`swarm`集群`overlay`网络是否正常

```bash
docker service create --detach=true --replicas 1 --name test-nginx --publish published=80,target=80 nginx
```

打开浏览器随机访问一个`swarm`节点都能够查看到`nginx`欢迎页面表示`overlay`网络正常运行
`http://192.168.1.203`、`http://192.168.1.204`、`http://192.168.1.205`



## `service`的使用

创建`service`

```bash
docker service create --detach=true --replicas 1 --name helloworld alpine ping www.baidu.com
```

查看`service`列表

```bash
docker service ls
```

查看指定`service`的详细信息

```bash
docker service inspect --pretty helloworld
```

查看`service`在哪个节点运行

```bash
docker service ps helloworld
```

删除`service`

```bash
docker service rm helloworld
```

动态缩放`service`

```bash
docker service scale helloworld=5
```

查看`service`日志

```bash
docker service logs -f helloworld
```



## 进入某个容器的`cli`


进入`service`某个容器`cli`，获取`helloworld service`在哪个节点运行信息

```bash
docker service ps helloworld
```

找到运行`helloworld service`节点，查找容器`id`，使用`docker exec -it`进入容器`cli`

```bash
docker ps -a
docker exec -it containerId /bin/sh
```



## 使`worker`节点进入`drain`模式，进行维护

> `https://docs.docker.com/engine/swarm/swarm-tutorial/drain-node/`

## `routing mesh`，使用`routing mesh`配置好`overlay`网络，否则请求响应缓慢

> `https://docs.docker.com/engine/swarm/ingress/`

```bash
# 创建nginx service，使用routing mesh
docker service create --name nginx --publish 80:8080 nginx

# 增加nginx负载实例
docker service scale nginx=10
```



## 配置`docker swarm`节点间跨主机通讯

> `https://blog.csdn.net/lixuanshengchao/article/details/82707249`

在`manger`节点创建`overlay`网络

```sh
docker network create --driver=overlay --subnet=10.0.9.0/24 --gateway=10.0.9.1 --attachable=true overlaynet1
```

分别在三台`docker`主机上面创建`docker`容器

```sh
docker run --rm --detach -it --network=overlaynet1 --name=test1 centos:7
docker run --rm --detach -it --network=overlaynet1 --name=test2 centos:7
docker run --rm --detach -it --network=overlaynet1 --name=test3 centos:7
```

在容器中相互`ping`对方（例如：`ping test2`）、`ping 192.168.1.123`、`ping www.baidu.com`都成功

`docker service`使用`overlay`网络通讯，创建名为`h1`服务，使用`overlay`网络通讯

```sh
docker service create --detach=true --network=overlaynet1 --replicas 1 --name h1 alpine ping www.baidu.com
```



## `docker stack`用法

>[`docker compose`文件的`deploy`设置参考](https://docs.docker.com/reference/compose-file/deploy/)

创建`docker-compose.yaml`

```yaml
version: '3.8'  
  
services:  
  nginx:  
    image: nginx
    ports:
      - "80:80"
    # 只启动一个nginx实例
    deploy:
      replicas: 1
```

根据`docker-compose.yaml`部署`stack`，其中`-c`表示`--compose-file`

```sh
docker stack deploy test1 -c docker-compose.yaml
```

查看已部署的堆栈

```sh
docker stack ls
```

查看`stack`中`service`的容器实例状态

```sh
docker stack ps test1
```

删除`stack`

```sh
docker stack rm test1
```

查看`stack`中所有服务

```sh
docker stack services test1
```

查看指定服务日志

```sh
docker service logs test1_web
docker service logs -f --tail 10 test1_web
```



## 指定`service`运行的节点



### 指定标签

查询`swarm`集群所有节点

```sh
docker node ls
```

给节点打标签

```sh
docker node update --label-add mylabel=ml1 worker1
```

删除节点标签

```sh
docker node update --label-rm mylabel worker1
```

查看节点标签

```sh
docker node inspect worker1
```

指定`service`部署的节点

```yaml
version: '3.8'  
  
services:  
  db:  
    image: mariadb:10.4.19
    ports:
      - "3306:3306"
    environment:
      - MARIADB_ROOT_PASSWORD="123456"
    deploy: 
      replicas: 1 
      placement:  
        constraints:
          - node.labels.mylabel==ml1
```

部署`service`

```sh
docker stack deploy test1 --compose-file docker-compose.yaml
```



### 指定节点名称

查询`swarm`集群所有节点

```sh
docker node ls
```

指定`service`部署的节点

```yaml
version: '3.8'  
  
services:  
  db:  
    image: mariadb:10.4.19
    ports:
      - "3306:3306"
    environment:
      - MARIADB_ROOT_PASSWORD="123456"
    deploy: 
      replicas: 1 
      placement:  
        constraints:
          - node.hostname==worker1
```

部署`service`

```sh
docker stack deploy test1 --compose-file docker-compose.yaml
```



## 使用基于`spring-cloud`的微服务测试`swarm`集群

参考文档运行 <a href="/spring-cloud/assistant示例.html#运行assisant-swarm示例" target="_blank">辅助示例</a>



## `docker swarm`管理

删除指定节点

>`https://docs.docker.com/reference/cli/docker/node/rm/`

```bash
# nodeid通过docker node ls获取
docker node rm <nodeid>
```



查看`stack`中所有服务

```sh
docker stack services test1
```



### 删除`stack`中的`service`

查看`stack test1`中的`service`列表

```bash
docker stack services test1
```

删除`service`

```bash
docker service rm test1_creator
```

