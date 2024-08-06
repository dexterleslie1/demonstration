# `service`服务

>注意：
>
>- 不同`pod`之间不能使用`pod`的名称直接通讯，需要借助`service`在不同`pod`之间通讯。
>- `statefulset`才能够使用`serviceName`指定无头服务，`deployment`不支持使用`serviceName`指定无头服务，需要使用标签选择器绑定`service`。

是一组同类`pod`对外访问接口，借助`service`，应用可以方便地实现服务发现和负载均衡

主要的流量负载组建分别为`service`（4层路由）和`ingress`（7层路由）



## 服务类型有哪些呢？

在Kubernetes（k8s）中，Service服务类型主要包括以下几种：

1. ClusterIP

- **描述**：ClusterIP是Kubernetes的默认Service类型。它会为Service分配一个集群内部可访问的虚拟IP地址（VIP），该地址在集群内部是唯一的，并且只能在集群内部访问。
- 特点：
  - 虚拟IP由Kubernetes系统动态分配。
  - 仅支持集群内部访问。
  - 可以分为普通Service和Headless Service两种。普通Service会分配虚拟IP并通过kube-proxy做反向代理和负载均衡；Headless Service则不会分配虚拟IP，主要通过DNS提供稳定的网络ID进行访问，常用于StatefulSet等场景。

2. NodePort

- **描述**：NodePort类型会在每个节点上打开一个特定的端口（nodePort），并将该端口映射到Service的端口上。这样，外部用户就可以通过`<NodeIP>:<NodePort>`的方式来访问Service。
- 特点：
  - 提供了从集群外部访问Service的方式。
  - 节点上开放的端口范围是30000-32767。
  - 需要配合ClusterIP一起使用。

3. LoadBalancer

- **描述**：LoadBalancer类型在NodePort的基础上，向底层云平台申请一个负载均衡器（如AWS的ELB、Azure的LoadBalancer等），并将请求通过该负载均衡器转发到集群的NodePort上。
- 特点：
  - 提供了更高层次的外部访问方式，适用于需要高可用性和负载均衡的场景。
  - 依赖于底层云平台的支持。
  - 在NodePort的基础上增加了负载均衡器的功能。

4. ExternalName

- **描述**：ExternalName类型通过返回CNAME和其值（即外部服务的地址），将服务映射到externalName字段的内容。这种类型主要用于将集群外部的服务引入到集群内部来，而不需要在集群内部创建任何代理。
- 特点：
  - 不需要分配虚拟IP或节点端口。
  - 通过DNS解析来实现服务的映射。
  - 仅适用于Kubernetes 1.7或更高版本的kube-dns。

总结

Kubernetes的Service服务类型主要包括ClusterIP、NodePort、LoadBalancer和ExternalName四种。每种类型都有其特定的使用场景和特点，用户可以根据实际需求选择合适的类型来暴露和访问服务。



## 同一个`pod`中的容器之间通讯

同一个`pod`中的容器之间可以通过`localhost`通讯。

```bash
# 1.yaml内容如下：
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
 
# 启动pod
kubectl apply -f 1.yaml

# 连接curl容器，使用curl通过localhost测试nginx服务
kubectl exec -it test-pod -c curl /bin/sh
curl localhost

# 删除pod
kubectl delete -f 1.yaml
```



## `ClusterIP`类型服务

>注意：当在`Kubernetes`中创建一个`Service`而不显式指定其类型（`type`）时，`Service`的默认类型是`ClusterIP`。

通过集群内分配的虚拟机`ip`访问服务。

### 使用`kubectl expose`创建服务

> 不使用`yaml`方式，使用`cli`方式创建服务

```shell
# 创建pod，--port为pod的端口
kubectl run nginx --image=nginx --port=80

# 创建ClusterIP类型的服务，expose名为nginx的pod，服务名称为nginx，服务端口80，--target-port为pod的端口
kubectl expose pod nginx --name=nginx --port=80 --target-port=80 --type=ClusterIP

# 查看服务列表
kubectl get service

# 访问服务ip即访问pod nginx，ip地址为上面服务列表中的服务ip
curl 10.1.57.17

# 删除nginx服务
kubectl delete service nginx
# 删除 pod
kubectl delete pod nginx
```



### `service`通过`endpoints`转发请求到`pods`中

服务并不是和`pod`直接相连的。相反，有一种资源介于两者之间，它就是`endpoint`资源。

