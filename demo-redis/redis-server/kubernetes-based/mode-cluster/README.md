## 注意

> 为了实现 redis-cluster-0 等待其他所有节点准备好并执行 redis-cli --cluster create ...逻辑，需要借助 helm 生成 REDIS_NODES(格式：redis-cluster-0:6379 redis-cluster-1:6379 redis-cluster-2:6379)。所以暂时放弃自动创建 redis cluster 方法转而使用人工介入的方式创建 redis cluster。步骤如下：

创建 redis cluster 所有节点

```sh
kubectl apply -f .
```

等待所有 redis cluster 节点 running 状态后手动创建 redis 集群

```sh
export REDIS_NODES=$(kubectl get pods  -l app.kubernetes.io/name=redis-cluster -o json | jq -r '.items | map(.status.podIP) | join(":6379 ")'):6379

kubectl exec -it redis-cluster-0 -- redis-cli --cluster create --cluster-replicas 1 ${REDIS_NODES}
```

