package com.future.demo.robot.detection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramostear.captcha.common.Fonts;
import com.ramostear.captcha.core.Captcha;
import com.ramostear.captcha.support.CaptchaType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.util.Assert;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPubSub;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public abstract class AbstractCaptchaCacheService {
    private final static ObjectMapper OMInstance = new ObjectMapper();

    public final static String CahceNameEhcacheEnable = "cacheEnable";
    public final static String CacheNameEhcacheWhitelist = "cacheWhitelist";
    public final static String CacheNameEhcacheRequestCounter = "cacheRequestCounter";

    public final static String CacheKeyEnable = "enable";
    public final static String CacheKeyPrefixWhitelist = "whitelistprefex#";

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
    public final static Integer CaptchaTimeoutInSeconds = 7*24*3600;

    private final static Integer CaptchaMaximumEntries = 20000;
    private final static String ChannelCaptchaEvent = "channelCaptchaEvent";
    private final static String ChannelCaptchaEnable = "channelCaptchaEnable";

    private final static int TimeoutSecondsRequestCounter = 15*60;

    // 本地ehcache captcha索引key
    private final static String CacheKeyEhcacheCaptchaIndex = "captchaIndex";

    private final Random random = new Random();

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final List<JedisPubSub> jedisPubSubList = new ArrayList<>();

    private final ExecutorService executorServiceCaptcha = Executors.newCachedThreadPool();

    private final static String UriCaptchaGet = "/api/v1/anti/captcha/get.do";
    private final static String UriCaptchaVerify = "/api/v1/anti/captcha/verify.do";
    private final static String UriVerifyHtml = "/anti/verify.html";

    private JedisCluster jedisCluster;
    private CacheManagera cacheManager;
    private RedissonClient redissonClient;

    private Cache cacheCaptcha;
    private Cache cacheCaptchaIndex;
    private Cache cacheWhitelist;
    private Cache cacheEnable;
    private Cache cacheRequestCounter;

    public AbstractCaptchaCacheService(JedisCluster jedisCluster,
                                       CacheManagera cacheManager,
                                       RedissonClient redissonClient) {
        this.jedisCluster = jedisCluster;
        this.cacheManager = cacheManager;
        this.redissonClient = redissonClient;

        // 动态创建ehcache缓存
        // https://www.ehcache.org/documentation/2.8/code-samples.html
        String name = "cacheCaptcha";
        Cache cacheTemporary = new Cache(name, 20480, false, false, 0, 0);
        this.cacheManager.getCacheManager().addCache(cacheTemporary);
        this.cacheCaptcha = this.cacheManager.getCache(name);

        name = "cacheCaptchaIndex";
        cacheTemporary = new Cache(name, 1, false, false, 0, 0);
        this.cacheManager.getCacheManager().addCache(cacheTemporary);
        this.cacheCaptchaIndex = this.cacheManager.getCache(name);

        name = CacheNameEhcacheWhitelist;
        cacheTemporary = new Cache(name, 40960, false, false, 0, 0);
        this.cacheManager.getCacheManager().addCache(cacheTemporary);
        this.cacheWhitelist = this.cacheManager.getCache(name);

        name = CahceNameEhcacheEnable;
        cacheTemporary = new Cache(name, 1, false, false, 0, 0);
        this.cacheManager.getCacheManager().addCache(cacheTemporary);
        this.cacheEnable = this.cacheManager.getCache(name);

        name = CacheNameEhcacheRequestCounter;
        cacheTemporary = new Cache(name, 100000, false, false, 0, 0);
        this.cacheManager.getCacheManager().addCache(cacheTemporary);
        this.cacheRequestCounter = this.cacheManager.getCache(name);

        name = "cacheTimeout";
        cacheTemporary = new Cache(name, 1000, false, false, 0, 0);
        this.cacheManager.getCacheManager().addCache(cacheTemporary);
    }

    /**
     * 机制开关
     *
     * @param enable
     */
    public void setEnable(boolean enable) {
        jedisCluster.set(CacheKeyEnable, String.valueOf(enable));
        Cache cacheEnable = cacheManager.getCache(CahceNameEhcacheEnable);
        Element element = new Element(CacheKeyEnable, enable);
        cacheEnable.put(element);
        this.jedisCluster.publish(ChannelCaptchaEnable, StringUtils.EMPTY);
    }

    /**
     * 机制启动后需要重定向的uri列表，例如：/、/index.jsp、/login.jsp等
     *
     * @return
     */
    abstract List<String> getRedirectUriList();

    /**
     * 验证成功后返回前端重定向uri，例如：/index.jsp
     *
     * @return
     */
    abstract String getRedirectLocationSuccessfullyVerify();

    /**
     * 获取机制开关uri，用于不管机制是否关闭都应该放行此uri
     *
     * @return
     */
    abstract String getEnableUri();

    /**
     * http请求处理逻辑
     *
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     */
    public void requestFilter(ServletRequest servletRequest,
                              ServletResponse servletResponse,
                              FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String uri = request.getRequestURI();

        if(uri.equals(this.getEnableUri())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            boolean enabled = false;
            Element elementEnable = this.cacheEnable.get(CacheKeyEnable);
            if (elementEnable != null) {
                enabled = (Boolean) elementEnable.getObjectValue();
            }

            if(!enabled) {
                // 机制没有启动时不能请求captcha相关接口
                if(uri.equals(UriCaptchaGet) ||
                        uri.equals(UriCaptchaVerify) ||
                        uri.equals(UriVerifyHtml)) {
                    response.setStatus(404);
                    ResponseUtils.write(response, StringUtils.EMPTY);
                    return;
                }
            }

            if (enabled) {
                // 机制已经启动

                String clientIp = RequestUtils.getRemoteAddress(request);
                String key = CacheKeyPrefixWhitelist + clientIp;

                // 为了提升性能先判断ehcache是否存在ip白名单
                boolean inWhitelist = false;
                Element element = this.cacheWhitelist.get(key);
                if (element == null) {
                    if (!jedisCluster.exists(key)) {

                        // 机制启动后，记录ip请求次数
                        Element elementRequestCounter = this.cacheRequestCounter.get(clientIp);
                        if (elementRequestCounter == null) {
                            elementRequestCounter = new Element(clientIp, 0);
                        }
                        int count = (Integer) elementRequestCounter.getObjectValue() + 1;
                        elementRequestCounter = new Element(clientIp, count);
                        elementRequestCounter.setTimeToLive(TimeoutSecondsRequestCounter);
                        this.cacheRequestCounter.put(elementRequestCounter);

                        // TODO forbidden日志
                    } else {
                        // redis存在此ip白名单时，加载redis中的ip白名单到ehcache以提升性能
                        long seconds = jedisCluster.ttl(key);
                        if (seconds > 0) {
                            element = new Element(key, StringUtils.EMPTY);
                            element.setTimeToLive((int) seconds);
                            this.cacheWhitelist.put(element);
                            inWhitelist = true;
                        }
                    }
                } else {
                    inWhitelist = true;
                }

                if (UriCaptchaGet.equals(uri)) {
                    // 随机给客户端分配captcha

                    AbstractCaptchaCacheService.ClientCaptchaEntry clientCaptchaEntry = assignClient();
                    String clientId = clientCaptchaEntry.getClientId();
                    String imageBase64 = StringUtils.EMPTY;
                    if (clientCaptchaEntry.getEntry() != null) {
                        imageBase64 = clientCaptchaEntry.getEntry().getImageBase64();
                    }

                    Map<String, String> mapReturn = new HashMap<>();
                    mapReturn.put("imageBase64", imageBase64);
                    mapReturn.put("clientId", clientId);
                    AjaxResponse ajaxResponse = new AjaxResponse();
                    ajaxResponse.setDataObject(mapReturn);
                    String JSON = OMInstance.writeValueAsString(ajaxResponse);
                    ResponseUtils.write(response, JSON);
                    return;

                } else if (UriCaptchaVerify.equals(uri)) {
                    // 验证客户端提供的captcha

                    String clientId = request.getParameter("clientId");
                    String code = request.getParameter("code");

                    AjaxResponse ajaxResponse = new AjaxResponse();

                    verifyClient(clientId, clientIp, code);

                    Map<String, String> mapReturn = new HashMap<>();
                    String location = this.getRedirectLocationSuccessfullyVerify();
                    mapReturn.put("location", location);
                    ajaxResponse.setDataObject(mapReturn);
                    String JSON = OMInstance.writeValueAsString(ajaxResponse);
                    ResponseUtils.write(response, JSON);
                    return;

                } else {
                    // ip地址没有在白名单
                    if(!inWhitelist) {
                        if (!UriVerifyHtml.equals(uri)) {
                            List<String> redirectUriList = this.getRedirectUriList();
                            if (redirectUriList.contains(uri)) {
                                response.sendRedirect(UriVerifyHtml);
                                return;
                            } else {
                                Map<String, String> mapReturn = new HashMap<>();
                                mapReturn.put("location", UriVerifyHtml);
                                AjaxResponse ajaxResponse = new AjaxResponse();
                                ajaxResponse.setDataObject(mapReturn);
                                ajaxResponse.setErrorCode(50000);
                                String JSON = OMInstance.writeValueAsString(ajaxResponse);
                                ResponseUtils.write(response, JSON);
                                return;
                            }
                        }
                    }
                }
            }

            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception ex) {
            if(!(ex instanceof IllegalArgumentException)) {
                log.error(ex.getMessage(), ex);
            }

            int errorCode = 50000;
            String errorMessage;

            if(ex instanceof IllegalArgumentException) {
                errorMessage = ex.getMessage();
            } else {
                errorMessage = "网络繁忙，稍后重试";
            }

            try {
                AjaxResponse ajaxResponse = new AjaxResponse();
                ajaxResponse.setErrorCode(errorCode);
                ajaxResponse.setErrorMessage(errorMessage);
                String JSON = OMInstance.writeValueAsString(ajaxResponse);
                ResponseUtils.write(response, JSON);
            } catch (JsonProcessingException e) {
                //
            }
        }
    }

    /**
     * 为客户端分配captcha项
     * @return
     */
    private ClientCaptchaEntry assignClient() {
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
    private void verifyClient(String clientId,
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

        String key = CacheKeyPrefixWhitelist + clientIp;
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
        element = this.cacheCaptcha.get(captchaId);
        if(element == null) {
            String JSON = jedisCluster.get(captchaId);
            if (!StringUtils.isEmpty(JSON)) {
                try {
                    CaptchaEntry entry = OMInstance.readValue(JSON, CaptchaEntry.class);
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
        loadEnable();
        // 通知其他节点加载enable
        this.jedisCluster.publish(ChannelCaptchaEnable, StringUtils.EMPTY);

        // 加载redis验证码到本地ehcache缓存中
        String captchaIndexKey = this.getRandomIndexKey();
        Set<String> captchaIdSet = this.jedisCluster.smembers(captchaIndexKey);
        if(captchaIdSet == null) {
            captchaIdSet = new HashSet<>();
        }
        List<String> captchaIdList = new ArrayList<>(captchaIdSet);
        Element element = new Element(CacheKeyEhcacheCaptchaIndex, captchaIdList);
        this.cacheCaptchaIndex.put(element);
        log.debug("成功加载redis中{}个captcha索引到本地ehcache中", captchaIdList.size());

        log.debug("完成加载redis缓存相关数据到本地ehcache缓存中");
    }

    private void loadEnable() {
        Cache cacheEnable = cacheManager.getCache(CahceNameEhcacheEnable);
        String value = jedisCluster.get(CacheKeyEnable);
        boolean enabled = false;
        if(!StringUtils.isEmpty(value)) {
            try {
                enabled = Boolean.parseBoolean(value);
            } catch (Exception ignored) {

            }
        }
        Element element = new Element(CacheKeyEnable, enabled);
        cacheEnable.put(element);
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

        // 订阅机制开关事件
        this.executorService.submit(() -> {
            JedisPubSub jedisPubSub = new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    log.debug("订阅到captcha开关事件");
                    loadEnable();
                }
            };
            this.jedisPubSubList.add(jedisPubSub);
            this.jedisCluster.subscribe(jedisPubSub, ChannelCaptchaEnable);
        });

        log.debug("完成初始化订阅redis验证码生成、更新事件");
    }

    private final static String LockCreateCaptcha = "lockCreateCaptcha";
    /**
     * 创建验证码redis缓存
     */
    private void createCaptcha() {
        this.executorServiceCaptcha.submit(() -> {
            Cache cacheTimeout = this.cacheManager.getCache("cacheTimeout");
            String keyTimeout = "captchaCreation";
            int cronbIntervalInSeconds = 3600;

            while(true) {
                Element element = new Element(keyTimeout, StringUtils.EMPTY);
                element.setTimeToLive(cronbIntervalInSeconds);
                cacheTimeout.put(element);

                RLock rLock = null;
                try {
                    rLock = this.redissonClient.getLock(LockCreateCaptcha);
                    if(!rLock.tryLock(1, TimeUnit.SECONDS)) {
                        continue;
                    }

                    log.debug("开始执行创建验证码任务");
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
                                String JSON = OMInstance.writeValueAsString(entry);
                                jedisCluster.setex(captchaId, CaptchaTimeoutInSeconds, JSON);
                                jedisCluster.publish(ChannelCaptchaEvent, captchaId);
                            } catch (JsonProcessingException e) {
                                log.error(e.getMessage(), e);
                            }
                        }
                    }

                    if (count > 0) {
                        log.debug("成功往redis生成{}个验证码", count);
                    }

                    log.debug("完成执行创建验证码任务");
                } catch (InterruptedException e) {
                    //
                } finally {
                   if(rLock != null && rLock.isHeldByCurrentThread()) {
                       rLock.unlock();
                   }
                }

                element = cacheTimeout.get(keyTimeout);
                if(element != null) {
                    Date timeNow = new Date();
                    long milliseconds = element.getExpirationTime() - timeNow.getTime();
                    if(milliseconds > 0) {
                        try {
                            Thread.sleep(milliseconds);
                        } catch (InterruptedException e) {
                            //
                        }
                    }
                }
            }
        });
    }

    private final static String LockRefreshExpiredCaptcha = "lockRefreshExpiredCaptcha";
    /**
     * 刷新redis缓存captcha
     */
    private void refreshExpiredCaptcha() {
        this.executorServiceCaptcha.submit(() -> {
            Cache cacheTimeout = this.cacheManager.getCache("cacheTimeout");
            String keyTimeout = "captchaRefresh";
            int cronbIntervalInSeconds = 30;

            while(true) {
                try {
                    Thread.sleep(cronbIntervalInSeconds*1000);
                } catch (InterruptedException e) {
                    //
                }

                // 非凌晨2点不刷新captcha
                Date timeNow1 = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
                String hourStr = simpleDateFormat.format(timeNow1);
                int hour = Integer.parseInt(hourStr);
                if(hour != 2) {
                    continue;
                }
                // 当天已经更新过captcha
                Element element = cacheTimeout.get(keyTimeout);
                if(element != null) {
                    continue;
                }
                element = new Element(keyTimeout, StringUtils.EMPTY);
                element.setTimeToLive(6*3600);
                cacheTimeout.put(element);

                RLock rLock = null;
                try {
                    rLock = this.redissonClient.getLock(LockRefreshExpiredCaptcha);
                    if (!rLock.tryLock(1, TimeUnit.SECONDS)) {
                        continue;
                    }

                    log.debug("开始执行刷新过期验证码任务");

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
                                    CaptchaEntry entry = OMInstance.readValue(JSON, CaptchaEntry.class);
                                    String captchaId = entry.getId();

                                    Captcha captcha = getCaptcha();
                                    String code = captcha.getCaptchaCode();
                                    String imageBase64 = captcha.toBase64();

                                    entry = new CaptchaEntry();
                                    entry.setId(captchaId);
                                    entry.setCode(code);
                                    entry.setImageBase64(imageBase64);
                                    entry.setCreateTime(new Date());

                                    JSON = OMInstance.writeValueAsString(entry);
                                    jedisCluster.setex(captchaId, CaptchaTimeoutInSeconds, JSON);
                                    jedisCluster.publish(ChannelCaptchaEvent, captchaId);

                                    count.incrementAndGet();
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
                } catch (InterruptedException ex) {
                    //
                } finally {
                    if(rLock != null && rLock.isHeldByCurrentThread()) {
                        rLock.unlock();
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
