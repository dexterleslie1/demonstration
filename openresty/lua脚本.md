# `openresty lua`脚本相关

> 注意：下面所有演示需要先参考 <a href="/openresty/编译docker基础镜像.html" target="_blank">链接</a> 编译`openresty`基础镜像

## 引用第三方`lua`库

>详细示例请参考 [链接](https://gitee.com/dexterleslie/demonstration/tree/master/openresty/lua-scripting/demo-lua-package-path-and-require)

使用`lua_package_path`指定`lua`库的路径

```nginx
lua_package_path "/usr/local/openresty/nginx/conf/lua/?.lua;;";
```

引用`lua`库

```nginx
location / {
    content_by_lua_block {
        -- 引用lua_common.lua库
        local common = require("lua_common");
        local varText = common.getText();
        ngx.header.content_type = "text/plain;charset=utf-8";
        ngx.say(varText);
    }
}
```



## `lua`入门

> `openresty lua`入门演示详细请参考 [链接](https://gitee.com/dexterleslie/demonstration/tree/master/openresty/lua-scripting/demo-getting-started)

编译演示

```bash
docker compose build
```

运行演示

```bash
docker compose up -d
```

访问`http://localhost`，显示`Hello world!`



## `lua`生成`uuid`

> 演示详细请参考 [链接](https://gitee.com/dexterleslie/demonstration/tree/master/openresty/lua-scripting/demo-uuid)

编译演示

```bash
docker compose build
```

运行演示

```bash
docker compose up -d
```

访问`http://localhost`，显示`Hello world! UUID: d85ab70f-096a-49e9-a9bd-3ebd026196e5`



## todo `lua api`

>[nginx for lua api之获取请求中的参数](http://www.shixinke.com/openresty/openresty-get-request-arguments)
>
>[nginx lua api](https://github.com/openresty/lua-nginx-module)