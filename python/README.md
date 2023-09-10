## pip使用

> 什么是pip
> https://www.jianshu.com/p/66d85c06238c
> pip是一个以Python计算机程序语言写成的软件包管理系统，他可以安装和管理软件包，另外不少的软件包也可以在“Python软件包索引”中找到。



### 基础使用

```
# 安装和卸载pip
# https://www.jianshu.com/p/66d85c06238c
# 安装pip
sudo easy_install pip
# 或者
# centOS8安装python-pip
# https://phoenixnap.com/kb/how-to-install-pip-on-centos-8
yum install epel-release

# 安装python-pip
yum install python-pip

安装python2-pip
yum -y install python2-pip

安装python3-pip
yum -y install python3-pip

pip --version

# 卸载pip
sudo pip uninstall pip

# 使用pip安装fire包
pip install fire

# 安装指定版本selenium
# https://stackoverflow.com/questions/5226311/installing-specific-package-version-with-pip
pip install selenium==4.0.0

# 查看当前安装的fire包版本
pip show fire

# 升级fire包到最新版本
pip install --upgrade fire

# 列出指定包远程所有版本
pip install click==

# 列出指定包所有版本
https://stackoverflow.com/questions/4888027/python-and-pip-list-all-versions-of-a-package-thats-available
# NOTE: 以下命令在pip3时报错不能使用，使用pip show替代
# pip index versions fire
# https://stackoverflow.com/questions/10214827/find-which-version-of-package-is-installed-with-pip
pip show fire

# 卸载selenium
pip uninstall selenium
```



### pip使用socks5

> https://stackoverflow.com/questions/22915705/how-to-use-pip-with-socks-proxy

```
# 安装socks5依赖，否则会报告缺失socks5依赖错误
sudo pip3 install pysocks

# pip install时候使用socks5代理
sudo pip3 install locust --proxy socks5://xxx:1080
```

