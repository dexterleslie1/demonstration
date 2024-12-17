package com.future.demo;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Dexterleslie.Chan
 */
@RestController
@RequestMapping(value = "/api/v1")
public class ApiController {

    // 拥有r1或者r2角色的用户都可以调用次方法。另外需要注意的是这里匹配的字符串需要添加前缀"ROLE_"
    @Secured(value = {"ROLE_r1", "ROLE_r2"})
    @GetMapping(value = "test1")
    public ObjectResponse<String> test1() {
        return ResponseUtils.successObject("成功调用接口/api/v1/test1");
    }

    // 拥有r1或者r2角色的用户都可以调用次方法。
    // 和前面@Secured(value = {"ROLE_r1", "ROLE_r2"})等价
    @PreAuthorize("hasAnyRole('r1','r2')")
    @GetMapping(value = "test2")
    public ObjectResponse<String> test2() {
        return ResponseUtils.successObject("成功调用接口/api/v1/test2");
    }

    // 同时拥有r1和r2角色的用户都可以调用次方法。
    // @PreAuthorize("hasRole('r1') and hasRole('r2')")
    @GetMapping(value = "test3")
    public ObjectResponse<String> test3() {
        return ResponseUtils.successObject("成功调用接口/api/v1/test3");
    }

    // 拥有权限 auth:test5才能调用此方法。
    // @PreAuthorize("hasAuthority('perm:test5')")
    @GetMapping(value = "test5")
    public ObjectResponse<String> test5() {
        return ResponseUtils.successObject("成功调用接口/api/v1/test5");
    }

    // https://stackoverflow.com/questions/65543907/using-a-request-header-value-in-preauthorize
    // 自定义权限验证逻辑
    @PreAuthorize("@customizePermissionService.hasPermission(#request,#authentication)")
    @GetMapping(value = "test6")
    public ObjectResponse<String> test6(HttpServletRequest request,
                                        Authentication authentication) {
        return ResponseUtils.successObject("成功调用接口/api/v1/test6");
    }
}
