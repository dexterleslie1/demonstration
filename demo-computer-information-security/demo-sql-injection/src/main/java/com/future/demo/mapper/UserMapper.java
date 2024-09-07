package com.future.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.future.demo.User;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.StatementType;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {
    @Select("select * from `user` where username='${username}'")
    List<User> getByUsername(@Param("username") String username);

    @Select("<script>" +
            "select * from `user`\n" +
            "where id in\n" +
            "<foreach item='item' index='index' collection='ids' open='(' separator=',' close=')'>" +
            "${item}" +
            "</foreach>" +
            "</script>")
    List<User> listByIds(@Param("ids") List<String> idList);

    /**
     * 协助存储过程sql注入测试
     *
     * @param username
     * @return
     */
    @Select("call proc_sql_injection_assistant(#{username,mode=IN,jdbcType=VARCHAR})")
    @Options(statementType = StatementType.CALLABLE)
    List<User> getByUsernameViaProcedure(@Param("username") String username);
}
