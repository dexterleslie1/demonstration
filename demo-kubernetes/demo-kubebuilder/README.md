## kubebuilder命令安装

https://book.kubebuilder.io/quick-start

```shell
# 下载kubebuilder
curl -L -o /tmp/kubebuilder "https://go.kubebuilder.io/dl/latest/$(go env GOOS)/$(go env GOARCH)"

# 配置全局kubebuilder命令
sudo chmod +x /tmp/kubebuilder && sudo mv /tmp/kubebuilder /usr/local/bin/
```



## 快速体验开始kubebuilder

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

