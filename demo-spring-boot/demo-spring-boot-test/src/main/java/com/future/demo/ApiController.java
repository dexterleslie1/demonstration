package com.future.demo;

import com.future.common.http.ObjectResponse;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("addUser")
    ObjectResponse<String> addUser() {
        UserModel userModel = new UserModel();
        userModel.setId(10001l);
        userModel.setAge(30);
        userModel.setName("中文测试");
        userModel.setEmail("dexterleslie@gmail.com");
        userService.save(userModel);

        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("成功创建用户");
        return response;
    }

    // 用于协助测试 MockMvc 读取 JSON 字符串内容
    @GetMapping("getUser")
    ObjectResponse<UserModel> getUser() {
        UserModel userModel = new UserModel();
        userModel.setId(10001l);
        userModel.setAge(30);
        userModel.setName("中文测试");
        userModel.setEmail("dexterleslie@gmail.com");
        ObjectResponse<UserModel> response = new ObjectResponse<>();
        response.setData(userModel);
        return response;
    }

    /**
     * 测试使用 @RequestBody
     *
     * @param userModel
     * @return
     */
    @PostMapping("postWithBody")
    ObjectResponse<String> postWithBody(@RequestBody UserModel userModel) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("用户名称：" + userModel.getName());
        return response;
    }
}
