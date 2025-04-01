# `pod`控制器

> `pod`控制，用于保证`pod`运行状态符合预期状态

## 初试`pod`控制器

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



## `ReplicationController`控制器

> `ReplicationController`已经被`ReplicaSet`取代。

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



## `ReplicaSet(RS)`

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



## `Deployment(deploy)`

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



## `Horizontal Pod Autoscaler(HPA)`

> 根据pod负载的变化自动调整pod的数量



## `DaemonSet(DS)`

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



## `Job`

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





## `CronJob(CJ)`

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



## `StatefulSet`

> [kubernetes——StatefulSet详解](https://www.jianshu.com/p/03cd2f2dc427)

RC、Deployment、DaemonSet都是面向无状态的服务，它们所管理的Pod的IP、名字，启停顺序等都是随机的，而StatefulSet是什么？顾名思义，有状态的集合，管理所有有状态的服务，比如MySQL、MongoDB集群等。
StatefulSet本质上是Deployment的一种变体，在v1.9版本中已成为GA版本，它为了解决有状态服务的问题，它所管理的Pod拥有固定的Pod名称，启停顺序，在StatefulSet中，Pod名字称为网络标识(hostname)，还必须要用到共享存储。

在Deployment中，与之对应的服务是service，而在StatefulSet中与之对应的headless service，headless service，即无头服务，与service的区别就是它没有Cluster IP，解析它的名称时将返回该Headless Service对应的全部Pod的Endpoint列表。

一个statefulset创建的每个pod都有一个从零开始的顺序索引，这个会体现在pod的名称和主机名上，同样还是体现在pod对应的固定存储上。这些pod的名称则是可以预知的，因为他是由statefulset的名称加该实例的顺序索引值组成的。

一个statefulset通常要求你创建一个用来记录每个pod网络标记的headless service。通过这个service，每个pod将拥有独立的DNS记录，这样集群里他的伙伴或者客户端可以通过主机名方便地找到他。比如说，一个属于default命名空间，名为foo的控制服务，他的一个pod名称为A-0，那么可以通过下面的完整域名来访问他: a-0.foo.default.svc.cluster.local。而在Replicaset中这样是行不通的。另外，也可以通过DNS服务，查找域名foo.default.svc.cluster.local对应的所有srv记录，获取一个statefulset中所有pod的名称。

`statefulset`中每个有状态实例都有其对应的专属存储。

>注意：下面实验需要先参考`storageclass`章节创建`storageclass` <a href="/kubernetes/volume数据存储.html#使用storageclass-存储类别-实现持久卷的动态卷配置" target="_blank">链接</a>

```yaml
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
docker build --tag 192.168.235.138:80/library/demo-k8s-statefulset .

# 推送镜像
docker push 192.168.235.138:80/library/demo-k8s-statefulset

# 测试镜像
docker run --rm --name=demo -p 8080:8080 192.168.235.138:80/library/demo-k8s-statefulset
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
        image: 192.168.235.138:80/library/demo-k8s-statefulset
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

