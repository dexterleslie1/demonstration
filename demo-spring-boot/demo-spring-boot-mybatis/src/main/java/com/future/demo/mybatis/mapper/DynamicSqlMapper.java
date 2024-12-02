package com.future.demo.mybatis.mapper;

import com.future.demo.mybatis.entity.EmpModel;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DynamicSqlMapper {
    List<EmpModel> testWhereIf(@Param("id") Long id,
                               @Param("name") String name,
                               @Param("age") Integer age);

    List<EmpModel> testChooseWhenOtherwise(@Param("id") Long id,
                                           @Param("name") String name,
                                           @Param("age") Integer age);

    List<EmpModel> testForeach(@Param("idList") List<Long> idList);

    List<EmpModel> testSqlTag(@Param("idList") List<Long> idList);
}
