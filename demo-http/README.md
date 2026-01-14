## HTTP1.0、HTTP1.1、HTTP2、HTTP3协议

>HTTP协议维基百科参考：https://en.wikipedia.org/wiki/HTTP

HTTP协议是Web通信的基础，经历了多个版本的演进，每个版本都有重要的改进。

### HTTP/1.0

HTTP/1.0是第一个被广泛使用的版本，采用**短连接**方式，即每个请求都需要建立新的TCP连接，请求完成后立即关闭。这种方式导致性能低下，因为每次请求都需要经历TCP三次握手和慢启动过程。此外，HTTP/1.0不支持Host头字段，一个服务器只能绑定一个域名。

### HTTP/1.1

HTTP/1.1是当前最广泛使用的版本，主要改进包括：
- **持久连接**：默认保持TCP连接，允许多个请求复用同一个连接
- **管道化**：允许客户端在收到响应前发送多个请求，但存在队头阻塞问题
- **Host头字段**：支持虚拟主机，一个IP可以绑定多个域名
- **分块传输编码**：支持流式传输
- **缓存控制**：引入Cache-Control、ETag等缓存机制

### HTTP/2

HTTP/2是二进制协议，主要特性包括：
- **多路复用**：在同一个连接上并行处理多个请求，彻底解决队头阻塞
- **头部压缩**：使用HPACK算法压缩请求头，减少重复传输
- **服务器推送**：服务器可以主动推送资源到客户端
- **流优先级**：允许设置请求的优先级
- **二进制分帧**：将消息分解为更小的帧，提高传输效率

### HTTP/3

HTTP/3基于QUIC协议，运行在UDP之上，主要改进：
- **0-RTT连接**：首次连接即可发送数据，减少握手延迟
- **改进的多路复用**：基于UDP，彻底解决TCP队头阻塞
- **连接迁移**：网络切换时连接不中断
- **前向纠错**：提高弱网环境下的传输可靠性
- **内置加密**：默认使用TLS 1.3加密

### 总结对比

| 特性     | HTTP/1.0 | HTTP/1.1 | HTTP/2   | HTTP/3   |
| -------- | -------- | -------- | -------- | -------- |
| 连接方式 | 短连接   | 持久连接 | 多路复用 | 基于QUIC |
| 队头阻塞 | 有       | 有       | 无       | 无       |
| 头部压缩 | 无       | 无       | HPACK    | QPACK    |
| 传输层   | TCP      | TCP      | TCP      | UDP      |
| 加密     | 可选     | 可选     | 可选     | 默认加密 |
| 连接建立 | 3-RTT    | 3-RTT    | 3-RTT    | 0-RTT    |

HTTP协议的演进主要围绕**性能优化**和**安全性提升**两个方向，从HTTP/1.0到HTTP/3，延迟不断降低，传输效率持续提升，安全性也得到显著增强。

## SpringBoot2.x支持HTTP1.0吗？

Spring Boot 2.x 支持 HTTP/1.0。不过，在实际应用中，HTTP/1.1 是默认且更常用的选择。

下面这个表格清晰地展示了 Spring Boot 2.x 中 HTTP 版本的支持情况：

| HTTP 版本    | Spring Boot 2.x 是否支持 | 默认情况 | 关键特性/说明                                                |
| :----------- | :----------------------- | :------- | :----------------------------------------------------------- |
| **HTTP/1.0** | **支持**                 | 否       | 默认为短连接，每个请求/响应后断开TCP连接。                   |
| **HTTP/1.1** | **支持**                 | **是**   | 默认为长连接（Keep-Alive），一个TCP连接可传输多个HTTP请求。可通过 `server.http-version=1.1` 显式配置。 |
| **HTTP/2**   | **支持**                 | 否       | 需要基于HTTPS并显式开启。支持多路复用、头部压缩等特性，能显著提升性能。配置项为 `server.http2.enabled=true`。 |

### 💡 注意事项与选择建议

了解支持情况后，在实际使用中还有几点需要注意：

- **配置方式**：虽然可以通过配置文件（如 `application.yml`）设置 `server.http-version` 属性来指定HTTP版本，但通常无需特意调整。Spring Boot 的默认设置（HTTP/1.1）已经能很好地满足绝大多数场景。

- **版本选择**：除非你有非常特殊的兼容性需求，否则**强烈建议使用默认的 HTTP/1.1**。它通过连接复用避免了 HTTP/1.0 频繁建立和断开 TCP 连接的开销，性能更好。对于追求更高性能的现代应用，可以考虑升级到 **HTTP/2**，它在高延迟和高并发场景下能带来显著的性能提升，但需要配置HTTPS。

- **与HTTP/2的关系**：Spring Boot 2.x 对 HTTP/2 的支持是其一个重要特性，但这并不影响其对 HTTP/1.0 和 1.1 的兼容。应用程序可以同时配置支持 HTTPS（HTTP/1.1）并重定向到 HTTP/2。

## curl判断服务器是否支持HTTP1.0

要判断服务器是否支持 HTTP/1.0，使用 curl 可以通过几种方法来实现。下面为你介绍具体的操作命令、结果解读以及一些需要注意的细节。

### 🔍 **直接测试与协议指定**

最直接的方法是使用 curl 的 `--http1.0` 选项来强制指定使用 HTTP/1.0 协议发起请求，并结合详细输出模式来观察协商过程。

*   **基本测试命令**：
    ```bash
    curl --http1.0 -v http://example.com
    ```
    或者使用等价的短选项 `-0`：
    ```bash
    curl -0 -v http://example.com
    ```
    这里的 `-v`（或 `--verbose`）参数至关重要，它会输出详细的连接和协议交互信息，这是我们判断的依据。

*   **如何解读结果**：执行上述命令后，请重点关注以 `*` 开头的行。
    *   **成功连接迹象**：如果你看到类似 `HTTP/1.0 200 OK` 或 `HTTP/1.0 301 Moved Permanently` 的响应状态行，这明确表示服务器接受并正确处理了你的 HTTP/1.0 请求。
    *   **连接过程信息**：在详细输出中，你可能会看到 `Connected to example.com port 80` 这样的信息，这表明TCP连接已建立。同时，留意后续的协议版本标识。

### 🔄 **对比测试与协议降级**

为了更准确地确认服务器的兼容性，可以进行对比测试。

*   **与HTTP/1.1对比**：你可以先使用默认的HTTP/1.1协议测试一下，确保服务器本身是可访问的。
    ```bash
    curl --http1.1 -I http://example.com
    ```
*   **理解协议降级**：有些服务器（尤其是现代服务器）可能配置为仅支持更高效、更安全的 HTTP/1.1 或 HTTP/2。如果服务器不支持 HTTP/1.0，curl 可能会报错，或者服务器在握手过程中拒绝连接。此时，观察 `-v` 输出的错误信息就非常关键。curl 的智能之处在于，它内置了协议协商机制，当高版本协议（如HTTP/2）不可用时，有时会自动降级尝试低版本协议，但通过明确指定 `--http1.0`，我们是在强制进行测试。

