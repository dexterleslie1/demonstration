package com.future.demo;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSATests {
    /**
     * 测试非对称密码算法加密和解密
     *
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    @Test
    public void testEncryptDecrypt() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        String plainText = RandomStringUtils.random(16);

        String algorithm = "RSA";
        String plainSecretKey = RandomStringUtils.random(2048);
        int keySize = 512;

        // 生成秘钥对
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize, new SecureRandom(plainSecretKey.getBytes()));
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();

        // 私钥加密，公钥解密
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        // PKCS#8标准中定义的一个结构，用于表示私钥信息。它包含一个版本字段、一个私钥算法标识符、一个私钥字段以及一个可选的属性字段。这个结构允许私钥以独立于公钥或证书的格式进行存储和传输
        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] encryptBytes = cipher.doFinal(plainText.getBytes());

        keyFactory = KeyFactory.getInstance(algorithm);
        // 于将X.509证书中的公钥转换为Java的公钥对象
        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
        String plainTextDecrypt = new String(decryptBytes);
        Assert.assertEquals(plainText, plainTextDecrypt);

        // 公钥加密，私钥解密
        keyFactory = KeyFactory.getInstance(algorithm);
        publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        encryptBytes = cipher.doFinal(plainText.getBytes());

        keyFactory = KeyFactory.getInstance(algorithm);
        privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        decryptBytes = cipher.doFinal(encryptBytes);
        plainTextDecrypt = new String(decryptBytes);
        Assert.assertEquals(plainText, plainTextDecrypt);
    }

    /**
     * 测试读取OpenSSL公钥和私钥密钥对
     *
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    @Test
    public void testOpenSSLKeyPair() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        String publicKeyString = System.getenv("publicKey");
        String privateKeyString = System.getenv("privateKey");

        String plainText = RandomStringUtils.random(16);

        String algorithm = "RSA";

        byte[] privateKeyBytes = Base64.decode(privateKeyString);
        byte[] publicKeyBytes = Base64.decode(publicKeyString);

        // 私钥加密，公钥解密
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] encryptBytes = cipher.doFinal(plainText.getBytes());

        keyFactory = KeyFactory.getInstance(algorithm);
        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
        String plainTextDecrypt = new String(decryptBytes);
        Assert.assertEquals(plainText, plainTextDecrypt);

        // 公钥加密，私钥解密
        keyFactory = KeyFactory.getInstance(algorithm);
        publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        encryptBytes = cipher.doFinal(plainText.getBytes());

        keyFactory = KeyFactory.getInstance(algorithm);
        privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        decryptBytes = cipher.doFinal(encryptBytes);
        plainTextDecrypt = new String(decryptBytes);
        Assert.assertEquals(plainText, plainTextDecrypt);
    }

    /**
     * 测试RSA数字签名和验签
     *
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    @Test
    public void testDigitalSignAndVerify() throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        String publicKeyString = System.getenv("publicKey");
        String privateKeyString = System.getenv("privateKey");

        String plainText = RandomStringUtils.random(16);

        byte[] privateKeyBytes = Base64.decode(privateKeyString);
        byte[] publicKeyBytes = Base64.decode(publicKeyString);

        // 签名
        PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyf = KeyFactory.getInstance("RSA");
        PrivateKey priKey = keyf.generatePrivate(priPKCS8);
        java.security.Signature signature = java.security.Signature.getInstance("SHA256WithRSA");
        signature.initSign(priKey);
        signature.update(plainText.getBytes());
        byte[] signed = signature.sign();
        String contentSigned = Base64.encode(signed);

        // 签名验证
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        signature = java.security.Signature.getInstance("SHA256WithRSA");
        signature.initVerify(pubKey);
        // 使用公钥验证数字签名是否是对应的密钥签发的
        signature.update(plainText.getBytes());
        boolean bverify = signature.verify(Base64.decode(contentSigned));
        Assert.assertTrue(bverify);
    }
}
