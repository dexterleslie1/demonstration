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



## 使用 kubectl apply -k . 应用 kustomize 配置

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



## 内置的 transformers 和 generators

### annotation

#### 参考

https://kubectl.docs.kubernetes.io/references/kustomize/builtins/#_annotationtransformer_

#### 通过 kustomization.yaml 的 commonAnnotations 字段

deployment.yaml 内容如下：

```yaml
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
```

使用 kustomization.yaml 添加 annotation，只能够添加 annotation 到所有资源不能指定添加到某个资源，   kustomization.yaml 内容如下：

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
  - deployment.yaml
# namespace to deploy all Resources to
namespace: default
# labels added to all Resources
commonAnnotations:
  a1: v1
  a2: v2
```

查看 kustomize 生成输出

```shell
kustomize build .
```



#### 通过 transformers 字段 kind=AnnotationsTransformer

deployment.yaml 内容如下：

```yaml
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
```

kustomize.yaml 内容如下：

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
  - deployment.yaml
# namespace to deploy all Resources to
namespace: default
transformers:
- |-
  apiVersion: builtin
  kind: AnnotationsTransformer
  metadata:
    name: not-important-to-example
  annotations:
    a1: v1
    a2: v2
  fieldSpecs:
  - path: metadata/annotations
    create: true
```

查看 kustomize 生成输出

```shell
kustomize build .
```



### ConfigMapGenerator

#### 参考

https://kubectl.docs.kubernetes.io/references/kustomize/builtins/#_configmapgenerator_

#### 通过 kustomization.yaml 的 configMapGenerator 字段

##### 通过加载文件或者 literal 创建 configMap

application.properties 内容如下：

```properties
k1=v1
```

more.properties 内容如下：

```properties
k2=v2
```

mydashboard.json 内容如下：

```json
{"k3":"v3"}
```

kustomization.yaml 内容如下：

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
# namespace to deploy all Resources to
namespace: default

# These labels are added to all configmaps and secrets.
generatorOptions:
  labels:
    fruit: apple

configMapGenerator:
- name: my-java-server-props
  files:
  - application.properties
  - more.properties
- name: my-java-server-env-vars
  literals: 
  - JAVA_HOME=/opt/java/jdk
  - JAVA_TOOL_OPTIONS=-agentlib:hprof
  options:
    disableNameSuffixHash: true
    labels:
      pet: dog
- name: dashboards
  files:
  - mydashboard.json
  options:
    annotations:
      dashboard: "1"
    labels:
      app.kubernetes.io/name: "app1"
```

查看 kustomize 生成输出

```shell
kustomize build .
```



##### 支持创建configMap时重命名导入的文件

whatever.ini 内容如下：

```ini
k1=v1
```

kustomization.yaml 内容如下：

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
# namespace to deploy all Resources to
namespace: default

configMapGenerator:
- name: app-whatever
  files:
  - myFileName.ini=whatever.ini
```

查看 kustomize 生成输出

```shell
kustomize build .
```

#### 通过 generators 字段 kind=ConfigMapGenerator

devops.env 内容如下：

```ini
e1=v1
```

uxteam.env 内容如下：

```ini
e2=v2
```

kustomization.yaml 内容如下：

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
# namespace to deploy all Resources to
namespace: default

generators:
- |-
  apiVersion: builtin
  kind: ConfigMapGenerator
  metadata:
    name: mymap
  envs:
  - devops.env
  - uxteam.env
  literals:
  - FRUIT=apple
  - VEGETABLE=carrot
```

查看 kustomize 生成输出

```shell
kustomize build .
```



### PatchesJson6902

#### 参考

https://kubectl.docs.kubernetes.io/references/kustomize/builtins/#_patchesjson6902_

#### 通过 kustomization.yaml 的 patchesJson6902 字段

##### patch 内嵌字符串方式

deployment.yaml 内容如下：

```yaml
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
```

kustomization.yaml 内容如下：

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
# namespace to deploy all Resources to
namespace: default

resources:
- deployment.yaml

patches:
- target:
    version: v1
    kind: Deployment
    name: deployment1
  patch: |-
    # 添加 a1=v1 annotation
    - op: add
      path: /metadata/annotations/a1
      value: v1
    # 替换 deployment metadata.name
    - op: replace
      path: /metadata/name
      value: new-deployment
    # 替换 deployment 镜像
    # https://medium.com/@giorgiodevops/kustomize-use-patches-to-add-or-override-resources-48ef65cb634c
    - op: replace
      path: /spec/template/spec/containers/0/image
      value: busybox
```

