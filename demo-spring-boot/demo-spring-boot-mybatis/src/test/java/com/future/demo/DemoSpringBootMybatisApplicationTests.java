package com.future.demo;

import com.future.demo.bean.Customer;
import com.future.demo.bean.Employee;
import com.future.demo.bean.Order;
import com.future.demo.bean.Student;
import com.future.demo.mapper.*;
import com.future.demo.service.EmployeeService;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
class DemoSpringBootMybatisApplicationTests {

    @Autowired
    EmployeeMapper employeeMapper;
    @Autowired
    EmployeeParamMapper employeeParamMapper;
    @Autowired
    EmployeeReturnValueMapper employeeReturnValueMapper;

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    StudentMapper studentMapper;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    EmployeeAnnotationMapper employeeAnnotationMapper;

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

        // region 测试数据库字段下划线到bean驼峰命名自动转换

        age = 20;
        empSalary = 1000.0;
        for (int i = 0; i < 5; i++) {
            empName = "张三" + i;
            employee = new Employee();
            employee.setEmpName(empName);
            employee.setAge(age);
            employee.setEmpSalary(empSalary);
            this.employeeMapper.insert(employee);
        }
        List<Employee> employeeList = this.employeeMapper.listAll();
        List<String> empNameList = employeeList.stream().map(Employee::getEmpName).collect(Collectors.toList());
        for (int i = 0; i < 5; i++) {
            empName = "张三" + i;
            Assertions.assertTrue(empNameList.contains(empName));
        }

        // endregion

        // region 测试获取参数值

        // 测试方法单个参数
        this.employeeParamMapper.test1(1L);
        this.employeeParamMapper.test2(Arrays.asList(1L, 2L, 3L));
        this.employeeParamMapper.test3(new Employee());
        this.employeeParamMapper.test4(new HashMap<String, Object>() {{
            this.put("id", 1L);
            this.put("name", "张三");
        }});

        // 测试方法多个参数
        this.employeeParamMapper.test5(1L, "张三");
        this.employeeParamMapper.test6(1L, new Employee());

        // endregion

        // region 测试mybatis返回结果类型

        empName = "张三";
        age = 20;
        empSalary = 1000.0;
        employee = new Employee();
        employee.setEmpName(empName);
        employee.setAge(age);
        employee.setEmpSalary(empSalary);
        this.employeeMapper.insert(employee);

        id = employee.getId();

        // 测试返回Long类型
        Long count = this.employeeReturnValueMapper.count();
        Assertions.assertEquals(6L, count);

        // 测试返回BigDecimal类型
        BigDecimal salary = this.employeeReturnValueMapper.getSalaryById(id);
        Assertions.assertTrue(new BigDecimal(empSalary).compareTo(salary) == 0);

        // 测试返回List类型
        String finalEmpName = empName;
        Employee employee1 = this.employeeReturnValueMapper.listAll().stream()
                .filter(e -> e.getEmpName().equals(finalEmpName)).findFirst().get();
        Assertions.assertNotNull(employee1);

        // 测试返回Map类型
        Map<Long, Employee> employeeMap = this.employeeReturnValueMapper.listAllMap();
        Employee employee2 = employeeMap.get(id);
        Assertions.assertEquals(empName, employee2.getEmpName());

        // 测试ResultMap类型
        Employee employee3 = this.employeeReturnValueMapper.get(id);
        Assertions.assertEquals(empName, employee3.getEmpName());

        // endregion

        // region 测试关联查询

        // 根据订单id查询订单信息并且同时查询订单对应的客户信息
        Long orderId = 1L;
        Order order = this.orderMapper.findByIdWithCustomer(orderId);
        Assertions.assertNotNull(order);
        Assertions.assertEquals(orderId, order.getId());
        Assertions.assertEquals("北京市", order.getAddress());
        Assertions.assertTrue(new BigDecimal("200.56").compareTo(order.getAmount()) == 0);
        Assertions.assertEquals(1L, order.getCustomerId());
        Assertions.assertEquals(1L, order.getCustomer().getId());
        Assertions.assertEquals("张三", order.getCustomer().getCustomerName());
        Assertions.assertEquals("13800000000", order.getCustomer().getPhone());

