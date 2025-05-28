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

    @Update("UPDATE t_product SET stock = #{stock} WHERE id = #{productId}")
    void updateStock(long productId, int stock);

//    @Insert("INSERT INTO t_product(id, name, stock) select #{id}, #{name}, #{stock} from dual where not exists (select 1 from t_product where id = #{id})")
//    int insert(ProductModel productModel);

    @Insert("<script>" +
            "insert into t_product(id,name,stock) values " +
            "<foreach collection=\"productModelList\" item=\"e\" separator=\",\">" +
            "   (#{e.id},#{e.name},#{e.stock})" +
            "</foreach>" +
            "</script>")
    void insert(List<ProductModel> productModelList);

//    @Delete("DELETE FROM t_product WHERE id=#{productId}")
//    void delete(Long productId);

    @Update("truncate table t_product")
    void truncate();
}
