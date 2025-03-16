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



## ClusterSSH

### 介绍

ClusterSSH是一个高效的SSH协议工具，为IT管理员提供了便捷的远程管理解决方案。以下是对ClusterSSH的详细介绍：

一、概述

SSH（Secure Shell）协议是一项广泛使用的安全网络协议，为数据通信提供了加密通道，确保了远程登录的安全性。然而，在面对多台服务器时，传统的SSH工具往往显得力不从心。这正是ClusterSSH的优势所在，它不仅继承了SSH协议的安全特性，还进一步扩展了其功能，实现了对多台服务器的同时管理。

二、核心特性

1. **批量操作**：ClusterSSH允许用户通过一个集中的控制台，同时向多台远程服务器发送命令。这些命令会被自动同步到所有连接的远程计算机上，极大地提高了工作效率。
2. **简化管理**：通过一个控制台管理多台服务器，极大地简化了工作流程，减少了人为错误的可能性。
3. **增强安全性**：基于SSH协议的安全特性，ClusterSSH确保了远程操作的安全性。
4. **易于部署**：ClusterSSH的安装过程简单，支持多种操作系统，便于快速集成到现有的IT环境中。
5. **灵活性高**：支持自定义脚本和命令，满足不同场景下的需求。

三、安装与配置

1. **安装**：
   - 在Ubuntu、Debian或Linux Mint上，可以使用以下命令安装ClusterSSH：`sudo apt-get install clusterssh`。
   - 在CentOS或RHEL上，首先需要设置EPEL存储库，然后运行安装命令：`sudo yum install clusterssh`。
   - 在Fedora上，只需运行安装命令：`sudo yum install clusterssh`。
2. **配置**：
   - 安装后，需要定义要在其上运行命令的主机群集。可以通过创建系统范围的ClusterSSH配置文件（如`/etc/clusters`）或用户特定的配置文件（如`~/.csshrc`）来实现。
   - 在配置文件中，可以定义多个集群，每个集群包含一组要管理的主机。例如：`my_cluster = host1 host2 host3 host4`。

四、使用

1. **启动ClusterSSH**：使用`cssh`命令并指定集群名称或主机名来启动ClusterSSH。例如：`cssh -l dev my_cluster`。
2. **执行命令**：在ClusterSSH控制台中输入命令，这些命令将被自动同步到所有连接的远程计算机上。
3. **监控命令执行**：通过查看各个主机的终端窗口，可以实时监控命令的执行进度和结果。

五、注意事项

1. **网络连接**：确保本地计算机与远程服务器之间的网络连接正常。
2. **防火墙设置**：如果网络中存在防火墙，请检查防火墙设置以确保ClusterSSH的通信端口未被阻止。
3. **权限验证**：使用正确的用户名和密码进行身份验证。如果使用密钥认证，则需确保密钥文件路径正确无误。

六、应用场景

ClusterSSH非常适合用于快速配置一个集群中的所有运行相同服务和具备相同配置的计算机节点。例如，当需要在数十台服务器上更新软件包时，ClusterSSH可以一键完成任务，而无需逐一登录每台服务器执行相同的步骤。

综上所述，ClusterSSH是一个功能强大且易于使用的工具，它极大地提高了IT管理员的工作效率和管理水平。无论是进行软件更新、系统配置还是故障排查，ClusterSSH都能成为得力助手。



### 使用

>[参考链接1](https://www.linux.com/training-tutorials/managing-multiple-linux-servers-clusterssh/)

安装 ClusterSSH

```bash
sudo apt install clusterssh
```



直接登录服务器，不需要通过 /etc/clusters 或 ~/.csshrc 文件配置

```bash
cssh -l root 192.168.1.2 192.168.1.3
```

- 使用用户 root 分别登录服务器 192.168.1.2 和 192.168.1.3
- `-l root`：这个选项指定了要用于登录远程服务器的用户名，这里是`root`。`-l`是`--login`的简写，表示登录名。
- 如果我想向某个终端发送某些内容，我只需单击所需的 XTerm 即可切换焦点，然后像平常一样在该窗口中键入内容。

