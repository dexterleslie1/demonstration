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

> 保证一定数量的pod能够正常运行，一旦pod发生故障就会自动重启或者重建pod，pod数量扩容，镜像版本的升级和回退

**pod基础**

```yaml
apiVersion: apps/v1
kind: ReplicaSet
metadata:
 name: replicaset1
 namespace: dev
spec:
 replicas: 3
 selector:
  matchLabels:
   app: nginx-pod
 template:
  metadata:
   labels:
    app: nginx-pod
  spec:
   containers:
   - name: nginx
     image: nginx
```

```shell
# 查看replicaset状态
[root@k8s-master ~]# kubectl get replicaset -n dev -o wide
NAME          DESIRED   CURRENT   READY   AGE   CONTAINERS   IMAGES   SELECTOR
replicaset1   3         3         3       13m   nginx        nginx    app=nginx-pod
# 查看pod状态
[root@k8s-master ~]# kubectl get pod -n dev
NAME                READY   STATUS    RESTARTS   AGE
replicaset1-666zx   1/1     Running   0          13m
replicaset1-nbfts   1/1     Running   0          13m
replicaset1-rw24c   1/1     Running   0          13m
```

**pod数量缩放**

```shell
# 使用下面命令编辑replicaset yaml中的replicas参数，退出保存后replicaset自动缩放pod
[root@k8s-master ~]# kubectl edit replicaset replicaset1 -n dev
replicaset.apps/replicaset1 edited

# 使用下面命令对replicaset缩放
[root@k8s-master ~]# kubectl scale replicaset replicaset1 -n dev --replicas=6
replicaset.apps/replicaset1 scaled
```

**pod镜像版本升级和回退**

```shell
# 使用下面命令在线编辑image参数，退出保存后replicaset自动更新镜像版本
[root@k8s-master ~]# kubectl edit replicaset replicaset1 -n dev
replicaset.apps/replicaset1 edited
# 查看当前image版本
[root@k8s-master ~]# kubectl get replicaset -n dev -o wide
NAME          DESIRED   CURRENT   READY   AGE   CONTAINERS   IMAGES         SELECTOR
replicaset1   2         2         2       31m   nginx        nginx:1.17.1   app=nginx-pod

# 使用命令修改image
[root@k8s-master ~]# kubectl set image replicaset replicaset1 -n dev nginx=nginx:1.17.1
replicaset.apps/replicaset1 image updated
```

**删除replicaset**

```shell
# 使用命令删除
[root@k8s-master ~]# kubectl delete replicaset replicaset1 -n dev
replicaset.apps "replicaset1" deleted

# 使用yaml删除
[root@k8s-master ~]# kubectl delete -f 1.yaml 
replicaset.apps "replicaset1" deleted
```

### Deployment(deploy)

> deployment控制器不直接管理pod，而是通过Replicaset间接管理pod

**基本用法**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
 namespace: dev
spec:
 replicas: 3
 selector:
  matchLabels:
   app: nginx-pod
 template:
  metadata:
   labels:
    app: nginx-pod
  spec:
   containers:
   - name: nginx
     image: nginx:1.17.1
```

```shell
[root@k8s-master ~]# kubectl create -f 1.yaml 
deployment.apps/deployment1 created
[root@k8s-master ~]# kubectl get deployment -n dev -o wide
NAME          READY   UP-TO-DATE   AVAILABLE   AGE   CONTAINERS   IMAGES         SELECTOR
deployment1   1/3     3            1           12s   nginx        nginx:1.17.1   app=nginx-pod
# 因为deployment通过replicaset管理pod，所以创建deployment后会自动创建一个对应的replicaset
[root@k8s-master ~]# kubectl get replicaset -n dev
NAME                     DESIRED   CURRENT   READY   AGE
deployment1-5d9c9b97bb   3         3         3       2m33s
```

**扩缩容**

```shell
# 通过命令方式实现扩容
[root@k8s-master ~]# kubectl scale deployment deployment1 -n dev --replicas=6
deployment.apps/deployment1 scaled
[root@k8s-master ~]# kubectl get pod -n dev -o wide
NAME                           READY   STATUS    RESTARTS   AGE     IP            NODE        NOMINATED NODE   READINESS GATES
deployment1-5d9c9b97bb-7tz7p   1/1     Running   0          9m56s   10.244.2.30   k8s-node2   <none>           <none>
deployment1-5d9c9b97bb-8vk5f   1/1     Running   0          9m56s   10.244.1.16   k8s-node1   <none>           <none>
deployment1-5d9c9b97bb-dqvdm   1/1     Running   0          14s     10.244.2.31   k8s-node2   <none>           <none>
deployment1-5d9c9b97bb-k7tg8   1/1     Running   0          14s     10.244.2.32   k8s-node2   <none>           <none>
deployment1-5d9c9b97bb-wwgcc   1/1     Running   0          9m56s   10.244.2.29   k8s-node2   <none>           <none>
deployment1-5d9c9b97bb-xlktk   1/1     Running   0          14s     10.244.1.17   k8s-node1   <none>           <none>

