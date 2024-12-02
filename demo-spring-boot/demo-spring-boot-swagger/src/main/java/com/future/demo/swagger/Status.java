package com.future.demo.swagger;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="用户状态")
public enum Status {
    @ApiModelProperty(value = "启用")
    Enable,
    @ApiModelProperty(value = "禁用")
    Disable;
}
