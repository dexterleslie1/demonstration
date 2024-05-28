package com.future.demo.mybatis.mapper;

import com.future.demo.mybatis.entity.DeptModel;
import com.future.demo.mybatis.entity.EmpModel;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

// 一对一、多对一、一对多、多对多
// https://www.cnblogs.com/duanxiaobiao/p/14531116.html
@Mapper
@Repository
public interface RelationshipMapper {
    // 联关系的多对一关系解决方案1：联合查询
    EmpModel getEmpAndDeptByEmpName1(@Param("name") String name);

    // 关联关系的多对一和一对一关系解决方案2：使用association的联合查询解决
    EmpModel getEmpAndDeptByEmpName2(@Param("name") String name);

    // 关联关系的多对一关系解决方案3：使用association的嵌套查询解决
    EmpModel getEmpAndDeptByEmpName3(@Param("name") String name);
    DeptModel getDeptById(@Param("id") Long id);

    // 关联关系的一对多关系解决方案1：使用association的联合查询解决
    DeptModel getDeptAndEmpByDeptName1(@Param("name") String name);

    // 关联关系的一对多关系解决方案2：使用association嵌套查询解决
    DeptModel getDeptAndEmpByDeptName2(@Param("name") String name);
    List<EmpModel> findEmpByDeptId(@Param("id") Long id);
}
