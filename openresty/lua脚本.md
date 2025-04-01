# `lua`脚本相关

> 注意：下面所有演示需要先参考 <a href="/openresty/编译docker基础镜像.html" target="_blank">链接</a> 编译`openresty`基础镜像



## 引用第三方`lua`库

>详细示例请参考`https://gitee.com/dexterleslie/demonstration/tree/master/openresty/lua-scripting/demo-lua-package-path-and-require`

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

> `openresty lua`入门演示详细请参考`https://gitee.com/dexterleslie/demonstration/tree/master/openresty/lua-scripting/demo-getting-started`

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

> 演示详细请参考`https://gitee.com/dexterleslie/demonstration/tree/master/openresty/lua-scripting/demo-uuid`

编译演示

```bash
docker compose build
```

运行演示

```bash
docker compose up -d
```

访问`http://localhost`，显示`Hello world! UUID: d85ab70f-096a-49e9-a9bd-3ebd026196e5`



## `lua`各个`phase`的用法

>演示详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/openresty/lua-scripting/demo-lua-phase`

各个`phase`执行顺序如下图：

![xxx](https://public-images-fut.oss-cn-hangzhou.aliyuncs.com/openresty-lua-phases.png)

### `init_by_lua`

> 参考链接`https://openresty-reference.readthedocs.io/en/latest/Directives/#init_by_lua`

`init_by_lua` 是 OpenResty（一个基于 Nginx 与 LuaJIT 的 Web 平台）中的一个指令，它允许用户在 Nginx 启动时执行 Lua 脚本代码。这个特性非常有用，因为它允许开发者在 Nginx 服务器启动时执行一些初始化操作，比如加载配置文件、初始化数据库连接池、预加载模块等。

`init_by_lua` 执行的 Lua 脚本是在 Nginx master 进程中运行的，而不是在 worker 进程中。这意味着它只会在 Nginx 启动时执行一次，而不是每次请求时。

由于 `init_by_lua` 是在 Nginx master 进程中执行的，因此它不能访问 `ngx.var`、`ngx.req`、`ngx.resp` 等与请求相关的 API，因为这些 API 只在处理请求的 worker 进程中有效。

`init_by_lua`用法如下：

- 在服务器启动时预加载`lua`模块

  ```lua
  init_by_lua_block {
      -- 通过此钩子在服务器启动时预加载 Lua 模块
      cjson = require "cjson"
  }
  ```

- 初始化共享存储

  ```lua
  lua_shared_dict dogs 1m;
  init_by_lua_block {
      -- 初始化共享存储
      local dogs = ngx.shared.dogs;
      dogs:set("Tom", 56)
  }
  ```

  

### `init_worker_by_lua`

>参考链接`https://openresty-reference.readthedocs.io/en/latest/Directives/#init_worker_by_lua`

`init_worker_by_lua` 是 OpenResty（一个基于 Nginx 与 LuaJIT 的 Web 平台）中的一个指令，它允许用户在 Nginx 的每个 worker 进程启动时执行 Lua 脚本代码。与 `init_by_lua` 不同，`init_worker_by_lua` 是在每个 worker 进程启动后立即执行的，而不是在 Nginx master 进程启动时执行。

使用场景

- **worker 进程特有的初始化**：由于 `init_worker_by_lua` 是在每个 worker 进程中独立执行的，因此它适用于执行与 worker 进程相关的初始化操作，比如设置定时器、监听信号、初始化 worker 进程特有的连接池等。
- **后端服务连接**：为每个 worker 进程建立并预热到后端服务（如数据库、缓存等）的连接。
- **性能监控**：启动性能监控的定时任务或收集启动时的性能指标。

注意事项

