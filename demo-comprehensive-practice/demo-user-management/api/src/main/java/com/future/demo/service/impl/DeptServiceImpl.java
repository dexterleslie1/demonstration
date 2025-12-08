package com.future.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.future.demo.entity.Dept;
import com.future.demo.mapper.DeptMapper;
import com.future.demo.service.DeptService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DeptServiceImpl implements DeptService {

    @Resource
    DeptMapper deptMapper;

    @Override
    public List<Dept> list() {
        QueryWrapper<Dept> queryWrapper = Wrappers.query();
        queryWrapper.orderByAsc("order_num");
        return deptMapper.selectList(queryWrapper);
    }
}
