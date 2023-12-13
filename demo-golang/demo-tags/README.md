## build tags用法

> 通过使用不同的build参数控制程序的编译行为。
> https://www.cnblogs.com/linyihai/p/10859945.html

```shell
# 编译dev版本
go build -tags dev -o demo

# 运行程序
./demo

# 编译release版本
go build -tags release -o demo

# 运行程序
./demo
```

