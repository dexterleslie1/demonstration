package com.future.demo;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class JWTTests {

    /**
     * 测试使用RSA签名和验签
     *
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
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

    /**
     * 测试使用HMAC256签名和验签
     */
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

    /**
     * 测试使用claim保存权限信息
     */
    @Test
    public void testStoreAuthorizationWithClaim() throws Exception {
        String publicKeyString = System.getenv("publicKey");
        String privateKeyString = System.getenv("privateKey");

        byte[] privateKeyBytes = com.sun.org.apache.xerces.internal.impl.dv.util.Base64.decode(privateKeyString);
        byte[] publicKeyBytes = com.sun.org.apache.xerces.internal.impl.dv.util.Base64.decode(publicKeyString);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        Algorithm algorithm = Algorithm.RSA512(null, privateKey);

        List<HashMap<String, String>> roleList = new ArrayList<>();
        roleList.add(new HashMap<String, String>() {{
            put("code", "role1");
            put("name", "角色1");
        }});
        roleList.add(new HashMap<String, String>() {{
            put("code", "role2");
            put("name", "角色2");
        }});

        List<Map<String, Object>> menuList = new ArrayList<>();
        Map<String, Object> menu1Node = new HashMap<String, Object>() {{
            put("code", "menu1");
            put("name", "菜单1");
        }};
        Map<String, Object> menu2Node = new HashMap<String, Object>() {{
            put("code", "menu2");
            put("name", "菜单2");
        }};
        menuList.add(menu1Node);
        menuList.add(menu2Node);

        List<Map<String, String>> authorityList = new ArrayList<>();
        authorityList.add(new HashMap<String, String>() {{
            put("code", "authority1-1");
            put("name", "功能1-1");
        }});
        authorityList.add(new HashMap<String, String>() {{
            put("code", "authority1-2");
            put("name", "功能1-2");
        }});
        menu1Node.put("authorityList", authorityList);

        authorityList = new ArrayList<>();
        authorityList.add(new HashMap<String, String>() {{
            put("code", "authority2-1");
            put("name", "功能2-1");
        }});
        authorityList.add(new HashMap<String, String>() {{
            put("code", "authority2-2");
            put("name", "功能2-2");
        }});
        menu2Node.put("authorityList", authorityList);


        Long userId = 12345678l;
        String loginname = "ak123456";

        String token = JWT.create()
                // payload
                .withClaim("userId", userId)
                .withClaim("loginname", loginname)
                .withClaim("roleList", roleList)
                .withClaim("menuList", menuList)
                .sign(algorithm);
        Assert.assertNotNull(token);

        String[] tokenArray = token.split("\\.");
        String tokenHeader = tokenArray[0];
        String tokenHeaderDecode = new String(Base64.getUrlDecoder().decode(tokenHeader), StandardCharsets.UTF_8);
        String tokenPayload = tokenArray[1];
        String tokenPayloadDecode = new String(Base64.getUrlDecoder().decode(tokenPayload), StandardCharsets.UTF_8);
        String tokenSignature = tokenArray[2];
        Assert.assertEquals("{\"typ\":\"JWT\",\"alg\":\"RS512\"}", tokenHeaderDecode);
        Assert.assertEquals("{\"menuList\":[{\"code\":\"menu1\",\"name\":\"菜单1\",\"authorityList\":[{\"code\":\"authority1-1\",\"name\":\"功能1-1\"},{\"code\":\"authority1-2\",\"name\":\"功能1-2\"}]},{\"code\":\"menu2\",\"name\":\"菜单2\",\"authorityList\":[{\"code\":\"authority2-1\",\"name\":\"功能2-1\"},{\"code\":\"authority2-2\",\"name\":\"功能2-2\"}]}],\"loginname\":\"ak123456\",\"roleList\":[{\"code\":\"role1\",\"name\":\"角色1\"},{\"code\":\"role2\",\"name\":\"角色2\"}],\"userId\":12345678}", tokenPayloadDecode);
        Assert.assertNotNull(tokenSignature);

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
}
