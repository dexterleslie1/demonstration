package com.future.demo.controller;

import cn.hutool.core.util.RandomUtil;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.entity.Employee;
import com.future.demo.mapper.CommonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class DemoController {
    @Resource
    CommonMapper commonMapper;

    private List<String> positionList = new ArrayList<String>() {{
        this.add("CEO");
        this.add("CFO");
        this.add("架构师");
        this.add("高级开发工程师");
        this.add("中级开发工程师");
        this.add("开发工程师");
        this.add("运维工程师");
    }};

    private List<Employee> employeeList = null;
    private List<String> nameList = null;

    @PostConstruct
    public void init() {
        employeeList = Collections.synchronizedList(new ArrayList<>());
        List<Employee> employees = this.commonMapper.selectAllEmployee();
        if (employees != null && !employees.isEmpty()) {
            employeeList.addAll(employees);
        }

        nameList = Collections.synchronizedList(new ArrayList<>());
        List<String> names = commonMapper.selectAllEmployeeName();
        if (names != null && !names.isEmpty()) {
            nameList.addAll(names);
        }
        if (nameList.size() < 100000) {
            while (nameList.size() < 100000) {
                nameList.add(UUID.randomUUID().toString());
            }
        }

        if (log.isInfoEnabled()) {
            log.info("employeeList size 为 {}, nameList size 为 {}", employeeList.size(), nameList.size());
        }
    }

    /**
     * 批量插入 Employee
     *
     * @return
     */
    @GetMapping("insertEmployeeBatch")
    public ObjectResponse<String> insertEmployeeBatch() {
        List<Employee> employeeList = new ArrayList<>();
        for (int i = 0; i < 1024; i++) {
            Employee employee = new Employee();

            int randomInt = RandomUtil.randomInt(0, nameList.size());
            String name = nameList.get(randomInt);
            employee.setName(name);

            employee.setAge(RandomUtil.randomInt(1, 101));

            randomInt = RandomUtil.randomInt(0, positionList.size());
            String position = positionList.get(randomInt);
            employee.setPosition(position);

            employee.setRemark(UUID.randomUUID().toString());
            employeeList.add(employee);
        }
        commonMapper.insertEmployeeBatch(employeeList);
        this.employeeList.addAll(employeeList);
        return ResponseUtils.successObject("成功调用");
    }

    /**
     * 最左前缀法则 - 全值匹配
     *
     * @return
     */
    @GetMapping("testLeftMostFully")
    public ObjectResponse<String> testLeftMostFully() throws Exception {
        Assert.isTrue(employeeList != null && !employeeList.isEmpty(), "没有 Employee 数据，请先调用 /api/v1/insertEmployeeBatch 初始化数据");
        int randomInt = RandomUtil.randomInt(0, this.employeeList.size());
        Employee employee = this.employeeList.get(randomInt);
        List<Employee> employeeList = commonMapper.selectLeftMostFully(employee.getName(), employee.getAge(), employee.getPosition());
        if (employeeList != null && !employeeList.isEmpty())
            return ResponseUtils.successObject("成功调用");
        else {
            throw new Exception("意料之外，employeeList为空");
        }
    }

    /**
     * 最左前缀法则 - 没有中间列
     *
     * @return
     */
    @GetMapping("testLeftMostWithoutMiddleColumn")
    public ObjectResponse<String> testLeftMostWithoutMiddleColumn() throws Exception {
        Assert.isTrue(employeeList != null && !employeeList.isEmpty(), "没有 Employee 数据，请先调用 /api/v1/insertEmployeeBatch 初始化数据");
        int randomInt = RandomUtil.randomInt(0, this.employeeList.size());
        Employee employee = this.employeeList.get(randomInt);
        List<Employee> employeeList = commonMapper.selectLeftMostWithoutMiddleColumn(employee.getName(), employee.getPosition());
        if (employeeList != null && !employeeList.isEmpty())
            return ResponseUtils.successObject("成功调用");
        else {
            throw new Exception("意料之外，employeeList为空");
        }
    }

    /**
     * 最左前缀法则 - 没有开始列
     *
     * @return
     */
    @GetMapping("testLeftMostWithoutStartColumn")
    public ObjectResponse<String> testLeftMostWithoutStartColumn() throws Exception {
        Assert.isTrue(employeeList != null && !employeeList.isEmpty(), "没有 Employee 数据，请先调用 /api/v1/insertEmployeeBatch 初始化数据");
        int randomInt = RandomUtil.randomInt(0, this.employeeList.size());
        Employee employee = this.employeeList.get(randomInt);
        List<Employee> employeeList = commonMapper.selectLeftMostWithoutStartColumn(employee.getAge(), employee.getPosition());
        if (employeeList != null && !employeeList.isEmpty())
            return ResponseUtils.successObject("成功调用");
        else {
            throw new Exception("意料之外，employeeList为空");
        }
    }

    /**
     * 测试覆盖索引性能 - 使用 * 时
     *
     * @return
     * @throws Exception
     */
    @GetMapping("testCoveringIndexPerfWithAsterisk")
    public ObjectResponse<String> testCoveringIndexPerfWithAsterisk() throws Exception {
        Assert.isTrue(employeeList != null && !employeeList.isEmpty(), "没有 Employee 数据，请先调用 /api/v1/insertEmployeeBatch 初始化数据");
        int randomInt = RandomUtil.randomInt(0, this.employeeList.size());
        Employee employee = this.employeeList.get(randomInt);
        List<Employee> employeeList = commonMapper.testCoveringIndexPerfWithAsterisk(employee.getName(), employee.getAge(), employee.getPosition());
        if (employeeList != null && !employeeList.isEmpty())
            return ResponseUtils.successObject("成功调用");
        else {
            throw new Exception("意料之外，employeeList为空");
        }
    }

    /**
     * 测试覆盖索引性能 - 不使用 * 时
     *
     * @return
     * @throws Exception
     */
    @GetMapping("testCoveringIndexPerfWithoutAsterisk")
    public ObjectResponse<String> testCoveringIndexPerfWithoutAsterisk() throws Exception {
        Assert.isTrue(employeeList != null && !employeeList.isEmpty(), "没有 Employee 数据，请先调用 /api/v1/insertEmployeeBatch 初始化数据");
        int randomInt = RandomUtil.randomInt(0, this.employeeList.size());
        Employee employee = this.employeeList.get(randomInt);
        List<Employee> employeeList = commonMapper.testCoveringIndexPerfWithoutAsterisk(employee.getName(), employee.getAge(), employee.getPosition());
        if (employeeList != null && !employeeList.isEmpty())
            return ResponseUtils.successObject("成功调用");
        else {
            throw new Exception("意料之外，employeeList为空");
        }
    }
}