# 通过编辑deployment配置中的replicas实现扩容
[root@k8s-master ~]# kubectl edit deployment deployment1 -n dev
deployment.apps/deployment1 edited
```

**升级策略**

- 重建更新: 删除所有旧版本pod，重新创建新版本pod

- 滚动更新: 删除一部分旧版本pod，创建一部分新版本pod，如此重复最终所有更新替换所有旧版本pod

重建更新

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
 namespace: dev
spec:
 replicas: 3
 strategy:
  type: Recreate # 重建更新
 selector:
  matchLabels:
   app: nginx-pod
 template:
  metadata:
   labels:
    app: nginx-pod
  spec:
   containers:
   - name: nginx
     image: nginx:1.17.1
```

```shell
# 模拟升级镜像版本
[root@k8s-master ~]# kubectl set image deployment deployment1 -n dev nginx=nginx:1.17.2
deployment.apps/deployment1 image updated

# 观察pod重建过程
[root@k8s-master ~]# kubectl get pod -n dev -o wide -w
NAME                           READY   STATUS    RESTARTS   AGE   IP            NODE        NOMINATED NODE   READINESS GATES
deployment1-5d9c9b97bb-7tz7p   1/1     Running   0          54m   10.244.2.30   k8s-node2   <none>           <none>
deployment1-5d9c9b97bb-8vk5f   1/1     Running   0          54m   10.244.1.16   k8s-node1   <none>           <none>
deployment1-5d9c9b97bb-xlktk   1/1     Running   0          44m   10.244.1.17   k8s-node1   <none>           <none>
deployment1-5d9c9b97bb-7tz7p   1/1     Terminating   0          54m   10.244.2.30   k8s-node2   <none>           <none>
deployment1-5d9c9b97bb-8vk5f   1/1     Terminating   0          54m   10.244.1.16   k8s-node1   <none>           <none>
deployment1-5d9c9b97bb-xlktk   1/1     Terminating   0          45m   10.244.1.17   k8s-node1   <none>           <none>
deployment1-5d9c9b97bb-7tz7p   0/1     Terminating   0          54m   <none>        k8s-node2   <none>           <none>
deployment1-5d9c9b97bb-8vk5f   0/1     Terminating   0          54m   10.244.1.16   k8s-node1   <none>           <none>
deployment1-5d9c9b97bb-xlktk   0/1     Terminating   0          45m   10.244.1.17   k8s-node1   <none>           <none>
deployment1-5d9c9b97bb-7tz7p   0/1     Terminating   0          55m   <none>        k8s-node2   <none>           <none>
deployment1-5d9c9b97bb-7tz7p   0/1     Terminating   0          55m   <none>        k8s-node2   <none>           <none>
deployment1-5d9c9b97bb-8vk5f   0/1     Terminating   0          55m   10.244.1.16   k8s-node1   <none>           <none>
deployment1-5d9c9b97bb-8vk5f   0/1     Terminating   0          55m   10.244.1.16   k8s-node1   <none>           <none>
deployment1-5d9c9b97bb-xlktk   0/1     Terminating   0          46m   10.244.1.17   k8s-node1   <none>           <none>
deployment1-5d9c9b97bb-xlktk   0/1     Terminating   0          46m   10.244.1.17   k8s-node1   <none>           <none>
deployment1-7c7477c7ff-vnhtq   0/1     Pending       0          0s    <none>        <none>      <none>           <none>
deployment1-7c7477c7ff-xcdqf   0/1     Pending       0          0s    <none>        <none>      <none>           <none>
deployment1-7c7477c7ff-wvnrk   0/1     Pending       0          0s    <none>        <none>      <none>           <none>
deployment1-7c7477c7ff-vnhtq   0/1     Pending       0          0s    <none>        k8s-node2   <none>           <none>
deployment1-7c7477c7ff-xcdqf   0/1     Pending       0          0s    <none>        k8s-node1   <none>           <none>
deployment1-7c7477c7ff-wvnrk   0/1     Pending       0          0s    <none>        k8s-node2   <none>           <none>
deployment1-7c7477c7ff-vnhtq   0/1     ContainerCreating   0          0s    <none>        k8s-node2   <none>           <none>
deployment1-7c7477c7ff-xcdqf   0/1     ContainerCreating   0          0s    <none>        k8s-node1   <none>           <none>
deployment1-7c7477c7ff-wvnrk   0/1     ContainerCreating   0          0s    <none>        k8s-node2   <none>           <none>
deployment1-7c7477c7ff-wvnrk   1/1     Running             0          22s   10.244.2.34   k8s-node2   <none>           <none>
deployment1-7c7477c7ff-vnhtq   1/1     Running             0          22s   10.244.2.33   k8s-node2   <none>           <none>
deployment1-7c7477c7ff-xcdqf   1/1     Running             0          23s   10.244.1.18   k8s-node1   <none>           <none>
```

