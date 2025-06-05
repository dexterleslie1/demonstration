package com.future.demo.mapper;

import com.datastax.driver.core.*;
import com.future.common.exception.BusinessException;
import com.future.demo.entity.DeleteStatus;
import com.future.demo.entity.OrderModel;
import com.future.demo.entity.Status;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class OrderMapper {

    @Resource
    Session session;

    private PreparedStatement preparedStatementInsert;
    private PreparedStatement preparedStatementListByUserIdAndStatus;

    @PostConstruct
    public void init() {
        String cql = "INSERT INTO t_order(id,user_id,create_time,status,delete_status) " +
                "VALUES (?, ?, ?, ?, ?)";
        preparedStatementInsert = session.prepare(cql);
        preparedStatementInsert.setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);

        StringBuilder builder = new StringBuilder("select order_id from t_order_list_by_userId where user_id=?");
        builder.append(" and status=?");
        builder.append(" and create_time>=? and create_time<=?");
        builder.append(" limit ?");
        cql = builder.toString();
        preparedStatementListByUserIdAndStatus = session.prepare(cql);
        preparedStatementListByUserIdAndStatus.setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);
    }

    public void insertBatch(List<OrderModel> orderModelList) throws BusinessException {
        // region 订单ID为主键的订单表

        // 创建批量语句
        BatchStatement batch = new BatchStatement(BatchStatement.Type.LOGGED);

        // 添加多个订单到批量语句
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        for (int i = 0; i < orderModelList.size(); i++) {
            OrderModel model = orderModelList.get(i);
            BoundStatement bound = preparedStatementInsert.bind(
                    model.getId(),
                    model.getUserId(),
                    Date.from(model.getCreateTime().atZone(zoneId).toInstant()),
                    model.getStatus().name(),
                    model.getDeleteStatus().name()
            );
            batch = batch.add(bound);
        }

        // 执行批量插入
        ResultSet result = session.execute(batch);
        if (!result.wasApplied()) {
            throw new BusinessException("t_order 批量插入失败");
        }
    }

    public void truncate() throws BusinessException {
        String cql = "truncate table t_order";
        ResultSet result = session.execute(cql);
        if (!result.wasApplied()) {
            throw new BusinessException("truncate t_order 表失败");
        }

        cql = "truncate table t_order_list_by_userId";
        result = session.execute(cql);
        if (!result.wasApplied()) {
            throw new BusinessException("truncate t_order_list_by_userId 表失败");
        }
    }

    /*@Select("SELECT * FROM t_order")*/
    public List<OrderModel> selectAll() {
        String cql = "select * from t_order";
        ResultSet result = session.execute(cql);
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
//        StringBuilder builder = new StringBuilder();
//        builder.append("select * from t_order_list_by_userId where user_id=?");
//
//        if (status == null) {
//            builder.append(" and status in(");
//            int counter = 0;
//            for (Status value : Status.values()) {
//                builder.append("'").append(value.name()).append("'");
//                if (counter + 1 < Status.values().length) {
//                    builder.append(",");
//                }
//                counter++;
//            }
//            builder.append(")");
//        } else {
//            builder.append(" and status=?");
//        }
//
//        if (deleteStatus == null) {
//            builder.append(" and delete_status in(");
//            int counter = 0;
//            for (DeleteStatus value : DeleteStatus.values()) {
//                builder.append("'").append(value.name()).append("'");
//                if (counter + 1 < DeleteStatus.values().length) {
//                    builder.append(",");
//                }
//                counter++;
//            }
//            builder.append(")");
//        } else {
//            builder.append(" and delete_status=?");
//        }
//
//        builder.append(" and create_time>=? and create_time<=?");
//        builder.append(" order by status desc,delete_status desc,create_time desc,order_id desc");
//        builder.append(" limit ?");
//
//        String cql = builder.toString();
//        PreparedStatement prepared = session.prepare(cql);
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        BoundStatement bound;
//        if (status != null) {
        bound = preparedStatementListByUserIdAndStatus.bind(
                userId, status.name(),
                Date.from(startTime.atZone(zoneId).toInstant())
                , Date.from(endTime.atZone(zoneId).toInstant())
                , size.intValue());
//        } else {
//            bound = preparedStatementListByUserIdAndStatus.bind(
//                    userId,
//                    Date.from(startTime.atZone(zoneId).toInstant())
//                    , Date.from(endTime.atZone(zoneId).toInstant())
//                    , size.intValue());
//        }
        ResultSet result = session.execute(bound);
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
        PreparedStatement prepared = session.prepare(cql);
        BoundStatement bound = prepared.bind(orderId);
        ResultSet result = session.execute(bound);
        Row row = result.one();
        return convertRowToOrderModel(row);
    }

    OrderModel convertRowToOrderModel(Row row) {
        if (row == null) {
            return null;
        }

        long id = row.getLong("order_id");
//        long id = row.getLong("id");
//        long userId = row.getLong("user_id");
//        Status status = Status.valueOf(row.getString("status"));
//        ZoneId zoneId = ZoneId.of("Asia/Shanghai");

//        Date dateTemporary = row.getTimestamp("pay_time");
//        Instant payTimeInstant = dateTemporary == null ? null : dateTemporary.toInstant();
//        LocalDateTime payTime = payTimeInstant == null ? null : LocalDateTime.ofInstant(payTimeInstant, zoneId);
//
//        dateTemporary = row.getTimestamp("delivery_time");
//        Instant deliveryTimeInstant = dateTemporary == null ? null : dateTemporary.toInstant();
//        LocalDateTime deliveryTime = deliveryTimeInstant == null ? null : LocalDateTime.ofInstant(deliveryTimeInstant, zoneId);
//
//        dateTemporary = row.getTimestamp("received_time");
//        Instant receivedTimeInstant = dateTemporary == null ? null : dateTemporary.toInstant();
//        LocalDateTime receivedTime = receivedTimeInstant == null ? null : LocalDateTime.ofInstant(receivedTimeInstant, zoneId);
//
//        dateTemporary = row.getTimestamp("cancel_time");
//        Instant cancelTimeInstant = dateTemporary == null ? null : dateTemporary.toInstant();
//        LocalDateTime cancelTime = cancelTimeInstant == null ? null : LocalDateTime.ofInstant(cancelTimeInstant, zoneId);

//        DeleteStatus deleteStatus = DeleteStatus.valueOf(row.getString("delete_status"));
//
//        Date dateTemporary = row.getTimestamp("create_time");
//        Instant createTimeInstant = dateTemporary == null ? null : dateTemporary.toInstant();
//        LocalDateTime createTime = createTimeInstant == null ? null : LocalDateTime.ofInstant(createTimeInstant, zoneId);

        OrderModel model = new OrderModel();
        model.setId(id);
//        model.setUserId(userId);
//        model.setStatus(status);
//        model.setPayTime(payTime);
//        model.setDeliveryTime(deliveryTime);
//        model.setReceivedTime(receivedTime);
//        model.setCancelTime(cancelTime);
//        model.setDeleteStatus(deleteStatus);
//        model.setCreateTime(createTime);
        return model;
    }
//
//    @Select("select min(id) from t_order")
//    BigInteger getIdMin();
//
//    @Select("select id from t_order where id>#{lowerIdBoundary} order by id asc limit #{pageSize}")
//    BigInteger[] listRangeIds(@Param("lowerIdBoundary") BigInteger lowerIdBoundary, @Param("pageSize") Integer pageSize);
}
