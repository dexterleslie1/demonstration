# 密码学

## 密码加密和解密算法分类

密码加密和解密算法主要可以分为以下三类：

1. 对称密码算法（Symmetric-key Algorithm）
   - 对称算法是指加密秘钥和解密秘钥相同的密码算法，又称为秘密秘钥算法或单密钥算法。
   - 该算法又分为分组密码算法（Block cipher）和流密码算法（Stream cipher）。
   - 常见的分组密码算法包括 AES、SM1（国密）、SM4（国密）、DES、3DES、IDEA、RC2 等。
   - 常见的流密码算法包括 RC4 等。
   - 其中，AES 是目前安全强度较高、应用范围较广的对称加密算法。
2. 非对称密码算法（Asymmetric-key Algorithm）
   - 非对称算法是指加密秘钥和解密秘钥不同的密码算法，又称为公开密码算法或公钥算法。
   - 加密秘钥可以公开，又称为公钥；解密秘钥必须保密，又称为私钥。
   - 常见非对称算法包括 RSA、SM2（国密）、DH、DSA、ECDSA、ECC 等。
3. 摘要算法（Digest Algorithm）
   - 摘要算法是指把任意长度的输入消息数据转化为固定长度的输出数据的一种密码算法，又称为散列函数、哈希函数、杂凑函数、单向函数等。
   - 常见的摘要算法包括 MD（Message Digest，消息摘要算法）、SHA-1（Secure Hash Algorithm，安全散列算法）、SM3（国密标准）等。
   - 摘要算法无秘钥，通常用来做数据完整性的判定，即对数据进行哈希计算然后比较摘要值是否一致。

这三类算法在密码学中各有其应用场景，如对称密码算法通常用于保护数据的机密性，非对称密码算法则常用于数字签名和密钥交换，而摘要算法则用于验证数据的完整性。





JWT 原理
https://www.jianshu.com/p/8c89683546ee
https://blog.csdn.net/xunileida/article/details/82961714

JAVA RSA
https://blog.csdn.net/u013314786/article/details/80324461

JAVA RSA 签名校验
https://www.cnblogs.com/demodashi/p/8458113.html

使用java-jwt库验证RSA签名
https://stackoverflow.com/questions/49693409/verify-signature-using-jwt-java-jwt

使用JWT实现统一登录
https://www.cnblogs.com/zhenghongxin/archive/2018/11/23/10006697.html



## 生成JWT RSA钥匙步骤

```
# openSSL生成私钥，NOTE: 这个钥匙不能用于作为应用的privateKey，需要使用下面的pkcs8格式作为privateKey
openssl genrsa -out private.pem 1024

# openSSL生成公钥
openssl rsa -in private.pem -outform PEM -pubout -out public.pem

# openSSL转换公钥PEM到PKCS8格式，NOTE: 这个钥匙作为privateKey
# https://stackoverflow.com/questions/8290435/convert-pem-traditional-private-key-to-pkcs8-private-key
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in private.pem -out private.pem.pkcs8
```



JAVA读取private、public key文件
https://stackoverflow.com/questions/11787571/how-to-read-pem-file-to-get-private-and-public-key