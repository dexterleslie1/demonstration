package com.future.demo.service;

import cn.hutool.core.util.RandomUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.demo.entity.ProductModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {

    public final static String KeyProduct = "product:";

    public final static int ProductTotalCount = 10000000;

    @Autowired
    StringRedisTemplate redisTemplate;
    @Resource
    ObjectMapper objectMapper;

    /**
     * 向 redis 缓存初始化商品
     */
    public void initCaching() throws Exception {
        int batchSize = 1000;
        int concurrentThreads = 32;
        AtomicInteger count = new AtomicInteger();

        ExecutorService threadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < concurrentThreads; i++) {
            threadPool.submit(() -> {
                try {
                    Map<String, String> productKeyToJSONMap = new HashMap<>();
                    int productId;
                    while ((productId = count.getAndIncrement()) <= ProductTotalCount) {
                        String key = KeyProduct + productId;
                        ProductModel model = new ProductModel();
                        model.setId((long) productId);
                        model.setMerchantId(RandomUtil.randomLong(0, Long.MAX_VALUE));
                        model.setName("商品" + productId);
                        model.setStock(0);
                        String JSON = this.objectMapper.writeValueAsString(model);
                        productKeyToJSONMap.put(key, JSON);

                        if (productKeyToJSONMap.size() >= batchSize) {
                            this.redisTemplate.opsForValue().multiSet(productKeyToJSONMap);
                            productKeyToJSONMap = new HashMap<>();
                        }

                        int countInternal;
                        if (((countInternal = count.get()) % (batchSize * 100)) == 0) {
                            log.info("count=" + countInternal);
                        }
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
        }

        threadPool.shutdown();
        while (!threadPool.awaitTermination(100, TimeUnit.MILLISECONDS)) ;
    }

    public List<ProductModel> listByIds(List<Long> idList) {
        List<String> idStrList = idList.stream().map(o -> KeyProduct + o).collect(Collectors.toList());
        List<String> jsonList = this.redisTemplate.opsForValue().multiGet(idStrList);
        return jsonList.stream().map(o -> {
            try {
                return this.objectMapper.readValue(o, ProductModel.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }
}
