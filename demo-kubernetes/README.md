# k8s





## minikube

> 原理: minikube使用docker容器环境启动一个名为minikube的容器，这个容器内会运行k8s所有组件。
>
> 支持ubuntu、centOS8部署
>
> https://minikube.sigs.k8s.io/docs/start/
>
> https://github.com/kubernetes/minikube/issues/14477

**使用minikube创建k8s集群**

```shell
# 使用dcli安装docker环境

# 设置mimikube cli
curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
sudo install minikube-linux-amd64 /usr/local/bin/minikube

# 使用minikube启动k8s环境，在root用户下启动k8s需要使用--force，指定启动v1.23.8版本k8s，否则不能成功启动
minikube start --kubernetes-version=v1.23.8 --force

# 验证集群是否正常通过查询所有pods
minikube kubectl -- get po -A

# 配置kubectl命令
alias kubectl="minikube kubectl --"

# 测试部署pod
kubectl create deployment hello-minikube --image=kicbase/echo-server:1.0
kubectl expose deployment hello-minikube --type=NodePort --port=8080
kubectl get services hello-minikube
kubectl port-forward service/hello-minikube 7080:8080

# 访问服务
curl http://localhost:7080/
```

**使用minikube配置k8s dashboard**

> https://stackoverflow.com/questions/47173463/how-to-access-local-kubernetes-minikube-dashboard-remotely

```shell
# 获取本地dashboard url
minikube dashboard --url

# 使用kubectl代理本地dashboard以便外部访问dashboard
kubectl proxy --address='0.0.0.0' --disable-filter=true

# 打开浏览器访问dashboard
http://external-ip:37337/api/v1/namespaces/kubernetes-dashboard/services/http:kubernetes-dashboard:/proxy/
```





## Kubernetes Google Container Registry国内镜像替换

> https://kubernetes.feisky.xyz/appendix/mirrors







## k8s安装

> NOTE: 使用terraform安装

检查k8s服务是否正常

```shell
# 在master节点查看基础容器运行状态
kubectl get pods -n kube-system

# 在master节点查看所有节点状态
kubectl get nodes

# 部署nginx服务
kubectl create deployment nginx --image=nginx
# 使用NodePort方式暴露nginx服务，其中target-port为容器中应用监听的port，port为服务通过服务集群ip地址访问的port
kubectl expose deployment nginx --target-port=80 --port=80 --type=NodePort
# 查看nginx NodePort端口并使用浏览器访问成功
kubectl get pod,service

# 使用curl测试nginx是否正常，其中30576端口为NodePort类型服务随机分配的端口
curl localhost:30576
```





## kubectl命令用法



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



### kubectl port-forward将本地网络端口转发到pod中的端口

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

# 将本地网络端口8888转发到pod中的端口8080，用于临时调试和测试
kubectl port-forward kubia-manual 8888:8080

# 使用curl命令向pod发送http请求
curl localhost:8888

# 删除pod
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

```
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

```shell
[root@k8s-master ~]# cat 1.yaml 
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
# 使用命令观察init容器和main容器过程
[root@k8s-master ~]# kubectl get pod
NAME                                      READY   STATUS     RESTARTS   AGE
base-pod                                  0/1     Init:0/2   0          8s
```

### 钩子函数

> 在main容器启动后postStart和关闭前preStop执行指定的动作，每个容器启动后和停止前执行的。

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

```shell
# app.js 内容如下:
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

# Dockerfile 内容如下:
FROM node:7

ADD app.js /app.js
ENTRYPOINT ["node", "app.js"]

# 编译镜像
docker build --tag docker.118899.net:10001/yyd-public/demo-k8s-readinessprobe .

# 推送镜像
docker push docker.118899.net:10001/yyd-public/demo-k8s-readinessprobe

# 1.yaml内容如下:
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

# 2.yaml内容如下:
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
 
# 1分钟后才能正常访问服务，因为readinessProbe作用，pod 1分钟后才ready状态
kubectl get pods
kubectl get services

[root@k8s-master ~]# curl 10.1.39.125
curl: (7) Failed connect to 10.1.39.125:80; Connection refused
[root@k8s-master ~]# curl 10.1.39.125
curl: (7) Failed connect to 10.1.39.125:80; Connection refused
[root@k8s-master ~]# curl 10.1.39.125
curl: (7) Failed connect to 10.1.39.125:80; Connection refused
[root@k8s-master ~]# curl 10.1.39.125
curl: (7) Failed connect to 10.1.39.125:80; Connection refused
[root@k8s-master ~]# curl 10.1.39.125
deployment1-78fdbb9b4-twm5m服务已准备好，可以访问

# 扩容到两个pod，第二个新的pod需要等待一分钟才ready状态接受请求
kubectl scale deployment deployment1 --replicas=2

# 销毁资源
kubectl delete -f 1.yaml
kubectl delete -f 2.yaml
```



### **headless(无头服务)**

> headless服务是通过service的dns解析访问相应的pod ip地址，例如下面例子：在busybox pod中通过headless-service无头服务名称就能够访问两个nginx pod endpoints。
> 尽管headless服务看起来可能与常规的服务不同，但在客户的视角上他们并无不同。即使使用headlesss服务，客户也可以通过连接的服务的DNS名称来连接到pod上，就像常规服务一样。但是对于headless服务，由于DNS返回了pod的ip，客户端直接连接到该pod，而不是通过服务代理。

```yaml
# 用于创建无头服务，1.yaml内容如下:
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

# 用于创建另个deployment，2.yaml内容如下:
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

# 用于创建，3.yaml内容如下:
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
kubectl create -f 1.yaml 

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
kubectl create -f 1.yaml 

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
kubectl create -f 2.yaml 

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

```
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



## API服务器的安全防护

### 创建ServiceAccount并在pod中使用新创建的ServiceAccount

```shell
[root@k8s-master ~]# cat 1.yaml 
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
 serviceAccountName: foo
 containers:
  - name: main
    image: alpine/curl
    command: ["sh", "-c", "sleep 7200;"]
  - name: ambassador
    image: luksa/kubectl-proxy
    
[root@k8s-master ~]# kubectl apply -f 1.yaml 
serviceaccount/foo created
pod/curl-custom-serviceaccount created
# 删除之前创建的clusterrolebinding(所有ServiceAccount能够操作任何资源)
[root@k8s-master ~]# kubectl delete clusterrolebinding permissive-binding
clusterrolebinding.rbac.authorization.k8s.io "permissive-binding" deleted
[root@k8s-master ~]# kubectl get serviceaccount
NAME                     SECRETS   AGE
default                  1         34d
foo                      1         41s
# 显示ServiceAccount详细信息
[root@k8s-master ~]# kubectl describe serviceaccount foo
Name:                foo
Namespace:           default
Labels:              <none>
Annotations:         <none>
Image pull secrets:  <none>
Mountable secrets:   foo-token-8vlsq
Tokens:              foo-token-8vlsq
Events:              <none>
# 显示ServiceAccount对应的Secret详细信息
[root@k8s-master ~]# kubectl describe secret foo-token-8vlsq
Name:         foo-token-8vlsq
Namespace:    default
Labels:       <none>
Annotations:  kubernetes.io/service-account.name: foo
              kubernetes.io/service-account.uid: 9d858e34-3f20-4e7b-a827-ae5bf826bd9b

