# 演示squid作为http代理服务器的使用

## 测试demo

> https://blog.csdn.net/never_late/article/details/127252668

```shell
# 启动demo
docker-compose up

# 测试 x-forwarded-for是否正确，查看控制台输出
curl -x http://squid服务器ip:3128 -L http://demo-squid-openresty

# 测试伪造 x-forwarded-for，查看控制台输出
curl -x http://squid服务器ip:3128 -L http://demo-squid-openresty -H "x-forwarded-for: 9999"
```

