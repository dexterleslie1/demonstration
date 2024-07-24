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

## 文件和目录

### 文件和目录权限rwx代表的意思

文件r：文件的内容可以被读取，cat、less、more等

文件w：内容可以被写，vi编辑

文件x：可以运行文件，./test

目录r：目录可以被浏览ls、tree，本质可以读取目录项

目录w：目录能删除、移动、创建文件，能创建目录，但可以修改有写权限的文件内容，mv、touch、mkdir，本质可以修改目录项

目录x：可以打开和进入目录，cd

## 用户和群组管理（user、group、passwd、shadow）

```shell
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

# 新增用户使用/bin/bash并创建用户目录
useradd -s /bin/bash -m tomcat

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



### centOS6 rc.local配置

> centOS6安装完系统后 rc.local 服务默认已经启动不需要手动配置，centOS6开机自启动配置文件 /etc/rc.local



### centOS7、8 rc.local配置

> centOS7、centOS8 开机自启动配置文件/etc/rc.d/rc.local，centOS7、centOS8 /etc/rc.local符号链接到/etc/rc.d/rc.local文件，所以centOS7、centOS8只需要编辑文件/etc/rc.d/rc.local即可
> 参考dcli配置启用rc.local服务，启用服务后配置 /etc/rc.d/rc.local 文件配置开机自启动服务

```shell
# centos7 rc.local服务配置和查看
# https://www.cnblogs.com/architectforest/p/12467474.html

# 设置/etc/rc.d/rc.local文件拥有可执行权限，这样rc.local服务才能正常启用
chmod +x /etc/rc.d/rc.local

# 编辑/usr/lib/systemd/system/rc-local.service添加如下
[Install]
WantedBy=multi-user.target

# centOS7、centOS8 开机自启动会出现Network Unreachable错误
# https://askubuntu.com/questions/882123/rc-local-only-executing-after-connecting-to-ethernet

# 修改/usr/systemd/system/rc-local.service
After=network.target修改为After=network-online.target

systemctl daemon-reload
systemctl restart rc-local.service

# 查看所有的开启启动项目里面有没有这个rc-local这个服务被加载和现在状态
systemctl list-units --type=service
# 查看rc-local.service服务状态
systemctl status rc-local.service
# 启用rc-local.service服务
systemctl enable rc-local.service

# 开机自启动调试日志，将以下配置添加到/etc/rc.local，centOS7、centOS8将配置添加到/etc/rc.d/rc.local
exec 2> /tmp/rc.local.log  # send stderr from rc.local to a log file
exec 1>&2                      # send stdout to the same log file
set -x                         # tell sh to display commands before execution

# 实例：开机自动启动redis集群服务
sudo -i sh -c "cd /usr/redis1 && /usr/local/bin/redis-server redis.conf"
sudo -i sh -c "cd /usr/redis2 && /usr/local/bin/redis-server redis.conf"
sudo -i sh -c "cd /usr/redis3 && /usr/local/bin/redis-server redis.conf"

# 此命令只能用于tomcat用户shell为/bin/bash
sudo -i su tomcat -c '/data/tomcat-hm-cronb/bin/startup.sh'

# 当tomcat用户shell为/sbin/nologin时使用此命令
sudo -u tomcat sh /usr/tomcat-hm/bin/startup.sh
```



### ubuntu rc.local配置

```shell
# https://helloworld.pixnet.net/blog/post/47874794-%E5%95%9F%E7%94%A8-ubuntu-20.04--etc-rc.local

# 1. 在檔案的最末端加入以下三行，存檔離開
sudo vi /lib/systemd/system/rc-local.service
[Install] 
WantedBy=multi-user.target
Alias=rc-local.service

# 2. 建立 rc.local
sudo vi /etc/rc.local
#!/bin/sh -e

echo `date` >> /tmp/reboot.log

exit 0

# 3. 加入可執行權限
sudo chmod u+x /etc/rc.local

# 4. 設定開機啟動，並手動啟用測試
sudo systemctl enable rc-local
sudo systemctl start rc-local

# 5. 檢視是否已啟用
sudo systemctl status rc-local

# 6. 重開機
sudo reboot

# /lib/systemd/system/与/etc/systemd/system/的区别
https://www.jianshu.com/p/32c7100b1b0c

systemd的使用大幅提高了系统服务的运行效率, 而unit的文件位置一般主要有三个目录：/etc/systemd/system、/run/systemd/system、/lib/systemd/system，这三个目录的配置文件优先级依次从高到低，如果同一选项三个地方都配置了，优先级高的会覆盖优先级低的。
```





## benchmark

### 概念

> **load average概念**
> https://en.wikipedia.org/wiki/Load_%28computing%29
> 前面三个值分别对应系统当前1分钟、5分钟、15分钟内的平均load。load用于反映当前系统的负载情况，对于16核的系统，如果每个核上cpu利用率为30%，则在不存在uninterruptible进程的情况下，系统load应该维持在4.8左右。对16核系统，如果load维持在16左右，在不存在uninterrptible进程的情况下，意味着系统CPU几乎不存在空闲状态，利用率接近于100%。结合iowait、vmstat和loadavg可以分析出系统当前的整体负载，各部分负载分布情况。
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

#### MySQL测试

> https://stackoverflow.com/questions/45584065/cannot-find-oltp-test-on-sysbench

```
# 准备测试数据
sysbench --db-driver=mysql --mysql-user=root --mysql-password=123456 \
  --mysql-host=192.168.1.193 --mysql-port=50000 --mysql-db=demo_db --range_size=100 \
  --table_size=10000 --tables=2 --threads=1 --events=0 --time=60 \
  --rand-type=uniform /usr/share/sysbench/oltp_read_only.lua prepare

# 运行测试
sysbench --db-driver=mysql --mysql-user=root --mysql-password=123456 \
  --mysql-host=192.168.1.193 --mysql-port=50000 --mysql-db=demo_db --range_size=100 \
  --table_size=10000 --tables=2 --threads=1 --events=0 --time=60 \
  --rand-type=uniform /usr/share/sysbench/oltp_read_only.lua run
  
# 清理测试数据 
sysbench --db-driver=mysql --mysql-user=root --mysql-password=123456 \
  --mysql-host=192.168.1.193 --mysql-port=50000 --mysql-db=demo_db --range_size=100 \
  --table_size=10000 --tables=2 --threads=1 --events=0 --time=60 \
  --rand-type=uniform /usr/share/sysbench/oltp_read_only.lua cleanup
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

## ntp服务

> 开源NTP服务器：cn.pool.ntp.org（当前vsphere使用此服务）
>
> 国内NTP服务器
> https://www.cnblogs.com/jarsing/articles/17503565.html



## 查看 linux 系统版本

### 查看 centOS 版本

参考 https://www.casbay.com/guide/kb/linux-os-centos-version

查看指定文件检查 centOS 版本

```sh
cat /etc/centos-release
cat /etc/system-release
cat /etc/os-release
cat /etc/redhat-release
```

使用 lsb_release 查看 centOS 版本

```sh
yum install redhat-lsb
lsb_release -d
```

使用 hostnamectl 命令查看  centOS 版本

```sh
hostnamectl
```
