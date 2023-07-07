package com.future.demo;

import com.yyd.common.http.ResponseUtils;
import com.yyd.common.http.response.ObjectResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @Resource
    TestService testService;
    @Resource
    TestService2 testService2;
    @Resource
    UserService userService;

    @GetMapping("add")
    ObjectResponse<Integer> add(@RequestParam("a") int a,
                                @RequestParam("b") int b) {
        int c = testService.add(a, b);
        ObjectResponse<Integer> response = new ObjectResponse<>();
        response.setData(c);
        return response;
    }

    @GetMapping("minus")
    ObjectResponse<Integer> minus(@RequestParam("a") int a,
                                  @RequestParam("b") int b) {
        int c = testService2.minus(a, b);
        ObjectResponse<Integer> response = new ObjectResponse<>();
        response.setData(c);
        return response;
    }

    @GetMapping("addUser")
    ObjectResponse<String> addUser() {
        UserModel userModel = new UserModel();
        userModel.setId(10001l);
        userModel.setAge(30);
        userModel.setName("中文测试");
        userModel.setEmail("dexterleslie@gmail.com");
        userService.save(userModel);

        return ResponseUtils.successObject("成功创建用户");
    }

}
