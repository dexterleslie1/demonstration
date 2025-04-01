## kubebuilder命令安装

https://book.kubebuilder.io/quick-start

```shell
# 下载kubebuilder
curl -L -o /tmp/kubebuilder "https://go.kubebuilder.io/dl/latest/$(go env GOOS)/$(go env GOARCH)"

# 配置全局kubebuilder命令
sudo chmod +x /tmp/kubebuilder && sudo mv /tmp/kubebuilder /usr/local/bin/
```



## 快速体验 kubebuilder

https://book.kubebuilder.io/quick-start

### 创建项目

创建项目目录并初始化项目

```shell
mkdir demo-quickstart
cd demo-quickstart
kubebuilder init --domain=my.domain --repo=my.domain/guestbook
```

### 添加 api

创建新api组webapp/v1和CRD Guestbook

```shell
kubebuilder create api --group webapp --version v1 --kind Guestbook
```

选择创建CR和controller会在当前目录生成`api/v1/guestbook_types.go`和`internal/controller/guestbook_controller.go`同时还会生成`kustomize`相关配置目录。

修改`api/v1/`内的 api 定义后，重新生成 CRs 和 CRDs

```shell
make manifests
```

### 测试

安装 CRDs 到 kubernetes 集群中

```shell
make install
```

运行 controller

```shell
make run
```

### 安装 CR 实例

创建 CRD 的实例 CR

```shell
kubectl apply -k config/samples
```

### TODO 使用部署方式在集群中运行 controller

### 卸载 CRDs

从集群中删除 CRDs

```shell
make uninstall
```

### 取消部署

从集群中删除 controller 的部署

```shell
make undeploy
```



## 入门例子 memcached

参考链接 https://book.kubebuilder.io/getting-started

首先，创建并切换到项目目录`demo-kubebuilder-gettingstarted`，然后使用`kubebuilder init`命令初始化

```shell
mkdir demo-kubebuilder-gettingstarted
cd demo-kubebuilder-gettingstarted
kubebuilder init --domain=example.com --repo=example.com/demo
```

创建 Memcached API(CRD)，其中 --image、--image-container-command 等参数都为插件`deploy-image/v1-alpha`参数

```shell
kubebuilder create api --group example.com --version v1alpha1 --kind Memcached --image=memcached:1.4.36-alpine --image-container-command="memcached,-m=64,-o,modern,-v" --image-container-port="11211" --run-as-user="1001" --plugins="deploy-image/v1-alpha" --make=false
```

生成 CRDs 配置定义文件

```shell
make manifests
```

安装 CRDs

```shell
make install
```

运行 controller

```shell
make run
```

创建 memcached CR

```shell
kubectl apply -k config/samples
```



## CronJob例子

参考链接 https://book.kubebuilder.io/cronjob-tutorial/cronjob-tutorial

创建项目

```shell
mkdir demo-kubebuilder-cronjob
cd demo-kubebuilder-cronjob
kubebuilder init --domain tutorial.kubebuilder.io --repo tutorial.kubebuilder.io/project
```

添加 API

```shell
kubebuilder create api --group batch --version v1 --kind CronJob
```

复制 https://book.kubebuilder.io/cronjob-tutorial/controller-implementation 相关业务逻辑到 controller 中

编辑 api/v1/cronjob_types.go 中的 CronJob 相关代码后，执行以下命令重新生成 CRDs 和 RBAC yaml 文件

```shell
make manifests
```

安装 CRDs

```shell
make install
```

点击 main.go 使用 debug 模式运行 controller

创建 CR

```shell
kubectl apply -k config/samples
```

