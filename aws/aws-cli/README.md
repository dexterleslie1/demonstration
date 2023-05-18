## 演示安装和配置aws-cli

> 参考
> https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html

### centOS8安装和配置aws-cli

```
下载aws-cli
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"

解压aws-cli
unzip awscliv2.zip

安装aws-cli到 /usr/local/sbin
./install -i /usr/local/aws-cli -b /usr/local/sbin

验证aws-cli是否安装成功
aws --version

配置aws
aws configure
```