### ⚠️ **注意事项**

1.  **HTTPS请求**：如果测试的是 HTTPS 站点（`https://...`），上述方法同样适用。不过，SSL/TLS 握手可能会先于 HTTP 协议通信进行。在详细输出中，你会先看到SSL/TLS协商信息，然后是HTTP协议部分。
2.  **服务器配置差异**：服务器的支持情况完全取决于其具体配置。即使同一个域名，不同的服务器或路径配置也可能不同。
3.  **结合状态码判断**：除了看协议版本，HTTP响应状态码本身也能说明问题。例如，`200` 状态码表示成功，`404` 表示资源未找到（但请求本身被服务器处理了），这些都意味着连接和协议使用是成功的。而如果出现 `505 HTTP Version Not Supported` 这样的状态码，则清晰地表明服务器不支持你请求的HTTP版本。

### 💎 **总结**

简单来说，判断服务器是否支持 HTTP/1.0，最直接有效的方法就是：
```bash
curl --http1.0 -v http://your-server.com
```
然后**仔细查看命令输出的、以星号`*`开头的详细连接信息**，寻找明确的 `HTTP/1.0` 响应状态行或相关的错误提示。

## 使用Java Socket实现HTTP客户端

>说明：支持连接的重复使用。
>
>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-http/demo-http-client-socket
>
>HTTP/1.0 RFC：https://www.rfc-editor.org/rfc/rfc1945
>
>HTTP/1.1 RFC：https://www.rfc-editor.org/rfc/rfc9112

HttpClient.java：

```java
package com.future.demo;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpClient {
    private String host;
    private int port;

    private Socket socket;

    public HttpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public synchronized void connect() throws IOException {
        if (socket != null) {
            throw new IllegalStateException("已连接");
        }

        InetSocketAddress address = new InetSocketAddress(host, port);
        socket = new Socket();
        socket.connect(address);
    }

    public synchronized void close() throws IOException {
        if (socket != null) {
            socket.close();
            socket = null;
        }
    }

    public Response get(String url) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        String requestLine = "GET " + url + " HTTP/1.1\r\n";
        writer.write(requestLine);
        System.out.print("> " + requestLine);
        // 添加Host头否则服务器响应400错误
        requestLine = "Host: " + host + ":" + port + "\r\n";
        writer.write(requestLine);
        System.out.print("> " + requestLine);
        // 请求完毕后关闭连接
        // writer.write("Connection: close\r\n");
        // 空行终止请求头
        requestLine = "\r\n";
        writer.write(requestLine);
        System.out.print("> " + requestLine);
        // 发送数据到服务器
        writer.flush();

        InputStream rawInputStream = socket.getInputStream();
        Response response = new Response();
        // 响应头Content-Length的值以根据长度读取内容
        int contentLength = -1;
        ByteArrayOutputStream linerBuffer = new ByteArrayOutputStream();
        while (true) {
            int dataByte = rawInputStream.read();

            // 服务器端关闭连接
            if (dataByte == -1) {
                break;
            }

            if (dataByte == '\r') {
                dataByte = rawInputStream.read();
                if (dataByte == '\n') {
                    if (linerBuffer.size() == 0) {
                        // 空行表示余下的响应内容为body
                        System.out.println("< ");
                        break;
                    } else {
                        // 一行响应
                        String line = linerBuffer.toString(StandardCharsets.UTF_8);
                        linerBuffer = new ByteArrayOutputStream();

                        System.out.println("< " + line);

                        if (line.startsWith("HTTP/")) {
                            String[] httpVersionAndStatusCode = line.split(" ");
                            String httpVersion = httpVersionAndStatusCode[0];
                            int statusCode = Integer.parseInt(httpVersionAndStatusCode[1]);
                            response.setHttpVersion(httpVersion);
                            response.setStatusCode(statusCode);
                        } else if (line.contains(":")) {
                            if (line.toLowerCase().startsWith("content-length:")) {
                                contentLength = Integer.parseInt(line.substring("Content-Length:".length()).trim());
                            }

                            String[] keyAndValue = line.split(":");
                            String key = keyAndValue[0];
                            String value = keyAndValue[1];
                            response.setHeader(key, value);
                        }
                    }
                }
            } else {
                linerBuffer.write(dataByte);
            }
        }

        String body = null;
        if (contentLength > 0) {
            // 有明确的Content-Length，精确读取指定长度的字符
            byte[] bodyBytes = new byte[contentLength];
            rawInputStream.read(bodyBytes, 0, contentLength);
            body = new String(bodyBytes, StandardCharsets.UTF_8);
        }
        response.setBody(body);
        System.out.println("< " + body);

        return response;
    }

    public static class Response {
        private String httpVersion;
        private int statusCode;
        private Map<String, String> headers = new HashMap<>();
        private String body;

        public Response() {

        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getBody() {
            return body;
        }

        public void setHeader(String key, String value) {
            headers.put(key, value);
        }

        public String getHeader(String key) {
            return headers.get(key);
        }

        public void setHttpVersion(String httpVersion) {
            this.httpVersion = httpVersion;
        }

        public String getHttpVersion() {
            return httpVersion;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public int getStatusCode() {
            return statusCode;
        }

        @Override
        public String toString() {
            return "Response{" +
                    "httpVersion='" + httpVersion + '\'' +
                    ", statusCode=" + statusCode +
                    ", headers=" + headers +
                    ", body='" + body + '\'' +
                    '}';
        }
    }
}

```

调用HttpClient：

```java
package com.future.demo;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String host = "localhost";
        int port = 8080;
        HttpClient httpClient = null;
        try {
            httpClient = new HttpClient(host, port);
            httpClient.connect();

            HttpClient.Response response = httpClient.get("/api/v1/testGetSubmitParamByUrl1?param1=v1");
            System.out.println("响应：" + response);
            System.out.println();

            response = httpClient.get("/api/v1/testGetSubmitParamByUrl2?param1=v1-2");
            System.out.println("响应：" + response);
            System.out.println();
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
        }
    }
}

```

## `GET`、`POST`、`PUT`、`DELETE`方法

