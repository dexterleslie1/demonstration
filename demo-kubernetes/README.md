# `kubernetes(k8s)`



## Kubernetes Google Container Registry国内镜像替换

> https://kubernetes.feisky.xyz/appendix/mirrors



## kubectl命令



### ubuntu安装kubectl命令

> https://kubernetes.io/docs/tasks/tools/install-kubectl-linux/

```shell
# 下载指定版本kubectl二进制程序
curl -LO https://dl.k8s.io/release/v1.20.0/bin/linux/amd64/kubect

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



### kubectl输出http调试日志

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



### kubectl get用法



#### kubectl get检查现有pod对应的yaml描述文件

```
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



#### kubectl get显示资源的标签

```
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



#### kubectl get通过标签选择其筛选资源

```
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



#### kubectl get pod --namespace查询指定命名空间下的pod

```
# 查询命名kube-system下的pod
kubectl get pod --namespace kube-system
kubectl get pod -n kube-system
```



#### kubectl get ns命名空间

```
# 获取所有命名空间
kubectl get ns
```



### kubectl create



#### kubectl create创建pod

```
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



#### kubectl create从yaml文件创建命名空间

```
# 1.yaml内容如下:
apiVersion: v1
kind: Namespace
metadata:
 name: custom-namespace
 
# 创建命名空间
kubectl create -f 1.yaml
```



#### kubectl create namespace创建命名空间

```
# 创建命名空间
kubectl create namespace custom-namespace
```



#### kubectl create创建资源时指定命名空间

```
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



#### 使用kubectl create configmap创建ConfigMap

```
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





### kubectl apply用法

#### 使用--dry-run转换yaml格式为JSON格式

```shell
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





#### 使用JSON格式文件创建pod

```shell
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



### kubectl apply用法

#### 使用--dry-run转换yaml格式为JSON格式

```shell
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





#### 使用JSON格式文件创建pod

```shell
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



### kubectl logs查看pod或者指定容器日志

```
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



### kubectl port-forward将本地网络端口转发到pod或者service的端口

#### 转发到`pod`端口

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

#### 转发到`service`端口(不能转发给headless服务)

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



### kubectl explain查看可用的api对象字段

```
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



### kubectl run运行pod

```
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



### kubectl delete

#### kubectl delete从yaml文件删除资源

```
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



#### kubectl delete按名称删除pod

```
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



#### kubectl delete pod --all删除指定命名空间中所有pod

```
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



#### kubectl delete使用标签选择器删除pod

```
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



#### kubectl delete ns删除命名空间

```
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



#### kubectl delete all --all删除命名空间中所有资源

```
# 删除当前默认命名空间下的所有资源
kubectl delete all --all

# 删除指定命名空间下所有资源
kubectl delete all --all -n custom-namespace
```



### kubectl label修改资源标签

```
### 为pod打标签

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

# 显示标签
kubectl get pod --show-labels

# 添加标签
kubectl label pod pod1 creation_method=manual

# 修改标签
kubectl label pod pod1 creation_method=manual1 --overwrite

# 删除资源
kubectl delete -f 1.yaml





### 使用标签和选择器来约束pod调度

# 显示工作节点的标签
kubectl get nodes --show-labels

# 使用标签分类工作节点
kubectl label node demo-k8s-node0 gpu=true

# 1.yaml内容如下:
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
   
# 删除资源
kubectl delete -f 1.yaml
```



### kubectl annotate添加和修改注解

```
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



### kubectl expose创建服务

```
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



### kubectl exec在pod中执行命令

```
### 执行一次命令
# 创建pod
kubectl run nginx --image=docker.118899.net:10001/yyd-public/demo-k8s-nodejs --port=8080

# 在pod中执行命令，双横杠代表这kubectl命令结束。在两个横杠之后的内容是指在pod内部需要执行的命令。如果需要执行的命令没有以横杠开始的参数，横杠也不是必需的。例如: kubectl exec nginx curl -s http://10.1.1.1，如果这里不使用横杠，-s选项会被解析成kubectl exec选项，会导致结果异常和歧义错误。
kubectl exec nginx -- date

# 删除pod
kubectl delete pod nginx



### 登录shell执行任意多条命令
# 创建pod
kubectl run nginx --image=docker.118899.net:10001/yyd-public/demo-k8s-nodejs --port=8080

# 登录shell执行任务多条命令
kubectl exec -it nginx  bash

# 删除pod
kubectl delete pod nginx
```



### kubectl cp

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



### kubectl top

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



## namespace命名空间

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



## 探针



### 创建基于http的存活探针

```shell
# 使用nodejs制作测试用的http应用
# 创建nodejs应用，请求大于5次后http返回500错误来模拟http服务器不能正常提供服务
# app.js内容如下:
const http = require("http")
const os = require("os")

console.log("Kubia server starting...")

var counter = 0
var handler = function(request, response) {
	console.log("Received request from " + request.connection.remoteAddress)
    if(counter < 5)
    	response.writeHead(200)
	else
		response.writeHead(500)
    counter++
    response.end("You've hit " + os.hostname() + " " + counter + " times\n")
}

var www = http.createServer(handler)
www.listen(8080)

# 创建Dockerfile用于编译docker镜像，Dockerfile内容如下:
FROM node:7

ADD app.js /app.js
ENTRYPOINT ["node", "app.js"]

# 编译docker镜像
docker build -t docker.118899.net:10001/yyd-public/demo-k8s-nodejs .

# 使用curl测试http服务是否按照预期工作
docker run --rm --name kubia-container -p 8080:8080 docker.118899.net:10001/yyd-public/demo-k8s-nodejs
curl localhost:8080

# 停止nodejs http服务
docker stop kubia-container

# 推送镜像到远程仓库
docker push docker.118899.net:10001/yyd-public/demo-k8s-nodejs

# 1.yaml内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: kubia-liveness
spec:
 containers:
 - name: kubia
   image: docker.118899.net:10001/yyd-public/demo-k8s-nodejs
   livenessProbe:
    httpGet:
     path: /
     port: 8080
    
# 创建资源
kubectl apply -f 1.yaml 

# 大概过了2分钟左右pod被探针机制重启，通过观察下面命令输出的restarts数据查看pod重启次数
kubectl get pod -w

# 分析pod被重启的原因和探针的默认附加属性
kubectl describe pod kubia-liveness

# 通过帮助命令查看livenessProbe配置
kubectl explain pods.spec.containers.livenessProbe

# 配置存活探针的附加属性，1.yaml内容如下: 
apiVersion: v1
kind: Pod
metadata:
 name: kubia-liveness
spec:
 containers:
 - name: kubia
   image: docker.118899.net:10001/yyd-public/demo-k8s-nodejs
   livenessProbe:
    httpGet:
     path: /
     port: 8080
    # 等pod启动30秒后才运行探针机制
    initialDelaySeconds: 30
    # 失败多少次认为服务失败并且重启
    failureThreshold: 5
    # 探针检查频率
    periodSeconds: 2
    # http超时时间
    timeoutSeconds: 2
```



### 服务就绪探针

#### http 服务就绪探针

app.js 内容如下：

```js
const http = require("http")
const os = require("os")

console.log("Kubia server starting...")

startTime = Date.now()
millisecondsToReady = 60000
var handler = function(request, response) {
    console.log("Received request from " + request.connection.remoteAddress)
    endTime = Date.now()
    response.setHeader("Content-Type", "text/plain; charset=utf-8")
    if(endTime - startTime >= millisecondsToReady) {
    	response.writeHead(200)
	    response.end(os.hostname() + "服务已准备好，可以访问\n")
    } else {
        response.writeHead(500)
	    response.end(os.hostname() + "服务未准备好，" + (millisecondsToReady - (endTime - startTime)) + "毫秒后访问\n")
    }
}

var www = http.createServer(handler)
www.listen(8080)

```

Dockerfile 内容如下：

```dockerfile
FROM node:7

ADD app.js /app.js
ENTRYPOINT ["node", "app.js"]

```

编译 docker 镜像

```sh
docker build --tag docker.118899.net:10001/yyd-public/demo-k8s-readinessprobe .
```

推送 docker 镜像

```sh
docker push docker.118899.net:10001/yyd-public/demo-k8s-readinessprobe
```

创建 deployment 和 service yaml 内容如下：

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
spec:
 selector:
  matchLabels:
   app: kubia
 template:
  metadata:
   labels:
    app: kubia
  spec:
   containers:
    - name: kubia
      image: docker.118899.net:10001/yyd-public/demo-k8s-readinessprobe
      readinessProbe:
       httpGet:
        path: /
        port: 8080
---
apiVersion: v1
kind: Service
metadata:
 name: myservice1
spec:
 type: ClusterIP
 selector:
  app: kubia
 ports:
  - port: 80
    targetPort: 8080
```

创建 deployment 和 service

```sh
kubectl apply -f 1.yaml
```

1分钟后才能正常访问服务，因为readinessProbe作用，pod 1分钟后才ready状态

```sh
kubectl get pods
kubectl get services
```

查看 pod 的关于服务就绪探针的 events 日志

```sh
kubectl describe pod deployment1-78fdbb9b4
```

使用curl测试服务是否正常，curl ip为服务的对应的ip地址

```sh
[root@k8s-master ~]# curl 10.1.39.125
curl: (7) Failed connect to 10.1.39.125:80; Connection refused
[root@k8s-master ~]# curl 10.1.39.125
curl: (7) Failed connect to 10.1.39.125:80; Connection refused
[root@k8s-master ~]# curl 10.1.39.125
curl: (7) Failed connect to 10.1.39.125:80; Connection refused
[root@k8s-master ~]# curl 10.1.39.125
curl: (7) Failed connect to 10.1.39.125:80; Connection refused
[root@k8s-master ~]# curl 10.1.39.125
deployment1-78fdbb9b4服务已准备好，可以访问
```

扩容到两个pod，第二个新的pod需要等待一分钟才ready状态接受请求

```sh
kubectl scale deployment deployment1 --replicas=2
```



#### 自定义脚本服务就绪探针

创建探针的 yaml 内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: configmap1
data:
  ping_liveness_local.sh: |-
    #!/bin/sh

    export REDISCLI_AUTH="123456"

    response=$(
      timeout -s SIGTERM 5 \
      redis-cli -h localhost -p 6379 ping
    )
    if [ "$response" != "PONG" ]; then
      echo "$response"
      exit 1
    fi

---
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
spec:
 selector:
  matchLabels:
   app: kubia
 template:
  metadata:
   labels:
    app: kubia
  spec:
    containers:
    - name: kubia
      image: redis:5.0.14
      imagePullPolicy: IfNotPresent
      volumeMounts:
      - name: config
        mountPath: /health/
      readinessProbe:
        initialDelaySeconds: 5
        periodSeconds: 5
        # One second longer than command timeout should prevent generation of zombie processes.
        timeoutSeconds: 6
        successThreshold: 1
        failureThreshold: 5
        exec:
          command:
            - sh
            - /health/ping_liveness_local.sh
    volumes:
    - name: config
      configMap:
        name: configmap1
        items:
        - key: ping_liveness_local.sh
          path: ping_liveness_local.sh

```

查看 readiness 探针状态

```sh
kubectl describe pod deployment1-9d8b8558c-sfbfb
```

5 秒后查看 deployment 和 pod 状态为就绪状态

```sh
kubectl get deployment
kubectl get pod
```



## pod

> pod代表了kubernetes中的基本部署单元。
>
> 通常一个pod只包含一个容器。
> 同一个pod中的容器之间的部分隔离，他们在相同的network中UTS命名空间下运行因此能够通过IPC进行通讯。
> 同一个pod中容器运行与相同的network命名空间中，因此他们共享相同的ip地址和端口空间。
> 同一个pod中所有容器具有相同的loopback网络接口，因此容器可以通过localhost与同一pod中的其他容器进行通讯。
> 当一个pod包含多个容器时，这些容器总是运行于同一个工作节点上，一个pod绝不会跨多个工作节点。
> 如果一个pod运行一个前端和后端容器组成，那么当你扩大pod的实例数量时，比如扩大为两个，最终会得到两个前端容器和两个后端容器是不好的结果。
>
> 在pod中使用多个容器的情况: pod应该包含紧密的耦合的容器组(通常是一个主容器和支持主容器的其他容器)，它们需要一起运行还是可以在不同的主机上运行？它们代表的是一个整体还是相互独立的组件？他们必须一起扩容还是可以分别进行？



### 标签

> 标签可以组织k8s资源
> 可以通过标签选择k8s资源

#### 创建pod时指定标签

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

#### 修改现有的pod标签

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

#### 使用标签选择器列出pod

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

#### 使用标签调度pod到指定节点

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

### 启动命令 command 和 args

> https://kubernetes.io/docs/tasks/inject-data-application/define-command-argument-container/

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

### init(初始化)容器使用

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

### 钩子函数 postStart、preStop

> 在main容器启动后postStart和关闭前preStop执行指定的动作，每个容器启动后和停止前执行的。

#### postStart 测试

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



#### 通过 pod events 列表查看 postStart 失败日志

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

```sh
kubectl describe pod pod1
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



## pod控制器

> pod控制，用于保证pod运行状态符合预期状态

```shell
# 创建deployment
kubectl create deployment nginx --image=nginx --port=80 --replicas=3

# 显示deployment列表
kubectl get deployment,pods

# 删除其中一个pod后，deployment控制器会自动创建一个新的pod
kubectl delete pod nginx-xxxxxxx

# 显示deployment详细信息
kubectl describe deployment

# 删除deployment
kubectl delete deployment nginx
```



### ReplicationController控制器

> ReplicationController已经被ReplicaSet取代。

```shell
# 创建ReplicationController

# 1.yaml内容如下:
apiVersion: v1
kind: ReplicationController
metadata:
 name: kubia
spec:
 # 副本个数
 replicas: 3
 # RC通过这个选择器和对应的pod关联
 selector:
  app: kubia
 # 用于创建pod的模板
 template:
  metadata:
   # 创建的pod被自动打上标签
   labels:
    app: kubia
  spec:
   containers:
    - name: kubia
      image: docker.118899.net:10001/yyd-public/demo-k8s-nodejs
      ports:
       - containerPort: 8080
       
# 创建资源
kubectl apply -f 1.yaml

# 查看pod
kubectl get pod

# ReplicationController使用声明的labels创建pod
kubectl get pod --show-labels

# 测试ReplicationController对已删除的pod的响应
kubectl delete pod kubia-7k79g
# 当删除其中一个pod时，ReplicationController会自动启动一个新的pod代替它
kubectl get pod

# 获取关于ReplicationControler的信息
kubectl get replicationcontroller

# 查看ReplicationController的附加信息
kubectl describe replicationcontroller kubia
  
# 因为ReplicationController通过标签找到属于它的pod列表，使用kubectl label命令修改其中一个pod之后，这个pod就不再属于原来的ReplicationController了，此时ReplicationController发现少了一个pod并马上启动新的pod以弥补被修改标签减少的一个pod
kubectl label pod kubia-9lc82 app=foo --overwrite

kubectl get pod --show-labels

# ReplicationController扩容
kubectl scale replicationcontroller kubia --replicas=7
kubectl get pod

# ReplicationController缩容
kubectl scale replicationcontroller kubia --replicas=3
kubectl get pod

# 删除ReplicationController
kubectl delete -f 1.yaml
```



### ReplicaSet(RS)

> 保证一定数量的pod能够正常运行，一旦pod发生故障就会自动重启或者重建pod，pod数量扩容，镜像版本的升级和回退。
> 使用RS替代RC，RS和RC的区别在与RS强大的标签选择表达式。

**pod基础**

```shell
# 1.yaml内容如下:
apiVersion: apps/v1
kind: ReplicaSet
metadata:
 name: replicaset1
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

# 查看replicaset状态
kubectl get replicaset -o wide

# 查看pod状态
kubectl get pod

### 演示pod数量缩放
# 使用下面命令编辑replicaset yaml中的replicas参数，退出保存后replicaset自动缩放pod
kubectl edit replicaset replicaset1

# 使用下面命令对replicaset缩放
kubectl scale replicaset replicaset1 --replicas=6


### pod镜像版本升级和回退
# 使用下面命令在线编辑image参数，退出保存后replicaset自动更新镜像版本
kubectl edit replicaset replicaset1

# 查看当前image版本
kubectl get replicaset -o wide

# 使用命令修改image
kubectl set image replicaset replicaset1 nginx=nginx:1.17.1

### 删除replicaset
# 使用命令删除
kubectl delete replicaset replicaset1

# 使用yaml删除
kubectl delete -f 1.yaml
```



### Deployment(deploy)

> deployment控制器不直接管理pod，而是通过Replicaset间接管理pod

```shell
# 1.yaml内容如下:
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
     imagePullPolicy: IfNotPresent

# 创建资源
kubectl create -f 1.yaml

# 查看deployment
kubectl get deployment -o wide

# 因为deployment通过replicaset管理pod，所以创建deployment后会自动创建一个对应的replicaset
kubectl get replicaset

# 通过命令方式实现扩容
kubectl scale deployment deployment1 --replicas=6

# 查询pod情况
kubectl get pod -o wide

# 通过编辑deployment配置中的replicas实现扩容
kubectl edit deployment deployment1
```



### Horizontal Pod Autoscaler(HPA)

> 根据pod负载的变化自动调整pod的数量



### DaemonSet(DS)

> DaemonSet类型的控制器可以保证集群中的每一台节点上都运行一个pod副本，一般适用于日志收集、节点监控等场景。

```shell
### 演示每一台节点上都运行一个pod副本
# 1.yaml内容如下:
apiVersion: apps/v1
kind: DaemonSet
metadata:
 name: daemonset1
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
     imagePullPolicy: IfNotPresent

# 创建资源
kubectl create -f 1.yaml

# 查询daemonset
kubectl get daemonset

# daemonset在每个节点上都运行一个pod
kubectl get pod -o wide

# 释放资源
kubectl delete -f 1.yaml





### 指定节点运行daemonset中的pod而不是全部节点
# 1.yaml内容如下:
apiVersion: apps/v1
kind: DaemonSet
metadata:
 name: daemonset1
spec:
 selector:
  matchLabels:
   app: nginx-pod
 template:
  metadata:
   labels:
    app: nginx-pod
  spec:
   nodeSelector:
    disk: ssd
   containers:
   - name: nginx
     image: nginx:1.17.1
     
# 给节点打上标签
kubectl label node demo-k8s-node0 disk=ssd

# 查看pod情况，被打上标签的节点会自动运行daemonset对应的pod
kubectl get pods -o wide

# 删除资源
kubectl delete -f 1.yaml
```



### Job

> 主要用于负责批量处理（一次要处理指定数量的任务）短暂的一次性（每个任务仅运行一次就结束）任务。

```shell
### 基本job测试
# 1.yaml内容如下:
apiVersion: batch/v1
kind: Job
metadata:
 name: job1
spec:
 manualSelector: true
 completions: 6 # 总共需要执行多少个pod
 parallelism: 3 # 并行运行pod的数量，如果不指定表示一个一个执行
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

# 创建资源
kubectl create -f 1.yaml

# 查看job运行状态
kubectl get job -o wide -w

# 查看pod运行状态
kubectl get pod -w

# 删除job
kubectl delete -f 1.yaml



### 测试restartPolicy: OnFailure
# 1.yaml内容如下:
apiVersion: batch/v1
kind: Job
metadata:
 name: job1
spec:
 manualSelector: true
 completions: 6 # 总共需要执行多少个pod
 parallelism: 3 # 并行运行pod的数量，如果不指定表示一个一个执行
 selector:
  matchLabels:
   app: counter-pod
 template:
  metadata:
   labels:
    app: counter-pod
  spec:
   restartPolicy: OnFailure
   containers:
   - name: counter
     image: busybox
     command: ["/bin/sh", "-c", "for i in 9 8 7 6 5 4 3 2 1; do echo $i;sleep 2; done; exit 1;"]
     
# 创建资源
kubectl create -f 1.yaml

# 查看job运行状态，可以看到COMPLETIONS成功的pod计数一直为0
kubectl get job -o wide -w

# 查看pod运行状态，可以看到pod为错误状态
kubectl get pod -w

# 删除job
kubectl delete -f 1.yaml



### 演示限制job运行时间，如果超过时间则标记job为失败状态
# 1.yaml内容如下:
apiVersion: batch/v1
kind: Job
metadata:
 name: job1
spec:
 # 限制pod的时间，如果pod运行时间超过此时间，系统将尝试终止pod，并将job标记为失败，但是pod会被系统自动删除不会保留
 activeDeadlineSeconds: 6
 manualSelector: true
 completions: 6 # 总共需要执行多少个pod
 parallelism: 3 # 并行运行pod的数量，如果不指定表示一个一个执行
 selector:
  matchLabels:
   app: counter-pod
 template:
  metadata:
   labels:
    app: counter-pod
  spec:
   restartPolicy: OnFailure
   containers:
   - name: counter
     image: busybox
     command: ["/bin/sh", "-c", "for i in 9 8 7 6 5 4 3 2 1; do echo $i;sleep 2; done;"]
     
# 创建资源
kubectl create -f 1.yaml

# 查看job运行状态，可以看到COMPLETIONS成功的pod计数一直为0
kubectl get job -o wide -w

# 查看pod运行状态，可以看到pod为错误状态
kubectl get pod -w

# 删除job
kubectl delete -f 1.yaml
```





### CronJob(CJ)

> 指定特定的时间点重复执行job任务

```shell
# 每分钟执行一次，1.yaml内容如下: 
apiVersion: batch/v1beta1
kind: CronJob
metadata:
 name: cronjob1
spec:
 schedule: "*/1 * * * *"
 jobTemplate:
  spec:
   template:
    spec:
     restartPolicy: OnFailure
     containers:
      - name: kubia
        image: busybox
        command: ["/bin/sh", "-c", "echo `date`"]
        
# 查看pod情况
kubectl get pod -w

# 查看cronjob日志
kubectl logs -f cronjob1-1672666680-hk8hc
kubectl logs -f cronjob1-1672666740-rnqqg
```



### StatefulSet

> RC、Deployment、DaemonSet都是面向无状态的服务，它们所管理的Pod的IP、名字，启停顺序等都是随机的，而StatefulSet是什么？顾名思义，有状态的集合，管理所有有状态的服务，比如MySQL、MongoDB集群等。
> StatefulSet本质上是Deployment的一种变体，在v1.9版本中已成为GA版本，它为了解决有状态服务的问题，它所管理的Pod拥有固定的Pod名称，启停顺序，在StatefulSet中，Pod名字称为网络标识(hostname)，还必须要用到共享存储。
>
> 在Deployment中，与之对应的服务是service，而在StatefulSet中与之对应的headless service，headless service，即无头服务，与service的区别就是它没有Cluster IP，解析它的名称时将返回该Headless Service对应的全部Pod的Endpoint列表。
>
> 一个statefulset创建的每个pod都有一个从零开始的顺序索引，这个会体现在pod的名称和主机名上，同样还是体现在pod对应的固定存储上。这些pod的名称则是可以预知的，因为他是由statefulset的名称加该实例的顺序索引值组成的。
>
> 一个statefulset通常要求你创建一个用来记录每个pod网络标记的headless service。通过这个service，每个pod将拥有独立的DNS记录，这样集群里他的伙伴或者客户端可以通过主机名方便地找到他。比如说，一个属于default命名空间，名为foo的控制服务，他的一个pod名称为A-0，那么可以通过下面的完整域名来访问他: a-0.foo.default.svc.cluster.local。而在Replicaset中这样是行不通的。另外，也可以通过DNS服务，查找域名foo.default.svc.cluster.local对应的所有srv记录，获取一个statefulset中所有pod的名称。
>
> statefulset中每个有状态实例都有其对应的专属存储。
>
> https://www.jianshu.com/p/03cd2f2dc427
>

```yaml
# NOTE: 参考storageclass章节创建storageclass


### 基本使用
# app.js内容如下:
const http = require("http")
const os = require("os")
const fs = require("fs")
const dns = require("dns")

const dataFile = "/var/data/kubia.txt";
const serviceName = "kubia.default.svc.cluster.local";
const port = 8080;

var handler = function(request, response) {
	if (request.method == "POST") {
		// POST请求把请求body存储到一个文件
		var file = fs.createWriteStream(dataFile);
		file.on("open", function(fd) {
			request.pipe(file);
			console.log("New data has been received and stored.");
			response.writeHead(200);
			response.end("Data stored on pod " + os.hostname() + "\n");
		});
	} else {
		response.writeHead(200)
		if (request.url == "/data") {
			// GET或者其他类型请求返回主机名和数据文件内容
                	var data = fs.existsSync(dataFile) ? fs.readFileSync(dataFile, "utf8") : "No data posted yet";
                	response.write("You've hit " + os.hostname() + "\n");
                	response.end("Data stored on this pod: " + data + "\n");
		} else {
			response.write("You've hit " + os.hostname() + "\n");
			response.write("DNS SRV records:\n");
			// 通过DNS查询SRV记录
			dns.resolveSrv(serviceName, function(err, addresses) {
				if (err) {
					response.end("Could not look up DNS SRV records: " + err);
					return;
				}

				var numResponses = 0;
				if (addresses.length == 0) {
					response.end("No peers discovered.");
				} else {
					// 与SRV记录对应的每个pod通讯获取其数据
					addresses.forEach(function (item) {
						numResponses++;
						response.write("- " + item.name + "\n");
						if (numResponses == addresses.length) {
							response.end();
						}
					});
				}
			});
		}
	}
}

var www = http.createServer(handler);
console.log("Server started!")
www.listen(8080);

# Dockerfile内容如下:
FROM node:7

ADD app.js /app.js
ENTRYPOINT ["node", "app.js"]

# 编译镜像
docker build --tag docker.118899.net:10001/yyd-public/demo-k8s-statefulset:v1 .

# 推送镜像
docker push docker.118899.net:10001/yyd-public/demo-k8s-statefulset:v1

# 测试镜像
docker run --rm --name=demo -p 8080:8080 docker.118899.net:10001/yyd-public/demo-k8s-statefulset:v1
curl http://localhost:8080/data

# 1.yaml 内容如下:
apiVersion: v1
kind: Service
metadata:
  name: kubia
  labels:
    app: kubia
spec:
  ports:
  - port: 80
    name: web
  clusterIP: None
  selector:
    app: kubia
---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: web
spec:
  selector:
    matchLabels:
      app: kubia # has to match .spec.template.metadata.labels
  serviceName: "kubia"  #声明它属于哪个Headless Service.
  replicas: 3 # by default is 1
  template:
    metadata:
      labels:
        app: kubia # has to match .spec.selector.matchLabels
    spec:
      terminationGracePeriodSeconds: 10
      containers:
      - name: kubia
        image: docker.118899.net:10001/yyd-public/demo-k8s-statefulset:v1
        imagePullPolicy: Always
        ports:
        - containerPort: 8080
          name: web
        volumeMounts:
        - name: www
          mountPath: /var/data
  volumeClaimTemplates:   #可看作pvc的模板
  - metadata:
      name: www
    spec:
      accessModes: [ "ReadWriteOnce" ]
      storageClassName: "nfs-client"  #存储类名，改为集群中已存在的
      resources:
        requests:
          storage: 1Gi
          
# 创建statefulset
kubectl apply -f 1.yaml

# 查看pod、pvc、pv
kubectl get pod
kubectl get pvc
kubectl get pv

# 提交数据到web-2服务中，下面ip地址是web-2对应pod ip地址
curl -X POST -d "Hello, there!" 10.244.1.88:8080

# 获取web-2中数据
curl 10.244.1.88:8080/data

# 删除web-2看其被statefulset重建后是否使用之前的数据
kubectl delete pod web-2
# 获取web-2中数据，数据依旧是之前的数据。
curl 10.244.1.92:8080/data



### 借助DNS服务器发现其他pod
# 临时执行dig命令
kubectl run -it temp1 --image=tutum/dnsutils --rm --restart=Never -- dig SRV kubia.default.svc.cluster.local




### 通过编程借助DNS服务器发现其他pod

# 获取web-2 pod ip
kubectl get service
# 请求web-2 获取DNS SRV，这个请求会调用nodejs dns.resolveSrv函数
curl 10.244.3.119:8080


### 了解statefulset如何处理节点失效
# 我们阐述了kubernetes必须完全保证: 一个有状态pod在创建他的代替者之前已经不再运行，当一个节点突然失效，kubernetes并不知道节点或者他上面的pod的状态。他不知道这些pod是否还在运行，或者他是否还存在，甚至是否能被客户端访问到，或者仅仅是kubelet停止向主节点上报本节点状态。
# 因为一个statefulset要保证不会有两个拥有相同标记的和存储的pod同时运行，当一个节点似乎失效时，statefulset在明确知道一个pod不再运行之前，他不能或者不应该创建一个替换pod。
# 只有当集群管理者告诉他这些信息时候，他才能明确知道。为了做到这一点，管理者需要删除这个pod，或者删除整个节点(这么做会删除所有调度到该节点上的pod)。

# 手动断开有pod正在运行的节点网络来模拟一个节点网络断开情况，此时参考节点情况会发现被断开网络的节点处于NotReady状态，过若干分钟后在该节点上的pod被主节点标记为Terminating。
kubectl get pod
kubectl get node

# 若该节点过段时间正常连通，并且重新汇报他上面的pod状态，那这个pod就会重新标记为Running。但如果这个pod未知状态持续几分钟(这个时间可以配置的)，这个pod就会自动从节点上驱逐。这是由主节点(kubernetes的控制组件)处理的。他通过删除pod的资源来把他从节点上驱逐。
# 当kubelet发现这个pod被标记为删除状态后，他开始终止运行该pod。在当前的示例中，kubelet已不能与主节点通信(因为你断开了这个节点的网络)，这也就意味着这个pod会一直运行着。
# 此时通过下面命令查看pod详细信息，web-2此时处于Terminating状态，但是没有新的pod被创建并替换此pod，因为主节点没有确切地知道web-2 pod不能再提供服务。
kubectl describe pod web-2

# 此时使用下面命令尝试删除pod web-2失败，命令一直在等待状态。这是因为在删除pod之前，这个pod已经被标记为删除(控制组件已经标记其为删除状态)。
# 可以看出这个pod状态是Terminating。这个pod之前已经被标记为删除，只要他所在的节点上的kubelet通知api服务器说这个pod容器已经终止，那么他就会被清除掉。但是因为这个节点上的网络断开了，所以上述情况永远都不会发生。
kubectl delete pod web-2

# 由于上面描述的原因，只能够强制删除pod，以便web-2重建。
# 现在你唯一可以做的告诉api服务器不用等待kubelet来确认这个pod已经不再运行，而是直接删除他。
kubectl delete pod web-2 --force --grace-period=0
```



## service服务

> 是一组同类pod对外访问接口，借助service，应用可以方便地实现服务发现和负载均衡
>
> 主要的流量负载组建分别为service（4层路由）和ingress（7层路由）



### 使用kubectl expose创建服务

> 不使用 yaml 方式，使用 CLI 方式创建服务

```shell
# 创建pod
kubectl run nginx --image=docker.118899.net:10001/yyd-public/demo-k8s-nodejs --port=8080

# 创建ClusterIP类型的服务，expose名为nginx的pod，服务名称为nginx，服务端口80
kubectl expose pod nginx --name=nginx --port=80 --target-port=8080 --type=ClusterIP

# 查看服务列表
kubectl get service

# 访问服务ip即访问pod nginx，ip地址为上面服务列表中的服务ip
curl 10.1.57.17

# 删除nginx服务
kubectl delete service nginx
# 删除 pod
kubectl delete pod nginx
```



