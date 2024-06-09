## ubuntu20安装visual code

> 不能使用snap安装visual code，因为snap visual code 是阉割版，不能切换中文输入法。
>
> 可以使用dcli安装visual code

```shell
# 参考文档
# https://code.visualstudio.com/docs/setup/linux

# 下载visual code deb安装包到本地

# 从deb安装包安装visual code
sudo apt install ./code_xxxxx.deb
```

## ubuntu20配置windows风格界面

```
# 安装tweak和gnome扩展程序
sudo apt install gnome-tweak-tool gnome-shell-extensions

# 安装dash-to-panel windows桌面风格扩展程序
sudo apt install gnome-shell-extension-dash-to-panel

# 重新启动操作系统
# ubuntu程序中搜索并发开Tweaks
# Tweaks设置程序1、关闭general > Animations。2、打开 Extensions > Application menu、Dash to panel、Desktop icons选项。3、打开Window Titlebars > Maximize、Minimize、Placement Right
```



## ubuntu20.4 server(非desktop版本)安装xrdp

### Xfce+xrdp

> https://www.digitalocean.com/community/tutorials/how-to-enable-remote-desktop-protocol-using-xrdp-on-ubuntu-22-04

```
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

> https://linuxize.com/post/how-to-install-xrdp-on-ubuntu-20-04/

```
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

> **Terminal**
>
> - Ctrl + Alt + T 打开终端
>
> - Ctrl + Shfit + T 在当前终端中打开新的tab
>
> - Ctrl + Shift + Q 关闭当前终端，即使有多个tab
> - Ctrl + Shift + W / Ctrl + D关闭当前tab
> - Ctrl + Page Up 切换到前一个tab，NOTE: macbook使用 Ctrl + Fn + Up，参考https://askubuntu.com/questions/105224/ctrl-page-down-ctrl-page-up
> - Ctrl + Page Down 切换到后一个tab，NOTE: macbook使用 Ctrl + Fn + Down，参考https://askubuntu.com/questions/105224/ctrl-page-down-ctrl-page-up
>
> 
>
> **窗口操作**
>
> - Ctrl + Shift + N 新建窗口，例如在vscode中，使用此快捷键会新建一个vscode窗口
>
> - Alt + F4 关闭当前窗口
>
> - Ctrl + Alt + D 显示/隐藏桌面，如果快捷键不起作用，是因为系统默认此快捷键disable状态，通过参考https://askubuntu.com/questions/175369/how-do-i-disable-ctrl-alt-d-in-gnome-shell 启用此快捷键，步骤点击 Settings > Keybord Shortcuts > Navigation > Hide all normal windows 在弹出窗口中输入 Ctrl + Alt + D 绑定快捷键
>
> - Alt + Tab 切换窗口
>
> - command/windows + up 最大化当前窗口
>
> - command/windows + down 还原当前窗口原始大小
>



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

https://www.freecodecamp.org/news/setting-a-static-ip-in-ubuntu-linux-ip-address-tutorial/

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

