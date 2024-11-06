# `volume`数据存储

## 简单存储

### EmptyDir

> pod创建时会自动创建一个空的目录，无需指定宿主机目录，因为k8s系统会自动分配一个目录，**在pod销毁时，emptydir中的数据也会被永久删除。**
> 使用emptydir实现pod内的容器共享数据

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



### GitRepo卷

> NOTE: 暂时没有需要使用这种类型的卷，所以不研究。



### HostPath

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



### NFS

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



## 高级存储

### pv和pvc

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



### 使用`storageclass(存储类别)`实现持久卷的动态卷配置

> `https://github.com/kubernetes-sigs/nfs-subdir-external-provisioner/blob/master/deploy/test-claim.yaml
> https://zahui.fan/posts/179eb842/`

卷置备程序会根据`pvc`自动创建`pv`，不需要集群管理员预先创建`pv`，集群管理员只需要定义一个或者多个`StorageClass`对象。



#### 部署`nfs`存储置备服务

搭建`nfs`服务器，请参考 <a href="/linux使用/搭建nfs服务器.html" target="_blank">链接</a>

配置`nfs`卷置备服务`deployment.yaml`内容如下：

```yaml
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
  
# 用于创建nfs置备程序的pod
---
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
              value: 192.168.235.191
            - name: NFS_PATH
              value: /data
      volumes:
        - name: nfs-client-root
          nfs:
            server: 192.168.235.191
            path: /data
            
# 创建storageclass
---
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

部署`nfs`存储置备服务

```bash
kubectl create -f deployment.yaml
```



#### 测试`nfs`存储置备服务是否正常

指定`storageclass`创建`pvc`，文件`test-claim.yaml`内容如下：

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
      storage: 1Gi
```

创建`pvc`会自动创建`pv`，`nfs`置备程序会自动在`nfs`服务器`/data`目录下创建`pv`对应的目录，在删除`pvc`时候也同时会自动删除此`pv`目录

```bash
kubectl create -f test-claim.yaml 
```

查看`pv`和`pvc`列表

```bash
kubectl get pv
kubectl get pvc
```

删除`pvc`会自动删除关联的`pv`

```bash
kubectl delete -f test-claim.yaml 
```

查看`pv`和`pvc`列表

```bash
kubectl get pvc
kubectl get pv
```



