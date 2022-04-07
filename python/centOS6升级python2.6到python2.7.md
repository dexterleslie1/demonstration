# centOS6升级默认python2.6到python2.7

## 注意

> centOS6从python2.6升级到python2.7之后，pip install fire无法正常工作，所以在centOS6升级python2.6到python2.7意义不大

## 资料

> [CentOS6.x机器安装Python2.7.x](https://www.cnblogs.com/stonehe/p/7944366.html)

## 升级步骤

### 查看机器默认的Python版本

```
python -V
whereis python
```

### 安装gcc

```
yum install gcc zlib zlib-devel openssl openssl-devel -y
```

### 编译安装python2.7

```
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

### 修改python2.6为python2.7

```
# 备份原来python
mv /usr/bin/python /usr/bin/python.bak

# 建立软链接到python2.7
ln -s /usr/local/bin/python2.7 /usr/bin/python

# 查看当前python版本
python -V
```

### python升级到2.7后，yum程序不能正常工作，编辑 vi /usr/bin/yum 修改 #!/usr/bin/python 为 #!/usr/bin/python2.6

```
# 测试yum是否正常工作
yum list
```