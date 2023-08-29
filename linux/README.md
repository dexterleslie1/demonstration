# linux相关

## shell相关

### bash shell设置自动补全

参考
https://www.pix-art.be/post/bash-history-completion

STEP 1: Create .inputrc
vim ~/.inputrc

STEP 2: Paste the following code
"\e[A": history-search-backward
"\e[B": history-search-forward
set show-all-if-ambiguous on
set completion-ignore-case on

STEP 3: Open a new window

STEP4: Testing
10 commands ago I executed this command "vim ~/.inputrc". Now I would like to repeat it but don't want to look it up in my history or type it all out again. I can now type "vi" and press Arrow Up. And it will auto complete with the last known command that started with "vi" in my case "vim ~/.inputrc".

### shell快捷键

ctr+a 快速回到行首

ctrl+e 快速回到行尾

command+t 打开新的tab

command+w 关闭当前tab

ctrl+tab tab之间切换



## 操作系统命令



### find命令

```
# 结合find命令删除指定名称的目录，包括子目录内的相应目录
# https://askubuntu.com/questions/43709/how-do-i-remove-all-files-that-match-a-pattern
find . -type d -name node_modules -exec rm -rfv {} \;
```



### ln命令

**硬链接使用**

https://www.cnblogs.com/su-root/p/9949807.html

硬链接应用：

- 文件备份：为了防止重要的文件被误删，文件备份是一种好的办法，但拷贝文件会带来磁盘空间的消耗。硬链接能不占用磁盘空间实现文件备份。
- 文件共享：多人共同维护同一份文件时，可以通过硬链接的方式，在私人目录里创建硬链接，每个人的修改都能同步到源文件，但又避免某个人误删就丢掉了文件的问题。
- 文件分类：不同的文件资源需要分类，比如某个电影即是的分类是外国、悬疑，那我们可以在外国的文件夹和悬疑的文件夹里分别创建硬链接，这样可以避免重复拷贝电影浪费磁盘空间。有人可能说，使用软链接不也可以吗？是的，但不太好。因为一旦源文件移动位置或者重命名，软链接就失效了。

```shell
# 硬链接指向的是节点(inode)
# 创建普通文件
echo "hello" > 1.txt
# 创建硬链接
ln 1.txt 1.txt.hard
# 查看文件硬链接数，源文件和硬链接的硬链接数为2
ls -l
# 查看文件的inode，硬链接对应的inode和源文件一样
ls -li
# mv源文件，文件移动和重命名不会影响硬链接，因为硬链接指向的inode没有发生变化
mv 1.txt 2.txt
# 删除源文件，源文件删除不会导致硬链接断开，只会导致硬链接书减少1
rm 1.txt
# 硬链接只能够链接文件，不能链接目录
# 硬链接只能够链接相同的文件系统，因为不同的文件系统有不同的inode table
```

**符号链接使用**

符号链接应用：

- 快捷方式：对于路径很深的文件，查找起来不太方便。利用软链接在桌面创建快捷方式，可以迅速打开并编辑文件。
- 灵活切换程序版本：对于机器上同时存在多个版本的程序，可以通过更改软链接的指向，从而迅速切换程序版本。这里提到了python版本的切换可以这么做。
- 动态库版本管理：不是很懂，具体可以看这里。

```shell
# 软链接指向的是路径(path)
# 创建普通文件
echo "hello" > 1.txt
# 创建符号链接
ln -s 1.txt 1.txt.soft
# 查看符号链接执行的源文件
ls -l
# 查看文件的inode，符号链接和源文件的inode不一样
ls -li
# 删除和移动源文件，会导致符号链接失效，因为符号链接指向文件路径的
# 符号链接能够链接文件和目录
# 符号链接能够链接不同的文件系统，因为符号链接指向文件的路径
```

**符号链接与硬链接的区别**

https://www.php.cn/linux-491726.html



### tr命令

> 删除字符命令，可以用于输出openssl rsa key以便不需要手动删除rsa key中的换行符号。
> https://stackoverflow.com/questions/800030/remove-carriage-return-in-unix



#### 删除换行符

> 删除文件1.txt中的换行符并把结果输出到当前窗口中

```
# 1.txt内容如下
---
line 1
line 2
---

# 删除换行符号
tr -d '\n' < 1.txt
```



### htop命令

> 使用top -c命令无法查看程序完整的启动命令，使用htop命令能够解决此问题
> https://apple.stackexchange.com/questions/343000/top-how-to-view-the-full-command-path

