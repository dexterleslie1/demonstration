package com.future.demo.mybatis.mapper;

import com.future.demo.mybatis.entity.UserModel;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GetAutoIncrementPrimaryKeyMapper {
    @Insert("insert into `user` values(NULL,#{name},#{password},#{age})")
    // 在 insert 插入操作后返回主键 id
    // https://blog.csdn.net/qq_44290077/article/details/119582418
    @SelectKey(keyProperty = "id", before = false, resultType = Long.class, statement = "SELECT LAST_INSERT_ID()")
    void test1(UserModel model);

    @Insert("insert into `user` values(NULL,#{name},#{password},#{age})")
    // https://blog.csdn.net/H1101370034/article/details/121231207
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    // 在 insert 插入操作后返回主键 id
    // https://blog.csdn.net/qq_44290077/article/details/119582418
    void test2(UserModel model);
}
