## libsoup库是什么呢？

>库中的API参考文档：https://www.manpagez.com/html/libsoup-2.4/libsoup-2.4-2.72.0/

libsoup 是一个用于 C 语言的 HTTP 客户端/服务器库，它是 GNOME 项目的一部分。

简单来说，它为开发者提供了一套强大的工具，方便应用程序通过网络进行通信，比如访问网页、与 API 交互等。它的设计与 GTK+ 和 GLib 紧密集成，特别适合用于开发 GNOME 桌面环境下的应用程序。

### 主要特性

*   **双模式 API**：同时支持异步（基于 GLib 主循环和回调）和同步两种编程模型，为开发者提供了灵活性。
*   **连接管理**：能够自动缓存连接，提高网络请求的效率。
*   **安全性**：通过 GnuTLS 提供对 SSL/TLS 加密连接的支持。
*   **代理支持**：支持通过代理服务器进行连接，包括代理认证和 SSL 隧道。
*   **身份验证**：支持多种客户端和服务端身份验证方式，如 Digest、NTLM 和 Basic 认证。

### 应用场景

除了基础的 HTTP 请求，libsoup 还被广泛用于实现更复杂的网络功能，例如构建支持标准 WebSocket (`ws://`) 和安全 WebSocket (`wss://`) 协议的客户端程序。

### 安全须知

作为一个处理网络数据的核心库，libsoup 也曾被发现存在一些安全漏洞，例如 HTTP 请求夹带、缓冲区溢出和拒绝服务等问题。因此，保持库的版本更新对于保障应用安全至关重要。

## libsoup库websocket服务器

具体用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/blob/main/demo-c++/demo-libsoup-wsserver.c

安装依赖

```sh
# 安装GLib
sudo apt install libglib2.0-dev

# 安装json-c库用于解析json
 sudo apt install libjson-c-dev
```

编译和运行

```sh
gcc -O2 -g -Wall -Wextra -o server demo-libsoup-wsserver.c $(pkg-config --cflags --libs libsoup-2.4 glib-2.0 gio-2.0 json-c)

./server
```

使用本站示例Websocket客户端测试：https://gitee.com/dexterleslie/demonstration/tree/main/demo-spring-boot/demo-spring-websocket/spring-websocket

## libsoup库websocket客户端

具体用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/blob/main/demo-c++/demo-libsoup-wsclient.c

安装依赖

```sh
# 安装GLib
sudo apt install libglib2.0-dev

# 安装json-c库用于解析json
 sudo apt install libjson-c-dev
```

编译和运行

```sh
gcc -O2 -g -Wall -Wextra -o client demo-libsoup-wsclient.c $(pkg-config --cflags --libs libsoup-2.4 glib-2.0 gio-2.0 json-c)

./client
```

使用本站示例Websocket服务器测试：https://gitee.com/dexterleslie/demonstration/tree/main/demo-spring-boot/demo-spring-websocket/spring-websocket
