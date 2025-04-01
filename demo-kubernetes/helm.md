# `helm`



## 为何`helm`

传统服务部署到`k8s`流程：`拉取代码`>`打包编译`>`构建镜像`>`准备部署相关的yaml(例如: deployment、service、ingress)`>`kubectl apply部署到k8s`

传统方式不能根据一套`yaml`文件来创建多个环境，需要手动进行修改。例如：一般环境都分为`dev`、预生产、生产环境，部署完了`dev`这套环境，后面再部署预生产和生产环境，还需要复制出两套，并手动修改才行。使用一套`helm`应用包部署多个环境。



## `helm`组件

`chart`包：就是`helm`的一个整合后的`chart`包，包含一个应用的所有`kubernetes`声明模板，类似于`yum`的`rpm`包或者`apt`的`dpkg`文件。

`helm`客户端：`helm`的客户端组件，负责和`k8s apiserver`通信

`helm`仓库：用于发布和存储`chart`包的仓库，类似`yum`仓库或者`docker`仓库

`release`：用`chart`包部署的一个实例。通过`chart`在`k8s`中部署的应用会产生一个唯一的`release`，同一个`chart`部署多次就会生产多个`release`。



## 安装

安装`dcli`命令

```bash
sudo rm -f /usr/bin/dcli && sudo curl https://fut001.oss-cn-hangzhou.aliyuncs.com/dcli/dcli-linux-x86_64 --output /usr/bin/dcli && sudo chmod +x /usr/bin/dcli
```

使用`dcli`安装`helm cli`

```bash
sudo dcli helm install
```



## 搭建`helm`私有仓库

`todo`搜索是否有免费的托管`helm`仓库。

注意：阿里云容器镜像服务企业版才支持`helm`仓库。

注意：使用`harbor`搭建(在`helm push`时候报告错误)，暂时不使用此方案

`helm`仓库`https://helm.sh/docs/topics/chart_repository/`

使用`chartmuseum`搭建私有仓库

- `https://github.com/helm/chartmuseum`
- `https://www.tinfoilcipher.co.uk/2021/04/26/creating-a-private-helm-repo-with-chartmuseum-using-aws-s3/`



使用`docker`运行`chartmuseum`

```bash
docker run \
  -d \
  -p 8080:8080 \
  -e DEBUG=1 \
  -e STORAGE=local \
  -e STORAGE_LOCAL_ROOTDIR=/charts \
  -e BASIC_AUTH_USER=root \
  -e BASIC_AUTH_PASS=123456 \
  -v ~/data-demo-helm-charts:/charts \
  ghcr.io/helm/chartmuseum:v0.16.1
```

授权`~/data-demo-helm-charts`目录写权限

```bash
sudo chmod -R a+w ~/data-demo-helm-charts/
```

添加`chartmuseum`仓库

```bash
helm repo add --username root --password 123456 chartmuseum http://localhost:8080
```

搜索`charts`用于测试`chartmuseum`仓库是否添加成功

```bash
helm search repo chartmuseum/
```

创建`charts`项目测试`chartmuseum`

```bash
mkdir temp-chartmuseum-testing
cd temp-chartmuseum-testing
helm create .
rm -rf templates/*
```

修改`Chart.yaml`中`name`为`mychart`，修改后的`Chart.yaml`内容如下：

```yaml
apiVersion: v2
name: mychart
description: A Helm chart for Kubernetes

# A chart can be either an 'application' or a 'library' chart.
#
# Application charts are a collection of templates that can be packaged into versioned archives
# to be deployed.
#
# Library charts provide useful utilities or functions for the chart developer. They're included as
# a dependency of application charts to inject those utilities and functions into the rendering
# pipeline. Library charts do not define any templates and therefore cannot be deployed.
type: application

# This is the chart version. This version number should be incremented each time you make changes
# to the chart and its templates, including the app version.
# Versions are expected to follow Semantic Versioning (https://semver.org/)
version: 0.1.0

# This is the version number of the application being deployed. This version number should be
# incremented each time you make changes to the application. Versions are not expected to
# follow Semantic Versioning. They should reflect the version the application is using.
# It is recommended to use it with quotes.
appVersion: "1.16.0"
```

新增`templates/1.yaml`内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap
data:
 k1: v1
