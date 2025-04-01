package com.future.demo.swagger;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value="User")
@Data
public class UserModel {
    @ApiModelProperty(value = "用户名")
    private String name;
    @ApiModelProperty(value = "用户密码")
    private String passWord;
    @ApiModelProperty(value = "用户userName")
    private String userName;
    @ApiModelProperty(value = "性别")
    private long sex;
    @ApiModelProperty(value = "用户account")
    private long acount;
    @ApiModelProperty(value = "用户状态")
    private Status status;
}
