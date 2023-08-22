## ubuntu20安装googlepinyin输入法

[ubuntu20.04中文输入法安装步骤](https://www.qetool.com/scripts/view/20653.html)

1. 打开language support后点击install安装所有支持语言

2. 打开language support选择输入法方式为fcitx

3. 安装googlepinyin输入法

   ```shell
   sudo apt install fcitx-googlepinyin
   ```

4. 重启系统后点击右上角输入法configure或者在应用搜索中输入 Fcitx Configuration，如果googlepinyin输入法不存在则添加，并且设置切换输入法快捷键为ctrl+alt+shift



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

## ubuntu/debian配置修改源 /etc/apt/sources.list

```
参考
https://www.cnblogs.com/Jimc/p/10214081.html

备份原始源文件，当然需要系统管理员权限操作
sudo cp /etc/apt/sources.list /etc/apt/sources.list.backup

获取ubuntu codename，把codename替换下面的bionic
https://www.ngui.cc/el/1326641.html?action=onClick
lsb_release -a

docker容器修改源sources.list内容如下:
deb http://mirrors.aliyun.com/debian/ buster main non-free contrib
deb-src http://mirrors.aliyun.com/debian/ buster main non-free contrib
deb http://mirrors.aliyun.com/debian-security buster/updates main
deb-src http://mirrors.aliyun.com/debian-security buster/updates main
deb http://mirrors.aliyun.com/debian/ buster-updates main non-free contrib
deb-src http://mirrors.aliyun.com/debian/ buster-updates main non-free contrib
deb http://mirrors.aliyun.com/debian/ buster-backports main non-free contrib
deb-src http://mirrors.aliyun.com/debian/ buster-backports main non-free contrib

修改/etc/apt/sources.list文件并添加国内阿里源，内容如下：
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

更新源
sudo apt-get update
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
> 
