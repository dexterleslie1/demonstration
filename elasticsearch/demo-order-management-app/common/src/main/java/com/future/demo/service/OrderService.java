package com.future.demo.service;

import cn.hutool.core.util.RandomUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.search.Hit;
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
import com.future.demo.util.OrderRandomlyUtil;
import com.tencent.devops.leaf.service.SnowflakeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {

    @Value("${productStock:100000000}")
    Integer productStock;

    @Resource
    OrderRandomlyUtil orderRandomlyUtil;
    @Autowired
    SnowflakeService snowflakeService;
    @Resource
    ElasticsearchClient client;

    @PostConstruct
    public void init() throws IOException {
        // region 创建 t_order 和 t_order_detail 表

        String index = "t_order";
        CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(index)
                .mappings(m -> m
                        .properties("id", p -> p.long_(l -> l.store(false)))
                        .properties("userId", p -> p.long_(t -> t.store(false)))
                        .properties("status", p -> p.keyword(t -> t.store(true)))
                        .properties("payTime", p -> p.date(t -> t.store(false).format("yyyy-MM-dd HH:mm:ss||epoch_millis")))
                        .properties("deliveryTime", p -> p.date(t -> t.store(false).format("yyyy-MM-dd HH:mm:ss||epoch_millis")))
                        .properties("receivedTime", p -> p.date(t -> t.store(false).format("yyyy-MM-dd HH:mm:ss||epoch_millis")))
                        .properties("cancelTime", p -> p.date(t -> t.store(false).format("yyyy-MM-dd HH:mm:ss||epoch_millis")))
                        .properties("deleteStatus", p -> p.keyword(t -> t.store(false)))
                        .properties("createTime", p -> p.date(t -> t.store(false).format("yyyy-MM-dd HH:mm:ss||epoch_millis")))
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
                .mappings(m -> m
                        .properties("id", p -> p.long_(l -> l.store(false)))
                        .properties("orderId", p -> p.long_(t -> t.store(false)))
                        .properties("userId", p -> p.long_(t -> t.store(true)))
                        .properties("productId", p -> p.long_(t -> t.store(false)))
                        .properties("merchantId", p -> p.long_(t -> t.store(false)))
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
    }

    public void insertBatch() throws IOException {
        List<OrderModel> orderModelList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Long userId = this.orderRandomlyUtil.getUserIdRandomly();
            // 创建订单
            OrderModel orderModel = new OrderModel();

            // biginteger 类型
            Long orderId = this.snowflakeService.getId("order").getId();
            orderModel.setId(orderId);

            orderModel.setUserId(userId);
            LocalDateTime createTime = OrderRandomlyUtil.getCreateTimeRandomly();
            orderModel.setCreateTime(createTime);

            DeleteStatus deleteStatus = OrderRandomlyUtil.getDeleteStatusRandomly();
            orderModel.setDeleteStatus(deleteStatus);

            Status status = OrderRandomlyUtil.getStatusRandomly();
            orderModel.setStatus(status);
            orderModelList.add(orderModel);
        }
        this.bulkInsertionOrder("t_order", orderModelList);

        List<Long> productIdList = orderModelList.stream().map(o -> {
            Long productId = this.orderRandomlyUtil.getProductIdRandomly();
            return productId;
        }).distinct().collect(Collectors.toList());
        List<OrderDetailModel> orderDetailModelList = orderModelList.stream().map(o -> {
            Long productId = productIdList.get(RandomUtil.randomInt(0, productIdList.size()));

            Integer amount = 1;
            Long userId = o.getUserId();

            OrderDetailModel orderDetailModel = new OrderDetailModel();

            Long orderDetailId = this.snowflakeService.getId("orderDetail").getId();
            orderDetailModel.setId(orderDetailId);

            orderDetailModel.setOrderId(o.getId());
            orderDetailModel.setUserId(userId);
            orderDetailModel.setProductId(productId);
            orderDetailModel.setMerchantId(RandomUtil.randomLong(0, Long.MAX_VALUE));
            orderDetailModel.setAmount(amount);
            return orderDetailModel;
        }).collect(Collectors.toList());
        this.bulkInsertionOrderDetail("t_order_detail", orderDetailModelList);
    }

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
                .refresh(Refresh.False) // 设置刷新策略为 IMMEDIATE
        );

        // 执行批量请求
        BulkResponse response = client.bulk(request);

        if (response.errors()) {
            throw new BusinessException("批量插入中有错误发生: " + response.items().get(0).error().reason());
        }
    }

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
                .refresh(Refresh.False) // 设置刷新策略为 IMMEDIATE
        );

        // 执行批量请求
        BulkResponse response = client.bulk(request);

        if (response.errors()) {
            throw new BusinessException("批量插入中有错误发生: " + response.items().get(0).error().reason());
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
        OrderModel orderModel = null;//this.orderMapper.getById(orderId);
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
        List<OrderModel> orderModelList = null;//this.orderMapper.listByUserId(userId, null, DeleteStatus.Normal, startTime, endTime, 0L, 20L);
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
                .from(0)
                .size(20)
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
     * @param deleteStatus
     * @param startTime
     * @param endTime
     * @return
     */
    public List<OrderDTO> listByMerchantIdAndWithoutStatus(
            Long merchantId,
            DeleteStatus deleteStatus,
            LocalDateTime startTime,
            LocalDateTime endTime) throws IOException {
        List<OrderModel> orderModelList = null;//this.orderMapper.listByMerchantId(merchantId, null, deleteStatus, startTime, endTime, 0L, 20L);
        return convertOrderEntityToOrderDTO(orderModelList);
    }

    /**
     * 商家查询指定日期范围+指定状态的订单
     *
     * @param merchantId
     * @param status
     * @param deleteStatus
     * @param startTime
     * @param endTime
     * @return
     */
    public List<OrderDTO> listByMerchantIdAndStatus(
            Long merchantId,
            Status status,
            DeleteStatus deleteStatus,
            LocalDateTime startTime,
            LocalDateTime endTime) throws IOException {
        List<OrderModel> orderModelList = null;//this.orderMapper.listByMerchantId(merchantId, status, deleteStatus, startTime, endTime, 0L, 20L);
        return convertOrderEntityToOrderDTO(orderModelList);
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
}
