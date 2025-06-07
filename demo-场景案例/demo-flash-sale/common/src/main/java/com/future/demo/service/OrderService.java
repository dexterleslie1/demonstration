package com.future.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.common.exception.BusinessException;
import com.future.demo.config.ConfigRocketMQ;
import com.future.demo.dto.OrderDTO;
import com.future.demo.dto.OrderDetailDTO;
import com.future.demo.dto.PreOrderDTO;
import com.future.demo.entity.*;
import com.future.demo.mapper.CommonMapper;
import com.future.demo.mapper.OrderDetailMapper;
import com.future.demo.mapper.OrderMapper;
import com.future.demo.mapper.ProductMapper;
import com.future.demo.util.OrderRandomlyUtil;
import com.tencent.devops.leaf.service.SnowflakeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {
    public final static String KeyproductStockWithHashTag = "product{%s}:stock";
    public final static String KeyProductPurchaseRecordWithHashTag = "product{%s}:purchase";

    public final static int ProductStock = Integer.MAX_VALUE;
    public final static int UserCount = 10000 * 10000;

    static DefaultRedisScript<Long> defaultRedisScript = null;
    static String Script = null;

    static {
        try {
            defaultRedisScript = new DefaultRedisScript<>();

            // 不能使用 defaultRedisScript.setLocation(new ClassPathResource("flash-sale.lua")); 读取脚本，因为每次调用时都会到硬盘中读取脚本导致性能问题
            // 使用下面方式读取脚本到内存中
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

    @Resource
    ObjectMapper objectMapper;

    @Autowired
    StringRedisTemplate redisTemplate;
    @Resource
    OrderMapper orderMapper;
    @Resource
    OrderDetailMapper orderDetailMapper;
    @Resource
    ProductMapper productMapper;
    @Resource
    DefaultMQProducer producer;
    @Resource
    OrderRandomlyUtil orderRandomlyUtil;
    @Autowired
    SnowflakeService snowflakeService;
    @Resource
    CommonMapper commonMapper;

    public void create(Long userId, Long productId, Integer amount) throws Exception {
        String productIdStr = String.valueOf(productId);
        String userIdStr = String.valueOf(userId);
        String amountStr = String.valueOf(amount);

        // 判断库存是否充足、用户是否重复下单
        Long result = this.redisTemplate.execute(defaultRedisScript, Collections.singletonList(productIdStr), productIdStr, userIdStr, amountStr);
        if (result != null) {
            if (result == 1) {
                throw new BusinessException("库存不足");
            } else if (result == 2) {
                throw new BusinessException("用户重复下单");
            } else {
                throw new BusinessException("下单失败");
            }
        }

        /*int randInt = RandomUtils.nextInt(Const.StreamCount);
        String streamName = Const.StreamName + randInt;
        StringRecord record = StreamRecords.string(new HashMap<String, String>() {{
            this.put("userId", userIdStr);
            this.put("productId", productIdStr);
            this.put("amount", amountStr);
        }}).withStreamKey(streamName);
        this.redisTemplate.opsForStream().add(record);*/
        // 创建消息实例，指定 topic、Tag和消息体

        PreOrderDTO preOrderDTO = new PreOrderDTO();
        preOrderDTO.setUserId(userId);
        preOrderDTO.setProductId(productId);
        preOrderDTO.setAmount(amount);
        String JSON = this.objectMapper.writeValueAsString(preOrderDTO);
        Message message = new Message(ConfigRocketMQ.ProducerAndConsumerGroup, null, JSON.getBytes());
        // 发送消息并获取发送结果
        SendResult sendResult = producer.send(message);
        if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
            throw new BusinessException("RocketMQ消息发送失败");
        }

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
    }

    public List<OrderModel> createOrderModel(List<PreOrderDTO> preOrderDTOList) {
        List<Long> productIdList = preOrderDTOList.stream().map(o -> o.getProductId()).distinct().collect(Collectors.toList());
        List<ProductModel> productModelList = this.productMapper.list(productIdList);
        Map<Long, ProductModel> productIdToModelMap = productModelList.stream().collect(Collectors.toMap(ProductModel::getId, o -> o));

        List<OrderModel> orderModelList = new ArrayList<>();
        for (PreOrderDTO preOrderDTO : preOrderDTOList) {
            long userId = preOrderDTO.getUserId();
            long productId = preOrderDTO.getProductId();
            int amount = preOrderDTO.getAmount();

            ProductModel productModel = productIdToModelMap.get(productId);

            OrderModel orderModel = new OrderModel();

            orderModel.setUserId(userId);
            LocalDateTime createTime = OrderRandomlyUtil.getCreateTimeRandomly();
            orderModel.setCreateTime(createTime);

            DeleteStatus deleteStatus = OrderRandomlyUtil.getDeleteStatusRandomly();
            orderModel.setDeleteStatus(deleteStatus);

            Status status = OrderRandomlyUtil.getStatusRandomly();
            orderModel.setStatus(status);

            OrderDetailModel orderDetailModel = new OrderDetailModel();

            orderDetailModel.setUserId(userId);
            orderDetailModel.setProductId(productId);
            orderDetailModel.setMerchantId(productModel.getMerchantId());
            orderDetailModel.setAmount(amount);
            orderModel.setOrderDetailList(Collections.singletonList(orderDetailModel));

            orderModelList.add(orderModel);
        }
        return orderModelList;
    }

    public void insertBatch(List<OrderModel> orderModelList) {
        List<OrderModel> orderModelListProcessed = new ArrayList<>();
        List<OrderDetailModel> orderDetailModelListProcessed = new ArrayList<>();

        for (OrderModel orderModel : orderModelList) {
            Long orderId = this.snowflakeService.getId("order").getId();
            orderModel.setId(orderId);

            orderModelListProcessed.add(orderModel);

            List<OrderDetailModel> orderDetailModelList = orderModel.getOrderDetailList();
            for (OrderDetailModel orderDetailModel : orderDetailModelList) {
                Long orderDetailId = this.snowflakeService.getId("orderDetail").getId();
                orderDetailModel.setId(orderDetailId);
                orderDetailModel.setOrderId(orderId);
                orderDetailModelListProcessed.add(orderDetailModel);
            }
        }

        this.orderMapper.insertBatch(orderModelListProcessed);
        this.orderDetailMapper.insertBatch(orderDetailModelListProcessed);
        this.commonMapper.updateIncreaseCount("order", orderModelListProcessed.size());
    }

    /**
     * 根据用户查询其所属订单
     *
     * @param userId
     * @return
     */
    public List<OrderDTO> list(Long userId) throws JsonProcessingException {
        List<OrderModel> orderList = null;//this.orderMapper.list(userId);
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

    /**
     * 协助测试用于重新初始化商品信息
     */
    public void initProduct() {
        long totalProductCount = this.orderRandomlyUtil.productIdArray.length;

        // 清空redis缓存所有数据
        try (RedisConnection connection = this.redisTemplate.getConnectionFactory().getConnection()) {
            connection.flushDb();
        }

        // 清空db中所有商品数据
        this.productMapper.truncate();

        // 批量大小
        int batchSize = 1000;

        // 商品库存
        Map<String, String> productStockMap = new HashMap<>();
        // 商品购买记录，用于防止一个人多次购买同一个商品
        List<String> productPurchaseRecordList = new ArrayList<>();
        // 商品列表
        List<ProductModel> productModelList = new ArrayList<>();
        // 已经执行的批次数
        int executedBatchCount = 0;
        for (int i = 0; i < this.orderRandomlyUtil.productIdArray.length; i++) {
            long productId = this.orderRandomlyUtil.productIdArray[i];

            // 批量初始化商品库存
            Integer productStock = OrderService.ProductStock;
            String keyProductStock = String.format(OrderService.KeyproductStockWithHashTag, productId);
            productStockMap.put(keyProductStock, String.valueOf(productStock));
            if (productStockMap.size() == batchSize || i == this.orderRandomlyUtil.productIdArray.length) {
                this.redisTemplate.opsForValue().multiSet(productStockMap);
                productStockMap = new HashMap<>();
            }

            // 批量删除商品购买记录
            String keyProductPurchaseRecord = String.format(OrderService.KeyProductPurchaseRecordWithHashTag, productId);
            productPurchaseRecordList.add(keyProductPurchaseRecord);
            if (productPurchaseRecordList.size() == batchSize || i == this.orderRandomlyUtil.productIdArray.length) {
                this.redisTemplate.delete(productPurchaseRecordList);
                productPurchaseRecordList = new ArrayList<>();
            }

            // 批量初始化商品db数据
            ProductModel productModel = new ProductModel();
            productModel.setId(productId);
            productModel.setName("产品" + productId);
            productModel.setStock(productStock);
            long merchantId = this.orderRandomlyUtil.getMerchantIdRandomly();
            productModel.setMerchantId(merchantId);
            productModelList.add(productModel);
            if (productModelList.size() == batchSize || i == this.orderRandomlyUtil.productIdArray.length) {
                this.productMapper.insertBatch(productModelList);
                productModelList = new ArrayList<>();

                executedBatchCount++;
                if (log.isInfoEnabled()) {
                    log.info("已经初始化{}个商品信息，剩余{}个商品信息", executedBatchCount * batchSize, totalProductCount - (executedBatchCount * batchSize));
                }
            }
        }

        if (log.isInfoEnabled()) {
            log.info("成功初始化{}个商品信息", totalProductCount);
        }
    }
}