### service通过endpoints转发请求到pods中

> 服务并不是和pod直接相连的。相反，有一种资源介于两者之间，它就是endpoint资源。
> endpoint资源就是暴露一个服务的ip地址和端口的列表，可以通过命令kubectl get endpoints查询endpoint资源列表。
> 尽管在spec服务中定义了pod选择器，但在重定向传入连接时不会直接使用它。相反，选择器用于构建ip和端口列表，然后存储在endpoint资源中。当客户端连接到服务时，服务代理选择这些ip和端口对中的一个，并将传入连接重定向到在该位置监听的服务器。

```
# 1.yaml内容如下:
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
spec:
 replicas: 3
 selector:
  matchLabels:
   app: kubia
 template:
  metadata:
   labels:
    app: kubia
  spec:
   containers:
    - name: kubia
      image: docker.118899.net:10001/yyd-public/demo-k8s-nodejs
      
# 2.yaml内容如下:
apiVersion: v1
kind: Service
metadata:
 name: kubia
spec:
 sessionAffinity: None
 selector:
  app: kubia
 ports:
  - port: 80
    targetPort: 8080
    
    
# 创建资源
kubectl create -f 1.yaml
kubectl create -f 2.yaml

# 查看service对应的endpoints信息
kubectl describe service kubia

# 查看指定endpoints的信息，这些endpoint ip和端口对应pod的ip和端口
kubectl get endpoints kubia

# 销毁资源
kubectl delete -f 1.yaml
kubectl delete -f 2.yaml
```



### 使用yaml创建和删除ClusterIP服务

```shell
# 创建pod
kubectl run nginx --image=docker.118899.net:10001/yyd-public/demo-k8s-nodejs

# 1.yaml内容如下:
apiVersion: v1
kind: Service
metadata:
 name: nginx
spec:
 type: ClusterIP
 selector:
  run: nginx
 ports:
  - port: 80
    targetPort: 8080

# 创建资源
kubectl apply -f 1.yaml

# 显示服务的selector
kubectl get service -o wide

# 访问服务ip地址，ip地址为上面服务列表中的服务ip
curl 10.1.247.146

# 删除服务
kubectl delete -f 1.yaml

# 删除pod
kubectl delete pod nginx
```



### 配置服务上的会话亲和性sessionAffinity

```shell
### 测试sessionAffinity: None，服务会随机分配pod响应请求
# 1.yaml内容如下: 
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
spec:
 replicas: 3
 selector:
  matchLabels:
   app: kubia
 template:
  metadata:
   labels:
    app: kubia
  spec:
   containers:
    - name: kubia
      image: docker.118899.net:10001/yyd-public/demo-k8s-nodejs
  
# 测试sessionAffinity=None，请求被随机分配到3个pod中，2.yaml内容如下:
apiVersion: v1
kind: Service
metadata:
 name: kubia
spec:
 sessionAffinity: None
 selector:
  app: kubia
 ports:
  - port: 80
    targetPort: 8080
    
# 查询服务列表
kubectl get service

# 测试服务是否随机分配pod
url 10.1.103.203
curl 10.1.103.203
curl 10.1.103.203
curl 10.1.103.203

### 测试sessionAffinity=ClientIP请求被指定分配到某个pod中
# 2.yaml内容如下:
apiVersion: v1
kind: Service
metadata:
 name: kubia
spec:
 sessionAffinity: ClientIP
 selector:
  app: kubia
 ports:
  - port: 80
    targetPort: 8080

# 查询服务列表
kubectl get service

# 测试是否分配指定的pod
curl 10.1.79.183
curl 10.1.79.183
curl 10.1.79.183
curl 10.1.79.183

# 删除服务
kubectl delete -f 2.yaml

# 删除deployment
kubectl delete -f 1.yaml
```



### 服务使用pod的命名端口

> 为什么要采用命名端口方式呢？最大好处就是即使更换端口也无须更改服务spec。如果你采用了命名的端口，仅仅需要做的就是改变spec pod中的端口号。在你的pod向新端口更新时，根据pod收到的连接(8080端口在旧的pod上、80端口在新的pod上)，用户连接将会转发到对应的端口上。

```
# 1.yaml内容如下:
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
spec:
 replicas: 3
 selector:
  matchLabels:
   app: kubia
 template:
  metadata:
   labels:
    app: kubia
  spec:
   containers:
    - name: kubia
      image: docker.118899.net:10001/yyd-public/demo-k8s-nodejs
      # 端口8080被命名为http，在服务中被引用
      ports:
       - name: http
         containerPort: 8080


# 创建deployment
kubectl create -f 1.yaml

# 2.yaml内容如下:
apiVersion: v1
kind: Service
metadata:
 name: kubia
spec:
 sessionAffinity: None
 selector:
  app: kubia
 ports:
  - port: 80
    # 使用pod中命名的名为http端口
    targetPort: http
    
# 创建服务
kubectl create -f 2.yaml

# 查看服务
kubectl get service

# 测试服务是否正常，ip地址为服务的集群地址
curl 10.1.47.153

# 删除deployment
kubectl delete -f 1.yaml

# 删除服务
kubectl delete -f 2.yaml
```



### 通过dns发现服务

```shell
# 1.yaml内容如下:
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
spec:
 replicas: 3
 selector:
  matchLabels:
   app: kubia
 template:
  metadata:
   labels:
    app: kubia
  spec:
   containers:
    - name: kubia
      image: docker.118899.net:10001/yyd-public/demo-k8s-nodejs

# 创建kubia-client用于curl测试服务，2.yaml内容如下:
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment2
spec:
 selector:
  matchLabels:
   app: kubia-client
 template:
  metadata:
   labels:
    app: kubia-client
  spec:
   containers:
    - name: kubia-client
      image: alpine/curl
      command: ["/bin/sh", "-c", "sleep 7200"]

# 创建服务myservice1暴露deployment1服务
kubectl expose deployment deployment1 --name=myservice1 --port=80 --target-port=8080 --type=ClusterIP

# 查看pod列表
kubectl get pod

# 进入kubia-client测试pod，使用curl myservice1和curl myservice1.default.svc.cluster.local测试服务是否正常
kubectl exec -it deployment2-6848fcdd67-2rt9g /bin/sh
/ # curl myservice1
You've hit deployment1-9677d889-65g9q 4 times
/ # curl myservice1.default.svc.cluster.local
You've hit deployment1-9677d889-mqzpn 4 times
/ # curl myservice1.default.svc.cluster.local
You've hit deployment1-9677d889-fp66h 4 times
/ # curl myservice1.default.svc.cluster.local
You've hit deployment1-9677d889-mqzpn 5 times
# 使用nslookup命令查询myservice1.default.svc.cluster.local只会返回服务对应的ip地址
/ # nslookup myservice1.default.svc.cluster.local
Server:		10.1.0.10
Address:	10.1.0.10:53


Name:	myservice1.default.svc.cluster.local
Address: 10.1.124.78

# 查看pod中的/etc/resolv.conf文件内容如下，其中nameserver为dns服务器地址
/ # cat /etc/resolv.conf 
nameserver 10.1.0.10
search default.svc.cluster.local svc.cluster.local cluster.local
options ndots:5

# 查询服务集群地址
kubectl get service
```



### 创建外部服务指定endpoint ip列表

```
# 创建辅助pod，用于模拟外部服务
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
    - name: kubia
      image: docker.118899.net:10001/yyd-public/demo-k8s-nodejs
      
# 创建pod
kubectl create -f 1.yaml

# 创建没有选择器的服务，所以在创建服务后不会自动创建endpoints
apiVersion: v1
kind: Service
metadata:
 name: kubia
spec:
 ports:
  - port: 80
  
# 创建服务
kuebctl create -f 2.yaml
  
# 查看pod ip地址，用于下面endpoints转发的目标ip
kubect get pods -o wide

# 创建endpoints资源，NOTE: 其中10.244.1.3、10.244.2.3为pod的地址
apiVersion: v1
kind: Endpoints
metadata:
 # endpoint的名称必须和服务的名称相匹配
 name: kubia
subsets:
 # 服务将连接重定向到endpoint的ip地址
 - addresses:
    - ip: 10.244.1.3
    - ip: 10.244.2.3
   ports:
    # endpoint的目标端口
    - port: 8080
    
# 创建endpoint资源
kubectl create -f 3.yaml

# 进入其中一个pod shell测试服务，测试外部服务是否成功转发
kubectl get services
kubectl exec -it deployment1-9677d889-5j4sp bash
curl kubia

# 销毁资源
kubectl delete -f 1.yaml
kubectl delete -f 2.yaml
kubectl delete -f 3.yaml
```



### 创建外部服务指定ExternalName

> ExternalName服务仅在DNS级别实施，为服务创建了简单的CNAME DNS记录。因此，连接到服务的客户端将直接连接到外部服务，完全绕过服务代理。出于这个原因，这些类型的服务甚至不会获取集群ip。

```
# 创建执行curl命令使用的pod
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
spec:
 selector:
  matchLabels:
   app: kubia-testing
 template:
  metadata:
   labels:
    app: kubia-testing
  spec:
   containers:
    - name: kubia-testing
      image: docker.118899.net:10001/yyd-public/demo-k8s-nodejs

# 创建pod
kubectl create -f 1.yaml

# 创建externalName服务
apiVersion: v1
kind: Service
metadata:
 name: kubia
spec:
 type: ExternalName
 externalName: www.sina.com
 ports:
  - port: 80

# 创建服务
kubectl create -f 2.yaml

# 进入pod shell测试externalName服务
kubectl get pods
kubectl exec -it deployment1-68bd78b7-zgx7j bash
curl kubia

# 销毁资源
kubectl delete -f 1.yaml
kubectl delete -f 2.yaml
```





### 使用NodePort类型的服务

> externalTrafficPolicy: Local配置，NOTE: 请求只转发到当前节点的pod中，不会转发到其他节点的pod上。NOTE: 因为接收连接的节点和托管目标pod的节点之间没有额外的跳跃(不执行SNAT)，所有客户端ip会被保留。

```shell
# 1.yaml内容如下: 
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
spec:
 replicas: 3
 selector:
  matchLabels:
   app: kubia
 template:
  metadata:
   labels:
    app: kubia
  spec:
   containers:
    - name: kubia
      image: docker.118899.net:10001/yyd-public/demo-k8s-nodejs

# 2.yaml NodePort服务内容如下:
apiVersion: v1
kind: Service
metadata:
 name: myservice1
spec:
 # NOTE: 请求只转发到当前节点的pod中，不会转发到其他节点的pod上
 # NOTE: 因为接收连接的节点和托管目标pod的节点之间没有额外的跳跃(不执行SNAT)，所有客户端ip会被保留
 externalTrafficPolicy: Local
 type: NodePort
 ports:
  - port: 80 # 服务端口80
    targetPort: 8080 # pod端口8080 
    nodePort: 30000 # NodePort端口30000
 selector:
  app: kubia
  
# 仍然能够使用ClusterIP访问服务
kubectl get services

curl 10.1.124.78
You've hit deployment1-9677d889-4jc6c 1 times
curl 10.1.124.78
You've hit deployment1-9677d889-s6jqj 3 times
curl 10.1.124.78
You've hit deployment1-9677d889-4jc6c 2 times

# 使用节点ip+nodePort访问服务
curl 192.168.1.171:30000
You've hit deployment1-9677d889-p66gb 3 times
curl 192.168.1.171:30000
You've hit deployment1-9677d889-4jc6c 3 times
curl 192.168.1.171:30000
You've hit deployment1-9677d889-p66gb 4 times
```

### 通过Google Kubernetes Engine的LoadBalancer将服务暴露出来

> todo 在GKE上实现

### 通过Ingress暴露服务

> todo 没有在本地k8s集群中成功启动ingress-controller

```
# 用于创建pod，1.yaml内容如下:
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
spec:
 replicas: 3
 selector:
  matchLabels:
   app: kubia
 template:
  metadata:
   labels:
    app: kubia
  spec:
   containers:
    - name: kubia
      image: docker.118899.net:10001/yyd-public/demo-k8s-nodejs
  
# 创建pod
kubectl create -f 1.yaml

# 用于创建service，2.yaml内容如下:
apiVersion: v1
kind: Service
metadata:
 name: myservice1
spec:
 type: NodePort
 ports:
  - port: 80 # 服务端口80
    targetPort: 8080 # pod端口8080 
    nodePort: 30000 # NodePort端口30000
 selector:
  app: kubia
  
# 创建service
kubectl create -f 2.yaml

```



### 服务就绪探针

> NOTE：参考上面的服务就绪探针演示



### **headless(无头服务)**

> headless服务是通过service的dns解析访问相应的pod ip地址，例如下面例子：在busybox pod中通过headless-service无头服务名称就能够访问两个nginx pod endpoints。
> 尽管headless服务看起来可能与常规的服务不同，但在客户的视角上他们并无不同。即使使用headlesss服务，客户也可以通过连接的服务的DNS名称来连接到pod上，就像常规服务一样。但是对于headless服务，由于DNS返回了pod的ip，客户端直接连接到该pod，而不是通过服务代理。
>
> jmeter cluster场景master启动测试时，能够借助headless服务获取所有slave ip地址。

```yaml
# 用于创建无头服务，1.yaml内容如下:
---
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
# 两个deploymnet用于演示无头服务
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
# 用于进入shell调试无头服务
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
     
# 查询所有pod
kubectl get pods -o wide

# 进入busybox容器测试headless service
kubectl exec -it headless-deployment-busybox-b9db9bbb-vsrvm /bin/sh
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

# 在本地节点中使用dig命令解析headless service dns到对应的pod ip地址
dig @10.1.0.10 headless-service.default.svc.cluster.local

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
# 返回两个pod A地址记录
headless-service.default.svc.cluster.local. 30 IN A 10.244.2.91
headless-service.default.svc.cluster.local. 30 IN A 10.244.2.90

;; Query time: 1 msec
;; SERVER: 10.1.0.10#53(10.1.0.10)
;; WHEN: Thu Dec 15 13:18:58 CST 2022
;; MSG SIZE  rcvd: 187
```





## volume数据存储





### 简单存储

#### EmptyDir

> pod创建时会自动创建一个空的目录，无需指定宿主机目录，因为k8s系统会自动分配一个目录，**在pod销毁时，emptydir中的数据也会被永久删除。**
>使用emptydir实现pod内的容器共享数据

```yaml
### emptydir存储在硬盘中
apiVersion: v1
kind: Pod 
metadata:
 name: pod1
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
   
# 查看pod ip地址
kubectl get pod -o wide

# 查看busybox日志输出
kubectl logs -f pod1 -c busybox

# 访问nginx
curl 10.244.2.55



### emptydir存储在内存中
apiVersion: v1
kind: Pod 
metadata:
 name: pod1
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
   emptyDir:
    # 存储介质为内存
    medium: Memory
   
# 查看pod ip地址
kubectl get pod -o wide

# 查看busybox日志输出
kubectl logs -f pod1 -c busybox

# 访问nginx
curl 10.244.2.55
```



#### GitRepo卷

> NOTE: 暂时没有需要使用这种类型的卷，所以不研究。



#### HostPath

> HostPath存储不会随着pod销毁而被删除。但是HostPath不能用于跨节点的数据持久化。

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 # 指定pod在节点demo-k8s-node1上运行
 nodeSelector:
  kubernetes.io/hostname: demo-k8s-node1
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
    
# 创建pod
kubectl create -f 1.yaml 

# 查看nginx日志
kubectl logs -f pod1 -c busybox

# 查看pod ip地址
kubectl get pod -o wide

# 请求nginx
curl 10.244.2.56

# 删除pod
kubectl delete -f 1.yaml

# 重新创建pod并再次查看nginx日志，之前的日志依旧存在
kubectl create -f 1.yaml
kubectl logs -f pod1 -c busybox
```



#### NFS

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
 - name: nginx
   image: nginx:1.17.1
   ports:
   - containerPort: 80
   volumeMounts:
   - name: logs-volume
     mountPath: /var/log/nginx
     # 在nfs挂载点下创建子目录demo-nfs/sub1，绝对路径为/data/demo-nfs/sub1
     # 但是pod内的路径还是/var/log/nginx
     subPath: demo-nfs/sub1
 - name: busybox
   image: busybox
   command: ["/bin/sh", "-c", "tail -f /logs/access.log"]
   volumeMounts:
   - name: logs-volume
     mountPath: /logs
     subPath: demo-nfs/sub1
 volumes:
 - name: logs-volume
   nfs:
    server: 192.168.1.186
    path: /data

# 创建pod
kubectl apply -f 1.yaml 

# 查询pod
kubectl get pod -o wide

# 请求nginx产生日志
curl 10.244.2.58

# 打印pod日志
kubectl logs -f pod1 -c busybox
```



### 高级存储

#### pv和pvc

> pv(Persistent Volume)是持久化卷的意思，是对底层共享存储的一种抽象。集群管理元管理底层的pv。
>
> pvc(Persistent Volume Claim)是持久卷声明的意思，是用户对于存储需求的一种声明。换句话说，pvc其实是用户向k8s系统发出一种资源需求申请。开发人员不需要关注底层的pv对应的存储技术，只需要创建pvc并使用存储就可以了。
>
> 
>
> 访问模式(RWO、ROX、RWX涉及可以同时使用卷的工作节点的数量而并非pod的数量):
>
> - RWO -- ReadWriteOnce -- 仅允许单个节点挂载读写。
> - ROX -- ReadOnlyMany -- 允许多个节点挂载只读。
> - RWX -- ReadWriteMany -- 允许多个节点挂载读写这个卷。
>
> 
>
> pv 相关资料
> https://www.jianshu.com/p/0fab432831b3
> https://kubernetes.io/zh-cn/docs/concepts/storage/persistent-volumes/
>
> pv的回收策略（persistentVolumeReclaimPolicy）:
>
> - Retain: 回收策略 Retain 使得用户可以手动回收资源。当 PersistentVolumeClaim 对象被删除时，PersistentVolume 卷仍然存在，对应的数据卷被视为"已释放（released）"。 由于卷上仍然存在这前一申领人的数据，该卷还不能用于其他申领。 管理员可以通过下面的步骤来手动回收该卷：
>
>   1. 删除 PersistentVolume 对象。与之相关的、位于外部基础设施中的存储资产 （例如 AWS EBS、GCE PD、Azure Disk 或Cinder 卷）在 PV 删除之后仍然存在。
>
>   2. 根据情况，手动清除所关联的存储资产上的数据。
>
>   3. 手动删除所关联的存储资产。
>
>      如果你希望重用该存储资产，可以基于存储资产的定义创建新的 PersistentVolume 卷对象。
>
> - Recycle：不保留数据。经测试pvc删除后，在nfs服务端的数据也会被删除（相当于执行rm -rf *）。只有hostPath和NFS支持这种方式。之后该pv会给新的pvc创建申请。NOTE: 不会自动删除底层的存储介质(Aws EBS、GCE PD、Azure Disk)，但是存储介质上的数据被删除了。
>
> - Delete： 这表示当用户删除对应的 PersistentVolumeClaim 时，底层的存储介质也会被删除(Aws EBS、GCE PD、Azure Disk)。AWS EBS, GCE PD, Azure Disk, and Cinder volumes支持这种方式。
>
> **todo accessModes初步测试ReadWriteOnce似乎没有作用**

```yaml
### 演示使用nfs创建pv和pvc
# 步骤1、创建pv
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
  path: /data
  server: 192.168.1.186
  
# 创建pv
kubectl apply -f 1.yaml 

# 查询pv列表，pv状态为Available因为没有pvc在引用此pv
kubectl get pv

# 步骤2、创建pvc
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
 name: pvc1
spec:
 accessModes:
 - ReadWriteMany
 resources:
  requests:
   storage: 1Gi
  
# 创建pvc
kubectl apply -f 2.yaml 

# 查看pv和pvc列表，此时发现pv和pvc都为Bound状态，并且pv对应的claim为default/pvc1
# 当创建好pvc，k8s就会找到适当的持久卷pv并将其绑定到声明，持久卷的容量必须足够大以满足声明的需求，并且卷的访问模式必须包含声明中指定的访问模式。
kubectl get pv
kubectl get pvc

# 步骤3、创建pod使用pvc
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
 - name: busybox
   image: busybox
   # 写入数据到 nfs 服务器 /data/demo-pv-and-pvc/out.txt 文件
   command: ["/bin/sh", "-c", "while true;do echo `date` >> /root/out.txt; sleep 10; done;"]
   volumeMounts:
   - name: volume
     mountPath: /root/
     # 在 nfs 服务器创建 /data/demo-pv-and-pvc 目录
     subPath: demo-pv-and-pvc
 volumes:
 - name: volume
   persistentVolumeClaim:
    claimName: pvc1
    readOnly: false
    
# 创建pod
kubectl create -f 3.yaml 

# 进入nfs服务，切换到/data/demo-pv-and-pvc目录查看out.txt内容
tail -f out.txt 
Wed Jan 4 13:02:07 UTC 2023
Wed Jan 4 13:02:17 UTC 2023
Wed Jan 4 13:02:27 UTC 2023
Wed Jan 4 13:02:37 UTC 2023




### 测试回收策略persistentVolumeReclaimPolicy: Recycle
apiVersion: v1
kind: PersistentVolume
metadata:
 name: pv1
spec:
 capacity: 
  storage: 2Gi
 accessModes:
 - ReadWriteMany
 persistentVolumeReclaimPolicy: Recycle
 nfs:
  path: /data
  server: 192.168.1.186
  
# 创建pv
kubectl create -f 1.yaml

apiVersion: v1
kind: PersistentVolumeClaim
metadata:
 name: pvc1
spec:
 accessModes:
 - ReadWriteMany
 resources:
  requests:
   storage: 1Gi

# 创建pvc
kubectl create -f 2.yaml

# 查看pv和pvc状态，此时pv会被自动分配到pvc中并处于Bound状态
kubectl get pv
kubectl get pvc

# 删除pvc并查看pv和pvc状态，此时pv为Released状态并且能够被其他pvc重复使用。pv中的数据被删除了。
kubectl delete -f 2.yaml
kubectl get pv
kubectl get pvc




### 测试回收策略persistentVolumeReclaimPolicy: Delete
apiVersion: v1
kind: PersistentVolume
metadata:
 name: pv1
spec:
 capacity: 
  storage: 2Gi
 accessModes:
 - ReadWriteMany
 persistentVolumeReclaimPolicy: Delete
 nfs:
  path: /data
  server: 192.168.1.186
  
# 创建pv
kubectl create -f 1.yaml

apiVersion: v1
kind: PersistentVolumeClaim
metadata:
 name: pvc1
spec:
 accessModes:
 - ReadWriteMany
 resources:
  requests:
   storage: 1Gi
   
# 创建pvc
kubectl create -f 2.yaml

# 查看pv和pvc状态，此时pv会被自动分配到pvc中并处于Bound状态
kubectl get pv
kubectl get pvc

# 删除pvc并查看pv和pvc状态，此时pv为Failed状态表示底层存储被删除不能够被其他pvc重复使用。pv中的数据被删除了。只能够重新创建pv和pvc才能够再次使用nfs存储。
kubectl delete -f 2.yaml
kubectl get pv
kubectl get pvc
```



#### 使用storageclass(存储类别)实现持久卷的动态卷配置

> 卷置备程序会根据pvc自动创建pv，不需要集群管理员预先创建pv，集群管理员只需要定义一个或者多个StorageClass对象。
>
> https://github.com/kubernetes-sigs/nfs-subdir-external-provisioner/blob/master/deploy/test-claim.yaml
> https://zahui.fan/posts/179eb842/

**创建rbac.yarml**

```yaml
# 配置nfs卷置备程序
# 用于创建nfs置备程序的相关rbac
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

# 创建相关资源
kubectl create -f rbac.yaml 

# 用于创建nfs置备程序的pod
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
              value: 192.168.1.186
            - name: NFS_PATH
              value: /data
      volumes:
        - name: nfs-client-root
          nfs:
            server: 192.168.1.186
            path: /data
            
# 创建deployment
kubectl create -f deployment.yaml

# 创建storageclass
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: nfs-client
# 必须与deployment.yaml中的PROVISIONER_NAME一致
provisioner: k8s-sigs.io/nfs-subdir-external-provisioner # or choose another name, must match deployment's env PROVISIONER_NAME'
parameters:
  # https://help.aliyun.com/document_detail/144398.html
  archiveOnDelete: "false"

kubectl create -f storageclass.yaml

# 指定storageclass创建pvc
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
      storage: 1Gi
     
# 创建pvc会自动创建pv，nfs置备程序会自动在nfs服务器/data目录下创建pv对应的目录，在删除pvc时候也同时会自动删除此pv目录
kubectl create -f test-claim.yaml 

# 查看pv和pvc列表
kubectl get pv
kubectl get pvc

# 删除pvc会自动删除关联的pv
kubectl delete -f test-claim.yaml 

# 查看pv和pvc列表
kubectl get pvc
kubectl get pv
```



## ConfigMap和Secret配置应用程序



### 在kubernetes中覆盖命令和参数

```shell
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
docker build --tag docker.118899.net:10001/yyd-public/demo-k8s-args .

# 以默认参数运行容器
docker run --rm --name=demo docker.118899.net:10001/yyd-public/demo-k8s-args

# 以自定义参数运行容器
docker run --rm --name=demo docker.118899.net:10001/yyd-public/demo-k8s-args 1

# 推送镜像
docker push docker.118899.net:10001/yyd-public/demo-k8s-args

# 以自定义entrypoint和参数运行pod，command覆盖docker entrypoint，args覆盖docker cmd参数
# 1.yaml内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: kubia
    image: docker.118899.net:10001/yyd-public/demo-k8s-args
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
    image: docker.118899.net:10001/yyd-public/demo-k8s-args
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
    image: docker.118899.net:10001/yyd-public/demo-k8s-args
    args:
     - "7"
     
# 查看pod输出日志
kubectl logs -f pod1
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
docker build --tag docker.118899.net:10001/yyd-public/demo-k8s-env .

# 使用参数运行容器
docker run --rm --name=demo --env SleepSeconds=3 docker.118899.net:10001/yyd-public/demo-k8s-env

# 推送镜像
docker push docker.118899.net:10001/yyd-public/demo-k8s-env

# 使用env传递环境变量
# 1.yaml内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: kubia
    image: docker.118899.net:10001/yyd-public/demo-k8s-env
    env:
     - name: SleepSeconds
       value: "8"

# 查看pod日志
kubectl logs -f pod1
```



### ConfigMap用法



#### 创建configmap



##### 使用kubectl create configmap创建ConfigMap

```shell
### 从指定键值对创建configmap
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



### 从文件内容创建configmap

# demo.conf内容如下:
demo {
 hello = demo
}

# 从文件内容创建configmap
kubectl create configmap demo-config3 --from-file=demo.conf

# 查看configmap详细信息
kubectl get configmap demo-config3 -o yaml



### 从文件内容创建configmap并指定key

# demo.conf内容如下:
demo {
 hello = demo
}

# 从文件内容创建configmap并指定key=customkey
kubectl create configmap demo-config3 --from-file=customkey=demo.conf

# 查看configmap详细信息
kubectl get configmap demo-config3 -o yaml



### 从文件夹创建configmap

# configmap-dir/key1内容
value1
# configmap-dir/key2内容
value2

# 从文件夹创建configmap
kubectl create configmap demo-config5 --from-file=./configmap-dir

# 查看configmap详细信息
kubectl get configmap demo-config5 -o yaml


### 预读取文件内容，再使用yaml创建ConfigMap
# https://stackoverflow.com/questions/51268488/kubernetes-configmap-set-from-file-in-yaml-configuration

# 模拟创建nginx.conf文件内容如下:
server {
	listen 80;
}

# 输出创建configmap的yaml文件格式
kubectl create configmap --dry-run=client somename --from-file=nginx.conf --output yaml

# 修改configmap yaml输出如下:
apiVersion: v1
data:
  nginx.conf: "server {\n\tlisten 80;\n}\n"
kind: ConfigMap
metadata:
  name: somename
```



##### 使用yaml文件创建configmap

```
# 1.yaml内容
apiVersion: v1
kind: ConfigMap
metadata:
 name: configmap1
data:
 1.properties: |
  username: admin
  password: 123456
 2.properties: |
  key1: value1
  key2: value2
  
# 创建configmap
kubectl create -f 1.yaml

# 查看configmap详细信息
kubectl get configmap configmap1 -o yaml
```



#### 在容器中使用configmap



##### 使用环境变量方式暴露configmap条目到容器中

```
# 创建configmap
kubectl create configmap demo-config1 --from-literal=sleep-interval=25

# 使用环境变量方式暴露configmap到容器中
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: kubia
    image: busybox
    command: ["/bin/sh", "-c", "sleep 3600"]
    env:
     - name: INTERVAL
       valueFrom:
        configMapKeyRef:
         name: demo-config1
         key: sleep-interval
         # configmap可选，即使configmap不存在pod依然能够启动
         # optional: true
         
# 创建pod
kubectl create -f 1.yaml

# 查看pod中的环境变量INTERVAL
kubectl exec -it pod1 sh

# 在pod的shell中执行命令env查看环境变量
/ # env



### 使用环境变量方式一次暴露多个configmap条目

# 创建configmap
kubectl create configmap demo-config1 --from-literal=key1=value1 --from-literal=key2=value2

# 用于创建pod，暴露的环境变量命名为MY_key1、MY_key2
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: kubia
    image: busybox
    command: ["/bin/sh", "-c", "sleep 3600"]
    envFrom:
     # 如果不指定prefix，则使用configmap中的key，环境变量为key1、key2
     - prefix: MY_
       configMapRef:
        name: demo-config1
        
# 创建pod
kubectl create -f 1.yaml

# 进入pod中的shell并使用命令env查看环境变量
kubectl exec -it pod1 sh
/ # env
```



##### 使用命令行参数方式暴露configmap条目

```
# 创建configmap
kubectl create configmap demo-config1 --from-literal=sleep-interval=25

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
docker build --tag docker.118899.net:10001/yyd-public/demo-k8s-args .

# 以自定义参数运行容器
docker run --rm --name=demo docker.118899.net:10001/yyd-public/demo-k8s-args 1

# 推送镜像
docker push docker.118899.net:10001/yyd-public/demo-k8s-args

# 1.yaml内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: kubia
    image: docker.118899.net:10001/yyd-public/demo-k8s-args
    command: ["sh", "/entrypoint.sh"]
    env:
     - name: INTERVAL
       valueFrom:
        configMapKeyRef:
         name: demo-config1
         key: sleep-interval
    # 字段pod.spec.containers.args中无法直接引用configmap的条目
    # 但可以利用configmap条目初始化某个环境变量，然后再在参数字段中引用该环境变量
    args: ["$(INTERVAL)"]
 
# 查看pod日志
kubectl logs -f pod1
```



##### 使用configmap卷将条目暴露为文件

> configmap卷会将configmap中的每个条目均暴露成一个文件。运行在容器中的进程可通过读取文件内容获取对应的条目值。

