# 演示lua-resty-http使用

# 资料

**[lua-resty-http](https://github.com/ledgetech/lua-resty-http)**
**[openresty安装lua-resty-http](https://stackoverflow.com/questions/48319678/install-resty-http-with-already-installed-openresty)**

# 启动方式

```
docker run --rm --name=openresty-demo -p 80:80 -v $(pwd)/demo-lua-resty-http.conf:/usr/local/openresty/nginx/conf/nginx.conf -v $(pwd)/http_headers.lua:/usr/local/openresty/lualib/resty/http_headers.lua -v $(pwd)/http_connect.lua:/usr/local/openresty/lualib/resty/http_connect.lua -v $(pwd)/http.lua:/usr/local/openresty/lualib/resty/http.lua openresty/openresty
```