滚动更新

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
 namespace: dev
spec:
 replicas: 3
 strategy:
  type: RollingUpdate # 滚动更新策略
  rollingUpdate:
   maxUnavailable: 25%
   maxSurge: 25%
 selector:
  matchLabels:
   app: nginx-pod
 template:
  metadata:
   labels:
    app: nginx-pod
  spec:
   containers:
   - name: nginx
     image: nginx:1.17.1
```

```shell
# 模拟升级镜像版本
[root@k8s-master ~]# kubectl set image deployment deployment1 -n dev nginx=nginx:1.17.2
deployment.apps/deployment1 image updated

# 观察滚动更新过程
[root@k8s-master ~]# kubectl get pod -n dev -w
NAME                           READY   STATUS    RESTARTS   AGE
deployment1-5d9c9b97bb-qjd58   1/1     Running   0          20s
deployment1-5d9c9b97bb-s8mst   1/1     Running   0          23s
deployment1-5d9c9b97bb-vjgwx   1/1     Running   0          22s
deployment1-76fd8c7f84-w2n8g   0/1     Pending   0          0s
deployment1-76fd8c7f84-w2n8g   0/1     Pending   0          0s
deployment1-76fd8c7f84-w2n8g   0/1     ContainerCreating   0          0s
deployment1-76fd8c7f84-w2n8g   1/1     Running             0          22s
deployment1-5d9c9b97bb-qjd58   1/1     Terminating         0          54s
deployment1-76fd8c7f84-xln89   0/1     Pending             0          0s
deployment1-76fd8c7f84-xln89   0/1     Pending             0          0s
deployment1-76fd8c7f84-xln89   0/1     ContainerCreating   0          0s
deployment1-5d9c9b97bb-qjd58   0/1     Terminating         0          55s
deployment1-5d9c9b97bb-qjd58   0/1     Terminating         0          58s
deployment1-5d9c9b97bb-qjd58   0/1     Terminating         0          58s
deployment1-76fd8c7f84-xln89   1/1     Running             0          22s
deployment1-5d9c9b97bb-vjgwx   1/1     Terminating         0          78s
deployment1-76fd8c7f84-tqn8c   0/1     Pending             0          0s
deployment1-76fd8c7f84-tqn8c   0/1     Pending             0          0s
deployment1-76fd8c7f84-tqn8c   0/1     ContainerCreating   0          0s
deployment1-76fd8c7f84-tqn8c   1/1     Running             0          1s
deployment1-5d9c9b97bb-vjgwx   0/1     Terminating         0          79s
deployment1-5d9c9b97bb-s8mst   1/1     Terminating         0          80s
deployment1-5d9c9b97bb-s8mst   0/1     Terminating         0          81s
deployment1-5d9c9b97bb-s8mst   0/1     Terminating         0          87s
deployment1-5d9c9b97bb-s8mst   0/1     Terminating         0          87s
deployment1-5d9c9b97bb-vjgwx   0/1     Terminating         0          90s
deployment1-5d9c9b97bb-vjgwx   0/1     Terminating         0          90s
```

**版本回退**

> 版本回退原理是通过多个replicaset实现的

```shell
# 查看版本更新历史
[root@k8s-master ~]# kubectl rollout history deployment deployment1 -n dev
deployment.apps/deployment1 
REVISION  CHANGE-CAUSE
2         <none>
3         <none>
4         <none>
[root@k8s-master ~]# kubectl get replicaset -n dev
NAME                     DESIRED   CURRENT   READY   AGE
deployment1-5d9c9b97bb   0         0         0       77m
deployment1-76fd8c7f84   3         3         3       16m
deployment1-7c7477c7ff   0         0         0       21m

