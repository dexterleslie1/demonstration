package com.future.demo.service;

import com.future.demo.entity.Dept;

import java.util.List;

public interface DeptService {
    /**
     * 查询所有部门列表
     *
     * @return
     */
    List<Dept> list(String name);

    /**
     * 更新部门信息
     *
     * @param dept
     */
    void update(Dept dept);

    /**
     * 新增部门
     *
     * @param dept
     */
    void add(Dept dept);

    /**
     * 删除部门
     *
     * @param id
     */
    void delete(long id);
}
