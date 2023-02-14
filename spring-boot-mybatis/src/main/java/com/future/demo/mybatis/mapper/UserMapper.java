package com.future.demo.mybatis.mapper;

import com.future.demo.mybatis.entity.UserModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {
    @Update("truncate table `user`")
    void truncate();

    @Select("select * from `user` where name=#{name}")
    UserModel getByName(@Param("name") String name);
}
