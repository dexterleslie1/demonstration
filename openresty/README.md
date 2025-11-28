## `nginx.conf` 配置



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