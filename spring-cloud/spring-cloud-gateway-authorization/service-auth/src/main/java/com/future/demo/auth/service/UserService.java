package com.future.demo.auth.service;

import com.future.demo.auth.model.UserModel;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    Map<String, UserModel> usernameToUserModelMapper = new HashMap<>();

    @PostConstruct
    void init() {
        String password = "123456";

        UserModel userModel = new UserModel();
        userModel.setId(1L);
        userModel.setUsername("admin");
        userModel.setPassword(password);
        userModel.setRoleList(Collections.singletonList("ROLE_admin"));
        this.usernameToUserModelMapper.put(userModel.getUsername(), userModel);

        userModel = new UserModel();
        userModel.setId(2L);
        userModel.setUsername("user1");
        userModel.setPassword(password);
        userModel.setPermissionList(Collections.singletonList("nuser:fun1"));
        userModel.setMenuList(Collections.singletonList("menu1"));
        this.usernameToUserModelMapper.put(userModel.getUsername(), userModel);
    }

    public UserModel findByUsername(String username) {
        return this.usernameToUserModelMapper.get(username);
    }
}