Type:  kubernetes.io/service-account-token

Data
====
namespace:  7 bytes
token:      eyJhbGciOiJSUzI1NiIsImtpZCI6InQ3aFlRU3VMbWtiUG5IcVRiZzljc1RqRmdzQnZ0dzFxNU1vTEU0VWJ5azQifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImZvby10b2tlbi04dmxzcSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50Lm5hbWUiOiJmb28iLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiI5ZDg1OGUzNC0zZjIwLTRlN2ItYTgyNy1hZTViZjgyNmJkOWIiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6ZGVmYXVsdDpmb28ifQ.IJL9-FyRYwd5V8ky6f5cBiSQcWJa9oYpSDi7C9170QRa47JQQZF0xttCzFHj2ez3PaVzkvz3z55PD7sDpQRycdt59fk2Uern20wJ2G7faMxFyssIMNkFByYjrip8XDnCrTW8BppbgMVm4gM7noFtbUgflZ9jjP45tHrvuOHcRrEsK9n2giEgs7__hBRjfPF3cyLPyYagATtc8tzLBCWFecAveEqvqEYUeMDhxFG7fIb2bopkbXK-Q96elxxDKQkeQAyTPrDIXHG_-XQZ_pyCaZBIyy4_yFpzGpIxUcmPLDXc6uEqvIicifl6KXIwLZ_kh7-1RugRrQLTmt6VHYiiSw
ca.crt:     1066 bytes

[root@k8s-master ~]# kubectl exec -it curl-custom-serviceaccount -c main /bin/sh
kubectl exec [POD] [COMMAND] is DEPRECATED and will be removed in a future version. Use kubectl exec [POD] -- [COMMAND] instead.
# 进入容器内查看当前使用的ServiceAccount token和Secret的token对应
/ # cat /var/run/secrets/kubernetes.io/serviceaccount/token 
eyJhbGciOiJSUzI1NiIsImtpZCI6InQ3aFlRU3VMbWtiUG5IcVRiZzljc1RqRmdzQnZ0dzFxNU1vTEU0VWJ5azQifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImZvby10b2tlbi04dmxzcSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50Lm5hbWUiOiJmb28iLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiI5ZDg1OGUzNC0zZjIwLTRlN2ItYTgyNy1hZTViZjgyNmJkOWIiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6ZGVmYXVsdDpmb28ifQ.IJL9-FyRYwd5V8ky6f5cBiSQcWJa9oYpSDi7C9170QRa47JQQZF0xttCzFHj2ez3PaVzkvz3z55PD7sDpQRycdt59fk2Uern20wJ2G7faMxFyssIMNkFByYjrip8XDnCrTW8BppbgMVm4gM7noFtbUgflZ9jjP45tHrvuOHcRrEsK9n2giEgs7__hBRjfPF3cyLPyYagATtc8tzLBCWFecAveEqvqEYUeMDhxFG7fIb2bopkbXK-Q96elxxDKQkeQAyTPrDIXHG_-XQZ_pyCaZBIyy4_yFpzGpIxUcmPLDXc6uEqvIicifl6KXIwLZ_kh7-1RugRrQLTmt6VHYiiSw/ # 
# 查看命名空间default下的ServiceAccount，只是foo ServiceAccount权限不足
/ # curl localhost:8001/api/v1/namespaces/default/serviceaccounts
{
  "kind": "Status",
  "apiVersion": "v1",
  "metadata": {
    
  },
  "status": "Failure",
  "message": "serviceaccounts is forbidden: User \"system:serviceaccount:default:foo\" cannot list resource \"serviceaccounts\" in API group \"\" in the namespace \"default\"",
  "reason": "Forbidden",
  "details": {
    "kind": "serviceaccounts"
  },
  "code": 403
}/ #
```

### 使用Role和RoleBinding

> Role和RoleBinding都是命名空间的资源，这意味着它们属于和应用在同一个命名空间资源上。
>
> 一个Role只允许访问和Role在同一命名空间的资源。如果你希望允许跨不同命名空间访问资源，就必须在每个命名空间中创建Role和RoleBinding。

```shell
# 授权ServiceAccount foo拥有对ServiceAccounts资源的get、list权限
[root@k8s-master ~]# cat 1.yaml 
apiVersion: v1
kind: ServiceAccount
metadata:
 name: foo

---
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
 name: service-reader
rules:
 # ServiceAccount拥有对ServiceAccounts资源的get、list权限
 - apiGroups: [""]
   verbs: ["get", "list"]
   resources: ["serviceaccounts"]

---
# 创建RoleBinding绑定Role:service-reader到ServiceAccount:default/foo
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
 name: test
roleRef:
 apiGroup: rbac.authorization.k8s.io
 kind: Role
 name: service-reader
subjects:
 - kind: ServiceAccount
   # ServiceAccount名称
   name: foo
   # 命名空间名称
   namespace: default

---
apiVersion: v1
kind: Pod
metadata:
 name: curl-custom-serviceaccount
spec:
 serviceAccountName: foo
 containers:
  - name: main
    image: alpine/curl
    command: ["sh", "-c", "sleep 7200;"]
  - name: ambassador
    image: luksa/kubectl-proxy

