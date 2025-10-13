package com.future.demo.mapper;

import com.future.demo.entity.AccountFreeze;
import org.apache.ibatis.annotations.*;

@Mapper
public interface AccountFreezeMapper {
    @Insert("insert into t_account_freeze(xid,user_id,freeze_money) values(#{xid},#{userId},#{freezeMoney})")
    int insert(AccountFreeze accountFreeze);

    @Delete("delete from t_account_freeze where xid=#{xid}")
    int delete(@Param("xid") String xid);

    @Select("select * from t_account_freeze where xid=#{xid}")
    AccountFreeze getByXid(@Param("xid") String xid);

    @Update("update t_account_freeze set freeze_money=#{freezeMoney} where xid=#{xid}")
    int update(AccountFreeze accountFreeze);
}
