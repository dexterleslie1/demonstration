package com.future.demo.mapper;

import com.future.demo.entity.Account;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;

@Mapper
public interface AccountMapper {
    @Update("update t_account\n" +
            "        set used    = used + #{amount},\n" +
            "            residue = residue - #{amount}\n" +
            "        where user_id = #{userId} and residue >= #{amount}")
    int deduct(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * 协助测试重置数据
     *
     * @return
     */
    @Update("update `t_account` set `total`=1000,`used`=0,`residue`=1000 where user_id=1")
    void reset();

    @Delete("delete from t_account where user_id=#{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    @Insert("insert into t_account(user_id,total,used,residue) values(#{userId},#{total},#{used},#{residue})")
    int insert(Account account);
}