[root@k8s-master ~]# kubectl exec -it curl-custom-serviceaccount -c main /bin/sh
kubectl exec [POD] [COMMAND] is DEPRECATED and will be removed in a future version. Use kubectl exec [POD] -- [COMMAND] instead.
/ # curl localhost:8001/api/v1/namespaces/default/serviceaccounts
{
  "kind": "ServiceAccountList",
  "apiVersion": "v1",
  "metadata": {
    "resourceVersion": "4626087"
  },
  "items": [
    {
      "metadata": {
        "name": "default",
        "namespace": "default",
        "uid": "48203853-e303-42f8-bfce-9c829e03bcbd",
        "resourceVersion": "394",
        "creationTimestamp": "2022-12-05T06:24:40Z"
      },
      "secrets": [
        {
          "name": "default-token-q8hxp"
        }
      ]
    },
    {
      "metadata": {
        "name": "foo",
        "namespace": "default",
        "uid": "dc89fb4f-0284-480c-90e3-4eca78b473f1",
        "resourceVersion": "4625998",
        "creationTimestamp": "2023-01-08T15:25:53Z",
        "annotations": {
          "kubectl.kubernetes.io/last-applied-configuration": "{\"apiVersion\":\"v1\",\"kind\":\"ServiceAccount\",\"metadata\":{\"annotations\":{},\"name\":\"foo\",\"namespace\":\"default\"}}\n"
        },
        "managedFields": [
          {
            "manager": "kube-controller-manager",
            "operation": "Update",
            "apiVersion": "v1",
            "time": "2023-01-08T15:25:53Z",
            "fieldsType": "FieldsV1",
            "fieldsV1": {"f:secrets":{".":{},"k:{\"name\":\"foo-token-txb6r\"}":{".":{},"f:name":{}}}}
          },
          {
            "manager": "kubectl-client-side-apply",
            "operation": "Update",
            "apiVersion": "v1",
            "time": "2023-01-08T15:25:53Z",
            "fieldsType": "FieldsV1",
            "fieldsV1": {"f:metadata":{"f:annotations":{".":{},"f:kubectl.kubernetes.io/last-applied-configuration":{}}}}
          }
        ]
      },
      "secrets": [
        {
          "name": "foo-token-txb6r"
        }
      ]
    },
    {
      "metadata": {
        "name": "nfs-client-provisioner",
        "namespace": "default",
        "uid": "051d8d13-15d9-48a8-9c67-b315e638f506",
        "resourceVersion": "3951847",
        "creationTimestamp": "2023-01-04T13:55:37Z",
        "annotations": {
          "kubectl.kubernetes.io/last-applied-configuration": "{\"apiVersion\":\"v1\",\"kind\":\"ServiceAccount\",\"metadata\":{\"annotations\":{},\"name\":\"nfs-client-provisioner\",\"namespace\":\"default\"}}\n"
        },
        "managedFields": [
          {
            "manager": "kube-controller-manager",
            "operation": "Update",
            "apiVersion": "v1",
            "time": "2023-01-04T13:55:37Z",
            "fieldsType": "FieldsV1",
            "fieldsV1": {"f:secrets":{".":{},"k:{\"name\":\"nfs-client-provisioner-token-vfg8h\"}":{".":{},"f:name":{}}}}
          },
          {
            "manager": "kubectl-client-side-apply",
            "operation": "Update",
            "apiVersion": "v1",
            "time": "2023-01-04T13:55:37Z",
            "fieldsType": "FieldsV1",
            "fieldsV1": {"f:metadata":{"f:annotations":{".":{},"f:kubectl.kubernetes.io/last-applied-configuration":{}}}}
          }
        ]
      },
      "secrets": [
        {
          "name": "nfs-client-provisioner-token-vfg8h"
        }
      ]
    }
  ]
}/ # 
```

### 使用ClusterRole和ClusterRoleBinding

> 为什么需要ClusterRole和ClusterRoleBinding？一个常规的角色只允许访问和角色在同一命名空间中的资源。如果你希望允许跨不同命名空间访问资源，就必须在每个命名空间中创建一个Role和RoleBinding。如果你想将这种行为扩展到所有命名空间（集群管理员可能需要），需要在每个命名空间创建相同的Role和RoleBinding。当创建一个新的命名空间时，必须记住也要在新的命名空间中创建这两个资源。
>
> ClusterRole是一种集群级别资源，它允许访问没有命名空间的资源和非资源型的URL，或者作为单个命名空间内部绑定的公共角色，从而避免必须在每个命名空间中重新定义相同的角色。

```shell
# 演示使用ClusterRole授权访问集群级别的资源
[root@k8s-master ~]# cat 1.yaml 
apiVersion: v1
kind: ServiceAccount
metadata:
 name: foo

---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
 name: service-reader
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
 name: test
roleRef:
 apiGroup: rbac.authorization.k8s.io
 kind: ClusterRole
 name: service-reader
subjects:
 - kind: ServiceAccount
   name: foo
   namespace: default

---
apiVersion: v1
kind: Pod
metadata:
 name: curl-custom-serviceaccount
spec:
 serviceAccountName: foo
 containers:
  - name: main
    image: alpine/curl
    command: ["sh", "-c", "sleep 7200;"]
  - name: ambassador
    image: luksa/kubectl-proxy

[root@k8s-master ~]# kubectl exec -it curl-custom-serviceaccount -c main /bin/sh
kubectl exec [POD] [COMMAND] is DEPRECATED and will be removed in a future version. Use kubectl exec [POD] -- [COMMAND] instead.
# 没有授权访问ServiceAccount资源
/ # curl localhost:8001/api/v1/namespaces/default/serviceaccounts
{
  "kind": "Status",
  "apiVersion": "v1",
  "metadata": {
    
  },
  "status": "Failure",
  "message": "serviceaccounts is forbidden: User \"system:serviceaccount:default:foo\" cannot list resource \"serviceaccounts\" in API group \"\" in the namespace \"default\"",
  "reason": "Forbidden",
  "details": {
    "kind": "serviceaccounts"
  },
  "code": 403
}
# 已经授权访问集群级别资源PersistentVolumes
/ # curl localhost:8001/api/v1/persistentvolumes
{
  "kind": "PersistentVolumeList",
  "apiVersion": "v1",
  "metadata": {
    "resourceVersion": "4728159"
  },
  "items": []
}/ #
```

## pod与集群节点自动伸缩

###  配置metrics-server

> https://github.com/kubernetes-sigs/metrics-server
>
> kubernetes +1.19 安装 metrics-server-v0.6.x

```shell
# 安装metrics-server-v0.6.2
# 下载 https://github.com/kubernetes-sigs/metrics-server/releases/download/v0.6.2/components.yaml

# metrics-server args添加 - --kubelet-insecure-tls 表示抓取指标数据时不使用https通讯
# metrics-server image修改为 registry.cn-hangzhou.aliyuncs.com/google_containers/metrics-server:v0.6.2
[root@k8s-master ~]# cat components.yaml
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
  
[root@k8s-master ~]# kubectl apply -f components.yaml 
serviceaccount/metrics-server created
clusterrole.rbac.authorization.k8s.io/system:aggregated-metrics-reader created
clusterrole.rbac.authorization.k8s.io/system:metrics-server created
rolebinding.rbac.authorization.k8s.io/metrics-server-auth-reader created
clusterrolebinding.rbac.authorization.k8s.io/metrics-server:system:auth-delegator created
clusterrolebinding.rbac.authorization.k8s.io/system:metrics-server created
service/metrics-server created
deployment.apps/metrics-server created
apiservice.apiregistration.k8s.io/v1beta1.metrics.k8s.io created

# 查看kube-system metrics-server是Running状态
[root@k8s-master ~]# kubectl get pod --namespace kube-system -w
NAME                                 READY   STATUS    RESTARTS   AGE
coredns-7f89b7bc75-9jvzv             1/1     Running   1          35d
coredns-7f89b7bc75-nwpk2             1/1     Running   1          35d
etcd-k8s-master                      1/1     Running   1          35d
kube-apiserver-k8s-master            1/1     Running   1          35d
kube-controller-manager-k8s-master   1/1     Running   1          35d
kube-proxy-2t2ck                     1/1     Running   2          35d
kube-proxy-6vmvt                     1/1     Running   1          35d
kube-proxy-qb8kg                     1/1     Running   2          35d
kube-scheduler-k8s-master            1/1     Running   1          35d
metrics-server-6cc4fb8489-nk5p2      1/1     Running   0          32s
```



### 基于CPU使用率的HPA自动伸缩

```shell
[root@k8s-master ~]# cat 1.yaml 
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

[root@k8s-master ~]# kubectl apply -f 1.yaml 
deployment.apps/deployment1 created
horizontalpodautoscaler.autoscaling/kubia created
# 刚刚创建HPA时，有3个pod在运行
[root@k8s-master ~]# kubectl get pod
NAME                                      READY   STATUS    RESTARTS   AGE
deployment1-65c8fdb647-4xl7b              1/1     Running   0          21s
deployment1-65c8fdb647-9npw6              1/1     Running   0          21s
deployment1-65c8fdb647-klsft              1/1     Running   0          21s
# 查看HPA状态
[root@k8s-master ~]# kubectl get hpa
NAME    REFERENCE                TARGETS   MINPODS   MAXPODS   REPLICAS   AGE
kubia   Deployment/deployment1   0%/30%    1         5         3          118s

