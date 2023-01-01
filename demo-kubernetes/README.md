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



## k8s安装

> NOTE 使用dcli安装

检查k8s服务是否正常

```shell
# 在master节点查看基础容器运行状态
kubectl get pods -n kube-system

# 在master节点查看所有节点状态
kubectl get nodes

# 部署nginx服务
kubectl create deployment nginx --image=nginx
# 使用NodePort方式暴露nginx服务
kubectl expose deployment nginx --port=80 --type=NodePort
# 查看nginx NodePort端口并使用浏览器访问成功
kubectl get pod,svc
```

## k8s yaml帮助查阅

```shell
# 查询Pod相关帮助
kubectl explain Pod

# 查询Pod.apiVersion帮助
kubectl explain Pod.apiVersion

# 查询apiVersion可填写的值
kubectl api-versions
```

## k8s namespace

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

## downward api

> 能够通过downward api获取k8s相关信息并通过环境变量传递到容器中。
>
> [链接1](https://kubernetes.io/docs/concepts/workloads/pods/downward-api/#available-fields)
>
> [通过环境变量传递pod信息](https://kubernetes.io/docs/tasks/inject-data-application/environment-variable-expose-pod-information/)

```shell
[root@k8s-master ~]# cat 1.yaml 
apiVersion: v1
kind: Pod
metadata:
  name: dapi-envars-fieldref
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
  
[root@k8s-master ~]# kubectl create -f 1.yaml 
pod/dapi-envars-fieldref created
[root@k8s-master ~]# kubectl logs dapi-envars-fieldref

k8s-node1
dapi-envars-fieldref
default
10.244.1.253
default
```

## pod

### 检查现有pod的yaml描述文件

```shell
# -o yaml表示以yaml格式输出pod的定义
# apiVersion: v1 描述文件所使用的kubernetes api版本
# kind: Pod kubernetes对象资源类型
# metadata pod元数据（名称、标签和注解等）
# spec pod规则/内容（pod的容器列表、volume等）
# status pod及其内部容器的详细状态
[root@k8s-master ~]# kubectl get pod kube-scheduler-k8s-master -n kube-system -o yaml
apiVersion: v1
kind: Pod
metadata:
  annotations:
    kubernetes.io/config.hash: 0378cf280f805e38b5448a1eceeedfc4
    kubernetes.io/config.mirror: 0378cf280f805e38b5448a1eceeedfc4
    kubernetes.io/config.seen: "2022-12-05T14:24:27.016187712+08:00"
    kubernetes.io/config.source: file
  creationTimestamp: "2022-12-05T06:24:33Z"
  labels:
    component: kube-scheduler
    tier: control-plane
  managedFields:
  - apiVersion: v1
    fieldsType: FieldsV1
    fieldsV1:
      f:metadata:
        f:annotations:
          .: {}
          f:kubernetes.io/config.hash: {}
          f:kubernetes.io/config.mirror: {}
          f:kubernetes.io/config.seen: {}
          f:kubernetes.io/config.source: {}
        f:labels:
          .: {}
          f:component: {}
          f:tier: {}
        f:ownerReferences:
          .: {}
          k:{"uid":"0eb13eba-67c2-4916-9866-7f7b962248c0"}:
            .: {}
            f:apiVersion: {}
            f:controller: {}
            f:kind: {}
            f:name: {}
            f:uid: {}
      f:spec:
        f:containers:
          k:{"name":"kube-scheduler"}:
            .: {}
            f:command: {}
            f:image: {}
            f:imagePullPolicy: {}
            f:livenessProbe:
              .: {}
              f:failureThreshold: {}
              f:httpGet:
                .: {}
                f:host: {}
                f:path: {}
                f:port: {}
                f:scheme: {}
              f:initialDelaySeconds: {}
              f:periodSeconds: {}
              f:successThreshold: {}
              f:timeoutSeconds: {}
            f:name: {}
            f:resources:
              .: {}
              f:requests:
                .: {}
                f:cpu: {}
            f:startupProbe:
              .: {}
              f:failureThreshold: {}
              f:httpGet:
                .: {}
                f:host: {}
                f:path: {}
                f:port: {}
                f:scheme: {}
              f:initialDelaySeconds: {}
              f:periodSeconds: {}
              f:successThreshold: {}
              f:timeoutSeconds: {}
            f:terminationMessagePath: {}
            f:terminationMessagePolicy: {}
            f:volumeMounts:
              .: {}
              k:{"mountPath":"/etc/kubernetes/scheduler.conf"}:
                .: {}
                f:mountPath: {}
                f:name: {}
                f:readOnly: {}
        f:dnsPolicy: {}
        f:enableServiceLinks: {}
        f:hostNetwork: {}
        f:nodeName: {}
        f:priorityClassName: {}
        f:restartPolicy: {}
        f:schedulerName: {}
        f:securityContext: {}
        f:terminationGracePeriodSeconds: {}
        f:tolerations: {}
        f:volumes:
          .: {}
          k:{"name":"kubeconfig"}:
            .: {}
            f:hostPath:
              .: {}
              f:path: {}
              f:type: {}
            f:name: {}
      f:status:
        f:conditions:
          .: {}
          k:{"type":"ContainersReady"}:
            .: {}
            f:lastProbeTime: {}
            f:lastTransitionTime: {}
            f:status: {}
            f:type: {}
          k:{"type":"Initialized"}:
            .: {}
            f:lastProbeTime: {}
            f:lastTransitionTime: {}
            f:status: {}
            f:type: {}
          k:{"type":"PodScheduled"}:
            .: {}
            f:lastProbeTime: {}
            f:lastTransitionTime: {}
            f:status: {}
            f:type: {}
          k:{"type":"Ready"}:
            .: {}
            f:lastProbeTime: {}
            f:lastTransitionTime: {}
            f:status: {}
            f:type: {}
        f:containerStatuses: {}
        f:hostIP: {}
        f:phase: {}
        f:podIP: {}
        f:podIPs:
          .: {}
          k:{"ip":"192.168.1.170"}:
            .: {}
            f:ip: {}
        f:startTime: {}
    manager: kubelet
    operation: Update
    time: "2022-12-05T06:26:01Z"
  name: kube-scheduler-k8s-master
  namespace: kube-system
  ownerReferences:
  - apiVersion: v1
    controller: true
    kind: Node
    name: k8s-master
    uid: 0eb13eba-67c2-4916-9866-7f7b962248c0
  resourceVersion: "589"
  uid: 73969be2-1b59-4846-8b21-a858728fa29d
spec:
  containers:
  - command:
    - kube-scheduler
    - --authentication-kubeconfig=/etc/kubernetes/scheduler.conf
    - --authorization-kubeconfig=/etc/kubernetes/scheduler.conf
    - --bind-address=127.0.0.1
    - --kubeconfig=/etc/kubernetes/scheduler.conf
    - --leader-elect=true
    - --port=0
    image: registry.aliyuncs.com/google_containers/kube-scheduler:v1.20.0
    imagePullPolicy: IfNotPresent
    livenessProbe:
      failureThreshold: 8
      httpGet:
        host: 127.0.0.1
        path: /healthz
        port: 10259
        scheme: HTTPS
      initialDelaySeconds: 10
      periodSeconds: 10
      successThreshold: 1
      timeoutSeconds: 15
    name: kube-scheduler
    resources:
      requests:
        cpu: 100m
    startupProbe:
      failureThreshold: 24
      httpGet:
        host: 127.0.0.1
        path: /healthz
        port: 10259
        scheme: HTTPS
      initialDelaySeconds: 10
      periodSeconds: 10
      successThreshold: 1
      timeoutSeconds: 15
    terminationMessagePath: /dev/termination-log
    terminationMessagePolicy: File
    volumeMounts:
    - mountPath: /etc/kubernetes/scheduler.conf
      name: kubeconfig
      readOnly: true
  dnsPolicy: ClusterFirst
  enableServiceLinks: true
  hostNetwork: true
  nodeName: k8s-master
  preemptionPolicy: PreemptLowerPriority
  priority: 2000001000
  priorityClassName: system-node-critical
  restartPolicy: Always
  schedulerName: default-scheduler
  securityContext: {}
  terminationGracePeriodSeconds: 30
  tolerations:
  - effect: NoExecute
    operator: Exists
  volumes:
  - hostPath:
      path: /etc/kubernetes/scheduler.conf
      type: FileOrCreate
    name: kubeconfig
status:
  conditions:
  - lastProbeTime: null
    lastTransitionTime: "2022-12-05T06:24:33Z"
    status: "True"
    type: Initialized
  - lastProbeTime: null
    lastTransitionTime: "2022-12-05T06:26:01Z"
    status: "True"
    type: Ready
  - lastProbeTime: null
    lastTransitionTime: "2022-12-05T06:26:01Z"
    status: "True"
    type: ContainersReady
  - lastProbeTime: null
    lastTransitionTime: "2022-12-05T06:24:33Z"
    status: "True"
    type: PodScheduled
  containerStatuses:
  - containerID: docker://bdfe8432da57f665218c351ea5bd078b677bd5737ff9d85f91a814740b8445c1
    image: registry.aliyuncs.com/google_containers/kube-scheduler:v1.20.0
    imageID: docker-pullable://registry.aliyuncs.com/google_containers/kube-scheduler@sha256:beaa710325047fa9c867eff4ab9af38d9c2acec05ac5b416c708c304f76bdbef
    lastState: {}
    name: kube-scheduler
    ready: true
    restartCount: 0
    started: true
    state:
      running:
        startedAt: "2022-12-05T06:24:19Z"
  hostIP: 192.168.1.170
  phase: Running
  podIP: 192.168.1.170
  podIPs:
  - ip: 192.168.1.170
  qosClass: Burstable
  startTime: "2022-12-05T06:24:33Z"
```

### 为pod创建一个简单的yaml描述文件

```shell
[root@k8s-master ~]# cat 1.yaml 
apiVersion: v1
kind: Pod
metadata:
 name: pod1 # pod名称为pod1
 namespace: dev # 在命名空间dev中创建pod1
 labels:
  test1: test1v
spec:
 containers:
 - name: nginx
   image: nginx
 - name: busybox
   image: busybox
```

### 使用kubectl explain查看可用的api对象字段

```shell
# 查看pods api可用的对象字段
[root@k8s-master ~]# kubectl explain pods
KIND:     Pod
VERSION:  v1

DESCRIPTION:
     Pod is a collection of containers that can run on a host. This resource is
     created by clients and scheduled onto hosts.

FIELDS:
   apiVersion	<string>
     APIVersion defines the versioned schema of this representation of an
     object. Servers should convert recognized schemas to the latest internal
     value, and may reject unrecognized values. More info:
     https://git.k8s.io/community/contributors/devel/sig-architecture/api-conventions.md#resources

   kind	<string>
     Kind is a string value representing the REST resource this object
     represents. Servers may infer this from the endpoint the client submits
     requests to. Cannot be updated. In CamelCase. More info:
     https://git.k8s.io/community/contributors/devel/sig-architecture/api-conventions.md#types-kinds

   metadata	<Object>
     Standard object's metadata. More info:
     https://git.k8s.io/community/contributors/devel/sig-architecture/api-conventions.md#metadata

   spec	<Object>
     Specification of the desired behavior of the pod. More info:
     https://git.k8s.io/community/contributors/devel/sig-architecture/api-conventions.md#spec-and-status

   status	<Object>
     Most recently observed status of the pod. This data may not be up to date.
     Populated by the system. Read-only. More info:
     https://git.k8s.io/community/contributors/devel/sig-architecture/api-conventions.md#spec-and-status
  
# 查看pods.metadata可用的对象字段
[root@k8s-master ~]# kubectl explain pods.metadata
KIND:     Pod
VERSION:  v1

RESOURCE: metadata <Object>

DESCRIPTION:
     Standard object's metadata. More info:
     https://git.k8s.io/community/contributors/devel/sig-architecture/api-conventions.md#metadata

     ObjectMeta is metadata that all persisted resources must have, which
     includes all objects users must create.

FIELDS:
   annotations	<map[string]string>
     Annotations is an unstructured key value map stored with a resource that
     may be set by external tools to store and retrieve arbitrary metadata. They
     are not queryable and should be preserved when modifying objects. More
     info: http://kubernetes.io/docs/user-guide/annotations

   clusterName	<string>
     The name of the cluster which the object belongs to. This is used to
     distinguish resources with same name and namespace in different clusters.
     This field is not set anywhere right now and apiserver is going to ignore
     it if set in create or update request.

   creationTimestamp	<string>
     CreationTimestamp is a timestamp representing the server time when this
     object was created. It is not guaranteed to be set in happens-before order
     across separate operations. Clients may not set this value. It is
     represented in RFC3339 form and is in UTC.

     Populated by the system. Read-only. Null for lists. More info:
     https://git.k8s.io/community/contributors/devel/sig-architecture/api-conventions.md#metadata

   deletionGracePeriodSeconds	<integer>
     Number of seconds allowed for this object to gracefully terminate before it
     will be removed from the system. Only set when deletionTimestamp is also
     set. May only be shortened. Read-only.

   deletionTimestamp	<string>
     DeletionTimestamp is RFC 3339 date and time at which this resource will be
     deleted. This field is set by the server when a graceful deletion is
     requested by the user, and is not directly settable by a client. The
     resource is expected to be deleted (no longer visible from resource lists,
     and not reachable by name) after the time in this field, once the
     finalizers list is empty. As long as the finalizers list contains items,
     deletion is blocked. Once the deletionTimestamp is set, this value may not
     be unset or be set further into the future, although it may be shortened or
     the resource may be deleted prior to this time. For example, a user may
     request that a pod is deleted in 30 seconds. The Kubelet will react by
     sending a graceful termination signal to the containers in the pod. After
     that 30 seconds, the Kubelet will send a hard termination signal (SIGKILL)
     to the container and after cleanup, remove the pod from the API. In the
     presence of network partitions, this object may still exist after this
     timestamp, until an administrator or automated process can determine the
     resource is fully terminated. If not set, graceful deletion of the object
     has not been requested.

     Populated by the system when a graceful deletion is requested. Read-only.
     More info:
     https://git.k8s.io/community/contributors/devel/sig-architecture/api-conventions.md#metadata

   finalizers	<[]string>
     Must be empty before the object is deleted from the registry. Each entry is
     an identifier for the responsible component that will remove the entry from
     the list. If the deletionTimestamp of the object is non-nil, entries in
     this list can only be removed. Finalizers may be processed and removed in
     any order. Order is NOT enforced because it introduces significant risk of
     stuck finalizers. finalizers is a shared field, any actor with permission
     can reorder it. If the finalizer list is processed in order, then this can
     lead to a situation in which the component responsible for the first
     finalizer in the list is waiting for a signal (field value, external
     system, or other) produced by a component responsible for a finalizer later
     in the list, resulting in a deadlock. Without enforced ordering finalizers
     are free to order amongst themselves and are not vulnerable to ordering
     changes in the list.

   generateName	<string>
     GenerateName is an optional prefix, used by the server, to generate a
     unique name ONLY IF the Name field has not been provided. If this field is
     used, the name returned to the client will be different than the name
     passed. This value will also be combined with a unique suffix. The provided
     value has the same validation rules as the Name field, and may be truncated
     by the length of the suffix required to make the value unique on the
     server.

     If this field is specified and the generated name exists, the server will
     NOT return a 409 - instead, it will either return 201 Created or 500 with
     Reason ServerTimeout indicating a unique name could not be found in the
     time allotted, and the client should retry (optionally after the time
     indicated in the Retry-After header).

     Applied only if Name is not specified. More info:
     https://git.k8s.io/community/contributors/devel/sig-architecture/api-conventions.md#idempotency

   generation	<integer>
     A sequence number representing a specific generation of the desired state.
     Populated by the system. Read-only.

   labels	<map[string]string>
     Map of string keys and values that can be used to organize and categorize
     (scope and select) objects. May match selectors of replication controllers
     and services. More info: http://kubernetes.io/docs/user-guide/labels

   managedFields	<[]Object>
     ManagedFields maps workflow-id and version to the set of fields that are
     managed by that workflow. This is mostly for internal housekeeping, and
     users typically shouldn't need to set or understand this field. A workflow
     can be the user's name, a controller's name, or the name of a specific
     apply path like "ci-cd". The set of fields is always in the version that
     the workflow used when modifying the object.

   name	<string>
     Name must be unique within a namespace. Is required when creating
     resources, although some resources may allow a client to request the
     generation of an appropriate name automatically. Name is primarily intended
     for creation idempotence and configuration definition. Cannot be updated.
     More info: http://kubernetes.io/docs/user-guide/identifiers#names

   namespace	<string>
     Namespace defines the space within which each name must be unique. An empty
     namespace is equivalent to the "default" namespace, but "default" is the
     canonical representation. Not all objects are required to be scoped to a
     namespace - the value of this field for those objects will be empty.

     Must be a DNS_LABEL. Cannot be updated. More info:
     http://kubernetes.io/docs/user-guide/namespaces

   ownerReferences	<[]Object>
     List of objects depended by this object. If ALL objects in the list have
     been deleted, this object will be garbage collected. If this object is
     managed by a controller, then an entry in this list will point to this
     controller, with the controller field set to true. There cannot be more
     than one managing controller.

   resourceVersion	<string>
     An opaque value that represents the internal version of this object that
     can be used by clients to determine when objects have changed. May be used
     for optimistic concurrency, change detection, and the watch operation on a
     resource or set of resources. Clients must treat these values as opaque and
     passed unmodified back to the server. They may only be valid for a
     particular resource or set of resources.

     Populated by the system. Read-only. Value must be treated as opaque by
     clients and . More info:
     https://git.k8s.io/community/contributors/devel/sig-architecture/api-conventions.md#concurrency-control-and-consistency

   selfLink	<string>
     SelfLink is a URL representing this object. Populated by the system.
     Read-only.

     DEPRECATED Kubernetes will stop propagating this field in 1.20 release and
     the field is planned to be removed in 1.21 release.

   uid	<string>
     UID is the unique in time and space value for this object. It is typically
     generated by the server on successful creation of a resource and is not
     allowed to change on PUT operations.

     Populated by the system. Read-only. More info:
     http://kubernetes.io/docs/user-guide/identifiers#uids

```

### 使用kubectl create创建pod

```shell
# 使用1.yaml pod定义文件创建pod
[root@k8s-master ~]# kubectl create -f 1.yaml 
pod/pod1 created

# 获取pod1详细的yaml定义
[root@k8s-master ~]# kubectl get pod pod1 -n dev -o yaml
apiVersion: v1
kind: Pod
metadata:
  creationTimestamp: "2023-01-01T16:32:29Z"
  labels:
    test1: test1v
  managedFields:
  - apiVersion: v1
    fieldsType: FieldsV1
    fieldsV1:
      f:metadata:
        f:labels:
          .: {}
          f:test1: {}
      f:spec:
        f:containers:
          k:{"name":"busybox"}:
            .: {}
            f:image: {}
            f:imagePullPolicy: {}
            f:name: {}
            f:resources: {}
            f:terminationMessagePath: {}
            f:terminationMessagePolicy: {}
          k:{"name":"nginx"}:
            .: {}
            f:image: {}
            f:imagePullPolicy: {}
            f:name: {}
            f:resources: {}
            f:terminationMessagePath: {}
            f:terminationMessagePolicy: {}
        f:dnsPolicy: {}
        f:enableServiceLinks: {}
        f:restartPolicy: {}
        f:schedulerName: {}
        f:securityContext: {}
        f:terminationGracePeriodSeconds: {}
    manager: kubectl-create
    operation: Update
    time: "2023-01-01T16:32:29Z"
  - apiVersion: v1
    fieldsType: FieldsV1
    fieldsV1:
      f:status:
        f:conditions:
          k:{"type":"ContainersReady"}:
            .: {}
            f:lastProbeTime: {}
            f:lastTransitionTime: {}
            f:message: {}
            f:reason: {}
            f:status: {}
            f:type: {}
          k:{"type":"Initialized"}:
            .: {}
            f:lastProbeTime: {}
            f:lastTransitionTime: {}
            f:status: {}
            f:type: {}
          k:{"type":"Ready"}:
            .: {}
            f:lastProbeTime: {}
            f:lastTransitionTime: {}
            f:message: {}
            f:reason: {}
            f:status: {}
            f:type: {}
        f:containerStatuses: {}
        f:hostIP: {}
        f:phase: {}
        f:podIP: {}
        f:podIPs:
          .: {}
          k:{"ip":"10.244.1.9"}:
            .: {}
            f:ip: {}
        f:startTime: {}
    manager: kubelet
    operation: Update
    time: "2023-01-01T16:33:02Z"
  name: pod1
  namespace: dev
  resourceVersion: "3591411"
  uid: 537a0a97-7558-40d1-ae3d-5536d7b4f880
spec:
  containers:
  - image: nginx
    imagePullPolicy: Always
    name: nginx
    resources: {}
    terminationMessagePath: /dev/termination-log
    terminationMessagePolicy: File
    volumeMounts:
    - mountPath: /var/run/secrets/kubernetes.io/serviceaccount
      name: default-token-pzzqr
      readOnly: true
  - image: busybox
    imagePullPolicy: Always
    name: busybox
    resources: {}
    terminationMessagePath: /dev/termination-log
    terminationMessagePolicy: File
    volumeMounts:
    - mountPath: /var/run/secrets/kubernetes.io/serviceaccount
      name: default-token-pzzqr
      readOnly: true
  dnsPolicy: ClusterFirst
  enableServiceLinks: true
  nodeName: k8s-node1
  preemptionPolicy: PreemptLowerPriority
  priority: 0
  restartPolicy: Always
  schedulerName: default-scheduler
  securityContext: {}
  serviceAccount: default
  serviceAccountName: default
  terminationGracePeriodSeconds: 30
  tolerations:
  - effect: NoExecute
    key: node.kubernetes.io/not-ready
    operator: Exists
    tolerationSeconds: 300
  - effect: NoExecute
    key: node.kubernetes.io/unreachable
    operator: Exists
    tolerationSeconds: 300
  volumes:
  - name: default-token-pzzqr
    secret:
      defaultMode: 420
      secretName: default-token-pzzqr
status:
  conditions:
  - lastProbeTime: null
    lastTransitionTime: "2023-01-01T16:32:30Z"
    status: "True"
    type: Initialized
  - lastProbeTime: null
    lastTransitionTime: "2023-01-01T16:32:30Z"
    message: 'containers with unready status: [busybox]'
    reason: ContainersNotReady
    status: "False"
    type: Ready
  - lastProbeTime: null
    lastTransitionTime: "2023-01-01T16:32:30Z"
    message: 'containers with unready status: [busybox]'
    reason: ContainersNotReady
    status: "False"
    type: ContainersReady
  - lastProbeTime: null
    lastTransitionTime: "2023-01-01T16:32:29Z"
    status: "True"
    type: PodScheduled
  containerStatuses:
  - containerID: docker://251a64aa724e1615c7ad5d134b264ecde2a081d9a0011007c2dc2d3339e9d052
    image: busybox:latest
    imageID: docker-pullable://busybox@sha256:5acba83a746c7608ed544dc1533b87c737a0b0fb730301639a0179f9344b1678
    lastState: {}
    name: busybox
    ready: false
    restartCount: 0
    started: false
    state:
      terminated:
        containerID: docker://251a64aa724e1615c7ad5d134b264ecde2a081d9a0011007c2dc2d3339e9d052
        exitCode: 0
        finishedAt: "2023-01-01T16:33:02Z"
        reason: Completed
        startedAt: "2023-01-01T16:33:02Z"
  - containerID: docker://fda6b59bf4699f6e6c996673f0c7663a5ac0ccd23f8c96073b21eec934b40fbb
    image: nginx:latest
    imageID: docker-pullable://nginx@sha256:0d17b565c37bcbd895e9d92315a05c1c3c9a29f762b011a10c54a66cd53c9b31
    lastState: {}
    name: nginx
    ready: true
    restartCount: 0
    started: true
    state:
      running:
        startedAt: "2023-01-01T16:32:46Z"
  hostIP: 192.168.1.171
  phase: Running
  podIP: 10.244.1.9
  podIPs:
  - ip: 10.244.1.9
  qosClass: BestEffort
  startTime: "2023-01-01T16:32:30Z"

# 获取pod列表，-n dev表示命名空间dev下的pod
[root@k8s-master ~]# kubectl get pod -n dev
NAME   READY   STATUS     RESTARTS   AGE
pod1   1/2     NotReady   4          2m52s

# 获取pod列表并输出pod所在的k8s node和pod ip信息
[root@k8s-master ~]# kubectl get pod -n dev -o wide
NAME   READY   STATUS             RESTARTS   AGE     IP           NODE        NOMINATED NODE   READINESS GATES
pod1   1/2     CrashLoopBackOff   4          3m10s   10.244.1.9   k8s-node1   <none>           <none>
```

### 使用kubectl run直接运行pod

```shell
# 使用kubectl run直接运行pod
[root@k8s-master ~]# kubectl run nginx --image=nginx --port=80 --namespace=dev
pod/nginx created

# 查看pod列表
[root@k8s-master ~]# kubectl get pod -n dev
NAME    READY   STATUS             RESTARTS   AGE
nginx   1/1     Running            0          19s

# 使用kubectl describe查看pod详细信息，包括pod启动日志和容器的状态
[root@k8s-master ~]# kubectl describe pod nginx -n dev 
Name:         nginx
Namespace:    dev
Priority:     0
Node:         k8s-node2/192.168.1.178
Start Time:   Mon, 02 Jan 2023 00:41:01 +0800
Labels:       run=nginx
Annotations:  <none>
Status:       Running
IP:           10.244.2.6
IPs:
  IP:  10.244.2.6
Containers:
  nginx:
    Container ID:   docker://a9de544417df4e79d00ea8194aad132ce5990ad077c089fb3810c4dc43d8850c
    Image:          nginx
    Image ID:       docker-pullable://nginx@sha256:0d17b565c37bcbd895e9d92315a05c1c3c9a29f762b011a10c54a66cd53c9b31
    Port:           80/TCP
    Host Port:      0/TCP
    State:          Running
      Started:      Mon, 02 Jan 2023 00:41:17 +0800
    Ready:          True
    Restart Count:  0
    Environment:    <none>
    Mounts:
      /var/run/secrets/kubernetes.io/serviceaccount from default-token-pzzqr (ro)
Conditions:
  Type              Status
  Initialized       True 
  Ready             True 
  ContainersReady   True 
  PodScheduled      True 
Volumes:
  default-token-pzzqr:
    Type:        Secret (a volume populated by a Secret)
    SecretName:  default-token-pzzqr
    Optional:    false
QoS Class:       BestEffort
Node-Selectors:  <none>
Tolerations:     node.kubernetes.io/not-ready:NoExecute op=Exists for 300s
                 node.kubernetes.io/unreachable:NoExecute op=Exists for 300s
Events:
  Type    Reason     Age   From               Message
  ----    ------     ----  ----               -------
  Normal  Scheduled  96s   default-scheduler  Successfully assigned dev/nginx to k8s-node2
  Normal  Pulling    96s   kubelet            Pulling image "nginx"
  Normal  Pulled     80s   kubelet            Successfully pulled image "nginx" in 15.400439377s
  Normal  Created    80s   kubelet            Created container nginx
  Normal  Started    80s   kubelet            Started container nginx

# 获取pod ip地址并使用curl访问nginx pod
[root@k8s-master ~]# kubectl get pod -n dev -o wide
NAME    READY   STATUS             RESTARTS   AGE     IP           NODE        NOMINATED NODE   READINESS GATES
nginx   1/1     Running            0          3m56s   10.244.2.6   k8s-node2   <none>           <none>
[root@k8s-master ~]# curl 10.244.2.6
<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
<style>
html { color-scheme: light dark; }
body { width: 35em; margin: 0 auto;
font-family: Tahoma, Verdana, Arial, sans-serif; }
</style>
</head>
<body>
<h1>Welcome to nginx!</h1>
<p>If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.</p>

<p>For online documentation and support please refer to
<a href="http://nginx.org/">nginx.org</a>.<br/>
Commercial support is available at
<a href="http://nginx.com/">nginx.com</a>.</p>

<p><em>Thank you for using nginx.</em></p>
</body>
</html>

# 删除pod
[root@k8s-master ~]# kubectl delete pod nginx -n dev
pod "nginx" deleted
```



### 将本地网络端口转发到pod中的端口

```shell
# 将本地端口8888转发到pod1 80端口
[root@k8s-master ~]# kubectl port-forward pod1 -n dev 8888:80
Forwarding from 127.0.0.1:8888 -> 80
Forwarding from [::1]:8888 -> 80
Handling connection for 8888

# 访问本地端口8888
[root@k8s-master mychart]# curl localhost:8888
<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
<style>
html { color-scheme: light dark; }
body { width: 35em; margin: 0 auto;
font-family: Tahoma, Verdana, Arial, sans-serif; }
</style>
</head>
<body>
<h1>Welcome to nginx!</h1>
<p>If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.</p>

<p>For online documentation and support please refer to
<a href="http://nginx.org/">nginx.org</a>.<br/>
Commercial support is available at
<a href="http://nginx.com/">nginx.com</a>.</p>

<p><em>Thank you for using nginx.</em></p>
</body>
</html>
```

### 使用kubectl delete删除pod

```shell
[root@k8s-master ~]# kubectl delete -f 1.yaml 
pod "pod1" deleted
```

### 创建pod时指定标签

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

### 修改现有的pod标签

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

### 使用标签选择器列出pod

```shell
# 列出标签creation_method=manual的pod
[root@k8s-master ~]# kubectl get pod -l creation_method=manual
NAME   READY   STATUS    RESTARTS   AGE
pod1   1/1     Running   0          9m20s

# 列出包含标签env的所有pod，无论标签值如何
[root@k8s-master ~]# kubectl get pod -l env
NAME   READY   STATUS    RESTARTS   AGE
pod1   1/1     Running   0          11m

# 列出不包含标签env1的所有pod
[root@k8s-master ~]# kubectl get pod -l '!env1'
NAME   READY   STATUS    RESTARTS   AGE
pod1   1/1     Running   0          12m

# 多个标签筛选pod，使用逗号分隔
[root@k8s-master ~]# kubectl get pod -l creation_method=manual,env=debug
NAME   READY   STATUS    RESTARTS   AGE
pod1   1/1     Running   0          19m
```

### 使用标签调度pod到指定节点

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

### 注解pod

```shell
# 创建pod时指定注解
[root@k8s-master ~]# cat 1.yaml 
apiVersion: v1
kind: Pod
metadata:
 name: pod1
 annotations:
  mycompany.com/someannotation: "foo bar"
spec:
 containers:
 - name: nginx
   image: nginx

# 查看注解
[root@k8s-master ~]# kubectl describe pod pod1
Name:         pod1
Namespace:    default
Priority:     0
Node:         k8s-node1/192.168.1.171
Start Time:   Mon, 02 Jan 2023 02:11:23 +0800
Labels:       <none>
Annotations:  mycompany.com/someannotation: foo bar
Status:       Pending
IP:           
IPs:          <none>
Containers:
  nginx:
    Container ID:   
    Image:          nginx
    Image ID:       
    Port:           <none>
    Host Port:      <none>
    State:          Waiting
      Reason:       ContainerCreating
    Ready:          False
    Restart Count:  0
    Environment:    <none>
    Mounts:
      /var/run/secrets/kubernetes.io/serviceaccount from default-token-q8hxp (ro)
Conditions:
  Type              Status
  Initialized       True 
  Ready             False 
  ContainersReady   False 
  PodScheduled      True 
Volumes:
  default-token-q8hxp:
    Type:        Secret (a volume populated by a Secret)
    SecretName:  default-token-q8hxp
    Optional:    false
QoS Class:       BestEffort
Node-Selectors:  <none>
Tolerations:     node.kubernetes.io/not-ready:NoExecute op=Exists for 300s
                 node.kubernetes.io/unreachable:NoExecute op=Exists for 300s
Events:
  Type    Reason     Age   From               Message
  ----    ------     ----  ----               -------
  Normal  Scheduled  16s   default-scheduler  Successfully assigned default/pod1 to k8s-node1
  Normal  Pulling    15s   kubelet            Pulling image "nginx"
 
# 向现有的pod添加注解
[root@k8s-master ~]# kubectl annotate pod pod1 mycompany.com/someannotation1="v2"
pod/pod1 annotated
```

### 发现其他命名空间及其pod

```shell
# 查询命名空间列表
[root@k8s-master ~]# kubectl get namespace
NAME              STATUS   AGE
default           Active   27d
dev               Active   27d
kube-flannel      Active   27d
kube-node-lease   Active   27d
kube-public       Active   27d
kube-system       Active   27d

# 查询命名空间kube-system下的所有pod
[root@k8s-master ~]# kubectl get pod --namespace kube-system
NAME                                 READY   STATUS    RESTARTS   AGE
coredns-7f89b7bc75-9jvzv             1/1     Running   0          27d
coredns-7f89b7bc75-nwpk2             1/1     Running   0          27d
etcd-k8s-master                      1/1     Running   0          27d
kube-apiserver-k8s-master            1/1     Running   0          27d
kube-controller-manager-k8s-master   1/1     Running   0          27d
kube-proxy-2t2ck                     1/1     Running   0          26d
kube-proxy-6vmvt                     1/1     Running   0          27d
kube-proxy-qb8kg                     1/1     Running   0          27d
kube-scheduler-k8s-master            1/1     Running   0          27d
```

### 创建命名空间

```shell
# 使用yaml创建命名空间
[root@k8s-master ~]# cat 2.yaml 
apiVersion: v1
kind: Namespace
metadata:
 name: custom-namespace
 
[root@k8s-master ~]# kubectl apply -f 2.yaml 
namespace/custom-namespace created

[root@k8s-master ~]# kubectl get namespace
NAME               STATUS   AGE
custom-namespace   Active   50s

[root@k8s-master ~]# kubectl delete namespace custom-namespace
namespace "custom-namespace" deleted

# 使用命令创建命名空间
[root@k8s-master ~]# kubectl create namespace custom-namespace
namespace/custom-namespace created
```

### 管理其他命名空间中的对象

```shell
[root@k8s-master ~]# cat 1.yaml
apiVersion: v1
kind: Pod
metadata:
 name: pod1
 annotations:
  mycompany.com/someannotation: "foo bar"
spec:
 containers:
 - name: nginx
   image: nginx
   
# 在default默认命名空间创建pod
[root@k8s-master ~]# kubectl create -f 1.yaml 
pod/pod1 created
# 在custom-namespace命名空间创建pod
[root@k8s-master ~]# kubectl create -f 1.yaml --namespace custom-namespace
pod/pod1 created
# 查询default命名空间的pod列表
[root@k8s-master ~]# kubectl get pod
NAME   READY   STATUS    RESTARTS   AGE
pod1   1/1     Running   0          22s
# 查询custom-namespace命名空间的pod列表
[root@k8s-master ~]# kubectl get pod --namespace custom-namespace
NAME   READY   STATUS    RESTARTS   AGE
pod1   1/1     Running   0          20s
```

### 停止和删除pod

```shell
# 按名称删除pod
[root@k8s-master ~]# kubectl delete pod pod1
pod "pod1" deleted

# 使用标签选择器删除pod
[root@k8s-master ~]# cat 1.yaml 
apiVersion: v1
kind: Pod
metadata:
 name: pod1
 labels:
  mylabel1: v1
spec:
 containers:
 - name: nginx
   image: nginx
[root@k8s-master ~]# kubectl delete pod -l mylabel1=v1
pod "pod1" deleted

# 通过删除整个命名空间来删除pod
[root@k8s-master ~]# kubectl apply -f 1.yaml --namespace custom-namespace
pod/pod1 created
[root@k8s-master ~]# kubectl delete namespace custom-namespace
namespace "custom-namespace" deleted

# 删除当前命名空间（几乎）所有资源，第一个all表示所有资源类型，--all指定将删除所有资源实例，而不是按名称指定它们
[root@k8s-master ~]# kubectl delete all --all
service "kubernetes" deleted
```



### 私有镜像拉取时提供帐号和密码

> [链接1](https://kubernetes.io/docs/tasks/configure-pod-container/pull-image-private-registry/)

```shell
# 创建帐号和密码secret
[root@k8s-master ~]# kubectl create secret docker-registry regcred --docker-server=my.docker.hub --docker-username=xxx --docker-password=xxxx
# 查看secret
[root@k8s-master ~]# kubectl get secret regcred --output=yaml
```

```yaml
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
   resources:
    limits:
     cpu: "1"
     memory: "1G" # 单位G或者M
    requests:
     cpu: "1"
     memory: "1M"
```

### 初始化容器使用

> 使用init容器模拟等待mysql和redis启动后才启动nginx的过程

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
 initContainers:
 - name: test-mysql
   image: busybox
   command: ["sh", "-c", "i=1;until [ $i -ge 15 ]; do echo 'simulating waiting for mysql...'; sleep 1; i=$((i+1)); done;"]
 - name: test-redis
   image: busybox
   command: ["sh", "-c", "i=1;until [ $i -ge 15 ]; do echo 'simulating waiting for mysql...'; sleep 1; i=$((i+1)); done;"]
```

```shell
# 使用命令观察init容器和main容器过程，-w参数
kubectl get pod -n dev -o wide -w
```

### 钩子函数

> 在main容器启动后postStart和关闭前preStop执行指定的动作

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
   lifecycle:
    postStart:
     exec:
      command: ["sh", "-c", "echo postStart... > /usr/share/nginx/html/index.html"]
```

```shell
# 获取pod ip地址
kubectl get pod -n dev -o wide
# 测试是否执行postStart
curl podIp:80
```

### 容器探测

- liveness probes: 决定是否重启容器
- readiness probes: 决定是否将请求转发给容器

**liveness探测**

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
   livenessProbe:
    exec:
     command: ["/bin/cat", "/tmp/hello.txt"] # 因为文件/tmp/hello.txt不存在，命令执行失败，导致容器不断重新启动
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

### 定向调度

> 通过nodename或者nodeselector指定pod运行的节点

**使用nodename指定节点**

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: base-pod
 namespace: dev
 labels:
  test1: test1v
spec:
 nodeName: k8s-node1 # 使用节点名称指定pod运行节点
 containers:
 - name: nginx
   image: nginx
   imagePullPolicy: Always
```

**使用nodeselector指定节点标签**

```shell
# 节点打标签
kubectl label node k8s-node1 node-label=node1
```

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: base-pod
 namespace: dev
 labels:
  test1: test1v
spec:
 nodeSelector:
  node-label: node1 # 指定节点标签
 containers:
 - name: nginx
   image: nginx
   imagePullPolicy: Always
```

### 亲和性调度

todo

### 污点调度

todo

### 容忍调度

todo

## pod控制器

> pod控制，用于保证pod运行状态符合预期状态

```shell
# 创建deployment
kubectl create deployment nginx --image=nginx --port=80 --replicas=3 --namespace=dev

# 显示deployment列表
kubectl get deployment,pods -n dev

# 删除其中一个pod后，deployment控制器会自动创建一个新的pod
kubectl delete pod nginx-xxxxxxx -n dev

# 显示deployment详细信息
kubectl describe deployment -n dev

# 删除deployment
kubectl delete deployment nginx -n dev
```

### ReplicaSet(RS)

> 保证一定数量的pod能够正常运行，一旦pod发生故障就会自动重启或者重建pod，pod数量扩容，镜像版本的升级和回退

**pod基础**

```yaml
apiVersion: apps/v1
kind: ReplicaSet
metadata:
 name: replicaset1
 namespace: dev
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
```

```shell
# 查看replicaset状态
[root@k8s-master ~]# kubectl get replicaset -n dev -o wide
NAME          DESIRED   CURRENT   READY   AGE   CONTAINERS   IMAGES   SELECTOR
replicaset1   3         3         3       13m   nginx        nginx    app=nginx-pod
# 查看pod状态
[root@k8s-master ~]# kubectl get pod -n dev
NAME                READY   STATUS    RESTARTS   AGE
replicaset1-666zx   1/1     Running   0          13m
replicaset1-nbfts   1/1     Running   0          13m
replicaset1-rw24c   1/1     Running   0          13m
```

**pod数量缩放**

```shell
# 使用下面命令编辑replicaset yaml中的replicas参数，退出保存后replicaset自动缩放pod
[root@k8s-master ~]# kubectl edit replicaset replicaset1 -n dev
replicaset.apps/replicaset1 edited

# 使用下面命令对replicaset缩放
[root@k8s-master ~]# kubectl scale replicaset replicaset1 -n dev --replicas=6
replicaset.apps/replicaset1 scaled
```

**pod镜像版本升级和回退**

```shell
# 使用下面命令在线编辑image参数，退出保存后replicaset自动更新镜像版本
[root@k8s-master ~]# kubectl edit replicaset replicaset1 -n dev
replicaset.apps/replicaset1 edited
# 查看当前image版本
[root@k8s-master ~]# kubectl get replicaset -n dev -o wide
NAME          DESIRED   CURRENT   READY   AGE   CONTAINERS   IMAGES         SELECTOR
replicaset1   2         2         2       31m   nginx        nginx:1.17.1   app=nginx-pod

# 使用命令修改image
[root@k8s-master ~]# kubectl set image replicaset replicaset1 -n dev nginx=nginx:1.17.1
replicaset.apps/replicaset1 image updated
```

**删除replicaset**

```shell
# 使用命令删除
[root@k8s-master ~]# kubectl delete replicaset replicaset1 -n dev
replicaset.apps "replicaset1" deleted

# 使用yaml删除
[root@k8s-master ~]# kubectl delete -f 1.yaml 
replicaset.apps "replicaset1" deleted
```

### Deployment(deploy)

> deployment控制器不直接管理pod，而是通过Replicaset间接管理pod

**基本用法**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
 namespace: dev
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
```

```shell
[root@k8s-master ~]# kubectl create -f 1.yaml 
deployment.apps/deployment1 created
[root@k8s-master ~]# kubectl get deployment -n dev -o wide
NAME          READY   UP-TO-DATE   AVAILABLE   AGE   CONTAINERS   IMAGES         SELECTOR
deployment1   1/3     3            1           12s   nginx        nginx:1.17.1   app=nginx-pod
# 因为deployment通过replicaset管理pod，所以创建deployment后会自动创建一个对应的replicaset
[root@k8s-master ~]# kubectl get replicaset -n dev
NAME                     DESIRED   CURRENT   READY   AGE
deployment1-5d9c9b97bb   3         3         3       2m33s
```

**扩缩容**

```shell
# 通过命令方式实现扩容
[root@k8s-master ~]# kubectl scale deployment deployment1 -n dev --replicas=6
deployment.apps/deployment1 scaled
[root@k8s-master ~]# kubectl get pod -n dev -o wide
NAME                           READY   STATUS    RESTARTS   AGE     IP            NODE        NOMINATED NODE   READINESS GATES
deployment1-5d9c9b97bb-7tz7p   1/1     Running   0          9m56s   10.244.2.30   k8s-node2   <none>           <none>
deployment1-5d9c9b97bb-8vk5f   1/1     Running   0          9m56s   10.244.1.16   k8s-node1   <none>           <none>
deployment1-5d9c9b97bb-dqvdm   1/1     Running   0          14s     10.244.2.31   k8s-node2   <none>           <none>
deployment1-5d9c9b97bb-k7tg8   1/1     Running   0          14s     10.244.2.32   k8s-node2   <none>           <none>
deployment1-5d9c9b97bb-wwgcc   1/1     Running   0          9m56s   10.244.2.29   k8s-node2   <none>           <none>
deployment1-5d9c9b97bb-xlktk   1/1     Running   0          14s     10.244.1.17   k8s-node1   <none>           <none>

# 通过编辑deployment配置中的replicas实现扩容
[root@k8s-master ~]# kubectl edit deployment deployment1 -n dev
deployment.apps/deployment1 edited
```

**升级策略**

- 重建更新: 删除所有旧版本pod，重新创建新版本pod

- 滚动更新: 删除一部分旧版本pod，创建一部分新版本pod，如此重复最终所有更新替换所有旧版本pod

重建更新

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
 namespace: dev
spec:
 replicas: 3
 strategy:
  type: Recreate # 重建更新
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
```

```shell
# 模拟升级镜像版本
[root@k8s-master ~]# kubectl set image deployment deployment1 -n dev nginx=nginx:1.17.2
deployment.apps/deployment1 image updated

# 观察pod重建过程
[root@k8s-master ~]# kubectl get pod -n dev -o wide -w
NAME                           READY   STATUS    RESTARTS   AGE   IP            NODE        NOMINATED NODE   READINESS GATES
deployment1-5d9c9b97bb-7tz7p   1/1     Running   0          54m   10.244.2.30   k8s-node2   <none>           <none>
deployment1-5d9c9b97bb-8vk5f   1/1     Running   0          54m   10.244.1.16   k8s-node1   <none>           <none>
deployment1-5d9c9b97bb-xlktk   1/1     Running   0          44m   10.244.1.17   k8s-node1   <none>           <none>
deployment1-5d9c9b97bb-7tz7p   1/1     Terminating   0          54m   10.244.2.30   k8s-node2   <none>           <none>
deployment1-5d9c9b97bb-8vk5f   1/1     Terminating   0          54m   10.244.1.16   k8s-node1   <none>           <none>
deployment1-5d9c9b97bb-xlktk   1/1     Terminating   0          45m   10.244.1.17   k8s-node1   <none>           <none>
deployment1-5d9c9b97bb-7tz7p   0/1     Terminating   0          54m   <none>        k8s-node2   <none>           <none>
deployment1-5d9c9b97bb-8vk5f   0/1     Terminating   0          54m   10.244.1.16   k8s-node1   <none>           <none>
deployment1-5d9c9b97bb-xlktk   0/1     Terminating   0          45m   10.244.1.17   k8s-node1   <none>           <none>
deployment1-5d9c9b97bb-7tz7p   0/1     Terminating   0          55m   <none>        k8s-node2   <none>           <none>
deployment1-5d9c9b97bb-7tz7p   0/1     Terminating   0          55m   <none>        k8s-node2   <none>           <none>
deployment1-5d9c9b97bb-8vk5f   0/1     Terminating   0          55m   10.244.1.16   k8s-node1   <none>           <none>
deployment1-5d9c9b97bb-8vk5f   0/1     Terminating   0          55m   10.244.1.16   k8s-node1   <none>           <none>
deployment1-5d9c9b97bb-xlktk   0/1     Terminating   0          46m   10.244.1.17   k8s-node1   <none>           <none>
deployment1-5d9c9b97bb-xlktk   0/1     Terminating   0          46m   10.244.1.17   k8s-node1   <none>           <none>
deployment1-7c7477c7ff-vnhtq   0/1     Pending       0          0s    <none>        <none>      <none>           <none>
deployment1-7c7477c7ff-xcdqf   0/1     Pending       0          0s    <none>        <none>      <none>           <none>
deployment1-7c7477c7ff-wvnrk   0/1     Pending       0          0s    <none>        <none>      <none>           <none>
deployment1-7c7477c7ff-vnhtq   0/1     Pending       0          0s    <none>        k8s-node2   <none>           <none>
deployment1-7c7477c7ff-xcdqf   0/1     Pending       0          0s    <none>        k8s-node1   <none>           <none>
deployment1-7c7477c7ff-wvnrk   0/1     Pending       0          0s    <none>        k8s-node2   <none>           <none>
deployment1-7c7477c7ff-vnhtq   0/1     ContainerCreating   0          0s    <none>        k8s-node2   <none>           <none>
deployment1-7c7477c7ff-xcdqf   0/1     ContainerCreating   0          0s    <none>        k8s-node1   <none>           <none>
deployment1-7c7477c7ff-wvnrk   0/1     ContainerCreating   0          0s    <none>        k8s-node2   <none>           <none>
deployment1-7c7477c7ff-wvnrk   1/1     Running             0          22s   10.244.2.34   k8s-node2   <none>           <none>
deployment1-7c7477c7ff-vnhtq   1/1     Running             0          22s   10.244.2.33   k8s-node2   <none>           <none>
deployment1-7c7477c7ff-xcdqf   1/1     Running             0          23s   10.244.1.18   k8s-node1   <none>           <none>
```

滚动更新

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
 namespace: dev
spec:
 replicas: 3
 strategy:
  type: RollingUpdate # 滚动更新策略
  rollingUpdate:
   maxUnavailable: 25%
   maxSurge: 25%
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
```

```shell
# 模拟升级镜像版本
[root@k8s-master ~]# kubectl set image deployment deployment1 -n dev nginx=nginx:1.17.2
deployment.apps/deployment1 image updated

# 观察滚动更新过程
[root@k8s-master ~]# kubectl get pod -n dev -w
NAME                           READY   STATUS    RESTARTS   AGE
deployment1-5d9c9b97bb-qjd58   1/1     Running   0          20s
deployment1-5d9c9b97bb-s8mst   1/1     Running   0          23s
deployment1-5d9c9b97bb-vjgwx   1/1     Running   0          22s
deployment1-76fd8c7f84-w2n8g   0/1     Pending   0          0s
deployment1-76fd8c7f84-w2n8g   0/1     Pending   0          0s
deployment1-76fd8c7f84-w2n8g   0/1     ContainerCreating   0          0s
deployment1-76fd8c7f84-w2n8g   1/1     Running             0          22s
deployment1-5d9c9b97bb-qjd58   1/1     Terminating         0          54s
deployment1-76fd8c7f84-xln89   0/1     Pending             0          0s
deployment1-76fd8c7f84-xln89   0/1     Pending             0          0s
deployment1-76fd8c7f84-xln89   0/1     ContainerCreating   0          0s
deployment1-5d9c9b97bb-qjd58   0/1     Terminating         0          55s
deployment1-5d9c9b97bb-qjd58   0/1     Terminating         0          58s
deployment1-5d9c9b97bb-qjd58   0/1     Terminating         0          58s
deployment1-76fd8c7f84-xln89   1/1     Running             0          22s
deployment1-5d9c9b97bb-vjgwx   1/1     Terminating         0          78s
deployment1-76fd8c7f84-tqn8c   0/1     Pending             0          0s
deployment1-76fd8c7f84-tqn8c   0/1     Pending             0          0s
deployment1-76fd8c7f84-tqn8c   0/1     ContainerCreating   0          0s
deployment1-76fd8c7f84-tqn8c   1/1     Running             0          1s
deployment1-5d9c9b97bb-vjgwx   0/1     Terminating         0          79s
deployment1-5d9c9b97bb-s8mst   1/1     Terminating         0          80s
deployment1-5d9c9b97bb-s8mst   0/1     Terminating         0          81s
deployment1-5d9c9b97bb-s8mst   0/1     Terminating         0          87s
deployment1-5d9c9b97bb-s8mst   0/1     Terminating         0          87s
deployment1-5d9c9b97bb-vjgwx   0/1     Terminating         0          90s
deployment1-5d9c9b97bb-vjgwx   0/1     Terminating         0          90s
```

**版本回退**

> 版本回退原理是通过多个replicaset实现的

```shell
# 查看版本更新历史
[root@k8s-master ~]# kubectl rollout history deployment deployment1 -n dev
deployment.apps/deployment1 
REVISION  CHANGE-CAUSE
2         <none>
3         <none>
4         <none>
[root@k8s-master ~]# kubectl get replicaset -n dev
NAME                     DESIRED   CURRENT   READY   AGE
deployment1-5d9c9b97bb   0         0         0       77m
deployment1-76fd8c7f84   3         3         3       16m
deployment1-7c7477c7ff   0         0         0       21m

# 回退到指定版本
[root@k8s-master ~]# kubectl rollout undo deployment deployment1 --to-revision=2 -n dev
deployment.apps/deployment1 rolled back
# 查看回退状态
[root@k8s-master ~]# kubectl rollout status deployment deployment1 -n dev
Waiting for deployment "deployment1" rollout to finish: 2 out of 3 new replicas have been updated...
Waiting for deployment "deployment1" rollout to finish: 2 out of 3 new replicas have been updated...
Waiting for deployment "deployment1" rollout to finish: 2 out of 3 new replicas have been updated...
Waiting for deployment "deployment1" rollout to finish: 1 old replicas are pending termination...
Waiting for deployment "deployment1" rollout to finish: 1 old replicas are pending termination...
deployment "deployment1" successfully rolled out
# 回退后的replicaset状态
[root@k8s-master ~]# kubectl get replicaset -n dev
NAME                     DESIRED   CURRENT   READY   AGE
deployment1-5d9c9b97bb   0         0         0       79m
deployment1-76fd8c7f84   0         0         0       19m
deployment1-7c7477c7ff   3         3         3       23m

```

**金丝雀发布**

todo

### Horizontal Pod Autoscaler(HPA)

> 根据pod负载的变化自动调整pod的数量

### DaemonSet(DS)

> DaemonSet类型的控制器可以保证集群中的每一台节点上都运行一个pod副本，一般适用于日志收集、节点监控等场景。

```yaml
apiVersion: apps/v1
kind: DaemonSet
metadata:
 name: daemonset1
 namespace: dev
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
```

```shell
[root@k8s-master ~]# kubectl create -f 1.yaml
daemonset.apps/daemonset1 created
[root@k8s-master ~]# kubectl get daemonset -n dev
NAME         DESIRED   CURRENT   READY   UP-TO-DATE   AVAILABLE   NODE SELECTOR   AGE
daemonset1   2         2         2       2            2           <none>          14s
# daemonset在每个节点上都运行一个pod
[root@k8s-master ~]# kubectl get pod -n dev -o wide
NAME               READY   STATUS    RESTARTS   AGE   IP            NODE        NOMINATED NODE   READINESS GATES
daemonset1-7qstq   1/1     Running   0          32s   10.244.2.41   k8s-node2   <none>           <none>
daemonset1-vz9sj   1/1     Running   0          32s   10.244.1.22   k8s-node1   <none>           <none>
[root@k8s-master ~]# kubectl delete -f 1.yaml
daemonset.apps "daemonset1" deleted
```

### Job

> 主要用于负责批量处理（一次要处理指定数量的任务）短暂的一次性（每个任务仅运行一次就结束）任务。

```yaml
apiVersion: batch/v1
kind: Job
metadata:
 name: job1
 namespace: dev
spec:
 manualSelector: true
 completions: 6 # 总共需要执行多少个pod
 parallelism: 3 # 并行运行pod的数量
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
```

```shell
[root@k8s-master ~]# kubectl create -f 1.yaml
job.batch/job1 created
# 查看job运行状态
[root@k8s-master ~]# kubectl get job -n dev -o wide -w
NAME   COMPLETIONS   DURATION   AGE   CONTAINERS   IMAGES    SELECTOR
job1   0/6                      0s    counter      busybox   app=counter-pod
job1   0/6           0s         0s    counter      busybox   app=counter-pod
job1   1/6           20s        20s   counter      busybox   app=counter-pod
job1   2/6           35s        35s   counter      busybox   app=counter-pod
job1   3/6           35s        35s   counter      busybox   app=counter-pod
job1   4/6           55s        55s   counter      busybox   app=counter-pod
job1   5/6           55s        55s   counter      busybox   app=counter-pod
job1   6/6           55s        55s   counter      busybox   app=counter-pod
# 查看pod运行状态
[root@k8s-master ~]# kubectl get pod -n dev -w
NAME         READY   STATUS    RESTARTS   AGE
job1-bwgld   0/1     Pending   0          0s
job1-bwgld   0/1     Pending   0          0s
job1-67pt8   0/1     Pending   0          0s
job1-j7bwp   0/1     Pending   0          0s
job1-j7bwp   0/1     Pending   0          0s
job1-67pt8   0/1     Pending   0          0s
job1-bwgld   0/1     ContainerCreating   0          0s
job1-j7bwp   0/1     ContainerCreating   0          0s
job1-67pt8   0/1     ContainerCreating   0          0s
job1-bwgld   1/1     Running             0          17s
job1-j7bwp   1/1     Running             0          17s
job1-67pt8   1/1     Running             0          32s
job1-bwgld   0/1     Completed           0          35s
job1-l2qxx   0/1     Pending             0          0s
job1-l2qxx   0/1     Pending             0          0s
job1-l2qxx   0/1     ContainerCreating   0          0s
job1-j7bwp   0/1     Completed           0          35s
job1-5t9xj   0/1     Pending             0          0s
job1-5t9xj   0/1     Pending             0          0s
job1-5t9xj   0/1     ContainerCreating   0          0s
job1-l2qxx   1/1     Running             0          2s
job1-5t9xj   1/1     Running             0          2s
job1-67pt8   0/1     Completed           0          50s
job1-cthqp   0/1     Pending             0          0s
job1-cthqp   0/1     Pending             0          0s
job1-cthqp   0/1     ContainerCreating   0          0s
job1-l2qxx   0/1     Completed           0          20s
job1-5t9xj   0/1     Completed           0          20s
job1-cthqp   1/1     Running             0          17s
job1-cthqp   0/1     Completed           0          34s
# 删除job
[root@k8s-master ~]# kubectl delete -f 1.yaml
job.batch "job1" deleted
```

### CronJob(CJ)

> 指定特定的时间点重复执行job任务

todo

### StatefulSet

> RC、Deployment、DaemonSet都是面向无状态的服务，它们所管理的Pod的IP、名字，启停顺序等都是随机的，而StatefulSet是什么？顾名思义，有状态的集合，管理所有有状态的服务，比如MySQL、MongoDB集群等。
> StatefulSet本质上是Deployment的一种变体，在v1.9版本中已成为GA版本，它为了解决有状态服务的问题，它所管理的Pod拥有固定的Pod名称，启停顺序，在StatefulSet中，Pod名字称为网络标识(hostname)，还必须要用到共享存储。
>
> 在Deployment中，与之对应的服务是service，而在StatefulSet中与之对应的headless service，headless service，即无头服务，与service的区别就是它没有Cluster IP，解析它的名称时将返回该Headless Service对应的全部Pod的Endpoint列表。
>
> [链接1](https://www.jianshu.com/p/03cd2f2dc427)

**参考storageclass章节创建storageclass**

**创建statefulset.yaml**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: nginx
  labels:
    app: nginx
spec:
  ports:
  - port: 80
    name: web
  clusterIP: None
  selector:
    app: nginx
---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: web
spec:
  selector:
    matchLabels:
      app: nginx # has to match .spec.template.metadata.labels
  serviceName: "nginx"  #声明它属于哪个Headless Service.
  replicas: 3 # by default is 1
  template:
    metadata:
      labels:
        app: nginx # has to match .spec.selector.matchLabels
    spec:
      terminationGracePeriodSeconds: 10
      containers:
      - name: nginx
        image: nginx:1.20.1
        ports:
        - containerPort: 80
          name: web
        volumeMounts:
        - name: www
          mountPath: /usr/share/nginx/html
  volumeClaimTemplates:   #可看作pvc的模板
  - metadata:
      name: www
    spec:
      accessModes: [ "ReadWriteOnce" ]
      storageClassName: "nfs-client"  #存储类名，改为集群中已存在的
      resources:
        requests:
          storage: 1Gi
```

```shell
# kubectl create -f statefulset.yaml启动statefulset后
# 在各个nginx目录下创建index.html，如下所示
[root@k8s-master datass]# tree
.
├── default-test-claim-pvc-34bc5c37-2507-4c66-b470-76f199fc07f9
├── default-www-web-0-pvc-532ce5e0-c614-4c4c-abd1-dd664d88298f
│   └── index.html
├── default-www-web-1-pvc-210e4f4a-d006-422f-bf39-3b36e4af89ef
│   └── index.html
└── default-www-web-2-pvc-5b905a3a-aed4-44d3-b874-b11f34ae3434
    └── index.html
# 每个index.html <h1>Welcome to nginx1!</h1>不一样
[root@k8s-master default-www-web-1-pvc-210e4f4a-d006-422f-bf39-3b36e4af89ef]# cat index.html 
<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
<style>
    body {
        width: 35em;
        margin: 0 auto;
        font-family: Tahoma, Verdana, Arial, sans-serif;
    }
</style>
</head>
<body>
<h1>Welcome to nginx1!</h1>
<p>If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.</p>

<p>For online documentation and support please refer to
<a href="http://nginx.org/">nginx.org</a>.<br/>
Commercial support is available at
<a href="http://nginx.com/">nginx.com</a>.</p>

<p><em>Thank you for using nginx.</em></p>
</body>
</html>
# 分别请求3个nginx，返回的内容不一样
[root@k8s-master ~]# kubectl get pod -o wide
NAME                                      READY   STATUS    RESTARTS   AGE   IP            NODE        NOMINATED NODE   READINESS GATES
nfs-client-provisioner-859477c96c-stc5k   1/1     Running   0          10m   10.244.1.40   k8s-node1   <none>           <none>
web-0                                     1/1     Running   0          10s   10.244.2.80   k8s-node2   <none>           <none>
web-1                                     1/1     Running   0          8s    10.244.1.41   k8s-node1   <none>           <none>
web-2                                     1/1     Running   0          7s    10.244.2.81   k8s-node2   <none>           <none>
[root@k8s-master ~]# curl 10.244.2.80
<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
<style>
    body {
        width: 35em;
        margin: 0 auto;
        font-family: Tahoma, Verdana, Arial, sans-serif;
    }
</style>
</head>
<body>
<h1>Welcome to nginx0!</h1>
<p>If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.</p>

<p>For online documentation and support please refer to
<a href="http://nginx.org/">nginx.org</a>.<br/>
Commercial support is available at
<a href="http://nginx.com/">nginx.com</a>.</p>

<p><em>Thank you for using nginx.</em></p>
</body>
</html>

[root@k8s-master ~]# curl 10.244.1.41
<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
<style>
    body {
        width: 35em;
        margin: 0 auto;
        font-family: Tahoma, Verdana, Arial, sans-serif;
    }
</style>
</head>
<body>
<h1>Welcome to nginx1!</h1>
<p>If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.</p>

<p>For online documentation and support please refer to
<a href="http://nginx.org/">nginx.org</a>.<br/>
Commercial support is available at
<a href="http://nginx.com/">nginx.com</a>.</p>

<p><em>Thank you for using nginx.</em></p>
</body>
</html>
```



## k8s service

> 是一组同类pod对外访问接口，借助service，应用可以方便地实现服务发现和负载均衡
>
> 主要的流量负载组建分别为service（4层路由）和ingress（7层路由）

```shell
# 创建service
kubectl expose deployment nginx --name=service-nginx --port=80 --target-port=80 --type=ClusterIP -n dev

# 查看service
kubectl get service -n dev

# 删除service
kubectl delete service service-nginx -n dev
```

### service

service类型

- ClusterIP: 设置service的ip地址
- NodePort: 使用宿主机暴露服务给外部调用
- LoadBalancer: 结合外部LB使用
- ExernalName: 在集群内部引入外部服务

**环境准备**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
 name: deployment1
 namespace: dev
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
     ports:
     - containerPort: 80
```

```shell
[root@k8s-master ~]# kubectl create -f 2.yaml
deployment.apps/deployment1 created
# 分别进入各个pod并修改index.html
[root@k8s-master ~]# kubectl exec -it deployment1-5ffc5bf56c-hk9mc -n dev /bin/sh
kubectl exec [POD] [COMMAND] is DEPRECATED and will be removed in a future version. Use kubectl exec [POD] -- [COMMAND] instead.
# echo "1" > /usr/share/nginx/html/index.html

# 确认index.html是否修改成功
[root@k8s-master ~]# kubectl get pod -n dev -o wide
NAME                           READY   STATUS    RESTARTS   AGE     IP            NODE        NOMINATED NODE   READINESS GATES
deployment1-5ffc5bf56c-hk9mc   1/1     Running   0          5m14s   10.244.2.53   k8s-node2   <none>           <none>
deployment1-5ffc5bf56c-qbsrt   1/1     Running   0          5m14s   10.244.1.28   k8s-node1   <none>           <none>
deployment1-5ffc5bf56c-s5lfb   1/1     Running   0          5m14s   10.244.2.52   k8s-node2   <none>           <none>
[root@k8s-master ~]# curl 10.244.2.53
1
```

**ClusterIP**

```yaml
apiVersion: v1
kind: Service
metadata:
 name: service1
 namespace: dev
spec:
 # 客户端地址会话保持莫斯
 # 如果不指定使用默认，随机、轮询
 #sessionAffinity: ClientIP
 selector:
  app: nginx-pod # 使用标签绑定service到指定的pod
 clusterIP: 10.1.97.97 # 不指定时，service会随机分配一个ip地址
 type: ClusterIP
 ports:
 - port: 81 # service端口
   targetPort: 80 # pod端口
```

```shell
[root@k8s-master ~]# kubectl create -f 1.yaml
service/service1 created
[root@k8s-master ~]# kubectl get service -n dev -o wide
NAME       TYPE        CLUSTER-IP   EXTERNAL-IP   PORT(S)   AGE   SELECTOR
service1   ClusterIP   10.1.97.97   <none>        81/TCP    41s   app=nginx-pod
# 测试ClusterIP会随机分配pod服务请求
[root@k8s-master ~]# while true;do curl 10.1.97.97:81;sleep 5;done;
2
1
1
3
[root@k8s-master ~]# kubectl delete -f 1.yaml 
service "service1" deleted
```

**headless(无头服务)**

> headless服务是通过service的dns解析访问相应的pod，例如下面例子：在busybox pod中通过headless-service无头服务名称就能够访问两个nginx pod endpoints。

```yaml
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
```

```shell
[root@k8s-master ~]# kubectl get pod
NAME                                         READY   STATUS    RESTARTS   AGE
headless-deployment-busybox-b9db9bbb-vsrvm   1/1     Running   0          4m36s
headless-deployment1-5ffc5bf56c-njvcl        1/1     Running   0          4m36s
headless-deployment2-5ffc5bf56c-786mm        1/1     Running   0          4m36s
nfs-client-provisioner-859477c96c-stc5k      1/1     Running   0          117m
web-0                                        1/1     Running   0          106m
web-1                                        1/1     Running   0          106m
web-2                                        1/1     Running   0          106m
# 进入busybox容器测试headless service
[root@k8s-master ~]# kubectl exec -it headless-deployment-busybox-b9db9bbb-vsrvm /bin/sh
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

# 使用dig命令解析headless service dns到对应的pod ip地址
[root@k8s-master ~]# dig @10.1.0.10 headless-service.default.svc.cluster.local

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
headless-service.default.svc.cluster.local. 30 IN A 10.244.2.91
headless-service.default.svc.cluster.local. 30 IN A 10.244.2.90

;; Query time: 1 msec
;; SERVER: 10.1.0.10#53(10.1.0.10)
;; WHEN: Thu Dec 15 13:18:58 CST 2022
;; MSG SIZE  rcvd: 187
```

**NodePort**

> 暴露服务到外部访问

```yaml
apiVersion: v1
kind: Service
metadata:
 name: service1
 namespace: dev
spec:
 selector:
  app: nginx-pod # 使用标签绑定service到指定的pod
 type: NodePort
 ports:
 - port: 81 # service端口
   targetPort: 80 # pod端口 
   nodePort: 30002 # 宿主机端口，如果不指定service会随机分配一个端口
```

```shell
[root@k8s-master ~]# kubectl create -f 1.yaml 
service/service1 created

# 使用外部浏览器访问 http://192.168.1.xxx:30002

[root@k8s-master ~]# kubectl delete -f 1.yaml 
service "service1" deleted
```

**LoadBalancer**

todo

**ExternalName**

todo

> 在集群内存引入外部服务

### ingress

todo

## 数据存储(Volume)

### 简单存储

#### EmptyDir

> pod创建时会自动创建一个空的目录，无需指定宿主机目录，因为k8s系统会自动分配一个目录，在pod销毁时，emptydir中的数据也会被永久删除。

```yaml
apiVersion: v1
kind: Pod 
metadata:
 name: pod1
 namespace: dev 
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
```

```shell
[root@k8s-master ~]# kubectl get pod -n dev -o wide
NAME   READY   STATUS    RESTARTS   AGE   IP            NODE        NOMINATED NODE   READINESS GATES
pod1   2/2     Running   0          96s   10.244.2.55   k8s-node2   <none>           <none>
[root@k8s-master ~]# curl 10.244.2.55
<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
<style>
    body {
        width: 35em;
        margin: 0 auto;
        font-family: Tahoma, Verdana, Arial, sans-serif;
    }
</style>
</head>
<body>
<h1>Welcome to nginx!</h1>
<p>If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.</p>

<p>For online documentation and support please refer to
<a href="http://nginx.org/">nginx.org</a>.<br/>
Commercial support is available at
<a href="http://nginx.com/">nginx.com</a>.</p>

<p><em>Thank you for using nginx.</em></p>
</body>
</html>

# 查看busybox日志输出
[root@k8s-master ~]# kubectl logs -f pod1 -c busybox -n dev
10.244.0.0 - - [09/Dec/2022:06:02:47 +0000] "GET / HTTP/1.1" 200 612 "-" "curl/7.29.0" "-"
```

#### HostPath

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: pod1
 namespace: dev
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
   hostPath:
    path: /root/logs
    type: DirectoryOrCreate # 目录不存在就创建
```

```shell
[root@k8s-master ~]# kubectl create -f 1.yaml 
pod/pod1 created

[root@k8s-master ~]# kubectl logs -f pod1 -c busybox -n dev
10.244.0.0 - - [09/Dec/2022:07:13:50 +0000] "GET / HTTP/1.1" 200 612 "-" "curl/7.29.0" "-"

[root@k8s-master ~]# kubectl get pod -n dev -o wide
NAME   READY   STATUS    RESTARTS   AGE     IP            NODE        NOMINATED NODE   READINESS GATES
pod1   2/2     Running   0          2m22s   10.244.2.56   k8s-node2   <none>           <none>
[root@k8s-master ~]# curl 10.244.2.56
<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
<style>
    body {
        width: 35em;
        margin: 0 auto;
        font-family: Tahoma, Verdana, Arial, sans-serif;
    }
</style>
</head>
<body>
<h1>Welcome to nginx!</h1>
<p>If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.</p>

<p>For online documentation and support please refer to
<a href="http://nginx.org/">nginx.org</a>.<br/>
Commercial support is available at
<a href="http://nginx.com/">nginx.com</a>.</p>

<p><em>Thank you for using nginx.</em></p>
</body>
</html>
```

#### NFS

```shell
# 配置nfs服务
[root@k8s-master ~]# yum install nfs-utils -y
[root@k8s-master ~]# systemctl start nfs-server
[root@k8s-master ~]# systemctl enable nfs-server
Created symlink from /etc/systemd/system/multi-user.target.wants/nfs-server.service to /usr/lib/systemd/system/nfs-server.service.
[root@k8s-master ~]# mkdir /data
# /etc/exports添加 /data *(rw,sync,no_root_squash,no_subtree_check)
[root@k8s-master ~]# vim /etc/exports
[root@k8s-master ~]# exportfs -a
```

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: pod1
 namespace: dev
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
   nfs:
    server: 192.168.1.170
    path: /data
```

```shell
[root@k8s-master ~]# kubectl create -f 1.yaml 
pod/pod1 created
[root@k8s-master ~]# kubectl get pod -n dev -o wide
NAME   READY   STATUS    RESTARTS   AGE   IP            NODE        NOMINATED NODE   READINESS GATES
pod1   2/2     Running   0          23s   10.244.2.58   k8s-node2   <none>           <none>
[root@k8s-master ~]# curl 10.244.2.58
<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
<style>
    body {
        width: 35em;
        margin: 0 auto;
        font-family: Tahoma, Verdana, Arial, sans-serif;
    }
</style>
</head>
<body>
<h1>Welcome to nginx!</h1>
<p>If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.</p>

<p>For online documentation and support please refer to
<a href="http://nginx.org/">nginx.org</a>.<br/>
Commercial support is available at
<a href="http://nginx.com/">nginx.com</a>.</p>

<p><em>Thank you for using nginx.</em></p>
</body>
</html>

[root@k8s-master ~]# kubectl logs -f pod1 -c busybox -n dev
10.244.0.0 - - [09/Dec/2022:12:32:20 +0000] "GET / HTTP/1.1" 200 612 "-" "curl/7.29.0" "-"
```

### 高级存储

#### pv和pvc

> pv(Persistent Volume)是持久化卷的意思，是对底层共享存储的一种抽象。
>
> pvc(Persistent Volume Claim)是持久卷声明的意思，是用户对于存储需求的一种声明。换句话说，pvc其实是用户向k8s系统发出一种资源需求申请。

**创建nfs**

```shell
# 分别在三个节点上安装nfs-utils
[root@k8s-master ~]# yum install nfs-utils -y
# 在master节点上启动nfs-server
[root@k8s-master ~]# systemctl start nfs-server
[root@k8s-master ~]# systemctl enable nfs-server
# 在master节点创建三个pv目录
[root@k8s-master ~]# mkdir /data/{pv1,pv2,pv3} -pv
mkdir: created directory ‘/data/pv1’
mkdir: created directory ‘/data/pv2’
mkdir: created directory ‘/data/pv3’
# 编辑/etc/exports加入如下内容
[root@k8s-master ~]# cat /etc/exports
/data/pv1 *(rw,sync,no_root_squash,no_subtree_check)
/data/pv2 *(rw,sync,no_root_squash,no_subtree_check)
/data/pv3 *(rw,sync,no_root_squash,no_subtree_check)
# 重启nfs-server服务
[root@k8s-master ~]# systemctl restart nfs-server
# 显示被nfs export的目录
[root@k8s-master ~]# showmount -e
Export list for k8s-master:
/datass   *
/data/pv3 *
/data/pv2 *
/data/pv1 *
```

**创建pv**

```yaml
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
  path: /data/pv1
  server: 192.168.1.170
```

```shell
[root@k8s-master ~]# kubectl create -f 1.yaml 
persistentvolume/pv1 created
[root@k8s-master ~]# kubectl get persistentvolume
NAME   CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS      CLAIM   STORAGECLASS   REASON   AGE
pv1    2Gi        RWX            Retain           Available                                   19s
```

**创建pvc**

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
 name: pvc1
 namespace: dev
spec:
 accessModes:
 - ReadWriteMany
 resources:
  requests:
   storage: 1Gi
```

```shell
[root@k8s-master ~]# kubectl create -f 2.yaml 
persistentvolumeclaim/pvc1 created
[root@k8s-master ~]# kubectl get persistentvolumeclaim -n dev
NAME   STATUS   VOLUME   CAPACITY   ACCESS MODES   STORAGECLASS   AGE
pvc1   Bound    pv1      2Gi        RWX                           9m39s
```

**pod使用pvc**

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: pod1
 namespace: dev
spec:
 containers:
 - name: busybox
   image: busybox
   command: ["/bin/sh", "-c", "while true;do echo pod1 >> /root/out.txt; sleep 10; done;"]
   volumeMounts:
   - name: volume
     mountPath: /root/
 volumes:
 - name: volume
   persistentVolumeClaim:
    claimName: pvc1
    readOnly: false
```

```shell
# 使用pvc
[root@k8s-master ~]# kubectl create -f pod.yaml 
pod/pod1 created
[root@k8s-master pv1]# tail -f out.txt 
pod1
pod1
pod1
```

#### storageclass

> 根据pvc自动创建pv
>
> [链接1](https://github.com/kubernetes-sigs/nfs-subdir-external-provisioner/blob/master/deploy/test-claim.yaml) [链接2](https://zahui.fan/posts/179eb842/)

**参考pv和pvc章节配置nfs服务器**

**创建rbac.yarml**

```yaml
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
```

```shell
[root@k8s-master ~]# kubectl create -f rbac.yaml 
serviceaccount/nfs-client-provisioner created
clusterrole.rbac.authorization.k8s.io/nfs-client-provisioner-runner created
clusterrolebinding.rbac.authorization.k8s.io/run-nfs-client-provisioner created
role.rbac.authorization.k8s.io/leader-locking-nfs-client-provisioner created
rolebinding.rbac.authorization.k8s.io/leader-locking-nfs-client-provisioner created
```

**创建deployment.yaml**

```yaml
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
              value: 192.168.1.170
            - name: NFS_PATH
              value: /datass
      volumes:
        - name: nfs-client-root
          nfs:
            server: 192.168.1.170
            path: /datass
```

```shell
[root@k8s-master ~]# kubectl create -f deployment.yaml 
deployment.apps/nfs-client-provisioner created
```

**创建storageclass.yaml**

```yaml
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

```shell
[root@k8s-master ~]# kubectl create -f storageclass.yaml 
storageclass.storage.k8s.io/nfs-client created
```

**使用创建test-claim.yaml测试**

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
      storage: 1Mi
```

```shell
[root@k8s-master ~]# kubectl create -f test-claim.yaml 
persistentvolumeclaim/test-claim created
[root@k8s-master ~]# kubectl get pvc
NAME         STATUS   VOLUME                                     CAPACITY   ACCESS MODES   STORAGECLASS   AGE
test-claim   Bound    pvc-34bc5c37-2507-4c66-b470-76f199fc07f9   1Mi        RWX            nfs-client     8s
```



### 配置存储

#### configmap

**键值对存储**

```shell
[root@k8s-master ~]# cat 1.yaml 
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
       
[root@k8s-master ~]# kubectl get configmap
NAME               DATA   AGE
configmap1         1      2m15s
kube-root-ca.crt   1      10d
# 显示configmap详细信息
[root@k8s-master ~]# kubectl describe configmap configmap1
Name:         configmap1
Namespace:    default
Labels:       <none>
Annotations:  <none>

Data
====
1.properties:
----
username: admin
password: 123456

Events:  <none>
# 进入pod查看1.properties
[root@k8s-master ~]# kubectl exec -it pod1 /bin/sh
kubectl exec [POD] [COMMAND] is DEPRECATED and will be removed in a future version. Use kubectl exec [POD] -- [COMMAND] instead.
/ # ls
2repath.properties  dev                 home                root                tmp                 var
bin                 etc                 proc                sys                 usr
/ # cat 2repath.properties 
key1: value1
key2: value2
/ # ls /root/
2repath.properties
/ # cat /root/2repath.properties 
key1: value1
key2: value2
/ # 
```

**nginx.conf配置存储**

```yaml
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
```

```shell
# NOTE: 使用下面命令获取yaml文件内的nginx.conf内容
# 否则直接复制粘贴nginx.conf内容到yaml会报告yaml文件格式错误
# https://stackoverflow.com/questions/51268488/kubernetes-configmap-set-from-file-in-yaml-configuration
[root@k8s-master ~]# kubectl create configmap --dry-run=client somename --from-file=nginx.conf --output yaml
apiVersion: v1
data:
  nginx.conf: |
    #user  nobody;
    #worker_processes  1;
    worker_rlimit_nofile 65535;
......

# 查看configmap
[root@k8s-master ~]# kubectl get configmap
NAME               DATA   AGE
configmap1         1      10s
kube-root-ca.crt   1      10d
[root@k8s-master ~]# kubectl describe configmap configmap1
Name:         configmap1
Namespace:    default
Labels:       <none>
Annotations:  <none>

Data
====
nginx.conf:
----
#user  nobody;
  #worker_processes  1;
  worker_rlimit_nofile 65535;
......

# 进入容器查看nginx.conf
[root@k8s-master ~]# kubectl exec -it pod1 /bin/sh
kubectl exec [POD] [COMMAND] is DEPRECATED and will be removed in a future version. Use kubectl exec [POD] -- [COMMAND] instead.
/ # cat /root/nginx.conf 
#user  nobody;
  #worker_processes  1;
  worker_rlimit_nofile 65535;
......
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

