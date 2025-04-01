# `volume`数据存储



## `todo`

- 是否可以通过`volumes csi driver`配置`nfs`卷`volumes`>`csi`>`nfs`配置
- `https://github.com/kubernetes-csi/csi-driver-nfs`动态置备
- `csi`架构
- 参考`https://kubernetes.io/docs/concepts/storage/persistent-volumes/`重新整理存储文档



## 简单存储

### `EmptyDir`

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



### `GitRepo`卷

> 注意：暂时没有需要使用这种类型的卷，所以不研究。



### `HostPath`

> `HostPath`存储不会随着`pod`销毁而被删除。但是`HostPath`不能用于跨节点的数据持久化。

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



### `NFS`



#### 不通过`pv`和`pvc`直接使用`nfs`存储

注意：下面实验会在`nfs`服务器创建`/data/demo-nfs/sub1`目录；下面实验不能指定`nfs mount`参数。

`1.yaml`内容如下：

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
    server: 192.168.235.191
    path: /data
```

创建`pod`

```bash
kubectl apply -f 1.yaml
```

查询`pod`

```bash
kubectl get pod -o wide
```

请求`nginx`产生日志

```bash
curl 10.244.2.58
```

打印`pod`日志

```bash
kubectl logs -f pod1 -c busybox
```



#### 通过`pv`和`pvc`使用`nfs`存储

注意：下面实验会在`nfs`服务器创建`/data/demo-nfs/sub1`目录；下面实验支持指定`nfs mount`参数。

`1.yaml`内容如下：

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
   persistentVolumeClaim:
    claimName: pvc1
    readOnly: false
---
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
 name: pvc1
spec:
 volumeName: pv1
 accessModes:
 - ReadWriteMany
 resources:
  requests:
   storage: 1Gi
---
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
 mountOptions:
  - vers=3
  - nolock
  - proto=tcp
  - rsize=1048576
  - wsize=1048576
  - timeo=600
  - hard
  - retrans=2
  # 阿里云nfs参数
  #- noresvport
 nfs:
  path: /data
  server: 192.168.235.191

```

创建`pod`

```bash
kubectl apply -f 1.yaml
```

查询`pod`

```bash
kubectl get pod -o wide
```

请求`nginx`产生日志

```bash
curl 10.244.2.58
```

打印`pod`日志

```bash
kubectl logs -f pod1 -c busybox
```



## 高级存储



### `pv`和`pvc`

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



### `storageclass`实现基于`nfs`动态置备

卷置备程序会根据`pvc`自动创建`pv`，不需要集群管理员预先创建`pv`，集群管理员只需要定义一个或者多个`StorageClass`对象。



#### 基于`nfs-subdir-external-provisioner`

> `https://github.com/kubernetes-sigs/nfs-subdir-external-provisioner/blob/master/deploy/test-claim.yaml`
>
> `https://zahui.fan/posts/179eb842/`



**部署`nfs`存储置备服务**

注意：

- 声明`pvc`后，`nfs`置备程序会在`nfs`服务器创建名为`/data/{namespace}-{pvcname}-{pvname}`的子目录。例如：`/data/default-test-claim-pvc-b7c2c473-d3f3-4d6f-9b2d-f791d283b4c4`
- 阿里云主机需要安装`nfs`客户端，否则`nfs provisioner`无法`mount`，安装`nfs`客户端命令`sudo yum install nfs-utils`

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
mountOptions:
  - vers=3
  - nolock
  - proto=tcp
  - rsize=1048576
  - wsize=1048576
  - timeo=600
  - hard
  - retrans=2
  # 阿里云nfs参数
  #- noresvport
```

部署`nfs`存储置备服务

```bash
kubectl create -f deployment.yaml
```



**测试`nfs`存储置备服务是否正常**

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



#### 基于`csi-driver-nfs`

>`https://github.com/kubernetes-csi/csi-driver-nfs`

注意：因为镜像位于`registry.k8s.io`仓库中，所以配置`csi-driver-nfs`需要配置`docker`使用`proxy`才能成功部署服务。

`deployment.yaml`内容如下（下面的配置只需要修改`nfs`服务器地址和`nfs`挂载目录即可）：