```
## 创建configmap

# 创建configmap-files/my-nginx-config.conf内容如下:
server {
	listen	80;
	server_name www.kubia-example.com;

	gzip 	on;
	gzip_types	text/plain application/xml;
	
	location / {
		root 	/usr/share/nginx/html;
		index	index.html	index.htm;
	}
}

# 创建configmap-files/sleep-interval内容如下:
25

# 从目录configmap-files创建configmap
kubectl create configmap demo-config1 --from-file=configmap-files

# 查看configmap详细信息
kubectl get configmap demo-config1 -o yaml

## 创建pod引用configmap卷
# 1.yaml内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - image: nginx:alpine
    name: nginx
    volumeMounts:
     - name: vol-config
       # 把卷vol-config挂载到/etc/nginx/conf.d目录下
       mountPath: /etc/nginx/conf.d
       readOnly: true
 volumes:
  - name: vol-config
    # 卷定义引用configmap demo-config1
    configMap:
     name: demo-config1
     
# 创建pod
kubectl create -f 1.yaml
   
## 调试pod中的nginx是否使用configmap卷中my-nginx-config.conf gzip配置

# 临时端口转发
kubectl port-forward pod1 8080:80

# 使用curl调试gzip，日志中包含Content-Encoding: gzip表示nginx gzip配置生效
curl -H "Accept-Encoding: gzip" -I localhost:8080

# 进入pod查看被挂载的configmap内容
kubectl exec -it pod1 ls /etc/nginx/conf.d
kubectl exec -it pod1 cat /etc/nginx/conf.d/my-nginx-config.conf


### 上面例子存在问题，它会在/etc/nginx/conf.d目录下暴露my-nginx-config.conf和sleep-interval两个文件，其中sleep-interval是configmap的一个key，但是不会被nginx使用。可以通过使用items指定configmap卷中需要暴露的条目达到隐藏sleep-interval目的并把my-nginx-config.conf暴露为名为gzip.conf的文件。

# 1.yaml内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - image: nginx:alpine
    name: nginx
    volumeMounts:
     - name: vol-config
       # 把卷vol-config挂载到/etc/nginx/conf.d目录下
       mountPath: /etc/nginx/conf.d
       readOnly: true
 volumes:
  - name: vol-config
    # 卷定义引用configmap demo-config1
    configMap:
     name: demo-config1
     # 指定只暴露configmap中的my-nginx-config.conf到名为gzip.conf文件
     # configmap中的sleep-interval不会被暴露
     items:
      - key: my-nginx-config.conf
        path: gzip.conf
        
# 查看pod中/etc/nginx/conf.d目录内容
kubectl exec -it pod1 ls /etc/nginx/conf.d
kubectl exec -it pod1 cat /etc/nginx/conf.d/gzip.conf



### configmap独立条目作为文件被挂载且不隐藏容器目录中已存在的其他文件或者目录。上面例子存在问题，因为挂在configmap卷到/etc/nginx/conf.d目录中，如果/etc/nginx/conf.d目录存在其他文件或者目录，则这些已存在的文件或者目录都会被挂载隐藏，所以需要使用subPath字段解决此问题。

# 1.yaml内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - image: nginx:alpine
    name: nginx
    volumeMounts:
     - name: vol-config
       # 把卷vol-config挂载到/etc/nginx/conf.d目录下
       # mountPath: /etc/nginx/conf.d
       # 指定挂载至某一个文件而不是/etc/nginx/conf.d目录
       mountPath: /etc/nginx/conf.d/my-n-c.conf
       # 指定需要挂载的configmap卷中的key
       subPath: gzip.conf
       readOnly: true
 volumes:
  - name: vol-config
    # 卷定义引用configmap demo-config1
    configMap:
     name: demo-config1
     # 指定只暴露configmap中的my-nginx-config.conf到名为gzip.conf文件
     # configmap中的sleep-interval不会被暴露
     items:
      - key: my-nginx-config.conf
        path: gzip.conf
        
# 查看pod中/etc/nginx/conf.d目录内容
kubectl exec -it pod1 ls /etc/nginx/conf.d
kubectl exec -it pod1 cat /etc/nginx/conf.d/my-n-c.conf



### 指定挂载文件的默认权限

# 1.yaml内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - image: nginx:alpine
    name: nginx
    volumeMounts:
     - name: vol-config
       # 把卷vol-config挂载到/etc/nginx/conf.d目录下
       # mountPath: /etc/nginx/conf.d
       # 指定挂载至某一个文件而不是/etc/nginx/conf.d目录
       mountPath: /etc/nginx/conf.d/my-n-c.conf
       # 指定需要挂载的configmap卷中的key
       subPath: gzip.conf
       readOnly: true
 volumes:
  - name: vol-config
    # 卷定义引用configmap demo-config1
    configMap:
     name: demo-config1
     # 挂载文件的默认权限 rw-rw----
     defaultMode: 0660
     # 指定只暴露configmap中的my-nginx-config.conf到名为gzip.conf文件
     # configmap中的sleep-interval不会被暴露
     items:
      - key: my-nginx-config.conf
        path: gzip.conf
 
# 查看pod中/etc/nginx/conf.d目录内容
kubectl exec -it pod1 -- ls -alh /etc/nginx/conf.d
```



#### configmap热更新且不重启应用程序

> 在次之前提过，使用环境变量或者命令行参数作为配置员的弊端在于无法在进程运行时更新配置。将configmap暴露为卷可以达到配置热更新的效果，无须重新创建pod或者重启容器。
>
> NOTE: 如果挂载的是容器中的单个文件而不是完整的卷，configmap更新之后对应的文件不会被更新！

```
## 创建configmap

# 创建configmap-files/my-nginx-config.conf内容如下:
server {
	listen	80;
	server_name www.kubia-example.com;

	gzip 	on;
	gzip_types	text/plain application/xml;
	
	location / {
		root 	/usr/share/nginx/html;
		index	index.html	index.htm;
	}
}

# 从目录configmap-files创建configmap
kubectl create configmap demo-config1 --from-file=configmap-files

# 查看configmap详细信息
kubectl get configmap demo-config1 -o yaml

## 创建pod引用configmap卷
# 1.yaml内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - image: nginx:alpine
    name: nginx
    volumeMounts:
     - name: vol-config
       # 把卷vol-config挂载到/etc/nginx/conf.d目录下
       mountPath: /etc/nginx/conf.d
       readOnly: true
 volumes:
  - name: vol-config
    # 卷定义引用configmap demo-config1
    configMap:
     name: demo-config1
     
# 创建pod
kubectl create -f 1.yaml

# 进入pod查看被挂载的configmap内容
kubectl exec -it pod1 cat /etc/nginx/conf.d/my-nginx-config.conf

# 编辑configmap demo-config1配置，修改gzip on为gzip off
kubectl edit configmap demo-config1

# 等待约1分钟后再次查看my-nginx-config.conf发现gzip热更新为off
kubectl exec -it pod1 cat /etc/nginx/conf.d/my-nginx-config.conf
```



#### 其他参考综合应用例子

##### 键值对存储

```shell
# 1.yaml内容如下:
apiVersion: v1
kind: ConfigMap
metadata:
 name: configmap1
data:
 1.properties: |
  username: admin
  password: 123456
 2.properties: |
  key1: value1
  key2: value2

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
     # 指定volume中的路径2repath.properties挂载到/2repath.properties
     # https://kubernetes.io/docs/concepts/storage/volumes/#using-subpath
     mountPath: /2repath.properties
     subPath: 2repath.properties
     # 挂载volume1到/root目录中
   - name: volume1
     mountPath: /root
 volumes:
 - name: volume1
   configMap:
    name: configmap1
    items:
     # 使用path指定configmap的2.properties这个key映射到volume的映射路径为2repath.properties
     # 并且volume被挂载后1.properties不会被挂载，因为只指定了2.properties
     # https://kubernetes.io/docs/tasks/configure-pod-container/configure-pod-configmap/#add-configmap-data-to-a-specific-path-in-the-volume
     - key: 2.properties
       path: 2repath.properties

# 查询configmap列表
kubectl get configmap

# 显示configmap详细信息
kubectl describe configmap configmap1

# 进入pod查看1.properties
kubectl exec -it pod1 /bin/sh
/ # ls
/ # cat 2repath.properties 
/ # ls /root/
/ # cat /root/2repath.properties 
/ # 
```





##### nginx.conf配置存储

```yaml
# 1.yaml内容如下:
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
  
# NOTE: 使用下面命令获取yaml文件内的nginx.conf内容
# 否则直接复制粘贴nginx.conf内容到yaml会报告yaml文件格式错误
# https://stackoverflow.com/questions/51268488/kubernetes-configmap-set-from-file-in-yaml-configuration
kubectl create configmap --dry-run=client somename --from-file=nginx.conf --output yaml
apiVersion: v1
data:
  nginx.conf: |
    #user  nobody;
    #worker_processes  1;
    worker_rlimit_nofile 65535;
......

# 查看configmap
kubectl get configmap
kubectl describe configmap configmap1

# 进入容器查看nginx.conf
kubectl exec -it pod1 /bin/sh
/ # cat /root/nginx.conf 
#user  nobody;
  #worker_processes  1;
  worker_rlimit_nofile 65535;
......
```





##### 综合应用案例

```shell
# 给容器传递ConfigMap条目作为环境变量
# 2.yaml内容如下:
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfig5
data:
 k1: v1
 k2: v2
 k3: v3

---
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: kubia
    image: busybox
    command: ["sh", "-c", "echo $MYENV1; sleep 7200"]
    env:
     - name: MYENV1
       valueFrom:
        configMapKeyRef:
         name: myconfig5
         key: k3

# 查看pod日志，控制台会输出v3
kubectl logs -f pod1

# 一次性传递ConfigMap的所有条目作为环境变量
# 2.yaml内容如下:
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfig5
data:
 k1: v1
 k2: v2
 k3: v3

---
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: kubia
    image: busybox
    command: ["sh", "-c", "echo $MYCONFIG_k1; sleep 7200"]
    envFrom:
     - prefix: MYCONFIG_
       configMapRef:
        name: myconfig5

# 查看pod日志，输出v1
kubectl logs -f pod1

# 传递ConfigMap条目作为命令行参数
# 2.yaml内容如下:
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfig5
data:
 k1: v1
 k2: "7"
 k3: v3

---
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: kubia
    image: docker.118899.net:10001/yyd-public/demo-k8s-args
    env:
     - name: MYENV1
       valueFrom:
        configMapKeyRef:
         name: myconfig5
         key: k2
    args: ["$(MYENV1)"]

# 查看pod日志
kubectl logs -f pod1

# 使用ConfigMap卷将条目暴露为文件
# redis.conf 内容如下:
daemon: yes
bind: 0.0.0.0
cluster: yes

# redis1.conf 内容如下:
daemon: yes
bind: 0.0.0.1
cluster: yes

# 从redis.conf和redis1.conf创建configmap
kubectl create configmap myconfigredis --from-file=redis.conf --from-file=redis1.conf

# 指定暴露ConfigMap中redis1.conf条目
# 2.yaml 内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: kubia
    image: busybox
    command: ["sh", "-c", "ls /etc/redis/; sleep 7200"]
    volumeMounts:
     - name: config
       mountPath: /etc/redis
       readOnly: true
 volumes:
  - name: config
    configMap:
     name: myconfigredis
     # 只暴露指定ConfigMap条目，例如：只显示redis1.conf条目，不显示redis.conf条目
     items:
        # 指定暴露ConfigMap条目的key
      - key: redis1.conf
        # 指定暴露ConfigMap条目的key重命名为新的文件名
        path: my-redis1.conf

# 查看pod日志，日志只输出my-redis1.conf
kubectl logs -f pod1

# mountPath以目录方式挂载会导致目录中已存在的文件被隐藏
# 针对以上缺陷使用ConfigMap独立条目作为文件被挂载且不隐藏文件夹中其他文件
# 指定只暴露redis1.conf
# 2.yaml 内容如下: 
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: kubia
    image: busybox
    command: ["sh", "-c", "ls /etc/redis; sleep 7200"]
    volumeMounts:
     - name: config
       mountPath: /etc/redis/my-redis.conf
       subPath: redis1.conf
 volumes:
  - name: config
    configMap:
     name: myconfigredis
```







### Secret用法

> configmap和secret对比，secret条目的内容以base64格式编码。secret条目可以用于存储二进制文件大小限制于1MB。secret卷存储于内存(secret采用内存文件系统挂载secret到容器目录)。



#### 默认令牌secret介绍

> 每个pod都会被自动挂载上一个secret卷。这个secret包含3个条目分别为ca.crt、namespace、token，包含了从pod内部安全访问kubernetes API服务器所需的全部信息。

```
# 用于创建pod
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: kubia
    image: busybox
    command: ["sh", "-c", "ls /etc/redis; sleep 7200"

# 创建pod
kubectl create -f 1.yaml

# 查看pod详细信息，可以看到pod Volumes中挂载了一个名为default-token-przdr secret卷，并且能够看到Mounts显示secret卷被挂载到/var/run/secrets/kubernetes.io/serviceaccount目录中
kubectl describe pod pod1

# 查看secret卷列表
kubectl get secret

# 查询secret卷详细信息
kubectl describe secret default-token-przdr

# 进入pod查看secret卷挂载目录
kubectl exec -it pod1 sh
/ # cd /var/run/secrets/kubernetes.io/serviceaccount
/ # ls -alh
```



#### secret卷存储于内存中

> 通过挂载secret卷至文件夹/etc/nginx/certs将证书和私钥成功传递给容器。secret卷采用内存文件系统tmpfs挂载到容器目录中，存储在secret中的数据不会写入磁盘，这样就无法被窃取。

```
# 创建https证书
openssl genrsa -out https.key 2048
openssl req -new -x509 -key https.key -out https.cert -days 3650 -subj /CN=www.kubia-example.com

# 创建secret
kubectl create secret generic demo-secret1 --from-file=https.key --from-file=https.cert

# 在pod中使用secret
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - image: nginx:alpine
    name: nginx
    volumeMounts:
     - name: certs
       mountPath: /etc/nginx/certs
       readOnly: true
    ports:
     - containerPort: 80
 volumes:
  - name: certs
    secret:
     secretName: demo-secret1

# 查看 /etc/nginx/certs对应的mount point，可以发现是使用tmpfs文件系统
kubectl exec pod1 -- mount | grep certs
```



#### 使用secret配置nginx https

```
## 创建nginx configmap

# my-nginx-config.conf内容如下:
server {
	listen	80;
	listen	443 ssl;
	server_name	www.kubia-example.com;
	ssl_certificate	certs/https.cert;
	ssl_certificate_key	certs/https.key;
	ssl_protocols	TLSv1 TLSv1.1 TLSv1.2;
	ssl_ciphers	HIGH:!aNULL:!MD5;
	
	location / {
		root /usr/share/nginx/html;
		index index.html index.htm;
	}
}

# 创建configmap
kubectl create configmap demo-config1 --from-file=my-nginx-config.conf

# 创建https证书
openssl genrsa -out https.key 2048
openssl req -new -x509 -key https.key -out https.cert -days 3650 -subj /CN=www.kubia-example.com

# 创建bar文件，内容为foo
echo bar > foo

# 创建secret
kubectl create secret generic secret-https --from-file=https.key --from-file=https.cert --from-file=foo

# 查看secret详细信息
kubectl get secret secret-https -o yaml

# 在pod中使用secret
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - image: nginx:alpine
    name: nginx
    volumeMounts:
     - name: https-config
       mountPath: /etc/nginx/conf.d
       readOnly: true
     - name: certs
       mountPath: /etc/nginx/certs
       readOnly: true
    ports:
     - containerPort: 80
 volumes:
  - name: https-config
    configMap:
     name: demo-config1
     items:
      - key: my-nginx-config.conf
        path: https.conf
  - name: certs
    secret:
     secretName: secret-https

# 测试nginx是否正确使用secret中的证书和密钥
kubectl port-forward pod1 443:443
curl https://localhost -k -v
```



#### 通过环境变量暴露secret条目

```
# 从键值对创建secret
kubectl create secret generic demo-secret1 --from-literal=key1=value1

# 查看secret详细信息
kubectl get secret demo-secret1 -o yaml

# 通过环境变量暴露secret条目到pod中
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - image: busybox
    name: busybox
    command: ["/bin/sh", "-c", "sleep 3600;"]
    env:
     - name: MY_KEY1
       valueFrom:
        secretKeyRef:
         name: demo-secret1
         key: key1
                 
# 创建pod
kubectl create -f 1.yaml

# 进入pod查看环境变量
kubectl exec -it pod1 sh
/ # env
```



#### 私有镜像拉取时提供帐号和密码

> https://kubernetes.io/docs/tasks/configure-pod-container/pull-image-private-registry/

```shell
# 创建帐号和密码secret
kubectl create secret docker-registry regcred --docker-server=my.docker.hub --docker-username=xxx --docker-password=xxxx

# 查看secret
kubectl get secret regcred --output=yaml

# 在pod中使用secret拉取镜像
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





## 从应用访问pod元数据以及其他资源

> downward api用于暴露那些不能预先知道的数据，比如pod的IP、主机名或者是pod自身的名称、pod的标签和注解。
>
> downward api可以通过环境变量或者downward api卷传递downard api相关数据给容器。



### 通过环境变量暴露元数据

> 能够通过downward api获取k8s相关信息并通过环境变量传递到容器中。
>
> https://kubernetes.io/docs/concepts/workloads/pods/downward-api/#available-fields
> https://kubernetes.io/docs/tasks/inject-data-application/environment-variable-expose-pod-information/

```shell
### 例子1
# 通过env和envFrom使用环境变量暴露元数据
# 1.yaml 内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: kubia
    image: busybox
    command: ["sh", "-c", "env; sleep 7200"]
    #resources:
    # requests:
    #  cpu: 15m
    #  memory: 100Ki
    # limits:
    #  cpu: 100m
    #  memory: 4Mi
    env:
     - name: POD_NAME
       valueFrom:
        # 引用pod manifest中的元数据名称字段，而不是设定一个具体值
        fieldRef:
         fieldPath: metadata.name
     - name: POD_NAMESPACE
       valueFrom:
        fieldRef:
         fieldPath: metadata.namespace
     - name: POD_IP
       valueFrom:
        fieldRef:
         fieldPath: status.podIP
     - name: NODE_NAME
       valueFrom:
        fieldRef:
         fieldPath: spec.nodeName
     - name: SERVICE_ACCOUNT
       valueFrom:
        fieldRef:
         fieldPath: spec.serviceAccountName
     - name: CONTAINER_CPU_REQUEST_MILLICORES
       valueFrom:
        # 容器请求的cpu和内存使用量是引用resourceFieldRef字段而不是fieldRef字段
        resourceFieldRef:
         resource: requests.cpu
         # 对于资源相关的字段，我们定义一个基数单位，从而生成每一部分的值
         # 例如: 设定cpu资源请求为15m，基数divisior为1m时，环境变量CONTAINER_CPU_REQUEST_MILLICORES的值为15
         divisor: 1m
     - name: CONTAINER_MEMORY_LIMIT_KIBIBYTES
       valueFrom:
        resourceFieldRef:
         resource: limits.memory
         # 例如: 设定内存使用限制为4Mi，技术divisor为1Ki时，环境变量CONTAINER_MEMORY_LIMIT_KIBIBYTES值为4096
         divisor: 1Ki

# 查看pod1环境变量
kubectl logs pod1




### 例子2，和上面例子1一样通过环境变量暴露pod元数据
# 1.yaml 内容如下:
apiVersion: v1
kind: Pod
metadata:
  name: pod1
spec:
  containers:
    - name: test-container
      image: busybox
      command: [ "sh", "-c"]
      args:
      - while true; do
          echo -en '\n';
          printenv MY_NODE_NAME MY_POD_NAME MY_POD_NAMESPACE;
          printenv MY_POD_IP MY_POD_SERVICE_ACCOUNT;
          sleep 10;
        done;
      env:
        - name: MY_NODE_NAME
          valueFrom:
            fieldRef:
              fieldPath: spec.nodeName
        - name: MY_POD_NAME
          valueFrom:
            fieldRef:
              fieldPath: metadata.name
        - name: MY_POD_NAMESPACE
          valueFrom:
            fieldRef:
              fieldPath: metadata.namespace
        - name: MY_POD_IP
          valueFrom:
            fieldRef:
              fieldPath: status.podIP
        - name: MY_POD_SERVICE_ACCOUNT
          valueFrom:
            fieldRef:
              fieldPath: spec.serviceAccountName
  restartPolicy: Never
  
# 创建pod
kubectl create -f 1.yaml 

# 查看pod环境变量
kubectl logs pod1
```



### 通过downwardAPI卷来传递元数据

> NOTE: pod标签和注解只能够使用downwardAPI卷来暴露。

```shell
### 使用downwardAPI卷暴露元数据
# 1.yaml 内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1
 annotations:
  key1: value1
  key2: |
   multi
   line
   value
 labels:
  foo: bar
spec:
 containers:
  - name: kubia
    image: busybox
    command: ["sh", "-c", "ls /etc/downward; sleep 7200;"]
    volumeMounts:
     - name: downward
       mountPath: /etc/downward
 volumes:
  - name: downward
    downwardAPI:
     items:
      - path: "podName"
        fieldRef:
         fieldPath: metadata.name
      - path: "podNamespace"
        fieldRef:
         fieldPath: metadata.namespace
      - path: "labels"
        fieldRef:
         fieldPath: metadata.labels
      - path: "annotations"
        fieldRef:
         fieldPath: metadata.annotations
      - path: "containerCpuRequestMilliCores"
        resourceFieldRef:
         # 打暴露容器可使用的资源限制或者资源请求，必须指定引用资源字段对应的容器名称
         containerName: kubia
         resource: requests.cpu
         divisor: 1m
      - path: "containerMemoryLimitBytes"
        resourceFieldRef:
          containerName: kubia
          resource: limits.memory
          divisor: 1
          
# 通过downwardAPI卷暴露的元数据被暴露到/etc/downward目录下
kubectl logs pod1

# 进入pod /etc/downward目录查看元数据
kubectl exec -it pod1 /bin/sh
/ # cd /etc/downward/
/etc/downward # ls
/etc/downward # cat annotations 
/etc/downward # cat podName
/etc/downward # cat labels



### 修改标签和注解。可以在pod运行时修改标签和注解。如我们所愿，当标签和注解被修改后，k8s会更新存储有相关信息的文件，从而pod可以获取最新的数据。这也解析了为何不能通过环境变量的方式暴露标签和注解，在环境变量方式下，一旦标签和注解被修改，新的值将无法暴露。
# 修改标签
kubectl label pod pod1 foo=bar1 --overwrite

# 再次查看labels文件内容会被同步到最新状态
kubectl exec pod1 cat /etc/downward/labels
```





### 调用kubernetes API

> https://www.jianshu.com/p/862314e0f56f
> https://kubernetes.io/docs/reference/generated/kubernetes-api/v1.21/#pod-v1-core
>
> REST api构成
>
> - 核心组， 对应的ApiVersion: v1对应的rest path为/api/v1
> - 其它的group的rest path为/apis/$GROUP_NAME/$VERSION(例如：apis/batch/v1、/apis/apps/v1), 对应的 apiVersion:$GROUP_NAME/$VERSION(例如：apiVersion: batch/v1、apiVersion: apps/v1)



#### 在本机获取kubernetes API URL并测试连通性

```
# 获取kubernetes API URL，control plane即kubernetes API URL
kubectl cluster-info

# 测试kubernetes API URL连通性，如果此时api返回403错误表示kubernetes API是连通的
curl https://192.168.1.188:6443 -k
```



#### 通过kubectl proxy访问kubernetes API服务器

> kubectl proxy命令启动了一个代理服务来接收来自你本机的http连接并转发至API服务器，同时处理身份认证，所以不需要每次请求都上传认证凭证。

```
# 在本地8001端口启动代理，我们也无须传递其他任何参数，因为kubectl已经知晓所需的所有参数(API服务器URL、认证凭证等)
kubectl proxy

# 测试代理，返回了大部分api组和版本信息，其中可以看出 /apis/batch支持v1和v1beta1两个版本
# 获取所有api-version，相当 kubectl api-versions命令
# 由api-group/version组成，例如：batch/v1、events.k8s.io/v1
curl localhost:8001

## 通过 /apis/batch 研究job资源API组的 REST endpoint

# 获取api可用版本和客户端推荐使用版本信息，可以看到batch api支持v1和v1beta1两个版本，并且指出客户端推荐使用v1版本
curl localhost:8001/apis/batch

# 查看指定版本下的api信息
# kind: APIResourceLIst表示该API组资源类型为APIResourceList。
# resources中name: jobs表示资源对应的endpoint为 /apis/batch/v1/jobs，name: jobs/status表示资源对应的endpoint为 /apis/batch/v1/jobs/status。
# namespaced: true表示资源是属于某个命名空间的。
# resources中kind: Job表示资源类型为Job。
# resources中verbs: ["create", "delete", "deletecollection", ...]表示可以通过endpoint恢复、修改以及删除Job资源。
curl localhost:8001/apis/batch/v1

# 获取kind对应的apiVersion
kubectl api-resources -o wide

## 列举集群中所有的Job实例

# 用于创建job
apiVersion: batch/v1
kind: Job
metadata:
 name: job1
spec:
 manualSelector: true
 completions: 6 # 总共需要执行多少个pod
 parallelism: 3 # 并行运行pod的数量，如果不指定表示一个一个执行
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

# 创建job
kubectl create -f 1.yaml

# 通过kubernetes API查询job列表
curl localhost:8001/apis/batch/v1/jobs



## 查询指定job详细信息

# 查询job详细信息时需要指定job所属的命名空间
curl localhost:8001/apis/batch/v1/namespaces/default/jobs/job1
```



#### 在pod内使用curl与API服务器通讯

```shell
# 用于创建pod，1.yaml 内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: kubia
    image: alpine/curl
    command: ["sh", "-c", "sleep 7200"]
    
# 执行命令赋予所有服务账号（也可以说所有pod）的集群管理员权限，否则在pod中使用curl调用API时会提示权限不足错误，这个命令赋予了所有服务账户(也可以说所有pod)的集群管理员权限，允许他们执行任何需要的操作，很明显这是一个危险的操作，永远都不应该在生产的集群中执行。
kubectl create clusterrolebinding permissive-binding --clusterrole=cluster-admin --group=system:serviceaccounts

# 进入pod shell
kubectl exec -it pod1 /bin/sh
# 测试curl是否存在
/ # curl
# 查看环境变量，在容器内通过查询KUBERNETES_SERVICE_HOST和KUBERNETES_SERVICE_PORT这两个环境变量就可以获取API服务器的ip地址和端口
/ # env
# 通过k8s默认创建的kubernetes服务请求API服务器，此时返回403错误表示pod和API服务器能够正常通讯
/ # curl https://kubernetes -k
# 使用服务器ca证书请求API服务器，否则会报告SSL certificate problem，此时返回403错误表示pod和API服务器能够正常通讯
/ # curl --cacert /var/run/secrets/kubernetes.io/serviceaccount/ca.crt https://kubernetes
# 获取API服务器认证token
/ #
# 把API token设置为环境变量
/ # TOKEN=$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)
# 打印 TOKEN 环境变量
/ # echo $TOKEN
/ # curl --cacert /var/run/secrets/kubernetes.io/serviceaccount/ca.crt -H "Authorization: Bearer $TOKEN" https://kubernetes
/ #
# 使用curl调用API查询所有pod列表
/ # NS=$(cat /var/run/secrets/kubernetes.io/serviceaccount/namespace)
# 打印 NS 环境变量
/ # echo $NS
# 查询指定命名空间中所有pod
/ # curl --cacert /var/run/secrets/kubernetes.io/serviceaccount/ca.crt -H "Authorization: Bearer $TOKEN" https://kubernetes/api/v1/namespaces/$NS/pods
/ #
```



#### 通过ambassador容器简化与API服务器的通讯

> curl向在ambassador容器内运行代理发送普通http请求(不包含任何授权相关的表头)，然后代理向API服务器发送https请求，通过发送凭证来对客户端授权，同时通过验证证书来识别服务器身份。

```shell
# 用于创建pod，1.yaml 内容如下:
apiVersion: v1
kind: Pod
metadata:
 name: pod1
spec:
 containers:
  - name: kubia
    image: alpine/curl
    command: ["sh", "-c", "sleep 7200"]
  - name: ambassador
    image: luksa/kubectl-proxy

# 进入pod shell
kubectl exec -it pod1 /bin/sh
/ # NS=$(cat /var/run/secrets/kubernetes.io/serviceaccount/namespace)
# 查看环境变量命名空间
/ # echo $NS
# 在kubia容器请求localhost:8001实质上是通过ambassador容器的kubectl proxy代理API调用
/ # curl localhost:8001/api/v1/namespaces/$NS/pods
/ #
```





#### 使用客户端库与API服务器通讯

> 参考 demo-kubernetes/client-fabric8





## deployment声明式地升级应用



### 删除旧版本pod，使用新版本pod替换

> NOTE: 只是演示目的，生成环境不采用此方式升级应用。

```shell
# v1版本nodejs
# app.js 内容如下:
const http = require("http")
const os = require("os")

console.log("Kubia server starting...")

var handler = function(request, response) {
    response.writeHead(200)
    response.end("This is v1 app, hostname " + os.hostname() + "\n")
}

var www = http.createServer(handler)
www.listen(8080)

# Dockerfile 内容如下:
FROM node:7

ADD app.js /app.js
ENTRYPOINT ["node", "app.js"]

# 编译镜像v1
docker build --tag docker.118899.net:10001/yyd-public/demo-k8s-upgrade:v1 .

# 推送镜像v1
docker push docker.118899.net:10001/yyd-public/demo-k8s-upgrade:v1

# v2版本nodejs
# app.js 内容如下:
const http = require("http")
const os = require("os")

console.log("Kubia server starting...")

var handler = function(request, response) {
    response.writeHead(200)
    response.end("This is v2 app, hostname " + os.hostname() + "\n")
}

var www = http.createServer(handler)
www.listen(8080)

# 编译镜像v2
docker build --tag docker.118899.net:10001/yyd-public/demo-k8s-upgrade:v2 .

# 推送镜像v2
docker push docker.118899.net:10001/yyd-public/demo-k8s-upgrade:v2

# 运行镜像v1
docker run --rm --name=demo -p 8080:8080 docker.118899.net:10001/yyd-public/demo-k8s-upgrade:v1

# 打开浏览器测试docker容器

# 创建版本v1 replicaset
# 1.yaml 内容如下:
apiVersion: v1
kind: Service
metadata:
 name: myservice1
spec:
 sessionAffinity: None
 type: ClusterIP
 selector:
  app: kubia
 ports:
  - port: 8080
    targetPort: 8080

---
apiVersion: apps/v1
kind: ReplicaSet
metadata:
 name: replicaset1
spec:
 replicas: 3
 selector:
  matchLabels:
   app: kubia
 template:
  metadata:
   labels:
    app: kubia
  spec:
   containers:
    - name: kubia
      image: docker.118899.net:10001/yyd-public/demo-k8s-upgrade:v1

# 查询服务列表
kubectl get service

# 访问v1版本的app
while true; do curl 10.1.26.31:8080; sleep 5; done;

# 把yaml文件版本修改为v2后应用最新的yaml
kubectl apply -f 1.yaml

# 手动删除旧的v1版本的pod
kubectl get pod
kubectl delete pod replicaset1-hq96v replicaset1-lq6cn replicaset1-w67km

# v1版本的pod被删除会，replicaset会根据最新的yaml创建v2版本的pod，流量逐渐被切换为v2版本
while true; do curl 10.1.26.31:8080; sleep 5; done;
```





### 使用ReplicationController实现自动的滚动升级(kubectl rolling-update)

> NOTE: kubectl rolling-update命令已经被取消不存在。





### 使用Deployment声明式地升级应用

> NOTE: 采用此方式升级应用。
>
> **升级策略**
>
> - 重建更新(Recreate): 删除所有旧版本pod，重新创建新版本pod
>
> - 滚动更新(RollingUpdate): 删除一部分旧版本pod，创建一部分新版本pod，如此重复最终所有更新替换所有旧版本pod



#### 重建更新

```shell
# 1.yaml 内容如下:
apiVersion: v1
kind: Service
metadata:
 name: myservice1
