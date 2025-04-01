package com.future.demo.mapper;

import com.future.demo.bean.Product;
import com.future.demo.bean.ProductDescription;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {
    @Insert("insert into product(`name`,create_time) values(#{name},#{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void addProduct(Product product);

    @Insert("insert into product_description(product_id,description) values(#{productId},#{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void addProductDescription(ProductDescription productDescription);

    @Select("select p.id,p.name,p.create_time,pd.id as description_id,pd.description from product p" +
            " join product_description pd on p.id=pd.product_id" +
            " where p.id=#{productId}")
    Product getById(Long productId);

    @Select("<script>" +
            "select p.id,p.name,p.create_time,pd.id as description_id,pd.description from product p" +
            "   join product_description pd on p.id=pd.product_id" +
            "   where p.id in(" +
            "   <foreach item=\"e\" collection=\"idList\" separator=\",\">" +
            "       #{e}" +
            "   </foreach>)" +
            " order by p.id asc" +
            "</script>")
    List<Product> listByIds(@Param("idList") List<Long> idList);
}