`endpoint`资源就是暴露一个服务的`ip`地址和端口的列表，可以通过命令`kubectl get endpoints`查询`endpoint`资源列表。

尽管在服务的`spec`中定义了`pod`选择器，但在重定向传入连接时不会直接使用它。相反，选择器用于构建`ip`和端口列表，然后存储在`endpoint`资源中。当客户端连接到服务时，**服务代理**选择这些`ip`和端口对中的一个，并将传入连接重定向到在该位置监听的服务器。

参考 <a href="/kubernetes/实验用的镜像.html#nodejs镜像" target="_blank">链接</a> 准备`registry.cn-hangzhou.aliyuncs.com/future-public/demo-k8s-nodejs`镜像

```bash
# 1.yaml内容如下:
---
apiVersion: v1
kind: Service
metadata:
 name: kubia
spec:
 sessionAffinity: None
 selector:
  app: kubia
 ports:
  # 服务的端口
  - port: 8080
    # pod的端口
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
   labels:
    app: kubia
  spec:
   containers:
    - name: kubia
      image: registry.cn-hangzhou.aliyuncs.com/future-public/demo-k8s-nodejs
    
# 创建资源
kubectl create -f 1.yaml

# 查看service对应的endpoints信息
kubectl describe service kubia

# 查看指定endpoints的信息，这些endpoint ip和端口对应pod的ip和端口
kubectl get endpoints kubia

# 销毁资源
kubectl delete -f 1.yaml
```



### 使用`yaml`创建和删除`ClusterIP`服务

```shell
# 创建pod
kubectl run nginx --image=registry.cn-hangzhou.aliyuncs.com/future-public/demo-k8s-nodejs

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



### 配置服务上的会话亲和性`sessionAffinity`

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
      image: registry.cn-hangzhou.aliyuncs.com/future-public/demo-k8s-nodejs
  
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

# 服务随机分配pod
curl 10.1.103.203
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

# 服务分配固定的pod
curl 10.1.79.183
curl 10.1.79.183
curl 10.1.79.183
curl 10.1.79.183

# 删除服务
kubectl delete -f 2.yaml

# 删除deployment
kubectl delete -f 1.yaml
```



### 服务使用`pod`的命名端口

为什么要采用命名端口方式呢？最大好处就是即使更换端口也无须更改服务`spec`。如果你采用了命名的端口，仅仅需要做的就是改变`pod`中`spec`的端口号。在你的`pod`向新端口更新时，根据`pod`收到的连接(`8080`端口在旧的`pod`上、`80`端口在新的`pod`上)，用户连接将会转发到对应的端口上。

```bash
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
      image: registry.cn-hangzhou.aliyuncs.com/future-public/demo-k8s-nodejs
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



### 通过`dns`发现服务

```shell
# 1.yaml内容如下:
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
   labels:
    app: kubia
  spec:
   containers:
    - name: kubia
      image: registry.cn-hangzhou.aliyuncs.com/future-public/demo-k8s-nodejs

---
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
      image: curlimages/curl
      command: ["/bin/sh", "-c", "sleep 7200"]

---
# 创建服务myservice1暴露deployment1服务
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

# 查看pod列表
kubectl get pod

# 进入kubia-client测试pod，使用curl myservice1和curl myservice1.default.svc.cluster.local测试服务是否正常
kubectl exec -it deployment2-6848fcdd67-2rt9g /bin/sh
# ping服务时，会解析到服务的ClusterIP
/ # ping myservice1
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



### 创建外部服务指定`endpoint ip`列表