运行示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-http/spring-boot-http`协助测试



### `GET`

> 获取服务器上的资源
>
> 注意：使用 GET 方法请求资源时，无法使用 multipart/form-data、application/x-www-form-urlencoded 提交表单数据

#### 提交参数方式

**使用query param方式**

```shell
curl -X GET http://localhost:8080/api/v1/testGetSubmitParamByUrl?param1=v1
```

**使用application/json方式**

```shell
curl -X GET -d '{"param1":"v1"}' -H "Content-Type: application/json" http://localhost:8080/api/v1/testGetSubmitParamByJSON
```



### `POST`

> 在服务器上创建资源

#### 提交参数方式

**使用query param方式**

```shell
curl -X POST http://localhost:8080/api/v1/testPostSubmitParamByUrl1?param1=v1
```

**使用multipart/form-data方式**

```shell
curl -X POST -F "param1=v1" -H "Content-Type: multipart/form-data" http://localhost:8080/api/v1/testPostSubmitParamByMultipartFormData
```

**使用application/x-www-form-urlencoded方式**

```shell
curl -X POST -d "param1=v1" -H "Content-Type: application/x-www-form-urlencoded" http://localhost:8080/api/v1/testPostSubmitParamByFormUrlencoded1
```

**使用application/json方式**

```shell
curl -X POST -d '{"param1":"v1"}' -H "Content-Type: application/json" http://localhost:8080/api/v1/testPostSubmitParamByJSON
```



### `PUT`

> 更新服务器上的资源
>
> 注意：使用 PUT 方法请求资源时，无法使用 multipart/form-data 提交表单数据

#### 提交参数方式

**使用query param方式**

```shell
curl -X PUT http://localhost:8080/api/v1/testPutSubmitParamByUrl1?param1=v1
```

**使用application/x-www-form-urlencoded方式**

```shell
curl -X PUT -d "param1=v1" -H "Content-Type: application/x-www-form-urlencoded" http://localhost:8080/api/v1/testPutSubmitParamByFormUrlencoded1
```

**使用application/json方式**

```shell
curl -X PUT -d '{"param1":"v1"}' -H "Content-Type: application/json" http://localhost:8080/api/v1/testPutSubmitParamByJSON
```



### `DELETE`

> 删除服务器上的资源
>
> 注意：使用 DELETE 方法请求资源时，无法使用 application/x-www-form-urlencoded、multipart/form-data 提交表单数据

#### 提交参数方式

**使用query param方式**

```shell
curl -X DELETE http://localhost:8080/api/v1/testDeleteSubmitParamByUrl1?param1=v1
```

**使用application/json方式**

```shell
curl -X DELETE -d '{"param1":"v1"}' -H "Content-Type: application/json" http://localhost:8080/api/v1/testDeleteSubmitParamByJSON
```



## `HTTP`缓存

> 参考链接：https://blog.csdn.net/CRMEB/article/details/122835505

### 什么是缓存

缓存是一种保存资源副本并在下次请求时直接使用该资源副本的技术。

### 缓存的类型

**强缓存：**强缓存不会向服务器发送请求，直接从缓存中读取资源，在 chrome 控制台的 network 选项中可以看到该请求返回 200 的状态码，并且 size 显示 from disk cache 或 from memory cache 。

**协商缓存：**协商缓存会先向服务器发送一个请求，服务器会根据这个请求的 request header 的一些参数来判断是否命中协商缓存，如果命中，则返回 304 状态码并带上新的 response header 通知浏览器从缓存中读取资源。

### 缓存控制

#### 强缓存控制

强缓存可以通过设置Expires和Cache-Control 两种响应头实现。如果同时存在，Cache-Control优先级高于Expires。

**Expires：**Expires 响应头，它是 HTTP/1.0 的产物。代表该资源的过期时间，其值为一个绝对时间。它告诉浏览器在过期时间之前可以直接从浏览器缓存中存取数据。由于是个绝对时间，客户端与服务端的时间时差或误差等因素可能造成客户端与服务端的时间不一致，将导致缓存命中的误差。如果在Cache-Control响应头设置了 max-age 或者 s-max-age 指令，那么 Expires 会被忽略。例如：Expires: Wed, 21 Oct 2015 07:28:00 GMT

**Cache-Control：**Cache-Control 出现于 HTTP/1.1。可以通过指定多个指令来实现缓存机制。主要用表示资源缓存的最大有效时间。即在该时间端内，客户端不需要向服务器发送请求。优先级高于 Expires。其过期时间指令的值是相对时间，它解决了绝对时间的带来的问题。例如：Cache-Control: max-age=315360000

#### 协商缓存控制

协商缓存由 Last-Modified / IfModified-Since， Etag /If-None-Match实现，每次请求需要让服务器判断一下资源是否更新过，从而决定浏览器是否使用缓存，如果是，则返回 304，否则重新完整响应。

**Last-Modified、If-Modified-Since：**都是 GMT 格式的时间字符串，代表的是文件的最后修改时间。

在服务器在响应请求时，会通过Last-Modified告诉浏览器资源的最后修改时间。

浏览器再次请求服务器的时候，请求头会包含Last-Modified字段，后面跟着在缓存中获得的最后修改时间。

服务端收到此请求头发现有if-Modified-Since，则与被请求资源的最后修改时间进行对比，如果一致则返回 304 和响应报文头，浏览器只需要从缓存中获取信息即可。如果已经修改，那么开始传输响应一个整体，服务器返回：200 OK

但是在服务器上经常会出现这种情况，一个资源被修改了，但其实际内容根本没发生改变，会因为Last-Modified时间匹配不上而返回了整个实体给客户端（即使客户端缓存里有个一模一样的资源）。为了解决这个问题，HTTP/1.1 推出了Etag。Etag 优先级高与Last-Modified。

**Etag、If-None-Match：**都是服务器为每份资源生成的唯一标识，就像一个指纹，资源变化都会导致 ETag 变化，跟最后修改时间没有关系，ETag可以保证每一个资源是唯一的。

在浏览器发起请求，浏览器的请求报文头会包含 If-None-Match 字段，其值为上次返回的Etag发送给服务器，服务器接收到次报文后发现 If-None-Match 则与被请求资源的唯一标识进行对比。如果相同说明资源没有修改，则响应返 304，浏览器直接从缓存中获取数据信息。如果不同则说明资源被改动过，则响应整个资源内容，返回状态码 200。

### 示例

>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/master/demo-http/demo-http-cache

测试 `expires` 缓存：使用谷歌浏览器访问 http://localhost 查看 `test-expires.js`，打开谷歌浏览器 `debugger` > `networks` 查看 `test-expires.js` 的 `size` 列显示 “(from memory cache)” 表示 `test-expires.js` 从内存中读取（在使用强制缓存）。

查看 `test-no-store.js`每次都请求服务器获取。

查看 `test-no-cache.js` 使用 `ETag` 和服务器协商是否需要更新缓存。

### http://localhost/首页index.html缓存不生效原因

在调试示例 https://gitee.com/dexterleslie/demonstration/tree/master/demo-http/demo-http-cache 过程中，发现访问http://localhost/，为何首页index.html即使响应头包含Cache-Control: public, max-age=1h，开发者工具Network中的Size依旧不会显示Served from memory cache，而JavaScript、CSS、图片等资源会显示Served from memory cache呢？

这是一个非常好的问题，它触及了浏览器缓存机制中一个精妙但重要的细节。简单来说，**这是因为浏览器对 HTML 文档和静态资源（JS、CSS、图片）采用了不同的缓存策略和加载逻辑。**

下面我们来详细拆解一下原因：

#### 核心原因：导航请求 vs. 资源请求

当你在地址栏输入 `http://localhost/`或点击链接时，浏览器发起的是一个 **“导航请求”** 。它的目标是获取一个全新的文档来渲染整个页面。而对于 `<script>`, `<link>`, `<img>`等标签的引用，浏览器发起的是 **“子资源请求”** 。

