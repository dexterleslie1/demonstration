package com.future.demo;

import com.future.demo.bean.Employee;
import com.future.demo.mapper.EmployeeMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 基于 MyBatis 批量插入性能测试
 */
@SpringBootTest
@Slf4j
public class BatchInsertTests {
    @Resource
    EmployeeMapper employeeMapper;
    @Resource
    SqlSessionTemplate sqlSessionTemplate;

    @Test
    public void test() {
        // https://blog.csdn.net/chang100111/article/details/115664432

        int totalCount = 1000000;

        // region 使用 for 循环一条一条插入数据

        this.employeeMapper.truncate();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        for (int i = 0; i < totalCount; i++) {
            Employee employee = new Employee();
            employee.setEmpName("张三" + i);
            employee.setAge(20);
            employee.setEmpSalary(1000.0);
            this.employeeMapper.insert(employee);
        }

        stopWatch.stop();
        log.info("使用 for 循环一条一条插入数据 - 耗时 {} 毫秒", stopWatch.getTotalTimeMillis());

        Assertions.assertEquals(totalCount, this.employeeMapper.count());

        // endregion

        // region 使用 foreach 动态生成 insert values (...),(...),(...)

        this.employeeMapper.truncate();

        stopWatch = new StopWatch();
        stopWatch.start();

        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < totalCount; i++) {
            Employee employee = new Employee();
            employee.setEmpName("张三" + i);
            employee.setAge(20);
            employee.setEmpSalary(1000.0);
            employees.add(employee);

            if (employees.size() == 1000) {
                this.employeeMapper.insertBatch(employees);
                employees = new ArrayList<>();
            }
        }

        if (!employees.isEmpty()) {
            this.employeeMapper.insertBatch(employees);
        }

        stopWatch.stop();
        log.info("使用 foreach 动态生成 insert values (...),(...),(...) - 耗时 {} 毫秒", stopWatch.getTotalTimeMillis());

        Assertions.assertEquals(totalCount, this.employeeMapper.count());

        // endregion

        // region 使用 foreach 动态生成  insert values (...); insert values (...); insert values (...);

        this.employeeMapper.truncate();

        stopWatch = new StopWatch();
        stopWatch.start();

        employees = new ArrayList<>();
        for (int i = 0; i < totalCount; i++) {
            Employee employee = new Employee();
            employee.setEmpName("张三" + i);
            employee.setAge(20);
            employee.setEmpSalary(1000.0);
            employees.add(employee);

            if (employees.size() == 1000) {
                this.employeeMapper.insertBatch2(employees);
                employees = new ArrayList<>();
            }
        }

        if (!employees.isEmpty()) {
            this.employeeMapper.insertBatch(employees);
        }

        stopWatch.stop();
        log.info("使用 foreach 动态生成  insert values (...); insert values (...); insert values (...); - 耗时 {} 毫秒", stopWatch.getTotalTimeMillis());

        Assertions.assertEquals(totalCount, this.employeeMapper.count());

        // endregion

        // region MyBatis batch 模式插入数据

        this.employeeMapper.truncate();

        stopWatch = new StopWatch();
        stopWatch.start();

        SqlSession sqlSession = null;
        try {
            sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH);
            EmployeeMapper employeeMapperInternal = sqlSession.getMapper(EmployeeMapper.class);

            for (int i = 0; i < totalCount; i++) {
                Employee employee = new Employee();
                employee.setEmpName("张三" + i);
                employee.setAge(20);
                employee.setEmpSalary(1000.0);
                employeeMapperInternal.insert(employee);

                if ((i + 1) % 1000 == 0) {
                    sqlSession.commit();
                }
            }
        } catch (Exception ex) {
            if (sqlSession != null)
                sqlSession.rollback();
        } finally {
            if (sqlSession != null)
                sqlSession.close();
        }

        stopWatch.stop();
        log.info("MyBatis batch 模式插入数据 - 耗时 {} 毫秒", stopWatch.getTotalTimeMillis());

        Assertions.assertEquals(totalCount, this.employeeMapper.count());

        // endregion

        // region 使用 CompletableFuture 多线程并发插入数据

        this.employeeMapper.truncate();

        stopWatch = new StopWatch();
        stopWatch.start();

        ExecutorService threadPool = Executors.newCachedThreadPool();
        int availableProcessors = Runtime.getRuntime().availableProcessors();

        CompletableFuture.allOf(IntStream.range(0, availableProcessors).mapToObj(index -> CompletableFuture.runAsync(() -> {
            int shardTotal = availableProcessors;
            int shardIndex = index;

            int internalCount = totalCount / shardTotal;
            int startIndex = shardIndex * internalCount;
            List<Employee> employeesInternal = new ArrayList<>();
            for (int i = startIndex; i < startIndex + internalCount; i++) {
                Employee employee = new Employee();
                employee.setEmpName("张三" + i);
                employee.setAge(20);
                employee.setEmpSalary(1000.0);
                employeesInternal.add(employee);

                if (employeesInternal.size() == 1000) {
                    this.employeeMapper.insertBatch(employeesInternal);
                    employeesInternal = new ArrayList<>();
                }
            }

            if (!employeesInternal.isEmpty()) {
                this.employeeMapper.insertBatch(employeesInternal);
            }
        }, threadPool)).collect(Collectors.toList()).stream().toArray(CompletableFuture[]::new)).join();

        stopWatch.stop();
        log.info("使用 CompletableFuture 多线程并发插入数据 - 耗时 {} 毫秒", stopWatch.getTotalTimeMillis());

        Assertions.assertEquals(totalCount, this.employeeMapper.count());

        // endregion
    }
}
