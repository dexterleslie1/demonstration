package com.future.demo.mapper;

import com.future.demo.bean.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

// 用于测试获取参数
@Mapper
public interface EmployeeParamMapper {
    Employee test1(Long id);

    Employee test2(List<Long> idList);

    Employee test3(Employee employee);

    Employee test4(Map<String, Object> map);

    Employee test5(@Param("id") Long id, @Param("name") String name);

    Employee test6(@Param("id") Long id, @Param("employee") Employee employee);
}
