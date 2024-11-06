# `pod`的基础

## 什么是`pod`呢？

`pod`代表了`kubernetes`中的基本部署单元。

通常一个`pod`只包含一个容器。

同一个`pod`中的容器之间的部分隔离，他们在相同的`network`中`UTS`命名空间下运行因此能够通过`IPC`进行通讯。

同一个`pod`中容器运行与相同的`network`命名空间中，因此他们共享相同的`ip`地址和端口空间。

同一个`pod`中所有容器具有相同的`loopback`网络接口，因此容器可以通过`localhost`与同一`pod`中的其他容器进行通讯。

当一个`pod`包含多个容器时，这些容器总是运行于同一个工作节点上，一个`pod`绝不会跨多个工作节点。

如果一个`pod`运行一个前端和后端容器组成，那么当你扩大`pod`的实例数量时，比如扩大为两个，最终会得到两个前端容器和两个后端容器是不好的结果。

在`pod`中使用多个容器的情况：`pod`应该包含紧密的耦合的容器组(通常是一个主容器和支持主容器的其他容器)，它们需要一起运行还是可以在不同的主机上运行？它们代表的是一个整体还是相互独立的组件？他们必须一起扩容还是可以分别进行？



## 标签

标签可以组织`k8s`资源
可以通过标签选择`k8s`资源

### 创建`pod`时指定标签

```shell
[root@k8s-master ~]# cat 1.yaml 
apiVersion: v1
kind: Pod
metadata:
 name: pod1
 labels:
  creation_method: manual
  env: prod
spec:
 containers:
 - name: nginx
   image: nginx
   
[root@k8s-master ~]# kubectl create -f 1.yaml 
pod/pod1 created

# 显示标签
[root@k8s-master ~]# kubectl get pod --show-labels
NAME   READY   STATUS    RESTARTS   AGE   LABELS
pod1   1/1     Running   0          83s   creation_method=manual,env=prod

# 只列出指定的标签creation_method、env
[root@k8s-master ~]# kubectl get pod -L creation_method,env
NAME   READY   STATUS    RESTARTS   AGE     CREATION_METHOD   ENV
pod1   1/1     Running   0          2m55s   manual            prod
```

### 修改现有的`pod`标签

```shell
# 新增标签
[root@k8s-master ~]# kubectl label pod pod1 mylabel1=v1
pod/pod1 labeled

# 修改现有的标签env=prod为env=debug
[root@k8s-master ~]# kubectl label pod pod1 env=debug --overwrite
pod/pod1 labeled

[root@k8s-master ~]# kubectl get pod --show-labels
NAME   READY   STATUS    RESTARTS   AGE     LABELS
pod1   1/1     Running   0          7m12s   creation_method=manual,env=debug,mylabel1=v1

# 删除标签
kubectl label pod nginx -n dev version-
```

### 使用标签选择器列出`pod`

```shell
# 列出标签creation_method=manual的pod
[root@k8s-master ~]# kubectl get pod -l creation_method=manual
NAME   READY   STATUS    RESTARTS   AGE
pod1   1/1     Running   0          9m20s

# 列出包含标签env的所有pod，无论标签值如何
[root@k8s-master ~]# kubectl get pod -l env
NAME   READY   STATUS    RESTARTS   AGE
pod1   1/1     Running   0          11m

# 列出没有env1标签的所有pod
[root@k8s-master ~]# kubectl get pod -l '!env1'
NAME   READY   STATUS    RESTARTS   AGE
pod1   1/1     Running   0          12m

# 多个标签筛选pod，使用逗号分隔
[root@k8s-master ~]# kubectl get pod -l creation_method=manual,env=debug
NAME   READY   STATUS    RESTARTS   AGE
pod1   1/1     Running   0          19m
```

### 使用标签调度`pod`到指定节点

```shell
# 查看node标签
[root@k8s-master ~]# kubectl get nodes --show-labels
NAME         STATUS   ROLES                  AGE   VERSION   LABELS
k8s-master   Ready    control-plane,master   27d   v1.20.0   beta.kubernetes.io/arch=amd64,beta.kubernetes.io/os=linux,kubernetes.io/arch=amd64,kubernetes.io/hostname=k8s-master,kubernetes.io/os=linux,node-role.kubernetes.io/control-plane=,node-role.kubernetes.io/master=
k8s-node1    Ready    <none>                 27d   v1.20.0   beta.kubernetes.io/arch=amd64,beta.kubernetes.io/os=linux,kubernetes.io/arch=amd64,kubernetes.io/hostname=k8s-node1,kubernetes.io/os=linux,node-label=node1
k8s-node2    Ready    <none>                 26d   v1.20.0   beta.kubernetes.io/arch=amd64,beta.kubernetes.io/os=linux,kubernetes.io/arch=amd64,kubernetes.io/hostname=k8s-node2,kubernetes.io/os=linux,node-label=node2

# pod只会分配到标签node-label=node1的节点运行
[root@k8s-master ~]# cat 1.yaml 
apiVersion: v1
kind: Pod
metadata:
 name: pod1
 labels:
  creation_method: manual
  env: prod
spec:
 # 指定node-label=node1的节点运行pod
 nodeSelector:
  node-label: node1
 containers:
 - name: nginx
   image: nginx
```



