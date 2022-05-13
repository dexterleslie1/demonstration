package com.future.demo.robot.detection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ramostear.captcha.common.Fonts;
import com.ramostear.captcha.core.Captcha;
import com.ramostear.captcha.support.CaptchaType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class CaptchaCacheService {
    public final static String CacheKeyCaptchaIndexPrefix = "captchaIndex#";
    public final static String CacheKeyCaptchaPrefix = "captcha#";
    public final static Integer CacheCaptchaIndexCount = 100;
    public final static Integer CaptchaTimeoutInSeconds = 3600;

    private final Random random = new Random();

    @Autowired
    JedisPool jedisPool;

    public CaptchaEntry get() {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();

            // 随机分配验证码索引key
            String randomKeyIndex = getRandomIndexKey();

            // 在验证码索引中随机返回一个验证码
            // redis set数据结构
            String randomKey = jedis.srandmember(randomKeyIndex);
            if (StringUtils.isEmpty(randomKey)) {
                return null;
            }

            // 根据randomKey读取验证码
            String JSON = jedis.get(randomKey);
            CaptchaEntry entry = null;
            if (!StringUtils.isEmpty(JSON)) {
                try {
                    entry = Const.OMInstance.readValue(JSON, CaptchaEntry.class);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);

                }
            }
            return entry;
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    public void init() {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();

            String randomKeyIndex = getRandomIndexKey();
            List<String> keyIndexList = listIndexKeys();

            // 缓存中验证码个数不足需要补充
            long length = jedis.scard(randomKeyIndex);
            int maximumEntries = 100000;
            if (length < maximumEntries) {
                int count = maximumEntries - (int) length;
                for (int i = 0; i < count; i++) {
                    Captcha captcha = getCaptcha();
                    String code = captcha.getCaptchaCode();
                    String imageBase64 = captcha.toBase64();

                    String captchaId = CacheKeyCaptchaPrefix + UUID.randomUUID().toString();
                    CaptchaEntry entry = new CaptchaEntry();
                    entry.setId(captchaId);
                    entry.setCode(code);
                    entry.setImageBase64(imageBase64);
                    entry.setCreateTime(new Date());

                    Jedis finalJedis = jedis;
                    keyIndexList.forEach(key -> finalJedis.sadd(key, captchaId));
                    try {
                        String JSON = Const.OMInstance.writeValueAsString(entry);
                        jedis.setex(captchaId, CaptchaTimeoutInSeconds, JSON);
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    public void refresh() {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();

            String randomKeyIndex = getRandomIndexKey();
            List<String> keyIndexList = listIndexKeys();

            // 找出过期的验证码并更新
            Set<String> captchaEntryKeyList = jedis.smembers(randomKeyIndex);
            if(captchaEntryKeyList != null && captchaEntryKeyList.size() > 0) {
                AtomicInteger count = new AtomicInteger();

                Jedis finalJedis = jedis;
                captchaEntryKeyList.forEach(key -> {
                    String JSON = finalJedis.get(key);
                    if(StringUtils.isEmpty(JSON)) {
                        keyIndexList.forEach(keyTemporary -> finalJedis.srem(keyTemporary, key));
                    } else {
                        try {
                            CaptchaEntry entry = Const.OMInstance.readValue(JSON, CaptchaEntry.class);
                            String captchaId = entry.getId();
                            Date createTime = entry.getCreateTime();
                            Date timeNow = new Date();
                            if(timeNow.getTime() - createTime.getTime() > 10*60*1000) {
                                Captcha captcha = getCaptcha();
                                String code = captcha.getCaptchaCode();
                                String imageBase64 = captcha.toBase64();

                                entry = new CaptchaEntry();
                                entry.setId(captchaId);
                                entry.setCode(code);
                                entry.setImageBase64(imageBase64);
                                entry.setCreateTime(new Date());

                                JSON = Const.OMInstance.writeValueAsString(entry);
                                finalJedis.setex(captchaId, CaptchaTimeoutInSeconds, JSON);

                                count.incrementAndGet();
                            }
                        } catch (IOException e) {
                            //
                        }
                    }
                });

                if(count.get() > 0) {
                    log.debug("{}个验证码被更新", count.get());
                }
            }
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 随机获取一个验证码索引key
     *
     * @return
     */
    private String getRandomIndexKey() {
        int randomInt = random.nextInt(CacheCaptchaIndexCount);
        return CacheKeyCaptchaIndexPrefix + randomInt;
    }

    private List<String> listIndexKeys() {
        List<String> keyList = new ArrayList<>();
        for(int i=0; i<CacheCaptchaIndexCount; i++) {
            keyList.add(CacheKeyCaptchaIndexPrefix + i);
        }
        return keyList;
    }

    private final CaptchaType type = CaptchaType.DEFAULT;
    private final Font font = Fonts.getInstance().defaultFont();
    private final int width = 160;
    private final int height = 50;
    private final int captchaLength = 4;

    /**
     * 生成captcha对象
     * @return
     */
    private Captcha getCaptcha() {
        Captcha captcha = new Captcha();
        captcha.setType(type);
        captcha.setWidth(width);
        captcha.setHeight(height);
        captcha.setLength(captchaLength);
        captcha.setFont(font);
        return captcha;
    }

    @Data
    public static class CaptchaEntry {
        /**
         * 唯一标识
         */
        private String id;
        /**
         * 验证码结果
         */
        private String code;
        /**
         * 验证码图片base64
         */
        private String imageBase64;
        /**
         * 验证码创建时间
         */
        private Date createTime;
    }
}
