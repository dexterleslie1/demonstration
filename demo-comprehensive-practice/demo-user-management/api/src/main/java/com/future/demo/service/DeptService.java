package com.future.demo.service;

import com.future.demo.entity.Dept;

import java.util.List;

public interface DeptService {
    /**
     * 查询所有部门列表
     *
     * @return
     */
    List<Dept> list();
}
