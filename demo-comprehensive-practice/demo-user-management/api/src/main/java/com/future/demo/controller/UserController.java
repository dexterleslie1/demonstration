package com.future.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.future.demo.R;
import com.future.demo.entity.User;
import com.future.demo.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Resource
    UserService userService;

    /**
     * 根据部门id分页查询用户列表
     *
     * @param deptId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("list")
    public R list(@RequestParam(value = "deptId", defaultValue = "0") Long deptId,
                  @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
                  @RequestParam(value = "pageSize", defaultValue = "0") Integer pageSize) {
        IPage<User> page = userService.list(deptId, pageNum, pageSize);
        return R.success(page.getRecords(),
                (int) page.getPages(),
                (int) page.getTotal(),
                (int) page.getCurrent(),
                (int) page.getSize());
    }

    /**
     * 根据用户id删除用户
     *
     * @param idList
     * @return
     */
    @DeleteMapping("delete")
    public R delete(@RequestParam("idList") List<Long> idList) {
        userService.delete(idList);
        return R.success();
    }

    /**
     * 重置用户密码
     *
     * @param id
     * @param password
     * @return
     */
    @PutMapping("resetPassword")
    public R resetPassword(@RequestParam("id") Long id,
                           @RequestParam("password") String password) {
        userService.resetPassword(id, password);
        return R.success();
    }

    /**
     * 修改用户
     *
     * @param user
     * @return
     */
    @PutMapping("update")
    public R update(@RequestBody User user) {
        userService.update(user);
        return R.success();
    }

    /**
     * 新增用户
     *
     * @param user
     * @return
     */
    @PostMapping("add")
    public R add(@RequestBody User user) {
        userService.add(user);
        return R.success();
    }
}
