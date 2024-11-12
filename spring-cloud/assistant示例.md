# 运行`assistant`示例



## 运行`assisant swarm`示例

下载示例`https://gitee.com/dexterleslie/demonstration/tree/master/spring-cloud/demo-spring-cloud-assistant-swarm`

编译镜像

```bash
cd compiler
./build.sh
```

推送镜像

```bash
./push.sh
```

运行`stack`

```sh
docker stack deploy test1 -c docker-stack.yaml
```

- `docker-stack.yaml`的内容是`deployer/docker-stack.yaml`文件的内容。

打开浏览器查看`eureka`微服务是否正常注册`http://192.168.1.205:9999/`

打开浏览器测试微服务是否正常`http://192.168.1.203:8080/api/v1/gateway/test1`

删除`stack`

```bash
docker stack rm test1
```



## 运行`assistant k8s`示例

下载示例`https://gitee.com/dexterleslie/demonstration/tree/master/spring-cloud/demo-spring-cloud-assistant`

编译镜像

```bash
cd compiler
./build.sh
```

推送镜像

```bash
./push.sh
```

创建集群

```bash
cd deployer
./create-k8s.sh
```

访问`eureka`，`http://192.168.235.145:30000/`

访问`kibana`，`http://192.168.235.145:30002/`

访问`skywalking`，`http://192.168.235.145:30003/`

测试`k8s`运行微服务是否正常`http://192.168.235.145:30001/api/v1/gateway/test1`

删除集群

```bash
./destroy-k8s.sh
```