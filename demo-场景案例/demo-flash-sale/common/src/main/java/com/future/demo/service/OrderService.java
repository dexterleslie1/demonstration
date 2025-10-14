package com.future.demo.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.datastax.driver.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.common.exception.BusinessException;
import com.future.count.IncreaseCountDTO;
import com.future.demo.config.PrometheusCustomMonitor;
import com.future.demo.dto.OrderDTO;
import com.future.demo.dto.OrderDetailDTO;
import com.future.demo.entity.*;
import com.future.demo.exception.ProductTypeNotSupportedException;
import com.future.demo.exception.StockInsufficientException;
import com.future.demo.mapper.OrderDetailMapper;
import com.future.demo.mapper.OrderMapper;
import com.future.demo.mapper.ProductMapper;
import com.tencent.devops.leaf.service.SnowflakeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.future.demo.constant.Const.*;

/**
 * 注意：实现从用户维度查询订单已经能够演示基于Cassandra建模的基本技能，所以不需要实现从商家维度查询订单功能。
 */
@Service
@Slf4j
public class OrderService {
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

    private PreparedStatement preparedStatementListByUserIdAndStatus;
    private PreparedStatement preparedStatementListByUserIdAndWithoutStatus;
    private PreparedStatement preparedStatementListByMerchantIdAndStatus;
    private PreparedStatement preparedStatementListByMerchantIdAndWithoutStatus;

    @PostConstruct
    public void init() {
        String cql = "select user_id,status,create_time,order_id,merchant_id,order_detail_json from t_order_list_by_userId where user_id=?" + " and status=?" +
                " and create_time>=? and create_time<=?";
        preparedStatementListByUserIdAndStatus = session.prepare(cql);
        preparedStatementListByUserIdAndStatus.setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);

