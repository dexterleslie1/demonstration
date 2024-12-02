package com.future.demo.mybatis.mapper;

import com.future.demo.mybatis.entity.UserModel;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SelectLikeMapper {
    // 模糊查询方案1
    @Select("select * from `user` where name like #{name}")
    List<UserModel> selectLike1(@Param("name") String name);

    // 模糊查询方案1
    @Select("select * from `user` where name like \"%\"#{name}\"%\"")
    List<UserModel> selectLike2(@Param("name") String name);
}
