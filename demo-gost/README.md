## 概念

> `todo`研究外网能够直接连接的代理
>
> [参考链接](https://gost.run)



## 安装

### `Ubuntu`

使用`dcli`程序安装`gost`客户端或者服务端

- 安装`dcli`命令

  ```bash
  sudo rm -f /usr/bin/dcli && sudo curl https://fut001.oss-cn-hangzhou.aliyuncs.com/dcli/dcli-linux-x86_64 --output /usr/bin/dcli && sudo chmod +x /usr/bin/dcli
  ```

- 根据提示安装`gost`客户端或者服务端

  ```bash
  dcli gost install
  ```



### `Windows11`

访问 https://github.com/go-gost/gost/releases?page=2 下载 `gost_3.0.0_windows_amd64.zip`（下载成功后`Windows`安全中心会报告木马威胁并自动隔离文件，此时在`Windows`安全中心设置恢复隔离文件）。

解压`gost_3.0.0_windows_amd64.zip`后`Windows`安全中心会报告`gost.exe`木马威胁并自动隔离文件，此时在`Windows`安全中心设置恢复隔离文件。

测试`gost.exe`是否正常执行

```cmd
gost.exe -L :1080 -F https://secretuser:YourNever8urX3!dRt03@23.91.96.217:30001
```

- 第一次执行上面命令`Windows`安全中心会报告`gost.exe`的安全威胁，此时在`Windows`安全中心选择`允许`即可。

创建`Windows`服务：

- 创建服务（需要管理员权限）

  ```cmd
  sc create GostService binPath= "C:\gost_3.0.0_windows_amd64\gost.exe -L :1080 -F https://secretuser:xxx@x.x.x.x:30001" start= auto
  ```

- 启动服务

  ```cmd
  sc start GostService
  ```

- 停止服务

  ```cmd
  sc stop GostService
  ```

- 删除服务

  ```cmd
  sc delete GostService
  ```



## 命令用法

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

