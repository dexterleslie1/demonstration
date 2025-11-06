//package com.future.demo.service;
//
//import com.future.demo.bean.Employee;
//import com.future.demo.mapper.EmployeeMapper;
//import com.github.pagehelper.PageHelper;
//import com.github.pagehelper.PageInfo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//public class EmployeeService {
//    @Autowired
//    EmployeeMapper employeeMapper;
//
//    /**
//     * 用于协助测试一级缓存
//     *
//     * @param id
//     * @return
//     */
//    // 开启事务（同一个SqlSession）才能使用一级缓存
//    @Transactional
//    public Employee testLevel1Cache(Long id) {
//        // 第一次会到数据库查询
//        this.employeeMapper.getById(id);
//        // 第二次会从缓存中查询
//        return this.employeeMapper.getById(id);
//    }
//
//    /**
//     * 用于协助测试二级缓存
//     *
//     * @param id
//     * @return
//     */
//    public Employee testLevel2Cache(Long id) {
//        this.employeeMapper.getById(id);
//
//        return this.employeeMapper.getById(id);
//    }
//
//    /**
//     * 用于协助测试pagehelper分页插件
//     */
//    public PageInfo<Employee> findByPage(int pageNum, int pageSize) {
//        return PageHelper.<Employee>startPage(pageNum, pageSize)
//                .doSelectPageInfo(() -> this.employeeMapper.listAll());
//    }
//}