这两种请求被浏览器区别对待。

------

##### 1. 为什么 HTML (index.html) 通常不走 Memory Cache？

尽管你的 `index.html`设置了 `Cache-Control: public, max-age=3600`（1小时），浏览器在处理导航请求时，出于正确性和用户体验的考虑，会采取更保守的策略：

- **保证内容实时性**：HTML 是页面的骨架和入口。如果上一个页面的 HTML 被缓存在内存中，用户可能会看到一个过时甚至错误的页面结构。例如，登录状态改变、A/B测试分组变化、或者有紧急的页面公告需要展示。浏览器默认倾向于为导航请求执行一次“新鲜度检查”，以确保用户看到的是服务器上最新的 HTML。
- **重新验证机制**：对于带有 `max-age`的响应，浏览器在后续导航请求中，**通常会发起一个条件请求**（如 `If-None-Match`或 `If-Modified-Since`），而不是完全无网络请求的从内存读取。
  - 服务器收到请求后，会对比资源的 ETag 或最后修改时间。
  - 如果资源未变，返回 `304 Not Modified`。这个 `304`响应体很小，但**它仍然是一个网络请求**，只是节省了下载完整 HTML 的时间。在 Network 面板里，你会看到这个请求的状态码是 `304`，Size 列可能显示为 “from disk cache” 或具体大小，但不会是 “from memory cache”。
  - 如果资源已变，则返回 `200 OK`和新内容。
- **Memory Cache 的特性**：Memory Cache 是易失的，它与标签页的生命周期绑定。当您关闭标签页或导航到新站点时，内存缓存通常会被清除。将作为导航入口的 HTML 长期放在不稳定的 Memory Cache 中是危险的。

**所以，在你的场景中，最可能的情况是：**

浏览器对 `index.html`发起了一次网络请求，服务器返回了 `304 Not Modified`。这个 `304`响应可能被浏览器从 **Disk Cache** 中快速提供（因为 `max-age`尚未过期，且强缓存失效后会进行协商缓存），或者进行了一次非常快的网络往返。无论如何，它都不是直接从 Memory Cache 中提供的，因此 Network 面板不会显示 “Served from memory cache”。

------

##### 2. 为什么 JS、CSS、图片等资源会显示 Served from memory cache？

这些静态资源的行为与 HTML 不同：

- **非导航性请求**：它们的加载是为了辅助渲染当前已经确定的 HTML 文档。即使它们稍微旧一点（在 `max-age`内），通常也不会破坏页面的基本结构和功能。
- **性能优先**：为了极致优化性能，避免不必要的网络延迟，浏览器会更大胆地使用快速的 Memory Cache。如果资源在内存中且未过期，浏览器会直接使用它，连向服务器验证的步骤都省去了。这能显著减少页面渲染时间。
- **缓存策略独立**：每个资源的缓存策略是独立的。你可以为 HTML 设置较短的 `max-age`或不缓存，同时为静态资源设置较长的 `max-age`并启用 Memory Cache，这是一种常见的最佳实践。

**所以，在你的场景中：**

当浏览器解析 `index.html`时，发现其中的 JS、CSS、图片等资源的缓存仍然有效（在1小时内），并且它们恰好在 Memory Cache 中，于是浏览器直接从内存中加载它们，无需任何网络活动。因此，Network 面板清晰地显示了 “Served from memory cache”。

------

#### 总结与类比

| 特性             | HTML (导航请求)                        | JS/CSS/Images (子资源请求)             |
| ---------------- | -------------------------------------- | -------------------------------------- |
| **缓存目标**     | 保证获取最新文档                       | 追求最快加载速度                       |
| **默认行为**     | 倾向进行新鲜度检查 (协商缓存)          | 倾向直接使用强缓存 (Memory/Disk Cache) |
| **Network 表现** | 通常是 `200`(from disk cache) 或 `304` | `200`(from memory cache)               |
| **生命周期**     | 与用户会话紧密相关，需谨慎缓存         | 可长期缓存，策略灵活                   |

**一个简单的比喻：**

- **访问首页 (`index.html`)**：就像你去一家每天更新菜单的餐厅。服务员（浏览器）每次都会去后厨（服务器）确认一下今天的菜单（HTML）是不是最新的，哪怕菜单纸本身没变（返回 `304`），他也要跑这一趟，以防万一。
- **加载页面上的图片/脚本**：就像餐厅里固定的餐具和桌椅（静态资源）。只要它们没坏（缓存未过期），服务员就直接拿过来用，不需要每次都跑去仓库确认。

#### 如何让 HTML 也显示 from memory cache？（不推荐）

虽然技术上可以通过一些“黑科技”实现，例如在 Service Worker 中拦截导航请求并从缓存中返回 Response，但这会引入复杂性和潜在的错误（如无法及时更新页面）。**在标准 Web 开发中，强烈不建议这样做**。默认的浏览器行为是为了在保证功能正确性的前提下，提供最好的性能和体验平衡。

### 配置Pragma

>提示：针对现代浏览器不需要再设置此配置了。

在早期 HTTP/1.0 中，`Pragma: no-cache`被用作 **向后兼容的“禁止缓存”指令**。（为什么还存在 `Pragma`？**向后兼容**：为了支持老版本 HTTP/1.0 客户端（如古老的浏览器或设备）。）

它试图告诉客户端或中间代理：**不要使用缓存的副本，应向服务器验证或直接获取最新内容**。

在现代开发中，**应使用 `Cache-Control`替代 `Pragma`来控制缓存**。

Pragma取值：

- no-cache：**唯一被广泛支持和认可的 `Pragma`值**。客户端请求中：告诉服务器“不要用缓存，给我最新数据”（如刷新页面时）。服务器响应中：试图禁止客户端缓存（但效果不可靠）。
- no-store：意图是“不允许在任何地方存储响应”（包括内存、磁盘）。但 **HTTP/1.0 规范并未明确定义此值**，且多数客户端/代理会忽略它。正确做法是使用：Cache-Control: no-store 所以 `Pragma: no-store`**不应使用**。
- 其他值（如 `public`, `private`, `max-age`等）：这些是 `Cache-Control`的合法值，**不能用于 `Pragma`**。如果在 `Pragma`中使用 `max-age=3600`，浏览器会**直接忽略**，不会报错，但也不会生效。**RFC 1945（HTTP/1.0）**：首次定义 `Pragma`，仅提到 `no-cache`作为示例。**RFC 7234（HTTP/1.1 Caching）**：明确指出：Pragma头已弃用用于控制缓存，仅应用于与 HTTP/1.0 客户端的向后兼容。

### 配置Cache-Control作用