查看 kustomize 生成输出

```shell
kustomize build .
```



##### patch yaml 文件方式

deployment.yaml 内容如下：

```yaml
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
```

deployment-var.yaml 内容如下：

```yaml
# 添加 a1=v1 annotation
- op: add
  path: /metadata/annotations/a1
  value: v1
# 替换 deployment metadata.name
- op: replace
  path: /metadata/name
  value: new-deployment
# 替换 deployment 镜像
- op: replace
  path: /spec/template/spec/containers/0/image
  value: busybox
```

kustomization.yaml 内容如下：

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
# namespace to deploy all Resources to
namespace: default

resources:
- deployment.yaml

patches:
- target:
    version: v1
    kind: Deployment
    name: deployment1
  path: deployment-var.yaml
```

查看 kustomize 生成输出

```shell
kustomize build .
```

#### 通过 transformers 字段 kind=PatchJson6902Transformer

deployment.yaml 内容如下：

```yaml
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
```

deployment-var.yaml 内容如下：

```yaml
# 添加 a1=v1 annotation
- op: add
  path: /metadata/annotations/a1
  value: v1
# 替换 deployment metadata.name
- op: replace
  path: /metadata/name
  value: new-deployment
# 替换 deployment 镜像
- op: replace
  path: /spec/template/spec/containers/0/image
  value: busybox
```

kustomization.yaml 内容如下：

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
# namespace to deploy all Resources to
namespace: default

resources:
- deployment.yaml

transformers:
- |-
  apiVersion: builtin
  kind: PatchJson6902Transformer
  metadata:
    name: not-important-to-example
  target:
    group: apps
    version: v1
    kind: Deployment
    name: deployment1
  path: deployment-var.yaml
```

查看 kustomize 生成输出

```shell
kustomize build .
```



### PatchesStrategicMerge

#### 参考

https://kubectl.docs.kubernetes.io/references/kustomize/builtins/#_patchesstrategicmerge_

#### 通过 kustomization.yaml 的 patchesStrategicMerge 字段

##### 内嵌字符串方式

deployment.yaml 内容如下：

```yaml
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
```

kustomization.yaml 内容如下：

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
# namespace to deploy all Resources to
namespace: default

resources:
- deployment.yaml

patchesStrategicMerge:
- |-
  apiVersion: apps/v1
  kind: Deployment
  metadata:
    name: deployment1
  spec:
    template:
      spec:
        containers:
          - name: nginx
            image: busybox
```

查看 kustomize 生成输出

```shell
kustomize build .
```

##### 片段文件方式

deployment.yaml 内容如下：

```yaml
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
```

deployment-slice.yaml 内容如下：

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: deployment1
spec:
  template:
    spec:
      containers:
        - name: nginx
          image: busybox
```

kustomization.yaml 内容如下：

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
# namespace to deploy all Resources to
namespace: default

resources:
- deployment.yaml

patchesStrategicMerge:
- deployment-slice.yaml
```

查看 kustomize 生成输出

```shell
kustomize build .
```

#### 通过 transformers 字段 kind=PatchStrategicMergeTransformer

deployment.yaml 内容如下：

```yaml
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
```

patch.yaml 内容如下：

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: deployment1
spec:
  template:
    spec:
      containers:
        - name: nginx
          image: busybox
```

kustomization.yaml 内容如下：

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
# namespace to deploy all Resources to
namespace: default

resources:
- deployment.yaml

transformers:
- |-
  apiVersion: builtin
  kind: PatchStrategicMergeTransformer
  metadata:
    name: not-important-to-example
  paths:
  - patch.yaml
```

查看 kustomize 生成输出

```shell
kustomize build .
```



## kustomize components 模块化

### 参考

https://kubectl.docs.kubernetes.io/guides/config_management/components/

### 演示

#### base 目录和文件如下

base/deployment.yaml 内容如下：

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: example
spec:
  template:
    spec:
      containers:
      - name: example
        image: example:1.0
        volumeMounts:
        - name: conf
          mountPath: /etc/config
      volumes:
        - name: conf
          configMap:
            name: conf
```

base/kustomization.yaml 内容如下：

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
# namespace to deploy all Resources to
namespace: default