# 5分钟后查看HPA，自动把deployment1 replicas修改为1
[root@k8s-master ~]# kubectl describe hpa kubia
Name:                                                  kubia
Namespace:                                             default
Labels:                                                <none>
Annotations:                                           <none>
CreationTimestamp:                                     Tue, 10 Jan 2023 12:59:52 +0800
Reference:                                             Deployment/deployment1
Metrics:                                               ( current / target )
  resource cpu on pods  (as a percentage of request):  0% (0) / 30%
Min replicas:                                          1
Max replicas:                                          5
Deployment pods:                                       3 current / 1 desired
Conditions:
  Type            Status  Reason              Message
  ----            ------  ------              -------
  AbleToScale     True    SucceededRescale    the HPA controller was able to update the target scale to 1
  ScalingActive   True    ValidMetricFound    the HPA was able to successfully calculate a replica count from cpu resource utilization (percentage of request)
  ScalingLimited  False   DesiredWithinRange  the desired count is within the acceptable range
Events:
  Type     Reason                        Age    From                       Message
  ----     ------                        ----   ----                       -------
  Warning  FailedGetResourceMetric       5m30s  horizontal-pod-autoscaler  failed to get cpu utilization: unable to get metrics for resource cpu: no metrics returned from resource metrics API
  Warning  FailedComputeMetricsReplicas  5m30s  horizontal-pod-autoscaler  invalid metrics (1 invalid out of 1), first error is: failed to get cpu utilization: unable to get metrics for resource cpu: no metrics returned from resource metrics API
  Normal   SuccessfulRescale             9s     horizontal-pod-autoscaler  New size: 1; reason: All metrics below target
# 最后剩余1个pod在运行
[root@k8s-master ~]# kubectl get pod
NAME                                      READY   STATUS    RESTARTS   AGE
deployment1-65c8fdb647-9npw6              1/1     Running   0          7m8s

# 模拟产生压力，pod自动扩容
[root@k8s-master ~]# kubectl run -it --rm --restart=Never loadgenerator --image=alpine/curl -- sh -c "while true; do curl -s myservice1 > /dev/null; done;"
If you don't see a command prompt, try pressing enter.
^Cpod "loadgenerator" deleted
pod default/loadgenerator terminated (Error)
# 稍过几分钟查看发现自动创建多3个pod响应压力
[root@k8s-master ~]# kubectl get hpa
NAME    REFERENCE                TARGETS   MINPODS   MAXPODS   REPLICAS   AGE
kubia   Deployment/deployment1   92%/30%   1         5         4          28m
[root@k8s-master ~]# kubectl get pod
NAME                                      READY   STATUS    RESTARTS   AGE
deployment1-65c8fdb647-4xwn2              1/1     Running   0          16s
deployment1-65c8fdb647-lz7pz              1/1     Running   0          27m
deployment1-65c8fdb647-q6jvq              1/1     Running   0          16s
deployment1-65c8fdb647-xfv6w              1/1     Running   0          16s
loadgenerator                             1/1     Running   0          75s
```

### 基于QPS的HPA自动伸缩

> todo 没有做实验

### 手动标记节点为不可调度、排空节点

```shell
[root@k8s-master ~]# cat 1.yaml 
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
[root@k8s-master ~]# cat 2.yaml 
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
      
[root@k8s-master ~]# kubectl apply -f 1.yaml 
deployment.apps/deployment1 created
# 有些pod被调度到k8s-node2
[root@k8s-master ~]# kubectl get pod -o wide
NAME                                      READY   STATUS    RESTARTS   AGE   IP            NODE        NOMINATED NODE   READINESS GATES
deployment1-66b6f8c9b7-75rmm              1/1     Running   0          48s   10.244.2.61   k8s-node2   <none>           <none>
deployment1-66b6f8c9b7-8gdgg              1/1     Running   0          48s   10.244.1.8    k8s-node1   <none>           <none>
deployment1-66b6f8c9b7-bgbkp              1/1     Running   0          48s   10.244.2.62   k8s-node2   <none>           <none>
deployment1-66b6f8c9b7-pvz64              1/1     Running   0          48s   10.244.2.59   k8s-node2   <none>           <none>
deployment1-66b6f8c9b7-ww55m              1/1     Running   0          48s   10.244.2.60   k8s-node2   <none>           <none>
# 标记节点为不可调度（但对其上的pod不做任何事情）
[root@k8s-master ~]# kubectl cordon k8s-node2
node/k8s-node2 cordoned
# 创建deployment2
[root@k8s-master ~]# kubectl apply -f 2.yaml 
deployment.apps/deployment2 created
# 可以看到后来创建的deployment2的pod不会被调度到k8s-node2中
[root@k8s-master ~]# kubectl get pod -o wide
NAME                                      READY   STATUS    RESTARTS   AGE   IP            NODE        NOMINATED NODE   READINESS GATES
deployment1-66b6f8c9b7-75rmm              1/1     Running   0          22m   10.244.2.61   k8s-node2   <none>           <none>
deployment1-66b6f8c9b7-8gdgg              1/1     Running   0          22m   10.244.1.8    k8s-node1   <none>           <none>
deployment1-66b6f8c9b7-bgbkp              1/1     Running   0          22m   10.244.2.62   k8s-node2   <none>           <none>
deployment1-66b6f8c9b7-pvz64              1/1     Running   0          22m   10.244.2.59   k8s-node2   <none>           <none>
deployment1-66b6f8c9b7-ww55m              1/1     Running   0          22m   10.244.2.60   k8s-node2   <none>           <none>
deployment2-66b6f8c9b7-bvspv              1/1     Running   0          19m   10.244.1.11   k8s-node1   <none>           <none>
deployment2-66b6f8c9b7-jnfwv              1/1     Running   0          19m   10.244.1.12   k8s-node1   <none>           <none>
deployment2-66b6f8c9b7-lsnqk              1/1     Running   0          19m   10.244.1.13   k8s-node1   <none>           <none>
deployment2-66b6f8c9b7-p7lpr              1/1     Running   0          19m   10.244.1.10   k8s-node1   <none>           <none>
deployment2-66b6f8c9b7-wdrx8              1/1     Running   0          19m   10.244.1.9    k8s-node1   <none>           <none>
# 标记节点为不可以调度，随后疏散其上所有pod
[root@k8s-master ~]# kubectl drain k8s-node2 --ignore-daemonsets
node/k8s-node2 already cordoned
WARNING: ignoring DaemonSet-managed Pods: kube-flannel/kube-flannel-ds-gjpqd, kube-system/kube-proxy-2t2ck
evicting pod default/deployment1-66b6f8c9b7-ww55m
evicting pod default/deployment1-66b6f8c9b7-75rmm
evicting pod default/deployment1-66b6f8c9b7-bgbkp
evicting pod default/deployment1-66b6f8c9b7-pvz64
pod/deployment1-66b6f8c9b7-ww55m evicted
pod/deployment1-66b6f8c9b7-pvz64 evicted
pod/deployment1-66b6f8c9b7-bgbkp evicted
pod/deployment1-66b6f8c9b7-75rmm evicted
node/k8s-node2 evicted
# 可以看到没有pod运行在k8s-node2上了
[root@k8s-master ~]# kubectl get pod -o wide
NAME                                      READY   STATUS    RESTARTS   AGE    IP            NODE        NOMINATED NODE   READINESS GATES
deployment1-66b6f8c9b7-6flcg              1/1     Running   0          104s   10.244.1.16   k8s-node1   <none>           <none>
deployment1-66b6f8c9b7-8gdgg              1/1     Running   0          26m    10.244.1.8    k8s-node1   <none>           <none>
deployment1-66b6f8c9b7-ct6km              1/1     Running   0          104s   10.244.1.17   k8s-node1   <none>           <none>
deployment1-66b6f8c9b7-pcjlk              1/1     Running   0          104s   10.244.1.14   k8s-node1   <none>           <none>
deployment1-66b6f8c9b7-tqxt6              1/1     Running   0          104s   10.244.1.15   k8s-node1   <none>           <none>
deployment2-66b6f8c9b7-bvspv              1/1     Running   0          23m    10.244.1.11   k8s-node1   <none>           <none>
deployment2-66b6f8c9b7-jnfwv              1/1     Running   0          23m    10.244.1.12   k8s-node1   <none>           <none>
deployment2-66b6f8c9b7-lsnqk              1/1     Running   0          23m    10.244.1.13   k8s-node1   <none>           <none>
deployment2-66b6f8c9b7-p7lpr              1/1     Running   0          23m    10.244.1.10   k8s-node1   <none>           <none>
deployment2-66b6f8c9b7-wdrx8              1/1     Running   0          23m    10.244.1.9    k8s-node1   <none>           <none>

