# 设置和获取客户端`ip`地址

>示例的详细用法请参考 [链接](https://gitee.com/dexterleslie/demonstration/tree/master/openresty/demo-set-and-get-client-ip)

演示面向客户端的`openresty`如何防止伪造`x-forwarded-for`头，演示面向客户端的`openresty`如何获取客户端`ip`地址，位于`cdn`代理的`openresty`如何获取客户端`ip`地址。

编译并运行示例

```bash
docker compose build
docker compose up
```

通过修改示例中的`frontend`变量调试应用并借助下面命令辅助调试，当`frontend=true`时无法伪造`x-forwarded-for`头，当`frontend=false`时支持传递`x-forwarded-for`头

```bash
curl http://192.168.235.129 -H "x-forwarded-for: a, b, c"
```