`Cache-Control`是 HTTP 协议中最核心的**缓存控制机制**，其主要作用是**通过明确的指令规范客户端（如浏览器）、代理服务器（如 CDN）如何缓存、何时使用缓存或重新请求资源**，从而平衡「性能优化」「服务器负载」和「数据新鲜度」三大目标。

#### **具体作用可拆解为以下核心维度**

------

##### **1. 提升资源加载性能，降低延迟**

缓存的本质是「避免重复请求」。`Cache-Control`通过 `max-age`、`public`等指令，让客户端或代理直接复用本地缓存的资源（如图片、JS/CSS、静态文件），无需每次向服务器发起网络请求。

- 例如：静态资源（如 `logo.png`）设置 `max-age=31536000`（1年），用户首次访问后，后续访问直接从本地内存/磁盘读取，**加载时间从几十毫秒降至几微秒**，大幅提升页面渲染速度。
- 代理缓存（如 CDN）：通过 `s-maxage`让 CDN 节点缓存资源，用户请求可直接从最近的 CDN 节点获取，避免跨地域回源到源服务器，**降低网络延迟**。

##### **2. 减少服务器负载，节省带宽成本**

重复的请求会消耗服务器的计算和带宽资源。`Cache-Control`通过限制不必要的请求，显著降低源服务器的压力：

- 对于高频访问的静态资源（如首页图片、公共 JS 库），`public`+ `max-age`让代理和客户端缓存资源，源服务器只需处理首次请求，**后续请求由缓存层直接响应**。
- 动态内容（如 API 接口）若允许客户端缓存（`private, max-age=60`），可减少短时间内相同请求的重复计算（如数据库查询），**降低服务器 CPU/IO 负载**。

##### **3. 控制数据新鲜度，避免过时内容**

并非所有资源都适合长期缓存（如实时股价、用户订单状态）。`Cache-Control`通过 `no-cache`、`must-revalidate`、`max-age`等指令，确保缓存的资源在「可用」和「新鲜」之间取得平衡：

- `max-age=300`（5分钟）：资源5分钟内直接使用缓存，5分钟后需验证（`no-cache`效果），避免用户看到过时数据。
- `no-cache`：强制每次使用前向服务器验证（通过 `ETag`或 `Last-Modified`），确保资源未修改时才使用缓存（如新闻详情页）。
- `must-revalidate`：缓存过期后必须验证，禁止使用已失效的缓存（如支付页面的安全策略）。

##### **4. 区分缓存对象，保护敏感数据**

不同资源的敏感性不同（如公开图片 vs 用户隐私信息），`Cache-Control`通过 `public`、`private`等指令限制缓存范围：

- `public`：允许所有缓存层（客户端、代理、CDN）存储资源（适合公开静态资源，如官网图片）。
- `private`：仅允许客户端（如浏览器）缓存，**禁止代理/CDN 缓存**（适合用户个人信息，如个人中心页面，避免中间节点泄露隐私）。

##### **5. 兼容复杂网络环境，提升可靠性**

在网络不稳定或服务器故障时，`Cache-Control`可通过 `stale-while-revalidate`、`stale-if-error`等指令提供「降级方案」，保障基础可用性：

- `stale-while-revalidate=60`：缓存过期后，先返回旧数据给用户（保证体验），同时后台异步验证新数据（60秒内完成）。
- `stale-if-error=3600`：若服务器返回 5xx 错误，允许客户端继续使用1小时内的过期缓存（如电商大促时服务器压力大，避免页面崩溃）。

##### **6. 替代传统缓存头（如 Expires），统一标准**

HTTP/1.0 依赖 `Expires`（基于绝对时间的过期头，受客户端时间误差影响），而 `Cache-Control`是 HTTP/1.1 引入的更灵活机制，支持相对时间（`max-age`）、细粒度控制（如 `s-maxage`针对代理），且优先级更高。它通过标准化指令避免了不同客户端对 `Expires`的解析差异，**提升缓存行为的可预测性**。

#### **总结**

`Cache-Control`的核心价值是**通过精细化的缓存策略，在「性能」「成本」「数据准确性」三者间找到最优解**。合理配置它能让网站更快、更稳定，同时减轻服务器压力；反之，错误的配置可能导致「缓存失效（频繁请求）」或「数据过时（用户看到旧内容）」。

### 配置Cache-Control设置

`Cache-Control`是 HTTP 协议中用于控制缓存策略的重要响应/请求头字段。它的取值可以分为**通用指令**、**请求指令**、**响应指令**，以及针对缓存验证、过期时间等场景的细分指令。以下是常见取值及含义：

#### **一、通用指令（适用于请求和响应）**

| 取值                 | 说明                                                         |
| -------------------- | ------------------------------------------------------------ |
| `public`             | 允许任何缓存器（包括客户端、代理服务器）缓存该资源。         |
| `private`            | 仅允许客户端（如浏览器）缓存，不允许中间代理服务器（如CDN）缓存。 |
| `no-cache`           | **不直接使用缓存**，但需向服务器验证缓存是否有效（通过 `If-None-Match`或 `If-Modified-Since`），有效则使用缓存，否则重新获取。 注意：`no-cache`≠ 不缓存，而是“强制验证后使用”。 |
| `no-store`           | **禁止任何缓存**（包括客户端和代理），每次必须向服务器重新请求完整资源。 |
| `max-age=<seconds>`  | 设置资源的最大缓存时间（从请求时间开始计算）。 例如 `max-age=3600`表示1小时内直接使用缓存，无需验证。 |
| `s-maxage=<seconds>` | 仅对共享缓存（如代理、CDN）生效，覆盖 `max-age`。 若未指定，共享缓存使用 `max-age`。 |
| `must-revalidate`    | 缓存过期后**必须向服务器验证**有效性，不能使用过期缓存（除非网络失败）。 |
| `proxy-revalidate`   | 类似 `must-revalidate`，但仅对共享缓存（代理/CDN）生效。     |
| `no-transform`       | 禁止缓存或代理对资源进行转换（如压缩图片、修改格式）。       |

#### **二、请求指令（客户端发送的请求头）**

客户端（如浏览器）可通过请求头的 `Cache-Control`影响缓存行为：

| 取值                    | 说明                                                         |
| ----------------------- | ------------------------------------------------------------ |
| `max-age=0`             | 要求缓存立即过期，必须向服务器验证（等价于 `no-cache`的严格模式）。 |
| `max-stale[=<seconds>]` | 允许使用过期的缓存（即使已过期），可选指定最大过期时间（如 `max-stale=300`表示最多接受5分钟内的过期缓存）。 |
| `min-fresh=<seconds>`   | 要求缓存至少在指定时间内保持新鲜（即缓存的剩余有效期 ≥ `min-fresh`）。 例如 `min-fresh=60`表示需要至少1分钟内不会过期的缓存。 |
| `only-if-cached`        | 仅使用本地缓存，若缓存不存在则返回504错误（网关超时），不向服务器请求。 |
| `cache-extension`       | 扩展指令（以 `-`开头），用于自定义功能（需双方支持）。       |