        // 根据订单id查询订单信息并且同时查询订单对应的客户信息，使用分步查询方法
        order = this.orderMapper.findByIdWithCustomerStep(orderId);
        System.out.println("=============================================================");
        Assertions.assertNotNull(order);
        Assertions.assertEquals(orderId, order.getId());
        Assertions.assertEquals("北京市", order.getAddress());
        Assertions.assertTrue(new BigDecimal("200.56").compareTo(order.getAmount()) == 0);
        Assertions.assertEquals(1L, order.getCustomerId());
        Assertions.assertEquals(1L, order.getCustomer().getId());
        Assertions.assertEquals("张三", order.getCustomer().getCustomerName());
        Assertions.assertEquals("13800000000", order.getCustomer().getPhone());

        // 根据id查询客户，并且查询出客户关联的订单
        Long customerId = 1L;
        Customer customer = this.customerMapper.findByIdWithOrders(customerId);
        Assertions.assertNotNull(customer);
        Assertions.assertEquals(customerId, customer.getId());
        Assertions.assertEquals("张三", customer.getCustomerName());
        Assertions.assertEquals("13800000000", customer.getPhone());
        Assertions.assertEquals(2, customer.getOrders().size());
        Assertions.assertEquals(1L, customer.getOrders().get(0).getId());
        Assertions.assertEquals("北京市", customer.getOrders().get(0).getAddress());
        Assertions.assertEquals(customerId, customer.getOrders().get(0).getCustomerId());
        Assertions.assertEquals(2L, customer.getOrders().get(1).getId());
        Assertions.assertEquals("上海市", customer.getOrders().get(1).getAddress());
        Assertions.assertEquals(customerId, customer.getOrders().get(1).getCustomerId());

        // 根据id查询客户，并且查询出客户关联的订单，使用分步查询方法
        customer = this.customerMapper.findByIdWithOrdersStep(customerId);
        Assertions.assertNotNull(customer);
        Assertions.assertEquals(customerId, customer.getId());
        Assertions.assertEquals("张三", customer.getCustomerName());
        Assertions.assertEquals("13800000000", customer.getPhone());
        Assertions.assertEquals(2, customer.getOrders().size());
        Assertions.assertEquals(1L, customer.getOrders().get(0).getId());
        Assertions.assertEquals("北京市", customer.getOrders().get(0).getAddress());
        Assertions.assertEquals(customerId, customer.getOrders().get(0).getCustomerId());
        Assertions.assertEquals(2L, customer.getOrders().get(1).getId());
        Assertions.assertEquals("上海市", customer.getOrders().get(1).getAddress());
        Assertions.assertEquals(customerId, customer.getOrders().get(1).getCustomerId());

        // endregion

        // region 测试动态SQL

        // 测试动态条件查询where、if
        this.employeeMapper.findByNameAndSalary(null, null);
        this.employeeMapper.findByNameAndSalary(null, 1000.0);
        this.employeeMapper.findByNameAndSalary("张三", null);
        this.employeeMapper.findByNameAndSalary("张三", 1000.0);

        // 测试动态条件查询where、choose、when、otherwise
        this.employeeMapper.findByNameAndSalaryAndId("张三", 1000.0, 1L);
        this.employeeMapper.findByNameAndSalaryAndId("张三", 1000.0, null);
        this.employeeMapper.findByNameAndSalaryAndId("张三", null, 1L);
        this.employeeMapper.findByNameAndSalaryAndId(null, 1000.0, 1L);
        this.employeeMapper.findByNameAndSalaryAndId(null, null, 1L);
        this.employeeMapper.findByNameAndSalaryAndId("张三", null, null);
        this.employeeMapper.findByNameAndSalaryAndId(null, null, null);

