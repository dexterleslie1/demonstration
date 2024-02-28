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

## 命令行工具

### find命令

```
# 结合find命令删除指定名称的目录，包括子目录内的相应目录
# https://askubuntu.com/questions/43709/how-do-i-remove-all-files-that-match-a-pattern
find . -type d -name node_modules -exec rm -rfv {} \;
```



### grep 命令

递归搜索指定文件，https://www.cyberciti.biz/faq/unix-linux-grep-include-file-pattern-recursive-example/

```sh
grep -r "17" --include "pom.xml" .
grep -r "17" --include "*.xml" .
```



不搜索指定的目录或者文件

```
grep -r "xxx" --exclude-dir mydir1 --exclude-dir mydir2 --exclude myfile1 --exclude myfile2 --exclude *.min.js
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

# 免host checking
# https://serverfault.com/questions/330503/scp-without-known-hosts-check
scp -o StrictHostKeyChecking=no 1.txt user@host_ip:1.txt

# 自动提供SSH密码
# https://www.cyberciti.biz/faq/linux-unix-applesox-ssh-password-on-command-line/
yum install sshpass -y
sshpass -p 'xxx' scp -o StrictHostKeyChecking=no 1.txt user@host_ip:1.txt
```

### curl命令

> 下载包含curl工具的zip压缩包，解压后搜索curl.exe，然后复制
> https://curl.haxx.se/dlwiz/?type=bin&os=Win64&flav=-&ver=-&cpu=x86_64

```shell
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

# curl post提交从文件读取并提交JSON数据
# https://gist.github.com/ungoldman/11282441
# data.json内容:
{"kind":"Event","apiVersion":"v1","metadata":{"name":"deployment1-5d9c9b97bb-4jlgb.100","namespace":"default","creationTimestamp":null},"involvedObject":{"kind":"Website","namespace":"default","name":"test","uid":"f637afc8-51de-4b09-a3ab-3254672b1a55","apiVersion":"v1","resourceVersion":"8904043"},"reason":"Reason Test","message":"Message Test","source":{"component":"my-source-test"},"firstTimestamp":"2023-12-08T02:40:54Z","lastTimestamp":"2023-12-08T02:40:54Z","count":1,"type":"Warning","eventTime":null,"reportingComponent":"my-source-test","reportingInstance":""}

curl -X POST -H "Content-Type: application/json" -d @./data.json http://127.0.0.1:8001/api/v1/namespaces/default/events
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

### ssh命令

```
# 指定私钥
ssh -i private.key root@xxx

# 免host checking
# https://tecadmin.net/disable-strict-host-key-checking-in-ssh/
ssh -o StrictHostKeyChecking=no user@host_ip

# ssh执行命令
ssh -o StrictHostKeyChecking=no user@host_ip date > /tmp/.temp.txt

# ssh一次执行多条命令
ssh -o StrictHostKeyChecking=no user@host_ip "date > /tmp/.temp.txt; uname -a"

# 自动提供SSH密码
# https://www.cyberciti.biz/faq/linux-unix-applesox-ssh-password-on-command-line/
yum install sshpass -y
sshpass -p 'xxx' ssh -o StrictHostKeyChecking=no root@host_ip
```

#### ssh免密码配置

> https://www.itzgeek.com/how-tos/linux/centos-how-tos/how-to-setup-ssh-passwordless-login-on-centos-8-rhel-8.html

```
# 在用户目录 /root/.ssh/中生成名为id_rsa和id_rsa.pub的公钥和私钥
ssh-keygen -t rsa

