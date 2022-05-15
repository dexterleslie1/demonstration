package com.future.demo.robot.detection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ramostear.captcha.common.Fonts;
import com.ramostear.captcha.core.Captcha;
import com.ramostear.captcha.support.CaptchaType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPubSub;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class CaptchaCacheService {
    /**
     * 客户端id和captcha结果对照
     */
    public final static String CacheKeyCaptchaClientIdToResultPrefix = "captchaClientIdToResult#";
    /**
     * 客户端id和captcha结果对照超时
     */
    public final static Integer TimeoutInSecondsCaptchaClientIdToResult = 3600;
    /**
     * 客户端白名单超时
     */
    public final static Integer TimeoutInSecondsWhitelist = 3600;

    public final static String CacheKeyCaptchaIndexPrefix = "captchaIndex#";
    public final static String CacheKeyCaptchaPrefix = "captcha#";
    public final static Integer CacheCaptchaIndexCount = 100;
    public final static Integer CaptchaTimeoutInSeconds = 3600;

    private final static Integer CaptchaMaximumEntries = 100;
    private final static String ChannelCaptchaEvent = "channelCaptchaEvent";

    // 本地ehcache captcha索引key
    private final static String CacheKeyEhcacheCaptchaIndex = "captchaIndex";

    private final Random random = new Random();

    @Autowired
    JedisCluster jedisCluster;
    @Autowired
    CacheManagera cacheManager;

    private Cache cacheCaptcha;
    private Cache cacheCaptchaIndex;

    @PostConstruct
    public void init() {
        this.cacheCaptcha = this.cacheManager.getCache("cacheCaptcha");
        this.cacheCaptchaIndex = this.cacheManager.getCache("cacheCaptchaIndex");
    }

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final List<JedisPubSub> jedisPubSubList = new ArrayList<>();

    private final ExecutorService executorServiceCaptcha = Executors.newCachedThreadPool();

    /**
     * 为客户端分配captcha项
     * @return
     */
    public ClientCaptchaEntry assignClient() {
        CaptchaEntry entry = this.getRandomCaptcha();
        String clientId = UUID.randomUUID().toString();
        ClientCaptchaEntry clientCaptchaEntry = new ClientCaptchaEntry();
        clientCaptchaEntry.setClientId(clientId);
        clientCaptchaEntry.setEntry(entry);

        String result = StringUtils.EMPTY;
        if(entry != null) {
            result = entry.getCode();
        }
        String captchaClientIdToResultKey = CacheKeyCaptchaClientIdToResultPrefix + clientId;
        this.jedisCluster.setex(captchaClientIdToResultKey, TimeoutInSecondsCaptchaClientIdToResult, result);

        return clientCaptchaEntry;
    }

    /**
     * 验证客户端提供的captcha
     * @param clientId
     * @param clientIp
     * @param result
     */
    public void verifyClient(String clientId,
                             String clientIp,
                             String result) {
        Assert.isTrue(!StringUtils.isBlank(clientId), "没有提供客户端id参数");

        String captchaClientIdToResultKey = CacheKeyCaptchaClientIdToResultPrefix + clientId;
        String resultStore = this.jedisCluster.get(captchaClientIdToResultKey);
        if(StringUtils.isBlank(resultStore)) {
            resultStore = StringUtils.EMPTY;
        }
        if(StringUtils.isBlank(result)) {
            result = StringUtils.EMPTY;
        }

        Assert.isTrue(resultStore.equalsIgnoreCase(result), "验证码错误");

        String key = Const.CacheKeyPrefixWhitelist + clientIp;
        jedisCluster.del(captchaClientIdToResultKey);
        jedisCluster.setex(key, TimeoutInSecondsWhitelist, StringUtils.EMPTY);
    }

    /**
     * 随机获取一个验证码
     *
     * @return
     */
    private CaptchaEntry getRandomCaptcha() {
        // 在验证码索引中随机返回一个验证码
        Element element = this.cacheCaptchaIndex.get(CacheKeyEhcacheCaptchaIndex);
        List<String> captchaIdList = (List<String>) element.getObjectValue();
        if (captchaIdList.size() <= 0) {
            return null;
        }
        int randomInt = random.nextInt(captchaIdList.size());
        String captchaId = captchaIdList.get(randomInt);
        if (StringUtils.isEmpty(captchaId)) {
            return null;
        }

        // 根据randomKey读取验证码
        // TODO 测试ehcache内存压力
        element = this.cacheCaptcha.get(captchaId);
        if(element == null) {
            String JSON = jedisCluster.get(captchaId);
            if (!StringUtils.isEmpty(JSON)) {
                try {
                    CaptchaEntry entry = Const.OMInstance.readValue(JSON, CaptchaEntry.class);
                    element = new Element(captchaId, entry);
                    this.cacheCaptcha.put(element);
                } catch (IOException e) {
                    log.error("转换captcha JSON时错误，captchaId={},JSON={}，原因：{}", captchaId, JSON, e.getMessage(), e);
                }
            } else {
                captchaIdList.remove(captchaId);
            }
        }

        if(element == null) {
            return null;
        }

        return (CaptchaEntry)element.getObjectValue();
    }

    /**
     * 服务启动时调用这个方法初始化验证码相关资源
     */
    public void startup() {
        this.initLoadRedisIntoEhcache();
        this.initSubscribeRedisCaptchaEvent();
        this.createCaptcha();
        this.refreshExpiredCaptcha();
    }

    /**
     * 服务关闭时调用这个方法释放验证码相关资源
     */
    public void shutdown() {
        // 解除订阅redis验证码相关事件
        this.jedisPubSubList.forEach(JedisPubSub::unsubscribe);

        executorService.shutdown();
        try {
            while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.debug("成功解除订阅redis验证码相关事件");

        this.executorServiceCaptcha.shutdownNow();
        try {
            while (!executorServiceCaptcha.awaitTermination(1, TimeUnit.SECONDS)) ;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.debug("成功停止验证码生成和刷新线程");
    }

    /**
     * 加载redis缓存相关数据到本地ehcache缓存中
     */
    private void initLoadRedisIntoEhcache() {
        log.debug("开始加载redis缓存相关数据到本地ehcache缓存中");

        // 加载redis enable到本地ehcache缓存中
        Cache cacheEnable = cacheManager.getCache(Const.CahceNameEhcacheEnable);
        String value = jedisCluster.get(Const.CacheKeyEnable);
        boolean enabled = true;
        if(!StringUtils.isEmpty(value)) {
            try {
                enabled = Boolean.parseBoolean(value);
            } catch (Exception ignored) {

            }
        }
        Element element = new Element(Const.CacheKeyEnable, enabled);
        cacheEnable.put(element);

        // 加载redis验证码到本地ehcache缓存中
        String captchaIndexKey = this.getRandomIndexKey();
        Set<String> captchaIdSet = this.jedisCluster.smembers(captchaIndexKey);
        if(captchaIdSet == null) {
            captchaIdSet = new HashSet<>();
        }
        List<String> captchaIdList = new ArrayList<>(captchaIdSet);
        element = new Element(CacheKeyEhcacheCaptchaIndex, captchaIdList);
        this.cacheCaptchaIndex.put(element);
        log.debug("成功加载redis中{}个captcha索引到本地ehcache中", captchaIdList.size());

        log.debug("完成加载redis缓存相关数据到本地ehcache缓存中");
    }

    /**
     * 初始化订阅redis验证码生成、更新事件
     */
    private void initSubscribeRedisCaptchaEvent() {
        log.debug("开始初始化订阅redis验证码生成、更新事件");

        // 订阅redis验证码相关事件
        Cache cacheCaptcha = this.cacheManager.getCache("cacheCaptcha");
        this.executorService.submit(() -> {
            JedisPubSub jedisPubSub = new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    log.debug("订阅到captcha消息：{}", message);

                    String captchaId = message;
                    boolean exists = jedisCluster.exists(captchaId);
                    Element element = cacheCaptchaIndex.get(CacheKeyEhcacheCaptchaIndex);
                    List<String> captchaIndex = (List<String>)element.getObjectValue();
                    if (!exists) {
                        // captcha entry不存在redis中时删除本地captcha索引
                        captchaIndex.remove(captchaId);
                        log.debug("redis中不存在captchaId={}的JSON，需要在本地ehcache中删除这个key", captchaId);
                    } else {
                        // captcha entry存在redis中时添加本地captcha索引
                        if(!captchaIndex.contains(captchaId)) {
                            captchaIndex.add(captchaId);
                        }
                        cacheCaptcha.remove(captchaId);
                        log.debug("更新本地ehcache中captchaId={}的captcah", captchaId);
                    }
                }
            };
            this.jedisPubSubList.add(jedisPubSub);
            this.jedisCluster.subscribe(jedisPubSub, ChannelCaptchaEvent);
        });

        log.debug("完成初始化订阅redis验证码生成、更新事件");
    }

    /**
     * 创建验证码redis缓存
     * TODO 分散captcha过期时间，以使captcha不在同一个时间刷新
     * TODO 并发控制
     * TODO 模糊captcha
     */
    private void createCaptcha() {
        this.executorServiceCaptcha.submit(() -> {
            Cache cacheTimeout = this.cacheManager.getCache("cacheTimeout");
            String keyTimeout = "captchaCreation";
            int cronbIntervalInSeconds = 3600;

            while(true) {
                log.debug("开始执行创建验证码任务");

                Element element = new Element(keyTimeout, StringUtils.EMPTY);
                element.setTimeToLive(cronbIntervalInSeconds);
                cacheTimeout.put(element);

                String randomKeyIndex = getRandomIndexKey();
                List<String> keyIndexList = listIndexKeys();

                // 缓存中验证码个数不足需要补充
                long length = jedisCluster.scard(randomKeyIndex);
                int count = CaptchaMaximumEntries - (int) length;
                if (count > 0) {
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

                        keyIndexList.forEach(key -> jedisCluster.sadd(key, captchaId));
                        try {
                            String JSON = Const.OMInstance.writeValueAsString(entry);
                            jedisCluster.setex(captchaId, CaptchaTimeoutInSeconds, JSON);
                            jedisCluster.publish(ChannelCaptchaEvent, captchaId);
                        } catch (JsonProcessingException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }

                if(count > 0) {
                    log.debug("成功往redis生成{}个验证码", count);
                }

                log.debug("完成执行创建验证码任务");

                element = cacheTimeout.get(keyTimeout);
                if(element != null) {
                    Date timeNow = new Date();
                    long milliseconds = element.getExpirationTime() - timeNow.getTime();
                    if(milliseconds > 0) {
                        Thread.sleep(milliseconds);
                    }
                }
            }
        });
    }

    /**
     * 刷新过期验证码redis缓存
     * TODO 并发控制
     */
    private void refreshExpiredCaptcha() {
        this.executorServiceCaptcha.submit(() -> {
            Cache cacheTimeout = this.cacheManager.getCache("cacheTimeout");
            String keyTimeout = "captchaRefresh";
            int cronbIntervalInSeconds = 30;

            while(true) {
                log.debug("开始执行刷新过期验证码任务");

                Element element = new Element(keyTimeout, StringUtils.EMPTY);
                element.setTimeToLive(cronbIntervalInSeconds);
                cacheTimeout.put(element);

                String randomKeyIndex = getRandomIndexKey();
                List<String> keyIndexList = listIndexKeys();

                // 找出过期的验证码并更新
                Set<String> captchaEntryKeyList = this.jedisCluster.smembers(randomKeyIndex);
                if (captchaEntryKeyList != null && captchaEntryKeyList.size() > 0) {
                    AtomicInteger count = new AtomicInteger();

                    captchaEntryKeyList.forEach(key -> {
                        String JSON = jedisCluster.get(key);
                        if (StringUtils.isEmpty(JSON)) {
                            keyIndexList.forEach(keyTemporary -> jedisCluster.srem(keyTemporary, key));
                            this.jedisCluster.publish(ChannelCaptchaEvent, key);
                        } else {
                            try {
                                CaptchaEntry entry = Const.OMInstance.readValue(JSON, CaptchaEntry.class);
                                String captchaId = entry.getId();
                                Date createTime = entry.getCreateTime();
                                Date timeNow = new Date();
                                if (timeNow.getTime() - createTime.getTime() > 30 * 60 * 1000) {
                                    Captcha captcha = getCaptcha();
                                    String code = captcha.getCaptchaCode();
                                    String imageBase64 = captcha.toBase64();

                                    entry = new CaptchaEntry();
                                    entry.setId(captchaId);
                                    entry.setCode(code);
                                    entry.setImageBase64(imageBase64);
                                    entry.setCreateTime(new Date());

                                    JSON = Const.OMInstance.writeValueAsString(entry);
                                    jedisCluster.setex(captchaId, CaptchaTimeoutInSeconds, JSON);
                                    jedisCluster.publish(ChannelCaptchaEvent, captchaId);

                                    count.incrementAndGet();
                                }
                            } catch (IOException e) {
                                //
                            }
                        }
                    });

                    if (count.get() > 0) {
                        log.debug("成功更新redis中{}个验证码", count.get());
                    }
                }

                log.debug("完成执行刷新过期验证码任务");

                element = cacheTimeout.get(keyTimeout);
                if(element != null) {
                    Date timeNow = new Date();
                    long milliseconds = element.getExpirationTime() - timeNow.getTime();
                    if(milliseconds > 0) {
                        Thread.sleep(milliseconds);
                    }
                }
            }
        });
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

    /**
     * 获取所有验证码索引key
     * @return
     */
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

    /**
     * 包含客户端信息的captcha项
     */
    @Data
    public static class ClientCaptchaEntry {
        /**
         * 客户端标识
         */
        private String clientId;
        /**
         * captcha项
         */
        private CaptchaEntry entry;
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
