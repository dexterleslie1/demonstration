package com.future.demo.common.feign;

import com.future.demo.common.vo.PermissionVo;
import com.future.demo.common.vo.UserVo;
import com.yyd.common.http.response.ListResponse;
import com.yyd.common.http.response.ObjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "service-auth-intranet", value = "service-auth", path = "/api/v1/user")
public interface UserFeignIntranet {

//    @GetMapping(value = "getByUsername")
//    ObjectResponse<UserVo> getByUsername(@RequestParam(value = "username") String username);

    @GetMapping(value = "listPermission")
    ListResponse<PermissionVo> listPermission();

}
