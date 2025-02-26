# `centOS`

## `yum install`时报告源错误处理

> 报告错误信息：Error: Failed to download metadata for repo 'appstream': Cannot prepare internal mirrorlist: No URLs in mirrorlist
>
> 参考 [链接](https://kb.fast-line.tw/linux/778/)

解决方案1：

>适用于`centOS8`、`centOS8-stream`，国外推荐使用此配置。

```bash
# 注释 /etc/yum.repos.d/CentOS-* 中所有 mirrorlist 开头的行
sed -i 's/mirrorlist/#mirrorlist/g' /etc/yum.repos.d/CentOS-*

# 把 /etc/yum.repos.d/CentOS-* 中所有 #baseurl= 开头的行替换为 baseurl=http://vault.centos.org
sed -i 's|#baseurl=http://mirror.centos.org|baseurl=http://vault.centos.org|g' /etc/yum.repos.d/CentOS-*
```

解决方案2：

>适用于`centOS8`、`centOS8-stream`、`centOS7`，国内推荐使用此配置。

```bash
# 注释 /etc/yum.repos.d/CentOS-* 中所有 mirrorlist 开头的行
sed -i 's/^mirrorlist/#mirrorlist/g' /etc/yum.repos.d/CentOS-*

# 把 /etc/yum.repos.d/CentOS-* 中所有 #baseurl= 开头的行替换为 baseurl=http://mirrors.aliyun.com
sed -i 's|^#baseurl=http://mirror.centos.org|baseurl=http://mirrors.aliyun.com|g' /etc/yum.repos.d/CentOS-*
```

