package com.future.demo.common.vo;

import lombok.Data;

import java.util.List;

@Data
public class LoginSuccessVo {
    private Long userId;
    private String loginName;
    private String refreshToken;
    private String accessToken;
    private List<String> menuList;
    private List<String> permissionList;
}
