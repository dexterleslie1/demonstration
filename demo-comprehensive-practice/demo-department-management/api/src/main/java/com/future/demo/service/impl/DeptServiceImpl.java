package com.future.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.future.demo.entity.Dept;
import com.future.demo.mapper.DeptMapper;
import com.future.demo.service.DeptService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DeptServiceImpl implements DeptService {

    @Resource
    DeptMapper deptMapper;

    @Override
    public List<Dept> list(String name) {
        QueryWrapper<Dept> queryWrapper = Wrappers.query();
        if (StringUtils.hasText(name)) {
            queryWrapper.like("`name`", name);
        }
        queryWrapper.orderByAsc("order_num");
        return deptMapper.selectList(queryWrapper);
    }

    @Override
    public void update(Dept dept) {
        Long deptId = dept.getId();
        Dept deptOrigin = deptMapper.selectById(deptId);
        deptOrigin.setName(dept.getName());
        deptOrigin.setOrderNum(dept.getOrderNum());
        deptMapper.updateById(deptOrigin);
    }

    @Override
    public void add(Dept dept) {
        deptMapper.insert(dept);
    }

    @Override
    public void delete(long id) {
        deptMapper.deleteById(id);
    }
}