        // 测试动态更新数据
        employee = new Employee();
        employee.setEmpName("张三");
        employee.setEmpSalary(1000.0);
        employee.setAge(20);
        employee.setId(Long.MAX_VALUE);
        this.employeeMapper.updateDynamicSet(employee);
        employee = new Employee();
        // employee.setEmpName("张三");
        employee.setEmpSalary(1000.0);
        employee.setAge(20);
        employee.setId(Long.MAX_VALUE);
        this.employeeMapper.updateDynamicSet(employee);
        employee = new Employee();
        employee.setEmpName("张三");
        // employee.setEmpSalary(1000.0);
        employee.setAge(20);
        employee.setId(Long.MAX_VALUE);
        this.employeeMapper.updateDynamicSet(employee);
        employee = new Employee();
        employee.setEmpName("张三");
        employee.setEmpSalary(1000.0);
        // employee.setAge(20);
        employee.setId(Long.MAX_VALUE);
        this.employeeMapper.updateDynamicSet(employee);
        employee = new Employee();
        // employee.setEmpName("张三");
        // employee.setEmpSalary(1000.0);
        employee.setAge(20);
        employee.setId(Long.MAX_VALUE);
        this.employeeMapper.updateDynamicSet(employee);
        employee = new Employee();
        employee.setEmpName("张三");
        // employee.setEmpSalary(1000.0);
        // employee.setAge(20);
        employee.setId(Long.MAX_VALUE);
        this.employeeMapper.updateDynamicSet(employee);

        // 测试foreach
        empName = "张三";
        age = 20;
        empSalary = 1000.0;
        employee = new Employee();
        employee.setEmpName(empName);
        employee.setAge(age);
        employee.setEmpSalary(empSalary);
        this.employeeMapper.insert(employee);
        id = employee.getId();

        List<Employee> employees = this.employeeMapper.findByIds(Arrays.asList(id));
        Assertions.assertEquals(1, employees.size());
        Assertions.assertEquals(id, employees.get(0).getId());
        Assertions.assertEquals(empName, employees.get(0).getEmpName());
        Assertions.assertEquals(age, employees.get(0).getAge());
        Assertions.assertTrue(empSalary == employees.get(0).getEmpSalary());

