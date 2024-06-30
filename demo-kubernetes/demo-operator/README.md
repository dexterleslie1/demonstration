## 安装 operator-sdk CLI

参考 https://sdk.operatorframework.io/docs/installation/

设置平台环境变量

```sh
export ARCH=$(case $(uname -m) in x86_64) echo -n amd64 ;; aarch64) echo -n arm64 ;; *) echo -n $(uname -m) ;; esac)
export OS=$(uname | awk '{print tolower($0)}')
```

下载 operator-sdk CLI 二进制程序

```sh
export OPERATOR_SDK_DL_URL=https://github.com/operator-framework/operator-sdk/releases/download/v1.33.0
curl -LO ${OPERATOR_SDK_DL_URL}/operator-sdk_${OS}_${ARCH}
```

安装 operator-sdk 二进制程序到 PATH 中

```sh
chmod +x operator-sdk_${OS}_${ARCH} && sudo mv operator-sdk_${OS}_${ARCH} /usr/local/bin/operator-sdk
```

查看 operator-sdk CLI 是否正确设置

```sh
operator-sdk version
```



## 创建一个基于 helm 的 operator

参考 https://sdk.operatorframework.io/docs/building-operators/helm/tutorial/

创建和初始化项目

```sh
mkdir nginx-operator
cd nginx-operator
operator-sdk init --plugins helm --domain example.com --group demo --version v1alpha1 --kind Nginx
```

