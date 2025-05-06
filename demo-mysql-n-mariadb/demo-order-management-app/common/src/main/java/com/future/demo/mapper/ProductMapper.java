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

    @Insert("INSERT INTO t_product(id, name, stock, merchantId) select #{id}, #{name}, #{stock}, #{merchantId} from dual where not exists (select 1 from t_product where id = #{id})")
    int insert(ProductModel productModel);

    @Delete("DELETE FROM t_product WHERE id=#{productId}")
    void delete(Long productId);
}