```
# 安装htop命令
yum install htop -y

# 运行htop命令
htop

# 通过键盘左右方向键滚动屏幕以查看完整的启动命令
```



### scp命令

```
# 复制本地文件到远程
scp README.md root@192.168.1.187:/data/temp/

# 复制本地目录到远程
scp -r demo-bind-dns/ root@192.168.1.187:/data/demo1

# 复制远程文件到本地
scp root@192.168.1.187:/data/temp/README.md .

# 复制远程目录到本地
scp -r root@192.168.1.187:/data/demo1/ .
```



### curl命令

> 下载包含curl工具的zip压缩包，解压后搜索curl.exe，然后复制
> https://curl.haxx.se/dlwiz/?type=bin&os=Win64&flav=-&ver=-&cpu=x86_64

```
# 使用参数-X指定http请求类型
curl -X {POST|GET|PUT|DELETE}

# 获取服务器支持http方法
curl -i -X OPTIONS http://192.168.1.178:8080/

# 隐藏curl progress bar信息-s或者--silent
curl --silent http://xxxxxx

# curl -i 或者 --include参数表示在输出http响应时候打印http请求头参数
# include the HTTP-header in the output. The HTTP-header includes things like server-name, date of the document, HTTP-version and more...

# 使用curl下载文件
curl --output apache-maven-3.6.0-bin.tar.gz http://mirrors.tuna.tsinghua.edu.cn/apache/maven/maven-3/3.6.0/binaries/apache-maven-3.6.0-bin.tar.gz

# 获取接口返回数据，如下例子演示status接口是否返回Ready字符串判断应用状态
curl -s http://localhost/status

# curl使用socks5访问
# https://www.5yun.org/20639.html
curl --proxy socks5h://host:port -H 'Authorization: token xxxxx' https://raw.githubusercontent.com/wwwwu8899/chat/master/APPchat/chat-docker-mongodb/init.js

# curl使用本地dns和远程dns
# https://blog.twofei.com/714/
# 本地dns
curl --proxy socks5://host:port https://www.google.com
# 远程dns
curl --proxy socks5h://host:port https://www.google.com

# curl ouput文件时创建相应目录
# https://stackoverflow.com/questions/40367413/how-do-i-download-a-file-to-a-newly-created-directory-with-curl-on-os-x/40425734
curl -H "Authorization: token {{ var_git_api_token }}" -o /tmp/{{ item }} --create-dirs https://raw.githubusercontent.com/wwwwu8899/chat/master/APPchat/{{ item }}

# curl post 指定参数
curl -X POST http://xxx.xxx.xxx.xxx:port/api/v1/console/did/number/replace -d "uniqueId=ty189289&virtualNumber=%2B32460250887"

# curl在返回时打印输出http header和cookie等信息
curl -X POST http://192.168.1.58:8093/api/v1/voip/fs/directory -i

# curl使用http basic认证
curl -X POST http://localhost:8080/hello --user "user:password" -i

# 请求头参数header
# https://stackoverflow.com/questions/356705/how-to-send-a-header-using-a-http-request-through-a-curl-call
curl -X POST -v 192.168.1.40:82/api/v1/message/getImageSize -H "token:xxxxxxxxx"

# 不压缩并打印下载字节数
# https://stackoverflow.com/questions/9190190/how-to-use-curl-to-compare-the-size-of-the-page-with-deflate-enabled-and-without
curl -X POST --silent --output /dev/null --write-out '%{size_download}' http://192.168.3.14:80/api/v1/downloadFile

# 压缩并打印下载字节数
# https://stackoverflow.com/questions/9190190/how-to-use-curl-to-compare-the-size-of-the-page-with-deflate-enabled-and-without
curl -X POST --compressed --silent --output /dev/null --write-out '%{size_download}' http://192.168.3.14:80/api/v1/downloadFile

# 通过命令行参数-v调试发现--compressed参数实际上是在请求头中添加Accept-Encoding: deflate, gzip，相应头中添加Content-Encoding: gzip

# 发送multipart/form-data、application/x-www-form-urlencoded、application/json请求
# https://stackoverflow.com/questions/52320831/curl-send-json-as-x-www-form-urlencoded

curl -X POST -d "param1=v1" -H "Content-Type: application/x-www-form-urlencoded" http://localhost:8080/api/v1/testPostSubmitParamByFormUrlencoded1

curl -X POST -F "param1=v1" -H "Content-Type: multipart/form-data" http://localhost:8080/api/v1/testPostSubmitParamByMultipartFormData

curl -X POST -d '{"param1":"v1"}' -H "Content-Type: application/json" http://localhost:8080/api/v1/testPostSubmitParamByJSON
```