## 镜像拉取策略

- `Always`：总是从远程仓库拉取镜像
- `IfNotPresent`：本地有则使用本地，否则拉取远程仓库
- `Never`：只使用本地，从不去远程拉取，本地没有则报错

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

## 启动命令`command`和`args`

> [Define a Command and Arguments for a Container](https://kubernetes.io/docs/tasks/inject-data-application/define-command-argument-container/)

### 使用`command`指定`args`

```bash
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
   
# 进入容器查看/tmp/hello.txt输出，base-pod是pod的名称，busybox是容器名称
kubectl exec base-pod -n dev -it -c busybox /bin/sh

# 进入容器后查看/tmp/hello.txt
tail -f /tmp/hello.txt
```



### 指定`pod`中`command`执行的脚本

`1.yaml`内容如下：

```yaml
---
apiVersion: batch/v1
kind: Job
metadata:
  name: demo
spec:
  manualSelector: true
  completions: 1 # 总共需要执行多少个pod
  parallelism: 1 # 并行运行pod的数量，如果不指定表示一个一个执行
  selector:
    matchLabels:
      app: demo
  template:
    metadata:
      labels:
        app: demo
    spec:
      restartPolicy: OnFailure
      containers:
        - name: demo
          image: busybox
          imagePullPolicy: IfNotPresent
          command:
            - sh
            - /my-scripts/entrypoint.sh
          volumeMounts:
            - name: entrypoint-sh
              mountPath: /my-scripts/
      volumes:
        - name: entrypoint-sh
          configMap:
            name: demo-configuration
            items:
              - key: entrypoint.sh
                path: entrypoint.sh
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: demo-configuration
  namespace: "default"
data:
  entrypoint.sh: |-
    #!/bin/bash
    
    for i in $(seq 1 15)
    do
      echo "key$i=value$i"
    done

```

启动`pod`

```bash
kubectl apply -f 1.yaml
```

查看`pod`日志是否符合预期

```bash
kubectl logs -f `kubectl get pod |grep demo- | awk '{print $1}'`
```

删除`pod`

```bash
kubectl delete -f 1.yaml
```



### 在`kubernetes`中覆盖命令和参数

```bash
# 创建支持一个传入参数的容器
# entrypoint.sh内容如下:
#!/bin/sh

echo `date` - app is going to sleep $1 seconds...
sleep $1
echo `date` - app sleep finishing.

# Dockerfile内容如下:
FROM busybox

COPY entrypoint.sh /
RUN chmod a+x /entrypoint.sh
CMD ["5"]
ENTRYPOINT ["sh", "/entrypoint.sh"]

# 编译镜像
docker build --tag registry.cn-hangzhou.aliyuncs.com/future-public/demo-k8s-args .

# 以默认参数运行容器
docker run --rm --name=demo registry.cn-hangzhou.aliyuncs.com/future-public/demo-k8s-args

# 以自定义参数运行容器
docker run --rm --name=demo registry.cn-hangzhou.aliyuncs.com/future-public/demo-k8s-args 1

# 推送镜像
docker push registry.cn-hangzhou.aliyuncs.com/future-public/demo-k8s-args

# 以自定义entrypoint和参数运行pod，command覆盖docker entrypoint，args覆盖docker cmd参数
# 1.yaml内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: kubia
    image: registry.cn-hangzhou.aliyuncs.com/future-public/demo-k8s-args
    command: ["sh", "/entrypoint.sh"]
    args: ["11"]
    
# 查看pod输出日志
kubectl logs -f pod1
    
# 1.yaml内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: kubia
    image: registry.cn-hangzhou.aliyuncs.com/future-public/demo-k8s-args
    args: ["6"]

# 查看pod输出日志
kubectl logs -f pod1

# 另外一种传递参数方法
# 1.yaml内容如下: 
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: kubia
    image: registry.cn-hangzhou.aliyuncs.com/future-public/demo-k8s-args
    args:
     - "7"
     
# 查看pod输出日志
kubectl logs -f pod1
```



## 环境变量

### `pod`中使用`env`配置环境变量

```bash
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
     
# 进入容器
kubectl exec base-pod -n dev -it -c busybox /bin/sh

# 打印环境变量
echo $username
echo $password
```

### 使用环境变量传递参数

```shell
# 制作使用环境变量的容器
# entrypoint.sh 内容如下:
#!/bin/sh

echo `date` - app is going to sleep $SleepSeconds seconds...
sleep $SleepSeconds
echo `date` - app sleep finishing.

# Dockerfile 内容如下:
FROM busybox

COPY entrypoint.sh /
RUN chmod a+x /entrypoint.sh
ENTRYPOINT ["sh", "/entrypoint.sh"]

# 编译镜像
docker build --tag registry.cn-hangzhou.aliyuncs.com/future-public/demo-k8s-env .

# 使用参数运行容器
docker run --rm --name=demo --env SleepSeconds=3 registry.cn-hangzhou.aliyuncs.com/future-public/demo-k8s-env

# 推送镜像
docker push registry.cn-hangzhou.aliyuncs.com/future-public/demo-k8s-env

# 使用env传递环境变量
# 1.yaml内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: kubia
    image: registry.cn-hangzhou.aliyuncs.com/future-public/demo-k8s-env
    env:
     - name: SleepSeconds
       value: "8"

# 查看pod日志
kubectl logs -f pod1
```



## 端口设置

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

## 资源配额

- limits: 用于限制容器运行时占用资源的最大值，容器占用资源超过limits限制时，容器被终止并且重启
- requests: 用于设置容器启动时的最小资源，如果资源不足，容器将不能启动

```shell
# 演示内存限制
# https://blog.csdn.net/weixin_48819467/article/details/124801452
[root@k8s-master ~]# cat 1.yaml 
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: demo
    image: polinux/stress
    command: ["stress"]
    args:
     - --vm
     - "1"
     - --vm-bytes
     - 200M
    resources:
     requests:
      memory: 50Mi
     limits:
      memory: 100Mi
# 内存溢出OOMKilled
[root@k8s-master ~]# kubectl get pod -w
NAME                                      READY   STATUS              RESTARTS   AGE
pod1                                      0/1     ContainerCreating   0          17s
pod1                                      0/1     OOMKilled           0          21s
```

## `init（初始化）`容器使用

> 使用init容器模拟等待mysql和redis启动后才启动nginx的过程

yaml 内容如下：

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: base-pod
 labels:
  test1: test1v
spec:
 containers:
 - name: nginx
   image: nginx
 initContainers:
 - name: test-mysql
   image: busybox
   command: ["sh", "-c", "i=1;until [ $i -ge 15 ]; do echo 'simulating waiting for mysql...'; sleep 1; i=$((i+1)); done;"]
 - name: test-redis
   image: busybox
   command: ["sh", "-c", "i=1;until [ $i -ge 15 ]; do echo 'simulating waiting for mysql...'; sleep 1; i=$((i+1)); done;"]
```

观察init容器和main容器过程

```sh
kubectl get pod
```

查看 init 容器状态和日志，https://kubernetes.io/docs/tasks/debug/debug-application/debug-init-containers/

```sh
kubectl describe pod base-pod
kubectl logs base-pod -c test-mysql
kubectl logs base-pod -c test-redis
```

## 钩子函数`postStart`、`preStop`

在`main`容器启动后`postStart`和关闭前`preStop`执行指定的动作，每个容器启动后和停止前执行的。

### `postStart`测试

```shell
[root@k8s-master ~]# cat 1.yaml 
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
spec:
 replicas: 2
 selector:
  matchLabels:
   app: kubia
 template:
  metadata:
   labels:
    app: kubia
  spec:
   containers:
    - name: nginx
      image: nginx
      lifecycle:
       postStart:
        exec:
         command: ["sh", "-c", "echo hello > /1.txt"]
[root@k8s-master ~]# kubectl apply -f 1.yaml 
deployment.apps/deployment1 created
[root@k8s-master ~]# kubectl get pod
NAME                                      READY   STATUS    RESTARTS   AGE
deployment1-678b9fdbf6-4jh5v              1/1     Running   0          2m3s
deployment1-678b9fdbf6-72mgg              1/1     Running   0          2m3s
# 每个pod都执行一次钩子函数
[root@k8s-master ~]# kubectl exec deployment1-678b9fdbf6-4jh5v -- cat /1.txt
hello
[root@k8s-master ~]# kubectl exec deployment1-678b9fdbf6-72mgg -- cat /1.txt
hello
```



### 通过`pod events`列表查看`postStart`失败日志

yaml 内容如下：

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - image: nginx:alpine
    name: nginx
    lifecycle:
       postStart:
        exec:
         command:
         - sh
         - -c
         - "echo '你好世界'; exit 1; date;"
```

查看失败日志

```bash
kubectl describe pod pod1
```



## 重启策略

`liveness`探测失败后使用`restartPolicy`重启策略重启容器，`Never`表示不重启容器，`Always`表示总是重启容器，`OnFailure`表示容器退出并且退出码不为0时重启容器

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