```

安装推送插件

```bash
helm plugin install https://github.com/chartmuseum/helm-push
```

推送`charts`项目到`chartmuseum`仓库

```bash
helm cm-push . chartmuseum
```

从`chartmuseum`安装`charts`

```sh
helm install chartmuseum/mychart --generate-name
```

卸载`mychart`

```bash
helm uninstall mychart-1704695350
```



## 使用`helm`创建一个`chart`



### 没有变量的`helm`

创建`mychart`项目

```bash
helm create mychart
```

删除`mychart/templates`文件夹下所有文件

```bash
rm -rf templates/*
```

在`mychart/templates`目录下创建`configmap.yaml`，内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: mychart-configmap1
data:
 myvalue: "hello world!"
```

创建`helm release`

```bash
helm install myconfigmap1 .
```

查看`helm release`

```bash
helm list
```

查看`configmap`

```bash
kubectl get configmap
```

查看`helm release`详细信息

```bash
helm get manifest myconfigmap1
```

删除`helm release`

```bash
helm uninstall myconfigmap1
```



### 带变量的`helm`

把上面没有变量的`helm`修改`templates/configmap.yaml`和`values.yaml`

`templates/configmap.yaml`内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: {{ .Release.Name }}-configmap1
data:
 myvalue: {{ .Values.MY_VALUE }}
```

`values.yaml`内容如下：

```yaml
MY_VALUE: "hello world!!"
```

安装

```bash
helm install myconfigmap2 .
```

查询安装列表

```bash
helm list
```

查询`configmap`列表

```bash
kubectl get configmap
```

查看`helm`安装详情

```bash
helm get manifest myconfigmap2
```

卸载

```bash
helm uninstall myconfigmap2
```



## `helm NOTES.txt`用于输出操作说明

创建`mychart`项目

```bash
helm create mychart
```

删除`mychart/templates`文件夹下所有文件

```bash
rm -rf templates/*
```

创建`templates/configmap.yaml`内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: mychart-configmap1
data:
 myvalue: "hello world!"
```

创建`templates/NOTES.txt（NOTES.txt 中可以编写模板语法）`内容如下：

```markdown
CHART NAME: {{ .Chart.Name }}
CHART VERSION: {{ .Chart.Version }}
APP VERSION: {{ .Chart.AppVersion }}

** Please be patient while the chart is being deployed **

Redis&reg; can be accessed on the following DNS names from within your cluster:

    myredis-master.default.svc.cluster.local for read/write operations (port 6379)
    myredis-replicas.default.svc.cluster.local for read-only operations (port 6379)

To get your password run:

    export REDIS_PASSWORD=$(kubectl get secret --namespace default myredis -o jsonpath="{.data.redis-password}" | base64 -d)
```

创建`helm release`

```bash
helm install myconfigmap1 .
```

删除`helm release`

```bash
helm uninstall myconfigmap1
```



## `debug`和`dry-run`

> 不实际执行`helm`，只是调试输出`helm`模板的执行过程

```bash
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



## `{{`和`{{-` 、`}}`和`-}}`的区别

> `{{`会保留左边空格，`{{-`相当于`left trim`，`}}`会保留右边空格，`-}}`相当于`right trim`。`https://stackoverflow.com/questions/69992198/what-is-different-between-and-syntax-in-helm3`

在当前目录创建`helm`项目

```bash
helm create .
```

删除`templates`中的文件

```bash
rm -rf templates/*
```

创建`templates/1.yaml`内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap
data:
 k1: {{"a"}} {{- "b"}}{{"c" -}} {{"d"}} {{- "e" -}} {{"f"}} {{"g"}}
```

调试

```bash
helm install demo1 . --debug --dry-run
```



## 变量



### 调试内置变量

`values.yaml`内容如下：

```yaml
MY_VALUE: "hello world!!"
```

`templates/configmap.yaml`内容如下：

```yaml
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
```

测试

```bash
helm install myconfigmap1 . --debug --dry-run
```

```shell
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



### `$`符号

>`https://helm.sh/docs/chart_template_guide/variables/`



#### 定义局部变量

在当前目录创建`helm`项目

```bash
helm create .
```

删除`templates`中的文件

```bash
rm -rf templates/*
```

创建`templates/1.yaml`内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: {{ .Release.Name }}-configmap
data:
  myvalue: "Hello World"
  {{- $relname := .Release.Name -}}
  {{- with .Values.favorite }}
  drink: {{ .drink | default "tea" | quote }}
  food: {{ .food | upper | quote }}
  release: {{ $relname }}
  {{- end }}
```

`values.yaml`内容如下：

```yaml
favorite:
  drink: "coffee"
  food: "PIZZA"
```

调试

```bash
helm install demo1 . --debug --dry-run
```



#### 在`range`中特殊用法

> 在`range`作用域中想引用全局变量`.Release.Name`时，因为`.Release.Name`中的`.(点号)`此时指代的是当前`item`导致不能成功引用全局变量`.Release.Name`，为了避免冲突使用`$`代替`.(点号)`解决此问题。

在当前目录创建`helm`项目

```bash
helm create .
```

删除`templates`中的文件

```bash
rm -rf templates/*
```

创建`templates/1.yaml`内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: {{ .Release.Name }}-configmap
data:
  {{- range .Values.items }}
  {{ .myK }}: {{ .myV }}
  releaseName: {{ $.Release.Name }}
  {{- end }}
```

`values.yaml`内容如下：

```yaml
items:
- myK: "k1"
  myV: "v1"
- myK: "k2"
  myV: "v2"
```

调试

```bash
helm install demo1 . --debug --dry-run
```



## `helm`相关命令



### `helm version`查看`helm`版本

```bash
helm version
```



### `helm env`查看`helm`命令使用的环境变量列表

```bash
helm env
```



### `helm repo`仓库管理

添加微软`helm`仓库

```bash
helm repo add stable  http://mirror.azure.cn/kubernetes/charts/
```

添加`basic auth`的私有仓库

```bash
helm repo add chartmuseum --username root --password 123456 http://localhost:8080
```

列出`helm`仓库

```bash
helm repo list
```

把远程仓库`index.yaml`更新到本地，`https://stackoverflow.com/questions/55973901/how-can-i-list-all-available-charts-under-a-helm-repo`

```bash
helm repo update
```

删除仓库

```bash
helm repo list
```

```properties
NAME   	URL                                      
stable 	http://mirror.azure.cn/kubernetes/charts/
stable2	http://mirror.azure.cn/kubernetes/charts/
```

`stable2`是`helm`仓库名称

```bash
helm repo remove stable2
```

在远程`helm`仓库中搜索`tomcat`包

```bash
helm search repo tomcat
```



### `helm chart`包管理

创建`chart`包

```bash
helm create mychart-test
```

显示`stable/tomcat`包信息

```bash
helm show chart stable/tomcat
```

显示`stable/tomcat values`信息

```bash
helm show values stable/tomcat
```

拉取`helm tgz`包，例如：`tomcat-0.4.3.tgz`

```bash
helm pull stable/tomcat --version 0.4.3
```

拉取`helm tgz`包并在当前目录解压

```bash
helm pull stable/tomcat --version 0.4.3 --untar
```



### `helm package`归档`chart`包

> `https://helm.sh/docs/helm/helm_package/`

在`mychart`目录内时打包`chart`资源，在当前目录生成`mychart-0.1.0.tgz`

```bash
helm package .
```

不在`mychart`目录内时打包`chart`资源，在当前目录生成`mychart-0.1.0.tgz`

```bash
helm package mychart
```



### `helm search`

> `https://stackoverflow.com/questions/55973901/how-can-i-list-all-available-charts-under-a-helm-repo`

先同步远程`helm`仓库中的`index.yaml`到本地缓存中

```bash
helm repo update
```

查看所有`helm`仓库中的所有`charts`

```bash
helm search repo
```

查看名为`chartmuseum`的`helm`仓库中所有`charts`，只显示最新版本

```bash
helm search repo chartmuseum/
```

查看`chartmuseum`的`helm`仓库中名为`mychart`的`chart`，并显示所有版本

```bash
helm search repo -l chartmuseum/mychart
```



### `helm template`

> `https://helm.sh/docs/helm/helm_template/`

显示所有模板

```bash
helm template .
```

显示指定模板`templates/1.yaml`

```bash
helm template -s templates/1.yaml .
```



### `helm upgrade`



#### 从本地`tgz chart`文件安装

下载`tgz`文件`https://github.com/kubernetes/dashboard/releases/download/kubernetes-dashboard-7.10.0/kubernetes-dashboard-7.10.0.tgz`

通过本地`tgz`文件安装`kubernetes dashboard`

```bash
helm upgrade --install kubernetes-dashboard ./kubernetes-dashboard-7.10.0.tgz --create-namespace --namespace kubernetes-dashboard
```



## 内置函数



### `quote`和`squote`函数

>`quote`：变量值添加双引号
>
>`squote`：变量值添加单引号

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



### 使用多个函数多次处理同一个变量

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



### `lower`和`upper`函数

> 注意：参考上面例子



### `repeat`函数

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



### `default`函数

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



### `lookup`函数

> 用于获取`k8s`集群信息
>
> 注意：`lookup`函数不能使用`dry-run`参数`install`，否则无法获取`k8s`集群信息
>
> 调用语法：`lookup "apiVersion" "kind" "namespace" "name"`（`namespace`和`name`参数是可选的，可以提供空字符串""，`namespace`提供空字符串表示所有命名空间）

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



### 逻辑和流程控制函数

`todo`



### 字符串函数

`todo`



### 类型转换函数

`todo`



### 正则表达式函数

`todo`



### 字典函数

#### `get`、`set`、`unset`函数

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



#### `keys`函数

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



### 列表函数

`todo`



### `tpl`函数

> 允许开发人员在模板中传递模板字符串，例如：开发人员可以在外部的配置文件中使用helm模板语法编写配置后，使用tpl函数引用这个配置文件内容，里面的helm模板语法被动态解析。
>
> `https://helm.sh/docs/howto/charts_tips_and_tricks/#using-the-tpl-function`

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



### `toYaml`函数

> 把对象转换为`yaml`格式

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



### `indent`和`nindent`函数

> `indent`和`nindent`函数区别是：`nindent`函数是在`indent`之前添加一个新换行。



### `trimSuffix`函数

> 删除后缀匹配的字符串

在当前目录创建`helm`项目

```bash
helm create .
```

删除`templates`中的文件

```bash
rm -rf templates/*
```

创建`templates/1.yaml`内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap
data:
 k1: {{ "abc-cba-" | trimSuffix "-" }}
```

调试

```bash
helm install demo1 . --debug --dry-run
```



### `trunc`函数

> 截断字符串

在当前目录创建`helm`项目

```bash
helm create .
```

删除`templates`中的文件

```bash
rm -rf templates/*
```

创建`templates/1.yaml`内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap
data:
 k1: {{ "abc-cba-" | trunc 3 }}
```

调试

```bash
helm install demo1 . --debug --dry-run
```



### `printf`函数

> 格式化字符串函数

在当前目录创建`helm`项目

```bash
helm create .
```

删除`templates`中的文件

```bash
rm -rf templates/*
```

创建`templates/1.yaml`内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap
data:
 k1: {{ printf "%s! Are you %d years old?" "Hello dexter" 23 | quote }}
```

调试

```bash
helm install demo1 . --debug --dry-run
```



### 字典类型和字典函数(`dict`、`dictionary`)

> `https://helm.sh/docs/chart_template_guide/function_list/#dictionaries-and-dict-functions`

在当前目录创建`helm`项目

```bash
helm create .
```

删除`templates`中的文件

```bash
rm -rf templates/*
```

创建`templates/1.yaml`内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap
data:
 {{/*定义字典变量*/}}
 {{- $myDict := dict "name1" "value1" "name2" "value2" "name3" "value 3" -}}
 {{/*修改字典变量，NOTE:set 返回字典（Go 模板函数的要求），因此您可能需要像上面那样使用 $_ 赋值来捕获值。*/}}
 {{- $_ := set $myDict "name4" "value4" -}}
 {{/*删除key=name3的键值*/}}
 {{- $_ := unset $myDict "name3" -}}
 hasKey3: {{ hasKey $myDict "name3" }}
 name4: {{ get $myDict "name4" }}
