package com.future.demo.robot.detection;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Const {
    public final static ObjectMapper OMInstance = new ObjectMapper();

    public final static String CahceNameEhcacheEnable = "cacheEnable";
    public final static String CacheNameEhcacheWhitelist = "cacheWhitelist";
    public final static String CacheNameEhcacheRequestCounter = "cacheRequestCounter";

    public final static String CacheKeyEnable = "enable";
    public final static String CacheKeyPrefixWhitelist = "whitelistprefex#";

//    public final static String CacheNameCaptchaPrefix = "cacheCaptcha#";
//    public final static List<String> CacheSuffixList = new ArrayList<>();
//    private final static int CacheSuffixListLength = 100;
//    private final static Random RANDOM = new Random();
//    static {
//        for(int i=1; i<=CacheSuffixListLength; i++) {
//            CacheSuffixList.add(String.valueOf(i));
//        }
//    }
//
//    /**
//     * 获取redis缓存后缀
//     * @return
//     */
//    public static String getCacheSuffix() {
//        int randomIndex = RANDOM.nextInt(CacheSuffixListLength);
//        return CacheSuffixList.get(randomIndex);
//    }
}
