package com.future.demo.auth.controller;

import com.future.demo.auth.model.PermissionModel;
import com.future.demo.auth.model.UserModel;
import com.future.demo.auth.service.PermissionService;
import com.future.demo.auth.service.UserService;
import com.future.demo.common.vo.PermissionVo;
import com.future.demo.common.vo.UserVo;
import com.yyd.common.bean.ModelMapperUtil;
import com.yyd.common.http.ResponseUtils;
import com.yyd.common.http.response.ListResponse;
import com.yyd.common.http.response.ObjectResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
public class UserApiController {

    @Resource
    UserService userService;
    @Resource
    PermissionService permissionService;

//    @GetMapping(value = "getByUsername")
//    public ObjectResponse<UserVo> getByUsername(@RequestParam(value = "username", defaultValue = "") String username) {
//        UserModel userModel = this.userService.findByUsername(username);
//        UserVo userVo = ModelMapperUtil.ModelMapperInstance.map(userModel, UserVo.class);
//        return ResponseUtils.successObject(userVo);
//    }

    @GetMapping(value = "listPermission")
    public ListResponse<PermissionVo> listPermission() {
        List<PermissionModel> permissionModelList = this.permissionService.findAll();
        List<PermissionVo> permissionVoList = permissionModelList.stream().map(o-> ModelMapperUtil.ModelMapperInstance.map(o, PermissionVo.class)).collect(Collectors.toList());
        return ResponseUtils.successList(permissionVoList);
    }

}
