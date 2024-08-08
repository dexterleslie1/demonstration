# 使用`kubernetes`运行`redis`

## 参考资料

参考以下资料基于`kubernetes`运行各种模式的`redis`

- https://github.com/bitnami/charts/tree/main/bitnami/redis-cluster
- https://github.com/bitnami/charts/tree/main/bitnami/redis
- https://github.com/bitnami/containers/tree/main/bitnami/redis-cluster
- https://github.com/bitnami/containers/tree/main/bitnami/redis

## 运行`redis cluser`模式

> 注意：为了实现`redis-cluster-0`等待其他所有节点准备好并执行`redis-cli --cluster create`逻辑，需要借助`helm`生成`REDIS_NODES`(格式：`redis-cluster-0:6379 redis-cluster-1:6379 redis-cluster-2:6379`)。所以暂时放弃自动创建`redis cluster`方法转而使用人工介入的方式创建`redis cluster`

下载`redis cluster`配置到本地，[链接](https://gitee.com/dexterleslie/demonstration/tree/master/demo-redis/redis-server/kubernetes-based/mode-cluster)

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

>[参考链接](https://medium.com/geekculture/redis-cluster-on-kubernetes-c9839f1c14b6)

```bash
# 注意：最新版本redis集群创建可以通过下面的 <pod-name>.<headless-service> 方式创建，不需要使用ip地址
export REDIS_NODES=$(kubectl get pods  -l app.kubernetes.io/name=redis-cluster -o json | jq -r '.items | map(.status.podIP) | join(":6379 ")'):6379

kubectl exec -it redis-cluster-0 -- redis-cli --cluster create --cluster-replicas 1 ${REDIS_NODES}

# 或者针对最新版本redis使用下面方式创建
# 注意：最新版本`redis`通过`<pod-name>.<headless-service>`方式创建集群
kubectl exec -it redis-cluster-0 -- redis-cli --cluster create --cluster-replicas 1 redis-cluster-0.redis-cluster-headless:6379 redis-cluster-1.redis-cluster-headless:6379 redis-cluster-2.redis-cluster-headless:6379 redis-cluster-3.redis-cluster-headless:6379 redis-cluster-4.redis-cluster-headless:6379 redis-cluster-5.redis-cluster-headless:6379
```

删除`redis`集群

```bash
./destroy-k8s.sh
```

使用 [链接](https://gitee.com/dexterleslie/demonstration/tree/master/demo-redis/redistemplate/redistemplate-cluster) 客户端测试`kubernetes`中`redis`是否正常

- 编译测试客户端镜像

  ```bash
  ./build.sh
  ```

- 推送测试客户端镜像

  ```bash
  ./push.sh
  ```

- 部署测试客户端到`kubernetes`中

  ```bash
  ./create-k8s.sh
  ```

- 查看测试客户端日志

  ```bash
  kubectl logs -f demo-redistemplate-cluster
  ```

- 访问`kubernetes`任意一个节点，触发测试客户端测试`kubernetes`中`redis`验证是否正常

  ```bash
  curl 192.168.1.10:30000/api/v1
  ```

- 删除测试客户端

  ```bash
  ./destroy-k8s.sh
  ```