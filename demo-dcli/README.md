# dcli



## 安装

配置`dcli`命令

> 注意：如果提示 curl command not found，ubuntu 系统运行 sudo apt install curl -y，CentOS 系统运行 yum install curl -y

```bash
sudo rm -f /usr/bin/dcli && sudo curl https://fut001.oss-cn-hangzhou.aliyuncs.com/dcli/dcli-linux-x86_64 --output /usr/bin/dcli && sudo chmod +x /usr/bin/dcli
```

检查`dcli`命令是否配置成功，结果输出`dcli`版本说明配置成功

```bash
sudo dcli -v
```