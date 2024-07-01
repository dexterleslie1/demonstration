# 配置应用程序开发环境

> 支持`java`应用的开发。

1. 配置`dcli`命令

   > 注意：如果提示curl command not found，ubuntu系统运行 sudo apt install curl -y，centOS系统运行 yum install curl -y

   ```bash
   sudo rm -f /usr/bin/dcli && sudo curl https://fut001.oss-cn-hangzhou.aliyuncs.com/dcli/dcli-linux-x86_64 --output /usr/bin/dcli && sudo chmod +x /usr/bin/dcli
   ```

2. 检查`dcli`命令是否配置成功，结果输出`dcli`版本说明配置成功

   ```bash
   sudo dcli -v
   ```

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