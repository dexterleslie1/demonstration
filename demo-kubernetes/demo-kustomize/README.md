# kustomize用法



## 安装kustomize

> 参考
> https://kubectl.docs.kubernetes.io/installation/kustomize/binaries/

```shell
# 下载kustomize二进制程序
# https://github.com/kubernetes-sigs/kustomize/releases

# 安装
sudo curl --output /tmp/kustomize_v5.3.0_linux_amd64.tar.gz https://bucketxyh.oss-cn-hongkong.aliyuncs.com/kubernetes/kustomize_v5.3.0_linux_amd64.tar.gz && sudo tar -xvzf /tmp/kustomize_v5.3.0_linux_amd64.tar.gz -C /usr/local/bin
```



## 使用 kubectl apply -k . 运行 kustomize 配置

> 参考
> https://kubectl.docs.kubernetes.io/references/kubectl/apply/
> https://kubectl.docs.kubernetes.io/guides/config_management/apply/

```shell
# service.yaml
apiVersion: v1
kind: Service
metadata:
  name: myservice1
spec:
  type: NodePort
  ports:
    - port: 80 # 服务端口80
      targetPort: 80
      nodePort: 30000 # NodePort端口30000
  selector:
    app: kubia
    
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: deployment1
spec:
  replicas: 1
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
        image: nginx
        imagePullPolicy: IfNotPresent

# kustomization.yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
# list of Resource Config to be Applied
resources:
  - deployment.yaml
  - service.yaml
# namespace to deploy all Resources to
namespace: default
# labels added to all Resources
commonLabels:
  app: example
  env: test

# 创建 service 和 deployment
kubectl apply -k .

# 删除 service 和 deployment
kubectl delete -k .
```



## kustomize build 命令

> https://kubectl.docs.kubernetes.io/guides/introduction/kustomize/

```shell
# service.yaml
apiVersion: v1
kind: Service
metadata:
  name: myservice1
spec:
  type: NodePort
  ports:
    - port: 80 # 服务端口80
      targetPort: 80
      nodePort: 30000 # NodePort端口30000
  selector:
    app: kubia
    
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: deployment1
spec:
  replicas: 1
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
        image: nginx
        imagePullPolicy: IfNotPresent

# kustomization.yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
# list of Resource Config to be Applied
resources:
  - deployment.yaml
  - service.yaml
# namespace to deploy all Resources to
namespace: default
# labels added to all Resources
commonLabels:
  app: example
  env: test
  
# 创建 service 和 deployment
kustomize build . | kubectl apply -f -

# 删除 service 和 deployment
kustomize build . | kubectl delete -f -
```

