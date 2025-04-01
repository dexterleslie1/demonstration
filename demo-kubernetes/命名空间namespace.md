# `namespace`命名空间

> 实现资源隔离和管理

系统预置`namespace`如下：

- `default`所有未指定`namespace`的对象都会自动分配到`default`命名空间
- `kube-flannel flannel`网络插件使用命名空间
- `kube-node-lease`集群节点之间的心跳维护
- `kube-public`此命名空间下的资源能够被所有人访问（包括未认证用户）
- `kube-system`所有有`kubernetes`系统创建的资源都分配到这个命名空间

```bash
# 获取所有命名空间
kubectl get namespace

# 描述命名空间
kubectl describe namespace default

# 查看指定命名空间下的pod
kubectl get pod -n kube-flannel

# 创建命名空间
kubectl create namespace dev

# 删除命名空间
kubectl delete namespace dev
```

