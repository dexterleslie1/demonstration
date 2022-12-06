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

## k8s yaml帮助查阅

```shell
# 查询Pod相关帮助
kubectl explain Pod

# 查询Pod.apiVersion帮助
kubectl explain Pod.apiVersion

# 查询apiVersion可填写的值
kubectl api-versions
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

### 基础

> 是容器的封装，通过管理pod达到控制多个容器的目的

```shell
# 获取kube-system命名空间下的pod
kubectl get pod -n kube-system

# 运行pod，但是不会创建deployment
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

### yaml基本配置

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: base-pod
 namespace: dev
 labels:
  test1: test1v
spec:
 containers:
 - name: nginx
   image: nginx
 - name: busybox
   image: busybox
```

```shell
# 使用配置文件创建pod
kubectl create -f 1.yaml

# 使用配置文件删除pod
kubectl delete -f 1.yaml
```

### 镜像拉取策略

- Always: 总是从远程仓库拉取镜像
- IfNotPresent: 本地有则使用本地，否则拉取远程仓库
- Never: 只使用本地，从不去远程拉取，本地没有则报错

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: base-pod
 namespace: dev
 labels:
  test1: test1v
spec:
 containers:
 - name: nginx
   image: nginx
   imagePullPolicy: Always
 - name: busybox
   image: busybox
```

### 启动命令

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: base-pod
 namespace: dev
 labels:
  test1: test1v
spec:
 containers:
 - name: nginx
   image: nginx
   imagePullPolicy: Always
 - name: busybox
   image: busybox
   command: ["/bin/sh", "-c", "touch /tmp/hello.txt;while true;do /bin/echo $(date +%T) >> /tmp/hello.txt;sleep 3;done;"]
```

```shell
# 进入容器查看/tmp/hello.txt输出，base-pod是pod的名称，busybox是容器名称
kubectl exec base-pod -n dev -it -c busybox /bin/sh

# 进入容器后查看/tmp/hello.txt
tail -f /tmp/hello.txt
```

### 环境变量

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: base-pod
 namespace: dev
 labels:
  test1: test1v
spec:
 containers:
 - name: nginx
   image: nginx
   imagePullPolicy: Always
 - name: busybox
   image: busybox
   command: ["/bin/sh", "-c", "touch /tmp/hello.txt;while true;do /bin/echo $(date +%T) >> /tmp/hello.txt;sleep 3;done;"]
   env:
   - name: "username"
     value: "admin"
   - name: "password"
     value: "123456"
```

```shell
# 进入容器
kubectl exec base-pod -n dev -it -c busybox /bin/sh

# 打印环境变量
echo $username
echo $password
```

### 端口设置

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: base-pod
 namespace: dev
 labels:
  test1: test1v
spec:
 containers:
 - name: nginx
   image: nginx
   imagePullPolicy: Always
   ports:
   - name: nginx-port
     containerPort: 80 # 访问这个端口需要使用curl podId:80
     protocol: TCP
```

```shell
# 获取pod ip
kubectl get pod -n dev -o wide

# 访问pod端口
curl podip:80
```

### 资源配额

- limits: 用于限制容器运行时占用资源的最大值，容器占用资源超过limits限制时，容器被终止并且重启
- requests: 用于设置容器启动时的最小资源，如果资源不足，容器将不能启动

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: base-pod
 namespace: dev
 labels:
  test1: test1v
spec:
 containers:
 - name: nginx
   image: nginx
   imagePullPolicy: Always
   resources:
    limits:
     cpu: "1"
     memory: "1G" # 单位G或者M
    requests:
     cpu: "1"
     memory: "1M"
```

### 初始化容器使用

> 使用init容器模拟等待mysql和redis启动后才启动nginx的过程

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: base-pod
 namespace: dev
 labels:
  test1: test1v
