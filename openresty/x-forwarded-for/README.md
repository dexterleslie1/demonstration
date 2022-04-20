# 演示x-forwarded-for、proxy_add_x_forwarded_for用法

## 参考资料

> [使用lua脚本操作x-forwarded-for头](https://github.com/openresty/lua-nginx-module/issues/801)  
> [HTTP X-Forwarded-For 介绍](https://www.runoob.com/w3cnote/http-x-forwarded-for.html)

## 运行demo
```
# 启动业务backend代理
docker run --rm --net=host --name=openresty-backend -v $(pwd)/nginx-backend.conf:/usr/local/openresty/nginx/conf/nginx.conf openresty/openresty

# 启动前端用户代理
docker run --rm --net=host --name=openresty-frontend -v $(pwd)/nginx-frontend.conf:/usr/local/openresty/nginx/conf/nginx.conf openresty/openresty
```

## 客户端设置x-forwarded-for头欺骗漏洞测试
```
curl http://192.168.1.111/api/v1/info -H "x-forwarded-for: 8888"
```