spec:
 sessionAffinity: None
 type: ClusterIP
 selector:
  app: kubia
 ports:
  - port: 8080
    targetPort: 8080

---
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
spec:
 strategy:
  type: Recreate
 replicas: 3
 selector:
  matchLabels:
   app: kubia
 template:
  metadata:
   labels:
    app: kubia
  spec:
   containers:
    - name: kubia
      image: docker.118899.net:10001/yyd-public/demo-k8s-upgrade:v1
      
# 手动修改yaml版本为v2后使用kubectl apply -f 1.yaml更新 deployment，deployment会自动删除旧的pod后创建新的pod
kubectl apply -f 1.yaml

# 查看滚动升级状态
kubectl rollout status deployment deployment1

# 观察更新过程，应用服务会断开一段时间
kubectl get service
while true; do curl 10.1.108.248:8080; sleep 5; done;
```



#### 滚动更新

```shell
# 1.yaml 内容如下:
apiVersion: v1
kind: Service
metadata:
 name: myservice1
spec:
 sessionAffinity: None
 type: ClusterIP
 selector:
  app: kubia
 ports:
  - port: 8080
    targetPort: 8080

---
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
spec:
 replicas: 3
 strategy:
  type: RollingUpdate # 滚动更新策略
  rollingUpdate:
   maxUnavailable: 25%
   maxSurge: 25%
 selector:
  matchLabels:
   app: kubia
 template:
  metadata:
   labels:
    app: kubia
  spec:
   containers:
   - name: kubia
     image: docker.118899.net:10001/yyd-public/demo-k8s-upgrade:v1

# 手动修改yaml版本为v2后使用kubectl apply -f 1.yaml更新 deployment，deployment会自动使用新的pod逐渐替换旧的pod
kubectl apply -f 1.yaml 

# 查看滚动升级状态
kubectl rollout status deployment deployment1

# 观察更新过程，应用服务不会断开
kubectl get service
while true; do curl 10.1.236.130:8080; sleep 5; done;
```



#### 使用spec.minReadySeconds减慢滚动升级速度

```shell
# 使用spec.minReadySeconds指定pod之间rollingupdate速度为60秒
# 1.yaml 内容如下:
apiVersion: v1
kind: Service
metadata:
 name: myservice1
spec:
 sessionAffinity: None
 type: ClusterIP
 selector:
  app: kubia
 ports:
  - port: 8080
    targetPort: 8080

---
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
spec:
 # rollingupdate新建的pod指定多少秒后才认为ready状态并进行下一轮rollingupdate
 minReadySeconds: 60
 replicas: 3
 strategy:
  type: RollingUpdate # 滚动更新策略
  rollingUpdate:
   maxUnavailable: 25%
   maxSurge: 25%
 selector:
  matchLabels:
   app: kubia
 template:
  metadata:
   labels:
    app: kubia
  spec:
   containers:
   - name: kubia
     image: docker.118899.net:10001/yyd-public/demo-k8s-upgrade:v1

# 创建资源
kubectl apply -f 1.yaml

# 手动修改版本为v2在应用yaml
kubectl apply -f 1.yaml

# 使用kubectl rollout status观察滚动升级速度
kubectl rollout status deployment deployment1
```





#### 回滚到上一个版本

```shell
# 注意：annotations.kubernetes.io/change-cause对应rollout history change-cause
# 1.yaml 内容如下:
apiVersion: v1
kind: Service
metadata:
 name: myservice1
spec:
 sessionAffinity: None
 type: ClusterIP
 selector:
  app: kubia
 ports:
  - port: 8080
    targetPort: 8080

---
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
 annotations:
  # 对应rollout history change-cause
  kubernetes.io/change-cause: "测试v1"
spec:
 replicas: 3
 strategy:
  type: RollingUpdate # 滚动更新策略
  rollingUpdate:
   maxUnavailable: 25%
   maxSurge: 25%
 selector:
  matchLabels:
   app: kubia
 template:
  metadata:
   labels:
    app: kubia
  spec:
   containers:
   - name: kubia
     image: docker.118899.net:10001/yyd-public/demo-k8s-upgrade:v1
     
# 手动修改版本为v2后应用yaml
kubectl apply -f 1.yaml

# 查看当前应用版本
kubectl get service
curl 10.1.26.45:8080

# 回滚到上一个版本
kubectl rollout undo deployment deployment1

# 查看当前应用版本
kubectl get service
curl 10.1.26.45:8080

# 查看deployment滚动升级历史
kubectl rollout history deployment deployment1
```



#### 回滚到一个特定的版本

> 版本回退原理是通过多个replicaset实现的
> NOTE: 可以通过deployment.spec.revisionHistoryLimit属性限制历史版本数量。

```shell
# 1.yaml 内容如下:
apiVersion: v1
kind: Service
metadata:
 name: myservice1
spec:
 sessionAffinity: None
 type: ClusterIP
 selector:
  app: kubia
 ports:
  - port: 8080
    targetPort: 8080

---
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
 annotations:
  # 对应rollout history change-cause
  kubernetes.io/change-cause: "测试v1"
spec:
 replicas: 3
 strategy:
  type: RollingUpdate # 滚动更新策略
  rollingUpdate:
   maxUnavailable: 25%
   maxSurge: 25%
 selector:
  matchLabels:
   app: kubia
 template:
  metadata:
   labels:
    app: kubia
  spec:
   containers:
   - name: kubia
     image: docker.118899.net:10001/yyd-public/demo-k8s-upgrade:v1

# 创建资源
kubectl apply -f 1.yaml 

# 手动修改yaml image到v2，修改annotations.kubernetes.io/change-cause到"测试v2"
kubectl apply -f 1.yaml 

# 观察v1升级v2过程
while true; do curl 10.1.12.32:8080; sleep 5; done;

# 查看版本更新历史
kubectl rollout history deployment deployment1
# 依旧保留旧的replicaset为了回滚历史使用
kubectl get replicaset

# 回退到指定版本
kubectl rollout undo deployment deployment1 --to-revision=1

# 观察回退过程
while true; do curl 10.1.12.32:8080; sleep 5; done;

# 查看回退状态
kubectl rollout status deployment deployment1

# 回退后的replicaset状态
kubectl get replicaset
```







#### 使用maxSurge和maxUnavailable控制滚动升级速率

> maxSurge: 超过预期副本数(spec.replicas)的百分比或者指定个数，例如replicas=5，maxSurge=0，那么在rollingupdate期间最多有5个ready的pod在运行，其中1个新pod + 4个旧pod。
>
> maxUnavailable: 在rollingupdate期间有想对于预期副本数(spec.replicas)百分比或者指定个数pod处于不可用状态(Terminating状态)。

```shell
# 1.yaml 内容如下:
apiVersion: v1
kind: Service
metadata:
 name: myservice1
spec:
 sessionAffinity: None
 type: ClusterIP
 selector:
  app: kubia
 ports:
  - port: 8080
    targetPort: 8080

---
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
spec:
 # rollingupdate新建的pod指定多少秒后才认为ready状态并进行下一轮rollingupdate
 minReadySeconds: 30
 replicas: 5
 strategy:
  type: RollingUpdate # 滚动更新策略
  rollingUpdate:
   maxUnavailable: 1
   maxSurge: 0
 selector:
  matchLabels:
   app: kubia
 template:
  metadata:
   labels:
    app: kubia
  spec:
   containers:
   - name: kubia
     image: docker.118899.net:10001/yyd-public/demo-k8s-upgrade:v1
     
# 当前版本的replicaset和预期想符合有5个pod正在运行
kubectl get replicaset

# 修改yaml image为v2并应用后，v2版本对应的replicatset有一个pod，v1版本的replicaset有4个pod，使用命令kubectl get pod有一个v1版本的pod处于Terminating状态
kubectl apply -f 1.yaml
kubectl get replicaset

# rollingupdate期间ready状态的pod为5,Terminating状态的pod为1，符合预期的maxSurge和maxUnavailable设置值
kubectl get pod
```





#### 使用暂停和恢复滚动更新实现金丝雀发布

```shell
# 1.yaml 内容如下:
apiVersion: v1
kind: Service
metadata:
 name: myservice1
spec:
 sessionAffinity: None
 type: ClusterIP
 selector:
  app: kubia
 ports:
  - port: 8080
    targetPort: 8080

---
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
spec:
 # rollingupdate新建的pod指定多少秒后才认为ready状态并进行下一轮rollingupdate
 minReadySeconds: 15
 replicas: 5
 strategy:
  type: RollingUpdate # 滚动更新策略
  rollingUpdate:
   maxUnavailable: 1
   maxSurge: 0
 selector:
  matchLabels:
   app: kubia
 template:
  metadata:
   labels:
    app: kubia
  spec:
   containers:
   - name: kubia
     image: docker.118899.net:10001/yyd-public/demo-k8s-upgrade:v1

# 观察当前pod版本
kubectl get service
while true; do curl 10.1.12.32:8080; sleep 2; done;

# 手动修改yaml image为v2后并应用yaml和暂停rollingupdate
kubectl apply -f 1.yaml && kubectl rollout pause deployment deployment1

# 有1个v2版本对应的replicaset创建1个新的v2版本pod，有4个旧版v1版本的pod正在运行
# 实质是有部分流量流向v2版本pod进行测试观察
kubectl get replicaset
kubectl get pod

# 如果v2版本pod没有问题，恢复rollingupdate继续更新其他pod
kubectl rollout resume deployment deployment1

# 如果v2版本pod有问题，回滚到上一版本
kubectl rollout resume deployment deployment1 && kubectl rollout undo deployment deployment1
```



#### 配置就绪探针来阻止新版本未就绪pod滚动更新

> NOTE: 通过deployment.spec.progressDeadlineSeconds为滚动升级配置deadline，在指定时间内不能完成滚动升级则视为失败。

```shell
# v1版本nodejs
# app.js 内容如下:
const http = require("http")
const os = require("os")

console.log("Kubia server starting...")

var handler = function(request, response) {
    response.writeHead(200)
    response.end("This is v1 app, hostname " + os.hostname() + "\n")
}

var www = http.createServer(handler)
www.listen(8080)

# Dockerfile 内容如下:
FROM node:7

ADD app.js /app.js
ENTRYPOINT ["node", "app.js"]

# 编译镜像v1
docker build --tag docker.118899.net:10001/yyd-public/demo-k8s-rollingupdate-readiness:v1 .

# 推送镜像v1
docker push docker.118899.net:10001/yyd-public/demo-k8s-rollingupdate-readiness:v1

# v2版本nodejs
# app.js 内容如下:
const http = require("http")
const os = require("os")

console.log("Kubia server starting...")

var counter = 0
var handler = function(request, response) {
	console.log("Received request from " + request.connection.remoteAddress)
    if(counter < 5)
    	response.writeHead(200)
	else
		response.writeHead(500)
    counter++
    response.end("This is v2 app, hostname " + os.hostname() + "\n")
}

var www = http.createServer(handler)
www.listen(8080)

# 编译镜像v2
docker build --tag docker.118899.net:10001/yyd-public/demo-k8s-rollingupdate-readiness:v2 .

# 推送镜像v2
docker push docker.118899.net:10001/yyd-public/demo-k8s-rollingupdate-readiness:v2

# 用于创建deployment
apiVersion: v1
kind: Service
metadata:
 name: myservice1
spec:
 sessionAffinity: None
 type: ClusterIP
 selector:
  app: kubia
 ports:
  - port: 8080
    targetPort: 8080

---
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
spec:
 # rollingupdate新建的pod指定多少秒后才认为ready状态并进行下一轮rollingupdate
 minReadySeconds: 15
 replicas: 5
 strategy:
  type: RollingUpdate # 滚动更新策略
  rollingUpdate:
   maxUnavailable: 1
   maxSurge: 0
 selector:
  matchLabels:
   app: kubia
 template:
  metadata:
   labels:
    app: kubia
  spec:
   containers:
   - name: kubia
     image: docker.118899.net:10001/yyd-public/demo-k8s-rollingupdate-readiness:v1
     readinessProbe:
      periodSeconds: 1
      httpGet:
       path: /
       port: 8080

# 测试deployment是否正常
kubectl get service
while true; do curl 10.1.93.56:8080; sleep 2; done;

# 手动修改版本为v2后执行下面命令
kubectl apply -f 1.yaml

# 通过下面命令查看deployment滚动更新情况，可以看到其中一个replicaset有对应的pod但是一直都没有处于ready状态，其中一个pod一直处于未ready状态。rollout status命令一直显示"Waiting for deployment "deployment1" rollout to finish: 1 out of 5 new replicas have been updated..."表示滚动更新有问题不继续执行更新。
kubectl get replicaset
kubectl get pod
kubectl rollout status deployment deployment1

# 这种情况下取消出错的滚动更新到上一个版本
kubectl rollout undo deployment deployment1
```





## kubernetes原理





### kubernetes架构

> kubernetes集群分为两部分: kubernetes控制平面和工作节点。
>
> 控制平面: 负责控制并使得整个集群正常运转。回顾一下，控制平面包含如下组件: etcd分布式持久化存储、API服务器、调度器、控制器管理器。这些组件用来存储、管理集群状态，但他们不是运行应用的容器。
>
> 工作节点: 运行容器的任务依赖于每个工作节点上运行的组件: kubelet、kubelet服务代理(kube-proxy)、容器运行时(Docker、rkt或者其他)
>
> 附加组件: 除了控制平面和运行在节点上的组件，还要有几个附加组件，这样才能提供所有之前讨论的功能。包含: kubernetes DNS服务器、仪表版、Ingress控制器、Heapster(容器集群监控)、容器网络接口插件。



#### kubernetes组件的分布式特性

**组件间如何通信**

> kubernetes系统组件间只能通过API服务器通信，他们之间不会直接通信。API服务器是和etcd通信的唯一组件。其他组件不会直接和etcd通信，而是通过API服务器来修改集群状态。

```shell
# 检查控制平面组件的状态
# API服务器对外暴露了一个名为ComponentStatus的API资源，用来显示每个控制平面组件的健康状态。可以通过kubectl列出各个组件以及他们的状态
# NOTE: 在执行下面命令是报告connection refused错误，参考这个链接解决问题: https://www.cnblogs.com/varden/p/15122227.html
kubectl get componentstatus
```

**单组件运行多实例**

> 尽管工作节点上的组件都需要运行在同一个节点上，控制平面的组件可以被简单地分割在多台服务器上。为了保证高可用性，控制平面的每个组件可以有多个实例。etcd和API服务器的多个实例可以同时并行工作，但是，调度器和控制器管理器在给定时间内只能有一个实例起作用，其他实例处于待命状态。

**组件是如何运行的**

> kubelet是唯一一个直接作为常规系统组件来运行的组件，他把其他组件作为pod来运行。为了将控制平面作为pod来运行，kubelet被部署在master上。

```shell
# 查看master上kube-system命名空间中的pod，所有控制平面组件在主节点上作为pod运行。这里有三个工作节点，每一个节点运行kube-proxy和一个flannel pod，用来为pod提供重叠网络。
kubectl get pod -n kube-system -o wide
```



#### kubernetes如何使用etcd

> 本书让你创建的所有对应pod、ReplicationController、服务和私密凭证等，需要以持久化方式存储到某个地方，这样他们的manifest在API服务器重启和失败的时候才不会丢失。为此，kubernetes使用etcd。etcd是一个响应快、分布式、一致性key-value存储。因为他是分布式的，故可以运行多个etcd实例来获取高可用性和更好的性能。
>
> 唯一能够直接和etcd通信的是kubernetes的API服务器。所有其他组件通过API服务器间接地读取、写入数据到etcd。这带来一些好处，其中之一就是增强乐观锁系统、验证系统的健壮性；并且，通过把实际存储机制从其他组件抽离，未来替换起来也更容易。值得强调的是，etcd是kubernetes存储集群状态和元数据的唯一的地方。

```shell
### 资源如何存储在etcd中
# NOTE: 做实验时未成功etcdctl命令连接etcd服务报告错误: {"level":"warn","ts":"2023-10-17T04:50:24.870Z","caller":"clientv3/retry_interceptor.go:62","msg":"retrying of unary invoker failed","target":"endpoint://client-69669693-0578-44e7-9b57-ee4eecc5ffaa/127.0.0.1:2379","attempt":0,"error":"rpc error: code = DeadlineExceeded desc = latest balancer error: all SubConns are in TransientFailure, latest connection error: connection closed"} Error: context deadline exceeded


# 进入etcd服务器shell
kubectl exec -it etcd-demo-k8s-master sh
# kubernetes存储所有数据到etcd的/registry下
etcdctl ls /registry
# 显示/registry/pods目录的内容，从输出结果名称可以看出，这两个条目对应default和kube-system命名空间，意味着pod按命名空间存储。
etcdctl ls /registry/pods
# 显示/registry/pods/default目录下的条目，从输出结果每个条目对应一个单独的pod。这些不是目录，而是键值对。
etcdctl ls /registry/pods/default
# 显示指定条目中存储的内容，从输出结果这是一个JSON格式的pod定义。API服务器将资源的完整JSON形式存储到etcd中。
etcdctl get /registry/pods/default/kubia-129041347-wt6ga
```



#### API服务器做了什么

> kubernetes API服务器作为中心组件，其他组件或者客户端(如kubectl)都会调用他。以RESTful API的形式提供了可以查询、修改集群状态的CRUD接口。他将状态存储到etcd中。
>
> API服务器除了提供一种一致的方式将对象存储到etcd，也对这些对象做校验，这样客户端就无法存入非法的对象了(直接写入存储的话是有可能的)。除了校验，还会处理乐观锁，这样对于并非更新的情况，对对象做更改就不会被其他客户端覆盖。
>
> API服务器的客户端之一就是本书一开始就介绍使用的命令行工具kubectl。举个例子，当以JSON文件创建一个资源，kubectl通过一个HTTP POST请求将文件内容发布到API服务器。API服务器接收到请求后先通过认证插件认证客户端，再通过授权插件授权客户端，通过准入控制插件验证AND/OR修改资源请求，验证资源以及持久化存储。
>
> **通过认证插件认证客户端**
> 首先，API服务器需要认证发送请求的客户端。这是通过配置在API服务器上的一个或者多个认证插件来实现的。API服务器会轮流调用这些插件，直到有一个能够确认是谁发送了该请求。这是通过检查HTTP请求实现的。
> 根据认证方式，用户信息可以从客户端证书或者HTTP Authorization头获取。插件抽取客户端的用户名、用户ID和归属组。这些数据在下一阶段，认证的时候会用到。
>
> **通过授权插件授权客户端**
> 除了认证插件，API服务器还可以配置使用一个或多个授权插件。他们的作用是决定认证的用户是否可以对请求资源执行请求操作。例如，当创建pod时，API服务器会轮询所有的授权插件，来确认该用户是否可以在请求命名空间创建pod。一旦插件确认了用户可以执行该操作，API服务器会继续下一步操作。
>
> **通过准入控制插件验证 AND/OR 修改资源请求**
> 如果请求尝试创建、修改或者删除一个资源，请求需要经过准入控制插件的验证。同理，服务器会配置多个准入控制插件。这些插件会因为各种原因修改资源，可能会初始化资源定义中漏配的字段为默认值甚至重写他们。插件甚至会去修改并不在请求中的相关资源，同时会因为某些原因拒绝一个请求。资源需要经过所有准入控制插件的验证。
> 准入控制插件包括: AlwaysPullImages插件重写pod的imagePullPolicy为Always，强制每次部署pod时拉取镜像。ServiceAccount插件未明确定义服务账户的使用默认账户。NamespaceLifecycle插件防止在命名空间中创建正在被删除的pod，或者不存在的命名空间中创建pod。ResourceQuota插件保证特定命名空间中的pod只能使用该命名空间分配数量的资源，如CPU和内存。
> 更多的准入控制插件可以在 https://kubernetes.io/docs/admin/admission-controllers/ 中查看kubernetes文档。
>
> **验证资源以及持久化存储**
> 请求通过了所有的准入控制插件后，API服务器会验证存储到etcd的对象，然后返回一个响应给客户端。



#### API服务器如何通知客户端资源变更

> 除了前面讨论的，API服务器没有做其他额外的工作。例如，当你创建一个ReplicaSet资源时，他不会去创建pod，同时他不会去管理服务的端点。那是控制器管理器的工作。
> API服务器甚至也没有告诉这些控制器去做什么。他做的就是，启动这些控制器，以及其他一些组件来监控已部署资源的变更。控制平面可以请求订阅资源被创建、修改或者删除的通知。这使得组件可以在集群元数据变化时候执行任何需要做的任务。
> 客户端通过创建到API服务器的HTTP连接来监听变更。通过此连接，客户端会接收到监听对象的一系列变更通知。每当更新对象，服务器把新版本对象发送至所有监听该对象的客户端。

```shell
# kubectl支持监听资源变更事件
kubectl get pod -w
```



#### 了接调度器

> 前面已经学习过，我们通常不会去指定pod应该运行在哪个集群节点上，这项工作交给调度器。宏观来看，调度器的操作比较简单。就是利用API服务器的监听机制等待新创建的pod，然后给每个新的、没有节点集的pod分配节点。
> 调度器不会命令选中的节点(或者节点上运行的kubelet)去运行pod。调度器做的就是通过API服务器更新pod定义。然后API服务器再去通知kubelet(同样，通过之前描述的监听机制)该pod已经被调度过。当目标节点上的kubelet发现该pod被调度到本节点，他就会创建并且运行pod的容器。
> 尽管宏观上调度的过程看起来比较简单，但实际上为pod选择最佳节点的任务并不简单。当然，最简单的调度方式是不关心节点上已经运行的pod，随机选择一个节点。另一方面，调度器可以利用高级技术，例如机器学习，来预测接下来几分钟或者几个小时哪种类型的pod将会被调度，然后以最大的硬件利用率、无须重新调度已运行pod的方式来调度。kubernetes的默认调度器实现方式处于最简单和最复杂程度之间。
>
> **默认的调度算法**
> 选择节点操作可以分解为两个部分: 1、过滤所有节点，找出能够分配给pod的可用节点列表。2、对可用节点按优先级排序，找出最优节点。如果多个节点都有最高的优先级分数，那么循环分配，确保平均分配给pod。
>
> **pod高级调度**
> 考虑另外一个例子。假设一个pod有多个副本。理想情况下，你会期望副本能够分散在尽可能多的节点上，而不是全部分配到单独一个节点上。该节点的柁机会导致pod支持的服务不可用。但是如果pod分散在不同的节点上，单个节点柁机，并不会对服务造成什么影响。
> 默认情况下，归属同一服务和ReplicaSet的pod会分散在多个节点上。但不保证每次都是这样。不过可以通过定义pod的亲缘性、非亲缘规则强制pod分散在集群内或者集中在一起。
> 仅通过这两个简单的例子就说明了调度有多复杂，因为他依赖于大量的因子。因此，调度器既可以配置成满足特定的需要或者基础设施特性，也可以整体替换为一个定制的实现。可以抛开调度器运行一个kubernetes，不过那样的话，就需要手动实现调度了。
>
> **使用多个调度器**
> 可以在集群中运行多个调度器而非单个。然后，对每一个pod，可以通过在pod特性中设置schedulerName属性指定调度器来调度特定的pod。
> 未设置该属性的pod由默认调度器调度，因此其schedulerName被设置为default-scheduler。其他设置了该属性的pod会被默认调度器忽略掉，他们要么是手动调用，要么被监听这类pod的调度器调用。
> 可以实现自己的调度器，部署到集群，或者可以部署有不同配置项的额外kubernetes调度器实例。



#### 介绍控制器管理器中运行的控制器

> 如前面提到的，API服务器只做了存储资源到etcd和通知客户端有变更的工作。调度器则只是给pod分配节点，所以需要有活跃的组件确保系统真实的状态朝API服务器定义的期望的状态收敛。这个工作由控制器管理器里的控制器来实现。
> 单个控制器、管理器进程当前组合了多个执行不同非冲突任务的控制器。这些控制器最终会被分解到不同的进程，如果需要的话，我们能够用自定义实现替换他们每一个。控制器包括: Replication管理器（ReplicationController资源的管理器）。ReplicaSet、DaemonSet以及Job控制器。Deployment控制器。Statefulset控制器。Node控制器。Service控制器。Endpoints控制器。Namespace控制器。PersistentVolume控制器。其他。
>
> **了解控制器做了什么以及如何做的**
> 控制器做了许多不同的事情，但是他们都通过API服务器监听资源（部署、服务等）变更，并且不论是创建新对象还是更新、删除已有对象，都对变更执行相应操作。大多数情况下，这些操作涵盖了新建其他资源或者更新监听的资源本身（例如，更新对象的status）。
> 总的来说，控制器执行一个“调和”循环，将实际状态调整为预期状态（在资源spec部分定义），然后将新的实际状态写入资源的status部分。控制器利用监听机制来订阅变更，但是由于使用监听机制并不保证控制器不会漏掉时间，所以仍然需要定期执行重列举操作来确保不会丢掉什么。
> 控制器之间不会直接通讯，他们甚至不知道其他控制器的存在。每个控制器都连接到API服务器，通过监听机制，请求订阅该控制器负责的一系列资源的变更。
>
> **Replication管理器**
> 启动ReplicationController资源的控制器叫作Replication管理器。我们已经介绍过ReplicationController是如何工作的，其实不是ReplicationController做了实际的工作，而是Replication管理器。让我们快速回顾一下该控制器做了什么，这有助于你理解其他控制器。
> 我们说过，ReplicationController的操作可以理解为一个无限循环，每次循环，控制器都会查找符合其pod选择器定义的pod的数量，并且将该数值和期望的复制集（replica）数量做比较。
> 既然你知道了API服务器可以通过监听机制通知客户端，那么明显地，控制器不会每次循环去轮询pod，而是通过监听机制订阅可能影响期望的复制集（replica）数量或者符合条件pod数量的变更事件。任何该类型的变化，将触发控制器重新检查期望的以及实际的复制集数量，然后做出相应操作。
> 你已经知道，当运行的pod实例太少时，ReplicationController会运行额外的实例，但他自己实际上不会去运行pod。他会创建新的pod清单，发布到API服务器，让调度器以及kubelet来做调度工作并运行pod。
> Replication管理器通过API服务器操纵pod API对象来完成其工作。所有控制器就是这样运作的。
> 现在，总体来说你应该对每个控制器做了什么，以及是如何工作的有个比较好的感觉了。再一次强调，所有这些控制器是通过API服务器来操作API对象的。他们不会直接和kubelet通信或者发送任何类型的指令。实际上他们不知道kubelet的存在。控制器更新API服务器的一个资源后，kubelet和kubernetes  service proxy（也不知道控制器的存在）会做他们的工作，例如启动pod容器、加载网络存储，或者就服务而言，创建跨pod的负载均衡。
>
> **ReplicaSet、DaemonSet以及Job控制器**
> 原理同上
>
> **Deployment控制器**
> 原理同上
>
> **Statefulset控制器**
> 原理同上
>
> **Node控制器**
> 原理同上
>
> **Service控制器**
> 原理同上
>
> **Endpoint控制器**
> 原理同上
>
> **Namespace控制器**
> 原理同上
>
> **PersistentVolume控制器**
> 原理同上



#### kubelet做了什么

> 简单地说，kubelet就是负责所有运行在工作节点上的内容组件。他第一个任务就是在API服务器中创建一个Node资源来注册该节点。然后需要持续监控API服务器是否把该节点分配给pod，然后启动pod容器。具体实现方式是告知配置好的容器运行时（Docker、CoreOS的rkt，或者其他一些东西）来从特定容器镜像运行容器。kubelet随后持续监控运行的容器，向API服务器报告他们的状态、事件和资源消耗。
> kubelet也是运行容器存活探针的组件，当探针报错时他会重启容器。最后一点，当pod从API服务器删除时，kubelet终止容器，并通知服务器pod已经被终止了。



#### kubernetes service proxy的作用

> 除了kubelet，每个工作节点还会运行kube-proxy，用于确保客户端可以通过kubernetes API连接到你定义的服务。kube-proxy确保对服务ip和端口的连接最终能到达支持服务（或者其他，非pod服务终端）的某个pod处。如果有多个pod支撑一个服务，那么代理会发挥对pod的负载均衡作用。
>
> **为什么被叫作代理**
> kube-proxy最初实现为userspace代理。利用实际的服务器集成接收连接，同时代理给pod。为了拦截发往服务ip的连接，代理配置了iptables规则（iptables是一个管理linux内核数据包过滤功能的工具），重定向连接到代理服务器。这个模式称为userspace代理模式。数据流向: 客户端 > iptables > kube-proxy > pod。
> kube-proxy之所以叫这个名字是因为他确实就是一个代理器，不过当前性能更好的实现方式仅仅通过iptables规则重定向数据包到一个随机选择的后端pod，而不会传递到一个实际的代理服务器。这个模式成为iptables代理模式。数据流向: 客户端 > iptables > pod。
> 两种模式的主要区别是: 数据包是否会传递给kube-proxy，是否必须在用户空间处理，或者数据包只会在内核处理（内核空间）。这对性能有巨大影响。
> 另外一个小的区别是: userspace代理模式以轮询模式对连接做负载均衡，而iptables代理模式不会，他随机选择pod。当只有少数客户端使用一个服务时，可能不会平均分布在pod中。例如，如果一个服务有两个pod支持，但有5个左右的客户端，如果你看到4个连接pod A，而只有一个连接到pod B，不必惊讶。对于客户端数量更多的pod，这个问题就不会特别明显。



#### 介绍kubernetes插件

> **如何部署插件**
> 通过提交yaml清单文件到API服务器（本书的通用做法），这些组件会成为插件并作为pod部署。有些组件是通过Deployment资源或者ReplicationController资源部署的，有些是通过DaemonSet。
> 例如，写作本书时，在Minikube中，Ingress控制器和仪表板插件按照ReplicationController部署。
> DNS插件作为Deployment部署。
>
> **DNS服务器如何工作**
> 集群中的所有pod默认配置使用集群内DNS服务器。这使得pod能够轻松地通过名称查询到服务，甚至是无头服务pod的ip地址。
> DNS服务pod通过kube-dns服务对外暴露，使得该pod能够像其他pod一样在集群中移动。服务的ip地址在集群每个容器的/etc/resolv.conf文件的nameserver中定义。kube-dns pod利用API服务器的监控机制来订阅Service和Endpoint的变动，以及DNS记录的变更，使得客户端（相对地）总是能够获取到最新的DNS信息。客观地说，在Service和Endpoint资源发生变化到DNS pod收到订阅通知时间点之间，DNS记录可能会无效。
>
> **Ingress控制器如何工作**
> 和DNS插件相比，Ingress控制器的实现有点不同，但他们大部分的工作方式相同。Ingress控制器运行一个反向代理服务器（例如，类似nginx），根据集群中定义的Ingress，Service以及Endpoint资源来配置该控制器。所以需要订阅这些资源（通过监听机制），然后每次其中一个发生变化则更新代理服务器的配置。
> 尽管Ingress资源的定义指向一个Service，Ingress控制器会直接将流量转到服务的pod而不经过服务ip。当外部客户端通过Ingress控制器连接时，会对客户端ip进行保存，这使得在某些用例中，控制器比Service更受欢迎。
>
> **使用其他插件**
> 你已经了解了DNS服务器和Ingress控制器插件同控制器管理器中运行的控制器比较相似，除了他们不会仅通过API服务器监听、修改资源，也会接收客户端的连接。
> 其他插件也类似。他们都需要监听集群状态，当有变更时执行相应动作。



### 控制器如何协作

> 准备包含Deployment清单的yaml文件，通过kubectl提交到kubernetes。kubectl通过HTTP POST请求发送清单到kubernetes API服务器。API服务器检查Deployment定义，存储到etcd，返回响应给kubectl。现在事件链开始被揭示出来。如下描述整个事件链:
>
> **Deployment控制器生成ReplicaSet**
> 当新创建Deployment资源时，所有通过API服务器监听机制监听Deployment列表的客户端马上会收到通知。其中有个客户端叫Deployment控制器，之前讨论过，该控制器是一个负责处理部署事务的活动组件。
> 一个Deployment由一个或者多个ReplicaSet支持，ReplicaSet后面会创建实际的pod。当Deployment控制器检查到有一个新的Deployment对象时，会按照Deployment当前定义创建ReplicaSet。这包括通过kubernetes API创建一个新的ReplicaSet资源。Deployment控制器完全不会去处理单个pod。
>
> **ReplicaSet控制器创建pod资源**
> 新创建的ReplicaSet由ReplicaSet控制器（通过API服务器创建、修改、删除ReplicaSet资源）接收。控制器会考虑replica数量、ReplicaSet中定义的pod选择器，然后检查是否有足够的满足选择器的pod。
> 然后控制器会基于ReplicaSet的pod模板创建pod资源（当Deployment控制器创建ReplicaSet时，会从Deployment复制pod模板）。
>
> **调度器分配节点给新创建的pod**
> 新创建的pod目前保存在etcd中，但是他们每个都缺少一个重要的东西，那就是他们还没有任何关联的节点，他们的nodeName属性还未被设置。调度器会监控像这样的pod，发现一个，就会为pod选择最佳节点，并将节点分配给pod。pod的定义现在就会包含他应该运行在哪个节点。
> 目前，所有的一切都发生在kubernetes控制平面中。参与这个全过程的控制器没有做其他具体的事情，除了通过API服务器更新资源。
>
> **kubelet运行pod容器**
> 目前，工作节点还没有做任何事情，pod容器还没有被启动起来，pod容器的镜像还没有下载。
> 随着pod目前分配给了特定的节点，节点上的kubelet终于可以工作了。kubelet通过API服务器监听pod变更，发现有新的pod分配到本节点后，会检查pod定义，然后命令Docker或者任何使用的容器运行时来启动pod容器，容器运行时就会去运行容器。

```shell
# 观察集群事件
kubectl get event -w
```



### 了解运行中的pod是什么

> 当pod运行时，让我们仔细看一下，运行的pod到底是什么。如果pod包含单个容器，你认为kubelet会只运行单个容器，还是更多？
> 通过运行下面命令看到输出结果，如你所望，你看到了nginx容器，以及一个附加容器。从COMMAND列判断，附加容器没有做任何事情（容器命令是"pause"）。如果仔细观察，你会发现容器是在nginx容器前几秒创建的。他的作用是什么呢？
> 被暂停的容器将一个pod所有的容器收纳到一起。还记得一个pod的所有容器是如何共享同一个网络和linux命名空间的吗？暂停的容器是一个基础容器，他的唯一目的就是保存所有的命名空间。所有pod的其他用户定义容器使用pod的该基础容器的命名空间。
> 实际的应用容器可能会挂掉并重启。当容器重启，容器需要处于与之前相同的linux命名空间中。基础容器使这成为可能，因为他的生命周期和pod绑定，基础容器pod被调度直到被删除一直会运行。如果基础pod在这期间被关闭，kubelet会重新创建他，以及pod的所有容器。

```shell
# 运行nginx pod
kubectl run nginx --image=nginx