#### **三、响应指令（服务器返回的响应头）**

服务器通过响应头的 `Cache-Control`控制客户端/代理的缓存策略（核心常用指令）：

| 取值                               | 说明                                                         |
| ---------------------------------- | ------------------------------------------------------------ |
| `immutable`                        | 声明资源内容**永久不变**（如静态文件版本化URL），客户端无需在有效期内验证（减少请求）。 |
| `stale-while-revalidate=<seconds>` | 允许客户端在后台异步验证缓存的同时，先返回过期缓存（提升用户体验）。 例如 `stale-while-revalidate=60`表示：缓存过期后，先返回旧数据，同时用60秒异步验证新数据。 |
| `stale-if-error[=<seconds>]`       | 当服务器出错（如5xx错误）时，允许使用过期缓存的最长时间。 例如 `stale-if-error=3600`表示服务器错误时，可使用1小时内的过期缓存。 |

#### **四、关键场景示例**

1. **静态资源（如JS/CSS/图片）**：

   `Cache-Control: public, max-age=31536000, immutable`

   （公开缓存，缓存1年，且声明内容不变，无需验证）

2. **动态API（如用户信息）**：

   `Cache-Control: private, no-cache`

   （仅客户端缓存，每次使用前需验证）

3. **敏感数据（如支付页面）**：

   `Cache-Control: no-store`

   （禁止任何缓存，每次重新请求）

4. **CDN缓存优化**：

   `Cache-Control: public, s-maxage=86400, max-age=3600`

   （代理/CDN缓存1天，客户端缓存1小时）

#### **注意事项**

- `no-cache`与 `no-store`的区别：`no-cache`允许缓存但需验证，`no-store`完全禁止缓存。
- `max-age`优先级高于 `Expires`（HTTP/1.0的过期头），现代浏览器优先使用 `max-age`。
- 组合使用时需注意逻辑（如 `public`+ `no-store`无意义，`no-store`会覆盖其他缓存指令）。

### Cache-Control、Expires、ETag、Last-Modified并存时优先级

当 HTTP 响应头中 **`Expires`、`ETag`、`Last-Modified`同时存在**时，它们的优先级和协作关系需要结合 **缓存验证机制** 来理解。这三类头分别服务于 **“过期时间判断”** 和 **“内容新鲜度验证”**，共同构成 HTTP 缓存的核心逻辑。

#### 📌 先明确三类头的基本作用

| 头字段          | 类型       | 作用                                                         |
| --------------- | ---------- | ------------------------------------------------------------ |
| `Expires`       | 过期时间头 | 指定资源的**绝对过期时间**（如 `Expires: Wed, 21 Oct 2024 07:28:00 GMT`）。客户端在该时间前可直接使用缓存，无需请求服务器。 |
| `Last-Modified` | 实体头     | 资源的**最后修改时间**（如 `Last-Modified: Tue, 20 Oct 2024 10:00:00 GMT`）。用于**条件请求验证**（如 `If-Modified-Since`）。 |
| `ETag`          | 实体头     | 资源的**唯一标识符**（如 `ETag: "33a64df551425fcc55e4d42a148795d9f25f89d4"`）。基于内容生成（如哈希），比 `Last-Modified`更精确，用于**强验证**（如 `If-None-Match`）。 |

#### ✅ 优先级与协作逻辑：分阶段判断

HTTP 缓存的核心流程是 **“先判断是否过期 → 若过期则验证是否新鲜”**，三类头的优先级和协作关系如下：

##### 阶段 1：判断缓存是否“新鲜”（是否直接使用缓存）

这一步由 **`Expires`（HTTP/1.0）** 和 **`Cache-Control: max-age`（HTTP/1.1）** 主导（若 `Cache-Control`存在，优先级高于 `Expires`）。

- 若客户端时间 < `Expires`时间（且 `Cache-Control`未禁止）：缓存**新鲜**，直接使用，**不发送请求**。
- 若客户端时间 ≥ `Expires`时间（或 `Cache-Control`指示过期）：缓存**过期**，进入**验证阶段**（需向服务器发送请求确认是否更新）。

##### 阶段 2：缓存过期后的“新鲜度验证”（是否需要重新下载）

当缓存过期时，客户端会发送**条件请求**，携带 `Last-Modified`或 `ETag`生成的验证头，服务器根据验证结果决定是否返回新资源：

- **`ETag`优先级高于 `Last-Modified`**（强验证 > 弱验证）。
- 客户端发送 `If-None-Match: "<ETag值>"`（基于上次响应的 `ETag`）。
- 服务器校验 ETag：若资源未变（ETag 匹配），返回 `304 Not Modified`（无响应体，继续使用缓存）；若变化，返回 `200 OK`+ 新资源。
- 若客户端未发送 `If-None-Match`（如首次请求后清除了 ETag），才会 fallback 到 `Last-Modified`：发送 `If-Modified-Since: "<Last-Modified值>"`。
- 服务器校验 Last-Modified：若资源未修改（时间匹配），返回 `304`；若修改，返回 `200`+ 新资源。

#### 📊 优先级总结表

| 场景             | 主导头                              | 优先级顺序                                       | 结果                                                         |
| ---------------- | ----------------------------------- | ------------------------------------------------ | ------------------------------------------------------------ |
| 缓存是否新鲜     | `Cache-Control: max-age`> `Expires` | 1. `Cache-Control: max-age` 2. `Expires`         | 未过期 → 直接用缓存；过期 → 进入验证阶段                     |
| 缓存过期后的验证 | `ETag`> `Last-Modified`             | 1. `ETag`（强验证） 2. `Last-Modified`（弱验证） | ETag 匹配 → 304；不匹配 → 200 + 新资源 若 ETag 缺失，Last-Modified 匹配 → 304；不匹配 → 200 + 新资源 |

#### 🔍 关键注意点

1. **`Cache-Control: max-age`会覆盖 `Expires`**

   若响应同时有 `Cache-Control: max-age=3600`和 `Expires`，客户端**优先使用 `max-age`**（`max-age`是相对时间，更准确，不受客户端时间误差影响）。

2. **`ETag`比 `Last-Modified`更可靠**

   - `Last-Modified`精度仅到秒级，若资源在 1 秒内多次修改，可能无法检测；
   - `ETag`基于内容哈希，可精确到字节级变化（如 `W/"xxx"`是弱 ETag，忽略 minor 变化；强 ETag 无 `W/`前缀，严格匹配内容）。

3. **三者共存的意义**

   同时设置是为了**兼容不同客户端和优化验证效率**：

   - `Expires/Cache-Control`减少不必要的请求（缓存新鲜时）；
   - `ETag`提供精确的强验证（避免 `Last-Modified`的秒级误差）；
   - `Last-Modified`作为 fallback（兼容不支持 ETag 的旧客户端）。