# 回退到指定版本
[root@k8s-master ~]# kubectl rollout undo deployment deployment1 --to-revision=2 -n dev
deployment.apps/deployment1 rolled back
# 查看回退状态
[root@k8s-master ~]# kubectl rollout status deployment deployment1 -n dev
Waiting for deployment "deployment1" rollout to finish: 2 out of 3 new replicas have been updated...
Waiting for deployment "deployment1" rollout to finish: 2 out of 3 new replicas have been updated...
Waiting for deployment "deployment1" rollout to finish: 2 out of 3 new replicas have been updated...
Waiting for deployment "deployment1" rollout to finish: 1 old replicas are pending termination...
Waiting for deployment "deployment1" rollout to finish: 1 old replicas are pending termination...
deployment "deployment1" successfully rolled out
# 回退后的replicaset状态
[root@k8s-master ~]# kubectl get replicaset -n dev
NAME                     DESIRED   CURRENT   READY   AGE
deployment1-5d9c9b97bb   0         0         0       79m
deployment1-76fd8c7f84   0         0         0       19m
deployment1-7c7477c7ff   3         3         3       23m

```

**金丝雀发布**

todo

### Horizontal Pod Autoscaler(HPA)

> 根据pod负载的变化自动调整pod的数量

### DaemonSet(DS)

> DaemonSet类型的控制器可以保证集群中的每一台节点上都运行一个pod副本，一般适用于日志收集、节点监控等场景。

```yaml
apiVersion: apps/v1
kind: DaemonSet
metadata:
 name: daemonset1
 namespace: dev
spec:
 selector:
  matchLabels:
   app: nginx-pod
 template:
  metadata:
   labels:
    app: nginx-pod
  spec:
   containers:
   - name: nginx
     image: nginx:1.17.1
```

```shell
[root@k8s-master ~]# kubectl create -f 1.yaml
daemonset.apps/daemonset1 created
[root@k8s-master ~]# kubectl get daemonset -n dev
NAME         DESIRED   CURRENT   READY   UP-TO-DATE   AVAILABLE   NODE SELECTOR   AGE
daemonset1   2         2         2       2            2           <none>          14s
# daemonset在每个节点上都运行一个pod
[root@k8s-master ~]# kubectl get pod -n dev -o wide
NAME               READY   STATUS    RESTARTS   AGE   IP            NODE        NOMINATED NODE   READINESS GATES
daemonset1-7qstq   1/1     Running   0          32s   10.244.2.41   k8s-node2   <none>           <none>
daemonset1-vz9sj   1/1     Running   0          32s   10.244.1.22   k8s-node1   <none>           <none>
[root@k8s-master ~]# kubectl delete -f 1.yaml
daemonset.apps "daemonset1" deleted
```

### Job

> 主要用于负责批量处理（一次要处理指定数量的任务）短暂的一次性（每个任务仅运行一次就结束）任务。

```yaml
apiVersion: batch/v1
kind: Job
metadata:
 name: job1
 namespace: dev