# 查看所有容器
docker ps
```



### 跨pod网络

> 现在，你知道每个pod有自己唯一ip地址，可以通过一个扁平的、非NAT网络和其他pod通信。kubernetes是如何做到这一点的？简单来说，kubernetes不负责这块。网络是由系统股哪里员或者Container Network Interface（CNI）插件建立的，而非kubernetes本身。
>
> **网络应该是什么样的**
> kubernetes并不会要求你使用特定的网络技术，但是授权pod（或者更准确地说，其容器）不论是否运行在同一工作节点上，可以互相通信。pod用于通信的网络必须是: pod自己认为的ip地址一定和所有其他节点认为该pod拥有的ip地址一致。
> 举个例子，当pod A连接（发送网络包）到pod B时，pod B获取到的源ip地址必须和pod A自己认为的ip地址一致。其间应该没有网络地址转换（NAT）操作（pod A发送到pod B的包必须保持源和目的地址不变。
> 这很重要，保证运行在pod内部的应用网络的简洁性，就像运行在同一个网关机上一样。pod没有NAT使得运行在其中的应用可以自己注册在其他pod中。
> 例如，有客户端pod X和pod Y，为所有通过他们注册的pod提供通知服务。pod X连接到pod Y并且告诉pod Y，“你好，我是pod X，ip地址为 1.2.3.4，请把更新发送到这个ip地址”。提供服务的pod可以通过收到的ip地址连接到第一个pod。
> pod到节点及节点到pod通讯也应用了无NAT通信。但是当pod和internet上的服务通信时，pod发送包的源ip不需要改变，因为pod的ip是私有的。向外发送包的源ip地址会被改成主机工作节点的ip地址。
>
> **深入了解网络工作原理**
> 我们看到创建了pod的ip地址以及网络命名空间，由基础设施容器（暂停容器）来保存这些信息，然后pod容器就可以使用网络命名空间了。pod网络接口就是生成在基础设施容器的一些东西。让我们看一下接口是如何被创建的，以及如何连接到其他pod接口。
> **同节点pod通讯**
> 基础设施容器启动之前，会为容器创建一个虚拟Ethernet接口对（一个veth pair），其中一个对的接口保留在主机的命名空间中（在节点上运行ifconfig命令时可以看到vethxxx的条目），而其他的对被移入容器网路命名空间，并重命名为eth0。两个虚拟接口就像管道的两端（或者说像Ethernet电缆连接的两个网络设备，从一端进入，另一端出来）。
> 主机网络命名空间的接口会绑定到容器运行时配置使用的网络桥接上。从网络的地址段中取ip地址赋值给容器内的eth0接口。应用的任何运行在容器内部的程序都会发送数据到eth0网络接口（在容器命名空间的那一个），数据从主机命名空间的另一个veth接口出来，然后发送给网桥。这意味着任何连接到网桥的网络接口都可以接收该数据。
> 如果pod A发送网络包到pod B，报文首先会经过pod A的veth对到网桥然后经过pod B的veth对。所有节点上的容器都会连接到同一个网桥，意味着他们都能够互相通信。但是要让运行在不同节点上的容器之间能够通信，这些节点的网桥需要以某种方式连接起来。
> **不同节点上的pod通信**
> 有多种连接不同节点上的网桥的方式。可以通过overlay或者underlay网段，或者常规的三层路由，我们会在后面看到。
> 跨整个集群的pod的ip地址必须是唯一的，所以跨节点的网桥必须使用非重叠地址段，防止不同节点上的pod拿到同一个ip。节点A上的网桥使用10.1.1.0/24 ip段，节点B上的网桥使用10.1.2.0/24 ip段，确保没有ip地址冲突的可能性。
> 下面描述通过三曾网络支持跨两个节点pod通信，节点的物理网络接口也需要连接到网桥。节点A的路由表需要记录节点B的网桥cidr，这样所有目的地为10.1.2.0/24 的报文会被路由到节点B，同时节点B的路由表需要记录节点A的网桥cidr，这样发送到10.1.1.0/24的包会被发送到节点A。
> 按照该配置，当报文从一个节点上容器发送到其他节点上的容器，报文先通过veth pair，通过网桥到节点物理适配器，然后通过网线传到其他节点的物理适配器，再通过其他节点的网桥，最终经过veth pair到达目标容器。
> 仅当节点连接到相同网关、之间没有任何路由时上述方案有效。否则，路由器会仍包因为他们所涉及的pod ip是私有的。当然，也可以配置路由使其在节点间能够路由报文，但是随着节点数增加，配置会变得更困难，也更容易出错。因此，使用SDN（软件定义网络）技术可以简化问题，SDN可以让节点忽略底层网络拓扑，无论多复杂，结果就像连接到同一个网关上。从pod发出的报文会被封装，通过网络发送给运行其他pod的网络，然后被解封装、以原始格式传递给pod。
>
> **引入容器网络接口**
> 为了让连接容器到网络更加方便，启动一个项目容器网络接口（CNI)。CNI允许kubernetes可配置使用任何CNI插件。这些插件包括: Calico、Flannel、Romana、Weave Net、其他。
> 安装一个网络插件并不难，只需要部署一个包含DaemonSet以及其他支持资源的yaml。每个插件项目首页都会提供这样一个yaml文件。如你所想，DaemonSet用于往所有集群节点部署一个网络代理，然后会绑定CNI接口到节点。但是，注意kubelet需要用 --network-plugin=cni 命令启动才能使用CNI。



### 服务是如何实现的

> **引入kube-proxy**
> 和Service相关的任何事情都由每个节点上运行的kube-proxy进程处理。开始的时候，kube-proxy确实是一个proxy，等待连接，对每个进来的连接，连接到一个pod。这称为userspace（用户空间）代理模式。后来，性能更好的iptables代理模式取代了他。iptables代理模式目前是默认的模式，如果你有需要也仍然可以配置kubernetes使用旧模式。
> 在我们继续之前，先快速回顾一下Service的几个知识点，对理解下面几段有帮助。
> 我们之前了解过，每个Service有其自己稳定的ip地址和端口。客户端（通常为pod）通过连接该ip和端口使用服务。ip地址是虚拟的，没有被分配给任何网络接口，当数据包离开节点时也不会列为数据包的源或目的ip地址。Service的一个关键细节是，他们包含一个ip、端口对（或者针对多端口Service有多个ip、端口对），所以服务ip本身并不代表任何东西。这就是为什么你不能够ping他们。
>
> **kube-proxy如何使用iptables**
> 当在API服务器中创建一个服务时，虚拟ip地址立刻就会分配给他。之后很短时间内，API服务器会通知所有运行在工作节点上的kube-proxy客户端有一个新服务已经被创建了。然后，每个kube-proxy都会让该服务在自己的运行节点上可寻址。原理是通过建立一些iptables规则，确保每个目的地为服务的ip/端口对的数据包被解析，目的地址被修改，这样数据包就会被重定向到支持服务的一个pod。
> 除了监控API对Service的更改，kube-proxy也监控对Endpoint对象的更改。我们在之前已经讨论过，下面回顾一下，因为你基本上不会去手动创建他们，所以比较容易忘记他们的存在。Endpoint对象保存所有支持服务的pod的ip/端口对（一个ip/端口对也可以指向除pod之外的其他对象）。这就是为什么kube-proxy必须监听所有Endpoint对象。毕竟Endpoint对象在每次新创建或删除支持pod时都会发生变更，当pod的就绪状态发生变化或者pod的标签发生变化，就会落入或超出服务的范畴。
> 这里举个例子描述客户端pod发送到支持服务的一个pod的数据包流程。让我们检查一下当通过客户端pod发送数据包时发生了什么。包目的地址初始设置为服务的ip和端口（在本例中，Service是在172.30.0.1:80）。发送到网络之前，节点A的内核会根据配置在该节点上的iptables规则处理数据包。
> 内核会检查数据包是否匹配任何这些iptables规则。其中有个规则规定如果有任何数据包的目的地ip等于172.30.0.1、目的地端口等于80，那么数据包的目的地址ip和端口应该被替换为随机选中的pod的ip和端口。
> 本例中的数据包满足规则，故而它的ip/端口被改变了。在本例中，pod B2被随机选中了，所有数据包的目的地址ip变更为10.1.2.1（pod B2的ip），端口改为8080（Service中定义的目标端口）。就好像是，客户端pod直接发送数据包给pod B而不是通过Service。









## API服务器的安全防护



### 基础概念

> 正常用户和ServiceAccount都可以属于一个或者多个组。我们已经讲过认证插件会连同用户名和用户ID返回组。组可以一次给多个用户赋予权限，而不是必须单独给用户赋予权限。
> 有插件返回的组仅仅是表示组名称的字符串，但是系统内置的组会有一些特许的含义。
> system:unauthenticated 组用于所有认证插件都不会认证客户端身份的请求。
> system:authenticated 组会自动分配给一个成功通过认证的用户。
> system:serviceaccounts 组包含所有在系统中的ServiceAccount。
> system:serviceaccounts:<namespace> 组包含了所有在特定命名空间中的ServiceAccount。
>
> ServiceAccount特性:
> 每个pod都与一个ServiceAccount相关联，他代表了运行在pod中应用程序的身份证明。ServiceAccount用户名的格式: system:serviceaccount:<namespace>:<service account name>
> ServiceAccount就像Pod、Secret、ConfigMap等一样都是资源，他们作用在单独的命名空间，为每个命名空间自动创建一个默认的ServiceAccount（如果在创建pod时不指定ServiceAccount则使用此默认ServiceAccount）。在pod的manifest文件中，可以用指定账户名称的方式将一个ServiceAccount赋值给一个pod。如果不显式地指定ServiceAccount的账户名称，pod会使用在这个命名空间中的默认ServiceAccount。
>
> RBAC授权规则是通过四种资源来进行配置的，他们可以分为两个组:
> Role（角色）和ClusterRole（集群角色），他们指定了在资源上可以执行那些动词。
> RoleBinding（角色绑定）和ClusterRoleBinding（集群角色绑定），他们将上述角色绑定到特定的用户、组或ServiceAccount上。
> 角色和集群角色，或者角色绑定和集群角色绑定之间的区别在于角色和角色绑定是命名空间的资源，而集群角色和集群角色绑定是集群级别的资源（不是命名空间的）。
> 一个Role只允许访问和Role在同一命名空间的资源。如果你希望允许跨不同命名空间访问资源，就必须在每个命名空间中创建Role和RoleBinding。
>
> ClusterRole和ClusterRoleBinding特性: 支持授权非资源型URL，支持授权非命名空间资源。
>
> **何时使用具体的role和binding的组合**
> 集群级别的资源（Nodes、PersistentVolumes、.....）: ClusterRole + ClusterRoleBinding。
> 非资源型URL（/api、/healthz、.....）: ClusterRole + ClusterRoleBinding。
> 在任何命名空间中的资源（和跨所有命名空间的资源）: ClusterRole + ClusterRoleBinding。
> 在具体命名空间的资源（在多个命名空间中重用这个相同的ClusterRole）: ClusterRole + RoleBinding。
> 在具体命名空间中的资源（Role必须在每个命名空间中定义）: Role + RoleBinding。



### 创建ServiceAccount

```shell
### 通过命令创建ServiceAccount
# 创建ServiceAccount
kubectl create serviceaccount foo

# 查看ServiceAccount详细信息
kubectl describe serviceaccount foo

# 查看ServiceAccount被自动创建并绑定的密钥，密钥中包含了ca.crt、namespace、token条目
kubectl get secret
kubectl describe secret foo-token-xt757




### 通过yaml文件创建ServiceAccount
# 1.yaml内容如下:
apiVersion: v1
kind: ServiceAccount
metadata:
 name: foo
 
# 创建ServiceAccount
```



### 创建ServiceAccount并在pod中使用新创建的ServiceAccount

```shell
# 1.yaml 内容如下:
apiVersion: v1
kind: ServiceAccount
metadata:
 name: foo

---
apiVersion: v1
kind: Pod
metadata:
 name: curl-custom-serviceaccount
spec:
 # 指定pod使用自定义的ServiceAccount
 serviceAccountName: foo
 containers:
  - name: main
    image: alpine/curl
    command: ["sh", "-c", "sleep 7200;"]
  - name: ambassador
    image: luksa/kubectl-proxy
    
# 创建ServiceAccount和相关pod
kubectl apply -f 1.yaml 

# 删除之前创建的clusterrolebinding(所有ServiceAccount能够操作任何资源)
kubectl delete clusterrolebinding permissive-binding

# 查看ServiceAccount列表
kubectl get serviceaccount

# 显示ServiceAccount详细信息
kubectl describe serviceaccount foo

# 显示ServiceAccount对应的Secret详细信息
kubectl describe secret foo-token-8vlsq

# 进入curl容器shell
kubectl exec -it curl-custom-serviceaccount -c main /bin/sh
# 进入容器内查看当前使用的ServiceAccount token和Secret的token对应
/ # cat /var/run/secrets/kubernetes.io/serviceaccount/token 
# 查看命名空间default下的ServiceAccount，只是foo ServiceAccount权限不足
/ # curl localhost:8001/api/v1/namespaces/default/serviceaccounts
```



### 使用Role和RoleBinding

```shell
# 创建命名空间foo和pod
kubectl create ns foo
kubectl run test --image=luksa/kubectl-proxy -n foo

# 创建命名空间bar和pod
kubectl create ns bar
kubectl run test --image=luksa/kubectl-proxy -n bar

# 进入命名空间foo中pod的shell
kubectl exec -it test -n foo sh
# 调用services列表api，结果返回403是预期情况，因为默认ServiceAccount没有权限调用此接口
/ # curl localhost:8001/api/v1/namespaces/foo/services

# 创建Role稍后授权给默认ServiceAccount
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
 namespace: foo
 name: service-reader
rules:
 # services是核心apiGroup的资源，所以没有apiGroup名就是""
 - apiGroups: [""]
   verbs: ["get", "list"]
   # 指定资源为services，NOTE: 必须使用复数的名字
   resources: ["services"]

kubectl create -f 1.yaml

# 使用下面命令在bar命名空间中创建Role，下面命令和上面yaml创建Role是等价的语法
kubectl create role service-reader --verb=get --verb=list --resource=services -n bar

# 通过RoleBinding绑定角色到ServiceAccount
kubectl create rolebinding test --role=service-reader --serviceaccount=foo:default -n foo

# 再次进入命名空间foo中pod的shell
kubectl exec -it test -n foo sh
# 调用services列表api，此时结果不再返回403错误而是返回200，说明授权生效了
/ # curl localhost:8001/api/v1/namespaces/foo/services


# 进入命名空间bar中pod的shell
kubectl exec -it test -n bar sh
# 调用services列表api，此时返回403错误，因为命名空间bar中的pod没有权限调用命名空间foo中的services列表api
/ # curl localhost:8001/api/v1/namespaces/foo/services

# 授权命名空间bar中的默认ServiceAccount调用命名空间foo中的services列表api
kubectl create rolebinding test1 --role=service-reader --serviceaccount=bar:default -n foo

# 再次进入命名空间bar中pod的shell
kubectl exec -it test -n bar sh
# 调用services列表api，此时返回200
/ # curl localhost:8001/api/v1/namespaces/foo/services

# 删除所有数据
kubectl delete namespace foo
kubectl delete namespace bar




### 上面例子的yaml等价实现
# 1.yaml 内容如下:
---
apiVersion: v1
kind: Namespace
metadata:
 name: foo

---
apiVersion: v1
kind: Namespace
metadata:
 name: bar

---
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
 name: service-reader
 namespace: foo
rules:
 # ServiceAccount拥有对ServiceAccounts资源的get、list权限
 - apiGroups: [""]
   verbs: ["get", "list"]
   resources: ["services"]
   
---
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
 name: service-reader
 namespace: bar
rules:
 - apiGroups: [""]
   verbs: ["get", "list"]
   resources: ["services"]

---
# 创建RoleBinding绑定Role:service-reader到ServiceAccount:foo:default
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
 name: test
 namespace: foo
roleRef:
 apiGroup: rbac.authorization.k8s.io
 kind: Role
 name: service-reader
subjects:
 - kind: ServiceAccount
   # ServiceAccount名称
   name: default
   # 命名空间名称
   namespace: foo
 
---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
 name: test1
 namespace: foo
roleRef:
 apiGroup: rbac.authorization.k8s.io
 kind: Role
 name: service-reader
subjects:
 - kind: ServiceAccount
   name: default
   namespace: bar

---
apiVersion: v1
kind: Pod
metadata:
 name: test
 namespace: foo
spec:
 containers:
  - name: ambassador
    image: luksa/kubectl-proxy

---
apiVersion: v1
kind: Pod
metadata:
 name: test
 namespace: bar
spec:
 containers:
  - name: ambassador
    image: luksa/kubectl-proxy

# 进入命名空间foo中pod的shell
kubectl exec -it test -c ambassador -n foo sh
# 调用services列表api，此时返回200
/ # curl localhost:8001/api/v1/namespaces/foo/services

# 进入命名空间bar中pod的shell
kubectl exec -it test -c ambassador -n bar sh
# 调用services列表api，此时返回200
/ # curl localhost:8001/api/v1/namespaces/foo/services
```



### 使用ClusterRole和ClusterRoleBinding

> Role和RoleBinding都是命名空间的资源，这意味着他们属于和应用在一个单一的命名空间资源上。但是，如我们所见，RoleBinding可以引用来自其他命名空间中的ServiceAccount。
> 除了这些命名空间里的资源，还存在两个集群级别的RBAC资源: ClusterRole和ClusterRoleBinding，他们不再命名空间里。让我们看看为什么需要他们。
> 一个常规的角色只允许访问和角色在同一命名空间中的资源。如果你希望跨不同命名空间访问资源，就必须要在每个命名空间中创建一个Role和RoleBinding。如果你想将这种行为扩展到所有的命名空间（集群管理员可能需要），需要在每个命名空间中创建相同的Role和RoleBinding。当创建一个新的命名空间时，必须记住也要在新的命名空间中创建这两个资源。
> 正如你在整本书中了解到的，一些特定的资源完全不在命名空间中（包括Node、PersistentVolume、Namespace等等）。我们也提到过API服务器对外暴露了一些不表示资源的URL路径（例如 /healthz）。常规角色不能对这些资源或非资源型的URL进行授权，但是ClusterRole可以。
> ClusterRole是一种集群级资源，他允许访问没有命名空间的资源和非资源型的URL，或者作为单个命名空间内部绑定的公共角色，从而避免必须在每个命名空间中重新定义相同的角色。



#### 允许访问集群级别的资源

```shell
# 创建集群角色允许pod列出集群中PersistentVolume
kubectl create clusterrole pv-reader --verb=get,list --resource=persistentvolumes

# 创建pod
kubectl create namespace foo
kubectl run test --image=luksa/kubectl-proxy -n foo

# 在绑定ClusterRole到pod的ServiceAccount之前，验证pod是否可以列出PersistentVolume
kubectl exec -it test -n foo sh
# 结果返回403表示没有权限列出PersistentVolume
/ # curl localhost:8001/api/v1/persistentvolumes

# 授权集群角色到命名空间foo的默认ServiceAccount
kubectl create clusterrolebinding pv-test --clusterrole=pv-reader --serviceaccount=foo:default

# 再次在pod的shell中列出persistentvolume，此时返回200表示授权成功
/ # curl localhost:8001/api/v1/persistentvolumes


### 使用yaml实现上面场景
# 1.yaml 内容如下:
---
apiVersion: v1
kind: Namespace
metadata:
 name: foo

---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
 name: pv-reader
rules:
 - apiGroups:
    - ""
   verbs:
    - get
    - list
   resources:
    - persistentvolumes

---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
 name: pv-test
roleRef:
 apiGroup: rbac.authorization.k8s.io
 kind: ClusterRole
 name: pv-reader
subjects:
 - kind: ServiceAccount
   name: default
   namespace: foo

---
apiVersion: v1
kind: Pod
metadata:
 name: test
 namespace: foo
spec:
 containers:
  - name: ambassador
    image: luksa/kubectl-proxy
    
# 创建资源
kubectl create -f 1.yaml

# 进入pod的shell
kubectl exec -it test -c ambassador -n foo /bin/sh
# 没有授权访问ServiceAccount资源，此时返回403错误
/ # curl localhost:8001/api/v1/namespaces/default/serviceaccounts
# 已经授权访问集群级别资源PersistentVolumes，此时返回200
/ # curl localhost:8001/api/v1/persistentvolumes

# 删除资源
kubectl delete -f 1.yaml
```



#### 允许访问非资源型URL

##### 了解系统预置的一个ClusterRole和ClusterRoleBinding实例

```shell
# 查看系统预置的ClusterRole system:discovery
# 可以发现，system:discovery引用的是URL路径而不是资源（使用的是非资源URL字段而不是资源字段）。verbs字段只允许在这些URL上使用GET HTTP方法。注意: 对于非资源型URL，使用普通的HTTP动词，如post、put和patch，而不是create或者update。动词需要使用小写的形式指定。
kubectl get clusterrole system:discovery -o yaml

# 查看与ClusterRole绑定的ClusterRoleBinding system:discovery
# yaml内容显示ClusterRoleBinding正如预期的那样指向system:discovery ClusterRole。他绑定到了两个组，分别是system:authenticated和system:unauthenticated，这使得他和所有用户绑定在一起。这意味着每个人都绝对可以访问列在ClusterRole中的URL。
kubectl get clusterrolebinding system:discovery -o yaml

# 根据上面分析结果，测试在默认授权的pod中请求 /api 非资源型URL是允许的
kubectl run test --image=luksa/kubectl-proxy
kubectl exec -it test sh
/ # curl localhost:8001/api
```



##### 使用ClusterRole来授权访问指定命名空间中的资源

> ClusterRole不是必须一直和集群级别的ClusterRoleBinding捆绑使用。他们也可以和常规的有命名空间的RoleBinding进行捆绑。

```shell
# 下面演示这两种情况: ClusterRole和ClusterRoleBinding绑定的主体可以列出所有命名空间中ClsuterRole授权的资源。相反，如果你把ClusterRole绑定到RoleBinding，那么在绑定中列出的主体只能够查看RoleBinding命名空间中的资源。


###  ClusterRole和ClusterRoleBinding绑定的主体可以列出所有命名空间中ClsuterRole授权的资源。
# 创建ClusterRole view-test
---
apiVersion: v1
kind: Namespace
metadata:
 name: foo

---
apiVersion: v1
kind: Namespace
metadata:
 name: bar

---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
 name: view-test
rules:
 - apiGroups:
    - ""
   # 这些都是有命名空间的资源
   resources:
    - configmaps
    - endpoints
    - persistentvolumeclaims
    - pods
    - replicationcontrollers
    - replicationcontrollers/scale
    - serviceaccounts
    - services
   # 只允许读操作，不能对列出的资源进行写操作
   verbs:
    - get
    - list
    - watch

---
apiVersion: v1
kind: Pod
metadata:
 name: test
 namespace: foo
spec:
 containers:
  - name: ambassador
    image: luksa/kubectl-proxy

# 创建ClusterRole
kubectl create -f 1.yaml

# 授予foo命名空间中的默认ServiceAccount ClusterRole view-test角色
kubectl create clusterrolebinding view-test --clusterrole=view-test --serviceaccount=foo:default

# 进入foo命名空间pod的shell
kubectl exec -it test -n foo sh
# 查询foo命名空间中的pod，返回200
/ # curl localhost:8001/api/v1/namespaces/foo/pods
# 查询bar命名空间中的pod，返回200
/ # curl localhost:8001/api/v1/namespaces/bar/pods
# 查询所有命名空间中的pod，返回200
/ # curl localhost:8001/api/v1/pods


### 相反，如果你把ClusterRole绑定到RoleBinding，那么在绑定中列出的主体只能够查看RoleBinding命名空间中的资源。
# 删除上面的clusterrolebinding
kubectl delete clusterrolebinding view-test

# 绑定foo:default到命名空间foo中RoleBinding，这样foo:default只能查看foo命名空间中的pod
kubectl create rolebinding view-test --clusterrole=view-test --serviceaccount=foo:default -n foo

# 再次进入foo命名空间pod的shell
kubectl exec -it test -n foo sh
# 查询foo命名空间中的pod，返回200
/ # curl localhost:8001/api/v1/namespaces/foo/pods
# 查询bar命名空间中的pod，返回403
/ # curl localhost:8001/api/v1/namespaces/bar/pods
# 查询所有命名空间中的pod，返回403
/ # curl localhost:8001/api/v1/pods
```





### 了解默认的ClusterRole和ClusterRoleBinding

> kubernetes提供了一组默认的ClusterRole和ClusterRoleBinding，每次API服务器启动时都会更新他们。这保证了在你错误地删除角色和绑定，或者kubernetes的新版本使用了不同的集群角色和绑定配置时，所有的默认角色和绑定都会被重新创建。
>
> **用view ClusterRole允许对资源的只读访问**
> 他允许读取一个命名空间的大多数资源，除了Role、RoleBinding和Secret。
> **用admin ClusterRole赋予一个命名空间全部的控制权限**
> 一个命名空间中的资源的完全控制权是由admin ClusterRole赋予的。有这个ClusterRole的主体可以读取和修改命名空间中的任何资源，除了ResourceQuota和命名空间资源本身。edit和admin ClusterRole之间的主要区别是能否在命名空间中查看和修改Role和RoleBinding。
> **用cluster-admin ClusterRole得到完全的控制**
> 通过将cluster-admin ClusterRole赋给主体，主体可以获得kubernetes集群完全控制的权限。正如你前面了解的那样，admin ClusterRole不允许用户修改命名空间的ResourceQuota对象或者命名空间资源本身。如果你想允许用户这样做，需要创建一个指向cluster-admin ClusterRole的RoleBinding。这使得RoleBinding中包含的用户能够完全控制创建RoleBinding所在命名空间上的所有方面。
> 如果你留心观察，可能已经知道如何授予用户一个集群中所有命名空间的完全控制权限。就是通过ClusterRoleBinding而不是RoleBinding中引用cluster-admin ClusterRole。
> **了解其他默认的ClusterRole**
> 默认的ClusterRole列表包含了大量的其他的ClusterRole，他们以system:为前缀。这些角色用于各种kubernetes组件中。在他们之中，可以找到如system:kube-scheduler之类的角色，他明显是给调度器使用的，system:node是给kubelet组件使用的。
> 虽然Controller Manager作为一个独立的pod来运行，但是在其中运行的每个控制器都可以使用单独的ClusterRole和ClusterRoleBinding（他们以system:Controller:为前缀）。
> 这些系统的每个ClusterRole都有一个ie匹配的ClusterRoleBinding，他会绑定到系统组件用来身份认证的用户上。例如，system:kube-scheduler ClusterRoleBinding将名称相同的ClusterRole分配给system:kube-scheduler用户，他是调度器作为身份认证的用户名。

```shell
# 查询clusterrolebinding列表，包括系统预置的clusterrolebinding
kubectl get clusterrolebinding

# 查询clusterrole列表，包括系统预置的clusterrole
kubectl get clusterrole
```









## 保障集群内节点和网络安全



### 在pod中使用宿主节点的linux命名空间

> pod中的容器通常在分开的linux命名空间中运行。这些命名空间将容器中的进程和其他容器中，或者宿主默认命名空间中的进程隔离开来。
> 例如，每一个pod有自己的ip和端口空间，这是因为他拥有自己的网络命名空间。类似地，每一个pod拥有自己的进程树，因为他有自己的PID命名空间。同样地，pod拥有自己的IPC命名空间，仅允许同一pod内的进程通过进程间通信（Inter Process Communication，简称IPC）机制进行交流。

#### 在pod中使用宿主节点网络命名空间

> 部分pod（特别是系统pod）需要在宿主节点的默认命名空间中运行，以允许他们看到和操作节点级别的资源和设备。例如，某个pod可能需要使用宿主节点上的网络适配器，而不是自己的虚拟网络设备。这可以通过将pod spec中的hostNetwork设置为true实现。
> 在这种情况下，这个pod可以使用宿主节点的网络接口，而不是拥有自己独立的网络。这意味这这个pod没有自己的ip地址；如果这个pod中的某一进程绑定了某个端口，那么该进程将绑定到宿主节点的端口上。
> kubernetes控制平面组件通过pod部署时，这些pod都会使用hostNetwork选项，让他们的行为与不在pod中运行时相同。

```shell
# 一个使用宿主节点默认的网络命名空间的pod
apiVersion: v1
kind: Pod
metadata:
 name: test
