# 演示logrotate配置和使用

## 注意

- logrotate修改配置后不需要重启，因为logrotate使用crond定时执行，在执行logrotate任务时会自动加载最新logrotate配置

## 简介

> 日志文件包含了关于系统中发生的事件的有用信息，在排障过程中或者系统性能分析时经常被用到。对于忙碌的服务器，日志文件大小会增长极快，服务器会很快消耗磁盘空间，这成了个问题。除此之外，处理一个单个的庞大日志文件也常常是件十分棘手的事。
>
> logrotate是个十分有用的工具，它可以自动对日志进行截断（或轮循）、压缩以及删除旧的日志文件。例如，你可以设置logrotate，让/var/log/foo日志文件每30天轮循，并删除超过6个月的日志。配置完后，logrotate的运作完全自动化，不必进行任何进一步的人为干预。另外，旧日志也可以通过电子邮件发送，不过该选项超出了本教程的讨论范围。
>
> 
>
> linux系统默认安装logrotate工具，它默认的配置文件在：
>
> - /etc/logrotate.conf
> - /etc/logrotate.d/
>
> logrotate.conf 才主要的配置文件，logrotate.d 是一个目录，该目录里的所有文件都会被主动的读入/etc/logrotate.conf中执行。另外，如果 /etc/logrotate.d/ 里面的文件中没有设定一些细节，则会以/etc/logrotate.conf这个文件的设定来作为默认值。
> logrotate是基于CRON来运行的，其脚本是/etc/cron.daily/logrotate，日志轮转是系统自动完成的。实际运行时，Logrotate会调用配置文件/etc/logrotate.conf。可以在/etc/logrotate.d目录里放置自定义好的配置文件，用来覆盖Logrotate的缺省值。

## 参考资料

> [Linux日志文件总管——logrotate](https://linux.cn/article-4126-1.html)  
[How to make log-rotate change take effect](https://unix.stackexchange.com/questions/116136/how-to-make-log-rotate-change-take-effect)

## 命令和调试

> https://blog.csdn.net/ghs1231561/article/details/129332675

```
# 根据日志切割设置进行操作，并显示详细信息
/usr/sbin/logrotate -v /etc/logrotate.conf

# 根据日志切割设置进行执行，并显示详细信息,但是不进行具体操作，debug模式
/usr/sbin/logrotate -d /etc/logrotate.conf

# 查看各log文件的具体执行情况
tail -f /var/lib/logrotate.status

# 手动强制切割日志，需要加-f参数
/usr/sbin/logrotate -f /etc/logrotate.d/nginx
```

## 切割原理

> 比如以系统日志/var/log/message做切割来简单说明下：
>
> - 第一次执行完rotate(轮转)之后，原本的messages会变成messages.1，而且会制造一个空的messages给系统来储存日志；
> - 第二次执行之后，messages.1会变成messages.2，而messages会变成messages.1，又造成一个空的messages来储存日志！
>
> 如果仅设定保留三个日志（即轮转3次）的话，那么执行第三次时，则 messages.3这个档案就会被删除，并由后面的较新的保存日志所取代！也就是会保存最新的几个日志。

## 详细配置

> **weekly** //默认每一周执行一次rotate轮转工作
> **rotate 4**//保留多少个日志文件(轮转几次).默认保留四个.就是指定日志文件删除之前轮转的次数，0 指没有备份
> **create** //自动创建新的日志文件，新的日志文件具有和原来的文件相同的权限；因为日志被改名,因此要创建一个新的来继续存储之前的日志
> **create mode owner group**轮转时指定创建新文件的属性，如 create 0777 nobody nobody
> **nocreate** 不建立新的日志文件
> **dateext** //这个参数很重要！就是切割后的日志文件以当前日期为格式结尾，如xxx.log-20131216这样,如果注释掉,切割出来是按数字递增,即前面说的 xxx.log-1这种格式
> **dateformat .%s** 配合dateext使用，紧跟在下一行出现，定义文件切割后的文件名，必须配合dateext使用，只支持 %Y %m %d %s 这四个参数
> **compress** //是否通过gzip压缩转储以后的日志文件，如xxx.log-20131216.gz ；如果不需要压缩，注释掉就行
> **minsize 1M** //文件大小超过 1M 后才会切割
> **nocompress** 不做gzip压缩处理
> **copytruncate** 用于还在打开中的日志文件，把当前日志备份并截断；是先拷贝再清空的方式，拷贝和清空之间有一个时间差，可能会丢失部分日志数据。
> **nocopytruncate** 备份日志文件不过不截断
> **missingok** 如果日志丢失，不报错继续滚动下一个日志
> **errorsaddress** 专储时的错误信息发送到指定的Email 地址
> **ifempty** 即使日志文件为空文件也做轮转，这个是logrotate的缺省选项。
> **notifempty** 当日志文件为空时，不进行轮转
> **mail address** 把转储的日志文件发送到指定的E-mail 地址
> **nomail** 转储时不发送日志文件
> **olddir directory**转储后的日志文件放入指定的目录，必须和当前日志文件在同一个文件系统
> **noolddir** 转储后的日志文件和当前日志文件放在同一个目录下
> **shareds** 运行postrotate脚本，作用是在所有日志都轮转后统一执行一次脚本。如果没有配置这个，那么每个日志轮转后都会执行一次脚本
> **prerotate** 在logrotate转储之前需要执行的指令，例如修改文件的属性等动作；必须独立成行
> **postrotate** 在logrotate转储之后需要执行的指令，例如重新启动 ( kill-HUP) 某个服务！必须独立成行

### 配置fail2ban一个月滚动一次日志

```
# 编辑/etc/logrotate.d/fail2ban文件，内容中添加monthly，如下：
/var/log/fail2ban.log {
    monthly
    missingok
    notifempty
    postrotate
      /usr/bin/fail2ban-client flushlogs >/dev/null || true
    endscript
}
```

### 配置openresty一天滚动一次日志

```
# 编辑/etc/logrotate.d/openresty文件，如下：
/usr/local/openresty/nginx/logs/*.log {
    daily
    copytruncate
    missingok
    ifempty
    dateext
    rotate 15
}
```

