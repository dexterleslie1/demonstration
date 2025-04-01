package com.future.demo;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class DES3Tests {
    /**
     * 测试DES3密码算法的加密和解密
     *
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    @Test
    public void testDES3EncryptDecrypt() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        String plainText = RandomStringUtils.random(256);

        String algorithm = "DESede";
        String plainSecretKey = RandomStringUtils.random(1024);

        // 生成秘钥
        KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
        keyGenerator.init(new SecureRandom(plainSecretKey.getBytes()));
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] secretKeyBytes = secretKey.getEncoded();

        // 加密
        secretKey = new SecretKeySpec(secretKeyBytes, algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptBytes = cipher.doFinal(plainText.getBytes());

        // 解密
        secretKey = new SecretKeySpec(secretKeyBytes, algorithm);
        cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] plainBytes = cipher.doFinal(encryptBytes);

        // 原来的明文能够匹配被加密和解密后的明文
        String plainTextDecrypt = new String(plainBytes);
        Assert.assertEquals(plainText, plainTextDecrypt);
    }
}