spec:
 # 使用宿主节点的网络命名空间
 hostNetwork: true
 containers:
  - name: main
    image: alpine
    command: ["/bin/sleep", "3600"]

# 创建pod
kubectl create -f 1.yaml

# 在pod中执行ifconfig命令，可以看到pod确实使用了宿主节点的网络命名空间
kubectl exec test ifconfig
```



#### 绑定宿主节点上的端口而不使用宿主节点的网络命名空间

> 一个与此有关的功能可以让pod在拥有自己的网络命名空间的同时，将端口绑定到宿主节点的端口上。这可以通过配置pod的spec.containers.ports字段中某个容器某一端口的hostPort属性来实现。
> 不要混淆使用hostPort的pod和通过NodePort服务暴露的pod，他们是不同的。对于一个使用hostPort的pod，到达宿主节点的端口的连接会被直接转发到pod的对应端口上；然而在NodePort服务中，到达宿主节点的端口连接将被转发到随机选取的pod上（这个pod可能在其他节点上）。另外一个区别是，对于使用hostPort的pod，仅有运行了这类pod的节点会绑定对应的端口；而NodePort类型的服务会在所有节点上绑定端口，即使这个节点上没有运行对应的pod。
> 很重要的一点是，如果一个pod绑定了宿主节点上的一个特定端口，每个宿主节点只能调度一个这样的pod实例，因为两个进程不能绑定宿主机上的同一个端口。调度器在调度pod时会考虑这一点，所以他不会把两个这样的pod调度到同一个节点上，如果要在3个节点上部署4个这样的pod副本，只有3个副本能够成功部署（剩余一个pod保持pending状态）。

```shell
# 将pod中的一个端口绑定到宿主节点默认网络命名空间的端口
# 用于创建pod的yaml
apiVersion: v1
kind: Pod
metadata:
 name: kubia-hostport
spec:
 containers:
  - image: luksa/kubia
    name: kubia
    ports:
     - containerPort: 8080
       # 可以通过pod所在节点的9000端口访问
       hostPort: 9000
       protocol: TCP
       
# 创建pod
kubectl create -f 1.yaml

# 查看pod所在的节点
kubectl get pod -o wide

# SSH连接到pod所在的节点通过9000端口访问pod的服务
curl localhost:9000
```



#### 使用宿主节点的PID和IPC命名空间

> pod spec中的hostPID和hostIPC选项与hostNetwork相似。当他们被设置为true时，pod中的容器会使用宿主节点的PID和IPC命名空间，分别允许他们看到宿主机上的全部进程，或者通过IPC机制与他们通信。

```shell
# 使用宿主节点的PID和IPC命名空间
# 用于创建pod的yaml定义
apiVersion: v1
kind: Pod
metadata:
 name: test
spec:
 # 这个pod使用宿主节点的PID命名空间
 hostPID: true
 # 同样地，pod使用宿主节点的IPC命名空间
 # 将hostIPC设置为true，pod中的进程就可以通过进程间通信机制与宿主机上的其他所有进程进行通信
 hostIPC: true
 containers:
  - name: main
    image: alpine
    command: ["/bin/sleep", "3600"]
    
# 创建pod
kuebctl create -f 1.yaml

# pod中通常之能够看到自己内部的进程，但在这个pod的容器中列出进程，可以看到宿主机上的所有进程，而不仅仅是容器内的进程。
kubectl exec test ps aux
```





### 配置节点的安全上下文

#### 运行pod而不配置安全上文

```shell
# 运行一个没有任何安全上下问配置的pod
kubectl run test --image alpine --restart Never -- /bin/sleep 3600

# 查看容器中的用户ID和组ID，这个容器在用户ID（uid）为0的用户，即root，用户组ID（gid）为0（同样是root）的用户组下运行。
kubectl exec test id

# 删除pod
kubectl delete pod test
```



#### 使用指定用户运行容器

```shell
# 用于创建pod
apiVersion: v1
kind: Pod
metadata:
 name: test
spec:
 containers:
  - name: main
    image: alpine
    command: ["sleep", "3600"]
    securityContext:
     # 你需要指明一个用户id，而不是用户名（id 405对应guest用户）
     runAsUser: 405

# 创建pod
kubectl create -f 1.yaml

# 可以看出该容器在guest用户下运行
kubectl exec test id
```



#### 阻止容器以root用户运行

```shell
# 指定pod以非root运行
apiVersion: v1
kind: Pod
metadata:
 name: test
spec:
 containers:
  - name: main
    image: alpine
    command: ["sleep", "3600"]
    securityContext:
     # 这个容器只允许以非root用户运行
     runAsNonRoot: true
     
# 结果pod不能运行，这是预期结果并且报错: Error: container has runAsNonRoot and image will run as root (pod: "test_default(ff069190-cd04-480b-a5ba-b3376f0990d8)", container: main)
kubectl describe pod test
```



#### 使用特权模式运行pod

> 有时候pod需要做他们的宿主节点上能够做的任何事，例如操作被保护的系统设备，或者使用其他在通常容器中不能使用的内核功能。
> 这种pod的一个样例就是kube-proxy pod，该pod修改宿主机的iptables规则来让kubernetes中的服务规则生效。使用kubeadm部署集群时，你会看到每个节点上都运行了kube-proxy pod，并且可以查看yaml描述文件中所有使用到的特殊特性。
> 为获取宿主机内核的完整权限，该pod需要在特权模式下运行。这可以通过将容器的securityContext的privileged设置为true。

```shell
# 用于创建非特权pod和特权pod
---
apiVersion: v1
kind: Pod
metadata:
 name: test
spec:
 containers:
  - name: main
    image: alpine
    command: ["sleep", "3600"]

---
apiVersion: v1
kind: Pod
metadata:
 name: test1
spec:
 containers:
  - name: main
    image: alpine
    command: ["sleep", "3600"]
    securityContext:
     # 这个容器将在特权模式下运行
     privileged: true

# 列出/dev目录下文件的方式查看先前部署的非特权模式容器中的设备，这个相当短的列表已经列出了全部的设备
kubectl exec test ls /dev

# 完整的设备列表很长，这里已经足以证明这个设备列表远远长于之前的列表。事实上，特权模式的pod可以看到宿主节点上的所有设备。这意味着他可以自由使用任何设备。
kubectl exec test1 ls /dev
```



#### 为容器单独添加内核功能

> 传统的unix实现只区分特权和非特权进程，但是经过多年的发展，linux已经可以通过内核功能支持更细粒度的权限系统。
> 相比于让容器运行在特权模式下以给予其无限的权限，一个更加安全的做法是只给予他使用真正需要的内核功能的权限。kubernetes允许为特定的容器添加内核功能，或者禁用部分内核功能，以允许对容器进行更加精细的权限控制，限制攻击者的侵入的影响。

```shell
### 一个容器通常不允许修改系统时间（硬件时钟的时间），报错: Operation not permitted
apiVersion: v1
kind: Pod
metadata:
 name: test
spec:
 containers:
  - name: main
    image: alpine
    command: ["sleep", "3600"]
    
# 设置硬件时钟的时间
kubectl exec -it test -- date +%T -s "12:00:00"



### 使用securityContext 允许容器修改系统时间
apiVersion: v1
kind: Pod
metadata:
 name: test
spec:
 containers:
  - name: main
    image: alpine
    command: ["sleep", "3600"]
    # 在securityContext中添加或者禁用内核功能
    securityContext:
     capabilities:
      add:
       # 允许容器修改系统时间
       # linux内核功能的名称通常以CAP_开头。但在pod spec中指定内核功能时，必须省略CAP_前缀。
       - SYS_TIME

# 成功修改系统时间不再报错
kubectl exec -it test -- date +%T -s "12:00:00"
kubectl exec -it test -- date
```



#### 在容器禁用内核功能

```shell
### 普通容器默认是支持chown命令的

apiVersion: v1
kind: Pod
metadata:
 name: test
spec:
 containers:
  - name: main
    image: alpine
    command: ["sleep", "3600"]
    
# 在普通容器中运行chown命令是正常的，最后/tmp目录属主修改为guest
kubectl exec -it test -- chown guest /tmp
kubectl exec -it test -- ls -la / | grep tmp

### 使用securityContext禁用chown内核功能

apiVersion: v1
kind: Pod
metadata:
 name: test
spec:
 containers:
  - name: main
    image: alpine
    command: ["sleep", "3600"]
    securityContext:
     capabilities:
      drop:
       # 禁止容器修改文件的所有者
       - CHOWN
       
# 禁用CHOWN内核功能后，不允许在这个pod中修改文件的所有者，报错: Operation not permitted
kubectl exec -it test -- chown guest /tmp
```



#### 阻止对容器根文件系统的写入

> 因为安全原因，你可能需要阻止容器中的进程对容器的根文件系统进行写入，仅允许他们写入挂在的存储卷。
> 假如你在运行一个有隐藏漏洞，可以允许攻击者写入文件系统的PHP应用。这些PHP文件在构建时放入容器的镜像中，并且在容器的根文件系统中提供服务。由于漏洞的存在，攻击者可以修改这些文件，在其中注入恶意代码。
> 这一类攻击可以通过阻止容器写入自己的根文件系统（应用的可执行代码的通常存储位置）来防止。通过securityContext将容器readOnlyRootFileSystem设置为true实现。

```shell
# 用于创建测试的pod
apiVersion: v1
kind: Pod
metadata:
 name: test
spec:
 containers:
 - name: main
   image: alpine
   command: ["sleep", "3600"]
   securityContext:
    # 这个容器的跟文件系统不允许写入
    readOnlyRootFilesystem: true
   volumeMounts:
   # 但是向/volume写入是允许的，因为这个目录挂载了一个存储卷
   - name: my-volume
     mountPath: /volume
     readOnly: false
 volumes:
 - name: my-volume
   emptyDir:
   
# 这个pod中容器虽然以root用户运行，拥有 / 目录的写权限，但在该目录下写入一个文件会失败，错误: touch: /new-file: Read-only file system
kubectl exec -it test touch /new-file

# 对挂载卷的写入是允许的
kubectl exec -it test touch /volume/new-file
kubectl exec -it test -- ls -la /volume
```



#### 设置pod级别的安全上下文

> 以上的例子都是对单独的容器设置安全上下文。这些选项中的一部分也可以从pod级别设定（通过pod.spec.securityContext属性）。他们会作为pod中每一个容器的默认安全上下文，但是会被容器级别的安全上下文覆盖。





### 限制pod使用安全相关的特性

> 之前例子已经介绍了如何在部署pod时在任一宿主节点上做任何想做的事。比如，部署一个特权模式的pod。很明显，需要有一种机制阻止用户使用其中部分功能。集群管理人员可以通过创建PodSecurityPolicy资源来限制对以上提到的安全相关的特性的使用。
> 当有人向API服务器发送pod资源时，PodSecurityPolicy准入控制插件会将这个pod与已经配置的PodSecurityPolicy进行校验。如果这个pod符合集群中已有安全策略，他会被接收并存入etcd；否则他会立即被拒绝。



#### 第一个PodSecurityPolicy例子

```shell
# 用于创建PodSecurityPolicy
apiVersion: policy/v1beta1
kind: PodSecurityPolicy
metadata:
 name: my-psp1
spec:
 # 容器不允许使用宿主节点的IPC、PID和网络命名空间
 hostIPC: false
 hostPID: false
 hostNetwork: false
 # 容器只能绑定宿主节点的10000-11000端口（含端点）或者13000-15000端口
 hostPorts:
 - min: 10000
   max: 11000
 - min: 13000
   max: 15000
 # 容器不能在特权模式下运行
 privileged: false
 # 容器强制使用只读的根文件系统
 readOnlyRootFilesystem: true
 # 容器可以以任意用户和用户组运行
 runAsUser:
  rule: RunAsAny
 fsGroup:
  rule: RunAsAny
 supplementalGroups:
  rule: RunAsAny
 # 他们也可以使用任何SELinux选项
 seLinux:
  rule: RunAsAny
 # pod可以使用所有类型的存储卷
 volumes:
 - '*'

# 
```



### 隔离pod的网络

#### 在一个命名空间中启用网络隔离

> 在默认情况下，某一命名空间中的pod可以被任意来源访问。首先，需要改变这个设定。需要创建一个default-deny NetworkPolicy，他会阻止任何客户端访问中的pod。

```shell
---
apiVersion: v1
kind: Namespace
metadata:
 name: foo

---
apiVersion: v1
kind: Namespace
metadata:
 name: bar

---
apiVersion: v1
kind: Pod
metadata:
 name: test-nginx
 namespace: foo
spec:
 containers:
 - name: main
   image: nginx

---
apiVersion: v1
kind: Pod
metadata:
 name: test-curl
 namespace: bar
spec:
 containers:
 - name: main
   image: alpine/curl
   command: ["sleep", "3600"]

#

apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
 name: default-deny
spec:
 # 空的标签选择器匹配命名空间中所有pod
 podSelector: {}

```









## 计算资源管理

> NOTE: 停止其他工作节点只保留一个并且配置调整为2核

### 为pod中的容器申请资源

> 设置pod的容器资源申请量保证了每个容器能够获得他所需要的资源的最小量。

#### 创建包含资源requests的pod

```shell
# 用于创建测试pod
apiVersion: v1
kind: Pod
metadata:
 name: test
spec:
 containers:
 - name: main
   image: busybox
   command: ["dd", "if=/dev/zero", "of=/dev/null"]
   # 我们为主容器指定了资源请求量
   # 容器申请200毫核（即一个cpu核心时间的1/5）
   # 容器申请10MB内存
   resources:
    requests:
     cpu: 200m
     memory: 10Mi
     
     
# 我们在容器中执行dd命令会消耗尽可能多的cpu，但因为他是单线程运行所以最多之能跑满一个核，而宿主节点有2个cpu，所以这里top命令显示cpu使用率为50%
# 对于2核来说，50%显然就是指一个核，说明容器实际使用量超过了我们pod定义中申请的200毫核。这是符合预期的，因为requests不会限制容器可以使用的cpu数量。我们需要指定cpu限制来实现这一点，稍后会进行尝试
kubectl exec -it test sh
# 在容器中查看cpu使用率
/ # top
```



#### 资源requests如何影响调度

```shell
# 查看节点的资源总量（Capacity）和可分配的资源剩余量（Allocatable）
kubectl get node
kubectl describe node demo-k8s-node0

# 创建一个请求量cpu=800m的pod
kubectl run test1 --image=busybox --restart=Never --requests='cpu=800m,memory=20Mi' -- dd if=/dev/zero of=/dev/null

# 再创建一个请求量cpu=1的pod
# 因为我们之前申请了200m+800m=1000m的pod，加上kube-system命名空间中申请的cpu量已经超过1000m，导致剩余的cpu不足当前申请的cpu=1（1000m），所以pod一直处于Pending状态。
kubectl run test2 --image=busybox --restart=Never --requests='cpu=1,memory=20Mi' -- dd if=/dev/zero of=/dev/null
# 通过命令查看test2 pod部署失败的原因
kubectl describe pod test2

# 删除test1 pod让test2 pod调度成功
kubectl delete pod test1

# 查看test2 pod调度成功
kubectl get pod -o wide
```



#### cpu requests如何影响cpu时间分配

> cpu requests不仅仅在调度时起作用，他还决定着剩余（未使用）的cpu时间如何在pod之间分配。因为第一个pod请求了200毫核，另外一个请求了1000毫核，所以未使用的cpu将按照1:5的比例来划分给这两个pod。如果两个pod都全力使用cpu，第一个pod将获得16.7%的cpu时间，另一个将获得83.3%的cpu时间。
> 另一方面，如果一个容器能够跑满cpu，而另一个容器在该时段处于空闲状态，那么前者将可以使用这个cpu时间（当然会减掉第二个容器消耗的少量时间）。毕竟没有其他人使用时提高整个cpu的利用率也是有意义的，对吧？当然，第二个容器需要cpu时间的时候就会获取到，同时第一个容器会被限制回来。



### 限制容器的可用资源

> 与资源requests不同的是，资源limits不受节点可分配资源量的约束。所有limits的总和允许超过资源总量的100%。换句话说，资源limits可以超卖。如果节点资源使用量超过100%，一些容器将被杀掉，这是一个很重要的结果。
> 对一进程的CPU使用率可以进行限制，因此当一个容器设置CPU限额时，该进程只会分不到比限额更多的CPU而已。
> 而内存却有所不同。当进程尝试申请分配比限额更多的内存时会被杀掉（我们会说这个容器被OOMKilled了，OOM是Out Of Memory缩写）。如果pod的重启策略为Always或者OnFailure，进程将会立即重启，因此用户可能根本觉察不到他被杀掉。但是如果他继续超限并被杀死，kubernetes会再次尝试重启，并且增加下次重启的间隔时间。

#### 创建一个带有资源limits的pod

```shell
apiVersion: v1
kind: Pod
metadata:
 name: test
spec:
 containers:
 - name: main
   image: busybox
   command: ["dd", "if=/dev/zero", "of=/dev/null"]
   resources:
    # 我们为容器指定资源limits
    # 这个容器允许最大使用1/5核cpu
    # 这个容器允许最大使用20MB内存
    # 因为没有指定资源requests，他将被设置为与资源limits相同的值
    limits:
     cpu: 200m
     memory: 20Mi
    
# 可以看到cpu使用率为10%，因为宿主节点是2核cpu，所以200m=10%cpu使用率
kubectl exec -it test sh
/ # top
```

 

#### 容器中的应用如何看待limits

> 在容器内看到的始终是节点的内存，而不是容器本身的内存。即使你为容器设置了最大可用内存限制，top命令显示的是运行该容器的节点的内存数量，而容器无法感知到此限制。
> 容器内同样可以看到节点所有的cpu核，与内存一样，无论有没有配置CPU limits，容器内也会看到节点所有的CPU。将CPU限额配置为1,并不会神奇地只为容器暴露一个核。CPU limits做的只是限制容器使用CPU时间。
> 一些程序通过查询系统CPU核数来决定启动工作线程的数量。同样在开发环境的笔记本电脑上运行良好，但是部署在拥有更多数量的CPU节点上，程序将快速启动大量线程，所有线程都会争夺（可能极其）有限的CPU时间。同时每个线程通常都需要额外的内存资源，导致应用的内存用量急剧增加。
> 不要依赖应用程序从系统获取CPU数量，你可能需要使用Downward API将CPU限额传递至容器并使用这个值。也可以通过cgroup系统直接获取配置的CPU限制。请查看下面的文件: /sys/fs/cgroup/cpu/cpu.cfs.quota.us、/sys/fs/cgroup/cpu/cpu.cfs_period_us



### 了解pod QoS等级

> 假设有两个pod，pod A使用了节点内存的90%，pod B突然需要比之前更多的内存，这时节点无法提供足量的内存，哪个容器将被杀掉呢？应该是pod B吗？因为节点无法满足他的内存要求。或者应该是pod A吗？这样释放的内存就可以提供给pod B了。



















## pod与集群节点自动伸缩

> todo 根据自定义指标实现自定义HPA

###  配置metrics-server

> https://github.com/kubernetes-sigs/metrics-server
>
> kubernetes +1.19 安装 metrics-server-v0.6.x
>
> 
>
> metrics-server 是用来取代heapster，负责从kubelet中采集数据， 并通过Metrics API在Kubernetes Apiserver中暴露它们。
> metrics-server 采集node 和pod 的cpu/mem，数据存在容器本地，不做持久化。这些数据的使用场景有 kubectl top 和scheduler 调度、hpa 弹性伸缩，以及原生的dashboard 监控数据展示。
> https://misa.gitbook.io/k8s-ocp-yaml/kubernetes-docs/2020-04-14-metrics-server

```shell
# 安装metrics-server-v0.6.2
# 下载 https://github.com/kubernetes-sigs/metrics-server/releases/download/v0.6.2/components.yaml

# metrics-server args添加 - --kubelet-insecure-tls 表示抓取指标数据时不使用https通讯
# metrics-server image修改为 registry.cn-hangzhou.aliyuncs.com/google_containers/metrics-server:v0.6.2
# components.yaml内容如下:
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
  
# 创建metrics-server
kubectl apply -f components.yaml

# 查看kube-system metrics-server是Running状态
kubectl get pod --namespace kube-system -o wide
```



### 基于CPU使用率的HPA自动伸缩

```shell
# 1.yaml内容如下:
apiVersion: v1
kind: Service
metadata:
 name: myservice1
spec:
 selector:
  app: kubia
 type: ClusterIP
 ports:
  - port: 80
    targetPort: 8080

---
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
spec:
 replicas: 3
 selector:
  matchLabels:
   app: kubia
 template:
  metadata:
   name: kubia
   labels:
    app: kubia
  spec:
   containers:
    - name: nodejs
      image: docker.118899.net:10001/yyd-public/demo-k8s-nodejs
      resources:
       requests:
        cpu: 100m

---
# 创建HPA
apiVersion: autoscaling/v1
kind: HorizontalPodAutoscaler
metadata:
 name: kubia
spec:
 maxReplicas: 5
 targetCPUUtilizationPercentage: 30
 minReplicas: 1
 scaleTargetRef:
  apiVersion: apps/v1
  kind: Deployment
  name: deployment1

# 创建pod
kubectl apply -f 1.yaml 

# 刚刚创建HPA时，有3个pod在运行
kubectl get pod -o wide

# 查看HPA状态
kubectl get hpa

# 5分钟后查看HPA，自动把deployment1 replicas修改为1
kubectl describe hpa kubia

# 最后剩余1个pod在运行
kubectl get pod -o wide

# 模拟产生压力，pod自动扩容
kubectl run -it --rm --restart=Never loadgenerator --image=alpine/curl -- sh -c "while true; do curl -s myservice1 > /dev/null; done;"

# 稍过几分钟查看发现自动创建多3个pod响应压力
kubectl get hpa
kubectl get pod -o wide
```





### 基于QPS的HPA自动伸缩

> todo 没有做实验







## 高级调度

### 使用污点和容忍度阻止节点调度到特定节点

> 节点选择器和节点亲缘性规则，是通过明确的在pod中添加的信息，来决定一个pod可以或者不可以被调度到那些节点上。而污点则是在不修改已有的pod信息的前提下，通过在节点上添加污点信息，来拒绝pod在某系节点上的部署
>
> **污点效果**
>
> - NoSchedule 表示如果pod没有容忍这些污点，pod则不能被调度到包含这些污点的污点上。
> - PreferNoSchedule 是NoSchedule的一个宽松的版本，表示尽量阻止pod被调度到这个节点上，但是如果没有其他节点可以调度，pod依然会被调度到这个节点上。
> - NoExecute 不同于NoSchedule以及PreferNoSchedule，后两者只在调度期间起作用，而NoExecute也会影响正在节点上运行着的pod。如果在一个节点上添加了NoExecute污点，那些在该节点上运行着的pod，如果没有容忍这个NoExecute污点，将会从这个节点去除。



#### 主节点的污点和系统级别pod的污点容忍度

```shell
# 默认情况下，集群中的主节点需要设置污点，这样才能保证只有控制面板pod才能够部署在主节点上
# 可以看到主节点被添加污点 node-role.kubernetes.io/master:NoSchedule
kubectl describe node demo-k8s-master

# 因为kube-proxy pod添加了污点容忍度node-role.kubernetes.io/master:NoSchedule
# 所以kube-proxy能够被调度到master节点上
kubectl get pod -n kube-system -o wide | grep kube-proxy
kubectl describe pod kube-proxy-825tq -n kube-system
```



#### NoSchedule污点效果

```shell
# 给节点demo-k8s-node1添加污点node-type=production:NoSchedule
kubectl taint node demo-k8s-node1 node-type=production:NoSchedule

# 查看节点污点信息
kubectl describe node demo-k8s-node1

# 部署没有容忍度的pod
apiVersion: apps/v1
kind: Deployment
metadata:
 name: test
spec:
 replicas: 9
 selector:
  matchLabels:
   app: test
 template:
  metadata:
   labels:
    app: test
  spec:
   containers:
   - name: nginx
     image: nginx
     imagePullPolicy: IfNotPresent
    
    
# 查看pod没有被调度到节点demo-k8s-node1上
kubectl get pod -o wide

# 删除节点污点node-type=production:NoSchedule
kubectl taint node demo-k8s-node1 node-type=production:NoSchedule-
```

#### NoExecute污点效果

```shell
# 用于创建pod 
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
spec:
 replicas: 9
 selector:
  matchLabels:
   app: kubia
 template:
  metadata:
   labels:
    app: kubia
  spec:
   containers:
    - name: kubia
      image: busybox
      imagePullPolicy: IfNotPresent
      command: ["sh", "-c", "sleep 7200;"]

# 创建pod
kubectl apply -f 1.yaml 

# 查看所有pod
kubectl get pod -o wide

# 标记demo-k8s-node1 NoExecute污点
kubectl taint node demo-k8s-node1 node-type=production:NoExecute

# 在节点demo-k8s-node1上的pod被重新调度到别的节点
kubectl get pod -o wide

# 删除污点
kubectl taint node demo-k8s-node1 node-type=production:NoExecute-
```



#### 创建pod时添加污点容忍度

```shell
# 标记demo-k8s-node1 NoSchedule污点
kubectl taint node demo-k8s-node1 node-type=production:NoSchedule

# 创建带容忍度的pod
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
spec:
 replicas: 9
 selector:
  matchLabels:
   app: kubia
 template:
  metadata:
   labels:
    app: kubia
  spec:
   tolerations:
   - key: node-type
     operator: Equal
     value: production
     effect: NoSchedule
   containers:
    - name: kubia
      image: busybox
      imagePullPolicy: IfNotPresent
      command: ["sh", "-c", "sleep 7200;"]

# 查看pod也会被调度到节点demo-k8s-node1
kubectl get pod -o wide

# 删除污点
kubectl taint node demo-k8s-node1 node-type=production:NoSchedule-
```



### 定向调度

> 通过nodename或者nodeselector指定pod运行的节点

#### 使用nodename指定节点

```shell
# 用于创建pod
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment
spec:
 replicas: 5
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
   nodeName: demo-k8s-node1 # 使用节点名称指定pod运行节点
  
# 所有节点被调度到demo-k8s-node1上
kubectl get pod -o wide
```



#### 使用nodeselector指定节点标签

```shell
# 节点打标签
kubectl label node demo-k8s-node1 node-label=node1

# 用于创建pod 
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment
spec:
 replicas: 5
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
   nodeSelector:
    node-label: node1 # 指定节点标签

# 所有pod被schedule到demo-k8s-node1
kubectl get pod -o wide

# 删除节点标签
kubectl label node demo-k8s-node1 node-label-
```



### 使用节点亲缘性将pod调度到特定节点上

#### 节点亲缘性(node affinity)

> 硬限制 requiredDuringSchedulingIgnoredDuringExecution，不匹配不调度，pod一直处于pending状态。
>
> 软限制 preferredDuringSchedulingIgnoredDuringExecution，不匹配也调度。

情景1（无法匹配节点，pod无法调度，一直处于pending状态）

```shell
# 因为没有节点labels包含nodeenv in xxx,yyy，所以deployment无法调度一直pending状态 
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment
spec:
 replicas: 5
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
      imagePullPolicy: IfNotPresent
   affinity:
    nodeAffinity:
     # 硬限制
     requiredDuringSchedulingIgnoredDuringExecution:
      nodeSelectorTerms:
       # 匹配节点labels in xxx,yyy
       - matchExpressions:
          - key: nodeenv
            operator: In
            values:
             - xxx
             - yyy
             
# 不匹配不调度，一直处于pending状态
kubectl get pod -o wide
```

情景2（给demo-k8s-node2节点打上标签，成功匹配，所有pod被调度到此节点）

```shell
# 给节点demo-k8s-node2打上标签成功使用nodeAffinity调度pod
kubectl label node demo-k8s-node2 nodeenv=prod

# 用于创建pod
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment
spec:
 replicas: 5
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
      imagePullPolicy: IfNotPresent
   affinity:
    nodeAffinity:
     requiredDuringSchedulingIgnoredDuringExecution:
      nodeSelectorTerms:
       - matchExpressions:
          - key: nodeenv
            operator: In
            values:
             - prod
             - yyy
             
# 所有pod被成功调度到demo-k8s-node2
kubectl get pod -o wide

# 删除节点标签
kubectl label node demo-k8s-node2 nodeenv-
```

情景3（软限制，即使不匹配节点，最终所有pod被随机调度到各个节点）

```shell
# 用于创建pod
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment
spec:
 replicas: 5
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
      imagePullPolicy: IfNotPresent
   affinity:
    nodeAffinity:
     preferredDuringSchedulingIgnoredDuringExecution:
      # 1-100权重参数，pod 优先调度到权重总和最高的节点上
      - weight: 1
        preference: 
         matchExpressions:
          - key: nodeenv
            operator: In
            values:
             - xxx
             - yyy
# 创建pod
kubectl apply -f 1.yaml

# 即使不匹配，pod也会被调度到各个节点
kubectl get pod -o wide
```

情景4（软限制，有匹配的节点标签，所有pod被调度到此节点）

```shell
# 节点打标签
kubectl label node demo-k8s-node2 nodeenv=prod

# 用于创建pod
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment
spec:
 replicas: 5
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
      imagePullPolicy: IfNotPresent
   affinity:
    nodeAffinity:
     preferredDuringSchedulingIgnoredDuringExecution:
      - weight: 1
        preference: 
         matchExpressions:
          - key: nodeenv
            operator: In
            values:
             - prod
             - yyy
             
# 节点有标签对应，所有pod被调度到相应的节点
kubectl get pod -o wide

# 删除标签
kubectl label node demo-k8s-node2 nodeenv-
```



#### pod亲缘性(pod affinity)

**硬限制不能匹配pod一直pending状态情景**

```shell
# 创建参考点pod，指定参考点pod被调度到demo-k8s-node2上
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
spec:
 replicas: 2
 selector:
  matchLabels:
   podenv: dev
 template:
  metadata:
   labels:
    podenv: dev
  spec:
   containers:
    - name: nginx
      image: nginx
      imagePullPolicy: IfNotPresent
   nodeName: demo-k8s-node2

---
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment2
spec:
 replicas: 5
 selector:
  matchLabels:
   app: kubia
 template:
  metadata:
   labels:
    app: kubia
  spec:
   containers:
    - name: busybox
      image: busybox
      imagePullPolicy: IfNotPresent
   affinity:
    podAffinity:
     requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
         matchExpressions:
          - key: podenv
            operator: In
            values:
             - prod
             - yyy
        topologyKey: kubernetes.io/hostname

# 创建不存在的pod label导致无法匹配pod一直处于pending状态
kubectl get pod -o wide
```

**硬限制能够匹配pod并且成功调度**

```shell
# 创建参考pod
---
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
spec:
 replicas: 2
 selector:
  matchLabels:
   podenv: dev
 template:
  metadata:
   labels:
    podenv: dev
  spec:
   containers:
    - name: nginx
      image: nginx
      imagePullPolicy: IfNotPresent
   nodeName: demo-k8s-node2