```yaml
# driverinfo
apiVersion: storage.k8s.io/v1
kind: CSIDriver
metadata:
  name: nfs.csi.k8s.io
spec:
  attachRequired: false
  volumeLifecycleModes:
    - Persistent
  fsGroupPolicy: File

# rbac
---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: csi-nfs-controller-sa
  namespace: kube-system
---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: csi-nfs-node-sa
  namespace: kube-system
---

kind: ClusterRole
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: nfs-external-provisioner-role
rules:
  - apiGroups: [""]
    resources: ["persistentvolumes"]
    verbs: ["get", "list", "watch", "create", "patch", "delete"]
  - apiGroups: [""]
    resources: ["persistentvolumeclaims"]
    verbs: ["get", "list", "watch", "update"]
  - apiGroups: ["storage.k8s.io"]
    resources: ["storageclasses"]
    verbs: ["get", "list", "watch"]
  - apiGroups: ["snapshot.storage.k8s.io"]
    resources: ["volumesnapshotclasses", "volumesnapshots"]
    verbs: ["get", "list", "watch"]
  - apiGroups: ["snapshot.storage.k8s.io"]
    resources: ["volumesnapshotcontents"]
    verbs: ["get", "list", "watch", "update", "patch"]
  - apiGroups: ["snapshot.storage.k8s.io"]
    resources: ["volumesnapshotcontents/status"]
    verbs: ["get", "update", "patch"]
  - apiGroups: [""]
    resources: ["events"]
    verbs: ["get", "list", "watch", "create", "update", "patch"]
  - apiGroups: ["storage.k8s.io"]
    resources: ["csinodes"]
    verbs: ["get", "list", "watch"]
  - apiGroups: [""]
    resources: ["nodes"]
    verbs: ["get", "list", "watch"]
  - apiGroups: ["coordination.k8s.io"]
    resources: ["leases"]
    verbs: ["get", "list", "watch", "create", "update", "patch"]
  - apiGroups: [""]
    resources: ["secrets"]
    verbs: ["get"]
---

kind: ClusterRoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: nfs-csi-provisioner-binding
subjects:
  - kind: ServiceAccount
    name: csi-nfs-controller-sa
    namespace: kube-system
roleRef:
  kind: ClusterRole
  name: nfs-external-provisioner-role
  apiGroup: rbac.authorization.k8s.io

# controller
---
kind: Deployment
apiVersion: apps/v1
metadata:
  name: csi-nfs-controller
  namespace: kube-system
spec:
  replicas: 1
  selector:
    matchLabels:
      app: csi-nfs-controller
  template:
    metadata:
      labels:
        app: csi-nfs-controller
    spec:
      hostNetwork: true  # controller also needs to mount nfs to create dir
      dnsPolicy: ClusterFirstWithHostNet  # available values: Default, ClusterFirstWithHostNet, ClusterFirst
      serviceAccountName: csi-nfs-controller-sa
      nodeSelector:
        kubernetes.io/os: linux  # add "kubernetes.io/role: master" to run controller on master node
      priorityClassName: system-cluster-critical
      securityContext:
        seccompProfile:
          type: RuntimeDefault
      tolerations:
        - key: "node-role.kubernetes.io/master"
          operator: "Exists"
          effect: "NoSchedule"
        - key: "node-role.kubernetes.io/controlplane"
          operator: "Exists"
          effect: "NoSchedule"
        - key: "node-role.kubernetes.io/control-plane"
          operator: "Exists"
          effect: "NoSchedule"
      containers:
        - name: csi-provisioner
          image: registry.k8s.io/sig-storage/csi-provisioner:v5.0.2
          args:
            - "-v=2"
            - "--csi-address=$(ADDRESS)"
            - "--leader-election"
            - "--leader-election-namespace=kube-system"
            - "--extra-create-metadata=true"
            - "--feature-gates=HonorPVReclaimPolicy=true"
            - "--timeout=1200s"
          env:
            - name: ADDRESS
              value: /csi/csi.sock
          volumeMounts:
            - mountPath: /csi
              name: socket-dir
          resources:
            limits:
              memory: 400Mi
            requests:
              cpu: 10m
              memory: 20Mi
          securityContext:
            capabilities:
              drop:
                - ALL
        - name: csi-snapshotter
          image: registry.k8s.io/sig-storage/csi-snapshotter:v8.0.1
          args:
            - "--v=2"
            - "--csi-address=$(ADDRESS)"
            - "--leader-election-namespace=kube-system"
            - "--leader-election"
            - "--timeout=1200s"
          env:
            - name: ADDRESS
              value: /csi/csi.sock
          imagePullPolicy: IfNotPresent
          volumeMounts:
            - name: socket-dir
              mountPath: /csi
          resources:
            limits:
              memory: 200Mi
            requests:
              cpu: 10m
              memory: 20Mi
          securityContext:
            capabilities:
              drop:
                - ALL
        - name: liveness-probe
          image: registry.k8s.io/sig-storage/livenessprobe:v2.13.1
          args:
            - --csi-address=/csi/csi.sock
            - --probe-timeout=3s
            - --http-endpoint=localhost:29652
            - --v=2
          volumeMounts:
            - name: socket-dir
              mountPath: /csi
          resources:
            limits:
              memory: 100Mi
            requests:
              cpu: 10m
              memory: 20Mi
          securityContext:
            capabilities:
              drop:
                - ALL
        - name: nfs
          image: registry.k8s.io/sig-storage/nfsplugin:v4.9.0
          securityContext:
            privileged: true
            capabilities:
              add: ["SYS_ADMIN"]
              drop:
                - ALL
            allowPrivilegeEscalation: true
          imagePullPolicy: IfNotPresent
          args:
            - "-v=5"
            - "--nodeid=$(NODE_ID)"
            - "--endpoint=$(CSI_ENDPOINT)"
          env:
            - name: NODE_ID
              valueFrom:
                fieldRef:
                  fieldPath: spec.nodeName
            - name: CSI_ENDPOINT
              value: unix:///csi/csi.sock
          livenessProbe:
            failureThreshold: 5
            httpGet:
              host: localhost
              path: /healthz
              port: 29652
            initialDelaySeconds: 30
            timeoutSeconds: 10
            periodSeconds: 30
          volumeMounts:
            - name: pods-mount-dir
              mountPath: /var/lib/kubelet/pods
              mountPropagation: "Bidirectional"
            - mountPath: /csi
              name: socket-dir
          resources:
            limits:
              memory: 200Mi
            requests:
              cpu: 10m
              memory: 20Mi
      volumes:
        - name: pods-mount-dir
          hostPath:
            path: /var/lib/kubelet/pods
            type: Directory
        - name: socket-dir
          emptyDir: {}

# node
---
kind: DaemonSet
apiVersion: apps/v1
metadata:
  name: csi-nfs-node
  namespace: kube-system
spec:
  updateStrategy:
    rollingUpdate:
      maxUnavailable: 1
    type: RollingUpdate
  selector:
    matchLabels:
      app: csi-nfs-node
  template:
    metadata:
      labels:
        app: csi-nfs-node
    spec:
      hostNetwork: true  # original nfs connection would be broken without hostNetwork setting
      dnsPolicy: ClusterFirstWithHostNet  # available values: Default, ClusterFirstWithHostNet, ClusterFirst
      serviceAccountName: csi-nfs-node-sa
      priorityClassName: system-node-critical
      securityContext:
        seccompProfile:
          type: RuntimeDefault
      nodeSelector:
        kubernetes.io/os: linux
      tolerations:
        - operator: "Exists"
      containers:
        - name: liveness-probe
          image: registry.k8s.io/sig-storage/livenessprobe:v2.13.1
          args:
            - --csi-address=/csi/csi.sock
            - --probe-timeout=3s
            - --http-endpoint=localhost:29653
            - --v=2
          volumeMounts:
            - name: socket-dir
              mountPath: /csi
          resources:
            limits:
              memory: 100Mi
            requests:
              cpu: 10m
              memory: 20Mi
          securityContext:
            capabilities:
              drop:
                - ALL
        - name: node-driver-registrar
          image: registry.k8s.io/sig-storage/csi-node-driver-registrar:v2.11.1
          args:
            - --v=2
            - --csi-address=/csi/csi.sock
            - --kubelet-registration-path=$(DRIVER_REG_SOCK_PATH)
          livenessProbe:
            exec:
              command:
                - /csi-node-driver-registrar
                - --kubelet-registration-path=$(DRIVER_REG_SOCK_PATH)
                - --mode=kubelet-registration-probe
            initialDelaySeconds: 30
            timeoutSeconds: 15
          env:
            - name: DRIVER_REG_SOCK_PATH
              value: /var/lib/kubelet/plugins/csi-nfsplugin/csi.sock
            - name: KUBE_NODE_NAME
              valueFrom:
                fieldRef:
                  fieldPath: spec.nodeName
          volumeMounts:
            - name: socket-dir
              mountPath: /csi
            - name: registration-dir
              mountPath: /registration
          resources:
            limits:
              memory: 100Mi
            requests:
              cpu: 10m
              memory: 20Mi
          securityContext:
            capabilities:
              drop:
                - ALL
        - name: nfs
          securityContext:
            privileged: true
            capabilities:
              add: ["SYS_ADMIN"]
              drop:
                - ALL
            allowPrivilegeEscalation: true
          image: registry.k8s.io/sig-storage/nfsplugin:v4.9.0
          args:
            - "-v=5"
            - "--nodeid=$(NODE_ID)"
            - "--endpoint=$(CSI_ENDPOINT)"
          env:
            - name: NODE_ID
              valueFrom:
                fieldRef:
                  fieldPath: spec.nodeName
            - name: CSI_ENDPOINT
              value: unix:///csi/csi.sock
          livenessProbe:
            failureThreshold: 5
            httpGet:
              host: localhost
              path: /healthz
              port: 29653
            initialDelaySeconds: 30
            timeoutSeconds: 10
            periodSeconds: 30
          imagePullPolicy: "IfNotPresent"
          volumeMounts:
            - name: socket-dir
              mountPath: /csi
            - name: pods-mount-dir
              mountPath: /var/lib/kubelet/pods
              mountPropagation: "Bidirectional"
          resources:
            limits:
              memory: 300Mi
            requests:
              cpu: 10m
              memory: 20Mi
      volumes:
        - name: socket-dir
          hostPath:
            path: /var/lib/kubelet/plugins/csi-nfsplugin
            type: DirectoryOrCreate
        - name: pods-mount-dir
          hostPath:
            path: /var/lib/kubelet/pods
            type: Directory
        - hostPath:
            path: /var/lib/kubelet/plugins_registry
            type: Directory
          name: registration-dir

# storageclass
---
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: nfs-client
provisioner: nfs.csi.k8s.io
parameters:
  server: 192.168.235.191
  share: /data
  # csi.storage.k8s.io/provisioner-secret is only needed for providing mountOptions in DeleteVolume
  # csi.storage.k8s.io/provisioner-secret-name: "mount-options"
  # csi.storage.k8s.io/provisioner-secret-namespace: "default"
reclaimPolicy: Delete
volumeBindingMode: Immediate
mountOptions:
  - vers=3
  - nolock
  - proto=tcp
  - rsize=1048576
  - wsize=1048576
  - timeo=600
  - hard
  - retrans=2
  # 阿里云nfs参数
  #- noresvport
```