# 登录到被连接的服务器把公钥加入到authorized_keys文件（NOTE：公钥格式是ssh-rsa+空格开头，两个==结尾，中间内容不能换行，例如下面：）
cat /root/.ssh/id_rsa.pub >> /root/.ssh/authorized_keys
ssh-rsa AAAAB3NzaC1yc2EAAAfeJQAAAQEAjKTZJf5zQEMv0ZMNYWJpY0SWih1pNF8HDjCQFGKd6JbGcAlFTV/r3PzIf4LdeywyWVw4IdX3AvQCp724nbjyMNZlg/CAOjhOa2x6YRWPcVjLhA09PGzFQuRY4ZwWpZFyFtbB36cfOwe4dk1RjdBuUvCgE/jEqRKrZskNkK0fjuIa77XJg5mvQk3u6IzPPyikgk2heI0+nCxTISfQXIq5n9fnP8/BkE29RH/4044PCJBb/ZkceSC4c59KnYlyPJPYkwER+pi74FvGN5TE6YgueP2aHC5Bni+0cMsAAE7k8DaG/HdpTDsBZzn9fKWsuq+fi71G1ivPOYLXzFytG0Wyhw==

# 修改private key权限为属主只读
chmod 400 id_rsa

# 使用private key: id_rsa测试连接192.168.1.151
ssh - i id_rsa root@192.168.1.151
ssh -p50111 root@192.168.1.151 

# 在主机上使用ssh-agent管理私有秘钥，但是主机重启后秘钥丢失，因为ssh-agent把秘钥存放在内存
ssh-agent bash 启动ssh-agent
ssh-add ~/.ssh/id_rsa 添加私有秘钥
ssh-add -d ~/.ssh/id_rsa 删除私有秘钥
ssh-add -l 查看私有秘钥
ssh root@192.168.1.151
```

#### ssh端口转发（port forwarding）

> https://www.jianshu.com/p/50c4160e62ac
>
> 端口转发windows服务器端使用bitvise服务器
>
> 使用-f可以使ssh进程后台运行

##### 本地端口转发

> 本地端口转发：创建监听本地端口（执行ssh命令的计算机）的ssh进程
>
> - 例子：vm1监听本地端口44567接收用户请求后转发给跳板机vm2端口22，vm2再转发给vm3 端口80，ssh进程监听44567端口在vm1运行
>   [root@vm1 ~] ssh -o ServerAliveInterval=60 -gNT -L 44567:vm3:80 root@vm2
> - 例子：vm1监听本地端口44567接收用户请求后转发给跳板机vm1端口22，vm1再转发给vm3端口80，ssh进程监听44567端口在vm1运行
>   [root@vm1 ~] ssh -o ServerAliveInterval=60 -gNT -L 44567:vm3:80 root@vm1
> - 例子：vm1监听本地端口44567接收用户请求后转发给跳板机vm3端口22，vm3再转发给vm3端口80，ssh进程监听44567端口在vm1运行
>   [root@vm1 ~] ssh -o ServerAliveInterval=60 -gNT -L 44567:localhost:80 root@vm3
> - 例子：本地端口转发，命令在本机上执行并监听端口8888，本机端口8888接收到数据通过ssh隧道转发到192.168.1.54，192.168.1.54转发数据到192.168.1.58:8080
>   ssh -L 8888:192.168.1.58:8080 192.168.1.54
> - 例子：本地端口转发，命令在本机上执行并监听端口8888，本机端口8888接收到数据通过ssh隧道转发到本机，本机转发数据到192.168.1.58:8080
>   ssh -L 8888:192.168.1.58:8080 127.0.0.1

##### 远程端口转发

> 远程端口转发：创建监听远程端口（被ssh的远程计算机）的ssh进程，NOTE: 远程端口转发类型监听客户端链接端口ssh配置需要修改/etc/ssh/sshd_config文件GatewayPorts yes并且使用0.0.0.0:44567:localhost:80方式绑定到所有网卡
>
> - 例子：vm1监听本地端口44567接收用户请求后转发给vm2端口22，vm2再转发		给vm3端口80，ssh进程监听44567端口在vm1运行
>   [root@vm2 ~] ssh -o "ServerAliveInterval 60" -NT -R 44567:vm3:80 root@vm1
> - 例子：使用/usr/keys/k1远程端口连接root@23.43.56.124:44770并在23.43.56.124监听44890数据转发到192.168.1.65:8090
>   ssh -o TCPKeepAlive=yes -o ServerAliveInterval=60 -o ServerAliveCountMax=30 -o ExitOnForwardFailure=no -p44770 -i /usr/keys/k1 -NTf -R 0.0.0.0:44890:192.168.1.65:8090 root@23.43.56.124
> - 例子：服务器23.91.97.126监听端口44791转发到服务器192.168.1.53:5900
>   ssh -o TCPKeepAlive=yes -o ServerAliveInterval=60 -o ServerAliveCountMax=30 -o ExitOnForwardFailure=no -p44790 -i /path/to/ssh/private/key -NTf -R 0.0.0.0:44791:192.168.1.53:5900 user@23.91.97.126



#### 使用 ssh 动态端口转发配置 socks5 代理服务

> https://www.cnblogs.com/zangfans/p/8848279.html

创建 socks5 服务

```sh
ssh -NTf -D 0.0.0.0:10080 -p22 root@localhost
ssh -NTf -D 0.0.0.0:10080 -p22 -i /private/key/path root@localhost
```

配置浏览器使用socks5连接服务器1080端口实现代理上网，或者使用下面命令测试 socks5 服务是否正常

```sh
git clone https://github.com/cmu-db/benchbase.git --config 'http.proxy=socks5://192.168.1.55:1080'
```



### netcat、nc命令

> https://www.cnblogs.com/nmap/p/6148306.html

```
# centOS6和centOS8安装netcat
yum install nc

