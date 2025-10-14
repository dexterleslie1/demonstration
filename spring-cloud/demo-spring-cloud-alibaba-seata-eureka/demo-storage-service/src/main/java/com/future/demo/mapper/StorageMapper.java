package com.future.demo.mapper;

import com.future.demo.entity.Storage;
import org.apache.ibatis.annotations.*;

@Mapper
public interface StorageMapper {
    @Update("update t_storage\n" +
            "        set used    = used + #{amount},\n" +
            "            residue = residue - #{amount}\n" +
            "        where product_id = #{productId} and residue >= #{amount}")
    int deduct(@Param("productId") Long productId, @Param("amount") int amount);

    /**
     * 协助测试重置数据
     *
     * @return
     */
    @Update("update `t_storage` set `total`=100,`used`=0,`residue`=100 where product_id=1")
    void reset();

    @Delete("delete from t_storage where product_id=#{productId}")
    int deleteByProductId(@Param("productId") Long productId);

    @Insert("insert into t_storage(product_id,total,used,residue) values(#{productId},#{total},#{used},#{residue})")
    int insert(Storage storage);
}
