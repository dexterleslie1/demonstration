package com.future.demo.mapper;

import com.future.demo.entity.ProductModel;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Select("SELECT * FROM t_product WHERE id = #{productId}")
    ProductModel getById(Long productId);

    // 注意：stock >= #{amount} 防止超卖问题
    @Update("UPDATE t_product SET stock = stock - #{amount} WHERE id = #{productId} and stock >= #{amount}")
    int decreaseStock(Long productId, Integer amount);

    @Insert("INSERT INTO t_product(id, name, stock, merchantId) values(#{id}, #{name}, #{stock}, #{merchantId})")
    int insert(ProductModel productModel);

    @Select("select id from t_product limit ${limitSize}")
    List<Long> listId(int limitSize);
}