spec:
 containers:
 - name: nginx
   image: nginx
   imagePullPolicy: Always
 initContainers:
 - name: test-mysql
   image: busybox
   command: ["sh", "-c", "i=1;until [ $i -ge 15 ]; do echo 'simulating waiting for mysql...'; sleep 1; i=$((i+1)); done;"]
 - name: test-redis
   image: busybox
   command: ["sh", "-c", "i=1;until [ $i -ge 15 ]; do echo 'simulating waiting for mysql...'; sleep 1; i=$((i+1)); done;"]
```

```shell
# 使用命令观察init容器和main容器过程，-w参数
kubectl get pod -n dev -o wide -w
```

### 钩子函数

> 在main容器启动后postStart和关闭前preStop执行指定的动作

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: base-pod
 namespace: dev
 labels:
  test1: test1v
spec:
 containers:
 - name: nginx
   image: nginx
   imagePullPolicy: Always
   lifecycle:
    postStart:
     exec:
      command: ["sh", "-c", "echo postStart... > /usr/share/nginx/html/index.html"]
```

```shell
# 获取pod ip地址
kubectl get pod -n dev -o wide
# 测试是否执行postStart
curl podIp:80
```

### 容器探测

- liveness probes: 决定是否重启容器
- readiness probes: 决定是否将请求转发给容器

**liveness探测**

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: base-pod
 namespace: dev
 labels:
  test1: test1v
spec:
 containers:
 - name: nginx
   image: nginx
   imagePullPolicy: Always
   livenessProbe:
    exec:
     command: ["/bin/cat", "/tmp/hello.txt"] # 因为文件/tmp/hello.txt不存在，命令执行失败，导致容器不断重新启动
```

### 重启策略

> liveness探测失败后使用restartPolicy重启策略重启容器，Never表示不重启容器，Always表示总是重启容器，OnFailure表示容器退出并且退出码不为0时重启容器

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: base-pod
 namespace: dev
 labels:
  test1: test1v
spec:
 restartPolicy: Never
 containers:
 - name: nginx
   image: nginx
   imagePullPolicy: Always
   livenessProbe:
    exec:
     command: ["/bin/cat", "/tmp/hello.txt"]
```

```shell
# 使用命令跟踪容器状态
kubectl get pod -n dev -o wide -w
```

### 定向调度

> 通过nodename或者nodeselector指定pod运行的节点

**使用nodename指定节点**

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: base-pod
 namespace: dev
 labels:
  test1: test1v
spec:
 nodeName: k8s-node1 # 使用节点名称指定pod运行节点
 containers:
 - name: nginx
   image: nginx
   imagePullPolicy: Always
```

**使用nodeselector指定节点标签**

```shell
# 节点打标签
kubectl label node k8s-node1 node-label=node1
```

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: base-pod
 namespace: dev
 labels:
  test1: test1v
spec:
 nodeSelector:
  node-label: node1 # 指定节点标签
 containers:
 - name: nginx
   image: nginx
   imagePullPolicy: Always
```

### 亲和性调度

todo

### 污点调度

todo

### 容忍调度

todo

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

## pod控制器

> pod控制，用于保证pod运行状态符合预期状态

```shell
# 创建deployment
kubectl create deployment nginx --image=nginx --port=80 --replicas=3 --namespace=dev

# 显示deployment列表
kubectl get deployment,pods -n dev

# 删除其中一个pod后，deployment控制器会自动创建一个新的pod
kubectl delete pod nginx-xxxxxxx -n dev

# 显示deployment详细信息
kubectl describe deployment -n dev

# 删除deployment
kubectl delete deployment nginx -n dev
```

### ReplicaSet(RS)

> 保证一定数量的pod能够正常运行，一旦pod发生故障就会自动重启或者重建pod

## k8s service

> 是一组同类pod对外访问接口，借助service，应用可以方便地实现服务发现和负载均衡

```shell
# 创建service
kubectl expose deployment nginx --name=service-nginx --port=80 --target-port=80 --type=ClusterIP -n dev

# 查看service
kubectl get service -n dev

# 删除service
kubectl delete service service-nginx -n dev
```

