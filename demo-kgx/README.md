## kgx开发环境配置

```shell
# 下载并配置dcli命令，NOTE: 如果提示curl command not found，ubuntu系统运行 sudo apt install curl -y，centOS系统运行 yum install curl -y
sudo rm -f /usr/bin/dcli && sudo curl -s https://bucketxyh.oss-cn-hongkong.aliyuncs.com/dcli/dcli-linux-x86_64 --output /usr/bin/dcli && sudo chmod +x /usr/bin/dcli

# 检查dcli命令是否配置成功，结果输出dcli版本说明配置成功
sudo dcli -v

# 启用SSH暴力破解保护、安装并配置xrdp服务、安装tomcat、安装jdk、安装 idea、安装maven、安装docker、设置上海时区
sudo dcli fail2ban install --install y && \
sudo -E dcli xrdp install --install y && \
sudo dcli tomcat install --install y && \
sudo dcli jdk install --install y --version 11 && \
sudo dcli idea install --install y && \
sudo dcli maven install --install y && \
sudo dcli docker install --install y && \
sudo dcli os timezone config --install y

# 在windows系统中连接 ubuntu 远程桌面，NOTE：远程桌面需要调节如下参数，否则会使用很高的带宽
显示 > 选择远程会话的颜色深度：增强色15位
体验 > 选择连接速度来优化性能：调制解调器(56 kbps)

# 克隆 kgx 仓库
git clone https://github.com/wwwwu8899/kgx.git

# 配置中文输入法
打开language support选择输入法方式为fcitx
打开Fcitx Configuration，如果googlepinyin输入法不存在则添加，并且设置切换输入法快捷键为ctrl+alt+shift

# 使用 idea 导入 lhcparent 目录
# 设置 idea maven 指向本地maven
# 添加本地 tomcat 到 idea
```

