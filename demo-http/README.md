# HTTP



## GET、POST、PUT、DELETE 方法

运行示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-http/spring-boot-http`协助测试



### GET

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



### POST

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



### PUT

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



### DELETE

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



## HTTP 缓存

> `https://blog.csdn.net/CRMEB/article/details/122835505`

详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-http/demo-http-cache`



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



## HTTP 头



### X-Frame-Options

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



## 在指定目录快速启动 HTTP 服务



### Five Server

在 VSCode 插件中安装 Five Server（名称：Live Server (Five Server)，作者：Yannick，名句：A better Live Server with instant updates, highlights and some PHP support.）

使用 VSCode 打开指定目录，在指定目录中点击鼠标右键 > `Open with Five Server (root)`

访问 `http://127.0.0.1:5500/` 即可



### Python

打开控制台并切换到指定目录，在指定目录中运行下面命令

```bash
python -m SimpleHTTPServer 8080
```

访问 `http://localhost:8080` 即可