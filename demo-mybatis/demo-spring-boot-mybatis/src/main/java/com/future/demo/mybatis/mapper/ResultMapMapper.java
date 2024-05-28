package com.future.demo.mybatis.mapper;

import com.future.demo.mybatis.entity.TestResultMapModel;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ResultMapMapper {
    // @ResultMap,@Results,@Result注解的使用
    // https://www.jb51.net/article/230878.htm
    @Select("select * from test_resultmap order by rmid asc")
    @Results(id="testResultMapMap", value = {
            @Result(id = true, column = "rmid", property = "id"),
            @Result(column = "rmname", property = "name"),
            @Result(column = "rmpassword", property = "password")
    })
    List<TestResultMapModel> findAll1();

    @Select("select * from test_resultmap order by rmid asc")
    @ResultMap(value = "testResultMapMap")
    List<TestResultMapModel> findAll2();
}