部署`nfs`存储置备服务

```bash
kubectl create -f deployment.yaml
```

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



### `storageclass provisioner`配置字段

在Kubernetes中，StorageClass的provisioner是一个关键组件，它负责根据PersistentVolumeClaim（PVC）的需求动态创建PersistentVolume（PV）。以下是关于StorageClass provisioner的详细解释：

**一、定义与作用**

- **定义**：Provisioner是指定用于创建持久卷的存储提供商。它是StorageClass对象中的一个重要字段，用于定义如何以及使用哪个存储系统来动态地准备存储资源。
- **作用**：当Kubernetes集群中的Pod需要存储资源时，它会通过PVC来请求。如果集群中配置了相应的StorageClass和provisioner，那么系统就会自动调用这个provisioner来创建满足PVC需求的PV。

**二、配置与实现**

- **配置方式**：在创建StorageClass对象时，管理员需要指定provisioner字段，以及其他相关参数如parameters（存储提供商特定的参数）、reclaimPolicy（回收策略）等。
- **实现原理**：Provisioner通常是一个独立的程序，它遵循Kubernetes定义的规范，能够监听PVC的创建事件，并根据PVC的规格和StorageClass的配置来动态创建PV。这些PV随后可以被PVC绑定并使用。

**三、常见Provisioner类型**

