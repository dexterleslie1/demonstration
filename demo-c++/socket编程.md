## Linux Socket UDP编程

具体用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-c++/demo-linux-programming/demo-socket/demo-udp

编译

```sh
gcc server.c -o server

gcc client.c -o client
```

运行

```sh
./server

./client
```

## Linux UDP打洞

>Github参考示例：https://github.com/mwarning/UDP-hole-punching-examples/tree/master

具体用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-c++/demo-c-udp-hole-punching

编译

```sh
gcc server.c -o server

gcc client.c -o client
```

运行

```sh
# 运行udp服务器
./server

# 运行两个udp客户端测试p2p通讯
# 客户端1
./client
# 客户端2
./client
```

