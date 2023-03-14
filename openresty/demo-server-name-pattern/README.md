# 演示server_name pattern正则表达式

## 运行demo

```
# 启动openresty
docker-compose up

# 编辑/etc/hosts加入如下内容
127.0.0.1 1.a.com
127.0.0.1 2.b.com

# 分别使用不同的域名访问openresty会返回不同的结果
http://xxx.xxx.xxx.xxx
http://1.a.com
http://2.b.com
```

