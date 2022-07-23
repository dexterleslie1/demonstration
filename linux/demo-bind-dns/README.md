# 演示使用bind在centOS8上搭建dns服务器

## 资料

**[How to Setup DNS Server (Bind) on CentOS 8 / RHEL8](https://www.linuxtechi.com/setup-bind-server-centos-8-rhel-8/)**
**[bind配置解析和DNS记录类型](https://www.cnblogs.com/momenglin/p/8556079.html)**

## 步骤

```
# 安装bind
yum install bind

# 编辑/etc/named.conf
listen-on port 53 { any; };
listen-on-v6 port 53 { any; };
allow-query     { any; };
forwarders { 114.114.114.114; };
dnssec-enable no;
dnssec-validation no;

# 编辑/etc/named.rfc1912.zones
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

# 新增文件
/var/named/target321.com.zone
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

/var/named/1.168.192.arpa
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

# 重启named服务
systemctl restart named

# 在另外一台centOS上测试dns服务是否正常

/etc/resolv.conf
nameserver 192.168.1.116 -- dns服务器ip地址

nslookup www.target321.com

```