        cql = "select user_id,status,create_time,order_id,merchant_id,order_detail_json from t_order_list_by_userId where user_id=?";
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
        cql = cql + " and create_time>=? and create_time<=?";
        preparedStatementListByUserIdAndWithoutStatus = session.prepare(cql);
        preparedStatementListByUserIdAndWithoutStatus.setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);

        cql = "select merchant_id,status,create_time,order_id,user_id,order_detail_json from t_order_list_by_merchantId where merchant_id=?" + " and status=?" +
                " and create_time>=? and create_time<=?";
        preparedStatementListByMerchantIdAndStatus = session.prepare(cql);
        preparedStatementListByMerchantIdAndStatus.setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);

        cql = "select merchant_id,status,create_time,order_id,user_id,order_detail_json from t_order_list_by_merchantId where merchant_id=?";
        cql = cql + " and status in(";
        count = 0;
        for (Status status : Status.values()) {
            cql = cql + "'" + status.name() + "'";
            if (count + 1 < Status.values().length) {
                cql = cql + ",";
            }
            count++;
        }
        cql = cql + ")";
        cql = cql + " and create_time>=? and create_time<=?";
        preparedStatementListByMerchantIdAndWithoutStatus = session.prepare(cql);
        preparedStatementListByMerchantIdAndWithoutStatus.setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);
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
    KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    SnowflakeService snowflakeService;
    @Resource
    PrometheusCustomMonitor prometheusCustomMonitor;

    /**
     * 普通下单
     *
     * @param userId
     * @param productId
     * @param amount
     * @param createTime 如果指定订单创建时间，则不会随机生成订单创建时间
     */
    @Transactional(
            rollbackFor = Exception.class,
            noRollbackFor = {
                    ProductTypeNotSupportedException.class,
                    StockInsufficientException.class
            })
    @SentinelResource(value = "createOrderOrdinary", blockHandler = "createOrdinaryBlockHandler",
            exceptionsToIgnore = {
                    BusinessException.class,
                    ProductTypeNotSupportedException.class,
                    StockInsufficientException.class})
    public Long create(Long userId,
                       Long productId,
                       Integer amount,
                       LocalDateTime createTime) throws Exception {
        ProductModel productModel = this.productMapper.getById(productId);
        // 不能使用普通方式向秒杀商品下单
        if (productModel.isFlashSale()) {
            prometheusCustomMonitor.getCounterOrdinaryPurchaseFlashSaleProductNotSupported().increment();
            throw new ProductTypeNotSupportedException("商品 " + productId + " 为秒杀类型，不支持普通方式下单");
        }

        int affectRows = this.productMapper.decreaseStock(productId, amount);
        if (affectRows <= 0) {
            prometheusCustomMonitor.getCounterOrdinaryPurchaseInsufficientStock().increment();
            throw new StockInsufficientException("库存不足");
        }

        OrderModel orderModel = new OrderModel();
        Long orderId = this.snowflakeService.getId("order").getId();
        orderModel.setId(orderId);
        orderModel.setUserId(userId);

        if (createTime == null) {
            // 注意：模拟实际不应该随机生成，否则有关于此字段的二级索引时插入性能很低
            /*createTime = OrderRandomlyUtil.getCreateTimeRandomly();*/
            createTime = LocalDateTime.now();
        }
        orderModel.setCreateTime(createTime);

        // 注意：模拟实际不应该随机生成，否则有关于此字段的二级索引时插入性能很低
        /*DeleteStatus deleteStatus = OrderRandomlyUtil.getDeleteStatusRandomly();*/
        DeleteStatus deleteStatus = DeleteStatus.Normal;
        orderModel.setDeleteStatus(deleteStatus);

        // 注意：模拟实际不应该随机生成，否则有关于此字段的二级索引时插入性能很低
        /*Status status = OrderRandomlyUtil.getStatusRandomly();*/
        Status status = Status.Unpay;
        orderModel.setStatus(status);

        orderModel.setMerchantId(productModel.getMerchantId());

        OrderDetailModel orderDetailModel = new OrderDetailModel();
        Long orderDetailId = this.snowflakeService.getId("orderDetail").getId();
        orderDetailModel.setId(orderDetailId);
        orderDetailModel.setOrderId(orderId);
        orderDetailModel.setUserId(userId);
        orderDetailModel.setAmount(amount);
        orderDetailModel.setProductId(productId);

        orderModel.setOrderDetailList(Collections.singletonList(orderDetailModel));

        this.orderMapper.insert(orderModel);
        this.orderDetailMapper.insert(orderDetailModel);

        List<ListenableFuture<SendResult<String, String>>> futureList = new ArrayList<>();
        // 下单成功后，把用户订单信息存储到 redis 中，cassandra 同步成功后会自动清除数据
        String JSON = this.objectMapper.writeValueAsString(orderModel);
        String userIdStr = String.valueOf(userId);
        String key = CacheKeyPrefixOrderInCacheBeforeCassandraIndexCreate + userIdStr;
        redisTemplate.opsForHash().put(key, String.valueOf(orderId), JSON);
        futureList.add(kafkaTemplate.send(TopicCreateOrderCassandraIndexListByUserId, JSON));
        futureList.add(kafkaTemplate.send(TopicCreateOrderCassandraIndexListByMerchantId, JSON));

        // 异步更新 t_count
        IncreaseCountDTO increaseCountDTO = new IncreaseCountDTO(orderId, "order");
        JSON = this.objectMapper.writeValueAsString(increaseCountDTO);
        futureList.add(kafkaTemplate.send(TopicIncreaseCountFast, JSON));

        if (!futureList.isEmpty()) {
            int index = -1;
            try {
                for (int i = 0; i < futureList.size(); i++) {
                    index = i;
                    futureList.get(i).get();
                }
            } catch (Exception ex) {
                log.error("发送Kafka消息失败，原因：{}，出错的 futureList 索引为 {}", ex.getMessage(), index, ex);
                throw ex;
            }
        }

        prometheusCustomMonitor.getCounterOrdinaryPurchaseSuccessfully().increment();

        return orderId;
    }

    /**
     * 秒杀
     *
     * @param userId
     * @param productId
     * @param amount
     * @param createTime 如果指定订单创建时间，则不会随机生成订单创建时间
     * @throws Exception
     */
    @SentinelResource(value = "createOrderFlashSale", blockHandler = "createFlashSaleBlockHandler", exceptionsToIgnore = BusinessException.class)
    public void createFlashSale(Long userId,
                                Long productId,
                                Integer amount,
                                LocalDateTime createTime) throws Exception {
        String productIdStr = String.valueOf(productId);
        String userIdStr = String.valueOf(userId);
        String amountStr = String.valueOf(amount);

        // 判断秒杀是否已经开始或者结束
        String key = String.format(ProductService.KeyFlashSaleProductStartTime, productIdStr);
        String value = redisTemplate.opsForValue().get(key);
        if (StringUtils.isBlank(value)) {
            prometheusCustomMonitor.getCounterFlashSalePurchaseProductNotExists().increment();
            throw new BusinessException("秒杀商品 " + productIdStr + " 不存在");
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime flashSaleStartTime = LocalDateTime.parse(value, dateTimeFormatter);
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        if (flashSaleStartTime.isAfter(localDateTimeNow)) {
            prometheusCustomMonitor.getCounterFlashSalePurchaseNotStartedYet().increment();
            throw new BusinessException("秒杀未开始，开始时间 " + value);
        }

        key = String.format(ProductService.KeyFlashSaleProductEndTime, productIdStr);
        value = redisTemplate.opsForValue().get(key);
        LocalDateTime flashSaleEndTime = !StringUtils.isBlank(value) ? LocalDateTime.parse(value, dateTimeFormatter) : null;
        if (flashSaleEndTime == null || flashSaleEndTime.isBefore(localDateTimeNow)) {
            prometheusCustomMonitor.getCounterFlashSalePurchaseEnded().increment();
            throw new BusinessException("秒杀已结束，结束时间 " + value);
        }

        // 判断库存是否充足、用户是否重复下单
        Long result = this.redisTemplate.execute(defaultRedisScript, Collections.singletonList(productIdStr), productIdStr, userIdStr, amountStr);
        if (result != null && result > 0) {
            if (result == 1) {
                prometheusCustomMonitor.getCounterFlashSalePurchaseInsufficientStock().increment();
                throw new StockInsufficientException("库存不足");
            } else if (result == 2) {
                prometheusCustomMonitor.getCounterFlashSalePurchaseAlreadyPurchased().increment();
                throw new BusinessException("重复下单");
            } else {
                prometheusCustomMonitor.getCounterFlashSalePurchaseUnknownException().increment();
                throw new BusinessException("未知秒杀异常，脚本返回值 " + result);
            }
        }

        OrderModel orderModel = new OrderModel();
        Long orderId = this.snowflakeService.getId("order").getId();
        orderModel.setId(orderId);
        orderModel.setUserId(userId);
        // 注意：模拟实际不应该随机生成，否则有关于此字段的二级索引时插入性能很低
        /*DeleteStatus deleteStatus = OrderRandomlyUtil.getDeleteStatusRandomly();*/
        DeleteStatus deleteStatus = DeleteStatus.Normal;
        orderModel.setDeleteStatus(deleteStatus);
        // 注意：模拟实际不应该随机生成，否则有关于此字段的二级索引时插入性能很低
        /*Status status = OrderRandomlyUtil.getStatusRandomly();*/
        Status status = Status.Unpay;
        orderModel.setStatus(status);
        if (createTime == null) {
            // 注意：模拟实际不应该随机生成，否则有关于此字段的二级索引时插入性能很低
            /*createTime = OrderRandomlyUtil.getCreateTimeRandomly();*/
            createTime = LocalDateTime.now();
        }
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

        List<ListenableFuture<SendResult<String, String>>> futureList = new ArrayList<>();
        // 秒杀成功后，把用户订单信息存储到 redis 中，cassandra 同步成功后会自动清除数据
        /*key = CacheKeyPrefixOrderInCacheBeforeCassandraIndexCreate + userIdStr;
        redisTemplate.opsForHash().put(key, String.valueOf(orderId), JSON);*/

        /*futureList.add(kafkaTemplate.send(Const.TopicCreateOrderCassandraIndexListByUserId, JSON));
        futureList.add(kafkaTemplate.send(Const.TopicCreateOrderCassandraIndexListByMerchantId, JSON));
        // 秒杀成功后，发出同步订单到数据库消息
        futureList.add(kafkaTemplate.send(TopicOrderInCacheSyncToDb, JSON));*/
        futureList.add(kafkaTemplate.send(TopicCreateFlashSaleOrderSuccessfully, JSON));

        if (!futureList.isEmpty()) {
            int index = -1;
            try {
                for (int i = 0; i < futureList.size(); i++) {
                    index = i;
                    futureList.get(i).get();
                }
            } catch (Exception ex) {
                log.error("发送Kafka消息失败，原因：{}，出错的 futureList 索引为 {}", ex.getMessage(), index, ex);
                throw ex;
            }
        }

        prometheusCustomMonitor.getCounterFlashSalePurchaseSuccessfully().increment();
    }

    /**
     * 限流时触发
     *
     * @param userId
     * @param productId
     * @param amount
     * @param createTime
     * @throws Exception
     */
    public void createFlashSaleBlockHandler(Long userId,
                                            Long productId,
                                            Integer amount,
                                            LocalDateTime createTime,
                                            BlockException ex) throws Exception {
        throw new BusinessException("当前秒杀流量过大，请稍后再重试！！！");
    }

    /**
     * 限流时触发
     *
     * @param userId
     * @param productId
     * @param amount
     * @param createTime
     * @throws Exception
     */
    public Long createOrdinaryBlockHandler(Long userId,
                                           Long productId,
                                           Integer amount,
                                           LocalDateTime createTime,
                                           BlockException ex) throws Exception {
        throw new BusinessException("当前下单流量过大，请稍后再重试！！！");
    }

    /**
     * 数据同步前填充订单商家ID
     *
     * @param orderModelList
     * @return
     */
    public void fillupOrderMerchantId(List<OrderModel> orderModelList) {
        List<Long> productIdList = orderModelList.stream().map(o -> o.getOrderDetailList().get(0).getProductId()).distinct().collect(Collectors.toList());
        List<ProductModel> productModelList = this.productMapper.list(productIdList);
        Map<Long, ProductModel> productIdToModelMap = productModelList.stream().collect(Collectors.toMap(ProductModel::getId, o -> o));

        for (OrderModel orderModel : orderModelList) {
            long productId = orderModel.getOrderDetailList().get(0).getProductId();
            ProductModel productModel = productIdToModelMap.get(productId);
            orderModel.setMerchantId(productModel.getMerchantId());
        }
    }

    /**
     * 填充订单中商品信息
     *
     * @param dtoList
     */
    public void fillupOrderListProductInfo(List<OrderDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty())
            return;

        List<Long> productIdList = new ArrayList<>();
        for (OrderDTO dto : dtoList) {
            List<OrderDetailDTO> detailDTOList = dto.getOrderDetailList();
            if (detailDTOList != null && !detailDTOList.isEmpty()) {
                for (OrderDetailDTO detailDTO : detailDTOList) {
                    Long productId = detailDTO.getProductId();
                    if (!productIdList.contains(productId))
                        productIdList.add(productId);
                }
            }
        }

        List<ProductModel> modelList = this.productMapper.list(productIdList);
        Map<Long, ProductModel> productIdToModelMap = modelList.stream().collect(Collectors.toMap(ProductModel::getId, o -> o));
        for (OrderDTO dto : dtoList) {
            List<OrderDetailDTO> detailDTOList = dto.getOrderDetailList();
            if (detailDTOList != null && !detailDTOList.isEmpty()) {
                for (OrderDetailDTO detailDTO : detailDTOList) {
                    Long productId = detailDTO.getProductId();
                    detailDTO.setProductName(productIdToModelMap.get(productId).getName());
                }
            }
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
                                                LocalDateTime endTime) throws Exception {
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        Date dateStartTime = Date.from(startTime.atZone(zoneId).toInstant());
        Date dateEndTime = Date.from(endTime.atZone(zoneId).toInstant());
        BoundStatement bound = preparedStatementListByUserIdAndStatus.bind(
                userId, status.name(),
                dateStartTime,
                dateEndTime/*, 15*/);
        ResultSet result = session.execute(bound);
        List<OrderModel> orderModelList = new ArrayList<>();
        for (Row row : result) {
            long userIdTemporary = row.getLong("user_id");
            long orderId = row.getLong("order_id");
            Status statusTemporary = Status.valueOf(row.getString("status"));
            LocalDateTime createTime = row.getTimestamp("create_time").toInstant().atZone(zoneId).toLocalDateTime();
            Long merchantId = row.getLong("merchant_id");
            String orderDetailJSON = row.getString("order_detail_json");

            OrderModel model = new OrderModel();
            model.setId(orderId);
            model.setUserId(userIdTemporary);
            model.setStatus(statusTemporary);
            model.setCreateTime(createTime);
            model.setMerchantId(merchantId);
            List<OrderIndexListByUserIdModel.OrderIndexListByUserIdDetailModel> detailList =
                    this.objectMapper.readValue(orderDetailJSON, new TypeReference<List<OrderIndexListByUserIdModel.OrderIndexListByUserIdDetailModel>>() {
                    });
            List<OrderDetailModel> orderDetailModelList = detailList.stream().map(o -> {
                OrderDetailModel orderDetailModel = new OrderDetailModel();
                orderDetailModel.setOrderId(orderId);
                orderDetailModel.setProductId(o.getProductId());
                orderDetailModel.setAmount(o.getAmount());
                return orderDetailModel;
            }).collect(Collectors.toList());
            model.setOrderDetailList(orderDetailModelList);
            orderModelList.add(model);
        }

        List<OrderDTO> orderDTOList = null;
        if (!orderModelList.isEmpty()) {
            orderDTOList = orderModelList.stream().map(o -> {
                OrderDTO orderDTO = new OrderDTO();
                BeanUtils.copyProperties(o, orderDTO);
                orderDTO.setId(String.valueOf(o.getId()));
                List<OrderDetailModel> orderDetailModelList = o.getOrderDetailList();
                List<OrderDetailDTO> orderDetailDTOList = orderDetailModelList.stream().map(oInternal1 -> {
                    OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
                    BeanUtils.copyProperties(oInternal1, orderDetailDTO);
                    return orderDetailDTO;
                }).collect(Collectors.toList());
                orderDTO.setOrderDetailList(orderDetailDTOList);

                return orderDTO;
            }).collect(Collectors.toList());
        }

        if (orderDTOList == null) {
            orderDTOList = new ArrayList<>();
        }

        // 查询 redis 缓存中是否有订单信息
        String userIdStr = String.valueOf(userId);
        String key = CacheKeyPrefixOrderInCacheBeforeCassandraIndexCreate + userIdStr;
        Long length = redisTemplate.opsForHash().size(key);
        if (length != null && length > 0) {
            List<String> values = redisTemplate.opsForHash().values(key).stream().map(o -> (String) o).collect(Collectors.toList());
            if (values != null && !values.isEmpty()) {
                for (String JSON : values) {
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
                        orderDTO.setId(String.valueOf(orderModel.getId()));
                        orderDTO.setOrderDetailList(orderDetailDTOList);
                        orderDTOList.add(0, orderDTO);
                    }
                }
            }
        }

        // 填充商品信息
        this.fillupOrderListProductInfo(orderDTOList);

        orderDTOList.sort(((o1, o2) -> {
            Long id1 = Long.parseLong(o1.getId());
            Long id2 = Long.parseLong(o2.getId());
            return id2.compareTo(id1);
        }));

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
            LocalDateTime endTime) throws Exception {
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        Date dateStartTime = Date.from(startTime.atZone(zoneId).toInstant());
        Date dateEndTime = Date.from(endTime.atZone(zoneId).toInstant());
        BoundStatement bound = preparedStatementListByUserIdAndWithoutStatus.bind(
                userId,
                dateStartTime,
                dateEndTime/*, 15*/);
        ResultSet result = session.execute(bound);
        List<OrderModel> orderModelList = new ArrayList<>();
        for (Row row : result) {
            long userIdTemporary = row.getLong("user_id");
            long orderId = row.getLong("order_id");
            Status statusTemporary = Status.valueOf(row.getString("status"));
            LocalDateTime createTime = row.getTimestamp("create_time").toInstant().atZone(zoneId).toLocalDateTime();
            Long merchantId = row.getLong("merchant_id");
            String orderDetailJSON = row.getString("order_detail_json");

            OrderModel model = new OrderModel();
            model.setId(orderId);
            model.setUserId(userIdTemporary);
            model.setStatus(statusTemporary);
            model.setCreateTime(createTime);
            model.setMerchantId(merchantId);
            List<OrderIndexListByUserIdModel.OrderIndexListByUserIdDetailModel> detailList =
                    this.objectMapper.readValue(orderDetailJSON, new TypeReference<List<OrderIndexListByUserIdModel.OrderIndexListByUserIdDetailModel>>() {
                    });
            List<OrderDetailModel> orderDetailModelList = detailList.stream().map(o -> {
                OrderDetailModel orderDetailModel = new OrderDetailModel();
                orderDetailModel.setOrderId(orderId);
                orderDetailModel.setProductId(o.getProductId());
                orderDetailModel.setAmount(o.getAmount());
                return orderDetailModel;
            }).collect(Collectors.toList());
            model.setOrderDetailList(orderDetailModelList);
            orderModelList.add(model);
        }

        List<OrderDTO> orderDTOList = null;
        if (!orderModelList.isEmpty()) {
            orderDTOList = orderModelList.stream().map(o -> {
                OrderDTO orderDTO = new OrderDTO();
                BeanUtils.copyProperties(o, orderDTO);
                orderDTO.setId(String.valueOf(o.getId()));
                List<OrderDetailModel> orderDetailModelList = o.getOrderDetailList();
                List<OrderDetailDTO> orderDetailDTOList = orderDetailModelList.stream().map(oInternal1 -> {
                    OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
                    BeanUtils.copyProperties(oInternal1, orderDetailDTO);
                    return orderDetailDTO;
                }).collect(Collectors.toList());
                orderDTO.setOrderDetailList(orderDetailDTOList);

                return orderDTO;
            }).collect(Collectors.toList());
        }

        if (orderDTOList == null) {
            orderDTOList = new ArrayList<>();
        }

        // 查询 redis 缓存中是否有订单信息
        String userIdStr = String.valueOf(userId);
        String key = CacheKeyPrefixOrderInCacheBeforeCassandraIndexCreate + userIdStr;
        Long length = redisTemplate.opsForHash().size(key);
        if (length != null && length > 0) {
            List<String> values = redisTemplate.opsForHash().values(key).stream().map(o -> (String) o).collect(Collectors.toList());
            if (values != null && !values.isEmpty()) {
                for (String JSON : values) {
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
                        orderDTO.setId(String.valueOf(orderModel.getId()));
                        orderDTO.setOrderDetailList(orderDetailDTOList);
                        orderDTOList.add(0, orderDTO);
                    }
                }
            }
        }

        // 填充商品信息
        this.fillupOrderListProductInfo(orderDTOList);

        orderDTOList.sort(((o1, o2) -> {
            Long id1 = Long.parseLong(o1.getId());
            Long id2 = Long.parseLong(o2.getId());
            return id2.compareTo(id1);
        }));

        return orderDTOList;
    }

    /**
     * 商家查询指定日期范围+指定状态的订单
     *
     * @param merchantId
     * @param status
     * @param startTime
     * @param endTime
     * @return
     * @throws Exception
     */
    public List<OrderDTO> listByMerchantIdAndStatus(
            Long merchantId,
            Status status,
            LocalDateTime startTime,
            LocalDateTime endTime) throws Exception {
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        Date dateStartTime = Date.from(startTime.atZone(zoneId).toInstant());
        Date dateEndTime = Date.from(endTime.atZone(zoneId).toInstant());
        BoundStatement bound = preparedStatementListByMerchantIdAndStatus.bind(
                merchantId, status.name(),
                dateStartTime,
                dateEndTime/*, 15*/);
        ResultSet result = session.execute(bound);
        List<OrderModel> orderModelList = new ArrayList<>();
        for (Row row : result) {
            Long merchantIdTemporary = row.getLong("merchant_id");
            long userId = row.getLong("user_id");
            long orderId = row.getLong("order_id");
            Status statusTemporary = Status.valueOf(row.getString("status"));
            LocalDateTime createTime = row.getTimestamp("create_time").toInstant().atZone(zoneId).toLocalDateTime();
            String orderDetailJSON = row.getString("order_detail_json");

            OrderModel model = new OrderModel();
            model.setId(orderId);
            model.setUserId(userId);
            model.setStatus(statusTemporary);
            model.setCreateTime(createTime);
            model.setMerchantId(merchantIdTemporary);
            List<OrderIndexListByUserIdModel.OrderIndexListByUserIdDetailModel> detailList =
                    this.objectMapper.readValue(orderDetailJSON, new TypeReference<List<OrderIndexListByUserIdModel.OrderIndexListByUserIdDetailModel>>() {
                    });
            List<OrderDetailModel> orderDetailModelList = detailList.stream().map(o -> {
                OrderDetailModel orderDetailModel = new OrderDetailModel();
                orderDetailModel.setOrderId(orderId);
                orderDetailModel.setProductId(o.getProductId());
                orderDetailModel.setAmount(o.getAmount());
                return orderDetailModel;
            }).collect(Collectors.toList());
            model.setOrderDetailList(orderDetailModelList);
            orderModelList.add(model);
        }

        List<OrderDTO> orderDTOList = null;
        if (!orderModelList.isEmpty()) {
            orderDTOList = orderModelList.stream().map(o -> {
                OrderDTO orderDTO = new OrderDTO();
                BeanUtils.copyProperties(o, orderDTO);
                orderDTO.setId(String.valueOf(o.getId()));
                List<OrderDetailModel> orderDetailModelList = o.getOrderDetailList();
                List<OrderDetailDTO> orderDetailDTOList = orderDetailModelList.stream().map(oInternal1 -> {
                    OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
                    BeanUtils.copyProperties(oInternal1, orderDetailDTO);
                    return orderDetailDTO;
                }).collect(Collectors.toList());
                orderDTO.setOrderDetailList(orderDetailDTOList);

                return orderDTO;
            }).collect(Collectors.toList());
        }

        if (orderDTOList == null) {
            orderDTOList = new ArrayList<>();
        }

        // 填充商品信息
        this.fillupOrderListProductInfo(orderDTOList);

        orderDTOList.sort(((o1, o2) -> {
            Long id1 = Long.parseLong(o1.getId());
            Long id2 = Long.parseLong(o2.getId());
            return id2.compareTo(id1);
        }));

        return orderDTOList;
    }

    /**
     * 商家查询指定日期范围+所有状态的订单
     *
     * @param merchantId
     * @param startTime
     * @param endTime
     * @return
     */
    public List<OrderDTO> listByMerchantIdAndWithoutStatus(
            Long merchantId,
            LocalDateTime startTime,
            LocalDateTime endTime) throws Exception {
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        Date dateStartTime = Date.from(startTime.atZone(zoneId).toInstant());
        Date dateEndTime = Date.from(endTime.atZone(zoneId).toInstant());
        BoundStatement bound = preparedStatementListByMerchantIdAndWithoutStatus.bind(
                merchantId,
                dateStartTime,
                dateEndTime/*, 15*/);
        ResultSet result = session.execute(bound);
        List<OrderModel> orderModelList = new ArrayList<>();
        for (Row row : result) {
            Long merchantIdTemporary = row.getLong("merchant_id");
            long userId = row.getLong("user_id");
            long orderId = row.getLong("order_id");
            Status statusTemporary = Status.valueOf(row.getString("status"));
            LocalDateTime createTime = row.getTimestamp("create_time").toInstant().atZone(zoneId).toLocalDateTime();
            String orderDetailJSON = row.getString("order_detail_json");

            OrderModel model = new OrderModel();
            model.setId(orderId);
            model.setUserId(userId);
            model.setStatus(statusTemporary);
            model.setCreateTime(createTime);
            model.setMerchantId(merchantIdTemporary);
            List<OrderIndexListByUserIdModel.OrderIndexListByUserIdDetailModel> detailList =
                    this.objectMapper.readValue(orderDetailJSON, new TypeReference<List<OrderIndexListByUserIdModel.OrderIndexListByUserIdDetailModel>>() {
                    });
            List<OrderDetailModel> orderDetailModelList = detailList.stream().map(o -> {
                OrderDetailModel orderDetailModel = new OrderDetailModel();
                orderDetailModel.setOrderId(orderId);
                orderDetailModel.setProductId(o.getProductId());
                orderDetailModel.setAmount(o.getAmount());
                return orderDetailModel;
            }).collect(Collectors.toList());
            model.setOrderDetailList(orderDetailModelList);
            orderModelList.add(model);
        }

        List<OrderDTO> orderDTOList = null;
        if (!orderModelList.isEmpty()) {
            orderDTOList = orderModelList.stream().map(o -> {
                OrderDTO orderDTO = new OrderDTO();
                BeanUtils.copyProperties(o, orderDTO);
                orderDTO.setId(String.valueOf(o.getId()));
                List<OrderDetailModel> orderDetailModelList = o.getOrderDetailList();
                List<OrderDetailDTO> orderDetailDTOList = orderDetailModelList.stream().map(oInternal1 -> {
                    OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
                    BeanUtils.copyProperties(oInternal1, orderDetailDTO);
                    return orderDetailDTO;
                }).collect(Collectors.toList());
                orderDTO.setOrderDetailList(orderDetailDTOList);

                return orderDTO;
            }).collect(Collectors.toList());
        }

        if (orderDTOList == null) {
            orderDTOList = new ArrayList<>();
        }

        // 填充商品信息
        this.fillupOrderListProductInfo(orderDTOList);

        orderDTOList.sort(((o1, o2) -> {
            Long id1 = Long.parseLong(o1.getId());
            Long id2 = Long.parseLong(o2.getId());
            return id2.compareTo(id1);
        }));

        return orderDTOList;
    }
}
