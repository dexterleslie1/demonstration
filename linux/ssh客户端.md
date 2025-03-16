# SSH 客户端



## 需求

需求如下：

- 支持 macOS、windows、ubuntu 操作系统
- 支持命令窗口和文件上传和下载同窗口，不需要频繁切换麻烦
- 支持群发命令功能（可选）



结论：暂时使用 FinalShell



## Termius

注意：软件过期后不支持拖拽文件上传和下载功能，所以放弃此软件。



### 总结

总结如下：

- 支持 ubuntu
- 支持文件上传和下载拖拽功能（需要频繁切换，不方便，但是在 Ubuntu 管理多台 CentOS8 使用还是可以的）
- 付费版本才支持群发命令功能



### 安装

下载 Termius.deb `https://termius.com/download/linux`

安装 Termius

```bash
sudo dpkg -i Termius.deb
```

注意：需要配置和登录 Termius Vaults 以保存 SSH 连接信息到 Termius 远程的 Vaults，帐号：gmail，密码：ri..90...34...



## FinalShell

注意：不使用此工具，直觉感觉是有后门的软件（GitHub 中没有公开源代码并多人开发）。



### 总结

总结如下：

- 支持 ubuntu
- 支持文件拖拽上传和右键点击下载功能（不需要频繁切换，方便）
- 支持群发命令功能
- 莫名其妙使用 CPU，感觉不安全



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



## MobaXterm

### 总结

总结如下：

- 使用 wine 在 ubuntu 上安装
- 支持文件拖拽上传和右键点击下载功能（不需要频繁切换，方便）
- 终端窗口打开太多时，终端窗口 TAB 排成一行不方便切换（致命原因导致不使用此软件）



### 安装

下载 msi 安装程序 `https://mobaxterm.mobatek.net/download.html`

安装 wine

```bash
sudo apt install wine
```

重启 ubuntu 系统

使用 wine 运行 msi 安装程序

```bash
LANG=zh_CN.UTF-8 wine msiexec /i MobaXterm_installer_25.0.msi
```

根据 msi 安装程序提示安装软件

在 ubuntu 应用中搜索 MobaXterm 应用并运行