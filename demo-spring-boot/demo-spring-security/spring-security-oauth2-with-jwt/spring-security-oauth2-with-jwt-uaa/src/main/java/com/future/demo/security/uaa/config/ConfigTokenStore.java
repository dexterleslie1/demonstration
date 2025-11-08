package com.future.demo.security.uaa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Configuration
public class ConfigTokenStore {

    // 使用JwtAccessTokenConverter转换器转jwt token
    @Bean
    TokenStore tokenStore() throws InvalidKeySpecException, NoSuchAlgorithmException {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    // 访问令牌JWT转换器
    @Bean
    JwtAccessTokenConverter jwtAccessTokenConverter() throws NoSuchAlgorithmException, InvalidKeySpecException {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();

//        // JWT非对称加密模式制定私钥和公钥
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        byte[] publicKeyBytes = Base64.decode("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDPsWeWhr9oydNQYesOrVKLD+Hf\n" +
//                "scpOKiakvjX/Oh7V1v1LojTDpK3G8yt7kOHfD7rn3Tdjid46RQ6avHXmPJniqtg9\n" +
//                "pqnAMmXqB+9HUcrqxh0k7cL6roD07xuEVNZfQ/MjTfMAJKejR99De3mrG+bCfrxe\n" +
//                "kwzIBgOppRSuanjK2wIDAQAB");
//        byte[] privateKeyBytes = Base64.decode("MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAM+xZ5aGv2jJ01Bh\n" +
//                "6w6tUosP4d+xyk4qJqS+Nf86HtXW/UuiNMOkrcbzK3uQ4d8PuufdN2OJ3jpFDpq8\n" +
//                "deY8meKq2D2mqcAyZeoH70dRyurGHSTtwvqugPTvG4RU1l9D8yNN8wAkp6NH30N7\n" +
//                "easb5sJ+vF6TDMgGA6mlFK5qeMrbAgMBAAECgYAqg27n0gdGROHbd1+tLm9SBds/\n" +
//                "dd4qZ9hnKoRVDSmYrhxFKhvQ3Fmx+r6w2XRSu56PramT13nExbP6mo8rpMX+0DJp\n" +
//                "RUVAqfSdEedRXVAwSO92dirBWoBSuYLNNDJ7f1upq3NSSLqcgfo7dVH4Lgl0yZWe\n" +
//                "dkqonAFebGha8YK6uQJBAPoBgkPgD2m/yvW4fhBMvzd6kkLt12fRUJC5CZ+x91eq\n" +
//                "9YJjpQWpPVkOrAlD5ybZzlwweDL0Pkf/9KZ9oKc7Uy0CQQDUrC/Ra/mBbOjiYmLO\n" +
//                "PUaw5OERXKzcSOnQuH4MEmDTvO3JyRF/WK0Ge5stBotWZoh8fx0DFRrgMVh72xjy\n" +
//                "qvsnAkEAssEfbf6nppn+uWC3qlnlovpd18MNcGqmK0RSkD+ENcfEEP3EQV73wVSP\n" +
//                "R3Soswuq1BnH587hNUPanqxWkRwG5QJASFbZXQ6xK8jz3i1BFo3ZQcpYlCNF2Rgk\n" +
//                "EA7xMQH/VYZqC70M6pgrIo1g1wvm0VjHDDHgmG/RWHjwdBCuh7yI+QJBAOLk5wqs\n" +
//                "4I/N5RGaOwCJmZBRp+DM8NOye50avbSNxfmg9NU+3svVcfseN/XSu8Kwl7rRoIxA\n" +
//                "JqLurr5RMLwwvPo=");
//        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
//        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
//        KeyPair keyPair = new KeyPair(publicKey, privateKey);
//        jwtAccessTokenConverter.setKeyPair(keyPair);

        // JWT对称加密模式指定密钥
        jwtAccessTokenConverter.setSigningKey("123456");

        return jwtAccessTokenConverter;
    }

}
