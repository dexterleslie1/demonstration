# 演示使用lua脚本操作cookie

## 参考

**[github lua-resty-cookie](https://github.com/cloudflare/lua-resty-cookie)**

## 启动方式

```
docker run --rm --name=openresty-demo -p 80:80 -v $(pwd)/nginx.conf:/usr/local/openresty/nginx/conf/nginx.conf -v $(pwd)/cookie.lua:/usr/local/openresty/lualib/resty/cookie.lua openresty/openresty
```

