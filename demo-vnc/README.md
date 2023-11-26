## ubuntu20.4 desktop版本配置tigervnc server + ultravnc viewer

> https://www.tecmint.com/install-and-configure-vnc-server-on-ubuntu/

```shell
### 安装并配置tigervnc server
# 安装tigervnc server
sudo apt install tigervnc-standalone-server tigervnc-common tigervnc-xorg-extension

# 配置tigervnc server密码，根据提示输入密码，NOTE: 不需要sudo vncserver
vncserver

# kill 刚刚安装并配置后自动启动的vncserver进程
vncserver -kill :1

# 配置 ~/.vnc/xstartup 脚本，NOTE: 此脚本不存在需要创建
#!/bin/sh
export XKL_XMODMAP_DISABLE=1
xrdb $HOME/.Xresources
# 启动vncconfig进程，在桌面已运行程序中点击小icon能够查看vncconfig GUI
vncconfig -iconic &
dbus-launch --exit-with-session gnome-session &

# 授权 ~/.vnc/xstartup
chmod 700 ~/.vnc/xstartup

# 编辑 ~/.xprofile
# 解决vnc无法切换拼音输入法(智能 pinyin或者google pinyin)问题
export GTK_IM_MODULE=fcitx
export QT_IM_MODULE=fcitx
export XMODIFIERS="@im=fcitx"

# 启动tigervnc server，-geometry 1440x900表示像素，-SecurityTypes=TLSVnc,VncAuth表示加密vnc viewer和tigervnc server之间通讯加密
vncserver :0 -localhost no -geometry 1440x900 -SecurityTypes=TLSVnc,VncAuth

### 使用ultravnc连接tigervnc server

# 下载ultravnc viewer
https://uvnc.com/downloads/ultravnc.html

# 运行ultravnc viewer并使用如下参数连接tigervnc server会使用比较少带宽
server:port 192.168.1.205:5900
勾选 High CPU lower bandwith: XZYW
勾选 zlib compression: 9
取消勾选 jpeg Quality: 0
勾选 256 colors
勾选 Use Zstd instead of zlib
勾选 Use CopyRect encoding
```

