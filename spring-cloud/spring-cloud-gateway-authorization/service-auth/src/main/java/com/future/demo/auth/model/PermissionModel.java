package com.future.demo.auth.model;

import lombok.Data;

import java.util.List;

@Data
public class PermissionModel {
    private String name;
    private String url;
    private List<String> roleListAccessing;
    private List<String> permissionListAccessing;
}