```

调试

```bash
helm install demo1 . --debug --dry-run
```



### 逻辑和流程控制功能函数(`and`、`or`、`eq`、`gt`等)

> `https://stackoverflow.com/questions/49789867/can-we-use-or-operator-in-helm-yaml-files`

#### `and`操作符

在当前目录创建`helm`项目

```bash
helm create .
```

删除`templates`中的文件

```bash
rm -rf templates/*
```

创建`templates/1.yaml`内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap
data:
 {{- if and .Values.b1 .Values.b2 }}
 k1: v1
 {{- end }}
```

`values.yaml`内容如下：

```yaml
b1: true
b2: true
```

调试

```bash
helm install demo1 . --debug --dry-run
```







## 程序流程控制语句



### `ifelse`语句

在当前目录创建`helm`项目

```bash
helm create .
```

删除`templates`中的文件

```bash
rm -rf templates/*
```

创建`templates/1.yaml`内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: {{ .Release.Name }}-configmap1
 namespace: {{ .Release.Namespace }}
data:
 {{- /* 判断布尔变量是否为真 */ -}}
 {{- if .Values.ingress.enabled }}
 ingress: "配置ingress"
 {{- else }}
 ingress: "不配置ingress"
 {{- end }}

 {{- /* 判断字符串是否相等 */ -}}
 {{- if eq .Values.Person.name "dexter" }}
 welcome: "你好Dexter!"
 {{- else }}
 welcome: "你好谁谁!!"
 {{- end }}

 {{- /* 使用if判断字符串是否为空 */ -}}
 {{- if .Values.Person.name }}
 ifNameEmptyMessage: "name不为空"
 {{- else }}
 ifNameEmptyMessage: "name为空"
 {{- end }}
