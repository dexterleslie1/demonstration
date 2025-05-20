package com.future.demo.mapper;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import com.future.common.exception.BusinessException;
import com.future.demo.entity.ProductModel;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductMapper {

    @Resource
    CqlSession cqlSession;

    public ProductModel getById(Long productId) {
        String cql = "select * from t_product where id=?";
        PreparedStatement prepared = this.cqlSession.prepare(cql);
        BoundStatement bound = prepared.bind(productId);
        ResultSet result = this.cqlSession.execute(bound);
        Row row = result.one();
        if (row == null) {
            return null;
        }

        long id = row.getLong("id");
        String name = row.getString("name");
        int stock = row.getInt("stock");
        long merchantId = row.getLong("merchantId");
        ProductModel productModel = new ProductModel();
        productModel.setId(id);
        productModel.setName(name);
        productModel.setStock(stock);
        productModel.setMerchantId(merchantId);
        return productModel;
    }

    /*@Select("<script>" +
            "   select * from t_product where id in " +
            "   <foreach collection=\"productIdList\" item=\"e\" separator=\",\" open=\"(\" close=\")\">" +
            "   #{e}" +
            "   </foreach>" +
            "</script>")*/
    public List<ProductModel> list(List<Long> productIdList) {
        StringBuilder builder = new StringBuilder();
        builder.append("select * from t_product where id in(");
        for (int i = 0; i < productIdList.size(); i++) {
            builder.append("?");
            if (i + 1 < productIdList.size()) {
                builder.append(",");
            }
        }
        builder.append(")");
        String cql = builder.toString();
        PreparedStatement prepared = this.cqlSession.prepare(cql);
        BoundStatement bound = prepared.bind(productIdList.toArray(new Long[]{}));
        ResultSet result = this.cqlSession.execute(bound);

        List<ProductModel> productModelList = new ArrayList<>();
        for (Row row : result) {
            long id = row.getLong("id");
            String name = row.getString("name");
            int stock = row.getInt("stock");
            long merchantId = row.getLong("merchantId");
            ProductModel productModel = new ProductModel();
            productModel.setId(id);
            productModel.setName(name);
            productModel.setStock(stock);
            productModel.setMerchantId(merchantId);
            productModelList.add(productModel);
        }
        return productModelList;
    }

    // 注意：stock >= #{amount} 防止超卖问题
    /*@Update("UPDATE t_product SET stock = stock - #{amount} WHERE id = #{productId} and stock >= #{amount}")*/
    public int decreaseStock(Long productId, Integer amount) throws BusinessException {
        String cql = "UPDATE t_product SET stock = stock - ? WHERE id = ? and stock >= ?";
        PreparedStatement prepared = this.cqlSession.prepare(cql);
        BoundStatement bound = prepared.bind(productId, amount, amount);
        ResultSet result = this.cqlSession.execute(bound);
        if (!result.wasApplied()) {
            return 0;
        }
        return 1;
    }
//
//    @Update("UPDATE t_product SET stock = #{stock} WHERE id = #{productId}")
//    public void updateStock(long productId, int stock) {
//        String cql = "UPDATE t_product SET stock = " + stock + " WHERE id = " + productId;
//        ResultSet result = this.cqlSession.execute(cql);
//        if(!result.wasApplied()) {
//            throw new BusinessException("")
//        }
//    }

    //
////    @Insert("INSERT INTO t_product(id, name, stock, merchantId) select #{id}, #{name}, #{stock}, #{merchantId} from dual where not exists (select 1 from t_product where id = #{id})")
////    int insert(ProductModel productModel);
//
    /*@Insert("<script>" +
            "   insert into t_product(id, name, stock, merchantId) values " +
            "   <foreach collection=\"productModelList\" item=\"e\" separator=\",\">" +
            "       (#{e.id},#{e.name},#{e.stock},#{e.merchantId})" +
            "   </foreach>" +
            "</script>")*/
    public void insertBatch(List<ProductModel> productModelList) throws BusinessException {
        String cql = "INSERT INTO t_product (id, name, stock, merchantId) " +
                "VALUES (?, ?, ?, ?)";

        PreparedStatement prepared = cqlSession.prepare(cql);

        // 创建批量语句
        BatchStatement batch = BatchStatement.newInstance(BatchType.LOGGED);

        // 添加多个订单到批量语句
        for (int i = 0; i < productModelList.size(); i++) {
            ProductModel productModel = productModelList.get(i);
            BoundStatement bound = prepared.bind(
                    productModel.getId(),
                    productModel.getName(),
                    productModel.getStock(),
                    productModel.getMerchantId()
            );
            batch = batch.add(bound);
        }

        // 执行批量插入
        ResultSet result = cqlSession.execute(batch);
        if (!result.wasApplied()) {
            throw new BusinessException("t_product批量插入失败");
        }
    }

    //
////    @Delete("DELETE FROM t_product WHERE id=#{productId}")
////    void delete(Long productId);
//
    /*@Update("update t_product set stock=#{productStock}")*/
    public void restoreStock(Integer productStock) throws BusinessException {
        String cql = "update t_product set stock=?";
        PreparedStatement prepared = this.cqlSession.prepare(cql);
        BoundStatement bound = prepared.bind(productStock);
        ResultSet result = this.cqlSession.execute(bound);
        if (!result.wasApplied()) {
            throw new BusinessException("restore t_product失败");
        }
    }

    public void truncate() throws BusinessException {
        String cql = "truncate table t_product";
        ResultSet result = this.cqlSession.execute(cql);
        if (!result.wasApplied()) {
            throw new BusinessException("truncate t_product表失败");
        }
    }
}