        // 测试使用foreach标签批量更新
        this.employeeMapper.deleteAll();
        employees = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            employee = new Employee();
            employee.setEmpName("张三" + i);
            employee.setAge(20);
            employee.setEmpSalary(1000.0);
            employees.add(employee);
        }
        this.employeeMapper.insertBatch(employees);
        employees = this.employeeMapper.listAll();
        Assertions.assertEquals(10, employees.size());
        List<Long> ids = employees.stream().map(Employee::getId).collect(Collectors.toList());

        employees = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            employee = new Employee();
            employee.setId(ids.get(i));
            employee.setEmpName("张三1-" + i);
            employee.setAge(20 + i);
            employee.setEmpSalary(1000.0 + i);
            employees.add(employee);
        }
        this.employeeMapper.updateBatch(employees);
        employees = this.employeeMapper.listAll();
        Assertions.assertEquals(10, employees.size());
        Assertions.assertEquals("张三1-1", employees.get(1).getEmpName());
        Assertions.assertEquals(21, employees.get(1).getAge());

        // endregion

        // region 测试MyBatis缓存

        // 测试一级缓存
        // 通过控制台观察是否只执行了一次sql查询
        this.employeeService.testLevel1Cache(id);

        // 测试二级缓存
        // 通过控制台观察是否只执行了一次sql查询
        this.employeeService.testLevel2Cache(id);

        // endregion

        // region 测试pagehelper分页插件

        PageInfo<Employee> page = this.employeeService.findByPage(1, 10);
        Assertions.assertEquals(10, page.getList().size());
        Assertions.assertEquals(10, page.getTotal());
        Assertions.assertEquals(1, page.getPageNum());
        Assertions.assertEquals(10, page.getPageSize());
        Assertions.assertEquals(1, page.getPages());
        Assertions.assertEquals("张三1-0", page.getList().get(0).getEmpName());

        page = this.employeeService.findByPage(1, 3);
        Assertions.assertEquals(3, page.getList().size());
        Assertions.assertEquals(10, page.getTotal());
        Assertions.assertEquals(1, page.getPageNum());
        Assertions.assertEquals(3, page.getPageSize());
        Assertions.assertEquals(4, page.getPages());
        Assertions.assertEquals("张三1-0", page.getList().get(0).getEmpName());
        page = this.employeeService.findByPage(-1, 3);
        Assertions.assertEquals(3, page.getList().size());
        Assertions.assertEquals(10, page.getTotal());
        Assertions.assertEquals(1, page.getPageNum());
        Assertions.assertEquals(3, page.getPageSize());
        Assertions.assertEquals(4, page.getPages());
        Assertions.assertEquals("张三1-0", page.getList().get(0).getEmpName());
        page = this.employeeService.findByPage(2, 3);
        Assertions.assertEquals(3, page.getList().size());
        Assertions.assertEquals(10, page.getTotal());
        Assertions.assertEquals(2, page.getPageNum());
        Assertions.assertEquals(3, page.getPageSize());
        Assertions.assertEquals(4, page.getPages());
        Assertions.assertEquals("张三1-3", page.getList().get(0).getEmpName());
        page = this.employeeService.findByPage(4, 3);
        Assertions.assertEquals(1, page.getList().size());
        Assertions.assertEquals(10, page.getTotal());
        Assertions.assertEquals(4, page.getPageNum());
        Assertions.assertEquals(3, page.getPageSize());
        Assertions.assertEquals(4, page.getPages());
        Assertions.assertEquals("张三1-9", page.getList().get(page.getList().size() - 1).getEmpName());
        page = this.employeeService.findByPage(100000, 3);
        Assertions.assertEquals(1, page.getList().size());
        Assertions.assertEquals(10, page.getTotal());
        Assertions.assertEquals(4, page.getPageNum());
        Assertions.assertEquals(3, page.getPageSize());
        Assertions.assertEquals(4, page.getPages());
        Assertions.assertEquals("张三1-9", page.getList().get(page.getList().size() - 1).getEmpName());

        // endregion

        // region 测试MybatisX插件生成的mapper

        Student student = new Student();
        student.setName("张三");
        student.setAge(20);
        student.setScore(new BigDecimal(100));
        this.studentMapper.insert(student);
        id = student.getId();
        student = this.studentMapper.selectByPrimaryKey(id);
        Assertions.assertEquals("张三", student.getName());

        // endregion

        // region 测试注解方式编程

        employee = new Employee();
        employee.setEmpName("张三");
        employee.setAge(20);
        employee.setEmpSalary(1000.0);
        this.employeeAnnotationMapper.insert(employee);
        id = employee.getId();
        employee = this.employeeAnnotationMapper.findById(id);
        Assertions.assertEquals("张三", employee.getEmpName());

        employees = this.employeeAnnotationMapper.findByIds(Collections.singletonList(id));
        Assertions.assertEquals(1, employees.size());
        Assertions.assertEquals("张三", employees.get(0).getEmpName());

        // endregion

        // region 测试 LocalDateTime

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        order = new Order();
        order.setAddress("address");
        order.setAmount(BigDecimal.valueOf(100));
        order.setCustomerId(1L);
        order.setCreateTime(now);
        int result = this.orderMapper.add(order);
        Assertions.assertEquals(1, result);
        id = order.getId();
        order = this.orderMapper.findByIdWithCustomer(id);
        this.orderMapper.delete(id);
        Assertions.assertEquals(now, order.getCreateTime());

        // endregion
    }

}