### yum命令

```
# 显示redis所有版本
yum list --showduplicates redis

# 安装指定版本redis，yum install redis-<version>
yum install redis-3.2.11-1.el6

# 搜索安装
yum search openjdk

# 清除yum缓存，删除/var/cache/dnf目录下yum缓存数据
yum clean all

# 创建yum缓存，下载远程yum源仓库数据到本地目录/var/cache/dnf目录
yum makecache
```





## 文件和目录

### 文件和目录权限rwx代表的意思

文件r：文件的内容可以被读取，cat、less、more等

文件w：内容可以被写，vi编辑

文件x：可以运行文件，./test

目录r：目录可以被浏览ls、tree，本质可以读取目录项

目录w：目录能删除、移动、创建文件，能创建目录，但可以修改有写权限的文件内容，mv、touch、mkdir，本质可以修改目录项

目录x：可以打开和进入目录，cd



## 用户和群组管理（user、group、passwd、shadow）

```
# 用户、密码、群组数据存储文件

存储用户数据文件/etc/passwd解析
https://www.cyberciti.biz/faq/understanding-etcpasswd-file-format/
存储密码数据文件/etc/shadow解析
https://www.cyberciti.biz/faq/understanding-etcshadow-file/
存储群组数据文件/etc/group解析
https://www.cyberciti.biz/faq/understanding-etcgroup-file/

# 查看jenkins用户所属群组列表
groups jenkins

# 查看jenkins组下所有成员
getent group jenkins

# 新增用户jenkins到docker组，-a表示追加用户，-G表示用户组列表
usermod -aG docker jenkins
或者gpasswd -a jenkins docker

# 从testG组删除用户testU
# https://blog.csdn.net/sqlquan/article/details/101001295
gpasswd -d testU testG

# 新增用户组
groupadd tomcat

# 新增用户
useradd -g tomcat tomcat -s /sbin/nologin

# 新增用户并且创建用户home目录
useradd -s /sbin/nologin -m tomcat

# 修改用户密码
passwd tomcat

# 修改用户加入权限组wheel
usermod -aG wheel tomcat

# 删除用户密码
passwd --delete userxxx

# 查看用户密码状态（LK锁定状态，NP没有设置密码，PS已设置密码）
passwd -S userxxx



# 创建用户指定shell、修改用户shell
# https://www.thegeekdiary.com/centos-rhel-how-to-change-the-login-shell-of-the-user/

# 查看系统支持shell
cat /etc/shells

# 创建用户指定shell
useradd -s /sbin/nologin userxxx

# 使用chsh修改用户shell
chsh -s /sbin/nologin userxxx

# 使用usermod修改用户shell
usermod -s /sbin/nologin userxxx

# NOTE：当修改用户shell为/sbin/nologin后用户不能通过shell登陆系统，su用户会提示“The account is currently not available”



# 锁定和解锁用户
# https://hoststud.com/resources/how-to-lock-and-unlock-user-account-on-centos-linux-server.132/

# 使用usermod -L和usermod -U锁定和解锁用户
usermod -L userxxx或者usermod -U userxxx

# 使用passwd -l和passwd -u锁定和解锁用户
passwd -l userxxx或者passwd -u userxxx

# NOTE：锁定用户后，用户登陆时会提示登陆失败


# 删除用户

# 删除用户user1并且保留用户对应的home目录
userdel user1

# 删除用户user1并且删除用户对应的home目录
userdel -r user1
```





## 配置环境变量

### ubuntu和centOS8配置环境变量

> 在/etc/profile.d/目录设置 xxxx.sh

```shell
[root@localhost ~]# cd /etc/profile.d/
[root@localhost profile.d]# cat go.sh
#!/bin/bash
export PATH=$PATH:/usr/local/go/bin
```

## rc.local配置

> 参考dcli配置启用rc.local服务，启用服务后配置 /etc/rc.local 文件配置开机自启动服务





## benchmark



### 概念