spec:
 manualSelector: true
 completions: 6 # 总共需要执行多少个pod
 parallelism: 3 # 并行运行pod的数量
 selector:
  matchLabels:
   app: counter-pod
 template:
  metadata:
   labels:
    app: counter-pod
  spec:
   restartPolicy: Never
   containers:
   - name: counter
     image: busybox
     command: ["/bin/sh", "-c", "for i in 9 8 7 6 5 4 3 2 1; do echo $i;sleep 2; done;"]
```

```shell
[root@k8s-master ~]# kubectl create -f 1.yaml
job.batch/job1 created
# 查看job运行状态
[root@k8s-master ~]# kubectl get job -n dev -o wide -w
NAME   COMPLETIONS   DURATION   AGE   CONTAINERS   IMAGES    SELECTOR
job1   0/6                      0s    counter      busybox   app=counter-pod
job1   0/6           0s         0s    counter      busybox   app=counter-pod
job1   1/6           20s        20s   counter      busybox   app=counter-pod
job1   2/6           35s        35s   counter      busybox   app=counter-pod
job1   3/6           35s        35s   counter      busybox   app=counter-pod
job1   4/6           55s        55s   counter      busybox   app=counter-pod
job1   5/6           55s        55s   counter      busybox   app=counter-pod
job1   6/6           55s        55s   counter      busybox   app=counter-pod
# 查看pod运行状态
[root@k8s-master ~]# kubectl get pod -n dev -w
NAME         READY   STATUS    RESTARTS   AGE
job1-bwgld   0/1     Pending   0          0s
job1-bwgld   0/1     Pending   0          0s
job1-67pt8   0/1     Pending   0          0s
job1-j7bwp   0/1     Pending   0          0s
job1-j7bwp   0/1     Pending   0          0s
job1-67pt8   0/1     Pending   0          0s
job1-bwgld   0/1     ContainerCreating   0          0s
job1-j7bwp   0/1     ContainerCreating   0          0s
job1-67pt8   0/1     ContainerCreating   0          0s
job1-bwgld   1/1     Running             0          17s
job1-j7bwp   1/1     Running             0          17s
job1-67pt8   1/1     Running             0          32s
job1-bwgld   0/1     Completed           0          35s
job1-l2qxx   0/1     Pending             0          0s
job1-l2qxx   0/1     Pending             0          0s
job1-l2qxx   0/1     ContainerCreating   0          0s
job1-j7bwp   0/1     Completed           0          35s
job1-5t9xj   0/1     Pending             0          0s
job1-5t9xj   0/1     Pending             0          0s
job1-5t9xj   0/1     ContainerCreating   0          0s
job1-l2qxx   1/1     Running             0          2s
job1-5t9xj   1/1     Running             0          2s
job1-67pt8   0/1     Completed           0          50s
job1-cthqp   0/1     Pending             0          0s
job1-cthqp   0/1     Pending             0          0s
job1-cthqp   0/1     ContainerCreating   0          0s
job1-l2qxx   0/1     Completed           0          20s
job1-5t9xj   0/1     Completed           0          20s
job1-cthqp   1/1     Running             0          17s
job1-cthqp   0/1     Completed           0          34s
# 删除job
[root@k8s-master ~]# kubectl delete -f 1.yaml
job.batch "job1" deleted
```

### CronJob(CJ)

> 指定特定的时间点重复执行job任务

todo

## k8s service

> 是一组同类pod对外访问接口，借助service，应用可以方便地实现服务发现和负载均衡
>
> 主要的流量负载组建分别为service（4层路由）和ingress（7层路由）

```shell
# 创建service
kubectl expose deployment nginx --name=service-nginx --port=80 --target-port=80 --type=ClusterIP -n dev

# 查看service
kubectl get service -n dev