resources:
- deployment.yaml

configMapGenerator:
- name: conf
  literals:
    - main.conf=|
        color=cornflower_blue
        log_level=info
```

#### components 目录和文件如下

**external_db 组件**

components/external_db/configmap.yaml 内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: conf
data:
  db.conf: |
    endpoint=127.0.0.1:1234
    name=app
    user=admin
    pass=/var/run/secrets/db/dbpass.txt
```

components/external_db/dbpass.txt 文件存在但没有内容

components/external_db/deployment.yaml 内容如下：

```yaml
- op: add
  path: /spec/template/spec/volumes/0
  value:
    name: dbpass
    secret:
      secretName: dbpass
- op: add
  path: /spec/template/spec/containers/0/volumeMounts/0
  value:
    mountPath: /var/run/secrets/db/
    name: dbpass
```

components/external_db/kustomization.yaml 内容如下：

```yaml
apiVersion: kustomize.config.k8s.io/v1alpha1  # <-- Component notation
kind: Component

secretGenerator:
- name: dbpass
  files:
    - dbpass.txt

patchesStrategicMerge:
  - configmap.yaml

patchesJson6902:
- target:
    group: apps
    version: v1
    kind: Deployment
    name: example
  path: deployment.yaml
```

**ldap 组件**

components/ldap/configmap.yaml 内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: conf
data:
  ldap.conf: |
    endpoint=ldap://ldap.example.com
    bindDN=cn=admin,dc=example,dc=com
    pass=/var/run/secrets/ldap/ldappass.txt
```

components/ldap/deployment.yaml 内容如下：

```yaml
- op: add
  path: /spec/template/spec/volumes/0
  value:
    name: ldappass
    secret:
      secretName: ldappass
- op: add
  path: /spec/template/spec/containers/0/volumeMounts/0
  value:
    mountPath: /var/run/secrets/ldap/
    name: ldappass
```

components/ldap/kustomization.yaml 内容如下：

```yaml
apiVersion: kustomize.config.k8s.io/v1alpha1
kind: Component

secretGenerator:
- name: ldappass
  files:
    - ldappass.txt

patchesStrategicMerge:
  - configmap.yaml

patchesJson6902:
- target:
    group: apps
    version: v1
    kind: Deployment
    name: example
  path: deployment.yaml
```

**recaptcha 组件**

components/recaptcha/deployment.yaml 内容如下：

```yaml
- op: add
  path: /spec/template/spec/volumes/0
  value:
    name: recaptcha
    secret:
      secretName: recaptcha
- op: add
  path: /spec/template/spec/containers/0/volumeMounts/0
  value:
    mountPath: /var/run/secrets/recaptcha/
    name: recaptcha
```

components/recaptcha/kustomization.yaml 内容如下：

```yaml
apiVersion: kustomize.config.k8s.io/v1alpha1
kind: Component

secretGenerator:
- name: recaptcha
  files:
    - site_key.txt
    - secret_key.txt

# Updating the ConfigMap works with generators as well.
configMapGenerator:
- name: conf
  behavior: merge
  literals:
    - recaptcha.conf=|
        enabled=true
        site_key=/var/run/secrets/recaptcha/site_key.txt
        secret_key=/var/run/secrets/recaptcha/secret_key.txt

patchesJson6902:
- target:
    group: apps
    version: v1
    kind: Deployment
    name: example
  path: deployment.yaml
```

components/recaptcha/scret_key.txt 文件存在但没有内容

components/recaptcha/site_key.txt 文件存在但没有内容

**overlays 组件**

components/overlays/community/kustomization.yaml 内容如下：

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization

resources:
  - ../../base

components:
  - ../../components/external_db
  - ../../components/recaptcha
```

components/overlays/dev/kustomization.yaml 内容如下：

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization

resources:
  - ../../base

components:
  - ../../components/external_db
  #- ../../components/ldap
  - ../../components/recaptcha
```

components/overlays/enterprise/kustomization.yaml 内容如下：

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization

resources:
  - ../../base

components:
  - ../../components/external_db
  - ../../components/ldap
```

**测试不同的 overlays 生成不同版本的配置**

生成 community 版本

```shell
kustomize build overlays/community
```

生成 dev 版本

```shell
kustomize build overlays/dev
```

生成 enterprise 版本

```shell
kustomize build overlays/enterprise
```

