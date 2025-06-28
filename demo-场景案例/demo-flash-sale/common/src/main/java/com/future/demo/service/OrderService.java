package com.future.demo.service;

import com.datastax.driver.core.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.common.exception.BusinessException;
import com.future.demo.dto.IncreaseCountDTO;
import com.future.demo.dto.OrderDTO;
import com.future.demo.entity.*;
import com.future.demo.mapper.*;
import com.future.demo.util.OrderRandomlyUtil;
import com.tencent.devops.leaf.service.SnowflakeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.future.demo.constant.Const.TopicIncreaseCount;
import static com.future.demo.constant.Const.TopicOrderInCacheSyncToDb;

/**
 * 注意：实现从用户维度查询订单已经能够演示基于Cassandra建模的基本技能，所以不需要实现从商家维度查询订单功能。
 */
@Service
@Slf4j
public class OrderService {
    public final static String KeyproductStockWithHashTag = "product{%s}:stock";
    public final static String KeyProductPurchaseRecordWithHashTag = "product{%s}:purchase";

    @Value("${productStock:2147483647}")
    public int productStock;

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
    Session session;

    private PreparedStatement preparedStatementUpdateIncreaseCount;
    private PreparedStatement preparedStatementListByUserIdAndStatus;
    private PreparedStatement preparedStatementListByUserIdAndWithoutStatus;

    @PostConstruct
    public void init() {
        String cql = "update t_count set count=count+? where flag=?";
        preparedStatementUpdateIncreaseCount = session.prepare(cql);
        preparedStatementUpdateIncreaseCount.setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);

        cql = "select user_id,status,create_time,order_id from t_order_list_by_userId where user_id=?" + " and status=?" +
                " and create_time>=? and create_time<=?" +
                " limit ?";
        preparedStatementListByUserIdAndStatus = session.prepare(cql);
        preparedStatementListByUserIdAndStatus.setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);

        cql = "select user_id,status,create_time,order_id from t_order_list_by_userId where user_id=?";
        cql = cql + " and status in(";
        int count = 0;
        for (Status status : Status.values()) {
            cql = cql + "'" + status.name() + "'";
            if (count + 1 < Status.values().length) {
                cql = cql + ",";
            }
            count++;
        }
        cql = cql + ")";
        cql = cql + " and create_time>=? and create_time<=?" +
                " limit ?";
        preparedStatementListByUserIdAndWithoutStatus = session.prepare(cql);
        preparedStatementListByUserIdAndWithoutStatus.setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);
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
    //    @Resource
