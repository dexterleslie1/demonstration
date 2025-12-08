package com.future.demo.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.future.demo.entity.Dept;
import com.future.demo.entity.User;
import com.future.demo.mapper.UserMapper;
import com.future.demo.service.UserService;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    UserMapper userMapper;

    @Override
    public IPage<User> list(Long deptId, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }
        Page<User> page = new Page<>(pageNum, pageSize);
        MPJLambdaWrapper<User> mpjLambdaWrapper = new MPJLambdaWrapper<>();
        mpjLambdaWrapper
                .selectAll(User.class)
                .selectAssociation(Dept.class, User::getDept)
                .leftJoin(Dept.class, Dept::getId, User::getDeptId);
        if (deptId != null && deptId > 0) {
            mpjLambdaWrapper.eq(User::getDeptId, deptId);
        }
        mpjLambdaWrapper.orderByDesc(User::getId);
        page = userMapper.selectPage(page, mpjLambdaWrapper);
        return page;
    }

    @Override
    public void delete(List<Long> idList) {
        if (idList != null && !idList.isEmpty()) {
            userMapper.deleteBatchIds(idList);
        }
    }

    @Override
    public void resetPassword(Long id, String password) {
        User user = userMapper.selectById(id);
        user.setPassword(password);
        userMapper.updateById(user);
    }

    @Override
    public void update(User user) {
        User userOrigin = userMapper.selectById(user.getId());
        /*userOrigin.setUserName(user.getUserName());*/
        userOrigin.setNickName(user.getNickName());
        userOrigin.setDeptId(user.getDeptId());
        userMapper.updateById(userOrigin);
    }

    @Override
    public void add(User user) {
        userMapper.insert(user);
    }
}
