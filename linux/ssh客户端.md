# SSH 客户端



## 需求

需求如下：

- 支持 macOS、windows、ubuntu 操作系统
- 支持命令窗口和文件上传和下载同窗口，不需要频繁切换麻烦
- 支持群发命令功能（可选）



结论：

- Ubuntu 没有批量实例操作需求时使用 Termius
- Windows 使用 MobaXterm



## Termius



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
- Windows Server 2016 Datacenter 上推荐使用此软件



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



### 使用总结

总结如下：

- 支持交互式输入命令
- 控制台输出中文时乱码
- 缺点：通过反向 scp 方式批量上传或者下载文件



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
>
>[参考链接2](https://github.com/duncs/clusterssh)

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



模拟用户登录 bash 并执行指定命令

>提醒：目的是为了加载 /etc/profile.d/xxx.sh 脚本中设置的环境变量。

```bash
$ cssh -l dexterleslie 192.168.1.181 -a 'bash --login -i'

# cssh 登录后输入 env 命令打印环境变量以确认是否加载 /etc/profile.d/xxx.sh 脚本中设置的环境变量
```



### 设置控制台字体大小

>[参考链接](https://unix.stackexchange.com/questions/230106/cluster-ssh-specify-terminal-font)

编辑 ~/.clusterssh/config 修改 terminal_font=6x13 为下面内容：

```properties
terminal_font=10x20
```



### 设置控制台选择即复制文本

>[参考链接](https://github.com/duncs/clusterssh/issues/80)

在 /etc/X11/app-defaults/XTerm 文件末尾添加如下内容：

```properties
*selectToClipboard: true
```



## mussh

提醒：todo 未研究。



## pdsh

提醒：todo 未研究。



## pssh



### 使用总结

总结如下：

- 不支持交互式输入命令



### 介绍

PSSH（Parallel SSH）是一种用于在多个远程主机上并行执行SSH命令的工具。它允许用户通过一个命令同时连接到多个服务器，并在这些服务器上执行相同的操作或命令。这种并行处理能力极大地提高了系统管理员在多服务器环境中进行管理和维护的效率。

PSSH的主要功能包括：

1. **并行执行**：允许用户同时向多个远程主机发送命令，显著减少执行相同任务所需的总时间。
2. **主机列表管理**：通过主机文件（通常是一个包含主机名和（可选）用户名的列表）来管理目标主机。
3. **输出收集**：将每个主机的命令输出收集到本地文件中，便于后续分析和处理。
4. **错误处理**：提供错误处理和重试机制，确保命令在尽可能多的主机上成功执行。
5. **安全性**：虽然PSSH本身不直接增强SSH的安全性，但它允许使用SSH密钥进行无密码登录，从而提高了自动化脚本的安全性。

使用PSSH时，用户需要确保：

- 已在所有目标主机上设置了SSH访问权限。
- （可选）配置了SSH密钥对以实现无密码登录。
- 安装了PSSH工具（在某些Linux发行版上可能需要手动安装）。

PSSH的基本用法通常涉及指定主机文件、要执行的命令以及输出文件的路径。例如，使用`pssh -h hosts.txt -i uptime`命令可以在`hosts.txt`文件中列出的所有主机上执行`uptime`命令，并将输出保存到本地文件中（默认情况下，每个主机的输出将保存在以主机名命名的单独文件中）。

总之，PSSH是一种强大的工具，能够显著提高在多服务器环境中进行管理和维护的效率和便捷性。



### 使用

>[参考链接1](https://www.cyberciti.biz/cloud-computing/how-to-use-pssh-parallel-ssh-program-on-linux-unix/)
>
>[参考链接2](https://www.voidking.com/dev-pssh-manage-linux/)

安装

```bash
sudo apt install pssh
```

查看版本

```bash
parallel-ssh --version
```

提醒：在使用 pssh 连接远程主机前，需要先参考 <a href="/linux/命令行工具列表.html#免密码配置" target="_blank">链接</a> 配置 SSH 免密码登录



使用 -H 直接指定主机 IP 地址（免去指定主机文件列表）

```bash
$ parallel-ssh -l root -H 192.168.1.185 -H 192.168.1.185 -i 'date'
[1] 22:23:14 [SUCCESS] 192.168.1.185
2025年 03月 18日 星期二 22:23:14 CST
[2] 22:23:14 [SUCCESS] 192.168.1.185
2025年 03月 18日 星期二 22:23:14 CST
```

- -l 表示登录使用的用户名
- -H 表示主机字符串，内容格式 `[user@]host[:port]`
- -i 表示每个服务器内部处理信息输出



模拟用户登录 bash 并执行指定命令

>提醒：目的是为了加载 /etc/profile.d/xxx.sh 脚本中设置的环境变量。

```bash
$ parallel-ssh -l dexterleslie -H 192.168.1.181 -i 'bash --login -c "env"' 
# 查看命令打印环境变量以确认是否加载 /etc/profile.d/xxx.sh 脚本中设置的环境变量
```



## Ansible

参考 <a href="/ansible/README.html#ad-hoc-方式执行命令" target="_blank">链接</a> 使用 ad-hoc 方式执行命令。



## Electerm

>`https://github.com/electerm/electerm`

### 总结

todo



### 安装

#### Ubuntu 安装

使用 snap 安装

```bash
sudo snap install electerm --classic
```

#### MacOS 安装

通过 [链接](https://github.com/electerm/electerm) 下载 electerm dmg

双击 dmg 安装 electerm

删除 /Applications/electerm.app com.apple.quarantine xattr

```bash
xattr -r -d com.apple.quarantine /Applications/electerm.app
```

