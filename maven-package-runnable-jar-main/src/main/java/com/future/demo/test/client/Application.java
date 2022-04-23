package com.future.demo.test.client;

import org.apache.commons.codec.binary.Base64;

import java.util.Random;

public class Application {
    public static void main(String args[]) {
        byte []datumBytes = new byte[32];
        Random varRandom = new Random();
        varRandom.nextBytes(datumBytes);
        String varStr = Base64.encodeBase64String(datumBytes);
        System.out.println("调用apache commons-codec Base64.encodeBase64String方法得到的base64：" + varStr);
    }
}
