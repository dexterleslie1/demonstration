# k8s

## k8s安装

> NOTE 使用dcli安装

检查k8s服务是否正常

```shell
# 在master节点查看基础容器运行状态
kubectl get pods -n kube-system

# 在master节点查看所有节点状态
kubectl get nodes

# 部署nginx服务
kubectl create deployment nginx --image=nginx
# 使用NodePort方式暴露nginx服务
kubectl expose deployment nginx --port=80 --type=NodePort
# 查看nginx NodePort端口并使用浏览器访问成功
kubectl get pod,svc
```

## k8s namespace

> 实现资源隔离和管理

预置namespance

- default 所有未指定namespace的对象都会自动分配到default命名空间
- kube-flannel flannel网络插件使用命名空间
- kube-node-lease 集群节点之间的心跳维护
- kube-public 此命名空间下的资源能够被所有人访问（包括未认证用户）
- kube-system 所有有kubernetes系统创建的资源都分配到这个命名空间

```shell
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

## k8s pod

> 是容器的封装，通过管理pod达到控制多个容器的目的

```shell
# 获取kube-system命名空间下的pod
kubectl get pod -n kube-system

# 运行pod
kubectl run nginx --image=nginx --port=80 --namespace=dev

# 查询dev命名空间的pod
kubectl get pod -n dev

# 显示pod详细信息
kubetl describe pod nginx -n dev

# 访问pod
# 获取pod ip地址
kubectl get pod nginx -n dev -o wide
curl ip:80

# 删除pod
kubectl delete pod nginx -n dev
```

## k8s label

> 在资源上添加标签，用于对资源的区分和选择

```shell
# 显示标签
kubectl get pod -n dev --show-labels

# 资源打标签
kubectl label pod nginx -n dev version=1.0

# 更新标签
kubectl label pod nginx -n dev version=2.0 --overwrite=true

# 标签等于筛选
kubectl get pod -n dev --show-labels -l "version=2.0"

# 标签不等于筛选
kubectl get pod -n dev --show-labels -l "version!=2.0"

# 删除标签
kubectl label pod nginx -n dev version-
```

