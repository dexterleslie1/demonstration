package com.future.demo.mybatis.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Mapper
@Repository
public interface SelectMapper {
    // count查询
    @Select("select count(id) from `user`")
    int countUser();

    // 查询单条结果为map
    @Select("select * from `user` where name=#{name}")
    Map<String, Object> getUserByNameToMap(@Param("name") String name);

    // 查询多条记录结果为map
    // https://blog.csdn.net/weixin_52385232/article/details/127536936
    // 注意：不能使用annotation实现这个功能，只能够使用xml实现
    @MapKey("id")
    Map<Long, Map<String, Object>> getUserAllToMap();
}
