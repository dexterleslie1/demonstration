package com.future.demo.auth.controller;

import com.future.demo.auth.model.UserModel;
import com.future.demo.auth.service.UserService;
import com.future.demo.common.vo.LoginSuccessVo;
import com.future.demo.common.vo.RefreshTokenVo;
import com.yyd.common.exception.BusinessException;
import com.yyd.common.http.ResponseUtils;
import com.yyd.common.http.response.ObjectResponse;
import com.yyd.common.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthApiController {

    @Resource
    UserService userService;
    @Value("${privateKey}")
    String privateKey;

    final static Integer TimeoutInSeconds = 2;

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
            String accessToken = JwtUtil.signWithPrivateKey(privateKey, o -> {
                o.withClaim("userId", userModel.getId());
                o.withClaim("roleList", userModel.getRoleList());
                o.withClaim("permissionList", userModel.getPermissionList());
                o.withClaim("type", "accessToken");
                o.withExpiresAt(DateUtils.addSeconds(new Date(), TimeoutInSeconds));
            });

            String refreshToken = JwtUtil.signWithPrivateKey(privateKey, o -> {
                o.withClaim("userId", userModel.getId());
                o.withClaim("type", "refreshToken");
            });

            LoginSuccessVo loginSuccessVo = new LoginSuccessVo();
            loginSuccessVo.setUserId(userModel.getId());
            loginSuccessVo.setLoginName(userModel.getUsername());
            loginSuccessVo.setAccessToken(accessToken);
            loginSuccessVo.setRefreshToken(refreshToken);
            loginSuccessVo.setMenuList(userModel.getMenuList());
            loginSuccessVo.setPermissionList(userModel.getPermissionList());

            return ResponseUtils.successObject(loginSuccessVo);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new BusinessException(50000, "帐号密码错误！");
        }
    }

    @PostMapping("refreshToken")
    public ObjectResponse<RefreshTokenVo> refreshToken(
            @RequestParam(value = "userId", defaultValue = "0") Long userId) throws InvalidKeySpecException, NoSuchAlgorithmException {
        UserModel userModel = this.userService.findByUserId(userId);

        String accessToken = JwtUtil.signWithPrivateKey(privateKey, o -> {
            o.withClaim("userId", userModel.getId());
            o.withClaim("roleList", userModel.getRoleList());
            o.withClaim("permissionList", userModel.getPermissionList());
            o.withClaim("type", "accessToken");
            o.withExpiresAt(DateUtils.addSeconds(new Date(), TimeoutInSeconds));
        });

//        String refreshToken = JwtUtil.signWithPrivateKey(privateKey, o -> {
//            o.withClaim("userId", userModel.getId());
//            o.withClaim("type", "refreshToken");
//        });

        RefreshTokenVo refreshTokenVo = new RefreshTokenVo();
        refreshTokenVo.setAccessToken(accessToken);

        return ResponseUtils.successObject(refreshTokenVo);
    }

}
