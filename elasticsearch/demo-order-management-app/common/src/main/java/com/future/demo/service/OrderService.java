package com.future.demo.service;

import cn.hutool.core.util.RandomUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.AnalyzeRequest;
import co.elastic.clients.elasticsearch.indices.AnalyzeResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.json.JsonData;
import com.future.common.exception.BusinessException;
import com.future.demo.dto.OrderDTO;
import com.future.demo.dto.OrderDetailDTO;
import com.future.demo.entity.DeleteStatus;
import com.future.demo.entity.OrderDetailModel;
import com.future.demo.entity.OrderModel;
import com.future.demo.entity.Status;
import com.future.random.id.picker.RandomIdPickerService;
import com.tencent.devops.leaf.service.SnowflakeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {

    @Autowired
    SnowflakeService snowflakeService;
    @Resource
    ElasticsearchClient client;
    @Resource
    RandomIdPickerService randomIdPickerService;
    @Resource
    UserService userService;
    @Resource
    MerchantService merchantService;
    @Resource
    ProductService productService;

    // 中文文章
    private String articles;
    // 中文文章总字符数
    private int articlesLength;
    // 所有中文分词
    private List<String> articlesTokenList = new ArrayList<>();

    @PostConstruct
    public void init() throws IOException {
        // 加载中文文章
        ClassPathResource resource = new ClassPathResource("articles.txt");
        try (InputStream inputStream = resource.getInputStream()) {
            this.articles = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            this.articlesLength = this.articles.length();
        }

        // region 创建 t_order 和 t_order_detail 表

        String index = "t_order";
        CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(index)
                .settings(s -> s.numberOfShards("8").numberOfReplicas("1")
                        .withJson(new ByteArrayInputStream(
                                ("{\"index.analyze.max_token_count\":30000}")
                                        .getBytes(StandardCharsets.UTF_8)
                        )))
                .mappings(m -> m
                        .properties("id", p -> p.long_(l -> l.store(false)))
                        .properties("userId", p -> p.long_(t -> t.store(false)))
                        .properties("merchantId", p -> p.long_(t -> t.store(false)))
                        .properties("status", p -> p.keyword(t -> t.store(true)))
                        .properties("payTime", p -> p.date(t -> t.store(false).format("yyyy-MM-dd HH:mm:ss||epoch_millis")))
                        .properties("deliveryTime", p -> p.date(t -> t.store(false).format("yyyy-MM-dd HH:mm:ss||epoch_millis")))
                        .properties("receivedTime", p -> p.date(t -> t.store(false).format("yyyy-MM-dd HH:mm:ss||epoch_millis")))
                        .properties("cancelTime", p -> p.date(t -> t.store(false).format("yyyy-MM-dd HH:mm:ss||epoch_millis")))
                        .properties("deleteStatus", p -> p.keyword(t -> t.store(false)))
                        .properties("createTime", p -> p.date(t -> t.store(false).format("yyyy-MM-dd HH:mm:ss||epoch_millis")))
                        .properties("content",
                                p -> p.text(t -> t.store(false).index(true).analyzer("ik_max_word")
                                        // content 字段添加子字段支持拼音搜索
                                        .fields("pinyin", ft -> ft.text(t1 ->
                                                t1.analyzer("pinyin")
                                                        .store(false)))
                                ))
                ).build();
        try {
            CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest);
            Assert.isTrue(Boolean.TRUE.equals(createIndexResponse.acknowledged()), "创建 t_order 表失败");
        } catch (ElasticsearchException ex) {
            if (!ex.getMessage().contains("already exists")) {
                throw ex;
            }
        }

        index = "t_order_detail";
        createIndexRequest = new CreateIndexRequest.Builder().index(index)
                .settings(s -> s.numberOfShards("8").numberOfReplicas("1"))
                .mappings(m -> m
                        .properties("id", p -> p.long_(l -> l.store(false)))
                        .properties("orderId", p -> p.long_(t -> t.store(false)))
                        .properties("userId", p -> p.long_(t -> t.store(true)))
                        .properties("productId", p -> p.long_(t -> t.store(false)))
                        .properties("amount", p -> p.integer(t -> t.store(false)))
                ).build();
        try {
            CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest);
            Assert.isTrue(Boolean.TRUE.equals(createIndexResponse.acknowledged()), "创建 t_order_detail 表失败");
        } catch (ElasticsearchException ex) {
            if (!ex.getMessage().contains("already exists")) {
                throw ex;
            }
        }

        // endregion

        // 缓存所有中文分词
        AnalyzeRequest analyzeRequest = AnalyzeRequest.of(a -> a.index("t_order").field("content").text(this.articles));
        AnalyzeResponse analyzeResponse = client.indices().analyze(analyzeRequest);
        analyzeResponse.tokens().forEach(t -> {
            // 缓存中文分词
            this.articlesTokenList.add(t.token());
        });
        analyzeRequest = AnalyzeRequest.of(a -> a.index("t_order").field("content.pinyin").text(this.articles));
        analyzeResponse = client.indices().analyze(analyzeRequest);
        analyzeResponse.tokens().forEach(t -> {
            // 缓存中文分词
            this.articlesTokenList.add(t.token());
        });
    }

    /**
     * 批量插入订单到 ES 中
     *
     * @throws IOException
     */
    public void insertBatch(Long userId,
                            Long productId,
                            Long merchantId,
                            String[] randomArticleSliceArray,
                            int batchSize) throws IOException {
        if (batchSize <= 0) {
            batchSize = 1000;
        }
        List<OrderModel> orderModelList = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            if (userId == null) {
                userId = userService.getIdRandomly();
            }
            // 创建订单
            OrderModel orderModel = new OrderModel();

            // biginteger 类型
            Long orderId = this.snowflakeService.getId("order").getId();
            orderModel.setId(orderId);

            orderModel.setUserId(userId);

            Long merchantIdTemp;
            if (merchantId == null) {
                merchantIdTemp = merchantService.getIdRandomly();
            } else {
                merchantIdTemp = merchantId;
            }
            orderModel.setMerchantId(merchantIdTemp);

            // 注意：模拟实际不应该随机生成，否则有关于此字段的二级索引时插入性能很低
            // LocalDateTime createTime = OrderRandomlyUtil.getCreateTimeRandomly();
            LocalDateTime createTime = LocalDateTime.now();
            orderModel.setCreateTime(createTime);

            // 注意：模拟实际不应该随机生成，否则有关于此字段的二级索引时插入性能很低
            /*DeleteStatus deleteStatus = OrderRandomlyUtil.getDeleteStatusRandomly();*/
            DeleteStatus deleteStatus = DeleteStatus.Normal;
            orderModel.setDeleteStatus(deleteStatus);

            // 注意：模拟实际不应该随机生成，否则有关于此字段的二级索引时插入性能很低
            /*Status status = OrderRandomlyUtil.getStatusRandomly();*/
            Status status = Status.Unpay;
            orderModel.setStatus(status);

            String articleSlice;
            if (randomArticleSliceArray == null || randomArticleSliceArray.length == 0) {
                // 随机抽取中文文章片段
                articleSlice = randomArticleSlice(0);
            } else {
                articleSlice = randomArticleSliceArray[i];
            }
            orderModel.setContent(articleSlice);

            orderModelList.add(orderModel);
        }
        this.bulkInsertionOrder("t_order", orderModelList);

        List<Long> productIdList = orderModelList.stream().map(o -> {
            if (productId == null) {
                return productService.getIdRandomly();
            } else {
                return productId;
            }
        }).distinct().collect(Collectors.toList());
        Long finalUserId = userId;
        List<OrderDetailModel> orderDetailModelList = orderModelList.stream().map(o -> {
            Long productIdTemp = productIdList.get(RandomUtil.randomInt(0, productIdList.size()));

            Integer amount = 1;

            OrderDetailModel orderDetailModel = new OrderDetailModel();

            Long orderDetailId = this.snowflakeService.getId("orderDetail").getId();
            orderDetailModel.setId(orderDetailId);

            orderDetailModel.setOrderId(o.getId());
            orderDetailModel.setUserId(finalUserId);
            orderDetailModel.setProductId(productIdTemp);
            orderDetailModel.setAmount(amount);
            return orderDetailModel;
        }).collect(Collectors.toList());
        this.bulkInsertionOrderDetail("t_order_detail", orderDetailModelList);

        // 插入 id 列表到随机 id 选择器服务中
        randomIdPickerService.addIdList("order", orderModelList.stream().map(o -> String.valueOf(o.getId())).collect(Collectors.toList()));
    }

    /**
     * 从中文文章中随机抽取文章片段
     *
     * @return
     */
    public String randomArticleSlice(int maxLength) {
        if (maxLength <= 0) {
            maxLength = 50;
        }

        int length = RandomUtil.randomInt(1, maxLength + 1);
        int startIndex = RandomUtil.randomInt(0, this.articlesLength);
        int endIndex = startIndex + length;
        if (endIndex > this.articlesLength - 1) {
            endIndex = this.articlesLength - 1;
        }
        return this.articles.substring(startIndex, endIndex);
    }

    /**
     * 批量插入订单主表到 ES 中
     *
     * @param index
     * @param datumList
     * @throws IOException
     */
    private void bulkInsertionOrder(String index, List<OrderModel> datumList) throws IOException {
        List<BulkOperation> operations = new ArrayList<>();

        for (OrderModel datum : datumList) {
            operations.add(BulkOperation.of(b -> b
                            .index(i -> i
                                    .index(index)
                                    .id(String.valueOf(datum.getId()))
                                    .document(datum)) // 设置文档内容
                    )
            );
        }

        BulkRequest request = BulkRequest.of(b -> b
                .operations(operations)
        );

        // 执行批量请求
        BulkResponse response = client.bulk(request);

        if (response.errors()) {
            BusinessException exception = new BusinessException("批量插入中有错误发生: " + response.items().get(0).error().reason());
            log.error(exception.getMessage(), exception);
            throw exception;
        }
    }

    /**
     * 批量插入订单明细表到 ES 中
     *
     * @param index
     * @param datumList
     * @throws IOException
     */
    private void bulkInsertionOrderDetail(String index, List<OrderDetailModel> datumList) throws IOException {
        List<BulkOperation> operations = new ArrayList<>();

        for (OrderDetailModel datum : datumList) {
            operations.add(BulkOperation.of(b -> b
                            .index(i -> i
                                    .index(index)
                                    .id(String.valueOf(datum.getId()))
                                    .document(datum)) // 设置文档内容
                    )
            );
        }

        BulkRequest request = BulkRequest.of(b -> b
                .operations(operations)
        );

        // 执行批量请求
        BulkResponse response = client.bulk(request);

        if (response.errors()) {
            BusinessException exception = new BusinessException("批量插入中有错误发生: " + response.items().get(0).error().reason());
            log.error(exception.getMessage(), exception);
            throw exception;
        }
    }

    /**
     * 根据订单ID查询订单
     *
     * @param orderId
     * @return
     */
    // long 类型
    public OrderDTO getById(Long orderId) throws IOException {
        GetRequest getRequest = GetRequest.of(g -> g
                .index("t_order")       // 指定索引名
                .id(String.valueOf(orderId))          // 指定文档 ID
        );
        GetResponse<OrderModel> getResponse = client.get(getRequest, OrderModel.class); // 指定返回类型为 Map
        OrderModel orderModel = getResponse.source();
        if (orderModel == null) {
            return null;
        }

        List<OrderDTO> orderDTOList = this.convertOrderEntityToOrderDTO(Collections.singletonList(orderModel));
        return orderDTOList.get(0);
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
            LocalDateTime endTime) throws IOException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("t_order")
                .query(q -> q
                        .bool(b -> b
                                .must(
                                        q1 -> q1.term(t -> t.field("userId").value(userId))
                                ).must(
                                        q1 -> q1.term(t -> t.field("deleteStatus").value(DeleteStatus.Normal.name()))
                                ).must(
                                        q1 -> q1.range(t -> t.field("createTime").gte(JsonData.of(dateTimeFormatter.format(startTime))))
                                ).must(
                                        q1 -> q1.range(t -> t.field("createTime").lte(JsonData.of(dateTimeFormatter.format(endTime))))
                                )
                        )
                )
                .sort(so -> so
                        .field(f -> f
                                .field("id")
                                .order(SortOrder.Desc)
                        )
                )
                .size(10000)
        );
        SearchResponse<OrderModel> searchResponse = client.search(searchRequest, OrderModel.class);
        List<OrderModel> orderModelList = searchResponse.hits().hits().stream().map(Hit::source).collect(Collectors.toList());
        return this.convertOrderEntityToOrderDTO(orderModelList);
    }

    /**
     * 用户查询指定日期范围+指定状态的订单
     *
     * @param userId
     * @param status
     * @param startTime
     * @param endTime
     * @return
     */
    public List<OrderDTO> listByUserIdAndStatus(
            Long userId,
            Status status,
            LocalDateTime startTime,
            LocalDateTime endTime) throws IOException {

        // 1. 构建查询条件
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Query boolQuery = QueryBuilders.bool()
                .filter(QueryBuilders.term().field("userId").value(userId).build()._toQuery())
                .filter(QueryBuilders.term().field("status").value(status.name()).build()._toQuery())
                .filter(QueryBuilders.terms().field("deleteStatus")
                        .terms(t -> t.value(Arrays.stream(DeleteStatus.values()).map(o -> FieldValue.of(o.name())).collect(Collectors.toList())))
                        .build()._toQuery())
                .filter(QueryBuilders.range()
                        .field("createTime")
                        .gte(JsonData.of(dateTimeFormatter.format(startTime)))
                        .lte(JsonData.of(dateTimeFormatter.format(endTime)))
                        .build()._toQuery()
                )
                .build()._toQuery();

        // 2. 构建排序和分页
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("t_order")
                .query(boolQuery)
                .sort(so -> so.field(f -> f.field("id").order(SortOrder.Desc)))
                .size(10000)
        );

        // 3. 执行查询
        SearchResponse<OrderModel> response = client.search(searchRequest, OrderModel.class);

        // 4. 解析结果
        List<OrderModel> orderModelList = response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());

        return this.convertOrderEntityToOrderDTO(orderModelList);
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
            LocalDateTime endTime) throws IOException {
        // 1. 构建查询条件
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Query boolQuery = QueryBuilders.bool()
                .filter(QueryBuilders.term().field("merchantId").value(merchantId).build()._toQuery())
                .filter(QueryBuilders.terms().field("deleteStatus")
                        .terms(t -> t.value(Arrays.stream(DeleteStatus.values()).map(o -> FieldValue.of(o.name())).collect(Collectors.toList())))
                        .build()._toQuery())
                .filter(QueryBuilders.range()
                        .field("createTime")
                        .gte(JsonData.of(dateTimeFormatter.format(startTime)))
                        .lte(JsonData.of(dateTimeFormatter.format(endTime)))
                        .build()._toQuery()
                )
                .build()._toQuery();

        // 2. 构建排序和分页
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("t_order")
                .query(boolQuery)
                .sort(so -> so.field(f -> f.field("id").order(SortOrder.Desc)))
                .size(10000)
        );

        // 3. 执行查询
        SearchResponse<OrderModel> response = client.search(searchRequest, OrderModel.class);

        // 4. 解析结果
        List<OrderModel> orderModelList = response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());

        return this.convertOrderEntityToOrderDTO(orderModelList);
    }

    /**
     * 商家查询指定日期范围+指定状态的订单
     *
     * @param merchantId
     * @param status
     * @param startTime
     * @param endTime
     * @return
     */
    public List<OrderDTO> listByMerchantIdAndStatus(
            Long merchantId,
            Status status,
            LocalDateTime startTime,
            LocalDateTime endTime) throws IOException {
        // 1. 构建查询条件
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Query boolQuery = QueryBuilders.bool()
                .filter(QueryBuilders.term().field("merchantId").value(merchantId).build()._toQuery())
                .filter(QueryBuilders.term().field("status").value(status.name()).build()._toQuery())
                .filter(QueryBuilders.terms().field("deleteStatus")
                        .terms(t -> t.value(Arrays.stream(DeleteStatus.values()).map(o -> FieldValue.of(o.name())).collect(Collectors.toList())))
                        .build()._toQuery())
                .filter(QueryBuilders.range()
                        .field("createTime")
                        .gte(JsonData.of(dateTimeFormatter.format(startTime)))
                        .lte(JsonData.of(dateTimeFormatter.format(endTime)))
                        .build()._toQuery()
                )
                .build()._toQuery();

        // 2. 构建排序和分页
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("t_order")
                .query(boolQuery)
                .sort(so -> so.field(f -> f.field("id").order(SortOrder.Desc)))
                .size(10000)
        );

        // 3. 执行查询
        SearchResponse<OrderModel> response = client.search(searchRequest, OrderModel.class);

        // 4. 解析结果
        List<OrderModel> orderModelList = response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());

        return this.convertOrderEntityToOrderDTO(orderModelList);
    }

    /**
     * 把 OrderEntity 转换为 OrderDTO 类型
     *
     * @param orderList
     * @return
     */
    public List<OrderDTO> convertOrderEntityToOrderDTO(List<OrderModel> orderList) throws IOException {
        if (orderList == null || orderList.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> orderIdList = orderList.stream().map(OrderModel::getId).collect(Collectors.toList());

        List<OrderDTO> orderDTOList = null;
        if (!orderList.isEmpty()) {
            List<OrderDetailModel> orderDetailList = this.listOrderDetailByOrderIds(orderIdList);

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

        return orderDTOList;
    }

    private List<OrderDetailModel> listOrderDetailByOrderIds(List<Long> orderIdList) throws IOException {
        // 1. 构建查询条件
        Query termsQuery = QueryBuilders.terms()
                .field("orderId")
                .terms(t -> t.value(orderIdList.stream().map(FieldValue::of).collect(Collectors.toList())))
                .build()._toQuery();

        // 2. 构建排序和分页
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("t_order_detail")
                .query(termsQuery)
        );

        // 3. 执行查询
        SearchResponse<OrderDetailModel> response = client.search(searchRequest, OrderDetailModel.class);

        // 4. 解析结果
        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    /**
     * 根据中文关键词和拼音搜索订单
     *
     * @return
     */
    public List<OrderDTO> listByKeyword(String keyword) throws IOException {
        if (!StringUtils.hasText(keyword)) {
            keyword = this.articlesTokenList.get(RandomUtil.randomInt(0, this.articlesTokenList.size()));
        }
        String finalKeyword = keyword;
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("t_order")
                .query(q -> q
                        .bool(b -> b
                                .should(
                                        q1 -> q1.match(t -> t.field("content").query(finalKeyword))
                                ).should(
                                        q2 -> q2.match(t -> t.field("content.pinyin").query(finalKeyword))
                                )
                        )
                )
                //.sort(so -> so.field(f -> f.field("id").order(SortOrder.Desc)))
                .size(25)
        );
        SearchResponse<OrderModel> response = client.search(searchRequest, OrderModel.class);
        List<OrderModel> orderModelList = response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());

        return this.convertOrderEntityToOrderDTO(orderModelList);
    }
}
