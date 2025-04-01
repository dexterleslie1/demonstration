package com.future.demo.mybatis.plus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.future.demo.mybatis.plus.entity.User;
import com.future.demo.mybatis.plus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class ApiController {
    @Autowired
    UserService userService;

    @GetMapping("name/{name}")
    public ResponseEntity<User> name(@PathVariable(value = "name", required = false) String name) {
        QueryWrapper<User> queryWrapper = Wrappers.query();
        queryWrapper.eq("name", name);
        User user = this.userService.getOne(queryWrapper);
        ResponseEntity<User> response = ResponseEntity.ok(user);
        return response;
    }
}
