# `x-forwarded-for`和`proxy_add_x_forwarded_for`用法

> [使用lua脚本操作x-forwarded-for头](https://github.com/openresty/lua-nginx-module/issues/801)

## 什么是`x-forward-for`？

>[HTTP X-Forwarded-For 介绍](https://www.runoob.com/w3cnote/http-x-forwarded-for.html)

X-Forwarded-For 是一个 HTTP 扩展头部。HTTP/1.1（RFC 2616）协议并没有对它的定义，它最开始是由 Squid 这个缓存代理软件引入，用来表示 HTTP 请求端真实 IP。如今它已经成为事实上的标准，被各大 HTTP 代理、负载均衡等转发服务广泛使用，并被写入 RFC 7239（Forwarded HTTP Extension）标准之中。

X-Forwarded-For 请求头格式非常简单，就这样：

```
X-Forwarded-For: client, proxy1, proxy2
```

可以看到，XFF 的内容由「英文逗号 + 空格」隔开的多个部分组成，最开始的是离服务端最远的设备 IP，然后是每一级代理设备的 IP。

如果一个 HTTP 请求到达服务器之前，经过了三个代理 Proxy1、Proxy2、Proxy3，IP 分别为 IP1、IP2、IP3，用户真实 IP 为 IP0，那么按照 XFF 标准，服务端最终会收到以下信息：

```
X-Forwarded-For: IP0, IP1, IP2
```

Proxy3 直连服务器，它会给 XFF 追加 IP2，表示它是在帮 Proxy2 转发请求。列表中并没有 IP3，IP3 可以在服务端通过 Remote Address 字段获得。我们知道 HTTP 连接基于 TCP 连接，HTTP 协议中没有 IP 的概念，Remote Address 来自 TCP 连接，表示与服务端建立 TCP 连接的设备 IP，在这个例子里就是 IP3。

Remote Address 无法伪造，因为建立 TCP 连接需要三次握手，如果伪造了源 IP，无法建立 TCP 连接，更不会有后面的 HTTP 请求。不同语言获取 Remote Address 的方式不一样，例如 php 是 $_SERVER["REMOTE_ADDR"]，Node.js 是 req.connection.remoteAddress，但原理都一样。



## 什么是`$proxy_add_x_forwarded_for`？

>$proxy_add_x_forwarded_for 说明`https://blog.csdn.net/bigtree_3721/article/details/72820594`

`$proxy_add_x_forwarded_for` 是 Nginx 中的一个内置变量，它用于在转发请求时，将客户端的原始 IP 地址（或经过其他代理服务器后的 IP 地址列表）添加到 `X-Forwarded-For` 请求头中。这个变量非常有用，尤其是在你的应用部署在反向代理（如 Nginx）之后，并且你希望获取到真实的客户端 IP 地址时。



## 调试`x-forwarded-for`

`x-forwarded-for`详细用法请参考`https://gitee.com/dexterleslie/demonstration/blob/master/openresty/x-forwarded-for/nginx-frontend.conf`

步骤如下：

1. 先运行基于`springboot`的辅助调试项目`https://gitee.com/dexterleslie/demonstration/tree/master/openresty/x-forwarded-for/demo-backend`

2. 使用配置`nginx-frontend.conf`运行`openresty`

   ```bash
   docker run --rm --net=host --name=demo-openresty -v $(pwd)/nginx-frontend.conf:/usr/local/openresty/nginx/conf/nginx.conf registry.cn-hangzhou.aliyuncs.com/future-public/demo-openresty-base-dev
   ```

3. 测试是否能够注入`x-forwarded-for`，预期是不能够注入`x-forwarded-for`

   ```bash
   curl http://192.168.235.128/api/v1/info -H "x-forwarded-for: 8888"
   ```

   

## 调试`$proxy_add_x_forwarded_for`

`$proxy_add_x_forwarded_for`详细用法请参考`https://gitee.com/dexterleslie/demonstration/blob/master/openresty/x-forwarded-for/nginx-backend.conf`

步骤如下：

1. 先运行基于`springboot`的辅助调试项目`https://gitee.com/dexterleslie/demonstration/tree/master/openresty/x-forwarded-for/demo-backend`

2. 使用配置`nginx-backend.conf`运行`openresty`

   ```bash
   docker run --rm --net=host --name=demo-openresty -v $(pwd)/nginx-backend.conf:/usr/local/openresty/nginx/conf/nginx.conf registry.cn-hangzhou.aliyuncs.com/future-public/demo-openresty-base-dev
   ```

3. 测试是否能够注入`x-forwarded-for`，预期是能够注入`x-forwarded-for`

   ```bash
   curl http://192.168.235.128/api/v1/info -H "x-forwarded-for: 8888"
   ```




## 设置和获取客户端`ip`地址

>示例的详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/openresty/demo-set-and-get-client-ip`

演示面向客户端的`openresty`如何防止伪造`x-forwarded-for`头，演示面向客户端的`openresty`如何获取客户端`ip`地址，位于`cdn`代理的`openresty`如何获取客户端`ip`地址。

编译并运行示例

```bash
docker compose build
docker compose up
```

通过修改示例中的`frontend`变量调试应用并借助下面命令辅助调试，当`frontend=true`时无法伪造`x-forwarded-for`头，当`frontend=false`时支持传递`x-forwarded-for`头

```bash
curl http://192.168.235.128 -H "x-forwarded-for: a, b, c"
```

