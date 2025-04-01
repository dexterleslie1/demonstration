package com.future.demo.mapper;

import com.future.demo.bean.Employee;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

// 用于测试mybatis的返回结果类型
@Mapper
public interface EmployeeReturnValueMapper {

    // 测试返回Long类型
    Long count();

    // 测试返回BigDecimal类型
    BigDecimal getSalaryById(Long id);

    List<Employee> listAll();

    // 测试返回Map类型，key为id，value为Employee对象
    @MapKey("id")
    Map<Long, Employee> listAllMap();

    // 测试ResultMap类型
    Employee get(Long id);
}
