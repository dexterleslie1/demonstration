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

### 私有镜像拉取时提供帐号和密码

> [链接1](https://kubernetes.io/docs/tasks/configure-pod-container/pull-image-private-registry/)

```shell
# 创建帐号和密码secret
[root@k8s-master ~]# kubectl create secret docker-registry regcred --docker-server=my.docker.hub --docker-username=xxx --docker-password=xxxx
# 查看secret
[root@k8s-master ~]# kubectl get secret regcred --output=yaml
```

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment-yyd-ops-db
spec:
 selector:
  matchLabels:
   app: yyd-ops-db
 template:
  metadata:
   labels:
    app: yyd-ops-db
  spec:
   # 提供私有镜像拉取帐号和密码
   imagePullSecrets:
   - name: regcred
   containers:
   - name: yyd-ops-db
     image: my.docker.hub/yyd-private/yyd-ops-db:1.0.0
     ports:
     - containerPort: 3306
     env:
     - name: MYSQL_ROOT_PASSWORD
       value: "123456"
     - name: TZ
       value: "Asia/Shanghai"
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

### StatefulSet

> RC、Deployment、DaemonSet都是面向无状态的服务，它们所管理的Pod的IP、名字，启停顺序等都是随机的，而StatefulSet是什么？顾名思义，有状态的集合，管理所有有状态的服务，比如MySQL、MongoDB集群等。
> StatefulSet本质上是Deployment的一种变体，在v1.9版本中已成为GA版本，它为了解决有状态服务的问题，它所管理的Pod拥有固定的Pod名称，启停顺序，在StatefulSet中，Pod名字称为网络标识(hostname)，还必须要用到共享存储。
>
> 在Deployment中，与之对应的服务是service，而在StatefulSet中与之对应的headless service，headless service，即无头服务，与service的区别就是它没有Cluster IP，解析它的名称时将返回该Headless Service对应的全部Pod的Endpoint列表。
>
> [链接1](https://www.jianshu.com/p/03cd2f2dc427)

**参考storageclass章节创建storageclass**

**创建statefulset.yaml**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: nginx
  labels:
    app: nginx
spec:
  ports:
  - port: 80
    name: web
  clusterIP: None
  selector:
    app: nginx
---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: web
spec:
  selector:
    matchLabels:
      app: nginx # has to match .spec.template.metadata.labels
  serviceName: "nginx"  #声明它属于哪个Headless Service.
  replicas: 3 # by default is 1
  template:
    metadata:
      labels:
        app: nginx # has to match .spec.selector.matchLabels
    spec:
      terminationGracePeriodSeconds: 10
      containers:
      - name: nginx
        image: nginx:1.20.1
        ports:
        - containerPort: 80
          name: web
        volumeMounts:
        - name: www
          mountPath: /usr/share/nginx/html
  volumeClaimTemplates:   #可看作pvc的模板
  - metadata:
      name: www
    spec:
      accessModes: [ "ReadWriteOnce" ]
      storageClassName: "nfs-client"  #存储类名，改为集群中已存在的
      resources:
        requests:
          storage: 1Gi
```

```shell
# kubectl create -f statefulset.yaml启动statefulset后
# 在各个nginx目录下创建index.html，如下所示
[root@k8s-master datass]# tree
.
├── default-test-claim-pvc-34bc5c37-2507-4c66-b470-76f199fc07f9
├── default-www-web-0-pvc-532ce5e0-c614-4c4c-abd1-dd664d88298f
│   └── index.html
├── default-www-web-1-pvc-210e4f4a-d006-422f-bf39-3b36e4af89ef
│   └── index.html
└── default-www-web-2-pvc-5b905a3a-aed4-44d3-b874-b11f34ae3434
    └── index.html
# 每个index.html <h1>Welcome to nginx1!</h1>不一样
[root@k8s-master default-www-web-1-pvc-210e4f4a-d006-422f-bf39-3b36e4af89ef]# cat index.html 
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
<h1>Welcome to nginx1!</h1>
<p>If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.</p>

<p>For online documentation and support please refer to
<a href="http://nginx.org/">nginx.org</a>.<br/>
Commercial support is available at
<a href="http://nginx.com/">nginx.com</a>.</p>

<p><em>Thank you for using nginx.</em></p>
</body>
</html>
# 分别请求3个nginx，返回的内容不一样
[root@k8s-master ~]# kubectl get pod -o wide
NAME                                      READY   STATUS    RESTARTS   AGE   IP            NODE        NOMINATED NODE   READINESS GATES
nfs-client-provisioner-859477c96c-stc5k   1/1     Running   0          10m   10.244.1.40   k8s-node1   <none>           <none>
web-0                                     1/1     Running   0          10s   10.244.2.80   k8s-node2   <none>           <none>
web-1                                     1/1     Running   0          8s    10.244.1.41   k8s-node1   <none>           <none>
web-2                                     1/1     Running   0          7s    10.244.2.81   k8s-node2   <none>           <none>
[root@k8s-master ~]# curl 10.244.2.80
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
<h1>Welcome to nginx0!</h1>
<p>If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.</p>

<p>For online documentation and support please refer to
<a href="http://nginx.org/">nginx.org</a>.<br/>
Commercial support is available at
<a href="http://nginx.com/">nginx.com</a>.</p>

<p><em>Thank you for using nginx.</em></p>
</body>
</html>

[root@k8s-master ~]# curl 10.244.1.41
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
<h1>Welcome to nginx1!</h1>
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

> headless服务是通过service的dns解析访问相应的pod，例如下面例子：在busybox pod中通过headless-service无头服务名称就能够访问两个nginx pod endpoints。

```yaml
apiVersion: v1
kind: Service
metadata:
 name: headless-service
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

---
apiVersion: apps/v1
kind: Deployment
metadata:
 name: headless-deployment1
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
     ports:
     - containerPort: 80

---
apiVersion: apps/v1
kind: Deployment
metadata:
 name: headless-deployment2
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
     ports:
     - containerPort: 80

---
apiVersion: apps/v1
kind: Deployment
metadata:
 name: headless-deployment-busybox
spec:
 selector:
  matchLabels:
   app: busybox-1
 template:
  metadata:
   labels:
    app: busybox-1
  spec:
   containers:
   - name: busybox
     image: busybox:1.28
     command: ["/bin/sh", "-c", "sleep 3600"]
```

```shell
[root@k8s-master ~]# kubectl get pod
NAME                                         READY   STATUS    RESTARTS   AGE
headless-deployment-busybox-b9db9bbb-vsrvm   1/1     Running   0          4m36s
headless-deployment1-5ffc5bf56c-njvcl        1/1     Running   0          4m36s
headless-deployment2-5ffc5bf56c-786mm        1/1     Running   0          4m36s
nfs-client-provisioner-859477c96c-stc5k      1/1     Running   0          117m
web-0                                        1/1     Running   0          106m
web-1                                        1/1     Running   0          106m
web-2                                        1/1     Running   0          106m
# 进入busybox容器测试headless service
[root@k8s-master ~]# kubectl exec -it headless-deployment-busybox-b9db9bbb-vsrvm /bin/sh
kubectl exec [POD] [COMMAND] is DEPRECATED and will be removed in a future version. Use kubectl exec [POD] -- [COMMAND] instead.
/ # ping headless-service
PING headless-service (10.244.2.90): 56 data bytes
64 bytes from 10.244.2.90: seq=0 ttl=62 time=0.526 ms
64 bytes from 10.244.2.90: seq=1 ttl=62 time=0.624 ms
64 bytes from 10.244.2.90: seq=2 ttl=62 time=0.593 ms
^C
--- headless-service ping statistics ---
3 packets transmitted, 3 packets received, 0% packet loss
round-trip min/avg/max = 0.526/0.581/0.624 ms
/ # ping headless-service
PING headless-service (10.244.2.91): 56 data bytes
64 bytes from 10.244.2.91: seq=0 ttl=62 time=0.628 ms
64 bytes from 10.244.2.91: seq=1 ttl=62 time=0.578 ms
64 bytes from 10.244.2.91: seq=2 ttl=62 time=0.587 ms
64 bytes from 10.244.2.91: seq=3 ttl=62 time=0.543 ms
^C
--- headless-service ping statistics ---
4 packets transmitted, 4 packets received, 0% packet loss
round-trip min/avg/max = 0.543/0.584/0.628 ms
/ # exit

# 使用dig命令解析headless service dns到对应的pod ip地址
[root@k8s-master ~]# dig @10.1.0.10 headless-service.default.svc.cluster.local

; <<>> DiG 9.9.4-RedHat-9.9.4-61.el7 <<>> @10.1.0.10 headless-service.default.svc.cluster.local
; (1 server found)
;; global options: +cmd
;; Got answer:
;; ->>HEADER<<- opcode: QUERY, status: NOERROR, id: 35058
;; flags: qr aa rd; QUERY: 1, ANSWER: 2, AUTHORITY: 0, ADDITIONAL: 1
;; WARNING: recursion requested but not available

;; OPT PSEUDOSECTION:
; EDNS: version: 0, flags:; udp: 4096
;; QUESTION SECTION:
;headless-service.default.svc.cluster.local. IN A

;; ANSWER SECTION:
headless-service.default.svc.cluster.local. 30 IN A 10.244.2.91
headless-service.default.svc.cluster.local. 30 IN A 10.244.2.90

;; Query time: 1 msec
;; SERVER: 10.1.0.10#53(10.1.0.10)
;; WHEN: Thu Dec 15 13:18:58 CST 2022
;; MSG SIZE  rcvd: 187
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

**创建nfs**

```shell
# 分别在三个节点上安装nfs-utils
[root@k8s-master ~]# yum install nfs-utils -y
# 在master节点上启动nfs-server
[root@k8s-master ~]# systemctl start nfs-server
[root@k8s-master ~]# systemctl enable nfs-server
# 在master节点创建三个pv目录
[root@k8s-master ~]# mkdir /data/{pv1,pv2,pv3} -pv
mkdir: created directory ‘/data/pv1’
mkdir: created directory ‘/data/pv2’
mkdir: created directory ‘/data/pv3’
# 编辑/etc/exports加入如下内容
[root@k8s-master ~]# cat /etc/exports
/data/pv1 *(rw,sync,no_root_squash,no_subtree_check)
/data/pv2 *(rw,sync,no_root_squash,no_subtree_check)
/data/pv3 *(rw,sync,no_root_squash,no_subtree_check)
# 重启nfs-server服务
[root@k8s-master ~]# systemctl restart nfs-server
# 显示被nfs export的目录
[root@k8s-master ~]# showmount -e
Export list for k8s-master:
/datass   *
/data/pv3 *
/data/pv2 *
/data/pv1 *
```

**创建pv**

```yaml
apiVersion: v1
kind: PersistentVolume
metadata:
 name: pv1
spec:
 capacity: 
  storage: 2Gi
 accessModes:
 - ReadWriteMany
 persistentVolumeReclaimPolicy: Retain
 nfs:
  path: /data/pv1
  server: 192.168.1.170
```

```shell
[root@k8s-master ~]# kubectl create -f 1.yaml 
persistentvolume/pv1 created
[root@k8s-master ~]# kubectl get persistentvolume
NAME   CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS      CLAIM   STORAGECLASS   REASON   AGE
pv1    2Gi        RWX            Retain           Available                                   19s
```

**创建pvc**

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
 name: pvc1
 namespace: dev
spec:
 accessModes:
 - ReadWriteMany
 resources:
  requests:
   storage: 1Gi
```

```shell
[root@k8s-master ~]# kubectl create -f 2.yaml 
persistentvolumeclaim/pvc1 created
[root@k8s-master ~]# kubectl get persistentvolumeclaim -n dev
NAME   STATUS   VOLUME   CAPACITY   ACCESS MODES   STORAGECLASS   AGE
pvc1   Bound    pv1      2Gi        RWX                           9m39s
```

**pod使用pvc**

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: pod1
 namespace: dev
spec:
 containers:
 - name: busybox
   image: busybox
   command: ["/bin/sh", "-c", "while true;do echo pod1 >> /root/out.txt; sleep 10; done;"]
   volumeMounts:
   - name: volume
     mountPath: /root/
 volumes:
 - name: volume
   persistentVolumeClaim:
    claimName: pvc1
    readOnly: false
```

```shell
# 使用pvc
[root@k8s-master ~]# kubectl create -f pod.yaml 
pod/pod1 created
[root@k8s-master pv1]# tail -f out.txt 
pod1
pod1
pod1
```

#### storageclass

> 根据pvc自动创建pv
>
> [链接1](https://github.com/kubernetes-sigs/nfs-subdir-external-provisioner/blob/master/deploy/test-claim.yaml) [链接2](https://zahui.fan/posts/179eb842/)

**参考pv和pvc章节配置nfs服务器**

**创建rbac.yarml**

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: nfs-client-provisioner
  namespace: default
---
kind: ClusterRole
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: nfs-client-provisioner-runner
rules:
  - apiGroups: [""]
    resources: ["nodes"]
    verbs: ["get", "list", "watch"]
  - apiGroups: [""]
    resources: ["persistentvolumes"]
    verbs: ["get", "list", "watch", "create", "delete"]
  - apiGroups: [""]
    resources: ["persistentvolumeclaims"]
    verbs: ["get", "list", "watch", "update"]
  - apiGroups: ["storage.k8s.io"]
    resources: ["storageclasses"]
    verbs: ["get", "list", "watch"]
  - apiGroups: [""]
    resources: ["events"]
    verbs: ["create", "update", "patch"]
---
kind: ClusterRoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: run-nfs-client-provisioner
subjects:
  - kind: ServiceAccount
    name: nfs-client-provisioner
    namespace: default
roleRef:
  kind: ClusterRole
  name: nfs-client-provisioner-runner
  apiGroup: rbac.authorization.k8s.io
---
kind: Role
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: leader-locking-nfs-client-provisioner
  namespace: default
rules:
  - apiGroups: [""]
    resources: ["endpoints"]
    verbs: ["get", "list", "watch", "create", "update", "patch"]
---
kind: RoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: leader-locking-nfs-client-provisioner
  namespace: default
subjects:
  - kind: ServiceAccount
    name: nfs-client-provisioner
    namespace: default
roleRef:
  kind: Role
  name: leader-locking-nfs-client-provisioner
  apiGroup: rbac.authorization.k8s.io
```

```shell
[root@k8s-master ~]# kubectl create -f rbac.yaml 
serviceaccount/nfs-client-provisioner created
clusterrole.rbac.authorization.k8s.io/nfs-client-provisioner-runner created
clusterrolebinding.rbac.authorization.k8s.io/run-nfs-client-provisioner created
role.rbac.authorization.k8s.io/leader-locking-nfs-client-provisioner created
rolebinding.rbac.authorization.k8s.io/leader-locking-nfs-client-provisioner created
```

**创建deployment.yaml**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nfs-client-provisioner
  labels:
    app: nfs-client-provisioner
  namespace: default
spec:
  replicas: 1
  strategy:
    type: Recreate
  selector:
    matchLabels:
      app: nfs-client-provisioner
  template:
    metadata:
      labels:
        app: nfs-client-provisioner
    spec:
      serviceAccountName: nfs-client-provisioner
      containers:
        - name: nfs-client-provisioner
          image: registry.cn-hangzhou.aliyuncs.com/iuxt/nfs-subdir-external-provisioner:v4.0.2
          volumeMounts:
            - name: nfs-client-root
              mountPath: /persistentvolumes
          env:
			# 必须与storageclass.yaml中的provisioner的名称一致
            - name: PROVISIONER_NAME
              value: k8s-sigs.io/nfs-subdir-external-provisioner
            - name: NFS_SERVER
              value: 192.168.1.170
            - name: NFS_PATH
              value: /datass
      volumes:
        - name: nfs-client-root
          nfs:
            server: 192.168.1.170
            path: /datass
```

```shell
[root@k8s-master ~]# kubectl create -f deployment.yaml 
deployment.apps/nfs-client-provisioner created
```

**创建storageclass.yaml**

```yaml
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: nfs-client
# 必须与deployment.yaml中的PROVISIONER_NAME一致
provisioner: k8s-sigs.io/nfs-subdir-external-provisioner # or choose another name, must match deployment's env PROVISIONER_NAME'
parameters:
  # https://help.aliyun.com/document_detail/144398.html
  archiveOnDelete: "false"
```

```shell
[root@k8s-master ~]# kubectl create -f storageclass.yaml 
storageclass.storage.k8s.io/nfs-client created
```

**使用创建test-claim.yaml测试**

```yaml
kind: PersistentVolumeClaim
apiVersion: v1
metadata:
  name: test-claim
spec:
  storageClassName: nfs-client
  accessModes:
    - ReadWriteMany
  resources:
    requests:
      storage: 1Mi
```

```shell
[root@k8s-master ~]# kubectl create -f test-claim.yaml 
persistentvolumeclaim/test-claim created
[root@k8s-master ~]# kubectl get pvc
NAME         STATUS   VOLUME                                     CAPACITY   ACCESS MODES   STORAGECLASS   AGE
test-claim   Bound    pvc-34bc5c37-2507-4c66-b470-76f199fc07f9   1Mi        RWX            nfs-client     8s
```



### 配置存储

#### configmap

**键值对存储**

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: configmap1
data:
 1.properties: |
  username: admin
  password: 123456

---
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
 - name: busybox
   image: busybox
   command: ["/bin/sh", "-c", "sleep 3600;"]
   volumeMounts:
   - name: volume1
     mountPath: /root/
 volumes:
 - name: volume1
   configMap:
    name: configmap1
```

```shell
[root@k8s-master ~]# kubectl get configmap
NAME               DATA   AGE
configmap1         1      2m15s
kube-root-ca.crt   1      10d
# 显示configmap详细信息
[root@k8s-master ~]# kubectl describe configmap configmap1
Name:         configmap1
Namespace:    default
Labels:       <none>
Annotations:  <none>

Data
====
1.properties:
----
username: admin
password: 123456

Events:  <none>
# 进入pod查看1.properties
[root@k8s-master ~]# kubectl exec -it pod1 /bin/sh
kubectl exec [POD] [COMMAND] is DEPRECATED and will be removed in a future version. Use kubectl exec [POD] -- [COMMAND] instead.
/ # cat /root/1.properties 
username: admin
password: 123456
/ # 
```

**nginx.conf配置存储**

> [链接1](https://stackoverflow.com/questions/51268488/kubernetes-configmap-set-from-file-in-yaml-configuration)

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: configmap1
data:
 nginx.conf: |
  #user  nobody;
    #worker_processes  1;
    worker_rlimit_nofile 65535;

    #error_log  logs/error.log;
    #error_log  logs/error.log  notice;
    #error_log  logs/error.log  info;

    #pid        logs/nginx.pid;
    error_log  logs/error.log  notice;

    events {
        worker_connections  65535;
    }


    http {
        #log_format access '[$time_local] "$request" $status $request_body "$http_refferer" "$http_user_agent" $http_x_forwarded_for';
        include       mime.types;
        #include       /usr/local/openresty/nginx/conf/naxsi_core.rules;
        default_type  application/octet-stream;

        #log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
        #                  '$status $body_bytes_sent "$http_referer" '
        #                  '"$http_user_agent" "$http_x_forwarded_for"';

        #access_log  logs/access.log  main;

        sendfile        on;
        #tcp_nopush     on;

        #keepalive_timeout  0;
        keepalive_timeout  65;

        #gzip  on;
        gzip on;
        gzip_min_length 1k;
        gzip_buffers 16 64k;
        gzip_http_version 1.1;
        gzip_comp_level 6;
        gzip_types application/json text/plain application/javascript text/css application/xml;
        gzip_vary on;
        server_tokens off;
        autoindex off;
        access_log off;
        client_body_buffer_size  10k;
        client_header_buffer_size 1k;
        client_max_body_size 2m;
        large_client_header_buffers 2 8k;
        gzip_proxied any;

        # 反向代理配置
        proxy_buffering on;
        proxy_buffer_size 8k;
        proxy_buffers 32 8k;
        proxy_busy_buffers_size 16k;

        proxy_cache_path /tmp/proxy_cache levels=1:2 keys_zone=cache_one:200m inactive=1d max_size=2g use_temp_path=off;

        upstream backend {
            keepalive 1024;
            server yyd-ops-api-dev:8080;
        }

        server {
            listen       80;
            server_name  localhost;

            #charset koi8-r;

            #access_log  logs/host.access.log  main;

            set $naxsi_extensive_log 1;

            location / {
                #include /usr/local/openresty/nginx/conf/naxsi.rules;
                proxy_set_header Host $host:$server_port;
                #proxy_set_header x-forwarded-for $remote_addr;
                proxy_http_version 1.1;
                proxy_set_header Connection '';
                proxy_pass http://backend;
            }

            location /request_denied {
                default_type application/json;
                return 403 '{"errorCode":600,"errorMessage":"您提交数据存在安全问题，被服务器拒绝，修改数据后重试"}';
            }
        }
    }

---
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
 - name: busybox
   image: busybox
   command: ["/bin/sh", "-c", "sleep 3600;"]
   volumeMounts:
   - name: volume1
     mountPath: /root/nginx.conf
     subPath: nginx.conf
 volumes:
 - name: volume1
   configMap:
    name: configmap1
    items:
    - key: nginx.conf
      path: nginx.conf
```

```shell
# 查看configmap
[root@k8s-master ~]# kubectl get configmap
NAME               DATA   AGE
configmap1         1      10s
kube-root-ca.crt   1      10d
[root@k8s-master ~]# kubectl describe configmap configmap1
Name:         configmap1
Namespace:    default
Labels:       <none>
Annotations:  <none>

Data
====
nginx.conf:
----
#user  nobody;
  #worker_processes  1;
  worker_rlimit_nofile 65535;
......

# 进入容器查看nginx.conf
[root@k8s-master ~]# kubectl exec -it pod1 /bin/sh
kubectl exec [POD] [COMMAND] is DEPRECATED and will be removed in a future version. Use kubectl exec [POD] -- [COMMAND] instead.
/ # cat /root/nginx.conf 
#user  nobody;
  #worker_processes  1;
  worker_rlimit_nofile 65535;
......
```

