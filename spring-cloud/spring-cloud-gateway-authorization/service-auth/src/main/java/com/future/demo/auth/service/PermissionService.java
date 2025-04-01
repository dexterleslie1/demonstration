package com.future.demo.auth.service;

import com.future.demo.auth.model.PermissionModel;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PermissionService {

    List<PermissionModel> permissionModelList = new ArrayList<>();

    @PostConstruct
    void init() {
        PermissionModel permissionModel = new PermissionModel();
        permissionModel.setName("管理员测试接口1");
        permissionModel.setUrl("/api/v1/admin/test1");
        permissionModel.setRoleListAccessing(Collections.singletonList("ROLE_admin"));
        this.permissionModelList.add(permissionModel);

        permissionModel = new PermissionModel();
        permissionModel.setName("管理员测试接口2");
        permissionModel.setUrl("/api/v1/admin/test2");
        permissionModel.setPermissionListAccessing(Collections.singletonList("admin:fun1"));
        this.permissionModelList.add(permissionModel);

        permissionModel = new PermissionModel();
        permissionModel.setName("普通用户测试接口1");
        permissionModel.setUrl("/api/v1/nuser/test1");
        permissionModel.setPermissionListAccessing(Collections.singletonList("nuser:fun1"));
        this.permissionModelList.add(permissionModel);

        permissionModel = new PermissionModel();
        permissionModel.setName("普通用户测试接口2");
        permissionModel.setUrl("/api/v1/nuser/test2");
        permissionModel.setRoleListAccessing(Collections.singletonList("ROLE_user"));
        this.permissionModelList.add(permissionModel);

        permissionModel = new PermissionModel();
        permissionModel.setName("accessToken刷新接口");
        permissionModel.setUrl("/api/v1/auth/refreshToken");
        this.permissionModelList.add(permissionModel);
    }

    public List<PermissionModel> findAll() {
        return this.permissionModelList;
    }
}
