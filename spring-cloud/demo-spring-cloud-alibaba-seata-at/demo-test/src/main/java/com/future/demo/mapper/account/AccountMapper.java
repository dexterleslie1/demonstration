package com.future.demo.mapper.account;

import com.future.demo.entity.Account;
import com.future.demo.entity.Storage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AccountMapper {
    @Select("select * from t_account where user_id=#{userId}")
    Account getByUserId(@Param("userId") long userId);
}
