# `jwt`用法

> [JWT 原理1](https://www.jianshu.com/p/8c89683546ee)
>
> [JWT 原理2](https://blog.csdn.net/xunileida/article/details/82961714)
>
> [使用java-jwt库验证RSA签名](https://stackoverflow.com/questions/49693409/verify-signature-using-jwt-java-jwt)
>
> [使用JWT实现统一登录](https://www.cnblogs.com/zhenghongxin/archive/2018/11/23/10006697.html)
>
> [JAVA读取private、public key文件](https://stackoverflow.com/questions/11787571/how-to-read-pem-file-to-get-private-and-public-key)

## 公钥和私钥的生成和使用

openSSL生成私钥，注意：这个钥匙不能用于作为应用的privateKey，需要使用下面的pkcs8格式作为privateKey

```bash
openssl genrsa -out private.pem 1024
```

openSSL生成公钥

```bash
openssl rsa -in private.pem -outform PEM -pubout -out public.pem
```

openSSL转换公钥PEM到PKCS8格式，注意：这个钥匙作为privateKey

> [参考链接](https://stackoverflow.com/questions/8290435/convert-pem-traditional-private-key-to-pkcs8-private-key)

```bash
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in private.pem -out private.pem.pkcs8
```

注意：使用公钥和密钥时，只需要复制除`-----xxx-----`外的内容即可，否则`java`在解析钥匙时报告错误。

## `HMAC256`算法

> 签名和验签使用同一个密码。
>
> 示例详细用法请参考 [链接](https://github.com/dexterleslie1/demonstration/blob/master/demo-encrypt-decrypt/src/test/java/com/future/demo/JWTTests.java#L86)

示例代码：

```java
@Test
public void testSignWithHMAC256AndVerify() {
    String secret = "123456";

    Long userId = 12345678l;
    String loginname = "ak123456";

    Map<String, Object> headerMap = new HashMap<>();
    headerMap.put("alg", "HS256");
    String token = JWT.create()
        // header
        .withHeader(headerMap)
        // payload
        .withClaim("userId", userId)
        .withClaim("loginname", loginname)
        // 使用密码创建HMAC256密码算法对象
        .sign(Algorithm.HMAC256(secret));

    String[] tokenArray = token.split("\\.");
    String tokenHeader = tokenArray[0];
    String tokenHeaderDecode = new String(Base64.getUrlDecoder().decode(tokenHeader), StandardCharsets.UTF_8);
    String tokenPayload = tokenArray[1];
    String tokenPayloadDecode = new String(Base64.getUrlDecoder().decode(tokenPayload), StandardCharsets.UTF_8);
    String tokenSignature = tokenArray[2];
    Assert.assertEquals("{\"typ\":\"JWT\",\"alg\":\"HS256\"}", tokenHeaderDecode);
    Assert.assertEquals("{\"loginname\":\"ak123456\",\"userId\":12345678}", tokenPayloadDecode);
    Assert.assertNotNull(tokenSignature);

    // 使用密码创建HMAC256密码算法对象
    Algorithm algorithm = Algorithm.HMAC256(secret);
    JWTVerifier jwtVerifier = JWT.require(algorithm).build();
    DecodedJWT decodedJWT = jwtVerifier.verify(token);
    Map<String, Claim> claimMap = decodedJWT.getClaims();

    Long claimUserId = claimMap.get("userId").asLong();
    String claimLoginname = claimMap.get("loginname").asString();
    Assert.assertEquals(userId, claimUserId);
    Assert.assertEquals(loginname, claimLoginname);

    Date issueAt = decodedJWT.getIssuedAt();
    Date expireAt = decodedJWT.getExpiresAt();
    Assert.assertNull(issueAt);
    Assert.assertNull(expireAt);
}
```



## `RSA512`算法

> 签名使用私钥，验签使用公钥。
>
> 示例详细用法请参考 [链接](https://github.com/dexterleslie1/demonstration/blob/master/demo-encrypt-decrypt/src/test/java/com/future/demo/JWTTests.java#L29)

示例代码：

```java
@Test
public void testSignWithRSAAndVerify() throws NoSuchAlgorithmException, InvalidKeySpecException {
    String publicKeyString = System.getenv("publicKey");
    String privateKeyString = System.getenv("privateKey");

    byte[] privateKeyBytes = com.sun.org.apache.xerces.internal.impl.dv.util.Base64.decode(privateKeyString);
    byte[] publicKeyBytes = com.sun.org.apache.xerces.internal.impl.dv.util.Base64.decode(publicKeyString);

    // 创建公钥和密钥对应的java对象并使用私钥创建rsa512密码算法对象
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
    RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
    Algorithm algorithm = Algorithm.RSA512(null, privateKey);

    Long userId = 12345678l;
    String loginname = "ak123456";

    // 生成jwt
    String token = JWT.create()
        // payload
        .withClaim("userId", userId)
        .withClaim("loginname", loginname)
        .sign(algorithm);
    Assert.assertNotNull(token);

    // 验证jwt数据
    String[] tokenArray = token.split("\\.");
    String tokenHeader = tokenArray[0];
    String tokenHeaderDecode = new String(Base64.getUrlDecoder().decode(tokenHeader), StandardCharsets.UTF_8);
    String tokenPayload = tokenArray[1];
    String tokenPayloadDecode = new String(Base64.getUrlDecoder().decode(tokenPayload), StandardCharsets.UTF_8);
    String tokenSignature = tokenArray[2];
    Assert.assertEquals("{\"typ\":\"JWT\",\"alg\":\"RS512\"}", tokenHeaderDecode);
    Assert.assertEquals("{\"loginname\":\"" + loginname + "\",\"userId\":" + userId + "}", tokenPayloadDecode);
    Assert.assertNotNull(tokenSignature);

    // 使用公钥创建rsa512密码算法对象
    algorithm = Algorithm.RSA512(publicKey, null);
    JWTVerifier verifier = JWT.require(algorithm)
        .build();
    DecodedJWT decodedJWT = verifier.verify(token);
    Map<String, Claim> claimMap = decodedJWT.getClaims();

    Long claimUserId = claimMap.get("userId").asLong();
    String claimLoginname = claimMap.get("loginname").asString();
    Assert.assertEquals(userId, claimUserId);
    Assert.assertEquals(loginname, claimLoginname);

    Date issueAt = decodedJWT.getIssuedAt();
    Date expireAt = decodedJWT.getExpiresAt();
    Assert.assertNull(issueAt);
    Assert.assertNull(expireAt);
}
```



## 过期特性

>详细用法请参考本站 [示例](https://github.com/dexterleslie1/demonstration/blob/master/demo-encrypt-decrypt/src/test/java/com/future/demo/JWTTests.java)

```java
/**
 * 测试 jwt 过期特性
 */
@Test
public void testExpiration() throws InterruptedException {
    String secret = "123456";

    Long userId = 12345678l;
    String loginname = "ak123456";

    Date expiresAt = Date.from(LocalDateTime.now().plusSeconds(2).toInstant(ZoneOffset.ofHours(8)));
    Map<String, Object> headerMap = new HashMap<>();
    headerMap.put("alg", "HS256");
    String token = JWT.create()
            // header
            .withHeader(headerMap)
            // payload
            .withClaim("userId", userId)
            .withClaim("loginname", loginname)
            .withExpiresAt(expiresAt)
            // 使用密码创建HMAC256密码算法对象
            .sign(Algorithm.HMAC256(secret));

    // 使用密码创建HMAC256密码算法对象
    Algorithm algorithm = Algorithm.HMAC256(secret);
    JWTVerifier jwtVerifier = JWT.require(algorithm).build();
    jwtVerifier.verify(token);

    TimeUnit.SECONDS.sleep(3);
    try {
        jwtVerifier.verify(token);
        Assert.fail();
    } catch (TokenExpiredException ignored) {
        // Token 过期抛出 TokenExpiredException 异常
    }
}
```
