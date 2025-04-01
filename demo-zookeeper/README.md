# Zookeeper



## 使用 docker 运行

>zookeeper jvm 内存设置，使用环境变量设置 JVMFLAGS=-Xmx512m -Xms512m -server，参考 `https://www.cnblogs.com/zqllove/p/13724195.html`

```yaml
version: "3.0"

services:
  demo-zookeeper:
    image: zookeeper:3.4.9
    environment:
      - TZ=Asia/Shanghai
      - JVMFLAGS=-Xmx512m -Xms512m -server
#    ports:
#      - 2181:2181
    network_mode: host
```



## 禁用 AdminServer

```yaml
version: "3.0"

services:
  demo-zookeeper:
#    image: zookeeper:3.4.9
    image: zookeeper:3.8.4
    environment:
      - TZ=Asia/Shanghai
      - JVMFLAGS=-Xmx512m -Xms512m -server
      # 禁用 zookeeper AdminServer
      # https://hub.docker.com/_/zookeeper
      - ZOO_ADMINSERVER_ENABLED=false
#    ports:
#      - 2181:2181
    network_mode: host
```



## 客户端工具



### ZooKeeper Assistant

特点：

- 支持 ubuntu
- 缺点：每次刷新后节点数自动闭合需要手动重新展开



安装

下载 ZA-2.0.0.4-linux-x64.tar.gz，地址 `https://www.redisant.cn/za`

解压 ZA-2.0.0.4-linux-x64.tar.gz

```bash
tar -xvzf ZA-2.0.0.4-linux-x64.tar.gz
```

进入解压后的目录并授予 ZooKeeperAssistant 执行权限

```bash
sudo chmod +x ZooKeeperAssistant
```

运行 ZooKeeperAssistant

```bash
./ZooKeeperAssistant
```

