# 演示openresty相关技术

## 资料

> [nginx for lua api之获取请求中的参数](http://www.shixinke.com/openresty/openresty-get-request-arguments)

## centOS运行openresty容器
```shell script
docker run --rm --name=openresty-demo --net=host -v $(pwd)/nginx-getting-started.conf:/usr/local/openresty/nginx/conf/nginx.conf openresty/openresty
```

## macOS运行openresty容器
```shell script
docker run --rm --name=openresty-demo -p 80:80 -v $(pwd)/nginx-getting-started.conf:/usr/local/openresty/nginx/conf/nginx.conf openresty/openresty
```