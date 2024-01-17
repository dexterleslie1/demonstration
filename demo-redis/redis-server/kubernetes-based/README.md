## 参考以下资料基于 kubernetes 创建各种模式的 redis

> https://github.com/bitnami/charts/tree/main/bitnami/redis-cluster
> https://github.com/bitnami/charts/tree/main/bitnami/redis
> https://github.com/bitnami/containers/tree/main/bitnami/redis-cluster
> https://github.com/bitnami/containers/tree/main/bitnami/redis

## 使用 demo-redis/redistemplate 客户端测试集群内的 redis

切换到 kubernetes-based/mode-xxx redis对应的模式目录中，然后在 kubernetes 中创建 redis

```sh
kubectl apply -f .
```

查看 redis statefulset 是否已经启动

```sh
kubectl get statefulset
```

使用 redistemplate-xxx 客户端测试 kubernetes 中 redis 是否正常

编译 redistemplate-xxx docker 镜像

```sh
sh build.sh
```

推送 redistemplate-xxx docker 镜像

```sh
sh push.sh
```

部署 redistemplate-xxx 到 kubernetes 中

```sh
kubectl apply -f k8s.yaml
```

查看 redistemplate-xxx 日志

```sh
kubectl logs -f demo-redistemplate-xxx
```

登录到 kubernetes 任意一个节点，触发 redistemplate-xxx 测试 kubernetes 中 redis 验证是否正常

```sh
curl localhost:30000/api/v1
```

