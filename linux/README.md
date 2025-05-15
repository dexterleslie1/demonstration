# Linux

## shell相关

### bash shell设置自动补全

参考
https://www.pix-art.be/post/bash-history-completion

STEP 1: Create .inputrc
vim ~/.inputrc

STEP 2: Paste the following code
"\e[A": history-search-backward
"\e[B": history-search-forward
set show-all-if-ambiguous on
set completion-ignore-case on

STEP 3: Open a new window

STEP4: Testing
10 commands ago I executed this command "vim ~/.inputrc". Now I would like to repeat it but don't want to look it up in my history or type it all out again. I can now type "vi" and press Arrow Up. And it will auto complete with the last known command that started with "vi" in my case "vim ~/.inputrc".

### shell快捷键

ctr+a 快速回到行首

ctrl+e 快速回到行尾

command+t 打开新的tab

command+w 关闭当前tab

ctrl+tab tab之间切换

## 文件和目录

### 文件和目录权限rwx代表的意思

文件r：文件的内容可以被读取，cat、less、more等

文件w：内容可以被写，vi编辑

文件x：可以运行文件，./test

目录r：目录可以被浏览ls、tree，本质可以读取目录项

目录w：目录能删除、移动、创建文件，能创建目录，但可以修改有写权限的文件内容，mv、touch、mkdir，本质可以修改目录项

目录x：可以打开和进入目录，cd

## 用户和群组管理（user、group、passwd、shadow）

```shell
# 用户、密码、群组数据存储文件

存储用户数据文件/etc/passwd解析
https://www.cyberciti.biz/faq/understanding-etcpasswd-file-format/
存储密码数据文件/etc/shadow解析
https://www.cyberciti.biz/faq/understanding-etcshadow-file/
存储群组数据文件/etc/group解析
https://www.cyberciti.biz/faq/understanding-etcgroup-file/

# 查看jenkins用户所属群组列表
groups jenkins

# 查看jenkins组下所有成员
getent group jenkins

# 新增用户jenkins到docker组，-a表示追加用户，-G表示用户组列表
usermod -aG docker jenkins
或者gpasswd -a jenkins docker

# 从testG组删除用户testU
# https://blog.csdn.net/sqlquan/article/details/101001295
gpasswd -d testU testG

# 新增用户组
groupadd tomcat

# 新增用户
useradd -g tomcat tomcat -s /sbin/nologin

# 新增用户并且创建用户home目录
useradd -s /sbin/nologin -m tomcat

# 新增用户使用/bin/bash并创建用户目录
useradd -s /bin/bash -m tomcat

# 修改用户密码
passwd tomcat

# 修改用户加入权限组wheel
usermod -aG wheel tomcat

# 删除用户密码
passwd --delete userxxx

# 查看用户密码状态（LK锁定状态，NP没有设置密码，PS已设置密码）
passwd -S userxxx



# 创建用户指定shell、修改用户shell
# https://www.thegeekdiary.com/centos-rhel-how-to-change-the-login-shell-of-the-user/

# 查看系统支持shell
cat /etc/shells

# 创建用户指定shell
useradd -s /sbin/nologin userxxx

# 使用chsh修改用户shell
chsh -s /sbin/nologin userxxx

# 使用usermod修改用户shell
usermod -s /sbin/nologin userxxx

# NOTE：当修改用户shell为/sbin/nologin后用户不能通过shell登陆系统，su用户会提示“The account is currently not available”



# 锁定和解锁用户
# https://hoststud.com/resources/how-to-lock-and-unlock-user-account-on-centos-linux-server.132/

# 使用usermod -L和usermod -U锁定和解锁用户
usermod -L userxxx或者usermod -U userxxx

# 使用passwd -l和passwd -u锁定和解锁用户
passwd -l userxxx或者passwd -u userxxx

# NOTE：锁定用户后，用户登陆时会提示登陆失败


# 删除用户

# 删除用户user1并且保留用户对应的home目录
userdel user1

# 删除用户user1并且删除用户对应的home目录
userdel -r user1
```



