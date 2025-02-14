package com.future.demo.mapper;

import com.future.demo.bean.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 表示这是一个mybatis的mapper接口
@Mapper
public interface EmployeeMapper {
    void insert(Employee employee);

    void update(Employee employee);

    void delete(@Param("id") Long id);

    Employee getById(@Param("id") Long id);

    List<Employee> listAll();

    void deleteAll();

    void truncate();

    int count();

    // region 测试动态SQL

    List<Employee> findByNameAndSalary(@Param("name") String name, @Param("salary") Double salary);

    List<Employee> findByNameAndSalaryAndId(@Param("name") String name, @Param("salary") Double salary, @Param("id") Long id);

    void updateDynamicSet(Employee employee);

    List<Employee> findByIds(@Param("ids") List<Long> ids);

    // 使用foreach标签批量插入
    void insertBatch(@Param("employees") List<Employee> employees);
    void insertBatch2(@Param("employees") List<Employee> employees);

    void updateBatch(@Param("employees") List<Employee> employees);

    // endregion
}