#### ✅ 示例流程

假设响应头为：

```
Cache-Control: max-age=3600
Expires: Wed, 21 Oct 2024 07:28:00 GMT
ETag: "abc123"
Last-Modified: Tue, 20 Oct 2024 10:00:00 GMT
```

1. **首次请求**：客户端无缓存，服务器返回 `200 OK`+ 上述头 + 资源体。

2. **1 小时内再次请求**：`max-age=3600`未过期，客户端直接用缓存，不发请求。

3. **1 小时后请求**：缓存过期，客户端发送条件请求：

   ```
   GET /resource HTTP/1.1
   If-None-Match: "abc123"
   If-Modified-Since: Tue, 20 Oct 2024 10:00:00 GMT
   ```

4. **服务器校验**：若资源未变（ETag 匹配且 Last-Modified 未变），返回 `304 Not Modified`；客户端继续用缓存。

5. **若资源修改**：服务器返回 `200 OK`+ 新资源 + 新的 `ETag`/`Last-Modified`。

#### 📌 结论

- **新鲜度判断**：`Cache-Control: max-age`> `Expires`（决定缓存是否直接使用）。
- **过期后验证**：`ETag`（强验证） > `Last-Modified`（弱验证）（决定是否需要重新下载资源）。
- 三者共存是为了兼顾兼容性、效率和精确性，无冲突，而是**分阶段协作**。

### Chrome浏览器访问没有Cache-Control和Expires响应头的CSS资源，默认缓存行为是什么呢？

当 Chrome 浏览器访问一个 CSS 资源（或其他静态资源），而服务器**没有返回 Cache-Control 或 Expires 响应头**时，Chrome 会采用一套默认的启发式缓存（heuristic caching）策略来决定是否缓存该资源以及缓存多久。

------

#### 1. 默认行为概述

