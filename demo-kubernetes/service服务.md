# `service`服务

是一组同类`pod`对外访问接口，借助`service`，应用可以方便地实现服务发现和负载均衡

主要的流量负载组建分别为`service`（4层路由）和`ingress`（7层路由）



## 使用kubectl expose创建服务

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



## service通过endpoints转发请求到pods中

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



## 使用yaml创建和删除ClusterIP服务

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



## 配置服务上的会话亲和性sessionAffinity

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



## 服务使用pod的命名端口

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



## 通过dns发现服务

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



## 创建外部服务指定endpoint ip列表

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



## 创建外部服务指定ExternalName

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





## 使用NodePort类型的服务

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

## 通过Google Kubernetes Engine的LoadBalancer将服务暴露出来

> todo 在GKE上实现

## 通过Ingress暴露服务

> todo 没有在本地k8s集群中成功启动ingress-controller

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



## 服务就绪探针

> NOTE：参考上面的服务就绪探针演示



## `headless`（无头服务）

headless服务是通过service的dns解析访问相应的pod ip地址，例如下面例子：在busybox pod中通过headless-service无头服务名称就能够访问两个nginx pod endpoints。
尽管headless服务看起来可能与常规的服务不同，但在客户的视角上他们并无不同。即使使用headlesss服务，客户也可以通过连接的服务的DNS名称来连接到pod上，就像常规服务一样。但是对于headless服务，由于DNS返回了pod的ip，客户端直接连接到该pod，而不是通过服务代理。

jmeter cluster场景master启动测试时，能够借助headless服务获取所有slave ip地址。

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