# 删除service
kubectl delete service service-nginx -n dev
```

### service

service类型

- ClusterIP: 设置service的ip地址
- NodePort: 使用宿主机暴露服务给外部调用
- LoadBalancer: 结合外部LB使用
- ExernalName: 在集群内部引入外部服务

**环境准备**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
 namespace: dev
spec:
 replicas: 3
 selector:
  matchLabels:
   app: nginx-pod
 template:
  metadata:
   labels:
    app: nginx-pod
  spec:
   containers:
   - name: nginx
     image: nginx:1.17.1
     ports:
     - containerPort: 80
```

```shell
[root@k8s-master ~]# kubectl create -f 2.yaml
deployment.apps/deployment1 created
# 分别进入各个pod并修改index.html
[root@k8s-master ~]# kubectl exec -it deployment1-5ffc5bf56c-hk9mc -n dev /bin/sh
kubectl exec [POD] [COMMAND] is DEPRECATED and will be removed in a future version. Use kubectl exec [POD] -- [COMMAND] instead.
# echo "1" > /usr/share/nginx/html/index.html

# 确认index.html是否修改成功
[root@k8s-master ~]# kubectl get pod -n dev -o wide
NAME                           READY   STATUS    RESTARTS   AGE     IP            NODE        NOMINATED NODE   READINESS GATES
deployment1-5ffc5bf56c-hk9mc   1/1     Running   0          5m14s   10.244.2.53   k8s-node2   <none>           <none>
deployment1-5ffc5bf56c-qbsrt   1/1     Running   0          5m14s   10.244.1.28   k8s-node1   <none>           <none>
deployment1-5ffc5bf56c-s5lfb   1/1     Running   0          5m14s   10.244.2.52   k8s-node2   <none>           <none>
[root@k8s-master ~]# curl 10.244.2.53
1
```

**ClusterIP**

```yaml
apiVersion: v1
kind: Service
metadata:
 name: service1
 namespace: dev
spec:
 # 客户端地址会话保持莫斯
 # 如果不指定使用默认，随机、轮询
 #sessionAffinity: ClientIP
 selector:
  app: nginx-pod # 使用标签绑定service到指定的pod
 clusterIP: 10.1.97.97 # 不指定时，service会随机分配一个ip地址
 type: ClusterIP
 ports:
 - port: 81 # service端口
   targetPort: 80 # pod端口
```

```shell
[root@k8s-master ~]# kubectl create -f 1.yaml
service/service1 created
[root@k8s-master ~]# kubectl get service -n dev -o wide
NAME       TYPE        CLUSTER-IP   EXTERNAL-IP   PORT(S)   AGE   SELECTOR
service1   ClusterIP   10.1.97.97   <none>        81/TCP    41s   app=nginx-pod
# 测试ClusterIP会随机分配pod服务请求
[root@k8s-master ~]# while true;do curl 10.1.97.97:81;sleep 5;done;
2
1
1
3
[root@k8s-master ~]# kubectl delete -f 1.yaml 
service "service1" deleted
```

**headless(无头服务)**

> headless服务是通过service的dns解析访问相应的pod

```yaml
apiVersion: v1
kind: Service
metadata:
 name: service1
 namespace: dev
spec:
 # 客户端地址会话保持莫斯
 # 如果不指定使用默认，随机、轮询
 #sessionAffinity: ClientIP
 selector:
  app: nginx-pod # 使用标签绑定service到指定的pod
 clusterIP: None # headless无头服务
 type: ClusterIP
 ports:
 - port: 81 # service端口
   targetPort: 80 # pod端口
```

