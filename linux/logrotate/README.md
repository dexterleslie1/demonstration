# 演示logrotate配置和使用

## 注意

- logrotate修改配置后不需要重启，因为logrotate使用crond定值执行，在执行logrotate任务时会自动加载最新logrotate配置

## 简介

> 日志文件包含了关于系统中发生的事件的有用信息，在排障过程中或者系统性能分析时经常被用到。对于忙碌的服务器，日志文件大小会增长极快，服务器会很快消耗磁盘空间，这成了个问题。除此之外，处理一个单个的庞大日志文件也常常是件十分棘手的事。
>
>logrotate是个十分有用的工具，它可以自动对日志进行截断（或轮循）、压缩以及删除旧的日志文件。例如，你可以设置logrotate，让/var/log/foo日志文件每30天轮循，并删除超过6个月的日志。配置完后，logrotate的运作完全自动化，不必进行任何进一步的人为干预。另外，旧日志也可以通过电子邮件发送，不过该选项超出了本教程的讨论范围。

## 参考资料

> [Linux日志文件总管——logrotate](https://linux.cn/article-4126-1.html)  
[How to make log-rotate change take effect](https://unix.stackexchange.com/questions/116136/how-to-make-log-rotate-change-take-effect)

## 详细配置

#### 配置fail2ban一个月滚动一次日志

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