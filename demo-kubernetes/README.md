# `kubernetes(k8s)`



## Kubernetes Google Container Registry国内镜像替换

> https://kubernetes.feisky.xyz/appendix/mirrors



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

