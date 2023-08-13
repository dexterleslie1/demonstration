## 使用harbor搭建私有镜像仓库

> NOTE: 不选用nexus3作为docker仓库，多个docker仓库需要运行在不同的端口上。
> NOTE: 成功设置nginx http反向代理harbor
> https://blog.csdn.net/lcl_xiaowugui/article/details/105422794
>
> todo: 设置nginx https反向代理harbor



### 安装步骤

```
# 使用dcli安装docker环境

# 下载harbor在线安装包harbor-online-installer-v2.5.3.tgz
https://github.com/goharbor/harbor/releases

# 解压后配置harbor.yaml，内容如下:
hostname: docker.118899.net # NOTE: 发现下面配置不需要关注ip
hostname: 0.0.0.0

注释https配置
external_url: http://docker.118899.net

# external_url: http://0.0.0.0 # NOTE: 不能使用这个配置，必须要仿上面那样配置域名，否则docker login时会报告: Error response from daemon: Get "http://192.168.1.181:50003/v2/": Get "http://0.0.0.0:50003/service/token?account=admin&client_id=docker&offline_token=true&service=harbor-registry": dial tcp 0.0.0.0:50003: connect: connection refused

harbor_admin_password: xxxxxx
data_volume: /data/data-harbor

# 启动harbor，等待几分钟harbor启动
# NOTE: 使用ubuntu运行需要sudo ./install.sh
sh install.sh

# 登陆harbor
http://docker.118899.net
账号：admin
密码：xxxxxx

# docker客户端配置/etc/docker/daemon.json添加insecure-registries
# https://stackoverflow.com/questions/42211380/add-insecure-registry-to-docker
"insecure-registries": ["docker.118899.net"]

# 客户端使用harbor默认项目仓库地址
docker.118899.net/library

# 推送镜像先登陆harbor
docker login docker.118899.net
账号：admin
密码：xxxxxx

# 关闭harbor
# NOTE: 使用ubuntu关闭harbor需要运行sudo docker-compose down
docker-compose down

# nginx配置如下:
upstream server_backend_harbor {
 server xxx:xxx;
}

server {
 listen 80 ;
 server_name docker.118899.net;

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

