# `gost`用法

> todo 研究外网能够直接连接的代理
>
> [参考链接](https://gost.run)

## `gost`安装

使用`dcli`程序安装`gost`客户端或者服务端

- 安装`dcli`命令

  ```bash
  sudo rm -f /usr/bin/dcli && sudo curl https://fut001.oss-cn-hangzhou.aliyuncs.com/dcli/dcli-linux-x86_64 --output /usr/bin/dcli && sudo chmod +x /usr/bin/dcli
  ```

- 根据提示安装`gost`客户端或者服务端

  ```bash
  dcli gost install
  ```



## `gost`命令用法

在端口 8080 启动 http 代理

```sh
gost -L http://:8080
```

在端口 1080 启动 socks5 代理

```sh
gost -L socks5://:1080
```

同时启动 http 和 socks5 代理

```sh
gost -L http://:8080 -L socks5://:1080
```

有认证的 http 代理

```sh
gost -L http://user:passwd@:8080
```



使用 gost https 隧道传输 socks5 协议

运行 gost 服务器端，注意：经过测试只能够使用 https 协议作为隧道传输协议，SSH2和http协议都不能正常工作，可能是由于gfw拦截问题。

```sh
./gost -L https://secretuser:xxx@:30000
```

运行 gost 客户端

```sh
./gost -L :1080 -F https://secretuser:xxx@xxx.xxx.xxx.xxx:30000
```