```

`values.yaml`内容如下：

```yaml
Person:
 name: "dexter1"
ingress:
 enabled: true
```

调试

```bash
helm install demo1 . --debug --dry-run
```



```bash
[root@k8s-master mychart]# cat templates/configmap.yaml 

 
[root@k8s-master mychart]# cat values.yaml 

 
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



### `with`语句

```bash
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



### `range`语句

#### `range index`索引

> `https://helm.sh/docs/chart_template_guide/variables/`

在当前目录创建`helm`项目

```bash
helm create .
```

删除 templates 中的文件

```
rm -rf templates/*
```

创建`templates/1.yaml`内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: {{ .Release.Name }}-configmap
data:
  toppings: |-
    {{- range $index, $topping := .Values.pizzaToppings }}
    {{ $index }}: {{ $topping }}
    {{- end }}
```

`values.yaml`内容如下：

```yaml
pizzaToppings:
- mushrooms
- cheese
- peppers
- onions
```

调试

```bash
helm install demo1 . --debug --dry-run
```



#### `range key`键值

> `https://helm.sh/docs/chart_template_guide/variables/`

在当前目录创建`helm`项目

```bash
helm create .
```

删除`templates`中的文件

```bash
rm -rf templates/*
```

创建`templates/1.yaml`内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: {{ .Release.Name }}-configmap
data:
  myvalue: "Hello World"
  {{- range $key, $val := .Values.favorite }}
  {{ $key }}: {{ $val | quote }}
  {{- end }}