## 配置环境变量



### Ubuntu 和 CentOS8 配置环境变量

在 /etc/profile.d/ 目录设置 xxxx.sh

```bash
[root@localhost ~]# cd /etc/profile.d/
[root@localhost profile.d]# cat go.sh
#!/bin/bash
export PATH=$PATH:/usr/local/go/bin
```



### Ubuntu /etc/profile.d/xxx.sh 为何 SSH 远程连接时不会被加载呢？

在 Ubuntu 系统中，`/etc/profile` 和 `/etc/profile.d/` 目录下的脚本主要用于配置系统级的环境变量和启动时的初始化设置。这些文件通常在用户登录时由 shell 读取和执行。然而，对于 SSH 远程连接，具体的行为可能会因所使用的 shell 类型（如 Bash、Zsh 等）以及 SSH 配置的不同而有所差异。

以下是一些可能导致 `/etc/profile.d/xxx.sh` 在 SSH 连接时不被加载的原因：

1. Shell 类型和配置：
   - 如果你使用的是 Bash，通常 `/etc/profile` 会被读取，并进而读取 `/etc/profile.d/` 目录下的脚本。但如果 SSH 配置为使用非交互式 shell（例如通过 `ssh user@host command`），则可能不会加载这些文件。
   - 对于非 Bash shell（如 Zsh），可能需要额外的配置来确保 `/etc/profile` 或 `/etc/profile.d/` 下的脚本被加载。例如，Zsh 通常使用 `/etc/zsh/zshenv`、`/etc/zsh/zprofile` 等文件。
2. SSH 配置：
   - SSH 服务器或客户端的配置文件（如 `/etc/ssh/sshd_config` 或 `~/.ssh/config`）中的某些设置可能会影响环境变量的加载。例如，`PermitUserEnvironment` 选项控制是否允许用户通过 SSH 会话加载其 `~/.ssh/environment` 文件。
3. 环境变量加载顺序：
   - 在登录过程中，环境变量的加载顺序可能因不同的 shell 和系统配置而异。例如，Bash 会按照 `/etc/profile` -> `/etc/bash.bashrc` -> `~/.bash_profile`、`~/.bash_login` 或 `~/.profile` 的顺序加载配置文件。如果某个文件中有 `exit` 命令，则后续文件将不会被加载。
4. 使用非登录 Shell：
   - 如果 SSH 连接配置为使用非登录 Shell（例如通过 SSH 隧道或某些特定命令），则可能不会执行 `/etc/profile` 和 `/etc/profile.d/` 下的脚本。非登录 Shell 通常只读取 `~/.bashrc` 或其他对应的 shell 配置文件。

为了解决这个问题，你可以：

- 确保你使用的是登录 Shell，并且 SSH 配置允许加载用户的环境。
- 检查你的 shell 配置文件（如 `.bashrc`、`.bash_profile` 等），看看是否有代码阻止了 `/etc/profile` 的加载。
- 如果使用非 Bash shell，检查该 shell 的配置文件和文档，了解如何正确加载环境变量。
- 使用 `ssh -v user@host` 命令增加 SSH 连接的详细输出，查看是否有关于环境变量加载的更多信息。

通过这些步骤，你应该能够诊断和解决 SSH 连接时环境变量不被加载的问题。



## rc.local配置



### centOS6 rc.local配置

> centOS6安装完系统后 rc.local 服务默认已经启动不需要手动配置，centOS6开机自启动配置文件 /etc/rc.local



### centOS7、8 rc.local配置

> centOS7、centOS8 开机自启动配置文件/etc/rc.d/rc.local，centOS7、centOS8 /etc/rc.local符号链接到/etc/rc.d/rc.local文件，所以centOS7、centOS8只需要编辑文件/etc/rc.d/rc.local即可
> 参考dcli配置启用rc.local服务，启用服务后配置 /etc/rc.d/rc.local 文件配置开机自启动服务

