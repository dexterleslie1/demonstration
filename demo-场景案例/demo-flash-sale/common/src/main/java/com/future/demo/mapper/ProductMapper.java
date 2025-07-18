package com.future.demo.mapper;

import com.future.demo.entity.ProductModel;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Select("SELECT * FROM t_product WHERE id = #{productId}")
    ProductModel getById(Long productId);

    @Select("<script>" +
            "   select * from t_product where id in " +
            "   <foreach collection=\"productIdList\" item=\"e\" separator=\",\" open=\"(\" close=\")\">" +
            "   #{e}" +
            "   </foreach>" +
            "</script>")
    List<ProductModel> list(List<Long> productIdList);

    // 注意：stock >= #{amount} 防止超卖问题
    @Update("UPDATE t_product SET stock = stock - #{amount} WHERE id = #{productId} and stock >= #{amount}")
    int decreaseStock(Long productId, Integer amount);

    @Update("UPDATE t_product SET stock = #{stock} WHERE id = #{productId}")
    void updateStock(long productId, int stock);

    @Insert("INSERT INTO t_product(id, name, stock, merchantId, flashSale, flashSaleStartTime, flashSaleEndTime, createTime) " +
            "values(#{id}, #{name}, #{stock}, #{merchantId}, #{flashSale}, #{flashSaleStartTime}, #{flashSaleEndTime}, #{createTime})")
    int insert(ProductModel productModel);

    @Update("update t_product set stock=#{productStock}")
    void restoreStock(@Param("productStock") Integer productStock);

    @Update("truncate table t_product")
    void truncate();
}
