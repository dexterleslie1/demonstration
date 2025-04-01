package com.future.demo.common.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserVo {
    private Long id;
    private String username;
    private String password;

    // 用户拥有的角色
    private List<String> roleList;
    // 用户拥有的功能权限
    private List<String> permissionList;
    // 用户拥有的菜单
    private List<String> menuList;
}