# 监听本地80端口
nc -l 80

# 使用telnet连接80端口
telnet 192.168.1.23 80

# 使用nc探测端口是否打开，-v表示verbose，-w等待连接超时时间，-z只是探测端口是否打开不发送数据
nc -v -w 10 -z xxx.xxx.xxx.xxx 88

# 重复探测端口是否打开直到成功
while ! nc -w 5 -z 192.168.1.188 22; do echo "retry ..."; sleep 2; done

# Windows平台使用telnet程序测试
# https://www.acronis.com/en-us/articles/telnet/
telnet 192.168.1.34 81

# Linux 平台使用netcat、nc程序测试
# https://www.tecmint.com/check-remote-port-in-linux/
nc -zv 192.168.1.34 81
```

### tar命令

```
# 不解压查看tar归档文件内容
# https://askubuntu.com/questions/392885/how-can-i-view-the-contents-of-tar-gz-file-without-extracting-from-the-command-l
tar -tf filename.tar.gz

# -z参数使用gzip压缩文件
tar -cvzf tomcat-lh-management.tar tomcat-lh-management 压缩文件夹
tar -xvzf tomcat-lh-management.tar 解压文件夹

# 压缩指定文件main.tf和public.key到文件demo.tar.gz
tar -cvzf demo.tar.gz main.tf public.key


# 压缩文件夹但不打包绝对路径,-C表示先切换到/usr/local目录
# https://stackoverflow.com/questions/18681595/tar-a-directory-but-dont-store-full-absolute-paths-in-the-archive
tar -cvzf tomcat-lh-management.tar -C /usr/local tomcat-lh-management

# 压缩文件夹不包含父目录，解压之后直接会把所有文件解压到当前目录
# https://unix.stackexchange.com/questions/168357/creating-a-tar-archive-without-including-parent-directory
cd /usr/temp1
tar -cvzf 1.tar *

# 保留属主和权限信息
tar --same-owner -cvzpf tomcat-hm-denmark.tar tomcat-hm-denmark/

# tar解压到指定目录，把tar.gz解压到/tmp文件夹
tar -xzvf archive.tar.gz -C /tmp
```

### zip和unzip命令

```

# zip压缩和unzip解压缩命令详解
https://blog.csdn.net/CareChere/article/details/50844846

# 把chat目录内容压缩到chat.zip
zip -r chat.zip chat

# 把当前目录所有内容压缩到chat.zip，解压时没有子目录
zip -r chat.zip .

# unzip解压
unzip mydata.zip -d mydatabak