# 取消标记
[root@k8s-master ~]# kubectl uncordon k8s-node2
node/k8s-node2 uncordoned
```

## 高级调度

### 使用污点和容忍度阻止节点调度到特定节点

> 节点选择器和节点亲缘性规则，是通过明确的在pod中添加的信息，来决定一个pod可以或者不可以被调度到那些节点上。而污点则是在不修改已有的pod信息的前提下，通过在节点上添加污点信息，来拒绝pod在某系节点上的部署
>
> 污点效果
>
> - NoSchedule 表示如果pod没有容忍这些污点，pod则不能被调度到包含这些污点的污点上。
> - PreferNoSchedule 是NoSchedule的一个宽松的版本，表示尽量阻止pod被调度到这个节点上，但是如果没有其他节点可以调度，pod依然会被调度到这个节点上。
> - NoExecute 不同于NoSchedule以及PreferNoSchedule，后两者只在调度期间起作用，而NoExecute也会影响正在节点上运行着的pod。如果在一个节点上添加了NoExecute污点，那些在该节点上运行着的pod，如果没有容忍这个NoExecute污点，将会从这个节点去除。

#### NoSchedule污点效果

```shell
# 显示节点的污点信息
[root@k8s-master ~]# kubectl describe node k8s-master
Name:               k8s-master
Roles:              control-plane,master
Labels:             beta.kubernetes.io/arch=amd64
                    beta.kubernetes.io/os=linux
                    kubernetes.io/arch=amd64
                    kubernetes.io/hostname=k8s-master
                    kubernetes.io/os=linux
                    node-role.kubernetes.io/control-plane=
                    node-role.kubernetes.io/master=
Annotations:        flannel.alpha.coreos.com/backend-data: {"VNI":1,"VtepMAC":"d6:3d:8f:99:66:e4"}
                    flannel.alpha.coreos.com/backend-type: vxlan
                    flannel.alpha.coreos.com/kube-subnet-manager: true
                    flannel.alpha.coreos.com/public-ip: 192.168.1.170
                    kubeadm.alpha.kubernetes.io/cri-socket: /var/run/dockershim.sock
                    node.alpha.kubernetes.io/ttl: 0
                    volumes.kubernetes.io/controller-managed-attach-detach: true
CreationTimestamp:  Mon, 05 Dec 2022 14:24:24 +0800
Taints:             node-role.kubernetes.io/master:NoSchedule
...

# 给节点k8s-node1添加污点node-type=production:NoSchedule
[root@k8s-master ~]# kubectl taint node k8s-node1 node-type=production:NoSchedule
node/k8s-node1 tainted
# 查看k8s-node1污点信息
[root@k8s-master ~]# kubectl describe node k8s-node1
Name:               k8s-node1
Roles:              <none>
Labels:             beta.kubernetes.io/arch=amd64
                    beta.kubernetes.io/os=linux
                    kubernetes.io/arch=amd64
                    kubernetes.io/hostname=k8s-node1
                    kubernetes.io/os=linux
                    node-label=node1
Annotations:        flannel.alpha.coreos.com/backend-data: {"VNI":1,"VtepMAC":"0e:04:60:3f:07:e6"}
                    flannel.alpha.coreos.com/backend-type: vxlan
                    flannel.alpha.coreos.com/kube-subnet-manager: true
                    flannel.alpha.coreos.com/public-ip: 192.168.1.171
                    kubeadm.alpha.kubernetes.io/cri-socket: /var/run/dockershim.sock
                    node.alpha.kubernetes.io/ttl: 0
                    volumes.kubernetes.io/controller-managed-attach-detach: true
CreationTimestamp:  Mon, 05 Dec 2022 14:31:03 +0800
Taints:             node-type=production:NoSchedule
...
# 删除节点k8s-node1污点node-type=production:NoSchedule
[root@k8s-master ~]# kubectl taint node k8s-node1 node-type=production:NoSchedule-
node/k8s-node1 untainted
```

#### NoExecute污点效果

```shell
[root@k8s-master ~]# cat 1.yaml 
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
[root@k8s-master ~]# kubectl apply -f 1.yaml 
deployment.apps/deployment1 created
[root@k8s-master ~]# kubectl get pod -o wide
NAME                                      READY   STATUS    RESTARTS   AGE     IP            NODE        NOMINATED NODE   READINESS GATES
deployment1-66b6f8c9b7-8d9t8              1/1     Running   0          3m21s   10.244.2.77   k8s-node2   <none>           <none>
deployment1-66b6f8c9b7-f987c              1/1     Running   0          3m21s   10.244.2.76   k8s-node2   <none>           <none>
deployment1-66b6f8c9b7-n94t9              1/1     Running   0          3m21s   10.244.1.27   k8s-node1   <none>           <none>
deployment1-66b6f8c9b7-vk2bp              1/1     Running   0          3m21s   10.244.2.75   k8s-node2   <none>           <none>
deployment1-66b6f8c9b7-z9kfd              1/1     Running   0          3m21s   10.244.2.74   k8s-node2   <none>           <none>