```shell
# centos7 rc.local服务配置和查看
# https://www.cnblogs.com/architectforest/p/12467474.html

# 设置/etc/rc.d/rc.local文件拥有可执行权限，这样rc.local服务才能正常启用
chmod +x /etc/rc.d/rc.local

# 编辑/usr/lib/systemd/system/rc-local.service添加如下
[Install]
WantedBy=multi-user.target

# centOS7、centOS8 开机自启动会出现Network Unreachable错误
# https://askubuntu.com/questions/882123/rc-local-only-executing-after-connecting-to-ethernet

# 修改/usr/systemd/system/rc-local.service
After=network.target修改为After=network-online.target

systemctl daemon-reload
systemctl restart rc-local.service

# 查看所有的开启启动项目里面有没有这个rc-local这个服务被加载和现在状态
systemctl list-units --type=service
# 查看rc-local.service服务状态
systemctl status rc-local.service
# 启用rc-local.service服务
systemctl enable rc-local.service

# 开机自启动调试日志，将以下配置添加到/etc/rc.local，centOS7、centOS8将配置添加到/etc/rc.d/rc.local
exec 2> /tmp/rc.local.log  # send stderr from rc.local to a log file
exec 1>&2                      # send stdout to the same log file
set -x                         # tell sh to display commands before execution

# 实例：开机自动启动redis集群服务
sudo -i sh -c "cd /usr/redis1 && /usr/local/bin/redis-server redis.conf"
sudo -i sh -c "cd /usr/redis2 && /usr/local/bin/redis-server redis.conf"
sudo -i sh -c "cd /usr/redis3 && /usr/local/bin/redis-server redis.conf"

# 此命令只能用于tomcat用户shell为/bin/bash
sudo -i su tomcat -c '/data/tomcat-hm-cronb/bin/startup.sh'

# 当tomcat用户shell为/sbin/nologin时使用此命令
sudo -u tomcat sh /usr/tomcat-hm/bin/startup.sh
```



### ubuntu rc.local配置

```shell
# https://helloworld.pixnet.net/blog/post/47874794-%E5%95%9F%E7%94%A8-ubuntu-20.04--etc-rc.local

# 1. 在檔案的最末端加入以下三行，存檔離開
sudo vi /lib/systemd/system/rc-local.service
[Install] 
WantedBy=multi-user.target
Alias=rc-local.service

# 2. 建立 rc.local
sudo vi /etc/rc.local
#!/bin/sh -e

echo `date` >> /tmp/reboot.log

exit 0

# 3. 加入可執行權限
sudo chmod u+x /etc/rc.local

# 4. 設定開機啟動，並手動啟用測試
sudo systemctl enable rc-local
sudo systemctl start rc-local

# 5. 檢視是否已啟用
sudo systemctl status rc-local

# 6. 重開機
sudo reboot

# /lib/systemd/system/与/etc/systemd/system/的区别
https://www.jianshu.com/p/32c7100b1b0c

systemd的使用大幅提高了系统服务的运行效率, 而unit的文件位置一般主要有三个目录：/etc/systemd/system、/run/systemd/system、/lib/systemd/system，这三个目录的配置文件优先级依次从高到低，如果同一选项三个地方都配置了，优先级高的会覆盖优先级低的。
```



## ntp服务

> 开源NTP服务器：cn.pool.ntp.org（当前vsphere使用此服务）
>
> 国内NTP服务器
> https://www.cnblogs.com/jarsing/articles/17503565.html



## 查看 linux 系统版本

### 查看 centOS 版本

参考 https://www.casbay.com/guide/kb/linux-os-centos-version

查看指定文件检查 centOS 版本

```sh
cat /etc/centos-release
cat /etc/system-release
cat /etc/os-release
cat /etc/redhat-release
```

使用 lsb_release 查看 centOS 版本

```sh
yum install redhat-lsb
lsb_release -d
```

使用 hostnamectl 命令查看  centOS 版本

```sh
hostnamectl
```



## 内核参数

### 文件描述符限制

#### 单个进程最大文件描述符限制

在 Linux 系统中，`/etc/security/limits.conf` 配置文件用于设置用户或进程的资源限制。您提到的配置：

