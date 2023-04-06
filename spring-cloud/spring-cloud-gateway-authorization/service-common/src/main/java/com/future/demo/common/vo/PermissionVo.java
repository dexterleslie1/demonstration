package com.future.demo.common.vo;

import lombok.Data;

import java.util.List;

@Data
public class PermissionVo {
    private String name;
    private String url;
    private List<String> roleListAccessing;
    private List<String> permissionListAccessing;
}
