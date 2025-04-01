# `kubectl`命令



## `ubuntu`安装`kubectl`命令

> [Install and Set Up kubectl on Linux](https://kubernetes.io/docs/tasks/tools/install-kubectl-linux/)

```bash
# 下载指定版本kubectl二进制程序
curl -LO https://dl.k8s.io/release/v1.23.0/bin/linux/amd64/kubectl

# 把当前目录中kubectl二进制程序移动到/usr/bin
sudo mv kubectl /usr/bin/

# /usr/bin/kubectl添加执行权限
sudo chmod +x /usr/bin/kubectl

## 配置kubectl认证信息
# 复制kubernetes master ~/.kube/config文件到ubuntu的 ~/.kube/config 中

# 查看kubectl版本
kubectl version

# 验证认证信息是否正确
kubectl cluster-info
```



## `kubectl`输出`http`调试日志

> https://www.shellhacks.com/kubectl-debug-increase-verbosity/
>
> kubectl调试日志等级从0到9共10个等级，数值越大调试日志越详细。

```shell
# 用于协助调试kubectl调用了哪些RESTApi
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
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

# 使用最详细的日志模式调试kubectl
kubectl apply -f 3.yaml --v=9
```



## `kubectl get`用法



### `kubectl get`检查现有`pod`对应的`yaml`描述文件

```bash
# -o yaml表示以yaml格式输出pod的定义
# apiVersion: v1 描述文件所使用的kubernetes api版本
# kind: Pod kubernetes对象资源类型
# metadata pod元数据（名称、标签和注解等）
# spec pod规则/内容（pod的容器列表、volume等）
# status pod及其内部容器的详细状态
kubectl get pod kube-scheduler-demo-k8s-master -n kube-system -o yaml

# 获取pod列表，-n dev表示命名空间dev下的pod
kubectl get pod -n dev

# 获取pod列表并输出pod所在的k8s node和pod ip信息
kubectl get pod -n dev -o wide
```



### `kubectl get`显示资源的标签

```bash
# 1.yaml内容如下: 
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

# 创建资源
kubectl create -f 1.yaml

# 显示标签
kubectl get pod --show-labels

# 只列出指定的标签creation_method、env并分别显示在自己列中
kubectl get pod -L creation_method,env
```



### `kubectl get`通过标签选择其筛选资源

```bash
# 1.yaml内容如下: 
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

# 创建资源
kubectl create -f 1.yaml

# 显示标签
kubectl get pod --show-labels

# 使用标签选择其列出pod
kubectl get pod -l creation_method=manual

# 列出没有env1标签的资源
kubectl get pod -l '!env1'

# 多个标签筛选pod，使用逗号分隔
kubectl get pod -l creation_method=manual,env=prod

# 选择creation_method!=auto的资源
kubectl get pod -l creation_method!=auto
```



### `kubectl get pod --namespace`查询指定命名空间下的`pod`

```bash
# 查询命名kube-system下的pod
kubectl get pod --namespace kube-system
kubectl get pod -n kube-system
```



### `kubectl get ns`命名空间

```bash
# 获取所有命名空间
kubectl get ns
```



## `kubectl create`



### `kubectl create`创建`pod`

```bash
# 1.yaml内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1 # pod名称为pod1
 labels:
  test1: test1v
spec:
 containers:
 - name: nginx
   image: nginx
   
# 创建pod
kubectl create -f 1.yaml

# 在pod列表中查看新创建的pod
kubectl get pods

# 删除pod
kubectl delete -f 1.yaml
```



### `kubectl create`从`yaml`文件创建命名空间

```bash
# 1.yaml内容如下:
apiVersion: v1
kind: Namespace
metadata:
 name: custom-namespace
 
# 创建命名空间
kubectl create -f 1.yaml
```



### `kubectl create namespace`创建命名空间

```bash
# 创建命名空间
kubectl create namespace custom-namespace
```



### `kubectl create`创建资源时指定命名空间

```bash
# 创建命名空间
kubectl create namespace custom-namespace

# 1.yaml内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1 # pod名称为pod1
spec:
 containers:
 - name: nginx
   image: nginx

# 创建资源时指定命名空间
kubectl create -f 1.yaml -n custom-namespace
```



### 使用`kubectl create configmap`创建`ConfigMap`

```bash
# 创建ConfigMap时指定键值对
kubectl create configmap demo-config1 --from-literal=sleep-interval=25

# 查询configmap对列表
kubectl get configmap

# 查看指定configmap详细信息
kubectl get configmap demo-config1 -o yaml

# 创建包含多个键值对的ConfigMap
kubectl create configmap demo-config2 --from-literal=foo=bar --from-literal=bar=baz

# 查看指定configmap详细信息
kubectl get configmap demo-config2 -o yaml
```





## `kubectl apply`用法

### 使用`--dry-run`转换`yaml`格式为`JSON`格式

```bash
# yaml文件内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
 - name: busybox
   image: busybox
   command: ["/bin/sh", "-c", "while true;do echo `date` >> /root/out.txt; sleep 10; done;"]
   volumeMounts:
   - name: volume
     mountPath: /root/
     subPath: demo-pv-and-pvc
 volumes:
 - name: volume
   persistentVolumeClaim:
    claimName: pvc1
    readOnly: false

# 转换yaml格式为JSON
kubectl apply -f 3.yaml --dry-run=client -o json
```





### 使用`JSON`格式文件创建`pod`

```bash
# yaml文件内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
 - name: busybox
   image: busybox
   command: ["/bin/sh", "-c", "while true;do echo `date` >> /root/out.txt; sleep 10; done;"]
   imagePullPolicy: IfNotPresent
   volumeMounts:
   - name: volume
     mountPath: /root/
     subPath: demo-pv-and-pvc
 volumes:
 - name: volume
   persistentVolumeClaim:
    claimName: test-pvc1
    readOnly: false

---
kind: PersistentVolumeClaim
apiVersion: v1
metadata:
  name: test-pvc1
spec:
  storageClassName: nfs-client
  accessModes:
    - ReadWriteMany
  resources:
    requests:
      storage: 1Gi

# 先将yaml文件转换为JSON格式文件
kubectl apply -f 3.yaml --dry-run=client -o json

# 使用JSON格式文件创建pod
cat test.json | kubectl apply -f -

# 使用JSON格式文件删除pod
cat test.json | kubectl delete -f -
```



## `kubectl logs`查看`pod`或者指定容器日志

```bash
# 1.yaml内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: kubia-manual
spec:
 containers:
  - image: luksa/kubia
    name: kubia
    ports:
     - containerPort: 8080
       protocol: TCP
   
# 创建pod
kubectl create -f 1.yaml

# 在pod列表中查看新创建的pod
kubectl get pods

# 查看pod日志
kubectl logs kubia-manual

# 查看pod指定容器日志
kubectl logs kubia-manual -c kubia

# 删除pod
kubectl delete -f 1.yaml
```



## `kubectl port-forward`将本地网络端口转发到`pod`或者`service`的端口

### 转发到`pod`端口

创建`pod` yaml 文件内容如下：

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: kubia-manual
spec:
 containers:
  - image: luksa/kubia
    name: kubia
    ports:
     - containerPort: 8080
       protocol: TCP
```

创建`pod`

```sh
kubectl apply -f 1.yaml
```

在pod列表中查看新创建的pod

```sh
kubectl get pods
```

将本地网络端口8888转发到pod中的端口8080，用于临时调试和测试

```sh
kubectl port-forward kubia-manual 8888:8080
```

或者使用下面命令等价于上面的命令

```
kubectl port-forward pod/kubia-manual 8888:8080
```

使用curl命令向pod发送http请求

```sh
curl localhost:8888
```

删除pod

```sh
kubectl delete -f 1.yaml
```

### 转发到`service`端口，注意：不能转发给`headless`服务

创建`service` yaml 文件内容如下：

```sh
apiVersion: v1
kind: Pod
metadata:
 name: kubia-manual
 labels:
    app: my-kubia
spec:
 containers:
  - image: luksa/kubia
    name: kubia
    ports:
     - containerPort: 8080
       protocol: TCP
---
apiVersion: v1
kind: Service
metadata:
 name: kubia-manual
spec:
 type: ClusterIP
 ports:
  - port: 8081
    targetPort: 8080 # pod端口8080
 selector:
  app: my-kubia

```

创建`service`

```sh
kubectl apply -f 1.yaml
```

转发本地端口`8888`到服务端口`8081`

```sh
kubectl port-forward service/kubia-manual 8888:8081
```

使用curl命令向pod发送http请求

```sh
curl localhost:8888
```

删除pod

```sh
kubectl delete -f 1.yaml
```



## `kubectl explain`查看可用的`api`对象字段

```bash
# 查询Pod相关帮助
kubectl explain Pod

# 查看pods api可用的对象字段
kubectl explain pods

# 查看pods.metadata可用的对象字段
kubectl explain pods.metadata

# 查看cronjob.spec
kubectl explain cronjob.spec

# 查询Pod.apiVersion帮助
kubectl explain Pod.apiVersion

# 查询apiVersion可填写的值
kubectl api-versions
```



## `kubectl run`运行`pod`

```bash
# 使用kubectl run直接运行pod
kubectl run nginx --image=nginx --port=80

# 查看pod列表
kubectl get pod

# 使用kubectl describe查看pod详细信息，包括pod启动日志和容器的状态
kubectl describe pod nginx 

# 获取pod ip地址并使用curl访问nginx pod
kubectl get pod -o wide

# 使用curl测试pod是否成功启动，10.244.2.6为pod地址
curl 10.244.2.6

# 删除pod
kubectl delete pod nginx

# 临时运行pod退出后自动删除
kubectl run -it temp1 --image=tutum/dnsutils --rm --restart=Never -- dig SRV kubia.default.svc.cluster.local
```



## `kubectl delete`

### `kubectl delete`从`yaml`文件删除资源

```bash
# 1.yaml内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: kubia-manual
spec:
 containers:
  - image: luksa/kubia
    name: kubia
    ports:
     - containerPort: 8080
       protocol: TCP
   
# 创建pod
kubectl create -f 1.yaml

# 在pod列表中查看新创建的pod
kubectl get pods

# 删除pod
kubectl delete -f 1.yaml
```



### `kubectl delete`按名称删除`pod`

```bash
# 1.yaml内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: kubia-manual
spec:
 containers:
  - image: luksa/kubia
    name: kubia
    ports:
     - containerPort: 8080
       protocol: TCP
   
# 创建pod
kubectl create -f 1.yaml

# 在pod列表中查看新创建的pod
kubectl get pods

# 按照名称删除pod
kubectl delete pod kubia-manual
```



### `kubectl delete pod --all`删除指定命名空间中所有`pod`

```bash
# 创建命名空间
kubectl create ns custom-namespace

# 1.yaml内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: kubia-manual
 namespace: custom-namespace
spec:
 containers:
  - image: luksa/kubia
    name: kubia
    ports:
     - containerPort: 8080
       protocol: TCP

# 删除命名空间custom-namespace下所有pod
kubectl delete pod --all -n custom-namespace
```



### `kubectl delete`使用标签选择器删除`pod`

```bash
# 1.yaml内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: kubia-manual
 labels:
  gpu: "true"
spec:
 containers:
  - image: luksa/kubia
    name: kubia
    ports:
     - containerPort: 8080
       protocol: TCP
       
# 创建资源
kubectl create -f 1.yaml 

# 使用标签选择器删除pod
kubectl delete pod -l gpu="true"
```



### `kubectl delete ns`删除命名空间

```bash
# 创建命名空间
kubectl create ns custom-namespace

# 1.yaml内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: kubia-manual
 namespace: custom-namespace
spec:
 containers:
  - image: luksa/kubia
    name: kubia
    ports:
     - containerPort: 8080
       protocol: TCP
   
# 删除命名空间
kubectl delete ns custom-namespace
```



### `kubectl delete all --all`删除命名空间中所有资源

```bash
# 删除当前默认命名空间下的所有资源
kubectl delete all --all

# 删除指定命名空间下所有资源
kubectl delete all --all -n custom-namespace
```



## `kubectl label`修改资源标签



### 为`pod`打标签

`1.yaml`内容如下：

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
 - name: nginx
   image: nginx
```

创建资源

```bash
kubectl create -f 1.yaml
```

显示标签

```bash
kubectl get pod --show-labels
```

添加标签

```bash
kubectl label pod pod1 creation_method=manual
```

修改标签

```bash
kubectl label pod pod1 creation_method=manual1 --overwrite
```

删除资源

```bash
kubectl delete -f 1.yaml
```



### 使用标签和选择器来约束`pod`调度

显示工作节点的标签

```bash
kubectl get nodes --show-labels
```

使用标签分类工作节点

```bash
kubectl label node demo-k8s-node0 gpu=true
```

`1.yaml`内容如下：

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 # 指定gpu=true的节点运行pod
 nodeSelector:
  gpu: "true"
 containers:
 - name: nginx
   image: nginx
```

删除资源

```bash
kubectl delete -f 1.yaml
```



### 为`node`打标签

```bash
kubectl label node k8s-node1 support-only-perf-target=
```



## `kubectl annotate`添加和修改注解

```bash
# 1.yaml内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
 - name: nginx
   image: nginx
   
# 创建资源
kubectl create -f 1.yaml

# 添加注解
kubectl annotate pod pod1 annotation1="Annotation one"

# 修改注解
kubectl annotate pod pod1 annotation1="Annotation two"

# 显示注解
kubectl describe pod pod1
```



## `kubectl expose`创建服务

```bash
# 创建pod
kubectl run nginx --image=docker.118899.net:10001/yyd-public/demo-k8s-nodejs --port=8080

# 创建ClusterIP类型的服务，expose名为nginx的pod，服务名称为nginx，服务端口80，target-port为容器应用服务监听的端口
kubectl expose pod nginx --name=nginx --port=80 --target-port=8080 --type=ClusterIP

# 查看服务列表
kubectl get service

# 访问服务ip即访问pod nginx，ip地址为上面服务列表中的服务ip
curl 10.1.0.1

# 删除nginx服务
kubectl delete service nginx

# 删除pod
kubectl delete pod nginx
```



## `kubectl exec`



### 在`pod`中执行一次命令

创建pod

```bash
kubectl run nginx --image=nginx --port=8080
```

在`pod`中执行命令，双横杠代表这`kubectl`命令结束。在两个横杠之后的内容是指在`pod`内部需要执行的命令。如果需要执行的命令没有以横杠开始的参数，横杠也不是必需的。例如：`kubectl exec nginx curl -s http://10.1.1.1`，如果这里不使用横杠，`-s`选项会被解析成`kubectl exec`选项，会导致结果异常和歧义错误。

```bash
kubectl exec nginx -- date
```

删除`pod`

```bash
kubectl delete pod nginx
```



### 登录`shell`执行任意多条命令

创建`pod`

```bash
kubectl run nginx --image=nginx --port=8080
```

登录`shell`执行任务多条命令

```bash
kubectl exec -it nginx bash
```

删除`pod`

```bash
kubectl delete pod nginx
```



### 连接`pod`中指定的容器

创建`pod`中有多个容器，`1.yaml`文件内容如下：

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: test-pod
spec:
 containers:
 - name: nginx
   image: nginx
 - name: curl
   image: curlimages/curl
   command: ["/bin/sh", "-c", "sleep 7200"]
```

启动`pod`

```bash
kubectl apply -f 1.yaml
```

连接`curl`容器，使用`curl`通过`localhost`测试`nginx`服务

```bash
kubectl exec -it test-pod -c curl /bin/sh
curl localhost
```

删除`pod`

```bash
kubectl delete -f 1.yaml
```



## `kubectl cp`

创建测试 pod yaml 内容如下：

```yaml
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
```

复制本地文件到 pod1 中

```sh
kubectl cp test.json pod1:/
```

进入 pod1 中查看 /test.json 文件是否存在

```sh
kubectl exec -it pod1 /bin/bash
ls -alh |grep test.json
```

复制 pod1 中文件到本地

```sh
kubectl cp pod1:etc/apt/sources.list ./sources.list
```



## `kubectl top`

需要先运行 metrics 服务器，否则`kubectl top`会报告 ”metrics服务不可用“ 错误，components.yaml 内容如下：

> 安装metrics-server-v0.6.2
> 下载 https://github.com/kubernetes-sigs/metrics-server/releases/download/v0.6.2/components.yaml
> metrics-server args添加 - --kubelet-insecure-tls 表示抓取指标数据时不使用https通讯
> metrics-server image修改为 registry.cn-hangzhou.aliyuncs.com/google_containers/metrics-server:v0.6.2

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  labels:
    k8s-app: metrics-server
  name: metrics-server
  namespace: kube-system
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  labels:
    k8s-app: metrics-server
    rbac.authorization.k8s.io/aggregate-to-admin: "true"
    rbac.authorization.k8s.io/aggregate-to-edit: "true"
    rbac.authorization.k8s.io/aggregate-to-view: "true"
  name: system:aggregated-metrics-reader
rules:
- apiGroups:
  - metrics.k8s.io
  resources:
  - pods
  - nodes
  verbs:
  - get
  - list
  - watch
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  labels:
    k8s-app: metrics-server
  name: system:metrics-server
rules:
- apiGroups:
  - ""
  resources:
  - nodes/metrics
  verbs:
  - get
- apiGroups:
  - ""
  resources:
  - pods
  - nodes
  verbs:
  - get
  - list
  - watch
---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  labels:
    k8s-app: metrics-server
  name: metrics-server-auth-reader
  namespace: kube-system
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: Role
  name: extension-apiserver-authentication-reader
subjects:
- kind: ServiceAccount
  name: metrics-server
  namespace: kube-system
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  labels:
    k8s-app: metrics-server
  name: metrics-server:system:auth-delegator
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: system:auth-delegator
subjects:
- kind: ServiceAccount
  name: metrics-server
  namespace: kube-system
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  labels:
    k8s-app: metrics-server
  name: system:metrics-server
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: system:metrics-server
subjects:
- kind: ServiceAccount
  name: metrics-server
  namespace: kube-system
---
apiVersion: v1
kind: Service
metadata:
  labels:
    k8s-app: metrics-server
  name: metrics-server
  namespace: kube-system
spec:
  ports:
  - name: https
    port: 443
    protocol: TCP
    targetPort: https
  selector:
    k8s-app: metrics-server
---
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    k8s-app: metrics-server
  name: metrics-server
  namespace: kube-system
spec:
  selector:
    matchLabels:
      k8s-app: metrics-server
  strategy:
    rollingUpdate:
      maxUnavailable: 0
  template:
    metadata:
      labels:
        k8s-app: metrics-server
    spec:
      containers:
      - args:
        - --cert-dir=/tmp
        - --secure-port=4443
        - --kubelet-preferred-address-types=InternalIP,ExternalIP,Hostname
        - --kubelet-use-node-status-port
        - --metric-resolution=15s
        - --kubelet-insecure-tls
        image: registry.cn-hangzhou.aliyuncs.com/google_containers/metrics-server:v0.6.2
        imagePullPolicy: IfNotPresent
        livenessProbe:
          failureThreshold: 3
          httpGet:
            path: /livez
            port: https
            scheme: HTTPS
          periodSeconds: 10
        name: metrics-server
        ports:
        - containerPort: 4443
          name: https
          protocol: TCP
        readinessProbe:
          failureThreshold: 3
          httpGet:
            path: /readyz
            port: https
            scheme: HTTPS
          initialDelaySeconds: 20
          periodSeconds: 10
        resources:
          requests:
            cpu: 100m
            memory: 200Mi
        securityContext:
          allowPrivilegeEscalation: false
          readOnlyRootFilesystem: true
          runAsNonRoot: true
          runAsUser: 1000
        volumeMounts:
        - mountPath: /tmp
          name: tmp-dir
      nodeSelector:
        kubernetes.io/os: linux
      priorityClassName: system-cluster-critical
      serviceAccountName: metrics-server
      volumes:
      - emptyDir: {}
        name: tmp-dir
---
apiVersion: apiregistration.k8s.io/v1
kind: APIService
metadata:
  labels:
    k8s-app: metrics-server
  name: v1beta1.metrics.k8s.io
spec:
  group: metrics.k8s.io
  groupPriorityMinimum: 100
  insecureSkipTLSVerify: true
  service:
    name: metrics-server
    namespace: kube-system
  version: v1beta1
  versionPriority: 100
```

创建metrics-server

```sh
kubectl apply -f components.yaml
```

查看kube-system metrics-server是Running状态

```sh
kubectl get pod --namespace kube-system -o wide
```

显示 node 资源利用率

```sh
kubectl top node
```

显示 pod 资源利用率

```sh
kubectl top pod
```



## `kubectl describe`

当`pod`状态一直为`Pending`时，通过`kubectl describe`查看`pod`调度失败的原因

```bash
kubectl describe pod redis-node-0
```

查看节点信息，例如：污点信息

```bash
kubectl describe node demo-k8s-node1
```



## `kubectl taint`

删除节点污点

```bash
# 删除node-type=production:NoSchedule
kubectl taint node demo-k8s-node1 node-type=production:NoSchedule-

# 删除污点support-only-openresty:NoSchedule
kubectl taint node k8s-node-openresty support-only-openresty:NoSchedule-
```



## `kubectl cordon/uncordon`

标记节点为不可调度（但对其上的`pod`不做任何事情）

```bash
kubectl cordon demo-k8s-node2
```

取消标记

```bash
kubectl uncordon demo-k8s-node2
```



## `kubectl drain`

疏散节点上的`pod`，`--ignore-daemonsets`是忽略节点上的`daemonset`，否则会报告错误

```bash
kubectl drain k8s-node2 --ignore-daemonsets
```