# 标记k8s-node2 NoExecute污点
[root@k8s-master ~]# kubectl taint node k8s-node2 node-type=production:NoExecute
node/k8s-node2 tainted
# 最后所有pod被重新调度到k8s-node1
[root@k8s-master ~]# kubectl get pod -o wide
NAME                                      READY   STATUS    RESTARTS   AGE   IP            NODE        NOMINATED NODE   READINESS GATES
deployment1-66b6f8c9b7-49n8q              1/1     Running   0          67s   10.244.1.28   k8s-node1   <none>           <none>
deployment1-66b6f8c9b7-9k9sz              1/1     Running   0          67s   10.244.1.30   k8s-node1   <none>           <none>
deployment1-66b6f8c9b7-h49dc              1/1     Running   0          67s   10.244.1.29   k8s-node1   <none>           <none>
deployment1-66b6f8c9b7-n94t9              1/1     Running   0          11m   10.244.1.27   k8s-node1   <none>           <none>
deployment1-66b6f8c9b7-r45qn              1/1     Running   0          67s   10.244.1.31   k8s-node1   <none>           <none>

# 删除污点
[root@k8s-master ~]# kubectl taint node k8s-node2 node-type=production:NoExecute-
node/k8s-node2 untainted
```

### 定向调度

> 通过nodename或者nodeselector指定pod运行的节点

#### 使用nodename指定节点

```shell
[root@k8s-master ~]# cat 1.yaml 
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
   nodeName: k8s-node1 # 使用节点名称指定pod运行节点
  
# 所有节点被调度到k8s-node1上
[root@k8s-master ~]# kubectl get pod -o wide
NAME                                      READY   STATUS    RESTARTS   AGE   IP            NODE        NOMINATED NODE   READINESS GATES
deployment-665d7b748d-5t5cf               1/1     Running   0          59s   10.244.1.46   k8s-node1   <none>           <none>
deployment-665d7b748d-75lww               1/1     Running   0          59s   10.244.1.47   k8s-node1   <none>           <none>
deployment-665d7b748d-k84vr               1/1     Running   0          59s   10.244.1.48   k8s-node1   <none>           <none>
deployment-665d7b748d-x59dm               1/1     Running   0          59s   10.244.1.44   k8s-node1   <none>           <none>
deployment-665d7b748d-xn2n6               1/1     Running   0          59s   10.244.1.45   k8s-node1   <none>           <none>
```

#### 使用nodeselector指定节点标签

```shell
# 节点打标签
[root@k8s-master ~]# kubectl label node k8s-node1 node-label=node1
node/k8s-node1 labeled

[root@k8s-master ~]# cat 1.yaml 
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

# 所有pod被schedule到k8s-node1
[root@k8s-master ~]# kubectl get pod -o wide
NAME                                      READY   STATUS    RESTARTS   AGE   IP            NODE        NOMINATED NODE   READINESS GATES
deployment-9d596955c-7gd4s                1/1     Running   0          51s   10.244.1.51   k8s-node1   <none>           <none>
deployment-9d596955c-bdnsq                1/1     Running   0          51s   10.244.1.50   k8s-node1   <none>           <none>
deployment-9d596955c-qbchg                1/1     Running   0          51s   10.244.1.53   k8s-node1   <none>           <none>
deployment-9d596955c-wxj79                1/1     Running   0          51s   10.244.1.52   k8s-node1   <none>           <none>
deployment-9d596955c-xdh7p                1/1     Running   0          51s   10.244.1.49   k8s-node1   <none>           <none>

# 删除节点标签
[root@k8s-master ~]# kubectl label node k8s-node1 node-label-
node/k8s-node1 labeled
```



### 使用节点亲缘性将pod调度到特定节点上

#### 节点亲缘性(node affinity)

> 硬限制 requiredDuringSchedulingIgnoredDuringExecution，不匹配不调度，pod一直处于pending状态。
>
> 软限制 preferredDuringSchedulingIgnoredDuringExecution，不匹配也调度。

情景1（无法匹配节点，pod无法调度，一直处于pending状态）

```shell
# 因为没有节点labels包含nodeenv in xxx,yyy，所以deployment无法调度一直pending状态
[root@k8s-master ~]# cat 1.yaml 
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
[root@k8s-master ~]# kubectl get pod
NAME                                      READY   STATUS    RESTARTS   AGE
deployment-6f8cc7cffc-5jbfk               0/1     Pending   0          3m46s
deployment-6f8cc7cffc-8znwf               0/1     Pending   0          3m46s
deployment-6f8cc7cffc-mpv6b               0/1     Pending   0          3m46s
deployment-6f8cc7cffc-njv72               0/1     Pending   0          3m46s
deployment-6f8cc7cffc-qhctx               0/1     Pending   0          3m46s
```

情景2（给k8s-node2节点打上标签，成功匹配，所有pod被调度到此节点）

```shell
# 给节点k8s-node2打上标签成功使用nodeAffinity调度pod
[root@k8s-master ~]# kubectl label node k8s-node2 nodeenv=prod
node/k8s-node2 labeled

[root@k8s-master ~]# cat 1.yaml 
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
# 所有pod被成功调度到k8s-node2
[root@k8s-master ~]# kubectl get pod -o wide
NAME                                      READY   STATUS    RESTARTS   AGE     IP            NODE        NOMINATED NODE   READINESS GATES
deployment-86f7955fdb-fkg7n               1/1     Running   0          2m19s   10.244.2.93   k8s-node2   <none>           <none>
deployment-86f7955fdb-ht9jb               1/1     Running   0          2m19s   10.244.2.94   k8s-node2   <none>           <none>
deployment-86f7955fdb-k9jb9               1/1     Running   0          2m19s   10.244.2.95   k8s-node2   <none>           <none>
deployment-86f7955fdb-snpqn               1/1     Running   0          2m19s   10.244.2.91   k8s-node2   <none>           <none>
deployment-86f7955fdb-x8k8f               1/1     Running   0          2m19s   10.244.2.92   k8s-node2   <none>           <none>
```

情景3（软限制，即使不匹配节点，最终所有pod被随机调度到各个节点）

```shell
[root@k8s-master ~]# cat 1.yaml 
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
   affinity:
    nodeAffinity:
     preferredDuringSchedulingIgnoredDuringExecution:
      - weight: 1
        preference: 
         matchExpressions:
          - key: nodeenv
            operator: In
            values:
             - xxx
             - yyy
