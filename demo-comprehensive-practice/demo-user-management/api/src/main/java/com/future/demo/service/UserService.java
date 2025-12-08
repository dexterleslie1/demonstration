package com.future.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.future.demo.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface UserService {
    /**
     * 查询用户列表
     *
     * @param deptId
     * @param userName
     * @param createTimeStart
     * @param createTimeEnd
     * @param pageNum
     * @param pageSize
     * @return
     */
    IPage<User> list(Long deptId,
                     String userName,
                     LocalDate createTimeStart,
                     LocalDate createTimeEnd,
                     Integer pageNum,
                     Integer pageSize);

    /**
     * 根据用户id删除用户
     *
     * @param idList
     */
    void delete(List<Long> idList);

    /**
     * 重置用户密码
     *
     * @param id
     * @param password
     */
    void resetPassword(Long id, String password);

    /**
     * 修改用户
     *
     * @param user
     */
    void update(User user);

    /**
     * 新增用户
     *
     * @param user
     */
    void add(User user);
}
