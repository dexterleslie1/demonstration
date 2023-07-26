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



## 文件和目录

### 文件和目录权限rwx代表的意思

文件r：文件的内容可以被读取，cat、less、more等

文件w：内容可以被写，vi编辑

文件x：可以运行文件，./test

目录r：目录可以被浏览ls、tree，本质可以读取目录项

目录w：目录能删除、移动、创建文件，能创建目录，但可以修改有写权限的文件内容，mv、touch、mkdir，本质可以修改目录项

目录x：可以打开和进入目录，cd

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
