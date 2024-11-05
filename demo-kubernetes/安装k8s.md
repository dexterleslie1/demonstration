# 安装`kubernetes`



## 使用`minikube`安装`k8s`

> [minikube start](https://minikube.sigs.k8s.io/docs/start/)

注意：todo `2024/07/30`无法使用`minikube`在`centOS8 stream`上成功运行`k8s`，报告下载`minikube`相关`k8s`镜像失败导致的结果。

`minikube`使用`docker`容器环境启动一个名为`minikube`的容器，这个容器内会运行`k8s`所有组件。`minikube`同时又是一个命令行工具，`minikube`支持在`ubuntu`、`centOS8`部署`k8s`

### 使用`minikube`创建`k8s`集群

- 使用`dcli`安装`docker`环境

  ```bash
  # 下载dcli
  sudo rm -f /usr/bin/dcli && sudo curl https://fut001.oss-cn-hangzhou.aliyuncs.com/dcli/dcli-linux-x86_64 --output /usr/bin/dcli && sudo chmod +x /usr/bin/dcli
  
  # 安装docker环境
  sudo dcli docker install
  ```

- 设置`mimikube cli`

  ```bash
  curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
  
  sudo install minikube-linux-amd64 /usr/local/bin/minikube
  ```

- 使用`minikube`启动`k8s`环境

  > 注意：在`root`用户下启动`k8s`需要使用`--force`，指定启动`v1.23.8`版本`k8s`，否则不能成功启动。[参考链接](https://github.com/kubernetes/minikube/issues/14477)

  ```bash
  minikube start --kubernetes-version=v1.23.8 --force
  ```

- 验证集群是否正常通过查询所有`pods`

  ```bash
  minikube kubectl -- get po -A
  ```

- 配置`kubectl`命令

  ```bash
  alias kubectl="minikube kubectl --"
  ```

- 测试部署`pod`

  ```bash
  kubectl create deployment hello-minikube --image=kicbase/echo-server:1.0
  kubectl expose deployment hello-minikube --type=NodePort --port=8080
  kubectl get services hello-minikube
  kubectl port-forward service/hello-minikube 7080:8080
  ```

- 访问服务

  ```bash
  curl http://localhost:7080/
  ```

  

### 使用`minikube`配置`k8s dashboard`

> [access local Kubernetes minikube dashboard remotely?](https://stackoverflow.com/questions/47173463/how-to-access-local-kubernetes-minikube-dashboard-remotely)

- 获取本地`dashboard url`

  ```bash
  minikube dashboard --url
  ```

- 使用`kubectl`代理本地`dashboard`以便外部访问`dashboard`

  ```bash
  kubectl proxy --address='0.0.0.0' --disable-filter=true
  ```

- 打开浏览器访问`dashboard`，`http://external-ip:37337/api/v1/namespaces/kubernetes-dashboard/services/http:kubernetes-dashboard:/proxy/`



## 使用二进制程序安装`k8s`

### 安装前准备

`master`和`worker`节点都需要先使用`dcli`程序安装`docker`环境

```bash
# 安装dcli程序
sudo rm -f /usr/bin/dcli && sudo curl https://fut001.oss-cn-hangzhou.aliyuncs.com/dcli/dcli-linux-x86_64 --output /usr/bin/dcli && sudo chmod +x /usr/bin/dcli

# 安装docker，选择版本26.1.1-1
dcli docker install
```



### `centOS8`平台安装`k8s`

#### 安装`master`节点

关闭`swap`，否则`kubelet`服务不能运行，删除`/etc/fstab`文件中的文件类型为`swap`的记录并重启系统即可关闭`swap`

设置主机名称

```bash
hostnamectl set-hostname k8s-master
```

设置静态`ip`地址

配置`/etc/hosts`，否则`kubelet`服务不能运行

```bash
# x.x.x.x是k8s-master ip地址
x.x.x.x k8s-master
```

配置`k8s yum`源，编辑`/etc/yum.repos.d/kubernetes.repo`内容如下：

```properties
[kubernetes]
name=Kubernetes
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64
enabled=1
gpgcheck=0
repo_gpgcheck=0
gpgkey=https://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg
# https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
```

使用`yum`安装`kubelet-1.23.0`、`kubeadm-1.23.0`、`kubectl-1.23.0`

```bash
yum install kubelet-1.23.0 kubeadm-1.23.0 kubectl-1.23.0 -y
```

`systemctl enable kubelet`

```bash
systemctl enable kubelet
```

执行`kubeadm init`初始化`master`节点，注意：如果`kubeadm init`命令执行失败，可以通过命令`kubeadm reset`重置`k8s`集群后再重新执行`kubeadm init`命令。

```bash
kubeadm init \
    --apiserver-advertise-address=x.x.x.x \
    --image-repository registry.aliyuncs.com/google_containers \
    --kubernetes-version v1.23.0 \
    --service-cidr=10.1.0.0/16 \
    --pod-network-cidr=10.244.0.0/16
```

配置`kubectl`命令运行环境

```bash
mkdir -p $HOME/.kube \
&& sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config \
&& sudo chown $(id -u):$(id -g) $HOME/.kube/config
```

启动`flannel`网络

```bash
kubectl apply -f kube-flannel.yml
```

`kube-flannel.yaml`内容如下：

```yaml
---
kind: Namespace
apiVersion: v1
metadata:
  name: kube-flannel
  labels:
    pod-security.kubernetes.io/enforce: privileged
---
kind: ClusterRole
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: flannel
rules:
- apiGroups:
  - ""
  resources:
  - pods
  verbs:
  - get
- apiGroups:
  - ""
  resources:
  - nodes
  verbs:
  - get
  - list
  - watch
- apiGroups:
  - ""
  resources:
  - nodes/status
  verbs:
  - patch
---
kind: ClusterRoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: flannel
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: flannel
subjects:
- kind: ServiceAccount
  name: flannel
  namespace: kube-flannel
---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: flannel
  namespace: kube-flannel
---
kind: ConfigMap
apiVersion: v1
metadata:
  name: kube-flannel-cfg
  namespace: kube-flannel
  labels:
    tier: node
    app: flannel
data:
  cni-conf.json: |
    {
      "name": "cbr0",
      "cniVersion": "0.3.1",
      "plugins": [
        {
          "type": "flannel",
          "delegate": {
            "hairpinMode": true,
            "isDefaultGateway": true
          }
        },
        {
          "type": "portmap",
          "capabilities": {
            "portMappings": true
          }
        }
      ]
    }
  net-conf.json: |
    {
      "Network": "10.244.0.0/16",
      "Backend": {
        "Type": "vxlan"
      }
    }
---
apiVersion: apps/v1
kind: DaemonSet
metadata:
  name: kube-flannel-ds
  namespace: kube-flannel
  labels:
    tier: node
    app: flannel
spec:
  selector:
    matchLabels:
      app: flannel
  template:
    metadata:
      labels:
        tier: node
        app: flannel
    spec:
      affinity:
        nodeAffinity:
          requiredDuringSchedulingIgnoredDuringExecution:
            nodeSelectorTerms:
            - matchExpressions:
              - key: kubernetes.io/os
                operator: In
                values:
                - linux
      hostNetwork: true
      priorityClassName: system-node-critical
      tolerations:
      - operator: Exists
        effect: NoSchedule
      serviceAccountName: flannel
      initContainers:
      - name: install-cni-plugin
       #image: flannelcni/flannel-cni-plugin:v1.1.0 for ppc64le and mips64le (dockerhub limitations may apply)
        image: docker.io/rancher/mirrored-flannelcni-flannel-cni-plugin:v1.1.0
        command:
        - cp
        args:
        - -f
        - /flannel
        - /opt/cni/bin/flannel
        volumeMounts:
        - name: cni-plugin
          mountPath: /opt/cni/bin
      - name: install-cni
       #image: flannelcni/flannel:v0.20.2 for ppc64le and mips64le (dockerhub limitations may apply)
        image: docker.io/rancher/mirrored-flannelcni-flannel:v0.20.2
        command:
        - cp
        args:
        - -f
        - /etc/kube-flannel/cni-conf.json
        - /etc/cni/net.d/10-flannel.conflist
        volumeMounts:
        - name: cni
          mountPath: /etc/cni/net.d
        - name: flannel-cfg
          mountPath: /etc/kube-flannel/
      containers:
      - name: kube-flannel
       #image: flannelcni/flannel:v0.20.2 for ppc64le and mips64le (dockerhub limitations may apply)
        image: docker.io/rancher/mirrored-flannelcni-flannel:v0.20.2
        command:
        - /opt/bin/flanneld
        args:
        - --ip-masq
        - --kube-subnet-mgr
        resources:
          requests:
            cpu: "100m"
            memory: "50Mi"
          limits:
            cpu: "100m"
            memory: "50Mi"
        securityContext:
          privileged: false
          capabilities:
            add: ["NET_ADMIN", "NET_RAW"]
        env:
        - name: POD_NAME
          valueFrom:
            fieldRef:
              fieldPath: metadata.name
        - name: POD_NAMESPACE
          valueFrom:
            fieldRef:
              fieldPath: metadata.namespace
        - name: EVENT_QUEUE_DEPTH
          value: "5000"
        volumeMounts:
        - name: run
          mountPath: /run/flannel
        - name: flannel-cfg
          mountPath: /etc/kube-flannel/
        - name: xtables-lock
          mountPath: /run/xtables.lock
      volumes:
      - name: run
        hostPath:
          path: /run/flannel
      - name: cni-plugin
        hostPath:
          path: /opt/cni/bin
      - name: cni
        hostPath:
          path: /etc/cni/net.d
      - name: flannel-cfg
        configMap:
          name: kube-flannel-cfg
      - name: xtables-lock
        hostPath:
          path: /run/xtables.lock
          type: FileOrCreate
```


#### 安装`worker`节点

关闭`swap`，否则`kubelet`服务不能运行，删除`/etc/fstab`文件中的文件类型为`swap`的记录并重启系统即可关闭`swap`

设置主机名称

```bash
hostnamectl set-hostname k8s-node1
hostnamectl set-hostname k8s-node2
```

设置静态`ip`地址

配置`k8s yum`源，编辑`/etc/yum.repos.d/kubernetes.repo`内容如下：

```properties
[kubernetes]
name=Kubernetes
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64
enabled=1
gpgcheck=0
repo_gpgcheck=0
gpgkey=https://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg
# https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
```

使用`yum`安装`kubelet-1.23.0`、`kubeadm-1.23.0`、`kubectl-1.23.0`

```bash
yum install kubelet-1.23.0 kubeadm-1.23.0 kubectl-1.23.0 -y
```

`systemctl enable kubelet`

```bash
systemctl enable kubelet
```

在`master`节点执行下面命令获取`kubeadm join`命令用于`worker`节点加入`k8s`集群

```bash
kubeadm token create --print-join-command
```

在`worker`节点执行上面命令获取的`kubeadm join`命令即可把`worker`节点加入到`k8s`集群，注意：执行命令后需要等待几分钟`worker`节点加入集群并`Ready`状态。



### 检查`k8s`服务是否正常

在`master`节点查看基础容器运行状态

```bash
kubectl get pods -n kube-system
```

在`master`节点查看所有节点状态，注意：刚刚安装完的环境需要等待几分钟节点到`Ready`状态。

```bash
kubectl get nodes
```

部署`nginx`服务，注意：需要安装`worker`节点才能够运行`nginx`，否则`nginx`无法调度。

```bash
kubectl create deployment nginx --image=nginx
```

使用`NodePort`方式暴露`nginx`服务，其中`target-port`为容器中应用监听的`port`，`port`为服务通过服务集群`ip`地址访问的`port`

```bash
kubectl expose deployment nginx --target-port=80 --port=80 --type=NodePort --overrides '{ "apiVersion": "v1","spec":{"ports":[{"port":80,"protocol":"TCP","targetPort":80,"nodePort":30000}]}}'
```

查看`nginx NodePort`端口并使用浏览器访问成功

```bash
kubectl get pod,service
```

使用`curl`测试`nginx`是否正常

```bash
curl localhost:30000
```