```bash
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
      image: registry.cn-hangzhou.aliyuncs.com/future-public/demo-k8s-nodejs
      
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

# 创建endpoints资源，注意：其中10.244.1.3、10.244.2.3为pod的地址
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



### `headless`（无头服务）

`headless`服务是通过`service`的`dns`解析访问相应的`pod ip`地址，例如下面例子：在`busybox pod`中通过`headless-service`无头服务名称就能够访问两个`nginx pod endpoints`。

尽管`headless`服务看起来可能与常规的服务不同，但在客户的视角上他们并无不同。即使使用`headlesss`服务，客户也可以通过连接的服务的`DNS`名称来连接到`pod`上，就像常规服务一样。但是对于`headless`服务，由于`DNS`返回了`pod`的`ip`，客户端直接连接到该`pod`，而不是通过服务代理。

`jmeter cluster`场景`master`启动测试时，能够借助`headless`服务获取所有`slave ip`地址。

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

# 查看服务，注意：无头服务没有ClusterIP地址
kubectl get service

# 进入busybox容器测试headless service
kubectl exec -it headless-deployment-busybox-b9db9bbb-vsrvm /bin/sh
kubectl exec [POD] [COMMAND] is DEPRECATED and will be removed in a future version. Use kubectl exec [POD] -- [COMMAND] instead.
# ping无头服务时，会解析到pod的ip地址
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



### 什么时候用无头服务？什么时候用非无头服务？

在Kubernetes（K8s）中，选择使用无头服务（Headless Service）还是非无头服务（即传统的带ClusterIP的Service），主要取决于具体的应用场景和需求。以下是一些指导原则：

无头服务（Headless Service）的使用场景

1. 需要直接访问Pod的场景：
   - 当应用需要直接与Pod通信，而不是通过负载均衡器间接访问时，无头服务非常适用。这通常用于需要保持会话状态或需要特定实例间通信的应用，如分布式数据库（如Redis集群、MySQL集群）、消息队列等。
2. 自定义负载均衡和服务发现：
   - 无头服务允许客户端通过DNS解析获取Pod列表，进而实现自定义的负载均衡算法或服务发现逻辑。这对于需要精细控制服务访问的应用非常有用。
3. 有状态应用：
   - 对于StatefulSet部署的有状态应用，无头服务能够确保客户端能够直接访问到特定的Pod实例，这对于需要维护状态的应用至关重要。

非无头服务（带ClusterIP的Service）的使用场景

1. 需要负载均衡的场景：
   - 当应用需要自动负载均衡以分散请求到多个Pod时，使用传统的带ClusterIP的Service是更合适的选择。Kubernetes会自动处理这些请求，将它们分发到后端的Pod上。
2. 无需直接访问Pod的场景：
   - 如果应用不需要直接与Pod通信，而是通过服务名称来访问，那么使用带ClusterIP的Service可以隐藏Pod的详细信息，简化访问方式。
3. 对外暴露服务：
   - 如果需要将服务暴露给集群外部的用户或应用，可以通过设置Service的type为NodePort、LoadBalancer或Ingress来实现。这些类型的Service都需要一个ClusterIP来在集群内部进行路由。

总结

- **无头服务**适用于需要直接访问Pod、实现自定义负载均衡算法或服务发现逻辑、以及部署有状态应用的场景。
- **非无头服务**（带ClusterIP的Service）适用于需要自动负载均衡、隐藏Pod详细信息、以及将服务暴露给集群外部用户的场景。

在选择使用哪种类型的服务时，需要根据具体的应用需求和场景来做出决策。



## `ExternalName`类型服务

### 创建外部服务指定`ExternalName`

`ExternalName`服务仅在`DNS`级别实施，为服务创建了简单的`CNAME DNS`记录。因此，连接到服务的客户端将直接连接到外部服务，完全绕过服务代理。出于这个原因，这些类型的服务甚至不会获取集群`ip`。

```bash
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
      image: curlimages/curl
      command: ["/bin/sh", "-c", "sleep 7200"]

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
kubectl exec -it deployment1-68bd78b7-zgx7j /bin/sh
curl kubia

# 销毁资源
kubectl delete -f 1.yaml
kubectl delete -f 2.yaml
```



## `NodePort`类型服务

### 使用`NodePort`类型的服务

当配置`externalTrafficPolicy: Local`时：

- 请求只转发到当前节点的`pod`中，不会转发到其他节点的`pod`上。
- 因为接收连接的节点和托管目标`pod`的节点之间没有额外的跳跃(不执行`SNAT`)，所有客户端`ip`会被保留。

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
      image: registry.cn-hangzhou.aliyuncs.com/future-public/demo-k8s-nodejs

# 2.yaml NodePort服务内容如下:
apiVersion: v1
kind: Service
metadata:
 name: myservice1
spec:
 # 注意：请求只转发到当前节点的pod中，不会转发到其他节点的pod上
 # 注意：因为接收连接的节点和托管目标pod的节点之间没有额外的跳跃(不执行SNAT)，所有客户端ip会被保留
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



## 通过`Google Kubernetes Engine`的`LoadBalancer`将服务暴露出来

> `todo`在`GKE`上实现



## 通过`Ingress`暴露服务

> `todo`没有在本地k8s集群中成功启动`ingress-controller`

```bash
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