```shell
[root@k8s-master ~]# kubectl exec -it deployment1-5ffc5bf56c-hk9mc -n dev /bin/sh
kubectl exec [POD] [COMMAND] is DEPRECATED and will be removed in a future version. Use kubectl exec [POD] -- [COMMAND] instead.
# cat /etc/resolv.conf
nameserver 10.1.0.10
search dev.svc.cluster.local svc.cluster.local cluster.local
options ndots:5
#
# 使用dig命令解析headless service dns到对应的pod ip地址
[root@k8s-master ~]# dig @10.1.0.10 service1.dev.svc.cluster.local

; <<>> DiG 9.9.4-RedHat-9.9.4-61.el7 <<>> @10.1.0.10 service1.dev.svc.cluster.local
; (1 server found)
;; global options: +cmd
;; Got answer:
;; ->>HEADER<<- opcode: QUERY, status: NOERROR, id: 34426
;; flags: qr aa rd; QUERY: 1, ANSWER: 3, AUTHORITY: 0, ADDITIONAL: 1
;; WARNING: recursion requested but not available

;; OPT PSEUDOSECTION:
; EDNS: version: 0, flags:; udp: 4096
;; QUESTION SECTION:
;service1.dev.svc.cluster.local.	IN	A

;; ANSWER SECTION:
service1.dev.svc.cluster.local.	30 IN	A	10.244.1.28
service1.dev.svc.cluster.local.	30 IN	A	10.244.2.52
service1.dev.svc.cluster.local.	30 IN	A	10.244.2.53

;; Query time: 0 msec
;; SERVER: 10.1.0.10#53(10.1.0.10)
;; WHEN: Fri Dec 09 11:19:53 CST 2022
;; MSG SIZE  rcvd: 197
```

**NodePort**

> 暴露服务到外部访问

```yaml
apiVersion: v1
kind: Service
metadata:
 name: service1
 namespace: dev
spec:
 selector:
  app: nginx-pod # 使用标签绑定service到指定的pod
 type: NodePort
 ports:
 - port: 81 # service端口
   targetPort: 80 # pod端口 
   nodePort: 30002 # 宿主机端口，如果不指定service会随机分配一个端口
```

```shell
[root@k8s-master ~]# kubectl create -f 1.yaml 
service/service1 created

# 使用外部浏览器访问 http://192.168.1.xxx:30002

[root@k8s-master ~]# kubectl delete -f 1.yaml 
service "service1" deleted
```

**LoadBalancer**

todo

**ExternalName**

todo

> 在集群内存引入外部服务

### ingress

todo

## 数据存储(Volume)

### 简单存储

#### EmptyDir

> pod创建时会自动创建一个空的目录，无需指定宿主机目录，因为k8s系统会自动分配一个目录，在pod销毁时，emptydir中的数据也会被永久删除。

```yaml
apiVersion: v1
kind: Pod 
metadata:
 name: pod1
 namespace: dev 
spec:
 containers:
 - name: nginx
   image: nginx:1.17.1
   ports:
   - containerPort: 80
   volumeMounts:
    - name: logs-volume
      mountPath: /var/log/nginx
 - name: busybox
   image: busybox
   command: ["/bin/sh", "-c", "tail -f /logs/access.log"]
   volumeMounts:
   - name: logs-volume
     mountPath: /logs
 volumes:
 - name: logs-volume
   emptyDir: {}
```

```shell
[root@k8s-master ~]# kubectl get pod -n dev -o wide
NAME   READY   STATUS    RESTARTS   AGE   IP            NODE        NOMINATED NODE   READINESS GATES
pod1   2/2     Running   0          96s   10.244.2.55   k8s-node2   <none>           <none>
[root@k8s-master ~]# curl 10.244.2.55
<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
<style>
    body {
        width: 35em;
        margin: 0 auto;
        font-family: Tahoma, Verdana, Arial, sans-serif;
    }
</style>
</head>
<body>
<h1>Welcome to nginx!</h1>
<p>If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.</p>

<p>For online documentation and support please refer to
<a href="http://nginx.org/">nginx.org</a>.<br/>
Commercial support is available at
<a href="http://nginx.com/">nginx.com</a>.</p>

<p><em>Thank you for using nginx.</em></p>
</body>
</html>

# 查看busybox日志输出
[root@k8s-master ~]# kubectl logs -f pod1 -c busybox -n dev
10.244.0.0 - - [09/Dec/2022:06:02:47 +0000] "GET / HTTP/1.1" 200 612 "-" "curl/7.29.0" "-"
```

#### HostPath

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: pod1
 namespace: dev
spec:
 containers:
 - name: nginx
   image: nginx:1.17.1
   ports:
   - containerPort: 80
   volumeMounts:
   - name: logs-volume
     mountPath: /var/log/nginx
 - name: busybox
   image: busybox
   command: ["/bin/sh", "-c", "tail -f /logs/access.log"]
   volumeMounts:
   - name: logs-volume
     mountPath: /logs
 volumes:
 - name: logs-volume
   hostPath:
    path: /root/logs
    type: DirectoryOrCreate # 目录不存在就创建
