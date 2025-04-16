package com.future.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.common.exception.BusinessException;
import com.future.demo.constant.Const;
import com.future.demo.dto.OrderDTO;
import com.future.demo.dto.OrderDetailDTO;
import com.future.demo.dto.PreOrderDTO;
import com.future.demo.entity.OrderDetailModel;
import com.future.demo.entity.OrderModel;
import com.future.demo.entity.ProductModel;
import com.future.demo.mapper.OrderDetailMapper;
import com.future.demo.mapper.OrderMapper;
import com.future.demo.mapper.ProductMapper;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.connection.stream.StringRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {
    public final static String KeyproductStockWithHashTag = "product{%s}:stock";
    public final static String KeyProductPurchaseRecordWithHashTag = "product{%s}:purchase";

    public final static int ProductCount = 300;
    public final static int ProductStock = 1000;
    public final static int UserCount = 10000 * 10000;

    static DefaultRedisScript<Long> defaultRedisScript = null;
    static String Script = null;

    static {
        try {
            defaultRedisScript = new DefaultRedisScript<>();
            /*defaultRedisScript.setLocation(new ClassPathResource("flash-sale.lua"));*/
            ClassPathResource classPathResource = new ClassPathResource("flash-sale.lua");
            String script = StreamUtils.copyToString(classPathResource.getInputStream(), StandardCharsets.UTF_8);
            classPathResource.getInputStream().close();
            defaultRedisScript.setScriptText(script);
            defaultRedisScript.setResultType(Long.class);

            Script = defaultRedisScript.getScriptAsString();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            System.exit(1);
        }
    }

    OrderService orderServiceProxy = null;
    BlockingQueue<PreOrderDTO> blockingQueue = new ArrayBlockingQueue<>(1024 * 1024);
    ExecutorService executor = Executors.newFixedThreadPool(1);
    ExecutorService executorRunner = Executors.newFixedThreadPool(64);

    @Resource
    RedisClusterCommands<String, String> sync;
    @Resource
    ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        // region 准备协助基准测试的数据

        /*this.orderDetailMapper.deleteAll();
        this.orderMapper.deleteAll();*/

        for (long i = 1; i <= ProductCount; i++) {
            // 准备 redis 数据辅助基于缓存的测试
            Integer productStock = OrderService.ProductStock;
            String keyProductStock = String.format(OrderService.KeyproductStockWithHashTag, i);
            this.redisTemplate.opsForValue().set(keyProductStock, String.valueOf(productStock));
            String keyProductPurchaseRecord = String.format(OrderService.KeyProductPurchaseRecordWithHashTag, i);
            this.redisTemplate.delete(keyProductPurchaseRecord);

            // 准备 db 数据辅助基于数据库的测试
            this.productMapper.delete(i);
            ProductModel productModel = new ProductModel();
            productModel.setId(i);
            productModel.setName("产品" + i);
            productModel.setStock(productStock);
            this.productMapper.insert(productModel);
        }

        // endregion
    }

    @PreDestroy
    public void destroy() throws InterruptedException {
        // BlockingQueue 退出信号
        this.blockingQueue.put(new PreOrderDTO(null, null, null));

        executorRunner.shutdown();
        while (!this.executorRunner.awaitTermination(10, TimeUnit.MILLISECONDS)) ;

        // 手动关闭 ExecutorService，否则 JMH 测试结束时会报告错误
        this.executor.shutdown();
        while (!this.executor.awaitTermination(10, TimeUnit.MILLISECONDS)) ;
    }

    @Autowired
    StringRedisTemplate redisTemplate;
    @Resource
    OrderMapper orderMapper;
    @Resource
    OrderDetailMapper orderDetailMapper;
    @Resource
    ProductMapper productMapper;
    @Resource
    RedissonClient redissonClient;

    // 基于数据库实现商品秒杀
    public void createOrderBasedDB(Long userId, Long productId, Integer amount) throws Exception {
        // 判断库存是否充足
        ProductModel productModel = this.productMapper.getById(productId);
        if (productModel.getStock() <= 0) {
            throw new BusinessException("库存不足");
        }

        // 使用分布式锁，防止用户重复下单
        String lockKey = "lock:" + userId + ":" + productId;
        RLock lock = null;
        boolean acquired = false;
        try {
            lock = redissonClient.getLock(lockKey);
            acquired = lock.tryLock();
            if (!acquired) {
                throw new BusinessException("用户重复下单");
            }

            // 获取 OrderService 的代理对象，否则 createOrderInternal 方法的 @Transactional 注解不生效
            OrderService proxy = (OrderService) AopContext.currentProxy();
            proxy.createOrderInternal(userId, productId, amount);
        } finally {
            if (lock != null && acquired) {
                lock.unlock();
            }
        }
    }

    // 基于 Redis+Lua脚本 实现商品秒杀，注意：只能够与 Redis Standalone 模式配合运行，与 Redis 其他模式配合运行会报错。
    // 结论：性能高于基于数据库的实现的 3 倍
    public void createOrderBasedRedisWithLuaScript(Long userId, Long productId, Integer amount) throws Exception {
        String productIdStr = String.valueOf(productId);
        String userIdStr = String.valueOf(userId);
        String amountStr = String.valueOf(amount);

        // 判断库存是否充足、用户是否重复下单
        Long result = this.redisTemplate.execute(defaultRedisScript, Collections.singletonList(productIdStr), productIdStr, userIdStr, amountStr);
        /*Long result = sync.eval(Script, ScriptOutputType.INTEGER, new String[]{productIdStr}, productIdStr, userIdStr, amountStr);*/
        if (result != null) {
            if (result == 1) {
                throw new BusinessException("库存不足");
            } else if (result == 2) {
                throw new BusinessException("用户重复下单");
            } else {
                throw new BusinessException("下单失败");
            }
        }

        int randInt = RandomUtils.nextInt(Const.StreamCount);
        String streamName = Const.StreamName + randInt;
        StringRecord record = StreamRecords.string(new HashMap<String, String>() {{
            this.put("userId", userIdStr);
            this.put("productId", productIdStr);
            this.put("amount", amountStr);
        }}).withStreamKey(streamName);
        this.redisTemplate.opsForStream().add(record);

        // 秒杀成功后，把用户订单信息存储到 redis 中，数据同步成功后会自动清除数据
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setCreateTime(LocalDateTime.now());
        OrderDetailModel orderDetailModel = new OrderDetailModel();
        orderDetailModel.setUserId(userId);
        orderDetailModel.setAmount(amount);
        orderDetailModel.setProductId(productId);
        orderModel.setOrderDetailList(Collections.singletonList(orderDetailModel));
        this.redisTemplate.opsForValue().set(userIdStr, this.objectMapper.writeValueAsString(orderModel));

        // 获取 OrderService 的代理对象，否则 createOrderInternal 方法的 @Transactional 注解不生效
        /*OrderService proxy = (OrderService) AopContext.currentProxy();
        proxy.createOrderInternal(userId, productId, amount);*/
//        if (this.orderServiceProxy == null) {
//            this.orderServiceProxy = (OrderService) AopContext.currentProxy();
//        }
//        this.blockingQueue.put(new PreOrderDTO(productId, userId, amount));
    }

    public void createOrderBasedRedisWithoutLuaScript(Long userId, Long productId, Integer amount) throws Exception {
//        String productIdStr = String.valueOf(productId);
//        String userIdStr = String.valueOf(userId);
//
//        // 库存余量不足时表示后续的所有请求无效
//        String key = KeyProductSoldOutPrefix + productIdStr;
//        if (Boolean.TRUE.equals(this.redisTemplate.hasKey(key))) {
//            throw new BusinessException("库存不足");
//        }
//
//        // region 判断库存是否充足、用户是否重复下单
//        RLock rLock = null;
//        boolean acquired = false;
//        try {
//            rLock = this.redissonClient.getLock(productIdStr);
//            acquired = rLock.tryLock(5, TimeUnit.SECONDS);
//            if (!acquired) {
//                throw new BusinessException("服务器被挤爆了！！！");
//            }
//
//            String productStockKey = OrderService.KeyProductStockPrefix + productIdStr;
//            String stockStr = this.redisTemplate.opsForValue().get(productStockKey);
//            int stock = Integer.parseInt(Objects.requireNonNull(stockStr));
//            if (stock < amount) {
//                String productSoldOutKey = KeyProductSoldOutPrefix + productIdStr;
//                this.redisTemplate.opsForValue().set(productSoldOutKey, "");
//                throw new BusinessException("库存不足");
//            }
//
//            String productPurchaseRecordKey = OrderService.KeyProductPurchaseRecordPrefix + productIdStr;
//            Boolean member = this.redisTemplate.opsForSet().isMember(productPurchaseRecordKey, userIdStr);
//            if (Boolean.TRUE.equals(member)) {
//                throw new BusinessException("用户重复下单");
//            }
//
//            this.redisTemplate.opsForValue().decrement(productStockKey, amount);
//            this.redisTemplate.opsForSet().add(productPurchaseRecordKey, userIdStr);
//        } finally {
//            if (rLock != null && acquired) {
//                rLock.unlock();
//            }
//        }
//
//        // endregion

        // 获取 OrderService 的代理对象，否则 createOrderInternal 方法的 @Transactional 注解不生效
        /*if (this.orderServiceProxy == null) {
            this.orderServiceProxy = (OrderService) AopContext.currentProxy();
        }
        this.blockingQueue.put(new PreOrderDTO(productId, userId, amount));*/
    }

    // 抛出异常后回滚事务
    @Transactional(rollbackFor = Exception.class)
    public void createOrderInternal(Long userId, Long productId, Integer amount) throws Exception {
        // 判断用户是否重复下单
        OrderDetailModel orderDetailModel = this.orderDetailMapper.getByUserIdAndProductId(userId, productId);
        if (orderDetailModel != null) {
            throw new BusinessException("用户重复下单");
        }

        // 创建订单
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        /*orderModel.setProductId(productId);
        orderModel.setAmount(amount);*/
        orderModel.setCreateTime(LocalDateTime.now());
        int count = this.orderMapper.insert(orderModel);
        if (count <= 0) {
            throw new BusinessException("创建订单失败");
        }

        orderDetailModel = new OrderDetailModel();
        orderDetailModel.setOrderId(orderModel.getId());
        orderDetailModel.setUserId(userId);
        orderDetailModel.setProductId(productId);
        orderDetailModel.setAmount(amount);
        this.orderDetailMapper.insert(orderDetailModel);

        // 扣减库存
        count = this.productMapper.decreaseStock(productId, amount);
        if (count <= 0) {
            throw new BusinessException("扣减库存失败");
        }

        // 删除缓存中的订单信息
        this.redisTemplate.delete(String.valueOf(userId));
    }

    /**
     * 根据用户查询其所属订单
     *
     * @param userId
     * @return
     */
    public List<OrderDTO> list(Long userId) throws JsonProcessingException {
        List<OrderModel> orderList = this.orderMapper.list(userId);
        List<Long> orderIdList = orderList.stream().map(OrderModel::getId).collect(Collectors.toList());

        List<OrderDTO> orderDTOList = null;
        if (!orderList.isEmpty()) {
            List<OrderDetailModel> orderDetailList = this.orderDetailMapper.list(orderIdList);
            Map<Long, List<OrderDetailModel>> orderDetailGroupByOrderId = orderDetailList.stream().collect(Collectors.groupingBy(OrderDetailModel::getOrderId));
            orderDTOList = orderList.stream().map(o -> {
                OrderDTO orderDTO = new OrderDTO();
                BeanUtils.copyProperties(o, orderDTO);
                Long orderId = o.getId();
                if (orderDetailGroupByOrderId.containsKey(orderId)) {
                    List<OrderDetailModel> orderDetailListTemporary = orderDetailGroupByOrderId.get(orderId);
                    List<OrderDetailDTO> orderDetailDTOList = orderDetailListTemporary.stream().map(oInternal1 -> {
                        OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
                        BeanUtils.copyProperties(oInternal1, orderDetailDTO);
                        return orderDetailDTO;
                    }).collect(Collectors.toList());
                    orderDTO.setOrderDetailList(orderDetailDTOList);
                }
                return orderDTO;
            }).collect(Collectors.toList());
        }

        if (orderDTOList == null) {
            orderDTOList = new ArrayList<>();
        }

        // 查询 redis 缓存中是否有订单信息
        if (Boolean.TRUE.equals(this.redisTemplate.hasKey(String.valueOf(userId)))) {
            String JSON = this.redisTemplate.opsForValue().get(String.valueOf(userId));
            if (StringUtils.hasText(JSON)) {
                OrderModel orderModel = this.objectMapper.readValue(JSON, OrderModel.class);
                if (orderModel != null) {
                    List<OrderDetailDTO> orderDetailDTOList = orderModel.getOrderDetailList().stream().map(
                            o -> {
                                OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
                                BeanUtils.copyProperties(o, orderDetailDTO);
                                return orderDetailDTO;
                            }
                    ).collect(Collectors.toList());
                    OrderDTO orderDTO = new OrderDTO();
                    BeanUtils.copyProperties(orderModel, orderDTO);
                    orderDTO.setOrderDetailList(orderDetailDTOList);
                    orderDTOList.add(0, orderDTO);
                }
            }
        }

        return orderDTOList;
    }
}