根据 [RFC 7234](https://tools.ietf.org/html/rfc7234#section-4.2.2)和浏览器的实现：

- 如果响应**没有明确的缓存控制指令**（如 `Cache-Control`、`Expires`），但包含 **Last-Modified** 响应头，浏览器可能会进行**启发式缓存**。

- Chrome 的启发式缓存算法大致为：

  取当前时间与 `Last-Modified`时间的差值，乘以一个系数（通常是 **10%**），作为缓存的**最大有效期**（max-age）。

  公式近似为：

  ```
  缓存时间 ≈ (当前时间 - Last-Modified时间) × 0.1
  ```

  但最终不会超过一定上限（不同浏览器可能不同，Chrome 一般最多几小时到几天）。

- 如果没有 `Last-Modified`，也没有其他缓存相关头，则可能**不缓存**，或仅使用非常短的缓存（比如内存缓存，会话级）。

------

#### 2. 具体示例

假设：

- 服务器返回 CSS 文件，无 `Cache-Control`、`Expires`。
- 有 `Last-Modified: Wed, 21 Oct 2023 07:28:00 GMT`。
- 用户现在访问的时间是 2024-01-15。

计算：

- 时间差 ≈ 约 86 天。
- 启发式缓存时间 ≈ 86 × 0.1 ≈ 8.6 天。
- Chrome 可能给这个资源一个约 **8.6 天** 的缓存期（实际可能取整，且受浏览器内部上限限制）。

这意味着在这段时间内，再次请求该 URL 时，Chrome 可能直接使用磁盘缓存（不发请求，或发条件 GET 带 If-Modified-Since）。

------

#### 3. 验证方法

在 Chrome DevTools 的 **Network** 面板中：

1. 勾选 **Disable cache** 再取消，确保缓存生效。
2. 首次加载 CSS，查看 Response Headers，确认没有 `Cache-Control`/`Expires`。
3. 刷新页面（非强制刷新），看 Size 列显示 “from disk cache” 或 “from memory cache”。
4. 记录 **Time** 列，如果是 0 或从缓存读取，说明命中缓存。
5. 可以修改系统时间或等待一段时间后再请求，观察何时缓存失效。

------

#### 4. 注意事项

- 不同浏览器（Firefox、Safari）的启发式算法可能不同。
- 现代网站强烈建议**显式设置 Cache-Control**，避免依赖启发式缓存，因为其行为不确定且可能因浏览器版本而异。
- 对于频繁变动的资源（如带 hash 的文件名），可设长缓存；对于 HTML 或不带 hash 的 CSS/JS，通常设 `no-cache`或短缓存以便更新。

------

#### 5. 参考规范与资料

- RFC 7234 Section 4.2.2 Heuristic Expiration
- Chrome 源码相关逻辑（如 `network::HttpCache::Transaction::UpdateCacheEntry`）
- MDN: [HTTP Caching > Heuristic Caching](https://developer.mozilla.org/en-US/docs/Web/HTTP/Caching#heuristic_caching)

------

**总结**：

Chrome 在没有 `Cache-Control`/`Expires`但有 `Last-Modified`时，会使用基于最后修改时间的 10% 作为缓存时长（启发式缓存），否则可能不缓存或极短缓存。为避免不确定性，生产环境应始终明确指定缓存策略。

## `HTTP`头



### `X-Frame-Options`

详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-computer-information-security/demo-csrf`



X-Frame-Options是一个HTTP响应头，用于给浏览器指示允许一个页面可否在`<frame>`、`<iframe>`、`<embed>`或者`<object>`中展现。这个响应头主要是为了防止点击劫持（clickjacking）攻击，即攻击者通过在透明的、或者不易察觉的iframe上覆盖一个看似无害的元素，诱使用户在该元素上进行点击，而实际上点击的却是隐藏在iframe里的恶意页面。以下是关于X-Frame-Options的详细解释：

一、作用

网站可以使用X-Frame-Options来确保自己网站的内容没有被嵌套到别人的网站中去，从而避免点击劫持攻击。

二、语法和可选值

X-Frame-Options有两个（或曾经有三个，但已有一个被废弃）可能的值：

1. **DENY**：表示该页面不允许在`<frame>`中展示，即便是在相同域名的页面中嵌套也不允许。

2. **SAMEORIGIN**：表示该页面可以在相同域名页面的`<frame>`中展示。

   曾经还有一个值**ALLOW-FROM uri**（现已被废弃），表示该页面可以在指定来源的`<frame>`中展示。但并非所有浏览器都支持这个值，而且随着Content-Security-Policy HTTP响应头的frame-ancestors指令的普及，X-Frame-Options的ALLOW-FROM值已经被废弃。

三、配置示例

以下是在不同服务器中配置X-Frame-Options的示例：

1. **Apache**

   要在所有页面上发送X-Frame-Options响应头，需要将以下行添加到`site`的配置中：

   - 若设置为DENY：`Header set X-Frame-Options "DENY"`
   - 若设置为SAMEORIGIN：`Header always set X-Frame-Options "SAMEORIGIN"`

2. **Nginx**

   要发送X-Frame-Options响应头，需要将以下行添加到`http`、`server`或者`location`的配置中：

   - 若设置为DENY：`add_header X-Frame-Options DENY;`
   - 若设置为SAMEORIGIN：`add_header X-Frame-Options SAMEORIGIN always;`
   - （对于已废弃的ALLOW-FROM，原则上不推荐使用，但如果需要配置，可以使用`add_header X-Frame-Options 'ALLOW-FROM https://xxx.xxxxxx.com';`这样的格式，注意'ALLOW-FROM'和URL之间有空格，且URL需要包含协议部分。然而，由于此选项已被废弃，且不被所有浏览器支持，因此应避免使用。）

3. **IIS**

   要发送X-Frame-Options响应头，需要添加以下配置到Web.config文件中：

   ```xml
   <system.webServer>
       ...
       <httpProtocol>
           <customHeaders>
               <add name="X-Frame-Options" value="SAMEORIGIN"/>
           </customHeaders>
       </httpProtocol>
       ...
   </system.webServer>
   ```

四、注意事项

1. 使用`<meta>`标签来设置X-Frame-Options是无效的。只有当像上面示例那样设置HTTP头X-Frame-Options才会生效。
2. 请注意浏览器的兼容性。不是所有浏览器都支持X-Frame-Options的所有值。特别是ALLOW-FROM值，已经被现代浏览器废弃。
3. 在配置X-Frame-Options时，请确保不要重复配置或配置错误，这可能会导致意外的行为或安全问题。

总之，X-Frame-Options是一个重要的安全响应头，可以帮助防止点击劫持攻击。但是，随着Web安全的发展和新技术的出现，如Content-Security-Policy的frame-ancestors指令的普及，X-Frame-Options的某些值已经被废弃或不再推荐使用。因此，在配置时需要注意浏览器的兼容性和安全性。



## 在指定目录快速启动`HTTP`服务



### `Five Server`

在 VSCode 插件中安装 Five Server（名称：Live Server (Five Server)，作者：Yannick，名句：A better Live Server with instant updates, highlights and some PHP support.）

使用 VSCode 打开指定目录，在指定目录中点击鼠标右键 > `Open with Five Server (root)`

访问 `http://127.0.0.1:5500/` 即可



### `Python`

打开控制台并切换到指定目录，在指定目录中运行下面命令

```bash
python -m SimpleHTTPServer 8080
```

访问 `http://localhost:8080` 即可

## Chrome开发者工具Network的Size列

在 Chrome 开发者工具的 **Network（网络）** 面板中，**Size（大小）** 列显示的是每个网络请求的资源大小信息。这个列的值会根据资源的加载方式和缓存状态呈现不同的内容。以下是 **Size 列可能出现的各种显示值及其含义**：

------

### 📌 Size 列的显示值及含义

| 显示值                                                       | 含义                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| **`<size> KB`/ `<size> B`**（如 `15.6 KB`）                  | **实际传输大小**（Transferred size）：表示从服务器下载到浏览器的字节数。 如果是压缩过的资源（如 gzip、brotli），这个值通常小于原始文件大小。 |
| **`(from cache)`** 或 **`(memory cache)`** / **`(disk cache)`** | 资源**未从网络下载**，而是直接从浏览器缓存中读取。 - `(memory cache)`：从内存缓存加载，速度快，关闭标签可能失效。 - `(disk cache)`：从磁盘缓存加载，持久化存储。 此时 **Size 列不显示具体字节数**，而是标注来源。 |
| **`0 B`**                                                    | 请求被取消、失败，或未传输任何数据（如 404 但无响应体，或被拦截）。 也可能是请求被阻止（如广告拦截器）。 |
| **`-`**（横线）                                              | 极少数情况下，Chrome 无法获取大小信息，显示为 `-`。常见于某些特殊协议或内部请求。 |
| **`pending`**                                                | 请求仍在进行中，尚未完成，因此大小未知。                     |
| **`cached`（旧版 Chrome）**                                  | 早期版本可能显示为 `cached`，现在统一为 `(from cache)`并细分类型。 |

------

### 🔍 示例说明

| 场景                                 | Size 列显示                        | 解释                                                         |
| ------------------------------------ | ---------------------------------- | ------------------------------------------------------------ |
| 首次访问一个 CSS 文件（未缓存）      | `8.2 KB`                           | 从服务器下载了 8.2KB 的数据（可能是压缩后大小）              |
| 刷新页面，CSS 来自磁盘缓存           | `(disk cache)`                     | 未发起网络请求，直接从硬盘缓存读取                           |
| 同域名下图片已加载过，再次访问       | `(memory cache)`                   | 从内存快速读取，无需网络                                     |
| 请求一个不存在的图片（404）          | `1.2 KB`                           | 虽然资源不存在，但服务器返回了错误页面（如 404 HTML），所以有传输大小 |
| 请求被 Service Worker 拦截并返回缓存 | `(from cache)`或 `0 B`             | 取决于实现方式，可能标记为缓存或无传输                       |
| 请求被取消（如跳转、停止加载）       | `0 B`或 `(canceled)`（状态栏显示） | 实际未传输数据                                               |

------

### 📎 补充说明

- **Size ≠ Content-Length**：

  `Size`是**实际传输大小**（考虑压缩、分块编码等），而 `Content-Length`是原始文件大小（在 Response Headers 中可见）。

- 要查看**原始文件大小**，可查看 **Headers 面板中的 `content-length`**，或关注 **Resource Size** 在 **Preview** 或 **Response** 标签页附近的信息。

- 在 **Network 面板底部汇总**中，Chrome 会显示：

  - **Total transferred**：所有资源实际下载的总大小（不含缓存）
  - **Total resources**：所有资源数量
  - **Finish time**：加载完成时间

------

### ✅ 如何区分“缓存”和“网络请求”？

| 特征                | 网络请求                      | 缓存请求                                |
| ------------------- | ----------------------------- | --------------------------------------- |
| Size 列             | 显示具体数值（如 `12 KB`）    | 显示 `(from cache)`、`(memory cache)`等 |
| Status 列           | `200`、`304`、`404`等         | 通常为空或为 `(from cache)`             |
| Waterfall（瀑布图） | 有蓝色/灰色条（表示下载时间） | 无条，或极短的灰色线（瞬间完成）        |

------

### 🛠️ 小贴士

- 想强制重新加载并忽略缓存？按 `Ctrl+F5`（Windows）或 `Cmd+Shift+R`（Mac）。
- 可在 Network 面板勾选 **“Disable cache”**（禁用缓存）来测试真实网络性能。

------

### ✅ 总结

**Size 列常见值：**

- `数字 + B/KB/MB`→ 实际下载大小
- `(from cache)`/ `(memory cache)`/ `(disk cache)`→ 来自缓存，未联网
- `0 B`→ 无数据传输（失败或被拦截）
- `pending`→ 请求未完成
- `-`→ 极少情况，信息不可用

掌握这些值有助于分析页面加载性能、缓存策略和请求有效性。