```

```shell
[root@k8s-master ~]# kubectl create -f 1.yaml 
pod/pod1 created

[root@k8s-master ~]# kubectl logs -f pod1 -c busybox -n dev
10.244.0.0 - - [09/Dec/2022:07:13:50 +0000] "GET / HTTP/1.1" 200 612 "-" "curl/7.29.0" "-"

[root@k8s-master ~]# kubectl get pod -n dev -o wide
NAME   READY   STATUS    RESTARTS   AGE     IP            NODE        NOMINATED NODE   READINESS GATES
pod1   2/2     Running   0          2m22s   10.244.2.56   k8s-node2   <none>           <none>
[root@k8s-master ~]# curl 10.244.2.56
<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
<style>
    body {
        width: 35em;
        margin: 0 auto;
        font-family: Tahoma, Verdana, Arial, sans-serif;
    }
</style>
</head>
<body>
<h1>Welcome to nginx!</h1>
<p>If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.</p>

<p>For online documentation and support please refer to
<a href="http://nginx.org/">nginx.org</a>.<br/>
Commercial support is available at
<a href="http://nginx.com/">nginx.com</a>.</p>

<p><em>Thank you for using nginx.</em></p>
</body>
</html>
```

#### NFS

```shell
# 配置nfs服务
[root@k8s-master ~]# yum install nfs-utils -y
[root@k8s-master ~]# systemctl start nfs-server
[root@k8s-master ~]# systemctl enable nfs-server
Created symlink from /etc/systemd/system/multi-user.target.wants/nfs-server.service to /usr/lib/systemd/system/nfs-server.service.
[root@k8s-master ~]# mkdir /data
# /etc/exports添加 /data *(rw,sync,no_root_squash,no_subtree_check)
[root@k8s-master ~]# vim /etc/exports
[root@k8s-master ~]# exportfs -a
```

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: pod1
 namespace: dev
spec:
 containers:
 - name: nginx
   image: nginx:1.17.1
   ports:
   - containerPort: 80
   volumeMounts:
   - name: logs-volume
     mountPath: /var/log/nginx
 - name: busybox
   image: busybox
   command: ["/bin/sh", "-c", "tail -f /logs/access.log"]
   volumeMounts:
   - name: logs-volume
     mountPath: /logs
 volumes:
 - name: logs-volume
   nfs:
    server: 192.168.1.170
    path: /data
```

```shell
[root@k8s-master ~]# kubectl create -f 1.yaml 
pod/pod1 created
[root@k8s-master ~]# kubectl get pod -n dev -o wide
NAME   READY   STATUS    RESTARTS   AGE   IP            NODE        NOMINATED NODE   READINESS GATES
pod1   2/2     Running   0          23s   10.244.2.58   k8s-node2   <none>           <none>
[root@k8s-master ~]# curl 10.244.2.58
<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
<style>
    body {
        width: 35em;
        margin: 0 auto;
        font-family: Tahoma, Verdana, Arial, sans-serif;
    }
</style>
</head>
<body>
<h1>Welcome to nginx!</h1>
<p>If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.</p>

<p>For online documentation and support please refer to
<a href="http://nginx.org/">nginx.org</a>.<br/>
Commercial support is available at
<a href="http://nginx.com/">nginx.com</a>.</p>

<p><em>Thank you for using nginx.</em></p>
</body>
</html>

[root@k8s-master ~]# kubectl logs -f pod1 -c busybox -n dev
10.244.0.0 - - [09/Dec/2022:12:32:20 +0000] "GET / HTTP/1.1" 200 612 "-" "curl/7.29.0" "-"
```

### 高级存储

#### pv和pvc

> pv(Persistent Volume)是持久化卷的意思，是对底层共享存储的一种抽象。
>
> pvc(Persistent Volume Claim)是持久卷声明的意思，是用户对于存储需求的一种声明。换句话说，pvc其实是用户向k8s系统发出一种资源需求申请。

todo

### 配置存储

todo