## 参考资料

> [nginx ddos protection](https://inmediatum.com/en/blog/engineering/ddos-attacks-prevention-nginx/)



## shared dict内存消耗测试

> 结论：1g openresty缓存大约能够存在1000w条ipv4格式的key-value记录



## limit_conn参数，单ip连接数限制

> 结论：控制单个ip能够和nginx建立的网络连接数，但是控制不了单个ip和操作系统建立网络连接数。
>
> 
>
> 现象
>
> - ​	nginx error.log报告错误：demo-openresty-limit-conn    | 2022/04/23 09:08:52 [error] 7#7: *17 limiting connections by zone "conn_zone", client: 192.168.1.53, server: localhost, request: "GET / HTTP/1.1", host: "192.168.1.111"
> - ​	浏览器访问报告错误：503 Service Temporarily Unavailable
>
> 
>
> 注意：需要ngx.sleep模拟接口处理过程，否则因为处理速度太快无法产生并发连接



## client_max_body_size参数，限制body大小

> 结论：nginx只能拒绝超过限制的大body并且返回客户端413错误码，但是大body数据例如10MB已经通过网络传输到服务器（通过iftop命令得到证明）
>
> TODO 集成到fail2ban



## client_body_buffer_size参数，没有必要做实验



## client_header_buffer_size、large_client_header_buffers参数

> 结论：client_header_buffer_size不会导致客户端请求错误，large_client_header_buffers会导致客户端请求错误。如果request line超过large_client_header_buffers配置客户端报告414错误，如果request header或者request line+request header超过large_client_header_buffers配置客户端报告400错误。
>
> TODO 集成到fail2ban



## client_header_timeout参数，NOTE：未编写基于socket的程序，不能模拟测试



## client_body_timeout参数，NOTE：未编写基于socket的程序，不能模拟测试