# 解压文件夹包含中文目录或者文件名称
# https://www.jb51.net/article/123666.htm
unzip -O CP936 chat.zip -d chat
```









### jq命令

> jq 是一个强大的命令行工具，用于处理 JSON 格式的数据。它可以帮助你查询、过滤、修改和处理 JSON 数据，使得在命令行环境下处理 JSON 变得非常方便。
>
> https://baijiahao.baidu.com/s?id=1773731505742878774&wfr=spider&for=pc

```shell
# 安装jq命令
yum install -y epel-release
yum install -y jq

### 查询和过滤数据
# 演示使用的JSON
[
   {
      "name":"Alice",
      "age":25,
      "city":"New York"
   },
   {
      "name":"Bob",
      "age":30,
      "city":"Los Angeles"
   },
   {
      "name":"Charlie",
      "age":22,
      "city":"Chicago"
   }
]

# 不选择字段，输出所有数组对象
cat 1.json | jq ".[]"

# 选择字段，查询并选择所有人的姓名
cat 1.json | jq ".[].name"

# 过滤，选择年龄大于 25 岁的人的姓名和城市
cat 1.json | jq ".[] | select(.age > 25) | .name, .city"

# 遍历数组，遍历并输出所有人的年龄
cat 1.json | jq ".[] | .age"

# 组合操作，选择年龄在 25 到 30 岁之间的人的姓名和城市，并按照姓名排序
cat 1.json | jq ".[] | select(.age >= 25 and .age <= 30) | .name, .city" | sort





### 修改数据

# 演示使用的JSON
{
   "name":"Alice",
   "age":25,
   "city":"New York"
}

# 修改字段值，修改年龄字段的值为 26
cat 2.json | jq ".age=26"

# 创建新字段，添加一个新的字段 country 并设置其值为 "USA"
cat 2.json | jq ".country=\"USA\""

# 组合操作，修改年龄字段的值为 26，并添加一个新的字段 country
cat 2.json | jq ".age=26 | .country=\"USA\""
```



### reset命令

> 因为程序意外终止导致终端处于不正常状态，例如没有echo、乱码等。使用reset能够重置终端到初始默认状态。
> https://www.geeksforgeeks.org/reset-command-in-linux-with-examples/

```shell
# 重置终端
reset
```



### openssl命令

```shell
# 获取指定文件的二进制base64编码，tr -d '\n'表示删除换行符号
openssl base64 -in xxx.p12 | tr -d '\n'
```



### timeout 命令

> https://linuxize.com/post/timeout-command-in-linux/

timeout 命令超时时候会返回 124 代码，否则返回命令的退出状态值，-s 15 表示命令超时时候发出第 15 号信号(SIGTERM，可以通过 kill -l 查看相关值)，2 表示命令的超时时间为 2 秒， sleep 10 表示休眠 10 秒。

```sh
timeout -s 15 2 sleep 10
```



### tree 命令

以 human readable 方式显示目录占用存储空间情况

```sh
tree -h
```

指定递归深度为 2

```sh
# NOTE: 因为此命令不会统计子目录的存储空间使用，所以使用下面的命令代替此命令
tree -L 2

du -d 2 -h .
```



### vi/vim 命令

#### vi/vim 中快速替换

参考

> https://linuxize.com/post/vim-find-replace/

在命令行模式中输入 :%s/foo/bar/g 表示替换所有 foo 为 bar，% 表示整个文件，/g 表示替换当前行所有 foo。 



### dig 命令

参考 https://phoenixnap.com/kb/linux-dig-command-examples

通过查看版本检查是否已经安装 dig 命令

```sh
dig -v
```

debian/ubuntu 安装 dig 命令

```sh
sudo apt-get install dnsutils
```

centOS/redhat 安装 dig 命令

```sh
sudo yum install bind-utils
```

查询指定域名的 dns 信息

```sh
dig baidu.com
```

指定 dns 服务器 114.114.114.114

```sh
dig @114.114.114.114 baidu.com
```

显示所有 dns 记录

```sh
dig baidu.com ANY
```

只显示 dns 对应的 ip 地址

```sh
dig baidu.com +short
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

