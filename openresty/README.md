## nginx.conf配置



### `try_files`

配置片段：

```nginx
location / {
    root   /usr/local/openresty/nginx/html;
    index  index.html index.htm;
    try_files $uri $uri/ /index.html;
}
```

这段 Nginx 配置用于定义**根路径 `/` 的请求处理规则**，主要涉及静态资源的路径解析、默认文件匹配和兜底策略。以下是逐行详细解释：


#### **1. `location / { ... }`**
- **作用**：匹配所有以 `/` 开头的请求（即所有未被其他更精确 `location` 规则匹配的请求）。
- **示例**：无论是 `/index.html`、`/images/logo.png` 还是 `/api`（假设没有其他 `location /api` 规则），都会被此块处理。


#### **2. `root /usr/local/openresty/nginx/html;`**
- **作用**：指定静态资源的**根目录**。Nginx 会将请求的 URI 拼接到 `root` 路径后，形成实际的文件系统路径。
- **示例**：
  - 若请求 URI 是 `/index.html`，则实际查找的文件路径是 `/usr/local/openresty/nginx/html/index.html`。
  - 若请求 URI 是 `/images/logo.png`，则实际路径是 `/usr/local/openresty/nginx/html/images/logo.png`。


#### **3. `index index.html index.htm;`**
- **作用**：当请求的 URI 是一个**目录**（而非具体文件）时，Nginx 会按顺序尝试访问该目录下的 `index.html` 或 `index.htm` 作为默认首页。
- **示例**：
  - 若请求 URI 是 `/`（根目录），Nginx 会检查 `/usr/local/openresty/nginx/html/index.html` 是否存在；若存在则返回，否则检查 `/usr/local/openresty/nginx/html/index.htm`。
  - 若请求 URI 是 `/subdir/`（假设 `/subdir` 是一个目录），则检查 `/usr/local/openresty/nginx/html/subdir/index.html`，依此类推。


#### **4. `try_files $uri $uri/ /index.html;`**
- **作用**：定义请求的**兜底处理逻辑**，按顺序尝试查找资源，若均不存在则最终指向 `/index.html`。
- **执行逻辑**（按顺序检查）：
  1. `$uri`：请求 URI 对应的具体文件路径（即 `root + $uri`）。  
     例如请求 `/foo.jpg`，检查 `/usr/local/openresty/nginx/html/foo.jpg` 是否存在。
  2. `$uri/`：请求 URI 对应的目录路径（即 `root + $uri/`）。  
     例如请求 `/subdir/`，检查 `/usr/local/openresty/nginx/html/subdir/` 是否是一个存在的目录。
  3. `/index.html`：若前两者均不存在，最终跳转到根目录的 `index.html`（即 `/usr/local/openresty/nginx/html/index.html`）。


#### **完整流程示例**
假设用户请求 `/about`：
1. Nginx 先尝试访问 `root + $uri` → `/usr/local/openresty/nginx/html/about`（假设这是一个文件）。  
   - 若文件存在，直接返回该文件内容。
   - 若文件不存在，进入下一步。
2. 尝试访问 `root + $uri/` → `/usr/local/openresty/nginx/html/about/`（假设这是一个目录）。  
   - 若目录存在且包含 `index.html` 或 `index.htm`（由 `index` 指令定义），返回目录默认页。
   - 若目录不存在或无默认页，进入下一步。
3. 最终跳转到 `/index.html` → 返回 `/usr/local/openresty/nginx/html/index.html` 的内容。


#### **总结**
这段配置的核心逻辑是：  
**优先返回请求 URI 对应的具体文件 → 若不存在则尝试目录默认页 → 若仍不存在则强制返回根目录的 `index.html`**。  
常见于前端单页应用（如 Vue/React 构建的 SPA），确保所有前端路由最终都指向 `index.html`，由前端路由接管后续逻辑。

## uri前缀截断

>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/openresty/backend-api

location配置：

```
location /my-api {
    proxy_set_header Host $host:$server_port;
    proxy_set_header x-forwarded-for $proxy_add_x_forwarded_for;
    proxy_http_version 1.1;
    proxy_set_header Connection '';
    # 匹配/my-api路径的请求发送给backend上游服务
    # proxy_pass http://backend/中最后/表示忽略掉/my-api，将剩余部分作为后端服务请求的URL
    # proxy_pass http://backend中最后没有/表示将/my-api作为后端服务请求的URL
    proxy_pass http://backend/;
}
```

请求接口 http://localhost/my-api/api/v1/testUriPrefixStrip 时/my-api会被openresty截断，转发给后端的api为/api/v1/testUriPrefixStrip

## nginx定义全局和局部变量

详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/blob/main/openresty/nginx-getting-started.conf

```nginx
server {
    # 定义全局变量
    set $my_global_variable "Hello world from Global!!!";

    listen       80;
    server_name  localhost;

    location / {
        # 定义局部变量
        set $my_local_variable "Hello world from Local!!!";

        #root   /usr/local/openresty/nginx/html;
        #index  index.html index.htm;
        content_by_lua_block {
            -- nginx for lua api之获取请求中的参数
            -- http://www.shixinke.com/openresty/openresty-get-request-arguments
            local args = ngx.req.get_uri_args();
            local p1 = args.p1;
            if not p1 then
                p1 = "";
            end

            ngx.header.content_type = "text/plain;charset=utf-8";
            ngx.say("Hello Dexterleslie. 参数p1=" .. p1 .. "，全局变量$my_global_variable：" .. ngx.var.my_global_variable .. "，局部变量$my_local_variable：" .. ngx.var.my_local_variable);
        }
    }
}
```

## OpenResty支持HTTP/2

编译OpenResty时启用HTTP/2

```sh
cd /tmp/openresty-$varOpenrestyVersion && ./configure --add-module=/tmp/naxsi-$varNaxsiVersion/naxsi_src --with-http_stub_status_module --with-http_v2_module
```

编译和调试OpenResty是否启用HTTP/2的本站辅助示例：https://gitee.com/dexterleslie/demonstration/tree/main/openresty/demo-build-base-image

示例运行步骤：

1. 生成证书

   ```sh
   ./generate-ssl.sh
   ```

2. 运行服务

   ```sh
   docker compose up -d
   ```

3. 使用谷歌浏览器访问 https://localhost 调试是否使用HTTP/2通讯，打开开发者工具右键点击表头（如 Name、Status、Type 等），勾选 **Protocol**，显示“协议”列。查看任意资源的 **Protocol** 列，如果显示 `h2`，则表示该资源是通过 **HTTP/2** 加载的。

   - http/1.1：表示 HTTP/1.1
   - h2：表示 HTTP/2
   - h3：表示 HTTP/3（基于 QUIC）

## HTTP/1.1和HTTP/2性能对比

使用本站示例辅助测试：https://gitee.com/dexterleslie/demonstration/tree/main/openresty/demo-http2

测试步骤：

1. 生成证书

   ```sh
   ./generate-cert.sh
   ```

2. 运行应用

   ```sh
   docker compose up -d
   ```

3. 分别访问HTTP/1.1和HTTP/2性能对比

   在测试前打开Chrome开发者工具，把网络切换到3G并且勾选`Disable cache`。

   分别访问 http://localhost:8080/ 和 https://localhost:8443/ 对比测试结果。HTTP/1.1的DOMContentLoaded为10258.00 ms，HTTP/2的DOMContentLoaded为4685.00 ms。表明在并发加载多个资源的情况下HTTP/2比HTTP/1.1快。