```
* soft nofile 65535
* hard nofile 65535
```

具体含义和作用如下：

**1. `nofile` 限制的作用**

- 定义：nofile 用于限制单个进程能够打开的最大文件描述符（File Descriptor）数量。
  - 文件描述符是操作系统管理打开文件、网络连接、套接字等资源的一种方式。
  - 每个进程默认打开的文件描述符数量有限（通常默认值为 `1024`），高并发场景（如 Web 服务器、数据库）容易遇到 `Too many open files` 错误。
- 提高并发能力：
  - 增加 `nofile` 值允许进程打开更多文件/网络连接，提升系统处理高并发请求的能力。
  - 例如：Nginx 处理大量客户端连接时，需要足够的文件描述符支持。

**2. `soft` 和 `hard` 的区别**

- 软限制（`soft`）：

  - 用户或进程可以**临时超过**软限制（需不超过硬限制），系统会发出警告。
  - 例如：设置 `soft nofile 1024`，进程打开 1200 个文件描述符时，系统会警告但允许操作。

- 硬限制（`hard`）：

  - **严格上限**，用户或进程**不能超过**硬限制，否则直接报错。
  - 例如：设置 `hard nofile 2048`，进程尝试打开第 2049 个文件描述符时，系统会拒绝并报错。

- 配置示例：

  ```conf
  * soft nofile 65535   # 所有用户的软限制为 65535
  * hard nofile 65535   # 所有用户的硬限制为 65535
  ```

  - 此配置将所有用户的软限制和硬限制统一设为 `65535`，表示任何用户或进程的打开文件数均不能超过此值。

**3. 实际应用场景**

- 高并发服务器：
  - Web 服务器（如 Nginx、Apache）、数据库（如 MySQL、Redis）需要处理大量并发连接，需提高 `nofile` 限制。
  - 例如：设置 `nofile` 为 `65535` 或更高（根据系统资源调整）。
- 大数据处理：
  - 需要同时打开多个文件（如日志文件分析、批量数据处理）时，需增加 `nofile` 避免资源耗尽。

**4. 注意事项**

- 系统级限制：
  - `/etc/security/limits.conf` 仅控制**用户级**限制，系统全局限制由 `/proc/sys/fs/file-max` 定义。
  - 需确保 `hard nofile` ≤ `file-max`，否则可能导致登录异常。
- 生效方式：
  - 修改配置后，需**重新登录用户**或**重启服务**使配置生效。
  - 临时调整可使用 `ulimit -n 65535`（仅对当前会话有效）。

**总结**

您的配置将所有用户的文件描述符限制统一设为 `65535`，适用于需要高并发文件/网络连接处理的场景。若系统资源充足（如内存、文件句柄数），此配置能显著提升系统性能；若资源紧张，需根据实际需求调整数值，避免过度分配导致系统不稳定。



#### 系统级最大文件描述符限制

`/proc/sys/fs/file-max` 是 Linux 系统中一个关键的内核参数，用于控制**系统级文件句柄数（File Handle）的最大限制**。文件句柄是操作系统管理打开文件、网络连接等资源的关键数据结构，每个打开的文件或网络连接都会消耗一个文件句柄。

**1. `/proc/sys/fs/file-max` 的作用**

- 定义系统级限制：
  - 该参数决定了整个系统（所有进程）可以打开的文件句柄总数。
  - 例如：若 `file-max` 设置为 `1000000`，则系统中所有进程打开的文件、网络连接等总数不能超过此值。
- 避免资源耗尽：
  - 当系统达到文件句柄数限制时，新的文件或网络连接请求会被拒绝，导致应用程序报错（如 `Too many open files`）。
  - 合理设置 `file-max` 可防止因资源耗尽导致的服务中断。

**2. 是否需要设置 `/proc/sys/fs/file-max`？**

