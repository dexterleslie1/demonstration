# 研究limit-conn

## 注意

> docker network 使用 host 模式才能够使用lsof查看openresty网络连接情况

## 查看网络connection情况

```
lsof -a -i:80
```

## 编译和运行demo

```
# 编译镜像
sh build.sh

# 运行
docker-compose up
```