# 演示fail2ban配置和使用

## 安装fail2ban

> 使用dcli程序安装fail2ban

## fail2ban命令使用

### 查看fail2ban版本

```
/usr/bin/fail2ban-server --version
```

## 设置fail2ban日志文件

> [fail2ban设置输出日志文件](https://www.cnblogs.com/mchina/archive/2012/06/01/2530953.html)

```
# 修改/etc/fail2ban/fail2ban.conf添加以下配置
logtarget = /var/log/fail2ban.log
```

## fail2ban日志滚动频率（rollover performed）配置

> [Configuration logrotate for fail2ban](https://en-wiki.ikoula.com/en/Configuration_logrotate_for_fail2ban)

NOTE: 因为fail2ban的 /var/log/fail2ban.log 日志文件使用系统的logrotate滚动日志，所以参考linux的logrotate配置fail2ban的logrotate

## fail2ban正则表达式使用

### 参考资料

> [fail2ban正则语法和pyhton re正则语法一致](https://www.cnblogs.com/12260420zxx/p/13663291.html)  
> [developing-filter-regular-expressions](https://fail2ban.readthedocs.io/en/latest/filters.html#developing-filter-regular-expressions)

### 调试正则表达式

> 注意: 正则表达式不指定<HOST>变量会报错提示ERROR: No failure-id group in，fail2ban-regex使用xxx.conf文件也会报错ERROR: No failure-id group in，使用fail2ban-regex file 'pattern string'方式解决xxx.conf报错问题

#### 使用filter配置文件调试日志文件匹配情况

```
#使用sshd.conf filter配置调试secure日志文件
fail2ban-regex /var/log/secure /etc/fail2ban/filter.d/sshd.conf

fail2ban-regex /usr/local/openresty/nginx/logs/error.log /etc/fail2ban/filter.d/my-limit-req.conf
```

#### 正则表达式基础，使用指定的正则表达式验证1.txt文件内容匹配情况

```
# 1.txt测试内容如下：
[ERROR] 192.168.1.1 
  [ERROR] 192.168.1.2 
        [ERROR] 192.168.1.3 
[ERROR1] 192.168.1.5

# ^\s*: 表示行以0个或者多个空格、tab开始
# \[ERROR\]: 表示行包含[ERROR]
# <HOST>: 表示fail2ban匹配的拦截ip
fail2ban-regex 1.txt '^\s*\[ERROR\] <HOST> $'
```