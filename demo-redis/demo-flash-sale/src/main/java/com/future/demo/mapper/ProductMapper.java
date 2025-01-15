package com.future.demo.mapper;

import com.future.demo.entity.ProductModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProductMapper {

    @Select("SELECT * FROM t_product WHERE id = #{productId}")
    ProductModel getById(Long productId);

    // 注意：stock >= #{amount} 防止超卖问题
    @Update("UPDATE t_product SET stock = stock - #{amount} WHERE id = #{productId} and stock >= #{amount}")
    int decreaseStock(Long productId, Integer amount);

    @Update("UPDATE t_product SET stock = #{stock} WHERE id = #{productId}")
    void updateStock(long productId, int stock);
}
