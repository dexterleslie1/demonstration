# `centOS6`升级默认`python2.6`到`python2.7`

>[`CentOS6.x`机器安装`Python2.7.x`](https://www.cnblogs.com/stonehe/p/7944366.html)

注意：`centOS6`从`python2.6`升级到`python2.7`之后，`pip install fire`无法正常工作，所以在`centOS6`升级`python2.6`到`python2.7`意义不大

## 升级步骤

### 查看机器默认的`Python`版本

```bash
python -V
whereis python
```

### 安装gcc

```bash
yum install gcc zlib zlib-devel openssl openssl-devel -y
```

### 编译安装`python2.7`

```bash
# 下载python2.7
wget https://bucketxy.oss-cn-hangzhou.aliyuncs.com/python/Python-2.7.15.tgz -O /tmp/Python-2.7.15.tgz

# 解压python2.7
cd /tmp && tar -zxvf Python-2.7.15.tgz

# configure python2.7
cd /tmp/Python-2.7.15 && ./configure --prefix=/usr/local

# 编译python2.7
cd /tmp/Python-2.7.15 && make

# 安装python2.7
cd /tmp/Python-2.7.15 && make install
```

### 修改`python2.6`为`python2.7`

```bash
# 备份原来python
mv /usr/bin/python /usr/bin/python.bak

# 建立软链接到python2.7
ln -s /usr/local/bin/python2.7 /usr/bin/python

# 查看当前python版本
python -V
```

### `python`升级到`2.7`后，`yum`程序不能正常工作，编辑`vi /usr/bin/yum`修改`#!/usr/bin/python`为`#!/usr/bin/python2.6`

```bash
# 测试yum是否正常工作
yum list
```