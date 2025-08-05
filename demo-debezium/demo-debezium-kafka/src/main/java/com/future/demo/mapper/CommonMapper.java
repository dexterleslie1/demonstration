package com.future.demo.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommonMapper {

    @Insert("insert into t_user(username,`password`,createTime) values(#{username},#{password},now())")
    int insertUser(@Param("username") String username,
                   @Param("password") String password);

    // 批量插入
    @Insert("<script>" +
            "insert into t_user(username,`password`,createTime) values " +
            "<foreach collection='userList' item='e' separator=','>" +
            "(#{e.username},#{e.password},now())" +
            "</foreach>" +
            "</script>")
    int insertUserBatch(@Param("userList") List<Map<String, String>> userList);

    @Update("update t_user set username=#{usernameNew} where username=#{username}")
    int updateUser(@Param("usernameNew") String usernameNew,
                   @Param("username") String username);

    // 批量修改
    @Update("<script>" +
            "update t_user set username=#{username} where id in(" +
            "<foreach collection='idList' item='e' separator=','>" +
            "#{e}" +
            "</foreach>" +
            ")" +
            "</script>")
    int updateUserBatch(@Param("idList") List<Long> idList,
                   @Param("username") String username);

    @Delete("delete from t_user where username=#{username}")
    int deleteUser(@Param("username") String username);

    // 批量删除
    @Delete("<script>" +
            "delete from t_user where id in(" +
            "<foreach collection='idList' item='e' separator=','>" +
            "#{e}" +
            "</foreach>" +
            ")" +
            "</script>")
    int deleteUserBatch(@Param("idList") List<Long> idList);
}
