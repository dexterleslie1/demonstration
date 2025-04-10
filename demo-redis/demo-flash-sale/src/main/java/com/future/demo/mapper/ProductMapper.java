package com.future.demo.mapper;

import com.future.demo.entity.ProductModel;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ProductMapper {

    @Select("SELECT * FROM t_product WHERE id = #{productId}")
    ProductModel getById(Long productId);

    // 注意：stock >= #{amount} 防止超卖问题
    @Update("UPDATE t_product SET stock = stock - #{amount} WHERE id = #{productId} and stock >= #{amount}")
    int decreaseStock(Long productId, Integer amount);

    @Update("UPDATE t_product SET stock = #{stock} WHERE id = #{productId}")
    void updateStock(long productId, int stock);

    @Insert("INSERT INTO t_product(id, name, stock) select #{id}, #{name}, #{stock} from dual where not exists (select 1 from t_product where id = #{id})")
    int insert(ProductModel productModel);

    @Delete("DELETE FROM t_product WHERE id=#{productId}")
    void delete(Long productId);
}