- `init_worker_by_lua` 执行的 Lua 脚本是在 Nginx 的 worker 进程中运行的，每个 worker 进程都会独立执行该脚本。
- 由于 `init_worker_by_lua` 是在 worker 进程中执行的，因此它可以访问与请求相关的 API（如 `ngx.var`、`ngx.req`、`ngx.resp`），但请注意，在 worker 进程启动时，这些 API 可能还没有与具体的请求相关联，因此它们的值可能是未定义的或不可用的。
- 在使用 `init_worker_by_lua` 时，要注意不要执行阻塞操作，因为这会延迟 worker 进程的启动时间，并可能影响 Nginx 的整体性能。

结论

`init_worker_by_lua` 是 OpenResty 提供的一个非常有用的功能，它允许开发者在每个 Nginx worker 进程启动时执行 Lua 脚本，以完成各种与 worker 进程相关的初始化任务。正确使用 `init_worker_by_lua` 可以帮助开发者更好地管理 Nginx worker 进程，提高应用程序的性能和可靠性。

在`worker`进程初始化时执行`lua`定时任务

```lua
init_worker_by_lua_block {
    ngx.timer.every(5, function(premature)  
        if premature then  
            return  
        end  

        -- 执行周期性任务  
        local now = ngx.now()  
        ngx.log(ngx.NOTICE, "执行周期性任务，当前时间： ", now)  
    end)
}
```



### `log_by_lua`

>参考链接`https://openresty-reference.readthedocs.io/en/latest/Directives/#log_by_lua`

`log_by_lua` 是 OpenResty（一个基于 Nginx 与 Lua 的动态 Web 平台）或任何支持 `ngx_lua` 模块的 Nginx 版本中的一个指令，它允许你在 Nginx 的日志处理阶段执行 Lua 代码。这个指令通常用于在请求被处理完毕后，但在发送响应给客户端之前，执行一些自定义的日志记录、监控或统计任务。

`log_by_lua` 的一个关键优势是它是非阻塞的，这意呀着它不会延迟响应的发送。这对于执行可能耗时的日志记录任务特别有用，因为你可以确保客户端不会因为这些任务而等待更长时间。

提醒：

- 即使`lua`脚本执行出错，此阶段依旧被回调
- `log_by_lua_block`在`location`配置块中时，各个`location`配置的`log_by_lua_block`是相互独立的
- 在`http`配置块中配置的`log_by_lua_block`会被所有请求触发回调

示例代码：

```nginx
http {
    # 在请求被处理完毕后，但在发送响应给客户端之前时执行
    # 提醒：即使lua脚本执行出错，此阶段依旧被回调
    # 提醒：在http配置块中配置的log_by_lua_block会被所有请求触发回调
    # log_by_lua_block {
    #     ngx.log(ngx.NOTICE, "这是log_by_lua_block在http配置块中输出的日志！！！")
    # }

    server {
    	listen       80;
    	server_name  localhost;
	
    	location / {
            content_by_lua_block {
                ngx.header.content_type = "text/json;charset=utf-8";
                ngx.say(cjson.encode({dog = ngx.shared.dogs:get('Tom'), cat = 8}))
            }
    	}

        location /2 {
            content_by_lua_block {
                ngx.header.content_type = "text/text;charset=utf-8";
                ngx.say("成功调用接口2")
            }

            # 测试log_by_lua_block在不同的location配置块中是否各自独立的
            # 结论：log_by_lua_block在location配置块中时，各个location配置的log_by_lua_block是相互独立的
            log_by_lua_block {
                ngx.log(ngx.NOTICE, "这是log_by_lua_block在location配置块中输出的日志！！！")
            }
    	}
    }
}
```



### `init_by_lua`和`init_worker_by_lua`

#### 分别在两个阶段创建的同名全局变量

注意：如果分别在两个阶段创建同名全家变量`init_worker_by_lua`阶段的全局变量会覆盖`init_by_lua`阶段的全局变量。

`init_worker_by_lua`中的全局变量

