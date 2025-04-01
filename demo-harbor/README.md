# `harbor`用法

## 为何`harbor`选择？

> 注意：不选用`nexus3`作为私有`docker`仓库，因为多个`docker`仓库需要运行在不同的端口上。
> 注意：成功设置`nginx http`反向代理`harbor`，[参考](https://blog.csdn.net/lcl_xiaowugui/article/details/105422794)
> 
>todo： 设置`nginx https`反向代理`harbor`



## 设置和运行

>注意：系统重启后，如果`harbor`应用某些服务没有自动启动，则切换到`harbor`目录执行`docker compose up -d`手动启动所有`harbor`相关服务。

使用`dcli`安装`docker`环境

下载最新版本的`harbor`离线安装包，例如：`harbor-offline-installer-v2.10.3.tgz`，地址：https://github.com/goharbor/harbor/releases，注意：由于国内下载远程镜像失败，所以使用离线安装包为宜。

解压`harbor-offline-installer-v2.10.3.tgz`后配置`harbor.yaml`，内容如下：

```ini
hostname: docker.xxx.net # NOTE: 发现下面配置不需要关注ip
hostname: 0.0.0.0

注释https配置
external_url: http://docker.xxx.net
# external_url: http://0.0.0.0 # NOTE: 不能使用这个配置，必须要仿上面那样配置域名，否则docker login时会报告: Error response from daemon: Get "http://192.168.1.181:50003/v2/": Get "http://0.0.0.0:50003/service/token?account=admin&client_id=docker&offline_token=true&service=harbor-registry": dial tcp 0.0.0.0:50003: connect: connection refused

harbor_admin_password: xxxxxx
data_volume: /data/data-harbor
```

启动`harbor`，等待几分钟`harbor`启动，注意：使用`ubuntu`运行需要`sudo ./install.sh`；注意：支持`helm charts`需要添加`--with-chartmuseum`参数，例如：`sh install.sh --with-chartmuseum`，[参考](https://github.com/goharbor/harbor/issues/14446)

```bash
sh install.sh
```

登陆`harbor`，访问`http://docker.xxx.net`，帐号：admin，密码：xxxxxx

`docker`客户端配置`/etc/docker/daemon.json`添加`insecure-registries`，[参考](https://stackoverflow.com/questions/42211380/add-insecure-registry-to-docker)

```json
"insecure-registries": ["http://docker.xxx.net:80"]
```

客户端使用`harbor`默认项目仓库地址：`docker.xxx.net/library`

推送镜像先登陆`harbor`

```bash
docker login docker.xxx.net

# 账号：admin
# 密码：xxxxxx
```

关闭`harbor`，注意：使用`ubuntu`关闭`harbor`需要运行`sudo docker-compose down`

```bash
docker-compose down
```

`nginx`反向代理`harbor`配置如下：

```ini
upstream server_backend_harbor {
 server xxx:xxx;
}

server {
 listen 80 ;
 server_name docker.xxx.net;

 client_max_body_size 0;

 location / {
  proxy_http_version 1.1;
  proxy_set_header Connection "";
  proxy_set_header Host $host:$server_port;
  proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
  proxy_pass http://server_backend_harbor;
 }
}
```



## 调用`harbor api`

打开`api swagger`步骤，登录`harbor`控制台 > 点击`Harbor API V2.0`

提供帐号和密码调用`api`，[参考](https://cloud.tencent.com/developer/article/1750999)

```bash
curl -u admin:xxxxxx http://192.168.1.151:81/api/v2.0/users/current
```

创建`project`

```bash
curl -u admin:xxxxxx -X POST http://192.168.1.151:81/api/v2.0/projects -d '{"project_name": "test1"}' -H "Content-Type: application/json"
```

更新`gc`策略

```bash
curl -u admin:xxxxxx -X PUT http://192.168.1.151:81/api/v2.0/system/gc/schedule -d '{"parameters":{"delete_untagged":true},"schedule":{"cron":"0 0 0 * * *","type":"Daily"}}' -H "Content-Type: application/json"
```

