# http、springboot http学习

## 调试环境准备

> 运行 demo-http/spring-boot-http demo 作为rest api调试环境

# http get、post、put、delete

### GET

> 获取服务器上的资源
>
> NOTE: 使用get方法请求资源时，无法使用multipart/form-data、application/x-www-form-urlencoded提交表单数据

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
> NOTE: 使用put方法请求资源时，无法使用multipart/form-data提交表单数据

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
> NOTE: 使用delete方法请求资源时，无法使用application/x-www-form-urlencoded、multipart/form-data提交表单数据

#### 提交参数方式

使用query param方式**

```shell
curl -X DELETE http://localhost:8080/api/v1/testDeleteSubmitParamByUrl1?param1=v1
```

**使用application/json方式**

```shell
curl -X DELETE -d '{"param1":"v1"}' -H "Content-Type: application/json" http://localhost:8080/api/v1/testDeleteSubmitParamByJSON
```

## spring mvc获取参数

### @RequestParam

> 所有请求方法通过query param提交的参数都能通过此注解获取
>
> 此注解能够获取multipart/form-data、application/x-www-form-urlencoded提交的参数
>
> NOTE：此注解不能获取GET、DELETE通过application/x-www-form-urlencoded提交的参数，因为这两个http方法不能通过application/x-www-form-urlencoded提交参数

### @RequestBody

> 所有请求方法通过content-type: application/json提交参数都能通过此注解获取

### 参数绑定(parameter binding)

> 实质是pojo属性注入，具体用法参考 spring-boot-http demo
>
> 所有方法通过query param提交的参数都能通过此方法获取

https://www.logicbig.com/tutorials/spring-framework/spring-web-mvc/spring-request-param-data-binding.html



# http缓存

> https://blog.csdn.net/CRMEB/article/details/122835505
>
> 参考demo-http-cache

## 什么是http缓存

> 缓存是一种保存资源副本并在下次请求时直接使用该资源副本的技术。

## http缓存的类型

> **强缓存：**强缓存不会向服务器发送请求，直接从缓存中读取资源，在 chrome 控制台的 network 选项中可以看到该请求返回 200 的状态码，并且size显示from disk cache或from memory cache。
>
> **协商缓存：**协商缓存会先向服务器发送一个请求，服务器会根据这个请求的 request header 的一些参数来判断是否命中协商缓存，如果命中，则返回 304 状态码并带上新的 response header 通知浏览器从缓存中读取资源。

## 缓存控制

### 强缓存控制

> 强缓存可以通过设置Expires和Cache-Control 两种响应头实现。如果同时存在，Cache-Control优先级高于Expires。
>
> 
>
> **Expires：**Expires 响应头，它是 HTTP/1.0 的产物。代表该资源的过期时间，其值为一个绝对时间。它告诉浏览器在过期时间之前可以直接从浏览器缓存中存取数据。由于是个绝对时间，客户端与服务端的时间时差或误差等因素可能造成客户端与服务端的时间不一致，将导致缓存命中的误差。如果在Cache-Control响应头设置了 max-age 或者 s-max-age 指令，那么 Expires 会被忽略。例如：Expires: Wed, 21 Oct 2015 07:28:00 GMT
>
> **Cache-Control：**Cache-Control 出现于 HTTP/1.1。可以通过指定多个指令来实现缓存机制。主要用表示资源缓存的最大有效时间。即在该时间端内，客户端不需要向服务器发送请求。优先级高于 Expires。其过期时间指令的值是相对时间，它解决了绝对时间的带来的问题。例如：Cache-Control: max-age=315360000

### 协商缓存控制

> 协商缓存由 Last-Modified / IfModified-Since， Etag /If-None-Match实现，每次请求需要让服务器判断一下资源是否更新过，从而决定浏览器是否使用缓存，如果是，则返回 304，否则重新完整响应。
> 
>
> **Last-Modified、If-Modified-Since：**都是 GMT 格式的时间字符串，代表的是文件的最后修改时间。
>
> 在服务器在响应请求时，会通过Last-Modified告诉浏览器资源的最后修改时间。
> 浏览器再次请求服务器的时候，请求头会包含Last-Modified字段，后面跟着在缓存中获得的最后修改时间。
> 服务端收到此请求头发现有if-Modified-Since，则与被请求资源的最后修改时间进行对比，如果一致则返回 304 和响应报文头，浏览器只需要从缓存中获取信息即可。如果已经修改，那么开始传输响应一个整体，服务器返回：200 OK
> 但是在服务器上经常会出现这种情况，一个资源被修改了，但其实际内容根本没发生改变，会因为Last-Modified时间匹配不上而返回了整个实体给客户端（即使客户端缓存里有个一模一样的资源）。为了解决这个问题，HTTP/1.1 推出了Etag。Etag 优先级高与Last-Modified。
>
> **Etag、If-None-Match：**都是服务器为每份资源生成的唯一标识，就像一个指纹，资源变化都会导致 ETag 变化，跟最后修改时间没有关系，ETag可以保证每一个资源是唯一的。
>
> 在浏览器发起请求，浏览器的请求报文头会包含 If-None-Match 字段，其值为上次返回的Etag发送给服务器，服务器接收到次报文后发现 If-None-Match 则与被请求资源的唯一标识进行对比。如果相同说明资源没有修改，则响应返 304，浏览器直接从缓存中获取数据信息。如果不同则说明资源被改动过，则响应整个资源内容，返回状态码 200。