//    DefaultMQProducer producer;
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    @Resource
    OrderRandomlyUtil orderRandomlyUtil;
    @Autowired
    SnowflakeService snowflakeService;
    @Resource
    CommonMapper commonMapper;
    @Resource
    IndexMapper indexMapper;

    /**
     * 普通下单
     *
     * @param userId
     * @param productId
     * @param amount
     */
    @Transactional(rollbackFor = Exception.class)
    public void create(Long userId, Long productId, Integer amount) throws BusinessException, JsonProcessingException, ExecutionException, InterruptedException {
        int affectRows = this.productMapper.decreaseStock(productId, amount);
        if (affectRows <= 0) {
            throw new BusinessException("库存不足");
        }

        OrderModel orderModel = new OrderModel();
        Long orderId = this.snowflakeService.getId("order").getId();
        orderModel.setId(orderId);
        orderModel.setUserId(userId);

        LocalDateTime createTime = OrderRandomlyUtil.getCreateTimeRandomly();
        orderModel.setCreateTime(createTime);

        DeleteStatus deleteStatus = OrderRandomlyUtil.getDeleteStatusRandomly();
        orderModel.setDeleteStatus(deleteStatus);

        Status status = OrderRandomlyUtil.getStatusRandomly();
        orderModel.setStatus(status);

        OrderDetailModel orderDetailModel = new OrderDetailModel();
        Long orderDetailId = this.snowflakeService.getId("orderDetail").getId();
        orderDetailModel.setId(orderDetailId);
        orderDetailModel.setOrderId(orderId);
        orderDetailModel.setUserId(userId);
        orderDetailModel.setAmount(amount);
        orderDetailModel.setProductId(productId);

        ProductModel productModel = this.productMapper.getById(productId);
        orderDetailModel.setMerchantId(productModel.getMerchantId());

        this.orderMapper.insert(orderModel);
        this.orderDetailMapper.insert(orderDetailModel);

        // 异步更新 t_count
        IncreaseCountDTO increaseCountDTO = new IncreaseCountDTO();
        increaseCountDTO.setFlag("order");
        increaseCountDTO.setCount(1);
        String JSON = this.objectMapper.writeValueAsString(increaseCountDTO);
        kafkaTemplate.send(TopicIncreaseCount, JSON).get();

//        // 秒杀成功后，把用户订单信息存储到 redis 中，数据同步成功后会自动清除数据
//        this.redisTemplate.opsForValue().set(userIdStr, this.objectMapper.writeValueAsString(orderModel));
    }

    /**
     * 秒杀
     *
     * @param userId
     * @param productId
     * @param amount
     * @throws Exception
     */
    public void createFlashSale(Long userId, Long productId, Integer amount) throws Exception {
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

        OrderModel orderModel = new OrderModel();
        Long orderId = this.snowflakeService.getId("order").getId();
        orderModel.setId(orderId);
        orderModel.setUserId(userId);
        LocalDateTime createTime = OrderRandomlyUtil.getCreateTimeRandomly();
        orderModel.setCreateTime(createTime);
        OrderDetailModel orderDetailModel = new OrderDetailModel();
        Long orderDetailId = this.snowflakeService.getId("orderDetail").getId();
        orderDetailModel.setId(orderDetailId);
        orderDetailModel.setOrderId(orderId);
        orderDetailModel.setUserId(userId);
        orderDetailModel.setAmount(amount);
        orderDetailModel.setProductId(productId);
        orderModel.setOrderDetailList(Collections.singletonList(orderDetailModel));
        String JSON = this.objectMapper.writeValueAsString(orderModel);
//        Message message = new Message(ConfigRocketMQ.ProducerAndConsumerGroup, null, JSON.getBytes());
//        // 发送消息并获取发送结果
//        SendResult sendResult = producer.send(message);
//        if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
//            throw new BusinessException("RocketMQ消息发送失败");
//        }
        kafkaTemplate.send(TopicOrderInCacheSyncToDb, JSON).get();

        // 秒杀成功后，把用户订单信息存储到 redis 中，数据同步成功后会自动清除数据
        this.redisTemplate.opsForValue().set(userIdStr, this.objectMapper.writeValueAsString(orderModel));
    }

    /**
     * 模拟真实业务数据，随机填充订单字段
     *
     * @param orderModelList
     * @return
     */
    public void fillupOrderRandomly(List<OrderModel> orderModelList) {
        List<Long> productIdList = orderModelList.stream().map(o -> o.getOrderDetailList().get(0).getProductId()).distinct().collect(Collectors.toList());
        List<ProductModel> productModelList = this.productMapper.list(productIdList);
        Map<Long, ProductModel> productIdToModelMap = productModelList.stream().collect(Collectors.toMap(ProductModel::getId, o -> o));

        for (OrderModel orderModel : orderModelList) {
            long productId = orderModel.getOrderDetailList().get(0).getProductId();

            ProductModel productModel = productIdToModelMap.get(productId);

            DeleteStatus deleteStatus = OrderRandomlyUtil.getDeleteStatusRandomly();
            orderModel.setDeleteStatus(deleteStatus);

            Status status = OrderRandomlyUtil.getStatusRandomly();
            orderModel.setStatus(status);

            OrderDetailModel orderDetailModel = orderModel.getOrderDetailList().get(0);
            orderDetailModel.setMerchantId(productModel.getMerchantId());
        }
    }

    /**
     * 批量插入订单到数据库
     *
     * @param orderModelList
     */
    public void insertBatch(List<OrderModel> orderModelList) throws Exception {
        List<OrderModel> orderModelListProcessed = new ArrayList<>();
        List<OrderDetailModel> orderDetailModelListProcessed = new ArrayList<>();

        for (OrderModel orderModel : orderModelList) {
            orderModelListProcessed.add(orderModel);

            List<OrderDetailModel> orderDetailModelList = orderModel.getOrderDetailList();
            orderDetailModelListProcessed.addAll(orderDetailModelList);
        }

        this.orderMapper.insertBatch(orderModelListProcessed);
        this.orderDetailMapper.insertBatch(orderDetailModelListProcessed);

        // 异步更新 t_count
        IncreaseCountDTO increaseCountDTO = new IncreaseCountDTO();
        increaseCountDTO.setFlag("order");
        increaseCountDTO.setCount(orderModelListProcessed.size());
        String JSON = this.objectMapper.writeValueAsString(increaseCountDTO);
        kafkaTemplate.send(TopicIncreaseCount, JSON).get();

//        String JSON = this.objectMapper.writeValueAsString(orderModelList);
//        Message message = new Message(ConfigRocketMQ.CassandraIndexTopic, null, JSON.getBytes());
//        // 发送消息并获取发送结果
//        SendResult sendResult = producer.send(message);
//        if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
//            throw new BusinessException("RocketMQ消息发送失败，Topic: " + ConfigRocketMQ.CassandraIndexTopic);
//        }
    }

    /**
     * 批量建立 listByUserId 索引
     */
    public void insertBatchOrderIndexListByUserId(List<OrderModel> orderModelList) throws BusinessException {
        List<OrderIndexListByUserIdModel> list = new ArrayList<>();
        for (int i = 0; i < orderModelList.size(); i++) {
            OrderModel orderModel = orderModelList.get(i);
            Long orderId = orderModel.getId();
            Long userId = orderModel.getUserId();
            LocalDateTime createTime = orderModel.getCreateTime();
            Status status = orderModel.getStatus();

            // 创建订单
            OrderIndexListByUserIdModel model = new OrderIndexListByUserIdModel();
            model.setOrderId(orderId);
            model.setUserId(userId);
            model.setCreateTime(createTime);
            model.setStatus(status);
            list.add(model);
        }
        this.indexMapper.insertBatchOrderIndexListByUserId(list);
        this.updateIncreaseCount("orderListByUserId", list.size());
    }

    public void updateIncreaseCount(String flag, long count) throws BusinessException {
        BoundStatement boundStatement = preparedStatementUpdateIncreaseCount.bind(count, flag);
        ResultSet resultSet = session.execute(boundStatement);
        if (!resultSet.wasApplied()) {
            throw new BusinessException("执行 updateIncreaseCount 失败，count=1");
        }
    }

    /**
     * 用户查询指定日期范围+指定状态的订单
     *
     * @param userId
     * @return
     */
    public List<OrderDTO> listByUserIdAndStatus(Long userId,
                                                Status status,
                                                LocalDateTime startTime,
                                                LocalDateTime endTime) {
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        BoundStatement bound = preparedStatementListByUserIdAndStatus.bind(
                userId, status.name(),
                Date.from(startTime.atZone(zoneId).toInstant())
                , Date.from(endTime.atZone(zoneId).toInstant())
                , 15);
        ResultSet result = session.execute(bound);
        List<OrderModel> orderModelList = new ArrayList<>();
        for (Row row : result) {
            long userIdTemporary = row.getLong("user_id");
            long orderId = row.getLong("order_id");
            Status statusTemporary = Status.valueOf(row.getString("status"));
            LocalDateTime createTime = row.getTimestamp("create_time").toInstant().atZone(zoneId).toLocalDateTime();

            OrderModel model = new OrderModel();
            model.setId(orderId);
            model.setUserId(userIdTemporary);
            model.setStatus(statusTemporary);
            model.setCreateTime(createTime);
            orderModelList.add(model);
        }

        List<OrderDTO> orderDTOList = null;
        if (!orderModelList.isEmpty()) {
            /*List<OrderDetailModel> orderDetailList = this.orderDetailMapper.list(orderIdList);
            Map<Long, List<OrderDetailModel>> orderDetailGroupByOrderId = orderDetailList.stream().collect(Collectors.groupingBy(OrderDetailModel::getOrderId));*/
            orderDTOList = orderModelList.stream().map(o -> {
                OrderDTO orderDTO = new OrderDTO();
                BeanUtils.copyProperties(o, orderDTO);
                /*Long orderId = o.getId();
                if (orderDetailGroupByOrderId.containsKey(orderId)) {
                    List<OrderDetailModel> orderDetailListTemporary = orderDetailGroupByOrderId.get(orderId);
                    List<OrderDetailDTO> orderDetailDTOList = orderDetailListTemporary.stream().map(oInternal1 -> {
                        OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
                        BeanUtils.copyProperties(oInternal1, orderDetailDTO);
                        return orderDetailDTO;
                    }).collect(Collectors.toList());
                    orderDTO.setOrderDetailList(orderDetailDTOList);
                }*/
                return orderDTO;
            }).collect(Collectors.toList());
        }

        if (orderDTOList == null) {
            orderDTOList = new ArrayList<>();
        }

        // 查询 redis 缓存中是否有订单信息
        /*if (Boolean.TRUE.equals(this.redisTemplate.hasKey(String.valueOf(userId)))) {
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
        }*/

        return orderDTOList;
    }

    /**
     * 用户查询指定日期范围+所有状态的订单
     *
     * @param userId
     * @param startTime
     * @param endTime
     * @return
     */
    public List<OrderDTO> listByUserIdAndWithoutStatus(
            Long userId,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        BoundStatement bound = preparedStatementListByUserIdAndWithoutStatus.bind(
                userId,
                Date.from(startTime.atZone(zoneId).toInstant())
                , Date.from(endTime.atZone(zoneId).toInstant())
                , 15);
        ResultSet result = session.execute(bound);
        List<OrderModel> orderModelList = new ArrayList<>();
        for (Row row : result) {
            long userIdTemporary = row.getLong("user_id");
            long orderId = row.getLong("order_id");
            Status statusTemporary = Status.valueOf(row.getString("status"));
            LocalDateTime createTime = row.getTimestamp("create_time").toInstant().atZone(zoneId).toLocalDateTime();

            OrderModel model = new OrderModel();
            model.setId(orderId);
            model.setUserId(userIdTemporary);
            model.setStatus(statusTemporary);
            model.setCreateTime(createTime);
            orderModelList.add(model);
        }

        List<OrderDTO> orderDTOList = null;
        if (!orderModelList.isEmpty()) {
            /*List<OrderDetailModel> orderDetailList = this.orderDetailMapper.list(orderIdList);
            Map<Long, List<OrderDetailModel>> orderDetailGroupByOrderId = orderDetailList.stream().collect(Collectors.groupingBy(OrderDetailModel::getOrderId));*/
            orderDTOList = orderModelList.stream().map(o -> {
                OrderDTO orderDTO = new OrderDTO();
                BeanUtils.copyProperties(o, orderDTO);
                /*Long orderId = o.getId();
                if (orderDetailGroupByOrderId.containsKey(orderId)) {
                    List<OrderDetailModel> orderDetailListTemporary = orderDetailGroupByOrderId.get(orderId);
                    List<OrderDetailDTO> orderDetailDTOList = orderDetailListTemporary.stream().map(oInternal1 -> {
                        OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
                        BeanUtils.copyProperties(oInternal1, orderDetailDTO);
                        return orderDetailDTO;
                    }).collect(Collectors.toList());
                    orderDTO.setOrderDetailList(orderDetailDTOList);
                }*/
                return orderDTO;
            }).collect(Collectors.toList());
        }

        if (orderDTOList == null) {
            orderDTOList = new ArrayList<>();
        }

        // 查询 redis 缓存中是否有订单信息
        /*if (Boolean.TRUE.equals(this.redisTemplate.hasKey(String.valueOf(userId)))) {
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
        }*/

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
            String keyProductStock = String.format(OrderService.KeyproductStockWithHashTag, productId);
            productStockMap.put(keyProductStock, String.valueOf(productStock));
            if (productStockMap.size() == batchSize || i + 1 == this.orderRandomlyUtil.productIdArray.length) {
                this.redisTemplate.opsForValue().multiSet(productStockMap);
                productStockMap = new HashMap<>();
            }

            // 批量删除商品购买记录
            String keyProductPurchaseRecord = String.format(OrderService.KeyProductPurchaseRecordWithHashTag, productId);
            productPurchaseRecordList.add(keyProductPurchaseRecord);
            if (productPurchaseRecordList.size() == batchSize || i + 1 == this.orderRandomlyUtil.productIdArray.length) {
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
            if (productModelList.size() == batchSize || i + 1 == this.orderRandomlyUtil.productIdArray.length) {
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