- `init_worker_by_lua*`在每个Nginx worker进程启动时执行，这里定义的全局变量实际上是每个worker进程内的局部变量。
- 不同worker进程之间的全局变量是隔离的，因此不会相互冲突。
- 但如果worker进程内部有多个协程并发执行，并且它们访问和修改同一个全局变量，那么就需要考虑并发控制的问题，以避免数据竞争和不一致。

在`init_by_lua*`和`init_worker_by_lua*`阶段定义的全局变量通常不会跨进程冲突，因为它们分别运行在Nginx的不同上下文（master进程和worker进程）中。



### `content_by_lua_block`和`access_by_lua_block`

`content_by_lua_block`和`access_by_lua_block`是OpenResty（一个基于Nginx与Lua的高性能Web平台）中用于处理HTTP请求的两个重要指令，它们分别代表了Nginx处理请求的不同阶段和用途。以下是两者的主要区别：

1. 使用阶段和目的

   - content_by_lua_block：
     - **阶段**：此指令在Nginx处理请求的**内容生成阶段**被调用。
     - **目的**：主要用于生成响应内容，即处理完请求后，由Lua脚本生成并返回给客户端的响应体。
     - **适用场景**：适用于需要根据请求动态生成响应内容的场景，如API接口开发、动态页面渲染等。

   - access_by_lua_block：
     - **阶段**：此指令在Nginx处理请求的**访问控制阶段**被调用，位于内容生成阶段之前。
     - **目的**：主要用于访问控制、权限校验、请求预处理等。
     - **适用场景**：适用于需要根据请求信息（如请求头、请求参数等）进行权限验证、日志记录、请求转发等操作的场景。

2. 功能和用法

   - content_by_lua_block：
     - 提供了一个Lua代码块，可以在其中编写Lua脚本来生成响应内容。
     - 可以使用Nginx提供的Lua API（如`ngx.say`、`ngx.print`等）来输出响应内容。
     - 通常与`location`指令配合使用，指定哪些请求会被这个Lua代码块处理。

   - access_by_lua_block：
     - 同样提供了一个Lua代码块，但主要用于访问控制和预处理。
     - 可以访问请求相关的变量（如请求头、请求参数等），并根据这些信息执行相应的逻辑。
     - 可以通过修改Nginx变量（如`ngx.var.some_var`）来影响后续的处理流程。

3. 示例

   - **content_by_lua_block示例**：

     ```nginx
     location /test {  
         default_type 'text/plain';  
         content_by_lua_block {  
             ngx.say('Hello, World!');  
         }  
     }
     ```

     这个配置会在访问`/test`路径时，返回文本`Hello, World!`。

   - **access_by_lua_block示例**：

     ```nginx
     location / {  
         access_by_lua_block {  
             if ngx.var.arg_key != "expected_value" then  
                 return ngx.HTTP_FORBIDDEN;  
             end  
         }  
         # 其他处理逻辑...  
     }
     ```

     这个配置会检查请求参数`key`的值，如果不是`expected_value`，则直接返回403 Forbidden状态码。

4. 总结

   `content_by_lua_block`和`access_by_lua_block`在Nginx处理HTTP请求的过程中扮演着不同的角色，分别用于生成响应内容和进行访问控制。根据实际需求选择合适的指令，并编写相应的Lua脚本来实现特定的功能。

`access_by_lua_block`实际使用请参考 [lua-resty-limit-traffic](https://github.com/openresty/lua-resty-limit-traffic) 插件的配置`README.md`涉及到这个指令

`content_by_lua_block`示例代码：

```nginx
location / {
    content_by_lua_block {
        ngx.header.content_type = "text/json;charset=utf-8";
        ngx.say(cjson.encode({dog = ngx.shared.dogs:get('Tom'), cat = 8}))
    }
}
```



## todo `lua api`

>nginx for lua api 之获取请求中的参数 `http://www.shixinke.com/openresty/openresty-get-request-arguments`
>
>nginx lua api `https://github.com/openresty/lua-nginx-module`