package com.future.demo.mapper;

import com.future.demo.bean.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper {
    @Insert("insert into `user`(`name`,create_time) values(#{name},#{createTime})")
    void add(User user);

    @Select("select * from `user`")
    List<User> listAll();

    @Update("truncate table `user`")
    void truncate();
}
