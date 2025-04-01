package com.future.demo.swagger;

import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

/**
 *
 */
@Api(value = "用户api", description = "微服务间调用的用户新增接口")
@RestController
@RequestMapping(value = "/user1")
public class UserController1 {

    /**
     * 传递单个参数
     * @param loginname
     * @param password
     * @return
     */
    @ApiOperation(value = "新增用户", notes = "新增用户对象")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "loginname", value = "用户登录s名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "用户密码", required = true, dataType = "String", paramType = "query")})
    @RequestMapping(value = "/add1", method = RequestMethod.POST)
    public String add(
            @RequestParam(defaultValue = "") String loginname,
            @RequestParam(defaultValue = "") String password) {
        return "ok";
    }
}
