package com.future.demo.mapper;

import com.datastax.driver.core.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.common.exception.BusinessException;
import com.future.demo.config.PrometheusCustomMonitor;
import com.future.demo.entity.OrderIndexListByUserIdModel;
import com.future.demo.entity.OrderModel;
import com.future.demo.entity.Status;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class CassandraMapper {
    @Resource
    Session session;
    @Resource
    ObjectMapper objectMapper;
    @Resource
    PrometheusCustomMonitor monitor;

    private PreparedStatement preparedStatementInsertionListByUserId;
    private PreparedStatement preparedStatementInsertionListByMerchantId;
    private PreparedStatement preparedStatementUpdateIncreaseCount;

    @PostConstruct
    public void init() {
        String cql = "INSERT INTO t_order_list_by_userId(user_id,create_time,status,order_id,merchant_id,pay_time,delivery_time,received_time,cancel_time,order_detail_json) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        preparedStatementInsertionListByUserId = session.prepare(cql);
        preparedStatementInsertionListByUserId.setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);

        cql = "INSERT INTO t_order_list_by_merchantId(merchant_id,create_time,status,order_id,user_id,pay_time,delivery_time,received_time,cancel_time,order_detail_json) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        preparedStatementInsertionListByMerchantId = session.prepare(cql);
        preparedStatementInsertionListByMerchantId.setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);

        cql = "update t_count set count=count+? where flag=?";
        preparedStatementUpdateIncreaseCount = session.prepare(cql);
        preparedStatementUpdateIncreaseCount.setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);
    }

    /**
     * 批量建立 listByUserId 索引
     */
    public void insertBatchOrderIndexListByUserId(List<OrderModel> orderModelList) throws Exception {
        List<OrderIndexListByUserIdModel> modelList = new ArrayList<>();
        for (int i = 0; i < orderModelList.size(); i++) {
            OrderModel orderModel = orderModelList.get(i);
            Long orderId = orderModel.getId();
            Long userId = orderModel.getUserId();
            LocalDateTime createTime = orderModel.getCreateTime();
            Status status = orderModel.getStatus();
            Long merchantId = orderModel.getMerchantId();
            LocalDateTime payTime = orderModel.getPayTime();
            LocalDateTime deliveryTime = orderModel.getDeliveryTime();
            LocalDateTime receivedTime = orderModel.getReceivedTime();
            LocalDateTime cancelTime = orderModel.getCancelTime();

            // 创建订单
            OrderIndexListByUserIdModel model = new OrderIndexListByUserIdModel();
            model.setOrderId(orderId);
            model.setUserId(userId);
            model.setCreateTime(createTime);
            model.setStatus(status);
            model.setMerchantId(merchantId);
            model.setPayTime(payTime);
            model.setDeliveryTime(deliveryTime);
            model.setReceivedTime(receivedTime);
            model.setCancelTime(cancelTime);
            model.setDetailModelList(Collections.singletonList(new OrderIndexListByUserIdModel.OrderIndexListByUserIdDetailModel() {{
                setProductId(orderModel.getOrderDetailList().get(0).getProductId());
                setAmount(orderModel.getOrderDetailList().get(0).getAmount());
            }}));
            modelList.add(model);
        }

        // 创建批量语句
        BatchStatement batch = new BatchStatement(BatchStatement.Type.LOGGED);

        // 添加多个订单到批量语句
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        for (int i = 0; i < modelList.size(); i++) {
            OrderIndexListByUserIdModel model = modelList.get(i);
            String orderDetailJSON = this.objectMapper.writeValueAsString(model.getDetailModelList());

            Date createTime = Date.from(model.getCreateTime().atZone(zoneId).toInstant());
            BoundStatement bound = preparedStatementInsertionListByUserId.bind(
                    model.getUserId(),
                    createTime,
                    model.getStatus().name(),
                    model.getOrderId(),
                    model.getMerchantId(),
                    model.getPayTime() == null ? null : Date.from(model.getPayTime().atZone(zoneId).toInstant()),
                    model.getDeliveryTime() == null ? null : Date.from(model.getDeliveryTime().atZone(zoneId).toInstant()),
                    model.getReceivedTime() == null ? null : Date.from(model.getReceivedTime().atZone(zoneId).toInstant()),
                    model.getCancelTime() == null ? null : Date.from(model.getCancelTime().atZone(zoneId).toInstant()),
                    orderDetailJSON
            );
            batch = batch.add(bound);
        }

        // 执行批量插入
        ResultSet result = session.execute(batch);
        if (!result.wasApplied()) {
            throw new BusinessException("t_order_list_by_userId 批量插入失败");
        }

        this.updateIncreaseCount("orderListByUserId", modelList.size());
        this.monitor.incrementCassandraIndexOrderListByUserId(modelList.size());
    }

    /**
     * 批量建立 listByMerchantId 索引
     */
    public void insertBatchOrderIndexListByMerchantId(List<OrderModel> orderModelList) throws Exception {
        List<OrderIndexListByUserIdModel> modelList = new ArrayList<>();
        for (int i = 0; i < orderModelList.size(); i++) {
            OrderModel orderModel = orderModelList.get(i);
            Long orderId = orderModel.getId();
            Long userId = orderModel.getUserId();
            LocalDateTime createTime = orderModel.getCreateTime();
            Status status = orderModel.getStatus();
            Long merchantId = orderModel.getMerchantId();
            LocalDateTime payTime = orderModel.getPayTime();
            LocalDateTime deliveryTime = orderModel.getDeliveryTime();
            LocalDateTime receivedTime = orderModel.getReceivedTime();
            LocalDateTime cancelTime = orderModel.getCancelTime();

            // 创建订单
            OrderIndexListByUserIdModel model = new OrderIndexListByUserIdModel();
            model.setOrderId(orderId);
            model.setUserId(userId);
            model.setCreateTime(createTime);
            model.setStatus(status);
            model.setMerchantId(merchantId);
            model.setPayTime(payTime);
            model.setDeliveryTime(deliveryTime);
            model.setReceivedTime(receivedTime);
            model.setCancelTime(cancelTime);
            model.setDetailModelList(Collections.singletonList(new OrderIndexListByUserIdModel.OrderIndexListByUserIdDetailModel() {{
                setProductId(orderModel.getOrderDetailList().get(0).getProductId());
                setAmount(orderModel.getOrderDetailList().get(0).getAmount());
            }}));
            modelList.add(model);
        }

        // region 订单ID为主键的订单表

        // 创建批量语句
        BatchStatement batch = new BatchStatement(BatchStatement.Type.LOGGED);

        // 添加多个订单到批量语句
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        for (int i = 0; i < modelList.size(); i++) {
            OrderIndexListByUserIdModel model = modelList.get(i);
            String orderDetailJSON = this.objectMapper.writeValueAsString(model.getDetailModelList());

            Date createTime = Date.from(model.getCreateTime().atZone(zoneId).toInstant());
            BoundStatement bound = preparedStatementInsertionListByMerchantId.bind(
                    model.getMerchantId(),
                    createTime,
                    model.getStatus().name(),
                    model.getOrderId(),
                    model.getUserId(),
                    model.getPayTime() == null ? null : Date.from(model.getPayTime().atZone(zoneId).toInstant()),
                    model.getDeliveryTime() == null ? null : Date.from(model.getDeliveryTime().atZone(zoneId).toInstant()),
                    model.getReceivedTime() == null ? null : Date.from(model.getReceivedTime().atZone(zoneId).toInstant()),
                    model.getCancelTime() == null ? null : Date.from(model.getCancelTime().atZone(zoneId).toInstant()),
                    orderDetailJSON
            );
            batch = batch.add(bound);
        }

        // 执行批量插入
        ResultSet result = session.execute(batch);
        if (!result.wasApplied()) {
            throw new BusinessException("t_order_list_by_merchantId 批量插入失败");
        }

        // endregion

        this.updateIncreaseCount("orderListByMerchantId", modelList.size());
        this.monitor.incrementCassandraIndexOrderListByMerchantId(modelList.size());
    }

    /**
     * 更新 t_count
     *
     * @param flag
     * @param count
     * @throws BusinessException
     */
    public void updateIncreaseCount(String flag, long count) throws BusinessException {
        BoundStatement boundStatement = preparedStatementUpdateIncreaseCount.bind(count, flag);
        ResultSet resultSet = session.execute(boundStatement);
        if (!resultSet.wasApplied()) {
            throw new BusinessException("执行 updateIncreaseCount 失败，count=1");
        }
    }
}
