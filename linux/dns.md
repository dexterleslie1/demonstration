

# DNS



## 概念和原理

> [DNS 原理入门](http://www.ruanyifeng.com/blog/2016/06/dns.html)
>
> 



### DNS 是什么？

DNS （Domain Name System 的缩写）的作用非常简单，就是根据域名查出IP地址。你可以把它想象成一本巨大的电话本。

举例来说，如果你要访问域名`math.stackexchange.com`，首先要通过DNS查出它的IP地址是`151.101.129.69`。

如果你不清楚为什么一定要查出IP地址，才能进行网络通信，建议先阅读我写的[《互联网协议入门》](https://www.ruanyifeng.com/blog/2012/05/internet_protocol_suite_part_i.html)。



### 查询过程

虽然只需要返回一个IP地址，但是DNS的查询过程非常复杂，分成多个步骤。

工具软件`dig`可以显示整个查询过程。

```bash
dig math.stackexchange.com
```

上面的命令会输出六段信息。

![img](bg2016061501.png)

第一段是查询参数和统计。

![img](bg2016061502.png)

第二段是查询内容。

![img](bg2016061503.png)

上面结果表示，查询域名`math.stackexchange.com`的`A`记录，`A`是address的缩写。

第三段是DNS服务器的答复。

![img](bg2016061504.png)

上面结果显示，`math.stackexchange.com`有四个`A`记录，即四个IP地址。`600`是TTL值（Time to live 的缩写），表示缓存时间，即600秒之内不用重新查询。

第四段显示`stackexchange.com`的NS记录（Name Server的缩写），即哪些服务器负责管理`stackexchange.com`的DNS记录。

![img](bg2016061505.png)

上面结果显示`stackexchange.com`共有四条NS记录，即四个域名服务器，向其中任一台查询就能知道`math.stackexchange.com`的IP地址是什么。

第五段是上面四个域名服务器的IP地址，这是随着前一段一起返回的。

![img](bg2016061506.png)

第六段是DNS服务器的一些传输信息。

![img](bg2016061514.png)

上面结果显示，本机的DNS服务器是`192.168.1.253`，查询端口是53（DNS服务器的默认端口），以及回应长度是305字节。

如果不想看到这么多内容，可以使用`+short`参数。

```bash
$ dig +short math.stackexchange.com

151.101.129.69
151.101.65.69
151.101.193.69
151.101.1.69
```

上面命令只返回`math.stackexchange.com`对应的4个IP地址（即`A`记录）。



### DNS 服务器

下面我们根据前面这个例子，一步步还原，本机到底怎么得到域名`math.stackexchange.com`的IP地址。

首先，本机一定要知道DNS服务器的IP地址，否则上不了网。通过DNS服务器，才能知道某个域名的IP地址到底是什么。

![img](bg2016061507.jpg)

DNS服务器的IP地址，有可能是动态的，每次上网时由网关分配，这叫做DHCP机制；也有可能是事先指定的固定地址。Linux系统里面，DNS服务器的IP地址保存在`/etc/resolv.conf`文件。

上例的DNS服务器是`192.168.1.253`，这是一个内网地址。有一些公网的DNS服务器，也可以使用，其中最有名的就是Google的[`8.8.8.8`](https://developers.google.com/speed/public-dns/)和Level 3的[`4.2.2.2`](https://www.tummy.com/articles/famous-dns-server/)。

本机只向自己的DNS服务器查询，`dig`命令有一个`@`参数，显示向其他DNS服务器查询的结果。

```bash
dig @4.2.2.2 math.stackexchange.com
```

上面命令指定向DNS服务器`4.2.2.2`查询。



### 域名的层级

DNS服务器怎么会知道每个域名的IP地址呢？答案是分级查询。

请仔细看前面的例子，每个域名的尾部都多了一个点。

![img](bg2016061503.png)

比如，域名`math.stackexchange.com`显示为`math.stackexchange.com.`。这不是疏忽，而是所有域名的尾部，实际上都有一个根域名。

举例来说，`www.example.com`真正的域名是`www.example.com.root`，简写为`www.example.com.`。因为，根域名`.root`对于所有域名都是一样的，所以平时是省略的。

根域名的下一级，叫做"顶级域名"（top-level domain，缩写为TLD），比如`.com`、`.net`；再下一级叫做"次级域名"（second-level domain，缩写为SLD），比如`www.example.com`里面的`.example`，这一级域名是用户可以注册的；再下一级是主机名（host），比如`www.example.com`里面的`www`，又称为"三级域名"，这是用户在自己的域里面为服务器分配的名称，是用户可以任意分配的。

总结一下，域名的层级结构如下。

```
主机名.次级域名.顶级域名.根域名
# 即
host.sld.tld.root
```



### 根域名服务器

DNS服务器根据域名的层级，进行分级查询。

需要明确的是，每一级域名都有自己的NS记录，NS记录指向该级域名的域名服务器。这些服务器知道下一级域名的各种记录。

所谓"分级查询"，就是从根域名开始，依次查询每一级域名的NS记录，直到查到最终的IP地址，过程大致如下。

1. 从"根域名服务器"查到"顶级域名服务器"的NS记录和A记录（IP地址）
2. 从"顶级域名服务器"查到"次级域名服务器"的NS记录和A记录（IP地址）
3. 从"次级域名服务器"查出"主机名"的IP地址

仔细看上面的过程，你可能发现了，没有提到DNS服务器怎么知道"根域名服务器"的IP地址。回答是"根域名服务器"的NS记录和IP地址一般是不会变化的，所以内置在DNS服务器里面。

下面是内置的根域名服务器IP地址的一个[例子](http://www.cyberciti.biz/faq/unix-linux-update-root-hints-data-file/)。

![img](bg2016061508.png)

上面列表中，列出了根域名（`.root`）的三条NS记录`A.ROOT-SERVERS.NET`、`B.ROOT-SERVERS.NET`和`C.ROOT-SERVERS.NET`，以及它们的IP地址（即`A`记录）`198.41.0.4`、`192.228.79.201`、`192.33.4.12`。

另外，可以看到所有记录的TTL值是3600000秒，相当于1000小时。也就是说，每1000小时才查询一次根域名服务器的列表。

目前，世界上一共有十三组根域名服务器，从`A.ROOT-SERVERS.NET`一直到`M.ROOT-SERVERS.NET`。



### 操作系统怎么知道根域名服务器有哪些呢？

>[IANA 机构维护并列出的根服务器信息](https://www.iana.org/domains/root/servers)

为 DNS 根区服务的权威名称服务器，通常称为“根服务器”，是遍布全球许多国家的数百台服务器组成的网络。它们在 DNS 根区中配置为 13 个命名权威，如下所示。

| Hostname           | IP Addresses                      | Operator                                                     |
| ------------------ | --------------------------------- | ------------------------------------------------------------ |
| a.root-servers.net | 198.41.0.4, 2001:503:ba3e::2:30   | Verisign, Inc.                                               |
| b.root-servers.net | 170.247.170.2, 2801:1b8:10::b     | University of Southern California, Information Sciences Institute |
| c.root-servers.net | 192.33.4.12, 2001:500:2::c        | Cogent Communications                                        |
| d.root-servers.net | 199.7.91.13, 2001:500:2d::d       | University of Maryland                                       |
| e.root-servers.net | 192.203.230.10, 2001:500:a8::e    | NASA (Ames Research Center)                                  |
| f.root-servers.net | 192.5.5.241, 2001:500:2f::f       | Internet Systems Consortium, Inc.                            |
| g.root-servers.net | 192.112.36.4, 2001:500:12::d0d    | US Department of Defense (NIC)                               |
| h.root-servers.net | 198.97.190.53, 2001:500:1::53     | US Army (Research Lab)                                       |
| i.root-servers.net | 192.36.148.17, 2001:7fe::53       | Netnod                                                       |
| j.root-servers.net | 192.58.128.30, 2001:503:c27::2:30 | Verisign, Inc.                                               |
| k.root-servers.net | 193.0.14.129, 2001:7fd::1         | RIPE NCC                                                     |
| l.root-servers.net | 199.7.83.42, 2001:500:9f::42      | ICANN                                                        |
| m.root-servers.net | 202.12.27.33, 2001:dc3::35        | WIDE Project                                                 |

管理 DNS 递归解析器的运营商通常需要配置“根提示文件”。此文件包含根服务器的名称和 IP 地址，因此软件可以引导 DNS 解析过程。对于许多软件来说，此列表内置在软件中。

通过 dig 命令列出根域名 . 对应的 13 个根域名服务器

```bash
$ dig NS .
;; communications error to 127.0.0.53#53: timed out

; <<>> DiG 9.18.30-0ubuntu0.20.04.2-Ubuntu <<>> NS .
;; global options: +cmd
;; Got answer:
;; ->>HEADER<<- opcode: QUERY, status: NOERROR, id: 50450
;; flags: qr rd ra; QUERY: 1, ANSWER: 13, AUTHORITY: 0, ADDITIONAL: 1

;; OPT PSEUDOSECTION:
; EDNS: version: 0, flags:; udp: 65494
;; QUESTION SECTION:
;.				IN	NS

;; ANSWER SECTION:
.			2258	IN	NS	k.root-servers.net.
.			2258	IN	NS	i.root-servers.net.
.			2258	IN	NS	m.root-servers.net.
.			2258	IN	NS	g.root-servers.net.
.			2258	IN	NS	b.root-servers.net.
.			2258	IN	NS	c.root-servers.net.
.			2258	IN	NS	e.root-servers.net.
.			2258	IN	NS	d.root-servers.net.
.			2258	IN	NS	l.root-servers.net.
.			2258	IN	NS	f.root-servers.net.
.			2258	IN	NS	h.root-servers.net.
.			2258	IN	NS	j.root-servers.net.
.			2258	IN	NS	a.root-servers.net.

;; Query time: 36 msec
;; SERVER: 127.0.0.53#53(127.0.0.53) (UDP)
;; WHEN: Wed Mar 05 14:02:50 CST 2025
;; MSG SIZE  rcvd: 239
```

通过 dig 命令解析其中一个根服务器 IP 地址

```bash
$ dig h.root-servers.net.
; <<>> DiG 9.18.30-0ubuntu0.20.04.2-Ubuntu <<>> h.root-servers.net.
;; global options: +cmd
;; Got answer:
;; ->>HEADER<<- opcode: QUERY, status: NOERROR, id: 17059
;; flags: qr rd ra; QUERY: 1, ANSWER: 1, AUTHORITY: 0, ADDITIONAL: 1

;; OPT PSEUDOSECTION:
; EDNS: version: 0, flags:; udp: 65494
;; QUESTION SECTION:
;h.root-servers.net.		IN	A

;; ANSWER SECTION:
h.root-servers.net.	5	IN	A	198.97.190.53

;; Query time: 12 msec
;; SERVER: 127.0.0.53#53(127.0.0.53) (UDP)
;; WHEN: Wed Mar 05 14:33:59 CST 2025
;; MSG SIZE  rcvd: 63

```



### 分级查询的实例

`dig`命令的`+trace`参数可以显示DNS的整个分级查询过程。

```bash
dig +trace math.stackexchange.com
```

上面命令的第一段列出根域名`.`的所有NS记录，即所有根域名服务器。

![img](bg2016061509.png)

根据内置的根域名服务器IP地址，DNS服务器向所有这些IP地址发出查询请求，询问`math.stackexchange.com`的顶级域名服务器`com.`的NS记录。最先回复的根域名服务器将被缓存，以后只向这台服务器发请求。

接着是第二段。

![img](bg2016061510.png)

上面结果显示`.com`域名的13条NS记录，同时返回的还有每一条记录对应的IP地址。

然后，DNS服务器向这些顶级域名服务器发出查询请求，询问`math.stackexchange.com`的次级域名`stackexchange.com`的NS记录。

![img](bg2016061511.png)

上面结果显示`stackexchange.com`有四条NS记录，同时返回的还有每一条NS记录对应的IP地址。

然后，DNS服务器向上面这四台NS服务器查询`math.stackexchange.com`的主机名。

![img](bg2016061512.png)

上面结果显示，`math.stackexchange.com`有4条`A`记录，即这四个IP地址都可以访问到网站。并且还显示，最先返回结果的NS服务器是`ns-463.awsdns-57.com`，IP地址为`205.251.193.207`。



### NS 记录的查询

`dig`命令可以单独查看每一级域名的NS记录。

```bash
dig ns com
dig ns stackexchange.com
```

`+short`参数可以显示简化的结果。

```bash
dig +short ns com
dig +short ns stackexchange.com
```



### DNS 的记录类型

域名与IP之间的对应关系，称为"记录"（record）。根据使用场景，"记录"可以分成不同的类型（type），前面已经看到了有`A`记录和`NS`记录。

常见的DNS记录类型如下。

- `A`：地址记录（Address），返回域名指向的IP地址。
- `NS`：域名服务器记录（Name Server），返回保存下一级域名信息的服务器地址。该记录只能设置为域名，不能设置为IP地址。
- `MX`：邮件记录（Mail eXchange），返回接收电子邮件的服务器地址。
- `CNAME`：规范名称记录（Canonical Name），返回另一个域名，即当前查询的域名是另一个域名的跳转，详见下文。
- `PTR`：逆向查询记录（Pointer Record），只用于从IP地址查询域名，详见下文。

一般来说，为了服务的安全可靠，至少应该有两条`NS`记录，而`A`记录和`MX`记录也可以有多条，这样就提供了服务的冗余性，防止出现单点失败。

`CNAME`记录主要用于域名的内部跳转，为服务器配置提供灵活性，用户感知不到。举例来说，`facebook.github.io`这个域名就是一个`CNAME`记录。

```bash
$ dig facebook.github.io

...

;; ANSWER SECTION:
facebook.github.io. 3370    IN  CNAME   github.map.fastly.net.
github.map.fastly.net.  600 IN  A   103.245.222.133
```

上面结果显示，`facebook.github.io`的CNAME记录指向`github.map.fastly.net`。也就是说，用户查询`facebook.github.io`的时候，实际上返回的是`github.map.fastly.net`的IP地址。这样的好处是，变更服务器IP地址的时候，只要修改`github.map.fastly.net`这个域名就可以了，用户的`facebook.github.io`域名不用修改。

由于`CNAME`记录就是一个替换，所以域名一旦设置`CNAME`记录以后，就不能再设置其他记录了（比如`A`记录和`MX`记录），这是为了防止产生冲突。举例来说，`foo.com`指向`bar.com`，而两个域名各有自己的`MX`记录，如果两者不一致，就会产生问题。由于顶级域名通常要设置`MX`记录，所以一般不允许用户对顶级域名设置`CNAME`记录。

`PTR`记录用于从IP地址反查域名。`dig`命令的`-x`参数用于查询`PTR`记录。

```bash
$ dig -x 192.30.252.153

...

;; ANSWER SECTION:
153.252.30.192.in-addr.arpa. 3600 IN    PTR pages.github.com.
```

上面结果显示，`192.30.252.153`这台服务器的域名是`pages.github.com`。

逆向查询的一个应用，是可以防止垃圾邮件，即验证发送邮件的IP地址，是否真的有它所声称的域名。

`dig`命令可以查看指定的记录类型。

```bash
$ dig a github.com
$ dig ns github.com
$ dig mx github.com
```



获取 github.com 所有 DNS 类型记录

```bash
$ dig github.com ANY
; <<>> DiG 9.18.30-0ubuntu0.20.04.2-Ubuntu <<>> github.com ANY
;; global options: +cmd
;; Got answer:
;; ->>HEADER<<- opcode: QUERY, status: NOERROR, id: 34583
;; flags: qr rd ra; QUERY: 1, ANSWER: 16, AUTHORITY: 0, ADDITIONAL: 1

;; OPT PSEUDOSECTION:
; EDNS: version: 0, flags:; udp: 65494
;; QUESTION SECTION:
;github.com.			IN	ANY

;; ANSWER SECTION:
github.com.		60	IN	A	20.205.243.166
github.com.		900	IN	NS	dns1.p08.nsone.net.
github.com.		900	IN	NS	dns2.p08.nsone.net.
github.com.		900	IN	NS	dns3.p08.nsone.net.
github.com.		900	IN	NS	dns4.p08.nsone.net.
github.com.		900	IN	NS	ns-1283.awsdns-32.org.
github.com.		900	IN	NS	ns-1707.awsdns-21.co.uk.
github.com.		900	IN	NS	ns-421.awsdns-52.com.
github.com.		900	IN	NS	ns-520.awsdns-01.net.
github.com.		900	IN	SOA	ns-1707.awsdns-21.co.uk. awsdns-hostmaster.amazon.com. 1 7200 900 1209600 86400
github.com.		3600	IN	MX	1 aspmx.l.google.com.
github.com.		3600	IN	MX	10 alt3.aspmx.l.google.com.
github.com.		3600	IN	MX	10 alt4.aspmx.l.google.com.
github.com.		3600	IN	MX	5 alt1.aspmx.l.google.com.
github.com.		3600	IN	MX	5 alt2.aspmx.l.google.com.
github.com.		3600	IN	TXT	"MS=6BF03E6AF5CB689E315FB6199603BABF2C88D805"

;; Query time: 492 msec
;; SERVER: 127.0.0.53#53(127.0.0.53) (TCP)
;; WHEN: Wed Mar 05 14:35:30 CST 2025
;; MSG SIZE  rcvd: 510

```



#### CName 类型

>[什么是 CName 类型 DNS 记录，他的使用场景](https://gcore.com/learning/dns-cname-record-explained/)

CNAME 记录是企业品牌和网站管理领域的重要工具，可用于多种用途。首先，想想子域名“store.example.com”或“blog.example.com”——它们代表网站的不同部分，为访问者提供特定类型的内容。使用 CNAME 记录可以创建“store”或“blog”等子域名。CNAME 记录的另一个用途是启用内容交付网络 (CDN)，从而缩短页面加载时间并提高用户体验。在本文中，我们将深入研究 CNAME 记录的工作原理、探索其用例并学习实施 CNAME 记录的最佳实践。

**什么是 CNAME 记录？**

CNAME 是规范名称的缩写，是一种 DNS（域名系统）记录。CNAME 记录用于两种情况：

- 创建指向不同服务器的子域
- 将访问者从一个域发送到另一个域

让我们从第一个情况开始。假设您有一个在线商店，并且想要创建子域“shop.example.com”。您可以创建 CNAME 记录以指向主域（example.com）或使用其提供的 URL 指向其他来源（例如第三方网站）。例如，如果您有一个 Shopify 帐户，则可以使用 CNAME 记录（shop.example.com）将您的客户发送到您的 Shopify 网站（shops.myshopify.com）。

在第二种情况下，CNAME 记录用作从一个域名到另一个域名的指针或别名。它允许您将一个域名与另一个域名的 IP 地址相关联。简单来说，CNAME 记录就像域名的昵称或快捷方式。

**CNAME 记录的组成部分有哪些？**

下表是 CNAME 记录的示例：

| Hostname         | Record Type | Value                         | TTL          |
| ---------------- | ----------- | ----------------------------- | ------------ |
| www.example.com  | CNAME       | @ (example.com)               | 1800 seconds |
| blog.example.com | CNAME       | blogalias.com                 | 3600 seconds |
| mail.example.com | CNAME       | domainverify.googlehosted.com | 3600 seconds |

让我们详细研究一下这些组件及其功能。

- **Hostname**：主机名是用于识别和区分网络内设备的标识符或唯一标签。在域名系统 (DNS) 中，主机名通常与域名相关联。例如，在域名“www.example.com”中，“www”是代表“example.com”域内特定服务器或服务的主机名。主机名有助于将网络请求路由到适当的设备或服务器。除了网站之外，它还可用于访问电子邮件服务器、FTP 服务器或任何其他网络连接设备等资源。
- **Record Type**：CNAME 记录的记录类型始终设置为“CNAME”，以便 DNS 解析器可以将其识别为 CNAME。这允许解析器跟踪别名并解析规范名称，从而检索相应的 IP 地址或其他服务器类型。
- **Value**：这表示 DNS 解析器应将指定主机名的查询重定向到的目的地或目标。例如，如果您需要在 Google 中验证您的域名，则需要添加一个 CNAME 记录，其中包含他们提供的唯一值，例如“domainverify.googlehosted.com”。
- **TTL（生存时间）**：CNAME 记录的 TTL 值决定了 DNS 解析器和缓存服务器在检查更新之前应缓存记录的时间长度。它以秒为单位指定。例如，TTL 为 3600 表示一小时的缓存时间。

**CNAME 记录的使用案例**

CNAME 记录在引导流量、设置子域或实施第三方服务方面提供了灵活性。无论是重定向域、管理内容交付网络 (CDN) 还是促进电子邮件服务，CNAME 记录都使网站所有者能够自定义和优化其在线状态，无论其业务类型或规模如何。让我们更深入地了解 CNAME 记录的一些使用案例。

- 域名重定向

  CNAME 记录可以将流量从一个域名重定向到另一个域名，从而可以轻松地在网站之间切换或将访问者重定向到不同的在线位置。为域名创建 CNAME 记录时，它充当别名或昵称，允许源域名指向与目标域名相同的 IP 地址。

  示例：在线服装零售商 John’s Apparel 决定重塑其网站并切换到新域名“new-johnsapparel.com”。为了确保无缝过渡，该公司将为旧域名“old-johnsapparel.com”设置 CNAME 记录，将用户重定向到新网站。当用户在 Web 浏览器中输入“old-johnsapparel.com”时，CNAME 记录会将他们定向到新网站。用户无需学习或记住新 URL，从而消除了更改域名时可能造成的业务损失。

  以下是域名重定向的示例 CNAME 记录：

  | **Type** | **Hostname**        | **Value**           |
  | -------- | ------------------- | ------------------- |
  | CNAME    | old-johnapparel.com | new-johnapparel.com |

- 子域名设置

  CNAME 记录通常用于创建指向单独服务器或服务的子域名。这有助于组织独立安排和处理其网站的特定部分。

  示例：ABC Corporation 是一家拥有众多服务和部门的软件开发公司。它希望在特定子域名下组织和管理其不同的服务。例如，子域名“support.abc-corp.com”需要指向其客户支持门户的“support-target-server.com”。这使他们能够独立处理业务的不同部分，同时保持具有良好用户体验的在线状态。

  以下是子域名设置的示例 CNAME 记录：

  | **Type** | **Hostname**         | **Value**                 |
  | -------- | -------------------- | ------------------------- |
  | CNAME    | support.abc-corp.com | support-target-server.com |

- 内容交付网络 (CDN)

  CNAME 记录可用于集成 CDN，CDN 可在多个服务器上缓存和分发网站内容，从而提高网站性能和全球可访问性。

  示例：Shoes & More 是一家在线商店，向全球客户销售各种鞋类产品和配饰。他们的业务增长迅速，导致网站流量增加，需要优化网站的性能和加载速度。该公司决定集成 CDN 来提高网站的性能。为此，他们注册了 CDN 服务，并被建议设置 CNAME 记录。

  以下是内容交付网络的 CNAME 记录示例：

  | **Type** | **Hostname**     | **Value**                |
  | -------- | ---------------- | ------------------------ |
  | CNAME    | shoesandmore.com | cdn-provider-example.com |

- 电子邮件服务

  CNAME 记录使域名所有者能够配置来自第三方提供商的电子邮件服务。例如，通过创建 CNAME 记录，您可以将域名的电子邮件子域名（例如“mail.example.com”）与专用的电子邮件服务提供商（如 Google 或 Microsoft）关联。

  示例：Handmade Crafts & Treasures 是一家小型电子商务企业，在线销售手工艺品和手工艺品。他们希望通过使用专业的电子邮件服务来增强电子邮件通信，因此他们决定注册第三方电子邮件服务提供商。要进行设置，他们需要为新的电子邮件服务创建 CNAME 记录。

  以下是电子邮件服务的 CNAME 记录示例：

  | **Type** | **Hostname**                      | **Value**         |
  | -------- | --------------------------------- | ----------------- |
  | CNAME    | mail.handmadecrafts-treasures.com | mail.provider.com |

- 第三方服务集成

  通过利用 CNAME 记录，组织可以轻松集成第三方服务，例如电子商务平台、客户支持系统或分析工具，从而确保无缝的用户体验。例如，如果您需要为网站使用 Google Analytics，则需要将 CNAME 记录添加到 DNS 记录以验证您的域名。

  示例：XYZ Goods 在线商店是一家通过其网站销售各种产品的电子商务企业。他们希望集成 Google Analytics，以获得有关其网站流量和客户行为的宝贵见解。为此，他们需要将 CNAME 记录添加到其 DNS 记录以验证其 Google Analytics 的域名。

  以下是第三方服务（Google Analytics）集成的 CNAME 记录：

  | **Type** | **Hostname**                  | **Value**            |
  | -------- | ----------------------------- | -------------------- |
  | CNAME    | www.xyz-goods-onlinestore.com | analytics.google.com |

**DNS 如何管理和解析 CNAME 记录？**

管理和解析 CNAME 记录需要使用 DNS，即域名系统。

![img](dns-cname-record-explained-1.png)

以下是 DNS 管理和解析 CNAME 记录的步骤：

1. 用户发送请求。该过程从用户发送请求开始，例如在其 Web 浏览器中输入网站的 URL。
2. DNS 解析器。用户设备通常由互联网服务提供商 (ISP) 或公共 DNS 提供商操作，充当 DNS 解析器。DNS 解析器负责处理 DNS 查询并将域名解析为 IP 地址。
3. DNS 根服务器。如果 DNS 解析器在其缓存中没有请求的信息，它会通过向根 DNS 服务器发送请求来启动 DNS 查询。根 DNS 服务器是 DNS 解析过程的起点，负责提供有关顶级域 (TLD) 服务器的信息。
4. TLD DNS 服务器。根 DNS 服务器通过提供负责所请求域的特定 TLD 的 TLD DNS 服务器的 IP 地址来响应解析器的查询。例如，如果请求的域是“example.com”，则会识别“.com”TLD 的 TLD DNS 服务器。
5. 权威 DNS 服务器。 DNS 解析器联系 TLD DNS 服务器以获取负责域“example.com”的权威 DNS 服务器的 IP 地址。权威 DNS 服务器是维护域的特定 DNS 记录（包括 CNAME 记录）的地方。之后，DNS 解析器会缓存获取的信息，这有助于更快地响应后续查询并减少 DNS 基础设施的负载。

总而言之，DNS 通过使用解析器系统地查询权威 DNS 服务器来管理 CNAME 记录，跟踪沿途遇到的任何 CNAME 记录，直到到达最终目的地。缓存可提高效率并减少重复查询的需要。



### 其他 DNS 工具

除了`dig`，还有一些其他小工具也可以使用。

**（1）host 命令**

`host`命令可以看作`dig`命令的简化版本，返回当前请求域名的各种记录。

```bash
$ host github.com

github.com has address 192.30.252.121
github.com mail is handled by 5 ALT2.ASPMX.L.GOOGLE.COM.
github.com mail is handled by 10 ALT4.ASPMX.L.GOOGLE.COM.
github.com mail is handled by 10 ALT3.ASPMX.L.GOOGLE.COM.
github.com mail is handled by 5 ALT1.ASPMX.L.GOOGLE.COM.
github.com mail is handled by 1 ASPMX.L.GOOGLE.COM.

$ host facebook.github.com

facebook.github.com is an alias for github.map.fastly.net.
github.map.fastly.net has address 103.245.222.133
```

`host`命令也可以用于逆向查询，即从IP地址查询域名，等同于`dig -x <ip>`。

```bash
$ host 192.30.252.153

153.252.30.192.in-addr.arpa domain name pointer pages.github.com.
```

**（2）nslookup 命令**

`nslookup`命令用于互动式地查询域名记录。

```bash
$ nslookup

> facebook.github.io
Server:     192.168.1.253
Address:    192.168.1.253#53

Non-authoritative answer:
facebook.github.io  canonical name = github.map.fastly.net.
Name:   github.map.fastly.net
Address: 103.245.222.133

> 
```

**（3）whois 命令**

`whois`命令用来查看域名的注册情况。

```bash
$ whois github.com
```



## 本地计算机 DNS 缓存

### Windows 系统

查看本地 DNS 缓存

```cmd
ipconfig /displaydns
```

- `ipconfig /displaydns` 是一个在Windows操作系统中使用的命令行指令，它用于显示DNS解析器缓存中的内容。DNS（域名系统）解析器缓存是用来存储之前查询过的域名及其对应的IP地址的临时数据库，这样当相同的域名再次被请求时，系统可以直接从缓存中获取IP地址，而不需要再次进行DNS查询，从而加快访问速度。

  使用`ipconfig /displaydns`命令可以列出当前系统中DNS解析器缓存的所有条目，包括已解析的域名、对应的IP地址、记录类型（如A记录指向IPv4地址，AAAA记录指向IPv6地址等）、生存时间（TTL，表示该记录可以在缓存中保留的时间长度）等信息。

  如果你遇到无法访问某个网站的问题，或者怀疑DNS缓存中有错误的条目，可以使用这个命令来检查DNS缓存的内容。如果发现缓存中有错误的条目，可以使用`ipconfig /flushdns`命令来清空DNS解析器缓存，这样系统会重新进行DNS查询，获取正确的IP地址。

  请注意，使用`ipconfig /displaydns`命令查看的DNS缓存信息可能包含敏感或私有数据，因此在共享或发布这些信息时需要谨慎。



清除本地 DNS 缓存

```cmd
ipconfig /flushdns
```



### Ubuntu 系统

>缓存并不保证在每个 Linux 系统上都存在。在传统配置中（即没有 systemd），应用程序会直接将 DNS 查询发送到 /etc/resolv.conf 中的服务器，因此首先看不到“系统”DNS 缓存。 发行版通常会默认启用 DNS 缓存，但具体机制各不相同。[引用自](https://superuser.com/questions/1680644/how-do-you-see-the-os-dns-cache-on-linux)

查看什么进程在监听 DNS 端口 53

```bash
$ sudo netstat -nulp | grep 53
udp        0      0 127.0.0.53:53           0.0.0.0:*                           882/systemd-resolve
```

- 表示 systemd-resolve 进程（Ubuntu 使用此程序作为 DNS 缓存）在监听 53 端口



查看系统当前 DNS 缓存统计信息

```bash
$ sudo systemd-resolve --statistics
DNSSEC supported by current servers: no

Transactions               
Current Transactions: 0    
  Total Transactions: 10593
                           
Cache                      
  Current Cache Size: 1    # DNS 缓存条目数 
          Cache Hits: 341  # DNS 缓存查询命中次数
        Cache Misses: 903  # DNS 缓存查询没有命中次数
                           
DNSSEC Verdicts            
              Secure: 0    
            Insecure: 0    
               Bogus: 0    
       Indeterminate: 0
```



查看 systemd-resolve 集成的 DNS 缓存

```bash
ping -c 1 github.com && sudo systemctl kill -s USR1 systemd-resolved
```

- 运行 systemctl kill -s USR1 systemd-resolved，systemd-resolved 会在收到 SIGUSR1 时将所有缓存内容转储到系统日志（journalctl -b -u systemd-resolved）。

查看 systemctl-resolve 进程收到 SIGUSR1 时将所有缓存内容转储到系统日志

```bash
$ journalctl -f -u systemd-resolved
3月 05 11:47:59 myvm1 systemd-resolved[882]: [Scope protocol=dns interface=ens33]
3月 05 11:47:59 myvm1 systemd-resolved[882]: CACHE:
3月 05 11:47:59 myvm1 systemd-resolved[882]:         github.com IN A 20.205.243.166
3月 05 11:47:59 myvm1 systemd-resolved[882]: [Scope protocol=dns]
3月 05 11:47:59 myvm1 systemd-resolved[882]: [Server 192.168.235.2 type=link interface=ens33]
3月 05 11:47:59 myvm1 systemd-resolved[882]:         Verified feature level: UDP
3月 05 11:47:59 myvm1 systemd-resolved[882]:         Possible feature level: UDP
3月 05 11:47:59 myvm1 systemd-resolved[882]:         DNSSEC Mode: no
3月 05 11:47:59 myvm1 systemd-resolved[882]:         Can do DNSSEC: no
3月 05 11:47:59 myvm1 systemd-resolved[882]:         Maximum UDP packet size received: 512
3月 05 11:47:59 myvm1 systemd-resolved[882]:         Failed UDP attempts: 0
3月 05 11:47:59 myvm1 systemd-resolved[882]:         Failed TCP attempts: 0
3月 05 11:47:59 myvm1 systemd-resolved[882]:         Seen truncated packet: no
3月 05 11:47:59 myvm1 systemd-resolved[882]:         Seen OPT RR getting lost: yes
3月 05 11:47:59 myvm1 systemd-resolved[882]:         Seen RRSIG RR missing: no
3月 05 11:47:59 myvm1 systemd-resolved[882]: [Server 114.114.114.114 type=link interface=ens33]
3月 05 11:47:59 myvm1 systemd-resolved[882]:         Verified feature level: UDP
3月 05 11:47:59 myvm1 systemd-resolved[882]:         Possible feature level: UDP
3月 05 11:47:59 myvm1 systemd-resolved[882]:         DNSSEC Mode: no
3月 05 11:47:59 myvm1 systemd-resolved[882]:         Can do DNSSEC: no
3月 05 11:47:59 myvm1 systemd-resolved[882]:         Maximum UDP packet size received: 512
3月 05 11:47:59 myvm1 systemd-resolved[882]:         Failed UDP attempts: 1
3月 05 11:47:59 myvm1 systemd-resolved[882]:         Failed TCP attempts: 0
3月 05 11:47:59 myvm1 systemd-resolved[882]:         Seen truncated packet: no
3月 05 11:47:59 myvm1 systemd-resolved[882]:         Seen OPT RR getting lost: yes
3月 05 11:47:59 myvm1 systemd-resolved[882]:         Seen RRSIG RR missing: no
```



## 调试 DNS 解析过程

调试 github.com DNS 解析过程

```bash
$ dig +trace github.com
; <<>> DiG 9.18.30-0ubuntu0.20.04.2-Ubuntu <<>> +trace github.com
;; global options: +cmd
# 根域名 . 所对应 13 个根命名服务器列表
.			2522	IN	NS	a.root-servers.net.
.			2522	IN	NS	j.root-servers.net.
.			2522	IN	NS	h.root-servers.net.
.			2522	IN	NS	f.root-servers.net.
.			2522	IN	NS	l.root-servers.net.
.			2522	IN	NS	d.root-servers.net.
.			2522	IN	NS	e.root-servers.net.
.			2522	IN	NS	c.root-servers.net.
.			2522	IN	NS	b.root-servers.net.
.			2522	IN	NS	g.root-servers.net.
.			2522	IN	NS	m.root-servers.net.
.			2522	IN	NS	i.root-servers.net.
.			2522	IN	NS	k.root-servers.net.
# 从本地 DNS 服务（Ubuntu 中为 systemd-resolve 进程）中获取 13 个根域名服务器
;; Received 262 bytes from 127.0.0.53#53(127.0.0.53) in 0 ms

# 尝试和根域名服务器 a.root-servers.net 建立通讯，但失败。
# 通过 [IANA 机构维护并列出的根服务器信息](https://www.iana.org/domains/root/servers) 可以查询到 IPv6 2001:503:ba3e::2:30 对应 a.root-servers.net 根域名服务器
;; UDP setup with 2001:503:ba3e::2:30#53(2001:503:ba3e::2:30) for github.com failed: network unreachable.
;; no servers could be reached
;; UDP setup with 2001:503:ba3e::2:30#53(2001:503:ba3e::2:30) for github.com failed: network unreachable.
;; no servers could be reached
;; UDP setup with 2001:503:ba3e::2:30#53(2001:503:ba3e::2:30) for github.com failed: network unreachable.
# 域名 com. 所对应的域名服务器列表
com.			172800	IN	NS	a.gtld-servers.net.
com.			172800	IN	NS	b.gtld-servers.net.
com.			172800	IN	NS	c.gtld-servers.net.
com.			172800	IN	NS	d.gtld-servers.net.
com.			172800	IN	NS	e.gtld-servers.net.
com.			172800	IN	NS	f.gtld-servers.net.
com.			172800	IN	NS	g.gtld-servers.net.
com.			172800	IN	NS	h.gtld-servers.net.
com.			172800	IN	NS	i.gtld-servers.net.
com.			172800	IN	NS	j.gtld-servers.net.
com.			172800	IN	NS	k.gtld-servers.net.
com.			172800	IN	NS	l.gtld-servers.net.
com.			172800	IN	NS	m.gtld-servers.net.
com.			86400	IN	DS	19718 13 2 8ACBB0CD28F41250A80A491389424D341522D946B0DA0C0291F2D3D7 71D7805A
com.			86400	IN	RRSIG	DS 8 1 86400 20250318050000 20250305040000 26470 . jMRXenERxrc6IR1XbJuhUz16TqqVDi4i32hDsg43ejOVyLGicAXpPvos VNPop4CYssJks8PZ3vXOlIBIjlOLYd6HpNt9oMeuacsjvjYFuBdSPaaS oH4hFIhvgJHApFqYyskEYzi2ezomgrZn+mP4w2f02shccfC9NPrb+4Ww 58tiJVpc2QNYXoKFLoxa4svGVtAtd7A3bQnUQhAqzxgRWUZsTU1xl0c/ rLXC+BBgDBsqAmo0b+EF4t3y6R8Zhmt9tDMAJlvXOYF/YUKJIDycuMOf XNfOIsFMvVdEaTrRw8voddTqNAkyMCghe5RA5p8ApLcaUmj0S4pnMUR+ tG6/3w==
# d.root-servers.net 域名服务器应答以上查询数据
;; Received 1170 bytes from 199.7.91.13#53(d.root-servers.net) in 352 ms

github.com.		172800	IN	NS	ns-520.awsdns-01.net.
github.com.		172800	IN	NS	ns-421.awsdns-52.com.
github.com.		172800	IN	NS	ns-1707.awsdns-21.co.uk.
github.com.		172800	IN	NS	ns-1283.awsdns-32.org.
github.com.		172800	IN	NS	dns1.p08.nsone.net.
github.com.		172800	IN	NS	dns2.p08.nsone.net.
github.com.		172800	IN	NS	dns3.p08.nsone.net.
github.com.		172800	IN	NS	dns4.p08.nsone.net.
CK0POJMG874LJREF7EFN8430QVIT8BSM.com. 900 IN NSEC3 1 1 0 - CK0Q3UDG8CEKKAE7RUKPGCT1DVSSH8LL NS SOA RRSIG DNSKEY NSEC3PARAM
CK0POJMG874LJREF7EFN8430QVIT8BSM.com. 900 IN RRSIG NSEC3 13 2 900 20250312002635 20250304231635 23202 com. 6GREDfZLJM5krM36gjTV+gdOYAslEsrjuYl+8da0m3hPU18P6tuxceas cVUgu7SdMhBzfm1pue8vCMLJFxyFyw==
4KB4DFS71LEP8G8P8VT4CCUSQNL4CNCS.com. 900 IN NSEC3 1 1 0 - 4KB57I4QH1TQEGK40RG09URSV4KUAU2I NS DS RRSIG
4KB4DFS71LEP8G8P8VT4CCUSQNL4CNCS.com. 900 IN RRSIG NSEC3 13 2 900 20250309023802 20250302012802 23202 com. qXvpLBERGVKaHORXAlaKbZ1QHpFYbaS4bX2Qd3Fs+wpNitMpgHRlj6Sp Dl0B+eCCjmmJcDedPbWmNgAxLsob/Q==
;; Received 635 bytes from 192.48.79.30#53(j.gtld-servers.net) in 328 ms

;; UDP setup with 2620:4d:4000:6259:7:8:0:3#53(2620:4d:4000:6259:7:8:0:3) for github.com failed: network unreachable.
;; UDP setup with 2a00:edc0:6259:7:8::2#53(2a00:edc0:6259:7:8::2) for github.com failed: network unreachable.
github.com.		60	IN	A	20.205.243.166
github.com.		900	IN	NS	dns1.p08.nsone.net.
github.com.		900	IN	NS	dns2.p08.nsone.net.
github.com.		900	IN	NS	dns3.p08.nsone.net.
github.com.		900	IN	NS	dns4.p08.nsone.net.
github.com.		900	IN	NS	ns-1283.awsdns-32.org.
github.com.		900	IN	NS	ns-1707.awsdns-21.co.uk.
github.com.		900	IN	NS	ns-421.awsdns-52.com.
github.com.		900	IN	NS	ns-520.awsdns-01.net.
;; Received 278 bytes from 205.251.198.171#53(ns-1707.awsdns-21.co.uk) in 52 ms

$ dig +trace +all github.com
;; communications error to 127.0.0.53#53: timed out

; <<>> DiG 9.18.30-0ubuntu0.20.04.2-Ubuntu <<>> +trace +all github.com
;; global options: +cmd
;; Got answer:
;; ->>HEADER<<- opcode: QUERY, status: NOERROR, id: 61747
;; flags: qr rd ra; QUERY: 1, ANSWER: 13, AUTHORITY: 0, ADDITIONAL: 1

;; OPT PSEUDOSECTION:
; EDNS: version: 0, flags: do; udp: 65494
; OPT=5: 05 07 08 0a 0d 0e 0f (".......")
; OPT=6: 01 02 04 ("...")
; OPT=7: 01 (".")
;; QUESTION SECTION:
;.				IN	NS

;; ANSWER SECTION:
.			2458	IN	NS	i.root-servers.net.
.			2458	IN	NS	m.root-servers.net.
.			2458	IN	NS	h.root-servers.net.
.			2458	IN	NS	e.root-servers.net.
.			2458	IN	NS	b.root-servers.net.
.			2458	IN	NS	a.root-servers.net.
.			2458	IN	NS	j.root-servers.net.
.			2458	IN	NS	d.root-servers.net.
.			2458	IN	NS	g.root-servers.net.
.			2458	IN	NS	c.root-servers.net.
.			2458	IN	NS	f.root-servers.net.
.			2458	IN	NS	k.root-servers.net.
.			2458	IN	NS	l.root-servers.net.

;; Query time: 31 msec
;; SERVER: 127.0.0.53#53(127.0.0.53) (UDP)
;; WHEN: Wed Mar 05 13:59:42 CST 2025
;; MSG SIZE  rcvd: 262

;; UDP setup with 2001:503:c27::2:30#53(2001:503:c27::2:30) for github.com failed: network unreachable.
;; no servers could be reached
;; UDP setup with 2001:503:c27::2:30#53(2001:503:c27::2:30) for github.com failed: network unreachable.
;; UDP setup with 2001:7fd::1#53(2001:7fd::1) for github.com failed: network unreachable.
;; Got answer:
;; ->>HEADER<<- opcode: QUERY, status: NOERROR, id: 56392
;; flags: qr; QUERY: 1, ANSWER: 0, AUTHORITY: 15, ADDITIONAL: 27

;; OPT PSEUDOSECTION:
; EDNS: version: 0, flags: do; udp: 1232
; COOKIE: 3c05907d6e95c2ae0100000067c7e84e1c9a003a6dcf7cc4 (good)
;; QUESTION SECTION:
;github.com.			IN	A

;; AUTHORITY SECTION:
com.			172800	IN	NS	g.gtld-servers.net.
com.			172800	IN	NS	c.gtld-servers.net.
com.			172800	IN	NS	m.gtld-servers.net.
com.			172800	IN	NS	b.gtld-servers.net.
com.			172800	IN	NS	i.gtld-servers.net.
com.			172800	IN	NS	k.gtld-servers.net.
com.			172800	IN	NS	j.gtld-servers.net.
com.			172800	IN	NS	e.gtld-servers.net.
com.			172800	IN	NS	d.gtld-servers.net.
com.			172800	IN	NS	f.gtld-servers.net.
com.			172800	IN	NS	l.gtld-servers.net.
com.			172800	IN	NS	h.gtld-servers.net.
com.			172800	IN	NS	a.gtld-servers.net.
com.			86400	IN	DS	19718 13 2 8ACBB0CD28F41250A80A491389424D341522D946B0DA0C0291F2D3D7 71D7805A
com.			86400	IN	RRSIG	DS 8 1 86400 20250318050000 20250305040000 26470 . jMRXenERxrc6IR1XbJuhUz16TqqVDi4i32hDsg43ejOVyLGicAXpPvos VNPop4CYssJks8PZ3vXOlIBIjlOLYd6HpNt9oMeuacsjvjYFuBdSPaaS oH4hFIhvgJHApFqYyskEYzi2ezomgrZn+mP4w2f02shccfC9NPrb+4Ww 58tiJVpc2QNYXoKFLoxa4svGVtAtd7A3bQnUQhAqzxgRWUZsTU1xl0c/ rLXC+BBgDBsqAmo0b+EF4t3y6R8Zhmt9tDMAJlvXOYF/YUKJIDycuMOf XNfOIsFMvVdEaTrRw8voddTqNAkyMCghe5RA5p8ApLcaUmj0S4pnMUR+ tG6/3w==

;; ADDITIONAL SECTION:
m.gtld-servers.net.	172800	IN	A	192.55.83.30
l.gtld-servers.net.	172800	IN	A	192.41.162.30
k.gtld-servers.net.	172800	IN	A	192.52.178.30
j.gtld-servers.net.	172800	IN	A	192.48.79.30
i.gtld-servers.net.	172800	IN	A	192.43.172.30
h.gtld-servers.net.	172800	IN	A	192.54.112.30
g.gtld-servers.net.	172800	IN	A	192.42.93.30
f.gtld-servers.net.	172800	IN	A	192.35.51.30
e.gtld-servers.net.	172800	IN	A	192.12.94.30
d.gtld-servers.net.	172800	IN	A	192.31.80.30
c.gtld-servers.net.	172800	IN	A	192.26.92.30
b.gtld-servers.net.	172800	IN	A	192.33.14.30
a.gtld-servers.net.	172800	IN	A	192.5.6.30
m.gtld-servers.net.	172800	IN	AAAA	2001:501:b1f9::30
l.gtld-servers.net.	172800	IN	AAAA	2001:500:d937::30
k.gtld-servers.net.	172800	IN	AAAA	2001:503:d2d::30
j.gtld-servers.net.	172800	IN	AAAA	2001:502:7094::30
i.gtld-servers.net.	172800	IN	AAAA	2001:503:39c1::30
h.gtld-servers.net.	172800	IN	AAAA	2001:502:8cc::30
g.gtld-servers.net.	172800	IN	AAAA	2001:503:eea3::30
f.gtld-servers.net.	172800	IN	AAAA	2001:503:d414::30
e.gtld-servers.net.	172800	IN	AAAA	2001:502:1ca1::30
d.gtld-servers.net.	172800	IN	AAAA	2001:500:856e::30
c.gtld-servers.net.	172800	IN	AAAA	2001:503:83eb::30
b.gtld-servers.net.	172800	IN	AAAA	2001:503:231d::2:30
a.gtld-servers.net.	172800	IN	AAAA	2001:503:a83e::2:30

;; Query time: 55 msec
;; SERVER: 192.36.148.17#53(i.root-servers.net) (UDP)
;; WHEN: Wed Mar 05 13:59:47 CST 2025
;; MSG SIZE  rcvd: 1198

;; UDP setup with 2001:503:eea3::30#53(2001:503:eea3::30) for github.com failed: network unreachable.
;; Got answer:
;; ->>HEADER<<- opcode: QUERY, status: NOERROR, id: 56630
;; flags: qr; QUERY: 1, ANSWER: 0, AUTHORITY: 12, ADDITIONAL: 2

;; OPT PSEUDOSECTION:
; EDNS: version: 0, flags: do; udp: 4096
;; QUESTION SECTION:
;github.com.			IN	A

;; AUTHORITY SECTION:
github.com.		172800	IN	NS	ns-520.awsdns-01.net.
github.com.		172800	IN	NS	ns-421.awsdns-52.com.
github.com.		172800	IN	NS	ns-1707.awsdns-21.co.uk.
github.com.		172800	IN	NS	ns-1283.awsdns-32.org.
github.com.		172800	IN	NS	dns1.p08.nsone.net.
github.com.		172800	IN	NS	dns2.p08.nsone.net.
github.com.		172800	IN	NS	dns3.p08.nsone.net.
github.com.		172800	IN	NS	dns4.p08.nsone.net.
CK0POJMG874LJREF7EFN8430QVIT8BSM.com. 900 IN NSEC3 1 1 0 - CK0Q3UDG8CEKKAE7RUKPGCT1DVSSH8LL NS SOA RRSIG DNSKEY NSEC3PARAM
CK0POJMG874LJREF7EFN8430QVIT8BSM.com. 900 IN RRSIG NSEC3 13 2 900 20250312002635 20250304231635 23202 com. 6GREDfZLJM5krM36gjTV+gdOYAslEsrjuYl+8da0m3hPU18P6tuxceas cVUgu7SdMhBzfm1pue8vCMLJFxyFyw==
4KB4DFS71LEP8G8P8VT4CCUSQNL4CNCS.com. 900 IN NSEC3 1 1 0 - 4KB57I4QH1TQEGK40RG09URSV4KUAU2I NS DS RRSIG
4KB4DFS71LEP8G8P8VT4CCUSQNL4CNCS.com. 900 IN RRSIG NSEC3 13 2 900 20250309023802 20250302012802 23202 com. qXvpLBERGVKaHORXAlaKbZ1QHpFYbaS4bX2Qd3Fs+wpNitMpgHRlj6Sp Dl0B+eCCjmmJcDedPbWmNgAxLsob/Q==

;; ADDITIONAL SECTION:
ns-421.awsdns-52.com.	172800	IN	A	205.251.193.165

;; Query time: 215 msec
;; SERVER: 192.52.178.30#53(k.gtld-servers.net) (UDP)
;; WHEN: Wed Mar 05 13:59:48 CST 2025
;; MSG SIZE  rcvd: 635

;; communications error to 205.251.197.3#53: timed out
;; Got answer:
;; ->>HEADER<<- opcode: QUERY, status: NOERROR, id: 23025
;; flags: qr aa ad; QUERY: 1, ANSWER: 1, AUTHORITY: 8, ADDITIONAL: 1

;; OPT PSEUDOSECTION:
; EDNS: version: 0, flags: do; udp: 4096
;; QUESTION SECTION:
;github.com.			IN	A

;; ANSWER SECTION:
github.com.		60	IN	A	20.205.243.166

;; AUTHORITY SECTION:
github.com.		900	IN	NS	dns1.p08.nsone.net.
github.com.		900	IN	NS	dns2.p08.nsone.net.
github.com.		900	IN	NS	dns3.p08.nsone.net.
github.com.		900	IN	NS	dns4.p08.nsone.net.
github.com.		900	IN	NS	ns-1283.awsdns-32.org.
github.com.		900	IN	NS	ns-1707.awsdns-21.co.uk.
github.com.		900	IN	NS	ns-421.awsdns-52.com.
github.com.		900	IN	NS	ns-520.awsdns-01.net.

;; Query time: 243 msec
;; SERVER: 205.251.194.8#53(ns-520.awsdns-01.net) (UDP)
;; WHEN: Wed Mar 05 13:59:53 CST 2025
;; MSG SIZE  rcvd: 278

```

- `dig +all` 是一个使用 DNS（域名系统）查询工具 `dig` 的命令，它用于获取指定域名的详细 DNS 信息。`+all` 选项指示 `dig` 显示所有可用的查询结果信息，包括权威服务器的回答、附加部分以及查询过程中可能遇到的各种记录。

  下面是 `dig +all` 命令可能返回的信息类型的简要说明：

  1. **HEADER**：这部分包含了查询的基本信息，如 ID（查询的唯一标识符）、OPCODE（操作码，指示查询类型，如查询或更新）、STATUS（返回状态码，如NOERROR表示查询成功）、FLAGS（标志位，如RD表示递归查询请求）、QUESTIONS（查询问题数）、ANSWER RRs（回答记录数）、AUTHORITY RRs（权威记录数）和ADDITIONAL RRs（附加记录数）。
  2. **QUESTION SECTION**：显示查询的具体问题，即查询的域名和类型（如A记录、MX记录等）。
  3. **ANSWER SECTION**：包含对查询问题的直接回答。例如，如果你查询一个域名的A记录，这里会显示IP地址。
  4. **AUTHORITY SECTION**：显示权威服务器的信息，这些服务器负责提供被查询域名的DNS记录。这部分通常包括NS（域名服务器）记录和相关的SOA（起始授权）记录。
  5. **ADDITIONAL SECTION**：包含额外的信息，如与查询相关的其他DNS记录。这些记录可能有助于理解或进一步查询域名信息。

  使用 `dig +all` 可以帮助你深入了解DNS查询的详细过程，以及域名背后的DNS记录结构。这对于网络管理员、系统管理员或任何对DNS工作原理感兴趣的人来说都是一个非常有用的工具。

  请注意，由于 `dig +all` 返回的信息非常详细，结果可能会相当冗长。因此，在处理大量查询或仅需要快速查看特定信息时，可能需要使用更具体的选项来限制返回的信息量。



## 递归查询和迭代查询

>[深入浅出 DNS 解析：递归与迭代](https://zhuanlan.zhihu.com/p/23562820067)

DNS 解析本质上是一个问答过程。为了高效完成域名到 IP 的转换，DNS 系统设计了一种分层架构：

- **[根域名服务器](https://zhida.zhihu.com/search?content_id=253734413&content_type=Article&match_order=1&q=根域名服务器&zd_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJ6aGlkYV9zZXJ2ZXIiLCJleHAiOjE3NDEzMzI2OTEsInEiOiLmoLnln5_lkI3mnI3liqHlmagiLCJ6aGlkYV9zb3VyY2UiOiJlbnRpdHkiLCJjb250ZW50X2lkIjoyNTM3MzQ0MTMsImNvbnRlbnRfdHlwZSI6IkFydGljbGUiLCJtYXRjaF9vcmRlciI6MSwiemRfdG9rZW4iOm51bGx9.HOmYgml3smrFNBiTp3UtEU1ARDG478yrf-thfs0mZQM&zhida_source=entity)（Root DNS Server）**：
  管理顶级域名（如 `.com`、`.org`）的解析，指向对应的[顶级域名服务器](https://zhida.zhihu.com/search?content_id=253734413&content_type=Article&match_order=1&q=顶级域名服务器&zd_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJ6aGlkYV9zZXJ2ZXIiLCJleHAiOjE3NDEzMzI2OTEsInEiOiLpobbnuqfln5_lkI3mnI3liqHlmagiLCJ6aGlkYV9zb3VyY2UiOiJlbnRpdHkiLCJjb250ZW50X2lkIjoyNTM3MzQ0MTMsImNvbnRlbnRfdHlwZSI6IkFydGljbGUiLCJtYXRjaF9vcmRlciI6MSwiemRfdG9rZW4iOm51bGx9.r_XEmf3i6kVDdWO01TrU-Z-PI39MzoK7oH43pIIBfMk&zhida_source=entity)。
- **顶级域名服务器（TLD DNS Server）**：
  管理具体顶级域名（如 `.com`），提供[权威 DNS 服务器](https://zhida.zhihu.com/search?content_id=253734413&content_type=Article&match_order=1&q=权威+DNS+服务器&zd_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJ6aGlkYV9zZXJ2ZXIiLCJleHAiOjE3NDEzMzI2OTEsInEiOiLmnYPlqIEgRE5TIOacjeWKoeWZqCIsInpoaWRhX3NvdXJjZSI6ImVudGl0eSIsImNvbnRlbnRfaWQiOjI1MzczNDQxMywiY29udGVudF90eXBlIjoiQXJ0aWNsZSIsIm1hdGNoX29yZGVyIjoxLCJ6ZF90b2tlbiI6bnVsbH0.RwIbpFjiJ2SH_SUaAOeJDbykXQNALeeEDrsogugivPs&zhida_source=entity)的地址。
- **权威 DNS 服务器（Authoritative DNS Server）**：
  存储具体域名（如 `baidu.com`）的解析记录。
- **[递归解析器](https://zhida.zhihu.com/search?content_id=253734413&content_type=Article&match_order=1&q=递归解析器&zd_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJ6aGlkYV9zZXJ2ZXIiLCJleHAiOjE3NDEzMzI2OTEsInEiOiLpgJLlvZLop6PmnpDlmagiLCJ6aGlkYV9zb3VyY2UiOiJlbnRpdHkiLCJjb250ZW50X2lkIjoyNTM3MzQ0MTMsImNvbnRlbnRfdHlwZSI6IkFydGljbGUiLCJtYXRjaF9vcmRlciI6MSwiemRfdG9rZW4iOm51bGx9.1S6hWem_vuXEFJ1eMX1gmH7pt3MUPz1-LkmMqtAmrao&zhida_source=entity)（Recursive Resolver）**：
  通常由 ISP 或第三方提供，负责与上述服务器交互并返回结果给用户。

用户向递归解析器查询是递归过程，递归解析器再向其他服务器逐步查询的过程则是迭代查询。

什么是递归解析和[迭代解析](https://zhida.zhihu.com/search?content_id=253734413&content_type=Article&match_order=1&q=迭代解析&zd_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJ6aGlkYV9zZXJ2ZXIiLCJleHAiOjE3NDEzMzI2OTEsInEiOiLov63ku6Pop6PmnpAiLCJ6aGlkYV9zb3VyY2UiOiJlbnRpdHkiLCJjb250ZW50X2lkIjoyNTM3MzQ0MTMsImNvbnRlbnRfdHlwZSI6IkFydGljbGUiLCJtYXRjaF9vcmRlciI6MSwiemRfdG9rZW4iOm51bGx9.ySeGMAb_1-IdJqmvAmLL8IslNuGfZjIame7TLRPWXBM&zhida_source=entity)？

- **递归解析**：
  好比你问朋友问题，朋友承诺帮你找到答案，无论需要问多少人，最后都直接告诉你。这是递归解析，由递归解析器完成。
- **迭代解析**：
  如果换成你自己动手查，先问根 DNS 服务器，再根据提示逐步问下一级服务器，直到找到答案。这是迭代解析，主动查询每一级服务器。

实际应用场景：递归与迭代的结合

1. **递归查询的应用**：
   - 用户通过本地的递归解析器完成 DNS 查询，减少复杂度和操作时间。
   - 例如，配置家庭路由器或主机的 DNS 服务器为 `223.5.5.5`，递归解析器自动完成所有的查询步骤。

1. **迭代查询的应用**：
   - 运维人员使用迭代查询逐步排查 DNS 问题，例如验证某一级 DNS 服务器是否正常工作。
   - 适用于故障排查、性能优化等场景。

1. **递归与迭代结合的应用**：
   - 用户日常上网时使用递归查询，简化访问过程。
   - 运维场景中，递归查询可以快速发现问题，而迭代查询可以逐级定位到具体的故障节点。



## 域名注册平台

### NameSilo

>提醒：因为需要预先充值至少 10 美金金额才能购买域名，所以放弃此平台。

登录信息如下：

- 登录地址：`https://www.namesilo.com`
- 帐号：`d...l...1`
- 注册邮箱：`qq邮箱`
- 密码：`Kl123`



### NameCheap

登录信息如下：

- 登录地址：`https://www.namecheap.com`
- 帐号：`d...l...`
- 注册邮箱：`谷歌邮箱`
- 密码：`Kl123`



## 通过 ICANN 查询域名注册信息

### 什么是 ICANN？

ICANN（The Internet Corporation for Assigned Names and Numbers），即互联网名称与数字地址分配机构，是一个非营利性的国际组织，以下是关于ICANN的详细介绍：

一、成立背景与目的

- **成立背景**：Internet起源于美国，在90年代之前一直是一个为军事、科研服务的网络。随着Internet的全球性发展，越来越多的国家对由美国独自对Internet进行管理的方式表示不满，强烈呼吁对Internet的管理进行改革。
- **成立目的**：为了顺应推进互联网商业化进程的需要，美国政府决定成立一个由私营部门主导，同时受美国政府监督与管理的“非营利机构”ICANN，来接管包括管理域名和IP地址的分配等与互联网相关的任务。

二、发展历程

- 1998年10月，ICANN正式成立，是一个集合了全球网络界商业、技术及学术各领域专家的非营利性国际组织。
- 2009年10月2日，ICANN已获准独立于美国政府之外，取得独立地位。
- 2016年10月1日（美国东部时间），美国商务部下属机构国家电信和信息局把互联网域名管理权完全交给位于加利福尼亚州的ICANN，两者之间的授权管理合同在10月1日自然失效，不再续签。这标志着互联网迈出走向全球共治的重要一步。

三、机构职能

- **IP地址分配**：ICANN负责在全球范围内对互联网协议（IP）地址的空间进行分配。
- **域名系统管理**：
  - **通用顶级域名（gTLD）管理**：如“.com”、“.org”等域名的管理。
  - **国家和地区顶级域名（ccTLD）管理**：如“.us”（美国）、“.uk”（英国）等国家特定域名的管理。
  - **域名争议解决**：提供解决域名争议的方法，如UDRP（统一域名争议解决政策），帮助解决涉及商标或网络侵占的冲突。
- **根服务器系统管理**：ICANN负责管理根服务器系统，确保全球DNS系统的不间断运行。

四、组织架构与支持组织

- **核心权利机构**：ICANN理事会是其核心权利机构，共由19位理事组成，包括9位At-Large理事、9位来自ICANN三个支持组织提名的理事（每家3名），和1位总裁。
- **支持组织**：
  - **地址支持组织（ASO）**：负责IP地址系统的管理。
  - **域名支持组织（已更名为通用名称支持组织GNSO）**：负责互联网上的域名系统（DNS）的管理，负责制定与通用顶级域（如“.com”和“.org”）相关的政策。
  - **协议支持组织（PSO）**：负责涉及Internet协议的唯一参数的分配，此协议是允许计算机在因特网上相互交换信息、管理通讯的技术标准。

此外，ICANN还设有政府咨询委员会（GAC）、一般会员咨询委员会（ALAC）、安全和稳定咨询委员会（SSAC）、根服务器系统咨询委员会（RSSAC）等多个组织和委员会，以协助其进行互联网管理和协调工作。

五、工作方法

- **决策过程**：ICANN的决策过程包括政府、企业、技术专家和公众的参与，确保互联网作为全球资源能够服务所有用户，而不至于被某一方过度控制。
- **域名注册管理**：ICANN通过认证域名注册商来监督域名注册过程。注册域名时，需要提供联系和所有权信息，并支付注册商费用。这些信息保存在域名注册数据库中，并可以通过注册商的控制面板进行更新和控制。

六、主要成就与合作

- ICANN在维护互联网运行的稳定性、促进竞争、广泛代表全球互联网组织以及通过自下而上和基于一致意见的程序制定与其使命相一致的政策方面取得了显著成就。
- ICANN还与其他国际组织进行合作，如与联合国教科文组织携手推动互联网语言多样性改善，支持用户在互联网域名系统中（DNS）安全使用其他脚本和语言，包括访问和在线通信的关键域名和电子邮件地址。

综上所述，ICANN在互联网管理和协调方面扮演着至关重要的角色，其工作涉及IP地址分配、域名系统管理以及根服务器系统管理等多个方面。通过其组织架构和支持组织的共同努力，ICANN致力于维护全球互联网的稳定、安全和统一。



### ICANN 和 IANA 关系

ICANN（互联网名称与数字地址分配机构）和IANA（互联网号码分配局）在互联网管理和协调方面各自扮演着重要的角色，以下是关于两者的详细介绍和对比：

ICANN

1. 定义与性质：
   - ICANN是一家非营利组织，主要负责管理和协调互联网协议（IP）地址和域名系统（DNS）的稳定、安全以及统一。
2. 主要职能：
   - 管理和维护域名根区文件，确保域名的唯一性和全球可访问性。
   - 认证和管理域名注册商，监督域名注册过程。
   - 制定与域名、IP地址和其他互联网资源相关的政策。
   - 参与互联网协议的制定和协调工作。
3. 组织架构：
   - ICANN包括董事会、支持组织（SO）和咨询委员会（AC），实行自下而上的民主决策机制。
4. 发展历史：
   - ICANN成立于1998年，接替了IANA在互联网资源管理方面的职能。
   - 2016年，美国商务部将IANA的职能管理权移交给ICANN，标志着ICANN在互联网治理方面取得了更大的自主权。

IANA

1. 定义与性质：
   - IANA是一个负责全球IP地址分配、域名系统管理以及许多协议参数协调的重要组织。它是互联网的关键架构部分之一，类似于一名“网络中介”，确保全球互联网资源的协调性和唯一性。
2. 主要职能：
   - 负责全球IP地址空间的分配，确保设备能够连接和通信。
   - 管理DNS的根区，包括顶级域（如“.com”、“.org”）和国家代码顶级域（如“.cn”、“.us”）。
   - 维护和管理多种互联网协议的参数，确保全球互联网设备之间的兼容性。
3. 与ICANN的关系：
   - 在ICANN成立之前，IANA是负责管理DNS根域名系统以及全球IP地址分配的组织。
   - ICANN成立后，接管了IANA的职能，但IANA在实际运作中保持了相对独立性，负责具体的技术执行工作。
   - 目前，IANA作为ICANN的一个分支机构（也称为PTI），继续在互联网资源管理方面发挥着重要作用。

对比与分析

1. 角色定位：
   - ICANN是一个更广泛、更综合的组织，负责互联网名称和数字地址的整体管理和协调工作。
   - IANA则更专注于具体的技术执行工作，如IP地址分配、DNS根区管理等。
2. 决策机制：
   - ICANN采用自下而上的民主决策机制，涉及政府、企业、技术专家和公众的广泛参与。
   - IANA则按照已商定的政策和原则执行职能，不直接制定政策。
3. 发展趋势：
   - 随着互联网的不断发展和全球化进程的加速，ICANN在互联网治理方面的作用将越来越重要。
   - IANA作为ICANN的一个重要组成部分，也将继续在互联网资源管理方面发挥关键作用。

综上所述，ICANN和IANA在互联网管理和协调方面各自承担着不同的职责和角色。ICANN负责整体的管理和协调工作，而IANA则专注于具体的技术执行工作。两者相互协作、共同推动互联网的稳定、安全和统一发展。



### 用法

访问 `https://lookup.icann.org/en/lookup`

输入 `baidu.com` 以查询域名的注册信息。



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