[root@k8s-master ~]# kubectl apply -f 1.yaml 
deployment.apps/deployment created
# 即使不匹配，pod也会被调度到各个节点
[root@k8s-master ~]# kubectl get pod -o wide
NAME                                      READY   STATUS    RESTARTS   AGE     IP            NODE        NOMINATED NODE   READINESS GATES
deployment-68d7784854-2wflh               1/1     Running   0          2m55s   10.244.2.96   k8s-node2   <none>           <none>
deployment-68d7784854-44gfz               1/1     Running   0          2m55s   10.244.1.54   k8s-node1   <none>           <none>
deployment-68d7784854-62cz7               1/1     Running   0          2m55s   10.244.2.97   k8s-node2   <none>           <none>
deployment-68d7784854-qgbbf               1/1     Running   0          2m55s   10.244.2.98   k8s-node2   <none>           <none>
deployment-68d7784854-xlb5l               1/1     Running   0          2m55s   10.244.1.55   k8s-node1   <none>           <none>
```

情景4（软限制，有匹配的节点标签，所有pod被调度到此节点）

```shell
[root@k8s-master ~]# kubectl label node k8s-node2 nodeenv=prod
node/k8s-node2 labeled
[root@k8s-master ~]# kubectl apply -f 1.yaml 
deployment.apps/deployment created
[root@k8s-master ~]# cat 1.yaml 
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
[root@k8s-master ~]# kubectl get pod -o wide
NAME                                      READY   STATUS    RESTARTS   AGE    IP             NODE        NOMINATED NODE   READINESS GATES
deployment-5ff5dc5845-82hgx               1/1     Running   0          118s   10.244.2.103   k8s-node2   <none>           <none>
deployment-5ff5dc5845-g5vp2               1/1     Running   0          118s   10.244.2.99    k8s-node2   <none>           <none>
deployment-5ff5dc5845-p9zp4               1/1     Running   0          118s   10.244.2.100   k8s-node2   <none>           <none>
deployment-5ff5dc5845-vn2vr               1/1     Running   0          118s   10.244.2.101   k8s-node2   <none>           <none>
deployment-5ff5dc5845-wjg9b               1/1     Running   0          118s   10.244.2.102   k8s-node2   <none>           <none>
```

#### pod亲缘性(pod affinity)

**硬限制不能匹配pod一直pending状态情景**

```shell
# 创建参考点pod，指定参考点pod被调度到k8s-node2上
[root@k8s-master ~]# cat 1.yaml 
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
   nodeName: k8s-node2
[root@k8s-master ~]# cat 2.yaml 
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
[root@k8s-master ~]# kubectl get pod
NAME                                      READY   STATUS    RESTARTS   AGE
deployment1-5df7f77687-dfvvh              1/1     Running   0          7h48m
deployment1-5df7f77687-k5hrq              1/1     Running   0          7h48m
deployment2-54cb7fd867-7vxm2              0/1     Pending   0          7h46m
deployment2-54cb7fd867-8jnsl              0/1     Pending   0          7h46m
deployment2-54cb7fd867-9h5bv              0/1     Pending   0          7h46m
deployment2-54cb7fd867-n6zl4              0/1     Pending   0          7h46m
deployment2-54cb7fd867-qfpxm              0/1     Pending   0          7h46m
```

**硬限制能够匹配pod并且成功调度**

```shell
# 创建参考pod
[root@k8s-master ~]# cat 1.yaml 
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
   nodeName: k8s-node2
[root@k8s-master ~]# cat 2.yaml 
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
[root@k8s-master ~]# kubectl get pod -o wide
NAME                                      READY   STATUS    RESTARTS   AGE     IP             NODE        NOMINATED NODE   READINESS GATES
deployment1-5df7f77687-dfvvh              1/1     Running   0          7h59m   10.244.2.121   k8s-node2   <none>           <none>
deployment1-5df7f77687-k5hrq              1/1     Running   0          7h59m   10.244.2.120   k8s-node2   <none>           <none>
deployment2-69d58d6b5b-9wtfl              1/1     Running   0          6m48s   10.244.2.129   k8s-node2   <none>           <none>
deployment2-69d58d6b5b-ld6dp              1/1     Running   0          6m48s   10.244.2.132   k8s-node2   <none>           <none>
deployment2-69d58d6b5b-r2tdt              1/1     Running   0          6m48s   10.244.2.131   k8s-node2   <none>           <none>
deployment2-69d58d6b5b-txwv8              1/1     Running   0          6m48s   10.244.2.130   k8s-node2   <none>           <none>
deployment2-69d58d6b5b-zbswt              1/1     Running   0          6m48s   10.244.2.128   k8s-node2   <none>           <none>
```

**pod非亲缘性(pod anti affinity)**

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
   podenv: dev
 template:
  metadata:
   labels:
    podenv: dev
  spec:
   containers:
    - name: nginx
      image: nginx
   nodeName: k8s-node2
[root@k8s-master ~]# cat 2.yaml 
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
# pod被调度到k8s-node1上
[root@k8s-master ~]# kubectl get pod -o wide
NAME                                      READY   STATUS    RESTARTS   AGE     IP             NODE        NOMINATED NODE   READINESS GATES
deployment1-5df7f77687-5xz8s              1/1     Running   0          2m9s    10.244.2.134   k8s-node2   <none>           <none>
deployment1-5df7f77687-66zd9              1/1     Running   0          2m9s    10.244.2.133   k8s-node2   <none>           <none>
deployment2-6f7bccdffb-fpfdh              1/1     Running   0          91s     10.244.1.62    k8s-node1   <none>           <none>
deployment2-6f7bccdffb-mlwgz              1/1     Running   0          91s     10.244.1.60    k8s-node1   <none>           <none>
deployment2-6f7bccdffb-rk5kk              1/1     Running   0          91s     10.244.1.61    k8s-node1   <none>           <none>
deployment2-6f7bccdffb-w6v4n              1/1     Running   0          91s     10.244.1.59    k8s-node1   <none>           <none>
deployment2-6f7bccdffb-whwr5              1/1     Running   0          91s     10.244.1.58    k8s-node1   <none>           <none>
```



## helm

### 安装

> 使用dcli安装helm cli

### 使用helm创建一个chart

**没有变量的helm**

```shell
# 创建mychart项目
[root@k8s-master ~]# helm create mychart
Creating mychart
# 删除mychart/templates文件夹下所有文件
[root@k8s-master ~]# cd mychart/templates/
[root@k8s-master templates]# rm -rf *
# 在mychart/templates目录下创建configmap.yaml
[root@k8s-master templates]# cat configmap.yaml 
apiVersion: v1
kind: ConfigMap
metadata:
 name: mychart-configmap1
data:
 myvalue: "hello world!"
 # 创建helm release
[root@k8s-master mychart]# helm install myconfigmap1 .
NAME: myconfigmap1
LAST DEPLOYED: Fri Dec 16 16:14:36 2022
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None
# 查看helm release
[root@k8s-master mychart]# helm list
NAME        	NAMESPACE	REVISION	UPDATED                                	STATUS  	CHART        	APP VERSION
myconfigmap1	default  	1       	2022-12-16 16:14:36.220185982 +0800 CST	deployed	mychart-0.1.0	1.16.0
# 查看configmap
[root@k8s-master mychart]# kubectl get configmap
NAME                            DATA   AGE
kube-root-ca.crt                1      11d
mychart-configmap1              1      3m30s
# 查看helm release详细信息
[root@k8s-master mychart]# helm get manifest myconfigmap1
---
# Source: mychart/templates/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: mychart-configmap1
data:
 myvalue: "hello world!"
# 删除helm release
[root@k8s-master mychart]# helm uninstall myconfigmap1
release "myconfigmap1" uninstalled
[root@k8s-master mychart]# helm list
NAME	NAMESPACE	REVISION	UPDATED	STATUS	CHART	APP VERSION
[root@k8s-master mychart]# kubectl get configmap
NAME                            DATA   AGE
kube-root-ca.crt                1      11d
```

**带变量的helm**

