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

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/demo-http/demo-http-cache)

测试 `expires` 缓存：使用谷歌浏览器访问 http://localhost/1.html 查看 `test-expires.js`，打开谷歌浏览器 `debugger` > `networks` 查看 `test-expires.js` 的 `size` 列显示 “(from memory cache)” 表示 `test-expires.js` 从内存中读取。

查看 `test-no-store.js`每次都请求服务器获取。

查看 `test-no-cache.js` 使用 `ETag` 和服务器协商是否需要更新缓存。



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