> **load average概念**
> https://en.wikipedia.org/wiki/Load_%28computing%29
> 前面三个值分别对应系统当前1分钟、5分钟、15分钟内的平均load。load用于反映当前系统的负载情况，对于16核的系统，如果每个核上cpu利用率为30%，则在不存在uninterruptible进程的情况下，系统load应该维持在4.8左右。对16核系统，如果load维持在16左右，在不存在uninterrptible进程的情况下，意味着系统CPU几乎不存在空闲状态，利用率接近于100%。结合iowait、vmstat和loadavg可以分析出系统当前的整体负载，各部分负载分布情况。
>
> 
>
> 随机读写
> https://en.wikipedia.org/wiki/Random_access
> 顺序读写
> https://en.wikipedia.org/wiki/Sequential_access



### sysbench工具

> 什么是sysbench
> https://www.howtoforge.com/how-to-benchmark-your-system-cpu-file-io-mysql-with-sysbench



#### 基本使用

```
# centOS8安装sysbench
yum install epel-release -y
yum install sysbench -y

# ubuntu安装sysbench
sudo apt-get update
sudo apt install sysbench

# 显示cpu子命令帮助信息
sysbench cpu help

# 显示fileio子命令帮助信息
sysbench fileio help



# 使用sysbench产生cpu负载和分析
sysbench --test=cpu --cpu-max-prime=2000000 --threads=100 run
# 使用mpstat分析cpu负载，下面命令表每隔2秒收集一次cpu使用情况
mpstat -P ALL 2
# 使用vmstat分析cpu负载，vmstat列具体含义使用man vmstat命令查看，下面命令表示每隔2秒收集一次vmstat相关数据
vmstat 2



# 使用sysbench产生io负载和分析
# 准备文件准备随机读写io测试
sysbench --test=fileio --file-total-size=20G prepare
# 开始随机读写io测试
sysbench --test=fileio --file-total-size=20G --file-test-mode=rndrw --max-time=300 --max-requests=0 --threads=10 run
# 清除随机读写io测试文件
sysbench --test=fileio --file-total-size=20G cleanup

# 使用iotop显示io线程使用状况
# 安装iotop
yum install iotop -y
# 显示所有线程io状况
Iotop

# 使用iostat显示io使用cpu iowait状态，一般使用此命令分析系统load average，可以配合mpstat -P ALL 1命令分析每个cpu iowait情况
# 每2秒收集io状况显示为MB
iostat -m 2
```



#### IO性能测试

> 测试指标：随机读、随机写、随机读写、4KB 块大小、多线程

```
# sysbench随机读性能测试
sysbench --test=fileio --file-num=10 --num-threads=16 --file-total-size=10G --file-test-mode=rndrd --max-time=30 --max-requests=0 prepare

sysbench --test=fileio --file-num=10 --num-threads=16 --file-total-size=10G --file-test-mode=rndrd --max-time=30 --max-requests=0 run

sysbench --test=fileio --file-num=10 --num-threads=16 --file-total-size=10G --file-test-mode=rndrd --max-time=30 --max-requests=0 cleanup




# sysbench随机写性能测试
sysbench --test=fileio --file-num=10 --num-threads=16 --file-total-size=10G --file-test-mode=rndwr --max-time=30 --max-requests=0 prepare

sysbench --test=fileio --file-num=10 --num-threads=16 --file-total-size=10G --file-test-mode=rndwr --max-time=30 --max-requests=0 run

sysbench --test=fileio --file-num=10 --num-threads=16 --file-total-size=10G --file-test-mode=rndwr --max-time=30 --max-requests=0 cleanup




# sysbench随机读写性能
sysbench --test=fileio --file-num=10 --num-threads=16 --file-total-size=10G --file-test-mode=rndrw --max-time=30 --max-requests=0 prepare

sysbench --test=fileio --file-num=10 --num-threads=16 --file-total-size=10G --file-test-mode=rndrw --max-time=30 --max-requests=0 run

sysbench --test=fileio --file-num=10 --num-threads=16 --file-total-size=10G --file-test-mode=rndrw --max-time=30 --max-requests=0 cleanup
```



### stress工具

```
# 使用stress产生内存负载和分析
# stress安装
yum install stress -y

# 并发使用2个worker每个worker占用1g virtual memory持续测试3000秒
stress --vm 3 --vm-bytes 1g --timeout 3000s

# 使用mpstat -P ALL 1可以看到每个cpu %syswait都达到100%
mpstat -P ALL 1
# 使用free命令显示可用内存，下面命令以g为单位显示可用内存
free -g
```

