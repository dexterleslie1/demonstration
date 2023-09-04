## https、ssl、tls三者之间的联系和区别

> 由于HTTPS的推出受到了很多人的欢迎，在SSL更新到3.0时，IETF对SSL3.0进行了标准化，并添加了少数机制(但是几乎和SSL3.0无差异)，标准化后的IETF更名为TLS1.0(Transport Layer Security 安全传输层协议)，可以说TLS就是SSL的新版本3.1，并同时发布“RFC2246-TLS加密协议详解”，如果想更深层次的了解TLS的工作原理可以去RFC的官方网站：www.rfc-editor.org，搜索RFC2246即可找到RFC文档！ ——以上就是历史背景
>
> HTTPS其实是由两部分组成的：HTTP+TLS/SSL，即HTTP下加入TLS/SSL层，HTTPS的安全基础就是TLS/SSL。服务端和客户端的信息传输都会通过TLS/SSL进行加密，所以传输的数据都是加密之后的数据。TLS的前身就是SSL协议，因此没有特别说明TLS/SSL说的都是同一个东西。

## 什么是x.509格式

> https://www.secure128.com/blog/x509-explanation
>
> X.509 is a standard defining the format of public key certificates.An X.509 certificate is a digital certificate that uses the widely accepted international X.509 public key infrastructure (PKI) standard to verify that a public key belongs to the hostname/domain, organization, or individual contained within the certificate. The X.509 certificate is either signed by a publicly trusted (meaning browsers trust it) Certificate Authority (Like DigiCert, Sectigo, GlobalSign, etc.) or self-signed. When a certificate is signed by a trusted certificate authority, or validated by other means, someone holding that certificate can rely on the public key it contains to establish secure communications with another party, or validate documents digitally signed by the corresponding private key.

## CSR、public key、private key、数字签名、数字证书、self-signed证书理解

> https://www.jianshu.com/p/80aa37311151
> Bob想要和Pat进行通信，首先就要告知Pat自己的公钥，Bob先向Pat发送自己的数字证书，Pat收到数字证书后，会向权威的数字证书认证中心进行认证，确认是否是Bob的数字证书。（这个认证的过程，实际上也是通过公钥和私钥的机制，Pat会根据数字证书的类别，查找发布这个数字证书的中心的公钥，然后用相应的公钥对证书进行家解码，如果能正确解码则说明这个数字证书确实是此中心颁布的，然后根据解码后的信息验证是否是Bob的数字证书，最后从解码后的信息中，获取Bob的公钥）。
> 然后，Pat可以利用Bob的公钥对Bob的数字签名进行解码，验证是否是Bob的数字签名，如果能正确解码，就说明数字签名是由Bob的私钥进行加密的。然后就进行完整性的验证，将信息原文进行hash，得到信息摘要，并与数字签名解码后得到的信息摘要进行对比，如果一致，就说明信息是完整的没有被篡改的！
>
> CSR在需要为你的public key生成数字签名证书时使用，第三方CA机构需要你提供此文件签名你的publich key。
>
> CSR包含公钥、FQDN、组织信息
> https://www.thesslstore.com/blog/what-is-a-csr/

```shell
# 使用OpenSSL生成private key、public key、signed key
# https://security.stackexchange.com/questions/108508/how-do-i-produce-a-ca-signed-public-key
# 生成private key
openssl genrsa -out private.pem 2048

# 生成public key
openssl rsa -in private.pem -outform PEM -pubout -out public.pem

# 生成CSR证书
openssl req -new -key private.pem -out certificate.csr

# 生成self-signed证书
openssl x509 -req -days 365 -in certificate.csr -signkey private.pem -out certificate.crt

# 验证self-signed证书
openssl verify certificate.crt

# 查看x509证书信息，例如到期信息
openssl x509 -in certificate.crt -text -noout

# 一行命令生成private key和CSR证书
openssl req -out certificate.csr -new -nodes -newkey rsa:2048 -keyout private.pem

# 使用OpenSSL导出baidu证书
true | openssl s_client -connect www.baidu.com:443 2>/dev/null | openssl x509

# 使用OpenSSL显示证书签名链
openssl s_client -connect www.baidu.com:443 -showcerts
```

## 证书格式

> https://blog.freessl.cn/ssl-cert-format-introduce/
>
> 你可以到这里进行格式转换：https://myssl.com/cert_convert.html

### DER

> 该格式是二进制文件内容，Java 和 Windows 服务器偏向于使用这种编码格式。

```shell
# OpenSSL查看
openssl x509 -in certificate.der -inform der -text -noout

# 转换为 PEM
openssl x509 -in cert.crt -inform der -outform pem -out cert.pem
```

### PEM

> Privacy Enhanced Mail，一般为文本格式，以 -----BEGIN... 开头，以 -----END... 结尾。中间的内容是 BASE64 编码。这种格式可以保存证书和私钥，有时我们也把PEM 格式的私钥的后缀改为 .key 以区别证书与私钥。具体你可以看文件的内容。
> 这种格式常用于 Apache 和 Nginx 服务器。

```shell
# OpenSSL 查看
openssl x509 -in certificate.pem -text -noout

# 转换为 DER
openssl x509 -in cert.crt -outform der -out cert.der
```

### CRT

> Certificate 的简称，有可能是 PEM 编码格式，也有可能是 DER 编码格式。如何查看请参考前两种格式。

### PFX

> Predecessor of PKCS#12，这种格式是二进制格式，且证书和私钥存在一个 PFX 文件中。一般用于 Windows 上的 IIS 服务器。改格式的文件一般会有一个密码用于保证私钥的安全。

```shell
# OpenSSL 查看
openssl pkcs12 -in for-iis.pfx

# 转换为 PEM
openssl pkcs12 -in for-iis.pfx -out for-iis.pem -nodes
```

### JKS

> Java Key Storage，很容易知道这是 JAVA 的专属格式，利用 JAVA 的一个叫 keytool 的工具可以进行格式转换。一般用于 Tomcat 服务器。

## 证书申请

> freessl.org申请免费key、dns.com申请域名
> https://www.cnblogs.com/xtxtx/p/12097177.html



## 生成SSH public和private key

> http://lunar.lyris.com/help/lm_help//11.3/Content/generating_public_and_private_keys.html

```
# 生成私钥
openssl genrsa -out private.key 1024

# 生成PEM格式公钥
openssl rsa -in private.key -out public.key -pubout -outform PEM

# 如果需要OPENSSH格式公钥，使用下面命令生成
# https://security.stackexchange.com/questions/32768/converting-keys-between-openssl-and-openssh
ssh-keygen -f private.key -y > public.key
```

