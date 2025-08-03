package com.future.demo.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommomMapper {

    /*@Delete("truncate table ${table}")
    void truncate(@Param("table") String table);*/

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

    @Update("update t_user set username=#{usernameNew} where username=#{usernameOrigin}")
    int updateUser(@Param("usernameOrigin") String usernameOrigin,
                   @Param("usernameNew") String usernameNew);

    @Delete("delete from t_user where username=#{username}")
    int deleteUser(@Param("username") String username);

    @Delete("<script>" +
            "delete from t_user where id not in(" +
            "<foreach collection='idList' item='e' separator=','>" +
            "   #{e}" +
            "</foreach>)" +
            "</script>")
    void deleteUserIdNotIn(@Param("idList") List<Long> idList);
}
