package com.future.demo.mapper;

import com.future.demo.bean.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface EmployeeAnnotationMapper {
    @Insert("insert into employee (id, emp_name, age, emp_salary) values (#{id}, #{empName}, #{age}, #{empSalary})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Employee employee);

    @Select("select * from employee where id = #{id}")
    Employee findById(Long id);

    // https://blog.csdn.net/qq_43753724/article/details/114274484
    // 集合类型参数
    @Select("<script>" +
            "select * from employee where id in" +
            "<foreach item=\"id\" collection=\"idList\" open=\"(\" separator=\",\" close=\")\">" +
            "#{id}" +
            "</foreach> order by id asc" +
            "</script>")
    List<Employee> findByIds(@Param("idList") List<Long> idList);
}
