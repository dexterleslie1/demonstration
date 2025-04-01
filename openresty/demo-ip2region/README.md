# 演示lua调用ip2region

## 参考

- [官方参考](https://github.118899.net/shixinke/lua-resty-ip2region)

## 启动方式

```
docker run --rm --name=openresty-demo -p 80:80 -v $(pwd)/nginx.conf:/usr/local/openresty/nginx/conf/nginx.conf -v $(pwd)/ip2region.lua:/usr/local/openresty/lualib/resty/ip2region.lua -v $(pwd)/ip2region.db:/usr/local/openresty/ip2region.db openresty/openresty
```