---
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment2
spec:
 replicas: 5
 selector:
  matchLabels:
   app: kubia
 template:
  metadata:
   labels:
    app: kubia
  spec:
   containers:
    - name: busybox
      image: busybox
      imagePullPolicy: IfNotPresent
      command: ["sh", "-c", "sleep 7200;"]
   affinity:
    podAffinity:
     requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
         matchExpressions:
          - key: podenv
            operator: In
            values:
             - dev
             - yyy
        topologyKey: kubernetes.io/hostname
        
# pod被成功调度到demo-k8s-node2上
kubectl get pod -o wide
```

**pod非亲缘性(pod anti affinity)**

情景1，硬限制

```shell
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
spec:
 replicas: 2
 selector:
  matchLabels:
   podenv: dev
 template:
  metadata:
   labels:
    podenv: dev
  spec:
   containers:
    - name: nginx
      image: nginx
      imagePullPolicy: IfNotPresent
   nodeName: demo-k8s-node2

---
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment2
spec:
 replicas: 5
 selector:
  matchLabels:
   app: kubia
 template:
  metadata:
   labels:
    app: kubia
  spec:
   containers:
    - name: busybox
      image: busybox
      imagePullPolicy: IfNotPresent
      command: ["sh", "-c", "sleep 7200;"]
   affinity:
    podAntiAffinity:
     requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
         matchExpressions:
          - key: podenv
            operator: In
            values:
             - dev
             - yyy
        topologyKey: kubernetes.io/hostname

# deployment2没有pod被调度到demo-k8s-node2节点上
kubectl get pod -o wide
```

情景2，软限制

```sh
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
spec:
 replicas: 1
 selector:
  matchLabels:
   podenv: dev
 template:
  metadata:
   labels:
    podenv: dev
  spec:
   containers:
    - name: nginx
      image: nginx
      imagePullPolicy: IfNotPresent
   nodeName: demo-k8s-node2

---
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment2
spec:
 replicas: 5
 selector:
  matchLabels:
   app: kubia
 template:
  metadata:
   labels:
    app: kubia
  spec:
   containers:
    - name: busybox
      image: busybox
      imagePullPolicy: IfNotPresent
      command: ["sh", "-c", "sleep 7200;"]
   affinity:
    podAntiAffinity:
     # 表示尽量不和有标签 podenv=dev 的pod在同一个节点上
     preferredDuringSchedulingIgnoredDuringExecution:
      - weight: 1
        podAffinityTerm:
          labelSelector:
            matchExpressions:
              - key: podenv
                operator: In
                values:
                - dev
                - yyy
          topologyKey: kubernetes.io/hostname
          
# 创建 pod
kubectl apply -f 1.yal

# deployment2 和 deployment1 中的 pod 不会在同一个节点上
kubectl get pod -o wide
```



### 平均地调度pod到所有节点中

> https://cloudhero.io/kubernetes-evenly-distribution-of-pods-across-cluster-nodes/

```shell
# 8个pod会被平均地分配到4个节点中
apiVersion: apps/v1
kind: Deployment
metadata:
 name: test
spec:
 replicas: 8
 selector:
  matchLabels:
   app: test
 template:
  metadata:
   labels:
    app: test
  spec:
   # 平均地调度pod到所有节点中
   topologySpreadConstraints:
   - maxSkew: 1
     topologyKey: kubernetes.io/hostname
     whenUnsatisfiable: ScheduleAnyway
     labelSelector:
      matchLabels:
       app: test
   containers:
   - name: test
     image: busybox
     command: ["sleep", "3600"]
     imagePullPolicy: IfNotPresent
```





## 开发应用的最佳实践



### init容器实现pod之间的依赖关系

> 下面演示test pod等待nginx pod启动完毕后才启动。
>
> NOTE: init容器是应用到整个pod。

```shell
---
apiVersion: v1
kind: Pod
metadata:
 name: nginx
 labels:
    app: nginx
spec:
 initContainers:
 - name: init1
   image: busybox
   # 使用多行命令写法
   command: ["sh", "-c"]
   args:
   - date;
     sleep 10;
     date;
   imagePullPolicy: IfNotPresent
 - name: init2
   image: busybox
   command: ["sleep", "10"]
 containers:
 - name: test
   image: nginx
   imagePullPolicy: IfNotPresent

---
apiVersion: v1
kind: Service
metadata:
 name: nginx
spec:
 sessionAffinity: None
 selector:
  app: nginx
 ports:
  - port: 80
    targetPort: 80

---
apiVersion: v1
kind: Pod
metadata:
 name: test
spec:
 initContainers:
 - name: init1
   image: alpine/curl
   # 等待nginx pod启动后才启动本pod
   command: ["sh", "-c"]
   args:
   - target_domain=nginx;
     while ! curl -s -f -o /dev/null --connect-timeout 5 $target_domain;
     do
        echo "`date` - 命令curl -s -f -o /dev/null --connect-timeout 5 $target_domain 执行失败重试";
        sleep 1;
     done
   imagePullPolicy: IfNotPresent
 containers:
 - name: test
   image: busybox
   command: ["sleep", "3600"]
   imagePullPolicy: IfNotPresent
```



### pod的生命周期钩子

> pod的生命周期钩子是基于每个容器来指定的。

#### 启动后（postStart）容器生命周期钩子

> 启动后钩子是在容器的主进程启动之后立即执行，这个钩子和主进程是并行执行的。
> 如果钩子执行失败或者返回了非零状态码，主容器会被杀死。
> 在启动后钩子未执行完毕时，pod一直处于ContainerCreating状态（下面postStart例子sleep 30用于观察次状态）。

```shell
apiVersion: v1
kind: Pod
metadata:
 name: test
spec:
 containers:
 - name: test
   image: busybox
   imagePullPolicy: IfNotPresent
   command: ["sleep", "3600"]
   lifecycle:
    postStart:
     exec:
      command:
      - sh
      - -c
      - |
       date;
       sleep 30;
       date;

# 如果pod因为postStart启动失败，通过describe命令查看原因
kubectl describe pod test
```



#### 停止前（preStop）容器生命周期钩子

> 停止前钩子是在容器被终止之前立即执行的。当一个容器需要终止运行的时候，kubelet在配置了停止前钩子的时候就会执行这个停止前钩子，并且仅在执行完钩子程序后才会向容器进程发送SIGTERM信号。
> 虽然停止pod时被标记为Terminating，但是因为停止前钩子的作用（如下面例子sleep 90，延迟容器接收SIGTERM信号）依旧能在90秒内正常提供服务。

```shell
apiVersion: v1
kind: Pod
metadata:
 name: test
spec:
 # 把默认的30秒终止宽限期修改为300秒
 # 这样容器就不会接收到SIGKILL信号导致被强制终止
 terminationGracePeriodSeconds: 300
 containers:
 - name: test
   image: nginx
   imagePullPolicy: IfNotPresent
   lifecycle:
    preStop:
     exec:
      command:
      - sh
      - -c
      - |
       date > /tmp/1.log;
       sleep 90;
       date >> /tmp/1.log;
```



### 确保所有客户端请求都得到了妥善处理

#### 在pod启动时避免客户端连接断开

> 通过添加就绪探针就能够很好第解决此问题

#### 在pod关闭时避免客户端连接断开

> 通过添加关闭前钩子解决此问题

```shell
apiVersion: v1
kind: Pod
metadata:
 name: test
spec:
 containers:
 - name: test
   image: nginx
   imagePullPolicy: IfNotPresent
   lifecycle:
    preStop:
     exec:
      command:
      - sh
      - -c
      - |
       sleep 20;
```



### 给进程终止提供更多的信息

```shell
apiVersion: v1
kind: Pod
metadata:
 name: test
spec:
 containers:
 - name: test
   image: busybox
   terminationMessagePath: /var/termination-reason
   command:
   - sh    
   - -c
   - 'echo "I''ve has enough" > /var/termination-reason; exit 1'

# 通过describe命令查看pod终止原因（在Last State列中写明了原因）
kubectl describe pod test
```



### 应用开发环境和 kubernetes 连接方便调试

> https://www.getambassador.io/blog/local-kubernetes-development-optimization-guide
> https://kubernetes.io/blog/2023/09/12/local-k8s-development-tools/
>
> Local Kubernetes Development Tools: Telepresence, Gefyra, and mirrord，这里采用最新的工具 mirrord。

#### mirrord

> TODO 没有配置成功

#### telepresence

> NOTE: 安装过程中需要到国外下载文件很慢并且配置步骤复杂暂时不采用。



## kubernetes节点运维

### 手动标记节点为不可调度、排空节点

```shell
# 1.yaml内容如下:
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
spec:
 replicas: 5
 selector:
  matchLabels:
   app: kubia
 template:
  metadata:
   labels:
    app: kubia
  spec:
   containers:
    - name: kubia
      image: busybox
      command: ["sh", "-c", "sleep 7200;"]
      imagePullPolicy: IfNotPresent

# 创建pod
kubectl apply -f 1.yaml 

# 有些pod被调度到k8s-node2
kubectl get pod -o wide

# 标记节点为不可调度（但对其上的pod不做任何事情）
kubectl cordon demo-k8s-node2

# 2.yaml内容如下:
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment2
spec:
 replicas: 5
 selector:
  matchLabels:
   app: kubia
 template:
  metadata:
   labels:
    app: kubia
  spec:
   containers:
    - name: kubia
      image: busybox
      command: ["sh", "-c", "sleep 7200;"]
      imagePullPolicy: IfNotPresent
# 创建deployment2
kubectl apply -f 2.yaml 

# 可以看到后来创建的deployment2的pod不会被调度到k8s-node2中
kubectl get pod -o wide

# 标记节点为不可以调度，随后疏散其上所有pod
kubectl drain demo-k8s-node2 --ignore-daemonsets

# 可以看到没有pod运行在k8s-node2上了
kubectl get pod -o wide

# 取消标记
kubectl uncordon demo-k8s-node2
```



## kubernetes rest API

### 为pod添加event

```shell
# 创建pod
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
   
# 使用kubectl启动proxy
unset HTTPS_PROXY
unset HTTP_PROXY
kubectl proxy

# 查看pod events
kubectl describe pod pod1

# 使用curl调用api添加event到指定的pod
curl -X POST -H "Content-Type: application/json" -d @./data.json http://127.0.0.1:8001/api/v1/namespaces/default/events
# data.json内容如下:
{
	"kind": "Event",
	"apiVersion": "v1",
	"metadata": {
		"name": "pod1.179f0a9f1e4f277a",
		"namespace": "default"
	},
	"involvedObject": {
		"kind": "Pod",
		"namespace": "default",
		"name": "pod1",
		"uid": "9176d70a-8033-4321-a75b-677a3f727b77",
		"apiVersion": "v1",
		"resourceVersion": "9013112"
	},
	"reason": "Reason Test",
	"message": "Message Test",
	"source": {
		"component": "my-source-test"
	},
	"firstTimestamp": "2023-12-09T03:01:02Z",
	"lastTimestamp": "2023-12-09T03:01:02Z",
	"count": 1,
	"type": "Warning",
	"reportingComponent": "my-source-test"
}

# 查看pod events
kubectl describe pod pod1
```



### 通过rest api更新cr资源状态

> 参考官方通过client-go更新cr资源状态
> https://github.com/kubernetes/sample-controller

```shell
# 创建crd
apiVersion: apiextensions.k8s.io/v1
kind: CustomResourceDefinition
metadata:
  name: websites.extensions.example.com
spec:
  # Website资源是属于某个命名空间的
  scope: Namespaced
  # api组
  group: extensions.example.com
  # api版本
  # 在定义Website资源时 apiVersion 应该填写 extensions.example.com/v1
  versions:
    - name: v1
      storage: true
      served: true
      schema:
        openAPIV3Schema:
          type: object
          properties:
            # Website资源的spec支持字段
            spec:
              type: object
              properties:
                gitRepo:
                  type: string
            status:
              type: object
              properties:
                phase:
                  type: string
                myMessage:
                  type: string

      # subresources for the custom resource
      subresources:
        # enables the status subresource
        status: {}

      # https://kubernetes.io/docs/tasks/extend-kubernetes/custom-resources/custom-resource-definitions/
      additionalPrinterColumns:
        - name: Status
          type: string
          description: The status of website
          jsonPath: .status.phase
        - name: Age
          type: date
          jsonPath: .metadata.creationTimestamp
  names:
    kind: Website
    # 单数
    singular: website
    # 复数
    plural: websites
    shortNames:
      - ws
    
# 创建crd
kubectl apply -f 1.yaml

# 创建cr资源
apiVersion: extensions.example.com/v1
kind: Website
metadata:
 name: test
spec:
 gitRepo: https://github.com/luksa/kubia-website-example.git

# 查看Website对象实例列表
kubectl get website

# 通过rest api更新cr资源状态
curl -X PUT -H "Content-Type: application/json" -d @./data.json http://127.0.0.1:8001/apis/extensions.example.com/v1/namespaces/default/websites/test/status
# data.json内容:
{
	"apiVersion": "extensions.example.com/v1",
	"kind": "Website",
	"status": {
	  "phase": "phase-testing",
	  "myMessage": "my message"
	},
	"metadata": {
	   "name": "test",
	   "resourceVersion": "9084804"
	},
	"spec": {
		"gitRepo": "https://github.com/luksa/kubia-website-example.git"
	}
}

# 查看website状态
kubectl get website
kubectl describe website test

# 使用rest api获取website数据
curl -X GET http://127.0.0.1:8001/apis/extensions.example.com/v1/namespaces/default/websites/test/status
```



## kubernetes客户端



### client-go



#### 使用client-go dynamic client操作内建资源

> 参考 demo-client-go/demo-dynamic-client-builtin-resource_test.go



#### 使用client-go dynamic client操作cr资源

> 参考 demo-client-go/demo-dynamic-client-crd-resource_test.go



#### discovery client用法

> 参考 demo-client-go/demo-discovery-client_test.go



### controller-runtime

#### 使用controller-runtime操作内建资源

> 参考 demo-client-go/demo-controller-runtime-builtin-resource_test.go

#### 使用controller-runtime操作cr资源

> 参考 demo-client-go/demo-controller-runtime-crd-resource_test.go



## kubernetes应用扩展



### CustomResourceDefinitions CRD介绍

> https://kubernetes.io/docs/tasks/extend-kubernetes/custom-resources/custom-resource-definitions/

```shell
### 用于创建CRD对象
apiVersion: apiextensions.k8s.io/v1
kind: CustomResourceDefinition
metadata:
  name: websites.extensions.example.com
spec:
  # Website资源是属于某个命名空间的
  scope: Namespaced
  # api组
  group: extensions.example.com
  # api版本
  # 在定义Website资源时 apiVersion 应该填写 extensions.example.com/v1
  versions:
    - name: v1
      storage: true
      served: true
      schema:
        openAPIV3Schema:
          type: object
          properties:
            # Website资源的spec支持字段
            spec:
              type: object
              properties:
                gitRepo:
                  type: string
            status:
              type: object
              properties:
                phase:
                  type: string
                myMessage:
                  type: string

      # subresources for the custom resource
      subresources:
        # enables the status subresource
        status: {}

      # https://kubernetes.io/docs/tasks/extend-kubernetes/custom-resources/custom-resource-definitions/
      additionalPrinterColumns:
        - name: Status
          type: string
          description: The status of website
          jsonPath: .status.phase
        - name: Age
          type: date
          jsonPath: .metadata.creationTimestamp
  names:
    kind: Website
    # 单数
    singular: website
    # 复数
    plural: websites
    shortNames:
      - ws

# 创建CRD
kubectl apply -f 1.yaml



### 创建和删除CRD对象实例
apiVersion: extensions.example.com/v1
kind: Website
metadata:
 name: test
spec:
 gitRepo: https://github.com/luksa/kubia-website-example.git

# 查看Website对象实例列表
kubectl get website

# 查看website对象实例详细信息
kubectl get website test -o yaml

# 删除CRD对象实例
kubectl delete website test
```



### 自定义控制器（网站控制器）

> 参考 website-controller

```shell
# 启动kubernetes API proxy，NOTE: 清除HTTPS_PROXY和HTTP_PROXY环境变量unset HTTPS_PROXY、unset HTTP_PROXY
kubectl proxy

# 在website-controller项目的根目录中执行如下命令启动website-controller
go run pkg/website-controller.go

# 用于创建website对象实例
apiVersion: extensions.example.com/v1
kind: Website
metadata:
 name: test
spec:
 gitRepo: https://github.com/luksa/kubia-website-example.git

# 创建website
kubectl apply -f 1.yaml

# 打开浏览器访问kubernetes任意一个节点测试website是否部署成功
http://192.168.1.1.188:30000
```





## helm



### 为何helm

> **传统服务部署到k8s流程**
> 拉取代码 > 打包编译 > 构建镜像 > 准备部署相关的yaml(例如: deployment、service、ingress) >  kubectl apply 部署到k8s
>
> 传统方式不能根据一套yaml文件来创建多个环境，需要手动进行修改。例如：一般环境都分为dev、预生产、生产环境，部署完了dev这套环境，后面再部署预生产和生产环境，还需要复制出两套，并手动修改才行。使用一套helm应用包部署多个环境。



### helm组件

> chart包： 就是helm的一个整合后的chart包，包含一个应用的所有kubernetes声明模板，类似于yum的rpm包或者apt的dpkg文件。
> helm客户端： helm的客户端组件，负责和k8s apiserver通信
> helm仓库： 用于发布和存储chart包的仓库，类似yum仓库或者docker仓库
> release： 用chart包部署的一个实例。通过chart在k8s中部署的应用会产生一个唯一的release，同一个chart部署多次就会生产多个release。



### 安装

> 使用dcli安装helm cli



### 搭建 helm 私有仓库

> helm 仓库 https://helm.sh/docs/topics/chart_repository/
>
> 使用 chartmuseum 搭建私有仓库
> https://github.com/helm/chartmuseum
> https://www.tinfoilcipher.co.uk/2021/04/26/creating-a-private-helm-repo-with-chartmuseum-using-aws-s3/
>
> NOTE: 使用 harbor 搭建(在helm push时候报告错误)，暂时不使用此方案

使用 docker 运行 chartmuseum

```sh
docker run \
  -d \
  -p 8080:8080 \
  -e DEBUG=1 \
  -e STORAGE=local \
  -e STORAGE_LOCAL_ROOTDIR=/charts \
  -e BASIC_AUTH_USER=root \
  -e BASIC_AUTH_PASS=123456 \
  -v ~/data-demo-helm-charts:/charts \
  ghcr.io/helm/chartmuseum:v0.16.1
