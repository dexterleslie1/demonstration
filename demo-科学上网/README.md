# 科学上网



## gost

> 使用`gost`作为`tunnel`封装为`https`流量逃避`GFW DPI`。

### 使用 dcli-go 方式安装

#### 服务端

在外网云主机提供商中创建主机 CentOS8，配置为 1C4G

参考 <a href="/dcli/README.html" target="_blank">链接</a> 安装 Dcli 程序

安装 gost 服务端

```bash
dcli gost install
```

启动`gost`服务端服务

```bash
systemctl start gost-server
```

停止`gost`服务端服务

```bash
systemctl stop gost-server
```



#### 客户端

在内网创建主机 CentOS8，配置为 1C4G

安装`gost`客户端

```bash
dcli gost install
```

- `gost`服务器`ip`地址为上面的`CentOS8 ip`地址。

启动`gost`客户端服务

```bash
systemctl start gost-client
```

停止`gost`客户端服务

```bash
systemctl stop gost-client
```



### 使用二进制方式安装

todo



### 使用服务

打开`firefox`浏览器，导航到`Settings`>`General`>`Network Settings`，点击`Settings...`按钮，`Connection Settings`配置信息如下：

- `Configure Proxy Access to the Internet`选择`Manual proxy configuration`
- `SOCKS Host`为内网创建的`CentOS8`主机`ip`地址
- `Port`为`1080`
- `SOCKS v5`为`勾选`
- `No proxy for`为`192.168.0.0/16,127.0.0.0/8,::1,www.example.com`
- `Proxy DNS when using SOCKS v5`为`勾选`

点击`OK`确认修改。

访问 `http://www.google.com` 如果能够打开说明`gost`服务配置成功。



## 使用 ssh 作为 tunnel 封装 socks5 协议以逃避 GFW DPI

注意：此方案按照繁琐，使用 SSH 动态端口转发方案更佳。

新建 centOS8 云主机

使用旧版本的 dcli 在 centOS8 主机上分别安装 frp 服务器端和客户端

frp 客户端按照 /etc/frp/frpc.ini 中的 socks5 配置示例配置 socks5服务

在 centOS8 云主机中新增用户并设置密码

```sh
groupadd sshportforwarding
useradd -g sshportforwarding sshportforwarding -s /bin/bash
passwd sshportforwarding
```

需要人工确认 centOS8 云主机指纹，NOTE：不需要登录服务器

```sh
ssh -p 22 sshportforwarding@xxx.xxx.xxx.xxx
```

在家庭内网中新建 linux 虚拟机并使用 ssh 端口转发功能 tunnel 封装 socks5 协议，-L 1080:127.0.0.1:1080 表示内网中 linux 虚拟机监听 1080 端口并通过 ssh tunnel 转发流量到 centOS8 主机的 1080 端口中

```sh
sshpass -p 'xxx' ssh -p 22 -o ServerAliveInterval=60 -o StrictHostKeyChecking=no -gNTf -L 1080:127.0.0.1:1080 sshportforwarding@xxx.xxx.xxx.xxx 2> /tmp/rc.local.log
```

使用 firefox 配置 socks5 代理测试是否配置成功



## 使用 ssh 动态端口转发配置 socks5 服务

注意：linux 虚拟机和 centOS8 云主机之间的通讯是 SSH 加密的。

新建 centOS8 云主机

在 centOS8 云主机中新增用户并设置密码

```sh
groupadd sshportforwarding
useradd -g sshportforwarding sshportforwarding -s /bin/bash
passwd sshportforwarding
```

在家庭内网中新建 linux 虚拟机并使用 ssh 动态端口转发配置 socks5 服务

```sh
ssh -NTf -D 0.0.0.0:10080 -p22 sshportforwarding@xxx.xxx.xxx.xxx
```

使用 firefox 配置 socks5 代理测试是否配置成功



## todo frp socks5 代理 + frp 客户端服务器之间加密通讯