```

`values.yaml`内容如下：

```yaml
favorite:
  drink: "coffee"
  food: "PIZZA"
```

调试

```sh
helm install demo1 . --debug --dry-run
```



#### `range`特殊变量`.(dot)`符号

> `https://helm.sh/docs/chart_template_guide/variables/`

在当前目录创建`helm`项目

```bash
helm create .
```

删除`templates`中的文件

```bash
rm -rf templates/*
```

创建`templates/1.yaml`内容如下：

```yaml
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
```

`values.yaml`内容如下：

```yaml
address:
- beijing
- shanghai
- guangzhou
```

调试

```sh
helm install demo1 . --debug --dry-run
```



## `helm subchart/dependency`

> `https://levelup.gitconnected.com/helm-dependencies-1907facbe410`
> `https://helm.sh/docs/chart_template_guide/subcharts_and_globals/`

创建并初始化`demo-mychart`和`demo-mysubchart`目录

```bash
mkdir demo-mychart
cd demo-mychart
helm create .
rm -rf templates/*
mkdir demo-mysubchart
cd demo-mysubchart
helm create .
rm -rf templates/*
```

`demo-mysubchart/Chart.yaml`内容如下：

```yaml
apiVersion: v2
name: mysubchart
description: A Helm chart for Kubernetes

# A chart can be either an 'application' or a 'library' chart.
#
# Application charts are a collection of templates that can be packaged into versioned archives
# to be deployed.
#
# Library charts provide useful utilities or functions for the chart developer. They're included as
# a dependency of application charts to inject those utilities and functions into the rendering
# pipeline. Library charts do not define any templates and therefore cannot be deployed.
type: application

# This is the chart version. This version number should be incremented each time you make changes
# to the chart and its templates, including the app version.
# Versions are expected to follow Semantic Versioning (https://semver.org/)
version: 0.1.0

# This is the version number of the application being deployed. This version number should be
# incremented each time you make changes to the application. Versions are not expected to
# follow Semantic Versioning. They should reflect the version the application is using.
# It is recommended to use it with quotes.
appVersion: "1.16.0"
```

