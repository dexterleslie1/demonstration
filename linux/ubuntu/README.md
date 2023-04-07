## ubuntu20安装googlepinyin输入法

[ubuntu20.04中文输入法安装步骤](https://www.qetool.com/scripts/view/20653.html)

1. 打开language support后点击install安装所有支持语言

2. 打开language support选择输入法方式为fcitx

3. 安装googlepinyin输入法

   ```shell
   sudo apt install fcitx-googlepinyin
   ```

4. 重启系统后点击右上角输入法configure，如果googlepinyin输入法不存在则添加，并且设置切换输入法快捷键为ctrl+alt+shift



## ubuntu20安装visual code

> 不能使用snap安装visual code，因为snap visual code 是阉割版，不能切换中文输入法。

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

