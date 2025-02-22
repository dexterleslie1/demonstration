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