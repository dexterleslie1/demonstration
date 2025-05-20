package com.future.demo.mapper;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import com.future.common.exception.BusinessException;
import com.future.demo.entity.OrderDetailModel;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderDetailMapper {

    @Resource
    CqlSession cqlSession;

    /*@Select("<script>" +
            "SELECT * FROM t_order_detail where orderId in " +
            "   <foreach item=\"item\" collection=\"orderIdList\" separator=\",\" open=\"(\" close=\")\">" +
            "       #{item}" +
            "   </foreach>" +
            "</script>")*/
    public List<OrderDetailModel> list(List<BigDecimal> orderIdList) {
        StringBuilder builder = new StringBuilder();
        builder.append("select * from t_order_detail where order_id in(");
        for (int i = 0; i < orderIdList.size(); i++) {
            builder.append("?");
            if (i + 1 < orderIdList.size()) {
                builder.append(",");
            }
        }
        builder.append(")");
        String cql = builder.toString();
        PreparedStatement prepared = this.cqlSession.prepare(cql);
        BoundStatement bound = prepared.bind(orderIdList.toArray(new BigDecimal[0]));
        ResultSet result = this.cqlSession.execute(bound);

        List<OrderDetailModel> modelList = new ArrayList<>();
        for (Row row : result) {
            long id = row.getLong("id");
            BigDecimal orderId = row.getBigDecimal("order_id");
            long userId = row.getLong("user_id");
            long productId = row.getLong("product_id");
            long merchantId = row.getLong("merchant_id");
            int amount = row.getInt("amount");
            OrderDetailModel model = new OrderDetailModel();
            model.setId(id);
            model.setOrderId(orderId);
            model.setUserId(userId);
            model.setProductId(productId);
            model.setMerchantId(merchantId);
            model.setAmount(amount);
            modelList.add(model);
        }
        return modelList;
    }

    //
//    @Insert("insert ignore into t_order_detail(id,orderId,userId,productId,merchantId,amount) values(#{orderDetail.id},#{orderDetail.orderId},#{orderDetail.userId},#{orderDetail.productId},#{orderDetail.merchantId},#{orderDetail.amount})")
//    int insert(@Param(value = "orderDetail") OrderDetailModel orderDetailModel);
//
    /*@Insert("<script>" +
            "   insert ignore into t_order_detail(id,orderId,userId,productId,merchantId,amount) values " +
            "   <foreach collection=\"orderDetailModelList\" item=\"e\" separator=\",\">" +
            "       (#{e.id},#{e.orderId},#{e.userId},#{e.productId},#{e.merchantId},#{e.amount})" +
            "   </foreach>" +
            "</script>")*/
    public void insertBatch(List<OrderDetailModel> orderDetailModelList) throws BusinessException {
        String cql = "INSERT INTO t_order_detail (id,order_id,user_id,product_id,merchant_id,amount) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement prepared = cqlSession.prepare(cql);

        // 创建批量语句
        BatchStatement batch = BatchStatement.newInstance(BatchType.LOGGED);

        for (int i = 0; i < orderDetailModelList.size(); i++) {
            OrderDetailModel model = orderDetailModelList.get(i);
            BoundStatement bound = prepared.bind(
                    model.getId(),
                    model.getOrderId(),
                    model.getUserId(),
                    model.getProductId(),
                    model.getMerchantId(),
                    model.getAmount()
            );
            batch = batch.add(bound);
        }

        // 执行批量插入
        ResultSet result = cqlSession.execute(batch);
        if (!result.wasApplied()) {
            throw new BusinessException("t_order_detail批量插入失败");
        }
    }
//
//    @Select("select * from t_order_detail where userId=#{userId} and productId=#{productId}")
//    OrderDetailModel getByUserIdAndProductId(
//            @Param(value = "userId") Long userId,
//            @Param(value = "productId") Long productId);
//
    public void truncate() throws BusinessException {
        String cql = "truncate table t_order_detail";
        ResultSet result = this.cqlSession.execute(cql);
        if (!result.wasApplied()) {
            throw new BusinessException("truncate t_order_detail表失败");
        }
    }
//
//    @Select("select * from t_order_detail")
//    List<OrderDetailModel> selectAll();
}
