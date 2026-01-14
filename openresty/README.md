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

## location匹配规则优先级

Nginx 的 `location`块用于根据请求的 URI 匹配不同的配置，其匹配规则和优先级是 Nginx 配置的核心知识点之一。理解这些规则能帮助你精准控制请求的处理逻辑（如反向代理、静态文件服务、重定向等）。

### **一、Location 的类型与语法**

Nginx 的 `location`有两种主要类型：**前缀字符串匹配**（Prefix String Match）和**正则匹配**（Regular Expression Match）。此外还有两种特殊修饰符，用于控制匹配行为。

#### 1. 前缀字符串匹配（无修饰符）

语法：

```
location [ = | ~ | ~* | ^~ ] uri { ... }
```

其中，`uri`是普通字符串（如 `/static/`、`.html`），默认是**前缀匹配**（即只要请求 URI 以 `uri`开头即匹配）。

#### 2. 正则匹配（带 `~`或 `~*`修饰符）

- `~`：区分大小写的正则匹配（如 `~ \.php$`匹配 `.php`结尾的 URI）；
- `~*`：不区分大小写的正则匹配（如 `~* \.(jpg|png)$`匹配 `.JPG`、`.PNG`等）。

#### 3. 精确匹配（`=`修饰符）

语法：`location = uri { ... }`

仅当请求 URI **完全等于** `uri`时匹配（如 `location = /login`仅匹配 `/login`，不匹配 `/login/`或 `/login?param=1`）。

#### 4. 优先前缀匹配（`^~`修饰符）

语法：`location ^~ uri { ... }`

表示**前缀匹配**，但一旦匹配成功，**不再检查后续的正则表达式**（优先级高于正则，但低于精确匹配）。

### **二、匹配优先级规则**

Nginx 对 `location`的匹配遵循**从高到低**的优先级顺序，具体规则如下：

#### 1. **最高优先级：精确匹配（`=`）**

若请求 URI 与 `location = uri`完全一致，直接命中该 `location`，**终止后续匹配**。

示例：

```
location = /api/v1/user {
    # 仅匹配 GET /api/v1/user（严格等于）
}
```

#### 2. **次高优先级：优先前缀匹配（`^~`）**

若请求 URI 以 `location ^~ uri`的前缀开头，且该前缀是最长匹配（即比其他普通前缀更长），则命中该 `location`，**终止后续正则匹配**（但继续检查是否有更精确的 `=`匹配，不过 `=`已优先处理）。

示例：

```
location ^~ /static/ {
    # 匹配所有以 /static/ 开头的 URI（如 /static/css/style.css）
    # 且不再检查后续的正则（如 ~ \.css$）
}
```

#### 3. **正则匹配（`~`或 `~\*`）：按配置文件顺序匹配第一个命中的**

若请求未命中 `=`或 `^~`，则按顺序检查所有正则 `location`（`~`或 `~*`），**第一个匹配的正则生效**（与长度无关）。

⚠️ 注意：正则匹配**不考虑前缀长度**，仅按配置文件中出现的顺序判断。

示例：

```
location ~ \.php$ {  # 第一个正则：匹配 .php 结尾
    proxy_pass http://php-server;
}
location ~* \.(jpg|png)$ {  # 第二个正则：匹配图片（但不会生效，因第一个正则已覆盖部分情况？不，需看 URI）
    # 只有当 URI 不以 .php 结尾时才会检查这里
}
```

#### 4. **普通前缀匹配（无修饰符）：最长前缀优先**

若请求未命中上述三种类型，则选择**最长的普通前缀匹配**（`uri`最长的那个）。

示例：

```
location / {  # 根路径，最短前缀
    root /var/www/html;
}
location /blog/ {  # 更长前缀，优先于 /
    root /var/www/blog;
}
location /blog/article/ {  # 最长前缀，优先于 /blog/
    root /var/www/article;
}
# 请求 /blog/article/123.html 会匹配 /blog/article/
```

### **三、匹配流程总结**

Nginx 处理请求的 `location`匹配流程可简化为以下步骤：

1. **检查精确匹配（`=`）**：若存在完全匹配的 `location = uri`，直接使用它。
2. **检查优先前缀匹配（`^~`）**：若存在以当前 URI 为前缀的 `location ^~ uri`，且是最长前缀，使用它（跳过正则）。
3. **检查正则匹配（`~`/`~\*`）**：按顺序遍历所有正则 `location`，第一个匹配的生效。
4. **检查普通前缀匹配**：选择最长的普通前缀 `location`。

### **四、关键注意事项**

1. **正则匹配的顺序敏感**：多个正则 `location`时，**先定义的优先**（即使后面的正则更“具体”）。

   错误示例（可能不符合预期）：

   ```
   location ~* \.txt$ { return 403; }       # 先定义：匹配所有 .txt（不区分大小写）
   location ~ \.TXT$ { proxy_pass http://txt-server; }  # 后定义：本意处理大写 .TXT，但永远不会生效
   ```

   正确做法：将更具体的正则放在前面。

2. **`^~`与正则的冲突**：`^~`会跳过正则匹配，因此若希望某类前缀优先于正则，需用 `^~`。

   示例：

   ```
   location ^~ /admin/ {  # 管理后台路径，优先于所有正则
       deny all;  # 拒绝访问
   }
   location ~ \.php$ {  # 即使有 .php 正则，/admin/*.php 会被 ^~ 拦截
       proxy_pass http://php-server;
   }
   ```

3. **普通前缀的“最长匹配”**：Nginx 会比较所有普通前缀的长度（字符数），选择最长的匹配。

   示例：`location /a/b/c/`比 `location /a/b/`更长，优先匹配前者。

4. **`location`的嵌套无效**：Nginx 的 `location`是平级的，不能在一个 `location`内部嵌套另一个 `location`（语法错误）。

### **五、实战示例**

假设 Nginx 配置如下：

```
location = / { 
    return 200 "Exact match /"; 
}
location ^~ /static/ { 
    return 200 "Prefix ^~ /static/"; 
}
location ~ \.css$ { 
    return 200 "Regex ~ .css"; 
}
location ~* \.js$ { 
    return 200 "Regex ~* .js"; 
}
location /images/ { 
    return 200 "Prefix /images/"; 
}
location / { 
    return 200 "Default prefix /"; 
}
```

不同请求的匹配结果：

| 请求 URI           | 匹配的 Location        | 原因                                                  |
| ------------------ | ---------------------- | ----------------------------------------------------- |
| `/`                | `location = /`         | 精确匹配最高优先级。                                  |
| `/static/logo.png` | `location ^~ /static/` | 优先前缀匹配，跳过后续正则。                          |
| `/css/style.css`   | `location ~ \.css$`    | 未命中 `=`或 `^~`，正则按顺序匹配第一个（`\.css$`）。 |
| `/js/app.JS`       | `location ~* \.js$`    | 不区分大小写的正则匹配（`.JS`符合 `\.(js)$`）。       |
| `/images/1.jpg`    | `location /images/`    | 普通前缀最长匹配（`/images/`比 `/`长）。              |
| `/about.html`      | `location /`           | 普通前缀匹配（无其他更长的普通前缀）。                |

### **总结**

Nginx `location`的优先级核心是：**精确匹配 > 优先前缀匹配 > 正则匹配（顺序优先）> 普通前缀匹配（最长优先）**。掌握这一规则能帮助你高效设计路由逻辑，避免因匹配顺序导致的意外行为。
