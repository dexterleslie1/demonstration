package com.future.demo;

import com.future.demo.bean.Employee;
import com.future.demo.mapper.EmployeeMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoSpringBootMybatisApplicationTests {

    @Autowired
    EmployeeMapper employeeMapper;

    @Test
    void contextLoads() {
        this.employeeMapper.deleteAll();

        // region 测试EmployeeMapper CRUD

        String empName = "张三";
        int age = 20;
        double empSalary = 1000.0;
        Employee employee = new Employee();
        employee.setEmpName(empName);
        employee.setAge(age);
        employee.setEmpSalary(empSalary);
        this.employeeMapper.insert(employee);

        Long id = employee.getId();

        employee = this.employeeMapper.getById(id);
        Assertions.assertEquals(empName, employee.getEmpName());
        Assertions.assertEquals(age, employee.getAge());
        Assertions.assertEquals(empSalary, employee.getEmpSalary());

        empName = "李四";
        age = 30;
        empSalary = 2000.0;
        employee.setEmpName(empName);
        employee.setAge(age);
        employee.setEmpSalary(empSalary);
        this.employeeMapper.update(employee);

        employee = this.employeeMapper.getById(id);
        Assertions.assertEquals(empName, employee.getEmpName());
        Assertions.assertEquals(age, employee.getAge());
        Assertions.assertEquals(empSalary, employee.getEmpSalary());

        this.employeeMapper.delete(id);

        employee = this.employeeMapper.getById(id);
        Assertions.assertNull(employee);

        // endregion
    }

}
