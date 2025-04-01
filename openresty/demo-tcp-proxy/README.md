# 演示使用openresty代理tcp连接

## 运行demo

```
# 运行容器，表示客户端可以连接20001端口，会把tcp连接代理到原来提供服务的机器上
docker run --rm --name=openresty-demo -v $(pwd)/nginx.conf:/usr/local/openresty/nginx/conf/nginx.conf -p 20001:80  openresty/openresty
```

