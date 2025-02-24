# SSH 客户端



## 需求

需求如下：

- 支持 macOS、windows、ubuntu 操作系统
- 支持文件上传和下载
- 支持群发命令功能



结论：暂时使用 FinalShell



## Termius

### 总结

总结如下：

- 支持 ubuntu
- 支持文件上传和下载拖拽功能
- 付费版本才支持群发命令功能



### 安装

下载 Termius.deb `https://termius.com/download/linux`

安装 Termius

```bash
sudo dpkg -i Termius.deb
```

注意：需要配置和登录 Termius Vaults 以保存 SSH 连接信息到 Termius 远程的 Vaults，帐号：gmail，密码：ri..90...34...



## FinalShell

### 总结

总结如下：

- 支持 ubuntu
- 支持文件拖拽上传和右键点击下载功能
- 支持群发命令功能



### 安装

下载 deb 安装包 `https://www.hostbuf.com/t/988.html`

安装 FinalShell

```bash
sudo dpkg -i finalshell_linux_x64.deb
```



### 使用

#### 群发命令

连接服务器

点击 ”命令“ 窗口，编辑命令后，选择发送到 ”全部会话“，点击 ”发送“ 按钮即可。