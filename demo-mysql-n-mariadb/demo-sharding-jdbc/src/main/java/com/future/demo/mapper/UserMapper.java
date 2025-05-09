package com.future.demo.mapper;

import com.future.demo.bean.User;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface UserMapper {
    @Insert("insert into `user`(`name`,balance,create_time) values(#{name},#{balance},#{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void add(User user);

    @Select("select * from `user`")
    List<User> listAll();

    @Update("truncate table `user`")
    void truncate();

    @Select("select * from `user` where id=#{id}")
    User get(@Param("id") Long id);

    /**
     * 扣减用户余额
     *
     * @param id
     * @param amount
     * @return
     */
    @Update("update `user` set balance=balance-#{amount} where id=#{id} and balance>=#{amount}")
    int updateDecreaseBalance(@Param("id") Long id,
                              @Param("amount") BigDecimal amount);
}
