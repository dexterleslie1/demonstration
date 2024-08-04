

# `dns`

## 概念和原理

> [`dns`原理](http://www.ruanyifeng.com/blog/2016/06/dns.html)
>
> [`cname`原理](https://blog.csdn.net/heluan123132/article/details/73331511)



## `centOS8`上搭建`dns`服务器

>[How to Setup DNS Server (Bind) on CentOS 8 / RHEL8](https://www.linuxtechi.com/setup-bind-server-centos-8-rhel-8/)
>
>[bind配置解析和DNS记录类型](https://www.cnblogs.com/momenglin/p/8556079.html)

### 搭建步骤

下面是演示搭建自定义`dns`服务器正向或者反向解析`www.target321.com`域名到多个`ip`地址的过程。

- 安装`bind`

  ```bash
  yum install bind -y
  ```

- 编辑`/etc/named.conf`修改如下对应的配置值：

  ```nginx
  listen-on port 53 { any; };
  listen-on-v6 port 53 { any; };
  allow-query     { any; };
  forwarders { 114.114.114.114; };
  dnssec-enable no;
  dnssec-validation no;
  ```

  这段配置通常用于BIND（Berkeley Internet Name Domain）DNS服务器的命名区域配置文件中，特别是`named.conf`或其包含的任何区域配置文件。这里，它定义了DNS服务器的一些关键行为参数。下面是对这些配置的详细解释：

  1. `listen-on port 53 { any; };`
     - 这行配置指定DNS服务器监听所有可用的IPv4地址（通过`any`关键字）上的53端口。53端口是DNS服务的默认端口。这意味着DNS服务器将接受来自任何IPv4地址的DNS查询请求。
  2. `listen-on-v6 port 53 { any; };`
     - 类似于上面的配置，但这行是针对IPv6的。它指示DNS服务器监听所有可用的IPv6地址上的53端口，接受来自任何IPv6地址的DNS查询请求。
  3. `allow-query { any; };`
     - 这行配置指定哪些客户端或网络可以查询DNS服务器。通过`any`关键字，它允许来自任何IP地址的查询。这意呀着DNS服务器不会对查询请求进行源地址过滤。
  4. `forwarders { 114.114.114.114; };`
     - 这行配置设置了DNS服务器的前向解析器（forwarders）。当DNS服务器无法直接解析某个查询时，它会将这些查询转发给指定的前向解析器。在这个例子中，只有一个前向解析器被指定，即IP地址为114.114.114.114的服务器。这是一个公共DNS服务，通常用于提供快速的DNS解析服务。
  5. `dnssec-enable no;`
     - 这行配置禁用了DNSSEC（DNS Security Extensions）的支持。DNSSEC是一种用于确保DNS查询结果完整性和真实性的安全扩展。将其设置为`no`意味着DNS服务器不会执行DNSSEC验证，也不会在响应中包含DNSSEC相关的签名或验证信息。
  6. `dnssec-validation no;`
     - 这行配置进一步明确了DNS服务器不会进行DNSSEC验证。与`dnssec-enable no;`相似，但更具体地指向了验证过程。在某些配置中，即使启用了DNSSEC支持，也可能选择不执行验证。

  总的来说，这段配置设置了一个DNS服务器，它监听所有IPv4和IPv6地址上的53端口，接受来自任何地址的查询请求，并将无法直接解析的查询转发给指定的前向解析器。同时，它禁用了DNSSEC的支持和验证，这可能会降低DNS查询的安全性，但可能会提高查询速度和兼容性。

- 编辑`/etc/named.rfc1912.zones`内容如下：

  ```nginx
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

  这两段配置是BIND DNS服务器中的区域（zone）定义，用于指定如何处理和解析特定的域名或IP地址反向解析。下面是对这两个区域配置的详细解释：

  第一个区域配置：`target321.com`

  ```nginx
  zone "target321.com" IN {  
      type master;  
      file "target321.com.zone";  
      allow-update { none; };  
  };
  ```

  - **zone "target321.com" IN { ... }**: 这行定义了一个名为`target321.com`的区域，`IN`表示这个区域使用Internet类（Class IN）的DNS记录。
  - **type master;**: 指定这个区域是一个主区域（master zone），意味着这个DNS服务器是这个区域的权威服务器，并且存储了该区域的所有DNS记录。
  - **file "target321.com.zone";**: 指定存储这个区域DNS记录的文件名为`target321.com.zone`。这个文件应该位于BIND服务器的配置目录中，并包含了该区域的所有资源记录（如A记录、CNAME记录等）。
  - **allow-update { none; };**: 指定不允许任何动态更新到这个区域。这通常用于确保DNS记录的安全性和一致性，因为手动管理记录比允许不受限制的自动更新更安全。

  第二个区域配置：`1.168.192.in-addr.arpa`

  ```nginx
  zone "1.168.192.in-addr.arpa" IN {  
      type master;  
      file "1.168.192.arpa";  
      allow-update { none; };  
  };
  ```

  - **zone "1.168.192.in-addr.arpa" IN { ... }**: 这行定义了一个用于IP地址反向解析的区域。`in-addr.arpa`是反向DNS查找的顶级域（TLD），而`1.168.192`是IP地址`192.168.1.x`的反向表示（IP地址段中的每个部分被反向，并用点分隔）。
  - **type master;**: 同样，这个区域被配置为主区域，意味着这个DNS服务器是这个反向区域的权威服务器。
  - **file "1.168.192.arpa";**: 指定存储这个反向区域DNS记录的文件名为`1.168.192.arpa`。这个文件包含了将IP地址映射到主机名的PTR记录。
  - **allow-update { none; };**: 禁止对这个反向区域进行动态更新，以维护DNS记录的安全性和准确性。

  总的来说，这两个配置片段定义了两个DNS区域：一个用于正向域名解析（`target321.com`），另一个用于IP地址的反向解析（`192.168.1.x`网段的反向表示）。这两个区域都被配置为主区域，并且都不允许动态更新。

- 新增文件`/var/named/target321.com.zone`内容如下（注意：其中`192.168.1.116`为`dns`服务器本机`ip`地址）：

  ```nginx
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

  这段DNS区域文件（zone file）配置是用于BIND DNS服务器或兼容的DNS软件的一部分，它定义了`target321.com`域名的DNS记录。下面是对这段配置的详细解释：

  ```nginx
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

  1. **`$TTL 1D`**: 这行设置了默认的时间到存活（TTL）值为1天。TTL是DNS记录缓存的时间长度，单位是秒。在这里，它被设置为86400秒（即1天），意味着DNS解析器可以将此区域的记录缓存1天时间。
  2. **`@ IN SOA ...`**: 这行定义了一个起始授权记录（SOA，Start of Authority）。SOA记录是DNS区域中的关键记录之一，它包含了关于区域的重要管理信息。
     - `@` 是一个简写，代表区域的根（即`target321.com`）。
     - `SOA` 指示这是一个SOA记录。
     - `target321.com.` 是区域名称。
     - `mail.target321.com.` 是负责管理此区域的管理员邮箱地址（注意：在DNS中，`.`被附加在域名的末尾以指示它是完整的域名）。
     - 接下来的部分定义了序列号、刷新间隔、重试间隔、过期时间和最小TTL值。
       - `0` 是序列号，通常用于版本控制，每次修改区域文件时应该增加。
       - `1D` 是刷新间隔，指示从属服务器应该多久检查一次主服务器以获取新的或更新的记录。
       - `1H` 是重试间隔，如果刷新失败，从属服务器应该多久后再次尝试。
       - `1W` 是过期时间，如果从属服务器在这么长的时间内没有成功从主服务器刷新记录，则认为记录已过期。
       - `3H` 是负载平衡的最小TTL值，建议缓存解析器至少保持记录这么长的时间。
  3. **`@ IN NS ns.target321.com.`**: 这行定义了一个名称服务器（NS）记录，它指定了负责解析`target321.com`域名的DNS服务器。`@`代表区域根，`NS`表明这是一个NS记录，而`ns.target321.com.`是名称服务器的域名。
  4. **`ns IN A 192.168.1.116`**: 这行定义了一个地址（A）记录，将`ns.target321.com.`的名称解析为IP地址`192.168.1.116`。这是名称服务器本身的IP地址。
  5. **`www IN A 192.168.1.111` 和 `www IN A 192.168.1.115`**: 这两行定义了两个地址（A）记录，都对应于`www.target321.com`。这意味着当有人尝试访问`www.target321.com`时，DNS服务器会返回这两个IP地址之一（具体返回哪个取决于DNS解析器的实现和配置，可能涉及负载均衡和/或轮询）。然而，在大多数情况下，一个主机名应该只对应一个IP地址，除非有特殊的负载均衡或高可用性需求。如果你的意图是设置负载均衡，请确保你的网络配置能够正确处理这种情况。如果`www.target321.com`应该只解析到一个IP地址，你应该删除其中一个A记录。

- 新增文件`/var/named/1.168.192.arpa`内容如下（注意：其中`192.168.1.116`为`dns`服务器本机`ip`地址）：

  ```nginx
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

  在您提供的DNS区域文件配置中，我们有一个SOA记录、一个NS记录、一个A记录和一个PTR记录。但是，PTR记录通常用于反向DNS查找，并且其名称（在这里是`111`）并不遵循反向DNS查找的常规命名约定。下面是对这些记录的详细解释，并对PTR记录提出可能的修改。

  ```nginx
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

  1. **SOA记录**：这是区域的起始授权记录，包含了关于区域的关键管理信息，如区域名称、管理员邮箱、序列号、刷新间隔、重试间隔、过期时间和最小TTL值。

  2. **NS记录**：这指定了负责解析`target321.com`域名的DNS服务器（`ns.target321.com.`），并且该服务器的IP地址通过A记录（`ns IN A 192.168.1.116`）给出。

  3. **A记录**：这将`ns.target321.com.`的名称解析为IP地址`192.168.1.116`。

  4. **PTR记录**：通常，PTR记录用于将IP地址的某个部分映射回一个主机名，作为反向DNS查找的一部分。然而，这里的PTR记录`111 IN PTR www.target321.com.`并不符合常规的反向DNS命名约定。在反向DNS中，PTR记录应该位于一个特定的反向区域（如`1.168.192.in-addr.arpa`），并且其名称应该反映IP地址的逆序。

     如果您的意图是为`192.168.1.111`这个IP地址设置一个PTR记录，那么您应该将该记录放在正确的反向区域文件中，并且记录的名称应该是该IP地址逆序后的表示（即`111.1.168.192.in-addr.arpa.`），如下所示：

     ```dns
     ; 假设这是在 1.168.192.in-addr.arpa 区域的文件中  
     111.1 IN PTR www.target321.com.
     ```

     但是，请注意，这里的`111.1`实际上是一个简化的表示，因为完整的名称应该包含IP地址的所有部分（在这种情况下是`111.1.168.192`），但是反向DNS区域的结构要求我们将这些部分逆序，并在每个部分之间加上点，然后附加到`in-addr.arpa`顶级域上。然而，由于DNS区域文件通常只包含它们各自区域的相关部分，因此`111.1`可能是一个编辑错误或简化表示，实际上应该是整个IP地址的逆序。

     另外，请注意，即使您设置了PTR记录，它也不会自动与A记录关联。PTR记录是独立的，用于反向DNS查找，而A记录用于正向DNS查找。它们共同工作，但各自服务于不同的目的。

- 重启`named`服务

  ```bash
  systemctl restart named
  ```

- 在另外一台`centOS`上测试`dns`服务是否正常，编辑`/etc/resolv.conf`内容如下（其中`192.168.1.116`为dns服务器ip地址）：

  ```
  nameserver 192.168.1.116
  ```

- 测试`dns`配置是否成功

  ```bash
  nslookup www.target321.com
  ping www.target321.com
  ```

  



## `centOS7`上搭建`dns`服务器（没有通过实验验证）

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