```

授权 ~/data-demo-helm-charts 目录写权限

```sh
sudo chmod -R a+w ~/data-demo-helm-charts/
```

添加 chartmuseum 仓库

```sh
helm repo add --username root --password 123456 chartmuseum http://localhost:8080
```

搜索 charts 用于测试 chartmuseum 仓库是否添加成功

```sh
helm search repo chartmuseum/
```

创建 charts 项目测试 chartmuseum

```sh
mkdir temp-chartmuseum-testing
cd temp-chartmuseum-testing
helm create .
rm -rf templates/*
```

修改 Chart.yaml 中 name 为 mychart，修改后的 Chart.yaml 内容如下：

```yaml
apiVersion: v2
name: mychart
description: A Helm chart for Kubernetes

# A chart can be either an 'application' or a 'library' chart.
#
# Application charts are a collection of templates that can be packaged into versioned archives
# to be deployed.
#
# Library charts provide useful utilities or functions for the chart developer. They're included as
# a dependency of application charts to inject those utilities and functions into the rendering
# pipeline. Library charts do not define any templates and therefore cannot be deployed.
type: application

# This is the chart version. This version number should be incremented each time you make changes
# to the chart and its templates, including the app version.
# Versions are expected to follow Semantic Versioning (https://semver.org/)
version: 0.1.0

# This is the version number of the application being deployed. This version number should be
# incremented each time you make changes to the application. Versions are not expected to
# follow Semantic Versioning. They should reflect the version the application is using.
# It is recommended to use it with quotes.
appVersion: "1.16.0"
```

新增 templates/1.yaml 内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap
data:
 k1: v1
```

安装推送插件

```sh
helm plugin install https://github.com/chartmuseum/helm-push
```

推送 charts 项目到 chartmuseum 仓库

```sh
helm cm-push . chartmuseum
```

从 chartmuseum 安装 charts

```sh
helm install chartmuseum/mychart --generate-name
```

卸载 mychart

```sh
helm uninstall mychart-1704695350
```



### 使用helm创建一个chart

**没有变量的helm**

```shell
# 创建mychart项目
helm create mychart

# 删除mychart/templates文件夹下所有文件
rm -rf templates/*

# 在mychart/templates目录下创建configmap.yaml，内容如下:
apiVersion: v1
kind: ConfigMap
metadata:
 name: mychart-configmap1
data:
 myvalue: "hello world!"
 
 # 创建helm release
helm install myconfigmap1 .

# 查看helm release
helm list

# 查看configmap
kubectl get configmap

# 查看helm release详细信息
helm get manifest myconfigmap1

# 删除helm release
helm uninstall myconfigmap1

helm list
kubectl get configmap
```



### helm NOTES.txt 用于输出操作说明

创建 mychart 项目

```sh
helm create mychart
```

删除mychart/templates文件夹下所有文件

```sh
rm -rf templates/*
```

创建 templates/configmap.yaml 内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: mychart-configmap1
data:
 myvalue: "hello world!"
```

创建 templates/NOTES.txt (NOTES.txt 中可以编写模板语法)内容如下：

```markdown
CHART NAME: {{ .Chart.Name }}
CHART VERSION: {{ .Chart.Version }}
APP VERSION: {{ .Chart.AppVersion }}

** Please be patient while the chart is being deployed **

Redis&reg; can be accessed on the following DNS names from within your cluster:

    myredis-master.default.svc.cluster.local for read/write operations (port 6379)
    myredis-replicas.default.svc.cluster.local for read-only operations (port 6379)

To get your password run:

    export REDIS_PASSWORD=$(kubectl get secret --namespace default myredis -o jsonpath="{.data.redis-password}" | base64 -d)
```

创建helm release

```sh
helm install myconfigmap1 .
```

删除helm release

```sh
helm uninstall myconfigmap1
```



**带变量的helm**

```shell
# 把上面 没有变量的helm 修改 templates/configmap.yaml和values.yaml
# templates/configmap.yaml内容如下:
apiVersion: v1
kind: ConfigMap
metadata:
 name: {{ .Release.Name }}-configmap1
data:
 myvalue: {{ .Values.MY_VALUE }}
 
# values.yaml内容如下: 
MY_VALUE: "hello world!!"

# 安装
helm install myconfigmap2 .

# 查询安装列表
helm list

# 查询configmap列表
kubectl get configmap

# 查看helm安装详情
helm get manifest myconfigmap2

# 卸载
helm uninstall myconfigmap2

helm list
```



### debug和dry-run

> 不实际执行helm，只是调试输出helm模板的执行过程

```shell
[root@k8s-master mychart]# helm install myconfigmap2 . --debug --dry-run
install.go:192: [debug] Original chart version: ""
install.go:209: [debug] CHART PATH: /root/mychart

NAME: myconfigmap2
LAST DEPLOYED: Fri Dec 16 16:49:49 2022
NAMESPACE: default
STATUS: pending-install
REVISION: 1
TEST SUITE: None
USER-SUPPLIED VALUES:
{}

COMPUTED VALUES:
MY_VALUE: hello world!!

HOOKS:
MANIFEST:
---
# Source: mychart/templates/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap2-configmap1
data:
 myvalue: hello world!!
```



### {{ 和 {{- 、}} 和 -}} 的区别

> {{ 会保留左边空格，{{- 相当于 left trim，}} 会保留右边空格，-}} 相当于 right trim。
> https://stackoverflow.com/questions/69992198/what-is-different-between-and-syntax-in-helm3

在当前目录创建 helm 项目

```
helm create .
```

删除 templates 中的文件

```
rm -rf templates/*
```

创建 templates/1.yaml 内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap
data:
 k1: {{"a"}} {{- "b"}}{{"c" -}} {{"d"}} {{- "e" -}} {{"f"}} {{"g"}}
```

调试

```sh
helm install demo1 . --debug --dry-run
```



### 变量

#### 调试内置变量

```shell
[root@k8s-master mychart]# cat values.yaml 
MY_VALUE: "hello world!!"

[root@k8s-master mychart]# cat templates/configmap.yaml 
apiVersion: v1
kind: ConfigMap
metadata:
 name: {{ .Release.Name }}-configmap1
 namespace: {{ .Release.Namespace }}
data:
 isupgrade: {{ .Release.IsUpgrade }}
 isinstall: {{ .Release.IsInstall }}
 revision: {{ .Release.Revision }}
 service: {{ .Release.Service }}
 myvalue: {{ .Values.MY_VALUE }}

[root@k8s-master mychart]# helm install myconfigmap1 . --debug --dry-run
install.go:192: [debug] Original chart version: ""
install.go:209: [debug] CHART PATH: /root/mychart

NAME: myconfigmap1
LAST DEPLOYED: Fri Dec 16 17:09:37 2022
NAMESPACE: default
STATUS: pending-install
REVISION: 1
TEST SUITE: None
USER-SUPPLIED VALUES:
{}

COMPUTED VALUES:
MY_VALUE: hello world!!

HOOKS:
MANIFEST:
---
# Source: mychart/templates/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap1-configmap1
 namespace: default
data:
 isupgrade: false
 isinstall: true
 revision: 1
 service: Helm
 myvalue: hello world!!
```

#### $ 符号

> 参考
> https://helm.sh/docs/chart_template_guide/variables/

##### 定义局部变量

在当前目录创建 helm 项目

```
helm create .
```

删除 templates 中的文件

```
rm -rf templates/*
```

创建 templates/1.yaml 内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: {{ .Release.Name }}-configmap
data:
  myvalue: "Hello World"
  {{- $relname := .Release.Name -}}
  {{- with .Values.favorite }}
  drink: {{ .drink | default "tea" | quote }}
  food: {{ .food | upper | quote }}
  release: {{ $relname }}
  {{- end }}
```

values.yaml 内容如下：

```yaml
favorite:
  drink: "coffee"
  food: "PIZZA"
```

调试

```sh
helm install demo1 . --debug --dry-run
```



##### 在 range 中特殊用法

> 在 range 作用域中想引用全局变量 .Release.Name 时，因为 .Release.Name 中的 .(点号) 此时指代的是当前 item 导致不能成功引用全局变量 .Release.Name ，为了避免冲突使用 $ 代替 .(点号) 解决此问题。

在当前目录创建 helm 项目

```
helm create .
```

删除 templates 中的文件

```
rm -rf templates/*
```

创建 templates/1.yaml 内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: {{ .Release.Name }}-configmap
data:
  {{- range .Values.items }}
  {{ .myK }}: {{ .myV }}
  releaseName: {{ $.Release.Name }}
  {{- end }}
```

values.yaml 内容如下：

```yaml
items:
- myK: "k1"
  myV: "v1"
- myK: "k2"
  myV: "v2"
```

调试

```sh
helm install demo1 . --debug --dry-run
```



### helm相关命令



#### helm version查看helm版本

```shell
helm version
```



#### helm env 查看helm命令使用的环境变量列表

```shell
helm env
```



#### helm repo 仓库管理

```shell
# 添加微软helm仓库
helm repo add stable  http://mirror.azure.cn/kubernetes/charts/

# 添加 basic auth 的私有仓库
helm repo add chartmuseum --username root --password 123456 http://localhost:8080

# 列出helm仓库
helm repo list

# 把远程仓库 index.yaml 更新到本地
# https://stackoverflow.com/questions/55973901/how-can-i-list-all-available-charts-under-a-helm-repo
helm repo update

# 删除仓库
helm repo list
NAME   	URL                                      
stable 	http://mirror.azure.cn/kubernetes/charts/
stable2	http://mirror.azure.cn/kubernetes/charts/
# stable2是helm仓库名称
helm repo remove stable2

# 在远程helm仓库中搜索tomcat包
helm search repo tomcat
```



#### helm chart包管理

```shell
# 创建chart包
helm create mychart-test

# 显示stable/tomcat包信息
helm show chart stable/tomcat

# 显示stable/tomcat values信息
helm show values stable/tomcat

# 拉取helm chart包，--untar解压
# 拉取 helm tgz 包，例如： tomcat-0.4.3.tgz
helm pull stable/tomcat --version 0.4.3
# 拉取 helm tgz 包并在当前目录解压
helm pull stable/tomcat --version 0.4.3 --untar
```



#### helm package 归档chart包

> https://helm.sh/docs/helm/helm_package/

```shell
# 在 mychart 目录内时打包 chart 资源，在当前目录生成 mychart-0.1.0.tgz
helm package .

# 不在 mychart 目录内时打包 chart 资源，在当前目录生产 mychart-0.1.0.tgz
helm package mychart
```



#### helm search

> https://stackoverflow.com/questions/55973901/how-can-i-list-all-available-charts-under-a-helm-repo

先同步远程 helm 仓库中的 index.yaml 到本地缓存中

```sh
helm repo update
```

查看所有 helm 仓库中的所有 charts

```sh
helm search repo
```

查看名为 chartmuseum 的 helm 仓库中所有 charts，只显示最新版本

```sh
helm search repo chartmuseum/
```

查看 chartmuseum 的 helm 仓库中名为 mychart 的 chart ，并显示所有版本

```sh
helm search repo -l chartmuseum/mychart 
```



#### helm template

> https://helm.sh/docs/helm/helm_template/

显示所有模板

```sh
helm template .
```

显示指定模板 templates/1.yaml

```sh
helm template -s templates/1.yaml .
```



### 内置函数

#### quote和squote函数

> quote: 变量值添加双引号
>
> squote: 变量值添加单引号

```shell
[root@k8s-master mychart]# cat templates/configmap.yaml 
apiVersion: v1
kind: ConfigMap
metadata:
 name: {{ .Release.Name }}-configmap1
 namespace: {{ .Release.Namespace }}
data:
 myvalue: {{ .Values.name | quote }}
 myvalue1: {{ .Values.name | squote }}

[root@k8s-master mychart]# cat values.yaml 
name: "hello world!!"

[root@k8s-master mychart]# helm install myconfigmap1 . --debug --dry-run
install.go:192: [debug] Original chart version: ""
install.go:209: [debug] CHART PATH: /root/mychart

NAME: myconfigmap1
LAST DEPLOYED: Mon Dec 19 10:09:07 2022
NAMESPACE: default
STATUS: pending-install
REVISION: 1
TEST SUITE: None
USER-SUPPLIED VALUES:
{}

COMPUTED VALUES:
name: hello world!!

HOOKS:
MANIFEST:
---
# Source: mychart/templates/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap1-configmap1
 namespace: default
data:
 myvalue: "hello world!!"
 myvalue1: 'hello world!!'
```

#### 使用多个函数多次处理同一个变量

```shell
[root@k8s-master mychart]# cat templates/configmap.yaml 
apiVersion: v1
kind: ConfigMap
metadata:
 name: {{ .Release.Name }}-configmap1
 namespace: {{ .Release.Namespace }}
data:
 myvalue: {{ .Values.name | lower | quote }}
 myvalue1: {{ .Values.name | upper | squote }}
 
[root@k8s-master mychart]# cat values.yaml 
name: "hello world!!"

[root@k8s-master mychart]# helm install myconfigmap1 . --debug --dry-run
install.go:192: [debug] Original chart version: ""
install.go:209: [debug] CHART PATH: /root/mychart

NAME: myconfigmap1
LAST DEPLOYED: Mon Dec 19 10:16:48 2022
NAMESPACE: default
STATUS: pending-install
REVISION: 1
TEST SUITE: None
USER-SUPPLIED VALUES:
{}

COMPUTED VALUES:
name: hello world!!

HOOKS:
MANIFEST:
---
# Source: mychart/templates/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap1-configmap1
 namespace: default
data:
 myvalue: "hello world!!"
 myvalue1: 'HELLO WORLD!!'
```

#### lower和upper函数

> Note：参考上面例子

#### repeat函数

```shell
[root@k8s-master mychart]# cat templates/configmap.yaml 
apiVersion: v1
kind: ConfigMap
metadata:
 name: {{ .Release.Name }}-configmap1
 namespace: {{ .Release.Namespace }}
data:
 myvalue: {{ .Values.name | repeat 3 | quote }}
 
[root@k8s-master mychart]# cat values.yaml 
name: "hello world!!"

[root@k8s-master mychart]# helm install myconfigmap1 . --debug --dry-run
install.go:192: [debug] Original chart version: ""
install.go:209: [debug] CHART PATH: /root/mychart

NAME: myconfigmap1
LAST DEPLOYED: Mon Dec 19 10:23:02 2022
NAMESPACE: default
STATUS: pending-install
REVISION: 1
TEST SUITE: None
USER-SUPPLIED VALUES:
{}

COMPUTED VALUES:
name: hello world!!

HOOKS:
MANIFEST:
---
# Source: mychart/templates/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap1-configmap1
 namespace: default
data:
 myvalue: "hello world!!hello world!!hello world!!"
```

#### default函数

```shell
[root@k8s-master mychart]# helm install myconfigmap1 . --debug --dry-run
install.go:192: [debug] Original chart version: ""
install.go:209: [debug] CHART PATH: /root/mychart

NAME: myconfigmap1
LAST DEPLOYED: Mon Dec 19 10:25:37 2022
NAMESPACE: default
STATUS: pending-install
REVISION: 1
TEST SUITE: None
USER-SUPPLIED VALUES:
{}

COMPUTED VALUES:
name: hello world!!

HOOKS:
MANIFEST:
---
# Source: mychart/templates/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap1-configmap1
 namespace: default
data:
 myvalue: "China"

[root@k8s-master mychart]# cat templates/configmap.yaml 
apiVersion: v1
kind: ConfigMap
metadata:
 name: {{ .Release.Name }}-configmap1
 namespace: {{ .Release.Namespace }}
data:
 myvalue: {{ .Values.name1 | default "China" | quote }}
```

#### lookup函数

> 用于获取k8s集群信息
>
> NOTE： lookup函数不能使用dry-run参数install，否则无法获取k8s集群信息
>
> 调用语法：lookup "apiVersion" "kind" "namespace" "name"(namespace和name参数是可选的，可以提供空字符串""，namespace提供空字符串表示所有命名空间)

```shell
[root@k8s-master mychart]# cat templates/configmap.yaml 
apiVersion: v1
kind: ConfigMap
metadata:
 name: {{ .Release.Name }}-configmap1
 namespace: {{ .Release.Namespace }}
data:
 myvalue: {{ lookup "v1" "Namespace" "" "" | quote }}

[root@k8s-master mychart]# helm install myconfigmap1 .
NAME: myconfigmap1
LAST DEPLOYED: Mon Dec 19 11:02:50 2022
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None
[root@k8s-master mychart]# helm get manifest myconfigmap1
---
# Source: mychart/templates/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap1-configmap1
 namespace: default
data:
 myvalue: "map[apiVersion:v1 items:[map[apiVersion:v1 kind:Namespace metadata:map[creationTimestamp:2022-12-05T06:24:25Z managedFields:[map[apiVersion:v1 fieldsType:FieldsV1 fieldsV1:map[f:status:map[f:phase:map[]]] manager:kube-apiserver operation:Update time:2022-12-05T06:24:25Z]] name:default resourceVersion:197 uid:cba1aa4d-eda8-413a-8627-ce6fa453ad7a] spec:map[finalizers:[kubernetes]] status:map[phase:Active]] map[apiVersion:v1 kind:Namespace metadata:map[creationTimestamp:2022-12-05T09:51:37Z managedFields:[map[apiVersion:v1 fieldsType:FieldsV1 fieldsV1:map[f:status:map[f:phase:map[]]] manager:kubectl-create operation:Update time:2022-12-05T09:51:37Z]] name:dev resourceVersion:16752 uid:ae4abfaa-d40e-4220-ac98-0081cbfae910] spec:map[finalizers:[kubernetes]] status:map[phase:Active]] map[apiVersion:v1 kind:Namespace metadata:map[annotations:map[kubectl.kubernetes.io/last-applied-configuration:{\"apiVersion\":\"v1\",\"kind\":\"Namespace\",\"metadata\":{\"annotations\":{},\"labels\":{\"pod-security.kubernetes.io/enforce\":\"privileged\"},\"name\":\"kube-flannel\"}}\n] creationTimestamp:2022-12-05T06:25:05Z labels:map[pod-security.kubernetes.io/enforce:privileged] managedFields:[map[apiVersion:v1 fieldsType:FieldsV1 fieldsV1:map[f:metadata:map[f:annotations:map[.:map[] f:kubectl.kubernetes.io/last-applied-configuration:map[]] f:labels:map[.:map[] f:pod-security.kubernetes.io/enforce:map[]]] f:status:map[f:phase:map[]]] manager:kubectl-client-side-apply operation:Update time:2022-12-05T06:25:05Z]] name:kube-flannel resourceVersion:471 uid:6651feb6-1c3f-4902-aa81-5d4318b545bf] spec:map[finalizers:[kubernetes]] status:map[phase:Active]] map[apiVersion:v1 kind:Namespace metadata:map[creationTimestamp:2022-12-05T06:24:24Z managedFields:[map[apiVersion:v1 fieldsType:FieldsV1 fieldsV1:map[f:status:map[f:phase:map[]]] manager:kube-apiserver operation:Update time:2022-12-05T06:24:24Z]] name:kube-node-lease resourceVersion:64 uid:994537f8-d842-44ea-bc2e-c660e38e1f55] spec:map[finalizers:[kubernetes]] status:map[phase:Active]] map[apiVersion:v1 kind:Namespace metadata:map[creationTimestamp:2022-12-05T06:24:24Z managedFields:[map[apiVersion:v1 fieldsType:FieldsV1 fieldsV1:map[f:status:map[f:phase:map[]]] manager:kube-apiserver operation:Update time:2022-12-05T06:24:24Z]] name:kube-public resourceVersion:43 uid:b568d7ad-ed30-4e5d-ab54-5a16219a9414] spec:map[finalizers:[kubernetes]] status:map[phase:Active]] map[apiVersion:v1 kind:Namespace metadata:map[creationTimestamp:2022-12-05T06:24:24Z managedFields:[map[apiVersion:v1 fieldsType:FieldsV1 fieldsV1:map[f:status:map[f:phase:map[]]] manager:kube-apiserver operation:Update time:2022-12-05T06:24:24Z]] name:kube-system resourceVersion:16 uid:c0bcf78f-273d-4833-94d5-57cb1d758e11] spec:map[finalizers:[kubernetes]] status:map[phase:Active]]] kind:NamespaceList metadata:map[resourceVersion:1877656]]"

[root@k8s-master mychart]# helm uninstall myconfigmap1
release "myconfigmap1" uninstalled
```

#### 逻辑和流程控制函数

todo

#### 字符串函数

todo

#### 类型转换函数

todo

#### 正则表达式函数

todo

#### 字典函数

##### get、set、unset函数

```shell
[root@k8s-master mychart]# cat templates/configmap.yaml 
apiVersion: v1
kind: ConfigMap
metadata:
 name: {{ .Release.Name }}-configmap1
 namespace: {{ .Release.Namespace }}
data:
 {{- $myDict := dict "key1" "value1" "key2" "value2" }}
 data1: {{ $myDict }}
 data2: {{ get $myDict "key2" }}
 data3: {{ set $myDict "key3" "value3" }}
 data4: {{ unset $myDict "key2" }}

[root@k8s-master mychart]# helm install myconfigmap1 . --debug --dry-run
install.go:192: [debug] Original chart version: ""
install.go:209: [debug] CHART PATH: /root/mychart

NAME: myconfigmap1
LAST DEPLOYED: Mon Dec 19 13:24:51 2022
NAMESPACE: default
STATUS: pending-install
REVISION: 1
TEST SUITE: None
USER-SUPPLIED VALUES:
{}

COMPUTED VALUES:
address:
- beijing
- shanghai
- guangzhou

HOOKS:
MANIFEST:
---
# Source: mychart/templates/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap1-configmap1
 namespace: default
data:
 data1: map[key1:value1 key2:value2]
 data2: value2
 data3: map[key1:value1 key2:value2 key3:value3]
 data4: map[key1:value1 key3:value3]
```

##### keys函数

```shell
[root@k8s-master mychart]# cat templates/configmap.yaml 
apiVersion: v1
kind: ConfigMap
metadata:
 name: {{ .Release.Name }}-configmap1
 namespace: {{ .Release.Namespace }}
data:
 {{- $myDict := dict "key1" "value1" "key2" "value2" }}
 {{- $myDict1 := dict "key1" "value1" "key3" "value3" }}
 {{- $myDict2 := dict "key1" "value1" "key4" "value4" }}
 data1: {{ keys $myDict $myDict1 $myDict2 | sortAlpha | uniq | quote }}

[root@k8s-master mychart]# helm install myconfigmap1 . --debug --dry-run
install.go:192: [debug] Original chart version: ""
install.go:209: [debug] CHART PATH: /root/mychart

NAME: myconfigmap1
LAST DEPLOYED: Mon Dec 19 13:29:14 2022
NAMESPACE: default
STATUS: pending-install
REVISION: 1
TEST SUITE: None
USER-SUPPLIED VALUES:
{}

COMPUTED VALUES:
address:
- beijing
- shanghai
- guangzhou

HOOKS:
MANIFEST:
---
# Source: mychart/templates/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap1-configmap1
 namespace: default
data:
 data1: "[key1 key2 key3 key4]"
```

#### 列表函数

todo

#### tpl函数

> 允许开发人员在模板中传递模板字符串，例如：开发人员可以在外部的配置文件中使用helm模板语法编写配置后，使用tpl函数引用这个配置文件内容，里面的helm模板语法被动态解析。
>
> [链接1](https://helm.sh/docs/howto/charts_tips_and_tricks/#using-the-tpl-function)

**例子1**

```shell
[root@k8s-master mychart]# cat templates/configmap.yaml 
apiVersion: v1
kind: ConfigMap
metadata:
 name: {{ .Release.Name }}-configmap1
data:
 1.conf: |
  data: {{ tpl .Values.template . | quote }}
[root@k8s-master mychart]# cat values.yaml 
template: "{{ .Values.name }}"
name: "Tom"
[root@k8s-master mychart]# helm install myconfigmap1 . --dry-run --debug
install.go:192: [debug] Original chart version: ""
install.go:209: [debug] CHART PATH: /root/mychart

NAME: myconfigmap1
LAST DEPLOYED: Wed Dec 21 10:25:42 2022
NAMESPACE: default
STATUS: pending-install
REVISION: 1
TEST SUITE: None
USER-SUPPLIED VALUES:
{}

COMPUTED VALUES:
name: Tom
template: '{{ .Values.name }}'

HOOKS:
MANIFEST:
---
# Source: mychart/templates/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap1-configmap1
data:
 1.conf: |
  data: "Tom"
```

**渲染外部配置文件**

```shell
[root@k8s-master mychart]# cat app.conf 
firstName={{ .Values.firstName }}
lastName={{ .Values.lastName }}

[root@k8s-master mychart]# cat templates/configmap.yaml 
apiVersion: v1
kind: ConfigMap
metadata:
 name: {{ .Release.Name }}-configmap1
data:
 1.conf: |
{{ tpl (.Files.Get "app.conf") . | indent 2 }}

[root@k8s-master mychart]# cat values.yaml 
firstName: Peter
lastName: Parker
```

#### toYaml函数

> 把对象转换为yaml格式

```shell
[root@k8s-master mychart]# cat templates/configmap.yaml 
apiVersion: v1
kind: ConfigMap
metadata:
 name: {{ .Release.Name }}-configmap1
data:
 1.conf: | {{ .Values.redis | toYaml | nindent 2 }}
 
[root@k8s-master mychart]# cat values.yaml 
redis:
  updateStrategy:
    type: RollingUpdate
    rollingUpdate:
      partition: 0
      
[root@k8s-master mychart]# helm install myconfigmap1 . --dry-run --debug
install.go:192: [debug] Original chart version: ""
install.go:209: [debug] CHART PATH: /root/mychart

NAME: myconfigmap1
LAST DEPLOYED: Wed Dec 21 13:46:41 2022
NAMESPACE: default
STATUS: pending-install
REVISION: 1
TEST SUITE: None
USER-SUPPLIED VALUES:
{}

COMPUTED VALUES:
redis:
  updateStrategy:
    rollingUpdate:
      partition: 0
    type: RollingUpdate

HOOKS:
MANIFEST:
---
# Source: mychart/templates/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap1-configmap1
data:
 1.conf: | 
  updateStrategy:
    rollingUpdate:
      partition: 0
    type: RollingUpdate
```

#### indent和nindent函数

> indent和nindent函数区别是：nindent函数是在indent之前添加一个新换行。



#### trimSuffix 函数

> 删除后缀匹配的字符串

在当前目录创建 helm 项目

```
helm create .
```

删除 templates 中的文件

```
rm -rf templates/*
```

创建 templates/1.yaml 内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap
data:
 k1: {{ "abc-cba-" | trimSuffix "-" }}
```

调试

```sh
helm install demo1 . --debug --dry-run
```



#### trunc 函数

> 截断字符串

在当前目录创建 helm 项目

```
helm create .
```

删除 templates 中的文件

```
rm -rf templates/*
```

创建 templates/1.yaml 内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap
data:
 k1: {{ "abc-cba-" | trunc 3 }}
```

调试

```sh
helm install demo1 . --debug --dry-run
```



#### printf 函数

> 格式化字符串函数

在当前目录创建 helm 项目

```
helm create .
```

删除 templates 中的文件

```
rm -rf templates/*
```

创建 templates/1.yaml 内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap
data:
 k1: {{ printf "%s! Are you %d years old?" "Hello dexter" 23 | quote }}
```

调试

```sh
helm install demo1 . --debug --dry-run
```



#### 字典类型和字典函数(dict、dictionary)

> https://helm.sh/docs/chart_template_guide/function_list/#dictionaries-and-dict-functions

在当前目录创建 helm 项目

```
helm create .
```

删除 templates 中的文件

```
rm -rf templates/*
```

创建 templates/1.yaml 内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap
data:
 {{/*定义字典变量*/}}
 {{- $myDict := dict "name1" "value1" "name2" "value2" "name3" "value 3" -}}
 {{/*修改字典变量，NOTE:set 返回字典（Go 模板函数的要求），因此您可能需要像上面那样使用 $_ 赋值来捕获值。*/}}
 {{- $_ := set $myDict "name4" "value4" -}}
 {{/*删除key=name3的键值*/}}
 {{- $_ := unset $myDict "name3" -}}
 hasKey3: {{ hasKey $myDict "name3" }}
 name4: {{ get $myDict "name4" }}
```

调试

```sh
helm install demo1 . --debug --dry-run
```



#### 逻辑和流程控制功能函数(and、or、eq、gt等)

> https://stackoverflow.com/questions/49789867/can-we-use-or-operator-in-helm-yaml-files

##### and 操作符

在当前目录创建 helm 项目

```
helm create .
```

删除 templates 中的文件

```
rm -rf templates/*
```

创建 templates/1.yaml 内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap
data:
 {{- if and .Values.b1 .Values.b2 }}
 k1: v1
 {{- end }}
```

values.yaml 内容如下：

```yaml
b1: true
b2: true
```

调试

```sh
helm install demo1 . --debug --dry-run
```







### 程序流程控制语句

#### ifelse语句

在当前目录创建 helm 项目

```
helm create .
```

删除 templates 中的文件

```
rm -rf templates/*
```

创建 templates/1.yaml 内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: {{ .Release.Name }}-configmap1
 namespace: {{ .Release.Namespace }}
data:
 {{- /* 判断布尔变量是否为真 */ -}}
 {{- if .Values.ingress.enabled }}
 ingress: "配置ingress"
 {{- else }}
 ingress: "不配置ingress"
 {{- end }}

 {{- /* 判断字符串是否相等 */ -}}
 {{- if eq .Values.Person.name "dexter" }}
 welcome: "你好Dexter!"
 {{- else }}
 welcome: "你好谁谁!!"
 {{- end }}

 {{- /* 使用if判断字符串是否为空 */ -}}
 {{- if .Values.Person.name }}
 ifNameEmptyMessage: "name不为空"
 {{- else }}
 ifNameEmptyMessage: "name为空"
 {{- end }}
```

values.yaml 内容如下：

```yaml
Person:
 name: "dexter1"
ingress:
 enabled: true
```

调试

```sh
helm install demo1 . --debug --dry-run
```



```shell
[root@k8s-master mychart]# cat templates/configmap.yaml 

 
[root@k8s-master mychart]# cat values.yaml 

 
[root@k8s-master mychart]# helm install myconfigmap1 . --debug --dry-run
install.go:192: [debug] Original chart version: ""
install.go:209: [debug] CHART PATH: /root/mychart

NAME: myconfigmap1
LAST DEPLOYED: Mon Dec 19 12:21:36 2022
NAMESPACE: default
STATUS: pending-install
REVISION: 1
TEST SUITE: None
USER-SUPPLIED VALUES:
{}

COMPUTED VALUES:
Person:
  name: dexter1
ingress:
  enabled: true

HOOKS:
MANIFEST:
---
# Source: mychart/templates/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap1-configmap1
 namespace: default
data:
 ingress: "配置ingress"
 welcome: "你好谁谁!!"
```

#### with语句

```shell
[root@k8s-master mychart]# cat templates/configmap.yaml 
apiVersion: v1
kind: ConfigMap
metadata:
 name: {{ .Release.Name }}-configmap1
 namespace: {{ .Release.Namespace }}
data:
 {{- with .Values.people }}
 name: {{ .info.name | quote }}
 age: {{ .info.age }}
 {{- end }}

[root@k8s-master mychart]# cat values.yaml 
people:
 info:
  name: "dexter1"
  age: 11
  
[root@k8s-master mychart]# helm install myconfigmap1 . --debug --dry-run
install.go:192: [debug] Original chart version: ""
install.go:209: [debug] CHART PATH: /root/mychart

NAME: myconfigmap1
LAST DEPLOYED: Mon Dec 19 12:27:03 2022
NAMESPACE: default
STATUS: pending-install
REVISION: 1
TEST SUITE: None
USER-SUPPLIED VALUES:
{}

COMPUTED VALUES:
people:
  info:
    age: 11
    name: dexter1

HOOKS:
MANIFEST:
---
# Source: mychart/templates/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap1-configmap1
 namespace: default
data:
 name: "dexter1"
 age: 11
```

#### range语句

##### range index 索引

> https://helm.sh/docs/chart_template_guide/variables/

在当前目录创建 helm 项目

```
helm create .
```

删除 templates 中的文件

```
rm -rf templates/*
```

创建 templates/1.yaml 内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: {{ .Release.Name }}-configmap
data:
  toppings: |-
    {{- range $index, $topping := .Values.pizzaToppings }}
    {{ $index }}: {{ $topping }}
    {{- end }}
```

values.yaml 内容如下：

```yaml
pizzaToppings:
- mushrooms
- cheese
- peppers
- onions
```

调试

```sh
helm install demo1 . --debug --dry-run
```



##### range key 键值

> https://helm.sh/docs/chart_template_guide/variables/

在当前目录创建 helm 项目

```
helm create .
```

删除 templates 中的文件

```
rm -rf templates/*
```

创建 templates/1.yaml 内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: {{ .Release.Name }}-configmap
data:
  myvalue: "Hello World"
  {{- range $key, $val := .Values.favorite }}
  {{ $key }}: {{ $val | quote }}
  {{- end }}
```

values.yaml 内容如下：

```yaml
favorite:
  drink: "coffee"
  food: "PIZZA"
```

调试

```sh
helm install demo1 . --debug --dry-run
```



##### range 特殊变量 .(dot) 符号

> https://helm.sh/docs/chart_template_guide/variables/

在当前目录创建 helm 项目

```
helm create .
```

删除 templates 中的文件

```
rm -rf templates/*
```

创建 templates/1.yaml 内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: {{ .Release.Name }}-configmap1
 namespace: {{ .Release.Namespace }}
data:
 address: |-
  {{- range .Values.address }}
  - {{ . | title }}
  {{- end }}
```

values.yaml 内容如下：

```yaml
address:
- beijing
- shanghai
- guangzhou
```

调试

```sh
helm install demo1 . --debug --dry-run
```



### helm subchart/dependency

> https://levelup.gitconnected.com/helm-dependencies-1907facbe410
> https://helm.sh/docs/chart_template_guide/subcharts_and_globals/

创建并初始化 demo-mychart 和 demo-mysubchart 目录

```
mkdir demo-mychart
cd demo-mychart
helm create .
rm -rf templates/*
mkdir demo-mysubchart
cd demo-mysubchart
helm create .
rm -rf templates/*
```

demo-mysubchart/Chart.yaml 内容如下：

```yaml
apiVersion: v2
name: mysubchart
description: A Helm chart for Kubernetes

# A chart can be either an 'application' or a 'library' chart.
#
# Application charts are a collection of templates that can be packaged into versioned archives
# to be deployed.
#
# Library charts provide useful utilities or functions for the chart developer. They're included as
# a dependency of application charts to inject those utilities and functions into the rendering
# pipeline. Library charts do not define any templates and therefore cannot be deployed.
type: application

# This is the chart version. This version number should be incremented each time you make changes
# to the chart and its templates, including the app version.
# Versions are expected to follow Semantic Versioning (https://semver.org/)
version: 0.1.0

# This is the version number of the application being deployed. This version number should be
# incremented each time you make changes to the application. Versions are not expected to
# follow Semantic Versioning. They should reflect the version the application is using.
# It is recommended to use it with quotes.
appVersion: "1.16.0"
```

demo-mysubchart/values.yaml 内容如下：

```yaml
name: "Dexter"
```

demo-mysubchart/templates/1.yaml 内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: {{ .Release.Name }}-configmap-mysubchart
 namespace: {{ .Release.Namespace }}
data:
  myName: {{ .Values.name }}
```

demo-mychart/Chart.yaml 内容如下：

```yaml
apiVersion: v2
name: .
description: A Helm chart for Kubernetes

# A chart can be either an 'application' or a 'library' chart.
#
# Application charts are a collection of templates that can be packaged into versioned archives
# to be deployed.
#
# Library charts provide useful utilities or functions for the chart developer. They're included as
# a dependency of application charts to inject those utilities and functions into the rendering
# pipeline. Library charts do not define any templates and therefore cannot be deployed.
type: application

# This is the chart version. This version number should be incremented each time you make changes
# to the chart and its templates, including the app version.
# Versions are expected to follow Semantic Versioning (https://semver.org/)
version: 0.1.0

# This is the version number of the application being deployed. This version number should be
# incremented each time you make changes to the application. Versions are not expected to
# follow Semantic Versioning. They should reflect the version the application is using.
# It is recommended to use it with quotes.
appVersion: "1.16.0"

dependencies:
  - name: mysubchart
    version: 0.1.0
    repository: file://../demo-mysubchart
```

demo-mychart/values.yaml 内容如下：

```yaml
# 设置 mysubchart .Values.name变量
mysubchart:
  name: "Dexterleslie"
```

demo-mychart/templates/1.yaml 内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: {{ .Release.Name }}-configmap-mychart
 namespace: {{ .Release.Namespace }}
data:
```

下载 mysubchart 到 demo-mychart/charts 子目录中

```sh
helm dependency update .
```

调试

```sh
helm install demo1 . --debug --dry-run
```



### helm 多个环境配置

> 使用多个 values-xxx.yaml 配置设置不同环境的参数
> https://codefresh.io/blog/helm-deployment-environments/
>
> NOTE: 上面的方案并不好，因为如果 values-xxx.yaml 参数一旦变动需要维护多个 values-xxx.yaml 文件。所以还是采用使用一个 values.yaml 配置不同环境的参数。

### 子模板和template、include使用

#### 在主模板中定义子模板

> NOTE: 这种方法在实际项目中不使用，所以不demo

#### 在_helpers.tpl定义子模板

> 在实际项目中使用这种方法

```shell
[root@k8s-master mychart]# cat templates/_helpers.tpl 
{{/* 注释 */}}
{{- define "mychart.labels" }}
 labels:
  author: test
  date: {{ now | htmlDate }}
{{- end }}
[root@k8s-master mychart]# cat templates/configmap.yaml 
apiVersion: v1
kind: ConfigMap
metadata:
 name: {{ .Release.Name }}-configmap1
 {{- include "mychart.labels" . }}
data:
 data1: "hello"
 
[root@k8s-master mychart]# helm install myconfigmap1 . --dry-run --debug
install.go:192: [debug] Original chart version: ""
install.go:209: [debug] CHART PATH: /root/mychart

NAME: myconfigmap1
LAST DEPLOYED: Mon Dec 19 20:56:29 2022
NAMESPACE: default
STATUS: pending-install
REVISION: 1
TEST SUITE: None
USER-SUPPLIED VALUES:
{}

COMPUTED VALUES:
address:
- beijing
- shanghai
- guangzhou

HOOKS:
MANIFEST:
---
# Source: mychart/templates/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap1-configmap1
 labels:
  author: test
  date: 2022-12-19
data:
 data1: "hello"
```

#### 带变量的子模板

```shell
[root@k8s-master mychart]# cat templates/configmap.yaml 
apiVersion: v1
kind: ConfigMap
metadata:
 name: {{ .Release.Name }}-configmap1
 labels:
 #template和include区别是：include输出能够被函数处理，如下面演示
{{- include "mychart.labels" . | toString | indent 2 }}
data:
 data1: "hello"
 
[root@k8s-master mychart]# cat templates/_helpers.tpl 
{{/* 注释 */}}
{{- define "mychart.labels" }}
name: {{ .Values.person.info.name }}
age: {{ .Values.person.info.age | quote }}
{{- end }}

[root@k8s-master mychart]# helm install myconfigmap1 . --dry-run --debug
install.go:192: [debug] Original chart version: ""
install.go:209: [debug] CHART PATH: /root/mychart

NAME: myconfigmap1
LAST DEPLOYED: Mon Dec 19 21:26:05 2022
NAMESPACE: default
STATUS: pending-install
REVISION: 1
TEST SUITE: None
USER-SUPPLIED VALUES:
{}

COMPUTED VALUES:
person:
  info:
    age: 22
    name: Dexter

HOOKS:
MANIFEST:
---
# Source: mychart/templates/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap1-configmap1
 labels:
 #template和include区别是：include输出能够被函数处理，如下面演示  
  name: Dexter
  age: "22"
data:
 data1: "hello"
```

#### include用法

> include 语法为 include <标签> <变量参数>，例如： include "mychart.demo1" . 表示使用全局变量 .(dot) 参数调用 mychart.demo1 标签模板，include "mychart.demo1" (dict "k1" "v1") 表示使用字典变量 dict "k1" "v1" 参数调用 mychart.demo1 标签模板。

##### if判断include返回true或者false

```shell
[root@k8s-master mychart]# cat templates/configmap.yaml 
apiVersion: v1
kind: ConfigMap
metadata:
 name: {{ .Release.Name }}-configmap1
data:
 {{- if (include "mychart.testing.bool" .) }}
 data1: "true"
 {{- else }}
 data1: "false"
 {{- end }}
 
[root@k8s-master mychart]# cat templates/_helpers.tpl 
{{/* 注释 */}}
{{- define "mychart.testing.bool" -}}
 {{- if .Values.enabled -}}
  {{- true -}}
 {{- end -}}
{{- end -}}

[root@k8s-master mychart]# cat values.yaml 
enabled: false

[root@k8s-master mychart]# helm install myconfigmap1 . --dry-run --debug
install.go:192: [debug] Original chart version: ""
install.go:209: [debug] CHART PATH: /root/mychart

NAME: myconfigmap1
LAST DEPLOYED: Wed Dec 21 00:02:40 2022
NAMESPACE: default
STATUS: pending-install
REVISION: 1
TEST SUITE: None
USER-SUPPLIED VALUES:
{}

COMPUTED VALUES:
enabled: false

HOOKS:
MANIFEST:
---
# Source: mychart/templates/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap1-configmap1
data:
 data1: "false"
```

##### include返回字符串

```shell
[root@k8s-master mychart]# cat templates/_helpers.tpl 
{{/* 注释 */}}
{{- define "mychart.testing.getversion" -}}
 {{- if .Values.beta -}}
  {{- print "app/v1beta" -}}
 {{- else -}}
  {{- print "app/v1" -}}
 {{- end -}}
{{- end -}}

[root@k8s-master mychart]# cat templates/configmap.yaml 
apiVersion: v1
kind: ConfigMap
metadata:
 name: {{ .Release.Name }}-configmap1
data:
 1.conf:
  data: {{- include "mychart.testing.getversion" . }}
  
[root@k8s-master mychart]# cat values.yaml 
beta: true

[root@k8s-master mychart]# helm install myconfigmap1 . --dry-run --debug
install.go:192: [debug] Original chart version: ""
install.go:209: [debug] CHART PATH: /root/mychart

NAME: myconfigmap1
LAST DEPLOYED: Wed Dec 21 00:16:41 2022
NAMESPACE: default
STATUS: pending-install
REVISION: 1
TEST SUITE: None
USER-SUPPLIED VALUES:
{}

COMPUTED VALUES:
beta: true

HOOKS:
MANIFEST:
---
# Source: mychart/templates/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap1-configmap1
data:
 1.conf:
  data:app/v1beta
```



##### 带 dict 参数的 include

> https://stackoverflow.com/questions/60636775/pass-multiple-variables-in-helm-template

在当前目录创建 helm 项目

```
helm create .
```

删除 templates 中的文件

```
rm -rf templates/*
```

创建 templates/_helpers.tpl 内容如下：

```yaml
{{- define "mychart.demo1" -}}
{{/* 获取 dict 的 k1键 的值 */}}
myK1: {{ .item.k1 }}
myK2: {{ .item.k2 }}
{{- end -}}
```

创建 templates/1.yaml 内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap
data:
 {{- range $item := .Values.items }}
 {{- include "mychart.demo1" (dict "item" $item) | indent 4 }}
 {{- end }}
```

values.yaml 内容如下：

```yaml
items:
- k1: v1
  k2: v2
```

调试

```sh
helm install demo1 . --debug --dry-run
```



#### template 和 include 区别

> https://stackoverflow.com/questions/71086697/how-does-template-and-include-differ-in-helm
>
> template 和 include 调用语法是一样的，template的结果无法捕获在变量中或包含在管道中。include的结果作为字符串返回并且能够包含在管道中。
>
> 如下例子：
> template 结果直接输出不能使用变量捕获，{{ template "common.tplvalues.merge" (dict "values" (list .customLabels $default) "context" .context) }}
> include 结果能够使用变量捕获或者输出到管道中，{{ include "common.names.namespace" . | quote }}

