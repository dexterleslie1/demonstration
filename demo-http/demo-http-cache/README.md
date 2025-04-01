# 演示http缓存

## 测试demo
```shell script
# 启动demo
docker-compose up

## 测试http expires缓存
# 使用chrome访问http://localhost/1.html查看test-expires.js，打开chrome debugger > networks查看test-expires.js的size列显示“(from memory cache)”表示test-expires.js从内存中读取。
# 查看test-no-store.js每次都请求服务器获取
# 查看test-no-cache.js使用ETag和服务器协商是否需要更新缓存
```