```shell
# 把上面 没有变量的helm 修改 templates/configmap.yaml和values.yaml
[root@k8s-master mychart]# cat templates/configmap.yaml 
apiVersion: v1
kind: ConfigMap
metadata:
 name: {{ .Release.Name }}-configmap1
data:
 myvalue: {{ .Values.MY_VALUE }}
[root@k8s-master mychart]# cat values.yaml 
MY_VALUE: "hello world!!"
[root@k8s-master mychart]# helm install myconfigmap2 .
NAME: myconfigmap2
LAST DEPLOYED: Fri Dec 16 16:45:23 2022
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None
[root@k8s-master mychart]# helm list
NAME        	NAMESPACE	REVISION	UPDATED                                	STATUS  	CHART        	APP VERSION
myconfigmap2	default  	1       	2022-12-16 16:45:23.180735938 +0800 CST	deployed	mychart-0.1.0	1.16.0     
[root@k8s-master mychart]# kubectl get configmap
NAME                            DATA   AGE
kube-root-ca.crt                1      11d
myconfigmap2-configmap1         1      14s
[root@k8s-master mychart]# helm get manifest myconfigmap2
---
# Source: mychart/templates/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap2-configmap1
data:
 myvalue: hello world!!

[root@k8s-master mychart]# helm uninstall myconfigmap2
release "myconfigmap2" uninstalled
[root@k8s-master mychart]# helm list
NAME	NAMESPACE	REVISION	UPDATED	STATUS	CHART	APP VERSION
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

#### $(dollar)变量

> 这个变量在用在range内，因为在range内点号指代的是当前item，为了避免冲突使用$代替。
>
> [链接1](https://helm.sh/docs/chart_template_guide/variables/)

### 常用命令

#### 查看helm版本

```shell
[root@k8s-master mychart]# helm version
version.BuildInfo{Version:"v3.10.3", GitCommit:"835b7334cfe2e5e27870ab3ed4135f136eecc704", GitTreeState:"clean", GoVersion:"go1.18.9"}

```

#### 仓库管理

```shell
# 添加微软helm仓库
[root@k8s-master mychart]# helm repo add stable  http://mirror.azure.cn/kubernetes/charts/
"stable" has been added to your repositories

# 列出helm仓库
[root@k8s-master mychart]# helm repo list
NAME  	URL                                      
stable	http://mirror.azure.cn/kubernetes/charts/

# 把远程仓库更新到本地
[root@k8s-master mychart]# helm repo update
Hang tight while we grab the latest from your chart repositories...
...Successfully got an update from the "stable" chart repository
Update Complete. ?Happy Helming!?

# 删除仓库
[root@k8s-master mychart]# helm repo list
NAME   	URL                                      
stable 	http://mirror.azure.cn/kubernetes/charts/
stable2	http://mirror.azure.cn/kubernetes/charts/
[root@k8s-master mychart]# helm repo remove stable2
"stable2" has been removed from your repositories
[root@k8s-master mychart]# helm repo list
NAME  	URL                                      
stable	http://mirror.azure.cn/kubernetes/charts/

# 在远程helm仓库中搜索tomcat包
[root@k8s-master ~]# helm search repo tomcat
NAME         	CHART VERSION	APP VERSION	DESCRIPTION                                       
stable/tomcat	0.4.3        	7.0        	DEPRECATED - Deploy a basic tomcat application ...
```

#### chart包管理

```shell
# 创建chart包
[root@k8s-master ~]# helm create mychart-test
Creating mychart-test

# 显示stable/tomcat包信息
[root@k8s-master ~]# helm show chart stable/tomcat
apiVersion: v1
appVersion: "7.0"
deprecated: true
description: DEPRECATED - Deploy a basic tomcat application server with sidecar as
  web archive container
home: https://github.com/yahavb
icon: http://tomcat.apache.org/res/images/tomcat.png
name: tomcat
version: 0.4.3
# 显示stable/tomcat values信息
[root@k8s-master ~]# helm show values stable/tomcat
# Default values for the chart.
# This is a YAML-formatted file.
# Declare variables to be passed into your templates.
replicaCount: 1

image:
  webarchive:
    repository: ananwaresystems/webarchive
    tag: "1.0"
  tomcat:
    repository: tomcat
    tag: "7.0"
  pullPolicy: IfNotPresent
  pullSecrets: []

deploy:
  directory: /usr/local/tomcat/webapps

service:
  name: http
  type: LoadBalancer
  externalPort: 80
  internalPort: 8080

hostPort: 8009

ingress:
  enabled: false
  annotations: {}
    # kubernetes.io/ingress.class: nginx
    # kubernetes.io/tls-acme: "true"
  path: /
  hosts:
    - chart-example.local
  tls: []
  #  - secretName: chart-example-tls
  #    hosts:
  #      - chart-example.local

env: []
  # - name: env
  #   value: test

extraVolumes: []
  # - name: extra
  #   emptyDir: {}

extraVolumeMounts: []
  # - name: extra
  #   mountPath: /usr/local/tomcat/webapps/app
  #   readOnly: true

extraInitContainers: []
  # - name: do-something
  #   image: busybox
  #   command: ['do', 'something']

readinessProbe:
  path: "/sample"
  initialDelaySeconds: 60
  periodSeconds: 30
  failureThreshold: 6
  timeoutSeconds: 5
livenessProbe:
  path: "/sample"
  initialDelaySeconds: 60
  periodSeconds: 30
  failureThreshold: 6
  timeoutSeconds: 5

resources: {}
#  limits:
#    cpu: 100m
#    memory: 256Mi
#  requests:
#    cpu: 100m
#    memory: 256Mi

nodeSelector: {}

tolerations: []

affinity: {}

# 拉取helm chart包，--untar解压
[root@k8s-master ~]# helm pull stable/tomcat --version 0.4.3
[root@k8s-master ~]# helm pull stable/tomcat --version 0.4.3 --untar
[root@k8s-master ~]# ll | grep tomcat
drwxr-xr-x. 3 root root    77 Dec 16 20:01 tomcat
-rw-r--r--. 1 root root  4241 Dec 16 20:00 tomcat-0.4.3.tgz
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



### 程序流程控制语句

#### ifelse语句

```shell
[root@k8s-master mychart]# cat templates/configmap.yaml 
apiVersion: v1
kind: ConfigMap
metadata:
 name: {{ .Release.Name }}-configmap1
 namespace: {{ .Release.Namespace }}
data:
 {{- if .Values.ingress.enabled }}
 ingress: "配置ingress"
 {{- else }}
 ingress: "不配置ingress"
 {{- end }}

 {{- if eq .Values.Person.name "dexter" }}
 welcome: "你好Dexter!"
 {{- else }}
 welcome: "你好谁谁!!"
 {{- end }}
 
[root@k8s-master mychart]# cat values.yaml 
Person:
 name: "dexter1"
ingress:
 enabled: true
 
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

```shell
[root@k8s-master mychart]# cat templates/configmap.yaml 
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
[root@k8s-master mychart]# cat values.yaml 
address:
 - beijing
 - shanghai
 - guangzhou

[root@k8s-master mychart]# helm install myconfigmap1 . --debug --dry-run
install.go:192: [debug] Original chart version: ""
install.go:209: [debug] CHART PATH: /root/mychart

NAME: myconfigmap1
LAST DEPLOYED: Mon Dec 19 12:37:49 2022
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
 address: |-
  - Beijing
  - Shanghai
  - Guangzhou
```

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

