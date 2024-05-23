

## dns

### dns 原理

参考 http://www.ruanyifeng.com/blog/2016/06/dns.html

### cname 原理

参考 https://blog.csdn.net/heluan123132/article/details/73331511





## centOS8 上搭建 dns 服务器

### 参考资料

How to Setup DNS Server (Bind) on CentOS 8 / RHEL8
https://www.linuxtechi.com/setup-bind-server-centos-8-rhel-8/

bind配置解析和DNS记录类型
https://www.cnblogs.com/momenglin/p/8556079.html



### 搭建步骤

安装bind

```sh
yum install bind
```

编辑 /etc/named.conf 修改如下对应的配置值：

```sh
listen-on port 53 { any; };
listen-on-v6 port 53 { any; };
allow-query     { any; };
forwarders { 114.114.114.114; };
dnssec-enable no;
dnssec-validation no;
```

编辑 /etc/named.rfc1912.zones 内容如下：

```
zone "target321.com" IN {
        type master;
        file "target321.com.zone";
        allow-update { none; };
};

zone "1.168.192.in-addr.arpa" IN {
        type master;
        file "1.168.192.arpa";
        allow-update { none; };
};
```

新增文件 /var/named/target321.com.zone 内容如下：

```
$TTL 1D
@	IN SOA	target321.com. mail.target321.com. (
					0	; serial
					1D	; refresh
					1H	; retry
					1W	; expire
					3H )	; minimum
@	IN NS	ns.target321.com.
ns	IN A 	192.168.1.116
www	IN A	192.168.1.111
www	IN A	192.168.1.115
```

新增文件 /var/named/1.168.192.arpa 内容如下：

```
$TTL 1D
@	IN SOA	target321.com. mail.target321.com. (
					0	; serial
					1D	; refresh
					1H	; retry
					1W	; expire
					3H )	; minimum
@	IN NS	ns.target321.com.
ns	IN A	192.168.1.116
111	IN PTR	www.target321.com.
```

重启named服务

```sh
systemctl restart named
```

在另外一台centOS上测试dns服务是否正常，编辑 /etc/resolv.conf 内容如下（其中 192.168.1.116 为dns服务器ip地址）：

```
nameserver 192.168.1.116
```

测试 dns 配置是否成功

```sh
nslookup www.target321.com
```



## centOS7 上搭建 dns 服务器（没有通过实验验证）

### 参考资料

https://www.ibm.com/developerworks/community/blogs/mhhaque/entry/how_to_setup_a_named_dns_service_on_rhel7?lang=en



### 搭建步骤

安装bind named程序

```sh
yum install bind*
```

配置bind named服务编辑 /etc/named.conf

```
listen-on port 53 { any; };
listen-on-v6 port 53 { any; };
allow-query     { any; };
```

编辑 /etc/named.rfc1912.zones 新增如下内容

```
zone "jmeter.internal" IN {
        type master;
        file "jmeter.internal.zone";
        allow-update { none; };
}
```

配置bind named服务zone，复制 /var/named/named.localhost 到 /var/named/jmeter.internal.zone 并编辑其中内容如下： 

```
$TTL 1D
@       IN SOA  @ rname.invalid. (
                                        0       ; serial
                                        1D      ; refresh
                                        1H      ; retry
                                        1W      ; expire
                                        3H )    ; minimum
        NS      @
        A       192.168.1.151
        AAAA    ::1
load    IN A    192.168.1.151
        IN A    192.168.1.152
```

配置防火墙

```sh
firewall-cmd --zone=public --add-port=53/tcp --permanent
firewall-cmd --zone=public --add-port=53/udp --permanent
```

启动bind named服务

```sh
systemctl start named.service
systemctl enable named.service
```

测试bind named服务是否配置成功，编辑 /etc/sysconfig/network-scripts/ifcfg-ens160 新增DNS1=”192.168.1.110”

```
DNS1="192.168.1.110"
DNS2="114.114.114.114"
```

重新启动网络

```sh
systemctl restart network.service
```

执行下面命令测试 dns 配置是否成功

```sh
for i in {1..12}; do ping load.jmeter.internal -c1|grep "PING"; done
```

使用dig命令检查是否配置成功

```sh
dig @192.168.1.110 load.jmeter.internal
```