- 默认值的适用性：

  - Linux 系统会根据内存大小自动计算默认值（通常为内存大小的 10%）。例如：

    ```bash
    # 计算默认值（内存大小以 KB 为单位，取 10%）
    grep -r MemTotal /proc/meminfo | awk '{printf("%d", $2/10)}'
    ```

  - 对于一般负载的服务器（如小型 Web 应用、开发环境），默认值通常足够。

- 需要调整的场景：

  - **高并发服务**：如 Web 服务器（Nginx、Apache）、数据库（MySQL、Redis）需处理大量并发连接。
  - **大数据处理**：需要同时打开大量文件（如日志分析、批量数据加载）。
  - **资源耗尽报错**：系统日志频繁出现 `Too many open files` 错误。

**3. 如何设置 `/proc/sys/fs/file-max`？**

**临时设置（重启失效）**

```bash
# 查看当前值
cat /proc/sys/fs/file-max
 
# 设置新值（例如 1000000）
echo 1000000 > /proc/sys/fs/file-max
 
# 验证
cat /proc/sys/fs/file-max
```

**永久设置（重启生效）**

1. **修改配置文件**：

   ```bash
   # 编辑 /etc/sysctl.conf
   echo "fs.file-max = 1000000" >> /etc/sysctl.conf
   ```

2. **应用配置**：

   ```bash
   # 加载配置并生效
   sysctl -p
   ```

**4. 注意事项**

- 用户级限制：

  - `/proc/sys/fs/file-max` 是系统级限制，需配合用户级限制（如 `/etc/security/limits.conf` 中的 `nofile`）使用。
  - 用户级限制需 ≤ 系统级限制，否则无效。

- 硬件资源：

  - 文件句柄数增加会消耗更多内存，需根据系统内存和硬件资源合理设置，避免过度分配。

- 监控与调优：

  - 监控已用文件句柄数：

    ```bash
    cat /proc/sys/fs/file-nr
    # 输出格式：已分配数  空闲数  系统最大值
    ```

  - 结合压力测试（如高并发请求）验证配置是否满足需求。

**总结**

- **默认值足够**：普通服务器无需调整 `file-max`。
- **需调整时**：针对高并发、大数据场景，结合监控和测试逐步调优。
- **操作规范**：优先通过 `/etc/sysctl.conf` 永久生效，避免临时修改导致配置丢失。



#### 设置

修改 nofile 配置：

```bash
sudo grep -q "^\* soft nofile" /etc/security/limits.conf && sudo sed -i '/^\* soft nofile/c \* soft nofile 65535' /etc/security/limits.conf || sudo sed -i '/^# End of file/i \* soft nofile 65535' /etc/security/limits.conf
sudo grep -q "^\* hard nofile" /etc/security/limits.conf && sudo sed -i '/^\* hard nofile/c \* hard nofile 65535' /etc/security/limits.conf || sudo sed -i '/^# End of file/i \* hard nofile 65535' /etc/security/limits.conf
```

修改 file-max 配置：

```bash
sudo grep -q "^fs.file-max=" /etc/sysctl.conf && sudo sed -i '/^fs.file-max=/c fs.file-max=10000000' /etc/sysctl.conf || sudo sed -i '$a\fs.file-max=10000000' /etc/sysctl.conf
```

重启系统后，查看配置是否生效

```bash
# 查看 nofile 和 file-max
ulimit -n && cat /proc/sys/fs/file-max
```



## SSH 服务



### 在一段时间内没有活动会自动断开

>[参考链接](https://developer.aliyun.com/article/1448310)

- 使用 SSH 连接到服务器。
- 打开 SSH 配置文件（通常为 /etc/ssh/sshd_config）。
- 寻找 ClientAliveInterval 和 ClientAliveCountMax 这两个选项。
- ClientAliveInterval 指定了服务器向客户端发送保持活动消息的时间间隔，单位是秒。将其设置为一个较大的值（比如 600 表示 10 分钟）。
- ClientAliveCountMax 指定了服务器在未收到客户端响应后断开连接之前发送保持活动消息的次数。将其设置为一个适当的值，以确保连接不会过于频繁地断开（比如 3）。
- 保存并关闭文件。
- 重启操作系统，使更改生效。
