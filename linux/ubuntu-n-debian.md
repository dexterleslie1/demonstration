# Ubuntu/Debian



## 启用`SSH`服务

>[`ubuntu`开启`SSH`服务，并允许`ROOT`权限远程登录](https://blog.csdn.net/jinghongluexia/article/details/90031842)

- 安装`ssh`服务器组件

  ```bash
  sudo apt-get install ssh
  ```

- 修改`/etc/ssh/sshd_config`配置允许`root`远程登录

  ```nginx
  PermitRootLogin yes
  PasswordAuthentication yes
  ```

- 设置`root`密码

  ```bash
  passwd root
  ```

- 启动`ssh`服务

  ```bash
  sudo systemctl start ssh && sudo systemctl enable ssh
  ```




## 配置应用程序开发环境

> 支持`java`应用的开发。

1. 参考 <a href="/dcli/README.html" target="_blank">链接</a> 配置 dcli 程序

3. 启用`ssh`暴力破解保护、安装并配置`xrdp`服务、安装`tomcat`、安装`jdk`、安装`idea`、安装`maven`、安装`docker`、设置上海时区

   ```bash
   sudo dcli fail2ban install --install y && \
   sudo -E dcli xrdp install --install y && \
   sudo dcli tomcat install --install y && \
   sudo dcli jdk install --install y --version 11 && \
   sudo dcli idea install --install y && \
   sudo dcli maven install --install y && \
   sudo dcli docker install --install y && \
   sudo dcli os timezone config --install y
   ```

4. 在`windows`系统中连接`ubuntu`远程桌面，注意：远程桌面需要调节如下参数，否则会使用很高的带宽

   - 显示 > 选择远程会话的颜色深度：增强色15位
   - 体验 > 选择连接速度来优化性能：调制解调器(56 kbps)

5. 配置中文输入法

   打开`language support`选择输入法方式为`fcitx`，再打开`fcitx configuration`，如果`googlepinyin`输入法不存在则添加，并且设置切换输入法快捷键为`ctrl + alt(macOS是option) + shift`

6. 把`googlepinyin`输入法修改为繁体输入

   > 参考 [链接](https://askubuntu.com/questions/1336435/how-do-i-get-traditional-chinese-input-with-pinyin-on-ubuntu-20-04)

   使用`ctrl + shift + f`切换到繁体输入(切换之后永久改变)

7. googlepinyin 输入法输入中文句号、逗号、冒号

   使用 ctrl + . 切换支持输入中文句号、逗号、冒号



## 配置修改源

### `sources.list`配置解析

在Debian和基于Debian的Linux发行版（如Ubuntu）中，`/etc/apt/sources.list` 文件是用于定义APT（Advanced Package Tool）从哪些仓库（repositories）中获取软件包及其源代码的。这个文件通常包含一系列的`deb`和`deb-src`行。

1. **deb**：
   - `deb`行定义了APT从哪个仓库获取二进制软件包（即已经编译好的、可以直接安装的软件包）。
   - 例如：`deb http://archive.ubuntu.com/ubuntu/ focal main restricted`
   - 这行告诉APT从`http://archive.ubuntu.com/ubuntu/`这个URL的`focal`版本的主（main）和限制（restricted）组件中获取二进制软件包。
2. **deb-src**：
   - `deb-src`行定义了APT从哪个仓库获取源代码包（即用于编译软件包的源代码）。
   - 例如：`deb-src http://archive.ubuntu.com/ubuntu/ focal main restricted`
   - 与上面的`deb`行类似，但这行告诉APT从相同的URL和组件中获取源代码包。

何时需要`deb-src`？

- **开发或编译软件**：如果你需要编译或修改某个软件包，你可能需要该软件包的源代码。
- **构建自定义的Debian包**：如果你正在为某个特定的环境或硬件配置自定义的Debian包，源代码将非常有用。
- **信任源代码**：对于某些用户来说，查看和验证软件包的源代码可能是一个重要的安全考虑因素。

注意事项：

- 启用`deb-src`可能会增加你的磁盘使用空间，因为源代码包通常比二进制包大得多。
- 不是所有的用户都需要或想要源代码包，因此默认情况下，许多发行版在`/etc/apt/sources.list`中只包含`deb`行。
- 你可以通过运行`sudo apt-get source <package-name>`来从已启用的`deb-src`仓库中获取特定软件包的源代码。
- 如果你添加了自定义的`deb-src`仓库，但APT无法找到相应的源代码包，你可能会在尝试获取源代码时看到错误消息。确保你的仓库配置正确，并且该仓库确实提供了源代码包。

### `main`, `restricted`, `universe`, `multiverse`, `non-free`, 和 `contrib` 是什么？

`main`, `restricted`, `universe`, `multiverse`, `non-free`, 和 `contrib` 是Debian和基于Debian的Linux发行版（如Ubuntu）中软件仓库的组件分类。这些分类帮助用户和组织更好地理解软件包的来源、许可证以及支持级别。以下是每个组件的简要说明：

1. main：

   - 包含自由软件，即遵循Debian自由软件指南的软件包。
   - 这些软件包由Ubuntu团队官方支持，并经过了严格的测试和审查。
   - 主要包括了大多数流行的和稳定的开源软件。

2. restricted：

   - 包含由于版权或法律问题而不能被自由分发的软件，但它们对于某些用户来说可能是必需的。
   - 这些软件包通常包括一些专有设备的驱动程序，它们不是完全的自由软件，但仍然得到了Ubuntu团队的支持。

3. universe：

   - 包含社区维护的自由软件，这些软件可能不在Ubuntu官方支持范围内。
   - 这些软件包由各种开源项目和独立开发者提供，并通过社区进行测试和支持。
   - universe组件的软件包数量庞大，提供了许多额外的功能和工具。

4. multiverse：

   - 包含非自由软件，这些软件可能包含专有代码或具有限制性的许可证。
   - 使用multiverse中的软件需要用户注意软件的许可协议和使用条款。
   - 这些软件包通常不会得到Ubuntu官方的直接支持。

5. non-free

   （主要在Debian中使用，而不是Ubuntu）：

   - 包含明确不遵循Debian自由软件指南的非自由软件。
   - 这些软件包可能包含专有软件或具有特定使用限制的软件。

6. contrib

   （也主要在Debian中使用）：

   - 包含依赖于non-free组件中软件的自由软件。
   - 这些软件包本身是自由的，但它们依赖于非自由软件才能正常工作。

需要注意的是，`non-free` 和 `contrib` 主要与Debian发行版相关，而在Ubuntu中，相应的分类更倾向于被整合到 `multiverse` 中。Ubuntu为了保持其对自由软件的承诺，在其官方仓库中不包括 `non-free` 分类，但用户可以通过添加第三方源或使用其他方法来安装这些软件包。

总的来说，这些分类有助于用户根据自己的需求和偏好选择合适的软件包，同时也反映了软件包的许可证状态和支持级别。

### `buster`、`buster-backports`、`buster-updates`、`buster-proposed`和`buster-security`是什么呢？

在Debian系统中，`buster`、`buster-backports`、`buster-updates`、`buster-proposed`和`buster-security`代表了不同的软件源组件，它们与Debian 10（代号Buster）版本相关。以下是这些组件的详细解释：

1. buster（Debian 10的稳定版软件仓库）：
   - 这是Debian 10的默认稳定版软件仓库，包含了该版本发布时的所有经过测试并被认为是稳定可靠的软件包。
2. buster-backports（backports存储库）：
   - backports存储库是一个额外的软件源，它包含了针对最新Debian稳定版本的软件包的更新版本。这些软件包可能包含新功能或修复了某些bug，但还没有被正式加入到稳定版中。
   - 用户可以通过这个源获取到比默认稳定版更新的软件包，而无需等待整个系统版本的升级。
3. buster-updates（更新仓库）：
   - 这个仓库包含了Debian 10的稳定版发布后，各个软件包的小版本升级和补丁。这些升级通常用于修复bug或增强功能。
   - 需要注意的是，buster-updates仓库中的软件包可能未经过充分的测试，因此不建议在生产环境中直接使用。
4. buster-proposed（提议的更新仓库）：
   - **buster-proposed** 并不是一个官方定义的仓库名称，但在Debian的开发过程中，有时会使用类似`-proposed-updates`的仓库来测试新的软件包或更新。这些软件包可能还没有经过充分的测试，但已经被提议加入到稳定版中。
   - Debian的某些特定项目或团队可能会使用类似的仓库来测试他们开发的软件包，但这并不是Debian官方提供的标准仓库之一。
5. buster-security（安全更新仓库）：
   - 这个仓库提供了对Debian稳定版本的安全更新。当软件包中发现安全漏洞时，修复这些漏洞的更新将被放置在此仓库中。
   - 对于运行Debian稳定版本的用户来说，启用这个仓库是非常重要的，因为它可以确保系统得到及时的安全更新。

**归纳**：

- `buster`：Debian 10的稳定版软件仓库。
- `buster-backports`：包含针对稳定版本的更新软件包的额外软件源。
- `buster-updates`：包含稳定版发布后软件包的小版本升级和补丁的仓库，但可能包含未经充分测试的软件包。
- `buster-proposed`（非官方标准仓库）：可能用于测试新的软件包或更新的提议仓库。
- `buster-security`：提供安全更新的仓库，确保系统安全。

在配置`/etc/apt/sources.list`文件时，用户可以根据需要启用或禁用这些仓库。对于大多数用户来说，启用`buster`和`buster-security`就足够了。对于需要最新功能或修复的用户，可以考虑启用`buster-backports`。而`buster-updates`通常不建议在生产环境中启用。`buster-proposed`（如果存在）通常是针对特定团队或项目的，普通用户不需要启用。

### `sudo apt install <package-name>`会自动从`deb-src`中下载源代码吗？

默认情况下，当你使用 `apt-get install <package-name>` 安装软件包时，你只会下载并安装编译好的二进制文件，而不是源代码。

`sudo apt-get source <package-name>` 命令用于从 Ubuntu 的软件仓库（或 Debian 的，因为两者在很多方面是相似的）中下载指定软件包的源代码。

如果你想下载源代码，你需要执行 `sudo apt-get source <package-name>`。这将会下载 `.dsc`（源代码描述文件）、`.diff.gz`（补丁文件）和 `.orig.tar.gz`（原始源代码压缩包）等文件。这些文件通常会被放置在 `/usr/src/<package-name>-<version>` 目录下（除非你指定了不同的目录）。

此外，你还需要确保你的 `/etc/apt/sources.list` 文件或 `/etc/apt/sources.list.d/` 目录下的文件中包含了源代码仓库（通常是 `deb-src` 行）。如果没有，`apt-get source` 命令将无法找到源代码。

例如，如果你的 `/etc/apt/sources.list` 文件包含以下行：

```bash
deb http://archive.ubuntu.com/ubuntu/ focal main restricted  
# deb-src http://archive.ubuntu.com/ubuntu/ focal main restricted
```

你需要取消 `# deb-src` 那一行的注释（即删除前面的 `#`），以便 `apt-get source` 能够工作。修改后的文件应该像这样：

```bash
deb http://archive.ubuntu.com/ubuntu/ focal main restricted  
deb-src http://archive.ubuntu.com/ubuntu/ focal main restricted
```

然后，你可以运行 `sudo apt-get update` 来更新你的软件包列表，包括源代码仓库。之后，你就可以使用 `sudo apt-get source <package-name>` 来下载源代码了。

### `/etc/apt/sources.list`配置国内加速源

> [ubuntu 把软件源修改为国内源](https://www.cnblogs.com/Jimc/p/10214081.html)

备份原始源文件，当然需要系统管理员权限操作

```bash
sudo cp /etc/apt/sources.list /etc/apt/sources.list.backup
```

获取`ubuntu codename`，把`codename`替换下面的`bionic`，[参考](https://www.ngui.cc/el/1326641.html?action=onClick)

>注意：如果`lsb_release`命令不存在，则查看原来的`sources.list`文件获取`codename`

```bash
lsb_release -a
```

`debian`或主流开源`docker`容器镜像中主要使用`main`、`non-free`、`contrib`组件分类，`/etc/apt/sources.list`配置如下：

非生产环境配置：

```properties
deb http://mirrors.aliyun.com/debian/ buster main non-free contrib
deb http://mirrors.aliyun.com/debian-security buster-security main
deb http://mirrors.aliyun.com/debian/ buster-updates main non-free contrib
deb http://mirrors.aliyun.com/debian/ buster-backports main non-free contrib
deb-src http://mirrors.aliyun.com/debian/ buster main non-free contrib
deb-src http://mirrors.aliyun.com/debian-security buster-security main
deb-src http://mirrors.aliyun.com/debian/ buster-updates main non-free contrib
deb-src http://mirrors.aliyun.com/debian/ buster-backports main non-free contrib
```

生产环境配置（稳定版软件仓库和提供安全更新的仓库就足够了）：

```properties
deb http://mirrors.aliyun.com/debian/ buster main non-free contrib
deb http://mirrors.aliyun.com/debian-security buster-security main
deb-src http://mirrors.aliyun.com/debian/ buster main non-free contrib
deb-src http://mirrors.aliyun.com/debian-security buster-security main
```

`ubuntu`中国内阿里源`/etc/apt/sources.list`配置如下：

非生产环境配置：

```properties
deb http://mirrors.aliyun.com/ubuntu/ bionic main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ bionic-security main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ bionic-updates main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ bionic-proposed main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ bionic-backports main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ bionic main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ bionic-security main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ bionic-updates main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ bionic-proposed main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ bionic-backports main restricted universe multiverse
```

生产环境配置（稳定版软件仓库和提供安全更新的仓库就足够了）：

```properties
deb http://mirrors.aliyun.com/ubuntu/ bionic main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ bionic-security main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ bionic main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ bionic-security main restricted universe multiverse
```

更新源

```bash
sudo apt-get update
```



## 安装 googlepinyin 输入法

> ubuntu20.04 中文输入法安装步骤参考 [链接](https://www.qetool.com/scripts/view/20653.html)

1. 安装中文语言支持

   ```bash
   sudo apt install -y gnome-user-docs-zh-hans firefox-locale-zh-hans language-pack-zh-hans thunderbird-locale-zh-cn thunderbird-locale-zh-hans language-pack-gnome-zh-hans
   ```

2. 安装google拼音输入法

   ```bash
   sudo apt install -y fcitx-googlepinyin
   ```

3. 打开`language support`选择输入法方式为`fcitx`

4. 重启系统后点击右上角输入法`configure`或者在应用搜索中输入`fcitx configuration`，如果`googlepinyin`输入法不存在则添加，并且设置切换输入法快捷键为`ctrl + alt(macOS为option) + shift`

5. 把`googlepinyin`输入法修改为繁体输入，参考 [链接](https://askubuntu.com/questions/1336435/how-do-i-get-traditional-chinese-input-with-pinyin-on-ubuntu-20-04)

6. 使用`ctrl + shift + f`切换到繁体输入(切换之后永久改变)



## `ubuntu`或者`debian`中安装工具

### 安装`ping`命令

```bash
sudo apt update
sudo apt install iputils-ping -y
```



## 取消`unattended-upgrades`

查看`unattended-upgrades`状态

```bash
sudo systemctl status unattended-upgrades
```

取消`unattended-upgrades`

```bash
sudo systemctl disable unattended-upgrades
sudo systemctl stop unattended-upgrades
```



## ubuntu20 配置 windows 风格界面

```bash
# 安装tweak和gnome扩展程序
sudo apt install gnome-tweak-tool gnome-shell-extensions

# 安装dash-to-panel windows桌面风格扩展程序
sudo apt install gnome-shell-extension-dash-to-panel

# 重新启动操作系统
# ubuntu程序中搜索并发开Tweaks
# Tweaks设置程序1、关闭general > Animations。2、打开 Extensions > Application menu、Dash to panel、Desktop icons选项。3、打开Window Titlebars > Maximize、Minimize、Placement Right
```



## ubuntu20.4 server（非desktop版本）安装 xrdp

### Xfce+xrdp

> `https://www.digitalocean.com/community/tutorials/how-to-enable-remote-desktop-protocol-using-xrdp-on-ubuntu-22-04`

```bash
sudo apt update

# 安装xfce
sudo apt install xfce4 xfce4-goodies -y
# 安装xfce过程中选择gdm3

# 安装xrdp
sudo apt install xrdp -y

# 查看xrdp运行状态
sudo systemctl status xrdp

# 启动xrdp服务
sudo systemctl start xrdp

# 重启xrdp服务
sudo systemctl restart xrdp

# 使用window自带的mstsc测试远程桌面
```



### Gnome+xrdp

> `https://linuxize.com/post/how-to-install-xrdp-on-ubuntu-20-04/`

```bash
sudo apt update

# 安装Gnome
sudo apt install ubuntu-desktop

# 安装xrdp
sudo apt install xrdp

# 查看xrdp运行状态
sudo systemctl status xrdp

# 添加xrdp用户到ssl-cert组
sudo adduser xrdp ssl-cert

# 重启xrdp服务
sudo systemctl restart xrdp

# 使用window自带的mstsc测试远程桌面
```







## ubuntu快捷键



**Terminal**

- Ctrl + Alt + T 打开终端
- Ctrl + Shfit + T 在当前终端中打开新的tab
- Ctrl + Shift + Q 关闭当前终端，即使有多个tab
- Ctrl + Shift + W / Ctrl + D关闭当前tab
- Ctrl + Page Up 切换到前一个tab，注意：macbook使用 Ctrl + Fn + Up，参考https://askubuntu.com/questions/105224/ctrl-page-down-ctrl-page-up
- Ctrl + Page Down 切换到后一个tab，注意：macbook使用 Ctrl + Fn + Down，参考https://askubuntu.com/questions/105224/ctrl-page-down-ctrl-page-up



**窗口操作**

- Ctrl + Shift + N 新建窗口，例如在vscode中，使用此快捷键会新建一个vscode窗口

- Alt + F4 关闭当前窗口

- Ctrl + Alt + D 显示/隐藏桌面，如果快捷键不起作用，是因为系统默认此快捷键disable状态，通过参考https://askubuntu.com/questions/175369/how-do-i-disable-ctrl-alt-d-in-gnome-shell 启用此快捷键，步骤点击 Settings > Keybord Shortcuts > Navigation > Hide all normal windows 在弹出窗口中输入 Ctrl + Alt + D 绑定快捷键

- Alt + Tab 切换窗口

- command/windows + up 最大化当前窗口

- command/windows + down 还原当前窗口原始大小



## apt包管理

```sh
# 更新本地包索引和包列表文件
sudo apt update

# 搜索名为net-tools包
apt-cache search net-tools

# 安装net-tools
apt-get install net-tools

# 搜索本机已安装包
https://linuxize.com/post/how-to-list-installed-packages-on-debian/
apt list --installed | grep net-tools
dpkg-query -l | grep openresty
# 或者
dpkg -S openresty

# apt卸载并重新安装openresty
删除openresty deb包
apt remove --purge openresty

# 删除openresty 相关deb依赖
apt autoremove

# 安装openresty
apt install openresty

# 查看远程仓库指定软件可安装版本
sudo apt-cache madison xrdp

# 从本地deb包安装
sudo apt install ./xxx.deb

# snap列出所有安装程序
snap list | grep code

# snap删除已安装程序
snap remove code

# apt-get update命令解析，apt update 会同步远程仓库软件版本索引到本地
# https://askubuntu.com/questions/222348/what-does-sudo-apt-get-update-do
# 
# It updates the available software list on your computer.
# 
# Your computer has a list (like a catalog) that contains all the available software that # the Ubuntu servers have available. But the available software and versions might change, # so a "update" will hit the server and see what software is available in order to update # its local lists (or catalogs).
# 
# Note that update is diferent from upgrade. Update, as mentioned above, will fetch 
# available software and update the lists while upgrade will install new versions of 
# software installed on your computer (actual software updates).
# 
# To actually upgrade your software (not "update" the lists), you execute the command
# 
# sudo apt-get upgrade
# which is usually executed after an "update".
```





## update-alternatives切换jdk版本

> https://askubuntu.com/questions/613016/removing-oracle-jdk-and-re-configuring-update-alternatives

```
# 列出所有java版本并根据提示切换版本
sudo update-alternatives --config java

# 删除指定版本的java配置
sudo update-alternatives --remove java /usr/local/jdk-11.0.19/bin/java

# 添加指定版本的java配置
sudo update-alternatives --install "/usr/bin/java" "java" "/usr/local/jdk-11.0.19/bin/java" 1500
sudo update-alternatives --install "/usr/bin/javac" "javac" "/usr/local/jdk-11.0.19/bin/javac" 1500
```





## ubuntu使用gsetting实现自动配置

```shell
### 通过参考下面链接找到gsettings需要设置的key
### https://askubuntu.com/questions/971067/how-can-i-script-the-settings-made-by-gnome-tweak-tool

# 先运行以下命令watch配置变化
dconf watch /

# 手动打开设置进行设置，随后dconf watch会有输出

# 经过转换后例如下面gsettings命令
sudo -E -u dexterleslie gsettings set org.gnome.desktop.interface enable-animations false
```





## ubuntu 通过命令行设置静态 ip 地址

> `https://www.freecodecamp.org/news/setting-a-static-ip-in-ubuntu-linux-ip-address-tutorial/`

ubuntu 安装 netplan

```bash
sudo apt install netplan.io
```

编辑 netplan 配置文件 /etc/netplan/00-installer-config.yaml，修改 ip 地址为 192.168.1.205/24

保存和应用 netplan 配置

```sh
sudo netplan try
```



## ubuntu升级firefox浏览器

升级本地包索引和包列表

```sh
sudo apt update
```

升级firefox浏览器

```sh
sudo apt install firefox
```



## 基于`debian`的`docker`容器安装`top`命令

先配置国内`sources.list`，再安装`procps`包含了`top`命令

```bash
apt update && apt install procps
```



## `Debian`系统安装

### 使用`iso`安装`debian10`

注意：在安装过程中需要启用`mirror`（任意选择一个`mirror`）并设置使用`http proxy`（否则安装因为网络问题极慢）。



## 生成 SSH 密钥对

### 方法1

>提醒：推荐使用此方法简洁。
>
>[参考链接](https://cloud.google.com/compute/docs/connect/create-ssh-keys)

在 Linux 和 macOS 工作站上，使用 ssh-keygen 实用程序创建新的 SSH 密钥对。以下示例创建 RSA 密钥对。

打开终端并使用带有 -C 标志的 ssh-keygen 命令创建一个新的 SSH 密钥对。

```bash
ssh-keygen -t rsa -f ~/.ssh/KEY_FILENAME -C USERNAME
```

替换以下内容：

- KEY_FILENAME：您的 SSH 密钥文件的名称。

  例如，文件名为 my-ssh-key 会生成一个名为 my-ssh-key 的私钥文件和一个名为 my-ssh-key.pub 的公钥文件。

- USERNAME：您在虚拟机上的用户名。例如，cloudysanfrancisco 或 cloudysanfrancisco_gmail_com。

  对于 Linux 虚拟机，USERNAME 不能是 root，除非您将虚拟机配置为允许 root 登录。有关更多信息，请参阅以 root 用户身份连接到虚拟机。

  对于使用 Active Directory (AD) 的 Windows 虚拟机，用户名必须以 DOMAIN\ 格式加上 AD 域。例如，ad.example.com AD 中的用户 cloudysanfrancisco 的 USERNAME 为 example\cloudysanfrancisco。

ssh-keygen 将您的私钥文件保存到 ~/.ssh/KEY_FILENAME，将您的公钥文件保存到 ~/.ssh/KEY_FILENAME.pub。

用户 cloudysanfrancisco 的公钥如下所示：

```
ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDAu5kKQCPF... cloudysanfrancisco
```

上传公钥到远程主机

- 参考 <a href="/linux/命令行工具列表.html#免密码配置" target="_blank">链接</a> 使用 ssh-copy-id 复制公钥到远程主机
- 或者如果是 GCE 主机，参考 <a href="/gcp/gce使用.html#配置-ssh-连接" target="_blank">链接</a> 上传公钥到远程主机

通过私钥 SSH 连接到远程主机

```bash
ssh USERNAME@x.x.x.x
```

- 提醒：不需要手动指定私钥文件路径，SSH 会自动在 ~/.ssh 目录中找到连接远程主机所需要的私钥文件



### 方法2

>提醒：推荐使用方法1

参考 <a href="/ssl-tls-https/密钥和证书的管理.html#生成ssh服务的公钥和私钥" target="_blank">链接</a> 为 SSH 创建密钥对。



## Ubuntu20.04 容器镜像中文乱码

在使用 Ubuntu20.04 中的 libreoffice 命令转换 Word 到 Pdf 文件过程中，遇到转换后的 Pdf 中文乱码问题。详情请参考本站 <a href="/word-to-pdf/README.html#docker-安装-libreoffice" target="_blank">链接</a>。最终发现其原因是因为 Ubuntu20.04 容器镜像中缺少支持中文的字体库。参考如下链接：

- `https://developer.baidu.com/article/details/2812003`
- `https://blog.csdn.net/qq_42610612/article/details/146418774?sharetype=blogdetail&shareId=146418774&sharerefer=APP&sharefrom=link`

安装支持中文的字体库

```bash
# 安装支持中文字体库，否则 libreoffice 在某些不被支持的字体在转换后乱码
apt install fonts-noto-cjk fonts-noto-cjk-extra fonts-noto-color-emoji fonts-noto-mono language-pack-zh* -y
```



## Ubuntu 配置应用支持在应用中心搜索并打开

下面演示配置 Cursor IDE 支持在应用中心打开

通过 [链接](https://www.cursor.com/cn/downloads) 下载 Cursor .AppImage (X64) 文件

移动并授权 AppImage 执行权限

```bash
sudo mv ~/Downloads/Cursor-0.48.6-x86_64.AppImage /usr/local/ && sudo chmod +x /usr/local/Cursor-0.48.6-x86_64.AppImage
```

新建文件`/usr/share/applications/cursor.desktop`内容如下：

```properties
[Desktop Entry]
Encoding=UTF-8
Name=Cursor AI Editor
Exec=/usr/local/Cursor-0.48.6-x86_64.AppImage
# Icon=/usr/local/android-studio/bin/studio.svg
Terminal=false
Type=Application
StartupNotify=true
```

在应用中心输入 `cursor` 搜索并打开应用
