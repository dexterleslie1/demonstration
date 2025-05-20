package com.future.demo.mapper;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import com.future.common.exception.BusinessException;
import com.future.demo.entity.DeleteStatus;
import com.future.demo.entity.OrderModel;
import com.future.demo.entity.Status;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderMapper {

    @Resource
    CqlSession cqlSession;

    public void insertBatch(List<OrderModel> orderModelList) throws BusinessException {
        // region 订单ID为主键的订单表

        String cql = "INSERT INTO t_order(id,user_id,create_time,status,delete_status) " +
                "VALUES (?, ?, ?, ?, ?)";

        PreparedStatement prepared = cqlSession.prepare(cql);

        // 创建批量语句
        BatchStatement batch = BatchStatement.newInstance(BatchType.LOGGED);

        // 添加多个订单到批量语句
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        for (int i = 0; i < orderModelList.size(); i++) {
            OrderModel model = orderModelList.get(i);
            BoundStatement bound = prepared.bind(
                    model.getId(),
                    model.getUserId(),
                    model.getCreateTime().atZone(zoneId).toInstant(),
                    model.getStatus().name(),
                    model.getDeleteStatus().name()
            );
            batch = batch.add(bound);
        }

        // 执行批量插入
        ResultSet result = cqlSession.execute(batch);
        if (!result.wasApplied()) {
            throw new BusinessException("t_order 批量插入失败");
        }

        // endregion

        // region 根据用户ID查询的订单索引表

        cql = "INSERT INTO t_order_list_by_userId(id,user_id,create_time,status,delete_status) " +
                "VALUES (?, ?, ?, ?, ?)";

        prepared = cqlSession.prepare(cql);

        // 创建批量语句
        batch = BatchStatement.newInstance(BatchType.LOGGED);

        // 添加多个订单到批量语句
        for (int i = 0; i < orderModelList.size(); i++) {
            OrderModel model = orderModelList.get(i);
            BoundStatement bound = prepared.bind(
                    model.getId(),
                    model.getUserId(),
                    model.getCreateTime().atZone(zoneId).toInstant(),
                    model.getStatus().name(),
                    model.getDeleteStatus().name()
            );
            batch = batch.add(bound);
        }

        // 执行批量插入
        result = cqlSession.execute(batch);
        if (!result.wasApplied()) {
            throw new BusinessException("t_order_list_by_userId 批量插入失败");
        }

        // endregion
    }

    public void truncate() throws BusinessException {
        String cql = "truncate table t_order";
        ResultSet result = this.cqlSession.execute(cql);
        if (!result.wasApplied()) {
            throw new BusinessException("truncate t_order 表失败");
        }

        cql = "truncate table t_order_list_by_userId";
        result = this.cqlSession.execute(cql);
        if (!result.wasApplied()) {
            throw new BusinessException("truncate t_order_list_by_userId 表失败");
        }
    }

    /*@Select("SELECT * FROM t_order")*/
    public List<OrderModel> selectAll() {
        String cql = "select * from t_order";
        ResultSet result = this.cqlSession.execute(cql);
        List<OrderModel> orderModelList = new ArrayList<>();
        for (Row row : result) {
            OrderModel model = convertRowToOrderModel(row);
            if (model != null) {
                orderModelList.add(model);
            }
        }
        return orderModelList;
    }

    public List<OrderModel> listByUserId(Long userId,
                                         Status status,
                                         DeleteStatus deleteStatus,
                                         LocalDateTime startTime,
                                         LocalDateTime endTime,
                                         Long start,
                                         Long size) {
        /*<select id="listByUserId" resultType="com.future.demo.entity.OrderModel">
        select *
        from t_order
        <where>
            <if test="userId!=null">
                userId=#{userId}
            </if>
            <if test="status!=null">
                and status=#{status}
            </if>
            <if test="deleteStatus!=null">
                and deleteStatus=#{deleteStatus}
            </if>
            and createTime&gt;=#{startTime} and createTime&lt;=#{endTime}
        </where>
        order by id desc
        limit #{start},#{size}
    </select>*/
        StringBuilder builder = new StringBuilder();
        builder.append("select * from t_order_list_by_userId where user_id=?");

        if (status == null) {
            builder.append(" and status in(");
            int counter = 0;
            for (Status value : Status.values()) {
                builder.append("'").append(value.name()).append("'");
                if (counter + 1 < Status.values().length) {
                    builder.append(",");
                }
                counter++;
            }
            builder.append(")");
        } else {
            builder.append(" and status=?");
        }

        if (deleteStatus == null) {
            builder.append(" and delete_status in(");
            int counter = 0;
            for (DeleteStatus value : DeleteStatus.values()) {
                builder.append("'").append(value.name()).append("'");
                if (counter + 1 < DeleteStatus.values().length) {
                    builder.append(",");
                }
                counter++;
            }
            builder.append(")");
        } else {
            builder.append(" and delete_status=?");
        }

        builder.append(" and create_time>=? and create_time<=?");
        builder.append(" order by status desc,delete_status desc,create_time desc,id desc");
        builder.append(" limit ?");

        String cql = builder.toString();
        PreparedStatement prepared = this.cqlSession.prepare(cql);
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        BoundStatement bound;
        if (status != null) {
            bound = prepared.bind(
                    userId, status.name(),
                    startTime.atZone(zoneId).toInstant(), endTime.atZone(zoneId).toInstant(), size.intValue());
        } else {
            bound = prepared.bind(
                    userId,
                    startTime.atZone(zoneId).toInstant(), endTime.atZone(zoneId).toInstant(), size.intValue());
        }
        ResultSet result = this.cqlSession.execute(bound);
        List<OrderModel> orderModelList = new ArrayList<>();
        for (Row row : result) {
            OrderModel model = convertRowToOrderModel(row);
            if (model != null) {
                orderModelList.add(model);
            }
        }
        return orderModelList;
    }

    public List<OrderModel> listByMerchantId(Long merchantId,
                                             Status status,
                                             DeleteStatus deleteStatus,
                                             LocalDateTime startTime,
                                             LocalDateTime endTime,
                                             Long start,
                                             Long size) {
        /*<select id="listByMerchantId" resultType="com.future.demo.entity.OrderModel">
        select tOrder.*
        from t_order tOrder
        inner join t_order_detail detail on tOrder.id=detail.orderId
        <where>
            <if test="merchantId!=null">
                merchantId=#{merchantId}
            </if>
            <if test="status!=null">
                and status=#{status}
            </if>
            <if test="deleteStatus!=null">
                and deleteStatus=#{deleteStatus}
            </if>
            and createTime&gt;=#{startTime} and createTime&lt;=#{endTime}
        </where>
        order by tOrder.id desc
        limit #{start},#{size}
    </select>*/
        /*StringBuilder builder = new StringBuilder();
        builder.append("select * from t_order where user_id=?");
        if (status != null) {
            builder.append(" and status=?");
        }
        if (deleteStatus != null) {
            builder.append(" and delete_status=?");
        }

        builder.append(" and create_time>=? and create_time<=?");
        builder.append(" order by id desc");
        builder.append(" limit ?,?");

        String cql = builder.toString();
        PreparedStatement prepared = this.cqlSession.prepare(cql);
        BoundStatement bound = prepared.bind(
                userId, status, deleteStatus, startTime, endTime, start, size);
        ResultSet result = this.cqlSession.execute(bound);
        List<OrderModel> orderModelList = new ArrayList<>();
        for (Row row : result) {
            OrderModel model = convertRowToOrderModel(row);
            if (model != null) {
                orderModelList.add(model);
            }
        }
        return orderModelList;*/
        return null;
    }

    /*@Select("select * from t_order where id=#{orderId}")*/
    public OrderModel getById(BigDecimal orderId) {
        String cql = "select * from t_order where id=?";
        PreparedStatement prepared = this.cqlSession.prepare(cql);
        BoundStatement bound = prepared.bind(orderId);
        ResultSet result = this.cqlSession.execute(bound);
        Row row = result.one();
        return convertRowToOrderModel(row);
    }

    OrderModel convertRowToOrderModel(Row row) {
        if (row == null) {
            return null;
        }
        BigDecimal id = row.getBigDecimal("id");
        long userId = row.getLong("user_id");
        Status status = Status.valueOf(row.getString("status"));
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");

        Instant payTimeInstant = row.getInstant("pay_time");
        LocalDateTime payTime = payTimeInstant == null ? null : LocalDateTime.ofInstant(payTimeInstant, zoneId);

        Instant deliveryTimeInstant = row.getInstant("delivery_time");
        LocalDateTime deliveryTime = deliveryTimeInstant == null ? null : LocalDateTime.ofInstant(deliveryTimeInstant, zoneId);

        Instant receivedTimeInstant = row.getInstant("received_time");
        LocalDateTime receivedTime = receivedTimeInstant == null ? null : LocalDateTime.ofInstant(receivedTimeInstant, zoneId);

        Instant cancelTimeInstant = row.getInstant("cancel_time");
        LocalDateTime cancelTime = cancelTimeInstant == null ? null : LocalDateTime.ofInstant(cancelTimeInstant, zoneId);

        DeleteStatus deleteStatus = DeleteStatus.valueOf(row.getString("delete_status"));

        Instant createTimeInstant = row.getInstant("create_time");
        LocalDateTime createTime = createTimeInstant == null ? null : LocalDateTime.ofInstant(createTimeInstant, zoneId);

        OrderModel model = new OrderModel();
        model.setId(id);
        model.setUserId(userId);
        model.setStatus(status);
        model.setPayTime(payTime);
        model.setDeliveryTime(deliveryTime);
        model.setReceivedTime(receivedTime);
        model.setCancelTime(cancelTime);
        model.setDeleteStatus(deleteStatus);
        model.setCreateTime(createTime);
        return model;
    }
//
//    @Select("select min(id) from t_order")
//    BigInteger getIdMin();
//
//    @Select("select id from t_order where id>#{lowerIdBoundary} order by id asc limit #{pageSize}")
//    BigInteger[] listRangeIds(@Param("lowerIdBoundary") BigInteger lowerIdBoundary, @Param("pageSize") Integer pageSize);
}