`demo-mysubchart/values.yaml`内容如下：

```yaml
name: "Dexter"
```

`demo-mysubchart/templates/1.yaml`内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: {{ .Release.Name }}-configmap-mysubchart
 namespace: {{ .Release.Namespace }}
data:
  myName: {{ .Values.name }}
```

`demo-mychart/Chart.yaml`内容如下：

```yaml
apiVersion: v2
name: .
description: A Helm chart for Kubernetes

# A chart can be either an 'application' or a 'library' chart.
#
# Application charts are a collection of templates that can be packaged into versioned archives
# to be deployed.
#
# Library charts provide useful utilities or functions for the chart developer. They're included as
# a dependency of application charts to inject those utilities and functions into the rendering
# pipeline. Library charts do not define any templates and therefore cannot be deployed.
type: application

# This is the chart version. This version number should be incremented each time you make changes
# to the chart and its templates, including the app version.
# Versions are expected to follow Semantic Versioning (https://semver.org/)
version: 0.1.0

# This is the version number of the application being deployed. This version number should be
# incremented each time you make changes to the application. Versions are not expected to
# follow Semantic Versioning. They should reflect the version the application is using.
# It is recommended to use it with quotes.
appVersion: "1.16.0"

dependencies:
  - name: mysubchart
    version: 0.1.0
    repository: file://../demo-mysubchart
```

`demo-mychart/values.yaml`内容如下：

```yaml
# 设置 mysubchart .Values.name变量
mysubchart:
  name: "Dexterleslie"
```

`demo-mychart/templates/1.yaml`内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: {{ .Release.Name }}-configmap-mychart
 namespace: {{ .Release.Namespace }}
data:
```

下载`mysubchart`到`demo-mychart/charts`子目录中

```sh
helm dependency update .
```

调试

```sh
helm install demo1 . --debug --dry-run
```



## `helm`多个环境配置

> 使用多个`values-xxx.yaml`配置设置不同环境的参数`https://codefresh.io/blog/helm-deployment-environments/`
> 
>注意：上面的方案并不好，因为如果`values-xxx.yaml`参数一旦变动需要维护多个`values-xxx.yaml`文件。所以还是采用使用一个`values.yaml`配置不同环境的参数。



## 子模板和`template`、`include`使用

### 在主模板中定义子模板

> 注意：这种方法在实际项目中不使用，所以不`demo`



### 在`_helpers.tpl`定义子模板

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



### 带变量的子模板

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



### `include`用法

> `include`语法为`include <标签> <变量参数>`，例如：`include "mychart.demo1"`。表示使用全局变量`.(dot)`参数调用`mychart.demo1`标签模板，`include "mychart.demo1" (dict "k1" "v1")`表示使用字典变量`dict "k1" "v1"`参数调用`mychart.demo1`标签模板。

#### `if`判断`include`返回`true`或者`false`

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



#### `include`返回字符串

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



#### 带`dict`参数的`include`

> `https://stackoverflow.com/questions/60636775/pass-multiple-variables-in-helm-template`

在当前目录创建`helm`项目

```bash
helm create .
```

删除`templates`中的文件

```bash
rm -rf templates/*
```

创建`templates/_helpers.tpl`内容如下：

```yaml
{{- define "mychart.demo1" -}}
{{/* 获取 dict 的 k1键 的值 */}}
myK1: {{ .item.k1 }}
myK2: {{ .item.k2 }}
{{- end -}}
```

创建`templates/1.yaml`内容如下：

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
 name: myconfigmap
data:
 {{- range $item := .Values.items }}
 {{- include "mychart.demo1" (dict "item" $item) | indent 4 }}
 {{- end }}
```

`values.yaml`内容如下：

```yaml
items:
- k1: v1
  k2: v2
```

调试

```sh
helm install demo1 . --debug --dry-run
```



### `template`和`include`区别

> `https://stackoverflow.com/questions/71086697/how-does-template-and-include-differ-in-helm`

`template`和`include`调用语法是一样的，`template`的结果无法捕获在变量中或包含在管道中。`include`的结果作为字符串返回并且能够包含在管道中。

如下例子：

- `template`结果直接输出不能使用变量捕获，`{{ template "common.tplvalues.merge" (dict "values" (list .customLabels $default) "context" .context) }}`
- `include`结果能够使用变量捕获或者输出到管道中，`{{ include "common.names.namespace" . | quote }}`