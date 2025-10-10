package com.future.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

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
}
