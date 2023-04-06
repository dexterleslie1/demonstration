package com.future.demo.auth.controller;

import com.future.demo.auth.model.UserModel;
import com.future.demo.auth.service.UserService;
import com.future.demo.common.vo.LoginSuccessVo;
import com.yyd.common.exception.BusinessException;
import com.yyd.common.http.ResponseUtils;
import com.yyd.common.http.response.ObjectResponse;
import com.yyd.common.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthApiController {

    @Resource
    UserService userService;
    @Value("${privateKey}")
    String privateKey;

    @PostMapping(value = "loginWithPassword")
    public ObjectResponse<LoginSuccessVo> loginWithPassword(@RequestParam(value = "username", defaultValue = "") String username,
                                                            @RequestParam(value = "password", defaultValue = "") String password) throws BusinessException {
        UserModel userModel = this.userService.findByUsername(username);
        if(userModel == null) {
            throw new BusinessException(50000, "帐号密码错误！");
        }

        if(!userModel.getPassword().equals(password)) {
            throw new BusinessException(50000, "帐号密码错误！");
        }

        try {
            String token = JwtUtil.signWithPrivateKey(privateKey, o -> {
                o.withClaim("userId", userModel.getId());
                o.withClaim("roleList", userModel.getRoleList());
                o.withClaim("permissionList", userModel.getPermissionList());
            });

            LoginSuccessVo loginSuccessVo = new LoginSuccessVo();
            loginSuccessVo.setUserId(userModel.getId());
            loginSuccessVo.setLoginName(userModel.getUsername());
            loginSuccessVo.setToken(token);
            loginSuccessVo.setMenuList(userModel.getMenuList());
            loginSuccessVo.setPermissionList(userModel.getPermissionList());

            return ResponseUtils.successObject(loginSuccessVo);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new BusinessException(50000, "帐号密码错误！");
        }
    }

}
