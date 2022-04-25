# 研究client_header_buffer_size、large_client_header_buffers等配置

## 参考资料

> [client_header_buffer_size配置](http://nginx.org/en/docs/http/ngx_http_core_module.html#client_header_buffer_size)  
> [large_client_header_buffers](http://nginx.org/en/docs/http/ngx_http_core_module.html#large_client_header_buffers)  
> [nginx配置参数解释：client_header_buffer_size、large_client_header_buffers](https://blog.csdn.net/kenzo2017/article/details/105877846)

## 编译和运行demo

```
# 编译镜像
sh build.sh

# 运行
docker-compose up

# 使用test-client测试大request line和大request header测试
```