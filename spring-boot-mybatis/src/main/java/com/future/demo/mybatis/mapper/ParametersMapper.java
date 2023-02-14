package com.future.demo.mybatis.mapper;

import com.future.demo.mybatis.entity.UserModel;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ParametersMapper {
    // 对象类型的参数传入
    @Insert("insert into `user` values(NULL,#{name},#{password},#{age})")
    // 在 insert 插入操作后返回主键 id
    // https://blog.csdn.net/qq_44290077/article/details/119582418
    @SelectKey(keyProperty = "id", before = false, resultType = Long.class, statement = "SELECT LAST_INSERT_ID()")
    void testParameterObjectType(UserModel model);

    // 命名参数对象类型
    @Insert("insert into `user` values(NULL,#{model.name},#{model.password},#{model.age})")
    void testNamedParameterObjectType(@Param("model") UserModel model);

    // 命名参数原始数据类型
    @Insert("insert into `user` values(NULL,#{name},#{password},#{age})")
    void testNamedParameterPrimitiveType(@Param("name") String name, @Param("password") String password, @Param("age") int age);

    // https://blog.csdn.net/qq_43753724/article/details/114274484
    // 集合类型参数
    @Select("<script>" +
            "select * from `user` where name in" +
            "<foreach item=\"name\" collection=\"nameList\" open=\"(\" separator=\",\" close=\")\">" +
            "#{name}" +
            "</foreach> order by id asc" +
            "</script>")
    List<UserModel> testNamedParameterListType(@Param("nameList") List<String> nameList);
}
