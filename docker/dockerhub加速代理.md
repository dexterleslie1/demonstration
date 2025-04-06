# 搭建`dockerhub`加速代理



## 方案1：使用`harbor cache`特性代理

参考 [链接](/harbor/#设置和运行) 搭建`harbor`服务

使用浏览器登录`harbor`后，导航到`Registries`功能新建名为`proxy`的`endpoint`，`endpoint`的配置如下：

- `Provider`：`Docker Hub`
- `Name`：`proxy`
- `Endpoint URL`：使用默认值
- `Verify Remote Cert`：勾选

填写以上的`endpoint`配置后，点击`TEST CONNECTION`按钮测试`endpoint`的连接是否正常（如果能够成功连接`Docker Hub`则提示成功）。再点击`OK`按钮保存`endpoint`信息。

导航到`Projects`功能新建名为`proxy`的项目，项目的配置如下：

- `Project Name`：`proxy`
- `Access Level`：`public`
- `Proxy Cache`：开关打开
- `Endpoint`：选择上面创建的`endpoint`

再点击`OK`按钮保存。

把代理服务器添加到`/etc/docker/daemon.json`的`insecure-registries`配置中。

在没有使用代理的情况下`docker pull untergeek/curator:5.7.6`拉取镜像失败。

在使用代理的情况下`docker pull xxx.xxx.xxx.xxx:80/proxy/untergeek/curator:5.7.6（其中xxx.xxx.xxx.xxx是代理服务器的ip地址或者域名）`成功拉取镜像。



## 方案2：使用`registry docker`镜像代理

>[Deploying a docker registry mirror as a container](https://medium.com/@shaikrish27/deploying-a-docker-registry-mirror-as-a-container-59565ff92c48)

注意：此方案通讯流量没有加密使用一段时间后会被`GFW`封锁导致不能继续使用。

`docker-compose.yaml`内容如下：

```yaml
version: "3.0"

services:
  proxy:
    image: registry:2.7.1
    environment:
      - REGISTRY_PROXY_REMOTEURL=https://registry-1.docker.io
      - TZ=Asia/Shanghai
    command:
      - /etc/docker/registry/config.yml
    ports:
      - '10005:5000'
    restart: always
```

`docker`客户端`/etc/docker/daemon.json`配置如下（注意：`http://dockerhub.118899.net:10005`需要放在`https://docker.m.daocloud.io`之前，否则不会使用加速代理）：

```json
{
  "registry-mirrors": ["http://dockerhub.xxx.net:10005", "https://docker.m.daocloud.io"],
  "insecure-registries": ["http://dockerhub.xxx.net:10005"]
}
```



## 方案3：配置`docker daemon`代理

>[代理配置](https://blog.csdn.net/a_917/article/details/140685790)

在`/etc/docker/daemon.json`配置文件中加入如下内容：

```json
"proxies": {
    "http-proxy": "http://192.168.235.128:1080",
    "https-proxy": "http://192.168.235.128:1080",
    "no-proxy": "127.0.0.0/8,192.168.1.1/24,192.168.235.1/24,registry.cn-hangzhou.aliyuncs.com"
}
```

- 提醒：以上 registry.cn-hangzhou.aliyuncs.com 可以替换为 *.aliyuncs.com

重启`docker daemon`

```bash
sudo systemctl restart docker
```



## 方案4：使用`cloudflare worker`代理

`todo`