- **内置Provisioner**：Kubernetes内置支持多种存储系统的Provisioner，如AWSElasticBlockStore、AzureDisk、GCEPersistentDisk等。这些Provisioner通常以“kubernetes.io/”为前缀命名。
- **外部Provisioner**：对于没有内置支持的存储系统，管理员可以运行和指定外部Provisioner。这些外部Provisioner需要遵循Kubernetes定义的规范，并能够与Kubernetes集群进行交互。

**四、使用场景与优势**

- **使用场景**：在大规模的Kubernetes集群中，可能有成千上万个PVC需要管理。使用StorageClass和provisioner可以大大简化运维管理成本，实现存储资源的自动化管理和动态分配。
- **优势**：
  - **自动化**：通过Provisioner，可以实现存储资源的自动化创建和管理，减少人工干预。
  - **灵活性**：管理员可以根据不同的存储需求配置不同的StorageClass和provisioner，以满足不同应用程序的存储要求。
  - **可扩展性**：Kubernetes支持多种存储系统和Provisioner，因此可以轻松扩展集群的存储能力。

**五、注意事项**

- **命名规范**：StorageClass对象的命名很重要，因为它将在创建PVC时被引用。管理员应该准确命名具有不同存储特性的StorageClass。
- **更新限制**：一旦创建了StorageClass对象，就不能再对其更新。如果需要更改配置，只能删除原对象并重新创建。
- **挂载选项与回收策略**：在配置StorageClass时，管理员还需要注意挂载选项（mountOptions）和回收策略（reclaimPolicy）的设置。这些设置将影响PV的创建和使用方式。

综上所述，StorageClass的provisioner是Kubernetes中实现存储资源自动化管理和动态分配的关键组件。通过合理配置和使用provisioner，可以大大提高集群的存储管理效率和灵活性。

